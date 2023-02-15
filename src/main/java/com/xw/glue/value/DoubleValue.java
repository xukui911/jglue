package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;

public class DoubleValue extends Value <Double> {
	Double value;
	Value<?> strValue;
	public DoubleValue(String src) {
		super(src);
		this.value = Double.valueOf(src);
	}
	
	public DoubleValue(double value) {
		super(String.valueOf(value));
		this.value = value;
	}
	
	@Override
	public Double value(JGlueContext context) {
		if(strValue!=null) {
			 value = Double.valueOf(strValue.value(context).toString());
		}
		return value;
	}
}
