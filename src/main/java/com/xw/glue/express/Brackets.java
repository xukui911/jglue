package com.xw.glue.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xw.glue.lexical.word.Word;
import com.xw.glue.value.FuncParamValue;
import com.xw.glue.value.JObjValue;
import com.xw.glue.value.ListValue;
import com.xw.glue.value.Value;

public enum Brackets {
	
	Round("(", ")") {
		@Override
		public BracketsResult value(List<Word> words, JGlueExpress express, Value<?> frontValue, List<Word> paramWords, List<Object> list) {
			if (frontValue==null && paramWords.size() ==0) {
				words.remove(0);
				words.remove(words.size()-1);
				return new BracketsResult(new JGlueExpress(words, express.properties, express.parentGrammar).value);
			}
			return new BracketsResult(new FuncParamValue(words, express.properties, express.parentGrammar), OperatorSign.FUNC_EXE);
		}
	},
	Quare("[", "]") {
		@Override
		public BracketsResult value(List<Word> words, JGlueExpress express, Value<?> frontValue, List<Word> paramWords, List<Object> list) {
			if (frontValue==null && paramWords.size() ==0) {
				return new BracketsResult(new ListValue(words, express.properties, express.parentGrammar));
			}
		
			words.remove(0);
			words.remove(words.size()-1);
			return new BracketsResult(new JGlueExpress(words, express.properties, express.parentGrammar).value, OperatorSign.ARRAY_SUFFIX);
		}
	},
	Curly("{", "}") {
		@Override
		public BracketsResult value(List<Word> words, JGlueExpress express, Value<?> frontValue, List<Word> paramWords, List<Object> list) {
			return new BracketsResult(new JObjValue(words, express.properties, express.parentGrammar));
		}
	};
	
	public static class BracketsResult {
		Value<?> value;
		OperatorSign sign;
		
		public BracketsResult(Value<?> value) {
			this(value, null);
		}
		
		BracketsResult(Value<?> value, OperatorSign sign) {
			this.value = value;
			this.sign = sign;
		}
	}
	
	private String start;
	private String end;
	
	Brackets(String start, String end) {
		this.start = start; 
		this.end = end;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
	
	public List<Word> bracketsWords(List<Word> words, int start) {
		List<Word> dWords = new ArrayList<Word>();
		int count = 0;
		
		for(;start < words.size();start++) {
			if (getStart().equals(words.get(start).toString())) {
				count++;
			}
			if (getEnd().equals(words.get(start).toString())) {
				count--;
			}
			dWords.add(words.get(start));
			if (count==0) {
				break;
			}
		}
		return dWords;
	}
	
	public abstract BracketsResult value(List<Word> words, JGlueExpress express, Value<?> frontValue, List<Word> paramWords, List<Object> list);
	
	private static Map<String, Brackets> map = new HashMap<String, Brackets>();
	static {
		Brackets[] brackets= Brackets.values();
		if(brackets!=null) {
			for(Brackets bracket: brackets) {
				map.put(bracket.getStart(), bracket);
			}
		}
	}
	
	public static Brackets bracket(String start) {
		return map.get(start);
	}
}
