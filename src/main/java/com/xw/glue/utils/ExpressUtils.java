package com.xw.glue.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.express.OperatorSign;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.func.factory.FuncFactory;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Grammars;
import com.xw.glue.grammar.Result;
import com.xw.glue.grammar.oop.ClassDefine;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.SimpleWord;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.object.JglueObj;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.AnonymousFuncDefValue;
import com.xw.glue.value.AnonymousFuncValue;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.ExpressValue;
import com.xw.glue.value.FuncNameParamValue;
import com.xw.glue.value.FuncValue;
import com.xw.glue.value.GlobeParamValue;
import com.xw.glue.value.InsFuncValue;
import com.xw.glue.value.LibValue;
import com.xw.glue.value.NamespaceFuncValue;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.StrValue;
import com.xw.glue.value.Value;
import com.xw.glue.value.Value.ValueType;

public class ExpressUtils {
	private static Map<String, KeyWords> keyWordMap = new HashMap<String, KeyWords>();
	
	//private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
	private static final Pattern funcPattern = Pattern.compile("[a-zA-z]([-_a-zA-Z]{0,19})\\s*\\(.*\\)");
	
	static {
		KeyWords[] keys = KeyWords.values();
		for(KeyWords key: keys) {
			keyWordMap.put(key.getKeyWord(), key);
		}
	}
	public static boolean isNumber(String val) {
		// return pattern.matcher(val).matches();
		if(val!=null && val.length() > 1) {
			if (val.charAt(0) == '-') {
				val = val.substring(1);
			}
			char last = val.charAt(val.length() -1);
			switch(last) {
				case 'I':
				case 'i':
				case 'L':
				case 'l':
				case 'F':
				case 'f':
				case 'D':
				case 'd': {
						 	val = val.substring(0, val.length() -1);
						 	break;
					 	  }
				default: break;
			}
		}
		int index = 0;
		int count = 0;
		int len = val.length();
		for (int i = 0 ; i<len; i++ ) {
			int chr = val.charAt(i);
			if(chr=='.') {
				index = i;
				count ++;
				continue;
			}
			if (chr < 48 || chr > 57)
				return false;
		}
		return (count == 0 && len > 0) || (count==1 && index > 0 && index < len-1);
	}
	
	public static boolean isInt(String val) {
		try {
			Integer.valueOf(val);
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	
	public static boolean isLong(String val) {
		try {
			Long.valueOf(val);
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	
	public static boolean isDouble(String val) {
		try {
			Double.valueOf(val);
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	
	public static boolean isString(String val) {
		 if(val!=null && val.length()>1 && val.startsWith("'")&& val.endsWith("'")) {
			 char[] chars = val.toCharArray();
			 int count = 0;
			 for(char ch:chars) {
				 if ('\'' == ch) {
					count++;
				 }
				 if ('+' == ch && count % 2 == 1) {
					 return false;
				 }
			 }
			 return true;
		 }
		 return false;
	}
	
	public static boolean isBoolean(String val) {
		return "true".equals(val)||"false".equals(val);
	}
	
	public static boolean isExeFunc(String val) {
		return funcPattern.matcher(val).matches();
	}
	
	public static String findFirst(String patt, String str) {
	    Pattern r = Pattern.compile(patt);
	    Matcher m = r.matcher(str);
	    if(m.find()) {
	    	return m.group();
	    }
	    return null;
	}
	
	public static Object str2ConstSimple(String src) {
		Object value = null;
		if(src.startsWith("'") && src.endsWith("'")) {
			value = src.substring(1, src.length()-1);
		} else if(src.equals("true")|| src.equals("false")) {
			value = Boolean.valueOf(src);
		} else if(ExpressUtils.isNumber(src)) {
			value = parseNumber(src);
		} else {
			throw new GlueException("不是简单类型（string|number|boolean）");
		}
		return value;
	}
	
	public static Value<?> parseValue(String word, int line,JProperties properties, Grammar parentGramar, boolean isExec, Express express) {
		Value<?> value = null;
		word = word.trim();
		if(isNumber(word)) {
			value = new NumberValue(parseNumber(word));
		} else if(isBoolean(word)) {
			value = new BoolValue(Boolean.valueOf(word));
		} else if(isString(word)) {
			return new StrValue(word);
		} else if(word.contains("(")) {
			if (express!=null && !word.equals(express.toString())) {
				throw new GlueException("express parse error!!"+ express);
			}
			value = new ExpressValue(word, line, properties, parentGramar);
		} else {
			if(isExec) {
				value = new ParamValue(word);
			} else {
				if(parentGramar!=null) {
					value =  parentGramar.getParamValue(word);
					if(value == null) {
						//检查是否是调用库函数
						Grammars libGrammars = parentGramar.getTopParent().findLib(word.toString());
						if(libGrammars!=null) {
							return new LibValue(word.toString(), libGrammars, parentGramar);
						} else if (FuncFactory.getInstance().hasNamespace(word.toString())){
							return new NamespaceFuncValue(word.toString());
						}
						//throw new GlueException("not define param " + word, line);
						value = new GlobeParamValue(word);
						parentGramar.addGlobleParamValue((ParamValue)value);
					}
				} else {
					value = new ParamValue(word);
				}
			}
		}
		return value;
	}
	
	public static Value<?> parseValue(List<Word> words, int line,JProperties properties, Grammar parentGramar, OperatorSign sign, boolean isExecFront, Express express) {
		Value<?> value = null;
		String word = words.get(0).toString().trim();
		if(words.size()==1) {
			if(isNumber(word)) {
				return new NumberValue(parseNumber(word));
			} else if(isBoolean(word)) {
				return new BoolValue(Boolean.valueOf(word));
			} else if(isString(word)) {
				return new StrValue(word);
			}
			if(isExecFront) {
				if(OperatorSign.FUNC_EXE==sign) {
					return new FuncNameParamValue(word);
				} else {
					return new ParamValue(word);
				}
			}else {
				if(parentGramar!=null) {
					value =  parentGramar.getParamValue(word);
					if(value == null) {
						//检查是否是调用库函数
						Grammars libGrammars = parentGramar.getTopParent().findLib(word.toString());
						if(libGrammars!=null) {
							return new LibValue(word.toString(), libGrammars, parentGramar);
						} else if (FuncFactory.getInstance().hasNamespace(word.toString())){
							return new NamespaceFuncValue(word.toString());
						}
						if(OperatorSign.FUNC_EXE == sign) {
							return new StrValue(word);
						}
						//throw new GlueException("not define param " + word, line);
						value = new GlobeParamValue(word);
						parentGramar.addGlobleParamValue((ParamValue)value);
					}
				} else {
					value = new ParamValue(word);
				}
			}
		} else {
			return new ExpressValue(word, line, properties, parentGramar);
		}
		return value;
	}
	
	public static Value<?> parseFuncValue(List<Word> words, JProperties properties, Grammar parentGrammar, boolean multiFunc) {
		FuncValue value = FuncUtils.createFuncValue(words, properties, parentGrammar);
		if(value!=null && value.getFunc() instanceof InstanceFunc) {
			return new InsFuncValue(value, (InstanceFunc)value.getFunc());
		}
		return value;
	}
	
	public static AnonymousFuncValue parseAnonymousFunc(List<Word> words, int index, JProperties properties, Grammar parentGrammar, AnonymousFuncDefValue funcDef) {
		if (words.size() > index && "(".equals(words.get(index).toString())) {
			return new AnonymousFuncValue(funcDef, words, index);
		}
		return null;
	}
	
	public static AnonymousFuncDefValue parseAnonymousFuncDef(List<Word> words, int index, JProperties properties, Grammar parentGrammar) {
		Word word = words.get(index);
		AnonymousFuncDefValue value = null;
		int endIndex = 0;
		if(word.toString().equals("(")) {
			int count = 1;
			List<Word> funcWords = new ArrayList<Word>();
			funcWords.add(new SimpleWord(word.getLine(), "function"));
			funcWords.add(word);
			int funcStart = 0;
			for(int i = index + 1; i<words.size()-1;i++) {
				Word cur = words.get(i);
				funcWords.add(cur);
				if(cur.toString().equals("(")) {
					count ++;
				}
				else if(cur.toString().equals(")")) {
					count --;
					if(count == 0 && (words.get(i+1).toString().equals("->") 
							|| words.get(i+1).toString().equals("=>"))) {
						//funcWords.add(cur);
						funcStart = i+2;
					}
					break;
				}
			}
			boolean isFunc = false;
			if(funcStart > 0) {
				for(int i = funcStart; i<words.size();i++) {
					Word cur = words.get(i);
					funcWords.add(cur);
					if(cur.toString().equals("{")) {
						count ++;
					}
					else if(cur.toString().equals("}")) {
						count --;
						if(count == 0) {
							isFunc = true;
							endIndex = i;
							break;
						}
					}
				}
			}
			if(isFunc) {
				value = new AnonymousFuncDefValue(funcWords, null, properties, parentGrammar);
				value.setWordSize(endIndex);
			}
		}
		return value;
	}
	
	public static Number parseNumber(String src) {
		Number dstNumber = null;
		char last = src.charAt(src.length() -1);
		switch(last) {
			case 'I':
			case 'i': dstNumber = Integer.valueOf(src.substring(0, src.length()-1)).intValue(); break;
			case 'L':
			case 'l': dstNumber = Long.valueOf(src.substring(0, src.length()-1)).longValue(); break;
			case 'F':
			case 'f': dstNumber = Float.valueOf(src.substring(0, src.length()-1)).floatValue(); break;
			case 'D':
			case 'd': dstNumber = Double.valueOf(src.substring(0, src.length()-1)).doubleValue(); break;
			default: {
				if (src.indexOf(".")!=-1) {
					dstNumber = Double.valueOf(src).doubleValue();
				} else {
					try {
						dstNumber = Integer.valueOf(src).intValue();
					}catch (Exception ex) {
						dstNumber = Long.valueOf(src).longValue();
					}
				}
				break;
			}
		}
		return dstNumber;
	}
	
	public static Number parseObjNumber(Object src) {
		if(src instanceof String) {
			return ExpressUtils.parseNumber((String)src);
		} else if(src instanceof Number) {
			return (Number)src;
		}
		throw new GlueException("错误的数字类型");
	}
	
	public static List<Object> parseKeys(String src) {
		List<Object> keys = new ArrayList<Object>();
		int len = src.length();
		StringBuilder sb = new StringBuilder();
		int count = 0;
		int middle = 0;
		for(int i=0;i<len;i++) {
			char ch = src.charAt(i);
			if(ch == '(') {
				count ++;
			}
			if(ch == ')') {
				count --;
			}
			if(ch=='.') {
				if(sb.length()>0 && count==0) {
					keys.add(sb.toString());
					sb.setLength(0);
				} else {
					sb.append(ch);
				}
			}else if(ch=='['){
				if(sb.length()>0 && count==0 && middle==0) {
					keys.add(sb.toString());
					sb.setLength(0);
				}
				if(middle > 0) {
					sb.append(ch);
				}
				middle++;
			}else if(ch==']'){
				middle--;
				if(count == 0 && middle == 0) {
					String key = sb.toString();
					if(key.startsWith("'") && key.endsWith("'")) {
						keys.add(key.substring(1, key.length()-1));
					}else if(isNumber(key)) {
						try {
							keys.add(Integer.valueOf(key));
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					} else {
						//keys.add(new StrValue(key ,true));
						keys.add(new JGlueExpress(key));
					}
					sb.setLength(0);
				}
				if(middle > 0) {
					sb.append(ch);
				}
			}else { 
				sb.append(ch);
			}
		}
		if(sb.length()>0) {
			keys.add(sb.toString());
		}
		return keys;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getValueByKeys(JGlueContext context,List<Object> keys) {
		String keyStr = (String)keys.get(0);
		Object obj = context.get(keyStr);
		for(int i=1;i<keys.size();i++) {
			Object key = keys.get(i);
			if(key instanceof Express) {
				obj = ((Express)key).execute(context);
			} else if(key instanceof String) {
				if(obj instanceof JglueObj) {
					JglueObj jglueObj = (JglueObj)obj;
					obj = jglueObj.get((String)key);
				} else if(obj instanceof Map) {
					keyStr += "."+key;
					obj = ((Map)obj).get(key);
				} else if(obj instanceof ClassDefine) {
					obj = ((ClassDefine)obj).get(key.toString());
				} else {
					throw new GlueException("暂时不支持bean取值");
				}
			} else {
				keyStr += "["+key +"]";
				List<Object> objList = (List)obj;
				if(objList==null) {
					throw new GlueException(keyStr+"发生空指针异常");
				} else if(objList.size()<=(Integer)key) {
					throw new GlueException(keyStr+"发生数组越界异常");
				}
				obj = objList.get((Integer)key);
			}
		}
		return obj;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getValueByKeys(String firstKey, Object obj,List<Object> keys) {
		String keyStr = (String)keys.get(0);
		for(int i=0;i<keys.size();i++) {
			Object key = keys.get(i);
			if(key instanceof String) {
				keyStr += "."+key;
				if(obj instanceof JglueObj) {
					JglueObj jglueObj = (JglueObj)obj;
					obj = jglueObj.get((String)key);
				} else if(obj instanceof Map) {
					obj = ((Map)obj).get(key);
				} else if(obj instanceof ClassDefine) {
					obj = ((ClassDefine)obj).get(key.toString());
				} else {
					throw new GlueException("暂时不支持bean取值");
				}
			} else {
				keyStr += "["+key +"]";
				List<Object> objList = (List)obj;
				if(objList==null) {
					throw new GlueException(keyStr+"发生空指针异常");
				} else if(objList.size()<=(Integer)key) {
					throw new GlueException(keyStr+"发生数组越界异常");
				}
				obj = objList.get((Integer)key);
			}
		}
		return obj;
	}
	
	public static ValueType getValueType(Object val) {
		if (val instanceof Number) {
			return ValueType.NUMBER ;
		} else if (val instanceof String) {
			return ValueType.STRING ;
		} else if (val instanceof List) {
			return ValueType.LIST ;
		} else if (val instanceof Boolean) {
			return ValueType.BOOLEAN ;
		} else if (val instanceof IFunc) {
			return ValueType.FUNC ;
		}
		return ValueType.OBJECT;
	}
	
	public static Result buildResult(Object obj, ValueType type) {
		return new Result(obj, type);
	}
	
	public static boolean isKeyWords(String word) {
		return keyWordMap.containsKey(word.toLowerCase());
	}
}
