package com.xw.glue.count;

import java.util.List;
import java.util.Map;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.factory.FuncFactory;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Grammars;
import com.xw.glue.grammar.RuntimeFunc;
import com.xw.glue.object.JglueArrayInstance;
import com.xw.glue.object.JglueClass;
import com.xw.glue.object.JglueString;
import com.xw.glue.utils.ReflectUtils;
import com.xw.glue.value.FuncNameParamValue;
import com.xw.glue.value.FuncValue;
import com.xw.glue.value.LibFuncValue;
import com.xw.glue.value.LibValue;
import com.xw.glue.value.NamespaceFuncValue;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value;

public class Exec extends Value<Object> implements Count<ObjValue> {
	private Value<?> left;
	private Value<?> right;
	private TypeExec typeExec;
	public Exec(Value<?> value, Value<?> value2) {
		this.left = value;
		this.right = value2;
		if(this.right instanceof FuncNameParamValue) {
			if(this.left instanceof NamespaceFuncValue) {
				typeExec = new GetNamespaceFuncExec((NamespaceFuncValue) left);
			} else if (left instanceof LibValue) {
				typeExec = new GetLibFuncExec((LibValue)left, (FuncNameParamValue) right);
			} else {
				typeExec = new GetFuncExec((ParamValue) right);
			}
		}
		else if(this.right instanceof ParamValue) {
			typeExec = new GetParamExec((ParamValue) right);
		} else if(this.right instanceof FuncValue) {
			if(this.left instanceof LibValue) {
				typeExec = new LibFuncExec((FuncValue) right);
			} else if(this.left instanceof NamespaceFuncValue) {
				typeExec = new NamespaceFuncExec((FuncValue) right, (NamespaceFuncValue) left);
			} else {
				typeExec = new FuncExec((FuncValue) right);
			}
		}
		if(typeExec==null) {
			throw new GlueException("没有找到对应的执行器："+this.right.getClass().getName());
		}
	}
	@Override
	public ObjValue count(JGlueContext context) {
		Object obj = value(context);
		return new ObjValue(obj);
	}

	@Override
	public Object value(JGlueContext context) {
		return typeExec.value(context);
	}
	public Value<?> getLeft() {
		return left;
	}

	public Value<?> getRight() {
		return right;
	}
	
	public static interface TypeExec {
		public Object value(JGlueContext context);
	}
	
	public class GetParamExec implements TypeExec {
		ParamValue paramValue;
		
		GetParamExec(ParamValue paramValue) {
			this.paramValue = paramValue;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Object value(JGlueContext context) {
			Object obj = left.value(context);
			if (obj instanceof String) {
				JglueClass leftObj = new JglueString(context, (String)obj);
				obj = leftObj.get(right.getSource());
			}else if(obj instanceof List) {
				JglueClass leftObj = new JglueArrayInstance(context, (List)obj);
				obj = leftObj.get(right.getSource());
			}else if(obj instanceof Map) {
				Map<String, Object> leftObj = (Map<String, Object>) obj;
				obj = leftObj.get(right.getSource());
			} else if(obj instanceof Grammars) {
				obj = ((Grammars)obj).getSubFunc(right.getSource()).getFunc();
			}else {
				obj = ReflectUtils.getFieldValue(obj, right.getSource());
			}
			return obj;
		}
	}
	
	public class GetFuncExec implements TypeExec {
		ParamValue paramValue;
		
		GetFuncExec(ParamValue paramValue) {
			this.paramValue = paramValue;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Object value(JGlueContext context) {
			Object obj = left.value(context);
			if (obj instanceof String) {
				obj = new JglueString(context, (String)obj);
			} else if(obj instanceof List) {
				obj = new JglueArrayInstance(context, (List)obj);
			}
			JglueClass leftObj = (JglueClass) obj;
			if(leftObj==null) {
				throw new GlueException("not exist object:" + left.getSource());
			}
			RuntimeFunc func = leftObj.getFunc(paramValue.getSource());
			if(func==null) {
				throw new GlueException("not defined method:" + paramValue.getSource() +" in Object ");
			}
			return func;
		}
	}
	
	public class GetNamespaceFuncExec implements TypeExec {
		NamespaceFuncValue namespaceFuncValue;
		
		GetNamespaceFuncExec(NamespaceFuncValue namespaceFuncValue) {
			this.namespaceFuncValue = namespaceFuncValue;
		}
		
		@Override
		public Object value(JGlueContext context) {
			return FuncFactory.getInstance().getFunc(namespaceFuncValue.getNamespace(), Exec.this.right.getSource());
		}
	}
	
	public class FuncExec implements TypeExec {
		FuncValue funcValue;
		
		FuncExec(FuncValue funcValue) {
			this.funcValue = funcValue;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Object value(JGlueContext context) {
			Object obj = left.value(context);
			if (obj instanceof String) {
				obj = new JglueString(context, (String)obj);
			} else if(obj instanceof List) {
				obj = new JglueArrayInstance(context, (List)obj);
			}
			JglueClass leftObj = (JglueClass) obj;
			if(leftObj==null) {
				throw new GlueException("not exist object:" + left.getSource(), funcValue.getWords().get(0).getLine());
			}
			RuntimeFunc func = leftObj.getFunc(funcValue.getFuncName());
			if(func==null) {
				throw new GlueException("not defined method:" + funcValue.getFuncName() +" in Object ", funcValue.getWords().get(0).getLine());
			}
			JGlueContext funcContext = ContextFactory.getContext(func.getGlobeContext(), (Grammar)func.getFunc());
			return funcValue.value(context , funcContext, func);
		}
	}
	
	public class GetLibFuncExec implements TypeExec {
		LibValue libValue;
		FuncNameParamValue paramValue;
		GetLibFuncExec(LibValue libValue, FuncNameParamValue paramValue) {
			this.libValue = libValue;
			this.paramValue = paramValue;
		}
		
		@Override
		public Object value(JGlueContext context) {
			return libValue.getFunc(paramValue.getSource());
		}
	}
	
	public class LibFuncExec implements TypeExec {
		LibFuncValue funcValue;
		LibFuncExec(FuncValue funcValue) {
			this.funcValue = new LibFuncValue(funcValue, (LibValue)left);
		}
		
		@Override
		public Object value(JGlueContext context) {
			return funcValue.value(context);
		}
	}
	
	public class NamespaceFuncExec implements TypeExec {
		NamespaceFuncValue funcValue;
		NamespaceFuncExec(FuncValue fValue, NamespaceFuncValue funcValue) {
			funcValue.setFuncValue(fValue);
			this.funcValue = funcValue;
		}
		
		@Override
		public Object value(JGlueContext context) {
			return funcValue.value(context);
		}
	}
}
