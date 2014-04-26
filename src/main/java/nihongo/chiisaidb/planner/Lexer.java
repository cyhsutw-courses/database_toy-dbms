package nihongo.chiisaidb.planner;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import nihongo.chiisaidb.ErrorMessage;

public class Lexer {
	private Collection<String> keywords;
	private StreamTokenizer tok;

	public Lexer(String s) {
		initKeywords();
		tok = new StreamTokenizer(new StringReader(s));
		tok.wordChars('_', '_');
		tok.ordinaryChar('.');
		tok.lowerCaseMode(true);
		nextToken();
	}

	public boolean matchDelim(char delimiter) {
		return delimiter == (char) tok.ttype;
	}

	public boolean matchStringConstant() {
		return '\'' == (char) tok.ttype;
	}

	public boolean matchNumericConstant() {
		return tok.ttype == StreamTokenizer.TT_NUMBER;
	}

	public boolean matchId() {
		return tok.ttype == StreamTokenizer.TT_WORD
				&& !keywords.contains(tok.sval);
	}

	public boolean matchKeyword(String keyword) {
		return tok.ttype == StreamTokenizer.TT_WORD && tok.sval.equals(keyword)
				&& keywords.contains(tok.sval);
	}

	public void eatDelim(char delimiter) {
		if (!matchDelim(delimiter))
			throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		nextToken();
	}

	public String eatStringConstant() {
		if (!matchStringConstant())
			throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		/*
		 * The input string constant is a quoted string token likes 'str', and
		 * its token type (ttype) is the quote character. So the string
		 * constants are not converted to lower case.
		 */
		String s = tok.sval;
		nextToken();
		return s;
	}

	public double eatNumericConstant() {
		if (!matchNumericConstant())
			throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		double d = tok.nval;
		nextToken();
		return d;
	}

	public String eatId() {
		if (!matchId()) {
			System.out.println(val());
			throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		}
		String s = tok.sval;
		nextToken();
		return s;
	}

	public void eatKeyword(String keyword) {
		if (!matchKeyword(keyword)) {
			System.out.println(val());
			throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		}
		nextToken();
	}

	public String val() {
		return tok.sval;
	}

	private void nextToken() {
		try {
			tok.nextToken();
		} catch (IOException e) {
			throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		}
	}

	private void initKeywords() {
		keywords = Arrays.asList("insert", "into", "values", "create", "table",
				"int", "varchar", "primary", "key", "select", "*", "from",
				"where");
	}

}
