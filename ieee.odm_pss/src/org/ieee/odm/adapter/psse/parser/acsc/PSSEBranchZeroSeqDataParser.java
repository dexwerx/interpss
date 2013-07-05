package org.ieee.odm.adapter.psse.parser.acsc;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.aclf.BasePSSEDataParser;
import org.ieee.odm.common.ODMException;

public class PSSEBranchZeroSeqDataParser extends BasePSSEDataParser {
	public PSSEBranchZeroSeqDataParser(PsseVersion ver) {
		super(ver);
	}

	@Override
	public String[] getMetadata() {
		/**
		 * PSS/E ver. 30-32 NON-TRANSFORMER Branch Zero sequence data
		 * 
		 * I, J, ICKT, RLINZ, XLINZ, BCHZ, GI, BI, GJ, BJ
		 */
		
		return new String[] {
				 //  0----------1----------2----------3----------4
				   "I",        "J",     "ICKT",    "RLINZ",   "XLINZ", 
				   
				 //  5          6          7          8           9  
				   "BCHZ",     "GI",      "BI",      "GJ",       "BJ"
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


	
