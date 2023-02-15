package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.Value;

public class Equals extends BoolValue implements Count<BoolValue>{
	private Value<?> value;
	private Value<?> value2;
	
	public Equals(Value<?> value, Value<?> value2) {
		super(false);
		this.value = value;
		this.value2 = value2;
	}

	@Override
	public BoolValue count(JGlueContext context) {
		return new BoolValue(value(context));
	}

	@Override
	public Boolean value(JGlueContext context) {
		Object valL = value.value(context);
		Object valR = value2.value(context);
		if(valL==null && valR==null) {
			return true;
		}
		if(valL==null || valR==null) {
			return false;
		}
		return valL.toString().equals(valR.toString());
	}
}
