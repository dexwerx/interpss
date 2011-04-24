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
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.PssIEE2STXmlType;
import org.ieee.odm.schema.PssIEEEDualInputXmlType;
import org.ieee.odm.schema.StabilizerXmlType;
import org.ieee.odm.schema.TimePeriodUnitType;
import org.ieee.odm.schema.VoltageUnitType;


public class BPADynamicPSSRecord {
	
	public static void processPSSData(String str, DStabModelParser parser){
    	final String[] strAry= getPSSDataFields(str);
/*    	
    	if(str.substring(0, 3).trim().equals("SS")||str.substring(0, 3).trim().equals("SP")
    			||str.substring(0, 3).trim().equals("SG")){
    		StabilizerXmlType pss=XBeanTranStabSimuHelper.addNewStablilizerGovernor(tranSimu);
    		pss.setStabilizerType(StabilizerXmlType.StabilizerType.IEE_2_ST);
    		PssIEE2STXmlType tstpss=pss.
    		                     addNewStabilizerModel().addNewIEE2ST();
    		
    		if(str.substring(0, 3).trim().equals("SS")){
    			tstpss.setFirstInputSignal(PssIEE2STXmlType.FirstInputSignal
    					.ROTOR_SPEED_DEVIATION);    			                 
    		}else if(str.substring(0, 3).trim().equals("SP")){
    			tstpss.setFirstInputSignal(PssIEE2STXmlType.FirstInputSignal
    					.GENERATOR_ACCELERATING_POWER);
    		}else{
    			tstpss.setFirstInputSignal(PssIEE2STXmlType.FirstInputSignal
    					.GENERATOR_ELECTRICAL_POWER);
    		}
    		//busId
    		String busId=strAry[1];
    		pss.addNewLocatedBus().setName(busId);
    		//bus Voltage
    		double v=new Double(strAry[2]).doubleValue();
    		XBeanDataSetter.setVoltageData(pss.addNewBusRatedVoltage(), v, VoltageUnitType.KV);
    		    		
    		//excId
    		String macId="1";
    		if(!strAry[3].equals("")){
    			macId=strAry[3];
    			
    		} 
    		// for local input mode, remote input is set to 0;
    		tstpss.setFirstRemoteBusId("0");
    		tstpss.setSecondInputSignal(PssIEE2STXmlType.SecondInputSignal.X_0);
    		tstpss.setSecondRemoteBusId("0");
    		
    		pss.addNewMacId().setName(macId);
    		//KQV 
    		double KQV=ModelStringUtil.getDouble(strAry[4], 0.0);
    		tstpss.setK1(KQV);
    		    		
    		//TQV
    		double TQV=ModelStringUtil.getDouble(strAry[5], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT1(), TQV, TimePeriodUnitType.SEC);
    				
    		
    		//KQS
    		double KQS= ModelStringUtil.getDouble(strAry[6], 0.0);    			
    		tstpss.setK2(KQS);
    		
    		//TQS
    		double TQS= ModelStringUtil.getDouble(strAry[7], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT2(), TQS, TimePeriodUnitType.SEC);
    		
    		//TQ
    		double TQ= ModelStringUtil.getDouble(strAry[8], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT3(), TQ, TimePeriodUnitType.SEC);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT4(), TQ, TimePeriodUnitType.SEC);
    		// TQ1
    		double TQ1= ModelStringUtil.getDouble(strAry[9], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT6(), TQ1, TimePeriodUnitType.SEC);
    		    		
    		//TQ11
    		double TQ11= ModelStringUtil.getDouble(strAry[10], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT5(), TQ11, TimePeriodUnitType.SEC);
    		
    		//TQ2
    		double TQ2= ModelStringUtil.getDouble(strAry[11], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT8(), TQ2, TimePeriodUnitType.SEC);
    		
    		// TQ21
    		double TQ21= ModelStringUtil.getDouble(strAry[12], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT7(), TQ21, TimePeriodUnitType.SEC);
    		   		
    		
    		//TQ31
    		double TQ31=ModelStringUtil.getDouble(strAry[14], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT9(), TQ31, TimePeriodUnitType.SEC);
    		    		
    		  		
    		//TQ3
    		double TQ3=ModelStringUtil.getDouble(strAry[13], 0.0);
    		XBeanDataSetter.setTimePeriodData(tstpss.addNewT10(), TQ3, TimePeriodUnitType.SEC);
    		    		
    		//VSMAX
    		double vsmax=ModelStringUtil.getDouble(strAry[15], 0.0);
    		tstpss.setVSMAX(vsmax);
    			
    		//VCUTOFF
    		double vcut=ModelStringUtil.getDouble(strAry[16], 0.0);
    		tstpss.setVCUTOFF(vcut);
    		    		
    		//VSLOW
    		double vsmin=0.0;
    		double Vslow=ModelStringUtil.getDouble(strAry[17], 0.0);
    		   		
    		if(Vslow<=0){
    			vsmin=-vsmax;
    		}else {
    			vsmin=-Vslow;
    		}
    		tstpss.setVSMIN(vsmin);
			    		
    		//KQS MVAbase for SP SG
    		double kqsMvaBase=ModelStringUtil.getDouble(strAry[19], 0.0);
    				
    		
    	}else if(str.substring(0, 3).trim().equals("SI")){
    		StabilizerXmlType pss=XBeanTranStabSimuHelper.addNewStablilizerGovernor(tranSimu);
    		pss.setStabilizerType(StabilizerXmlType.StabilizerType.IEEE_DUAL_INPUT);
    		PssIEEEDualInputXmlType dualInputPss=pss.
    		                     addNewStabilizerModel().addNewIEEEDualInput();
    		
    		
    		//busId
    		String busId=strAry[1];
    		pss.addNewLocatedBus().setName(busId);
    		//bus Voltage
    		double v=new Double(strAry[2]).doubleValue();
    		XBeanDataSetter.setVoltageData(pss.addNewBusRatedVoltage(), v, VoltageUnitType.KV);
    		    		
    		//excId
    		String macId="1";
    		if(!strAry[3].equals("")){
    			macId=strAry[3];
    			
    		}    		
    		pss.addNewMacId().setName(macId);
    		//TRW
    		double  trw=ModelStringUtil.getDouble(strAry[4], 0.0);;
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewTrw(), trw, TimePeriodUnitType.SEC);
    		
    		//T5
    		double  t5=ModelStringUtil.getDouble(strAry[5], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT5(), t5, TimePeriodUnitType.SEC);
    		//T6
    		double  t6=ModelStringUtil.getDouble(strAry[6], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT6(), t6, TimePeriodUnitType.SEC);
    		
    		//T7
    		double  t7=ModelStringUtil.getDouble(strAry[7], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT7(), t7, TimePeriodUnitType.SEC);
    		
    		//KR
    		double kr= ModelStringUtil.getDouble(strAry[8], 0.0);
    		dualInputPss.setKr(kr);    		
    		// TRP
    		double  trp=ModelStringUtil.getDouble(strAry[9], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewTrp(), trp, TimePeriodUnitType.SEC);
    		
    		//TW
    		double  tw=ModelStringUtil.getDouble(strAry[10], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewTW(), tw, TimePeriodUnitType.SEC);
    		
    		//TW1
    		double  tw1=ModelStringUtil.getDouble(strAry[11], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewTW1(), tw1, TimePeriodUnitType.SEC);
    		
    		// TW2
    		double  tw2=ModelStringUtil.getDouble(strAry[12], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewTW2(), tw2, TimePeriodUnitType.SEC);
    		
    		//KS
    		double ks= ModelStringUtil.getDouble(strAry[13], 0.0);
    		dualInputPss.setKS(ks);    	
    		//T9
    		double  t9=ModelStringUtil.getDouble(strAry[14], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT9(), t9, TimePeriodUnitType.SEC);
    		
    		//T10
    		double t10=ModelStringUtil.getDouble(strAry[15], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT10(), t10, TimePeriodUnitType.SEC);
    		
    		//T12
    		double t12=ModelStringUtil.getDouble(strAry[16], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT12(), t12, TimePeriodUnitType.SEC);
    	
    		//INP input signal:0for w and Pg, 1 for w, 2for pg
    		int INP=ModelStringUtil.getInt(strAry[17], 0);
    		    		
    		if(INP==0){
    			dualInputPss.setFirstInputSignal(PssIEEEDualInputXmlType
    					.FirstInputSignal.ROTOR_SPEED_DEVIATION );
    			dualInputPss.setSecondInputSignal(PssIEEEDualInputXmlType
    					.SecondInputSignal.GENERATOR_ACCELERATING_POWER);
    		}else if(INP==1){
    			dualInputPss.setFirstInputSignal(PssIEEEDualInputXmlType
    					.FirstInputSignal.ROTOR_SPEED_DEVIATION );
    		}else if(INP==2){
    			dualInputPss.setSecondInputSignal(PssIEEEDualInputXmlType
    					.SecondInputSignal.GENERATOR_ACCELERATING_POWER);
    		}
    		
    		
    	}else if(str.substring(0, 3).trim().equals("SI+")){
    		
    		String busId=strAry[1];    		
    		//bus Voltage    		   		    		
    		//excId
    		String macId="1";
    		if(!strAry[3].equals("")){
    			macId=strAry[3];
    		}    		
    		StabilizerXmlType pss=XBeanParserHelper.getPSSRecord(tranSimu, busId, macId);
    		PssIEEEDualInputXmlType dualInputPss=pss
                      .getStabilizerModel().getIEEEDualInput();
    		
    		
    		//KP
    		double kp= ModelStringUtil.getDouble(strAry[4], 0.0);    		
    		//T1
    		double  t1=ModelStringUtil.getDouble(strAry[5], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT1(), t1, TimePeriodUnitType.SEC);    		
    		//T2
    		double  t2=ModelStringUtil.getDouble(strAry[6], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT2(), t2, TimePeriodUnitType.SEC);
    		
    		//T13
    		double  t13=ModelStringUtil.getDouble(strAry[7], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT13(), t13, TimePeriodUnitType.SEC);
    	
    		//T14
    		double  t14=ModelStringUtil.getDouble(strAry[8], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT14(), t14, TimePeriodUnitType.SEC);
    		
    		// T3
    		double  t3=ModelStringUtil.getDouble(strAry[9], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT3(), t3, TimePeriodUnitType.SEC);
    		
    		//T4
    		double  t4=ModelStringUtil.getDouble(strAry[10], 0.0);
    		XBeanDataSetter.setTimePeriodData(dualInputPss.addNewT4(), t4, TimePeriodUnitType.SEC);
    		
    		//VSMAX
    		double vsmax= ModelStringUtil.getDouble(strAry[11], 0.0);
    		dualInputPss.setVSMAX(vsmax);
    		
    		// VSMIN
    		double vsmin=ModelStringUtil.getDouble(strAry[12], 0.0);
    		dualInputPss.setVSMIN(vsmin);
    		}
*/    	
    }
	
	private static String[] getPSSDataFields(String str){
    	final String[] strAry= new String[20];
    	
    	try{
    		if(str.substring(0, 3).trim().equals("SS")||str.substring(0, 3).trim().equals("SP")
        			||str.substring(0, 3).trim().equals("SG")){
        		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
        		//busId
        		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
        		//bus Voltage
        		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
        		//excId
        		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
        		//KQV 
        		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 20).trim();
        		//TQV
        		strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,21, 23).trim();
        		//KQS
        		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,24, 27).trim();
        		//TQS
        		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,28, 30).trim();
        		//TQ
        		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,31, 34).trim();
        		// TQ1
        		strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,35, 38).trim();
        		//TQ11
        		strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,39, 42).trim();
        		//TQ2
        		strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,43, 46).trim();
        		// TQ21
        		strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,47, 50).trim();
        		//TQ3
        		strAry[13]=ModelStringUtil.getStringReturnEmptyString(str,51, 54).trim();
        		//TQ31
        		strAry[14]=ModelStringUtil.getStringReturnEmptyString(str,55, 58).trim();
        		//VSMAX
        		strAry[15]=ModelStringUtil.getStringReturnEmptyString(str,59, 62).trim();	
        		//VCUTOFF
        		strAry[16]=ModelStringUtil.getStringReturnEmptyString(str,63, 66).trim();
        		//VSLOW
        		strAry[17]=ModelStringUtil.getStringReturnEmptyString(str,67, 68).trim();
        		//REMOTE BUS
        		strAry[18]=ModelStringUtil.getStringReturnEmptyString(str,69, 76).trim();
        		//REMOTE VOLTAGE,  KQS MVAbase for SP SG
        		strAry[19]=ModelStringUtil.getStringReturnEmptyString(str,77, 80).trim();
        		
        	}else if(str.substring(0, 3).trim().equals("SI")){
        		
        		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
        		//busId
        		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
        		//bus Voltage
        		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
        		//excId
        		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
        		//TRW
        		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 20).trim();
        		//T5
        		strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,21, 25).trim();
        		//T6
        		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,26, 30).trim();
        		//T7
        		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,31, 35).trim();
        		//KR
        		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,36, 41).trim();
        		// TRP
        		strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,42, 45).trim();
        		//TW
        		strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,46, 50).trim();
        		//TW1
        		strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,51, 55).trim();
        		// TW2
        		strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,56, 60).trim();
        		//KS
        		strAry[13]=ModelStringUtil.getStringReturnEmptyString(str,61, 64).trim();
        		//T9
        		strAry[14]=ModelStringUtil.getStringReturnEmptyString(str,65, 69).trim();
        		//T10
        		strAry[15]=ModelStringUtil.getStringReturnEmptyString(str,70, 74).trim();	
        		//T12
        		strAry[16]=ModelStringUtil.getStringReturnEmptyString(str,75, 79).trim();
        		//INP input signal:0for w and Pg, 1 for w, 2for pg
        		strAry[17]=ModelStringUtil.getStringReturnEmptyString(str,80, 80).trim();
        		
        		
        	}else if(str.substring(0, 3).trim().equals("SI+")){
        		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 3).trim();
        		//busId
        		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
        		//bus Voltage
        		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
        		//excId
        		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
        		//KP
        		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 21).trim();
        		//T1
        		strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,22, 26).trim();
        		//T2
        		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,27, 31).trim();
        		//T13
        		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,32, 36).trim();
        		//T14
        		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,37, 41).trim();
        		// T3
        		strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,42, 46).trim();
        		//T4
        		strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,47, 51).trim();
        		//VSMAX
        		strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,52, 57).trim();
        		// VSMIN
        		strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,58, 63).trim();
        		//KMVA, MVAbase for kr in SI 
        		strAry[13]=ModelStringUtil.getStringReturnEmptyString(str,77, 80).trim();
        		
        	}
    	}catch (Exception e){
    		ODMLogger.getLogger().severe(e.toString());
    	}
    	
    	    	
    	return strAry;
    }

}
