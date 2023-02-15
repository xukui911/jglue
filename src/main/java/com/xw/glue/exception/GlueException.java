package com.xw.glue.exception;

public class GlueException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StringBuilder appends;
	int line;
	
	public GlueException() {
		super();
	}
	
	public GlueException(String message) {
		super(message);
	}
	
	public GlueException(Throwable ex) {
		super(ex);
	}
	
	public GlueException(Throwable ex, int line) {
		super(ex);
		this.line = line;
	}
	
	public GlueException(String message, int line) {
		super(message);
		this.line = line;
	}
	
	public GlueException(String message, Throwable ex) {
		super(message, ex);
	}

	public GlueException append(String message) {
		if(appends==null) {
			appends = new StringBuilder();
		}
		appends.append(message);
		return this;
	}
	
	public String getMessage() {
		String message = super.getMessage();
		if(appends!=null) {
			message += appends.toString();
		}
		if(line > 0) {
			message += ("(line: "+line+")");
		}
		return message;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
}
