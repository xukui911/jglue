package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;

public class ParamValue extends Value<Object> {
	boolean isDef;
	boolean isVar;
	String paramCode;
	long offset;
	int index;
	int type;

	public ParamValue(String paramCode) {
		super(paramCode);
	}
	
	public ParamValue(String paramCode, int offset) {
		super(paramCode);
		this.offset = offset;
	}
	
	@Override
	public Object value(JGlueContext context) {
		//return context.get(this.getSource());	
		Object obj = context.get(this);
		return obj;
	}
	
	//普通赋值
	public void assign(JGlueContext context, Object obj) {
		context.set(this, obj);
	}
	
	//直接赋值
	public void update(JGlueContext context, Object obj) {
		context.getJValue(source).setValue(obj);
	}

	public ParamValue setNotDef() {
		this.isDef = false;
		return this;
	}
	
	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
		this.paramCode = source + offset;
	}
	
	public boolean isVar() {
		return isVar;
	}

	public void setVar(boolean isVar) {
		this.isVar = isVar;
	}

	public String toString() {
		return "{paramCode: " + source+ ",offset: " + offset + ",index:" + index + "}";
	}

	public String getParamCode() {
		return paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
