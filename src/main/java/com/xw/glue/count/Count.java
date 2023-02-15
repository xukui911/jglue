package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.value.Value;

public interface Count <R extends Value<?>> {
	
	public  R count(JGlueContext context);
}
