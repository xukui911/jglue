package com.xw.glue.func.namespace;

import java.util.HashMap;
import java.util.Map;

import com.xw.glue.func.AbstractFunc;
import com.xw.glue.func.IFunc;

public class FuncNamespace {
	private String namespace;
	private Map<String, IFunc> funcMap = new HashMap<String, IFunc>();
	
	FuncNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Map<String, IFunc> getFuncMap() {
		return funcMap;
	}
	public void setFuncMap(Map<String, IFunc> funcMap) {
		this.funcMap = funcMap;
	}
	
	public void addFunc(IFunc func) {
		if (func instanceof AbstractFunc) {
			((AbstractFunc) func).setNamespace(namespace);
		}
		funcMap.put(func.getFuncName(), func);
	}
	
	public IFunc getFunc(String funcName) {
		return funcMap.get(funcName);
	} 
	
	public IFunc getFuncOrDelete(String funcName, boolean isDelete) {
		IFunc func = funcMap.get(funcName);
		if (isDelete && func!=null) {
			funcMap.remove(funcName);
		}
		return func;
	} 
}
