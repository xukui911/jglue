package com.xw.glue.func.factory;

import com.xw.glue.func.Funcs;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.func.LenFunc;
import com.xw.glue.func.PrintFunc;
import com.xw.glue.func.PushFunc;
import com.xw.glue.func.SshExecFunc;
import com.xw.glue.func.SubstrFunc;
import com.xw.glue.func.instance.DateInstance;
import com.xw.glue.func.instance.ThreadInstance;
import com.xw.glue.func.instance.promise.PromiseInstance;
import com.xw.glue.func.namespace.FuncNamespaces;
import com.xw.glue.func.namespace.InstanceFuncs;
import com.xw.glue.func.namespace.NamespaceFuncs;
import com.xw.glue.func.namespace.math.MathFuncs;
import com.xw.glue.utils.StringUtils;

public class FuncFactory implements Regist{
	private static FuncFactory factory = new FuncFactory();
	private FuncNamespaces namespaces = FuncNamespaces.getInstance();
	private InstanceFuncs insFuncs = new InstanceFuncs();
	private FuncFactory() {
		initRegisty();
	}
	
	private void initRegisty() {
		this.regist(new LenFunc());
		this.regist(new SubstrFunc());
		this.regist(new PrintFunc());
		this.regist(new PushFunc());
		this.regist(new SshExecFunc());
		//this.regist(new ThreadFuncs());
		
		this.regist(new ThreadInstance());
		this.regist(new DateInstance());
		this.regist(new PromiseInstance());
		
		
		//NameSpace注册
		this.regist(new MathFuncs());
	}

	public static FuncFactory getInstance() {
		return factory;
	}

	@Override
	public synchronized void regist(IFunc func) {
		if(func!=null) {
			regist(func.getNamespace(), func);
		}
		
	}
	
	@Override
	public synchronized void regist(Funcs funcs) {
		if(funcs!=null) {
			IFunc[] ifuncs = funcs.getFuncs();
			if(ifuncs!=null && ifuncs.length > 0) {
				for(IFunc func: ifuncs) {
					regist(funcs.getNamespace(), func);
				}
			}
		}
	}
	
	@Override
	public synchronized void regist(String namespace, IFunc func) {
		if(func!=null) {
			if (func instanceof InstanceFunc) {
				insFuncs.regist((InstanceFunc)func);
				return;
			}
			if (StringUtils.isBlank(namespace)) {
				namespace = FuncNamespaces.STATIC_NAMESPACE;
			}
			namespaces.addFunc(namespace, func);
		}
	}
	
	@Override
	public synchronized void regist(NamespaceFuncs namespaceFuncs) {
		if(namespaceFuncs!=null) {
			if(namespaceFuncs.namespaceFuncs()!=null) {
				for(IFunc func: namespaceFuncs.namespaceFuncs()) {
					namespaces.addFunc(namespaceFuncs.namespace(), func);
				}
			}
		}
	}
	
	public boolean hasNamespace(String namespace) {
		return namespaces.hasNamespace(namespace);
	}

	@Override
	public IFunc getFunc(String funcName) {
		return namespaces.getFunc(funcName);
	}
	
	public InstanceFunc getInsFunc(String funcName) {
		return insFuncs.getFunc(funcName);
	}
	
	@Override
	public IFunc getFunc(String namespace, String funcName) {
		if(namespace==null) {
			return namespaces.getFunc(FuncNamespaces.STATIC_NAMESPACE, funcName);
		}
		return namespaces.getFunc(namespace, funcName);
	}

	@Override
	public synchronized IFunc offline(String funcName) {
		IFunc func = namespaces.getFunc(funcName);
		if(func!=null) {
			namespaces.removeFunc(funcName);
		}
		return func;
	}

}
