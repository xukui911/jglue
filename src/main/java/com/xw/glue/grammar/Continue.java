package com.xw.glue.grammar;

import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.grammar.exec.GrammarElement;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.value.Value.ValueType;

public class Continue extends Grammar {
	private static Result RESULT_CONTINUE = ExpressUtils.buildResult(null, ValueType.CONTINUE);
	@Override
	public void parse(Stack<Word> stack) {
		Word first = stack.pop();
		Word end = stack.pop();
		if(!end.toString().equals(";")) {
			throw new GlueException(KeyWords.CONTINUE.getKeyWord() 
					+"没有以;结束(" +first.getLine()+ ")");
		}
		int index = JGlueStack.size() -1;
		while(index>=0) {
			GrammarElement element = JGlueStack.peek(index);
			if(element.getGrammar() instanceof While) {
				return;
			} else if(element.getGrammar() instanceof Func){
				break;
			}
			index--;
		}
		throw new GlueException(KeyWords.CONTINUE.getKeyWord() 
				+ "必须在循环结构体中("+first.getLine()+ ")");
	}
	
	public Result exec(JGlueContext context) {
		return RESULT_CONTINUE;
	}

}
