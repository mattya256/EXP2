package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Variable extends CParseRule {
	//private CToken op;
	private CParseRule ident;
	private CParseRule array,call;
	CToken tk;
	public Variable(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Variable");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		tk = ct.getCurrentToken(pcx);
		ident = new Ident(pcx);
		ident.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if (Array.isFirst(tk)) {
			array = new Array(pcx);
			array.parse(pcx); 
			tk = ct.getCurrentToken(pcx);
		}else if(Call.isFirst(tk)) {
			call = new Call(pcx);
			call.parse(pcx); 
			tk = ct.getCurrentToken(pcx);
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic-Variable");
		ident.semanticCheck(pcx);			
		if(array != null &&(ident.getCType().getType()==CType.T_int || ident.getCType().getType()==CType.T_pint)) {
			pcx.warning("配列の場合のidentはint[]型またはint*[]型です,vari"+tk.toExplainString());
		}
		if(array == null &&(ident.getCType().getType()==CType.T_Bint || ident.getCType().getType()==CType.T_Bpint) && pcx.gethairetu()==0) {
			pcx.warning("型がint[]またはint*[]ですが[]がありません");
	    }
		else if(array == null &&(ident.getCType().getType()==CType.T_Bint || ident.getCType().getType()==CType.T_Bpint) && pcx.gethairetu()==1) {
			pcx.hairetu();
	    }
		setCType(ident.getCType());		
		setConstant(ident.isConstant());
		if (array != null) {
			array.semanticCheck(pcx);
			if(array.getCType().getType()!=CType.T_int && array.getCType().getType()!=CType.T_Bint) {
				pcx.warning("[]の中はint型です;;;sem-vari");
			}else {	
				if(ident.getCType().getType()==CType.T_Bint) {
					this.setCType(CType.getCType(CType.T_int));
				}else if(ident.getCType().getType()==CType.T_Bpint){
					this.setCType(CType.getCType(CType.T_pint));
				}else {
					System.err.println("?,Vari");
				}
			}
		}
		if (call != null) {
			call.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;variable starts");
		PrintStream o = pcx.getIOContext().getOutStream();
		if (ident != null) { ident.codeGen(pcx); }
		if (array != null) { array.codeGen(pcx); }
		o.println(";;; variable completes");
	}
}