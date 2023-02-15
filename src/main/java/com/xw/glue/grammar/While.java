package com.xw.glue.grammar;

import java.util.Stack;
import com.xw.glue.context.JGlueContext;
import com.xw.glue.exception.GlueException;
import com.xw.glue.express.Express;
import com.xw.glue.express.JGlueExpress;
import com.xw.glue.lexical.word.Word;
import com.xw.glue.value.Value.ValueType;

public class While extends Grammar {
	int count = 0;
	String judge;
	Express judgeExpress;
	@Override
	public void parse(Stack<Word> stack) {
		stack.pop();
		Word first = stack.pop();
		if (first.toString().equals("(")) {
			parseCondition(stack);
			//解析循环体
			parseContent(stack, "{", "}");
		} else {
			throw new GlueException("错误的while格式，while后面必须是(condition)");
		}
	}
	
	private void parseCondition(Stack<Word> stack) {
		count ++;
		judge = "";
		setLine(stack.peek().getLine());
		while(!stack.isEmpty()) {
			Word next = stack.pop();
			if(next.toString().equals("(")) {
				count++;
			}
			if(next.toString().equals(")")) {
				count--;
			}
			if(count > 0) {
				judge += next.toString();
			} else {
				break;
			}
		}
		judgeExpress = new JGlueExpress(judge, getProperties(), getLine(), this);
	}
	
	public Result execSelf(JGlueContext context) {
		try {
			Result result = null;
			for(;(Boolean)judgeExpress.execute(context);) {
				result = super.execSelf(context);
				if(result!=null) {
					if(result.getType()==ValueType.CONTINUE) {
						continue;
					} else {
						break;
					}
				}
			}
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new GlueException(t);
		}
	}
	
	public String toString() {
		return "while("+judgeExpress.getExpress()+") {...}";
	}
}
