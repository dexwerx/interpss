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

package org.ieee.pes.odm.pss.adapter.bpa;

import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PerUnitXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.StabilizerModelListXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.StabilizerXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TimeXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TransientSimulationXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.VoltageXmlType;
import org.ieee.pes.odm.pss.model.IEEEODMPSSModelParser;
import org.ieee.pes.odm.pss.model.ODMData2XmlHelper;
import org.ieee.pes.odm.pss.model.StringUtil;
import org.ieee.pes.odm.pss.model.TranStabSimuHelper;


public class BPADynamicPSSRecord {
	
	public static void processPSSData(String str,TransientSimulationXmlType tranSimu,
    		IEEEODMPSSModelParser parser, BPAAdapter adapter){
    	final String[] strAry= getPSSDataFields(str,adapter);
    	
    	if(str.substring(0, 3).trim().equals("SS")||str.substring(0, 3).trim().equals("SP")
    			||str.substring(0, 3).trim().equals("SG")){
    		StabilizerXmlType pss=TranStabSimuHelper.addNewStablilizerGovernor(tranSimu);
    		pss.setStabilizerType(StabilizerXmlType.StabilizerType.IEE_2_ST);
    		StabilizerModelListXmlType.IEE2ST tstpss=pss.
    		                     addNewStabilizerModel().addNewIEE2ST();
    		
    		if(str.substring(0, 3).trim().equals("SS")){
    			tstpss.setFirstInputSignal(StabilizerModelListXmlType.IEE2ST.FirstInputSignal
    					.ROTOR_SPEED_DEVIATION);    			                 
    		}else if(str.substring(0, 3).trim().equals("SP")){
    			tstpss.setFirstInputSignal(StabilizerModelListXmlType.IEE2ST.FirstInputSignal
    					.GENERATOR_ACCELERATING_POWER);
    		}else{
    			tstpss.setFirstInputSignal(StabilizerModelListXmlType.IEE2ST.FirstInputSignal
    					.GENERATOR_ELECTRICAL_POWER);
    		}
    		//busId
    		String busId=strAry[1];
    		pss.addNewLocatedBus().setName(busId);
    		//bus Voltage
    		double v=new Double(strAry[2]).doubleValue();
    		ODMData2XmlHelper.setVoltageData(pss.addNewBusRatedVoltage(), v, VoltageXmlType.Unit.KV);
    		    		
    		//excId
    		String macId="1";
    		if(!strAry[3].equals("")){
    			macId=strAry[3];
    			
    		} 
    		// for local input mode, remote input is set to 0;
    		tstpss.setFirstRemoteBusId("0");
    		tstpss.setSecondInputSignal(StabilizerModelListXmlType.IEE2ST.SecondInputSignal.X_0);
    		tstpss.setSecondRemoteBusId("0");
    		
    		pss.addNewMacId().setName(macId);
    		//KQV 
    		double KQV=StringUtil.getDouble(strAry[4], 0.0);
    		tstpss.setK1(KQV);
    		    		
    		//TQV
    		double TQV=StringUtil.getDouble(strAry[5], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT1(), TQV, TimeXmlType.Unit.SEC);
    				
    		
    		//KQS
    		double KQS= StringUtil.getDouble(strAry[6], 0.0);    			
    		tstpss.setK2(KQS);
    		
    		//TQS
    		double TQS= StringUtil.getDouble(strAry[7], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT2(), TQS, TimeXmlType.Unit.SEC);
    		
    		//TQ
    		double TQ= StringUtil.getDouble(strAry[8], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT3(), TQ, TimeXmlType.Unit.SEC);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT4(), TQ, TimeXmlType.Unit.SEC);
    		// TQ1
    		double TQ1= StringUtil.getDouble(strAry[9], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT6(), TQ1, TimeXmlType.Unit.SEC);
    		    		
    		//TQ11
    		double TQ11= StringUtil.getDouble(strAry[10], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT5(), TQ11, TimeXmlType.Unit.SEC);
    		
    		//TQ2
    		double TQ2= StringUtil.getDouble(strAry[11], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT8(), TQ2, TimeXmlType.Unit.SEC);
    		
    		// TQ21
    		double TQ21= StringUtil.getDouble(strAry[12], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT7(), TQ21, TimeXmlType.Unit.SEC);
    		   		
    		
    		//TQ31
    		double TQ31=StringUtil.getDouble(strAry[14], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT9(), TQ31, TimeXmlType.Unit.SEC);
    		    		
    		  		
    		//TQ3
    		double TQ3=StringUtil.getDouble(strAry[13], 0.0);
    		ODMData2XmlHelper.setTimeData(tstpss.addNewT10(), TQ3, TimeXmlType.Unit.SEC);
    		    		
    		//VSMAX
    		double vsmax=StringUtil.getDouble(strAry[15], 0.0);
    		ODMData2XmlHelper.setPUData(tstpss.addNewVSMAX(), vsmax, PerUnitXmlType.Unit.PU);
    			
    		//VCUTOFF
    		double vcut=StringUtil.getDouble(strAry[16], 0.0);
    		ODMData2XmlHelper.setPUData(tstpss.addNewVCUTOFF(), vcut, PerUnitXmlType.Unit.PU);
    		    		
    		//VSLOW
    		double vsmin=0.0;
    		double Vslow=StringUtil.getDouble(strAry[17], 0.0);
    		   		
    		if(Vslow<=0){
    			vsmin=-vsmax;
    		}else {
    			vsmin=-Vslow;
    		}
    		ODMData2XmlHelper.setPUData(tstpss.addNewVSMIN(), vsmin, PerUnitXmlType.Unit.PU);
			    		
    		//KQS MVAbase for SP SG
    		double kqsMvaBase=StringUtil.getDouble(strAry[19], 0.0);
    				
    		
    	}else if(str.substring(0, 3).trim().equals("SI")){
    		StabilizerXmlType pss=TranStabSimuHelper.addNewStablilizerGovernor(tranSimu);
    		pss.setStabilizerType(StabilizerXmlType.StabilizerType.IEEE_DUAL_INPUT);
    		StabilizerModelListXmlType.IEEEDualInput dualInputPss=pss.
    		                     addNewStabilizerModel().addNewIEEEDualInput();
    		
    		
    		//busId
    		String busId=strAry[1];
    		pss.addNewLocatedBus().setName(busId);
    		//bus Voltage
    		double v=new Double(strAry[2]).doubleValue();
    		ODMData2XmlHelper.setVoltageData(pss.addNewBusRatedVoltage(), v, VoltageXmlType.Unit.KV);
    		    		
    		//excId
    		String macId="1";
    		if(!strAry[3].equals("")){
    			macId=strAry[3];
    			
    		}    		
    		pss.addNewMacId().setName(macId);
    		//TRW
    		double  trw=StringUtil.getDouble(strAry[4], 0.0);;
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewTrw(), trw, TimeXmlType.Unit.SEC);
    		
    		//T5
    		double  t5=StringUtil.getDouble(strAry[5], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT5(), t5, TimeXmlType.Unit.SEC);
    		//T6
    		double  t6=StringUtil.getDouble(strAry[6], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT6(), t6, TimeXmlType.Unit.SEC);
    		
    		//T7
    		double  t7=StringUtil.getDouble(strAry[7], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT7(), t7, TimeXmlType.Unit.SEC);
    		
    		//KR
    		double kr= StringUtil.getDouble(strAry[8], 0.0);
    		dualInputPss.setKr(kr);    		
    		// TRP
    		double  trp=StringUtil.getDouble(strAry[9], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewTrp(), trp, TimeXmlType.Unit.SEC);
    		
    		//TW
    		double  tw=StringUtil.getDouble(strAry[10], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewTW(), tw, TimeXmlType.Unit.SEC);
    		
    		//TW1
    		double  tw1=StringUtil.getDouble(strAry[11], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewTW1(), tw1, TimeXmlType.Unit.SEC);
    		
    		// TW2
    		double  tw2=StringUtil.getDouble(strAry[12], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewTW2(), tw2, TimeXmlType.Unit.SEC);
    		
    		//KS
    		double ks= StringUtil.getDouble(strAry[13], 0.0);
    		dualInputPss.setKS(ks);    	
    		//T9
    		double  t9=StringUtil.getDouble(strAry[14], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT9(), t9, TimeXmlType.Unit.SEC);
    		
    		//T10
    		double t10=StringUtil.getDouble(strAry[15], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT10(), t10, TimeXmlType.Unit.SEC);
    		
    		//T12
    		double t12=StringUtil.getDouble(strAry[16], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT12(), t12, TimeXmlType.Unit.SEC);
    	
    		//INP input signal:0for w and Pg, 1 for w, 2for pg
    		int INP=StringUtil.getInt(strAry[17], 0);
    		    		
    		if(INP==0){
    			dualInputPss.setFirstInputSignal(StabilizerModelListXmlType.IEEEDualInput
    					.FirstInputSignal.ROTOR_SPEED_DEVIATION );
    			dualInputPss.setSecondInputSignal(StabilizerModelListXmlType.IEEEDualInput
    					.SecondInputSignal.GENERATOR_ACCELERATING_POWER);
    		}else if(INP==1){
    			dualInputPss.setFirstInputSignal(StabilizerModelListXmlType.IEEEDualInput
    					.FirstInputSignal.ROTOR_SPEED_DEVIATION );
    		}else if(INP==2){
    			dualInputPss.setSecondInputSignal(StabilizerModelListXmlType.IEEEDualInput
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
    		StabilizerXmlType pss=ODMData2XmlHelper.getPSSRecord(tranSimu, busId, macId);
    		StabilizerModelListXmlType.IEEEDualInput dualInputPss=pss
                      .getStabilizerModel().getIEEEDualInput();
    		
    		
    		//KP
    		double kp= StringUtil.getDouble(strAry[4], 0.0);    		
    		//T1
    		double  t1=StringUtil.getDouble(strAry[5], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT1(), t1, TimeXmlType.Unit.SEC);    		
    		//T2
    		double  t2=StringUtil.getDouble(strAry[6], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT2(), t2, TimeXmlType.Unit.SEC);
    		
    		//T13
    		double  t13=StringUtil.getDouble(strAry[7], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT13(), t13, TimeXmlType.Unit.SEC);
    	
    		//T14
    		double  t14=StringUtil.getDouble(strAry[8], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT14(), t14, TimeXmlType.Unit.SEC);
    		
    		// T3
    		double  t3=StringUtil.getDouble(strAry[9], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT3(), t3, TimeXmlType.Unit.SEC);
    		
    		//T4
    		double  t4=StringUtil.getDouble(strAry[10], 0.0);
    		ODMData2XmlHelper.setTimeData(dualInputPss.addNewT4(), t4, TimeXmlType.Unit.SEC);
    		
    		//VSMAX
    		double vsmax= StringUtil.getDouble(strAry[11], 0.0);
    		ODMData2XmlHelper.setPUData(dualInputPss.addNewVSMAX(), vsmax, PerUnitXmlType.Unit.PU);
    		
    		// VSMIN
    		double vsmin=StringUtil.getDouble(strAry[12], 0.0);
    		ODMData2XmlHelper.setPUData(dualInputPss.addNewVSMIN(), vsmin, PerUnitXmlType.Unit.PU);
    		}
    	
    }
	
	private static String[] getPSSDataFields(String str, BPAAdapter adapter){
    	final String[] strAry= new String[20];
    	
    	try{
    		if(str.substring(0, 3).trim().equals("SS")||str.substring(0, 3).trim().equals("SP")
        			||str.substring(0, 3).trim().equals("SG")){
        		strAry[0]=StringUtil.getStringReturnEmptyString(str,1, 2).trim();
        		//busId
        		strAry[1]=StringUtil.getStringReturnEmptyString(str,4, 11).trim();
        		//bus Voltage
        		strAry[2]=StringUtil.getStringReturnEmptyString(str,12, 15).trim();
        		//excId
        		strAry[3]=StringUtil.getStringReturnEmptyString(str,16, 16).trim();
        		//KQV 
        		strAry[4]=StringUtil.getStringReturnEmptyString(str,17, 20).trim();
        		//TQV
        		strAry[5]=StringUtil.getStringReturnEmptyString(str,21, 23).trim();
        		//KQS
        		strAry[6]=StringUtil.getStringReturnEmptyString(str,24, 27).trim();
        		//TQS
        		strAry[7]=StringUtil.getStringReturnEmptyString(str,28, 30).trim();
        		//TQ
        		strAry[8]=StringUtil.getStringReturnEmptyString(str,31, 34).trim();
        		// TQ1
        		strAry[9]=StringUtil.getStringReturnEmptyString(str,35, 38).trim();
        		//TQ11
        		strAry[10]=StringUtil.getStringReturnEmptyString(str,39, 42).trim();
        		//TQ2
        		strAry[11]=StringUtil.getStringReturnEmptyString(str,43, 46).trim();
        		// TQ21
        		strAry[12]=StringUtil.getStringReturnEmptyString(str,47, 50).trim();
        		//TQ3
        		strAry[13]=StringUtil.getStringReturnEmptyString(str,51, 54).trim();
        		//TQ31
        		strAry[14]=StringUtil.getStringReturnEmptyString(str,55, 58).trim();
        		//VSMAX
        		strAry[15]=StringUtil.getStringReturnEmptyString(str,59, 62).trim();	
        		//VCUTOFF
        		strAry[16]=StringUtil.getStringReturnEmptyString(str,63, 66).trim();
        		//VSLOW
        		strAry[17]=StringUtil.getStringReturnEmptyString(str,67, 68).trim();
        		//REMOTE BUS
        		strAry[18]=StringUtil.getStringReturnEmptyString(str,69, 76).trim();
        		//REMOTE VOLTAGE,  KQS MVAbase for SP SG
        		strAry[19]=StringUtil.getStringReturnEmptyString(str,77, 80).trim();
        		
        	}else if(str.substring(0, 3).trim().equals("SI")){
        		
        		strAry[0]=StringUtil.getStringReturnEmptyString(str,1, 2).trim();
        		//busId
        		strAry[1]=StringUtil.getStringReturnEmptyString(str,4, 11).trim();
        		//bus Voltage
        		strAry[2]=StringUtil.getStringReturnEmptyString(str,12, 15).trim();
        		//excId
        		strAry[3]=StringUtil.getStringReturnEmptyString(str,16, 16).trim();
        		//TRW
        		strAry[4]=StringUtil.getStringReturnEmptyString(str,17, 20).trim();
        		//T5
        		strAry[5]=StringUtil.getStringReturnEmptyString(str,21, 25).trim();
        		//T6
        		strAry[6]=StringUtil.getStringReturnEmptyString(str,26, 30).trim();
        		//T7
        		strAry[7]=StringUtil.getStringReturnEmptyString(str,31, 35).trim();
        		//KR
        		strAry[8]=StringUtil.getStringReturnEmptyString(str,36, 41).trim();
        		// TRP
        		strAry[9]=StringUtil.getStringReturnEmptyString(str,42, 45).trim();
        		//TW
        		strAry[10]=StringUtil.getStringReturnEmptyString(str,46, 50).trim();
        		//TW1
        		strAry[11]=StringUtil.getStringReturnEmptyString(str,51, 55).trim();
        		// TW2
        		strAry[12]=StringUtil.getStringReturnEmptyString(str,56, 60).trim();
        		//KS
        		strAry[13]=StringUtil.getStringReturnEmptyString(str,61, 64).trim();
        		//T9
        		strAry[14]=StringUtil.getStringReturnEmptyString(str,65, 69).trim();
        		//T10
        		strAry[15]=StringUtil.getStringReturnEmptyString(str,70, 74).trim();	
        		//T12
        		strAry[16]=StringUtil.getStringReturnEmptyString(str,75, 79).trim();
        		//INP input signal:0for w and Pg, 1 for w, 2for pg
        		strAry[17]=StringUtil.getStringReturnEmptyString(str,80, 80).trim();
        		
        		
        	}else if(str.substring(0, 3).trim().equals("SI+")){
        		strAry[0]=StringUtil.getStringReturnEmptyString(str,1, 3).trim();
        		//busId
        		strAry[1]=StringUtil.getStringReturnEmptyString(str,4, 11).trim();
        		//bus Voltage
        		strAry[2]=StringUtil.getStringReturnEmptyString(str,12, 15).trim();
        		//excId
        		strAry[3]=StringUtil.getStringReturnEmptyString(str,16, 16).trim();
        		//KP
        		strAry[4]=StringUtil.getStringReturnEmptyString(str,17, 21).trim();
        		//T1
        		strAry[5]=StringUtil.getStringReturnEmptyString(str,22, 26).trim();
        		//T2
        		strAry[6]=StringUtil.getStringReturnEmptyString(str,27, 31).trim();
        		//T13
        		strAry[7]=StringUtil.getStringReturnEmptyString(str,32, 36).trim();
        		//T14
        		strAry[8]=StringUtil.getStringReturnEmptyString(str,37, 41).trim();
        		// T3
        		strAry[9]=StringUtil.getStringReturnEmptyString(str,42, 46).trim();
        		//T4
        		strAry[10]=StringUtil.getStringReturnEmptyString(str,47, 51).trim();
        		//VSMAX
        		strAry[11]=StringUtil.getStringReturnEmptyString(str,52, 57).trim();
        		// VSMIN
        		strAry[12]=StringUtil.getStringReturnEmptyString(str,58, 63).trim();
        		//KMVA, MVAbase for kr in SI 
        		strAry[13]=StringUtil.getStringReturnEmptyString(str,77, 80).trim();
        		
        	}
    	}catch (Exception e){
    		adapter.logErr(e.toString());
    	}
    	
    	    	
    	return strAry;
    }

}
