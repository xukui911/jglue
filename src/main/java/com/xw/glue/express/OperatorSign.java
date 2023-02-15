package com.xw.glue.express;

import java.util.HashMap;
import java.util.Map;

import com.xw.glue.count.Add;
import com.xw.glue.count.AddDouble;
import com.xw.glue.count.AddEqual;
import com.xw.glue.count.And;
import com.xw.glue.count.ArrayGet;
import com.xw.glue.count.Assign;
import com.xw.glue.count.Count;
import com.xw.glue.count.Derelease;
import com.xw.glue.count.DereleaseDouble;
import com.xw.glue.count.DereleaseEqual;
import com.xw.glue.count.Division;
import com.xw.glue.count.Equals;
import com.xw.glue.count.Exec;
import com.xw.glue.count.Less;
import com.xw.glue.count.LessOrEquals;
import com.xw.glue.count.MethodExec;
import com.xw.glue.count.Mod;
import com.xw.glue.count.More;
import com.xw.glue.count.MoreOrEquals;
import com.xw.glue.count.Multiplication;
import com.xw.glue.count.New;
import com.xw.glue.count.Not;
import com.xw.glue.count.NotEquals;
import com.xw.glue.count.Or;
import com.xw.glue.count.Ternary;
import com.xw.glue.count.Ternary.TernaryChoose;
import com.xw.glue.count.TypeOf;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.BoolValue;
import com.xw.glue.value.NumberValue;
import com.xw.glue.value.ObjValue;
import com.xw.glue.value.StrValue;
import com.xw.glue.value.Value;

public enum OperatorSign {
	// 算术表达式
	ADD("+", 20) {
		@Override
		public CountBuilder<?> getBuilder() {
			return new CountBuilder<Value<?>>() {
				@Override
				public Count<Value<?>> build(JProperties properties) throws Exception {
					return new Add(value, value2);
				}
			};
		}
	} ,
	
	// 算术表达式
		ADD_EQUAL("+=", 5) {
			@Override
			public CountBuilder<?> getBuilder() {
				return new CountBuilder<Value<?>>() {
					@Override
					public Count<Value<?>> build(JProperties properties) throws Exception {
						return new AddEqual(value, value2);
					}
				};
			}
		} ,
	
	// 双目表达式
	ADD_DOUBLE("++", 25) {
		@Override
		public CountBuilder<?> getBuilder() {
			CountBuilder<?> builder = new CountBuilder<NumberValue>() {
				@Override
				public Count<NumberValue> build(JProperties properties) throws Exception {
					return new AddDouble(value);
				}
			};
			return builder.valueLen(1);
		}
	} ,
	
	DERELEASE("-", 20){
		@Override
		public CountBuilder<?> getBuilder() {
			return new CountBuilder<NumberValue>() {
				@Override
				public Count<NumberValue> build(JProperties properties) throws Exception {
					return new Derelease(value, value2);
				}
			};
		}
	},
	DERELEASE_EQUAL("-=", 5){
		@Override
		public CountBuilder<?> getBuilder() {
			return new CountBuilder<NumberValue>() {
				@Override
				public Count<NumberValue> build(JProperties properties) throws Exception {
					return new DereleaseEqual(value, value2);
				}
			};
		}
	},
	DERELEASE_DOUBLE("--", 25){
		@Override
		public CountBuilder<?> getBuilder() {
			CountBuilder<?> builder = new CountBuilder<NumberValue>() {
				@Override
				public Count<NumberValue> build(JProperties properties) throws Exception {
					return new DereleaseDouble(value);
				}
			};
			return builder.valueLen(1);
		}
	},
	
	MULTIPLICATION ("*", 21) {

		@Override
		public CountBuilder<?> getBuilder() {
			return new CountBuilder<NumberValue>() {
				@Override
				public Count<NumberValue> build(JProperties properties) throws Exception {
					return new Multiplication(value, value2);
				}
			};
		}
		
	},
	
	DIVISION("/", 21) {
		@Override
		public CountBuilder<?> getBuilder() {
			return new CountBuilder<NumberValue>() {
				@Override
				public Count<NumberValue> build(JProperties properties) throws Exception {
					return new Division(value, value2, properties.getHighPrecision());
				}
			};
		}
	},
	MOD("%", 21) {
		@Override
		public CountBuilder<?> getBuilder() {
			return new CountBuilder<NumberValue>() {
				@Override
				public Count<NumberValue> build(JProperties properties) throws Exception {
					return new Mod(value, value2);
				}
			};
		}
	},
	
	
	//逻辑表达式
	OR_SINGE("|", 10) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new Or(value, value2);
				}
			};
		}
	},
	
	AND_SING("&", 10) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new And(value, value2);
				}
			};
		}
	},
	OR("||", 10) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new Or(value, value2, true);
				}
			};
		}
	},
	
	AND("&&", 10) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new And(value, value2, true);
				}
			};
		}
	},
	
	NOT("!", 30) {
		@Override
		public CountBuilder<?> getBuilder() {
			CountBuilder<BoolValue> builder =  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new Not(value);
				}
			};
			return builder.valueLen(1);
		}
	},
	
	EQUALS("==", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new Equals(value, value2);
				}
			};
		}
	},
	
	EQUALS_ALLWAYS("===", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new Equals(value, value2);
				}
			};
		}
	},
	
	NOT_EQUALS("!=", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new NotEquals(value, value2);
				}
			};
		}
	},
	NOT_EQUALS_ALLWAYS("!==", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new NotEquals(value, value2);
				}
			};
		}
	},
	
	MORE(">", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@SuppressWarnings("unchecked")
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new More((Value<Number>)value, (Value<Number>)value2);
				}
			};
		}
	},
	LESS("<", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new Less(value, value2);
				}
			};
		}
	},
	MORE_OR_EQUAL(">=", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new MoreOrEquals(value, value2);
				}
			};
		}
	},
	LESS_OR_EQUAL("<=", 15) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<BoolValue>() {
				@Override
				public Count<BoolValue> build(JProperties properties) throws Exception {
					return new LessOrEquals(value, value2);
				}
			};
		}
	},
	//双目运算符 a > b ? "1" : "2"
	TERNARY_CHOOSE(":", 7) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<Value<?>>() {
				@Override
				public Count<Value<?>> build(JProperties properties) throws Exception {
					return new TernaryChoose(value, value2);
				}
			};
		}
	},
	//双目运算符 a > b ? "1" : "2"
	TERNARY("?", 6, TERNARY_CHOOSE) {
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<Value<?>>() {
				@Override
				public Count<Value<?>> build(JProperties properties) throws Exception {
					return new Ternary(value, value2);
				}
			};
		}
	},
	ASSIGN("=", 5) { //赋值操作
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<ObjValue>() {
				@Override
				public Count<ObjValue> build(JProperties properties) throws Exception {
					return new Assign(value, value2, properties);
				}
			};
		}
	},
	EXEC(".", 100) { //取值操作
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<ObjValue>() {
				@Override
				public Count<ObjValue> build(JProperties properties) throws Exception {
					return new Exec(value, value2);
				}
			};
		}
	},
	
	NEW("new", 100) { //实例化操作
		@Override
		public CountBuilder<?> getBuilder() {
			CountBuilder<ObjValue> builder =  new CountBuilder<ObjValue>() {
				@Override
				public Count<ObjValue> build(JProperties properties) throws Exception {
					return new New(value, parentGrammar);
				}
			};
			return builder.valueLen(1);
		}
	},
	
	TYPEOF("typeof", 16) { //对象类型
		@Override
		public CountBuilder<?> getBuilder() {
			CountBuilder<StrValue> builder =  new CountBuilder<StrValue>() {
				@Override
				public Count<StrValue> build(JProperties properties) throws Exception {
					return new TypeOf(value);
				}
			};
			return builder.valueLen(1);
		}
	},
	ARRAY_SUFFIX ("[", 100) { //数组下标
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<ObjValue>() {
				@Override
				public Count<ObjValue> build(JProperties properties) throws Exception {
					return new ArrayGet(value, value2);
				}
			};
		}
	},
	FUNC_EXE ("(", 100) { //方法执行
		@Override
		public CountBuilder<?> getBuilder() {
			return  new CountBuilder<ObjValue>() {
				@Override
				public Count<ObjValue> build(JProperties properties) throws Exception {
					return new MethodExec(value, value2);
				}
			};
		}
	}
	
	/**,
	DIVISION("/", 10),
	REMAINDER("%", 10),
	
	//逻辑表达式
	OR_SINGE("|", 20),
	AND_SING("&", 20),
	OR("||", 20),
	AND("&&", 20),
	NOT("!", 20)
	**/
	;
	String sign;
	int priority;
	OperatorSign endSign;
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	<R> OperatorSign(String sign, int priority) {
		this.sign = sign;
		this.priority = priority;
	}
	
	<R> OperatorSign(String sign, int priority, OperatorSign endSign) {
		this.sign = sign;
		this.priority = priority;
		this.endSign = endSign;
	}
	
	public abstract CountBuilder<?> getBuilder() throws Exception;
	
	private static Map<String, OperatorSign> map = new HashMap<String, OperatorSign>();
	static {
		OperatorSign[] operatorSigns= OperatorSign.values();
		if(operatorSigns!=null) {
			for(OperatorSign sign: operatorSigns) {
				map.put(sign.getSign(), sign);
			}
		}
	}
	
	public static OperatorSign getOperatorSign(String word) {
		return map.get(word);
	}
	public static OperatorSign getOperatorSign(char ch, int index, char[] chars) {
		OperatorSign operatorSign =  map.get(String.valueOf(ch));
		if(operatorSign!=null) {
			if(ch == chars[index+1]) {
				if(ch=='&') {
					operatorSign = OperatorSign.AND;
				}
				if(ch=='|') {
					operatorSign = OperatorSign.OR;
				}
				if(ch=='+') {
					operatorSign = OperatorSign.ADD_DOUBLE;
				}
				if(ch=='-') {
					operatorSign = OperatorSign.DERELEASE_DOUBLE;
				}
				if(operatorSign==ASSIGN) {
					operatorSign = EQUALS;
				}
			} else if (operatorSign == MORE && '=' == chars[index+1]) {
				operatorSign = MORE_OR_EQUAL;
			} else if (operatorSign == LESS && '=' == chars[index+1]) {
				operatorSign = LESS_OR_EQUAL;
			} else if (operatorSign == NOT && '=' == chars[index+1]) {
				operatorSign = NOT_EQUALS;
			}
		}
		return operatorSign;
	}
	
	public static class SignResult {
		Value<?> value;
		OperatorSign sign;
		int lastIndex;
		public Value<?> getValue() {
			return value;
		}
		public void setValue(Value<?> value) {
			this.value = value;
		}
		public OperatorSign getSign() {
			return sign;
		}
		public void setSign(OperatorSign sign) {
			this.sign = sign;
		}
		public int getLastIndex() {
			return lastIndex;
		}
		public void setLastIndex(int lastIndex) {
			this.lastIndex = lastIndex;
		}
	}
}
