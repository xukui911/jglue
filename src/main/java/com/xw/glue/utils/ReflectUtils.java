package com.xw.glue.utils;

import java.lang.reflect.Field;

public class ReflectUtils {
	/**
	 * 反射获取对象属性值
	 * @Title: getFieldValue   
	 * @Description: 
	 * @param: @param obj
	 * @param: @param fieldName
	 * @param: @return      
	 * @return: Object      
	 * @throws
	 */
	public static Object getFieldValue(Object obj, String fieldName) {
		try {
			Field field = obj.getClass().getField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * 反射设置属性值
	 * @Title: setFieldValue   
	 * @Description: 
	 * @param: @param obj
	 * @param: @param fieldName
	 * @param: @param value      
	 * @return: void      
	 * @throws
	 */
	public static void setFieldValue(Object obj, String fieldName, Object value) {
		try {
			Field field = obj.getClass().getField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			
		}
	}
}
