package com.xw.glue.func.instance.promise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.instance.promise.PromiseInstance.Promise;
import com.xw.glue.func.instance.promise.PromiseInstance.RejectFunc;
import com.xw.glue.func.instance.promise.PromiseInstance.ResolveFunc;
import com.xw.glue.grammar.Func;
import com.xw.glue.grammar.Result;
import com.xw.glue.grammar.RuntimeInstFunc;

public class PromiseHanlder {
	Promise promise;
	private static ResolveFunc resolveFunc = new ResolveFunc();
	private static RejectFunc rejectFunc = new RejectFunc();
	List<PromiseThenFunc> thenFuncs = new ArrayList<PromiseThenFunc>();
	CompletableFuture<?> cFuture;
	PromiseFunc finallyFunc;
	PromiseFunc catchFunc;
	volatile boolean isStart;
	long ctime;
	volatile int index;
	Object param;
	volatile int result;  //1: resolve, 2:reject
	CompletableFuture<?> getFuture;
	public PromiseHanlder(IFunc executor, JGlueContext context, Promise promise) {
		Func func = (Func)executor;
		this.promise = promise;
		this.cFuture = CompletableFuture.supplyAsync(() -> {
			JGlueContext fContext = ContextFactory.getContext(context, func);
			if(func.getParams().size() > 0) {
				fContext.set(func.getParams().get(0), new RuntimeInstFunc<PromiseHanlder>(resolveFunc, context, PromiseHanlder.this));
			}
			if(func.getParams().size() > 1) {
				fContext.set(func.getParams().get(1), new RuntimeInstFunc<PromiseHanlder>(rejectFunc, context, PromiseHanlder.this));
			}
			Result result = executor.exec(fContext);
			if(result!=null) {
				return result.getVal();
			}
			return null;
		});
	}
	
	public void addThen(Object[] funcs, JGlueContext context) {
		if(funcs!=null && funcs.length > 0) {
			IFunc rejectFunc = null;
			if (funcs.length > 1) {
				rejectFunc = (IFunc)funcs[1];
			}
			PromiseThenFunc thenFunc = new PromiseThenFunc((IFunc)funcs[0], rejectFunc, context);
			synchronized (thenFuncs) {
				thenFuncs.add(thenFunc);
			}
			thenDo(null);
		}
	}
	
	public void addFinally(IFunc func, JGlueContext context) {
		this.finallyFunc = new PromiseFunc(func, context);
	}
	
	public void addCatch(IFunc func, JGlueContext context) {
		this.catchFunc = new PromiseFunc(func, context);
		catchDo();
	}
	
	public Object get() throws Exception {
		if(getFuture!=null) {
			return this.getFuture.get();
		} else {
			if(thenFuncs.size()==0 && isStart) {
				return this.param;
			}
			LockSupport.parkNanos(5000000);
			return get();
		}
	}
	
	public Object get(long timeout) throws Exception {
		if(getFuture!=null) {
			return this.getFuture.get(timeout, TimeUnit.MILLISECONDS);
		} else {
			if (timeout < 0) {
				throw new GlueException("Promise.get() timeout");
			}
			if(thenFuncs.size()==0 && isStart) {
				return this.param;
			}
			LockSupport.parkNanos(5000000);
			return get(timeout - 5);
		}
	}
	
	public void resolve(Object obj) {
		this.param = obj;
		result = 1;
		thenDo(obj);
		isStart = true;
	}
	
	private void thenDo(Object obj) {
		if(thenFuncs.size()>0 && result > 0) {
			synchronized (thenFuncs) {
				if(result==3) {
					return;
				}
				if (index == 0) {
					PromiseThenFunc thenFunc = thenFuncs.get(0);
					cFuture = CompletableFuture.supplyAsync(() -> {
						if(result==1) {
							return thenFunc.resolve(param);
						} else if(result==2) {
							if(thenFunc.hasReject()) {
								return thenFunc.reject(param);
							}
						}
						result = 3;
						return null;
					});
					index++;
				}
				for(;index<thenFuncs.size();index++) {
					int x = index;
					cFuture = cFuture.thenApply((req) -> {
						Object objx = thenFuncs.get(x).resolve(req);
						return objx;
					});
				}
				getFuture = cFuture;
			}
		}
	}
	
	public void reject(Object obj) {
		this.param = obj;
		result = 2;
		thenDo(obj);
		isStart = true;
	}
	
	void reject(Object obj , int count) {
		if(count==3) {
			return;
		}
		if(thenFuncs.size() > 0) {
			synchronized (thenFuncs) {
				PromiseThenFunc thenFunc = thenFuncs.get(0);
				if(thenFunc.hasReject()) {
					cFuture = CompletableFuture.supplyAsync(() -> {
						return thenFunc.reject(obj);
					});
					return;
				} else {
					/*
					cFuture = CompletableFuture.supplyAsync(() -> {
						return thenFunc.resolve(obj);
					});*/
					return;
				}
				/*
				for(int i=1;i<thenFuncs.size();i++) {
					int x = i;
					cFuture.thenApply((req) -> {
						return thenFuncs.get(x).resolve(req);
					});
				}
				thenFuncs.clear();
				*/
			}
		} else {
			LockSupport.parkNanos(10000000l);
			reject(++count);
		}
		catchDo();
		finallyDo();
	}
	
	public void catchDo() {
		if(this.catchFunc!=null) {
			cFuture.whenComplete((req, ex) -> {
				if(ex!=null) {
					CompletableFuture.supplyAsync(() -> {
						return catchFunc.exec(ex);
					});
				}
			});
		}
	}
	
	public void finallyDo() {
		if(this.finallyFunc!=null) {
			cFuture.whenComplete((req, ex) -> {
				if(ex!=null) {
					cFuture = CompletableFuture.supplyAsync(() -> {
						return catchFunc.exec(ex);
					});
				}
				cFuture = cFuture.thenApply((ret) -> {
					return finallyFunc.exec(ret);
				});
			});
		}
	}

	public Promise getPromise() {
		return promise;
	}
}
