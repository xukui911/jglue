package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.count.NumberCount.NumberTypeEnum;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.Value;

public class Mod extends Value<Number> implements Count<NumberValue>{
	private Value<?> value;
	private Value<?> value2;
	
	public Mod(Value<?> value, Value<?> value2) {
		this.value = value;
		this.value2 = value2;
	}

	@Override
	public NumberValue count(JGlueContext context) {
		return new NumberValue(value(context));
	}

	@Override
	public Number value(JGlueContext context) {
		Number number1 = (Number)value.value(context);
		Number number2 = (Number)value2.value(context);
		NumberTypeEnum type1 = NumberCount.getNumberTypeEnum(number1.getClass());
		NumberTypeEnum type2 = NumberCount.getNumberTypeEnum(number2.getClass());
		NumberTypeEnum type = type1.getSort() > type2.getSort() ? type1: type2;
		return type.mod(number1, number2);
	}
}
