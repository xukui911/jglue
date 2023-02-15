package com.xw.glue.value;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Grammars;

public class LibValue extends Value<Grammars>{
	Grammars grammars;
	public LibValue(String libName, Grammars libGrammar, Grammar parentGrammar) {
		this.setSource(libName);
		this.grammars = libGrammar;
	}
	
	@Override
	public Grammars value(JGlueContext context) {
		return grammars;
	}
	
	public IFunc getFunc(String funcName) {
		return grammars.getSubFunc(funcName).getFunc();
	}

}
