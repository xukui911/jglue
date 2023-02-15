package com.xw.glue.utils;

import com.xw.glue.func.IFunc;
import com.xw.glue.grammar.Sentence.SentenceType;
import com.xw.glue.properties.JProperties;
import com.xw.glue.value.ParamFuncValue;
import com.xw.glue.value.ParamValue;

public class ParamUtils {
	public static ParamValue createParamValue(String paramCode, JProperties properties) {
		return new ParamValue(paramCode);
	}
	
	public static ParamValue createSentenceParamValue(String paramCode, JProperties properties, SentenceType sentenceType) {
		return new ParamValue(paramCode);
	}
	
	public static ParamFuncValue createParamFuncValue(IFunc func, JProperties properties) {
		return new ParamFuncValue(func, properties);
	}
	
	public static ParamValue createThisParamValue(String paramCode, JProperties properties) {
		return new ParamValue(paramCode);
	}
}

