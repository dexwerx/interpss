/*
 * @(#)BPANetRecord.java   
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

import java.util.StringTokenizer;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.ApparentPowerXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NameValuePairListXmlType;
import org.ieee.odm.schema.NetAreaXmlType;
import org.ieee.odm.schema.NetZoneXmlType;

public class BPANetRecord {
	/*
	 *   Network data
	 *   ============ 
	 */
	public static void processReadComment(final String str, final LoadflowNetXmlType baseCaseNet){
		
	//	adapter.logErr("This line is for comment only:  "+str);
		// to do in future
		
	}

	public static void processNetData(final String str, final NameValuePairListXmlType nvList, final LoadflowNetXmlType baseCaseNet) {
			// parse the input data line
			final String[] strAry = getNetDataFields(str);			
	        //read powerflow, caseID,projectName, 			
			if (strAry[0]!= null ){
				BaseJaxbHelper.addNVPair(nvList, strAry[0], strAry[1]);
				ODMLogger.getLogger().fine(strAry[0] +": " + strAry[1]);
			}
			
			if (strAry[2]!= null ){
				BaseJaxbHelper.addNVPair(nvList, strAry[2], strAry[4]);
				ODMLogger.getLogger().fine(strAry[2]+": " + strAry[4] );
			}			
			// more name-vale could be added in future 
			
			if(str.startsWith("/MVA_BASE")){
				if(strAry[5]!= null) {
					double baseMva = new Double(strAry[5]).doubleValue(); // in MVA
					ApparentPowerXmlType baseKva = baseCaseNet.getBasePower();
			    	baseKva.setValue(baseMva);   
				}
			}
	}

	/*
	 *   area data
	 *   ================ 
	 */

	public static void processAreaData(final String str,final AclfModelParser parser ,
			final LoadflowNetXmlType baseCaseNet, int areaId	) {
		
		final String[] strAry = getAreaDataFields(str);
		int zoneId=0;
	
		if(str.trim().startsWith("A")||str.trim().startsWith("AC")){	
			NetAreaXmlType area = parser.createNetworkArea();
			
			final String areaType=strAry[0];			
			if(!strAry[1].equals("")){
				final String modCode=strAry[1];				
			}
			String areaName="";
			if(!strAry[2].equals("")){
				areaName=strAry[2];
				area.setName(areaName);
				area.setNumber(areaId);
			}
			
//			String slackBusId;
//			double ratedVoltage;
//			if(!strAry[3].equals("")){
//				slackBusId=strAry[3];
//				area.addNewSwingBusId().setName(slackBusId);
//			}
//            if(!strAry[4].equals("")){
//				ratedVoltage =new Double(strAry[4]).doubleValue();
//				area.setRatedVoltage(BaseDataSetter.createVoltageValue(ratedVoltage, VoltageUnitType.KV));
//			}
//            double exchangeMW=0.0;
//            if(!strAry[5].equals("")){            	
//            	AclfDataSetter.setPowerData(area.addNewTotalExchangePower(),
//            			           exchangeMW, 0, ApparentPowerUnitType.MVA);            	
//            }        
            
            if(!strAry[6].trim().equals("")){
            	int Str6length=strAry[6].length();
            	
            	final String[] s= new String[20];
            	int cnt=0, i=0;            	
            	while((!strAry[6].substring(i, i+2).equals(""))&& i+2<=Str6length){            		
            		s[cnt]=strAry[6].trim().substring(i, i+2);            		
            		NetZoneXmlType zone= parser.createNetworkLossZone();
            		zone.setName(s[cnt]);
            		
            		String zoneRanking =new  Integer(areaId).toString()+ new Integer(zoneId++).toString();            		
            		int out= new Integer(zoneRanking).intValue();           		
            		zone.setNumber(out);
            		if(i+2==Str6length){
            			break;
            		}
            		i=i+3;
            		cnt=cnt+1;            		
            	}            	
            }
		}
		else if(str.trim().startsWith("AO")){
			final String dataType=strAry[0];
			
			if(!strAry[1].equals("")){
				final String modCode= strAry[1];
			}
			if(!strAry[2].trim().equals("")){
//				String areaName=strAry[2];
//				NetAreaXmlType area=BaseJaxbHelper.getAreaRecordByAreaName(areaName, baseCaseNet);
//				if(area==null){
//					area.setName(areaName);
//				}
			}
			if(!strAry[3].trim().equals("")){
				// suppose there is a maximum of 40 areas
            	final String s[]= new String[40];
            	int cnt=0, i=0;            	
            }
		}
		else if(str.trim().startsWith("I")){
			final String dataType=strAry[0];
			if(!strAry[1].equals("")){
				final String modCode= strAry[1];
			}
			String fBus="";
			if(!strAry[2].equals("")){
			    fBus= strAry[2];			    
			}
			String tBus="";
			if(!strAry[3].equals("")){
			    tBus= strAry[3];
			}
			double exchangePower=0.0;
			if(!strAry[4].equals("")){
				exchangePower= new Double(strAry[4]).doubleValue();				
			}			
			if(!fBus.equals("")&& exchangePower!=0){				
//				NetAreaXmlType area=BaseJaxbHelper.getAreaRecordByAreaName(fBus, baseCaseNet);	
//				NetAreaXmlType.ExchangePower exchange=area.addNewExchangePower();
//				exchange.setToArea(tBus);
//				XBeanDataSetter.setPowerData(exchange.addNewExchangePower(),
//     			    exchangePower, 0, ApparentPowerUnitType.MVA);				
			}			
		}	
		
	}
	
	private static String[] getNetDataFields(final String str) {
		final String[] strAry = new String[7];
		//the first line data
		if(str.startsWith("(POWERFLOW")){
		     String s1[]= new String[3];		     
		     final StringTokenizer st= new StringTokenizer(str, ",");
		     s1[0]=st.nextToken();
		     s1[1]=st.nextToken();
		     s1[2]=st.nextToken();		     
		     final StringTokenizer st1= new StringTokenizer(s1[1], "=");	 
		     strAry[0]=st1.nextToken();
		     strAry[1]=st1.nextToken();
		     final StringTokenizer st2= new StringTokenizer(s1[2], "=");
		     strAry[2]=st2.nextToken();
		     strAry[3]=st2.nextToken();
		     int length= strAry[3].length();
		     strAry[4]=strAry[3].substring(0, length-1);
			}
		// select certain concerned data to added
		//strAry[4]= baseMVA
		if(str.startsWith("/MVA_BASE")){			
			strAry[5]=str.substring(10, str.length()-1);			
		}		
	   return strAry;
	}
	
	private static String[] getAreaDataFields(final String str) {
		final String[] strAry = new String[7];
		
		try{
			if (str.trim().startsWith("A")||str.trim().startsWith("AC")){			
				strAry[0] = ModelStringUtil.getStringReturnEmptyString(str, 1, 2);
				strAry[1] = ModelStringUtil.getStringReturnEmptyString(str, 3, 3);
				strAry[2] = ModelStringUtil.getStringReturnEmptyString(str, 4, 13);
				strAry[3] = ModelStringUtil.getStringReturnEmptyString(str, 14, 21);
				strAry[4] = ModelStringUtil.getStringReturnEmptyString(str, 22, 25);
				strAry[5] = ModelStringUtil.getStringReturnEmptyString(str, 26, 34);
				// zones within area
				int strlength=str.trim().length();
				
				strAry[6] = ModelStringUtil.getStringReturnEmptyString(str,36, strlength);;
			}
			else if(str.trim().startsWith("AO")){ 
			    strAry[0] = ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
				strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,3, 3).trim();
				// area name
				strAry[2] = ModelStringUtil.getStringReturnEmptyString(str,4, 13).trim();
				// zones within the area
				int strlength=str.length();
				strAry[3] = ModelStringUtil.getStringReturnEmptyString(str,15, strlength).trim();				
			}
			else if(str.trim().startsWith("I")){
			    strAry[0] = ModelStringUtil.getStringReturnEmptyString(str, 1, 1);
				strAry[1] = ModelStringUtil.getStringReturnEmptyString(str, 3, 3);
				strAry[2] = ModelStringUtil.getStringReturnEmptyString(str, 4, 13);
				strAry[3] = ModelStringUtil.getStringReturnEmptyString(str, 15, 24);
				strAry[4] = ModelStringUtil.getStringReturnEmptyString(str, 27, 34);		
			}	
		}	catch (Exception e){
			ODMLogger.getLogger().severe(e.toString());
		}
		return strAry;
	}
}
	
