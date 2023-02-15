package com.xw.glue.value;

import java.util.ArrayList;
import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.express.Express;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Result;
import com.xw.glue.grammar.RuntimeFunc;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.FuncUtils;

public class AnonymousFuncValue extends  Value<Object> {
	AnonymousFuncDefValue funcDef;
	List<Express> paramExpresses;
	private List<Word> paramWords;
	public AnonymousFuncValue(AnonymousFuncDefValue funcDef, List<Word> words, int index) {
		this.funcDef = funcDef;
		paramWords = new ArrayList<Word>();
		this.initParam(words, index);
	}
	
	private void initParam(List<Word> words, int index) {
		int count = 0;
		for(int i=index;i<words.size();i++) {
			Word next = words.get(i);
			if("(".equals(next.toString())) {
				count ++;
			} else if(")".equals(next.toString())) {
				count --;
			}
			if (count==0) {
				break;
			} else {
				paramWords.add(next);
			}
		}
		paramExpresses = FuncUtils.getParamExpressList(paramWords, funcDef.properties, funcDef.parentGrammar);
	}
	
	@Override
	public Object value(JGlueContext context) {
		RuntimeFunc rFunc = (RuntimeFunc) funcDef.value(context);
		JGlueContext funcContext = ContextFactory.getContext(rFunc.getGlobeContext(), (Grammar)rFunc.getFunc());
		FuncUtils.initFuncParamValue(context, funcContext, rFunc, paramExpresses);
		Result result = rFunc.exec(funcContext);
		if (result!=null) {
			return result.getVal();
		}
		return null;
	}

	public int getWordSize() {
		return paramWords.size() + funcDef.getWordSize() + 1;
	}
}
