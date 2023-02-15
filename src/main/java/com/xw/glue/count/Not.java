package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.Value;

public class Not extends Value<Boolean> implements Count<BoolValue> {
	Value<?> value;
	public Not(Value<?> value) {
		this.value = value;
	}
	@Override
	public BoolValue count(JGlueContext context) {
		Boolean isTrue = value(context);
		return new BoolValue(isTrue);
	}
	@Override
	public Boolean value(JGlueContext context) {
		Object obj = this.value.value(context);
		if (obj==null) {
			return true;
		}
		if (obj instanceof Boolean) {
			return !((Boolean)obj);
		}
		if("0".equals(obj.toString())) {
			return true;
		}
		return false;
	}
}
