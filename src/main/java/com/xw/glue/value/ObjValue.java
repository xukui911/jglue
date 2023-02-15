package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;

public class ObjValue extends Value<Object> {
	Object value;
	public ObjValue(Object value) {
		super(value ==null ? null: value.toString());
		this.value = value;
	}
	@Override
	public Object value(JGlueContext context) {
		return value;
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public String toString() {
		if(value!=null) {
			return value.toString();
		}
		return null;
	}
	
}
