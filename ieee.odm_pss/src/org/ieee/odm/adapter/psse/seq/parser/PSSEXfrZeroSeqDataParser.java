package org.ieee.odm.adapter.psse.seq.parser;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.BasePSSEDataParser;

public class PSSEXfrZeroSeqDataParser extends BasePSSEDataParser {
	public PSSEXfrZeroSeqDataParser(PsseVersion ver) {
		super(ver);
	}
	
	@Override public String[] getMetadata() {

	
	/**
	 * PSS/E ver. 30-32 TRANSFORMER Zero sequence data
	 * 
	 * I,J,K, CKT,ConnectionType, R0,X0,B0,GI0,BI0,GJ0,BJ0
	 */
	//Data sample
	//101,   151,     0,'1 ',  2,    0.00000,    0.00000,    0.00030,    0.01360
	
	
	}
}
