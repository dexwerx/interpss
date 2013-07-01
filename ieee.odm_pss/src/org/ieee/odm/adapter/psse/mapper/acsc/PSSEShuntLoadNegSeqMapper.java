package org.ieee.odm.adapter.psse.mapper.acsc;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.mapper.aclf.BasePSSEDataMapper;
import org.ieee.odm.adapter.psse.parser.acsc.PSSEMachineZeroSeqZParser;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.acsc.AcscModelParser;

public class PSSEShuntLoadNegSeqMapper extends BasePSSEDataMapper{
	public PSSEShuntLoadNegSeqMapper(PsseVersion ver) {
		super(ver);
		this.dataParser = new PSSEMachineZeroSeqZParser(ver);
	}
	
	
	
	/*
	 * I      Bus number; bus I must be present in the working case.
       GNEG   Active component of negative sequence shunt admittance to ground, including all
              load to be represented at the bus; entered in pu.
       BNEG   Reactive component of negative sequence shunt admittance to ground, including all
              load to be represented at the bus; entered in pu.
	 * 
	 * Only exceptional negative sequence shunt load (i.e. differ from postive sequence load)
	 * should be entered in the sequence file, since it is set be to  same as the positive
	 * sequence data
	 * 
	 * 
	 * For any bus where no such data record is specified, or GNEG and BNEG are both specified as zero,
       the load elements are assumed to be equal in the positive and negative sequence networks.
	 * 
	 */
	
	public void procLineString(String lineStr, AcscModelParser parser) throws ODMException {
		dataParser.parseFields(lineStr);
		
	}

}
