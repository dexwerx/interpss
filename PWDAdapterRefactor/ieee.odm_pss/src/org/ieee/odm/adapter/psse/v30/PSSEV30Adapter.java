/*
 * @(#)PSSEV30Adapter.java   
 *
 * Copyright (C) 2006-2009 www.interpss.org
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
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */
package org.ieee.odm.adapter.psse.v30;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.psse.PSSEBusRecord;
import org.ieee.odm.adapter.psse.PSSENetDataRec;
import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30BusDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30DcLine2TDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30GenDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30LineDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30LoadDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30XfrDataRec;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.ObjectFactory;
import org.ieee.odm.schema.OriginalDataFormatEnumType;

public class PSSEV30Adapter extends AbstractODMAdapter{
	public final static String Token_CaseDesc = "Case Description";     
	public final static String Token_CaseId = "Case ID";		
	
	private boolean elemCntOnly = false;
	private String  elemCntStr = "";

	private ObjectFactory factory = null;

	public PSSEV30Adapter() {
		super();
		this.factory = new ObjectFactory();
	}

	public PSSEV30Adapter(boolean elemCntOnly) {
		this();
		this.elemCntOnly = elemCntOnly;
	}
	
	public String countElements(String filename) {
		try {
			parseInputFile(filename);
		} catch ( Exception e) {
			this.elemCntStr += e.toString();
		}
		return "PSS/E File elements coount\n" + this.elemCntStr;
	}
	
	@Override
	protected AclfModelParser parseInputFile(
			final IFileReader din, String encoding) throws Exception {
		AclfModelParser parser = new AclfModelParser();
		parser.setLFTransInfo(OriginalDataFormatEnumType.PSS_E);
		parser.getStudyCase().getContentInfo().setOriginalFormatVersion("PSSEV30");

		LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
		// no space is allowed for ID field
		baseCaseNet.setId("Base_Case_from_PSS_E_format");

		PsseVersion version = PsseVersion.PSSE_30;
		
  		String lineStr = null;
  		int lineNo = 0;
  		try {
      		boolean headerProcessed = false;
      		boolean busProcessed = false;
      		boolean loadProcessed = false;
      		boolean genProcessed = false;
      		boolean lineProcessed = false;
      		boolean xfrProcessed = false;
      		boolean areaInterProcessed = false;
      		boolean dcLine2TProcessed = false;
      		boolean vscDcLineProcessed = false;
      		boolean switchedShuntProcessed = false;
      		boolean xfrZCorrectionProcessed = false;
      		boolean dcLineMTProcessed = false;
      		boolean multiSectionLineGroupProcessed = false;
      		boolean zoneProcessed = false;
      		boolean interareaTransferProcessed = false;
      		boolean ownerProcessed = false;
      		boolean factsProcessed = false;
      		
      		int busCnt = 0, loadCnt = 0, genCnt = 0, lineCnt = 0, xfrCnt = 0, xfr3WCnt = 0, xfrZTableCnt = 0,
      		    areaInterCnt = 0, dcLineCnt = 0, vscDcLineCnt = 0, mtDcLineCnt = 0, factsCnt = 0,
      		    switchedShuntCnt = 0, ownerCnt = 0, interTransCnt = 0, zoneCnt = 0, multiSecCnt = 0;
      		
      		do {
      			lineStr = din.readLine();
      			if (lineStr != null) {
      				lineNo++;
      				if (!headerProcessed) {
						if (lineNo == 3) 
      						headerProcessed = true;
						if (!this.elemCntOnly)
							PSSENetDataRec.HeaderRec.procLineString(lineStr, lineNo, version, baseCaseNet, this.factory);
      				}
      				else if (!busProcessed) {
						if (isEndRecLine(lineStr)) {
							 busProcessed = true;
							 ODMLogger.getLogger().info("PSS/E Bus record processed");
							 this.elemCntStr += "Bus record " + busCnt +"\n";
						}	 
						else {
							if (!this.elemCntOnly)
								PSSEV30BusDataRec.procLineString(lineStr, version, parser);
							busCnt++;
						}	 
      				}
      				else if (!loadProcessed) {
						if (isEndRecLine(lineStr)) {
							 loadProcessed = true;
							 ODMLogger.getLogger().info("PSS/E Load record processed");
							 this.elemCntStr += "Load record " + loadCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSEV30LoadDataRec.procLineString(lineStr, version, parser);
							loadCnt++;
						}	 
      				}
      				else if (!genProcessed) {
						if (isEndRecLine(lineStr)) {
							 genProcessed = true;
							 ODMLogger.getLogger().info("PSS/E Gen record processed");
							 this.elemCntStr += "Gen record " + genCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSEV30GenDataRec.procLineString(lineStr, version, parser);
							genCnt++;
						}	 
      				}
      				else if (!lineProcessed) {
						if (isEndRecLine(lineStr)) {
							 lineProcessed = true;
							 ODMLogger.getLogger().info("PSS/E Line record processed");
							 this.elemCntStr += "Line record " + lineCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSEV30LineDataRec.procLineString(lineStr, version, parser);
							lineCnt++;
						}	 
      				}
      				else if (!xfrProcessed) {
						if (isEndRecLine(lineStr)) {
							 xfrProcessed = true;
							 ODMLogger.getLogger().info("PSS/E Xfr record processed");
							 this.elemCntStr += "2W Xfr record " + xfrCnt +"\n";
							 this.elemCntStr += "3W Xfr record " + xfr3WCnt +"\n";
						}
						else {
      						String lineStr2 = din.readLine();
      						String lineStr3 = din.readLine();
      						String lineStr4 = din.readLine();
      						lineNo++; lineNo++; lineNo++;
      						String lineStr5 = "";
      						if (is3WXfr(lineStr)) {
          						lineStr5 = din.readLine();
          						lineNo++;
    							xfr3WCnt++;
      						}
      						else
    							xfrCnt++;
							
							if (!this.elemCntOnly)
								PSSEV30XfrDataRec.procLineString(lineStr, lineStr2, lineStr3, lineStr4, lineStr5, version, parser);
						}	 
      				}
      				else if (!areaInterProcessed) {
						if (isEndRecLine(lineStr)) {
							 areaInterProcessed = true;
							 ODMLogger.getLogger().info("PSS/E AreaInterchange record processed");
							 this.elemCntStr += "Area interchange record " + areaInterCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSENetDataRec.processAreaRec(lineStr, PsseVersion.PSSE_30, baseCaseNet, parser);
							areaInterCnt++;
						}	 
      				}
      				else if (!dcLine2TProcessed) {
						if (isEndRecLine(lineStr)) {
							 dcLine2TProcessed = true;
							 ODMLogger.getLogger().info("PSS/E DC line record processed");
							 this.elemCntStr += "2T DC line record " + dcLineCnt +"\n";
						}
						else {
      						String lineStr2 = din.readLine();
      						String lineStr3 = din.readLine();
      						lineNo++; lineNo++;
							if (!this.elemCntOnly)
								PSSEV30DcLine2TDataRec.procLineString(lineStr, lineStr2, lineStr3, version, parser);
							dcLineCnt++;
						}	 
      				}
      				else if (!vscDcLineProcessed) {
						if (isEndRecLine(lineStr)) {
							vscDcLineProcessed = true;
							ODMLogger.getLogger().info("PSS/E vscDcLine record processed");
							 this.elemCntStr += "vscDcLine record " + vscDcLineCnt +"\n";
						}
						else {
							//	PSSEVscDCLineDataRec rec = new PSSEVscDCLineDataRec(lineStr, version);
							//	rec.processVscDCLine(adjNet, msg);
							vscDcLineCnt++;
						}	 
      				}
      				else if (!switchedShuntProcessed) {
						if (isEndRecLine(lineStr)) {
							 switchedShuntProcessed = true;
							 ODMLogger.getLogger().info("PSS/E switched shunt record processed");
							 this.elemCntStr += "Switched Shunt record " + switchedShuntCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSEBusRecord.processSwitchedShuntData(lineStr, PsseVersion.PSSE_30, parser);
							switchedShuntCnt++;
						}	 
      				}
      				else if (!xfrZCorrectionProcessed) {
						if (isEndRecLine(lineStr)) {
							xfrZCorrectionProcessed = true;
							ODMLogger.getLogger().info("PSS/E Xfr table record processed");
							 this.elemCntStr += "Xfr table record " + xfrZTableCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSENetDataRec.processXfrZTableRec(lineStr, PsseVersion.PSSE_30, baseCaseNet, this.factory);
							xfrZTableCnt++;
						}	 
      				}
      				else if (!dcLineMTProcessed) {
						if (isEndRecLine(lineStr)) {
							dcLineMTProcessed = true;
							ODMLogger.getLogger().info("PSS/E multi terminal DC Line record processed");
							 this.elemCntStr += "MT DC line record " + mtDcLineCnt +"\n";
						}
						else {
							//	PSSEMultiTermDCLineDataRec rec = new PSSEMultiTermDCLineDataRec(lineStr, version);
							//	rec.processMultiTerminalDCLine(adjNet, msg);
							mtDcLineCnt++;
						}	 
      				}
      				else if (!multiSectionLineGroupProcessed) {
						if (isEndRecLine(lineStr)) {
							multiSectionLineGroupProcessed = true;
							ODMLogger.getLogger().info("PSS/E multi section Line Group record processed");
							 this.elemCntStr += "MultiSec record " + multiSecCnt +"\n";
						}
						else {
							//PSSEMultiSecLineDataRec rec = new PSSEMultiSecLineDataRec(lineStr, version);
							//rec.processMultiSecLine(adjNet, msg);
							multiSecCnt++;
						}	 
      				}
      				else if (!zoneProcessed) {
						if (isEndRecLine(lineStr)) {
							zoneProcessed = true;
							ODMLogger.getLogger().info("PSS/E Zone record processed");
							 this.elemCntStr += "Zone record " + zoneCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSENetDataRec.processZoneRec(lineStr, PsseVersion.PSSE_30, baseCaseNet, this.factory);
							//rec.processZone(adjNet, msg);
							zoneCnt++;
						}	 
      				}
      				else if (!interareaTransferProcessed) {
						if (isEndRecLine(lineStr)) {
							interareaTransferProcessed = true;
							ODMLogger.getLogger().info("PSS/E Interarea Transfer record processed");
							 this.elemCntStr += "Interarea transfer record " + interTransCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSENetDataRec.processInterareaTransferRec(lineStr, PsseVersion.PSSE_30, baseCaseNet, this.factory);
							interTransCnt++;
						}	 
      				}
      				else if (!ownerProcessed) {
						if (isEndRecLine(lineStr)) {
							ownerProcessed = true;
							ODMLogger.getLogger().info("PSS/E Owner record processed");
							 this.elemCntStr += "Owner record " + ownerCnt +"\n";
						}
						else {
							if (!this.elemCntOnly)
								PSSENetDataRec.processOwnerRec(lineStr, PsseVersion.PSSE_30, baseCaseNet, this.factory);
							ownerCnt++;
						}	 
      				}
      				else if (!factsProcessed) {
						if (isEndRecLine(lineStr)) {
							factsProcessed = true;
							ODMLogger.getLogger().info("PSS/E FACTS record processed");
							 this.elemCntStr += "Facts record " + factsCnt +"\n";
						}
						else { 
							//PSSEFACTSDataRec rec = new PSSEFACTSDataRec(lineStr, version);
							//rec.processFACTS(adjNet, msg);
							factsCnt++;
						}	 
      				}
      				
      			}
    		} while (lineStr != null);
  		} catch (Exception e) {
  			e.printStackTrace();
    		throw new Exception("PSSE data input error, line no " + lineNo + ", " + e.toString());
  		}
             
		if (!this.elemCntOnly)
			AclfParserHelper.createBusEquivData(parser);
  		
   	   	return parser;
	}
	
	protected IODMModelParser parseInputFile(IODMAdapter.NetType type, final IFileReader[] din, String encoding) throws Exception {
		throw new ODMException("not implemented yet");
	}
	
	/**
	 * PTI use 0 to indicate end of a data set, Bus Data for example. This function checks
	 * if the input line is the end of record line
	 *
	 * @param str a input data line string
	 */
	public static boolean isEndRecLine(String str) {
		String s = str.trim();
		return s.startsWith("0") || s.startsWith("/") || s.startsWith("Q");
	}	

	
	public static boolean is3WXfr(String str) {
		// for 2W xfr, line1, K = 0
  		StringTokenizer st = new StringTokenizer(str, ",");
		st.nextToken();
		st.nextToken();
		int K = new Integer(st.nextToken().trim()).intValue();
		return K != 0;
	}
}
