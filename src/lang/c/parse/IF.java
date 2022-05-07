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

public class IF extends CParseRule {
	//private CToken op;
	private CParseRule condition;
    private List<CParseRule> statelist = new ArrayList<CParseRule>();
    private List<CParseRule> elselist = new ArrayList<CParseRule>();
    private CParseRule else1;
    
	public IF(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IF;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-IF");
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
		int state_count=0;
		while (Statement.isFirst(tk) ) {
			System.out.println(";;;parse-Program-"+state_count);
			if(Statement.isFirst(tk)) {
				try {
					// 文の途中で構文エラーになるかもしれないので...
					statelist.add(new Statement(pcx));
					statelist.get(state_count).parse(pcx);
					state_count++;
				} catch (RecoverableErrorException e) {
					state_count++;
					System.out.println(":::if-Rec");
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
		if (Else.isFirst(tk) ) {
			System.out.println(";;;parse-else");
			else1 = new Else(pcx);
			else1.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-IF");
		condition.semanticCheck(pcx);
		for(CParseRule state:statelist) {
			state.semanticCheck(pcx);
		}
		for(CParseRule elseif:elselist) {
			elseif.semanticCheck(pcx);
		}
		if(else1 !=null) {
			else1.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		System.out.println(";;;IF starts");
		condition.codeGen(pcx);
		int seq = pcx.getSeqId();
		o.println("\tMOV\t-(R6), R0\t; IF:スタックの値を取り出す");
		if(else1!=null) {
			o.println("\tBRZ\tELSE" + seq  + "\t; IF: falseの場合はラベルに移動");
		}else {
			o.println("\tBRZ\tFIN" + seq  + "\t; IF: falseの場合はラベルに移動");
		}
		for(CParseRule state:statelist) {
			state.codeGen(pcx);
		}
		
//		for(CParseRule elseif:elselist) {		
//			elseif.codeGen(pcx);
//		}
		
		if(else1 !=null) {
			o.println("\tJMP\tFIN" + seq + "\t; IF: 無条件分岐");
			o.println("ELSE" + seq +":");
			else1.codeGen(pcx);
		}
		o.println("FIN" + seq+":");
		o.println(";;; IF completes");
	}
}

