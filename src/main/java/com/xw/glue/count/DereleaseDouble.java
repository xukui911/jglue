package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value;

public class DereleaseDouble extends Value<Number> implements Count<NumberValue>{
	ParamValue value;
	public DereleaseDouble(Value<?> value) {
		this.value = (ParamValue)value;
		this.setSource(value.getSource());
	}
	
	@Override
	public NumberValue count(JGlueContext context) {
		Number number = value(context);
		return new NumberValue(number);
	}

	@Override
	public Number value(JGlueContext context) {
		Number number = (Number)value.value(context);
		context.set(value, Derelease.derelease(number, 1));
		return number;
	}
}
