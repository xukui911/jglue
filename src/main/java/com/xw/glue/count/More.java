package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.Value;

public class More extends Value<Boolean> implements Count<BoolValue>{
	private Value<Number> value;
	private Value<Number> value2;
	
	public More(Value<Number> value, Value<Number> value2) {
		this.value = value;
		this.value2 = value2;
	}

	@Override
	public BoolValue count(JGlueContext context) {
		Boolean isMore = value(context);
		return new BoolValue(isMore);
	}
	
	@Override
	public Boolean value(JGlueContext context) {
		return value.value(context).doubleValue() 
				> value2.value(context).doubleValue();
	}

	public Value<Number> getValueL() {
		return value;
	}

	public Value<Number> getValueR() {
		return value2;
	}
}
