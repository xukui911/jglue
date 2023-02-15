package com.xw.glue.object;

import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.func.factory.AbstractInsFuncFactory;

public class JglueArrayInstance extends JglueClass<List<Object>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static AbstractInsFuncFactory<List<Object>> factory;
	static {
		factory = new AbstractInsFuncFactory<List<Object>>();
		factory.add(new ArrayLengthFunc());
		factory.add(new ArrayPushFunc());
		factory.add(new ArrayIndexOfFunc());
	}
	public JglueArrayInstance(JGlueContext context, List<Object> array) {
		super(context, array);
		put("length", array.size());
	}

	@Override
	public AbstractInstFunc<List<Object>> buildFunc(String funcName) {
		if(factory.getFunc(funcName)==null) {
			throw new GlueException("不存在的方法:" + funcName);
		}
		return factory.getFunc(funcName);
	}
	
	
	/***************** DEFINE AbstractInstFuncs START ********************/
	public static class ArrayLengthFunc extends AbstractInstFunc<List<Object>> {

		public ArrayLengthFunc() {
			super("size");
		}

		@Override
		public Object exec(JGlueContext context, Object[] objs, List<Object> array) {
			return array.size();
		}
	}
	
	
	public static class ArrayPushFunc extends AbstractInstFunc<List<Object>> {

		public ArrayPushFunc() {
			super("push");
			addParam("obj");
		}

		@Override
		public Object exec(JGlueContext context, Object[] objs, List<Object> array) {
			Object obj = objs[0];
			/**
			if (obj instanceof List) {
				array.addAll((List<?>)obj);
			} else {
				array.add(obj);
			}**/
			array.add(obj);
			return array;
		}
	}
	
	public static class ArrayIndexOfFunc extends AbstractInstFunc<List<Object>> {

		public ArrayIndexOfFunc() {
			super("indexOf");
			addParam("obj");
		}

		@Override
		public Object exec(JGlueContext context, Object[] objs, List<Object> array) {
			if (array==null || array.size() ==0) {
				return -1;
			}
			return array.indexOf(objs[0]);
		}
	}
	
	/***************** DEFINE AbstractInstFuncs END ********************/
}
