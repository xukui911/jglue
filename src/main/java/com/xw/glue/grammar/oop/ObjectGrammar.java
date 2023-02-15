package com.xw.glue.grammar.oop;

import java.util.Stack;

import com.xw.glue.grammar.Grammar;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.FuncUtils;
import com.xw.glue.utils.ParamUtils;
import com.xw.glue.value.ParamValue;

public class ObjectGrammar extends Grammar {
	public ObjectGrammar(Grammar parent) {
		this.setParent(parent);
		setOffset(FuncUtils.getNextIndex());
		ParamValue paramValue = ParamUtils.createParamValue("this", null);
		addParamValue(paramValue);
	}
	@Override
	public void parse(Stack<Word> stack) {
		
	}
}
