package com.xw.glue.value;

import java.util.List;
import java.util.Map;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.lexical.word.Word;

public class MapValue extends Value<Map<String, Object>>{

	public MapValue(List<Word> words) {
		super(words);
	}

	@Override
	public Map<String, Object> value(JGlueContext context) {
		return null;
	}
}
