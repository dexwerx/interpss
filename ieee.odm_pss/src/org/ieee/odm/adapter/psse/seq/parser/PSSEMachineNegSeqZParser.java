package org.ieee.odm.adapter.psse.seq.parser;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.BasePSSEDataParser;

public class PSSEMachineNegSeqZParser extends BasePSSEDataParser {
	public PSSEMachineNegSeqZParser(PsseVersion ver) {
		super(ver);
	}
	
	@Override public String[] getMetadata() {
		
	/*
	 * PSS/E ver. 30-32 machine negative sequence data
	 * 
	 * I, ID, ZNEG_RE, ZNEG_IM
	 * 
	 */
		return new String[] {
				   //  0----------1----------2----------3
					 "I",        "ID",  "ZNEG_RE", "ZNEG_IM"            
				};	
		
	}

}
