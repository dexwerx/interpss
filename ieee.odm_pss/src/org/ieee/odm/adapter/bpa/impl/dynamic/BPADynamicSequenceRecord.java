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
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.ClassicMachineXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.LoadCharacteristicXmlType;


public class BPADynamicSequenceRecord {

	public static void processSequenceData(String str, DStabModelParser parser){				
		
		final String strAry[]=getSequenceDataFields(str);
		
/*		
		// positive sequence data		
		// to do  
		// zero sequence data		
		if(strAry[0].equals("XO")){
			ZeroSequenceDataListXmlType.XfrZeroList.XfrZero xfrZero=
				XBeanTranStabSimuHelper.addNewXfrZero(tranSimu);		
			
			//bus1
			String bus1=strAry[1];
			xfrZero.addNewBusI().setName(bus1);			
			//bus2
			String bus2=strAry[3];
			xfrZero.addNewBusJ().setName(bus2);			
			//zrLocation
			int location= new Integer(strAry[5]).intValue();
			if(location==1){
				xfrZero.setConectionLocation(ZeroSequenceDataListXmlType.
						 XfrZeroList.XfrZero.ConectionLocation.AT_BUS_1);
			}else if(location==2){
				xfrZero.setConectionLocation(ZeroSequenceDataListXmlType.
						 XfrZeroList.XfrZero.ConectionLocation.AT_BUS_2);
			}else {
				xfrZero.setConectionLocation(ZeroSequenceDataListXmlType.
						 XfrZeroList.XfrZero.ConectionLocation.BETWEEN_BUS_1_AND_BUS_2);
			}			
			//par
			String cirId="1";
			if(!strAry[6].equals("")){
				cirId=strAry[6];				
			}			
			xfrZero.setCirId(cirId);
			//X0
			double x0=ModelStringUtil.getDouble(strAry[7], 0.0);
			xfrZero.setX1(x0);
						
			//R0
			double r0=ModelStringUtil.getDouble(strAry[8], 0.0);
			xfrZero.setR1(r0);
		}else if(strAry[0].equals("XR")){
			ZeroSequenceDataListXmlType.ShuntLoadZeroList.ShuntLoadZero SHZero=
				XBeanTranStabSimuHelper.addNewShuntLoadZero(tranSimu);		
			//bus1
			String bus1=strAry[1];
			SHZero.addNewBusId().setName(bus1);	    	
    		//r0
			double r0=ModelStringUtil.getDouble(strAry[3], 0.0);
			SHZero.setRZer(r0);
			  		
    		//x0
			double x0=ModelStringUtil.getDouble(strAry[4], 0.0);
			SHZero.setXZer(x0);
			    		
		}else if(strAry[0].equals("LO")){
			ZeroSequenceDataListXmlType.LineZeroList.LineZero lineZero=
				XBeanTranStabSimuHelper.addNewLineZero(tranSimu);
			//bus1
			String bus1=strAry[1];
			lineZero.addNewFBusId().setName(bus1);			
			//bus2
			String bus2=strAry[3];
		    lineZero.addNewTBusId().setName(bus2);	
    		
		    //par
			String cirId="";
			if(!strAry[6].equals("")){
				cirId=strAry[6];
				lineZero.setCirId(cirId);
			}			
    		//R0			
			double r0=ModelStringUtil.getDouble(strAry[7], 0.0);
			lineZero.setRLineZer(r0);
			    		
    		//X0
			double x0=ModelStringUtil.getDouble(strAry[8], 0.0);
			lineZero.setXLineZer(x0);
			     		
    		//G1
			double g1=ModelStringUtil.getDouble(strAry[9], 0.0);
			lineZero.setGfZer(g1);
			   		
    		//B1
			double b1=ModelStringUtil.getDouble(strAry[10], 0.0);
			lineZero.setBfZer(b1);
			     		
    		//G2
    		double g2=ModelStringUtil.getDouble(strAry[11], 0.0);
			lineZero.setGtZer(g2);
			     		
    		//B2
			double b2=ModelStringUtil.getDouble(strAry[12], 0.0);
			lineZero.setBtZer(b2);
		}else if(strAry[0].equals("LM")){
			ZeroSequenceDataListXmlType.MutualImpedanceZeroList.MutualImpedanceZero mutZero=
				XBeanTranStabSimuHelper.addNewMutualZero(tranSimu);
			//bus1
			String bus1=strAry[1];
			mutZero.addNewBranch1BusI().setName(bus1);			
			//bus2
			String bus2=strAry[3];
			mutZero.addNewBranch1BuJ().setName(bus2);
			//par
			String cirId="";
			if(!strAry[5].equals("")){
				cirId=strAry[5];
				mutZero.setBranch1CirId(cirId);
			}	    		
    		//busK line2
			String bus3=strAry[6];
			mutZero.addNewBranch2BusK().setName(bus3);			
			//bus2
			String bus4=strAry[8];
			mutZero.addNewBranch2BusL().setName(bus4);    		
    		//par2
			String cir2Id="";
			if(!strAry[10].equals("")){
				cirId=strAry[10];
				mutZero.setBranch2CirId(cir2Id);
			}	    		
			//R0			
			double r0=ModelStringUtil.getDouble(strAry[11], 0.0);
			mutZero.setRM(r0);
			      		
    		//X0
			double x0=ModelStringUtil.getDouble(strAry[12], 0.0);
			mutZero.setXM(x0);
		}
*/		
	}
	
	public static void processNegativeData( DStabModelParser parser){
/*		
		// negative sequence generator data
		
		for(GeneratorXmlType gen:tranSimu.getDynamicDataList().getBusDynDataList()
				.getGeneratorDataList().getGeneratorArray()){
			double xd1=0.0;
			double x2=0.0;
			double tq01=0.0;
			if(gen.getGeneratorType().equals(GeneratorXmlType.GeneratorType.SUBTRANS_MODEL)){
				SubTransientMachineXmlType subGen=
					gen.getGeneratorModel().getSubTransientModel();
				xd1=subGen.getXd1();
				tq01=subGen.getTq01().getValue();
				
				
			}else if(gen.getGeneratorType().equals(GeneratorXmlType.GeneratorType.TRANSIENT_MODEL)){
				TransientMachineXmlType tranGen=
					gen.getGeneratorModel().addNewTransModel();
					xd1=tranGen.getXd1();
					tq01=tranGen.getTq01().getValue();
			}else if(gen.getGeneratorType().equals(GeneratorXmlType.GeneratorType.CLASSICAL_MODEL)){
				ClassicMachineXmlType claGen=
					gen.getGeneratorModel().getClassicalModel();
				xd1=claGen.getXd1();
				tq01=0.0;;				
			}
			//non-salient pole machine
			if(tq01!=0.0){
				x2=1.22*0.65*xd1;
			}
			//salient pole generator 
			else{
				x2=0.65*xd1;
			}
			NegativeSequenceDataListXmlType.GeneratorNegativeList.GeneratorNegative xfrNeg=
				XBeanTranStabSimuHelper.addNewGenNeg(tranSimu);
			
			String busId=gen.getLocatedBus().getName();
			String genId="";
			if(gen.getGenId()!=null){
				genId=gen.getGenId().getName();
				xfrNeg.addNewMacId().setName(genId);
			}
			xfrNeg.addNewBusId().setName(busId);
			xfrNeg.setZXNeg(x2);
			
		}
		// negative load data
		
		for( LoadCharacteristicXmlType load: tranSimu.getDynamicDataList().
				getBusDynDataList().getLoadCharacteristicDataList().getLoadArray() ){
			NegativeSequenceDataListXmlType.ShuntLoadNegativeList.ShuntLoadNegative loadNeg=
				XBeanTranStabSimuHelper.addNewShuntLoadNeg(tranSimu);
			
			if(load.getLocation().equals(LoadCharacteristicXmlType.Location.AT_AREA)){
				loadNeg.setLoadLocation(NegativeSequenceDataListXmlType.ShuntLoadNegativeList.
						ShuntLoadNegative.LoadLocation.AT_AREA);
				
			}else if(load.getLocation().equals(LoadCharacteristicXmlType.Location.AT_BUS)){
				loadNeg.setLoadLocation(NegativeSequenceDataListXmlType.ShuntLoadNegativeList.
						ShuntLoadNegative.LoadLocation.AT_BUS);
			}else {
				loadNeg.setLoadLocation(NegativeSequenceDataListXmlType.ShuntLoadNegativeList.
						ShuntLoadNegative.LoadLocation.AT_ZONE);
			}			
			loadNeg.addNewLocationId().setName(load.getLocationId().getName());
// TODO: hard coded values
			loadNeg.setRNeg(0.19);
			loadNeg.setXNeg(0.36);
		}
*/			
	}	
	
	private static String[] getSequenceDataFields(String str){
		final String[] strAry= new String[13];
		
		try{
			if(str.substring(0, 2).startsWith("XO")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//bus1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//bus1 Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//bus2
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,19, 26).trim();
	    		//bus2 Voltage
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,27, 30).trim();
	    		//zrLocation
	    		strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,32, 32).trim();
	    		//par
	    		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,34, 34).trim();
	    		//X0
	    		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,38, 44).trim();
	    		//R0
	    		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,45, 51).trim();			
				
			}else if(str.substring(0, 2).startsWith("XR")){			
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//bus1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//bus1 Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//r0
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,22, 28).trim();
	    		//x0
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,29, 35).trim();
			}else if(str.substring(0, 2).startsWith("LO")){			
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//bus1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//bus1 Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//bus2
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,19, 26).trim();
	    		//bus2 Voltage
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,27, 30).trim();    		
	    		//par
	    		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,33, 33).trim();
	    		//R0
	    		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,36, 42).trim();
	    		//X0
	    		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,43, 49).trim();
	    		//G1
	    		strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,50, 56).trim();
	    		//B1
	    		strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,57, 63).trim();
	    		//G2
	    		strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,64, 70).trim();
	    		//B2
	    		strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,71, 77).trim();
			}else if(str.substring(0, 2).startsWith("LM")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//busI line 1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//busI Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//busJ line 1
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,19, 26).trim();
	    		//busJ Voltage
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,27, 30).trim();    		
	    		//par
	    		strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,33, 33).trim();
	    		//busK line2
	    		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,36, 43).trim();
	    		//busk voltage
	    		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,44, 47).trim();
	    		//busL line2
	    		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,50, 57).trim();
	    		//busL voltage
	    		strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,58, 61).trim();
	    		//par2
	    		strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,64, 64).trim();
	    		//r
	    		strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,67, 73).trim();
	    		//x
	    		strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,74, 80).trim();
			}
		}catch(Exception e){
			ODMLogger.getLogger().severe(e.toString());
		}
				
		return strAry;
	}	
}
