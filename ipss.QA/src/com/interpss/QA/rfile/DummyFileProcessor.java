package com.interpss.QA.rfile;

import java.util.List;


public class DummyFileProcessor implements IFileProcessor {
	@Override
	public List<String> getErrMsgList() {
		// TODO Auto-generated method stub
		return null;
	}

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
