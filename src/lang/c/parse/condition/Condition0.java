package lang.c.parse.condition;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Condition0 extends CParseRule {
	// factor ::= number
	private CParseRule condition;
	public Condition0(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LBRA||Condition.isFirst(tk) );
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-0condition");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_LBRA) {
			System.out.println(";;;parse-0condition1");
			tk = ct.getNextToken(pcx);
			condition = new Condition3(pcx);
			condition.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_RBRA) {
				tk = ct.getNextToken(pcx);
			}else {
				pcx.recoverableError(":::con0 ']' がありません");
			}
		}else {
			System.out.println(";;;parse-0condition2");
			condition = new Condition(pcx);
			condition.parse(pcx);
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_0condition");
		if(condition != null) {
			System.out.println(";;;semantic_0condition1");
			condition.semanticCheck(pcx);
			setCType(condition.getCType());	
			if(condition.getCType().getType() != CType.T_bool) {
				System.err.println("con0_ctype");
			}	
		} else {
			pcx.recoverableError(";;;semantic_0condition");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;;condition0 starts");
		if(condition!=null) {
			condition.codeGen(pcx);
		}
		o.println(";;; condition completes");
	}
}