package com.xw.glue.object;

import java.util.ArrayList;
import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.ParamValue;

public class JglueArray extends ArrayList<Object>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Word> words;
	private JGlueContext context;
	private Grammar parentGrammar;
	private JProperties properties;
	private int parseIndex;
	
	public JglueArray(List<Word> words, JGlueContext context, JProperties properteis, Grammar parentGrammar) {
		this(words, 0, context, properteis, parentGrammar);
	}
	
	public JglueArray(List<Word> words, int startIndex, JGlueContext context, JProperties properties, Grammar parentGrammar) {
		this.words = words;
		this.parseIndex = startIndex;
		this.context = context;
		this.parentGrammar = parentGrammar;
		this.properties = properties;
		ParamValue paramValue = parentGrammar.getParamValue("this");
		if(paramValue!=null) {
			this.context = ContextFactory.getContext(context, parentGrammar);
			this.context.set(paramValue, this);
		} else {
			this.context = context;
		}
	}
	
	public JglueArray parse() {
		JglueArray list = parse(words);
		return list;
	}
	public JglueArray parse(List<Word> words) {
		int count = 1;
		List<Word> eWords = new ArrayList<Word>();
		boolean first = false;
		for(this.parseIndex++; this.parseIndex < words.size(); parseIndex++) {
			Word word = words.get(parseIndex);
			if(!first) {
				first = true;
				if ("{".equals(word.toString())) {
					JglueObj child = new JglueObj(words, parseIndex, context, properties, parentGrammar).parse();
					parseIndex = child.getParseIndex();
					add(child);
					if(",".equals(words.get(parseIndex + 1).toString())) {
						parseIndex ++;
					}
					continue;
				} else if ("[".equals(word.toString())) {
					JglueArray child = new JglueArray(words, parseIndex, context, properties, parentGrammar).parse();
					parseIndex = child.getParseIndex();
					add(child);
					if(",".equals(words.get(parseIndex + 1).toString())) {
						parseIndex ++;
					}
					continue;
				}
			}
			if("(".equals(word.toString()) 
					|| "[".equals(word.toString())
					|| "{".equals(word.toString())) {
				count++;
			}
			if(")".equals(word.toString()) 
					|| "]".equals(word.toString())
					|| "}".equals(word.toString())) {
				count--;
			}
			if(count==0) {
				break;
			}
			if(count==1 && ",".equals(word.toString())) {
				add(new JGlueExpress(eWords, properties, parentGrammar).execute(context));
				eWords = new ArrayList<Word>();
				first = false;
			} else {
				eWords.add(word);
			}
		}
		if(eWords.size() > 0) {
			add(new JGlueExpress(eWords, properties, parentGrammar).execute(context));
		}
		return this;
	}

	public int getParseIndex() {
		return parseIndex;
	}

	public Grammar getParentGrammar() {
		return parentGrammar;
	}

	public void setParentGrammar(Grammar parentGrammar) {
		this.parentGrammar = parentGrammar;
	}
}
