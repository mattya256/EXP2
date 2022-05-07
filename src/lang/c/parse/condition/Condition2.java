package lang.c.parse.condition;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Condition2 extends CParseRule {
	// factor ::= number
	private CParseRule condition;
	private CParseRule condition_2;
	private boolean bool = true;
	public Condition2(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (Condition1.isFirst(tk) );
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-2condition");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		condition = new Condition1(pcx);
		condition.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_AND) {
			System.out.println(";;;parse-2condition1");
			tk = ct.getNextToken(pcx);
			condition_2 = new Condition1(pcx);
			condition_2.parse(pcx);
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_2condition");
		if(condition != null) {
			System.out.println(";;;semantic_2condition1");
			condition.semanticCheck(pcx);
			if(condition.getCType().getType() != CType.T_bool) {
				System.err.println("con0_ctype");
			}
			if(condition_2 != null) {
				System.out.println(";;;semantic_2condition2");
				condition_2.semanticCheck(pcx);
				if(condition_2.getCType().getType() != CType.T_bool) {
					System.err.println("con0_ctype");
				}
			}
			setCType(condition.getCType());		
		} else {
			pcx.recoverableError(";;;semantic_2condition");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;;condition2 starts");
		if(condition !=null) {
			condition.codeGen(pcx);
		}
		if(condition_2 != null){
			condition_2.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; Condition2: ANDならば２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; Condition2:");
			o.println("\tAND\tR0, R1\t; Condition2: ");
			o.println("\tMOV\tR1, (R6)+\t; Condition2:");
		}
		o.println(";;; condition2 completes");
	}
}