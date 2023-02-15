package com.xw.glue.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.func.factory.AbstractInsFuncFactory;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.ParamValue;

public class JglueObj extends JglueClass<JglueObj> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private static String prefix = "J:";
	List<Word> words;
	private JGlueContext context;
	private Grammar parentGrammar;
	private JProperties properteis;
	private int parseIndex;private static AbstractInsFuncFactory<JglueObj> factory;
	static {
		factory = new AbstractInsFuncFactory<JglueObj>();
		factory.add(new ToStringFunc());
	}
	
	public JglueObj(List<Word> words) {
		this(words, null, null, null);
	}
	
	public JglueObj(List<Word> words, JGlueContext context, JProperties properteis, Grammar parentGrammar) {
		this(words, 0, context, properteis, parentGrammar);
	}
	
	public JglueObj(List<Word> words, int startIndex, JGlueContext context, JProperties properteis, Grammar parentGrammar) {
		super(context, null);
		this.parseIndex = startIndex;
		this.words = words;
		this.parentGrammar = parentGrammar;
		this.properteis = properteis;
		ParamValue paramValue = parentGrammar.getParamValue("this");
		if(paramValue!=null) {
			this.context = ContextFactory.getContext(context, parentGrammar);
			this.context.set(paramValue, this);
		} else {
			this.context = context;
		}
	}
	
	public JglueObj parse() {
		return parse(words);
	}
	
	public JglueObj parse(List<Word> words) {
		for(parseIndex++;parseIndex<words.size();parseIndex++) {
			if("}".equals(words.get(parseIndex).toString())) {
				break;
			}
			put(parseKey(), parseValue());
		}
		return this;
	}
	
	public String parseKey() {
		String key = words.get(parseIndex).toString();
		parseIndex++;
		if(!":".equals(words.get(parseIndex).toString())) {
			throw new GlueException("wrong jglue object!!!");
		}
		parseIndex++;
		return key;
	}
	
	public void check() {
		 
	}
	
	public Object parseValue() {
		int count = 1;
		List<Word> eWords = new ArrayList<Word>();
		for(;parseIndex<words.size();parseIndex++) {
			Word word = words.get(parseIndex);
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
				parseIndex --;
				break;
			} else if(count==1 && ",".equals(word.toString())){
				break;
			}else {
				eWords.add(word);
			}
		}
		return new JGlueExpress(eWords, properteis, this.parentGrammar).execute(context);
	}

	public void setParseIndex(int parseIndex) {
		this.parseIndex = parseIndex;
	}
	
	public int getParseIndex() {
		return this.parseIndex;
	}
	
	public String toString(JGlueContext context) {
		StringBuilder builder = new StringBuilder();
		return builder.toString();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		for(Map.Entry<String, Object> entity: this.entrySet()) {
			if (builder.length()>1) {
				builder.append(", ");
			}
			builder.append(entity.getKey()).append(": ");
			if (entity.getValue() instanceof String) {
				builder.append("'").append(entity.getValue()).append("'");
			} else {
				builder.append(entity.getValue());
			}
		}
		String str = builder.append("}").toString();
		return str;
	}

	public void rigistFuncs() {		
	}

	public Grammar getParentGrammar() {
		return parentGrammar;
	}

	public void setParentGrammar(Grammar parentGrammar) {
		this.parentGrammar = parentGrammar;
	}

	@Override
	public AbstractInstFunc<JglueObj> buildFunc(String funcName) {
		return factory.getFunc(funcName);
	}
	
	public static class ToStringFunc extends AbstractInstFunc<JglueObj> {

		public ToStringFunc() {
			super("toString");
		}

		@Override
		public Object exec(JGlueContext context, Object[] objs, JglueObj instance) {
			if(instance!=null) {
				return instance.toString();
			}
			return null;
		}
		
	}
}
