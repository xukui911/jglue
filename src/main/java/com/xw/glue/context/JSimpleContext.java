package com.xw.glue.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.xw.glue.func.IFunc;
import com.xw.glue.value.ParamValue;

public class JSimpleContext implements JGlueContext {
	Map<String, JValue> map = new HashMap<String, JValue>();
	@Override
	public Object get(String key) {
		JValue obj = map.get(key);
		if(obj!=null) {
			return obj.getValue();
		}
		return null;
	}

	@Override
	public JValue set(String name, Object value) {
		JValue jvalue = map.get(name);
		if(jvalue==null) {
			jvalue = new JValue(name, value);
			map.put(name, jvalue);
		} else {
			jvalue.setValue(value);
		}
		return jvalue;
	}

	@Override
	public String[] keys() {
		return map.keySet().toArray(new String[0]);
	}

	@Override
	public boolean contains(String key) {
		return map.containsKey(key);
	}

	@Override
	public IFunc getFunc(String key) {
		JValue obj = map.get(key);
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
		return map.values();
	}

	@Override
	public void addJValue(JValue value) {
		map.put(value.getKey(), value);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public JValue getJValue(String key) {
		return map.get(key);
	}

	@Override
	public JValue set(ParamValue paramValue, Object obj) {
		return set(paramValue.getSource(), obj);
	}

	@Override
	public Object get(ParamValue paramValue) {
		return get(paramValue.getSource());
	}

	@Override
	public JValue getJValue(ParamValue paramValue) {
		return getJValue(paramValue.getSource());
	}
}
