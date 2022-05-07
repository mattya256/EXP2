package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Output extends CParseRule {
	// program ::= expression EOF
	private CParseRule output;
	CToken tk;

	public Output(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OUTPUT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-output");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_OUTPUT) {
			tk = ct.getNextToken(pcx);
		}else {
			//System.err.println(";がありません parse-staA");
			pcx.warning("outputがありません");
		}
		if (FactorAmp.isFirst(tk)) {
			output = new FactorAmp(pcx);
			output.parse(pcx);
		}else if(addressToValue.isFirst(tk)){
			output = new addressToValue(pcx);
			output.parse(pcx);
		}else {
			//System.err.println(";がありません parse-staA");
			pcx.warning("FactorAmpまたはaTOvがありません input");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		}else {
			//System.err.println(";がありません parse-staA");
			pcx.warning(";がありません output");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_output");
		output.semanticCheck(pcx);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; output starts");
		output.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; Output: 値を積む<" + tk.toString() + ">");
		o.println("\tMOV\t#0xFFE0, R1\t; Output:");
		o.println("\tMOV\tR0, (R1)\t; Output:値を出力する");
		o.println(";;;output completes");
	}
}
