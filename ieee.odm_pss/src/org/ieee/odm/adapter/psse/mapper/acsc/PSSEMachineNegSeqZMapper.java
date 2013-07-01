package org.ieee.odm.adapter.psse.mapper.acsc;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.mapper.aclf.BasePSSEDataMapper;
import org.ieee.odm.adapter.psse.parser.acsc.PSSEMachineNegSeqZParser;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.model.acsc.AcscParserHelper;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitGenDataXmlType;
import org.ieee.odm.schema.ZUnitType;

public class PSSEMachineNegSeqZMapper extends BasePSSEDataMapper{
    
	public PSSEMachineNegSeqZMapper(PsseVersion ver) {
		super(ver);
		this.dataParser = new PSSEMachineNegSeqZParser(ver);
	}
	
	/*
	 * Format 
	 * I, ID, ZRNEG, ZXNEG

	 */
	public void procLineString(String lineStr, AcscModelParser parser) throws ODMException {
		dataParser.parseFields(lineStr);
		
		String machId = dataParser.getString("ID");
		
		double ZRNEG = dataParser.getDouble("ZRNEG");
		
		double ZXNEG = dataParser.getDouble("ZXNEG");
		
		int i = dataParser.getInt("I");
	    final String busId = AbstractModelParser.BusIdPreFix+i;
	    
	    ShortCircuitBusXmlType acscBus=parser.getBus(busId);
	    ShortCircuitGenDataXmlType scGenData= AcscParserHelper.getScGenData(acscBus, machId);
	    
	    scGenData.setNegativeZ(BaseDataSetter.createZValue(ZRNEG,ZXNEG, ZUnitType.PU));
	    
	    
	}
}
