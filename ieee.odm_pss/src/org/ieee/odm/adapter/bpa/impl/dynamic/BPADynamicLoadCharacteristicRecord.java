/*
 * @(#)BPADynamicRecord.java   
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
 * @Author Stephen Hau
 * @Version 1.0
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.adapter.bpa.impl.dynamic;

import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.adapter.bpa.impl.BusRecord;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.DynamicLoadIEEEStaticLoadXmlType;
import org.ieee.odm.schema.IDRefRecordXmlType;
import org.ieee.odm.schema.LoadCharacteristicLocationEnumType;
import org.ieee.odm.schema.LoadCharacteristicModelListXmlType;
import org.ieee.odm.schema.LoadCharacteristicTypeEnumType;
import org.ieee.odm.schema.LoadCharacteristicXmlType;

public class BPADynamicLoadCharacteristicRecord {
	
public static void processLoadCharacteristicData(String str, DStabModelParser parser,LoadCharacteristicXmlType load){
	final String[] strAry= getLoadDataFields(str); 	
	
	// TODO comment out to pass compile
	

	//busId
	String busName="";
	if(!strAry[1].equals("")){
		busName=strAry[1];
		String BusId="";
		try {
			BusId = BusRecord.getBusId(busName);
		} catch (ODMException e) {
			e.printStackTrace();
		}
		BusXmlType bus = parser.getBus(BusId);
		load.setLocation(LoadCharacteristicLocationEnumType.AT_BUS);
		//TODO add the bus Id ref record
//		load.setLocationId(bus);
		
		
	}		
	//zone name
	String zoneId="";
	if(!strAry[3].equals("")){
		zoneId=strAry[3];
		load.setLocation(LoadCharacteristicLocationEnumType.AT_ZONE);
		//TODO
//		load.setLocationId(parser.getDStabNet().get)
     }
	//area name
	String areaId="";
	if(!strAry[4].equals("")){
		areaId=strAry[4];
		load.setLocation(LoadCharacteristicLocationEnumType.AT_AREA);
		//TODO
//		load.setLocationId(value)
	}
	if(strAry[0].equals("LA")||strAry[0].equals("LB")){
		load.setLoadXmlType(LoadCharacteristicTypeEnumType.IEEE_STATIC_LOAD);
		DynamicLoadIEEEStaticLoadXmlType staLoad=parser.getFactory().createDynamicLoadIEEEStaticLoadXmlType();

		
		double a1=0.0;
		if(!strAry[5].equals("")){
			a1= new Double(strAry[5]).doubleValue();
			staLoad.setA1(a1);
			staLoad.setN1(2);
		}
		//qz
		double a4=0.0;
		if(!strAry[6].equals("")){
			a4= new Double(strAry[6]).doubleValue();
			staLoad.setA4(a4);
			staLoad.setN4(2);
		}			
		//pi
		double a2=0.0;
		if(!strAry[7].equals("")){
			a2= new Double(strAry[7]).doubleValue();
			staLoad.setA2(a2);
			staLoad.setN2(1);
		}
		//qi
		double a5=0.0;
		if(!strAry[8].equals("")){
			a5= new Double(strAry[8]).doubleValue();
			staLoad.setA5(a5);
			staLoad.setN5(1);
		}
		// pp
		double a3=0.0;
		if(!strAry[9].equals("")){
			a3= new Double(strAry[9]).doubleValue();
			staLoad.setA3(a3);
			staLoad.setN3(0);
		}
		//qp
		double a6=0.0;
		if(!strAry[10].equals("")){
			a6= new Double(strAry[10]).doubleValue();
			staLoad.setA6(a6);
			staLoad.setN6(0);
		}			
		//pf
		double a9=0.0;
		if(!strAry[11].equals("")){
			a9= new Double(strAry[11]).doubleValue();
			staLoad.setA9(a9);				
		}
		// qf
		double a10=0.0;
		if(!strAry[12].equals("")){
			a10= new Double(strAry[12]).doubleValue();
			staLoad.setA10(a10);				
		}
		//Ldp
		double a7=0.0;
		if(!strAry[13].equals("")){
			a7= new Double(strAry[13]).doubleValue();
			staLoad.setA7(a7);				
		}		
		//Ldq
		double a8=0.0;
		if(!strAry[14].equals("")){
			a8= new Double(strAry[14]).doubleValue();
			staLoad.setA8(a8);
			
			
		LoadCharacteristicModelListXmlType loadModelList=parser.getFactory().createLoadCharacteristicModelListXmlType();
		loadModelList.setIEEEStaticLoad(staLoad);//TODO why a setter but not an "adder" is used here? 
		load.setLoadModel(loadModelList);			
		}
	}
//	else if(strAry[0].equals("MI")){
//	// to do	
//	}
	
}
private static String[] getLoadDataFields(String str){
	final String[] strAry= new String[19];
	
	try{
		if(str.substring(0, 2).trim().equals("LA")||str.substring(0, 2).trim().equals("LB")){
			strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
			//busId
			strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
			//bus Voltage
			strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
			//zone name
			strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 17).trim();
			//area name
			strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,18, 27).trim();
			//pz
			strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,28, 32).trim();
			//qz
			strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,33, 37).trim();
			//pi
			strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,38, 42).trim();
			//qi
			strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,43, 47).trim();
			// pp
			strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,48, 52).trim();
			//qp
			strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,53, 57).trim();
			//pf
			strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,58, 62).trim();
			// qf
			strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,63, 67).trim();
			//Ldp
			strAry[13]=ModelStringUtil.getStringReturnEmptyString(str,68, 72).trim();
			//Ldq
			strAry[14]=ModelStringUtil.getStringReturnEmptyString(str,73, 77).trim();
			
				
	   }else if(str.substring(0, 2).trim().equals("MI")){
			
		 }
	
	}catch( Exception e){
		ODMLogger.getLogger().severe(e.toString());
	}
	return strAry;
}
	
}
