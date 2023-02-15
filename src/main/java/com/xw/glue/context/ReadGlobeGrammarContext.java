package com.xw.glue.context;

import com.xw.glue.exception.GlueException;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.value.ParamValue;

public class ReadGlobeGrammarContext extends GrammerContext {
	JValue[] jValues;
	Grammar grammar;
	public ReadGlobeGrammarContext(JGlueContext globeContext, Grammar grammar) {
		super(globeContext);
		this.grammar = grammar;
		this.jValues = new JValue[grammar.getParamSize()];
	}
	@Override
	public JValue getJValue(String key) {
		JValue value = paramContext.get(key);
		if(value==null) {
			return globeContext.getJValue(key);
		}
		return value;
	}
	@Override
	public JValue set(ParamValue paramValue, Object obj) {
		if(paramValue.getOffset() == grammar.getOffset()) {
			JValue value = jValues[paramValue.getIndex()];
			if(value == null) {
				value = new JValue(paramValue.getSource(), obj);
				jValues[paramValue.getIndex()] = value;
				return value;
			} else {
				value.value = obj;
				return value;
			}
		} else {
			return globeContext.set(paramValue, obj);
		}
	}
	@Override
	public Object get(ParamValue paramValue) {
		if(paramValue.getOffset() == grammar.getOffset()) {
			if(paramValue.getIndex() >= jValues.length) {
				throw new GlueException("not defined param:" + paramValue.getSource());
			}
			JValue jvalue = jValues[paramValue.getIndex()];
			if(jvalue != null) {
				return jvalue.value;
			} else {
				if (!globeContext.contains(paramValue.getSource())) {
					throw new GlueException("not defined param:" + paramValue.getSource());
				} else {
					Object globeValue = globeContext.get(paramValue.getSource());
					if (globeValue!=null && globeValue instanceof JValue) {
						jvalue = (JValue)globeValue;
						jValues[paramValue.getIndex()] = jvalue;
						return jvalue.getValue();
					} else {
						return globeValue;
					}
				}
			}
		} else {
			return globeContext.get(paramValue);
		}
	}
	
	
	@Override
	public JValue getJValue(ParamValue paramValue) {
		if(paramValue.getOffset() == grammar.getOffset()) {
			return jValues[paramValue.getIndex()];
		} else {
			return globeContext.getJValue(paramValue);
		}
	}
	public Grammar getGrammar() {
		return grammar;
	}
	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}
}
