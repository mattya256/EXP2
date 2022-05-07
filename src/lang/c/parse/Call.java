package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Call extends CParseRule {
	//private CToken op;
    private List<CParseRule> list = new ArrayList<CParseRule>();
	public Call(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-call");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		}
		int count =0;
		if(Expression.isFirst(tk)) {
			list.add(new Expression(pcx));
			list.get(count).parse(pcx);
			count++;
			if(tk.getType()==CToken.TK_COMMA) {
				if(Expression.isFirst(tk)) {
					list.add(new Expression(pcx));
					list.get(count).parse(pcx);
					count++;
				}else {
					pcx.recoverableError(tk.toExplainString() +"expressionがありません");
				}
			}
		}
		if(tk.getType()== CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() + "RPARがありません");
		}
			
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-call");
		for(CParseRule Expression:list) {
			Expression.semanticCheck(pcx);
			pcx.sethikisulist(Expression.getCType());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;call starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;;call completes");
	}
}