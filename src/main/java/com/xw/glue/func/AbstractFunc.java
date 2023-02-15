package com.xw.glue.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Result;
import com.xw.glue.grammar.exec.GrammarElement;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.utils.FuncUtils;
import com.xw.glue.utils.ParamUtils;
import com.xw.glue.value.ParamValue;

public abstract class AbstractFunc extends Grammar implements IFunc{
	String funcName;
	List<ParamValue> params;
	private String namespace;
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public AbstractFunc() {
	}

	public AbstractFunc(String funcName) {
		this(null, funcName);
	}
	
	public AbstractFunc(String namespace, String funcName) {
		this.funcName = funcName;
		this.namespace = namespace;
		this.setOffset(FuncUtils.getNextIndex());
		this.needParseStack(false);
	}
	
	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	
	public List<ParamValue> getParams() {
		return params;
	}

	public void setParams(List<ParamValue> params) {
		this.params = params;
	}
	
	public void addParam(String param) {
		if(this.params==null) {
			this.params = new ArrayList<ParamValue>();
		}
		ParamValue paramValue = ParamUtils.createParamValue(param, null);
		addParamValue(paramValue);
		this.params.add(paramValue);
	}
	
	public Result exec(JGlueContext context) {
		JGlueStack.push(new GrammarElement(this, context));
		try {
			Object[] objs = null;
			if(params!=null && params.size()>0) {
				objs = new Object[params.size()];
				for(int i=0;i<params.size();i++) {
					objs[i] = context.get(params.get(i));
				}
			}
			Object obj = exec(context, objs);
			return ExpressUtils.buildResult(obj, null);
		}finally {
			JGlueStack.pop();
		}
	}

	public boolean isAnonymous() {
		return false;
	}
	
	@Override
	public void parse(Stack<Word> stack) {			
	}
	
	public abstract Object exec(JGlueContext context, Object[] objs);
}
