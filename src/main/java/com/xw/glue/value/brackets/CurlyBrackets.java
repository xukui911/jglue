package com.xw.glue.value.brackets;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.value.Value;

/**
 * 波形括号 大括号 {}
 * @author xukui
 *
 */
public class CurlyBrackets extends Value<Object>{
	List<Word> words;
	public CurlyBrackets(List<Word> words) {
		this.words = words;
	}

	@Override
	public Object value(JGlueContext context) {
		return null;
	}
}
