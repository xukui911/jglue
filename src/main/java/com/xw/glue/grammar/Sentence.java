package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.lexical.word.SimpleWord;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.utils.ParamUtils;
import com.xw.glue.value.ParamValue;

/**
 * @description: 程序句子
 * @author XK
 *
 */
public class Sentence extends Grammar {
	List<Word> command = new ArrayList<Word>();
	SentenceType type;
	Express express;
	Express expressRight;
	List<Object> keys;
	Object lastKey;
	ParamValue varParamValue;
	public void parse(Stack<Word> stack) {
		int count =0;
		while(!stack.isEmpty()) {
			Word word = stack.pop();
			String str = word.toString();
			if(str.equals("(") || str.equals("{")) {
				count++;
			}
			if(str.equals(")") || str.equals("}")) {
				count--;
			}
			if(count == 0 && (str.equals(";"))) {
				break;
			} else {
				command.add(word);
				if (count == 0 && word.isLineLast()) {
					if(!str.equals(".") &&
							(stack.isEmpty() || !stack.peek().toString().equals("."))
							) {
						break;
					}
				}
			}
		}
		parseExpress();
	}
	
	void newInstanceName(Stack<Word> stack, int line) {
		String name = "";
		while(!stack.isEmpty()) {
			Word word = stack.pop();
			String str = word.toString();
			if(str.equals("(")) {
				stack.push(word);
				break;
			} else {
				name += str;
			}
		}
		command.add(new SimpleWord(line, name));
	}
	
	public Sentence() {
		needCreateContext = false;
	}
	
	public void parseExpress() {
		if(command.size()<1) {
			throw new GlueException("wrong command:" + toString(), line);
		}
		String first = command.get(0).toString();
		if(first.equals(SentenceType.Return.getCode())) {
			type = SentenceType.Return;
			command.remove(0);
		} else if(first.equals(SentenceType.Var.getCode())) {
			type = SentenceType.Var;
			paramExec();
		}else if(first.equals(SentenceType.Let.getCode())) {
			type = SentenceType.Let;
			paramExec();
		}else if(first.equals(SentenceType.Set.getCode())) {
			type = SentenceType.Set;
			paramExec();
		}else if(first.equals(SentenceType.CONST.getCode())) {
			type = SentenceType.CONST;
			paramExec();
		} else if(first.equals(SentenceType.New.getCode())) {
			type = SentenceType.New;
		} else {
			type = SentenceType.Execute;
		}
		if(command.size() > 0) {
			express = new JGlueExpress(command, getProperties(), this.parent);
		}
	}
	
	private void paramExec() {
		command.remove(0);
		varParamValue = ParamUtils.createSentenceParamValue(command.get(0).toString(), properties, type);
		varParamValue.setVar(true);
		try {
			parent.addParamValue(varParamValue);
		}catch(GlueException ge) {
			ge.setLine(command.get(0).getLine());
			throw ge;
		}
	}
	
	public void setFuncReturn(Grammar grammar) {
		if(grammar instanceof Func) {
			((Func)grammar).setHasReturn(true);
		} else if(grammar.getParent()!=null){
			setFuncReturn(grammar.getParent());
		}
	}
	@Override
	public Result exec(JGlueContext context) {
		Result result = execSelf(context);
		return result;
	}
	@Override
	public Result execSelf(JGlueContext context) {
		switch(type) {
			case Return: {
				if(express==null) {
					return null;
				}
				Object obj = execExpressComand(context);
				return ExpressUtils.buildResult(obj, null);
			}
			case Let: 
			case Set: 
			case Var: {
				if(command.size()>1) {
					execExpressComand(context);
				}
				break;
			}
			case Execute: execExpressComand(context); break;
			default: {
				execExpressComand(context);
				break;
			}
		}
		return null;
	}
	
	public Object execExpressComand(JGlueContext context) {
		try {
			Object obj = express.execute(context);
			return obj;
		} catch (Exception e) {
			if(e instanceof GlueException) {
				((GlueException) e).append("\nwrong line: 第" + command.get(0).getLine() +"行")
								   .append("\nwrong express: " + express.getExpress());
				throw e;
			}
			throw new GlueException("express: " + toString() + ","
					+ "wrong line: 第" + command.get(0).getLine() +"行",e);
		}
	}
	
	public String toString() {
		String strCommand = "";
		for(Word str: command) {
			if(str.toString().equals("'") && !str.isTrans()) {
				continue;
			}
			strCommand += str.toString() + " ";
		}
		return strCommand.trim();
 	}
	
	public static enum SentenceType {
		Return("return"),
		Set("set"),
		Let("let"),
		Var("var"),
		CONST("const"),
		Execute("exec"),
		New("new");
		
		String code;
		
		SentenceType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}
