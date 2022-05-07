package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Declaration extends CParseRule {
	//private CToken op;
	private CParseRule decl;
	public Declaration(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk) || VoidDecl.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Declaration");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(IntDecl.isFirst(tk)) {
			decl = new IntDecl(pcx);
			decl.parse(pcx);
		}else if(ConstDecl.isFirst(tk)){
			decl = new ConstDecl(pcx);
			decl.parse(pcx);
		}else if(VoidDecl.isFirst(tk)){
			decl = new VoidDecl(pcx);
			decl.parse(pcx);
		}else {
			pcx.warning(tk.toExplainString());
		}
			
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-Declaration");
		decl.semanticCheck(pcx);

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;Declaration starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		decl.codeGen(pcx);
		o.println(";;; ConstDecl complete");
	}
}