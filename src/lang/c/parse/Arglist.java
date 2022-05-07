package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Arglist extends CParseRule {
	//private CToken op;
	private List<CParseRule> list = new ArrayList<CParseRule>();
	public Arglist(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Argitem.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Arglist");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		int count = 0;
		if (Argitem.isFirst(tk)) {
			list.add(new Argitem(pcx));
			list.get(count).parse(pcx);
			count++;
		}else {
			pcx.recoverableError(tk.toExplainString() +"Argitemがありません,arglist");
		}
		tk = ct.getCurrentToken(pcx);
		while(tk.getType()== CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (Argitem.isFirst(tk)) {
				list.add(new Argitem(pcx));
				list.get(count).parse(pcx);
				count++;
			}else {
				pcx.recoverableError(tk.toExplainString() +"Argitemがありません,arglist");
			}
			tk = ct.getCurrentToken(pcx);
		}

	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-Arglist");
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;Arglistl starts");
		PrintStream o = pcx.getIOContext().getOutStream();
//		o.println("\tJSR\t" + identname.getText()  + "\t; StCall: ラベルに移動");
		o.println(";;; Arglist completes");
	}
}