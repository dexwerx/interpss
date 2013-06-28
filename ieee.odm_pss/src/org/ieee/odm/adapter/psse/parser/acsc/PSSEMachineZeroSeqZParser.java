package org.ieee.odm.adapter.psse.parser.acsc;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.aclf.BasePSSEDataParser;

public class PSSEMachineZeroSeqZParser extends BasePSSEDataParser {
	public PSSEMachineZeroSeqZParser(PsseVersion ver) {
		super(ver);
	}
	
	@Override public String[] getMetadata() {
		
	/*
	 * PSS/E ver. 30-32 machine zero sequence data
	 * 
	 * I, ID, ZRZERO, ZXZERO
	 * 
	 */
		return new String[] {
				   //  0----------1----------2----------3
					 "I",        "ID",  "ZRZERO", "ZXZERO"            
				};	
		
	}

}
