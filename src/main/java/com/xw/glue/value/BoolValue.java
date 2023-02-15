package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;

public class BoolValue extends Value<Boolean>{
	private Boolean value;
	
	public BoolValue(String src) {
		super(src);
		this.value = Boolean.valueOf(src);
	}
	
	public BoolValue(boolean value) {
		super(String.valueOf(value));
		this.value = value;
	}

	@Override
	public Boolean value(JGlueContext context) {
		return value;
	}
}
