package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.Value;

public class AddNumber extends Value<Number> implements Count<Value<?>>{
	private Value<?> value;
	private Value<?> value2;
	public AddNumber(Value<?> value, Value<?> value2) {
		this.value = value;
		this.value2 = value2;
	}

	@Override
	public NumberValue count(JGlueContext context) {
		return new NumberValue(value(context));
	}

	@Override
	public Number value(JGlueContext context) {
		Object obj = value.value(context);
		Object obj2 = value2.value(context);
		Number number = Add.add((Number)obj, (Number)obj2);
		return number;
	}
}
