package com.xw.glue.grammar.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.xw.glue.lexical.word.Word;

public class GrammarStack extends Stack<Word> {
	Words words = new Words();
	boolean isParsing;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public synchronized Word pop() {
		Word word = super.pop();
		if(isParsing) {
			words.add(word);
		}
		return word;
	}
	
	public boolean isParsing() {
		return isParsing;
	}
	
	public List<Word> start() {
		isParsing = true;
		return words.start();
	}
	
	public List<Word> stop() {
		return words.end();
	}
	
	public Word push(Word item) {
		Word word = super.push(item);
		if(isParsing) {
			words.remove(item);
		}
		return word;
	}
	
	public static class Words {
		Stack<List<Word>> stack = new Stack<List<Word>>();

		public void add(Word word) {
			for(List<Word> list: stack) {
				list.add(word);
			}
		}
		
		public void remove(Word word) {
			for(List<Word> list: stack) {
				if(list.size() > 0) {
					list.remove(word);
				}
			}
		}
		
		public List<Word> start() {
			List<Word> list = new ArrayList<Word>();
			stack.push(list);
			return list;
		}
		
		public List<Word> end() {
			return stack.pop();
		}
	}
}
