package org.ieee.odm.adapter.psse.seq.parser;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.aclf.BasePSSEDataParser;

public class PSSEBranchZeroSeqDataParser extends BasePSSEDataParser {
	public PSSEBranchZeroSeqDataParser(PsseVersion ver) {
		super(ver);
	}

	@Override
	public String[] getMetadata() {
		/**
		 * PSS/E ver. 30-32 NON-TRANSFORMER Branch Zero sequence data
		 * 
		 * I,J,CKT,R0,X0,B0,GI0,BI0,GJ0,BJ0
		 */
		
		return new String[] {
				 //  0----------1----------2----------3----------4
				   "I",          "J",     "CKT",      "R0",      "X0",
				   
				 //  5          6          7          8           9  
				   "B0",        "GI0",     "BI0",     "GJ0",     "BJ0"	
		};
	}
	
	
}
