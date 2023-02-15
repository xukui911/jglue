package com.xw.glue.grammar;

import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.value.Value.ValueType;

public class Result {
	private Object val;
	public Object getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	private ValueType type;
	
	public Result (Object val) {
		this(val, null);
	}
	
	public Result (Object val, ValueType type) {
		this.val = val;
		this.type = type;
	}
	
	
	public ValueType getType() {
		if(type==null) {
			type = ExpressUtils.getValueType(val);
		}
		return type;
	}
	
	public String toString() {
		String typeStr = getType() ==null ? "void" : type.getAlias();
		Object strVal = val;
		if(val instanceof String) {
			strVal = "'" + val + "'";
		}
		return "{val:" + strVal + ", type: '" + typeStr + "'}";
	}
}


