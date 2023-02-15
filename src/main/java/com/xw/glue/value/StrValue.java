package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.properties.JProperties;

public class StrValue extends Value<Object> {
	private String src;
	private String value;
	private JProperties properties;
	
	public StrValue(String src) {
		super(src);
		if(src!=null) {
			if(src.startsWith("'") && src.endsWith("'")) {
				this.value  = src.substring(1, src.length()-1);
			} else {
				this.value = src;
			}
		}
	}

	public JProperties getProperties() {
		return properties;
	}

	public void setProperties(JProperties properties) {
		this.properties = properties;
	}

	@Override
	public Object value(JGlueContext context) {
		return value;
	}

	@Override
	public String toString() {
		if(value==null) {
			return src;
		}
		return value.toString();
	}

	@Override
	public String value() {
		return value;
	}
}
