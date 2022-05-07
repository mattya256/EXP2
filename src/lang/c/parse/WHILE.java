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
import lang.c.parse.condition.Condition3;

public class WHILE extends CParseRule {
	//private CToken op;
	private CParseRule condition;
    private List<CParseRule> list = new ArrayList<CParseRule>();
    
	public WHILE(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_WHILE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-WHILE");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if(tk.getType()== CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() + "'('がありません,補いました");
		}
		if(Condition3.isFirst(tk)) {
			condition = new Condition3(pcx);
			condition.parse(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() + "Conditionがありません");
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() + "')'がありません,補いました");
		}
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
		System.out.println(";;;semantic-IF");
		condition.semanticCheck(pcx);
		for(CParseRule state:list) {
			state.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		System.out.println(";;;WHILE starts");		
		int seq = pcx.getSeqId();
		o.println("RE" + seq+":");
		condition.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; WHILE:スタックの値を取り出す");
		o.println("\tBRZ\tFIN" + seq  + "\t; WHILE: falseの場合はラベルに移動");
		for(CParseRule state:list) {
			state.codeGen(pcx);
		}
		o.println("\tJMP\tRE" + seq + "\t; WHILE: 無条件分岐");
		o.println("FIN" + seq+":");
		o.println(";;; WHILE completes");
	}
}