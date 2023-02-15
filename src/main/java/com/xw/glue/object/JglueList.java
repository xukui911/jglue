package com.xw.glue.object;

import java.util.ArrayList;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.grammar.Grammar;

public class JglueList extends ArrayList<Object>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String jglueSon;
	private int parseIndex;
	private char[] chars;
	private JGlueContext context;
	private Grammar parentGrammar;
	
	public JglueList(String jglueSon) {
		this(jglueSon, null);
	}
	
	public JglueList(String jglueSon, JGlueContext context) {
		this(jglueSon, context, null);
	}
	
	public JglueList(String jglueSon, JGlueContext context, Grammar parentGrammar) {
		this.jglueSon = jglueSon;
		this.chars = jglueSon.toCharArray();
		this.parseIndex = 0;
		this.context = context;
		this.parentGrammar = parentGrammar;
	}
	
	public JglueList(char[] chars, int startIndex, JGlueContext context, Grammar parentGrammar) {
		this.chars = chars;
		this.parseIndex = startIndex;
		this.context = context;
		this.parentGrammar = parentGrammar;
	}
	public JglueList parse() {
		JglueList list = parse(chars);
		return list;
	}
	public JglueList parse(char[] chars) {
		int objStart = ++parseIndex;
		JglueObject object = new JglueObject(chars, objStart, context, parentGrammar);
		for(;parseIndex<chars.length;parseIndex = object.getParseIndex()) {
			if(chars[parseIndex] == ']') {
				parseIndex ++;
				break;
			}
			if(chars[parseIndex] == ',' ||
					chars[parseIndex] == ' ') {
				object.parseIndexAdd();
				continue;
			}
			add(object.parseValue(chars));
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
