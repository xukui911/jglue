package com.xw.glue.lexical.word;

public abstract class Word {
	int line;
	String value;
	boolean isLineLast;
	boolean isTrans;
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public boolean isTrans() {
		return isTrans;
	}
	public void setTrans(boolean isTrans) {
		this.isTrans = isTrans;
	}
	public Word (int line) {
		this.line = line;
	}
	
	public abstract Word append(char ch);
	
	public abstract char lastChar();
	
	public abstract void removeLastChar();
	
	public abstract void removeLastChar(int size);
	
	public abstract int length();
	
	public boolean isLineLast() {
		return isLineLast;
	}
	public void setLineLast(boolean isLineLast) {
		this.isLineLast = isLineLast;
	}

}
