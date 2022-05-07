package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Factor extends CParseRule {
	// factor ::= number
	private CParseRule plusfactor;
	private CParseRule minusfactor;
	private CParseRule unsignedfactor;
	public Factor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (plusFactor.isFirst(tk) || minusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk));
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Factor");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(plusFactor.isFirst(tk) ) {
			System.out.println(";;;parse-Factor1");
			plusfactor = new plusFactor(pcx);
			plusfactor.parse(pcx);
		}else if(minusFactor.isFirst(tk) ) {
			System.out.println(";;;parse-Factor2");
			minusfactor = new minusFactor(pcx);
			minusfactor.parse(pcx);
		}else if(UnsignedFactor.isFirst(tk) ) {
			System.out.println(";;;parse-Factor3");
			unsignedfactor = new UnsignedFactor(pcx);
			unsignedfactor.parse(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString()+"Factorではありません");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_Factor");
		if(plusfactor != null) {
			System.out.println(";;;semantic_Factor1");
			plusfactor.semanticCheck(pcx);
			setCType(plusfactor.getCType());		// plusfactor の型をそのままコピー
			setConstant(plusfactor.isConstant());	// plusfactorは常に定数
		}else if (minusfactor != null) {
			System.out.println(";;;semantic_Factor2");
			minusfactor.semanticCheck(pcx);
			setCType(minusfactor.getCType());		// minusfactor の型をそのままコピー
			setConstant(minusfactor.isConstant());	// minusfactor は常に定数
		}else if(unsignedfactor != null){
			System.out.println(";;;semantic_Factor3");
			unsignedfactor.semanticCheck(pcx);
			setCType(unsignedfactor.getCType());		// unsignedfactor の型をそのままコピー
			setConstant(unsignedfactor.isConstant());	// unsignedfactor は常に定数
		}else {
			System.err.println(";;;semantic_Facotor");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (plusfactor != null) { plusfactor.codeGen(pcx); }
		else if(minusfactor!=null){minusfactor.codeGen(pcx);}
		else if(unsignedfactor != null) {unsignedfactor.codeGen(pcx);}
		o.println(";;; factor completes");
	}
}