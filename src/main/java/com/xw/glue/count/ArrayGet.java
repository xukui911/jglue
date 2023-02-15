package com.xw.glue.count;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.count.MethodExec.FuncExec;
import com.xw.glue.utils.ReflectUtils;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.Value;

public class ArrayGet extends Value<Object> implements Count<ObjValue> {
	Value<?> valueL;
	Value<?> valueR;
	FuncExec exec;
	
	public ArrayGet(Value<?> valueL, Value<?> valueR) {
		this.valueL = valueL;
		this.valueR = valueR;
	}
	
	@Override
	public ObjValue count(JGlueContext context) {
		return null;
	}

	@SuppressWarnings({"rawtypes" })
	@Override
	public Object value(JGlueContext context) {
		Object target = valueL.value(context);
		Object key = valueR.value(context);
		if (target instanceof List) {
			Integer iKey = null;
			if (key instanceof BigDecimal) {
				iKey = ((BigDecimal)key).intValue();
			} else {
				iKey = (Integer)key;
			}
			return ((List<?>)target).get(iKey);
		} else if (target instanceof Map) {
			return ((Map)target).get(key);
		} else {
			return ReflectUtils.getFieldValue(target, (String)key);
		}
	}
}
