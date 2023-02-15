package com.xw.glue.grammar.parse;

import com.xw.glue.grammar.Fragment;
import com.xw.glue.grammar.Grammars;

public interface GrammarParse {
	
	public void parse(Fragment fragment, Grammars grammars);
}
