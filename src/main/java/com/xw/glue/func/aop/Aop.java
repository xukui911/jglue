package com.xw.glue.func.aop;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Result;

public abstract class Aop {
	
	private String pointCut;
	
	public Aop(String pointCut) {
		this.pointCut = pointCut;
	}
	
	public abstract AopAround getAround();
	
	public static interface AopAround {
		public Result around(IFunc func, JGlueContext context);
	}

	public String getPointCut() {
		return pointCut;
	}

	public void setPointCut(String pointCut) {
		this.pointCut = pointCut;
	}
}
