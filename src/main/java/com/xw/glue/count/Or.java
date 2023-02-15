package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.Value;

public class Or extends Value<Boolean> implements Count<BoolValue>{
	private Value<?> value;
	private Value<?> value2;
	private boolean isDouble;
	
	public Or(Value<?> value, Value<?> value2) {
		this(value, value2, false);
	}
	
	public Or(Value<?> value, Value<?> value2, boolean isDouble) {
		this.value = value;
		this.value2 = value2;
		this.isDouble = isDouble;
	}

	@Override
	public BoolValue count(JGlueContext context) {
		return new BoolValue(value(context));
	}

	@Override
	public Boolean value(JGlueContext context) {
		if (!isDouble) {
			return (Boolean)value.value(context) | (Boolean)value2.value(context);
		}
		return (Boolean)value.value(context) || (Boolean)value2.value(context);
	}
}
