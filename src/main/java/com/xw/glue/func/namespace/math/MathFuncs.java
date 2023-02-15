package com.xw.glue.func.namespace.math;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.AbstractFunc;
import com.xw.glue.func.namespace.NamespaceFuncs;

public class MathFuncs implements NamespaceFuncs{

	@Override
	public String namespace() {
		return "Math";
	}

	@Override
	public AbstractFunc[] namespaceFuncs() {
		return new AbstractFunc[] {
				new FloorMethod(),
				new MinMethod(),
				new MaxMethod(),
				new RandomMethod()
		};
	}

	public static class RandomMethod extends AbstractFunc {
		public RandomMethod() {
			super("random");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs) {
			return Math.random();
		}
		
	}
	
	public static class FloorMethod extends AbstractFunc {
		public FloorMethod() {
			super("floor");
			addParam("value");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs) {
			if (objs[0] ==null) {
				return null;
			}
			if(objs[0] instanceof Float) {
				return Math.floor((Float)objs[0]);
			}
			if(objs[0] instanceof Double) {
				return Math.floor((Double)objs[0]);
			}
			return objs[0];
		}
		
	}
	
	public static class MinMethod extends AbstractFunc {
		public MinMethod() {
			super("min");
			addParam("frist");
			addParam("second");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs) {
			if(objs[0] instanceof Number && objs[1] instanceof Number) {
				if(objs[0] instanceof Integer && objs[1] instanceof Integer) {
					return Math.min((Integer)objs[0], (Integer)objs[1]);
				}
				if(objs[0] instanceof Long && objs[1] instanceof Long) {
					return Math.min((Long)objs[0], (Long)objs[1]);
				}
				return Math.min(Double.parseDouble(objs[0].toString()), Double.parseDouble(objs[1].toString()));
			} else {
				throw new GlueException("please set two number params");
			}
		}
		
	}
	
	public static class MaxMethod extends AbstractFunc {
		public MaxMethod() {
			super("max");
			addParam("frist");
			addParam("second");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs) {
			if (objs[0] ==null) {
				return null;
			}
			if(objs[0] instanceof Number && objs[1] instanceof Number) {
				if(objs[0] instanceof Integer && objs[1] instanceof Integer) {
					return Math.max((Integer)objs[0], (Integer)objs[1]);
				}
				if(objs[0] instanceof Long && objs[1] instanceof Long) {
					return Math.max((Long)objs[0], (Long)objs[1]);
				}
				return Math.max(Double.parseDouble(objs[0].toString()), Double.parseDouble(objs[1].toString()));
			} else {
				throw new GlueException("please set two number params");
			}
		}
		
	}
}
