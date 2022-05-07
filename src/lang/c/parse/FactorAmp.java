package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class FactorAmp extends CParseRule {
	// factorAmp ::= Amp number
//	private CToken op;
	private CParseRule number;
	private CParseRule primary;
	public FactorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;FactorAmp");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
//		op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
		} else if (Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "&の後ろはnumberかprimaryです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		pcx.hairetuchance();
		if (number != null) {
			number.semanticCheck(pcx);
			this.setCType(CType.getCType(CType.T_pint));
			this.setConstant(true);
		}else if(primary!=null){
			primary.semanticCheck(pcx);
			if(primary.isprimaryMult()) {
				pcx.recoverableError("primaryのインスタンスがprimaryMultです");
			}
			if(primary.getCType().getType()!=CType.T_int && primary.getCType().getType()!=CType.T_Bint) {
				pcx.warning("&の後ろにくるのはintもしくはint[]です");
			}
			if(primary.getCType().getType()==CType.T_int) {
				this.setCType(CType.getCType(CType.T_pint));
			}else if (primary.getCType().getType()==CType.T_Bint) {
				this.setCType(CType.getCType(CType.T_Bpint));
			}
			this.setConstant(primary.isConstant());
		}
		if(pcx.gethairetu()==2) {
			if(primary.getCType().getType()==CType.T_Bint) {
				this.setCType(CType.getCType(CType.T_Bint));
			}else if (primary.getCType().getType()==CType.T_Bpint) {
				this.setCType(CType.getCType(CType.T_Bpint));
			}
			System.out.println(";;;配列を[0]のアドレスとして使用します");
		}
		pcx.hairetureset();
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		if (number != null) { number.codeGen(pcx); }
		if (primary != null) { primary.codeGen(pcx); }
		o.println(";;; factorAmp completes");
	}
}