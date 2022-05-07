package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Array extends CParseRule {
	// factor ::= number
	private CParseRule expression;
	CToken tk;
	public Array(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LBRA);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Array");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		tk = ct.getCurrentToken(pcx);
		// (の次の字句を読む
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType()== CToken.TK_RBRA) {
				tk = ct.getNextToken(pcx);
			}else {
				pcx.warning(tk.toExplainString() + "']'がありません,補いました");
			}
		} else {
			pcx.recoverableError(tk.toExplainString() + "[の後ろはexpressionです");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_Array");
		if(expression!=null){
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}else{
			System.err.println("Array_semantic_err");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;;Array starts");
		if(expression != null) {
			expression.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; Array: ２数を取り出して、足し、積む<" + tk.toString() + ">");
			o.println("\tMOV\t-(R6), R1\t; Array:");
			o.println("\tADD\tR1, R0\t; Array:");
			o.println("\tMOV\tR0, (R6)+\t; Array:");
		}else {
			System.err.println("array_codegen...expressionがありません");
		}
		o.println(";;; Array completes");
	}
}