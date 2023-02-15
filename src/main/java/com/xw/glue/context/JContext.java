package com.xw.glue.context;

import com.xw.glue.exception.GlueException;

public abstract class JContext implements JGlueContext {

	@Override
	public String get(String key) {
		return getString(key);
	}

	@Override
	public JValue set(String name, Object obj) {
		throw new GlueException("not allow operator set method!!");
	}

	@Override
	public String[] keys() {
		throw new GlueException("not allow operator keys method!!");
	}

	@Override
	public boolean contains(String key) {
		throw new GlueException("not allow operator contains method!!");
	}
	
	public abstract String getString(String key);
}
