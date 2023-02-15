package com.xw.glue.grammar.stack;

import java.util.List;
import java.util.Stack;

import com.xw.glue.lexical.word.Word;

public class GrammarArrayStack extends Stack<Word> {
	Word[] words;
	int index = 0;
	public GrammarArrayStack(List<Word> words) {
		this.words = words.toArray(new Word[0]);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public synchronized Word pop() {
		return words[index++];
	}
	
	public synchronized Word peek() {
		return words[index];
	}
	
	public List<Word> start() {
		return null;
	}
	
	public List<Word> stop() {
		return null;
	}
	
	public Word push(Word item) {
		words[--index] = item;
		return item;
	}
	
	public synchronized boolean isEmpty() {
		return index == words.length;
	}
	
	public synchronized String toString() {
		 StringBuilder sb = new StringBuilder();
	        sb.append('[');
	        for (int i=0;i<words.length;i++) {
	        	Word word = words[i];
	        	sb.append(word);
	        	if(i<words.length-1) {
	        		sb.append(',').append(' ');
	        	}
	        }
	        return sb.append(']').toString();
	}
}
