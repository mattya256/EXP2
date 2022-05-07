package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class minusFactor extends CParseRule {
	private CToken op;
	private CParseRule unsignedfactor;
	public minusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;minusFactor");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (UnsignedFactor.isFirst(tk)) {
			unsignedfactor = new UnsignedFactor(pcx);
			unsignedfactor.parse(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "-の後ろはnumberです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedfactor != null) {
			unsignedfactor.semanticCheck(pcx);
			setCType(unsignedfactor.getCType());		// unsignedfactor の型をそのままコピー
			setConstant(unsignedfactor.isConstant());	// unsignedfactor は常に定数
			if(unsignedfactor.getCType().getType()==2 || unsignedfactor.getCType().getType()==4) { //unsignedfactorが番地だった場合
				pcx.warning(op.toExplainString() + "番地に-を付けることはできません");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; minusFactor starts");
		if (unsignedfactor != null) { 
			unsignedfactor.codeGen(pcx);
//			o.println("\tMOV\t#0, (R6)+\t; minusFactor: 0を積み、数字を取り出し、負の値にする<" + op.toString() + ">");
//			o.println("\tMOV\t-(R6), R0\t; minusFactor:");
			o.println("\tMOV\t#0, R0\t; minusFactor: 0から取り出した数字を引き、負の値にする<" + op.toString() + ">");
			o.println("\tMOV\t-(R6), R1\t; minusFactor:");
			o.println("\tSUB\tR1, R0\t; minusFactor:");
			o.println("\tMOV\tR0, (R6)+\t; minusFactor:");
		}else { System.err.println("minusFactor_codeGen_unsignedfactorが空です");
		}
		o.println(";;; minusFactor completes");
		
	}
}