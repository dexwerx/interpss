/*
 * @(#)BPAAdapter.java   
 *
 * Copyright (C) 2006 www.interpss.org
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
 * @Author Stephen Hou
 * @Version 1.0
 * @Date 08/11/2008
 * 
 *   Revision History
 *   ================
 *   
 *   modified for Jaxb Mike Zhou 02/28/2011
 *
 */

package org.ieee.odm.adapter.bpa;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.bpa.impl.BusRecord;
import org.ieee.odm.adapter.bpa.impl.LineBranchRecord;
import org.ieee.odm.adapter.bpa.impl.NetRecord;
import org.ieee.odm.adapter.bpa.impl.XfrBranchRecord;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.BaseDataSetter;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.AnalysisCategoryEnumType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NameValuePairListXmlType;
import org.ieee.odm.schema.ObjectFactory;
import org.ieee.odm.schema.OriginalDataFormatEnumType;


public class BPAAdapter  extends AbstractODMAdapter {
	public final static String Token_CaseType = "Type";
	public final static String Token_ProjectName = "Original Project Name";
	public final static String Token_CaseId = "Case Identification";
	public final static String Token_BN="Bus Name";
	
	private ObjectFactory factory = null;

	public BPAAdapter() {
		super();
		this.factory = new ObjectFactory();		
	}
	
	protected IODMModelParser parseInputFile(final IFileReader din) throws Exception {
		String str="";
		// first line, as a sign to run power flow data or transient data
		// there may be comments starting with . or blank line
		do{
			str = din.readLine();
			
		} while (str.startsWith(".") || str.trim().equals(""));
		
		if(str.equals("loadflow") || str.contains("POWERFLOW")){
			AclfModelParser parser = new AclfModelParser();
			parser.getStudyCase().setAnalysisCategory(AnalysisCategoryEnumType.LOADFLOW);
			parser.setLFTransInfo(OriginalDataFormatEnumType.BPA);

			LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
			baseCaseNet.setId("Base_Case_from_BPA_loadflow_format");			

			// we set default base MVA here, since MVA line is optional
	    	double baseMva = 100.0;   
			baseCaseNet.setBasePower(BaseDataSetter.createPowerMvaValue(baseMva));
	    	
			NameValuePairListXmlType nvList = this.factory.createNameValuePairListXmlType();
			baseCaseNet.setNvPairList(nvList);
			
			int areaId=1;// used to arrange a number to each area 

			do{
				str = din.readLine();					
				if(!str.trim().equals("(END)")&&!str.trim().equals("(STOP)")){
					try{
						if(str.startsWith(".")||str.startsWith("C")){
							ODMLogger.getLogger().fine("load comment");
						}
						else if(str.startsWith("(POWERFLOW")||str.startsWith("/")
								||str.startsWith(">")){
							ODMLogger.getLogger().fine("load header data");
							NetRecord.processNetData(str,nvList,baseCaseNet);
						}
						else if(str.startsWith("A")||str.trim().startsWith("I")){
							NetRecord.processAreaData(str, parser,	baseCaseNet, this,areaId++);
							
						}
						else if((str.trim().startsWith("B")||str.trim().startsWith("+")
								||str.trim().startsWith("X"))
								&&!str.trim().startsWith("BD")&&!str.trim().startsWith("BM")){
							ODMLogger.getLogger().fine("load AC bus data");						
							BusRecord.processBusData(str, parser);
//							System.out.println(str); //for test
						}
						else if(str.trim().startsWith("BD")||str.trim().startsWith("BM")){
							ODMLogger.getLogger().fine("load DCLine bus data");						
							// **** BPABusRecord.processDCLineBusData(str, parser.addNewBaseCaseDCLineBus(),this);
						}
						
						else if( (str.trim().startsWith("L")||str.trim().startsWith("E"))
								&&!str.trim().startsWith("LD")&&!str.trim().startsWith("LM")){
							ODMLogger.getLogger().fine("load AC line data");
							LineBranchRecord.processBranchData(str, parser, baseCaseNet);
						}
						else if( str.trim().startsWith("T")){
							ODMLogger.getLogger().fine("load transformer data");
							XfrBranchRecord.processXfrData(str, parser, baseCaseNet);
						}
						else if(str.trim().startsWith("R")){
							ODMLogger.getLogger().fine("load transformer adjustment data");
							XfrBranchRecord.processXfrAdjustData(str, parser, baseCaseNet);
						}
						else if( str.trim().startsWith("LD")||str.trim().startsWith("LM")){
							ODMLogger.getLogger().fine("load DC Line data");
							// *** BPABranchRecord.processDCLineBranchData(str, parser.addNewBaseCaseDCLineBranch(),
							// ***		parser,baseCaseNet, this);
						}
						else{
							NetRecord.processReadComment(str, baseCaseNet,this);
						}						
					}
					catch (final Exception e) {
						ODMLogger.getLogger().severe("Error, input : " + str);
						e.printStackTrace();
					}					
				}
			}while(!str.trim().equals("(END)")&&!str.trim().equals("(STOP)"));
			
			return parser;
		}
		else if(str.equals("transient")){
//			parser.getStudyCase().setAnalysisCategory(AnalysisCategoryEnumType.TRANSIENT_STABILITY);
//			baseCaseNet.setId("Base_Case_from_BPA_transient_format");
//
//			TransientSimulationXmlType tranSimu= parser.getDefaultTransSimu();
//			do{
//				str = din.readLine();
//				if(!str.trim().equals("(END)")){
//					try{
//						if(str.startsWith(".")||str.startsWith("C")){
//							
//							getLogger().fine("load comment");
//							
//						}else if(str.startsWith("(POWERFLOW")||str.startsWith("/")
//								||str.startsWith(">")){
//							getLogger().fine("load header data");
//							processNetData(str,nvList,baseCaseNet);
//						}else if(str.startsWith("A")||str.trim().startsWith("I")){							
//							processAreaData(str, parser,	baseCaseNet, this, areaId++);
//							
//						}else if((str.trim().startsWith("B")||str.trim().startsWith("+")
//								||str.trim().startsWith("X"))
//								&&!str.trim().startsWith("BD")&&!str.trim().startsWith("BM")){
//							getLogger().fine("load AC bus data");						
//							BPABusRecord.processBusData(str,parser.addNewBaseCaseBus(),this);
//						}else if(str.trim().startsWith("BD")||str.trim().startsWith("BM")){
//							getLogger().fine("load DCLine bus data");						
//							// **** BPABusRecord.processDCLineBusData(str, parser.addNewBaseCaseDCLineBus(),this);
//						}
//						
//						else if( (str.trim().startsWith("L")||str.trim().startsWith("E"))
//								&&!str.trim().startsWith("LD")&&!str.trim().startsWith("LM")){
//							getLogger().fine("load AC line data");
//							BPABranchRecord.processBranchData(str, parser.addNewBaseCaseBranch(), 
//									parser,baseCaseNet, this);
//						}else if( str.trim().startsWith("T")){
//							getLogger().fine("load transformer data");
//							BPABranchRecord.processXfrData(str, parser.addNewBaseCaseBranch(), 
//									parser,baseCaseNet, this);
//						}else if(str.trim().startsWith("R")){
//							getLogger().fine("load transformer adjustment data");
//							BPABranchRecord.processXfrAdjustData(str, baseCaseNet, this);
//						}
//						else if( str.trim().startsWith("LD")||str.trim().startsWith("LM")){
//							getLogger().fine("load DC Line data");
//							// *** BPABranchRecord.processDCLineBranchData(str, parser.addNewBaseCaseDCLineBranch(),
//							// ***		parser,baseCaseNet, this);
//						}else{
//							processReadComment(str, baseCaseNet,this);
//						}						
//					}catch (final Exception e) {
//						e.printStackTrace();
//					}					
//				}else if(str.startsWith("(END)"))	{					
//					
//				}				
//			}while(!str.trim().equals("(END)"));		
//			
//			BPADynamicRecord.processDynamicData(str, tranSimu, din,
//					parser,this);
		}
		
		return null;
	}
}