package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Func;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Result;
import com.xw.glue.grammar.RuntimeFunc;
import com.xw.glue.object.JglueClass;
import com.xw.glue.object.JglueInstance;
import com.xw.glue.utils.FuncUtils;
import com.xw.glue.value.AbsFuncValue;
import com.xw.glue.value.FuncDefValue;
import com.xw.glue.value.FuncParamValue;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.StrValue;
import com.xw.glue.value.Value;

public class MethodExec extends Value<Object> implements Count<ObjValue> {
	Value<?> valueL;
	FuncParamValue valueR;
	FuncExec exec;
	
	public MethodExec(Value<?> valueL, Value<?> valueR) {
		this.valueL = valueL;
		this.valueR = (FuncParamValue)valueR;
		if (valueL instanceof StrValue ||
				valueL instanceof ParamValue) {
			AbsFuncValue funcValue = new AbsFuncValue(this.valueL.getSource(), this.valueR);
			funcValue.initFuncAfterParse();
			exec = new FuncValueExec(funcValue);
		} else if (valueL instanceof MethodExec){
			exec = new MethodExecContinue();
		} else if (valueL instanceof ArrayGet){
			exec = new MethodExecContinue();
		} else if (valueL instanceof FuncDefValue){
			exec = new MethodExecContinue();
		} else {
			exec = new MethodExecContinue();
		}
	}
	@Override
	public ObjValue count(JGlueContext context) {
		return new ObjValue(value(context));
	}

	@Override
	public Object value(JGlueContext context) {	
		return exec.value(context);
	}
	
	public interface FuncExec {
		public Object value(JGlueContext context);
		
	}
	public class FuncValueExec implements FuncExec {
		AbsFuncValue funcValue;
		
		FuncValueExec(AbsFuncValue funcValue){
			this.funcValue = funcValue;
		}
		
		@Override
		public Object value(JGlueContext context) {
			return funcValue.value(context);
		}
		
	}
	
	public class MethodExecContinue implements FuncExec {
		@Override
		public Object value(JGlueContext context) {
			IFunc func = (IFunc)valueL.value(context);
			JGlueContext funcContext = null;
			if(func instanceof RuntimeFunc) {
				RuntimeFunc rFunc = (RuntimeFunc)func;
				funcContext = ContextFactory.getContext(rFunc.getGlobeContext(), (Grammar)rFunc.getFunc());
			} else {
				funcContext = context;
			}
			FuncUtils.initFuncParamValue(context, funcContext, func, valueR.getParamExpressList());
			Result result = func.exec(funcContext);
			if(result!=null) {
				return result.getVal();
			}
			return null;
		}
		
		@SuppressWarnings("rawtypes")
		public JglueClass valueNew(JGlueContext context) {
			JglueClass instance = null;
			IFunc func = (IFunc)valueL.value(context);
			JGlueContext funcContext = null;
			if(func instanceof RuntimeFunc) {
				RuntimeFunc rFunc = (RuntimeFunc)func;
				funcContext = ContextFactory.getContext(rFunc.getGlobeContext(), (Grammar)rFunc.getFunc());
				instance = new JglueInstance(context, rFunc);
				funcContext.set(rFunc.getParamValue("this"), instance);
				if(rFunc.getFunc().isAnonymous()) {
					throw new GlueException("Anonymous func not use new!!!");
				}
			} if(func instanceof Func) {
				Func rFunc = (Func) func;
				instance = new JglueInstance(context, rFunc);
				funcContext.set(rFunc.getParamValue("this"), instance);
				funcContext = ContextFactory.getContext(context, rFunc);
			}else {
				throw new GlueException("new Object must func type");
			}
			FuncUtils.initFuncParamValue(context, funcContext, func, valueR.getParamExpressList());
			func.exec(funcContext);
			return instance;
		}
	}
}
