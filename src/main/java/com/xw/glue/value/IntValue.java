package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;

public class IntValue extends Value <Integer> {
	int value;
	public IntValue(Integer value) {
		super(String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Integer value(JGlueContext context) {
		return value;
	}
}
