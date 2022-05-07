package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class primaryMult extends CParseRule {
	// factorAmp ::= Amp number
	private CToken op;
	private CParseRule left, right;
	public primaryMult(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-primaryMult");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// ×の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if(Variable.isFirst(tk)) {
			right = new Variable(pcx);
			right.parse(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "*の後ろはvariableです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_Primarymult");
		right.semanticCheck(pcx);
//		if(right.getCType().getType()==CType.T_int || right.getCType().getType()==CType.T_Bint) {
//			System.err.println("*の後にint型やint[]型が来るのは不正です");
//		}
		if(right.getCType().getType()==CType.T_pint) {
			this.setCType(CType.getCType(CType.T_int));	
		}else if(right.getCType().getType()==CType.T_Bpint) {
			this.setCType(CType.getCType(CType.T_Bint));	
		}else {
			pcx.warning("*の後にint型やint[]型が来るのは不正です");
			if(right.getCType().getType()==CType.T_int) {
				this.setCType(CType.getCType(CType.T_int));	
			}else if(right.getCType().getType()==CType.T_Bint) {
				this.setCType(CType.getCType(CType.T_Bint));	
			}
		}
		setConstant(right.isConstant());	
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;primaryMult-starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		if (right != null) {
			right.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; PrimaryMult: アドレスを取り出して、内容を参照して、積む<"
			+ op.toExplainString() + ">");
			o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
			}
		System.out.println(";;;primaryMult-complete");
	}
}