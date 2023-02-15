package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.Value;

public class One extends Value<Object> implements Count<Value<?>> {
	Value<?> value;
	
	public One(Value<?> value) {
		this.value = value;
	}

	@Override
	public Value<?> count(JGlueContext context) {
		return new ObjValue(value(context));
	}

	@Override
	public Object value(JGlueContext context) {
		return value.value(context);
	}
}
