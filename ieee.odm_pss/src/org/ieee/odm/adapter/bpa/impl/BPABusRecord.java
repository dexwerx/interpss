/*
 * @(#)BPABusRecord.java   
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
package org.ieee.odm.adapter.bpa.impl;

import java.util.Hashtable;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;

public class BPABusRecord {
	private static final int swingBus=1;
	private static final int pqBus=2;
	private static final int pvBus=3;		
	private static final int pvBusNoQLimit=4;
	private static final int supplementaryBusInfo=5;
	
	/*
	 *  BPA data format does not have bus number, only has bus name. 
	 *  Bus number is generated and a looupTable for busName -> BusId
	 */
	private static long busCnt = 0;
	private static Hashtable<String,String> busIdLookupTable = null;
	
	/**
	 * reset the bus count and lookup table
	 */
	public static void resetBusCnt() { 
		busCnt = 0; 
		busIdLookupTable = new Hashtable<String,String>();
	}
	/**
	 * get bus Id and add an item to the lookup table for busName -> busId
	 * 
	 * @param busName
	 * @return
	 */
	private static String createBusId(String busName) { 
		String id = AbstractModelParser.BusIdPreFix + ++busCnt;
		busIdLookupTable.put(busName.trim(), id);
		return id;
	}
	/**
	 * get busId from busName using the lookup table
	 * 
	 * @param busName
	 * @return
	 */
	public static String getBusId(String busName) throws ODMException { 
		String id =  busIdLookupTable.get(busName.trim()); 
		if (id == null) {
			throw new ODMException("Bus id not found, bus name: " + busName);
		}
		return id; 
	}
	
	public static void processBusData(final String str, AclfModelParser parser) throws Exception {		
		final double baseMVA = parser.getAclfNet().getBasePower().getValue();

		// parse the input data line
		final String[] strAry = getBusDataFields(str);
		
        //busType	
		int busType=0;
		String stemp = strAry[0];
		if(stemp.equals("B")||stemp.equals("BC")||stemp.equals("BT")||stemp.equals("BV")){
			busType=pqBus;
		}
		else if(stemp.equals("BQ")||stemp.equals("BG")||stemp.equals("BF")){
			busType=pvBus;
		}
		else if(stemp.equals("BE")){
			busType=pvBusNoQLimit;
		}
		else if(stemp.equals("BS")){
			busType=swingBus;
		}
		else if (stemp.equals("+")){
			busType=supplementaryBusInfo;
		}
		
		/*
		 * set bus record attributes
		 * =========================
		 */
		
		// modification code
		final String modCode=strAry[1];
		//owner code
		final String ownerName=strAry[2];
		//Name
		final String busName = strAry[3];
		final String busId =  createBusId(busName);
		ODMLogger.getLogger().fine("Bus data loaded, busName: " + busId);

		LoadflowBusXmlType busRec = null;
		try {
			busRec = parser.createAclfBus(busId);
		} catch (ODMException e) {
			ODMLogger.getLogger().severe(e.toString());
			return;
		}		
		
		busRec.setId(busId);		
		busRec.setName(busName);
		
		// TODO set bus owner
        //busRec.getOwnerList().getOwner().get(0).setName(ownerName);
		//basekv
		double baseKv=100.0;
		if(!strAry[4].equals("")){
			baseKv= new Double(strAry[4]).doubleValue();
		}
		busRec.setBaseVoltage(BaseDataSetter.createVoltageValue(baseKv, VoltageUnitType.KV));

		// TODO area name??
		//busRec.setAreaName(value);
		//zone name
		final String zoneName= strAry[5];
		busRec.setZoneName(zoneName);
		
		
		
		/*
		 * Parse Loadflow data
		 * ===================
		 */
		
		//load mw and mvar
		double loadMw=0.0;
		double loadMvar=0.0;
		if(!strAry[6].equals("")){
			loadMw = new Double(strAry[6]).doubleValue();			
		}
		if(!strAry[7].equals("")){			
			loadMvar = new Double(strAry[7]).doubleValue();
		}
		
		//Shunt mw--> G 
		//Shunt var B -->B
		double shuntMw=0.0;
		double shuntVar=0.0;
		if(!strAry[8].trim().equals("") && !strAry[8].trim().equals(".")){
			shuntMw= new Double(strAry[8]).doubleValue();
		}		
		if(!strAry[9].equals("")){
			shuntVar= new Double(strAry[9]).doubleValue();
		}		       
		final double g=ModelStringUtil.getNumberFormat(shuntMw/baseMVA);
		final double b=ModelStringUtil.getNumberFormat(shuntVar/baseMVA);

		// set pGenMax
		double pGenMax=0.0;
		if(!strAry[10].equals("")){
			pGenMax= new Double(strAry[10]).doubleValue();
		}
		double pGen=0.0;
		if(!strAry[11].equals("")){
			pGen= new Double(strAry[11]).doubleValue();
		}		
		
		// qGen for PQ bus, qGenMax for PV bus
		double qGenOrQGenMax=0.0;
		if(!strAry[12].equals("")){
			qGenOrQGenMax=new Double(strAry[12]).doubleValue();
		}
		
		// TODO - not sure what its meaning
		if(strAry[13].equals(".")){
			ODMLogger.getLogger().info(str+"str 13 is .");
		}
		
		double qGenMin=0.0;
		if(!strAry[13].equals("")){
			qGenMin= new Double(strAry[13]).doubleValue();
		}
		
		// TODO - not sure what its meaning
		if(strAry[14].equals(".")){
			ODMLogger.getLogger().info(str+"str 14 is .");
		}
		
		double vpu=0.0;
		if(!strAry[14].equals("")){
			vpu = new Double(strAry[14]).doubleValue();
			if(vpu!=0.0) {
				if(vpu > 10)
					vpu=vpu*0.001; //F4.3
			}
		}		
		
		//for swing bus, this value is angle(degrees), for others it is vmin.
		double vMinOrAngDeg=0.0;
		if(!strAry[15].equals("")){
			vMinOrAngDeg= new Double(strAry[15]).doubleValue();			
		}	
		
		double varSupplied=0.0;
		if(!strAry[18].equals("")){
			varSupplied= new Double(strAry[18]).doubleValue();
		}	
		
		/*
		 * process data and map to the ODM bus record
		 * ==========================================
		 */
		if(loadMw != 0.0 || loadMvar != 0.0 || 
				pGen!=0.0|| qGenOrQGenMax!=0.0 ||vpu!=0.0||
				vMinOrAngDeg!=0.0||pGenMax!=0.0
				||g!=0||b!=0) {
			// set G B
			if (g != 0.0 || b != 0.0) {
				busRec.setShuntY(BaseDataSetter.createYValue(g, b,YUnitType.PU));
			}	
			
			// set load
			if (loadMw != 0.0 || loadMvar != 0.0) {
				AclfDataSetter.setLoadData(busRec,
						LFLoadCodeEnumType.CONST_P, loadMw,
						loadMvar, ApparentPowerUnitType.MVA);
			}
			
			if(busType==swingBus){
				// set bus voltage
					busRec.setVoltage(BaseDataSetter.createVoltageValue(vpu, VoltageUnitType.PU));
				// set bus angle
				busRec.setAngle(BaseDataSetter.createAngleValue(vMinOrAngDeg, AngleUnitType.DEG));
				
				//set gen data
				if(pGen!=0.0||qGenOrQGenMax!=0.0){				
					AclfDataSetter.setGenData(busRec,
							LFGenCodeEnumType.SWING,
							vpu, VoltageUnitType.PU,
							vMinOrAngDeg, AngleUnitType.DEG,
							pGen, 0.0, ApparentPowerUnitType.MVA);
				}
				
				// set Q limit
				if(qGenOrQGenMax!=0.0||qGenMin!=0.0){
					busRec.getGenData().getEquivGen().setQLimit(BaseDataSetter.createReactivePowerLimit( 
							qGenOrQGenMax, qGenMin, ReactivePowerUnitType.MVAR));				
				}
				
				// set P limit
				if(pGenMax!=0.0){
					busRec.getGenData().getEquivGen().setPLimit(BaseDataSetter.createActivePowerLimit(
							pGenMax, 0, ActivePowerUnitType.MW));
				}	
			}
			else if(busType==pqBus){			
				AclfDataSetter.setGenData(busRec,
						LFGenCodeEnumType.NONE_GEN, 
						1.0, VoltageUnitType.PU, 0.0, AngleUnitType.DEG);
				if(pGen!=0.0||qGenOrQGenMax!=0.0){
					busRec.getGenData().getEquivGen().setCode(LFGenCodeEnumType.PQ);
					busRec.getGenData().getEquivGen()
						.setPower(BaseDataSetter.createPowerValue(
							pGen, qGenOrQGenMax, ApparentPowerUnitType.MVA));
			    // for a PQ Bus, it is not proper to set the Vlimit;
//				// set V limit    
//				if(vpu!=0 ||vMinOrAngDeg!=0){
//					    busRec.getGenData().getEquivGen()
//					    	.setVoltageLimit(BaseDataSetter.createVoltageLimit(
//					    			vpu, vMinOrAngDeg, VoltageUnitType.PU));
//					}
				if(pGen!=0.0&&vpu!=0){
						ODMLogger.getLogger().info("This bus seems to be a GenPV bus: "+ busId+","+busName
								+" ,please check! ");
					
				}
					
				}
			}
			else if(busType==pvBus || busType==pvBusNoQLimit){
				// set bus voltage
					busRec.setVoltage(BaseDataSetter.createVoltageValue(vpu, VoltageUnitType.PU));
				// set gen data
				AclfDataSetter.setGenData(busRec,
							LFGenCodeEnumType.PV, 
							vpu, VoltageUnitType.PU, 0.0, AngleUnitType.DEG,
							pGen, 0.0, ApparentPowerUnitType.MVA);
				// set Q limit
				if(qGenOrQGenMax!=0.0||qGenMin!=0.0){
					busRec.getGenData().getEquivGen().setQLimit(BaseDataSetter.createReactivePowerLimit( 
							qGenOrQGenMax, qGenMin, ReactivePowerUnitType.MVAR));	
				// for "BE" type the limit if disabled
					if (busType==pvBusNoQLimit)
						busRec.getGenData().getEquivGen().getQLimit().setActive(false);
				}
				// set P limit
				if(pGenMax!=0.0){
					busRec.getGenData().getEquivGen().setPLimit(BaseDataSetter.createActivePowerLimit(
							pGenMax, 0, ActivePowerUnitType.MW));
				}	
				
			}
			//a bus recored starting with"+", is to supplement
			// info(e.g.,ZIP type load and generation) to an existing bus record with the same busId
			if(busType==supplementaryBusInfo){
				
				LoadflowBusXmlType Bus=parser.getAclfBus(busId);
				final String loadType=strAry[5];
				//loadType: *I or 01 for constI,  and *P or 02 for constP
				final double p=new Double(strAry[6]).doubleValue();
				final double q=new Double(strAry[7]).doubleValue();
				//TODO how to set constI type load
				
				if(!strAry[9].equals("")||!strAry[8].equals("")){
					final double ShuntG=new Double(strAry[8]).doubleValue();
					final double ShuntB=new Double(strAry[9]).doubleValue();
					
					double re=ModelStringUtil.getNumberFormat(ShuntG/baseMVA); // x(pu)=Var/baseMVA
					double im=ModelStringUtil.getNumberFormat(ShuntB/baseMVA);
					if(re!=0.0||im!=0.0){
						AclfDataSetter.addBusShuntY(Bus, re, im, YUnitType.PU);	
					}
					
				}
			}
				
			//for BG and BX, controlled bus name and voltage
			// desired bus voltage is specified in strAry[14], equals to vpu
			final String controlledBus= strAry[16];
			double controlledBusRatedVol=0.0;
			if(!strAry[17].equals("")){
				controlledBusRatedVol = new Double(strAry[17]).doubleValue();			
			}
			
			if(strAry[0].equals("BG")||strAry[0].equals("BX")){
				if(!controlledBus.equals("")) {			
					busRec.getGenData().getEquivGen().getRemoteVoltageControlBus().setIdRef(controlledBus);
					busRec.getGenData().getEquivGen().setDesiredVoltage(BaseDataSetter.createVoltageValue(
							controlledBusRatedVol, VoltageUnitType.PU));
				}
			}
			
			
		}						
	}
	
	private static String[] getBusDataFields(final String str) throws Exception {
		final String[] strAry = new String[19];
/* sample data
B     XIANLS= 500.XX305.3 -215.                                                                      
*/
		//Columns  1- 2   Bus type
	    strAry[0] = ModelStringUtil.getStringReturnEmptyString(str,1, 2); 
		//Columns  3 code for modification			
		strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,3, 3).trim();
		//Columns 3-5   owner code
		
		strAry[2] = ModelStringUtil.getStringReturnEmptyString(str,4, 6).trim();
		
		//to process the Chinese characters first, if any.
		String tem=ModelStringUtil.getStringReturnEmptyString(str,7, 14).trim();
		int chineseCharNum=ModelStringUtil.getChineseCharNum(tem);
		
		//Columns 6-13 busName  
		strAry[3] = ModelStringUtil.getStringReturnEmptyString(str,7, 14-chineseCharNum).trim();
		
		String str2=chineseCharNum==0?str:ModelStringUtil.replaceChineseChar(str);
		//14-17 rated voltage
		strAry[4] = ModelStringUtil.getStringReturnEmptyString(str2,15, 18).trim();

		//Columns 18-19   zone name for Bus card, load type for complementary Bus card.
		strAry[5] = ModelStringUtil.getStringReturnEmptyString(str2,19, 20).trim();
		//Columns 20-24   Load MW [F] *
		//Columns 25-29   Load MVAR [F] *
		strAry[6] = ModelStringUtil.getStringReturnEmptyString(str2,21, 25).trim();
		strAry[7] = ModelStringUtil.getStringReturnEmptyString(str2,26, 30).trim();			
		//Columns 30-33   shunt MW [F] *
		//Columns 34-39   shunt MVAR [F] *
		strAry[8] = ModelStringUtil.getStringReturnEmptyString(str2,31, 34).trim();
		strAry[9] = ModelStringUtil.getStringReturnEmptyString(str2,35, 38).trim();	
		// Columns 38-41 pmax
		// Columns 42-46 pgen
		strAry[10] = ModelStringUtil.getStringReturnEmptyString(str2,39, 42).trim();			
		strAry[11] = ModelStringUtil.getStringReturnEmptyString(str2,43, 47).trim();
		//Qmax Qmin
		strAry[12]= ModelStringUtil.getStringReturnEmptyString(str2,48, 52).trim();
		strAry[13]= ModelStringUtil.getStringReturnEmptyString(str2,53, 57).trim();			
		//scheduled V or Vmax, Vmin
		strAry[14]= ModelStringUtil.getStringReturnEmptyString(str2,58, 61).trim();			
		strAry[15]=ModelStringUtil.getStringReturnEmptyString(str2,62, 65).trim();
		//remoted busName, rated voltage
		strAry[16]= ModelStringUtil.getStringReturnEmptyString(str2,66, 73).trim();
		strAry[17]= ModelStringUtil.getStringReturnEmptyString(str2,74, 77).trim();
		// used in remoted bus control, var fraction
		strAry[18]= ModelStringUtil.getStringReturnEmptyString(str2,78, 80).trim();
		return strAry;
	}
	
	
	
}
	
	
