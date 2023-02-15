package com.xw.glue.func;

import com.xw.glue.context.JGlueContext;

/**
 * @Desc 计算长度
 * @author xukui
 *
 */
public class LenFunc extends AbstractFunc {

	public LenFunc() {
		super("len");
		addParam("str");
	}

	@Override
	public Object exec(JGlueContext context, Object[] objs) {
		Object obj = objs[0];
		if(obj==null) {
			return null;
		}
		String str = obj.toString();
		int len = 0;
		if(str!=null) {
			len = str.length();
			if(len > 1 && str.startsWith("'") && str.endsWith("'")) {
				len -= 2;
			}
		}
		return len;
	}
}
