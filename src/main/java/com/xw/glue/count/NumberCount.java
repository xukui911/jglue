package com.xw.glue.count;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.xw.glue.exception.GlueException;
import com.xw.glue.properties.HighPrecision;

public class NumberCount {
	private static Map<Class<?>, NumberTypeEnum> numberTypeMap = new HashMap<Class<?>, NumberTypeEnum>();
	static {
		numberTypeMap.put(Integer.class, NumberTypeEnum.INT);
		numberTypeMap.put(int.class, NumberTypeEnum.INT);
		numberTypeMap.put(Long.class, NumberTypeEnum.LONG);
		numberTypeMap.put(long.class, NumberTypeEnum.LONG);
		numberTypeMap.put(Double.class, NumberTypeEnum.DOUBLE);
		numberTypeMap.put(double.class, NumberTypeEnum.DOUBLE);
		numberTypeMap.put(Float.class, NumberTypeEnum.FLOAT);
		numberTypeMap.put(float.class, NumberTypeEnum.FLOAT);
		numberTypeMap.put(Short.class, NumberTypeEnum.SHORT);
		numberTypeMap.put(short.class, NumberTypeEnum.SHORT);
		numberTypeMap.put(BigDecimal.class, NumberTypeEnum.BIG_DECIMAL);
		numberTypeMap.put(Byte.class, NumberTypeEnum.BYTE);
		numberTypeMap.put(byte.class, NumberTypeEnum.BYTE);
	}
	public static NumberTypeEnum getNumberTypeEnum(Class<?> clazz) {
		if(clazz == Integer.class || clazz == int.class) 
			return NumberTypeEnum.INT;
		if(clazz == Long.class || clazz == long.class) 
			return NumberTypeEnum.LONG;
		if(clazz == Double.class || clazz == double.class) 
			return NumberTypeEnum.DOUBLE;
		if(clazz == Float.class || clazz == float.class) 
			return NumberTypeEnum.FLOAT;
		if(clazz == Short.class || clazz == short.class) 
			return NumberTypeEnum.SHORT;
		if(clazz == Byte.class || clazz == byte.class)  
			return NumberTypeEnum.BYTE;
		if(clazz == BigDecimal.class) 
			return NumberTypeEnum.BIG_DECIMAL;
		throw new GlueException("type " + clazz +" is not number");
	}
	
	public enum NumberTypeEnum {
		BYTE(1) {
			@Override
			public Number add(Number number1, Number number2) {
				return number1.byteValue() + number2.byteValue();
			}
			@Override
			public Number derelease(Number number1, Number number2) {
				return number1.byteValue() - number2.byteValue();
			}
			@Override
			public Number multiply(Number number1, Number number2) {
				return number1.byteValue() * number2.byteValue();
			}
			@Override
			public Number division(Number number1, Number number2) {
				return number1.byteValue() / number2.byteValue();
			}
			@Override
			public Number mod(Number number1, Number number2) {
				return number1.byteValue() % number2.byteValue();
			}
		},
		SHORT(2) {
			@Override
			public Number add(Number number1, Number number2) {
				return number1.shortValue() + number2.shortValue();
			}
			@Override
			public Number derelease(Number number1, Number number2) {
				return number1.shortValue() - number2.shortValue();
			}
			@Override
			public Number multiply(Number number1, Number number2) {
				return number1.shortValue() * number2.shortValue();
			}
			@Override
			public Number division(Number number1, Number number2) {
				return number1.shortValue() / number2.shortValue();
			}
			@Override
			public Number mod(Number number1, Number number2) {
				return number1.shortValue() % number2.shortValue();
			}
		},
		INT(3) {
			@Override
			public Number add(Number number1, Number number2) {
				return number1.intValue() + number2.intValue();
			}
			@Override
			public Number derelease(Number number1, Number number2) {
				return number1.intValue() - number2.intValue();
			}
			@Override
			public Number multiply(Number number1, Number number2) {
				return number1.intValue() * number2.intValue();
			}
			@Override
			public Number division(Number number1, Number number2) {
				return number1.intValue() / number2.intValue();
			}
			@Override
			public Number mod(Number number1, Number number2) {
				return number1.intValue() % number2.intValue();
			}
		},
		LONG(4) {
			@Override
			public Number add(Number number1, Number number2) {
				return number1.longValue() + number2.longValue();
			}
			@Override
			public Number derelease(Number number1, Number number2) {
				return number1.longValue() - number2.longValue();
			}
			@Override
			public Number multiply(Number number1, Number number2) {
				return number1.longValue() * number2.longValue();
			}
			@Override
			public Number division(Number number1, Number number2) {
				return number1.longValue() / number2.longValue();
			}
			@Override
			public Number mod(Number number1, Number number2) {
				return number1.longValue() % number2.longValue();
			}
		},
		FLOAT(5) {
			@Override
			public Number add(Number number1, Number number2) {
				return number1.floatValue() + number2.floatValue();
			}
			@Override
			public Number derelease(Number number1, Number number2) {
				return number1.floatValue() - number2.floatValue();
			}
			@Override
			public Number multiply(Number number1, Number number2) {
				return number1.floatValue() * number2.floatValue();
			}
			@Override
			public Number division(Number number1, Number number2) {
				return number1.floatValue() / number2.floatValue();
			}
			@Override
			public Number mod(Number number1, Number number2) {
				return number1.floatValue() % number2.floatValue();
			}
		},
		DOUBLE(6) {
			@Override
			public Number add(Number number1, Number number2) {
				return number1.doubleValue() + number2.doubleValue();
			}
			@Override
			public Number derelease(Number number1, Number number2) {
				return number1.doubleValue() - number2.doubleValue();
			}
			@Override
			public Number multiply(Number number1, Number number2) {
				return number1.doubleValue() * number2.doubleValue();
			}
			@Override
			public Number division(Number number1, Number number2) {
				return number1.doubleValue() / number2.doubleValue();
			}
			@Override
			public Number mod(Number number1, Number number2) {
				return number1.doubleValue() % number2.doubleValue();
			}
		},
		BIG_DECIMAL(7) {
			@Override
			public Number add(Number number1, Number number2) {
				return  new BigDecimal(number1.toString()).add(new BigDecimal(number2.toString()));
			}
			@Override
			public Number derelease(Number number1, Number number2) {
				return  new BigDecimal(number1.toString()).subtract(new BigDecimal(number2.toString()));
			}
			@Override
			public Number multiply(Number number1, Number number2) {
				return new BigDecimal(number1.toString()).multiply(new BigDecimal(number2.toString()));
			}
			@Override
			public Number division(Number number1, Number number2) {
				return division(number1, number2, new HighPrecision());
			}
			@Override
			public Number mod(Number number1, Number number2) {
				return new BigDecimal(number1.toString()).divideAndRemainder(new BigDecimal(number2.toString()))[1];
			}

		};
		private int sort;
		NumberTypeEnum(int sort) {
			this.sort = sort;
		}
		public abstract Number add(Number number1, Number number2);
		public abstract Number derelease(Number number1, Number number2);
		public abstract Number multiply(Number number1, Number number2);
		public abstract Number division(Number number1, Number number2);
		public abstract Number mod(Number number1, Number number2);
		
		public Number division(Number number1, Number number2, HighPrecision highPrecision) {
			return new BigDecimal(number1.toString()).divide(
					new BigDecimal(number2.toString()),
					highPrecision.getScale(),
					highPrecision.getRoundingMode());
		}
		
		public int getSort() {
			return sort;
		}
		public void setSort(int sort) {
			this.sort = sort;
		}
	}
}
