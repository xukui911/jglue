package com.xw.glue.grammar;

import java.util.Stack;

import com.xw.glue.lexical.word.Word;

public class Else extends Grammar {
	
	public void parse(Stack<Word> stack) {
		while(!stack.isEmpty()) {
			Word word = stack.pop();
			if (word.toString().equals("{")) {
				continue;
			} else if (word.toString().equals("}")) {
				break;
			} else {
				parseWord(word, stack);
			}
		}
	}
}
