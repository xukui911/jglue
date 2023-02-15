package com.xw.glue.func;

import com.xw.glue.context.JGlueContext;

public class PrintFunc extends AbstractFunc {

	public PrintFunc() {
		super("print");
		super.addParam("str");
	}

	@Override
	public Object exec(JGlueContext context, Object[] objs) {
		System.out.println(objs[0]);
		return null;
	}
}
