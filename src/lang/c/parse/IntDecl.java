package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class IntDecl extends CParseRule {
	//private CToken op;
	private CParseRule declitem;
	private List<CParseRule> list = new ArrayList<CParseRule>();
	private CToken num;
	public IntDecl(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		pcx.resethikisu();
		System.out.println(";;;parse-INTDecl");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
		}
		int count =0;
		if (DeclItem.isFirst(tk)) {
			list.add(new DeclItem(pcx));
			list.get(count).parse(pcx);
			count++;
		}else {
			pcx.recoverableError(tk.toExplainString() +"declitemがありません");
		}
		tk = ct.getCurrentToken(pcx);
		pcx.resethikisu();
		while(tk.getType()== CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (DeclItem.isFirst(tk)) {
				list.add(new DeclItem(pcx));
				list.get(count).parse(pcx);
				count++;
			}else {
				pcx.recoverableError(tk.toExplainString() +"declitemがありません");
			}
			tk = ct.getCurrentToken(pcx);
			pcx.resethikisu();
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"SEMIがありません");
		}
		pcx.resethikisu();
			
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-INTtDecl");
		for(CParseRule DeclItem:list) {
			DeclItem.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;INTDecl starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		for(CParseRule DeclItem:list) {
			DeclItem.codeGen(pcx);
		}
		o.println(";;; INTDecl completes");
	}
}