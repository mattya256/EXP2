package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConstDecl extends CParseRule {
	//private CToken op;
	private CParseRule constitem;
	private List<CParseRule> list = new ArrayList<CParseRule>();
	private CToken num;
	
	public ConstDecl(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-ConstDecl");
		pcx.resethikisu();
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_CONST) {
			tk = ct.getNextToken(pcx);
		}
		if (tk.getType()== CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"INTがありません");
		}
		int count =0;
		if (ConstItem.isFirst(tk)) {
			list.add(new ConstItem(pcx));
			list.get(count).parse(pcx);
			count++;
		}else {
			pcx.recoverableError(tk.toExplainString() +"constitemがありません");
		}
		tk = ct.getCurrentToken(pcx);
		pcx.resethikisu();
		while(tk.getType()== CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (ConstItem.isFirst(tk)) {
				list.add(new ConstItem(pcx));
				list.get(count).parse(pcx);
				count++;
			}else {
				pcx.recoverableError(tk.toExplainString() +"constitemがありません");
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
		System.out.println(";;;semantic-ConstDecl");
		for(CParseRule ConstItem:list) {
			ConstItem.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;ConstDecl starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		for(CParseRule ConstItem:list) {
			ConstItem.codeGen(pcx);
		}
		o.println(";;; ConstDecl completes");
	}
}