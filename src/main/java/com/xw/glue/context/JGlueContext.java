package com.xw.glue.context;

import java.util.Collection;

import com.xw.glue.func.IFunc;
import com.xw.glue.value.ParamValue;

public interface JGlueContext {
	
	public Object get(String key);
		
	public IFunc getFunc(String key);
	
	public JValue set(String name, Object obj);
			
	public String[] keys();
	
	public boolean contains(String key);
	
	public int size();
	
	public Collection<JValue> values();
	
	public void addJValue(JValue value);
	
	public JValue getJValue(String key);
	
	public JValue getJValue(ParamValue paramValue);
	
	public abstract JValue set(ParamValue paramValue, Object obj);
	
	public abstract Object get(ParamValue paramValue);
}
