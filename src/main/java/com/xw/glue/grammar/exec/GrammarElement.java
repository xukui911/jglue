package com.xw.glue.grammar.exec;

import java.util.HashMap;
import java.util.Map;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.grammar.Grammar;

public class GrammarElement {
	private Grammar grammar;
	private JGlueContext context;
	private Map<String,Object> cache = new HashMap<String,Object>();
	private String namespace;
	
	public GrammarElement(Grammar grammar) {
		this(grammar, null, grammar.getNamespace());
	}
	
	public GrammarElement(Grammar grammar, JGlueContext context) {
		this(grammar, context, null);
	}
	
	public GrammarElement(Grammar grammar, JGlueContext context, String namespace) {
		this.grammar = grammar;
		this.context = context;
		this.namespace = namespace;
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public JGlueContext getContext() {
		return context;
	}

	public void setContext(JGlueContext context) {
		this.context = context;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public void remove(String key) {
		if(cache.containsKey(key)) {
			cache.remove(key);
		}
	}
	
	public Object get(String key) {
		return cache.get(key);
	}
	
	public Object put(String key, Object obj) {
		return cache.put(key, obj);
	}
}
