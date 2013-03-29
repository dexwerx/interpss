package org.ieee.odm.adapter.psse.v30.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchMeterLocationEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class PSSEV30LineDataRec {
	private static int i, j, status;
	private static String ckt;
	private static double r, x, b, ratea, rateb, ratec, gi, bi, gj, bj, len; 
	private static int o1 = 0, o2 = 0, o3 = 0, o4 = 0;
	private static double f1 = 0.0, f2 = 0.0, f3 = 0.0, f4 = 0.0;	

	/*
	 * BranchData
	 * I,J,CKT,R,X,B,RATEA,RATEB,RATEC,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
	 */
	public static void procLineString(String lineStr, PsseVersion version, AclfModelParser parser) {
		procLineFields(lineStr, version);

/*
		I,J,CKT,R,X,B,RATEA,RATEB,RATEC,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
		
		ST Initial branch status where 1 designates in-service and 0 designates out-of-service. ST = 1 by default.
*/
		boolean fromMetered = true;
		if (j < 0) {
			fromMetered = false;
			j = -j;
		}
      	
		final String fid = AbstractModelParser.BusIdPreFix+i;
		final String tid = AbstractModelParser.BusIdPreFix+j;

		LineBranchXmlType branchRec;
		try {
			branchRec = parser.createLineBranch(fid, tid, ckt);
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			return;
		}		
		
		branchRec.setOffLine(status != 1);
		
		branchRec.setMeterLocation( fromMetered ? BranchMeterLocationEnumType.FROM_SIDE :
										BranchMeterLocationEnumType.TO_SIDE);
      	
		AclfDataSetter.setLineData(branchRec, r, x, ZUnitType.PU, 0.0, b, YUnitType.PU);

		branchRec.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
		AclfDataSetter.setBranchRatingLimitData(branchRec.getRatingLimit(),
    				ratea, rateb, ratec, ApparentPowerUnitType.MVA);
        
       if ( gi != 0.0 || bi != 0.0)
    	   branchRec.setFromShuntY(BaseDataSetter.createYValue(gi, bi, YUnitType.PU));
       if ( gj != 0.0 || bj != 0.0)
    	   branchRec.setToShuntY(BaseDataSetter.createYValue(gj, bj, YUnitType.PU));
      
    	BaseJaxbHelper.addOwner(branchRec, 
    			new Integer(o1).toString(), f1, 
    			new Integer(o2).toString(), o2==0?0.0:f2, 
    			new Integer(o3).toString(), o3==0?0.0:f3, 
    			new Integer(o4).toString(), o4==0?0.0:f4);
	}
	
	private static void procLineFields(String lineStr, PsseVersion version) {
		StringTokenizer st;
		st = new StringTokenizer(lineStr, ",");
		i = new Integer(st.nextToken().trim()).intValue();
		j = new Integer(st.nextToken().trim()).intValue();
		ckt = ModelStringUtil.trimQuote(st.nextToken()).trim();
		r = new Double(st.nextToken().trim()).doubleValue();
		x = new Double(st.nextToken().trim()).doubleValue();
		b = new Double(st.nextToken().trim()).doubleValue();
		ratea = new Double(st.nextToken().trim()).doubleValue();
		rateb = new Double(st.nextToken().trim()).doubleValue();
		ratec = new Double(st.nextToken().trim()).doubleValue();
		gi = new Double(st.nextToken().trim()).doubleValue();
		bi = new Double(st.nextToken().trim()).doubleValue();
		gj = new Double(st.nextToken().trim()).doubleValue();
		bj = new Double(st.nextToken().trim()).doubleValue();
		status = new Integer(st.nextToken().trim()).intValue();
		len = new Double(st.nextToken().trim()).doubleValue();

		if (st.hasMoreTokens())
			o1 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f1 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o2 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f2 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o3 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f3 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o4 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f4 = new Double(st.nextToken().trim()).doubleValue();
	}	
}
