package com.xw.glue.grammar;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.AbstractInstFunc;

public class RuntimeInstFunc<T> extends RuntimeFunc {
	AbstractInstFunc<T> instFunc;
	JGlueContext globeContext;
	T instance;
	public RuntimeInstFunc(AbstractInstFunc<T> func, JGlueContext context, T instance) {
		super(func, context);
		this.instFunc = func;
		this.instance = instance;
	}
	@Override
	public Result exec(JGlueContext context) {
		return instFunc.exec(context, instance);
	}
}
