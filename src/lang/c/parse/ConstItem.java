package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ConstItem extends CParseRule {
	//private CToken op;
	private CSymbolTable table;
	//private CSymbolTableEntry entry;
	private CParseRule ident;
	private CParseRule array;
	private CToken name;
	private CToken num;
	private CType[] hikisu;
	private boolean mult=false;
	private boolean amp=false;
	private boolean global=false;
	private CSymbolTableEntry entry,get;
	public ConstItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk) || tk.getType() == CToken.TK_MULT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		global = pcx.checkglobal();
		System.out.println(";;;parse-ConstItem");
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
		if(tk.getType()== CToken.TK_ASSIGN) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString()+"＝がありません");
		}
		if (tk.getType()== CToken.TK_AMP) {
			tk = ct.getNextToken(pcx);
			amp = true;
		}
		if (tk.getType()== CToken.TK_NUM) {
			tk = ct.getCurrentToken(pcx);
			num = tk;
			tk = ct.getNextToken(pcx);
		}else {
			pcx.recoverableError(tk.toExplainString() +"numがありません");
		}
		get = pcx.search(name.getText());
		if(get==null) {
			if(mult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint),1,true,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
			}else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int),1,true,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
			}
		pcx.set(name.getText(),entry);
		pcx.show();
		}else {
			if(get.getisGlobal()&&!pcx.checkglobal()) {
				System.out.println(tk.toExplainString()+"nameは大域変数として登録されています");
				if(mult) {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_pint),1,true,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
				}else {
					entry = new CSymbolTableEntry(CType.getCType(CType.T_int),1,true,pcx.checkglobal(),pcx.getsumsize(),false,pcx.gethikisu());
				}
				pcx.set(name.getText(),entry);
				pcx.show();
			}else {
				pcx.warning(tk.toExplainString()+"nameは使用済みです");
			}
		}
		System.out.println(pcx.search(name.getText()));
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-ConstItem");
		this.setConstant(true);
		if(amp == true && mult == true) {
			this.setCType(CType.getCType(CType.T_pint));
		}else if(amp == false && mult == false) {
			this.setCType(CType.getCType(CType.T_int));
		}else {
			this.setCType(CType.getCType(CType.T_int));
			pcx.warning("型の形が不一致です_constitem");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;ConstItem starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		if(global) {
			o.println(name.getText()+":\t.WORD\t"+num.getIntValue()+"\t; ConstItem: 領域確保");
		}else {
			o.println("\tMOV\tR4, R0\t; ConstItem: ２数を取り出して、足し、積む<" + name.toString() + ">");
			o.println("\tMOV\t#"+(entry.getaddr())+", R1\t; ConstItem:");
			o.println("\tADD\tR1, R0\t; ConstItem:");
			o.println("\tMOV\tR0, (R6)+\t; ConstItem: 変数アドレスを積む");
			o.println("\tMOV\t"+num.getIntValue()+", (R6)+\t; ConstItem: 数を積む");
			o.println("\tMOV\t-(R6), R0\t; ConstItem: ２数を取り出して、積む");
			o.println("\tMOV\t-(R6), R1\t; ConstItem:");
			o.println("\tMOV\tR0, (R1)\t; ConstItem:");
			
		}
		o.println(";;; ConstItem completes");
	}
}