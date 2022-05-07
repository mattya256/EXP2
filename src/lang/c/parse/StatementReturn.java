package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class StatementReturn extends CParseRule {
	//private CToken op;
	private CParseRule expression;
	private CToken num;
	private boolean mult = false;
	private String name;
	private CSymbolTableEntry get;
	public StatementReturn(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_RETURN;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-stRe");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		name = pcx.getkansu();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_RETURN) {
			tk = ct.getNextToken(pcx);
		}
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() + "SEMIがありません");
		}
		pcx.returnset();
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-stRE");
		get = pcx.search(name);
		if(expression!=null) {
			expression.semanticCheck(pcx);
			this.setCType(expression.getCType());		// expression の型をそのままコピー
			this.setConstant(expression.isConstant());
			if(this.getCType().getType()!=get.getCtype().getType()) {
				pcx.warning(this.getCType()+"と"+get.getCtype()+"　で型が不一致です,ret");
			}
		}else {
			if(get.getCtype().getType() != CType.T_void) {
				pcx.warning("void" +"と"+get.getCtype()+"　で型が不一致です,ret");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;stRE starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		if(expression!=null) {
			expression.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; Returnする値をR0に移動:");
		}
		o.println("\tJMP\t" + name  + "_RET"+"\t; StReturn: ラベルに移動");
		o.println(";;; stRE completes");
	}
}