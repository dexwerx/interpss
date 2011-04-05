package com.interpss.QA.rfile;

import java.util.List;

import org.ieee.odm.common.ODMException;

import com.interpss.common.exp.InterpssException;

public interface IFileProcessor {
	boolean processLine(String lineStr) throws InterpssException, ODMException ;
	
	List<String> getErrMsgList();
	
	int getTotalBus();
}
