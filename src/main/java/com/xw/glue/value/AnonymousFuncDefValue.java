package com.xw.glue.value;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.RuntimeFunc;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;

public class AnonymousFuncDefValue extends FuncDefValue {
	List<Word> paramWords;
	int wordSize;
	public AnonymousFuncDefValue(List<Word> words, List<Word> paramWords,JProperties properties, Grammar parentGrammar) {
		super(words, properties, parentGrammar);
		this.paramWords = paramWords;
	}
	@Override
	public Object value(JGlueContext context) {
		return new RuntimeFunc(func, context);
	}
	
	public int getWordSize() {
		return wordSize;
	}
	public void setWordSize(int wordSize) {
		this.wordSize = wordSize;
	}
}
