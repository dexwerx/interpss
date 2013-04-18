 /*
  * @(#)PSSEV30LoadDataRec.java   
  *
  * Copyright (C) 2006 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.adapter.psse.v30.impl;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowLoadXmlType;

public class PSSEV30LoadDataRec {
	private static int i, status, area = 1, zone = 1, owner = 1;
	private static String id;
	private static double pl = 0.0, ql = 0.0, ip = 0.0, iq = 0.0, yp = 0.0, yq = 0.0;
	
	/*
	 * LoadData I, ID, STATUS, AREA, ZONE, PL, QL, IP, IQ, YP, YQ, OWNER
	 */	
	public static void procLineString(String lineStr, PsseVersion version, final AclfModelParser parser) {
		procLineString(lineStr, version);
/*
		I, ID, STATUS, AREA, ZONE, PL, QL, IP, IQ, YP, YQ, OWNER
*/		
	    final String busId = AbstractModelParser.BusIdPreFix+i;
		LoadflowBusXmlType busRecXml = parser.getAclfBus(busId);
	    if (busRecXml == null){
	    	ODMLogger.getLogger().severe("Bus "+ busId+ " not found in the network");
	    	return;
	    }
		
	    LoadflowLoadXmlType contribLoad = AclfParserHelper.createContriLoad(busRecXml); 

	    contribLoad.setId(id);
	    contribLoad.setName("Load:" + id + "(" + i + ")");
	    contribLoad.setDesc("PSSE Load " + id + " at Bus " + i);
	    contribLoad.setOffLine(status!=1);

	    contribLoad.setAreaNumber(area);
	    contribLoad.setZoneNumber(zone);
	    BaseJaxbHelper.addOwner(contribLoad, new Integer(owner).toString());
		
		if (pl != 0.0 || ql != 0.0)
			contribLoad.setConstPLoad(BaseDataSetter.createPowerValue(pl, ql, ApparentPowerUnitType.MVA));
		if (ip != 0.0 || iq != 0.0)
			contribLoad.setConstILoad(BaseDataSetter.createPowerValue(ip, iq, ApparentPowerUnitType.MVA));
		if (yp != 0.0 || yq != 0.0)
			contribLoad.setConstZLoad(BaseDataSetter.createPowerValue(yp, yq, ApparentPowerUnitType.MVA));
	}

	private static void procLineString(String lineStr, PsseVersion version) {
		StringTokenizer st;

		st = new StringTokenizer(lineStr, ",");
		i = new Integer(st.nextToken().trim()).intValue();
		id = st.nextToken();
	
		status = new Integer(st.nextToken().trim()).intValue();
		area = new Integer(st.nextToken().trim()).intValue();
		zone = new Integer(st.nextToken().trim()).intValue();
		pl = new Double(st.nextToken().trim()).doubleValue();
		ql = new Double(st.nextToken().trim()).doubleValue();
		ip = new Double(st.nextToken().trim()).doubleValue();
		iq = new Double(st.nextToken().trim()).doubleValue();
		yp = new Double(st.nextToken().trim()).doubleValue();
		yq = new Double(st.nextToken().trim()).doubleValue();
		owner = new Integer(st.nextToken().trim()).intValue();
	}
}
