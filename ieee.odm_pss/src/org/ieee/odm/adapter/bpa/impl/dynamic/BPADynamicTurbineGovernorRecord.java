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
import org.ieee.odm.schema.GovHydroSteamGeneralModelXmlType;
import org.ieee.odm.schema.GovHydroTurbineXmlType;
import org.ieee.odm.schema.GovHydroXmlType;
import org.ieee.odm.schema.SteamTurbineXmlType;
import org.ieee.odm.schema.TimePeriodUnitType;
import org.ieee.odm.schema.TurbineGovernorXmlType;
import org.ieee.odm.schema.TurbineXmlType;
import org.ieee.odm.schema.VoltageUnitType;


public class BPADynamicTurbineGovernorRecord {
	
	public static void processTurbineGovernorData(String str, DStabModelParser parser){
    	final String strAry[]=getTGDataFields(str);
/*    	
    	if(strAry[0].equals("GG")){ 
    		TurbineGovernorXmlType tg=XBeanTranStabSimuHelper.addNewTurbineGovernor(tranSimu);
    		tg.setTurbineGovernorType(TurbineGovernorXmlType.TurbineGovernorType.HYDRO_STREAM_GENERAL_MODEL);
    		GovHydroSteamGeneralModelXmlType gg=
    			tg.addNewTurbineGovernorModel().addNewHydroStreamGeneralModel();
    		//busId
    		String busId=strAry[1];
    		tg.addNewLocatedBus().setName(busId);			
			//bus Voltage
    		double v= new Double(strAry[2]).doubleValue();    		
    		XBeanDataSetter.setVoltageData(tg.addNewBusRatedVoltage(), v,
    				VoltageUnitType.KV);			
			//excId
    		String tgId;
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];
    			tg.addNewTgId().setName(tgId);
    		}			
			//PMAX 
    		double pmax=ModelStringUtil.getDouble(strAry[4], 0.0);
    		gg.setPMAX(pmax);
			//R
    		double r=ModelStringUtil.getDouble(strAry[5], 0.0);
    		gg.setR(r);
			
			//T1
    		double T1=ModelStringUtil.getDouble(strAry[6], 0.0);
    		XBeanDataSetter.setTimePeriodData(gg.addNewT1(), 
    					T1, TimePeriodUnitType.SEC);
			//T2
    	    double T2=ModelStringUtil.getDouble(strAry[7], 0.0);
		    XBeanDataSetter.setTimePeriodData(gg.addNewT2(), 
					       T2, TimePeriodUnitType.SEC);		   
			//T3
		    double T3=ModelStringUtil.getDouble(strAry[8], 0.0);
		    XBeanDataSetter.setTimePeriodData(gg.addNewT3(), 
					       T3, TimePeriodUnitType.SEC);			
			// T4
		    double T4=ModelStringUtil.getDouble(strAry[9], 0.0);
		    XBeanDataSetter.setTimePeriodData(gg.addNewT4(), 
					       T4, TimePeriodUnitType.SEC);			
			//T5
		    double T5=ModelStringUtil.getDouble(strAry[10], 0.0);
		    XBeanDataSetter.setTimePeriodData(gg.addNewT5(), 
					       T5, TimePeriodUnitType.SEC);
			
			//F
		    double f=ModelStringUtil.getDouble(strAry[11], 0.0);
    		gg.setF(f);
			
    	}else if(strAry[0].equals("GH")){
    		TurbineGovernorXmlType tg=XBeanTranStabSimuHelper.addNewTurbineGovernor(tranSimu);
    		tg.setTurbineGovernorType(TurbineGovernorXmlType.TurbineGovernorType.HYDRO_GOVERNER_AND_TURBINE);
    		GovHydroTurbineXmlType gh=
    			tg.addNewTurbineGovernorModel().addNewHydroGovernerAndTurbine();
    		//busId
    		String busId=strAry[1];
    		tg.addNewLocatedBus().setName(busId);			
			//bus Voltage
    		double v= ModelStringUtil.getDouble(strAry[2], 0.0);
    		XBeanDataSetter.setVoltageData(tg.addNewBusRatedVoltage(), v,
    				VoltageUnitType.KV);			
			//excId
    		String tgId;
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];
    			tg.addNewTgId().setName(tgId);
    		}			
			//PMAX 
    		double pmax=ModelStringUtil.getDouble(strAry[4], 0.0);
    		gh.setPMAX(pmax);
    		//R
    		double r=ModelStringUtil.getDouble(strAry[5], 0.0);
    		gh.setR(r);
			//TG
    		double Tg=ModelStringUtil.getDouble(strAry[6], 0.0);    		
    		
		    XBeanDataSetter.setTimePeriodData(gh.addNewTG(), 
					       Tg, TimePeriodUnitType.SEC);			
			//TP
		    double Tp=ModelStringUtil.getDouble(strAry[7], 0.0);
		    XBeanDataSetter.setTimePeriodData(gh.addNewTP(), 
					       Tp, TimePeriodUnitType.SEC);		
			//TD
		    double Td= ModelStringUtil.getDouble(strAry[8], 0.0);
		    XBeanDataSetter.setTimePeriodData(gh.addNewTD(), 
				       Td, TimePeriodUnitType.SEC);			
			// TW/2
		    double Tw= ModelStringUtil.getDouble(strAry[9], 0.0);
		    XBeanDataSetter.setTimePeriodData(gh.addNewTWhalf(), 
				       Tw, TimePeriodUnitType.SEC);			
			//VELCLOSE
		    double Uc=ModelStringUtil.getDouble(strAry[10], 0.0);
    		gh.setUc(Uc);
			
			//FVELOPEN
    		double Uo=ModelStringUtil.getDouble(strAry[11], 0.0);
    		gh.setUo(Uo);
			
			//Dd
    		double Dd=ModelStringUtil.getDouble(strAry[12], 0.0);
    		gh.setD4(Dd);		
    	}else if(strAry[0].equals("GS")){
    		TurbineGovernorXmlType tg=XBeanTranStabSimuHelper.addNewTurbineGovernor(tranSimu);
    		tg.setTurbineGovernorType(TurbineGovernorXmlType.TurbineGovernorType.HYDRO_GOVERNER);
    		GovHydroXmlType gs=
    			tg.addNewTurbineGovernorModel().addNewHydroGoverner();
    		//busId
    		String busId=strAry[1];
    		tg.addNewLocatedBus().setName(busId);
    		//bus Voltage
    		double v= ModelStringUtil.getDouble(strAry[2], 0.0);    		
    		XBeanDataSetter.setVoltageData(tg.addNewBusRatedVoltage(), v,
    				VoltageUnitType.KV);			
			//excId
    		String tgId;
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];
    			tg.addNewTgId().setName(tgId);
    		}			
			//PMAX 
    		double pmax=new Double(strAry[4]).doubleValue();
    		gs.setPMAX(pmax);
    		//PMIN
    		double pmin=ModelStringUtil.getDouble(strAry[5], 0.0);
    		gs.setPMIN(pmin);	
    			
    		//R
    		double r=ModelStringUtil.getDouble(strAry[6], 0.0);
    		gs.setR(r);
			//T1
    		double T1=ModelStringUtil.getDouble(strAry[7], 0.0);
    		XBeanDataSetter.setTimePeriodData(gs.addNewT1(), 
    				       T1, TimePeriodUnitType.SEC);
    		
    					
			//T2
    		double T2=ModelStringUtil.getDouble(strAry[8], 0.0);
    		XBeanDataSetter.setTimePeriodData(gs.addNewT2(), 
    				       T2, TimePeriodUnitType.SEC);
    				    		
			// T3
		    double T3= ModelStringUtil.getDouble(strAry[9], 0.0);
		    XBeanDataSetter.setTimePeriodData(gs.addNewT3(), 
				       T3, TimePeriodUnitType.SEC);			
			//VELOPEN
		    double Uo=ModelStringUtil.getDouble(strAry[4], 0.0);
    		gs.setU0(Uo);			
			//FVELCLOSE
    		double Uc=ModelStringUtil.getDouble(strAry[11], 0.0);
    		gs.setUC(Uc);
    	}else if(strAry[0].equals("TA")){
    		//busId
    		String busId=strAry[1];    		
    		String tgId="";
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];    			
    		}
    		TurbineGovernorXmlType tgOld=XBeanParserHelper.getTGRecord(tranSimu, busId, tgId);
    		if(tgOld.getTurbineGovernorType().equals
    				(TurbineGovernorXmlType.TurbineGovernorType.HYDRO_GOVERNER)){
    			TurbineXmlType tur=tgOld.getTurbineGovernorModel().getHydroGoverner().addNewTurbine();
    			SteamTurbineXmlType steamTur=tur.addNewSteamTurbine();
    			//TCH
    			double TCH= new Double(strAry[4]).doubleValue();
    		    XBeanDataSetter.setTimePeriodData(steamTur.addNewTCH(),
    		    		TCH, TimePeriodUnitType.SEC);	    			
    			//k1
    		    double k1=new Double(strAry[5]).doubleValue();
    		    steamTur.setK(k1);
    		}
    		
    	}else if(strAry[0].equals("TB")){
    		//busId
    		String busId=strAry[1];    		
    		String tgId="";
    		if(!strAry[3].equals("")){
    			tgId=strAry[3];    			
    		}
    		TurbineGovernorXmlType tgOld=XBeanParserHelper.getTGRecord(tranSimu, busId, tgId);
    		if(tgOld.getTurbineGovernorType().equals
    				(TurbineGovernorXmlType.TurbineGovernorType.HYDRO_GOVERNER)){
    			TurbineXmlType tur=tgOld.getTurbineGovernorModel().getHydroGoverner().addNewTurbine();
    			
    			SteamTurbineXmlType steamTur=tur.addNewSteamTurbine();
    			
    			//TCH
    			double TCH= ModelStringUtil.getDouble(strAry[4], 0.0);
    		    XBeanDataSetter.setTimePeriodData(steamTur.addNewTCH(),
    		    		TCH, TimePeriodUnitType.SEC);	  
    			//FHP
    		    double FHP= ModelStringUtil.getDouble(strAry[5], 0.0);
    			steamTur.setFHP(FHP);
    			//TRH
    		    double TRH= ModelStringUtil.getDouble(strAry[6], 0.0);
    		    XBeanDataSetter.setTimePeriodData(steamTur.addNewTRH(),
    		    		TRH, TimePeriodUnitType.SEC);	    			
    			//FIP
    		    double FIP= ModelStringUtil.getDouble(strAry[7], 0.0);
    		    steamTur.setFIP(FIP);    			
    			//TCO
    		    double TCO=ModelStringUtil.getDouble(strAry[8], 0.0);
    		    XBeanDataSetter.setTimePeriodData(steamTur.addNewTCO(),
        		    		TCO, TimePeriodUnitType.SEC);
    		       		    	    			
    			// FLP
    		    double FLP=ModelStringUtil.getDouble(strAry[9], 0.0);
       		    steamTur.setFLP(FLP);
    		        		        			
    		}    
    	}
*/    	
    }
	
	 private static String[] getTGDataFields ( final String str) {
		   	
	    	final String[] strAry = new String[19];
	    	
	    	try{
	    		if(str.substring(0, 2).trim().equals("GG")){
		    		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
					//busId
					strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
					//bus Voltage
					strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
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
		    		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
					//busId
					strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
					//bus Voltage
					strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
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
					
		    		
		    	}
		    	else if(str.substring(0, 2).trim().equals("GS")){
		    		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
					//busId
					strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
					//bus Voltage
					strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
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
		    		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
					//busId
					strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
					//bus Voltage
					strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
					//excId
					strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,16, 16).trim();
					//TCH
					strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,17, 21).trim();
					//k1
					strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 26).trim();
		    	}else if(str.substring(0, 2).trim().equals("TB")){
		    		strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
					//busId
					strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,4, 11).trim();
					//bus Voltage
					strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,12, 15).trim();
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
					
		    	}
	    	}catch(Exception e){
	    		ODMLogger.getLogger().severe(e.toString());
	    	}
	    	return strAry;
		
	    }

}
