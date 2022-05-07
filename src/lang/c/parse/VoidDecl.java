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

public class VoidDecl extends CParseRule {
	//private CToken op;
	private CParseRule declitem;
	private List<CToken> namelist = new ArrayList<CToken>();
	private List<CParseRule> typelist = new ArrayList<CParseRule>();
	private CToken num;
	private String name;
	private CType[] hikisu;
	private int count = 0;
	public VoidDecl(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_VOID;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-voidDecl");
//		if(!pcx.checkglobal()) {
//			pcx.fatalError("voidは局所変数内では定義できません");
//		}

		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_VOID) {
			tk = ct.getNextToken(pcx);
		}
		if(tk.getType()== CToken.TK_IDENT) {
			name = tk.getText();
			namelist.add(tk);
			count++;
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"IDENTがありません,voidde");
		}
		if(tk.getType()== CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"LPARがありません,補いました,voidde");
		}
		int typecount=0;
		if(Typelist.isFirst(tk)) {
			typelist.add(new Typelist(pcx));
			typelist.get(typecount).parse(pcx);
			typecount++;
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"RPARがありません,補いました,voidde");
		}
		
		CSymbolTableEntry entry;
		entry = new CSymbolTableEntry(CType.getCType(CType.T_void),1,false,pcx.checkglobal(),pcx.getsumsize(),true,pcx.gethikisu());
		CSymbolTableEntry get;
		get = pcx.search(name);
		if(get==null) {
			pcx.set(name,entry);
			pcx.show();
		}else {
			if(get.getisGlobal()&&!pcx.checkglobal()) {
				System.out.println(tk.toExplainString()+"nameは大域変数として登録されています,voidde");
				pcx.set(name,entry);
				pcx.show();
			}else {
				pcx.warning(tk.toExplainString()+"nameは使用済みです,voidde");
			}
		}
		if(pcx.checkglobal()) {
			pcx.setkansulist(name);
		}else {
			pcx.kyokusyosetkansulist(name);
		}
		pcx.resethikisu();
		
		int count =0;
		while (tk.getType()== CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if(tk.getType()== CToken.TK_IDENT) {
				name = tk.getText();
				namelist.add(tk);
				count++;
				tk = ct.getNextToken(pcx);
			}else {
				pcx.recoverableError(tk.toExplainString() +"IDNETがありません,void,de");
			}		
			if(tk.getType()== CToken.TK_LPAR) {
				tk = ct.getNextToken(pcx);
			}else {
				pcx.warning(tk.toExplainString() +"LPARがありません,補いました,voidde");
			}
			if(Typelist.isFirst(tk)) {
				typelist.add(new Typelist(pcx));
				typelist.get(typecount).parse(pcx);
				typecount++;
			}
			tk = ct.getCurrentToken(pcx);
			if(tk.getType()== CToken.TK_RPAR) {
				tk = ct.getNextToken(pcx);
			}else {
				pcx.warning(tk.toExplainString() +"RPARがありません,補いました,voidde");
			}
			
			entry = new CSymbolTableEntry(CType.getCType(CType.T_void),1,false,pcx.checkglobal(),pcx.getsumsize(),true,pcx.gethikisu());
			get = pcx.search(name);
			if(get==null) {
				pcx.set(name,entry);
				pcx.show();
			}else {
				if(get.getisGlobal()&&!pcx.checkglobal()) {
					System.out.println(tk.toExplainString()+"nameは大域変数として登録されています,voidde");
					pcx.set(name,entry);
					pcx.show();
				}else {
					pcx.warning(tk.toExplainString()+"nameは使用済みです,voidde");
				}
			}
			if(pcx.checkglobal()) {
				pcx.setkansulist(name);
			}else {
				pcx.kyokusyosetkansulist(name);
			}
			pcx.resethikisu();
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"SEMIがありません,voidde");
		}
			
		pcx.resethikisu();
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-voidDecl");
		for(CParseRule TypeList:typelist) {
			TypeList.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;VOIDDecl starts");
		PrintStream o = pcx.getIOContext().getOutStream();

		o.println(";;; VOIDDecl completes");
	}
}