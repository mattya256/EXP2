package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
	private class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {
		@Override
		public CSymbolTableEntry register(String name, CSymbolTableEntry e) { return put(name, e); }
		@Override
		public CSymbolTableEntry search(String name) { return get(name); }
	}
	private OneSymbolTable global ; // 大域変数用
	private OneSymbolTable local; // 局所変数用
	private int size =0;
	// private SymbolTable<CSymbolTableEntry> global; // こう書いても、もちろんOK
	// private SymbolTable<CSymbolTableEntry> local; // （同上）
	
	public CSymbolTable() {
		global = new OneSymbolTable();
	}
	
	public CSymbolTableEntry set(String name, CSymbolTableEntry e) {
		if(local != null && !e.getkansu()) {
			size += e.getsize();
			return local.register(name, e);
		}else {
			return global.register(name, e);
		}
	}
	
	private int hikisusize =-3;
	public CSymbolTableEntry hikisuuset(String name, CSymbolTableEntry e) {
		if(local != null && !e.getkansu()) {
			hikisusize -= e.getsize();
			return local.register(name, e);
		}else {
			return global.register(name, e);
		}
	}
	
	public void show() {
		System.out.println("大域変数");
		global.show();
		if(local!=null) {
			System.out.println("局所変数");
			local.show();
		}
	}
	
	public CSymbolTableEntry search(String str) {
		CSymbolTableEntry ctable = null;
		if(local != null) {
			ctable = local.search(str);
		}
		if(ctable != null) {
			return ctable;
		}
		return global.search(str);
	}
	
	public void setupLocalSymbolTable() {
		local = new OneSymbolTable();
	}
	
	public void deleteLocalSymbolTable() {
		local = null;
		size = 0;
		hikisusize=-3;
	}
	
	public boolean checkglobal() {
		if(local != null) {
			return false;
		}
		return true;
	}
	
	public int getsumsize() {
		return size;
	}
	
	public int gethikisusize() {
		return hikisusize;
	}
}