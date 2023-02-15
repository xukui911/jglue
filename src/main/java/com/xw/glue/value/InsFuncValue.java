package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.object.JglueClass;

public class InsFuncValue extends Value<Object> {
	FuncValue funcValue;
	InstanceFunc insFunc;
	public InsFuncValue(FuncValue funcValue, InstanceFunc insFunc) {
		this.funcValue = funcValue;
		this.insFunc = insFunc;
	}

	@Override
	public Object value(JGlueContext context) {
		JGlueContext funcContext = ContextFactory.getContext(context, (Grammar)funcValue.getFunc());
		Object resp = this.funcValue.value(context, funcContext, insFunc);
		return resp;
	}
	
	@SuppressWarnings("rawtypes")
	public static class InsFuncResp {
		Object target;
		JglueClass instance;
		
		public InsFuncResp(Object target, JglueClass instance) {
			this.target = target;
			this.instance = instance;
		}
		
		public Object getTarget() {
			return target;
		}
		public void setTarget(Object target) {
			this.target = target;
		}
		public JglueClass getInstance() {
			return instance;
		}
		public void setInstance(JglueClass instance) {
			this.instance = instance;
		}
	}
}
