package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.Tokenizer;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule	rule;
	private int			lineNo, colNo;
	private char		backCh;
	private boolean		backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1; colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n')  { colNo = 1; ++lineNo; }
		return ch;
	}
	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') { --lineNo; }
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;
	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}
	
	public CToken skipTo(CParseContext pctx,int...num) {
		CToken tk = getCurrentToken(pctx);
		boolean ok = true;
		while(ok) {
			for (int i = 0; i < num.length && ok; i++){
				if(tk.getType() == num[i]||tk.getType() == CToken.TK_EOF) {
					ok = false;
				}
			}
			if(ok) {tk = getNextToken(pctx);}
		}
		return currentTk;
	}
	
	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
//		System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}
	private CToken readToken() {
		CToken tk = null;
		char ch;
		int  startCol = colNo;
		int MAX = 0xffff;
		int MIN = 0;
		StringBuffer text = new StringBuffer();

		int state = 0;
		boolean accept = false;
		while (!accept) {
			switch (state) {
			case 0:					// 初期状態
				ch = readChar();
				if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
				} else if (ch == (char) -1) {	// EOF
					startCol = colNo - 1;
					state = 1;
				} else if (ch >= '1' && ch <= '9') {
					startCol = colNo - 1;
					text.append(ch);
					state = 3;
				} else if (ch == '+') {
					startCol = colNo - 1;
					text.append(ch);
					state = 4;
				} else if (ch == '-') {
					startCol = colNo - 1;
					text.append(ch);
					state = 5;
				} else if (ch == '/') {
					startCol = colNo - 1;
					//text.append(ch);
					state = 6;
				} else if (ch == '0') {
					startCol = colNo - 1;
					text.append(ch);
					state = 10;
				} else if (ch == '&') {
					startCol = colNo - 1;
					text.append(ch);
					state = 13;
				} else if (ch == '*') {
					startCol = colNo - 1;
					text.append(ch);
					state = 14;
				} else if (ch == '(') {
					startCol = colNo - 1;
					text.append(ch);
					state = 16;
				} else if (ch == ')') {
					startCol = colNo - 1;
					text.append(ch);
					state = 17;
				} else if (ch == '[') {
					startCol = colNo - 1;
					text.append(ch);
					state = 18;
				} else if (ch == ']') {
					startCol = colNo - 1;
					text.append(ch);
					state = 19;
				} else if (ch == '_' || ch >= 'a' && ch <= 'z') {
					startCol = colNo - 1;
					text.append(ch);
					state = 20;
				} else if (ch == '=') {
					startCol = colNo - 1;
					text.append(ch);
					state = 21;
				} else if (ch == ';') {
					startCol = colNo - 1;
					text.append(ch);
					state = 22;
				} else if (ch == '<') {
					startCol = colNo - 1;
					text.append(ch);
					state = 23;
				} else if (ch == '>') {
					startCol = colNo - 1;
					text.append(ch);
					state = 24;
				} else if (ch == '!') {
					startCol = colNo - 1;
					text.append(ch);
					state = 25;
				} else if (ch == '{') {
					startCol = colNo - 1;
					text.append(ch);
					state = 26;
				} else if (ch == '}') {
					startCol = colNo - 1;
					text.append(ch);
					state = 27;
				} else if (ch == '|') {
					startCol = colNo - 1;
					text.append(ch);
					state = 28;
				} else if (ch == ',') {
					startCol = colNo - 1;
					text.append(ch);
					state = 29;
				}  else if (ch == '@') {
					startCol = colNo - 1;
					text.append(ch);
					state = 30;
				}else {			// ヘンな文字を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 2;
				}
				break;
			case 1:					// EOFを読んだ
				tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
				accept = true;
				break;
			case 2:					// ヘンな文字を読んだ
				tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 3:					// 数（10進数）の開始
				ch = readChar();
				if (Character.isDigit(ch)) {
					text.append(ch);
				} else {
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					if(MIN <= Integer.decode(text.toString()).intValue() && MAX >= Integer.decode(text.toString()).intValue()) {
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					}else {
						System.err.print("範囲外の数値です");
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					}
					accept = true;
				}
				break;
			case 4:					// +を読んだ
				tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
				accept = true;
				break;
			case 5:					// -を読んだ
				tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
				accept = true;
				break;
			case 6:					// "/"を読んだ
				ch = readChar();
				//System.out.println(ch + " " + "6");
				if (ch == '/') {
					startCol = colNo - 1;
					state = 7;
				}else if (ch == '*') {
					startCol = colNo - 1;
					state = 8;
				}else {
					backChar(ch);
					startCol = colNo - 1;
					state = 15;
				}
				break;
			case 7:					// " // "を読んだ
				ch = readChar();
				//System.out.println(ch + "  7");
				if (ch == '\n' || ch == '\r') {//終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					startCol = colNo - 1;
					state = 0;
				} else if (ch == (char) -1){
					System.err.println("コメント中に終了しました");
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					startCol = colNo - 1;
					state = 1;
					accept = true;
				}else {
					// コメント読む
				}
				break;

			case 8:					// " /* "を読んだ
				ch = readChar();
				//System.out.println(ch + "  8");
				if (ch == '*') {//終わりの可能性
					startCol = colNo - 1;
					state = 9;
				} else if (ch == (char) -1){
					System.err.println("コメント中に終了しました");
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					startCol = colNo - 1;
					state = 1;
					accept = true;
				}else {
					// コメント読む
				}
				break;
			case 9: // " /* "のあとに " * "を読んだ
				ch = readChar();
				//System.out.println(ch + "  9");
				if (ch == '/') {//コメント終わり
					startCol = colNo - 1;
					state = 0;
				}else if(ch == '*') {
					// コメント読む
				}else if (ch == (char) -1){
					System.err.println("コメント中に終了しました");
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					startCol = colNo - 1;
					state = 1;
					accept = true;
				}else {//終わらなかった
					startCol = colNo - 1;
					state = 8;
				}
				break;
			case 10: // " 0 "を読んだ
				ch = readChar();
				if (ch=='x') {
					startCol = colNo - 1;
					text.append(ch);
					state = 11;
				}else if (ch >= '0' && ch <= '9') {
					startCol = colNo - 1;
					text.append(ch);
					state = 12;
				} else {
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					accept = true;
				}
				break;
			case 11:					// 数（16進数）の開始
				ch = readChar();
				if (Character.isDigit(ch) || (ch >= 'a' && ch <= 'f') ) {
					text.append(ch);
				} else {
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					if(text.toString().equals("0x")) {
						System.err.print("0xは不正です");
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						accept=true;
						break;
					}
					if(MIN <= Integer.decode(text.toString()).intValue() && MAX >= Integer.decode(text.toString()).intValue()) {
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					}else {
						System.err.print("範囲外の数値です");
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					}
					accept = true;
				}
				break;
			case 12:					// 数（8進数）の開始
				ch = readChar();
//				System.out.println(ch + "  12");
				if (ch >= '1' && ch <= '7' ) {
					text.append(ch);
				} else {
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					if(MIN <= Integer.decode(text.toString()).intValue() && MAX >= Integer.decode(text.toString()).intValue()) {
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					}else {
						System.err.print("範囲外の数値です");
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					}
					accept = true;
				}
				break;
			case 13:  // "&"を読んだ
				ch = readChar();
				if (ch == '&') {
					tk = new CToken(CToken.TK_AND, lineNo, startCol, "&&");
					accept = true;
					break;
				}else {
					backChar(ch);
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
					accept = true;
				}
				break;
			case 14:  // "*"を読んだ
				tk = new CToken(CToken.TK_MULT, lineNo, startCol, "*");
				accept = true;
				break;
			case 15:  // "/"を読んだ
				text.append("/");
				tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
				accept = true;
				break;
			case 16:  // "("を読んだ
				tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
				accept = true;
				break;
			case 17:  // ")"を読んだ
				tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
				accept = true;
				break;
			case 18:  // "["を読んだ
				tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
				accept = true;
				break;
			case 19:  // "]"を読んだ
				tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
				accept = true;
				break;
			case 20:					// 文字列の開始
				ch = readChar();
				if (ch == '_' || ch >= 'a' && ch <= 'z' || Character.isDigit(ch)) {
					text.append(ch);
				} else if(ch==' '){
					// 文字列の終わり
//					ch = readChar();
//					if(ch == 'i') {
//						ch = readChar();
//						if(ch == 'f') {
//							String s = text.toString();
//							Integer i = (Integer) rule.get(s);
//							// 切り出した字句が登録済みキーワードかどうかはi がnull かどうかで判定する
//							tk = new CToken(CToken.TK_ELSEIF , lineNo, startCol, "elseif");
//							accept = true;
//							break;
//						}
//						backChar(ch);	// 文字列を表さない文字は戻す（読まなかったことにする）
//					}
//					backChar(ch);	// 文字列を表さない文字は戻す（読まなかったことにする）
					// 識別子を切り出す仕事が終わったら
					String s = text.toString();
					Integer i = (Integer) rule.get(s);
					// 切り出した字句が登録済みキーワードかどうかはi がnull かどうかで判定する
					tk = new CToken(((i == null) ? CToken.TK_IDENT : i.intValue()), lineNo, startCol, s);
					accept = true;
				} else {
					// 文字列の終わり
					backChar(ch);	// 文字列を表さない文字は戻す（読まなかったことにする）
					// 識別子を切り出す仕事が終わったら
					String s = text.toString();
					Integer i = (Integer) rule.get(s);
					// 切り出した字句が登録済みキーワードかどうかはi がnull かどうかで判定する
					tk = new CToken(((i == null) ? CToken.TK_IDENT : i.intValue()), lineNo, startCol, s);
					accept = true;
				}
				break;
			case 21:  // "="を読んだ
				ch = readChar();
				if (ch == '=') {
					text.append(ch);
					tk = new CToken(CToken.TK_EQ, lineNo, startCol, "==");
					accept = true;
					break;
				}else {
					backChar(ch);
					tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
					accept = true;
					break;
				}
			case 22:  // ";"を読んだ
				tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
				accept = true;
				break;
			case 23:					// "<"を読んだ
				ch = readChar();
				//System.out.println(ch + " " + "6");
				if (ch == '=') {
					text.append(ch);
					tk = new CToken(CToken.TK_LE, lineNo, startCol, "<=");
					accept = true;
					break;
				}else {
					backChar(ch);
					tk = new CToken(CToken.TK_LT, lineNo, startCol, "<");
					accept = true;
					break;
				}
			case 24:					// ">"を読んだ
				ch = readChar();
				//System.out.println(ch + " " + "6");
				if (ch == '=') {
					text.append(ch);
					tk = new CToken(CToken.TK_GE, lineNo, startCol, ">=");
					accept = true;
					break;
				}else {
					backChar(ch);
					tk = new CToken(CToken.TK_GT, lineNo, startCol, ">");
					accept = true;
					break;
				}
			case 25:					// "!"を読んだ
				ch = readChar();
				//System.out.println(ch + " " + "6");
				if (ch == '=') {
					text.append(ch);
					tk = new CToken(CToken.TK_NE, lineNo, startCol, "!=");
					accept = true;
					break;
				}else {
					backChar(ch);
					tk = new CToken(CToken.TK_NOT, lineNo, startCol, "!");
					accept = true;
					break;
				}
			case 26:  // "{"を読んだ
				tk = new CToken(CToken.TK_LCBRA, lineNo, startCol, "{");
				accept = true;
				break;
			case 27:  // "}"を読んだ
				tk = new CToken(CToken.TK_RCBRA, lineNo, startCol, "}");
				accept = true;
				break;
			case 28:  // "|"を読んだ
				ch = readChar();
				if (ch == '|') {
					tk = new CToken(CToken.TK_OR, lineNo, startCol, "||");
					accept = true;
					break;
				}else {
					backChar(ch);
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				}
			case 29:					// <を読んだ
				tk = new CToken(CToken.TK_COMMA, lineNo, startCol, ",");
				accept = true;
				break;
			case 30:					// @を読んだ
				tk = new CToken(CToken.TK_ATO, lineNo, startCol, "@");
				accept = true;
				break;
			}
		}
		return tk;
	}
}
