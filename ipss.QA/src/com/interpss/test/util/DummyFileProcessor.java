package com.interpss.test.util;


public class DummyFileProcessor implements IFileProcessor {
	private int lineCnt = 0;
	
	@Override
	public boolean processLine(String lineStr) {
		this.lineCnt++;
		return true;
	}

	public void printLineCnt() {
		System.out.println("File Line Cnt: " + this.lineCnt);
	}
	
	public static void main(String args[]) {
		DummyFileProcessor proc = new DummyFileProcessor();
		
		new FileReader("output/pslf/Sample18Bus_epc_01192011.txt")
				.processFile(proc);
		
		proc.printLineCnt();
	}	
}
