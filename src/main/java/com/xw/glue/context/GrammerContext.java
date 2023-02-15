package com.xw.glue.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.xw.glue.exception.GlueException;
import com.xw.glue.func.IFunc;
import com.xw.glue.value.ParamValue;

public abstract class GrammerContext implements JGlueContext {
	
	JGlueContext globeContext;
	Map<String, JValue> paramContext;
	boolean isFunc;
	public GrammerContext(JGlueContext globeContext) {
		this.globeContext = globeContext;
		this.paramContext = new HashMap<String, JValue>();
	}
	
	@Override
	public Object get(String key) {
		JValue value = getJValue(key);
		if(value==null) {
			throw new GlueException("not defined param:" + key);
		}
		return value.value;
	}

	@Override
	public IFunc getFunc(String key) {
		JValue func = getJValue(key);
		if(func!=null && func.getValue() instanceof IFunc) {
			return (IFunc) func.getValue();
		} else {
			return null;
		}
	}
	
	@Override
	public JValue set(String name, Object obj) {
		JValue value = getJValue(name);
		if(value!=null) {
			value.setValue(obj);
		} else {
			value = new JValue(name, obj);
			paramContext.put(name, value);
		}
		return value;
	}

	@Override
	public String[] keys() {
		return null;
	}

	@Override
	public boolean contains(String key) {
		return getJValue(key)!=null;
	}

	@Override
	public int size() {
		return paramContext.size();
	}

	@Override
	public Collection<JValue> values() {
		return paramContext.values();
	}

	@Override
	public void addJValue(JValue value) {	
		paramContext.put(value.key, value);
	}

	public void setGlobleContext(JGlueContext globeContext) {
		this.globeContext = globeContext;
	}
	
	@Override
	public JValue getJValue(String key) {
		JValue value = paramContext.get(key);
		if(value==null && globeContext!=null) {
			return globeContext.getJValue(key);
		}
		return value;
	}
	
	@Override
	public JValue getJValue(ParamValue paramValue) {
		return getJValue(paramValue.getSource());
	}
	
	public JGlueContext getGlobeContext() {
		return this.globeContext;
	}
	
	public boolean isFuncContext() {
		return isFunc;
	}
	
	public void setFuncContext(boolean isFunc) {
		this.isFunc = isFunc;
	}
}
