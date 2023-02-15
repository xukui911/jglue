package com.xw.glue.lexical;

import com.xw.glue.grammar.Fragment;
import com.xw.glue.lexical.word.IndexWord;
import com.xw.glue.lexical.word.OperatorWord;
import com.xw.glue.lexical.word.SimpleWord;
import com.xw.glue.lexical.word.SplitWord;
import com.xw.glue.lexical.word.Word;

public class LexicalAnalyzer {
	String defaultSpit = " ";
	int count = 0;
	boolean isStart;
	int line = 1;
	char startChar;
	int descType = 0;
	int descCount = 0;
	public LexicalAnalyzer() {
	}
	
	public void init() {
		count = 0;
		isStart = false;
		line = 1;
	}
	
	public synchronized Fragment paser(String content) {
		init();
		Fragment fragment = new Fragment(content);
		//content = content.replaceAll("\n", "#");
		content = content.trim();
		char[] chars = content.toCharArray();
		int len = chars.length;
		Word cur = null;
		for (int i=0;i<len;i++) {
			char ch = chars[i];
			if(descType>0) {
				switch(ch) {
				  case '/': 
				  case '\n': this.descWord(ch, chars, i, cur, fragment); break;
				  default : break;
				}
			}else {
				switch(ch) {
				  case '\n': cur = lineLastWord(ch, chars, i, cur, fragment); line++;break;
				  case ' ':  
				  case ' ' : //与上一个空格不同，不同编码
				  case '\t' : 
				  case '\b' : 
				  case '\f' :
				  case '\r' : cur = blankWord(ch, chars, i, cur, fragment); break;
				  case ';' :
				  case ',' :
				  case ':':
				  case '{':
				  case '(':
				  case '[':
				  case '}':
				  case ')':
				  case ']': cur = splitWord(ch, chars, i, cur, fragment); break;
				  case '+': 
				  case '-': 
				  case '*': 
				  case '/': 
				  case '%':  
				  case '>': 
				  case '<': 
				  case '|': 
				  case '?':
				  case '&':
				  case '!':
				  case '.':
				  case '=': {
					cur = operatorWord(ch, chars, i, cur, fragment);
					if (cur==null) {
						Word lastWord = fragment.getLast();
						if(lastWord!=null) {
							i += lastWord.length() - 1;
						}
					}
					break;
				  }
				  default : cur = stringWord(ch, chars, i, cur, fragment); break; 
				}
			}
		}
		return fragment;
	}

	public Word descWord(char ch, char[] chars, int index, Word word, Fragment fragment) {
		if(ch == '\n') {
			 line++;
		}
		if(this.descType==1 && ch == '\n') {
			this.descType = 0;
			return null;
		} else if(this.descType==2 && ch == '/' && chars[index-1]=='*') {
			descCount--;
			if (descCount==0) {
				this.descType = 0;
				return null;
			}
		} else if(this.descType==2 && ch == '/' && chars[index+1]=='*') {
			descCount++;
		}
		return word;
	}
	
	public Word blankWord(char ch, char[] chars, int index, Word word, Fragment fragment) {
		if (count == 0 || word == null) {
			return null;
		} else {
			word.append(ch);
		}
		return word;
	}
	
	public Word lineLastWord(char ch, char[] chars, int index, Word word, Fragment fragment) {
		if (count == 0 || word == null) {
			if(fragment.getLast()!=null) {
				fragment.getLast().setLineLast(true);
			}
			return null;
		} else {
			word.append(ch);
		}
		return word;
	}
	
	public Word stringWord(char ch, char[] chars, int index, Word word, Fragment fragment) {
		if(word==null) {
			word = new SimpleWord(line);
		//	word = new IndexWord(line, chars, index);
			fragment.addWord(word);
			
		}
		if (ch == '\'') {
			if(word.length() > 0 && word.lastChar()=='\\') {
				word.removeLastChar();
				word.append(ch);
				word.setTrans(true);
			} else {
				word.append(ch);
				if (count == 0) {
					count ++;
				} else {
					count --;
					return null;
				}
			}
		} else {
			word.append(ch);
		}
		return word;
	}
	
	public Word splitWord(char ch, char[] chars, int index, Word word, Fragment fragment) {
		if (count == 0) {
			word = new SplitWord(line);
			word.append(ch);
			if(ch == ';' && fragment.getLast().isLineLast()) {
				fragment.getLast().setLineLast(false);
			}
			fragment.addWord(word);
			return null;
		} else {
			word.append(ch);
		}
		return word;
	}
	
	/**
	 * 
	 * @Title: operatorWord   
	 * @Description: 操作符处理
	 * @param: @param ch
	 * @param: @param chars
	 * @param: @param index
	 * @param: @param word
	 * @param: @param fragment
	 * @param: @return      
	 * @return: Word      
	 * @throws
	 */
	public Word operatorWord(char ch, char[] chars, int index, Word word, Fragment fragment) {
		if (count == 0) {
			if (ch=='.' && isNumberBefore(word)) {
				word.append(ch);
				return word;
			}
			if(ch=='/' && (chars[index+1] == '/' )) {
				descType = 1;
				return null;
			}
			if(ch=='/' && (chars[index+1] == '*' )) {
				descType = 2;
				descCount ++;
				return null;
			}
			//双目操作符类型
			word = new OperatorWord(line);
			word.append(ch);
			Word lastWord = fragment.getLast();
			fragment.addWord(word);
			if (ch == chars[index +1]) {
				if (ch=='-') {
					if(!isNumberChar(chars[index +2])) {
						word.append(ch);
					}
				} else {
					word.append(ch);
					if (ch == '=' && chars[index +2] == '=') {
						word.append('=');
					}
				}
			}else {
				switch(ch) {
				 case '!':
				 case '>':
				 case '<':
				 case '+':
				 case '-': if(chars[index +1] == '=') {
					 			word.append('=');
					 			if (ch == '!' && chars[index +2] == '=') {
					 				word.append('=');
					 			}
					 	   } break;
				 default: break;	 
				}
				if((ch == '-' || ch == '=') && chars[index +1] == '>') {
					word.append('>');
				}
				//负数处理
				if(ch == '-') {
					if(lastWord instanceof OperatorWord && isNumberChar(chars[index +1])) {
						word = new SimpleWord(line);
						word.append(ch);
						word.append(chars[index+1]);
						fragment.removeLast();
						fragment.addWord(word);
						for(int s = index+2; s < chars.length; s++) {
							if(isNumberChar(chars[s])) {
								word.append(chars[s]);
							} else {
								break;
							}
						}
					}
				}
			}
			return null;
		} else {
			word.append(ch);
		}
		return word;
	}
	
	private boolean isNumberBefore(Word word) {
		if (word == null || word.toString() == null) {
			return false;
		}
		char[] wordChars = word.toString().toCharArray();
		for(int i=0;i<wordChars.length;i++) {
			if(!(wordChars[i] >= '0' && wordChars[i] <= '9')) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isNumberChar(char ch) {
		return ch >= '0' && ch <= '9';
	}
	
	public void startWord(char ch, char[] chars, int index) {
		count ++;
		
	}

	public void endWord(char ch, char[] chars, int index) {
		count --;
		if (isStart && count ==1) {
			count --;
		}
	}
	
	public void funcWord(char ch, char[] chars, int index) {
		count ++;
		isStart = true;
	}
}
