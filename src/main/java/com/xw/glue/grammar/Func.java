package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.express.Express;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.exec.GrammarElement;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.ParamUtils;
import com.xw.glue.utils.StringUtils;
import com.xw.glue.value.ParamValue;

/**
 * @description：程序方法
 * @author xukui
 *
 */
public class Func extends Grammar implements IFunc, Express {
	String funcName;
	boolean isAnonymous;
	private String namespace;
	private boolean hasReturn;
	List<ParamValue> paramList;
	public Func(String funcName) {
		this.funcName = funcName;
		needCreateContext = false;
	}
	
	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Func addParam(ParamValue paramValue) {
		if(paramList==null) {
			paramList = new ArrayList<ParamValue>();
		}
		paramList.add(paramValue);
		return this;
	}
	
	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public void parse(Stack<Word> stack) {
		stack.pop();
		parseFuncName(stack);
		parseParam(stack);
		boolean isFinish = false;
		Word next = null;
		while(!stack.isEmpty()) {
			 next = stack.pop();
			if(next.toString().equals("{")) {
				continue;
			} else if(next.toString().equals("}")) {
				isFinish = true;
				break;
			} else {
				parseWord(next, stack);
			}
		}
		if (!isFinish) {
			throw new RuntimeException("define func "+this.funcName+" not finish!!!");
		}
	}
	
	
	private void parseFuncName(Stack<Word> stack) {
		Word name = stack.pop();
		if(!name.toString().equals("(")) {
			this.setFuncName(name.toString());
			addParamValue(ParamUtils.createParamValue("this", null));
		} else {
			setAnonymous(true);
			stack.push(name);
		}
	}

	public void parseParam(Stack<Word> stack) {
		Word left = stack.pop();
		if(!left.toString().equals("(")) {
			throw new RuntimeException("wrong func define at line(" + left.getLine() + ")");
		}
		//addParamValue(ParamUtils.createParamValue("this", null));
		String word = stack.pop().toString();
		if(!word.equals(")")) {
			String paramStr = word;
			while(!stack.isEmpty()) {
				Word nextWord = stack.pop();
				String next = nextWord.toString();
				if(!next.equals(")")) {
					paramStr += next;
				} else {
					break;
				}
			}
			if(StringUtils.isNotBlank(paramStr)) {
				for(String param: paramStr.split(",")) {
					String paramCode = param.trim();
					ParamValue paramValue = ParamUtils.createParamValue(paramCode, properties);
					paramValue.setVar(true);
					addParam(paramValue);
					addParamValue(paramValue);
				}
			}
		}
	}

	@Override
	public Result exec(JGlueContext context) {
		JGlueStack.push(new GrammarElement(this, context));
		try {
			Result result = super.exec(context);
			return result;
		}finally {
			JGlueStack.pop();
		}
	}
	
	public String toString() {
		return this.funcName +"(), offset:" + offset;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T execute(JGlueContext context) {
		return (T) exec((JGlueContext)context);
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public String getExpress() {
		return null;
	}

	public boolean isHasReturn() {
		return hasReturn;
	}

	public void setHasReturn(boolean hasReturn) {
		this.hasReturn = hasReturn;
	}

	@Override
	public List<ParamValue> getParams() {
		return paramList;
	}
}
