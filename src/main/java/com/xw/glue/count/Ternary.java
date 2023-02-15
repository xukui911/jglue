package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.Value;

//双目运算符 a > b ? "1" : "2"
public class Ternary extends Value<Object> implements Count<Value<?>> {
	Value<?> value;
	Value<?> value2;
	public Ternary(Value<?> value, Value<?> value2) {
		this.value = value;
		this.value2 = value2;
	}
	@Override
	public Value<?> count(JGlueContext context) {
		return new ObjValue(value(context));
	}
	
	@Override
	public Object value(JGlueContext context) {
		TernaryChoose choose = (TernaryChoose)value2;
		Boolean isTrue = (Boolean)value.value(context);
		if(isTrue) {
			return choose.value.value(context);
		}
		return choose.value2.value(context);
	}

	
	public static class TernaryChoose extends Value<Object> implements Count<Value<?>> {
		Value<?> value;
		Value<?> value2;
		public TernaryChoose(Value<?> value, Value<?> value2) {
			super(value.getSource());
			this.value = value;
			this.value2 = value2;
		}
		
		@Override
		public Value<?> count(JGlueContext context) {
			return new ObjValue(this);
		}

		@Override
		public Object value(JGlueContext context) {
			return this;
		}
	}
}
