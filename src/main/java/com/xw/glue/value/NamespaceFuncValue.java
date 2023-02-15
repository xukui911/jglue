package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;

public class NamespaceFuncValue extends Value<Object> {
	
	private String namespace;
	private FuncValue funcValue;
	
	public NamespaceFuncValue(String namespace) {
		this.namespace = namespace;
	}
	@Override
	public Object value(JGlueContext context) {
		return this.funcValue.value(context);
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public NamespaceFuncValue setFuncValue(FuncValue funcValue) {
		funcValue.afterNamespaceFunc(this.namespace);
		this.funcValue = funcValue;
		return this;
	}
}
