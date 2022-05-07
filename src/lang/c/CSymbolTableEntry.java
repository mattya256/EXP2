package lang.c;


import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry {
	private CType type; // この識別子に対して宣言された型
	private int size; // メモリ上に確保すべきワード数
	private boolean constp; // 定数宣言か？
	private boolean isGlobal; // 大域変数か？
	private int address; // 割り当て番地
	private boolean kansu; // 関数か？
	private boolean canuse = true;
	private CType[] hikisu = new CType[100];
	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isGlobal, int addr,boolean kansu,CType[] hiki) {
		int hikisua[] = new int[100];
		CType hikisub[] = new CType[100];
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isGlobal = isGlobal;
		this.address = addr; 
		this.kansu = kansu;
		this.hikisu = hiki;
	}
	public String toExplainString() { // このエントリに関する情報を作り出す。記号表全体を出力するときに使う。
		String str="";
		int i=0;
		while(i<100) {
			//System.err.println(this.hikisu[i]);
			if(this.hikisu[i]==null) {
				break;
			}
			//System.err.println("a"+this.hikisu[i]);
			str +=  ","+this.hikisu[i].toString() ;
			i++;
		}
		return type.toString() + ", " + size + ", " +  (isGlobal ? "大域" : "局所")+","+(constp ? "定数" : "変数")
				+","+address + ", " +  (kansu ? "関数" : "非関数") + str;
	}
	
	public int getsize() {
		return size;
	}
	
	public CType getCtype() {
		return type;
	}
	
	public boolean getConstant() {
		return constp;
	}
	
	public boolean getisGlobal() {
		return isGlobal;
	}
	
	public int getaddr() {
		return address;
	}
	
	public boolean getkansu() {
		return kansu;
	}
	public CType[] gethikisu() {
		return hikisu;
	}
}