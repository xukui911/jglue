package com.xw.glue.grammar.oop;

import java.util.HashMap;
import java.util.Map;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Result;

public class ClassDefine {
	Map<String, Object> params = new HashMap<String, Object>();
	IFunc func;
	JGlueContext context;
	public ClassDefine(IFunc func, JGlueContext context) {
		this.func = func;
		for(String key: context.keys()) {
			if(key.startsWith("this.")) {
				params.put(key, context.get(key));
			}
		}
	}
	
	public void set(String key, Object value) {
		params.put(key, value);
	}
	
	public Object get(String key) {
		Object val = params.get(key);
		return val;
	}
	
	public Result exec(String express) {
		if(express.indexOf("(")==-1) {
			
		}
		return null;
	}
}
