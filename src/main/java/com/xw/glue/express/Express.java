package com.xw.glue.express;

import com.xw.glue.context.JGlueContext;

public interface Express {
	
	public <T> T execute(JGlueContext context);
	
	public boolean validate();
	
	public String getExpress();
}
