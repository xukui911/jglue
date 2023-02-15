package com.xw.glue.express;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.count.Count;
import com.xw.glue.count.One;
import com.xw.glue.exception.GlueException;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.lexical.LexicalAnalyzer;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.value.AnonymousFuncDefValue;
import com.xw.glue.value.AnonymousFuncValue;
import com.xw.glue.value.ExpressValue;
import com.xw.glue.value.FuncDefValue;
import com.xw.glue.value.ParamExpressValue;
import com.xw.glue.value.Value;

//(3 + 5) * 4 > 25
/**
 * @description 表达式定义
 * @author XuKui
 *
 */
public  class JGlueExpressBak implements Express {
	String express;
	List<Word> words;
	List<?> postfixList;
	Count<Value<?>> count;
	Value<?> value;
	JProperties properties;
	Grammar parentGrammar;
	int line;
	
	public int getLine() {
		return line;
	}
	
	public void setLine(int line) {
		this.line = line;
	}

	public JGlueExpressBak(String express) {
		this(express, new JProperties());
	}
	
	public JGlueExpressBak(String express, JProperties properties) {
		this(express, properties, 0);
	}
	
	public JGlueExpressBak(String express, JProperties properties, int line) {
		this(express, properties, line, null);
	}
	
	public JGlueExpressBak(List<Word> words) {
		this(words, new JProperties());
	}
	
	public JGlueExpressBak(List<Word> words, JProperties properties) {
		this(words, properties, null);
	}
	
	public JGlueExpressBak(String express, JProperties properties, int line, Grammar parentGrammar) {
		this.express = express;
		this.properties = properties;
		this.parentGrammar = parentGrammar;
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		words = lexicalAnalyzer.paser(express).getWords();
		this.line = line;
		try {
			postfixList = words2postFixList();
			parseCount();
		} catch (GlueException e) {
			e.setLine(words.get(0).getLine());
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
			throw new GlueException(e, words.get(0).getLine());
		}
	}
	
	public JGlueExpressBak(List<Word> words, JProperties properties, Grammar parentGrammar) {
		this.words = words;
		this.properties = properties;
		this.parentGrammar = parentGrammar;
		if(words!=null && words.size() > 0) {
			this.line = words.get(0).getLine();
		}
		postfixList = words2postFixList(); 
		try {
			parseCount();
		} catch (GlueException e) {
			throw e;
		}catch (Exception e) {
			System.out.println(this.words);
			throw new GlueException(e);
		}
	}
	
	public List<?> words2postFixList() {
		List<Object> list = new ArrayList<Object>();
		Stack<Object> stack = new Stack<Object>();
		StringBuilder builder = new StringBuilder();
		//Function Execute
		List<Word> funcWords = null;
		boolean multiFunc = false;
		int count = 0;
		//Function Define
		Value<?> value = null;
		List<Word> funcDefWords = null;
		int funcDefCount = 0;
		//Parameter Define like a['b']
		List<Word> paramWords = null;
		int paramCount = 0;
		int countUnion = 0;
		OperatorSign lastSign = null;
		List<Word> ternaryWords = null;
		int ternaryCount = 0;
		StringBuilder unionWords = new StringBuilder();
		int line = 0;
		for(int i=0;i<words.size();i++) {
			Word word = words.get(i);
			String wordStr = word.toString();
			line = word.getLine();
			if(ternaryWords!=null) {
				if(wordStr.equals("?")) {
					ternaryCount ++;
				} else if(wordStr.equals(":")) {
					ternaryCount --;
				}
				if(ternaryCount==0) {
					value = new ExpressValue(new JGlueExpressBak(ternaryWords, properties, parentGrammar));
					ternaryWords = null;
					i = i-1;
				} else {
					ternaryWords.add(word);
				}
				continue;
			}
			if(funcDefWords!=null) {
				if(wordStr.equals("{")) {
					funcDefCount++;
				}
				if(wordStr.equals("}")) {
					funcDefCount--;
				}
				funcDefWords.add(word);
				if(funcDefCount == 0) {
					value = new FuncDefValue(funcDefWords, properties, parentGrammar);
					funcDefWords = null;
				}
				continue;
			}
			
			if(paramWords!=null) {
				if(wordStr.equals("[")) {
					paramCount++;
				}
				if(wordStr.equals("]")) {
					paramCount--;
				}
				paramWords.add(word);
				if(paramCount==0) {
					value = new ParamExpressValue(paramWords, properties, parentGrammar);
					paramWords = null;
				}
				continue;
			}
			if(funcWords!=null) {
				if(wordStr.equals("(")) {
					count++;
				}
				if(wordStr.equals(")")) {
					count--;
				}
				funcWords.add(word);
				if(count==0) {
					if (i+1 == words.size() || !"(".equals(words.get(i + 1).toString())) {
						value = ExpressUtils.parseFuncValue(funcWords, properties, parentGrammar, multiFunc);
						funcWords = null;
						multiFunc = false;
					} else {
						multiFunc = true;
					}
				}
				continue;
			} else {
				if(wordStr.equals("{")||wordStr.equals("[")) {
					unionWords.append(wordStr);
					if(unionWords.charAt(0)==wordStr.charAt(0)) {
						countUnion++;
					}
					continue;
				} else {
					if(unionWords.length()>0) {
						unionWords.append(wordStr);
						if(ExpressUtils.isKeyWords(wordStr)) {
							unionWords.append(" ");
						}
						if(wordStr.equals("}")||wordStr.equals("]")) {
							if((unionWords.charAt(0)=='{' && wordStr.charAt(0) == '}') ||
									(unionWords.charAt(0)=='[' && wordStr.charAt(0) == ']')) {
								countUnion--;
							}
						}
						if(countUnion>0) {
							continue;
						} else {
							wordStr = unionWords.toString();
							unionWords.setLength(0);
						}
					}
				}
			}
			OperatorSign sign = OperatorSign.getOperatorSign(wordStr);
			if(sign!=null) {
				if(builder.length()>0) {
					list.add(ExpressUtils.parseValue(builder.toString(), line, properties, parentGrammar, OperatorSign.EXEC == lastSign, this));
					builder.setLength(0);
				} else if(value!=null) {
					list.add(value);
					value = null;
				}
				while(true) {
					if(stack.isEmpty()) {
						stack.push(sign);
						break;
					}else {
						Object obj = stack.peek();
						if(obj instanceof OperatorSign) {
							if(((OperatorSign) obj).getPriority() < sign.getPriority()) {
								stack.push(sign);
								break;
							}else {
								list.add(stack.pop());
							}
						}else if(obj.toString().equals("(")) {
							stack.push(sign);
							break;
						}else {
							break;
						}
					}
				}
				lastSign = sign;
				if(OperatorSign.TERNARY == lastSign) {
					ternaryWords = new ArrayList<Word>();
					ternaryCount++;
				}
			} else if("(".equals(wordStr)) {
				AnonymousFuncDefValue asFuncDefValue = ExpressUtils.parseAnonymousFuncDef(words, i, properties, parentGrammar);
				if(asFuncDefValue != null) {
					int nextIndex = asFuncDefValue.getWordSize();
					AnonymousFuncValue asFuncValue = ExpressUtils.parseAnonymousFunc(words, nextIndex, properties, parentGrammar, asFuncDefValue);
					if (asFuncValue==null) {
						list.add(asFuncDefValue);
						i+=asFuncDefValue.getWordSize();
					} else {
						list.add(asFuncValue);
						i+=asFuncValue.getWordSize();
					}
					continue;
				}
				stack.push(wordStr);
			}else if(")".equals(wordStr)) {
				if(builder.length()>0) {
					list.add(ExpressUtils.parseValue(builder.toString(), line, properties, parentGrammar, false, this));
					builder.setLength(0);
				}else if(value!=null) {
					list.add(value);
					value = null;
				}
				while(true) {
					if(!stack.isEmpty()) {
						Object obj = stack.pop();
						if(!obj.toString().equals("(")) {
							list.add(obj);
						}else {
							break;
						}
					}else {
						break;
					}
				}
			} else {
				if(i<words.size()-1) {
					for(String funcKey: KeyWords.FUNC.getAlias()) {
						if(funcKey.equals(wordStr)) {
							funcDefWords = new ArrayList<Word>();
							funcDefWords.add(word);
							for(int j=i;j<words.size();j++) {
								Word next = words.get(j+1);
								if(next.toString().equals("{")) {
									i = j;
									break;
								}
								funcDefWords.add(next);
							}
							break;
						}
					}
					if(funcDefWords!=null) {
						continue;
					}
					char first = wordStr.charAt(0);
					if((first >= 'a' && first<= 'z')
							||(first>='A' && first <= 'Z')) {
						Word next = words.get(i+1);
						if(next.toString().equals("(")) {
							funcWords = new ArrayList<Word>();
							funcWords.add(word);
							funcWords.add(next);
							count++;
							i++;
							continue;
						}
						if(next.toString().equals("[")) {
							paramWords = new ArrayList<Word>();
							paramWords.add(word);
							paramWords.add(next);
							paramCount++;
							i++;
							continue;
						}
					}
				}
				builder.append(wordStr);
			}
		}
		if(builder.length()>0) {
			String strSrc = builder.toString().trim();
			if(!"".equals(strSrc)) {
				Value<?> val = ExpressUtils.parseValue(strSrc, line, properties, parentGrammar, false, this);
				list.add(val);
			}
		}else if(value!=null) {
			list.add(value);
		}
		if(!stack.isEmpty()) {
			while(stack.size()!=0) {
				list.add(stack.pop());
			}
		}
		return list;
	}

	public boolean validate() {
		return postfixList!=null && postfixList.size()>1;
	}
	
	@Override
	public <T> T execute(JGlueContext contxt) {
		try {
			T t = count(contxt);
			return t;
		}catch(Exception ex) {
			GlueException gx = null;
			if(ex instanceof GlueException) {
				gx = (GlueException)ex;
				if(gx.getLine() <=0) {
					gx.setLine(line);
				}
				throw gx;
			}
			ex.printStackTrace();
			gx = new GlueException(ex);
			gx.setLine(line);
			throw gx;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void parseCount() throws Exception {
		List<?> list = postfixList;
		if(list!=null&&list.size()>0) {
			if(list.size()==1) {
				CountBuilder<?> builder =  new CountBuilder<Value<?>>() {
					@Override
					public Count<Value<?>> build(JProperties properties) throws Exception {
						return new One(value);
					}
				};
				count = (Count)builder.one(((Value<?>)list.get(0))).build(properties);
			} else {
				Stack<Value<?>> stack = new Stack<Value<?>>();
				for(Object obj: list) {
					if(obj instanceof Value) {
						stack.push((Value<?>)obj);
					}else if(obj instanceof OperatorSign) {
						CountBuilder<?> builder = ((OperatorSign)obj).getBuilder();
						if(builder.valueLen==1) {
							builder.one(stack.pop());
							builder.parentGrammar(parentGrammar);
						}else if (builder.valueLen==2){
							builder.two(stack.pop());
							builder.one(stack.pop());
							builder.parentGrammar(parentGrammar);
						}else {
							throw new RuntimeException("错误的计算个数");
						}
						stack.push((Value<?>)builder.build(properties));
					}
				}
				if(stack.size() == 1) {
					value = stack.pop();
					if(value instanceof Count) {
						this.count = (Count)value;
					}
				} else {
					System.out.println(this.getWords());
					throw new GlueException("表达式项不能为空", line);
				}
			}
		}
		if(value==null) {
			value = ((Value<?>)count);
		}
	}
	
	public Count<?> getCount() {
		return this.count;
	}
	
	public Value<?> getValue() {
		return this.value;
	}
	
	/**
	 *  运算
	 * @param args
	 * @throws Exception 
	 */
	
	@SuppressWarnings({"unchecked" })
	public <T> T count(JGlueContext context) throws Exception {
		if(this.value!=null) {
			return (T)this.value.value(context);
		}
		return null;
	}

	@Override
	public String getExpress() {
		return express;
	}
	
	public List<Word> getWords() {
		return words;
	}
	
	public String wordsToString() {
		StringBuilder wordExpress = new StringBuilder();
		this.words.forEach( word -> {
			wordExpress.append(word.toString());
		});
		return wordExpress.toString();
	}
}
