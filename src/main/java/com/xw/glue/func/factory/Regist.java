package com.xw.glue.func.factory;

import com.xw.glue.func.Funcs;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.func.namespace.NamespaceFuncs;

public interface Regist {
	
	public void regist(IFunc func);
	
	public void regist(String namespace, IFunc func);
	
	public void regist(Funcs funcs);
	
	public void regist(NamespaceFuncs namespaceFuncs);
	
	public IFunc getFunc(String funcName);
	
	public IFunc getFunc(String namespace, String funcName);
	
	public IFunc offline(String funcName);
	
	public InstanceFunc getInsFunc(String funcName);
}
