package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.xw.glue.lexical.word.Word;

/**
  * @Desc程序片段
  * @author XK
  *
  */
public class Fragment {
	String content;
	List<Word> words;
	Map<String,Func> funcMap;
	public Fragment(String content) {
		this.content = content;
	}
	
	public Fragment(List<Word> words) {
		this.words = words;
	}
	
	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Word word: words) {
			builder.append(word.toString()).append("\n");
		}
		return builder.toString();
	}

	public void addWord(Word word) {
		if (words==null) {
			words = new ArrayList<Word>();
		}
		words.add(word);
	}
	
	public void addFunc(Func func) {
		if (funcMap==null) {
			funcMap = new HashMap<String, Func>();
		}
		funcMap.put(func.getFuncName(), func);
	}
	
	public Word getLast() {
		if(words!=null&& words.size()>0) {
			return words.get(words.size()-1);
		}
		return null;
	}
	
	public void removeLast() {
		if (words!=null && words.size() > 0) {
			words.remove(words.size() - 1);
		}
	}
}
