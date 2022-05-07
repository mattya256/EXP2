package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Primary extends CParseRule {
	// factor ::= number
	private CParseRule variable;
	private CParseRule primarymult;
	public Primary(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (primaryMult.isFirst(tk) || Variable.isFirst(tk) );
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Primary");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(primaryMult.isFirst(tk) ) {
			System.out.println(";;;parse-Primary1");
			primarymult = new primaryMult(pcx);
			primarymult.parse(pcx);
			setisprimaryMult(true);
		}else if(Variable.isFirst(tk) ) {
			System.out.println(";;;parse-Primary2");
			variable = new Variable(pcx);
			variable.parse(pcx);
		}else {
			pcx.recoverableError("Primary_parse_err");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_Primary");
		if(primarymult != null) {
			System.out.println(";;;semantic_Primary1");
			primarymult.semanticCheck(pcx);
			setCType(primarymult.getCType());		
			setConstant(primarymult.isConstant());	
		}else if (variable != null) {
			System.out.println(";;;semantic_Primary2");
			variable.semanticCheck(pcx);
			setCType(variable.getCType());		
			setConstant(variable.isConstant());	
		} else {
			pcx.recoverableError(";;;semantic_Primary");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;;primary starts");
		if (primarymult != null) { primarymult.codeGen(pcx); }
		else if(variable!=null){variable.codeGen(pcx);}
		o.println(";;; primary completes");
	}
}