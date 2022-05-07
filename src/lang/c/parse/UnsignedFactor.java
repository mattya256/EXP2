package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class UnsignedFactor extends CParseRule {
	// factor ::= number
	private CParseRule number;
	private CParseRule factorAmp;
	private CParseRule expression;
	private CParseRule addresstovalue;
	private CParseRule stCall;
	public UnsignedFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return (Number.isFirst(tk) || FactorAmp.isFirst(tk) 
				|| tk.getType() == CToken.TK_LPAR  || addressToValue.isFirst(tk))||StatementCall2.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-UnsignedFactor");
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Number.isFirst(tk) ) {
			System.out.println(";;;parse-unsignedFactor2");
			number = new Number(pcx);
			number.parse(pcx);
		}else if(FactorAmp.isFirst(tk)){
			System.out.println(";;;parse-unsignedFactor1");
			factorAmp = new FactorAmp(pcx);
			factorAmp.parse(pcx);
		}else if(tk.getType() == CToken.TK_LPAR){
			System.out.println(";;;parse-unsignedFactor3");
			// (の次の字句を読む
			tk = ct.getNextToken(pcx);
			if (Expression.isFirst(tk)) {
				expression = new Expression(pcx);
				expression.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if(tk.getType()== CToken.TK_RPAR) {
					tk = ct.getNextToken(pcx);
				}else {
					pcx.warning(tk.toExplainString() + "')'がありません,補いました");
				}
			} else {
				pcx.recoverableError(tk.toExplainString() + "+の後ろはnumberです");
			}
		}else if(addressToValue.isFirst(tk)){
			System.out.println(";;;parse-unsigned-Factor4");
			addresstovalue = new addressToValue(pcx);
			addresstovalue.parse(pcx);
		}else if(StatementCall2.isFirst(tk)){
			System.out.println(";;;parse-unsigned-Factor5");
			stCall = new StatementCall2(pcx);
			stCall.parse(pcx);
		}else{
			pcx.warning("unsignedFactor_parse_err");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;semantic_Unsigned");
		if (number != null) {
			System.out.println(";;;semantic_Unsigned1");
			number.semanticCheck(pcx);
			setCType(number.getCType());		// number の型をそのままコピー
			setConstant(number.isConstant());	// number は常に定数
		}else if(factorAmp != null){
			System.out.println(";;;semantic_Unsigned2");
			factorAmp.semanticCheck(pcx);
			setCType(factorAmp.getCType());		// factorAmp の型をそのままコピー
			setConstant(factorAmp.isConstant());	// factorAmp は常に定数
		}else if(expression!=null){
			System.out.println(";;;semantic_Unsigned3");
			expression.semanticCheck(pcx);
			setCType(expression.getCType());		
			setConstant(expression.isConstant());	
		}else if(addresstovalue!=null){
			System.out.println(";;;semantic_Unsigned4");
			addresstovalue.semanticCheck(pcx);
			setCType(addresstovalue.getCType());		
			setConstant(addresstovalue.isConstant());	
		}else if(stCall!=null){
			System.out.println(";;;semantic_Unsigned4");
			stCall.semanticCheck(pcx);
			setCType(stCall.getCType());		
			setConstant(stCall.isConstant());	
		}else{
			System.err.println("unsignedFactor_semantic_err");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsignedfactor starts");
		if (number != null) { number.codeGen(pcx); }
		else if(factorAmp!=null){factorAmp.codeGen(pcx);}
		else if(expression != null) {expression.codeGen(pcx);}
		else if(addresstovalue != null) {addresstovalue.codeGen(pcx);}
		else if(stCall != null) {stCall.codeGen(pcx);}
		o.println(";;; unsigendfactor completes");
	}
}