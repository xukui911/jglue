package com.xw.glue.grammar.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.xw.glue.grammar.Func;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.value.FuncValue;

public class JGlueStack {
	private static ThreadLocal<Stack<GrammarElement>> stackLocal = new ThreadLocal<Stack<GrammarElement>>();
	private static ThreadLocal<List<FuncValue>> funcValues = new ThreadLocal<List<FuncValue>>();
	
	public static void push(GrammarElement element) {
		Stack<GrammarElement> stack = stackLocal.get();
		if(stackLocal.get()==null) {
			stack = new Stack<GrammarElement>();
			stackLocal.set(stack);
		}
		stack.push(element);
	}
	
	public static GrammarElement pop() {
		Stack<GrammarElement> stack = stackLocal.get();
		if(stack!=null) {
			GrammarElement grammar = stack.pop();
			if(grammar!=null) {
				return grammar;
			}
		}
		return null;
	}
	
	public static GrammarElement peek() {
		Stack<GrammarElement> stack = stackLocal.get();
		if(stack!=null && !stack.isEmpty()) {
			GrammarElement grammar = stack.peek();
			if(grammar!=null) {
				return grammar;
			}
		}
		return null;
	}
	
	public static GrammarElement peek(int index) {
		Stack<GrammarElement> stack = stackLocal.get();
		if(stack!=null) {
			GrammarElement grammar = stack.get(index);
			if(grammar!=null) {
				return grammar;
			}
		}
		return null;
	}
	
	public static int size() {
		Stack<GrammarElement> stack = stackLocal.get();
		if(stack!=null) {
			return stack.size();
		}
		return 0;
	}
	
	public static Func getCurFunc() {
		Stack<GrammarElement> stack = stackLocal.get();
		if(stack!=null) {
			int index = stack.size()-1;
			while(index>=0) {
				Grammar grammar = stack.get(index).getGrammar();
				if(grammar instanceof Func) {
					return ((Func)grammar);
				}
				index--;
			}
		}
		return null;
	}
	
	public static List<Func> getCurFuncs() {
		Stack<GrammarElement> stack = stackLocal.get();
		if(stack!=null) {
			List<Func> funcs = new ArrayList<Func>();
			int index = stack.size()-1;
			while(index>=0) {
				Grammar grammar = stack.get(index).getGrammar();
				if(grammar instanceof Func) {
					funcs.add((Func)grammar);
				}
				index--;
			}
			return funcs;
		}
		return null;
	}
	
	public static void addFuncValue(FuncValue funcValue) {
		List<FuncValue> values = funcValues.get();
		if(funcValues.get()==null) {
			values = new ArrayList<FuncValue>();
			funcValues.set(values);
		}
		values.add(funcValue);
	}
	
	public static List<FuncValue> getFuncValues() {
		return funcValues.get();
	}
	
	public static void clear() {
		stackLocal.remove();
	}
	
	public static void clearFuncValue() {
		funcValues.remove();
	}
}
