package org.ieee.odm.adapter.psse.mapper;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.parser.PSSELineDataParser;
import org.ieee.odm.common.ODMBranchDuplicationException;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchBusSideEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class PSSELineDataMapper extends BasePSSEDataMapper {

	public PSSELineDataMapper(PsseVersion ver) {
		super(ver);
		this.dataParser = new PSSELineDataParser(ver);
	}
	
	/*
	 * BranchData
	 * I,J,CKT,R,X,B,RATEA,RATEB,RATEC,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
	 */
	public void procLineString(String lineStr, AclfModelParser parser) throws ODMException {
		//procLineFields(lineStr, version);
		dataParser.parseFields(lineStr);
/*
		I,J,CKT,R,X,B,RATEA,RATEB,RATEC,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
		
		ST Initial branch status where 1 designates in-service and 0 designates out-of-service. ST = 1 by default.
*/
		int i = dataParser.getInt("I");
		int j = dataParser.getInt("J");
		boolean fromMetered = true;
		if (j < 0) {
			fromMetered = false;
			j = -j;
		}
      	
		final String fid = AbstractModelParser.BusIdPreFix+i;
		final String tid = AbstractModelParser.BusIdPreFix+j;

		LineBranchXmlType braRecXml;
		try {
			braRecXml = parser.createLineBranch(fid, tid, dataParser.getString("CKT"));
		} catch (ODMBranchDuplicationException e) {
			ODMLogger.getLogger().severe(e.toString());
			return;
		}		
		
		int status = dataParser.getInt("ST", 1);
		braRecXml.setOffLine(status != 1);
		
		braRecXml.setMeterLocation( fromMetered ? BranchBusSideEnumType.FROM_SIDE :
										BranchBusSideEnumType.TO_SIDE);
      	
		double r = dataParser.getDouble("R", 0.0);
		double x = dataParser.getDouble("X", 0.0);
		double b = dataParser.getDouble("B", 0.0);
		AclfDataSetter.setLineData(braRecXml, r, x, ZUnitType.PU, 0.0, b, YUnitType.PU);

		double ratea = dataParser.getDouble("RATEA", 0.0);
		double rateb = dataParser.getDouble("RATEB", 0.0);
		double ratec = dataParser.getDouble("RATEC", 0.0);
		braRecXml.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
		AclfDataSetter.setBranchRatingLimitData(braRecXml.getRatingLimit(),
    				ratea, rateb, ratec, ApparentPowerUnitType.MVA);
        
		double gi = dataParser.getDouble("GI", 0.0);
		double bi = dataParser.getDouble("BI", 0.0);
       if ( gi != 0.0 || bi != 0.0)
    	   braRecXml.setFromShuntY(BaseDataSetter.createYValue(gi, bi, YUnitType.PU));
       
		double gj = dataParser.getDouble("GJ", 0.0);
		double bj = dataParser.getDouble("BJ", 0.0);
       if ( gj != 0.0 || bj != 0.0)
    	   braRecXml.setToShuntY(BaseDataSetter.createYValue(gj, bj, YUnitType.PU));
       
       mapOwnerInfo(braRecXml);
	}
}
