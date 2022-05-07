package lang.c;

import java.util.HashMap;

public class CTokenRule extends HashMap<String, Object> {
	private static final long serialVersionUID = 1139476411716798082L;

	public CTokenRule() {
		put("true", new Integer(CToken.TK_TRUE));
		put("false", new Integer(CToken.TK_FALSE));
		put("if", new Integer(CToken.TK_IF));
		put("elseif", new Integer(CToken.TK_ELSEIF));
		put("else", new Integer(CToken.TK_ELSE));
		put("while", new Integer(CToken.TK_WHILE));
		put("input", new Integer(CToken.TK_INPUT));
		put("output", new Integer(CToken.TK_OUTPUT));
		put("endif", new Integer(CToken.TK_ENDIF));
		put("int", new Integer(CToken.TK_INT));
		put("const", new Integer(CToken.TK_CONST));
		put("void", new Integer(CToken.TK_VOID));
		put("func", new Integer(CToken.TK_FUNC));
		put("call", new Integer(CToken.TK_CALL));
		put("return", new Integer(CToken.TK_RETURN));
	}
}
