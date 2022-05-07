package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Declblock extends CParseRule {
	// program ::= expression EOF
	private List<CParseRule> list = new ArrayList<CParseRule>();
	private List<CParseRule> decllist = new ArrayList<CParseRule>();
	private String name;
	private CSymbolTableEntry get;
		
	private int size;
	public Declblock(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LCBRA;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;parse-Declblock");

		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);
		int declcount=0;
//		pcx.setupLocalSymbolTable() ;
		while (Declaration.isFirst(tk) ) {
			try {
				decllist.add(new Declaration(pcx));
				decllist.get(declcount).parse(pcx);
				declcount++;
				tk = ct.getCurrentToken(pcx);
			}catch (RecoverableErrorException e) {
				declcount++;
				System.out.println(":::decblo-Rec1");
				// そのときにはここで例外を捕まえて、’;’ か’}’ が出るまで読み飛ばして回復する（立ち直る）
				ct.skipTo(pcx, CToken.TK_SEMI, CToken.TK_RCBRA);
				tk = ct.getCurrentToken(pcx);
				if (tk.getType() == CToken.TK_SEMI) {
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
		while (Statement.isFirst(tk) ) {
			System.out.println(";;;parse-declblock-"+i);
			try {
				list.add(new Statement(pcx));
				list.get(i).parse(pcx);
				i++;
			}catch (RecoverableErrorException e) {
				i++;
				System.out.println(":::decblo-Rec2");
				// そのときにはここで例外を捕まえて、’;’ か’}’ が出るまで読み飛ばして回復する（立ち直る）
				ct.skipTo(pcx,CToken.TK_SEMI, CToken.TK_RCBRA);
				tk = ct.getCurrentToken(pcx);
			}
			if (tk.getType() == CToken.TK_SEMI) {
				tk = ct.getNextToken(pcx);
			}
			tk = ct.getCurrentToken(pcx);
		}
		size = pcx.getsumsize();
		//pcx.deleteLocalSymbolTable() ;
		if (tk.getType() == CToken.TK_RCBRA) {
			tk = ct.getNextToken(pcx);
		}else {
			pcx.warning(tk.toExplainString()+"}がありません、補いました");
		}
		name = pcx.getkansu();
		get = pcx.search(name);
		if(name == null || get ==null) {
			System.err.println("Retrun文の確認ができません");
		}
		else if(get.getCtype().getType() != CType.T_void && !pcx.getreturn()) {
			pcx.warning(tk.toExplainString()+name+"Return文が設定されていません");
		}
		pcx.returnreset();
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		System.out.println(";;;declblock_semantic");
		for(CParseRule Declaration:decllist) {
			Declaration.semanticCheck(pcx);
		}
		for(CParseRule statement:list) {
			statement.semanticCheck(pcx);
		}
		System.out.println(";;;declblock_semantic-fin");
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; declblock starts");
		//o.println("\tMOV\tR4,(R6)+\t; DeclBlock: フレームポインタ保存");
		//o.println("\tMOV\tR6, R4\t; DeclBlock: フレームポインタ設定");
		o.println("\tADD\t#"+size+", R6\t; DeclBlock: 領域確保");
		// ここには将来、宣言に対するコード生成が必要
		for(CParseRule Declaration:decllist) {
			Declaration.codeGen(pcx);
		}
		for(CParseRule statement:list) {
			statement.codeGen(pcx);
		}
		//o.println("\tMOV\tR4,R6\t; DeclBlock: ");
		//o.println("\tMOV\t-(R6),R4\t; DeclBlock: フレームポインタ復旧");
		o.println(";;; declblock completes");
	}
}
