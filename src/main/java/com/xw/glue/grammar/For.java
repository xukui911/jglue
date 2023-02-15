package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.JValue;
import com.xw.glue.count.Add;
import com.xw.glue.count.AddDouble;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.lexical.LexicalAnalyzer;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.object.JglueArray;
import com.xw.glue.object.JglueObj;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.ParamUtils;
import com.xw.glue.utils.StringUtils;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value.ValueType;

public class For extends Grammar{
	int count = 0;
	// for(a:[]) for( a in [])
	ForExec forExec;
	// for(;;)
	@Override
	public void parse(Stack<Word> stack) {
		stack.pop();
		Word first = stack.pop();
		if (first.toString().equals("(")) {
			count ++;
			parseCondition(stack);
			//解析循环体
			parseContent(stack, "{", "}");
		} else {
			throw new GlueException("错误的while格式，while后面必须是(condition)");
		}
	}
	
	private void parseCondition(Stack<Word> stack) {
		List<Word> words = new ArrayList<Word>();
		while(!stack.isEmpty()) {
			Word next = stack.pop();
			if(next.toString().equals("(")) {
				count++;
			}
			if(next.toString().equals(")")) {
				count--;
			}
			if(count > 0) {
				words.add(next);
			} else {
				break;
			}
		}
		parseJudge(words);
	}
	
	private void parseJudge(List<Word> words) {
		String str = "";
		for(Word word: words) {
			str += word.toString() +" ";
		}
		
		if(str.contains(";")) {
			List<List<Word>> wordList = new ArrayList<List<Word>>();
			List<Word> curList = new ArrayList<Word>();
			for(Word word: words) {
				if(word.toString().equals(";")) {
					wordList.add(curList);
					curList = new ArrayList<Word>();
				} else {
					curList.add(word);
				}
			}
			wordList.add(curList);
			if(wordList.size()==3) {
				forExec = new SimpleFor(wordList.get(0), wordList.get(1), wordList.get(2), getProperties());
			}else {
				throw new GlueException("错误的for(...)语法格式:\n请检查"
						+JGlueStack.getCurFunc()+"()第" + words.get(0).getLine()+"行");
			}
		} else {
			if(str.startsWith("var") ||
					str.startsWith("set") ||
					str.startsWith("let")) {
				str = str.substring(4);
			}
			String[] strs = splitFor(str);
			if(strs!=null && strs.length==2) {
				String[] codes = strs[0].toString().split(",");
				if(codes.length==2) {
					forExec = new IncreaseFor(codes[0].trim(), codes[1].trim(), strs[1].trim(), getProperties(), words.get(0).getLine());
				} else if(codes.length>2) {
					forExec = new IncreaseFor(codes[0].trim(), codes[1].trim(), codes[2].trim(), strs[1].trim(), getProperties(), words.get(0).getLine());
				} else {
					forExec = new IncreaseFor(codes[0].trim(), null, strs[1].trim(), getProperties(), words.get(0).getLine());
				}
			} else {
				throw new GlueException("错误的for(...)语法格式:\n请检查"
						+JGlueStack.getCurFunc()+"()第" + words.get(0).getLine()+"行");	
			}
		}
	}
	
	private String[] splitFor(String str) {
		String[] strs = null;
		if(str.contains(" in ")) {
			strs = str.split(" in ");
		} else if(str.contains(" of ")) {
			strs = str.split(" of ");
		}
		return strs;
	}
	
	public Result execSelf(JGlueContext context) {
		return forExec.exec(context);
	}
	
	public static interface ForExec {
		public Result exec(JGlueContext context);
	}
	
	/**
	 * 常规for循环
	 * @author xukui
	 *
	 */
	public class SimpleFor implements ForExec {
		JGlueExpress firstExpress;
		JGlueExpress judge;
		JGlueExpress lastExpress;
		ParamValue paramValue;
		SimpleFor(List<Word> firstWords, List<Word> judgeWords, List<Word> lastWords, JProperties properties) {
			if(firstWords.size()>0) {
				boolean isVar = false;
				if(firstWords.get(0).toString().equals("var") ||
						firstWords.get(0).toString().equals("set") ||
						firstWords.get(0).toString().equals("let")) {
					firstWords.remove(0);
					isVar = true;
				}
				JProperties jProperties = null;
				if(properties!=null) {
					jProperties = properties.copy().setDefValue(isVar);
				}else {
					jProperties = new JProperties().setDefValue(isVar);
				}
				/* 赋值语句不管有没有var关键字都定义局部变量
				if(isVar) {
					addParamValue(ParamUtils.createParamValue(firstWords.get(0).toString(), properties));
				}*/
				if(isVar || (firstWords.size() > 1 && firstWords.get(1).toString().equals("="))) {
					addParamValue(ParamUtils.createParamValue(firstWords.get(0).toString(), properties));
				}
				firstExpress = new JGlueExpress(firstWords, jProperties, For.this);
			}
			if(lastWords.size()>0) {
				lastExpress = new JGlueExpress(lastWords, properties, For.this);
				if(lastExpress.getCount() instanceof AddDouble) {
					paramValue = ((AddDouble)lastExpress.getCount()).getValueL();
				}
			}
			if(judgeWords.size()>0) {
				judge = new JGlueExpress(judgeWords, properties, For.this);
			}
		}
		
		//针对最基本的常量做情况做提速处理 
		
		public String getExpress(List<Word> words) {
			String strCommand = "";
			for(Word word: words) {
				strCommand += word.toString() + " ";
			}
			return strCommand.trim();
		}
		public Result exec(JGlueContext context) {
			if(paramValue!=null) {
				return execSpecial(context);
			}
			return execCommon(context);
		}
		
		public Result execSpecial(JGlueContext context) {
			firstExpress.execute(context);
			JValue jvalue = context.getJValue(paramValue);
			Result result = null;
			int count = 0;
			for(;judge==null||(Boolean)judge.execute(context);) {
				if(count > 100000001) {
					throw new GlueException("循环次数超过100000000次(").append(judge.getExpress()+")");
				}
				result = For.super.execSelf(context);
				if(result!=null) {
					if(result.getType()==ValueType.CONTINUE) {
						lastExpress.execute(context);
						continue;
					} else {
						if(result.getType()==ValueType.BREAK) {
							result = null;
						}
						break;
					}
				} else {
					jvalue.setValue(Add.addMyself((Number) jvalue.getValue(), 1));
				}
				count++;
			}
			return result;
		}
		
		public Result execCommon(JGlueContext context) {
			if(firstExpress!=null) {
				firstExpress.execute(context);
			}
			Result result = null;
			int count = 0;
			for(;judge==null||(Boolean)judge.execute(context);) {
				if(count > 100000001) {
					throw new GlueException("循环次数超过10000000次(").append(judge.getExpress()+")");
				}
				result = For.super.execSelf(context);
				if(result!=null) {
					if(result.getType()==ValueType.CONTINUE) {
						if(lastExpress!=null) {
							lastExpress.execute(context);
						}
						continue;
					} else {
						break;
					}
				} else {
					if(lastExpress!=null) {
						lastExpress.execute(context);
					}
				}
				count++;
			}
			return result;
		} 
	}
	
	/**
	 * @desc 增强for循环
	 * @author xukui
	 *
	 */
	public class IncreaseFor implements ForExec {
		 ParamValue itemCode;
		 ParamValue itemValue;
		 ParamValue indexCode;
		 Express listExpress;
		 String listCode;
		
		public IncreaseFor(String itemCode, String indexCode, String listCode, JProperties properties, int line) {
			if(StringUtils.isNotBlank(indexCode)) {
				this.indexCode = ParamUtils.createParamValue(indexCode, properties);
				addParamValue(this.indexCode);
			}
			if(StringUtils.isNotBlank(itemCode)) {
				this.itemCode = ParamUtils.createParamValue(itemCode, properties);
				addParamValue(this.itemCode);
			}
			this.listCode = listCode.trim();
			if (!listCode.startsWith("[")) {
				this.listExpress = new JGlueExpress(listCode, properties, line, For.this);
			}
		}
		
		public IncreaseFor(String itemValue, String itemCode, String indexCode, String listCode, JProperties properties, int line) {
			if(StringUtils.isNotBlank(indexCode)) {
				this.indexCode = ParamUtils.createParamValue(indexCode, properties);
				addParamValue(this.indexCode);
			}
			if(StringUtils.isNotBlank(itemCode)) {
				this.itemCode = ParamUtils.createParamValue(itemCode, properties);
				addParamValue(this.itemCode);
			}
			if(StringUtils.isNotBlank(itemValue)) {
				this.itemValue = ParamUtils.createParamValue(itemValue, properties);
				addParamValue(this.itemValue);
			}
			this.listCode = listCode.trim();
			if (!listCode.startsWith("[")) {
				this.listExpress = new JGlueExpress(listCode, properties, line, For.this);
			}
		}
		
		// 增强for循环实现
		@SuppressWarnings("unchecked")
		public Result exec(JGlueContext context) {
			Object resultObject = null;
			if(listExpress!=null) {
				resultObject = listExpress.execute(context);
			} else {
				if(listCode.startsWith("[")) {
					LexicalAnalyzer analyzer = new LexicalAnalyzer();
					resultObject = new JglueArray(analyzer.paser(listCode).getWords(), context, For.this.properties, For.this.getParent()).parse();
				} else if(listCode.startsWith("{")) {
					LexicalAnalyzer analyzer = new LexicalAnalyzer();
					resultObject = new JglueObj(analyzer.paser(listCode).getWords(), context, For.this.properties, For.this.getParent()).parse();
				}
			}
			Result result = null;
			boolean hasIndex = indexCode != null;
			boolean hasValue = itemValue != null;
			if(resultObject instanceof List) {
				List<Object> list = (List<Object>)resultObject;
				for(int i=0;i<list.size();i++) {
					context.set(itemCode, list.get(i));
					if(hasIndex) {
						context.set(indexCode, i);
					}
					result = For.super.execSelf(context);
					if(result!=null) {
						if(result.getType()==ValueType.CONTINUE) {
							continue;
						}
						if(result.getType()==ValueType.BREAK) {
							break;
						}
						return result;
					}
				}
			} else if (resultObject instanceof Map){
				Map<String, Object> map = (Map<String, Object>)resultObject;
				Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
				int index = 0;
				while(iterator.hasNext()) {
					Map.Entry<String, Object> entry = iterator.next();
					context.set(itemCode, entry.getKey());
					if(hasIndex) {
						if(hasValue) {
							context.set(indexCode, index);
							index++;
							context.set(itemValue, entry.getValue());
						} else {
							context.set(indexCode, entry.getValue());
						}
					}
					result = For.super.execSelf(context);
					if(result!=null) {
						if(result.getType()==ValueType.CONTINUE) {
							continue;
						}
						if(result.getType()==ValueType.BREAK) {
							break;
						}
						return result;
					}
				}
				
			}
			return null;
		}
	}
}
