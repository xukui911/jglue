package com.xw.glue.func;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.utils.StringUtils;

public class ThreadFuncs implements Funcs {
	@Override
	public String getNamespace() {
		return "thread";
	}

	@Override
	public IFunc[] getFuncs() {
		return new IFunc[] {
				new ThreadStartFunc(),
				new ThreadJoinFunc()
		};
	}
	
	public static class ThreadStartFunc extends AbstractFunc {

		public ThreadStartFunc() {
			super("thread", "start");
			addParam("callback");
			addParam("name"); // 可选参数
		}

		@Override
		public Object exec(JGlueContext context, Object[] objs) {
			Object obj = context.get("callback");
			String name = (String) context.get("name");
			if(obj!=null && obj instanceof IFunc) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						((IFunc)obj).exec(context);
					}
				});
				if(StringUtils.isNotBlank(name)) {
					t.setName(name);
				}
				t.start();
				return t;
			} else {
				throw new GlueException("start thread must set param func type!!");
			}
		}
	}
	
	public static class ThreadJoinFunc extends AbstractFunc {

		public ThreadJoinFunc() {
			super("thread", "join");
			addParam("thread");
		}

		@Override
		public Object exec(JGlueContext context, Object[] objs) {
			Object obj = context.get("thread");
			if(obj!=null && obj instanceof Thread) {
				Thread thread = (Thread)obj;
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			} else {
				throw new GlueException("start thread must set param Thread type!!");
			}
		}
	}
}
