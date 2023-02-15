package com.xw.glue.lexical.word;

import java.util.Stack;

import com.xw.glue.grammar.Break;
import com.xw.glue.grammar.Condition;
import com.xw.glue.grammar.Continue;
import com.xw.glue.grammar.For;
import com.xw.glue.grammar.Func;
import com.xw.glue.grammar.Grammar;
import com.xw.glue.grammar.Import;
import com.xw.glue.grammar.Sentence;
import com.xw.glue.grammar.Switch;
import com.xw.glue.grammar.Try;
import com.xw.glue.grammar.Try.Catch;
import com.xw.glue.grammar.Try.Finally;
import com.xw.glue.grammar.Try.Throw;
import com.xw.glue.grammar.While;
import com.xw.glue.properties.JProperties;
import com.xw.glue.utils.FuncUtils;

public enum KeyWords {
  FUNC("func", new String[] {"func", "function"}) {
    @Override
    public Grammar createGramar() {
      Func func = new Func("");
      func.setOffset(FuncUtils.getNextIndex());
      //func.setAnonymous(true);
      return func;
    }
  },
  FUNCTION("function", new String[] {"func", "function"}) {
	    @Override
	    public Grammar createGramar() {
	      return FUNC.createGramar();
	    }
  },
  IF("if") {
    @Override
    public Grammar createGramar() {
      return  new Condition();
    }
  },
  ELSE("else") {
    @Override
    public Grammar createGramar() {
      return new Sentence();
    }
  },
  VAR("var") {
    @Override
    public Grammar createGramar() {
      return new Sentence();
    }
  },
  NEW("new") {
	    @Override
	    public Grammar createGramar() {
	      return new Sentence();
	    }
 },
  WHILE("while") {
    @Override
    public Grammar createGramar() {
      return new While();
    }
  },
  FOR("for") {
    @Override
    public Grammar createGramar() {
      return new For();
    }
  },
  CONTINUE("continue") {
    @Override
    public Grammar createGramar() {
      return new Continue();
    }
  },
  BREAK("break") {
    @Override
    public Grammar createGramar() {
      return new Break();
    }
  },
  SWITCH("switch") {
    @Override
    public Grammar createGramar() {
      return new Switch();
    }
  },
  IMPORT("import") {
    @Override
    public Grammar createGramar() {
      return new Import();
    }
  },
  TRY("try") {
    @Override
    public Grammar createGramar() {
      return new Try();
    }
  },
  CATCH("catch") {
    @Override
    public Grammar createGramar() {
      return new Catch();
    }
  },
  FINALLY("finally") {
    @Override
    public Grammar createGramar() {
      return new Finally();
    }
  },
  THROW("throw") {

    @Override
    public Grammar createGramar() {
      return new Throw();
    }
  },
  RETURN("return") {
    @Override
    public Grammar createGramar() {
      return new Sentence();
    }
  },
  CASE("case") {
    @Override
    public Grammar createGramar() {
      return new Sentence();
    }
  },
  DEFAULT("default") {
    @Override
    public Grammar createGramar() {
      return new Sentence();
    }
  };
  
  private String keyWord;
  private String[] alias;
  
  public String getKeyWord() {
    return keyWord;
  }

  public void setKeyWord(String keyWord) {
    this.keyWord = keyWord;
  }

  public String[] getAlias() {
	if(alias==null) {
		alias = new String[] {keyWord};
	}
	return alias;
  }

  public void setAlias(String[] alias) {
	 this.alias = alias;
  }

  KeyWords(String keyWord) {
    this.keyWord = keyWord;
  }
  
  KeyWords(String keyWord, String[] alias) {
	  this.keyWord = keyWord;
	  this.alias = alias;
  }
  
  public Grammar parseGrammar(Word curWord,Stack<Word> stack, JProperties properties, String namespace, Grammar parent) {
    stack.push(curWord);
    Grammar grammar = createGramar();
    grammar.setLine(curWord.getLine());
    grammar.setNamespace(namespace);
    grammar.setProperties(properties);
    grammar.setParent(parent);
    grammar.parseGrammar(stack);
    return grammar;
  }
  
  public abstract Grammar createGramar();
}
