package com.xw.glue.grammar;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.xw.glue.func.IFunc;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.FuncUtils;
import com.xw.glue.utils.StringUtils;

public class Grammars extends Grammar {
	Procedure procedure;
	volatile AtomicBoolean isParse = new AtomicBoolean(false);
	private Map<String, Grammars> libGrammarsMap = new ConcurrentHashMap<String, Grammars>();

	public Grammars(Procedure procedure) {
		this.procedure = procedure;
		this.setOffset(FuncUtils.getCurIndex());
		this.properties = procedure.getProperties();
	}
	
	@Override
	public void parse(Stack<Word> stack) {
		if(stack!=null) {
			while(!stack.isEmpty()) {
				Word word = stack.pop();
				if (word.toString().equals(KeyWords.IMPORT.getKeyWord())) {
					KeyWords.IMPORT.parseGrammar(word, stack, properties, namespace, this);
					continue;
				}
				if (word.toString().equals(KeyWords.FUNC.getKeyWord()) ||
						word.toString().equals(KeyWords.FUNCTION.getKeyWord()) ) {
					Func func = (Func)KeyWords.FUNC.parseGrammar(word, stack, properties, namespace, this);
					addSubFunc(func);
				} else {
					Grammar grammar = KeyWords.valueOf(word.toString()).parseGrammar(word, stack, properties, namespace, this);
					grammars.add(grammar);
				}
			}
		}
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}
	
	public boolean isParse() {
		return isParse.get();
	}
	
	public void addImport(Import impt) {
		Grammars grammars = null;
		boolean hasFile = procedure.hasFile(impt.getFilePath());
		if(StringUtils.isNotBlank(impt.getLibName())) {
			grammars = procedure.createFileGrammars(impt.getFilePath());
			libGrammarsMap.put(impt.getLibName(), grammars);
		} else {
			grammars = this;
		}
		if(!hasFile) {
			Fragment fragent = procedure.parseFile(impt.getFilePath());
			procedure.getGrammerParse().parse(fragent, grammars);
		}
	}
	
	public void addImportOld(Import impt) {
		Grammars grammars = null;
		if(!procedure.hasFile(impt.getFilePath())) {
			Fragment fragent = procedure.parseFile(impt.getFilePath());
			grammars = procedure.createFileGrammars(impt.getFilePath());
			procedure.getGrammerParse().parse(fragent, grammars);
		} else {
			grammars = procedure.createFileGrammars(impt.getFilePath());
		}
		if(StringUtils.isNotBlank(impt.getLibName())) {
			libGrammarsMap.put(impt.getLibName(), grammars);
		} else {
			String[] paths = impt.getFilePath().split("/");
			libGrammarsMap.put(paths[paths.length - 1].split("\\.")[0], grammars);
		}
	}
	
	public void addSubFunc(String libName, IFunc func) {
		Grammars grammers = findLib(libName);
		if (grammers==null) {
			grammers = new Grammars(procedure);
			libGrammarsMap.put(libName, grammers);
		}
		grammers.addSubFunc(func);
	}
	
	public Grammars findLib(String libName) {
		return libGrammarsMap.get(libName);
	}
}
