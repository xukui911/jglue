package com.xw.glue.value;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.ExpressUtils;
/**
 * 中括号格式访问属性map['a'], list[0]
 * @ClassName:  ParamExpressValue   
 * @Description:   
 * @author: XuKui
 * @date:   2022年12月16日 下午3:30:07      
 * @Copyright:
 */
public class ParamExpressValue extends Value<Object> {
	Value<?> keyValue;
	public ParamExpressValue(String paramCode) {
		super(paramCode);
	}
	
	public ParamExpressValue(List<Word> words, JProperties properties, Grammar parentGrammar) {
		super(words.get(0).toString());
		if(words.size() > 2) {
			words.remove(0);
			words.remove(words.size() - 1);
			if (words.size() == 1) {
				keyValue = ExpressUtils.parseValue(words, words.get(0).getLine(), properties, parentGrammar, null, false, null);
			} else {
				keyValue = new ExpressValue(new JGlueExpress(words, properties, parentGrammar));
			}
		} else {
			GlueException ex = new GlueException("wrong param style!!");
			ex.setLine(words.get(0).getLine());
			throw ex;
		}
	}
	
	@Override
	public Object value(JGlueContext context) {
		return keyValue.value(context);
	}

	public Value<?> getKeyValue() {
		return keyValue;
	}
	
	public Object getTarget(JGlueContext context) {
		throw new GlueException("not support method!!!");
	}
}
