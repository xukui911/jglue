package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.JValue;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value;

public class AddDouble extends Value<Number> implements Count<NumberValue>{
	ParamValue value;
	
	public AddDouble(Value<?> value) {
		this.value = (ParamValue)value;
		this.setSource(value.getSource());
	}
	@Override
	public NumberValue count(JGlueContext context) {
		Number number = value(context);
		return new NumberValue(number);
	}
	@Override
	public Number value(JGlueContext context) {
		JValue jvalue = context.getJValue(value);
		Number number = Add.addMyself((Number)jvalue.getValue(), 1);
		jvalue.setValue(number);
		return number;
	}
	public Value<?> getValue() {
		return value;
	}
	
	public ParamValue getValueL() {
		return value;
	}
}
