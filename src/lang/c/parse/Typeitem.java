package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Typeitem extends CParseRule {
	//private CToken op;
	private CParseRule argitem;
	private List<CParseRule> list = new ArrayList<CParseRule>();
	private CToken name;
	private boolean mult = false;
	private boolean BRA = false;
	public Typeitem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-typeitem");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (tk.getType() == CToken.TK_MULT) {
			mult = true;
			ct.getNextToken(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LBRA) {
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_RBRA) {
				BRA = true;
				ct.getNextToken(pcx);
			}else {
				pcx.warning(tk.toExplainString() +"RBRAがありません,補いました,typeitem");
			}
		}
		if(mult) {
			if(BRA) {
				pcx.sethikisulist(CType.getCType(CType.T_Bpint));
			}else {
				pcx.sethikisulist(CType.getCType(CType.T_pint));
			}
		}else {
			if(BRA) {
				pcx.sethikisulist(CType.getCType(CType.T_Bint));
			}else {
				pcx.sethikisulist(CType.getCType(CType.T_int));
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-typeitem");
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;typeitem starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; typeitemt completes");
	}
}