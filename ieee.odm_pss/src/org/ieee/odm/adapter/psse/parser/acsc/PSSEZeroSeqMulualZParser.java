package org.ieee.odm.adapter.psse.parser.acsc;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.psse.BasePSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.aclf.BasePSSEDataParser;
import org.ieee.odm.common.ODMException;

public class PSSEZeroSeqMulualZParser extends BasePSSEDataParser {
	public PSSEZeroSeqMulualZParser(PsseVersion ver) {
		super(ver);
	}
	
	@Override public String[] getMetadata() {
	
	/*
	 * Format:
	 * I, J, ICKT1, K, L, ICKT2, RM, XM, BIJ1, BIJ2, BKL1, BKL2
	 */
		return new String[]{
			//  0----------1----------2----------3----------4
				"I",      "J",      "ICKT1",     "K",     "L",
			//  5----------6----------7----------8----------9	
				"ICKT2",  "RM",      "XM",     "BIJ1",   "BIJ2", 
			//  10----------11	
				"BKL1",   "BKL2"		
		};
	}
	
	@Override 
	public void parseFields(final String lineStr) throws ODMException {
		this.clearNVPairTableData();
		StringTokenizer st = new StringTokenizer(lineStr,",");
		for (int i = 0; i < st.countTokens(); i++)
			setValue(i, st.nextToken().trim());
	}

	
	

}
