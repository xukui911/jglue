package com.xw.glue.func.instance.promise;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.func.factory.AbstractInsFuncFactory;
import com.xw.glue.grammar.Func;
import com.xw.glue.object.JglueClass;

public class PromiseInstance extends InstanceFunc {
	private static AbstractInsFuncFactory<PromiseHanlder> factory;
	static {
		factory = new AbstractInsFuncFactory<PromiseHanlder>();
		factory.add(new ThenApplyFunc());
		factory.add(new CatchFunc());
		factory.add(new GetResultFunc());
	}
	public PromiseInstance() {
		super("Promise");
		addParam("executor");
	}

	@Override
	public JglueClass<PromiseHanlder> build(JGlueContext context, Object[] objs) {
		return new Promise(context, (Func)objs[0]);
	}
	
	
	/******************** DEFINE JglueClass START ***********************/
	public static class Promise extends JglueClass<PromiseHanlder> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Promise(JGlueContext context, Func func) {
			super(context, null);
			instance = new PromiseHanlder(func, context, this);
		}

		@Override
		public AbstractInstFunc<PromiseHanlder> buildFunc(String funcName) {
			return factory.getFunc(funcName);
		}
	}
	
	/******************** DEFINE JglueClass END ***********************/
	
	/******************** DEFINE FUNC START ***************************/
	public static class ResolveFunc extends AbstractInstFunc<PromiseHanlder> {
		public ResolveFunc() {
			super("resolve");
			addParam("result");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, PromiseHanlder handler) {
			handler.resolve(objs[0]);
			return null;
		}
		
	}
	
	public static class RejectFunc extends AbstractInstFunc<PromiseHanlder> {
		public RejectFunc() {
			super("reject");
			addParam("result");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, PromiseHanlder handler) {
			handler.reject(objs[0]);
			return null;
		}
		
	}
	
	public static class ThenApplyFunc extends AbstractInstFunc<PromiseHanlder> {
		public ThenApplyFunc() {
			super("then");
			addParam("apply");
			addParam("rejectDo");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, PromiseHanlder handler) {
			handler.addThen(objs, context);
			return handler.getPromise();
		}
	}
	
	public static class CatchFunc extends AbstractInstFunc<PromiseHanlder> {
		public CatchFunc() {
			super("catch");
			addParam("catchDo");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, PromiseHanlder handler) {
			handler.addCatch((IFunc)objs[0], context);
			return handler.getPromise();
		}
	}
	
	public static class GetResultFunc extends AbstractInstFunc<PromiseHanlder> {
		public GetResultFunc() {
			super("get");
			addParam("timeout");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, PromiseHanlder handler) {
			try {
				if(objs!=null && objs.length > 0 && objs[0] !=null) {
					return handler.get(Long.valueOf(objs[0].toString()));
				} else {
					return handler.get();
				}
			} catch (Exception e ) {
				throw new GlueException("execute method Promise.get exception!!!");
			}
		}
		
	}
}
