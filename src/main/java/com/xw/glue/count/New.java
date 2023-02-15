package com.xw.glue.count;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.count.MethodExec.FuncValueExec;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.grammar.Func;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.object.JglueClass;
import com.xw.glue.object.JglueInstance;
import com.xw.glue.value.FuncValue;
import com.xw.glue.value.InsFuncValue;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.Value;

@SuppressWarnings("rawtypes")
public class New extends Value<JglueClass> implements Count<ObjValue>{
	Value<?> value;
	Grammar parentGrammar;
	public New(Value<?> value, Grammar parentGrammar) {
		this.value = value;
		this.parentGrammar = parentGrammar;
	}
	@Override
	public ObjValue count(JGlueContext context) {
		return new ObjValue(value(context));
	}

	@Override
	public JglueClass value(JGlueContext context) {
		if(value instanceof MethodExec) {
			MethodExec exec = (MethodExec)value;
			if(exec.exec instanceof MethodExec.MethodExecContinue) {
				MethodExec.MethodExecContinue mc = (MethodExec.MethodExecContinue) exec.exec;
				return mc.valueNew(context);
			}
			FuncValueExec fExec = (FuncValueExec)exec.exec;
			IFunc gFunc = (IFunc)fExec.funcValue.getFunc();
			if(gFunc instanceof Func) {
				Func rFunc = (Func)gFunc;
				JGlueContext funcContext = ContextFactory.getContext(context, rFunc);
				JglueClass instance = new JglueInstance(context, gFunc);
				funcContext.set(rFunc.getParamValue("this"), instance);
				fExec.funcValue.value(context, funcContext, rFunc);
				return instance;
			} else if(gFunc instanceof InstanceFunc) {
				JGlueContext funcContext = ContextFactory.getContext(context, (InstanceFunc)gFunc);
				return (JglueClass) fExec.funcValue.value(context, funcContext, gFunc);
			}
		} else if(value instanceof FuncValue) {
			FuncValue funcValue = (FuncValue)value;
			Func gFunc = null;
			if (funcValue.getFuncName().indexOf(".")!=-1) {
				gFunc = new JGlueExpress(funcValue.getFuncName(), parentGrammar.getProperties(), 999, parentGrammar).execute(context);
			} else {
				gFunc = (Func)funcValue.getFunc();
			}
			JGlueContext funcContext = ContextFactory.getContext(context, gFunc);
			JglueClass instance = new JglueInstance(context, gFunc);
			funcContext.set(gFunc.getParamValue("this"), instance);
			funcValue.value(context, funcContext, gFunc);
			return instance;
		}
		else if(value instanceof InsFuncValue) {
			return (JglueClass)((InsFuncValue)value).value(context);
		} else if(value instanceof Exec) {
			return (JglueClass)((Exec)value).value(context);
		}
		throw new GlueException("not support oop["+value.getSource()+"]!!!");
	}
	public Value<?> getName() {
		return value;
	}
}
