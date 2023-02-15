package com.xw.glue.utils;

import java.util.ArrayList;
import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.FuncValue;
import com.xw.glue.value.ParamValue;

public class FuncUtils {
	private static IndexCreater creater;
	static {
		creater = new IndexCreater();
	}
	public static FuncValue createFuncValue(List<Word> words, JProperties properties, Grammar parentGrammar) {
		FuncValue funcValue = new FuncValue(words, properties, parentGrammar);
		if(!funcValue.initFuncAfterParse()) {
			JGlueStack.addFuncValue(funcValue);
		}
		return funcValue;
	}
	
	public static int getNextIndex() {
		return creater.next();
	}
	
	public static int getCurIndex() {
		return creater.current();
	}
	
	public static class IndexCreater {
		int curIndex = 16384;		
		public int next() {
			curIndex += 1024;
			return curIndex;
		}
		
		public int current() {
			return curIndex;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Express>[] getParamExpressesArray(List<Word> words, JProperties properties, Grammar parentGrammar) {
		List<Word> paramWords = new ArrayList<Word>();
		List<List<Express>> paramExpressesList = new ArrayList<List<Express>>();
		int count = 0;
		int i = 0;
		for(; i< words.size();i++) {
			Word next = words.get(i);
			if(next.toString().equals("(")) {
				break;
			}
		}
		for(; i< words.size();i++) {
			Word next = words.get(i);
			if(next.toString().equals("(") || 
					next.toString().equals("{") ||
					next.toString().equals("[")
					) {
				count++;
			}
			if(next.toString().equals(")") ||
					next.toString().equals("}") ||
					next.toString().equals("]")) {
				count--;
			}
			paramWords.add(next);
			if (count == 0) {
				paramExpressesList.add(getParamExpressList(paramWords, properties, parentGrammar));
				paramWords = new ArrayList<Word>();
			}
		}
		return paramExpressesList.toArray(new List[0]);
	}
	
	public static List<Express> getParamExpressList(List<Word> words, JProperties properties, Grammar parentGrammar) {
		List<Word> paramWords = new ArrayList<Word>();
		List<Express> paramExpresses = new ArrayList<Express>();
		int count = 0;
		words.remove(0);
		words.remove(words.size()-1);
		for(int i=0; i< words.size();i++) {
			Word next = words.get(i);
			if(next.toString().equals("(") || 
					next.toString().equals("{") ||
					next.toString().equals("[")
					) {
				count++;
			}
			if(next.toString().equals(")") ||
					next.toString().equals("}") ||
					next.toString().equals("]")) {
				count--;
			}
			if(count == 0 && next.toString().endsWith(",")) {
				paramExpresses.add(new JGlueExpress(paramWords, properties, parentGrammar));
				paramWords = new ArrayList<Word>();
			}else {
				paramWords.add(next);
			}
		}
		if(paramWords.size()>0) {
			// 参数格式(XXX,YYY)
			paramExpresses.add(new JGlueExpress(paramWords, properties, parentGrammar));
		}
		return paramExpresses;
	}
	
	public static void initFuncParamValue(JGlueContext context, JGlueContext funcContext, IFunc func, List<Express> paramExpressList) {
		if (func instanceof InstanceFunc) {
			initInsFuncParamValue(context, funcContext, func, paramExpressList);
			return;
		}
		List<ParamValue> paramList = func.getParams();
		if (paramList!=null) {
			int i = 0;
			for (; i<paramList.size();i++) {
				ParamValue paramValue = paramList.get(i);
				Object value = null;
				if(paramExpressList!=null && paramExpressList.size() > i) {
					Express express = paramExpressList.get(i);
					try {
						value = express.execute(context);
					} catch (GlueException e) {
						throw e;
					} catch (Exception e) {
						throw new GlueException("执行"+ express.getExpress() + "报错", ((JGlueExpress)express).getLine());
					}
				}
				funcContext.set(paramValue, value);
			}
		}
	}
	
	public static void initInsFuncParamValue(JGlueContext context, JGlueContext funcContext, IFunc func, List<Express> paramExpressList) {
		List<ParamValue> paramList = func.getParams();
		if (paramList!=null) {
			int i = 1;
			funcContext.set(paramList.get(0), null);
			for (; i<paramList.size();i++) {
				ParamValue paramValue = paramList.get(i);
				Object value = null;
				if(paramExpressList!=null && paramExpressList.size() > i-1) {
					Express express = paramExpressList.get(i-1);
					try {
						value = express.execute(context);
					} catch (GlueException e) {
						throw e;
					} catch (Exception e) {
						throw new GlueException("执行"+ express.getExpress() + "报错", ((JGlueExpress)express).getLine());
					}
				}
				funcContext.set(paramValue, value);
			}
		}
	}
}
