package com.xw.glue.func;

import java.util.List;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.object.JglueClass;
import com.xw.glue.value.ParamValue;

public abstract class InstanceFunc extends AbstractFunc {
	public InstanceFunc(String funcName) {
		super(funcName);
		addParam("this");
	}
	
	//不能使用单例，每次建造新的对象
	public abstract JglueClass<?> build(JGlueContext context, Object[] objs);
	
	@Override
	public Object exec(JGlueContext context, Object[] objs) {
		Object[] bObjs = new Object[objs.length - 1];
		for(int i=1;i<objs.length;i++) {
			bObjs[i-1] = objs[i];
		}
		JglueClass<?> instance = build(context, bObjs);
		List<ParamValue> params = this.getParams();
		if(params!=null) {
			for(int i=0;i<params.size();i++) {
				if(i<objs.length) {
					instance.put(params.get(i).getSource(), objs[i]);
				}
			}
		}
		context.set(getParams().get(0), instance);
		return instance;
	}
}
