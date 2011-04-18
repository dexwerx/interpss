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

import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.ModelStringUtil;

public class BPADynamicRecord {	
	
	static int dataType=0;
	final static int header=1;
	final static int faultOperation=2;
	final static int generatorData=3;
	final static int exciterData=4;
	final static int pssData=5;
	final static int turbine_governorData=6;
	final static int loadData=7;
	final static int sequenceData=8;
	
	
	public static int getDataType(String str,BPAAdapter adapter){	
		
				
			
		 if (str.startsWith(".")&& str.startsWith("")){
				dataType=0;
			}else if(str.startsWith("CASE")||str.startsWith("SOL")) {
				dataType=header;
			}else if(str.startsWith("LS")){
				dataType=faultOperation;
			}else if(str.substring(0, 2).trim().equals("M")||
					str.substring(0, 2).trim().equals("MF")||
					str.substring(0, 2).trim().equals("MC")||
					str.substring(0, 2).trim().equals("LN")){
				
				dataType=generatorData;
			}else if (str.substring(0, 2).trim().equals("FA")||
					str.substring(0, 2).trim().equals("FB")||str.substring(0, 2).trim().equals("FC")
					||str.substring(0, 2).trim().equals("FD")||str.substring(0, 2).trim().equals("FE")||
					str.substring(0, 2).trim().equals("FG")
					||str.substring(0, 2).trim().equals("FH")||str.substring(0, 2).trim().equals("FJ")
					||str.substring(0, 2).trim().equals("FK")||str.substring(0, 2).trim().equals("FL")
					||str.substring(0, 2).trim().equals("FQ")||str.substring(0, 2).trim().equals("FV")
					||str.substring(0, 1).trim().equals("E")||
					str.substring(0, 2).trim().equals("FZ")||str.substring(0, 2).trim().equals("F+")){
				dataType=exciterData;
			}else if(str.substring(0, 2).trim().equals("SS")||
					str.substring(0, 2).trim().equals("SP")||str.substring(0, 2).trim().equals("SG")
					||str.substring(0, 2).trim().equals("SI")||str.substring(0, 3).trim().equals("SI+")){
				dataType=pssData;
			}else if(str.substring(0, 2).trim().equals("GS")||
					str.substring(0, 2).trim().equals("TA")||str.substring(0, 2).trim().equals("TB")
					||str.substring(0, 2).trim().equals("GH")||str.substring(0, 2).trim().equals("GG")){
				dataType=turbine_governorData;
			}else if(str.substring(0, 2).trim().equals("LA")||
					str.substring(0, 2).trim().equals("LB")||str.substring(0, 2).trim().equals("MI")){
				dataType=loadData;				
			}else if(str.substring(0, 2).trim().equals("LO")||str.substring(0, 2).trim().equals("XO")
					||str.substring(0, 2).trim().equals("XR")||str.substring(0, 2).trim().equals("LM")){
				dataType=sequenceData;
			}else {				
				adapter.logErr("This line data is not processed"+"   "+"'"+str+"'");
				
			}		 
		 return dataType;
	}
	
	public static void processDynamicData(String str, TransientSimulationXmlType tranSimu, 
			final IFileReader din,
			XBeanODMModelParser parser ,BPAAdapter adapter) throws Exception{
		PSSNetworkXmlType baseCaseNet=parser.getBaseCase();
		
		do{
			str= din.readLine();
			if(!str.startsWith("90")){
				getDataType(str,adapter);
				try{
					if(dataType==header){
						processHeaderData(str, tranSimu,adapter);
					}else if(dataType==faultOperation){
						BPADynamicFaultOperationRecord.processFaultOperationData(str, tranSimu,adapter);
					}else if(dataType==generatorData){
						BPADynamicGeneratorRecord.processGeneratorData(str, tranSimu, baseCaseNet,adapter);
					}else if(dataType==exciterData){
						BPADynamicExciterRecord.processExciterData(str, tranSimu, parser,adapter);
					}else if(dataType==turbine_governorData){
						BPADynamicTurbineGovernorRecord.processTurbineGovernorData(str, tranSimu, 
								                        parser,adapter);
					}else if(dataType==pssData){
						BPADynamicPSSRecord.processPSSData(str, tranSimu, parser,adapter);
					}else if(dataType==loadData){
						BPADynamicLoadCharacteristicRecord.processLoadCharacteristicData(str, 
								tranSimu, XBeanTranStabSimuHelper.addNewLoad(tranSimu),adapter);
					}else if(dataType==sequenceData){
						BPADynamicSequenceRecord.processSequenceData(str, tranSimu, parser,adapter);
					}else if(dataType==0){
						
					}
					
				}catch (final Exception e){				
					e.printStackTrace();
				}				
			}			
		}while (!str.startsWith("90"));
		// when all the data is converted, calculate negative sequence data
		BPADynamicSequenceRecord.processNegativeData(parser, tranSimu);		
	}
	
	public static void processHeaderData(String str,TransientSimulationXmlType tranSimu
			,BPAAdapter adapter){
		final String strAry[]= getHeaderDataFields(str,adapter);
		
		TransientSimulationXmlType.PowerFlowInitialization pfInitial=
			tranSimu.getPowerFlowInitialization();
		
		// network solution card--SOL
		if(str.startsWith("SOL")){			
			String powerFlowMethod="PQ";
			if(!strAry[2].equals("")){
				if(new Integer(strAry[2]).intValue()==1){
					powerFlowMethod="NR";
					pfInitial.setPowerFlowMethod(TransientSimulationXmlType.
							PowerFlowInitialization.PowerFlowMethod.NR);
				}
			}else{
				pfInitial.setPowerFlowMethod(TransientSimulationXmlType.
						PowerFlowInitialization.PowerFlowMethod.PQ);
			}			
		}
		// CASE card
		if(str.startsWith("CASE")){
			//TransientSimulationXmlType.SimulationSetting simuSet= tranSimu.getSimulationSetting();
			
			String pfCase= strAry[1];
			//pfInitial.setPowerFlowCase(pfCase);
		}		
	}
    private static String[] getHeaderDataFields ( final String str,BPAAdapter adapter) {
		final String[] strAry = new String[16];
		
		try{// for SOL card
			if(str.startsWith("SOL")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str, 0, 3);
				strAry[1]=ModelStringUtil.getStringReturnEmptyString(str, 5, 6);
				strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,9, 9);
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,12, 12);
			}		
			// for Case card
			else if(str.startsWith("CASE")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 4);
				strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,6, 15);
				strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,16, 17);
				strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,20, 20);
				strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,22, 22);
				strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,23, 23);
				strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,24, 24);
				strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,24, 34);
				strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,45, 49);
				strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,50, 54);
				strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,55, 59);
				strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,60, 64);
				strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,65, 69);
				strAry[13]=ModelStringUtil.getStringReturnEmptyString(str,70, 74);
				strAry[14]=ModelStringUtil.getStringReturnEmptyString(str,75, 80);			
			}
			}catch(Exception e){
				adapter.logErr(e.toString());
			}
		
		return strAry;
    }

}
