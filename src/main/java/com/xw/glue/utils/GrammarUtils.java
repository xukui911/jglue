package com.xw.glue.utils;

import java.util.Stack;

import com.xw.glue.grammar.Condition;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Sentence;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;

public class GrammarUtils {
	
	public static Grammar createGrammarByKeyWords(Word curWord,Stack<Word> stack) {
		String keyWord = curWord.toString();
		Grammar grammar = null;
		if (keyWord.equals(KeyWords.IF.getKeyWord())) {
			grammar = new Condition();			
		} else if (keyWord.equals(KeyWords.FOR.getKeyWord())) {
			throw new RuntimeException("不支持的关键字for");
		} else {
			stack.push(curWord);
			grammar = new Sentence();
		}
		return grammar;
	}
	
	public void parseWord(Word curWord, Stack<Word> stack) {
		
	}
}
