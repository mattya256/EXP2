package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {
	public static final int TK_PLUS			= 2;				// +
	public static final int TK_MINUS			= 3;				// -
	public static final int TK_AMP			= 4;				//AMP &
	public static final int TK_MULT			= 5;				//  "*"
	public static final int TK_DIV			= 6;				//  "/"
	public static final int TK_LPAR			= 7;				//  "("
	public static final int TK_RPAR			= 8;				//  ")"
	public static final int TK_LBRA			= 9;				//  "["
	public static final int TK_RBRA			= 10;				//  "]"
	public static final int TK_IDENT			= 11;				//  "識別子"
	public static final int TK_ASSIGN		= 12;				//  "="
	public static final int TK_SEMI			= 13;				//  ";"
	public static final int TK_LT				= 14;				//  "<"
	public static final int TK_LE				= 15;				//  "<="
	public static final int TK_GT				= 16;				//  ">"
	public static final int TK_GE				= 17;				//  ">="
	public static final int TK_EQ				= 18;				//  "=="
	public static final int TK_NE				= 19;				//  "!="
	public static final int TK_TRUE			= 20;				//  "TRUE"
	public static final int TK_FALSE			= 21;				//  "FALSE"
	public static final int TK_LCBRA			= 22;				//  "{"
	public static final int TK_RCBRA			= 23;				//  "}"
	public static final int TK_IF				= 24;				//  "if"
	public static final int TK_ELSEIF		= 25;				//  "elseif"
	public static final int TK_ELSE			= 26;				//  "else"
	public static final int TK_WHILE			= 27;				//  "while"
	public static final int TK_INPUT			= 28;				//  "INPUT"
	public static final int TK_OUTPUT			= 29;			//  "OUTPUT"
	public static final int TK_ENDIF			= 30;			//  "ENDIF"
	public static final int TK_NOT			= 31;				//  "!"
	public static final int TK_AND			= 32;			//  "&&"
	public static final int TK_OR			= 33;			//  "||"
	public static final int TK_INT			= 34;			//  "INT"
	public static final int TK_CONST			= 35;			//  "CONST"
	public static final int TK_COMMA			= 36;			//  ","
	public static final int TK_VOID			= 37;			//  "VOID"
	public static final int TK_FUNC			= 38;			//  "FUNC"
	public static final int TK_CALL			= 39;			//  "CALL"
	public static final int TK_RETURN			= 40;			//  "return"
	public static final int TK_ATO			= 41;			//  "@"
	
	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
