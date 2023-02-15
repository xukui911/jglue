package com.xw.glue.func.instance;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.func.factory.AbstractInsFuncFactory;
import com.xw.glue.object.JglueClass;

public class ThreadInstance extends InstanceFunc {
	private static AbstractInsFuncFactory<Thread> factory;
	static {
		factory = new AbstractInsFuncFactory<Thread>();
		factory.add(new StartFunc());
		factory.add(new JoinFunc());
	}

	public ThreadInstance() {
		super("Thread");
		addParam("run");
		addParam("tName");
	}

	@Override
	public JglueClass<Thread> build(JGlueContext context, Object[] objs) {
		if(objs.length > 1) {
			return new ThreadClass(context, (IFunc)objs[0], (String)objs[1]);
		} else {
			return new ThreadClass(context, (IFunc)objs[0]);
		}
	}
	
	public static class ThreadClass extends JglueClass<Thread> {
		ThreadClass(JGlueContext context, IFunc func) {
			this(context, func, null);
		}
		ThreadClass(JGlueContext context, IFunc func, String name) {
			super(context, new  Thread(new Runnable() {
				public void run() {
					func.exec(context);
				}
			}));
			if(name!=null && name.trim().length() > 0) {
				instance.setName(name);
			}
			put("name", instance.getName());
			put("id", instance.getId());
			if(func.getParams()!=null && func.getParams().size() > 0) {
				context.set(func.getParams().get(0), this);
			}
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Override
		public AbstractInstFunc<Thread> buildFunc(String funcName) {
			return factory.getFunc(funcName);
		}
	}
	
	static class StartFunc extends AbstractInstFunc<Thread> {
		StartFunc() {
			super("start");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, Thread thread) {
			thread.start();
			return null;
		}
		
	}
	
	static class JoinFunc extends AbstractInstFunc<Thread> {
		JoinFunc() {
			super("join");
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, Thread thread) {
			try {
				thread.join();
			}catch(Exception e) {
				throw new GlueException(e);
			}
			return null;
		}
		
	}
	
	
}
