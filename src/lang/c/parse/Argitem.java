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

public class Argitem extends CParseRule {
	//private CToken op;
	private CParseRule argitem;
	private List<CParseRule> list = new ArrayList<CParseRule>();
	private CToken name;
	private boolean mult = false;
	private boolean BRA = false;
	private CType[] hikisu;
	public Argitem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Argitem");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (tk.getType() == CToken.TK_MULT) {
			mult = true;
			tk = ct.getNextToken(pcx);
		}
		if(tk.getType()== CToken.TK_IDENT) {
			name=tk;
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"IDENTがありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LBRA) {
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_RBRA) {
				tk = ct.getNextToken(pcx);
				BRA = true;
			}else {
				pcx.warning(tk.toExplainString() +"RBRAがありません,補いました");
			}
		}

		CSymbolTableEntry entry;
		if(mult) {
			if(BRA) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_Bpint),1,false,pcx.checkglobal(),pcx.gethikisusize(),false,pcx.gethikisu());
			}else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint),1,false,pcx.checkglobal(),pcx.gethikisusize(),false,pcx.gethikisu());
			}
		}else {
			if(BRA) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_Bint),1,false,pcx.checkglobal(),pcx.gethikisusize(),false,pcx.gethikisu());
			}else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int),1,false,pcx.checkglobal(),pcx.gethikisusize(),false,pcx.gethikisu());
			}
		}
		CSymbolTableEntry get;
		get = pcx.search(name.getText());
		if(get==null) {
			pcx.hikisuset(name.getText(),entry);
			pcx.show();
		}else {
			if(get.getisGlobal()&&!pcx.checkglobal()) {
				System.out.println(tk.toExplainString()+"nameは大域変数として登録されています");
				pcx.hikisuset(name.getText(),entry);
				pcx.show();
			}else {
			pcx.warning(tk.toExplainString()+"nameは使用済みです");
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
		System.out.println(";;;semantic-Argitem");
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;Argitem starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Argitemt completes");
	}
}