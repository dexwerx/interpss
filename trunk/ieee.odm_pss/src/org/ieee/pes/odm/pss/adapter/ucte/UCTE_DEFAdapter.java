/*
 * @(#)UCTE_DEFAdapter.java   
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
 * @Author Mike Zhou
 * @Version 1.0
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.pes.odm.pss.adapter.ucte;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.ieee.cmte.psace.oss.odm.pss.schema.v1.AdjustmentDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.AnalysisCategoryEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.AngleUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ApparentPowerUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BranchRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BusRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.CurrentUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowBusDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NameValuePairListXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NetworkCategoryEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PSSNetworkXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PhaseShiftXfrDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ReactivePowerUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.StudyCaseXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TapUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TransformerDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.VoltageUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.YUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ZUnitType;
import org.ieee.pes.odm.pss.adapter.AbstractODMAdapter;
import org.ieee.pes.odm.pss.model.IEEEODMPSSModelParser;
import org.ieee.pes.odm.pss.model.ODMData2XmlHelper;
import org.ieee.pes.odm.pss.model.StringUtil;

/*
	UCTE data exchange format for load flow and three phase short circuit studies (UCTE-DEF)
	Version 02 (coming into force: 2007.05.01)
*/

public class UCTE_DEFAdapter extends AbstractODMAdapter {
	public final static String Token_Status = "Status";
	public final static String Token_MinGenMW = "Min Gen MW";
	public final static String Token_MaxGenMW = "Max Gen MW";
	public final static String Token_SPControl = "Sstatic Primary Control";
	public final static String Token_NPPControl = "Normal Power Primary Control";
	public final static String Token_SCMva3P = "SC MVA 3P";
	public final static String Token_XRRatio = "X/R Ratio";
	public final static String Token_PPlanType = "PowerPlanType";
	
	public final static String Token_dUPhase = "dUPhase";
	public final static String Token_nPhase = "nPhase";
	public final static String Token_n1Phase = "n1Phase";
	public final static String Token_uKvPhase = "uKvPhase";
	public final static String Token_dUAngle = "dUAngle";
	public final static String Token_thetaDegAngle = "thetaDegAngle";
	public final static String Token_nAngle = "nAngle";
	public final static String Token_n1Angle = "n1Angle";
	public final static String Token_pMwAngle = "pMwAngle";
	
	private final static String PsXfrType_ASYM = "ASYM"; 
	private enum RecType {Comment, BaseVoltage, Node, Line, Xfr2W, Xfr2WReg, Xfr2WLookup, ExPower, NotDefined};

	/**
	 * default constructor
	 * 
	 * @param logger Logger object
	 */
	public UCTE_DEFAdapter(Logger logger) {
		super(logger);
	}

	protected IEEEODMPSSModelParser parseInputFile(
			final java.io.BufferedReader din) throws Exception {
		IEEEODMPSSModelParser parser = new IEEEODMPSSModelParser();

		StudyCaseXmlType.ContentInfo info = parser.getStudyCase().addNewContentInfo();
		info.setOriginalDataFormat(	StudyCaseXmlType.ContentInfo.OriginalDataFormat.UCTE_DEF);
		info.setAdapterProviderName("www.interpss.org");
		info.setAdapterProviderVersion("1.00");

		parser.getStudyCase().getBaseCase().setAnalysisCategory(
				AnalysisCategoryEnumType.LOADFLOW);
		parser.getStudyCase().getBaseCase().setNetworkCategory(
				NetworkCategoryEnumType.TRANSMISSION);

		// BaseCase object, plus busRecList and BranchRecList are created 
		PSSNetworkXmlType baseCaseNet = parser.getBaseCase();
		baseCaseNet.setId("Base_Case_from_UCTE_format");

    	// no base kva definition in UCTE format, so use 100 MVA
    	// UCTE data are in actual units, mw, mva ...
		ODMData2XmlHelper.setPowerMva(baseCaseNet.addNewBasePower(), 100.0);   

    	// scan all lines and process the data
    	customBaseVoltage = false;
      	String str;   
      	RecType recType = RecType.NotDefined;
    	do {
          	str = din.readLine();   
        	if (str != null && !str.trim().equals("")) {
        		String isoId = "";
    			try {
    				if (str.startsWith("##C"))
    					recType = RecType.Comment;
    				else if (str.startsWith("##BaseVoltage")) {
    					// this is an extension, not defined in the UCTE spec
    					recType = RecType.BaseVoltage;
    			    	customBaseVoltage = true;
    					customBaseVoltageList.clear();
    				}
    				else if (str.startsWith("##N"))
    					recType = RecType.Node;
    				else if (str.startsWith("##Z") && recType == RecType.Node) 
    					isoId = str.substring(3);
    				else if (str.startsWith("##L"))
    					recType = RecType.Line;
    				else if (str.startsWith("##T"))
    					recType = RecType.Xfr2W;
    				else if (str.startsWith("##R"))
    					recType = RecType.Xfr2WReg;
    				else if (str.startsWith("##TT"))
    					recType = RecType.Xfr2WLookup;
    				else if (str.startsWith("##E")) {
    					recType = RecType.ExPower;
    					baseCaseNet.addNewInterchangeList();
    				}
    				else {
    					// process data lines
    					if (recType == RecType.Comment) {
    			    	    processCommentRecord(str, baseCaseNet);
    			    	}
    					else if (recType == RecType.BaseVoltage) {
    			    	    processBaseVoltageRecord(str);
    			    	}
    			    	else if (recType == RecType.Node) {
    			    	    processNodeRecord(str, isoId, baseCaseNet);
    			    	}
    			    	else if (recType == RecType.Line) {
    			    	    processLineRecord(str, baseCaseNet);
    			    	}
    			    	else if (recType == RecType.Xfr2W) {
    			    	    processXfr2WindingRecord(str, baseCaseNet);
    			    	}
    			    	else if (recType == RecType.Xfr2WReg) {
    			    	    processXfr2WRegulationRecord(str, baseCaseNet);
    			    	}
    			    	else if (recType == RecType.Xfr2WLookup) {
    			    	    processXfr2LookupRecord(str, baseCaseNet);
    			    	}
    			    	else if (recType == RecType.ExPower) {
    			    	    processExchangePowerRecord(str, baseCaseNet);
    			    	}
    				}
    			} catch (final Exception e) {
    				logErr(e.toString());
    			}
        	}
       	} while (str != null);

    	return parser;
    }

    /*
     * ##C section
     */
    private boolean processCommentRecord(String str, PSSNetworkXmlType xmlBaseNet) {
    	getLogger().fine("Comment: " + str);
		// there is no need to do anything to the comment lines
    	return true;
    }

    /*
     * ##N and ##Z sections
     */
    private void processNodeRecord(String str, String isoId, PSSNetworkXmlType xmlBaseNet) {
    	getLogger().fine("Node Record: " + str);

		// parse the input line for node information
		String id, name;
		double baseKv = 0.0;
		int status, nodeType;
		double voltage, pLoadMW, qLoadMvar, pGenMW, qGenMvar;
		double 	minGenMW, maxGenMW, minGenMVar, maxGenMVar, 
				staticPrimaryControl, normalPowerPrimaryControl,
				scMVA3P, x_rRatio;
		String powerPlanType;
		try {
			id = StringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			name = StringUtil.getString(str, 10, 21).trim();

			if (!customBaseVoltage)
				baseKv = getBaseVoltageKv(id);

			status = StringUtil.getInt(str, 23, 23);  // 0 real, 1 equivalent
			nodeType = StringUtil.getInt(str, 25, 25);  // 0 PQ, 1 QAng, 2 PV, 3 Swing
			voltage = StringUtil.getDouble(str, 27, 32);  
			if (customBaseVoltage) {
				baseKv = findCustomBaseVoltage(voltage);
				if (baseKv == 0.0) {
					logErr("Custom base voltage lookup, cannot find proper value");
					return;
				}
			}
			else { 
				if (voltage == 0.0)
					voltage = baseKv;
			}
			
			pLoadMW = StringUtil.getDouble(str, 34, 40);  
			qLoadMvar = StringUtil.getDouble(str, 42, 48);  
			pGenMW = -StringUtil.getDouble(str, 50, 56);    // UCTE assumes out next as the positive direction
			qGenMvar = -StringUtil.getDouble(str, 58, 64);
			
			// optional fields
			minGenMW = -StringUtil.getDouble(str, 66, 72); 
			maxGenMW = -StringUtil.getDouble(str, 74, 80);
			minGenMVar = -StringUtil.getDouble(str, 82, 88); 
			maxGenMVar = -StringUtil.getDouble(str, 90, 96); 
			staticPrimaryControl = StringUtil.getDouble(str, 98, 102); 
			normalPowerPrimaryControl = StringUtil.getDouble(str, 104, 110);
			scMVA3P = StringUtil.getDouble(str, 112, 118);
			x_rRatio = StringUtil.getDouble(str, 120, 126);

			powerPlanType = StringUtil.getString(str, 128, 128);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}

		// create a bus record
		BusRecordXmlType busRec = xmlBaseNet.getBusList().addNewBus(); 
      	busRec.setId(id);
      	if (name != null && !name.trim().equals(""))
      		busRec.setName(name);
      	if (isoId != null && !isoId.trim().equals(""))
      		busRec.setIsoCode(isoId);
		ODMData2XmlHelper.setVoltageData(busRec.addNewBaseVoltage(), baseKv, VoltageUnitType.KV);
		
		LoadflowBusDataXmlType busData = busRec.addNewLoadflowData();
		ODMData2XmlHelper.setVoltageData(busData.addNewVoltage(), voltage,
				VoltageUnitType.KV);

		ODMData2XmlHelper.setAngleData(busData.addNewAngle(), 0.0,
				AngleUnitType.DEG);    	
    	
		if (pLoadMW != 0.0 || qLoadMvar != 0.0) {
			ODMData2XmlHelper.setLoadData(busData,
					LoadflowBusDataXmlType.LoadData.Code.CONST_P, pLoadMW,
					qLoadMvar, ApparentPowerUnitType.MVA);
		}

		switch (nodeType) {
		case 0: // PQ bus
			if (pGenMW != 0.0 || qGenMvar != 0.0) {
				ODMData2XmlHelper.setGenData(busData,
						LoadflowBusDataXmlType.GenData.Code.PQ, pGenMW, qGenMvar,
						ApparentPowerUnitType.MVA);				
			}
			break;
		case 1: // Q angle bus
			logErr("Node type = 1, not support currently. Please contact support@interpss.org");
			return;
		case 2: // PV bus
			ODMData2XmlHelper.setGenData(busData,
					LoadflowBusDataXmlType.GenData.Code.PV, pGenMW, qGenMvar,
					ApparentPowerUnitType.MVA);
			if (((maxGenMVar != 0.0) || (minGenMVar != 0.0))
					&& maxGenMVar > minGenMVar) {
				// PV Bus limit control
				getLogger().fine("Bus is a PVLimitBus, id: " + id);
				ODMData2XmlHelper.setGenQLimitData(busData.getGenData(),  
						maxGenMVar, minGenMVar, ReactivePowerUnitType.MVAR);
			}
			break;
		case 3: // swing bus
			ODMData2XmlHelper.setGenData(busData,
					LoadflowBusDataXmlType.GenData.Code.SWING, pGenMW, qGenMvar,
					ApparentPowerUnitType.MVA);
			break;
		default:
			// error bus nodeType code
			logErr("Wrong node type code, " + nodeType);
			return;
		}

		NameValuePairListXmlType nvList = busRec.addNewNvPairList();
		if (status != 0)
			ODMData2XmlHelper.addNVPair(nvList, Token_Status, new Integer(status).toString());
		if (minGenMW != 0.0)
			ODMData2XmlHelper.addNVPair(nvList, Token_MinGenMW, new Double(minGenMW).toString());
		if (maxGenMW != 0.0)
			ODMData2XmlHelper.addNVPair(nvList, Token_MaxGenMW, new Double(maxGenMW).toString());
		if (staticPrimaryControl != 0.0)
			ODMData2XmlHelper.addNVPair(nvList, Token_SPControl, new Double(staticPrimaryControl).toString());
		if (normalPowerPrimaryControl != 0.0)
			ODMData2XmlHelper.addNVPair(nvList, Token_NPPControl, new Double(normalPowerPrimaryControl).toString());
		if (scMVA3P != 0.0)
			ODMData2XmlHelper.addNVPair(nvList, Token_SCMva3P, new Double(scMVA3P).toString());
		if (x_rRatio != 0.0)
			ODMData2XmlHelper.addNVPair(nvList, Token_XRRatio, new Double(x_rRatio).toString());
		if (powerPlanType != null)
			ODMData2XmlHelper.addNVPair(nvList, Token_PPlanType, powerPlanType);
    }
    
    /*
     * ##L section
     */
    private void processLineRecord(String str, PSSNetworkXmlType xmlBaseNet) {
    	getLogger().fine("Line Record: " + str);

		// parse the input line for line information
		String fromNodeId, toNodeId, orderCode, elemName;
		int status, currentLimit;
		double rOhm, xOhm, bMuS;  
		try {
			fromNodeId = StringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			toNodeId = StringUtil.getString(str, 10, 17).trim().replace(' ', '_');
			orderCode = StringUtil.getString(str, 19, 19);

			status = StringUtil.getInt(str, 21, 21);  // 0 real, i equivalent
			rOhm = StringUtil.getDouble(str, 23, 28);  
			xOhm = StringUtil.getDouble(str, 30, 35);  
			bMuS = StringUtil.getDouble(str, 37, 44);  
			currentLimit = StringUtil.getInt(str, 46, 51);  

			elemName = StringUtil.getString(str, 53, 64);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}

    	// create a branch record
		BranchRecordXmlType branchRec = xmlBaseNet.getBranchList().addNewBranch();
      	if (elemName != null)
      		branchRec.setName(elemName);
      	branchRec.setCircuitId(orderCode);
      	branchRec.addNewFromBus().setIdRef(fromNodeId);
      	branchRec.addNewToBus().setIdRef(toNodeId);    
		
		branchRec.addNewLoadflowData();
		
		// LineData object created in the following call
		ODMData2XmlHelper.setLineData(branchRec.getLoadflowData(), rOhm, xOhm,
				ZUnitType.OHM, 0.0, bMuS, YUnitType.MICROMHO);
      	
    	// by default the branch is active
    	if (status == 8 || status == 9) 
    		branchRec.setOffLine(true);

		ODMData2XmlHelper.setBranchRatingLimitData(branchRec.getLoadflowData(),
				currentLimit, CurrentUnitType.AMP);
    }
    
    /*
     * ##T section
     */
    private void processXfr2WindingRecord(String str, PSSNetworkXmlType xmlBaseNet) {
    	getLogger().fine("Xfr 2W Record: " + str);

		// parse the input line for xformer information
		String fromNodeId, toNodeId, orderCode, elemName;
		int status, currentLimit;
		double fromRatedKV, toRatedKV, normialMva,
		       rOhm, xOhm, bMuS, gMuS;  
		try {
			fromNodeId = StringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			toNodeId = StringUtil.getString(str, 10, 17).trim().replace(' ', '_');
			orderCode = StringUtil.getString(str, 19, 19);

			status = StringUtil.getInt(str, 21, 21);  // 0 real, i equivalent
			fromRatedKV = StringUtil.getDouble(str, 23, 27);  
			toRatedKV = StringUtil.getDouble(str, 29, 33);  
			normialMva = StringUtil.getDouble(str, 35, 39);  
			rOhm = StringUtil.getDouble(str, 41, 46);  
			xOhm = StringUtil.getDouble(str, 48, 53);  
			bMuS = StringUtil.getDouble(str, 55, 62);  
			gMuS = StringUtil.getDouble(str, 64, 69);  
			currentLimit = StringUtil.getInt(str, 71, 76);  

			elemName = StringUtil.getString(str, 78, 89);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}

    	if (status == 0 || status == 8) {
    		if (rOhm < 0.0 || xOhm < 0.0) {
    			logErr("Error: transform r < 0 or x < 0, data line: " + str);
    			return;
    		}
    	}

    	// create a branch record
		BranchRecordXmlType branchRec = xmlBaseNet.getBranchList().addNewBranch();
      	if (elemName != null)
      		branchRec.setName(elemName);
      	branchRec.setCircuitId(orderCode);
      	branchRec.addNewFromBus().setIdRef(fromNodeId);
      	branchRec.addNewToBus().setIdRef(toNodeId);    

		branchRec.addNewLoadflowData();

		// r, x, g, b are measured at from side in Ohms
		// they are converted to PU using from bus base voltage
		if (fromRatedKV < toRatedKV) {
			// TODO: need to transfer R,X to high voltage side
			getLogger().severe("Need more implementation");
		}
		// XformerData object created in the following call
		ODMData2XmlHelper.createXformerData(branchRec.getLoadflowData(),
				rOhm, xOhm, ZUnitType.OHM, 1.0, 1.0, 0.0, 0.0, gMuS, bMuS,
				YUnitType.MICROMHO);

		ODMData2XmlHelper.setXfrRatingData(branchRec.getLoadflowData().getXformerData(),
				fromRatedKV, toRatedKV, VoltageUnitType.KV,
				normialMva, ApparentPowerUnitType.MVA);

		// turn ratio is defied at to side, 1.0 for un-regulated xfr, 
		ODMData2XmlHelper.setTapPU(branchRec.getLoadflowData().getXformerData().addNewToTap(), 1.0);
		
    	// by default the branch is active
    	if (status == 8 || status == 9) 
    		branchRec.setOffLine(true);
    	
		ODMData2XmlHelper.setBranchRatingLimitData(branchRec.getLoadflowData(),
				currentLimit, CurrentUnitType.AMP);
    }
    
    /*
     * ##R section
     */
    private void processXfr2WRegulationRecord(String str, PSSNetworkXmlType xmlBaseNet) {
    	getLogger().fine("Xfr 2W Reg Record: " + str);

		String fromNodeId, toNodeId, orderCode, type;

		double dUPhase, uKvPhase; 
		int nPhase, n1Phase;  

		double dUAngle, thetaDegAngle, pMwAngle;  
		int nAngle, n1Angle;  

		try {
			fromNodeId = StringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			toNodeId = StringUtil.getString(str, 10, 17).trim().replace(' ', '_');
			orderCode = StringUtil.getString(str, 19, 19);

			dUPhase = StringUtil.getDouble(str, 21, 25);  
			nPhase = StringUtil.getInt(str, 27, 28);  
			n1Phase = StringUtil.getInt(str, 30, 32);  
			uKvPhase = StringUtil.getDouble(str, 34, 38);  

			dUAngle = StringUtil.getDouble(str, 40, 44);  
			thetaDegAngle = StringUtil.getDouble(str, 46, 50);  
			nAngle = StringUtil.getInt(str, 52, 53);  
			n1Angle = StringUtil.getInt(str, 55, 57);  
			pMwAngle = StringUtil.getDouble(str, 59, 63);  

			type = StringUtil.getString(str, 65, 68);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}
		
		if (dUPhase > 0.0 && dUAngle > 0.0) {
			logErr("Error: both phase regulation and angle regulation data are presented");
			return;
		}

		BranchRecordXmlType branchRec = ODMData2XmlHelper.getBranchRecord(fromNodeId, toNodeId, orderCode, xmlBaseNet); 
      	if (branchRec == null) {
      		logErr("Error: branch cannot be found, line: " + str);
      		return;
      	}
      	
		NameValuePairListXmlType nvList = branchRec.addNewNvPairList();

      	if (dUPhase > 0.0) {
      		getLogger().fine("Phase regulation data persented");
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_dUPhase, new Double(dUPhase).toString());
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_nPhase, new Double(nPhase).toString());
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_n1Phase, new Double(n1Phase).toString());
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_uKvPhase, new Double(uKvPhase).toString());

			TransformerDataXmlType xfr = branchRec.getLoadflowData().getXformerData();
			double ratioFactor = xfr.getToTap().getValue();

			double x = 1.0 / (1.0 + n1Phase*dUPhase*0.01);
			// UCTE model at to side x : 1.0, InterPSS model 1.0:turnRatio
			ODMData2XmlHelper.setTapPU(xfr.addNewToTap(), ratioFactor/x);
			
			if (uKvPhase > 0.0) {
				TransformerDataXmlType.TapAdjustment tapAdj = xfr.addNewTapAdjustment();
				tapAdj.setAdjustmentType(TransformerDataXmlType.TapAdjustment.AdjustmentType.VOLTAGE);
				
				// tap control of voltage at to node side
				//     2 - Variable tap for voltage control (TCUL, LTC)
          		double maxTap = ratioFactor*(nPhase*dUPhase), 
          		       minTap = ratioFactor*(-nPhase*dUPhase);

				ODMData2XmlHelper.setTapLimitData(tapAdj.addNewTapLimit(), maxTap, minTap);
				tapAdj.getTapLimit().setUnit(TapUnitType.PERCENT);
          		tapAdj.setTapAdjStepSize(dUPhase);
          		tapAdj.setTapAdjOnFromSide(false);
          		
          		TransformerDataXmlType.TapAdjustment.VoltageAdjData vAdjData = tapAdj.addNewVoltageAdjData();
          		
          		vAdjData.setMode(AdjustmentDataXmlType.Mode.VALUE_ADJUSTMENT);
          		vAdjData.setDesiredValue(uKvPhase);				
          		vAdjData.setDesiredVoltageUnit(TransformerDataXmlType.TapAdjustment.VoltageAdjData.DesiredVoltageUnit.KV);
          		vAdjData.addNewAdjVoltageBus().setIdRef(toNodeId);
          		vAdjData.setAdjBusLocation(TransformerDataXmlType.TapAdjustment.VoltageAdjData.AdjBusLocation.TO_BUS);
			}
		}
		else if (dUAngle > 0.0) {
			getLogger().fine("angle regulation data persented");
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_dUAngle, new Double(dUAngle).toString());
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_thetaDegAngle, new Double(thetaDegAngle).toString());
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_nAngle, new Double(nAngle).toString());
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_n1Angle, new Double(n1Angle).toString());
			if (dUPhase != 0.0)
				ODMData2XmlHelper.addNVPair(nvList, Token_pMwAngle, new Double(pMwAngle).toString());

			double ratioFactor = branchRec.getLoadflowData().getXformerData().getToTap().getValue();

	    	double ang = 0.0, angMax = 0.0, angMin = 0.0, x = 1.0;
			double a    = n1Angle*dUAngle*0.01,
				   aMax = nAngle*dUAngle*0.01,
			       aMin = -nAngle*dUAngle*0.01;
			if (type.equals(PsXfrType_ASYM)) {
				if (thetaDegAngle == 90.0) {
					ang = Math.atan(a);
					angMax = Math.atan(aMax);
					angMin = Math.atan(aMin);
					x = 1.0 / Math.sqrt(1.0 + a*a);
				}
				else {
					double theta = thetaDegAngle * ODMData2XmlHelper.Deg2Rad,
					       asin = a*Math.sin(theta),
					       acos = 1.0 + a*Math.cos(theta),
					       asinMax = aMax*Math.sin(theta),
					       acosMax = 1.0 + aMax*Math.cos(theta),
					       asinMin = aMin*Math.sin(theta),
					       acosMin = 1.0 + aMin*Math.cos(theta);
					ang = Math.atan(asin/acos);
					angMax = Math.atan(asinMax/acosMax);
					angMin = Math.atan(asinMin/acosMin);
					x = 1.0 / Math.sqrt(asin*asin + acos*acos);
				}
			}
			else {  // default type is SYMM
				ang = 2.0 * Math.atan(a/2.0);
				angMax = 2.0 * Math.atan(aMax/2.0);
				angMin = 2.0 * Math.atan(aMin/2.0);
			}
			
			ODMData2XmlHelper.branchXfrData2PsXfr(branchRec.getLoadflowData());
			
			PhaseShiftXfrDataXmlType psXfr = branchRec.getLoadflowData().getPhaseShiftXfrData();
			ODMData2XmlHelper.setAngleData(psXfr.addNewToAngle(), -ang*ODMData2XmlHelper.Rad2Deg, AngleUnitType.DEG);
			ODMData2XmlHelper.setTapPU(psXfr.addNewToTap(), ratioFactor/x);
			
			if (pMwAngle != 0.0) {
				PhaseShiftXfrDataXmlType.AngleAdjustment angAdj = psXfr.addNewAngleAdjustment();
          		angAdj.setMode(AdjustmentDataXmlType.Mode.VALUE_ADJUSTMENT);
          		angAdj.setDesiredValue(pMwAngle);				
				angAdj.setDesiredPowerUnit(PhaseShiftXfrDataXmlType.AngleAdjustment.DesiredPowerUnit.MW);
				ODMData2XmlHelper.setAngleLimitData(angAdj.addNewAngleLimit(), angMax, angMin,
						AngleUnitType.DEG);
				angAdj.setAngleAdjOnFromSide(false);
				// this part if not specified in the UCTE spec. We assume it is measured on to side
				angAdj.setDesiredMeasuredOnFromSide(false);
			}
		}
    }
    
    /*
     * ##TT section
     */
    private void processXfr2LookupRecord(String str, PSSNetworkXmlType xmlBaseNet) {
    	getLogger().fine("Xfr 2W Desc Record: " + str);
    	getLogger().severe("##TT not implemented yet. Contact support@interpss.org for more info");
		return;
    }
    
    /*
     * ##E section
     */
    private void processExchangePowerRecord(String str, PSSNetworkXmlType xmlBaseNet) {
    	getLogger().info("Exchange Power Record: " + str);

		String fromIsoId, toIsoId, comment;
		double exPower;  

		try {
			fromIsoId = StringUtil.getString(str, 1, 2);
			toIsoId = StringUtil.getString(str, 4, 5);
			exPower = StringUtil.getDouble(str, 7, 13);  
			comment = StringUtil.getString(str, 15, 26);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}

		PSSNetworkXmlType.InterchangeList.Interchange.UcteExchange ucteExRec = xmlBaseNet.getInterchangeList().addNewInterchange().addNewUcteExchange();
		ucteExRec.setFromIsoId(fromIsoId);
		ucteExRec.setToIsoId(toIsoId);
		ODMData2XmlHelper.setPowerData(ucteExRec.addNewExchangePower(), exPower, 0.0, ApparentPowerUnitType.MVA); 
		if (comment != null)
			ucteExRec.setComment(comment);
    }

    /*
     * util functions and extensions 
     */

    // custom base voltage is an extension to the UCTE std
	private List<Double> customBaseVoltageList = new ArrayList<Double>();
	private boolean customBaseVoltage = false;

    
    private boolean processBaseVoltageRecord(String str) {
    	customBaseVoltageList.add(new Double(str));
    	return true;
    }

    private double getBaseVoltageKv(String nodeId) throws Exception {
        // According to the spec the node base voltage code is stored at the 7th char
    	int code = StringUtil.getInt(nodeId, 7, 7);
    	return getBaseVoltageKv(code);
    }
    
    private double getBaseVoltageKv(int code) {
    	switch(code) {
    	case 0 : return 750.0;
    	case 1 : return 380.0;
    	case 2 : return 220.0;
    	case 3 : return 150.0;
    	case 4 : return 120.0;
    	case 5 : return 110.0;
    	case 6 : return 70.0;
    	case 7 : return 27.0;
    	case 8 : return 330.0;
    	case 9 : return 500.0;
    	default: 
    		logErr("Wrong base voltage code, " + code);
    		return 0.0;
    	}
    }
    
	private double findCustomBaseVoltage(double voltage) {
		double baseKv = 0.0;
		double e = 1.0e10;
		for (Double bv : customBaseVoltageList) {
			if (Math.abs(voltage - bv.doubleValue()) < e) {
				baseKv = bv.doubleValue();
				e = Math.abs(voltage - bv.doubleValue());
			}
		}
		return baseKv;
	}
}