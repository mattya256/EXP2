package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Statement extends CParseRule {
	// program ::= expression EOF
	private CParseRule statement;

	public Statement(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk) || IF.isFirst(tk) || WHILE.isFirst(tk) 
				|| Input.isFirst(tk)||Output.isFirst(tk) || StatementCall.isFirst(tk) || StatementReturn.isFirst(tk) ;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Statement");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(IF.isFirst(tk)) {
			System.out.println(";;;parse-Statement1");
			statement = new IF(pcx);
			statement.parse(pcx);
		}else if(WHILE.isFirst(tk)) {
			System.out.println(";;;parse-Statement2");
			statement = new WHILE(pcx);
			statement.parse(pcx);
		}else if(Input.isFirst(tk)) {
			System.out.println(";;;parse-Statement3");
			statement = new Input(pcx);
			statement.parse(pcx);
		}else if(Output.isFirst(tk)) {
			System.out.println(";;;parse-Statement4");
			statement = new Output(pcx);
			statement.parse(pcx);
		}else if(StatementAssign.isFirst(tk)){
			System.out.println(";;;parse-Statement5");
			statement = new StatementAssign(pcx);
			statement.parse(pcx);
		}else if(StatementCall.isFirst(tk)){
			System.out.println(";;;parse-Statement6");
			statement = new StatementCall(pcx);
			statement.parse(pcx);
		}else if(StatementReturn.isFirst(tk)){
			System.out.println(";;;parse-Statement7");
			statement = new StatementReturn(pcx);
			statement.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "存在しないstatementです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;Statement_semantic");
		statement.semanticCheck(pcx);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement starts");
		statement.codeGen(pcx);
		o.println(";;; statement completes");
	}
}
