package com.xw.glue.func.factory;

import java.util.HashMap;
import java.util.Map;
import com.xw.glue.func.AbstractInstFunc;

public class AbstractInsFuncFactory<T> {
	private Map<String, AbstractInstFunc<T>> funcs = new HashMap<String, AbstractInstFunc<T>>();

	
	public AbstractInsFuncFactory<T> add(AbstractInstFunc<T> func) {
		funcs.put(func.getFuncName(), func);
		return this;
	}
	
	public  AbstractInstFunc<T> getFunc(String funcName) {
		return funcs.get(funcName);
	}
}
