package com.xw.glue.context;

import java.util.Collection;

import com.xw.glue.exception.GlueException;
import com.xw.glue.func.IFunc;
import com.xw.glue.value.ParamValue;

public class CopyGlobeContext extends GrammerContext {
	public CopyGlobeContext(JGlueContext globeContext) {
		super(globeContext);
		setGlobleContext(globeContext);
	}
	
	@Override
	public Object get(String key) {
		JValue obj = paramContext.get(key);
		if(obj == null) {
			throw new GlueException("not defined param:" + key);
		}
		return obj.getValue();
	}

	@Override
	public JValue set(String name, Object obj) {
		JValue jvalue = paramContext.get(name);
		if(jvalue==null) {
			jvalue = new JValue(name, obj);
			paramContext.put(name, jvalue);
		} else {
			jvalue.setValue(obj);
		}
		return jvalue;
	}
	
	public void setIfNotExist(String name, Object obj) {
		if(!paramContext.containsKey(name)) {
			paramContext.put(name,new JValue(name, obj));
		}
	}

	@Override
	public String[] keys() {
		return paramContext.keySet().toArray(new String[0]);
	}

	@Override
	public boolean contains(String key) {
		return paramContext.containsKey(key);
	}
	
	@Override
	public IFunc getFunc(String key) {
		JValue obj = paramContext.get(key);
		if(obj!=null) {
			Object o = obj.getValue();
			if(o instanceof IFunc) {
				return (IFunc)o;
			}
		}
		return null;
	}

	@Override
	public Collection<JValue> values() {
		return paramContext.values();
	}

	@Override
	public void addJValue(JValue value) {
		this.paramContext.put(value.getKey(), value);
	}

	@Override
	public int size() {
		return paramContext.size();
	}

	@Override
	public void setGlobleContext(JGlueContext globeContext) {
		if(globeContext!=null) {
			for(JValue value: globeContext.values()) {
				if(!paramContext.containsKey(value.key)) {
					addJValue(value);
				}
			}
		}
	}

	@Override
	public JValue getJValue(String key) {
		return paramContext.get(key);
	}

	@Override
	public JValue set(ParamValue paramValue, Object obj) {
		return set(paramValue.getSource(), obj);
	}

	@Override
	public Object get(ParamValue paramValue) {
		return get(paramValue.getSource());
	}
}
