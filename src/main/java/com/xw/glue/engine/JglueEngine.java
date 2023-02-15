package com.xw.glue.engine;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.factory.FuncFactory;
import com.xw.glue.grammar.Procedure;
import com.xw.glue.grammar.Result;
import com.xw.glue.properties.HighPrecision;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.ParamFuncValue;

public class JglueEngine {
	private Procedure procedure;
	private HighPrecision highPrecision;

	public JglueEngine() {
		JProperties propeties = new JProperties();
		procedure = new Procedure(propeties);
	}

	public JglueEngine addContent(String content) {
		procedure.addContent(content);
		return this;
	}
	
	public JglueEngine addFileContent(String filePath, String content) {
		procedure.addFileContent(filePath, content);
		return this;
	}
	
	public JglueEngine loadFile(String filePath) {
		procedure.addFile(filePath);
		return this;
	}
	
	public JglueEngine grammer() {
		procedure.grammer();
		return this;
	}
	
	public JglueEngine addFunc(IFunc func) {
		FuncFactory.getInstance().regist(func);
		this.procedure.getGrammars().addSubFunc(func);
		return this;
	}
	
	public JglueEngine addFunc(String namespace, IFunc func) {
		this.procedure.getGrammars().addSubFunc(namespace, func);
		return this;
	}
	
	public IFunc getFunc(String name) {
		ParamFuncValue funcParam = this.procedure.getGrammars().getSubFunc(name);
		if (funcParam!=null) {
			return funcParam.getFunc();
		}
		return FuncFactory.getInstance().getFunc(name);
	}
	
	public Object  execFunc(String funcName, JGlueContext context) {
		Result result = procedure.exec(funcName, context);
		if (result!=null) {
			return result.getVal();
		}
		return null;
	}
	
	public Object  execRuntime(String anonymousFunc, JGlueContext context) {
		StringBuilder builder = new StringBuilder();
		builder.append("func runtime() {\n").append(anonymousFunc).append("\n}");
		Procedure  procedure = new Procedure(new JProperties());
		Result result = procedure.addContent(builder.toString()).grammer().exec("runtime", context);
		if (result!=null) {
			return result.getVal();
		}
		return null;
	}
	
	public JglueEngine highPrecision() {
		highPrecision = new HighPrecision(2);
		procedure.getProperties().setHighPrecision(highPrecision);
		return this;
	}
	
	
}

