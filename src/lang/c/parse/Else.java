package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Else extends CParseRule {
	//private CToken op;
	private CParseRule condition;
	private List<CParseRule> list = new ArrayList<CParseRule>();
 
	public Else(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_ELSE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-ElseIF");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if(tk.getType()== CToken.TK_LCBRA) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() + "'{'がありません,補いました");
		}
		int i=0;
		while (Statement.isFirst(tk) ) {
			System.out.println(";;;parse-Program-"+i);
			if(Statement.isFirst(tk)) {
				try {
					// 文の途中で構文エラーになるかもしれないので...
					list.add(new Statement(pcx));
					list.get(i).parse(pcx);
					i++;
				} catch (RecoverableErrorException e) {
					i++;
					System.out.println(":::else-Rec");
					// そのときにはここで例外を捕まえて、’;’ か’}’ が出るまで読み飛ばして回復する（立ち直る）
					ct.skipTo(pcx, CToken.TK_SEMI, CToken.TK_RCBRA);
					tk = ct.getNextToken(pcx);
				}
				tk = ct.getCurrentToken(pcx);
			}else {
				pcx.recoverableError(tk.toExplainString() + "Statementがありません");
			}
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_RCBRA) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() + "'}'がありません,補いました");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-Else");
		for(CParseRule state:list) {
			state.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		System.out.println(";;;Else starts");
		for(CParseRule state:list) {
			state.codeGen(pcx);
		}
		o.println(";;; Else completes");
	}
}