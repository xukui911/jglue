package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.count.NumberCount.NumberTypeEnum;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.StrValue;
import com.xw.glue.value.Value;

public class Add extends Value<Object> implements Count<Value<?>>{
	Value<?> left;
	Value<?> right;
	
	public Add(Value<?> value, Value<?> value2) {
		this.left = value;
		this.right = value2;
	}

	@Override
	public Value<?> count(JGlueContext context) {
		Object obj = value(context);
		if (obj instanceof String) {
			return new StrValue((String)obj);
		} else {
			return new NumberValue((Number)obj);
		}
	}

	public Object value(JGlueContext context) {
		Object obj = left.value(context);
		Object obj2 = right.value(context);
		if(obj==null && obj2==null) {
			return null;
		}
		if(obj instanceof Number && obj2 instanceof Number) {
			Object rtn = add((Number)obj, (Number)obj2);
			return rtn;
		} else {
			Object rtn = String.valueOf(obj).concat(String.valueOf(obj2));
			return rtn;
		}
	}
	
	public String getStr(String str) {
		if(str.charAt(0)=='\'') {
			return str.substring(1, str.length()-1);
		}
		return str;
	}
	
	public static Number add(Number number1, Number number2) {
		NumberTypeEnum type1 = NumberCount.getNumberTypeEnum(number1.getClass());
		NumberTypeEnum type2 = NumberCount.getNumberTypeEnum(number2.getClass());
		NumberTypeEnum type = type1.getSort() > type2.getSort() ? type1: type2;
		return type.add(number1, number2);
	}
	
	public static Number addMyself(Number number1, Integer number2) {
		com.xw.glue.count.NumberCount.NumberTypeEnum type1 = NumberCount.getNumberTypeEnum(number1.getClass());
		NumberTypeEnum type2 = NumberTypeEnum.INT;
		NumberTypeEnum type = type1.getSort() > type2.getSort() ? type1: type2;
		return type.add(number1, number2);
	}
	
	public String toString() {
		return "(" + this.left + " + " + right.toString() +")";
	}

	public Value<?> getLeft() {
		return left;
	}

	public void setLeft(Value<?> left) {
		this.left = left;
	}

	public Value<?> getRight() {
		return right;
	}

	public void setRight(Value<?> right) {
		this.right = right;
	}
}
