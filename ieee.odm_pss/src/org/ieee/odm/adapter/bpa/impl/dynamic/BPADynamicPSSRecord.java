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

import org.ieee.odm.adapter.bpa.impl.BPABusRecord;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.model.dstab.DStabParserHelper;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.PssBPADualInputXmlType;
import org.ieee.odm.schema.PssIEE2STXmlType;
import org.ieee.odm.schema.PssIEEEDualInputXmlType;
import org.ieee.odm.schema.StabilizerInputSignalEnumType;


public class BPADynamicPSSRecord {
	
	public static void processPSSData(String str, DStabModelParser parser) throws ODMException {
    	final String[] strAry= getPSSDataFields(str);
    	
    	String busId = BPABusRecord.getBusId(strAry[1]);
    	DStabBusXmlType bus = parser.getDStabBus(busId);
    	
    	DynamicGeneratorXmlType dynGen = DStabParserHelper.getDynamicGenRec(bus);   
    	
    	if(str.substring(0, 3).trim().equals("SS")||str.substring(0, 3).trim().equals("SP")
    			||str.substring(0, 3).trim().equals("SG")){
    		PssIEE2STXmlType pss = DStabParserHelper.createPssIEE2STXmlType(dynGen);

    		//machine Id
    		String macId="1";
    		if(!strAry[3].equals("")){
    			macId=strAry[3];
    		} 
    		

    		if(str.substring(0, 3).trim().equals("SS")){
    			pss.setDesc("BPA SS type PSS, machine Id-" + macId);
    			pss.setFirstInputSignal(StabilizerInputSignalEnumType.ROTOR_SPEED_DEVIATION);    			                 
    		}
    		else if(str.substring(0, 3).trim().equals("SP")){
    			pss.setDesc("BPA SP type PSS,machine Id-" + macId);
    			pss.setFirstInputSignal(StabilizerInputSignalEnumType.GENERATOR_ACCELERATING_POWER);
    		}
    		else{
    			pss.setDesc("BPA SG type PSS,machine Id-" + macId);
    			pss.setFirstInputSignal(StabilizerInputSignalEnumType.GENERATOR_ELECTRICAL_POWER);
    		}

    		// QV block is rarely used.
    		//KQV
    		double KQV=ModelStringUtil.getDouble(strAry[4], 0.0);
    		pss.setK1(-KQV);// since the sign of QV block is opposite to that of IEE2ST
    		    		
    		//TQV
    		double TQV=ModelStringUtil.getDouble(strAry[5], 0.0);
    		pss.setT1(BaseDataSetter.createTimeConstSec(TQV));
    		
    		//KQS
    		double KQS= ModelStringUtil.getDouble(strAry[6], 0.0);    			
    		pss.setK2(KQS);
    		
    		//TQS
    		double TQS= ModelStringUtil.getDouble(strAry[7], 0.0);
    		pss.setT2(BaseDataSetter.createTimeConstSec(TQS));
    		
    		//TQ
    		double TQ= ModelStringUtil.getDouble(strAry[8], 0.0);
    		pss.setT3(BaseDataSetter.createTimeConstSec(TQ));

    		pss.setT4(BaseDataSetter.createTimeConstSec(TQ)); // In BPA model, T3=T4

    		// TQ1
    		double TQ1= ModelStringUtil.getDouble(strAry[9], 0.0);
    		pss.setT6(BaseDataSetter.createTimeConstSec(TQ1));
    		
    		
    		    		
    		//TQ11
    		double TQ11= ModelStringUtil.getDouble(strAry[10], 0.0);
    		pss.setT5(BaseDataSetter.createTimeConstSec(TQ11));
    		
    		//TQ2
    		double TQ2= ModelStringUtil.getDouble(strAry[11], 0.0);
    		pss.setT8(BaseDataSetter.createTimeConstSec(TQ2));
    		
    		// TQ21
    		double TQ21= ModelStringUtil.getDouble(strAry[12], 0.0);
    		pss.setT7(BaseDataSetter.createTimeConstSec(TQ21));
    		
    		//TQ3
    		double TQ3=ModelStringUtil.getDouble(strAry[13], 0.0);
    		pss.setT10(BaseDataSetter.createTimeConstSec(TQ3));
    		   		    		
    		//TQ31
    		double TQ31=ModelStringUtil.getDouble(strAry[14], 0.0);
    		pss.setT9(BaseDataSetter.createTimeConstSec(TQ31));    		    		
    		  		

    		    		
    		//VSMAX
    		double vsmax=ModelStringUtil.getDouble(strAry[15], 0.0);
    		pss.setVSMAX(vsmax);
    			
    		//VCUTOFF
    		double vcut=ModelStringUtil.getDouble(strAry[16], 0.0);
    		pss.setVCUTOFF(vcut);
    		    		
    		//VSLOW
    		double vsmin=0.0;
    		double Vslow=ModelStringUtil.getDouble(strAry[17], 0.0);
    		   		
    		if(Vslow<=0){
    			vsmin=-vsmax;
    		}else {
    			vsmin=-Vslow;
    		}
    		pss.setVSMIN(vsmin);
    		
            //TODO there is no corresponding element in IEE2ST model		    		
//    		//KQS MVAbase for SP SG
//    		double kqsMvaBase=ModelStringUtil.getDouble(strAry[19], 0.0);
    		
    		
    	}
    	else if(str.substring(0, 3).trim().equals("SI")){
    		PssBPADualInputXmlType dualPss= DStabParserHelper.createPssBPADualInputXmlType(dynGen);
    		
    		//machine Id
    		String macId="1";
    		if(!strAry[3].equals("")){
    			macId=strAry[3];
    		} 
    		dualPss.setDesc("BPA SI Type Dual Input Pss model,machine Id-" + macId);

    		//TRW
    		double  Trw=ModelStringUtil.getDouble(strAry[4], 0.0);;
    		dualPss.setTrw(BaseDataSetter.createTimeConstSec(Trw));
    		
    		
    		//T5
    		double  T5=ModelStringUtil.getDouble(strAry[5], 0.0);
    		dualPss.setT5(BaseDataSetter.createTimeConstSec(T5));
    		//T6
    		double  T6=ModelStringUtil.getDouble(strAry[6], 0.0);
    		dualPss.setT6(BaseDataSetter.createTimeConstSec(T6));
    		
    		//T7
    		double  T7=ModelStringUtil.getDouble(strAry[7], 0.0);
    		dualPss.setT7(BaseDataSetter.createTimeConstSec(T7));
    		
    		//KR
    		double Kr= ModelStringUtil.getDouble(strAry[8], 0.0);
    		dualPss.setKr(Kr);  		
    		// TRP
    		double  Trp=ModelStringUtil.getDouble(strAry[9], 0.0);
    		dualPss.setTrp(BaseDataSetter.createTimeConstSec(Trp));
    		
    		//TW
    		double  Tw=ModelStringUtil.getDouble(strAry[10], 0.0);
    		dualPss.setTW(BaseDataSetter.createTimeConstSec(Tw));
    		
    		//TW1
    		double  Tw1=ModelStringUtil.getDouble(strAry[11], 0.0);
    		dualPss.setTW1(BaseDataSetter.createTimeConstSec(Tw1));
    		
    		// TW2
    		double  Tw2=ModelStringUtil.getDouble(strAry[12], 0.0);
    		dualPss.setTW2(BaseDataSetter.createTimeConstSec(Tw2));
    		
    		//KS
    		double Ks= ModelStringUtil.getDouble(strAry[13], 0.0);
    		dualPss.setKS(Ks);    	
    		//T9
    		double  T9=ModelStringUtil.getDouble(strAry[14], 0.0);
    		dualPss.setT9(BaseDataSetter.createTimeConstSec(T9));
    		
    		//T10
    		double T10=ModelStringUtil.getDouble(strAry[15], 0.0);
    		dualPss.setT10(BaseDataSetter.createTimeConstSec(T10));
    		
    		//T12
    		double T12=ModelStringUtil.getDouble(strAry[16], 0.0);
    		dualPss.setT12(BaseDataSetter.createTimeConstSec(T12));
    	    
    		//INP input signal:0 for speed deviation(delta_w) and generator accelerating power(delta_Pg), 
    		//1 for only delta_w, 2 for only delta_pg
    		int INP=ModelStringUtil.getInt(strAry[17], 0);
    		    		
    		if(INP==0){
    			dualPss.setFirstInputSignal(StabilizerInputSignalEnumType.ROTOR_SPEED_DEVIATION );
    			dualPss.setSecondInputSignal(StabilizerInputSignalEnumType.GENERATOR_ACCELERATING_POWER);
    		}else if(INP==1){
    			dualPss.setFirstInputSignal(StabilizerInputSignalEnumType.ROTOR_SPEED_DEVIATION );
    		}else if(INP==2){
    			dualPss.setSecondInputSignal(StabilizerInputSignalEnumType.GENERATOR_ACCELERATING_POWER);
    		}
    	
    	}
    	// SI+ is to store the rest data of DualInputPss Model
    	else if(str.substring(0, 3).trim().equals("SI+")){ 
    		PssBPADualInputXmlType dualPss=(PssBPADualInputXmlType) dynGen.getStabilizer().getValue();

    		//KP
    		double kp= ModelStringUtil.getDouble(strAry[4], 0.0); 
    		dualPss.setKp(kp);
    		//T1
    		double  t1=ModelStringUtil.getDouble(strAry[5], 0.0);
    		dualPss.setT1(BaseDataSetter.createTimeConstSec(t1));
   		
    		//T2
    		double  t2=ModelStringUtil.getDouble(strAry[6], 0.0);
    		dualPss.setT2(BaseDataSetter.createTimeConstSec(t2));
    		
    		//T13
    		double  t13=ModelStringUtil.getDouble(strAry[7], 0.0);
    		dualPss.setT13(BaseDataSetter.createTimeConstSec(t13));
    	
    		//T14
    		double  t14=ModelStringUtil.getDouble(strAry[8], 0.0);
    		dualPss.setT14(BaseDataSetter.createTimeConstSec(t14));
    		
    		// T3
    		double  t3=ModelStringUtil.getDouble(strAry[9], 0.0);
    		dualPss.setT3(BaseDataSetter.createTimeConstSec(t3));
    		
    		//T4
    		double  t4=ModelStringUtil.getDouble(strAry[10], 0.0);
    		dualPss.setT4(BaseDataSetter.createTimeConstSec(t4));
    		
    		//VSMAX
    		double vsmax= ModelStringUtil.getDouble(strAry[11], 0.0);
    		dualPss.setVSMAX(vsmax);
    		
    		// VSMIN
    		double vsmin=ModelStringUtil.getDouble(strAry[12], 0.0);
    		dualPss.setVSMIN(vsmin);
    		
    		//krBaseMVA-- the base MVA for the Kr parameter
    		//TODO still don't know how this parameter is used in the dynamic analysis
    		double krBaseMVA=ModelStringUtil.getDouble(strAry[13], 0.0);
    		if(krBaseMVA==0.0){
    			krBaseMVA=dynGen.getRatedPower().getValue();
    		}
    		dualPss.setKrBaseMVA(krBaseMVA);
   		}
    }
	
	private static String[] getPSSDataFields(String str){
    	final String[] strAry= new String[20];
    	strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
    	//to process the Chinese characters first, if any.
		int chineseCharNum=ModelStringUtil.getChineseCharNum(str.substring(3,10).trim());
		//Columns 6-13 busName  
		strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,4, 11-chineseCharNum).trim();
		
		str=chineseCharNum==0?str:ModelStringUtil.replaceChineseChar(str);
    	try{
    		if(str.substring(0, 3).trim().equals("SS")||str.substring(0, 3).trim().equals("SP")
        			||str.substring(0, 3).trim().equals("SG")){
        		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
        		//busId
        		//strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
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
        		
        		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
        		//busId
        		//strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
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
        		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 3).trim();
        		//busId
        		//strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
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
