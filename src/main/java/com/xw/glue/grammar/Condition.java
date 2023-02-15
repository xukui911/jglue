package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;

public class Condition extends Grammar {
	List<If> ifs = new ArrayList<If>();
	Else elseDo;
	Result result;
	@Override
	public void parse(Stack<Word> stack) {
		stack.pop();
		If firstIf = new If();
		firstIf.setParent(this);
		firstIf.setProperties(properties);
		firstIf.parse(stack);
		ifs.add(firstIf);
		Word word = stack.pop();
		if(word.toString().equals(KeyWords.ELSE.getKeyWord())) {
			word = stack.pop();
			if(word.toString().equals(KeyWords.IF.getKeyWord())) {
				stack.push(word);
				parse(stack);
			} else {
				stack.push(word);
				elseDo = new Else();
				elseDo.setParent(this);
				elseDo.setProperties(properties);
				elseDo.parse(stack);
			}
		}else {
			stack.push(word);
		}
	}
	
	@Override
	public Result exec(JGlueContext context) {
		for (If ifDo: ifs) {
			if(ifDo.execJudge(context)) {
				return ifDo.exec(context);
			}
		}
		if(elseDo!=null) {
			return elseDo.exec(context);
		}
		return null;
	}
}
