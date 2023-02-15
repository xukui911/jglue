package com.xw.glue.grammar.parse;

import java.util.List;
import java.util.Stack;

import com.xw.glue.grammar.Fragment;
import com.xw.glue.grammar.Func;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Grammars;
import com.xw.glue.grammar.Procedure;
import com.xw.glue.grammar.stack.GrammarArrayStack;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;

public class SimpleGrammerParse implements GrammarParse {
	
	public SimpleGrammerParse(Procedure procedure) {
	}
	
	public void parse(Fragment fragment, Grammars grammars) {
		Stack<Word> stack = createStack(fragment);
		grammars.parse(stack);
	}
	
	public static Func parseFunc(List<Word> words, JProperties properties, String namespace) {
		return parseFunc(words, properties, namespace, null);
	}
	
	public static Func parseFunc(List<Word> words, JProperties properties, String namespace, Grammar parentGrammar) {
		return parseFunc(new Fragment(words), properties, namespace, parentGrammar);
	}
	
	public static Func parseFunc(Fragment fragment, JProperties properties, String namespace, Grammar parentGrammar) {
		Func func = parseFunc(createStack(fragment), properties, namespace, parentGrammar);
		fragment.addFunc(func);
		return func;
	}
	public static Func parseFunc(Stack<Word> stack, JProperties properties, String namespace, Grammar parentGrammar) {
		//可以通过扩展换成其他FUNC
		Func func = (Func) KeyWords.FUNC.parseGrammar(stack.pop(), stack, properties, namespace, parentGrammar);
		return func;
	}
	
	public static Stack<Word> createStack(Fragment fragment) {
		return createGrammarStack(fragment.getWords());
	}
	
	public static Stack<Word> createStack(List<Word> words) {
		Stack<Word> stack = null;
		if (words != null) {
			stack = new Stack<Word>();
			int size = words.size();
			for (int i=size-1; i>=0; i--) {
				stack.push(words.get(i));
			}
		}
		return stack;
	}
	
	public static Stack<Word> createGrammarStack(List<Word> words) {
		Stack<Word> stack = null;
		if (words != null) {
			stack = new GrammarArrayStack(words);
		}
		return stack;
	}
}
