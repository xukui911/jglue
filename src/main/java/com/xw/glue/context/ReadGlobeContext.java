package com.xw.glue.context;

import com.xw.glue.value.ParamValue;

public class ReadGlobeContext extends GrammerContext {
	
	public ReadGlobeContext(JGlueContext globeContext) {
		super(globeContext);
	}

	@Override
	public JValue set(ParamValue paramValue, Object obj) {
		return set(paramValue.getSource(), obj);
	}
	@Override
	public Object get(ParamValue paramValue) {
		return get(paramValue.getSource());
	}
}
