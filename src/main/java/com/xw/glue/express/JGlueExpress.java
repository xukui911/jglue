package com.xw.glue.express;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.count.Count;
import com.xw.glue.count.One;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Brackets.BracketsResult;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.lexical.LexicalAnalyzer;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.value.AnonymousFuncDefValue;
import com.xw.glue.value.ExpressValue;
import com.xw.glue.value.FuncDefValue;
import com.xw.glue.value.Value;

//(3 + 5) * 4 > 25
/**
 * @description 表达式定义
 * @author XuKui
 *
 */
public  class JGlueExpress implements Express {
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

	public JGlueExpress(String express) {
		this(express, new JProperties());
	}
	
	public JGlueExpress(String express, JProperties properties) {
		this(express, properties, 0);
	}
	
	public JGlueExpress(String express, JProperties properties, int line) {
		this(express, properties, line, null);
	}
	
	public JGlueExpress(List<Word> words) {
		this(words, new JProperties());
	}
	
	public JGlueExpress(List<Word> words, JProperties properties) {
		this(words, properties, null);
	}
	
	public JGlueExpress(String express, JProperties properties, int line, Grammar parentGrammar) {
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
	
	public JGlueExpress(List<Word> words, JProperties properties, Grammar parentGrammar) {
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
		List<Word> paramWords = new ArrayList<Word>();
		//Function Execute
		//Function Define
		Value<?> value = null;
		//Parameter Define like a['b']
		OperatorSign lastSign = null;
		List<Word> ternaryWords = null;
		Value<?> bValue = null;
		int ternaryCount = 0;
		int line = 0;
		for(int i=0;i<words.size();i++) {
			Word word = words.get(i);
			String wordStr = word.toString();
			line = word.getLine();
			List<Word> funcDefWords = funcDef(i);
			if (funcDefWords!=null && funcDefWords.size() > 0) {
				i+= funcDefWords.size() - 1;
				value = new FuncDefValue(funcDefWords, properties, parentGrammar);
				continue;
			}
			if(OperatorSign.NEW == lastSign) {
				List<Word> newFuncWords = newInstance(i);
				value = new JGlueExpress(newFuncWords, this.properties, this.parentGrammar).value;
				i+= newFuncWords.size() - 1;
				lastSign = null;
				continue;
			}
			if(ternaryWords!=null) {
				if(wordStr.equals("?")) {
					ternaryCount ++;
				} else if(wordStr.equals(":")) {
					ternaryCount --;
				}
				if(ternaryCount==0) {
					value = new ExpressValue(new JGlueExpress(ternaryWords, properties, parentGrammar));
					ternaryWords = null;
					i = i-1;
				} else {
					ternaryWords.add(word);
				}
				continue;
			}
			if(bValue!=null) {
				value = bValue;
				bValue = null;
				i--;
				continue;
			}
			//处理括号
			Brackets brackets = Brackets.bracket(wordStr);
			List<Word> bWords = null;
			OperatorSign sign = null;
			if(brackets!=null) {
				if(Brackets.Round == brackets) {
					AnonymousFuncDefValue funcDefValue = ExpressUtils.parseAnonymousFuncDef(words, i, properties, parentGrammar);
					if(funcDefValue!=null) {
						value = funcDefValue;
						i += funcDefValue.getWordSize();
						continue;
					}
				}
				bWords = brackets.bracketsWords(words, i);
				i+=bWords.size() - 1;
				
				
				BracketsResult result = brackets.value(bWords, this, value, paramWords, list);
				if(result.sign==null) {
					value = result.value;
					continue;
				} else {
					bValue = result.value;
				}
			}
			sign = OperatorSign.getOperatorSign(wordStr);
			if(sign!=null) {
				if(paramWords.size()>0) {
					list.add(ExpressUtils.parseValue(paramWords, line, properties, parentGrammar, sign, OperatorSign.EXEC == lastSign,this));
					paramWords = new ArrayList<Word>();
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
			}else {
				paramWords.add(word);
			}
		}
		if(paramWords.size()>0) {
			Value<?> val = ExpressUtils.parseValue(paramWords, line, properties, parentGrammar, null, false, this);
			list.add(val);
		}else if(value!=null) {
			list.add(value);
		}else if(bValue!=null) {
			list.add(bValue);
			bValue = null;
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
	
	private List<Word> newInstance(int i) {
		List<Word> newFuncWords = new ArrayList<Word>();
		int count = 0;
		boolean isStart = false;
		for(int j=i;j<words.size();j++) {
			Word word = words.get(j);
			newFuncWords.add(word);
			if("(".equals(word.toString())) {
				count ++;
				isStart = true;
			}
			if(")".equals(word.toString())) {
				count --;
			}
			if(isStart && count==0) {
				break;
			}
		}
		return newFuncWords;
	}
	private List<Word> funcDef(int i) {
		Word word = words.get(i);
		String wordStr = word.toString();
		List<Word> funcDefWords = null;
		for(String funcKey: KeyWords.FUNC.getAlias()) {
			if(funcKey.equals(wordStr)) {
				funcDefWords = new ArrayList<Word>();
				funcDefWords.add(word);
				int count = 0;
				boolean isStart = false;
				for(int j=i+1;j<words.size();j++) {
					Word next = words.get(j);
					funcDefWords.add(next);
					if(next.toString().equals("{")) {
						count++;
						isStart = true;
					}
					if(next.toString().equals("}")) {
						count--;
					}
					if(isStart && count==0) {
						break;
					}
				}
				break;
			}
		}
		return funcDefWords;
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
