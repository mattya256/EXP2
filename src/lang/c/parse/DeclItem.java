package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class DeclItem extends CParseRule {
	//private CToken op;
	private CParseRule ident;
	private CParseRule array;
	private CParseRule typelist;
	private CToken num;
	private CToken name;
	private CType[] hikisu;
	private boolean mult=false;
	private boolean amp=false;
	private boolean global=false;
	private boolean kansu=false;
	public DeclItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk) || tk.getType() == CToken.TK_MULT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		global = pcx.checkglobal();
		System.out.println(";;;parse-DeclItem");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_MULT) {
			tk = ct.getNextToken(pcx);
			mult = true;
		}
		if(Ident.isFirst(tk)) {
			name = tk;
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"numがありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_LBRA) {
			tk = ct.getNextToken(pcx);
			if (tk.getType()== CToken.TK_NUM) {
				tk = ct.getCurrentToken(pcx);
				num = tk;
				tk = ct.getNextToken(pcx);
			}else {
				pcx.recoverableError(tk.toExplainString() +"numがありません,decitem");
			}
			if (tk.getType()== CToken.TK_RBRA) {
				tk = ct.getNextToken(pcx);
			}else {
				pcx.warning(tk.toExplainString() +"RBRAがありません,declitem");
			}
		}else if (tk.getType()== CToken.TK_LPAR) {
			kansu = true;
			tk = ct.getNextToken(pcx);
			if(Typelist.isFirst(tk)) {
				typelist = new Typelist(pcx);
				typelist.parse(pcx);
				tk = ct.getCurrentToken(pcx);
			}
			if (tk.getType()== CToken.TK_RPAR) {
				tk = ct.getNextToken(pcx);
			}else {
				pcx.warning(tk.toExplainString() +"RPARがありません、補いました,decitem");
			}
		}
		CSymbolTableEntry entry;
		
		if(num==null) {
			if(kansu) {
				pcx.setkansulist(name.getText());
				if(mult) {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_pint),1,false,pcx.checkglobal(),pcx.getsumsize(),true,pcx.gethikisu());
				}else {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_int),1,false,pcx.checkglobal(),pcx.getsumsize(),true,pcx.gethikisu());
				}
			}else {
				if(mult) {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_pint),1,false,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
				}else {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_int),1,false,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
				}
			}
		}else {
			if(mult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_Bpint),num.getIntValue(),false,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
			}else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_Bint),num.getIntValue(),false,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
			}
		}
		CSymbolTableEntry get;
		get = pcx.search(name.getText());
		if(get==null) {
			pcx.set(name.getText(),entry);
			pcx.show();
		}else {
			if(get.getisGlobal()&&!pcx.checkglobal()) {
				System.out.println(tk.toExplainString()+"nameは大域変数として登録されています");
				pcx.set(name.getText(),entry);
				pcx.show();
			}else {
			pcx.warning(tk.toExplainString()+"nameは使用済みです");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-DeclItem");
		this.setConstant(true);
		if(mult == true) {
			this.setCType(CType.getCType(CType.T_pint));
		}else if(mult == false) {
			this.setCType(CType.getCType(CType.T_int));
		}
		if(typelist!=null) {
			typelist.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;DeclItem starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		if(kansu) {
			;
		}
		else if(global) {
			if(num==null){
				o.println(name.getText()+":\t.WORD\t0\t; DeclItem: 領域確保");
			}else {
				o.println(name.getText()+":\t.BLKW\t"+num.getIntValue()+"\t; DeclItem: 領域確保");
			}
		}
		o.println(";;; DeclItem completes");
	}
}