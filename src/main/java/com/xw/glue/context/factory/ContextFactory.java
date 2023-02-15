package com.xw.glue.context.factory;

import com.xw.glue.context.CopyGlobeContext;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.ReadGlobeContext;
import com.xw.glue.context.ReadGlobeGrammarContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.grammar.Grammar;

public class ContextFactory {
	public static JGlueContext getContext(JGlueContext globeContext) {
		return getContext(globeContext, null);
	}
	
	public static JGlueContext getContext(JGlueContext globeContext, Grammar grammer) {
		return JGlueContextBuilder.READ_GRAMMAR_GLOBE.getContext(globeContext, grammer);
	}
	
	public static JGlueContext getContext(String type, JGlueContext globeContext, Grammar grammer) {
		return JGlueContextBuilder.valueOf(type).getContext(globeContext, grammer);
	}
	
	public static enum JGlueContextBuilder {
		 COPY_GLOBE {
			@Override
			public JGlueContext getContext(JGlueContext globeContext, Grammar grammer) {
				return new CopyGlobeContext(globeContext);
			}
		},
		 READ_GLOBE {
			@Override
			public JGlueContext getContext(JGlueContext globeContext, Grammar grammar) {
				return new ReadGlobeContext(globeContext);
			}
		},
		 READ_GRAMMAR_GLOBE {
			@Override
			public JGlueContext getContext(JGlueContext globeContext, Grammar grammar) {
				JGlueContext runContext = null;
				if(grammar==null) {
					throw new GlueException("grammar is null, can't use this context!!!");
				} else {
					if(grammar.getOffset() > 0) {
						runContext =  new ReadGlobeGrammarContext(globeContext, grammar);
					} else {
						runContext =  globeContext;
					}
				}
				return runContext;
			}
		};
		public abstract JGlueContext getContext(JGlueContext globeContext, Grammar grammer);
	}
}
