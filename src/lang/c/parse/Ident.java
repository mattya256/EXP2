package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Ident extends CParseRule {
	// number ::= NUM
	private CToken ident;
	private CSymbolTableEntry nameinfo;
	public Ident(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Ident");
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		//System.out.println(pcx.getcall());
		if(pcx.search(ident.getText())!= null && pcx.search(ident.getText()).getkansu()==true && !pcx.getcall()){
			pcx.recoverableError(tk.toExplainString()+"このidentは関数です.ident");
		}else if(pcx.search(ident.getText())!= null &&pcx.search(ident.getText()).getkansu()==false && pcx.getcall()) {
			pcx.recoverableError(tk.toExplainString()+"このidentは関数ではありません.ident");
		}else if(pcx.search(ident.getText())!= null && pcx.search(ident.getText())!=null) {
			nameinfo = pcx.search(ident.getText());
		}else {
			pcx.recoverableError(tk.toExplainString()+"このidentは登録されていません");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-Ident");
		if(nameinfo!=null) {
			this.setCType(nameinfo.getCtype());
			this.setConstant(nameinfo.getConstant());
		}else if(ident.getText().startsWith("i_")) {
			this.setCType(CType.getCType(CType.T_int));
			this.setConstant(false);
		}else if(ident.getText().startsWith("ip_")) {
			this.setCType(CType.getCType(CType.T_pint));
			this.setConstant(false);
		}else if(ident.getText().startsWith("ia_")) {
			this.setCType(CType.getCType(CType.T_Bint));
			this.setConstant(false);
		}else if(ident.getText().startsWith("ipa_")) {
			this.setCType(CType.getCType(CType.T_Bpint));
			this.setConstant(false);
		}else if(ident.getText().startsWith("c_")) {
			this.setCType(CType.getCType(CType.T_int));
			this.setConstant(true);
		}else {
			pcx.warning("T_err型です"); 
			this.setCType(CType.getCType(CType.T_err));
		}
//		System.out.println(this.getCType().getType());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Ident starts");
		if (ident != null) {
			if(nameinfo.getisGlobal()) {
				o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
						+ ident.toExplainString() + ">");
			}else {
				o.println("\tMOV\tR4, R0\t; Ident: ２数を取り出して、足し、積む<" + ident.toString() + ">");
				o.println("\tMOV\t#"+(nameinfo.getaddr())+", R1\t; Ident:");
				o.println("\tADD\tR1, R0\t; Ident:");
				o.println("\tMOV\tR0, (R6)+\t; Ident: 変数アドレスを積む");
			}
		}
		o.println(";;; Ident completes");
	}
}
