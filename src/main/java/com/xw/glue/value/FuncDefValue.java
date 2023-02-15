package com.xw.glue.value;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Func;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.RuntimeFunc;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.grammar.parse.SimpleGrammerParse;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;

public class FuncDefValue extends Value<Object> {
	Func func;
	List<Word> words;
	boolean isParam;
	JProperties properties;
	Grammar parentGrammar;
	public FuncDefValue(List<Word> words, JProperties properties, Grammar parentGrammar) {
		this(words, properties.isDefValue(), properties, parentGrammar);
	}
	
	public FuncDefValue(List<Word> words, boolean isParam, JProperties properties, Grammar parentGrammar) {
		String namesapce = null;
		this.words = words;
		this.isParam = isParam;
		this.properties = properties;
		this.parentGrammar = parentGrammar;
		if(JGlueStack.peek()!=null) {
			namesapce = JGlueStack.peek().getNamespace();
		}
		func = SimpleGrammerParse.parseFunc(words, properties, namesapce, parentGrammar);
		func.setParent(parentGrammar);
	}

	@Override
	public Object value(JGlueContext context) {
		IFunc rfunc = new RuntimeFunc(func, context);
		return rfunc;
	}
}
