package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementCall2 extends CParseRule {
	//private CToken op;
	private CParseRule ident;
    private List<CParseRule> list = new ArrayList<CParseRule>();
    private CToken identname; 
	private int count =0;
	private boolean mult = false;
	public StatementCall2(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CALL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-stCall2");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		pcx.call();
		if(tk.getType()== CToken.TK_CALL) {
			tk = ct.getNextToken(pcx);
		}
		if (Ident.isFirst(tk)) {
			identname = ct.getCurrentToken(pcx);
			ident = new Ident(pcx);
			ident.parse(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"identがありません,stcall2");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"LPARがありません,補いました,stcall2");
		}
		pcx.callcancel();
		if(Expression.isFirst(tk)) {
			list.add(new Expression(pcx));
			list.get(count).parse(pcx);
			count++;
			tk = ct.getCurrentToken(pcx);
			while(tk.getType()==CToken.TK_COMMA) {
				tk = ct.getNextToken(pcx);
				if(Expression.isFirst(tk)) {
					list.add(new Expression(pcx));
					list.get(count).parse(pcx);
					count++;
				}else {
					pcx.recoverableError(tk.toExplainString() +"expressionがありません,stcall2");
				}
				tk = ct.getCurrentToken(pcx);
			}
			tk = ct.getCurrentToken(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"RPARがありません,補いました,stcall2");
		}
		if(!pcx.kyokusyokansuusecheck(identname.getText())) {
			pcx.warning("使用できない局所関数を使用しています");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-stCall2");
		ident.semanticCheck(pcx);
		this.setCType(ident.getCType());		// ident の型をそのままコピー
		this.setConstant(ident.isConstant());
		for(CParseRule Expression:list) {
			Expression.semanticCheck(pcx);
			pcx.sethikisulist(Expression.getCType());
		}
		CSymbolTableEntry get;
		get = pcx.search(identname.getText());
		for(int i=0;i<100;i++) {
			if(get.gethikisu()[i] != pcx.gethikisu()[i]) {
				pcx.fatalError(get.gethikisu()[i]+","+pcx.gethikisu()[i]+","+"後ろから"+(i+1)+"番目の引数が違います,Func");
			}
		}
		pcx.resethikisu();
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;stCall2 starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		int gomi = count;
		for(;count>=1;) {
			count--;
			list.get(count).codeGen(pcx);
		}
		o.println("\tJSR\t" + identname.getText()  + "\t; StCall2: ラベルに移動");
		o.println("\tSUB\t#"+gomi+ ",R6\t; StCall2: 引数を削除:");
		o.println("\tMOV\tR0, (R6)+\t; StCall2: Returnされた値をスタックに移動:");
		o.println(";;; stCall2 completes");
	}
}