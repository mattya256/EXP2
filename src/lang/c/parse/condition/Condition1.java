package lang.c.parse.condition;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Condition1 extends CParseRule {
	// factor ::= number
	private CParseRule expression;
	private CParseRule condition;
	private boolean bool = true;
	public Condition1(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_NOT|| Condition0.isFirst(tk) );
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-1condition");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_NOT) {
			System.out.println(";;;parse-1condition1");
			tk = ct.getNextToken(pcx);
			bool=false;
		}
		condition = new Condition0(pcx);
		condition.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_1condition");
		if(condition != null) {
			System.out.println(";;;semantic_1condition1");
			condition.semanticCheck(pcx);	
			if(condition.getCType().getType() != CType.T_bool) {
				System.err.println("con0_ctype");
			}
			setCType(condition.getCType());		
		} else {
			pcx.recoverableError(";;;semantic_1condition");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;;condition1 starts");
		if(condition!=null) {
			condition.codeGen(pcx);
		}
		if(bool){
		}else{
			o.println("\tMOV\t#0x0001, R1\t; Condition0: NOTの時論理反転");
			o.println("\tMOV\t-(R6), R2\t; Condition0:");
			o.println("\tXOR\tR1, R2\t; Condition0:");
			o.println("\tMOV\tR2, (R6)+\t; Condition0:");
		}
		o.println(";;; condition1 completes");
	}
}