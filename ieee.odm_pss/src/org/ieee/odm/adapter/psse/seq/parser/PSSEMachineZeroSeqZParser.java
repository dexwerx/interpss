package org.ieee.odm.adapter.psse.seq.parser;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.BasePSSEDataParser;

public class PSSEMachineZeroSeqZParser extends BasePSSEDataParser {
	public PSSEMachineZeroSeqZParser(PsseVersion ver) {
		super(ver);
	}
	
	@Override public String[] getMetadata() {
		
	/*
	 * PSS/E ver. 30-32 machine zero sequence data
	 * 
	 * I, ID, ZZERO_RE, ZZERO_IM
	 * 
	 */
		return new String[] {
				   //  0----------1----------2----------3
					 "I",        "ID",  "ZZERO_RE", "ZZERO_IM"            
				};	
		
	}

}
