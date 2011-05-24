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
import org.ieee.odm.adapter.bpa.impl.BusRecord;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.model.dstab.DStabParserHelper;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.GovBPAHydroTurbineGHXmlType;
import org.ieee.odm.schema.GovHydroSteamGeneralModelXmlType;
import org.ieee.odm.schema.SpeedGovBPAGSModelXmlType;
import org.ieee.odm.schema.SteamTurbineBPATBModelXmlType;
import org.ieee.odm.schema.SteamTurbineNRXmlType;


public class BPADynamicTurbineGovernorRecord {
	
	public static void processTurbineGovernorData(String str, DStabModelParser parser) throws ODMException {
    	final String strAry[]=getTGDataFields(str);
    	
    	String busId = BusRecord.getBusId(strAry[1]);
    	DStabBusXmlType bus = parser.getDStabBus(busId);
    	
    	DynamicGeneratorXmlType dynGen = DStabParserHelper.getDynamicGenRec(bus);    	
    	
    	if(strAry[0].equals("GG")){ 
    		GovHydroSteamGeneralModelXmlType gov = DStabParserHelper.createGovHydroSteamGeneralModelXmlType(dynGen);
    					
			//machine Id
    		String tgId = "";
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];
    		}	
    		gov.setDesc("Gov Hydro Steam General Model, MachId#" + tgId);
    		
			//PMAX 
    		double pmax=ModelStringUtil.getDouble(strAry[4], 0.0);
    		gov.setPMAX(pmax);
    		
			//R
    		double r=ModelStringUtil.getDouble(strAry[5], 0.0);
    		gov.setR(r);
			
			//T1
    		double T1=ModelStringUtil.getDouble(strAry[6], 0.0);
    		gov.setT1(BaseDataSetter.createTimeConstSec(T1));

    		//T2
    	    double T2=ModelStringUtil.getDouble(strAry[7], 0.0);
    		gov.setT2(BaseDataSetter.createTimeConstSec(T2));

    		//T3
		    double T3=ModelStringUtil.getDouble(strAry[8], 0.0);
    		gov.setT3(BaseDataSetter.createTimeConstSec(T3));

    		// T4
		    double T4=ModelStringUtil.getDouble(strAry[9], 0.0);
    		gov.setT4(BaseDataSetter.createTimeConstSec(T4));

    		//T5
		    double T5=ModelStringUtil.getDouble(strAry[10], 0.0);
    		gov.setT5(BaseDataSetter.createTimeConstSec(T5));
			
			//F
		    double f=ModelStringUtil.getDouble(strAry[11], 0.0);
    		gov.setF(f);
    	}
    	else if(strAry[0].equals("GH")){
    		GovBPAHydroTurbineGHXmlType gov = DStabParserHelper.createGovBPAHydroTurbineGHXmlType(dynGen);
		
			//machine Id
    		String id;
    		if(!strAry[3].equals("")){
    			id=strAry[3];
    			gov.setDesc("GOV Hydro Turbine GH type, machId#"+id);
    		}	
    		
			//PMAX 
    		double pmax=ModelStringUtil.getDouble(strAry[4], 0.0);
    		gov.setPMAX(pmax);
    		//R
    		double r=ModelStringUtil.getDouble(strAry[5], 0.0);
    		gov.setR(r);
    		gov.setSIGMA(r);
			//TG
    		double Tg=ModelStringUtil.getDouble(strAry[6], 0.0);    		
    		
		    gov.setTG(BaseDataSetter.createTimeConstSec(Tg));		
			//TP
		    double Tp=ModelStringUtil.getDouble(strAry[7], 0.0);
		    gov.setTP(BaseDataSetter.createTimeConstSec(Tp));		
			//TD is corresponding to the TR in the ieee model
		    double Td= ModelStringUtil.getDouble(strAry[8], 0.0);
		    gov.setTR(BaseDataSetter.createTimeConstSec(Td));
		    
			// TW/2
		    double Twhalf= ModelStringUtil.getDouble(strAry[9], 0.0);
		    gov.setTW(BaseDataSetter.createTimeConstSec(Twhalf*2));	
		    // NOTE: Both VELCLOSE and VELOPEN is in PU based on PMAX.
			//VELCLOSE
		    double Uc=ModelStringUtil.getDouble(strAry[10], 0.0);
		    //TODO change it to a double
    		gov.setVClose(Uc);
			
			//FVELOPEN
    		double Uo=ModelStringUtil.getDouble(strAry[11], 0.0);
    		gov.setVOpen(Uo);
			
			//Dd
    		double Dd=ModelStringUtil.getDouble(strAry[12], 0.0);
    		gov.setDELTA(Dd);
    		//Epsilon
    		double Epsilon=ModelStringUtil.getDouble(strAry[13], 0.0);
    		gov.setEpsilon(Epsilon);
    		gov.setA11(0.5);
    		gov.setA13(1.5);
    		gov.setA21(1.0);
    		gov.setA23(1.0);
    		
    				
    	}
    	else if(strAry[0].equals("GS")){
    		SpeedGovBPAGSModelXmlType gov = DStabParserHelper.createSpeedGovBPAGSModelXmlType(dynGen);
			
			//machine Id
    		String tgId="";
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];
    			gov.setDesc("GOV Hydro Turbine GH type, machId#"+tgId);
    		}			
			//PMAX 
    		double pmax=new Double(strAry[4]).doubleValue();
    		gov.setPmax(pmax);
    		//PMIN
    		double pmin=ModelStringUtil.getDouble(strAry[5], 0.0);
    		gov.setPmin(pmin);	
    			
    		//R
    		double r=ModelStringUtil.getDouble(strAry[6], 0.0);
    		gov.setR(r);
			//T1
    		double T1=ModelStringUtil.getDouble(strAry[7], 0.0);
    		gov.setT1(BaseDataSetter.createTimeConstSec(T1));
    		    					
			//T2
    		double T2=ModelStringUtil.getDouble(strAry[8], 0.0);
    		gov.setT2(BaseDataSetter.createTimeConstSec(T2));
    				    		
			// T3
		    double T3= ModelStringUtil.getDouble(strAry[9], 0.0);
		    gov.setT3(BaseDataSetter.createTimeConstSec(T3));			
			//VELOPEN
		    double Vopen=ModelStringUtil.getDouble(strAry[4], 0.0);
		    gov.setVELOPEN(Vopen);			
			//FVELCLOSE
    		double Vclose=ModelStringUtil.getDouble(strAry[11], 0.0);
    		gov.setVELCLOSE(Vclose);
    		
    	}
    	else if(strAry[0].equals("TA")){
    		//TODO now we use a general stream turbine to represent 
    		
    		SteamTurbineNRXmlType st=DStabParserHelper.createSteamTurbineNRXmlType(dynGen);
    		//Machine Id   		
    		String tgId="";
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];
    			st.setDesc("GOV Steam Turbine BPA TA type(non reheat), machId#"+tgId);
    		}	
    		//TCH
    		double TCH= new Double(strAry[4]).doubleValue();
    		st.setTCH(BaseDataSetter.createTimeConstSec(TCH));    			
    		st.setK(1.0);
    		
    
    	}
    	else if(strAry[0].equals("TB")){
    		// since tur is part of the parent governor, it is assume that the parent genernor has been
    		// created
    		//TODO How to create one if there is no governor defined yet.
    		assert(dynGen.getGovernor() != null);
    		SteamTurbineBPATBModelXmlType st=DStabParserHelper.createSteamTurbineBPATBModelXmlType(dynGen);
    		//busId   		
    		String tgId="";
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];
    			st.setDesc("GOV Steam Turbine BPA TB type, machId#"+tgId);
    		}
               
    			//TCH
    			double TCH= ModelStringUtil.getDouble(strAry[4], 0.0);
    			st.setTCH(BaseDataSetter.createTimeConstSec(TCH));	  
    			//FHP
    		    double FHP= ModelStringUtil.getDouble(strAry[5], 0.0);
    			st.setFHP(FHP);
    			//TRH
    		    double TRH= ModelStringUtil.getDouble(strAry[6], 0.0);
    		    st.setTRH(BaseDataSetter.createTimeConstSec(TRH));	    			
    			//FIP
    		    double FIP= ModelStringUtil.getDouble(strAry[7], 0.0);
    		    st.setFIP(FIP);   			
    			//TCO
    		    double TCO=ModelStringUtil.getDouble(strAry[8], 0.0);
    		    st.setTCO(BaseDataSetter.createTimeConstSec(TCO));
    		       		    	    			
    			// FLP
    		    double FLP=ModelStringUtil.getDouble(strAry[9], 0.0);
       		    st.setFLP(FLP);
       		    //Lambda
       		   double Lambda=ModelStringUtil.getDouble(strAry[10], 0.0);
       		    st.setLambda(Lambda);
    		   
    		       		        			
    	}
    }
	
	private static String[] getTGDataFields ( String str) {
    	final String[] strAry = new String[19];
    	strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
    	//to process the Chinese characters first, if any.
		int chineseCharNum=ModelStringUtil.getChineseCharNum(str.substring(3,10).trim());
		//Columns 6-13 busName  
		strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,4, 11-chineseCharNum).trim();
		str=chineseCharNum==0?str:ModelStringUtil.replaceChineseChar(str);
		//bus Voltage
		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
    	try{
    		if(str.substring(0, 2).trim().equals("GG")){
	    		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		
				//strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				//excId
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
				//PMAX 
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 22).trim();
				//R
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 27).trim();
				//T1
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,28, 32).trim();
				//T2
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,33, 37).trim();
				//T3
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,38, 42).trim();
				// T4
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,43, 47).trim();
				//T5
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,48, 52).trim();
				//F
				strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,53, 57).trim();
				
	    		
	    	}else if(str.substring(0, 2).trim().equals("GH")){
	    		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				//busId
				//strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
				//bus Voltage
				//strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				//excId
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
				//PMAX 
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 22).trim();
				//R
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 27).trim();
				//TG
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,28, 32).trim();					
				//TP
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,33, 37).trim();
				//TD
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,38, 42).trim();
				// TW/2
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,43, 47).trim();
				//VELCLOSE
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,48, 52).trim();
				//FVELOPEN
				strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,53, 57).trim();
				//Dd
				strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,58, 62).trim();
				//Epsilon
				strAry[13]=ModelStringUtil.getStringReturnEmptyString(str,63, 68).trim();
				
	    		
	    	}
	    	else if(str.substring(0, 2).trim().equals("GS")){
	    		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				//busId
				//strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
				//bus Voltage
				//strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				//excId
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
				//PMAX 
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 22).trim();
				//PMIN
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 28).trim();
				//R
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,29, 33).trim();
				//T1
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,34, 38).trim();
				//T2
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,39, 43).trim();
				// T3
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,44, 48).trim();
				//VELOPEN
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,49, 54).trim();
				//FVELCLOSE
				strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,55, 60).trim();			
	    		
	    	}else if(str.substring(0, 2).trim().equals("TA")){
	    		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				//busId
				//strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
				//bus Voltage
				//strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				//excId
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
				//TCH
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 21).trim();
				//k1
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 26).trim();
	    	}else if(str.substring(0, 2).trim().equals("TB")){
	    		//strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				//busId
				//strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
				//bus Voltage
				//strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
				//excId
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,14, 16).trim();
				//tch
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 21).trim();
				//FHP
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,22, 26).trim();
				//TRH
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,32, 36).trim();
				//FIP
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,37, 41).trim();
				//TCO
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,47, 51).trim();
				// FLP
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,52, 56).trim();
				//Lambda
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,77, 80).trim();
				
	    	}
    	}catch(Exception e){
    		ODMLogger.getLogger().severe(e.toString());
    	}
    	return strAry;
    }
}
