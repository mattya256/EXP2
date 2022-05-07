package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.parse.condition.Condition;

public class ElseIF extends CParseRule {
	//private CToken op;
	private CParseRule condition;
	private List<CParseRule> list = new ArrayList<CParseRule>();
 
	public ElseIF(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_ELSEIF;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.err.println(";;;parse-ElseIf-Error");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if(tk.getType()== CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "'('がありません");
		}
		condition = new Condition(pcx);
		condition.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "')'がありません");
		}
		if(tk.getType()== CToken.TK_LCBRA) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "'{'がありません");
		}
		int i=0;
		while (Statement.isFirst(tk) ) {
			System.out.println(";;;parse-Program-"+i);
			list.add(new Statement(pcx));
			list.get(i).parse(pcx);
			i++;
			tk = ct.getCurrentToken(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_RCBRA) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "'}'がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-ElseIF");
		condition.semanticCheck(pcx);
		for(CParseRule state:list) {
			state.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		System.out.println(";;;ElseIF starts");
		condition.codeGen(pcx);
		for(CParseRule state:list) {
			state.codeGen(pcx);
		}
		o.println(";;; ElseIF completes");
	}
}