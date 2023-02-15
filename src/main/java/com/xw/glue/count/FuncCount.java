package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.Value;

public class FuncCount implements Count<Value<?>> {
	Value<?> value;
	
	public FuncCount(Value<?> value) {
		this.value = value;
	}
	@Override
	public Value<?> count(JGlueContext context) {
		return new ObjValue(null);
	}

}
