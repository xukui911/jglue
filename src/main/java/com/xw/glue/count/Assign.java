package com.xw.glue.count;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.ReflectUtils;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.ParamExpressValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value;

/**
 * 赋值操作
 * @author xukui
 *
 */
public class Assign extends Value<Object> implements Count<ObjValue>{
        Value<?> valueL;
        Value<?> valueR;
        JProperties propertis;
        AssignType assignType;
        public Assign(Value<?> value, Value<?> value2, JProperties propertis) {
                this.valueL = value;
                this.valueR = value2;
                this.propertis = propertis;
                if(this.valueL instanceof ParamValue) {
                        assignType = AssignType.Param;
                } else if(this.valueL instanceof ParamExpressValue) {
                        assignType = AssignType.ParamExpress; 
                } else if(this.valueL instanceof Exec) {
                        assignType = AssignType.Exec;
                } else if(this.valueL instanceof ArrayGet){
                	assignType = AssignType.ArraySet;
                }
        }
        
        @Override
        public ObjValue count(JGlueContext context) {
                return new ObjValue(value(context));
        }
        
        @Override
        public Object value(JGlueContext context) {
                assignType.assign(this, context);
                return null;
        }

        public Value<?> getValueL() {
                return valueL;
        }

        public void setValueL(Value<?> valueL) {
                this.valueL = valueL;
        }

        public Value<?> getValueR() {
                return valueR;
        }

        public void setValueR(Value<?> valueR) {
                this.valueR = valueR;
        }
        
        public static enum AssignType {
                //给变量赋值
                Param {
                        @Override
                        public void assign(Assign assign, JGlueContext context) {
                                Object rightObj = assign.valueR.value(context);
                                ParamValue paramValue = (ParamValue) assign.valueL;
                                paramValue.assign(context, rightObj);
                        }
                },
       
                //给下标变量赋值a['a'] 或者 a[1]
                ParamExpress{
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        @Override
                        public void assign(Assign assign, JGlueContext context) {
                                Object rightObj = assign.valueR.value(context);
                                ParamExpressValue pExpress = (ParamExpressValue) assign.valueL;
                                Object key = pExpress.getKeyValue().value(context);
                                Object target = pExpress.getTarget(context);
                                if(target instanceof Map) {
                                        ((Map)target).put(key, rightObj);
                                } else if(target instanceof List) {
                                        List list = ((List) target);
                                        Integer iKey = null;
                            			if (key instanceof BigDecimal) {
                            				iKey = ((BigDecimal)key).intValue();
                            			} else {
                            				iKey = (Integer)key;
                            			}
                                        if (iKey==list.size()) {
                                                list.add(rightObj);
                                        } else if(list.size() > iKey) {
                                                list.set(iKey, rightObj);
                                        } else {
                                                throw new GlueException("请按顺序设置数组的值");
                                        }
                                        
                                }
                        }
                },
                //给实例属性赋值
                Exec{
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        @Override
                        public void assign(Assign assign, JGlueContext context) {
                                Object rightObj = assign.valueR.value(context);
                                Object target = ((Exec)assign.valueL).getLeft().value(context);
                                if (target instanceof Map) {
                                	((Map)target).put(((Exec)assign.valueL).getRight().getSource(), rightObj);
                                } else {
                                	ReflectUtils.setFieldValue(target, ((Exec)assign.valueL).getRight().getSource(), rightObj);
                                }
                        }
                },
                ArraySet {
					@SuppressWarnings("unchecked")
					@Override
					public void assign(Assign assign, JGlueContext context) {
						Object rightObj = assign.valueR.value(context);
						Object leftObj = ((ArrayGet)assign.valueL).valueL.value(context);
						Object key = ((ArrayGet)assign.valueL).valueR.value(context);
						if(leftObj instanceof Map) {
							Map<Object, Object> target = (Map<Object, Object>)leftObj;
							target.put(key, rightObj);
						} else if(leftObj instanceof List) {
							List<Object> target = (List<Object>)leftObj;
							Integer iKey = null;
	            			if (key instanceof BigDecimal) {
	            				iKey = ((BigDecimal)key).intValue();
	            			} else {
	            				iKey = (Integer)key;
	            			}
	            			if (iKey==target.size()) {
	            				target.add(rightObj);
		                    } else if(target.size() > iKey) {
		                    	target.set(iKey, rightObj);
		                    } else {
		                        throw new GlueException("请按顺序设置数组的值");
		                    }
						} else {
							//通过反射给对象属性设置
						}
					}
                };
                public abstract void assign(Assign assign, JGlueContext context);
        }
}
