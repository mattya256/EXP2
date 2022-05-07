package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class addressToValue extends CParseRule {
	// number ::= NUM
	private CParseRule primary;
	CToken tk;
	public addressToValue(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-AtoV");
		CTokenizer ct = pcx.getTokenizer();
		tk = ct.getCurrentToken(pcx);
		primary = new Primary(pcx);
		primary.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-AtoV");
		primary.semanticCheck(pcx);
		this.setCType(primary.getCType());
		this.setConstant(primary.isConstant());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; AtoV starts");
		primary.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; AtoV: アドレスを取り出して、内容を参照して、積む<"
				+ tk.toExplainString() + ">");
				o.println("\tMOV\t(R0), (R6)+\t; AtoV:");
		o.println(";;; AtoV completes");
	}
}
