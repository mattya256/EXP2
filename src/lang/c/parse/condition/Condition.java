package lang.c.parse.condition;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.parse.Expression;

public class Condition extends CParseRule {
	// factor ::= number
	private CParseRule expression;
	private CParseRule condition;
	private boolean bool;
	public Condition(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_TRUE || tk.getType() == CToken.TK_FALSE || Expression.isFirst(tk) );
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-condition");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_TRUE) {
			System.out.println(";;;parse-condition1");
			tk = ct.getNextToken(pcx);
			bool=true;
		}else if(tk.getType() == CToken.TK_FALSE) {
			System.out.println(";;;parse-condition3");
			tk = ct.getNextToken(pcx);
			bool=false;
		}else {
			System.out.println(";;;parse_condition3");
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType()== CToken.TK_LT) {
				condition = new ConditionLT(pcx,expression);
				condition.parse(pcx);
			}else if(tk.getType()== CToken.TK_LE) {
					condition = new ConditionLE(pcx,expression);
					condition.parse(pcx);
			}else if(tk.getType()== CToken.TK_GT) {
				condition = new ConditionGT(pcx,expression);
				condition.parse(pcx);
			}else if(tk.getType()== CToken.TK_GE) {
				condition = new ConditionGE(pcx,expression);
				condition.parse(pcx);
			}else if(tk.getType()== CToken.TK_EQ) {
				condition = new ConditionEQ(pcx,expression);
				condition.parse(pcx);
			}else if(tk.getType()== CToken.TK_NE) {
				condition = new ConditionNE(pcx,expression);
				condition.parse(pcx);
			}else {
				pcx.recoverableError(tk.toExplainString() + "conditionが必要です");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_condition");
		setCType(CType.getCType(CType.T_bool));	
		if(condition != null) {
			System.out.println(";;;semantic_condition1");
			condition.semanticCheck(pcx);
			setConstant(condition.isConstant());	
		} else {
			//pcx.warning(";;;semantic_condition");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;;condition starts");
		if(condition!=null) {
			condition.codeGen(pcx);
		}else if(bool){
			o.println("\tMOV\t#0x0001, (R6)+\t; Condition: set true");
		}else{
			o.println("\tMOV\t#0x0000, (R6)+\t; Condition: set false");
		}
		o.println(";;; condition completes");
	}
}