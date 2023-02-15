package com.xw.glue.func;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.grammar.Result;
import com.xw.glue.grammar.exec.GrammarElement;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.utils.ExpressUtils;

public abstract class AbstractInstFunc<T> extends AbstractFunc{
	public AbstractInstFunc(String funcName) {
		super(funcName);
	}
	public abstract Object exec(JGlueContext context, Object[] objs, T instance);
	
	public Result exec(JGlueContext context, T instance) {
		JGlueStack.push(new GrammarElement(this, context));
		try {
			Object[] objs = null;
			if(params!=null && params.size()>0) {
				objs = new Object[params.size()];
				for(int i=0;i<params.size();i++) {
					objs[i] = context.get(params.get(i));
				}
			}
			Object obj = exec(context, objs, instance);
			return ExpressUtils.buildResult(obj, null);
		}finally {
			JGlueStack.pop();
		}
	}
	
	public Object exec(JGlueContext context, Object[] objs) {
		throw new GlueException("AbstractInstFunc not allow call exec(JGlueContext context, Object[] objs)");
	}
}
