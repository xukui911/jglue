package com.xw.glue.value;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.lexical.word.Word;

public abstract class Value<T> implements IValue {
	String source;
	public Value() {}
	public Value(String source) {
		this.source = source;
	}
	
	public Value(List<Word> words) {
	
	}
	
	public T value() {
		return (T)null;
	};
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public Object getParamValue(JGlueContext context, String key) {
		Object value = context.get(key);
		return value;
	}
	
	public static enum ValueType {
		VOID("void"),
		STRING("string"),
		NUMBER("number"),
		FUNC("func"),
		OBJECT("object"),
		LIST("array"),
		CONTINUE("continue"),
		BREAK("break"),
		BOOLEAN("boolean"),
		UNDEFINED("undefined");
		
		private String alias;
		
		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		ValueType(String name) {
			this.alias = name;
		}
	} 
	
	public abstract T value(JGlueContext context);

}
