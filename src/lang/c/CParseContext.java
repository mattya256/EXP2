package lang.c;

import lang.IOContext;
import lang.ParseContext;

public class CParseContext extends ParseContext {
	public CParseContext(IOContext ioCtx,  CTokenizer tknz) {
		super(ioCtx, tknz);
	}

	@Override
	public CTokenizer getTokenizer()		{ return (CTokenizer) super.getTokenizer(); }

	private int seqNo = 0;
	public int getSeqId() { return ++seqNo; }
	private CSymbolTable table = new CSymbolTable();
	
	public CSymbolTableEntry set(String name, CSymbolTableEntry e) {
		return table.set(name,e);
	}
	
	public CSymbolTableEntry hikisuset(String name, CSymbolTableEntry e) {
		return table.hikisuuset(name,e);
	}
	
	public void show() {
		table.show();
	}
	
	public void setupLocalSymbolTable() {
		table.setupLocalSymbolTable();
	}
	
	public void deleteLocalSymbolTable() {
		table.deleteLocalSymbolTable();
	}
	
	public CSymbolTableEntry search(String str) {
		return table.search(str);
	}
	
	public boolean checkglobal() {
		return table.checkglobal();
	}
	
	public int getsumsize() {
		return table.getsumsize();
	}
	
	public int gethikisusize() {
		return table.gethikisusize();
	}
	
	private String kansu = null;
	public void setkansu(String str) {
		kansu = str;
	}
	
	public String getkansu() {
		return kansu;
	}
	
	public void resetkansu() {
		kansu = null;
	}
	
	private boolean callnow=false;
	
	public void call() {
		callnow = true;
	}
	
	public void callcancel() {
		callnow = false;
	}
	
	public boolean getcall() {
		return callnow;
	}
	
	private boolean returnok=false;
	
	public void returnset() {
		this.returnok = true;
	}
	
	public void returnreset() {
		this.returnok = false;
	}
	
	public boolean getreturn() {
		return this.returnok;
	}
	
	private String[] kansulist = new String[1000];
	private boolean[] kansuset = new boolean[1000];
	private int kansunum = 0;
	
	public void setkansulist(String str) {
		kansulist[kansunum]=str;
		kansuset[kansunum]=false;
		kansunum++;
	}
	
	public void kansuset(String str) {
		for(int i=0;i<kansunum;i++) {
			if(kansulist[i].equals(str)) {
				kansuset[i]=true;
			}
		}
	}
	
	public boolean checkkansuset() {
		boolean check = true;
		for(int i=0;i<kansunum;i++) {
			if(!kansuset[i]) {
				check = false;
				System.err.println(kansulist[i]+"が登録されていません");
			}
		}
		return check;
	}
	
	private String[] kyokusyokansulist = new String[1000];
	private boolean[] kyokusyokansuset = new boolean[1000];
	private boolean[] kyokusyokansucanuse = new boolean[1000];
	private int kyokusyokansunum = 0;
	
	public void kyokusyosetkansulist(String str) {
		kyokusyokansulist[kyokusyokansunum]=str;
		kyokusyokansuset[kyokusyokansunum]=false;
		kyokusyokansucanuse[kyokusyokansunum]=true;
		kyokusyokansunum++;
	}
	
	public void kyokusyokansuset(String str) {
		for(int i=0;i<kyokusyokansunum;i++) {
			if(kyokusyokansulist[i].equals(str)) {
				kyokusyokansuset[i]=true;
			}
		}
	}
	
	public void kyokusyokansurenotuse() {
		for(int i=0;i<kansunum;i++) {
			kyokusyokansucanuse[i]=false;
		}
	}
	
	public boolean kyokusyokansuusecheck(String str) {
		boolean check = true;
		for(int i=0;i<kyokusyokansunum;i++) {
			if(kyokusyokansulist[i].equals(str) && !kyokusyokansucanuse[i]) {
				System.err.println(kyokusyokansulist[i]+"が使用されようとしています");
				check = false;
			}
		}
		return check;
	}
	
	public boolean kyokusyocheckkansuset() {
		boolean check = true;
		for(int i=0;i<kyokusyokansunum;i++) {
			if(!kyokusyokansuset[i]) {
				check = false;
				System.err.println(kyokusyokansulist[i]+"が登録されていません(局所関数)");
			}
		}
		return check;
	}
	
	private CType[] hikisulist = new CType[100];
	private int hikisucount = 0;
	public void sethikisulist(CType c) {
		hikisulist[hikisucount] = c;
		hikisucount++;
	}
	
	public CType[] gethikisu() {
		CType[] list = new CType[100];
		int count = 0;
		for(int i = hikisucount-1;i>=0;i--) {
			list[count] = hikisulist[i];
			count++;
		}
		return list;
	}
	
	public void resethikisu() {
		hikisulist = new CType[100];
		hikisucount = 0;
	}
	
	public int hairetuwatasi = 0;
	public void hairetureset() {
		hairetuwatasi = 0;
	}
	
	public void hairetuchance() {
		hairetuwatasi = 1;
	}
	
	public void hairetu() {
		hairetuwatasi = 2;
	}
	
	public int gethairetu() {
		return hairetuwatasi;
	}
}
