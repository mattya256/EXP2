package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Term extends CParseRule {
	// term ::= factor
	private CParseRule factor;
	private CParseRule term;
	public Term(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		System.out.println(";;;parse-Term");
		CParseRule mult_or_div = null;
		factor = new Factor(pcx);
		factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (TermMult.isFirst(tk) || TermDiv.isFirst(tk)) {
			if(TermMult.isFirst(tk)) {
				mult_or_div = new TermMult(pcx,factor);
				mult_or_div.parse(pcx);
				factor=mult_or_div;
			    tk = ct.getCurrentToken(pcx);
			}else if(TermDiv.isFirst(tk)){
				mult_or_div = new TermDiv(pcx,factor);
				mult_or_div.parse(pcx);
				factor = mult_or_div;
			    tk = ct.getCurrentToken(pcx);
			}else {
				pcx.warning("parse term");
			}
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-term");
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(factor.getCType());		// factor の型をそのままコピー
			this.setConstant(factor.isConstant());
		}else {
			pcx.warning("term-semantic");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (factor != null) { factor.codeGen(pcx); }
		o.println(";;; term completes");
	}
}
