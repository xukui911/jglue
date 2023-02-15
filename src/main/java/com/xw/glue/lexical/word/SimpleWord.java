package com.xw.glue.lexical.word;

public class SimpleWord extends Word {
	StringBuilder word;
	String value;

	
	public SimpleWord (int line) {
		super(line);
		this.word = new StringBuilder();
	}
	
	public SimpleWord (int line, String wordStr) {
		super(line);
		this.word = new StringBuilder(wordStr);
	}
	
	public Word append(char ch) {
		word.append(ch);
		return this;
	}
	
	public char lastChar() {
		return word.charAt(word.length()-1);
	}
	
	public void removeLastChar() {
		word.deleteCharAt(word.length()-1);
	}
	
	public void removeLastChar(int size) {
		word.delete(word.length()-size, word.length());
	}
	
	public int length() {
		return word.length();
	}
	
	
	public String toString() {
		return word.toString();
	}
	
	public static void main(String[] args) {
		Word word = new SimpleWord(1);
		word.append('1');
		word.append('2');
		word.append('3');
		word.append('4');
		System.out.println(word.lastChar());
		word.removeLastChar(2);
		System.out.println(word.toString());
		word.append('5');
		System.out.println(word.toString());
	}
}
