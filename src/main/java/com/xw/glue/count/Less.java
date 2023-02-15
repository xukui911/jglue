package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.Value;

public class Less extends Value<Boolean> implements Count<BoolValue>{
	private Value<?> value;
	private Value<?> value2;
	
	public Less(Value<?> value, Value<?> value2) {
		this.value = value;
		this.value2 = value2;
	}

	@Override
	public BoolValue count(JGlueContext context) {
		return new BoolValue(value(context));
	}

	@Override
	public Boolean value(JGlueContext context) {
		Number left = (Number) this.value.value(context);
		Number right = (Number) this.value2.value(context);
		return left.doubleValue() < right.doubleValue();
	}

	public Value<?> getValueL() {
		return value;
	}

	public Value<?> getValueR() {
		return value2;
	}
	
	
}
