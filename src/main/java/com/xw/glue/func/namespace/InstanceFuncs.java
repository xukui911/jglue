package com.xw.glue.func.namespace;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xw.glue.func.InstanceFunc;

public class InstanceFuncs {
	Map<String, InstanceFunc> funcs = new ConcurrentHashMap<String, InstanceFunc>();
	
	public void regist(InstanceFunc func) {
		funcs.put(func.getFuncName(), func);
	}
	
	public InstanceFunc getFunc(String funcName) {
		return funcs.get(funcName);
	}
}
