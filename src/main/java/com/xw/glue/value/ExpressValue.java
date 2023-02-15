package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.properties.JProperties;

public class ExpressValue extends Value<Object> {
	Express express;
	public ExpressValue(Express express) {
		super(express.getExpress());
		this.express = express;
	}
	
	public ExpressValue(String express, int line, JProperties properties, Grammar parentGrammar) {
		super(express);
		this.express = new JGlueExpress(express, properties, line, parentGrammar);
	}
	
	@Override
	public Object value(JGlueContext context) {
		return express.execute(context);
	}

	@Override
	public String toString() {
		return express.toString();
	}
	@Override
	public Object value() {
		return null;
	}
	
}
