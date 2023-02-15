package com.xw.glue.object;

import java.util.Map;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.func.factory.AbstractInsFuncFactory;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.utils.StringUtils;

public class JglueObject extends JglueClass<JglueObject> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String prefix = "J:";
	private String jglueSon;
	private int parseIndex;
	private char[] chars;
	private Grammar parentGrammar;
	private static AbstractInsFuncFactory<JglueObject> factory;
	static {
		factory = new AbstractInsFuncFactory<JglueObject>();
	}
	
	public JglueObject(String jglueSon) {
		this(jglueSon, null, null);
	}
	
	public JglueObject(String jglueSon, JGlueContext context, Grammar parentGrammar) {
		super(context, null);
		this.jglueSon = jglueSon;
		this.chars = this.jglueSon.toCharArray();
		parseIndex = 0;
		this.parentGrammar = parentGrammar;
	}
	
	public JglueObject(char[] chars, int startIndex, JGlueContext context, Grammar parentGrammar) {
		super(context, null);
		this.parseIndex = startIndex;
		this.chars = chars;
		this.context = context;
		this.parentGrammar = parentGrammar;
	}
	
	public JglueObject parse() {
		return parse(chars);
	}
	
	public JglueObject parse(char[] chars) {
		for(++parseIndex;parseIndex<chars.length;parseIndex++) {
			if(chars[parseIndex] == ' ') {
				continue;
			}
			if(chars[parseIndex] == '}') {
				parseIndex ++;
				break;
			}
			if(chars[parseIndex] == ']') {
				break;
			}
			String key = parseKey(chars);
			Object value = parseValue(chars);
			put(key, value);
		}
		return this;
	}
	
	public String parseKey(char[] chars) {
		StringBuilder keyBuilder = new StringBuilder();
		for(;parseIndex<chars.length;parseIndex++) {
			if(chars[parseIndex]==':') {
				parseIndex ++;
				break;
			}
			keyBuilder.append(chars[parseIndex]);
		}
		return keyBuilder.toString().trim();
	}
	
	public void check() {
		 
	}
	
	public Object parseValue(char[] chars) {
		char first = chars[parseIndex];
		while(first == ' ') {
			parseIndex ++;
			first = chars[parseIndex];
		}
		if(first == '"' || first == '\'') {
			return parseStrValue(chars);
		} else if (first >= '0' && first <= '9') {
			return parseNumberValue(chars);
		} else if (first == '{') {
			JglueObject value = new JglueObject(chars, parseIndex, context, parentGrammar);
			value.parse();
			parseIndex = value.getParseIndex();
			return value;
		} else if (first == '[') {
			JglueList value = new JglueList(chars, parseIndex, context, parentGrammar);
			value.parse();
			parseIndex = value.getParseIndex();
			return value;
		} else {
			Object obj = parseExpressValue(chars);
			return obj;
		}
	}
	
	private Number parseNumberValue(char[] chars) {
		StringBuilder valueBuilder = new StringBuilder();
		for(;parseIndex<chars.length;parseIndex++) {
			if(chars[parseIndex]==',' ||
					chars[parseIndex]=='}'||
					chars[parseIndex]==']') {
				break;
			}
			valueBuilder.append(chars[parseIndex]);
		}
		String value = valueBuilder.toString().trim();
		return ExpressUtils.parseNumber(value);
	}

	private String parseStrValue(char[] chars) {
		StringBuilder keyBuilder = new StringBuilder();
		for(;parseIndex<chars.length;parseIndex++) {
			if(chars[parseIndex]==',') {
				break;
			} else if(
					chars[parseIndex]=='}'||
					chars[parseIndex]==']') {
				//parseIndex--;   //这里需要注意为什么之前需要-1
				break;
			} 
			keyBuilder.append(chars[parseIndex]);
		}
		String key = keyBuilder.toString().trim();
		if((key.charAt(0)=='\'' && key.charAt(key.length()-1)=='\'') ||
				(key.charAt(0)=='"' && key.charAt(key.length()-1)=='"')) {
			key = key.substring(1, key.length()-1);
		}
		if(StringUtils.isBlank(key)) {
			throw new GlueException("非法的对象值");
		}
		return key;
	}
	
	private Object parseExpressValue(char[] chars) {
		StringBuilder valueBuilder = new StringBuilder();
		int count = 0;
		for(;parseIndex<chars.length;parseIndex++) {
			if(count == 0 && (chars[parseIndex]==',' ||
					chars[parseIndex]=='}'||
					chars[parseIndex]==']')) {
				break;
			} else {
				if(chars[parseIndex] == '('
						|| chars[parseIndex] == '['
						|| chars[parseIndex] == '{') {
					count++;
				} else if(chars[parseIndex] == ')'
						|| chars[parseIndex] == ']'
						|| chars[parseIndex] == '}') {
					count--;
				}
			}
			valueBuilder.append(chars[parseIndex]);
		}
		String value = valueBuilder.toString().trim();
		if(context!=null) {
			try {
				Express valExpress = new JGlueExpress(value, new JProperties(), 0, this.parentGrammar);
				return valExpress.execute(context);
			}catch(Exception e) {
				throw e;
			}
		}
		if(StringUtils.isBlank(value)) {
			throw new GlueException("非法的对象值");
		}
		if(value.equals("null")) {
			return null;
		}
		if(context!=null && context.contains(value)) {
			return context.get(value);
		}
		return prefix + value;
	}
	
	/**
	 * @Title: get   
	 * @Description: 重写get方法
	 * @param: @return      
	 * @return: Object      
	 * @throws
	 */
	public Object get(String key) {
		Object obj = super.get(key);
		return obj;
	}

	public int getParseIndex() {
		return parseIndex;
	}
	
	public void parseIndexAdd() {
		 parseIndex++;
	}
	
	public int parseIndexAddAndGet() {
		 return ++parseIndex;
	}

	public void setParseIndex(int parseIndex) {
		this.parseIndex = parseIndex;
	}
	
	public String toString(JGlueContext context) {
		StringBuilder builder = new StringBuilder();
		return builder.toString();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		for(Map.Entry<String, Object> entity: this.entrySet()) {
			if (builder.length()>1) {
				builder.append(", ");
			}
			builder.append(entity.getKey()).append(": ");
			if (entity.getValue() instanceof String) {
				builder.append("'").append(entity.getValue()).append("'");
			} else {
				builder.append(entity.getValue());
			}
		}
		String str = builder.append("}").toString();
		return str;
	}

	public void rigistFuncs() {		
	}

	public Grammar getParentGrammar() {
		return parentGrammar;
	}

	public void setParentGrammar(Grammar parentGrammar) {
		this.parentGrammar = parentGrammar;
	}

	@Override
	public AbstractInstFunc<JglueObject> buildFunc(String funcName) {
		return factory.getFunc(funcName);
	}
	
	public static class ToStringFunc extends AbstractInstFunc<JglueObject> {

		public ToStringFunc() {
			super("toString");
		}

		@Override
		public Object exec(JGlueContext context, Object[] objs, JglueObject instance) {
			if(instance!=null) {
				return instance.toString();
			}
			return null;
		}
		
	}
	
}
