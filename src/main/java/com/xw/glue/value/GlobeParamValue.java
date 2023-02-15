package com.xw.glue.value;

import com.xw.glue.context.GrammerContext;
import com.xw.glue.context.JGlueContext;

public class GlobeParamValue extends ParamValue {

	public GlobeParamValue(String paramCode) {
		super(paramCode);
	}
	
	public GlobeParamValue(String paramCode, int offset) {
		super(paramCode, offset);
	}
	
	@Override
	public Object value(JGlueContext context) {
		Object obj = ((GrammerContext)context).getGlobeContext().get(this);
		return obj;
	}
	
	//普通赋值
	public void assign(JGlueContext context, Object obj) {
		((GrammerContext)context).getGlobeContext().set(this, obj);
	}
}
