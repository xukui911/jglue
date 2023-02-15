package com.xw.glue.value;

import java.util.List;
import com.xw.glue.context.GrammerContext;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.factory.FuncFactory;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Grammars;
import com.xw.glue.grammar.Result;
import com.xw.glue.grammar.RuntimeFunc;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.FuncUtils;

public class AbsFuncValue extends Value<Object> {
	List<Word> words;
	IFunc func;
	JProperties properties;
	JProperties paramProperties;
	String funcName;
	String namespace;
	private List<Express> paramExpressList;
	Grammar parentGrammar;
	FuncExec exec;
	int line;
	public AbsFuncValue(String funcName, FuncParamValue funcParamValue) {
		this.parentGrammar = funcParamValue.getParentGrammar();
		this.funcName = funcName;
		this.line = funcParamValue.line;
		if(JGlueStack.peek()!=null) {
			namespace = JGlueStack.peek().getNamespace();
		}
		this.properties = funcParamValue.getParamProperties();
		paramProperties = this.properties.copy().setDefValue(true);
		this.paramExpressList = funcParamValue.getParamExpressList();
	}	
	
	
	public boolean initFuncAfterParse() {
		if (funcName==null) {
			return false;
		}
		if(parentGrammar!=null) {
			ParamFuncValue funcNameParamValue = parentGrammar.getSubFunc(funcName);
			if (funcNameParamValue != null) {
				// 子函数需要通过上下文实时获取
				Grammar grammer = (Grammar) funcNameParamValue.getFunc();
				if (grammer.getParent() != null && !(grammer.getParent() instanceof Grammars)) {
					exec = new SubFuncExec(funcNameParamValue);
				} else {
					func = funcNameParamValue.getFunc();
				}
			}
			ParamValue paramValue = parentGrammar.getParamValue(funcName);
			if (paramValue != null) {
				if (exec == null) {
					exec = new ParamExec(paramValue);
				} else {
					if(paramValue.getOffset() > funcNameParamValue.getOffset() || paramValue.getIndex() > funcNameParamValue.getIndex()) {
						exec = new ParamExec(paramValue, funcNameParamValue);
					}
				}
			}
			if(exec!=null) {
				return true;
			}
		}
		if(func==null) {
			this.func = FuncFactory.getInstance().getFunc(namespace, funcName);
		}
		if(func==null) {
			this.func = FuncFactory.getInstance().getInsFunc(funcName);
		}
		if(func!=null) {
			exec = new GlobleFuncExec();
		}
		return false;
	}
	
	public void afterNamespaceFunc(String namespace) {
		this.namespace = namespace;
		func = FuncFactory.getInstance().getFunc(namespace, getFuncName());
	}
	
	@Override
	public Object value(JGlueContext context) {
		if(exec == null) {
			this.initFuncAfterParse();
		}
		if(exec==null) {
			throw new GlueException("not defined method:" + funcName, line);
		}
		GrammerContext grammerContext = (GrammerContext)context;
		Result result = exec.value(grammerContext);
		Object val = null;
		if(result!=null) {
			val = result.getVal();
		}
		return val;
	}
	
	public Object value(JGlueContext context, JGlueContext funcContext, IFunc func) {
		if (func!=null) {
			FuncUtils.initFuncParamValue(context, funcContext, func, paramExpressList);
			Result result = func.exec(funcContext);
			Object val = null;
			if(result!=null && !ValueType.VOID.equals(result.getType())) {
				val = result.getVal();
			}
			return val;
		} else {
			throw new GlueException("not defined method:" + funcName, words.get(0).getLine());
		}
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}	
	
	public JProperties getProperties() {
		return properties;
	}

	public List<Express> getParamExpressList() {
		return paramExpressList;
	}
    
	public Grammar getParentGrammar() {
		return parentGrammar;
	}


	public void setParentGrammar(Grammar parentGrammar) {
		this.parentGrammar = parentGrammar;
	}


	public List<Word> getWords() {
		return words;
	}

	public static interface FuncExec {
		public Result value(GrammerContext context);
	}
	
	public class ParamExec implements FuncExec {
		ParamValue paramValue;
		ParamFuncValue funcNameParamValue;
		public ParamExec(ParamValue paramValue) {
			this.paramValue = paramValue;
		}
		public ParamExec(ParamValue paramValue, ParamFuncValue funcNameParamValue) {
			this.paramValue = paramValue;
			this.funcNameParamValue = funcNameParamValue;
		}
		public Result value(GrammerContext context) {
			Object oFunc = context.get(paramValue);
			if (oFunc!=null && oFunc instanceof RuntimeFunc) {
				RuntimeFunc rFunc = (RuntimeFunc)oFunc;
				JGlueContext funcContext = ContextFactory.getContext(rFunc.getGlobeContext(), (Grammar)rFunc.getFunc());
				FuncUtils.initFuncParamValue(context, funcContext, rFunc, paramExpressList);
				return rFunc.exec(funcContext);
			} else {
				if (funcNameParamValue!=null) {
					AbsFuncValue.this.exec = new SubFuncExec(funcNameParamValue);
				} else {
					if(func==null) {
						AbsFuncValue.this.func = FuncFactory.getInstance().getFunc(namespace, funcName);
					}
					if(func==null) {
						AbsFuncValue.this.func = FuncFactory.getInstance().getInsFunc(funcName);
					}
					AbsFuncValue.this.exec = new GlobleFuncExec();					
				}
				return AbsFuncValue.this.exec.value(context);
			}
		}
	}
	
	public class GlobleFuncExec implements FuncExec {
		public Result value(GrammerContext context) {
			if (func==null) {
				initFuncAfterParse();
			}
			if (func==null) {
				throw new GlueException("not defined method:" + funcName, words.get(0).getLine());
			}
			JGlueContext funcContext = null;
			funcContext = ContextFactory.getContext(context.getGlobeContext(), (Grammar)func);
			FuncUtils.initFuncParamValue(context, funcContext, func, paramExpressList);
			return func.exec(funcContext);
		}
	}
	
	public class SubFuncExec implements FuncExec {
		ParamFuncValue paramValue;
		public SubFuncExec(ParamFuncValue paramValue) {
			this.paramValue = paramValue;
		}
		public Result value(GrammerContext context) {
			IFunc pFunc = paramValue.getFunc();
			RuntimeFunc rFunc = (RuntimeFunc)context.get(paramValue);
			JGlueContext funcContext = ContextFactory.getContext(rFunc.getGlobeContext(), (Grammar)pFunc);
			FuncUtils.initFuncParamValue(context, funcContext, rFunc, paramExpressList);
			return rFunc.exec(funcContext);
		}
	}
	
	public IFunc getFunc() {
		if(func==null) {
			initFuncAfterParse();
		}
		return func;
	}
}
