package com.xw.glue.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.xw.glue.exception.GlueException;
import com.xw.glue.lexical.word.Word;

/**   
 * @ClassName:  Import   
 * @Description:   
 * @author: XuKui
 * @date:   2022年8月12日 下午1:47:08      
 * @Copyright:  
 */
public class Import extends Grammar {
	List<Word> command = new ArrayList<Word>();
	String filePath = "";
	String libName = "";
	@Override
	public void parse(Stack<Word> stack) {
		stack.pop();
		boolean isLibName = false;
		while(!stack.isEmpty()) {
			Word word = stack.pop();
			String str = word.toString();
			if(str.equals(";")) {
				break;
			} else {
				command.add(word);
				if(word.toString().equals("as")) {
					isLibName = true;
					continue;
				}
				if(isLibName) {
					libName = word.toString();
				} else {
					filePath += word.toString();
				}
			}
		}
		
		if(parent instanceof Grammars) {
			((Grammars)parent).addImport(this);
		} else {
			throw new GlueException("import must at first level", line);
		}
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getLibName() {
		return libName;
	}
	public void setLibName(String libName) {
		this.libName = libName;
	}
}
