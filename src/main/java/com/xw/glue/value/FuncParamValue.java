package com.xw.glue.value;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.FuncUtils;

public class FuncParamValue extends Value<Object[]>{
	private List<Express> paramExpressList;
	Grammar parentGrammar;
	JProperties properties;
	JProperties paramProperties;
	int line;
	
	public FuncParamValue(List<Word> words, JProperties properties, Grammar parentGrammar) {
		this.parentGrammar = parentGrammar;
		if(properties==null) {
			properties = new JProperties();
		}
		this.properties = properties;
		paramProperties = properties.copy().setDefValue(true);
		
		if(words.size()>2) {
			line = words.get(0).getLine();
			paramExpressList = FuncUtils.getParamExpressList(words, properties, this.parentGrammar);
		}
	}

	public List<Express> getParamExpressList() {
		return paramExpressList;
	}

	public void setParamExpressList(List<Express> paramExpressList) {
		this.paramExpressList = paramExpressList;
	}

	public Grammar getParentGrammar() {
		return parentGrammar;
	}

	public void setParentGrammar(Grammar parentGrammar) {
		this.parentGrammar = parentGrammar;
	}

	public JProperties getParamProperties() {
		return paramProperties;
	}

	public void setParamProperties(JProperties paramProperties) {
		this.paramProperties = paramProperties;
	}

	public JProperties getProperties() {
		return properties;
	}

	public void setProperties(JProperties properties) {
		this.properties = properties;
	}

	@Override
	public Object[] value(JGlueContext context) {
		Object[] vals = new Object[paramExpressList.size()];
		for(int i=0;i<paramExpressList.size();i++) {
			Express express = paramExpressList.get(i);
			try {
				vals[i] = express.execute(context);
			} catch (GlueException e) {
				throw e;
			} catch (Exception e) {
				throw new GlueException("执行"+ express.getExpress() + "报错", ((JGlueExpress)express).getLine());
			}
		}
		return vals;
	}
}
