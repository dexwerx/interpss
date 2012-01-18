 /*
  * @(#)IEEECDF_ODMTest.java   
  *
  * Copyright (C) 2008 www.interpss.org
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
  * @Date 02/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.ieeecdf;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.ieeecdf.IeeeCDFAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;
import org.junit.Test;

public class IEEECDF_ODMTest { 
	@Test
	public void testCaseNew() throws Exception {
		final LogManager logMgr = LogManager.getLogManager();
		Logger logger = Logger.getLogger("IEEE ODM Logger");
		logger.setLevel(Level.INFO);
		logMgr.addLogger(logger);
		
		IODMAdapter adapter = new IeeeCDFAdapter();
		assertTrue(adapter.parseInputFile("testdata/ieee_format/Ieee14Bus.ieee"));
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		System.out.println(parser.toXmlDoc());
		
		LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
		assertTrue(baseCaseNet.getBusList().getBus().size() == 14);
		assertTrue(baseCaseNet.getBranchList().getBranch().size() == 20);

		assertTrue(baseCaseNet.getBasePower().getValue() == 100.0);
		assertTrue(baseCaseNet.getBasePower().getUnit() == ApparentPowerUnitType.MVA);

		// Check Bus Data
		// ==============
		
		// Bus 1 is a swing bus
		//    1 Bus 1     HV  1  1  3 1.060    0.0      0.0      0.0    232.4   -16.9   132.0  1.060     0.0     0.0   0.0    0.0        0
		LoadflowBusXmlType busRec = parser.getAclfBus("Bus1");
		//System.out.println(busRec);
		assertTrue(busRec.getBaseVoltage().getValue() == 132.0);
		assertTrue(busRec.getVoltage().getValue() == 1.060);
		assertTrue(busRec.getAngle().getValue() == 0.0);
		assertTrue(busRec.getGenData().getEquivGen().getCode() == LFGenCodeEnumType.SWING);
		assertTrue(busRec.getLoadData() == null);
		assertTrue(busRec.getShuntY() == null);

		// Bus 2 is a PV bus with load
		//   2 Bus 2     HV  1  1  2 1.045  -4.98     21.7     12.7     40.0    42.4   132.0  1.045    50.0   -40.0   0.0    0.0        0
		busRec = parser.getAclfBus("Bus2");
		//System.out.println(busRec);
		assertTrue(busRec.getGenData().getEquivGen().getCode() == LFGenCodeEnumType.PV);
		assertTrue(busRec.getGenData().getEquivGen().getPower().getRe() == 40.0);
		assertTrue(busRec.getGenData().getEquivGen().getPower().getUnit() == ApparentPowerUnitType.MVA);
		assertTrue(busRec.getGenData().getEquivGen().getQLimit().getMax() == 50.0);
		assertTrue(busRec.getGenData().getEquivGen().getQLimit().getMin() == -40.0);
		assertTrue(busRec.getGenData().getEquivGen().getQLimit().getUnit() == ReactivePowerUnitType.MVAR);
		
		assertTrue(busRec.getLoadData().getEquivLoad().getCode() == LFLoadCodeEnumType.CONST_P);
		assertTrue(busRec.getLoadData().getEquivLoad().getConstPLoad().getRe() == 21.7);
		assertTrue(busRec.getLoadData().getEquivLoad().getConstPLoad().getIm() == 12.7);
		assertTrue(busRec.getLoadData().getEquivLoad().getConstPLoad().getUnit() == ApparentPowerUnitType.MVA);

		// Bus 9 is a load bus, also there is a capacitor of 0.19 pu
		//    9 Bus 9     LV  1  1  0 1.056 -14.94     29.5     16.6      0.0     0.0    35.0  0.0       0.0     0.0   0.0    0.19       0
		busRec = parser.getAclfBus("Bus9");
		assertTrue(busRec.getLoadData().getEquivLoad().getCode() == LFLoadCodeEnumType.CONST_P);
		assertTrue(busRec.getLoadData().getEquivLoad().getConstPLoad().getRe() == 29.5);
		assertTrue(busRec.getLoadData().getEquivLoad().getConstPLoad().getIm() == 16.6);
		assertTrue(busRec.getLoadData().getEquivLoad().getConstPLoad().getUnit() == ApparentPowerUnitType.MVA);
		
		assertTrue(busRec.getShuntY().getRe() == 0.0);
		assertTrue(busRec.getShuntY().getIm() == 0.19);
		assertTrue(busRec.getShuntY().getUnit() == YUnitType.PU);
		
		// Bus 7 is non-gen and non-load bus
		//    7 Bus 7     ZV  1  1  0 1.062 -13.37      0.0      0.0      0.0     0.0    35.0  0.0       0.0     0.0   0.0    0.0        0
		busRec = parser.getAclfBus("Bus7");
		//assertTrue(busRec.getLoadflowData().getGenData() == null);
		assertTrue(busRec.getLoadData() == null);
		assertTrue(busRec.getShuntY() == null);

		// Check Branch Data
		// =================
		
		// Branch 1->2 is a LIne
		//    1    2  1  1 1 0  0.01938   0.05917     0.0528     0     0     0    0 0  0.0       0.0 0.0    0.0     0.0    0.0   0.0
		LineBranchXmlType braRec = parser.getLineBranch("Bus1", "Bus2", "1");
		assertTrue(braRec != null);
		
		assertTrue(braRec.getZ().getRe() == 0.01938); 
		assertTrue(braRec.getZ().getIm() == 0.05917); 
		assertTrue(braRec.getZ().getUnit() == ZUnitType.PU); 
		assertTrue(braRec.getTotalShuntY().getRe() == 0.0); 
		assertTrue(braRec.getTotalShuntY().getIm() == 0.0528); 
		assertTrue(braRec.getTotalShuntY().getUnit() == YUnitType.PU); 
		
		// Branch 4->7 is a Xfr
		//   4    7  1  1 1 1  0.0       0.20912     0.0        0     0     0    0 0  0.978     0.0 0.0    0.0     0.0    0.0   0.0
		XfrBranchXmlType xfrBraRec = parser.getXfrBranch("Bus4", "Bus7", "1");
		assertTrue(braRec != null);
		assertTrue(xfrBraRec.getZ().getRe() == 0.0); 
		assertTrue(xfrBraRec.getZ().getIm() == 0.20912); 
		assertTrue(xfrBraRec.getZ().getUnit() == ZUnitType.PU); 
		//System.out.println(braRec.getLoadflowData().getXformerData().getFromTap());
		//System.out.println(braRec.getLoadflowData().getXformerData().getToTap());
		assertTrue(xfrBraRec.getFromTurnRatio().getValue() == 0.978); 
		assertTrue(xfrBraRec.getToTurnRatio().getValue() == 1.0);

		//parser.stdout();
	}
}

