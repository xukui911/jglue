package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.count.Count;
import com.xw.glue.utils.ExpressUtils;

public class NumberValue extends Value<Number> {
	Number constNumber;
	Count<Value<?>> count;
	
	public NumberValue(String src) {
		super(src);
		if(ExpressUtils.isNumber(src)) {
			constNumber = ExpressUtils.parseNumber(src);
		}
	}

	public NumberValue(Number number) {
		super(String.valueOf(number));
		this.constNumber = number;
	}
	
	@Override
	public Number value(JGlueContext context) {
		return constNumber;
	}
}
