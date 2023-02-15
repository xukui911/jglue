package com.xw.glue.func.instance.promise;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Result;

public class PromiseThenFunc {
	JGlueContext context;
	IFunc resolveFunc;
	IFunc rejectFunc;
	
	public PromiseThenFunc(IFunc resolveFunc, IFunc rejectFunc, JGlueContext context) {
		this.resolveFunc = resolveFunc;
		this.rejectFunc = rejectFunc;
		this.context = context;
	}
	
	public Object reject(Object param) {
		JGlueContext fContext = ContextFactory.getContext(context, (Grammar)rejectFunc);
		fContext.set(rejectFunc.getParams().get(0), param);
		Result result =  rejectFunc.exec(fContext);
		if(result!=null) {
			return result.getVal();
		}
		return result;
		
	}
	
	public Object resolve(Object param) {
		JGlueContext fContext = ContextFactory.getContext(context, (Grammar)resolveFunc);
		fContext.set(resolveFunc.getParams().get(0), param);
		Result result = resolveFunc.exec(fContext);
		if(result!=null) {
			return result.getVal();
		}
		return result;
	}
	
	public boolean hasReject() {
		return rejectFunc != null;
	}
}
