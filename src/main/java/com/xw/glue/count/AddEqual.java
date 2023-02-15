package com.xw.glue.count;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.JValue;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.ParamExpressValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.StrValue;
import com.xw.glue.value.Value;

public class AddEqual extends Value<Object> implements Count<Value<?>> {
	Value<?> valueL;
	Value<?> valueR;
	CountType countType;
	public AddEqual(Value<?> value, Value<?> value2) {
		this.valueL = value;
		this.valueR = value2;
		if(value instanceof ParamValue) {
			countType = CountType.Param;
		} else if(value instanceof ParamExpressValue) {
			countType = CountType.ParamExpress; 
		} else if(value instanceof ArrayGet) {
			countType = CountType.ArrayGet; 
		} else if(value instanceof Exec) {
			countType = CountType.Exec; 
		}
	}

	@Override
	public Value<?> count(JGlueContext context) {
		Object obj = value(context);
		if (obj instanceof String) {
			return new StrValue((String)obj);
		} else {
			return new NumberValue((Number)obj);
		}
	}

	@Override
	public Object value(JGlueContext context) {
		return countType.value(this, context);
	}
	
	 public static enum CountType {
		 Param {
			@Override
			public Object value(AddEqual count, JGlueContext context) {
				JValue jvalue = context.getJValue((ParamValue)count.valueL);
				Object lValue = count.valueL.value(context);
				Object rValue = count.valueR.value(context);
				if (lValue instanceof Number && rValue instanceof Number) {
					Number newValue = Add.add((Number)jvalue.getValue(), (Number)rValue);
					jvalue.setValue(newValue);
				} else {
					jvalue.setValue(lValue.toString() + rValue.toString());
				}
				return jvalue.getValue();
			}
		},
		 ParamExpress {
			@Override
			public Object value(AddEqual count, JGlueContext context) {
				 Object rightObj = count.valueR.value(context);
                 ParamExpressValue pExpress = (ParamExpressValue) count.valueL;
                 Object key = pExpress.getKeyValue().value(context);
                 Object target = pExpress.getTarget(context);
                 addValue(target, rightObj, key);
				return null;
			}
		},
			ArrayGet {
				@Override
				public Object value(AddEqual count, JGlueContext context) {
					Object rightObj = count.valueR.value(context);
					Object leftObj = ((ArrayGet) count.valueL).valueL.value(context);
					Object key = ((ArrayGet) count.valueL).valueR.value(context);
					addValue(leftObj, rightObj, key);
					return null;
				}
			},
			Exec {
				@Override
				public Object value(AddEqual count, JGlueContext context) {
					Object rightObj = count.valueR.value(context);
					Object leftObj = ((Exec) count.valueL).getLeft().value(context);
					Object key = ((Exec) count.valueL).getRight().getSource();
					addValue(leftObj, rightObj, key);
					return null;
				}
			};

			public abstract Object value(AddEqual count, JGlueContext context);

			@SuppressWarnings("unchecked")
			public Object addValue(Object leftObj, Object rightObj, Object key) {
				Object newValue = null;
				if (leftObj instanceof Map) {
					Map<Object, Object> target = (Map<Object, Object>) leftObj;
					Object oldVal = target.get(key);
					if (oldVal instanceof Number && rightObj instanceof Number) {
						newValue = Add.add((Number) oldVal, (Number) rightObj);
						target.put(key, newValue);
					} else {
						newValue = oldVal.toString() + rightObj.toString();
						target.put(key, newValue);
					}
				} else if (leftObj instanceof List) {
					List<Object> target = (List<Object>) leftObj;
					Integer iKey = null;
					if (key instanceof BigDecimal) {
						iKey = ((BigDecimal) key).intValue();
					} else {
						iKey = (Integer) key;
					}
					Object oldVal = target.get(iKey);
					if (oldVal instanceof Number && rightObj instanceof Number) {
						newValue = Add.add((Number) oldVal, (Number) rightObj);
						target.set(iKey, newValue);
					} else {
						newValue = oldVal.toString() + rightObj.toString();
						target.set(iKey, newValue);
					}
				} else {
					// 通过反射给对象属性设置
				}
				return newValue;
			}
		}
}
