package com.interpss.test.util;

import com.interpss.common.exp.InterpssException;

public interface IFileProcessor {
	boolean processLine(String lineStr) throws InterpssException ;
}
