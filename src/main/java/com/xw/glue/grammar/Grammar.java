package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.exec.GrammarElement;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.grammar.stack.GrammarStack;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.ParamUtils;
import com.xw.glue.value.ParamFuncValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value.ValueType;

public abstract class Grammar {
	JProperties properties;
	List<Grammar> grammars = new ArrayList<Grammar>();
	Map<String, ParamFuncValue> subFuncs = new HashMap<String,ParamFuncValue>();
	Map<String, ParamValue> paramValues = new HashMap<String,ParamValue>();
    String namespace;
    Grammar parent;
    List<Word> words;
    int line;
    long offset;
    int paramSize;
    boolean needCreateContext = true;
    boolean needParseStack = true;
    boolean needExecStack = true;
	public abstract void parse(Stack<Word> stack);
	
	//静态解析方法
	public void parseGrammar(Stack<Word> stack) {
		if(stack instanceof GrammarStack) {
			((GrammarStack) stack).start();	
		}
		parse(stack);
		if(stack instanceof GrammarStack) {
			words = ((GrammarStack) stack).stop();
		}
	}
	
	public Result exec(JGlueContext context) {
		//给子函数携带上父函数的上下文环境变量，允许子函数访问父函数的变量
		if(!subFuncs.isEmpty()) {
			for(ParamFuncValue paramFunc: subFuncs.values()) {
				context.set(paramFunc, new RuntimeFunc(paramFunc.getFunc(), context));
			}
		}
		return execSelf(context);
	}
	
	public Result execSelf(JGlueContext context) {
		int size = grammars.size();
			for (int i=0;i<size;i++) {
				Grammar grammar = grammars.get(i);
				Result result = grammar.exec(context);
				if(result!=null && result.getType()!=ValueType.VOID) {
					return result;
				}
			}
		return null;
	}
	
	public void parseContent(Stack<Word> stack, String startMark, String endMark) {
		while(!stack.isEmpty()) {
			Word next = stack.pop();
			if(next.toString().equals(startMark)) {
				continue;
			} else if(next.toString().equals(endMark)) {
				break;
			} else {
				parseWord(next, stack);
			}
		}
	}
	
	public void parseWord(Word curWord, Stack<Word> stack) {
		if(curWord.toString().equals(";")) {
			return;
		}
		GrammarElement parent = JGlueStack.peek();
		String namespace = null;
		if (parent!=null) {
			namespace = parent.getNamespace();
		}
		JGlueStack.push(new GrammarElement(this));
		Grammar grammar = keyWordGrammer(curWord, stack, namespace);
		if (grammar!=null) {
			if(grammar instanceof IFunc) {
				IFunc subFunc = (IFunc) grammar;
				addSubFunc(subFunc);
			} else {
				grammars.add(grammar);	
			}
			grammar.parseAfter();
		}
		JGlueStack.pop();	
	}
	
	public void parseAfter() {	
	}
	
	public Grammar keyWordGrammer(Word curWord, Stack<Word> stack, String namespace) {
		String key = curWord.toString();
		KeyWords keyWords = null;
		try {
			keyWords = KeyWords.valueOf(key.toUpperCase());
		}catch(Exception ex) {
			keyWords = KeyWords.DEFAULT;
		}
		Grammar grammar = keyWords.parseGrammar(curWord, stack, properties, namespace, this);
		return grammar;
	}
	
	public ParamFuncValue getSubFunc(String subFuncName) {
		ParamFuncValue paramFunc = this.subFuncs.get(subFuncName);
		if (paramFunc==null) {
			if (parent!=null) {
				return parent.getSubFunc(subFuncName);
			}
		}
		return paramFunc;
	}
	
	public ParamValue getParamValue(String paramCode) {
		ParamValue paramValue = this.paramValues.get(paramCode);
		if (paramValue==null) {
			if (parent!=null) {
				return parent.getParamValue(paramCode);
			}
		}
		return paramValue;
	}
	
	public boolean contains(String paramCode) {
		return this.paramValues.containsKey(paramCode) ||
				(parent!=null && parent.contains(paramCode));
	}
	
	private void addParamValueCommon(ParamValue paramValue) {
		long offset = getNoZeroOffset();
		if(offset > 0) {
			paramValue.setOffset(offset);
			paramValue.setIndex(getParamSize());
			addParamSize();
		}
	}
	
	public void addParamValue(ParamValue paramValue) {
		if(paramValues.containsKey(paramValue.getSource())) {
			throw new GlueException("already exists param name[" + paramValue.getSource() + "]!!!");
		}
		addParamValueCommon(paramValue);
		if(paramValue instanceof ParamFuncValue) {
			ParamFuncValue paramFunc = (ParamFuncValue) paramValue;
			subFuncs.put(paramFunc.getParamCode(), paramFunc);
		} else {
			paramValues.put(paramValue.getSource(), paramValue);
		}
	}
	
	public void addSubFunc(IFunc func) {
		ParamFuncValue funcParam = ParamUtils.createParamFuncValue(func, properties);
		addParamValueCommon(funcParam);
		subFuncs.put(funcParam.getSource(), funcParam);
	}
	
	public void addGlobleParamValue(ParamValue paramValue) {
		if(this.parent==null) {
			addParamValue(paramValue);
		} else {
			parent.addGlobleParamValue(paramValue);
		}
	}
	
	public Map<String, ParamValue> getParamValues() {
		return paramValues;
	}

	public JProperties getProperties() {
		return properties;
	}

	public void setProperties(JProperties properties) {
		this.properties = properties;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Grammar getParent() {
		return parent;
	}

	public void setParent(Grammar parent) {
		this.parent = parent;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
	
	public long getOffset() {
		return this.offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
	
	public long getNoZeroOffset() {
		if(offset > 0) {
			return offset;
		} else {
			if(parent!=null) {
				return parent.getNoZeroOffset();
			}
			return 0;
		}
	}
	
	public void addParamSize() {
		if(offset > 0) {
			this.paramSize += 1;
		} else {
			parent.addParamSize();
		}
	}
	
	public int getParamSize() {
		if(offset > 0) {
			return paramSize;
		} else {
			return parent.getParamSize();
		}
	}
	
	public void needParseStack(boolean needParseStack) {
		this.needParseStack = needParseStack;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "[offset:"+offset+"]";
	}
	
	public Grammars getTopParent() {
		if(parent==null) {
			return (Grammars)this;
		} else {
			return parent.getTopParent();
		}
	}
}
