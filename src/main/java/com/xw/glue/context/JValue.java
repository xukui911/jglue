package com.xw.glue.context;

public class JValue {
	Object value;
	String key;
	public JValue(String key, Object value) {
		this.value = value;
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
} 