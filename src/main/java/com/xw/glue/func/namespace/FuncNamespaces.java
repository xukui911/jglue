package com.xw.glue.func.namespace;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.xw.glue.func.IFunc;

public class FuncNamespaces {
	private Map<String, FuncNamespace> namespaceMap = new HashMap<String, FuncNamespace>();
	private static FuncNamespaces namespaces = new FuncNamespaces();
	
	public static String STATIC_NAMESPACE = "sys_default";
	
	private FuncNamespaces(){
		namespaceMap.put(STATIC_NAMESPACE, new FuncNamespace(STATIC_NAMESPACE));
	}
	
	public static FuncNamespaces getInstance() {
		return namespaces;
	}
	
	public Set<String> namespaces() {
		return namespaceMap.keySet();
	}
	
	public IFunc getFunc(String funcName) {
		String[] namespaceFunc = funcName.split("\\.");
		if (namespaceFunc.length == 1) {
			return getFunc(STATIC_NAMESPACE, funcName);
		} else if (namespaceFunc.length == 2){
			return getFunc(namespaceFunc[0], namespaceFunc[1]);
		} else if (namespaceFunc.length > 2){
			String tempFuncName = namespaceFunc[1];
			for (int i=2;i<namespaceFunc.length;i++) {
				tempFuncName += '.' + namespaceFunc[i];
			}
			return getFunc(namespaceFunc[0], tempFuncName);
		}
		return null;
	}
	
	public IFunc getFunc(String namespace, String funcName) {
		return getOrDelFunc(namespace, funcName, false);
	}
	
	public IFunc getOrDelFunc(String namespace, String funcName, boolean isDelete) {
		FuncNamespace funcNamespace = namespaceMap.get(namespace);
		if (funcNamespace==null) {
			funcNamespace = namespaceMap.get(STATIC_NAMESPACE);
			return funcNamespace.getFuncOrDelete(namespace + "." + funcName, isDelete);
		}
		return funcNamespace.getFuncOrDelete(funcName, isDelete);
	}
	
	public synchronized void addFunc(String namespace, IFunc func) {
		FuncNamespace funcNamespace = namespaceMap.get(namespace);
		if (funcNamespace==null) {
			funcNamespace = new FuncNamespace(namespace);
			namespaceMap.put(namespace, funcNamespace);
		}
		funcNamespace.addFunc(func);
	}

	public IFunc removeFunc(String funcName) {
		String[] namespaceFunc = funcName.split("\\.");
		if (namespaceFunc.length == 1) {
			return getOrDelFunc(STATIC_NAMESPACE, funcName, true);
		} else if (namespaceFunc.length == 2){
			return getOrDelFunc(namespaceFunc[0], namespaceFunc[1], true);
		} else if (namespaceFunc.length > 2){
			String tempFuncName = namespaceFunc[1];
			for (int i=2;i<namespaceFunc.length;i++) {
				tempFuncName += '.' + namespaceFunc[i];
			}
			return getOrDelFunc(namespaceFunc[0], tempFuncName, true);
		}
		return null;
	}

	public boolean hasNamespace(String namespace) {
		return namespaceMap.containsKey(namespace);
	}
}
