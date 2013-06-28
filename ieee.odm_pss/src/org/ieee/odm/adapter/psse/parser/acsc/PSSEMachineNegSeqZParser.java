package org.ieee.odm.adapter.psse.parser.acsc;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.aclf.BasePSSEDataParser;

public class PSSEMachineNegSeqZParser extends BasePSSEDataParser {
	public PSSEMachineNegSeqZParser(PsseVersion ver) {
		super(ver);
	}
	
	@Override public String[] getMetadata() {
		
	/*
	 * PSS/E ver. 30-32 machine negative sequence data
	 * It is equal to ZPOS, by default.
	 * 
	 * I, ID, ZRNEG, ZXNEG
	 * 
	 */
		return new String[] {
				   //  0----------1----------2----------3
					 "I",        "ID",  "ZRNEG", "ZXNEG"            
				};	
		
	}

}
