package com.xw.glue.value;

import com.xw.glue.context.GrammerContext;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.utils.FuncUtils;

public class LibFuncValue extends Value<Object> {
	IFunc func;
	FuncValue funcValue;
	public LibFuncValue(FuncValue funcValue, LibValue libValue) {
		this.funcValue = funcValue;
		this.func = libValue.getFunc(funcValue.getFuncName());
	}
	
	@Override
	public Object value(JGlueContext context) {
		if (func==null) {
			throw new GlueException("not defined method:" + funcValue.getFuncName(), funcValue.getWords().get(0).getLine());
		}
		JGlueContext funcContext = null;
		funcContext = ContextFactory.getContext(((GrammerContext)context).getGlobeContext(), (Grammar)func);
		FuncUtils.initFuncParamValue(context, funcContext, func, funcValue.getParamExpressList());
		return func.exec(funcContext);
	}
}
