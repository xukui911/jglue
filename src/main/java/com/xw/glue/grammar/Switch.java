package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.grammar.exec.GrammarElement;
import com.xw.glue.grammar.exec.JGlueStack;
import com.xw.glue.lexical.word.KeyWords;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.utils.ExpressUtils;
import com.xw.glue.utils.ParamUtils;
import com.xw.glue.value.ParamValue;
import com.xw.glue.value.Value.ValueType;

/**
 * 
 * @ClassName:  Switch   
 * @Description:   
 * @author: XuKui
 * @date:   2021年5月7日 上午10:17:35      
 * @Copyright:
 */
public class Switch extends Grammar {
	Map<Object, Case> cases = new HashMap<Object, Case>();
	ParamValue paramValue;
	@Override
	public void parse(Stack<Word> stack) {
		stack.pop();
		Word first = stack.pop();
		if (first.toString().equals("(")) {
			parseParam(stack);
			//解析循环体
			parseContent(stack, "{", "}");
		} else {
			throw new GlueException("错误的switch格式，switch后面必须是(your paramCode)");
		}
	}
	
	private void parseParam(Stack<Word> stack) {
		String paramCode = stack.pop().toString();
		paramValue = (ParamValue)parent.getParamValue(paramCode);
		if(paramValue==null) {
			paramValue = ParamUtils.createParamValue(paramCode, properties);
			parent.addGlobleParamValue(paramValue);
		}
		if(!")".equals(stack.pop().toString())) {
			throw new GlueException("错误的switch格式，switch后面必须是(your paramCode)");
		}
	}

	/**
	 *  @desc 重写父类方法解析case体
	 */
	public void parseWord(Word curWord, Stack<Word> stack) {
		JGlueStack.push(new GrammarElement(this));
		int count = 0;
		stack.push(curWord);
		List<Case> emptyCases = new ArrayList<Case>();
		while(!stack.isEmpty() && count > -1) {
			Word nextWord = stack.pop();
			String next = nextWord.toString();
			if(next.equals("}")) {
				stack.push(nextWord);
				break;
			}
			if(KeyWords.CASE.getKeyWord().equals(next)) {
				Case nCase = new Case(stack.pop().toString());
				nCase.setParent(this);
				nCase.parse(stack);
				if(nCase.grammars==null||nCase.grammars.size()==0) {
					emptyCases.add(nCase);
				} else {
					if(emptyCases.size()>0) {
						emptyCases.forEach(emptyCase -> {
							emptyCase.grammars = nCase.grammars;
						});
						emptyCases.clear();
					}
				}
			} else if(KeyWords.DEFAULT.getKeyWord().equals(next)) {
				Case nCase = new Case("'default'");
				nCase.setParent(this);
				nCase.parse(stack);
			}
		}
		JGlueStack.pop();
	}
	
	/**
	 *  @description 执行体
	 */
	public Result execSelf(JGlueContext context) {
		Case eCase = this.cases.get(paramValue.value(context));
		if(eCase!=null) {
			Result caseResult = eCase.exec(context);
			if(caseResult!=null) {
				if(caseResult.getType()==ValueType.BREAK) {
					return null;
				}
				return caseResult;
			}
		}
		Case defCase = this.cases.get("default");
		if(defCase!=null) {
			Result defResult = defCase.exec(context);
			if(defResult!=null && defResult.getType()==ValueType.BREAK) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @ClassName:  Case   
	 * @Description:   
	 * @author: XuKui
	 * @date:   2021年5月7日 下午12:52:42      
	 * @Copyright:
	 */
	public class Case extends Grammar {
		Object caseValue;
		public Case(String paramValue) {
			this.caseValue = ExpressUtils.str2ConstSimple(paramValue);
		}
		@Override
		public void parse(Stack<Word> stack) {
			if(!stack.pop().toString().equals(":")) {
				throw new GlueException("your must use like this [case paramValue :] or [default :]");
			}
			Word next = stack.pop();
			if("{".equals(next.toString())) {
				stack.push(next);
				parseContent(stack, "{", "}");
			} else {
				stack.push(next);
				int count = 0;
				while(!stack.isEmpty()) {
					next = stack.pop();
					if(next.toString().equals("{")) {
						count ++;
					}
					if(next.toString().equals("}")) {
						count --;
						if(count==-1) {
							stack.push(next);
							break;
						}
					}
					if(next.toString().equals(KeyWords.CASE.getKeyWord())
							||next.toString().equals(KeyWords.DEFAULT.getKeyWord())) {
						stack.push(next);
						break;
					} else {
						parseWord(next, stack);
					}
				}
			}
			cases.put(caseValue, this);
		}		
	}
}
