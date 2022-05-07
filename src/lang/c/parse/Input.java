package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Input extends CParseRule {
	// program ::= expression EOF
	private CParseRule input;
	CToken tk;

	public Input(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Input");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_INPUT) {
			tk = ct.getNextToken(pcx);
		}else {
			//System.err.println(";がありません parse-staA");
			pcx.warning("inputがありません,補いました");
		}
		if (FactorAmp.isFirst(tk)) {
			input = new FactorAmp(pcx);
			input.parse(pcx);
		}else if(Primary.isFirst(tk)){
			input = new Primary(pcx);
			input.parse(pcx);
		}else {
			//System.err.println(";がありません parse-staA");
			pcx.recoverableError("FactorAmpまたはaTOvがありません input");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		}else {
			//System.err.println(";がありません parse-staA");
			pcx.warning(";がありません,補いました");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_input");
		input.semanticCheck(pcx);
		if(input.isConstant()) {
			pcx.warning("定数にはinputできません");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; input starts");
		input.codeGen(pcx);		
		o.println("\tMOV\t-(R6), R0\t; Input: アドレスを取り出す<" + tk.toString() + ">");
		o.println("\tMOV\t#0xFFE0, R1\t; Input:");
		o.println("\tMOV\t(R1), (R0)\t; Input:アドレスに値を入れる");
		o.println(";;;input completes");
	}
}
