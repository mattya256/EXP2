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

public class Function2 extends CParseRule {
	//private CToken op;
	private CParseRule declblock;
	private CParseRule arglist;
	private List<CParseRule> list = new ArrayList<CParseRule>();
	private CToken num;
	private CToken name;
	private boolean inta = false;
	private boolean mult = false;
	private boolean voida = false;
	public Function2(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_FUNC;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		pcx.resethikisu();
		System.out.println(";;;parse-Function");
		pcx.setupLocalSymbolTable() ;
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType()== CToken.TK_FUNC) {
			tk = ct.getNextToken(pcx);
		}
		if (tk.getType()== CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
			inta = true;
			if (tk.getType()== CToken.TK_MULT) {
				tk = ct.getNextToken(pcx);
				mult = true;
			}
		}else if (tk.getType()== CToken.TK_VOID) {
			tk = ct.getNextToken(pcx);
			voida=true;
		}else {
			pcx.recoverableError(tk.toExplainString() +"intかvoidがありません");
		}

		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_IDENT) {
			name = tk;
			pcx.setkansu(name.getText());
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"IDENTがありません");
		}
		
		
		if (tk.getType()== CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"LPARがありません,補いました");
		}
		if(Arglist.isFirst(tk)) {
			arglist = new Arglist(pcx);
			arglist.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType()== CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString() +"RPARがありません,補いました,func");
		}
		
		CSymbolTableEntry get;
		get = pcx.search(name.getText());
		if(get==null) {
			CSymbolTableEntry entry;
			if(voida) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_void),1,false,pcx.checkglobal(),pcx.getsumsize(),true,pcx.gethikisu());
			}
			else if(num==null) {
				if(mult) {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_pint),1,false,pcx.checkglobal(),pcx.getsumsize(),true,pcx.gethikisu());
				}else {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_int),1,false,pcx.checkglobal(),pcx.getsumsize(),true,pcx.gethikisu());
				}

			}else {
				if(mult) {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_Bpint),num.getIntValue(),false,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
				}else {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_Bint),num.getIntValue(),false,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
				}
			}
			pcx.set(name.getText(),entry);
			pcx.show();
			pcx.setkansulist(name.getText());
		}else {
			if(get.getkansu()) {
				if(get.getCtype().getType()==CType.T_int && inta && !mult ) {
				}else if(get.getCtype().getType()==CType.T_pint && inta && mult ){
				}else if(get.getCtype().getType()==CType.T_void && voida ) {
				}else {
					pcx.warning(tk.toExplainString()+"宣言と定義の型が違います");
				}
			}else {
			pcx.warning(tk.toExplainString()+"これは関数として登録されていません,function");
			}
		}
		
		
		if(Declblock.isFirst(tk)) {
			declblock = new Declblock(pcx);
			declblock.parse(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() + "Conditionがありません,func");
		}
		
//		CSymbolTableEntry get;
//		get = pcx.search(name.getText());
//		if(get==null) {
//			pcx.recoverableError("このidentは登録されていません,function");
//		}else {
//			if(get.getkansu()) {
//				if(get.getCtype().getType()==CType.T_int && inta && !mult ) {
//				}else if(get.getCtype().getType()==CType.T_pint && inta && mult ){
//				}else if(get.getCtype().getType()==CType.T_void && voida ) {
//				}else {
//					pcx.warning(tk.toExplainString()+"宣言と定義の型が違います");
//				}
//			}else {
//			pcx.warning(tk.toExplainString()+"これは関数として登録されていません,function");
//			}
//		}
		get = pcx.search(name.getText());
		for(int i=0;i<100;i++) {
			if(get.gethikisu()[i] != pcx.gethikisu()[i]) {
				pcx.fatalError(tk.toExplainString()+";"+get.gethikisu()[i]+","+pcx.gethikisu()[i]+","+"後ろから"+(i+1)+"番目の引数が違います,Func");
			}
		}
		pcx.resetkansu();
		pcx.kansuset(name.getText());
		pcx.kyokusyokansuset(name.getText());
		pcx.kyokusyokansurenotuse();
		pcx.deleteLocalSymbolTable() ;
		pcx.resethikisu();
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-Function");
		if(arglist!=null) {
			arglist.semanticCheck(pcx);
		}
		declblock.semanticCheck(pcx);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;Functionl starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(name.getText() + ":");
		o.println("\tMOV\tR4,(R6)+\t; Function: フレームポインタ保存");
		o.println("\tMOV\tR6, R4\t; Function: フレームポインタ設定");
		//int seq = pcx.getSeqId();
		if(arglist!=null) {
			arglist.codeGen(pcx);
		}
		declblock.codeGen(pcx);
		o.println(name.getText() + "_RET:");
		o.println("\tMOV\tR4, R6\t; Function:局所変数領域を捨てる");
		o.println("\tMOV\t-(R6),R4\t; Function: フレームポインタ復旧");
		o.println("\tRET\t; Function: RET命令");
		o.println(";;; Function completes");
	}
}