package lang.c.parse.condition;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.parse.Expression;

class ConditionLT extends CParseRule {
	private CParseRule left, right;
	private int seq;
	private CToken op;
	
	public ConditionLT(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-conditionLT");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getNextToken(pcx);
		if(Expression.isFirst(op)) {
			right = new Expression(pcx);
			right.parse(pcx);
		}else {
			pcx.recoverableError("Expressionがありません");
		}
	}
	
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_conditionLT");
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "] と右辺の型["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException{
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition < (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionLT: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionLT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLT: set true");
			o.println("\tCMP\tR0, R1\t; ConditionLT: R1<R0 = R1-R0<0");
			o.println("\tBRN\tLT" + seq + "\t; ConditionLT");
			o.println("\tCLR\tR2\t\t; ConditionLT: set false");
			o.println("LT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLT:");
		}
		o.println(";;;condition < (compare) completes");
	}
}
