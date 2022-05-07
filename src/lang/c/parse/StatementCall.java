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
import lang.c.CType;

public class StatementCall extends CParseRule {
	//private CToken op;
	private CParseRule ident;
    private List<CParseRule> list = new ArrayList<CParseRule>();
    private CToken identname; 
    private int count;
	private boolean mult = false;
	public StatementCall(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CALL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-stCall");
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
			pcx.recoverableError(tk.toExplainString() +"identがありません,stcall");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"LPARがありません,補いました,stcall");
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
					pcx.recoverableError(tk.toExplainString() +"expressionがありません,stcall");
				}
				tk = ct.getCurrentToken(pcx);
			}
			tk = ct.getCurrentToken(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"RPARがありません,補いました,stcall");
		}
		if(tk.getType()== CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() + "SEMIがありません,stcall");
		}	
		if(!pcx.kyokusyokansuusecheck(identname.getText())) {
			pcx.warning("使用できない局所関数を使用しています");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-stCall");
		ident.semanticCheck(pcx);
		this.setCType(ident.getCType());		// ident の型をそのままコピー
		this.setConstant(ident.isConstant());
		if(ident.getCType().getType()!=CType.T_void) {
			pcx.fatalError("void型以外を呼び出すことはできません");
		}
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
		System.out.println(";;;stCall starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		int gomi = count;
		for(;count>=1;) {
			count--;
			list.get(count).codeGen(pcx);
		}
		o.println("\tJSR\t" + identname.getText()  + "\t; StCall: ラベルに移動");
		o.println("\tSUB\t#"+gomi+ ",R6\t; 引数を削除:");
		o.println(";;; stCall completes");
	}
}