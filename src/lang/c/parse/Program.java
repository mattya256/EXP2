package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Program extends CParseRule {
	// program ::= expression EOF
	private List<CParseRule> list = new ArrayList<CParseRule>();
	private List<CParseRule> decllist = new ArrayList<CParseRule>();
		
	public Program(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return  Function2.isFirst(tk) || Declaration.isFirst(tk) || tk.getType() == CToken.TK_EOF;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Program");

		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		int declcount=0;
		while (Declaration.isFirst(tk) ) {
			try {
				decllist.add(new Declaration(pcx));
				decllist.get(declcount).parse(pcx);
				declcount++;
				tk = ct.getCurrentToken(pcx);
			}catch (RecoverableErrorException e) {
				declcount++;
				System.out.println(":::pro-Rec1");
				// そのときにはここで例外を捕まえて、’;’ か’}’ が出るまで読み飛ばして回復する（立ち直る）
				ct.skipTo(pcx, CToken.TK_SEMI, CToken.TK_RCBRA);
				tk = ct.getNextToken(pcx);
				if (tk.getType() == CToken.TK_RCBRA) {
					tk = ct.getNextToken(pcx);
				} 
//				while(!Statement.isFirst(tk) && tk.getType() != CToken.TK_EOF){
//					ct.skipTo(pcx, CToken.TK_RCBRA);
//					tk = ct.getNextToken(pcx);
//					if (tk.getType() == CToken.TK_RCBRA) {
//						tk = ct.getNextToken(pcx);
//					} 
//				}
			}
		}
		int i=0;
		while (Function2.isFirst(tk) ) {
			System.out.println(";;;parse-Program-"+i);
			try {
				list.add(new Function2(pcx));
				list.get(i).parse(pcx);
				i++;
			}catch (RecoverableErrorException e) {
				i++;
				System.out.println(":::pro-Rec2");
				// そのときにはここで例外を捕まえて、’;’ か’}’ が出るまで読み飛ばして回復する（立ち直る）
				ct.skipTo(pcx,CToken.TK_LCBRA);
				tk = ct.getCurrentToken(pcx);
			}
			tk = ct.getCurrentToken(pcx);
		}
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
		if(!pcx.checkkansuset() || !pcx.kyokusyocheckkansuset()) {
			pcx.warning("宣言されたが定義されていない関数があります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;Program_semantic");
		for(CParseRule Declaration:decllist) {
			Declaration.semanticCheck(pcx);
		}
		for(CParseRule function:list) {
			function.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		// ここには将来、宣言に対するコード生成が必要
		o.println("__START:");
		o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
		o.println("\tJMP\tmain\t; ProgramNode: 最初の実行文へ");

		o.println("\tHLT\t\t\t; ProgramNode:");
		o.println(";;; program completes");
		for(CParseRule Declaration:decllist) {
			Declaration.codeGen(pcx);
		}
		for(CParseRule function:list) {
			function.codeGen(pcx);
		}
		o.println("\t.END\t\t\t; ProgramNode:");
	}
}
