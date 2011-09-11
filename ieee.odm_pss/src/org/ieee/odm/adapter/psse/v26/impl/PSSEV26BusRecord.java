/*
 * @(#)PSSEBusRecord.java   
 *
 * Copyright (C) 2006-2008 www.interpss.org
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
 * @Author Stephen Hau, Mike Zhou
 * @Version 1.0
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */
package org.ieee.odm.adapter.psse.v26.impl;

import java.util.StringTokenizer;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenDataXmlType;
import org.ieee.odm.schema.LoadflowLoadDataXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class PSSEV26BusRecord {
	public static void processBusData(final String str, final AclfModelParser parser) {
		// parse the input data line
		final String[] strAry = getBusDataFields(str);	    

		// I,    NAME        BASKV, IDE,  GL,      BL, AREA, ZONE, VM,      VA,      OWNER
		// 31212,'ADLIN  1', 115.00,1,    0.00,    0.00,  1,  1,   1.01273, -10.5533,1 
		// 45035,'CAPJAK 1', 500.00,1,    0.00,  400.00,  8, 11,1.08554,  18.6841,1,    /* [45035_CAPTJACK_500_B2] */ 

		final String busId = AbstractModelParser.BusIdPreFix+strAry[0];
			// XML requires id start with a char
		ODMLogger.getLogger().fine("Bus data loaded, id: " + busId);
		LoadflowBusXmlType busRec;
		try {
			busRec = parser.createAclfBus(busId, new Integer(strAry[0]));
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			return;
		}
		
		busRec.setNumber(ModelStringUtil.getLong(strAry[0], 0));
		
		final String busName = ModelStringUtil.removeSingleQuote(strAry[1]);
		busRec.setName(busName);
		double baseKv = ModelStringUtil.getDouble(strAry[2], 0.0);
		if (baseKv == 0.0) {
			ODMLogger.getLogger().severe("Error: base kv = 0.0");
			baseKv = 1.0;
		}
		
		final String owner=strAry[10];
		BaseJaxbHelper.addOwner(busRec, owner);
		
		busRec.setBaseVoltage(BaseDataSetter.createVoltageValue(baseKv, VoltageUnitType.KV));
		
		// vm voltage, p.u. [F] *
		//va angle, degrees [F] *
		final double vpu = ModelStringUtil.getDouble(strAry[8], 1.0);
		final double angDeg = ModelStringUtil.getDouble(strAry[9], 0.0);
		busRec.setVoltage(BaseDataSetter.createVoltageValue(vpu, VoltageUnitType.PU));
		busRec.setAngle(BaseDataSetter.createAngleValue(angDeg, AngleUnitType.DEG));
		
		/* bus type identifier IDE
			1 - load bus (no generator boundary condition)
			2 - generator or plant bus (either voltage regulating or fixed Mvar)
			3 - swing bus
			4 - disconnected (isolated) bus
			IDE = 1 by default.
		*/			
		final int IDE = ModelStringUtil.getInt(strAry[3], 1);
		if (IDE ==3){//Swing bus
			busRec.setGenData(parser.getFactory().createLoadflowBusXmlTypeGenData());
			LoadflowGenDataXmlType equivGen = parser.getFactory().createLoadflowGenDataXmlType(); 
			busRec.getGenData().setEquivGen(equivGen);
			equivGen.setCode(LFGenCodeEnumType.SWING);
			equivGen.setDesiredVoltage(BaseDataSetter.createVoltageValue(vpu, VoltageUnitType.PU));
			equivGen.setDesiredAngle(BaseDataSetter.createAngleValue(angDeg, AngleUnitType.DEG));
		}
		else if (IDE==2){// generator bus. At this point we do not know if it is a PQ or PV bus
			// by default, Gen is a PV bus
			busRec.setGenData(parser.getFactory().createLoadflowBusXmlTypeGenData());
			busRec.getGenData().setEquivGen(parser.getFactory().createLoadflowGenDataXmlType());
			busRec.getGenData().getEquivGen().setCode(LFGenCodeEnumType.PV);
		} else if (IDE==4){// Isolated bus
			// should be no gen and load defined
			busRec.setOffLine(true);
		}
		else { //Non-Gen Load Bus
			busRec.setLoadData(parser.getFactory().createLoadflowBusXmlTypeLoadData());
			busRec.getLoadData().setEquivLoad(parser.getFactory().createLoadflowLoadDataXmlType());
		}
		
		//GL BL in Mva
		final double gMw = ModelStringUtil.getDouble(strAry[4], 0.0);
		final double bMvar= ModelStringUtil.getDouble(strAry[5], 0.0);
		if (gMw != 0.0 || bMvar != 0.0) {
			busRec.setShuntY(BaseDataSetter.createYValue(gMw, bMvar, YUnitType.MVAR));
		}
		//area zone	
		final String areaNo = strAry[6];
		final String zoneNo = strAry[7];
		busRec.setAreaNumber(ModelStringUtil.getInt(areaNo, 0));
		busRec.setZoneNumber(ModelStringUtil.getInt(zoneNo, 0));		
	}
		
	public static  void processLoadData(final String str,final AclfModelParser parser) {
		// I,    ID,  STATUS, AREA, ZONE, PL,   QL,   IP,   IQ,   YP,    YQ,  OWNER
		// 33547,' 1',1,      1,    1,    3.00, 9.54, 0.00, 0.00, 0.00,  0.00,1,   /* [EnergyConsumer_1704] */
		
		final String[] strAry = getLoadDataFields(str);

	    final String busId = AbstractModelParser.BusIdPreFix+strAry[0];
	    //to test if there is a responding bus in the bus data record
		LoadflowBusXmlType busRec = parser.getAclfBus(busId);
	    if (busRec == null){
	    	ODMLogger.getLogger().severe("Bus "+ busId+ " not found in the network");
	    	return;
	    }

	    // ODM allows one equiv load has many contribute loads, but here, we assume there is only one contribute load.

		LoadflowBusXmlType.LoadData loadData = busRec.getLoadData();
		if (loadData == null) { 
			loadData = parser.getFactory().createLoadflowBusXmlTypeLoadData(); 
			busRec.setLoadData(loadData);
			loadData.setEquivLoad(parser.getFactory().createLoadflowLoadDataXmlType());
		}
		if (loadData.getContributeLoadList() == null) 
			loadData.setContributeLoadList(parser.getFactory().createLoadflowBusXmlTypeLoadDataContributeLoadList());
	    LoadflowLoadDataXmlType contribLoad = parser.getFactory().createLoadflowLoadDataXmlType(); 
	    loadData.getContributeLoadList().getContributeLoad().add(contribLoad); 
		
	    // processing contributing load data

	    //loadId is used to distinguish multiple loads at one bus
	    final String loadId =strAry[1];
		contribLoad.setId(loadId);
		
		// STATUS - Initial load status of one for in-service and zero for out-of-service. STATUS = 1 by default
		int status = ModelStringUtil.getInt(strAry[1], 1);
		int area = ModelStringUtil.getInt(strAry[2], 1);
		int zone = ModelStringUtil.getInt(strAry[3], 1);
		contribLoad.setOffLine(status != 1);
		contribLoad.setAreaNumber(area);
		contribLoad.setZoneNumber(zone);
		
		//set owner and it's factor
		final String owner =strAry[11];
		BaseJaxbHelper.addOwner(contribLoad, owner);
		    
	    //Constant-P load
		final double CPloadMw = ModelStringUtil.getDouble(strAry[5], 0.0);
		final double CQloadMvar = ModelStringUtil.getDouble(strAry[6], 0.0);
		//Constant-I load
		final double CIloadMw = ModelStringUtil.getDouble(strAry[7], 0.0);
		final double CIloadMvar = ModelStringUtil.getDouble(strAry[8], 0.0);
		//Constant-Y load
		final double CYloadMw = ModelStringUtil.getDouble(strAry[9], 0.0);
		final double CYloadMvar = ModelStringUtil.getDouble(strAry[10], 0.0);

		if (CPloadMw!=0.0 || CQloadMvar!=0.0 )
			contribLoad.setConstPLoad(BaseDataSetter.createPowerValue(
	    			CPloadMw, CQloadMvar, ApparentPowerUnitType.MVA));

	    if (CIloadMw!=0.0 || CIloadMvar!=0.0)
	    	contribLoad.setConstILoad(BaseDataSetter.createPowerValue(
	    			CIloadMw, CIloadMvar, ApparentPowerUnitType.MVA));
	   
	    if (CYloadMw!=0.0 || CYloadMvar!=0.0)
	    	contribLoad.setConstZLoad(BaseDataSetter.createPowerValue(
	    			CYloadMw, CYloadMvar, ApparentPowerUnitType.MVA));
	    
	    // processing equiv load data
	    loadData.getEquivLoad().setCode(LFLoadCodeEnumType.CONST_P);
	    LoadflowLoadDataXmlType load = loadData.getEquivLoad();
	    if (load == null) {
	    	load = parser.getFactory().createLoadflowLoadDataXmlType();
	    	load.setConstPLoad(parser.getFactory().createPowerXmlType());
	    }
	    if(load.getConstPLoad() == null)
	    	load.setConstPLoad(parser.getFactory().createPowerXmlType());
	    double tp = CPloadMw + CIloadMw + CYloadMw + load.getConstPLoad().getRe();
	    double tq = CQloadMvar + CIloadMvar + CYloadMvar  + load.getConstPLoad().getIm();;
	    load.setConstPLoad(BaseDataSetter.createPowerValue(tp, tq, ApparentPowerUnitType.MVA));
	}
	
	public static  void processGenData(final String str,final AclfModelParser parser) {
		//I,    ID,      PG,      QG,     QT,      QB,   VS,        IREG,MBASE, ZR,    ZX,    RT,    XT,    GTAP,  STAT,RMPCT,  PT,         PB,  O1,F1,...,O4,F4
		//31435,' 1',    8.52,    2.51,   10.00,   -6.00,1.0203,    0,   100.00,0.0000,1.0000,0.0000,0.0000,1.0000,1,   100.00, 9999.00,    0.00,1,1.00,0,0.00,0,0.00,0,0.00,   /* [SynchronousMachine_78] */ 
		
		//37585,' 1',    0.00,    0.00,    0.00,    0.00,1.0331,    0,   100.00,0.0000,1.0000,0.0000,0.0000,1.0000,0,   100.00, 9999.00,-5322.00,1,1.00,0,0.00,0,0.00,0,0.00,   /* [SynchronousMachine_27209] */ 
		//37585,' 2',   -1.00,  186.48, 1774.00,-1774.00,1.0331,    0,   100.00,0.0000,1.0000,0.0000,0.0000,1.0000,1,   100.00, 9999.00,-5322.00,1,1.00,0,0.00,0,0.00,0,0.00,   /* [SynchronousMachine_20804] */ 
		
		// parse the input data line
	    final String[] strAry = getGenDataFields(str);
		final String busId = AbstractModelParser.BusIdPreFix+strAry[0];
		// get the responding-bus data with busId
		LoadflowBusXmlType busRec = parser.getAclfBus(busId);
		if (busRec==null){
			ODMLogger.getLogger().severe("Error: Bus not found in the network, bus number: " + busId);
        	return;
        }
				
	    // ODM allows one equiv gen has many contribute generators

		LoadflowBusXmlType.GenData genData = busRec.getGenData();
		if (genData == null) {
			genData = parser.getFactory().createLoadflowBusXmlTypeGenData();
			busRec.setGenData(genData);
			busRec.getGenData().setEquivGen(parser.getFactory().createLoadflowGenDataXmlType());
		}
		LoadflowGenDataXmlType equivGen = genData.getEquivGen();
	    if (genData.getContributeGenList() == null)
	    	genData.setContributeGenList(parser.getFactory().createLoadflowBusXmlTypeGenDataContributeGenList());
	    LoadflowGenDataXmlType contriGen = parser.getFactory().createLoadflowGenDataXmlType(); 
	    genData.getContributeGenList().getContributeGen().add(contriGen);
		
	    // processing contributing gen data
	    
		// genId is used to distinguish multiple generations at one bus		
		final String genId = strAry[1];
		contriGen.setId(genId);
		
		double mbase = ModelStringUtil.getDouble(strAry[8], 0.0),
		       zr = ModelStringUtil.getDouble(strAry[9], 0.0),
		       zx = ModelStringUtil.getDouble(strAry[10], 0.0),
		       rt = ModelStringUtil.getDouble(strAry[11], 0.0),
		       xt = ModelStringUtil.getDouble(strAry[12], 0.0),
		       gtap = ModelStringUtil.getDouble(strAry[13], 0.0); 
		contriGen.setRatedPower(BaseDataSetter.createPowerMvaValue(mbase));
		if(zr != 0.0 || zx != 0.0)
			contriGen.setSourceZ(BaseDataSetter.createZValue(zr, zx, ZUnitType.PU));
		if(rt != 0.0 || xt != 0.0)
			contriGen.setXfrZ(BaseDataSetter.createZValue(rt, xt, ZUnitType.PU));
		contriGen.setXfrTap(gtap);
		
		// STATUS - Initial load status of one for in-service and zero for out-of-service. STATUS = 1 by default
		int status = ModelStringUtil.getInt(strAry[14], 1);
		contriGen.setOffLine(status != 1);
		
		double genMw = ModelStringUtil.getDouble(strAry[2], 0.0);
		double genMvar = ModelStringUtil.getDouble(strAry[3], 0.0);
		contriGen.setPower(BaseDataSetter.createPowerValue(genMw, genMvar, ApparentPowerUnitType.MVA));

		BaseJaxbHelper.addOwner(contriGen, 
				strAry[18], ModelStringUtil.getDouble(strAry[19], 0.0), 
				strAry[20], ModelStringUtil.getDouble(strAry[21], 0.0), 
				strAry[22], ModelStringUtil.getDouble(strAry[23], 0.0), 
				strAry[24], ModelStringUtil.getDouble(strAry[25], 0.0));

		// processing Equiv Gen Data
		if (!contriGen.isOffLine()) {
			// power may exist already
			if (equivGen.getPower() != null) {
				genMw += equivGen.getPower().getRe();
				genMvar += equivGen.getPower().getIm();
			}
			equivGen.setPower(BaseDataSetter.createPowerValue(genMw, genMvar, ApparentPowerUnitType.MVA));

			final double vSpecPu = ModelStringUtil.getDouble(strAry[6], 1.0);
			if (genData.getEquivGen().getCode() == LFGenCodeEnumType.SWING) {
				equivGen.setDesiredVoltage(BaseDataSetter.createVoltageValue(vSpecPu, VoltageUnitType.PU));
			}
			else {
				// qmax, gmin in Mvar. there may exist already
				double max = ModelStringUtil.getDouble(strAry[4], 0.0);
				double min = ModelStringUtil.getDouble(strAry[5], 0.0);
				if (equivGen.getQLimit() != null) {
					max += equivGen.getQLimit().getMax();
					min += equivGen.getQLimit().getMin();
				}
				equivGen.setDesiredVoltage(BaseDataSetter.createVoltageValue(vSpecPu, VoltageUnitType.PU));
				equivGen.setQLimit(BaseDataSetter.createReactivePowerLimit(max, min, ReactivePowerUnitType.MVAR));

				// Desired volts (pu) (This is desired remote voltage if this bus is controlling another bus.)
				/*  IREG  */
		      	final int iReg = ModelStringUtil.getInt(strAry[7], 0);
				if (iReg > 0) {
					final String reBusId = AbstractModelParser.BusIdPreFix+strAry[7];
					equivGen.setRemoteVoltageControlBus(parser.createBusRef(reBusId));
				}
			}
		}
//		else {  there might be multiple gen on a bus
//			if (genData.getEquivGen() == null)
//				genData.setEquivGen(parser.getFactory().createLoadflowGenDataXmlType());
//			genData.getEquivGen().setCode(LFGenCodeEnumType.OFF);
//		}
		
		//System.out.println(busRec.toString());
    }

	private static String[] getBusDataFields(final String lineStr) {
		final String[] strAry = new String[11];

		StringTokenizer st = new StringTokenizer(lineStr,",");
		for (int i = 0; i < 11; i++)
			strAry[i]=st.nextToken().trim();
		return strAry;
	}
	
	private static String[] getLoadDataFields(final String lineStr) {
		final String[] strAry = new String[12];
  		StringTokenizer st = new StringTokenizer(lineStr, ",");
		for (int i = 0; i < 12; i++)
			strAry[i]=st.nextToken().trim();
  		return strAry;
	}	
	
	
	private static String[] getGenDataFields(final String lineStr) {
		final String[] strAry = new String[26];		
		//I,ID,PG,QG,QT,QB,VS,IREG,MBASE,ZR,ZX,RT,XT,GTAP,STAT,RMPCT,PT,PB,
  		StringTokenizer st = new StringTokenizer(lineStr, ",");
		for (int i = 0; i < 18; i++)
			strAry[i]=st.nextToken().trim();

        //O1,F1,...,O4,F4
		for (int i = 18; i < 26; i++)
			strAry[i]="0";
		if (st.hasMoreTokens()) {
			strAry[18]=st.nextToken().trim();
	  		strAry[19]=st.nextToken().trim();
		}
		if (st.hasMoreTokens()) {
			strAry[20]=st.nextToken().trim();
	  		strAry[21]=st.nextToken().trim();
		}
		if (st.hasMoreTokens()) {
			strAry[22]=st.nextToken().trim();
	  		strAry[23]=st.nextToken().trim();
		}
		if (st.hasMoreTokens()) {
			strAry[24]=st.nextToken().trim();
	  		strAry[25]=st.nextToken().trim();
		}
				
		return strAry;
	}
}
