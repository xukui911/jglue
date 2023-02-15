package com.xw.glue.func;

import com.xw.glue.context.JGlueContext;

/**
 * @Desc 截取字符串
 * @author xukui
 *
 */
public class SubstrFunc extends AbstractFunc {

	public SubstrFunc() {
		super("substr");
		addParam("str");
		addParam("start");
		addParam("end");
	}

	@Override
	public Object exec(JGlueContext context, Object[] objs) {
		String str = (String)objs[0];
		int start = (Integer)objs[1];
		int end = (Integer)objs[2];
		String result = str.substring(start, end);
		return result;
	}
	
}
