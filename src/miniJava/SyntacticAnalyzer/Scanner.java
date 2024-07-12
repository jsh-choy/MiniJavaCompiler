package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import miniJava.ErrorReporter;

public class Scanner {
	private InputStream _in;
	private ErrorReporter _errors;
	private StringBuilder _currentText;
	private char _currentChar;
	private boolean _eot = false;

	public Scanner(InputStream in, ErrorReporter errors) {
		this._in = in;
		this._errors = errors;
		this._currentText = new StringBuilder();

		nextChar();
	}

	public Token scan() {
		// TODO: Consider what happens if the current char is whitespace
		// While it's either whitespace or not the end of the token, skip it...
		while (!_eot && isWhitespace(_currentChar)) {
			skipIt();
		}

		if (_eot) {
			return makeToken(TokenType.EOT);
		}

		// TODO: This function should check the current char to determine what the token could be.
		if (validStartChar(_currentChar)) {
			// StringBuilder text = new StringBuilder();
			_currentText.setLength(0);

			while (!_eot && validCharId(_currentChar)) {
				takeIt();
			}

			switch (_currentText.toString()) {
				case "class":
					return makeToken(TokenType.CLASS);
				case "public":
					return makeToken(TokenType.PUBLIC);
				case "private":
					return makeToken(TokenType.PRIVATE);
				case "static":
					return makeToken(TokenType.STATIC);
				case "void":
					return makeToken(TokenType.VOID);
				case "null":
					return makeToken(TokenType.NULL);
				case "this":
					return makeToken(TokenType.THIS);
				case "if":
					return makeToken(TokenType.IF);
				case "else":
					return makeToken(TokenType.ELSE);
				case "while":
					return makeToken(TokenType.WHILE);
				case "boolean":
					return makeToken(TokenType.BOOLEAN);
				case "int":
					return makeToken(TokenType.INT);
				case "new":
					return makeToken(TokenType.NEW);
				case "true":
					return makeToken(TokenType.TRUE);
				case "false":
					return makeToken(TokenType.FALSE);

				default:
					return makeToken(TokenType.ID);
			}
		} else if (isDigit(_currentChar)) {
			_currentText.setLength(0);
			while (!_eot && isDigit(_currentChar)) {
				takeIt();
			}

			return makeToken(TokenType.INTLITERAL);
		}

		else {
			_currentText.setLength(0);
			switch (_currentChar) {
				case '{':
					takeIt();
					return makeToken(TokenType.LBRACE);
				case '}':
					takeIt();
					return makeToken(TokenType.RBRACE);
				case '(':
					takeIt();
					return makeToken(TokenType.LPAREN);
				case ')':
					takeIt();
					return makeToken(TokenType.RPAREN);
				case '[':
					takeIt();
					return makeToken(TokenType.LBRACKET);
				case ']':
					takeIt();
					return makeToken(TokenType.RBRACKET);
				case '=':
					takeIt();
					if (_currentChar == '=') {
						takeIt();
						return makeToken(TokenType.EQUALEQUAL);
					}
					return makeToken(TokenType.EQUAL);

				// TODO: Consider what happens if there is a comment (// or /* */)
				case '/':
					skipIt();
					if (_currentChar == '/') {
						skipIt();
						do {
							skipIt();
						} while (_currentChar != '\n' && !_eot);
						return scan();
					} else if (_currentChar == '*') {
						skipIt();
						do {
							skipIt();
							if (_currentChar == '*') {
								skipIt();
								if (_currentChar == '/') {
									skipIt();
									return scan();
								}
							}
						} while (!_eot);
					} else {
						return makeToken(TokenType.DIV);
					}
				case ';':
					takeIt();
					return makeToken(TokenType.SEMICOLON);
				case '!':
					takeIt();
					if (_currentChar == '=') {
						takeIt();
						return makeToken(TokenType.NEQ);
					} else {
						return makeToken(TokenType.NOT);
					}

				case '>':
					takeIt();
					if (_currentChar == '=') {
						takeIt();
						return makeToken(TokenType.GTEQ);
					} else {
						return makeToken(TokenType.GT);
					}

				case '<':
					takeIt();
					if (_currentChar == '=') {
						takeIt();
						return makeToken(TokenType.LTEQ);
					} else {
						return makeToken(TokenType.LT);
					}

				case '&':
					takeIt();
					if (_currentChar == '&') {
						takeIt();
						return makeToken(TokenType.AND);
					}

				case '|':
					takeIt();
					if (_currentChar == '|') {
						takeIt();
						return makeToken(TokenType.OR);
					}

				case '+':
					takeIt();
					if (_currentChar == '+') {
						takeIt();
						return makeToken(TokenType.PLUSPLUS);
					}
					return makeToken(TokenType.PLUS);

				case '-':
					takeIt();
					if (_currentChar == '-') {
						takeIt();
						return makeToken(TokenType.MINUSMINUS);
					}
					return makeToken(TokenType.MINUS);

				case '_':
					takeIt();
					return makeToken(TokenType.ID);

				case '.':
					// TODO: Implement this correctly
					return makeToken(TokenType.DOT);
				case ',':
					// TODO: Implement this correctly
					return makeToken(TokenType.COMMA);

				default:
					return makeToken(TokenType.ERROR);
			}
		}
	}

	private void takeIt() {
		_currentText.append(_currentChar);
		nextChar();
	}

	private void skipIt() {
		nextChar();
	}



	private void nextChar() {
		try {
			int c = _in.read();

			// TODO: What happens if c == -1?
			if (c == -1) {
				_eot = true;
			}

			// TODO: What happens if c is not a regular ASCII character?
			else if (c > 127) {
				_errors.reportError("Not Regular ASCII character.");
				_eot = true;
			}
			_currentChar = (char)c;
		} catch( IOException e ) {
			// TODO: Report an error here
			_errors.reportError("I/O Exception!");
		}
	}

	private static boolean isNewline (char c) {
		return c == '\n' || c == '\r';
	}

	private static boolean isWhitespace (char c) {
		return isNewline(c) || c == ' ' || c == '\t';
	}

	private static boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}

	private static boolean isChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean validStartChar(char c) {
		return isChar(c) | c == '$' | c == '_';
	}

	private boolean validCharId(char c) {
		return validStartChar(c) || isDigit(c);
	}

	private void resetText() {
		_currentText.setLength(0);
	}

	private Token makeToken( TokenType toktype ) {
		// TODO: return a new Token with the appropriate type and text
		//  contained in

		switch (toktype) {
			case ID:
				return new Token(TokenType.ID, _currentText.toString());
			case PUBLIC:
				return new Token(TokenType.PUBLIC, "public");
			case PRIVATE:
				return new Token(TokenType.PRIVATE, "private");
			case STATIC:
				return new Token(TokenType.STATIC, "static");
			case VOID:
				return new Token(TokenType.VOID, "void");
			case NULL:
				return new Token(TokenType.NULL, "null");
			case IF:
				return new Token(TokenType.IF, "if");
			case ELSE:
				return new Token(TokenType.ELSE, "else");
			case WHILE:
				return new Token(TokenType.WHILE, "while");
			case CLASS:
				return new Token(TokenType.CLASS, "class");
			case THIS:
				return new Token(TokenType.THIS, "this");
			case BOOLEAN:
				return new Token(TokenType.BOOLEAN, "boolean");
			case INT:
				return new Token(TokenType.INT, "int");
			case NEW:
				return new Token(TokenType.NEW, "new");
			case TRUE:
				return new Token(TokenType.TRUE, "true");
			case FALSE:
				return new Token(TokenType.FALSE, "false");
			case INTLITERAL:
				return new Token(TokenType.INTLITERAL, _currentText.toString());
			case EQUAL:
				return new Token(TokenType.EQUAL, "=");
			case EQUALEQUAL:
				return new Token(TokenType.EQUALEQUAL, "==");
			case COMMENT:
				return new Token(TokenType.COMMENT, "//");
			case BLOCKCOMMENT:
				return new Token(TokenType.BLOCKCOMMENT, "/*");
			case MULT:
				return new Token(TokenType.MULT, "*");
			case DIV:
				return new Token(TokenType.DIV, "/");
			case NEQ:
				return new Token(TokenType.NEQ, "!=");
			case NOT:
				return new Token(TokenType.NOT, "!");
			case GT:
				return new Token(TokenType.GT, ">");
			case GTEQ:
				return new Token(TokenType.GTEQ, ">=");
			case LT:
				return new Token(TokenType.LT, "<");
			case LTEQ:
				return new Token(TokenType.LTEQ, "<=");
			case AND:
				return new Token(TokenType.AND, "&&");
			case OR:
				return new Token(TokenType.OR, "||");
			case PLUS:
				return new Token(TokenType.PLUS, "+");
			case PLUSPLUS:
				return new Token(TokenType.PLUSPLUS, "++");
			case MINUS:
				return new Token(TokenType.MINUS, "-");
			case MINUSMINUS:
				return new Token(TokenType.MINUSMINUS, "--");
			case LPAREN:
				return new Token(TokenType.LPAREN, "(");
			case RPAREN:
				return new Token(TokenType.RPAREN, ")");
			case LBRACKET:
				return new Token(TokenType.LBRACKET, "[");
			case RBRACKET:
				return new Token(TokenType.RBRACKET, "]");
			case LBRACE:
				return new Token(TokenType.LBRACE, "{");
			case RBRACE:
				return new Token(TokenType.RBRACE, "}");
			case SEMICOLON:
				return new Token(TokenType.SEMICOLON, ";");
			case COMMA:
				return new Token(TokenType.COMMA, ",");
			case DOT:
				return new Token(TokenType.DOT, ".");
			case EOT:
				return new Token(TokenType.EOT, "end of token");

			default:
				return new Token(TokenType.ERROR, _currentText.toString());
		}
	}
}
