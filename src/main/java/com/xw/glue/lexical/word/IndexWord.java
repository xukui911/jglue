package com.xw.glue.lexical.word;

public class IndexWord extends Word {
	char[] chars;
	int start;
	int end;
	IndexWord subWord;
	public IndexWord(int line, char[] chars, int start) {
		super(line);
		this.start = start;
		this.end = start;
		this.chars = chars;
	}

	@Override
	public Word append(char ch) {
		if(subWord==null) {
			this.end++;
		} else {
			subWord.append(ch);
		}
		return this;
	}

	@Override
	public char lastChar() {
		if(subWord==null) {
			return chars[end-1];
		}
		return subWord.lastChar();
	}

	@Override
	public void removeLastChar() {
		if(subWord==null) {
			subWord = new IndexWord(line, chars, end);
			end --;
		} else {
			subWord.removeLastChar();
		}
	}

	@Override
	public void removeLastChar(int size) {
		if(subWord==null) {
			subWord = new IndexWord(line, chars, end);
			end -= size;
		} else {
			subWord.removeLastChar(size);
		}
	}

	@Override
	public int length() {
		if(subWord==null) {
			return end - start;
		} else  {
			return end - start + subWord.length();
		}
	}
	
	public String toString() {
		String str = new String(chars, start, length());
		if(subWord!=null) {
			str += subWord.toString();
		}
		return str;
	}
}
