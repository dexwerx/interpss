package org.ieee.odm.adapter.psse.mapper.acsc;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.mapper.aclf.BasePSSEDataMapper;

import org.ieee.odm.adapter.psse.parser.acsc.PSSEMachinePosSeqZParser;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.AbstractModelParser;

import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.model.acsc.AcscParserHelper;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.ScGenDataXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ZUnitType;

public class PSSEMachinePosSeqZMapper extends BasePSSEDataMapper {

	public PSSEMachinePosSeqZMapper(PsseVersion ver) {
		super(ver);
		this.dataParser = new PSSEMachinePosSeqZParser(ver);
	}
	
	/*
	 * Format 
	 * I, ID, ZRPOS, ZXPOS

	 */
	public void procLineString(String lineStr, AcscModelParser parser) throws ODMException {
		dataParser.parseFields(lineStr);
		
		String machId = dataParser.getString("ID");
		
		double ZRPOS = dataParser.getDouble("ZRPOS");
		
		double ZXPOS = dataParser.getDouble("ZXPOS");
		
		int i = dataParser.getInt("I");
	    final String busId = AbstractModelParser.BusIdPreFix+i;
	    
	    ShortCircuitBusXmlType acscBus=parser.getAcscBus(busId);
	    ScGenDataXmlType scGenData=null;
	    if((scGenData =AcscParserHelper.getScGenData(parser, busId, machId))==null){
	    	scGenData = parser.createScGenData(acscBus,machId);
	    }
	    scGenData.setPotiveZ(BaseDataSetter.createZValue(ZRPOS,ZXPOS, ZUnitType.PU));
	    
	    
	}

}
