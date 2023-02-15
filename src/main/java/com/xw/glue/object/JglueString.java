package com.xw.glue.object;

import java.util.Arrays;
import java.util.List;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.func.factory.AbstractInsFuncFactory;

public class JglueString extends JglueClass<String> {
	private static AbstractInsFuncFactory<String> factory;
	static {
		factory = new AbstractInsFuncFactory<String>();
		factory.add(new LengthFunc());
		factory.add(new SubStrFunc());
		factory.add(new SplitFunc());
		factory.add(new IndexOfFunc());
		factory.add(new TrimFunc());
		factory.add(new StartsWithFunc());
		factory.add(new EndsWithFunc());
		factory.add(new CharAtFunc());
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JglueString(JGlueContext context, String value) {
		super(context, value);
	}
	
	@Override
	public AbstractInstFunc<String> buildFunc(String funcName) {
		return factory.getFunc(funcName);
	}

	public static class LengthFunc extends AbstractInstFunc<String> {
		/**
		 * function length() {}
		* <p>Title: </p>  
		* <p>Description: </p>
		 */
		public LengthFunc() {
			super("length");
		}

		@Override
		public Integer exec(JGlueContext context, Object[] objs, String value) {
			return value.length();
		}
	}
	
	public static class SubStrFunc extends AbstractInstFunc<String> {
		/**
		 * 
		* <p>Title: </p>  
		* <p>Description: function substring(beginIndex, endIndex) {}</p>
		* <p>Example: 'abc'.substring(0,1)</p>
		 */
		public SubStrFunc() {
			super("substring");
			addParam("beginIndex");
			addParam("endIndex");
		}

		@Override
		public String exec(JGlueContext context, Object[] objs, String value) {
			Integer beginIndex = (Integer)objs[0];
			Integer endIndex = (Integer)objs[1];
			if(beginIndex==null && endIndex == null) {
				throw new GlueException("substring method must set beginIndex");
			} else if (beginIndex!=null && endIndex == null) {
				return value.substring(beginIndex);
			} else{
				return value.substring(beginIndex, endIndex);
			}
		}
	}
	
	public static class SplitFunc extends AbstractInstFunc<String> {
		/**
		 * function length() {}
		* <p>Title: </p>  
		* <p>Description: </p>
		 */
		public SplitFunc() {
			super("split");
			addParam("regex");
		}

		@Override
		public List<String> exec(JGlueContext context, Object[] objs, String value) {
			return Arrays.asList(value.split((String)objs[0]));
		}
	}
	
	public static class IndexOfFunc extends AbstractInstFunc<String> {
		/**
		 * function indexOf() {}
		* <p>Title: </p>  
		* <p>Description: </p>
		 */
		public IndexOfFunc() {
			super("indexOf");
			addParam("str");
		}

		@Override
		public Integer exec(JGlueContext context, Object[] objs, String value) {
			return value.indexOf((String)objs[0]);
		}
	}
	
	public static class TrimFunc extends AbstractInstFunc<String> {
		/**
		 * function trim() {}
		* <p>Title: </p>  
		* <p>Description: </p>
		 */
		public TrimFunc() {
			super("trim");
		}

		@Override
		public String exec(JGlueContext context, Object[] objs, String value) {
			return value.trim();
		}
	}
	
	public static class StartsWithFunc extends AbstractInstFunc<String> {
		/**
		 * function startsWith(prefix) {}
		* <p>Title: </p>  
		* <p>Description: </p>
		 */
		public StartsWithFunc() {
			super("startsWith");
			addParam("prefix");
		}

		@Override
		public Boolean exec(JGlueContext context, Object[] objs, String value) {
			return value.startsWith((String)objs[0]);
		}
	}
	
	public static class EndsWithFunc extends AbstractInstFunc<String> {
		/**
		 * function endsWith(suffix) {}
		* <p>Title: </p>  
		* <p>Description: </p>
		 */
		public EndsWithFunc() {
			super("endsWith");
			addParam("suffix");
		}

		@Override
		public Boolean exec(JGlueContext context, Object[] objs, String value) {
			return value.endsWith((String)objs[0]);
		}
	}
	
	public static class CharAtFunc extends AbstractInstFunc<String> {
		/**
		 * function endsWith(suffix) {}
		* <p>Title: </p>  
		* <p>Description: </p>
		 */
		public CharAtFunc() {
			super("charAt");
			addParam("index");
		}

		@Override
		public String exec(JGlueContext context, Object[] objs, String value) {
			if (objs[0] instanceof Integer) {
				return String.valueOf(value.charAt((Integer)objs[0]));
			}
			if (objs[0] instanceof Double) {
				return String.valueOf(value.charAt(((Double)objs[0]).intValue()));
			}
			if (objs[0] instanceof Float) {
				return String.valueOf(value.charAt(((Float)objs[0]).intValue()));
			}
			if (objs[0] instanceof Long) {
				return String.valueOf(value.charAt(((Long)objs[0]).intValue()));
			}
			return String.valueOf(value.charAt(Integer.valueOf(objs[0].toString())));
		}
	}
}
