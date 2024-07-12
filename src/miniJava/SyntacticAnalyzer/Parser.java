package miniJava.SyntacticAnalyzer;

import miniJava.ErrorReporter;

public class Parser {
	private Scanner _scanner;
	private ErrorReporter _errors;
	private Token _currentToken;

	public Parser( Scanner scanner, ErrorReporter errors ) {
		this._scanner = scanner;
		this._errors = errors;
		this._currentToken = this._scanner.scan();
	}

	class SyntaxError extends Error {
		private static final long serialVersionUID = -6461942006097999362L;
	}

	public void parse() {
		try {
			// The first thing we need to parse is the Program symbol
			parseProgram();
		} catch( SyntaxError e ) {
		}
	}

	// Program ::= (ClassDeclaration)* eot
	private void parseProgram() throws SyntaxError {
		// TODO: Keep parsing class declarations until eot
		while(_currentToken.getTokenType() != TokenType.EOT) {
			parseClassDeclaration();
		}
		// _currentToken = _scanner.scan();
	}

	// ClassDeclaration ::= class identifier { (FieldDeclaration|MethodDeclaration)* }
	private void parseClassDeclaration() throws SyntaxError {
		// TODO: Take in a "class" token (check by the TokenType)
		//  What should be done if the first token isn't "class"?
		accept(TokenType.CLASS);

		// TODO: Take in an identifier token
		if (_currentToken.getTokenText().charAt(0) == '_') {
			String errMsg = "Class id cannot start with _";
			_errors.reportError(errMsg);
			throw new SyntaxError();
		} else {
			accept(TokenType.ID);
		}

		// TODO: Take in a {
		accept(TokenType.LBRACE);
		System.out.println(_currentToken.getTokenType());
		// TODO: Parse either a FieldDeclaration or MethodDeclaration

		while (_currentToken.getTokenType() != TokenType.RBRACE) {
			parseClassOptions();
		}

		// TODO: Take in a }
		accept(TokenType.RBRACE);
	}

	private void parseClassOptions() {
		parseVisibility();

		if (_currentToken.getTokenType() == TokenType.VOID) {
			_currentToken = _scanner.scan();
			accept(TokenType.ID);
			accept(TokenType.LPAREN);
			parseMethodDecl();
		} else {
			parseType();
			accept(TokenType.ID);

			if (_currentToken.getTokenType() == TokenType.SEMICOLON) {
				parseFieldDecl();
			} else {
				accept(TokenType.LPAREN);
				parseMethodDecl();
			}
		}
	}

	private void parseVisibility() {
		if (_currentToken.getTokenType() == TokenType.PUBLIC || _currentToken.getTokenType() == TokenType.PRIVATE) {
			_currentToken = _scanner.scan();
//			accept(_currentToken.getTokenType());
		} else if (_currentToken.getTokenType() == TokenType.STATIC) {
			_currentToken = _scanner.scan();
//			accept(TokenType.STATIC);
		}
	}

	private void parseFieldDecl() {
		_currentToken = _scanner.scan();
	}

	private void parseMethodDecl() {
		if (_currentToken.getTokenType() != TokenType.RPAREN) {
			parseParameterList();
			accept(TokenType.RPAREN);
		} else {
			_currentToken = _scanner.scan();
		}

		accept(TokenType.LBRACE);

		while (_currentToken.getTokenType() != TokenType.RBRACE) {
			parseStatement();
		}

		_currentToken = _scanner.scan();
	}

	// int, boolean, id, []
	private void parseType() {
		if (_currentToken.getTokenType() != TokenType.INT
				&& _currentToken.getTokenType() != TokenType.BOOLEAN) {
			accept(TokenType.ID);
		} else {
			_currentToken = _scanner.scan();
		}

		if (_currentToken.getTokenType() == TokenType.LBRACKET) {
			_currentToken = _scanner.scan();
			accept(TokenType.RBRACKET);
		}
	}

	private void parseParameterList() {
		// TODO:
		parseType();
		accept(TokenType.ID);

		while (_currentToken.getTokenType() == TokenType.COMMA) {
			_currentToken = _scanner.scan();
			parseType();
			accept(TokenType.ID);
		}
	}

	private void parseArgList() {
		// TODO
		parseExpression();

		while (_currentToken.getTokenType() == TokenType.COMMA) {
			_currentToken = _scanner.scan();
			parseExpression();
		}
	}

	private void parseReference() {
		// TODO:
		if (_currentToken.getTokenType() == TokenType.THIS) {
			_currentToken = _scanner.scan();
		} else {
			accept(TokenType.ID);
		}

		while (_currentToken.getTokenType() == TokenType.DOT) {
			_currentToken = _scanner.scan();
			accept(TokenType.ID);
		}
	}

	private void parseStatement() {
		switch(_currentToken.getTokenType()) {
			case LBRACE:
				_currentToken = _scanner.scan();
				while (_currentToken.getTokenType() != TokenType.RBRACE) {
					parseStatement();
				}
				_currentToken = _scanner.scan();
				break;

			case RETURN:
				_currentToken = _scanner.scan();

				if (_currentToken.getTokenType() != TokenType.SEMICOLON) {
					parseExpression();
				}
				accept(TokenType.SEMICOLON);
				break;

			case IF:
				_currentToken = _scanner.scan();
				accept(TokenType.LPAREN);
				parseExpression();
				accept(TokenType.RPAREN);
				parseStatement();
				if (_currentToken.getTokenType() == TokenType.ELSE) {
					_currentToken = _scanner.scan();
					parseStatement();
				}
				break;

			case WHILE:
				_currentToken = _scanner.scan();
				accept(TokenType.LPAREN);
				parseExpression();
				accept(TokenType.RPAREN);
				parseStatement();
				break;

			case INT:
			case BOOLEAN:
				parseType();
				accept(TokenType.ID);
				accept(TokenType.EQUAL);
				parseExpression();
				accept(TokenType.SEMICOLON);
				break;

			case THIS:
				parseReference();

				if (_currentToken.getTokenType() == TokenType.LBRACKET) {
					_currentToken = _scanner.scan();
					parseExpression();
					accept(TokenType.RBRACKET);
					accept(TokenType.EQUAL);
					parseExpression();
					accept(TokenType.SEMICOLON);
				} else if (_currentToken.getTokenType() == TokenType.LPAREN) {
					_currentToken = _scanner.scan();

					if (_currentToken.getTokenType() == TokenType.ID) {
						parseArgList();
					}

					accept(TokenType.RPAREN);
					accept(TokenType.SEMICOLON);
				} else {
					accept(TokenType.EQUAL);
					parseExpression();
					accept(TokenType.SEMICOLON);
				}
				break;

			case ID:

			default:
				accept(TokenType.ID);

				switch(_currentToken.getTokenType()) {
					case ID:
						_currentToken = _scanner.scan();
						accept(TokenType.ID);
						parseExpression();
						accept(TokenType.SEMICOLON);
						break;

					case LBRACKET:
						_currentToken = _scanner.scan();
						if (_currentToken.getTokenType() != TokenType.RBRACKET) {
							parseExpression();
							accept(TokenType.RBRACKET);
						} else {
							_currentToken = _scanner.scan();
							accept(TokenType.ID);
						}
						accept(TokenType.EQUAL);
						parseExpression();
						accept(TokenType.SEMICOLON);
						break;

					case DOT:
						_currentToken = _scanner.scan();
						accept(TokenType.ID);

						while(_currentToken.getTokenType() == TokenType.DOT) {
							_currentToken = _scanner.scan();
							accept(TokenType.ID);
						}

						if (_currentToken.getTokenType() == TokenType.LBRACKET) {
							_currentToken = _scanner.scan();
							parseExpression();
							accept(TokenType.RBRACKET);
							accept(TokenType.EQUAL);
							parseExpression();
							accept(TokenType.SEMICOLON);
						} else if (_currentToken.getTokenType() == TokenType.EQUAL) {
							_currentToken = _scanner.scan();
							parseExpression();
							accept(TokenType.SEMICOLON);
						} else {
							accept(TokenType.LPAREN);
							parseArgList();
							accept(TokenType.RPAREN);
							accept(TokenType.SEMICOLON);
						}

						break;

					case EQUAL:
						_currentToken = _scanner.scan();
						parseExpression();
						accept(TokenType.SEMICOLON);
						break;

					case LPAREN:
					default:
						accept(TokenType.LPAREN);
						parseArgList();
						accept(TokenType.RPAREN);
						accept(TokenType.SEMICOLON);
				}
		}
	}

	private void parseExpression() {
		switch (_currentToken.getTokenType()) {
			case NOT:
			case MINUS:
				_currentToken = _scanner.scan();
				parseExpression();
				break;

			case LPAREN:
				_currentToken = _scanner.scan();
				parseExpression();
				accept(TokenType.RPAREN);
				break;

			case INTLITERAL:
			case TRUE:
			case FALSE:
				_currentToken = _scanner.scan();
				break;

			case NEW:
				_currentToken = _scanner.scan();

				if (_currentToken.getTokenType() == TokenType.INT) {
					_currentToken = _scanner.scan();
					accept(TokenType.LBRACKET);
					parseExpression();
					accept(TokenType.RBRACKET);
				} else {
					accept(TokenType.ID);
					if (_currentToken.getTokenType() == TokenType.LPAREN) {
						_currentToken = _scanner.scan();
						accept(TokenType.RPAREN);
					} else {
						accept(TokenType.LBRACKET);
						parseExpression();
						accept(TokenType.RBRACKET);
					}
				}
				break;

			case ID:
			case THIS:
			default:
				parseReference();
				if (_currentToken.getTokenType() == TokenType.LBRACKET) {
					_currentToken = _scanner.scan();
					parseExpression();
					accept(TokenType.RBRACKET);
				}
				else if (_currentToken.getTokenType() == TokenType.LPAREN) {
					_currentToken = _scanner.scan();

					if (_currentToken.getTokenType() == TokenType.ID) {
						parseArgList();
					}
					accept(TokenType.RPAREN);
				}
				break;
		}

		switch(_currentToken.getTokenType()) {
			case GT:
			case GTEQ:
			case LT:
			case LTEQ:
			case EQUAL:
			case NEQ:
			case AND:
			case OR:
			case PLUS:
			case MINUS:
			case MULT:
			case DIV:
				_currentToken = _scanner.scan();
				parseExpression();

			default:
				break;
		}
	}

	// This method will accept the token and retrieve the next token.
	//  Can be useful if you want to error check and accept all-in-one.
	private void accept(TokenType expectedType) throws SyntaxError {
		if( _currentToken.getTokenType() == expectedType ) {
			_currentToken = _scanner.scan();
		} else {
			// TODO: Report an error here.
			//  "Expected token X, but got Y"
			String errMsg = "Expected token: " + expectedType + ", but got: " + _currentToken.getTokenType();
			_errors.reportError(errMsg);
			throw new SyntaxError();
		}
	}
}
