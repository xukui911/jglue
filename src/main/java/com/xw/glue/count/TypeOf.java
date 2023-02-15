package com.xw.glue.count;

import java.util.List;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.StrValue;
import com.xw.glue.value.Value;

public class TypeOf extends Value<String> implements Count<StrValue> {
	Value<?> value;
	public TypeOf(Value<?> value) {
		this.value = value;
	}
	@Override
	public StrValue count(JGlueContext context) {
		return new StrValue(value(context));
	}
	@Override
	public String value(JGlueContext context) {
		Object obj = value.value(context);
		if (obj==null) {
			return "null";
		}
		if(obj instanceof String) {
			return "string";
		}
		if(obj instanceof Number) {
			return "number";
		}
		if(obj instanceof List ||
				obj.getClass().isArray()) {
			return "array";
		}
		return "object";
	}

}
