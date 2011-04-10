/*
 * @(#)ContingencyFileParser.java   
 *
 * Copyright (C) 2008 www.interpss.org
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
 * @Author Mike Zhou
 * @Version 1.0
 * @Date 04/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.custom.run.psseCon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AclfStudyCaseListXmlType;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.BranchChangeRecXmlType;
import org.interpss.xml.schema.InterPSSXmlType;
import org.interpss.xml.schema.ModificationXmlType;

import com.interpss.common.util.StringUtil;


public class ContingencyFileParser {
	public static final String Token_ConBegin 	= "CONTINGENCY";
	public static final String Token_ConEnd 	= "END";
	public static final String Token_FileEnd 	= "END";
	public static final String Token_OpenLine 	= "OPEN LINE";
	public static final String Token_Comment 	= "COM";
	public static final String Token_Comment1 	= "/";
	
	public static final int Position_OpenLline_FromBus 	= 4;
	public static final int Position_OpenLline_ToBus 	= 7;
	public static final int Position_OpenLline_CirNo 	= 9;

	public static enum FileStatus {ConBegin, ConEnd, FileEnd, UnKnown}; 
	
	/**
	 * parse the contingency control file for modifications to the network for contingency analysis
	 * 
	 * @param filepath contingency control file path
	 * @return
	 * @throws Exception
	 */
	public static InterPSSXmlType parseControlFile(AnalysisRunDataType type, String filepath) throws Exception {
		final File file = new File(filepath);
		final InputStream stream = new FileInputStream(file);
		final BufferedReader din = new BufferedReader(new InputStreamReader(stream));	
		
		List<String> strList = new ArrayList<String>();
		String lineStr = null;
		do {
			lineStr = din.readLine();
			if (lineStr != null)
				strList.add(lineStr);
		} while (lineStr != null);
		
		String[] strAry = new String[strList.size()];
		int cnt = 0;
		for (String s : strList)
			strAry[cnt++] = s;
		
		return parseControlScripts(type, strAry);
	}
	
	/**
	 * parse the string array for modifications to the network for contingency analysis
	 * 
	 * @param strAry
	 * @return
	 * @throws Exception
	 */
	public static InterPSSXmlType parseControlScripts(AnalysisRunDataType type, String[] strAry) throws Exception {
		IpssXmlParser parser = new IpssXmlParser(type);
		if (type == AnalysisRunDataType.CONTINGENCY_ANALYSIS)
			createAclfStudyCaseList(parser.getContingencyAnalysis().getAclfStudyCaseList(), strAry);
		else
			createAclfStudyCaseList(parser.getRunAclfStudyCase().getAclfStudyCaseList(), strAry);
		
		//System.out.println(parser.toString());
		return parser.getRootDoc();
	}

	public static void createAclfStudyCaseList(AclfStudyCaseListXmlType caseList, String[] strAry) throws Exception {
		ContingencyFileParser.FileStatus status = ContingencyFileParser.FileStatus.UnKnown;
		int conCnt = 0, changeCnt = 0;
		ModificationXmlType mod = null;
		
		for (String lineStr : strAry) {
			if (lineStr.trim().equals("") || lineStr.startsWith("COM") || lineStr.startsWith("/")) {
				// empty line, do noting
			}
			else if (lineStr.startsWith(ContingencyFileParser.Token_ConBegin)) {
				status = ContingencyFileParser.FileStatus.ConBegin;
				conCnt++;

				String[] sAry = StringUtil.strToken2Array(lineStr, " ");
				String conName = sAry[1];
//				System.out.println("[" + conCnt + "], " + conName);
				
				AclfStudyCaseXmlType scase = IpssXmlParser.getFactory().createAclfStudyCaseXmlType(); 
				caseList.getAclfStudyCase().add(scase);
				scase.setRecId(conName+"_"+conCnt);
				scase.setRecName(conName);
				scase.setRecDesc(lineStr);

				mod = IpssXmlParser.getFactory().createModificationXmlType();
				scase.setModification(mod);
				mod.setBranchChangeRecList(IpssXmlParser.getFactory().createModificationXmlTypeBranchChangeRecList());
				changeCnt = 0;
			}
			else if (lineStr.startsWith(ContingencyFileParser.Token_ConEnd) && status != ContingencyFileParser.FileStatus.ConEnd) {
				status = ContingencyFileParser.FileStatus.ConEnd;
			}
			else if (lineStr.startsWith(ContingencyFileParser.Token_FileEnd)) {
				status = ContingencyFileParser.FileStatus.FileEnd;
			}
			else {
				//System.out.println(lineStr);
				changeCnt++;
				if (lineStr.startsWith(ContingencyFileParser.Token_OpenLine)) {
					
					BranchChangeRecXmlType braChange = IpssXmlParser.getFactory().createBranchChangeRecXmlType(); 
					mod.getBranchChangeRecList().getBranchChangeRec().add(braChange);
					braChange.setRecId("OpenLine_" + conCnt + "_" + changeCnt);
					braChange.setRecName("Open line, contingency " + conCnt + " event " + changeCnt);
					braChange.setRecDesc(lineStr);
					
					String[] sAry = StringUtil.strToken2Array(lineStr, " ");
					String fromBus = sAry[ContingencyFileParser.Position_OpenLline_FromBus];
					String toBus = sAry[ContingencyFileParser.Position_OpenLline_ToBus];
					// per spec, default cirId = 1;
					String cirId = "1";
					if (sAry.length >= ContingencyFileParser.Position_OpenLline_CirNo+1)
						cirId = sAry[ContingencyFileParser.Position_OpenLline_CirNo];
//					System.out.println(fromBus + ", " + toBus + ", " + cirId);
					braChange.setFromBusId(fromBus);
					braChange.setToBusId(toBus);
					braChange.setCircuitNumber(cirId);
					braChange.setOffLine(true);
				}
			}
		}
	}
}
