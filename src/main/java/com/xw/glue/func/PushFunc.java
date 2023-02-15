package com.xw.glue.func;

import java.util.List;

import com.xw.glue.context.JGlueContext;

public class PushFunc extends AbstractFunc {

	public PushFunc() {
		super("push");
		addParam("list");
		addParam("obj");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object exec(JGlueContext context, Object[] objs) {
		List list = (List)objs[0];
		list.add(objs[1]);
		return null;
	}

}
