package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.Value;

public class NotEquals extends Value<Boolean> implements Count<BoolValue>{
	private Value<?> value;
	private Value<?> value2;
	
	public NotEquals(Value<?> value, Value<?> value2) {
		this.value = value;
		this.value2 = value2;
	}

	@Override
	public BoolValue count(JGlueContext context) {
		return new BoolValue(value(context));
	}

	@Override
	public Boolean value(JGlueContext context) {
		String val1 = value.value(context).toString();
		String val2 = value2.value(context).toString();
		if(ExpressUtils.isNumber(val1) && ExpressUtils.isNumber(val2)) {
			return Double.parseDouble(val1) != Double.parseDouble(val2);
		}
		return !val1.equals(val2);
	}
}
