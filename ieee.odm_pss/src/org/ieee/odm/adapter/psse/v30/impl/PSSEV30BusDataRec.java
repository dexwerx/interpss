 /*
  * @(#)PSSEBusDataRec.java   
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
import java.util.logging.Logger;

import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;

public class PSSEV30BusDataRec {
	private static int i, ide, area = 1, zone = 1, owner = 1;
	private static String name;
	private static double baseKv, gl = 0.0, bl = 0.0, vm = 1.0, va = 0.0;
	
	/*
	 * BusData Format: I, ’NAME’, BASKV, IDE, GL, BL, AREA, ZONE, VM, VA, OWNER
	 *       101743,'TAU 9A,8    ',  13.8000,2,     0.000,     0.000, 101, 101,1.02610, -98.5705,   1

	 */
	public static void procLineString(String lineStr, PsseVersion version, AclfModelParser parser) {
		try {
			procFields(lineStr, version);
		} catch (Exception e) {
			ODMLogger.getLogger().severe("lineStr: " + lineStr);
			e.printStackTrace();
		}

/*
		Format: I,    ’NAME’,    BASKV, IDE,  GL,      BL,  AREA, ZONE, VM, VA, OWNER
*/
		String iStr = AbstractModelParser.BusIdPreFix+i;
		LoadflowBusXmlType aclfBusXml;
		try {
			aclfBusXml = parser.createAclfBus(iStr, i);
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			return;
		}
		aclfBusXml.setNumber((long)i);
		
		aclfBusXml.setAreaNumber(area);
		aclfBusXml.setZoneNumber(zone);
		if (owner > 0) {
			BaseJaxbHelper.addOwner(aclfBusXml, new Integer(owner).toString());
		}
		
		aclfBusXml.setName(name);
		aclfBusXml.setBaseVoltage(BaseDataSetter.createVoltageValue(baseKv, VoltageUnitType.KV));
		
		aclfBusXml.setVoltage(BaseDataSetter.createVoltageValue(vm, VoltageUnitType.PU));
		aclfBusXml.setAngle(BaseDataSetter.createAngleValue(va, AngleUnitType.DEG));

    	if (gl != 0.0 || bl != 0.0) {
    		double factor = parser.getAclfNet().getBasePower().getValue();  
    		// for transfer G+jB to PU on system base, gl, bl are entered in MW at one per unit voltage
    		// bl is reactive power consumed, - for capactor
    		aclfBusXml.setShuntY(BaseDataSetter.createYValue(gl/factor, bl/factor, YUnitType.PU));
    	}
      	
    	// set input data to the bus object
		LFGenCodeEnumType genType = ide == 3? LFGenCodeEnumType.SWING : 
								( ide == 1? LFGenCodeEnumType.PQ : 
									( ide == 2 ? LFGenCodeEnumType.PV : LFGenCodeEnumType.NONE_GEN ));
		AclfDataSetter.setGenData(aclfBusXml, genType, vm, VoltageUnitType.PU, va, AngleUnitType.DEG, 
						0.0, 0.0,	ApparentPowerUnitType.MVA);

		if (ide == 1 || ide == 2 || ide == 3) 
			aclfBusXml.setOffLine(false);
		else
			aclfBusXml.setOffLine(true);
			
	}
	
	private static void procFields(String lineStr, PsseVersion version) throws Exception {
		StringTokenizer st;

	//	 101743,'TAU 9A,8    ',  13.8000,2,     0.000,     0.000, 101, 101,1.02610, -98.5705,   1
	//    10001,'ALB_T4*     ',   1.0000,1,     0.000,     0.000,   1,   1,1.03259, -13.5044,   1
	// -- str1-- ----str2---- -----------str3---------------	
		String str1 = lineStr.substring(0, lineStr.indexOf('\'')),
	           strbuf = lineStr.substring(lineStr.indexOf('\'')+1),
	           str2 = strbuf.substring(0, strbuf.indexOf('\'')),
	           str3 = strbuf.substring(strbuf.indexOf('\'')+1);
	           
		
		st = new StringTokenizer(str1, ",");
		i = new Integer(st.nextToken().trim()).intValue();
		name = str2;

		st = new StringTokenizer(str3, ",");
        String s = st.nextToken().trim();
        if (s.equals(""))   // in case there are spaces between '  ,
            s = st.nextToken().trim();		
		baseKv = new Double(s).doubleValue();
		ide = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			gl = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			bl = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			area = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			zone = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			vm = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			va = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			owner = new Integer(st.nextToken().trim()).intValue();
	}
}
