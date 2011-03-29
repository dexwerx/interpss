package com.interpss.QA.rfile;

import java.util.List;

import com.interpss.common.exp.InterpssException;

public interface IFileProcessor {
	boolean processLine(String lineStr) throws InterpssException ;
	
	List<String> getErrMsgList();
	
	int getTotalBus();
}
