package com.xw.glue.func.instance.promise;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Grammar;

public class PromiseFunc {
	IFunc func;
	JGlueContext context;
	
	public PromiseFunc(IFunc func, JGlueContext context) {
		this.func = func;
		this.context = context;
	}

	public IFunc getFunc() {
		return func;
	}

	public void setFunc(IFunc func) {
		this.func = func;
	}

	public JGlueContext getContext() {
		return context;
	}

	public void setContext(JGlueContext context) {
		this.context = context;
	}
	
	public Object exec(Object applay) {
		JGlueContext fContext = ContextFactory.getContext(context, (Grammar)func);
		fContext.set(func.getParams().get(0), applay);
		return func.exec(fContext).getVal();
	}
	
}
