package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.IFunc;
import com.xw.glue.properties.JProperties;

public class ParamFuncValue extends ParamValue {
	IFunc func;
	public ParamFuncValue(IFunc func, JProperties properties) {
		super(func.getFuncName());
		this.func = func;
	}
	@Override
	public Object value(JGlueContext context) {
		return this.func.exec(context);
	}
	public IFunc getFunc() {
		return func;
	}	
}
