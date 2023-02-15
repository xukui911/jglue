package com.xw.glue.object;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.JValue;
import com.xw.glue.func.AbstractInstFunc;

public class JglueInstance extends JglueClass<Object> {
	public JglueInstance(JGlueContext context, Object object) {
		super(context, object);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	@Override
	public List<JValue> registParams() {
		return null;
	}



	@Override
	public AbstractInstFunc<Object> buildFunc(String funcName) {
		return null;
	}

}
