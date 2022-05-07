package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementAssign extends CParseRule {
	// program ::= expression EOF
	private CParseRule primary;
	private CParseRule expression;
	CToken tk;

	public StatementAssign(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-StatementAssign");
		// ここにやってくるときは、必ずisFirst()が満たされている
		primary = new Primary(pcx);
		primary.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_ASSIGN) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString()+"=がありません,補いましたstaA");
		}
		expression = new Expression(pcx);
		expression.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		}else {
			//System.err.println(";がありません parse-staA");
			pcx.warning(tk.toExplainString()+";がありません,補いましたstaA");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;StatementAssign_semantic");
		primary.semanticCheck(pcx);
		expression.semanticCheck(pcx);
		if(!(
				(
						(primary.getCType().getType() == 1 || primary.getCType().getType() == 3 )&&
						(expression.getCType().getType() == 1 || expression.getCType().getType() == 3 )
						)||(
								(primary.getCType().getType() == 2 || primary.getCType().getType() == 4 )&&
								(expression.getCType().getType() == 2 || expression.getCType().getType() == 4) 
								)
				)) {
			pcx.warning(":::stateA"+primary.getCType() +"と"+expression.getCType() +"で型の形が不一致です");
		}
		if(primary.isConstant()) {
			pcx.warning(":::stateA:primaryが定数です staA_sem");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementassign starts");
		primary.codeGen(pcx);
		expression.codeGen(pcx);
		o.println("\tMOV\t-(R6), R0\t; StatementAssign: ２数を取り出して、積む<" + tk.toString() + ">");
		o.println("\tMOV\t-(R6), R1\t; StatementAssign:");
		o.println("\tMOV\tR0, (R1)\t; StatementAssign:");
		o.println(";;;statementassign completes");
	}
}
