package com.xw.glue.object;

import java.util.HashMap;
import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.JValue;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.grammar.RuntimeFunc;
import com.xw.glue.grammar.RuntimeInstFunc;

public abstract class JglueClass<T> extends HashMap<String, Object>{
	protected JGlueContext context;
	protected T instance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JglueClass(JGlueContext context, T instance) {
		this.instance = instance;
		this.context = context;
		List<JValue> params = registParams();
		if(params!=null) {
			for(JValue param: params) {
				put(param.getKey(), param.getValue());
			}
		}
	}
	
	public Object remove(Object key) {
         return super.remove(key);
    }
	
	public JGlueContext getContext() {
		return context;
	}

	public void setContext(JGlueContext context) {
		this.context = context;
	}
	
	public RuntimeFunc getFunc(String funcName) {
		RuntimeFunc func = (RuntimeFunc)get(funcName);
		if(func==null) {
			AbstractInstFunc<T> insFunc = buildFunc(funcName);
			func = new RuntimeInstFunc<T>(insFunc, context, instance);
			put(funcName, func);
		}
		return func;
	}
	
	public abstract AbstractInstFunc<T> buildFunc(String funcName);
	
	public List<JValue> registParams() {return null;}
	
	public String toString() {
		return super.toString() + ",JglueClass:" + this.getClass().getSimpleName();
	}
}
