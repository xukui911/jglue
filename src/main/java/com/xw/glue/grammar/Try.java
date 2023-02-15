package com.xw.glue.grammar;

import java.util.Stack;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.context.JValue;
import com.xw.glue.exception.GlueException;
import com.xw.glue.lexical.word.Word;

public class Try extends Grammar {
	Catch catchGrammer;
	Finally finallyGrammer;
	@Override
	public void parse(Stack<Word> stack) {
		Word tryWord = stack.pop();
		//解析循环体
		parseContent(stack, "{", "}");
		Word catchWord = stack.peek();
		if(catchWord.toString().equals("catch")) {
			catchGrammer = (Catch)keyWordGrammer(stack.pop(), stack, this.getNamespace());
			Word next = stack.peek();
			if(next.toString().equals("finally")) {
				finallyGrammer = (Finally)keyWordGrammer(stack.pop(), stack, this.getNamespace());
			}
		} else if(catchWord.toString().equals("finally")) {
			finallyGrammer = (Finally)keyWordGrammer(stack.pop(), stack, this.getNamespace());
		}
		if(catchGrammer==null && finallyGrammer==null) {
			throw new GlueException("try after must have catch or finally (line:" + tryWord.getLine()+")");
		}
	}
	
	public Result execSelf(JGlueContext context) {
		try {
			return super.execSelf(context);
		}catch(Exception ex) {
			if(catchGrammer!=null) {
				catchGrammer.setEx(ex);
				return catchGrammer.exec(context);
			}
			throw ex;
		}finally {
			if(finallyGrammer!=null) {
				finallyGrammer.exec(context);
			}
		}
	}
	
	public static class Catch extends Grammar {
		int count;
		String exCode;
		Exception ex;
		int line;
		@Override
		public void parse(Stack<Word> stack) {
			Word first = stack.pop();
			line = first.getLine();
			if (first.toString().equals("catch")) {
				//解析循环体
				parseExceptionParam(stack);
			}
			parseContent(stack, "{", "}");
		}
		
		private void parseExceptionParam(Stack<Word> stack) {
			exCode = "";
			while(!stack.isEmpty()) {
				Word next = stack.pop();
				if(next.toString().equals("(")) {
					count++;
					continue;
				}
				if(next.toString().equals(")")) {
					count--;
				}
				
				if(count > 0) {
					exCode += next.toString();
				} else {
					break;
				}
			}
		}
		
		public Result execSelf(JGlueContext context) {
			context.addJValue(new JValue(exCode, ex));
			return super.execSelf(context);
		}

		public Exception getEx() {
			return ex;
		}

		public void setEx(Exception ex) {
			this.ex = ex;
		}

		public String getExCode() {
			return exCode;
		}
		
		public void parseAfter() {
			throw new GlueException("catch must after try (line:" + line+")");
		}
	}
	
	public static class Throw extends Grammar {
		String ex;
		int startLine;
		Catch catchGrammer;
		@Override
		public void parse(Stack<Word> stack) {
			Word throwWord = stack.pop();
			startLine = throwWord.getLine();
			ex = stack.pop().toString();
			if(!stack.pop().toString().equals(";")) {
				throw new GlueException("is wrong throw grammer(line:" + throwWord.getLine()+")");
			}
		}
		
		public void parseAfter() {
			if(!(getParent() instanceof Catch)) {
				throw new GlueException("keywords throw must in catch(line:" + startLine+")");
			}
			catchGrammer = (Catch)getParent();
		}
		
		public Result execSelf(JGlueContext context) {
			if(!ex.equals(catchGrammer.getExCode())) {
				throw new GlueException(ex + " is not defined!!!--(line:"+startLine+")");
			}
			throw new GlueException(catchGrammer.getEx());
		}
	}
	
	public static class Finally extends Grammar {

		@Override
		public void parse(Stack<Word> stack) {
			stack.pop();
			parseContent(stack, "{", "}");
		}
		
	}

}
