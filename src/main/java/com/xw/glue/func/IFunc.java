package com.xw.glue.func;

import java.util.List;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.grammar.Result;
import com.xw.glue.value.ParamValue;

public interface IFunc {
	
	public String getFuncName();
	
	public List<ParamValue> getParams();
	
	public Result exec(JGlueContext context);
	
	public boolean isAnonymous();
	
	public String getNamespace();
	
	public long getOffset();
	
}
