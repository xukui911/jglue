package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.count.NumberCount.NumberTypeEnum;
import com.xw.glue.properties.HighPrecision;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.Value;

public class Division extends Value<Number> implements Count<NumberValue>{
	private Value<?> value;
	private Value<?> value2;
	private HighPrecision highPrecision;
	
	public Division(Value<?> value, Value<?> value2, HighPrecision highPrecision) {
		this.value = value;
		this.value2 = value2;
		this.highPrecision = highPrecision;
	}

	@Override
	public NumberValue count(JGlueContext context) {
		Number number = value(context);
		return new NumberValue(number);
	}

	@Override
	public Number value(JGlueContext context) {
		Number number = division((Number)value.value(context),
				(Number)value2.value(context));
		return number;
	}
	
	public Number division(Number number1, Number number2) {
		if(highPrecision!=null) {
			return NumberTypeEnum.BIG_DECIMAL.division(number1, number2, highPrecision);
		} else {
			NumberTypeEnum type1 = NumberCount.getNumberTypeEnum(number1.getClass());
			NumberTypeEnum type2 = NumberCount.getNumberTypeEnum(number2.getClass());
			NumberTypeEnum type = type1.getSort() > type2.getSort() ? type1: type2;
			return type.division(number1, number2);
		}
	}
}
