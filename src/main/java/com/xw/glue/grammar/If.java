package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.lexical.word.Word;

public class If extends Grammar {
	List<Word> judgeWords = new ArrayList<Word>();
	JGlueExpress judgeExpress;
	public void parse(Stack<Word> stack) {
		int count = 0;
		int line = stack.peek().getLine();
		while(!stack.isEmpty()) {
			Word word = stack.pop();
			if (word.toString().equals("(")) {
				count ++;
				if (count > 1) {
					judgeWords.add(word); 
				}
			} else if (word.toString().equals(")")) {
				if (count > 1) {
					judgeWords.add(word); 
				}
				count --;
			} else {
				judgeWords.add(word); 
			}
			if (count==0) {
				break;
			}
		}
		judgeExpress = new JGlueExpress(judgeWords, getProperties(), this);
		judgeExpress.setLine(line);
		while(!stack.isEmpty()) {
			Word word = stack.pop();
			if (word.toString().equals("{")) {
				count ++;
			} else if (word.toString().equals("}")) {
				count --;
			} else {
				parseWord(word, stack);
			}
			if (count==0) {
				break;
			}
		}
	}
	
	public boolean execJudge(JGlueContext context) {
		Boolean isTrue = judgeExpress.execute(context);
		return isTrue;
	}
}
