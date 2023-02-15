package com.xw.glue.express;

import com.xw.glue.count.Count;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.Value;

public abstract class CountBuilder <R extends Value<?>> {
	Value<?> value;
	Value<?> value2;
	Grammar parentGrammar;
	int valueLen = 2;
		
	public CountBuilder<R> one(Value<?> value) {
		this.value = value;
		return this;
	}
	
	public CountBuilder<R> parentGrammar(Grammar parentGrammar) {
		this.parentGrammar = parentGrammar;
		return this;
	}
	
	public CountBuilder<R> two(Value<?> value) {
		this.value2 = value;
		return this;
	}
	
	public CountBuilder<R> valueLen(int valueLen) {
		this.valueLen = valueLen;
		return this;
	}
	
	public abstract Count<R> build(JProperties properties) throws Exception;
}
