package com.xw.glue.value;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.oop.ListGrammar;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.object.JglueArray;
import com.xw.glue.properties.JProperties;

public class ListValue extends Value<JglueArray>{
	Grammar parentGrammar;
	List<Word> words;
	JProperties properteis;
	public ListValue(List<Word> words, JProperties properteis, Grammar parentGrammar) {
		this.parentGrammar = new ListGrammar(parentGrammar);
		StringBuilder builder = new StringBuilder();
		if(words!=null) {
			for(Word word: words) {
				builder.append(word.toString()).append(" ");
			}
		}
		this.words = words;
		this.properteis = properteis;
		setSource(builder.toString());
	}
	
	@Override
	public JglueArray value(JGlueContext context) {
		return new JglueArray(words, context, properteis, parentGrammar).parse();
	}
}
