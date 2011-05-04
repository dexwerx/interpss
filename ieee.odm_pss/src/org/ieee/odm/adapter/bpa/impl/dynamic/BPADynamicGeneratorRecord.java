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

import java.text.NumberFormat;

import org.ieee.odm.adapter.bpa.impl.BusRecord;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.model.dstab.DStabDataSetter;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.model.dstab.DStabParserHelper;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BusRecordXmlType;
import org.ieee.odm.schema.ClassicMachineXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabNetXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.Eq11Ed11MachineXmlType;
import org.ieee.odm.schema.Eq1Ed1MachineXmlType;
import org.ieee.odm.schema.EquiMachineXmlType;
import org.ieee.odm.schema.TimePeriodUnitType;
import org.ieee.odm.schema.VoltageUnitType;

public class BPADynamicGeneratorRecord {
	
	public static void processGeneratorData(String str, DStabModelParser parser) throws ODMException {
    	final String strAry[]=getGeneratorDataFields(str);
    	
    	DStabNetXmlType net = parser.getDStabNet();
    	
    	if (str.substring(0,2).trim().equals("MC")){
    		String busId = BusRecord.getBusId(strAry[1]);
        	DStabBusXmlType bus = parser.getDStabBus(busId);
    		DynamicGeneratorXmlType dynGen = DStabParserHelper.getDynamicGenRec(bus);
    		ClassicMachineXmlType mach = DStabParserHelper.createClassicMachine(dynGen);
    		
    		double ratedVoltage=ModelStringUtil.getDouble(strAry[2], 0.0);
	   		dynGen.setRatedVoltage(DStabDataSetter.createVoltageValue(ratedVoltage, VoltageUnitType.KV));
	   		
	   		String dynGenId="1";
    		if(!strAry[3].equals("")){
    			dynGenId=strAry[3];    			
    		}
	   		dynGen.setId(dynGenId);
	   		
	   		double Emws=ModelStringUtil.getDouble(strAry[4], 0.0);
			// infinite bus
			if(Emws==999999){
				mach.setH(999999);				
			} else{
				double MvaBase=ModelStringUtil.getDouble(strAry[7], net.getBasePower().getValue());
				
				double h=0.0;
				if(Emws!=0.0){
					h=Emws/MvaBase;
					NumberFormat ddf1= NumberFormat.getInstance();
					ddf1.setMaximumFractionDigits(4);
					h= new Double(ddf1.format(h)).doubleValue();
					mach.setH(h);
				}
				
				double pContri=ModelStringUtil.getDouble(strAry[5], 1.0);
				double qContri=ModelStringUtil.getDouble(strAry[6], 1.0);
				
				if(pContri<=1.0&&pContri!=0.0){
				    pContri=pContri*100;				
					dynGen.setPContributionPercent(pContri);
				}
				if(qContri<=1.0&&qContri!=0.0){
					qContri=qContri*100;
					dynGen.setQContributionPercent(qContri);
				}
				//TODO
				dynGen.setRatedPower(DStabDataSetter.createActivePowerValue(MvaBase, ActivePowerUnitType.MW));				
			}
			
			double xd1=ModelStringUtil.getDouble(strAry[9], 0.0);
    		mach.setXd1(xd1);
    			
    		double D=ModelStringUtil.getDouble(strAry[18], 2.0);// why 2.0 by default?
			mach.setD(D);
    	}
    	/*
    	 * M record is only to store the sub-transient info, 
    	 * this, together with MF/MG record,represents a full machine model considering damper.
    	 */
    	else if(str.substring(0, 2).trim().equals("M")){
    		String busId = BusRecord.getBusId(strAry[1]);
        	DStabBusXmlType bus = parser.getDStabBus(busId);
    		DynamicGeneratorXmlType dynGen = DStabParserHelper.getDynamicGenRec(bus);
    		Eq11Ed11MachineXmlType mach = DStabParserHelper.createEq11Ed11MachineXmlType(dynGen);
    		
    		double ratedVoltage=ModelStringUtil.getDouble(strAry[2], 0.0);
	   		dynGen.setRatedVoltage(DStabDataSetter.createVoltageValue(ratedVoltage, VoltageUnitType.KV));
	   			   		
	   		String dynGenId="1";
    		if(!strAry[3].equals("")){
    			dynGenId=strAry[3];    			
    		}
	   		dynGen.setId(dynGenId);
	   		
	   		String owner="";
    		if(!strAry[7].equals("")){
    			owner= strAry[7];
    		}
    		dynGen.setOwnerName(owner);
    		
    		double xd11=ModelStringUtil.getDouble(strAry[8], 0.0);  
    		mach.setXd11(xd11);
    		
    		double xq11=ModelStringUtil.getDouble(strAry[9], 0.0);
    		mach.setXq11(xq11);
    		
    		double td011=ModelStringUtil.getDouble(strAry[10], 0.0);
    		mach.setTd011(DStabDataSetter.createTimePeriodValue(td011, TimePeriodUnitType.SEC));
    		
    		double tq011=ModelStringUtil.getDouble(strAry[11], 0.0);
    		mach.setTq011(DStabDataSetter.createTimePeriodValue(tq011, TimePeriodUnitType.SEC));
    	}
    	else if(str.substring(0, 2).trim().equals("MF")){
    		String busId = BusRecord.getBusId(strAry[1]);
        	DStabBusXmlType bus = parser.getDStabBus(busId);
        	
    		String dynGenId="1";
    		if(!strAry[3].equals("")){
    		    dynGenId=strAry[3];
    		}
    		
    		DynamicGeneratorXmlType dynGen=bus.getDynamicGenList().getDynamicGen().get(new Integer(dynGenId).intValue()-1);
    		
    		// sub-transient model
			if(dynGen!=null){
				Eq11Ed11MachineXmlType mach = (Eq11Ed11MachineXmlType) dynGen.getMachineModel().getValue();
				
				double ratedVoltage=ModelStringUtil.getDouble(strAry[2], 0.0);
		   		dynGen.setRatedVoltage(DStabDataSetter.createVoltageValue(ratedVoltage, VoltageUnitType.KV));
		   				   		
				double pContri=ModelStringUtil.getDouble(strAry[5], 1.0);
				double qContri=ModelStringUtil.getDouble(strAry[6], 1.0);
				
				if(pContri<=1.0&&pContri!=0.0){
				    pContri=pContri*100;				
					dynGen.setPContributionPercent(pContri);
				}
				if(qContri<=1.0&&qContri!=0.0){
					qContri=qContri*100;
					dynGen.setQContributionPercent(qContri);
				}
				
				double Emws=ModelStringUtil.getDouble(strAry[4], 0.0);				
				double MvaBase=ModelStringUtil.getDouble(strAry[7], net.getBasePower().getValue());
				dynGen.setRatedPower(DStabDataSetter.createActivePowerValue(MvaBase, ActivePowerUnitType.MW));
 
				double h=0.0;
				if(Emws!=0.0){
					h=Emws/MvaBase;
					NumberFormat ddf1= NumberFormat.getInstance();
					ddf1.setMaximumFractionDigits(4);
					h= new Double(ddf1.format(h)).doubleValue();
					mach.setH(h);
				}
				
				double ra=ModelStringUtil.getDouble(strAry[8], 0.0);
				mach.setRa(ra);	    			
	    		
				double xd1=ModelStringUtil.getDouble(strAry[9], 0.0);
				mach.setXd1(xd1);	    			
	    		
				double xq1=ModelStringUtil.getDouble(strAry[10], 0.0);
				mach.setXq1(xq1);	    			
	    		
				double xd=ModelStringUtil.getDouble(strAry[11], 0.0);
				mach.setXd(xd);	    			
	    		
				double xq=ModelStringUtil.getDouble(strAry[12], 0.0);
				mach.setXq(xq);	    			
	    		
				double td01=ModelStringUtil.getDouble(strAry[13], 0.0);
				mach.setTd01(DStabDataSetter.createTimePeriodValue(td01,TimePeriodUnitType.SEC));
	    		
	    		double tq01=ModelStringUtil.getDouble(strAry[14], 0.0);
	    		mach.setTq01(DStabDataSetter.createTimePeriodValue(tq01,TimePeriodUnitType.SEC));
	    			    		
	    		double xl=ModelStringUtil.getDouble(strAry[15], 0.0);
				mach.setXl(xl); 	
	    		
				double SE1=ModelStringUtil.getDouble(strAry[16], 0.0);
				mach.getSeFmt1().setSe100(SE1);
				
				double SE2=ModelStringUtil.getDouble(strAry[17], 0.0);
				mach.getSeFmt1().setSe120(SE2);
				
				double D=ModelStringUtil.getDouble(strAry[18], 0.0);
				mach.setD(D);			
			}
			
			//only MF record(there is no M record prior to MF record) represents a transient type machine model(Eq1Ed1) 
			else if(dynGen==null){
				dynGen = DStabParserHelper.getDynamicGenRec(bus);
				Eq1Ed1MachineXmlType mach = DStabParserHelper.createEq1Ed1Machine(dynGen);
				dynGen.setId(dynGenId);
				
				double ratedVoltage=ModelStringUtil.getDouble(strAry[2], 0.0);
		   		dynGen.setRatedVoltage(DStabDataSetter.createVoltageValue(ratedVoltage, VoltageUnitType.KV));
	    				   		    		
		   		double pContri=ModelStringUtil.getDouble(strAry[5], 1.0);
				double qContri=ModelStringUtil.getDouble(strAry[6], 1.0);
				
				if(pContri<=1.0&&pContri!=0.0){
				    pContri=pContri*100;				
					dynGen.setPContributionPercent(pContri);
				}
				if(qContri<=1.0&&qContri!=0.0){
					qContri=qContri*100;
					dynGen.setQContributionPercent(qContri);
				}
				
				double Emws=ModelStringUtil.getDouble(strAry[4], 0.0);				
				double MvaBase=ModelStringUtil.getDouble(strAry[7], net.getBasePower().getValue());
				dynGen.setRatedPower(DStabDataSetter.createActivePowerValue(MvaBase, ActivePowerUnitType.MW));
				//TODO Mike,  this is the baseMVA for the per unit system in BPA, I don't think this is the same as the rated power of a machine. 
				// sometimes, these two are set differently, such as the baseMVA would be chosen equal to system baseMVA .
				double h=0.0;
				if(Emws!=0.0){
					h=Emws/MvaBase;
					NumberFormat ddf1= NumberFormat.getInstance();
					ddf1.setMaximumFractionDigits(4);
					h= new Double(ddf1.format(h)).doubleValue();
					mach.setH(h);
				}
				
				double ra=ModelStringUtil.getDouble(strAry[8], 0.0);
				mach.setRa(ra);	    			
	    		
				double xd1=ModelStringUtil.getDouble(strAry[9], 0.0);
				mach.setXd1(xd1);	    			
	    		
				double xq1=ModelStringUtil.getDouble(strAry[10], 0.0);
				mach.setXq1(xq1);	    			
	    		
				double xd=ModelStringUtil.getDouble(strAry[11], 0.0);
				mach.setXd(xd);	    			
	    		
				double xq=ModelStringUtil.getDouble(strAry[12], 0.0);
				mach.setXq(xq);	    			
	    		
				double td01=ModelStringUtil.getDouble(strAry[13], 0.0);
				mach.setTd01(DStabDataSetter.createTimeConstSec(td01));
	    		
	    		double tq01=ModelStringUtil.getDouble(strAry[14], 0.0);
	    		mach.setTq01(DStabDataSetter.createTimeConstSec(tq01));
	    			    		
	    		double SE1=ModelStringUtil.getDouble(strAry[16], 0.0);
				mach.getSeFmt1().setSe100(SE1);
				
				double SE2=ModelStringUtil.getDouble(strAry[17], 0.0);
				mach.getSeFmt1().setSe120(SE2);
				
				double D=ModelStringUtil.getDouble(strAry[18], 0.0);
				mach.setD(D);
			}			
		}
    	else if(str.substring(0, 2).trim().equals("LN")){    		
    		String busId1="";
			if(!strAry[1].equals("")){
				busId1 = BusRecord.getBusId(strAry[1]);
			}
			double Vol1=ModelStringUtil.getDouble(strAry[2], 0.0);
			
			if(!busId1.equals("")&&Vol1!=0.0){
		    	DStabBusXmlType bus1 = parser.getDStabBus(busId1);
				DynamicGeneratorXmlType dynGen = DStabParserHelper.getDynamicGenRec(bus1);
				dynGen.setRatedVoltage(DStabDataSetter.createVoltageValue(Vol1, VoltageUnitType.KV));
				EquiMachineXmlType mach = DStabParserHelper.createEquiMachine(dynGen);
				
		
				BusRecordXmlType busRec=(BusRecordXmlType) parser.getBus(busId1);
				//BusRecordXmlType busRec=XBeanParserHelper.findBusRecord(bus1, baseCaseNet);
				
				
				if(busRec!=null&&busRec.getLoadflowData().getGenData().getEquivGen()!=null){
					double pGen=busRec.getLoadflowData().getGenData().
					              getEquivGen().getPower().getRe();
					mach.getEquivGen().setEquiPgen(pGen);//TODO why only pGen, for equivalence, qGen should be included
					mach.getEquivGen().setPGenUnit(ApparentPowerUnitType.MVA);					
				}else{
					mach.getEquivGen().setDCLineBus(true);
				}		
	
			  }
//			String busId2="";
//			if(!strAry[3].equals("")){
//				busId2=BusRecord.getBusId(strAry[3]);
//			}
//			double Vol2=ModelStringUtil.getDouble(strAry[4], 0.0);
//			
//			String busId3="";
//			if(!strAry[5].equals("")){
//				busId3=BusRecord.getBusId(strAry[5]);
//			}
//			double Vol3=ModelStringUtil.getDouble(strAry[6], 0.0);
//			
//			String busId4="";
//			if(!strAry[7].equals("")){
//				busId4=BusRecord.getBusId(strAry[7]);
//			}
//			double Vol4=ModelStringUtil.getDouble(strAry[8], 0.0);
//			
//			String busId5="";
//			if(!strAry[9].equals("")){
//				busId5=BusRecord.getBusId(strAry[9]);
//			}
//			double Vol5=ModelStringUtil.getDouble(strAry[10], 0.0);
    	}    	
    }
	
	private static String[] getGeneratorDataFields ( final String str) {
		final String[] strAry = new String[19];
		
		try{
			if(str.substring(0, 2).trim().equals("M")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
				strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 21).trim();
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 25).trim();
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,31, 32).trim();
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,34, 36).trim();
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,38, 42).trim();
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,43, 47).trim();
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,48, 51).trim();
				strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,52, 55).trim();				
			}
			else if(str.substring(0, 2).trim().equals("MC")||str.substring(0, 2).trim().equals("MF")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
				strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 22).trim();
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 25).trim();
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,26, 28).trim();
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,29, 32).trim();
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,33, 36).trim();
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,37, 41).trim();
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,42, 46).trim();
				strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,47, 51).trim();
				strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,52, 56).trim();
				strAry[13]=ModelStringUtil.getStringReturnEmptyString(str,57, 60).trim();
				strAry[14]=ModelStringUtil.getStringReturnEmptyString(str,61, 63).trim();
				strAry[15]=ModelStringUtil.getStringReturnEmptyString(str,64, 68).trim();
				strAry[16]=ModelStringUtil.getStringReturnEmptyString(str,69, 73).trim();
				strAry[17]=ModelStringUtil.getStringReturnEmptyString(str,74, 77).trim();
				strAry[18]=ModelStringUtil.getStringReturnEmptyString(str,78, 80).trim();
			}
			else if(str.substring(0, 2).trim().equals("LN")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
				strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,19, 26).trim();
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,27, 30).trim();
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,34, 41).trim();
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,42, 45).trim();
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,49, 56).trim();
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,57, 60).trim();
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,64, 71).trim();
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,72, 75).trim();
			}
		}catch(Exception e){
			ODMLogger.getLogger().severe(e.toString());
		}
		
		return strAry;
    }
}