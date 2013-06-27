package org.ieee.odm.adapter.psse.seq.parser;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.BasePSSEDataParser;

public class PSSEMachinePosSeqZParser extends BasePSSEDataParser {
	public PSSEMachinePosSeqZParser(PsseVersion ver) {
		super(ver);
	}
	
	@Override public String[] getMetadata() {
		
	/*
	 * PSS/E ver. 30-32 machine positive sequence data
	 * 
	 * I, ID, ZPOS_RE, ZPOS_IM
	 * 
	 */
		return new String[] {
				   //  0----------1----------2----------3
					 "I",        "ID",  "ZPOS_RE", "ZPOS_IM"            
				};	
		
	}
   
}
