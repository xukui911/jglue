package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;

public class LongValue extends Value<Long> {
	String src;
	Long constNumber;
	StrValue strValue;
	public LongValue(Long value) {
		super(String.valueOf(value));
		constNumber = value;
	}

	@Override
	public Long value(JGlueContext context) {
		 return (Long)constNumber;
	}

	@Override
	public String toString() {
		return source;
	}
}
