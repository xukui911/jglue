package com.xw.glue.grammar;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.IFunc;
import com.xw.glue.value.ParamValue;

public class RuntimeFunc extends Func {
	IFunc func;
	JGlueContext globeContext;
	public RuntimeFunc(IFunc func, JGlueContext context) {
		super(func.getFuncName());
		this.func = func;
		this.globeContext = context;
	}
	@Override
	public String getFuncName() {
		return func.getFuncName();
	}
	@Override
	public List<ParamValue> getParams() {
		return func.getParams();
	}
	@Override
	public Result exec(JGlueContext context) {
		return func.exec(context);
	}
	@Override
	public boolean isAnonymous() {
		return func.isAnonymous();
	}
	@Override
	public String getNamespace() {
		return func.getNamespace();
	}
	
	public ParamValue getParamValue(String paramCode) {
		return ((Func)func).getParamValue(paramCode);
	}
	
	
	public IFunc getFunc() {
		return func;
	}
	public JGlueContext getGlobeContext() {
		return globeContext;
	}
	public void setGlobeContext(JGlueContext globeContext) {
		this.globeContext = globeContext;
	}
}
