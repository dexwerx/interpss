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

package org.ieee.odm.adapter.ucte;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelContansts;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AdjustmentModeEnumType;
import org.ieee.odm.schema.AngleAdjustmentXmlType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.CurrentUnitType;
import org.ieee.odm.schema.FactorUnitType;
import org.ieee.odm.schema.InterchangeXmlType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NameValuePairListXmlType;
import org.ieee.odm.schema.ObjectFactory;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.TapAdjustBusLocationEnumType;
import org.ieee.odm.schema.TapAdjustmentEnumType;
import org.ieee.odm.schema.TapAdjustmentXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

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
	public UCTE_DEFAdapter() {
		super();
	}

	@Override
	protected AclfModelParser parseInputFile(
			final IFileReader din, String encoding) throws Exception {
		AclfModelParser parser = new AclfModelParser(encoding);
		parser.setLFTransInfo(OriginalDataFormatEnumType.UCTE_DEF);

		// BaseCase object, plus busRecList and BranchRecList are created 
		LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
		baseCaseNet.setId("Base_Case_from_UCTE_format");

    	// no base kva definition in UCTE format, so use 100 MVA
    	// UCTE data are in actual units, mw, mva ...
		baseCaseNet.setBasePower(BaseDataSetter.createPowerMvaValue(100.0));   

    	// scan all lines and process the data
    	customBaseVoltage = false;
      	String str;   
      	RecType recType = RecType.NotDefined;
      	int busCnt = 0;
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
    					baseCaseNet.setInterchangeList(parser.getFactory().createLoadflowNetXmlTypeInterchangeList());
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
    			    	    processNodeRecord(str, isoId, ++busCnt, parser);
    			    	}
    			    	else if (recType == RecType.Line) {
    			    	    processLineRecord(str, parser);
    			    	}
    			    	else if (recType == RecType.Xfr2W) {
    			    	    processXfr2WindingRecord(str, parser);
    			    	}
    			    	else if (recType == RecType.Xfr2WReg) {
    			    	    processXfr2WRegulationRecord(str, parser);
    			    	}
    			    	else if (recType == RecType.Xfr2WLookup) {
    			    	    processXfr2LookupRecord(str, baseCaseNet);
    			    	}
    			    	else if (recType == RecType.ExPower) {
    			    	    processExchangePowerRecord(str, baseCaseNet, parser.getFactory());
    			    	}
    				}
    			} catch (final Exception e) {
    				e.printStackTrace();
    				logErr(e.toString());
    			}
        	}
       	} while (str != null);

    	return parser;
    }

	protected IODMModelParser parseInputFile(IODMAdapter.NetType type, final IFileReader[] din, String encoding) throws Exception {
		throw new ODMException("not implemented yet");
	}
	
    /*
     * ##C section
     */
    private boolean processCommentRecord(String str, LoadflowNetXmlType xmlBaseNet) {
    	ODMLogger.getLogger().fine("Comment: " + str);
		// there is no need to do anything to the comment lines
    	return true;
    }

    /*
     * ##N and ##Z sections
     */
    private void processNodeRecord(String str, String isoId, int busCnt, AclfModelParser parser) throws Exception {
    	ODMLogger.getLogger().fine("Node Record: " + str);

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
			id = ModelStringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			name = ModelStringUtil.getString(str, 10, 21).trim();

			if (!customBaseVoltage)
				baseKv = getBaseVoltageKv(id);

			status = ModelStringUtil.getInt(str, 23, 23);  // 0 real, 1 equivalent
			nodeType = ModelStringUtil.getInt(str, 25, 25);  // 0 PQ, 1 QAng, 2 PV, 3 Swing
			voltage = ModelStringUtil.getDouble(str, 27, 32);  
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
			
			pLoadMW = ModelStringUtil.getDouble(str, 34, 40);  
			qLoadMvar = ModelStringUtil.getDouble(str, 42, 48);  
			pGenMW = -ModelStringUtil.getDouble(str, 50, 56);    // UCTE assumes out next as the positive direction
			qGenMvar = -ModelStringUtil.getDouble(str, 58, 64);
			
			// optional fields
			minGenMW = -ModelStringUtil.getDouble(str, 66, 72); 
			maxGenMW = -ModelStringUtil.getDouble(str, 74, 80);
			minGenMVar = -ModelStringUtil.getDouble(str, 82, 88); 
			maxGenMVar = -ModelStringUtil.getDouble(str, 90, 96); 
			staticPrimaryControl = ModelStringUtil.getDouble(str, 98, 102); 
			normalPowerPrimaryControl = ModelStringUtil.getDouble(str, 104, 110);
			scMVA3P = ModelStringUtil.getDouble(str, 112, 118);
			x_rRatio = ModelStringUtil.getDouble(str, 120, 126);

			powerPlanType = ModelStringUtil.getString(str, 128, 128);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}

		// create a bus record
		LoadflowBusXmlType aclfBus = parser.createAclfBus(id); 
      	aclfBus.setId(id);
      	aclfBus.setNumber((long)busCnt);
      	if (name != null && !name.trim().equals(""))
      		aclfBus.setName(name);
      	if (isoId != null && !isoId.trim().equals(""))
      		aclfBus.setIsoCode(isoId);
      	aclfBus.setBaseVoltage(BaseDataSetter.createVoltageValue(baseKv, VoltageUnitType.KV));
		
      	aclfBus.setVoltage(BaseDataSetter.createVoltageValue(voltage, VoltageUnitType.KV));

      	aclfBus.setAngle(BaseDataSetter.createAngleValue(0.0, AngleUnitType.DEG));    	
    	
		if (pLoadMW != 0.0 || qLoadMvar != 0.0) {
			AclfDataSetter.setLoadData(aclfBus,
					LFLoadCodeEnumType.CONST_P, pLoadMW,
					qLoadMvar, ApparentPowerUnitType.MVA);
		}

		switch (nodeType) {
		case 0: // PQ bus
			if (pGenMW != 0.0 || qGenMvar != 0.0) {
				AclfDataSetter.setGenData(aclfBus,
						LFGenCodeEnumType.PQ,
						1.0, VoltageUnitType.PU, 0.0, AngleUnitType.DEG,
						pGenMW, qGenMvar, ApparentPowerUnitType.MVA);				
			}
			break;
		case 1: // Q angle bus
			logErr("Node type = 1, not support currently. Please contact support@interpss.org");
			return;
		case 2: // PV bus
			AclfDataSetter.setGenData(aclfBus,
					LFGenCodeEnumType.PV, 
					voltage, VoltageUnitType.KV, 0.0, AngleUnitType.DEG,
					pGenMW, qGenMvar, ApparentPowerUnitType.MVA);
			if (((maxGenMVar != 0.0) || (minGenMVar != 0.0))
					&& maxGenMVar > minGenMVar) {
				// PV Bus limit control
				ODMLogger.getLogger().fine("Bus is a PVLimitBus, id: " + id);
				aclfBus.getGenData().getEquivGen().setQLimit(BaseDataSetter.createReactivePowerLimit(  
						maxGenMVar, minGenMVar, ReactivePowerUnitType.MVAR));
			}
			break;
		case 3: // swing bus
			AclfDataSetter.setGenData(aclfBus,
					LFGenCodeEnumType.SWING,
					voltage, VoltageUnitType.KV, 0.0, AngleUnitType.DEG,
					pGenMW, qGenMvar, ApparentPowerUnitType.MVA);
			break;
		default:
			// error bus nodeType code
			logErr("Wrong node type code, " + nodeType);
			return;
		}

		NameValuePairListXmlType nvList = parser.getFactory().createNameValuePairListXmlType();
		aclfBus.setNvPairList(nvList);
		if (status != 0)
			BaseJaxbHelper.addNVPair(nvList, Token_Status, new Integer(status).toString());
		if (minGenMW != 0.0)
			BaseJaxbHelper.addNVPair(nvList, Token_MinGenMW, new Double(minGenMW).toString());
		if (maxGenMW != 0.0)
			BaseJaxbHelper.addNVPair(nvList, Token_MaxGenMW, new Double(maxGenMW).toString());
		if (staticPrimaryControl != 0.0)
			BaseJaxbHelper.addNVPair(nvList, Token_SPControl, new Double(staticPrimaryControl).toString());
		if (normalPowerPrimaryControl != 0.0)
			BaseJaxbHelper.addNVPair(nvList, Token_NPPControl, new Double(normalPowerPrimaryControl).toString());
		if (scMVA3P != 0.0)
			BaseJaxbHelper.addNVPair(nvList, Token_SCMva3P, new Double(scMVA3P).toString());
		if (x_rRatio != 0.0)
			BaseJaxbHelper.addNVPair(nvList, Token_XRRatio, new Double(x_rRatio).toString());
		if (powerPlanType != null)
			BaseJaxbHelper.addNVPair(nvList, Token_PPlanType, powerPlanType);
    }
    
    /*
     * ##L section
     */
    private void processLineRecord(String str, AclfModelParser parser) throws Exception {
    	ODMLogger.getLogger().fine("Line Record: " + str);

		// parse the input line for line information
		String fromNodeId, toNodeId, orderCode, elemName;
		int status, currentLimit;
		double rOhm, xOhm, bMuS;  
		try {
			fromNodeId = ModelStringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			toNodeId = ModelStringUtil.getString(str, 10, 17).trim().replace(' ', '_');
			orderCode = ModelStringUtil.getString(str, 19, 19);

			status = ModelStringUtil.getInt(str, 21, 21);  // 0 real, i equivalent
			rOhm = ModelStringUtil.getDouble(str, 23, 28);  
			xOhm = ModelStringUtil.getDouble(str, 30, 35);  
			bMuS = ModelStringUtil.getDouble(str, 37, 44);  
			currentLimit = ModelStringUtil.getInt(str, 46, 51);  

			elemName = ModelStringUtil.getString(str, 53, 64);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}

    	// create a branch record
		LineBranchXmlType aclfLine = parser.createLineBranch(fromNodeId, toNodeId, orderCode);
      	if (elemName != null)
      		aclfLine.setName(elemName);
      	/*
      	aclfLine.setCircuitId(orderCode);
      	aclfLine.setFromBus(parser.createBusRef(fromNodeId));
      	aclfLine.setToBus(parser.createBusRef(toNodeId));    
		*/
		// LineData object created in the following call
		AclfDataSetter.setLineData(aclfLine, rOhm, xOhm,
				ZUnitType.OHM, 0.0, bMuS, YUnitType.MICROMHO);
      	
    	// by default the branch is active
    	if (status == 8 || status == 9) 
    		aclfLine.setOffLine(true);

    	aclfLine.setRatingLimit(parser.getFactory().createBranchRatingLimitXmlType());
    	AclfDataSetter.setBranchRatingLimitData(aclfLine.getRatingLimit(),
				currentLimit, CurrentUnitType.AMP);
    }
    
    /*
     * ##T section
     */
    private void processXfr2WindingRecord(String str, AclfModelParser parser)
                     throws Exception {
    	ODMLogger.getLogger().fine("Xfr 2W Record: " + str);

		// parse the input line for xformer information
		String fromNodeId, toNodeId, orderCode, elemName;
		int status, currentLimit;
		double fromRatedKV, toRatedKV, normialMva,
		       rOhm, xOhm, bMuS, gMuS;  
		try {
			fromNodeId = ModelStringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			toNodeId = ModelStringUtil.getString(str, 10, 17).trim().replace(' ', '_');
			orderCode = ModelStringUtil.getString(str, 19, 19);

			status = ModelStringUtil.getInt(str, 21, 21);  // 0 real, i equivalent
			fromRatedKV = ModelStringUtil.getDouble(str, 23, 27);  
			toRatedKV = ModelStringUtil.getDouble(str, 29, 33);  
			normialMva = ModelStringUtil.getDouble(str, 35, 39);  
			rOhm = ModelStringUtil.getDouble(str, 41, 46);  
			xOhm = ModelStringUtil.getDouble(str, 48, 53);  
			bMuS = ModelStringUtil.getDouble(str, 55, 62);  
			gMuS = ModelStringUtil.getDouble(str, 64, 69);  
			currentLimit = ModelStringUtil.getInt(str, 71, 76);  

			elemName = ModelStringUtil.getString(str, 78, 89);
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
		XfrBranchXmlType branchRec = parser.createXfrBranch(fromNodeId, toNodeId, orderCode);
      	if (elemName != null)
      		branchRec.setName(elemName);
      	/*
      	branchRec.setCircuitId(orderCode);
      	branchRec.setFromBus(parser.createBusRef(fromNodeId));
      	branchRec.setToBus(parser.createBusRef(toNodeId));    
		*/
		// r, x, g, b are measured at from side in Ohms
		// they are converted to PU using from bus base voltage
		if (fromRatedKV < toRatedKV) {
			// TODO: need to transfer R,X to high voltage side
			ODMLogger.getLogger().severe("Need more implementation");
		}
		// XformerData object created in the following call
		AclfDataSetter.createXformerData(branchRec,
				rOhm, xOhm, ZUnitType.OHM, 1.0, 1.0, gMuS, bMuS,
				YUnitType.MICROMHO);

		AclfDataSetter.setXfrRatingData(branchRec,
				fromRatedKV, toRatedKV, VoltageUnitType.KV,
				normialMva, ApparentPowerUnitType.MVA);

    	// by default the branch is active
    	if (status == 8 || status == 9) 
    		branchRec.setOffLine(true);
    	
    	branchRec.setRatingLimit(parser.getFactory().createBranchRatingLimitXmlType());
    	AclfDataSetter.setBranchRatingLimitData(branchRec.getRatingLimit(),
				currentLimit, CurrentUnitType.AMP);
    }
    
    /*
     * ##R section
     */
    private void processXfr2WRegulationRecord(String str, AclfModelParser parser) 
    				throws Exception {
    	ODMLogger.getLogger().fine("Xfr 2W Reg Record: " + str);

		String fromNodeId, toNodeId, orderCode, type;

		double dUPhase, uKvPhase; 
		int nPhase, n1Phase;  

		double dUAngle, thetaDegAngle, pMwAngle;  
		int nAngle, n1Angle;  

		try {
			fromNodeId = ModelStringUtil.getString(str, 1, 8).trim().replace(' ', '_');
			toNodeId = ModelStringUtil.getString(str, 10, 17).trim().replace(' ', '_');
			orderCode = ModelStringUtil.getString(str, 19, 19);

			dUPhase = ModelStringUtil.getDouble(str, 21, 25);  
			nPhase = ModelStringUtil.getInt(str, 27, 28);  
			n1Phase = ModelStringUtil.getInt(str, 30, 32);  
			uKvPhase = ModelStringUtil.getDouble(str, 34, 38);  

			dUAngle = ModelStringUtil.getDouble(str, 40, 44);  
			thetaDegAngle = ModelStringUtil.getDouble(str, 46, 50);  
			nAngle = ModelStringUtil.getInt(str, 52, 53);  
			n1Angle = ModelStringUtil.getInt(str, 55, 57);  
			pMwAngle = ModelStringUtil.getDouble(str, 59, 63);  

			type = ModelStringUtil.getString(str, 65, 68);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}
		
		if (dUPhase > 0.0 && dUAngle > 0.0) {
			logErr("Error: both phase regulation and angle regulation data are presented");
			return;
		}

		XfrBranchXmlType xfrBranch = parser.getXfrBranch(fromNodeId, toNodeId, orderCode); 
      	if (xfrBranch == null) {
      		logErr("Error: branch cannot be found, line: " + str);
      		return;
      	}

		NameValuePairListXmlType nvList = parser.getFactory().createNameValuePairListXmlType();
		xfrBranch.setNvPairList(nvList);

      	if (dUPhase > 0.0) {
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_dUPhase, new Double(dUPhase).toString());
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_nPhase, new Double(nPhase).toString());
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_n1Phase, new Double(n1Phase).toString());
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_uKvPhase, new Double(uKvPhase).toString());

			double ratioFactor = xfrBranch.getToTurnRatio().getValue();

			double x = 1.0 / (1.0 + n1Phase*dUPhase*0.01);
			// UCTE model at to side x : 1.0, InterPSS model 1.0:turnRatio
			xfrBranch.setToTurnRatio(BaseDataSetter.createTurnRatioPU(ratioFactor/x));
			
			if (uKvPhase > 0.0) {
				TapAdjustmentXmlType tapAdj = parser.getFactory().createTapAdjustmentXmlType();
				xfrBranch.setTapAdjustment(tapAdj);
				tapAdj.setAdjustmentType(TapAdjustmentEnumType.VOLTAGE);
				
				// tap control of voltage at to node side
				//     2 - Variable tap for voltage control (TCUL, LTC)
          		double maxTap = ratioFactor*(nPhase*dUPhase), 
          		       minTap = ratioFactor*(-nPhase*dUPhase);

          		tapAdj.setTapLimit(BaseDataSetter.createTapLimit(maxTap, minTap));
				tapAdj.getTapLimit().setUnit(FactorUnitType.PERCENT);
          		tapAdj.setTapAdjStepSize(dUPhase);
          		tapAdj.setTapAdjOnFromSide(false);
          		
          		TapAdjustmentXmlType.VoltageAdjData vAdjData = parser.getFactory().createTapAdjustmentXmlTypeVoltageAdjData();
          		tapAdj.setVoltageAdjData(vAdjData);
          		
          		vAdjData.setMode(AdjustmentModeEnumType.VALUE_ADJUSTMENT);
          		vAdjData.setDesiredValue(uKvPhase);				
          		vAdjData.setDesiredVoltageUnit(VoltageUnitType.KV);

          		vAdjData.setAdjVoltageBus(parser.createBusRef(toNodeId));
       			vAdjData.setAdjBusLocation(TapAdjustBusLocationEnumType.TO_BUS);
			}
		}
		else if (dUAngle > 0.0) {
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_dUAngle, new Double(dUAngle).toString());
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_thetaDegAngle, new Double(thetaDegAngle).toString());
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_nAngle, new Double(nAngle).toString());
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_n1Angle, new Double(n1Angle).toString());
			if (dUPhase != 0.0)
				BaseJaxbHelper.addNVPair(nvList, Token_pMwAngle, new Double(pMwAngle).toString());

			double ratioFactor = xfrBranch.getToTurnRatio().getValue();

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
					double theta = thetaDegAngle * ModelContansts.Deg2Rad,
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
			
			PSXfrBranchXmlType psXfrBranch = (PSXfrBranchXmlType)ModelStringUtil.casting(
					xfrBranch, "XfrBranchXmlType", "PSXfrBranchXmlType", parser.getEncoding());
			
			psXfrBranch.setToAngle(BaseDataSetter.createAngleValue(-ang*ModelContansts.Rad2Deg, AngleUnitType.DEG));
			psXfrBranch.setToTurnRatio(BaseDataSetter.createTurnRatioPU(ratioFactor/x));
			
			if (pMwAngle != 0.0) {
				AngleAdjustmentXmlType angAdj = parser.getFactory().createAngleAdjustmentXmlType();
				psXfrBranch.setAngleAdjustment(angAdj);
          		angAdj.setMode(AdjustmentModeEnumType.VALUE_ADJUSTMENT);
          		angAdj.setDesiredValue(pMwAngle);				
				angAdj.setDesiredPowerUnit(ActivePowerUnitType.MW);
				angAdj.setAngleLimit(BaseDataSetter.createAngleLimit(angMax, angMin, AngleUnitType.DEG));
				angAdj.setAngleAdjOnFromSide(false);
				// this part if not specified in the UCTE spec. We assume it is measured on to side
				angAdj.setDesiredMeasuredOnFromSide(false);
			}
		}
    }
    
    /*
     * ##TT section
     */
    private void processXfr2LookupRecord(String str, LoadflowNetXmlType xmlBaseNet) {
    	ODMLogger.getLogger().fine("Xfr 2W Desc Record: " + str);
    	ODMLogger.getLogger().severe("##TT not implemented yet. Contact support@interpss.org for more info");
		return;
    }
    
    /*
     * ##E section
     */
    private void processExchangePowerRecord(String str, LoadflowNetXmlType xmlBaseNet, ObjectFactory factory) {
    	ODMLogger.getLogger().info("Exchange Power Record: " + str);

		String fromIsoId, toIsoId, comment;
		double exPower;  

		try {
			fromIsoId = ModelStringUtil.getString(str, 1, 2);
			toIsoId = ModelStringUtil.getString(str, 4, 5);
			exPower = ModelStringUtil.getDouble(str, 7, 13);  
			comment = ModelStringUtil.getString(str, 15, 26);
		} catch (Exception e) {
			logErr(e.toString());
			return;
		}

		InterchangeXmlType interChange = factory.createInterchangeXmlType();
		xmlBaseNet.getInterchangeList().getInterchange().add(interChange);
		
		InterchangeXmlType.UcteExchange ucteExRec = factory.createInterchangeXmlTypeUcteExchange(); 
		interChange.setUcteExchange(ucteExRec);
		ucteExRec.setFromIsoId(fromIsoId);
		ucteExRec.setToIsoId(toIsoId);
		ucteExRec.setExchangePower(BaseDataSetter.createPowerValue(exPower, 0.0, ApparentPowerUnitType.MVA)); 
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
    	int code = ModelStringUtil.getInt(nodeId, 7, 7);
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