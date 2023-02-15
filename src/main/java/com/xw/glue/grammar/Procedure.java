package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.factory.ContextFactory;
import com.xw.glue.func.IFunc;
import com.xw.glue.func.factory.FuncFactory;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.grammar.parse.GrammarParse;
import com.xw.glue.grammar.parse.SimpleGrammerParse;
import com.xw.glue.lexical.LexicalAnalyzer;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.LoadUtils;
import com.xw.glue.value.FuncValue;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value.ValueType;

public class Procedure {
	LexicalAnalyzer analyzer;
	GrammarParse grammerParse;
	List<Fragment> fragments = new ArrayList<Fragment>();
	List<Fragment> incrementFragments = new CopyOnWriteArrayList<Fragment>();
	private JProperties properties;
	private String namespace;
	Grammars grammars;
	private Map<String, Grammars> fileGrammars = new ConcurrentHashMap<String, Grammars>();

	private static Result UNDEFINED_RESULT = new Result(null, ValueType.UNDEFINED);

	public Procedure() {
		this(new JProperties(), null);
	}
	
	public Procedure(String namespace) {
		this(new JProperties(), namespace);
	}
	
	public Procedure(JProperties properties) {
		this(properties, null);
	}
	
	public Procedure(JProperties properties, String namespace) {
		this(null, null, properties, namespace);
	}
	
	public Procedure(LexicalAnalyzer analyzer, GrammarParse grammerParse, String namespace) {
		this(analyzer, grammerParse, new JProperties(), namespace);
	}
	
	public Procedure(LexicalAnalyzer analyzer, GrammarParse grammerParse, JProperties properties, String namespace) {
		this.namespace = namespace;
		this.properties = properties;
		if(analyzer==null) {
			analyzer = new LexicalAnalyzer();
		}
		if(grammerParse==null) {
			grammerParse = new SimpleGrammerParse(this);
		}
		this.analyzer = analyzer;
		this.grammerParse = grammerParse;
		this.grammars = new Grammars(this);
	}
	
	public Procedure parse(String content) {
		incrementFragments.add(analyzer.paser(content));
		return this;
	}
	
	public Fragment parseFile(String filePath) {
		String content = LoadUtils.getContentByPath(filePath);
		Fragment fragment = analyzer.paser(content);
		return fragment;
	}
	
	public Procedure addContent(String content) {
		Fragment fragment = analyzer.paser(content);
		incrementFragments.add(fragment);
		return this;
	}
	
	public Procedure addFileContent(String filePath, String content) {
		Grammars fGrammars = null;
		if (fileGrammars.containsKey(filePath)) {
			fGrammars = fileGrammars.get(filePath);
		} else {
			fGrammars = grammars;
			fileGrammars.put(filePath, fGrammars);
		}
		Fragment fragment = analyzer.paser(content);
		grammerParse.parse(fragment, fGrammars);
		return this;
	}
	
	public Procedure addFile(String filePath) {
		Grammars fGrammars = null;
		if (fileGrammars.containsKey(filePath)) {
			fGrammars = fileGrammars.get(filePath);
		} else {
			fGrammars = grammars;
			fileGrammars.put(filePath, fGrammars);
		}
		Fragment fragment = parseFile(filePath);
		incrementFragments.add(fragment);
		return this;
	}
	
	public Procedure grammer() {
		try {
			synchronized (analyzer) {
				for(Fragment fragment: incrementFragments) {
					grammerParse.parse(fragment, grammars);
					fragments.add(fragment);
				}
				incrementFragments.clear();
			}
			List<FuncValue> funcValues = JGlueStack.getFuncValues();
			if(funcValues!=null) {
				for(FuncValue funcValue: funcValues) {
					funcValue.initFuncAfterParse();
				}
			}
		}finally {
			JGlueStack.clearFuncValue();
			JGlueStack.clear();
		}
		return this;
	}
	
	public Result exec(String funcName, JGlueContext context) {
		if (incrementFragments!=null && incrementFragments.size() > 0) {
			grammer();
		}
		IFunc func = null;
		if (grammars.getSubFunc(funcName)!=null) {
			func = grammars.getSubFunc(funcName).getFunc();
		}
		if(func==null) {
			func = FuncFactory.getInstance().getFunc(funcName);
		}
		if(func!=null) {
			try {
				JGlueContext gContext = ContextFactory.getContext(context, grammars);
				for(ParamValue pv: grammars.getParamValues().values()) {
					gContext.set(pv, context.get(pv.getSource()));
				}				
				JGlueContext funcContext = ContextFactory.getContext(gContext, (Grammar)func);
				if(func.getParams()!=null) {
					for(ParamValue pv: func.getParams()) {
						funcContext.set(pv, context.get(pv.getSource()));
					}
				}
				Result result = func.exec(funcContext);
				if(result == null) {
					result = UNDEFINED_RESULT;
				}
				return result;
			}finally {
				JGlueStack.clear();
			}
		}
		throw new RuntimeException("不存在的方法("+funcName+")！");
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public JProperties getProperties() {
		return properties;
	}

	public void setProperties(JProperties properties) {
		this.properties = properties;
	}

	public Grammars getGrammars() {
		return grammars;
	}

	public void setGrammars(Grammars grammars) {
		this.grammars = grammars;
	}
	
	public GrammarParse getGrammerParse() {
		return this.grammerParse;
	}
	
	public Grammars createFileGrammars(String filepath) {
		if(fileGrammars.containsKey(filepath)) {
			return fileGrammars.get(filepath);
		}
		synchronized (fileGrammars) {
			if(!fileGrammars.containsKey(filepath)) {
				Grammars grammars = new Grammars(this);
				this.fileGrammars.put(filepath, grammars);
			}
			return fileGrammars.get(filepath);
		}
	}
	
	public boolean hasFile(String filePath) {
		return fileGrammars.containsKey(filePath);
	}
}
