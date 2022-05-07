package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Typelist extends CParseRule {
	//private CToken op;
	private List<CParseRule> list = new ArrayList<CParseRule>();
	public Typelist(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Typeitem.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-typelist");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		int count = 0;
		if (Typeitem.isFirst(tk)) {
			list.add(new Typeitem(pcx));
			list.get(count).parse(pcx);
			count++;
		}else {
			pcx.recoverableError(tk.toExplainString() +"typeitemがありません,typelist");
		}
		tk = ct.getCurrentToken(pcx);
		while(tk.getType()== CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (Typeitem.isFirst(tk)) {
				list.add(new Typeitem(pcx));
				list.get(count).parse(pcx);
				count++;
			}else {
				pcx.recoverableError(tk.toExplainString() +"typeitemがありません,typelist");
			}
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-typelist");
		for(CParseRule TypeItem:list) {
			TypeItem.semanticCheck(pcx);
			pcx.sethikisulist(TypeItem.getCType());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;typelistl starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		//o.println("\tJSR\t" + identname.getText()  + "\t; StCall: ラベルに移動");
		o.println(";;; typelist completes");
	}
}