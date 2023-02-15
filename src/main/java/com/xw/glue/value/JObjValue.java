package com.xw.glue.value;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.oop.ObjectGrammar;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.object.JglueObj;
import com.xw.glue.properties.JProperties;

public class JObjValue extends Value<JglueObj>{
	Grammar parentGrammar;
	JProperties properteis;
	List<Word> words;
	
	public JObjValue(List<Word> words, JProperties properteis, Grammar parentGrammar) {
		this.parentGrammar = new ObjectGrammar(parentGrammar);
		StringBuilder builder = new StringBuilder();
		if(words!=null) {
			for(Word word: words) {
				builder.append(word.toString());
			}
		}
		this.words = words;
		this.properteis = properteis;
		setSource(builder.toString());
	}
	
	@Override
	public JglueObj value(JGlueContext context) {
		return new JglueObj(this.words, context, properteis, parentGrammar).parse();
	}
}
