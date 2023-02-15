package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value;

public class DereleaseEqual extends Value<Number> implements Count<NumberValue>{
	private ParamValue value;
	private Value<?> value2;
	
	public DereleaseEqual(Value<?> value, Value<?> value2) {
		this.value = (ParamValue)value;
		this.value2 = value2;
	}

	@Override
	public NumberValue count(JGlueContext context) {
		Number newValue = value(context);
		return new NumberValue(newValue);
	}

	@Override
	public Number value(JGlueContext context) {
		Number val = (Number)value.value(context);
		Number right = (Number)value2.value(context);
		Number newValue = Derelease.derelease(val, right);
		context.set(value, newValue);
		return newValue;
	}
}
