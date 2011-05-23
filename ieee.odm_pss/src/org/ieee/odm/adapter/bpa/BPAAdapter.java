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

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.impl.BusRecord;
import org.ieee.odm.adapter.bpa.impl.GenLoadDataModifyRecord;
import org.ieee.odm.adapter.bpa.impl.LineBranchRecord;
import org.ieee.odm.adapter.bpa.impl.NetRecord;
import org.ieee.odm.adapter.bpa.impl.XfrBranchRecord;
import org.ieee.odm.adapter.bpa.impl.dynamic.BPADynamicRecord;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.dstab.DStabModelParser;
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
		BusRecord.resetBusCnt();
		this.factory = new ObjectFactory();		
	}
	
	protected IODMModelParser parseInputFile(final IFileReader din, String encoding) throws Exception {
		String str="";
		// first line, as a sign to run power flow data or transient data
		// there may be comments starting with . or blank line
		do{
			str = din.readLine();
		} while (str.startsWith(".") || str.trim().equals(""));
		
		if(str.equals("loadflow") || str.contains("POWERFLOW")){
			AclfModelParser parser = new AclfModelParser(encoding);
			parser.getStudyCase().setAnalysisCategory(AnalysisCategoryEnumType.LOADFLOW);
			parser.setLFTransInfo(OriginalDataFormatEnumType.BPA);

			processLoadflowInfo(parser, din);			
			return parser;
		}
		throw new ODMException("Only LF info could be prcessed");
	}
	
	protected IODMModelParser parseInputFile(IODMAdapter.NetType type, final IFileReader[] dinAry, String encoding) throws Exception {
		if (type == IODMAdapter.NetType.DStabNet) {
			DStabModelParser parser = new DStabModelParser(encoding);
			parser.getStudyCase().setAnalysisCategory(AnalysisCategoryEnumType.TRANSIENT_STABILITY);
			parser.setLFTransInfo(OriginalDataFormatEnumType.BPA);
			
			// assumption: two files, first loadflow data, second dstab info
			
			// parse Loadflow file
			String str="";
			IFileReader din = dinAry[0];
			do{
				str = din.readLine();
			} while (str.startsWith(".") || str.trim().equals(""));
			
			if(str.equals("loadflow") || str.contains("POWERFLOW")){
				processLoadflowInfo(parser, din);			
			}
			
			// parse DStab file
			din = dinAry[1];
			BPADynamicRecord.processDynamicData(din, parser);
			return parser;
		}
		else
			throw new ODMException("Functionality not implemented yet");
	}

	private void processLoadflowInfo(AclfModelParser parser, final IFileReader din) throws Exception {
		LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
		baseCaseNet.setId("Base_Case_from_BPA_loadflow_format");			

		// we set default base MVA here, since MVA line is optional
    	double baseMva = 100.0;   
		baseCaseNet.setBasePower(BaseDataSetter.createPowerMvaValue(baseMva));
    	
		NameValuePairListXmlType nvList = this.factory.createNameValuePairListXmlType();
		baseCaseNet.setNvPairList(nvList);
		
		int areaId=1;// used to arrange a number to each area 

		List<String> strList = new ArrayList<String>(100);
		String str;
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
//						System.out.println(str); //for test
					}
					else if(str.trim().startsWith("BD")||str.trim().startsWith("BM")){
						ODMLogger.getLogger().fine("load DCLine bus data");						
						// **** BPABusRecord.processDCLineBusData(str, parser.addNewBaseCaseDCLineBus(),this);
					}
					
					// since bus info could be defined at the branch info, we
					// cache branch info for late processing
					else if( (str.trim().startsWith("L")||str.trim().startsWith("E"))
							&&!str.trim().startsWith("LD")&&!str.trim().startsWith("LM")){
						ODMLogger.getLogger().fine("load AC line data");
						strList.add(str);
						//LineBranchRecord.processBranchData(str, parser);
					}
					else if( str.trim().startsWith("T")){
						ODMLogger.getLogger().fine("load transformer data");
						strList.add(str);
						//XfrBranchRecord.processXfrData(str, parser);
					}
					else if(str.trim().startsWith("R")){
						ODMLogger.getLogger().fine("load transformer adjustment data");
						strList.add(str);
						//XfrBranchRecord.processXfrAdjustData(str, parser);
					}
					else if( str.trim().startsWith("LD")||str.trim().startsWith("LM")){
						ODMLogger.getLogger().fine("load DC Line data");
						strList.add(str);
						// *** BPABranchRecord.processDCLineBranchData(str, parser.addNewBaseCaseDCLineBranch(),
						// ***		parser,baseCaseNet, this);
					}
					// the gen and load data modification is usually defined  at the end of all network data
					else if( str.trim().startsWith("PA")||str.trim().startsWith("PZ")||str.trim().startsWith("PO")
							||str.trim().startsWith("PC")||str.trim().startsWith("PB")){
						ODMLogger.getLogger().fine("load Gen AND Load modification data");
						GenLoadDataModifyRecord.processGenLoadModificationData(str,parser);

					}
					
					else{
						NetRecord.processReadComment(str, baseCaseNet,this);
					}						
				}
				catch (final Exception e) {
					ODMLogger.getLogger().severe("Error, input : " + str + "\n" + e.toString());
					e.printStackTrace();
				}					
			}
		} while(!str.trim().equals("(END)")&&!str.trim().equals("(STOP)"));
		
		// processing branch info after all bus info are processed 
		processBranchInfo(strList, parser);
		
		//process Gen and Load Modification Data 
	}
	
	/**
	 * process branch info
	 * 
	 * @param strList
	 * @param parser
	 * @throws ODMException
	 */
	private void processBranchInfo(List<String> strList, AclfModelParser parser) throws ODMException {
		for (String str : strList) {
			if( (str.trim().startsWith("L")||str.trim().startsWith("E"))
					&&!str.trim().startsWith("LD")&&!str.trim().startsWith("LM")){
				ODMLogger.getLogger().fine("load AC line data");
				LineBranchRecord.processBranchData(str, parser);
			}
			else if( str.trim().startsWith("T")){
				ODMLogger.getLogger().fine("load transformer data");
				XfrBranchRecord.processXfrData(str, parser);
			}
			else if(str.trim().startsWith("R")){
				ODMLogger.getLogger().fine("load transformer adjustment data");
				XfrBranchRecord.processXfrAdjustData(str, parser);
			}
			else if( str.trim().startsWith("LD")||str.trim().startsWith("LM")){
				ODMLogger.getLogger().fine("load DC Line data");
				// *** BPABranchRecord.processDCLineBranchData(str, parser.addNewBaseCaseDCLineBranch(),
				// ***		parser,baseCaseNet, this);
			}
		}
	}
}
