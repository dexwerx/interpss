

/*
 * @(#)BPABranchRecord.java   
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

import java.text.NumberFormat;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.BaseDataSetter;
import org.ieee.odm.model.BaseJaxbHelper;
import org.ieee.odm.model.ModelStringUtil;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.AdjustmentModeEnumType;
import org.ieee.odm.schema.AngleAdjustmentXmlType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.CurrentUnitType;
import org.ieee.odm.schema.LengthUnitType;
import org.ieee.odm.schema.LineBranchInfoXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.TapAdjustBusLocationEnumType;
import org.ieee.odm.schema.TapAdjustmentXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class BPABranchRecord {
	public static void processBranchData(final String str,	AclfModelParser parser,
			final LoadflowNetXmlType baseCaseNet) {		
		// symmetry line data
		if(str.startsWith("L")){
			final String[] strAry = getBranchDataFields(str);
			// symetry  branch
			final String branchType=strAry[0];

			final String modCode =strAry[1];
			final String owner=strAry[2];
			
			final String fid =  strAry[3];
			final String tid =  strAry[6];
			ODMLogger.getLogger().fine("Branch data loaded, from-Bus, to-Bus: " + fid + ", " + tid);

			// set cirId, if not specified, set to 1
			String cirId="";
			if(!strAry[8].equals("")){
				cirId = strAry[8];
			}
			else{
				cirId="1";
			}			
			
			LineBranchXmlType branchRec = null;
			try {
				branchRec = parser.createLineBranch(fid, tid, cirId);
			} catch (Exception e) {
				ODMLogger.getLogger().severe("branch data error, " + e.toString());
				return;
			}
			
			double fVol=0.0;
			double tVol=0.0;
			if(!strAry[4].equals("")){
				fVol= new Double(strAry[4]).doubleValue();
			}
			if(!strAry[7].equals("")){
				tVol= new Double(strAry[4]).doubleValue();
			}
			
//			// measure location for power interchange, 1--from side, 2- to side
//			//set transfer power measured location in tie line data 
//			int measureLocation=0;
//			if(!strAry[5].equals("")){
//				measureLocation= new Integer(strAry[5]).intValue();
//				try{
//					if(measureLocation==1){
//						PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();
//						// set tieline data
//						tieLine.addNewMeteredBus().setName(fid);
//						tieLine.addNewNonMeteredBus().setName(tid);	
//						
//						BusRecordXmlType busRecFrom=XBeanParserHelper.findBusRecord(fid, baseCaseNet);
//						busRecFrom.getZoneNumber();
//						NetAreaXmlType areaFrom=XBeanParserHelper.
//						  getAreaRecordByZone(busRecFrom.getZoneNumber(), baseCaseNet);
//						tieLine.setMeteredArea(areaFrom.getName());
//						
//						BusRecordXmlType busRecTo=XBeanParserHelper.findBusRecord(tid, baseCaseNet);
//						busRecTo.getZoneNumber();
//						NetAreaXmlType areaTo=XBeanParserHelper.
//						  getAreaRecordByZone(busRecTo.getZoneNumber(), baseCaseNet);
//						tieLine.setNonMeteredArea(areaTo.getName());
//						// to do: set area number
//						
//					}
//					else{
//						PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();
//
//						tieLine.addNewMeteredBus().setName(tid);
//						tieLine.addNewNonMeteredBus().setName(fid);					
//						XBeanParserHelper.findBusRecord(fid, baseCaseNet).getZoneNumber();
//						
//						BusRecordXmlType busRecFrom=XBeanParserHelper.findBusRecord(tid, baseCaseNet);
//						busRecFrom.getZoneNumber();
//						NetAreaXmlType areaFrom=XBeanParserHelper.
//						  getAreaRecordByZone(busRecFrom.getZoneNumber(), baseCaseNet);
//						tieLine.setMeteredArea(areaFrom.getName());
//						
//						BusRecordXmlType busRecTo=XBeanParserHelper.findBusRecord(fid, baseCaseNet);
//						busRecTo.getZoneNumber();
//						NetAreaXmlType areaTo=XBeanParserHelper.
//						  getAreaRecordByZone(busRecTo.getZoneNumber(), baseCaseNet);
//						tieLine.setNonMeteredArea(areaTo.getName());						
//					}
//				} catch (final Exception e){
//					e.printStackTrace();
//				}
//			}
			
			branchRec.setId(ModelStringUtil.formBranchId(fid, tid, cirId));			
			
			String multiSectionId="";
			if(!strAry[9].equals("")){
				multiSectionId = strAry[9];
				//set multiSection data if necessary
				// TODO
			}			
			
			//if currentRating!=0.0,set rated current
			double currentRating=0.0;
			if(!strAry[10].equals("")){
				currentRating = new Double(strAry[10]).doubleValue();
				AclfDataSetter.setBranchRatingLimitData(branchRec.getRatingLimit(),
						currentRating, CurrentUnitType.AMP);
				
			}	
			
			double rpu=0.0, xpu=0.0001, halfGpu=0.0, halfBpu=0.0;
			if(!strAry[12].equals("")){
				rpu = new Double(strAry[12]).doubleValue();
				if(rpu>10.0){
					rpu=rpu/100000;
				}
				rpu=ModelStringUtil.getNumberFormat(rpu);
			}
			
			if(!strAry[13].equals("")){
				xpu = new Double(strAry[13]).doubleValue();
				if(xpu>10.0){
					xpu=xpu/100000;
				}
				xpu=ModelStringUtil.getNumberFormat(xpu);
			}
			
			if(!strAry[14].equals("")){
				halfGpu = new Double(strAry[14]).doubleValue();
				if(halfGpu>10.0){
					halfGpu=halfGpu/100000;
				}
			}
			
			if(!strAry[15].equals("")){
				halfBpu = new Double(strAry[15]).doubleValue();
				if(halfBpu>10.0){
					halfBpu=halfBpu/100000;
				}
			}
			
			if(rpu!=0.0||xpu!=0.0||halfGpu!=0.0||halfBpu!=0.0){
				AclfDataSetter.setLineData(branchRec, rpu, xpu,
						ZUnitType.PU, 2*halfGpu, 2*halfBpu, YUnitType.PU);;
			}
   		    
			//branch length
			double length=0.0;
			if(!strAry[16].equals("")){
				LineBranchInfoXmlType lineInfo = branchRec.getLineInfo();
				lineInfo.getLength().setValue(length);
				lineInfo.getLength().setUnit(LengthUnitType.MILE);
			}			

			// if there is a description, set
			String desc= "";
			if(!strAry[17].equals("")){
				desc= strAry[17];
				BaseJaxbHelper.addNVPair(branchRec.getNvPairList(), "branch description", desc);
			}			
			
            
		}
//		else if(str.startsWith("E")){
//			final String[] strAry = getBranchDataFields(str,adapter);
//			// symetry  branch
//			final String branchType=strAry[0];
//
//			final String modCode =strAry[1];
//			final String owner=strAry[2];
//			
//			final String fid = strAry[3];
//			final String tid = strAry[6];
//			ODMLogger.getLogger().fine("Branch data loaded, from-Bus, to-Bus: " + fid + ", " + tid);
//			
//			if(!fid.equals("")){
//				branchRec.addNewFromBus().setIdRef(fid);
//			}
//			if(!tid.equals("")){
//				branchRec.addNewToBus().setIdRef(tid);
//			}
//			
//			double fVol=0.0;
//			double tVol=0.0;
//			if(!strAry[4].equals("")){
//				fVol= new Double(strAry[4]).doubleValue();
//			}
//			if(!strAry[7].equals("")){
//				tVol= new Double(strAry[4]).doubleValue();
//			}			
			// measure location for power interchange, 1--from side, 2- to side
			//set transfer power measured location in tie line data 
//			int measureLocation=0;
//			if(!strAry[5].equals("")){
//				measureLocation= new Integer(strAry[5]).intValue();
//				try{
//					if(measureLocation==1){
//						PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();
//
//						// set tieline data
//						tieLine.addNewMeteredBus().setName(fid);
//						tieLine.addNewNonMeteredBus().setName(tid);	
//						
//						BusRecordXmlType busRecFrom=XBeanParserHelper.findBusRecord(fid, baseCaseNet);
//						busRecFrom.getZoneNumber();
//						NetAreaXmlType areaFrom=XBeanParserHelper.
//						  getAreaRecordByZone(busRecFrom.getZoneNumber(), baseCaseNet);
//						tieLine.setMeteredArea(areaFrom.getName());
//						
//						BusRecordXmlType busRecTo=XBeanParserHelper.findBusRecord(tid, baseCaseNet);
//						busRecTo.getZoneNumber();
//						NetAreaXmlType areaTo=XBeanParserHelper.
//						  getAreaRecordByZone(busRecTo.getZoneNumber(), baseCaseNet);
//						tieLine.setNonMeteredArea(areaTo.getName());
//						// to do: set area number
//						
//					}else{
//						PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();
//
//						tieLine.addNewMeteredBus().setName(tid);
//						tieLine.addNewNonMeteredBus().setName(fid);					
//						XBeanParserHelper.findBusRecord(fid, baseCaseNet).getZoneNumber();
//						
//						BusRecordXmlType busRecFrom=XBeanParserHelper.findBusRecord(tid, baseCaseNet);
//						busRecFrom.getZoneNumber();
//						NetAreaXmlType areaFrom=XBeanParserHelper.
//						  getAreaRecordByZone(busRecFrom.getZoneNumber(), baseCaseNet);
//						tieLine.setMeteredArea(areaFrom.getName());
//						
//						BusRecordXmlType busRecTo=XBeanParserHelper.findBusRecord(fid, baseCaseNet);
//						busRecTo.getZoneNumber();
//						NetAreaXmlType areaTo=XBeanParserHelper.
//						  getAreaRecordByZone(busRecTo.getZoneNumber(), baseCaseNet);
//						tieLine.setNonMeteredArea(areaTo.getName());						
//					}
//				}catch (final Exception e){
//					e.printStackTrace();
//				}
//				
//			}
			
//			// set cirId, if not specified, set to 1
//			String cirId="";
//			if(!strAry[8].equals("")){
//				cirId = strAry[8];
//				branchRec.setCircuitId(cirId);
//			}
//			else{
//				branchRec.setCircuitId("1");
//			}			
//			
//			LoadflowBranchDataXmlType branchData = branchRec.addNewLoadflowData();
//			
//			branchRec.setId(ModelStringUtil.formBranchId(fid, tid, cirId));			
//			branchData.setCode(LFBranchCodeEnumType.LINE);
//			
//			String multiSectionId="";
//			if(!strAry[9].equals("")){
//				multiSectionId = strAry[9];
//				//set multiSection data if necessary
//			}			
//			
//			//if currentRating!=0.0,set rated current
//			double currentRating=0.0;
//			if(!strAry[10].equals("")){
//				currentRating = new Double(strAry[10]).doubleValue();
//				XBeanDataSetter.setBranchRatingLimitData(branchData.addNewBranchRatingLimit(), 
//						currentRating, CurrentUnitType.AMP);
//			}			 
//			double rpu=0.0, xpu=0.0001, G1pu=0.0, B1pu=0.0, G2pu=0.0, B2pu=0.0;
//			if(!strAry[12].equals("")){
//				rpu = new Double(strAry[12]).doubleValue();
//			}
//			if(!strAry[13].equals("")){
//				xpu = new Double(strAry[13]).doubleValue();
//			}
//			if(!strAry[14].equals("")){
//				G1pu = new Double(strAry[14]).doubleValue();
//			}
//			if(!strAry[15].equals("")){
//				B1pu = new Double(strAry[15]).doubleValue();
//			}
//			if(!strAry[16].equals("")){
//				G2pu = new Double(strAry[16]).doubleValue();
//			}
//			if(!strAry[17].equals("")){
//				B2pu = new Double(strAry[17]).doubleValue();
//			}
//			ZXmlType z= branchData.addNewZ();
//			XBeanDataSetter.setZValue(z, rpu, xpu, ZUnitType.PU);
//			YXmlType y1 = branchData.addNewFromShuntY();
//			YXmlType y2 = branchData.addNewToShuntY();
//			XBeanDataSetter.setYData(y1, G1pu, B1pu, YUnitType.PU);
//			XBeanDataSetter.setYData(y2, G2pu, B2pu, YUnitType.PU); 			
//		}
	}	
	
	public static void processXfrData(final String str, AclfModelParser parser,
			final LoadflowNetXmlType baseCaseNet){
		
		final int transformer=1;
		final int phaseShiftXfr=2;
		final int transformerAndPhaseShiftXfr=3;
		
		int dataType=0;	    	
		
		final String[] strAry = getXformerDataFields(str);			
			
		if(strAry[0].startsWith("T")){
			dataType=transformer;
		}
		else if(strAry[0].startsWith("TP")){
			dataType=phaseShiftXfr;
		}		
		
		final String modCode =strAry[1];
		final String owner=strAry[2];
			
		final String fid = strAry[3];
		final String tid = strAry[6];
		ODMLogger.getLogger().fine("Branch data loaded, from-bus, to-bus: " + fid + ", " + tid);
		
		String cirId="1";
		if(!strAry[8].equals("")){
			cirId = strAry[8];
		}
			
		XfrBranchXmlType branchRec = null;
		try {
			branchRec = dataType == transformer ?
								parser.createXfrBranch(fid, tid, cirId) : parser.createPSXfrBranch(fid, tid, cirId);
		} catch (Exception e) {
			ODMLogger.getLogger().severe("branch data error, " + e.toString());
		}
		
		branchRec.setId(ModelStringUtil.formBranchId(fid, tid, cirId));
		
		final double fVol= new Double(strAry[4]).doubleValue();
		final double tVol= new Double(strAry[7]).doubleValue();	
			
			
		//  set tieline data, measure location for power interchange, 1--from side, 2- to side
//		int measureLocation=0;
//		if(!strAry[5].equals("")){				
//			measureLocation= new Integer(strAry[5]).intValue();
//			try{
//				if(measureLocation==1){	
//					PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();
//
//					tieLine.addNewMeteredBus().setName(fid);
//					tieLine.addNewNonMeteredBus().setName(tid);	
//						
//					BusRecordXmlType busRecFrom=XBeanParserHelper.findBusRecord(fid, baseCaseNet);						
//					NetAreaXmlType areaFrom=XBeanParserHelper.
//						 getAreaRecordByZone(busRecFrom.getZoneNumber(), baseCaseNet);
//					tieLine.setMeteredArea(areaFrom.getName());
//						
//					BusRecordXmlType busRecTo=XBeanParserHelper.findBusRecord(tid, baseCaseNet);						
//					NetAreaXmlType areaTo=XBeanParserHelper.
//						 getAreaRecordByZone(busRecTo.getZoneNumber(), baseCaseNet);
//					tieLine.setNonMeteredArea(areaTo.getName());					
//				}else{
//					PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();
//
//					tieLine.addNewMeteredBus().setName(tid);
//					tieLine.addNewNonMeteredBus().setName(fid);					
//					XBeanParserHelper.findBusRecord(fid, baseCaseNet).getZoneNumber();
//						
//					BusRecordXmlType busRecFrom=XBeanParserHelper.findBusRecord(tid, baseCaseNet);
//					busRecFrom.getZoneNumber();
//					NetAreaXmlType areaFrom=XBeanParserHelper.
//						 getAreaRecordByZone(busRecFrom.getZoneNumber(), baseCaseNet);
//					tieLine.setMeteredArea(areaFrom.getName());
//						
//					BusRecordXmlType busRecTo=XBeanParserHelper.findBusRecord(fid, baseCaseNet);
//					busRecTo.getZoneNumber();
//					NetAreaXmlType areaTo=XBeanParserHelper.
//						 getAreaRecordByZone(busRecTo.getZoneNumber(), baseCaseNet);
//					tieLine.setNonMeteredArea(areaTo.getName());					
//				}					
//			}catch (final Exception e) {
//					e.printStackTrace();
//			}				
//		}
			
			
		final String multiSectionId = strAry[9];
		//set rated current
		double MwRating=0.0;
		if(!strAry[11].equals("")){
			MwRating = new Integer(strAry[11]).intValue();
		}
			
		// set xfr rating data
		AclfDataSetter.setXfrRatingData(branchRec, 
					fVol, tVol,VoltageUnitType.KV, MwRating, ApparentPowerUnitType.MVA);
			

		double rpu=0.0, xpu=0.0001, Gpu=0.0, Bpu=0.0;
		if(!strAry[12].equals("")){
			rpu = new Double(strAry[12]).doubleValue();
			if(rpu>10.0){
				rpu=rpu/100000;
			}
		}
		if(!strAry[13].equals("")){
			xpu = new Double(strAry[13]).doubleValue();
			if(xpu>10.0){
				xpu=xpu/100000;
			}
		}
		if(!strAry[14].equals("")){
			Gpu = new Double(strAry[14]).doubleValue();
			if(Gpu>10.0){
				Gpu=Gpu/100000;
			}
		}
		if(!strAry[15].equals("")){
			Bpu = new Double(strAry[15]).doubleValue();
			if(Bpu>10.0){
				Bpu=Bpu/100000;
			}
		}
		
		// set r x
		if(rpu!=0.0||xpu!=0.0){
			branchRec.setZ(BaseDataSetter.createZValue(rpu, xpu, ZUnitType.PU));
		}
		
		//set g b, g, b---> from side
		if(Gpu!=0.0||Bpu!=0.0){
			branchRec.setMagnitizingY(BaseDataSetter.createYValue(Gpu, Bpu, YUnitType.PU));
		}
		
		// tap1 tap2 or angle for phase shift
		double fromTurnRatedVolOrAngDeg=0.0, toTurnRatedVolOrZero=0.0;
		if(!strAry[16].equals("")){
			fromTurnRatedVolOrAngDeg = new Double(strAry[16]).doubleValue();				
		}
		if(!strAry[17].equals("")){
			toTurnRatedVolOrZero = new Double(strAry[17]).doubleValue();				 
		}
		double fRatio=1.0, tRatio=1.0;			
        
		// set transformer ratio and phaseshiftxfr angle
		if (dataType==transformer){					
			//to see what is the input data format, specified or not.
			if(fromTurnRatedVolOrAngDeg>=2*fVol){
				fromTurnRatedVolOrAngDeg=fromTurnRatedVolOrAngDeg/100;				
			}	
			fRatio=fromTurnRatedVolOrAngDeg/fVol;
			branchRec.setFromTurnRatio(BaseDataSetter.createTurnRatioPU(fRatio));
			
			if(toTurnRatedVolOrZero>=2*tVol){
				toTurnRatedVolOrZero=toTurnRatedVolOrZero/100;				
			}
			tRatio = toTurnRatedVolOrZero / tVol;
			NumberFormat ddf1 = NumberFormat.getNumberInstance();
			ddf1.setMaximumFractionDigits(4);
			tRatio = new Double(ddf1.format(tRatio)).doubleValue();		
			branchRec.setToTurnRatio(BaseDataSetter.createTurnRatioPU(tRatio));
		}
		else {			
			PSXfrBranchXmlType psXfrRec = (PSXfrBranchXmlType)branchRec;
			psXfrRec.setFromAngle(BaseDataSetter.createAngleValue(fromTurnRatedVolOrAngDeg, AngleUnitType.DEG));
			psXfrRec.setToAngle(BaseDataSetter.createAngleValue(0, AngleUnitType.DEG));						
		}			
	}			
	
	
	public static void processXfrAdjustData(final String str, AclfModelParser parser, LoadflowNetXmlType baseCaseNet){
		
		final String[] strAry = getXfrAdjustDataFields(str);
		
		int dataType=0;
		int angleAdjustment=1;
		int tapAdjustment=2;
		
		int adjustType=3;
		int tapVoltageAdjustment=4;
		int tapVarAdjustment=5;
		int pAngleAdjustment=6;
		int mAngleAdjustment=7;
		
		if(strAry[0].equals("RP")||strAry[0].equals("RM")){			
			dataType=angleAdjustment;
			if(strAry[0].equals("RP")){
				adjustType=pAngleAdjustment;
			}else{
				adjustType=mAngleAdjustment;
			}
		}
		else{
			dataType=tapAdjustment;
			if(strAry[0].equals("R")||strAry[0].equals("RV")){
				adjustType=tapVoltageAdjustment;
			}else {
				adjustType=tapVarAdjustment;
			}
		}			
		
		//adjustType: R or RV---remote bus control			
		final String modCode = strAry[1];
		final String owner = strAry[2];
		final String fromBus =strAry[3];
		final double fromTurnRatedV = new Double(strAry[4]).doubleValue();
		final String toBus = strAry[6];
		final double toTurnRatedV = new Double(strAry[7]).doubleValue();	
		String cirId = "unknow";
		
		XfrBranchXmlType branchRec = (XfrBranchXmlType)parser.getBranch(fromBus, toBus, cirId);	
		
		String controlBusId = "";		
		
		final String adjBus =strAry[8];
		final String adjVol =strAry[9];	
		// set tapAdjSide
		int tapAdjSide = 0;
		if(!strAry[5].equals("")){
			tapAdjSide=new Integer(strAry[5]).intValue();				
		}
		
		double   stepSize=0.0,maxVoltPQ = 0.0, minVoltPQ = 0.0, totalTap=0.0;
		double max=0.0, min=0.0;
		// Minimum  MVAR or MW limit [F]
		// Maximum  MVAR or MW limit [F]			
		
		
		if(!strAry[10].equals("")){
			max = new Double(strAry[10]).doubleValue();				
		}
		if(!strAry[11].equals("")){
			min = new Double(strAry[11]).doubleValue();				
		}
		if(!strAry[12].equals("")){
			totalTap = new Double(strAry[12]).doubleValue();
		}
		if(!strAry[13].equals("")){
			maxVoltPQ = new Double(strAry[13]).doubleValue();
		}
		if(!strAry[14].equals("")){
			minVoltPQ = new Double(strAry[14]).doubleValue();
		}			
		// calculate stepsize
		if (totalTap!=0.0){
			stepSize =(max-min)/totalTap;
		}
		
		// scheduled Q for RQ 			
		double scheduleQ=0.0;
		if(!strAry[13].equals("")){
			scheduleQ = new Double(strAry[13]).doubleValue();
		}	
		
		if(dataType==tapAdjustment){	
			TapAdjustmentXmlType tapAdj = parser.getFactory().createTapAdjustmentXmlType();
			branchRec.setTapAdjustment(tapAdj);
			
            if(tapAdjSide==1){
				tapAdj.setTapAdjOnFromSide(true);
				if(max>=2*fromTurnRatedV){
					max=max/100;
				}
				max=max/fromTurnRatedV;
				if(min>=2*fromTurnRatedV){
					min=min/100;
				}
				min=min/fromTurnRatedV;
			}
            else{
				tapAdj.setTapAdjOnFromSide(false);
				if(max>=2*toTurnRatedV){
					max=max/100;
				}
				max=max/toTurnRatedV;
				if(min>=2*toTurnRatedV){
					min=min/100;
				}
				min=min/toTurnRatedV;
			}
            // save result to two digits after .
            NumberFormat ddf1=NumberFormat.getNumberInstance() ;
            ddf1.setMaximumFractionDigits(2);
            max= new Double(ddf1.format(max)).doubleValue() ; 
            min= new Double(ddf1.format(min)).doubleValue() ; 
			
			if(tapAdjSide==2){
				tapAdj.setTapAdjOnFromSide(false);
				controlBusId=toBus;
			}else{
				tapAdj.setTapAdjOnFromSide(true);
				controlBusId=fromBus;
			}
			
			tapAdj.setTapLimit(BaseDataSetter.createTapLimit(max, min));
			tapAdj.setTapAdjStepSize(stepSize);
			if (adjustType==tapVoltageAdjustment ){// voltage control					
				TapAdjustmentXmlType.VoltageAdjData voltTapAdj = parser.getFactory().createTapAdjustmentXmlTypeVoltageAdjData();
				tapAdj.setVoltageAdjData(voltTapAdj);
				try {
					voltTapAdj.setAdjVoltageBus(parser.createBusRef(controlBusId));
				} catch (Exception e) {
					ODMLogger.getLogger().severe("Xfr control bus not defined properly, " + e.toString());
				}
					
				voltTapAdj.setAdjBusLocation(adjBus == toBus ? TapAdjustBusLocationEnumType.NEAR_TO_BUS
												: TapAdjustBusLocationEnumType.NEAR_FROM_BUS);
				voltTapAdj.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
				BaseDataSetter.setLimit(voltTapAdj, maxVoltPQ, minVoltPQ);				
			} 
			else if (adjustType==tapVarAdjustment) {// var control						
				TapAdjustmentXmlType.MvarFlowAdjData mvarTapAdj = parser.getFactory().createTapAdjustmentXmlTypeMvarFlowAdjData();
				tapAdj.setMvarFlowAdjData(mvarTapAdj);
				BaseDataSetter.setLimit(mvarTapAdj, maxVoltPQ, minVoltPQ);
				mvarTapAdj.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
				mvarTapAdj.setMvarMeasuredOnFormSide(true);				
			}
		} 
		else if(dataType==angleAdjustment){
			PSXfrBranchXmlType psXfrBranch = (PSXfrBranchXmlType)branchRec;
			AngleAdjustmentXmlType angAdj = parser.getFactory().createAngleAdjustmentXmlType();
			psXfrBranch.setAngleAdjustment(angAdj);
			angAdj.setAngleLimit(parser.getFactory().createAngleLimitXmlType());
			BaseDataSetter.setLimit(angAdj.getAngleLimit(), maxVoltPQ, minVoltPQ);
			BaseDataSetter.setLimit(angAdj, maxVoltPQ, minVoltPQ);
			angAdj.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
			angAdj.setDesiredMeasuredOnFromSide(true);			
		}
		
	}
/*	
	public static void processDCLineBranchData(final String str, 
			final DCLineBranchRecordXmlType dcBranch, 
			ODMModelParser parser,
			final PSSNetworkXmlType baseCaseNet,BPAAdapter adapter){
		final String strAry[] = getDCLineBranchDataFields(str,adapter);	
		
		final String dataType= strAry[0];
		final String modCode= strAry[1];
		final String owner = strAry[2];
		final String rectifierBus = strAry[3];
		double rectifierRatedVoltage = 0.0;
		if(!strAry[4].equals("")){
			rectifierRatedVoltage= new Double(strAry[4]).doubleValue();			
		}
		dcBranch.setRectifierBus(rectifierBus);
		
		
		final String inverterBus = strAry[6];
		double inverterRatedVoltage = 0.0;
		if(!strAry[7].equals("")){
			inverterRatedVoltage= new Double(strAry[7]).doubleValue();			
		}
		dcBranch.setInverterBus(inverterBus);
		
		int measureLocation=0;
		if(!strAry[5].equals("")){
			measureLocation = new Integer(strAry[5]).intValue();
			// add to tieline data
			try{
				if(measureLocation==1){
					// set tieline data
					PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();

					tieLine.addNewMeteredBus().setName(rectifierBus);
					tieLine.addNewNonMeteredBus().setName(inverterBus);	
					
					// **** DCLineBusRecordXmlType busRecFrom=ParserHelper.getDCLineBusRecord(rectifierBus, baseCaseNet);					
					/*NetAreaXmlType areaFrom=ContainerHelper.
						  getAreaRecordByZone(busRecFrom.getConverter().getData().getZoneNumber(), baseCaseNet);
					tieLine.setMeteredArea(areaFrom.getName());
					DCLineBusRecordXmlType busRecTo=ContainerHelper.getDCLineBusRecord(inverterBus, baseCaseNet);
										
					NetAreaXmlType areaTo=ContainerHelper.
						  getAreaRecordByZone(busRecTo.getConverter().getData().getZoneNumber(), baseCaseNet);
					tieLine.setNonMeteredArea(areaTo.getName());*/
					
					
					// to do: set area number
/*					
				}
			else{
					PSSNetworkXmlType.TieLineList.Tieline tieLine=parser.addNewBaseCaseTieline();

					tieLine.addNewMeteredBus().setName(inverterBus);
					tieLine.addNewNonMeteredBus().setName(rectifierBus);					
					ParserHelper.findBusRecord(rectifierBus, baseCaseNet).getZoneNumber();
					
					BusRecordXmlType busRecFrom=ParserHelper.findBusRecord(inverterBus, baseCaseNet);
					busRecFrom.getZoneNumber();
					NetAreaXmlType areaFrom=ParserHelper.
					  getAreaRecordByZone(busRecFrom.getZoneNumber(), baseCaseNet);
					tieLine.setMeteredArea(areaFrom.getName());
					
					BusRecordXmlType busRecTo=ParserHelper.findBusRecord(inverterBus, baseCaseNet);
					busRecTo.getZoneNumber();
					NetAreaXmlType areaTo=ParserHelper.
					  getAreaRecordByZone(busRecTo.getZoneNumber(), baseCaseNet);
					tieLine.setNonMeteredArea(areaTo.getName());						
				}
			}catch (final Exception e){
				e.printStackTrace();
			}
		}
		
		
		double lineRatingCurrent=0.0;
		if(!strAry[8].equals("")){
			lineRatingCurrent = new Double(strAry[8]).doubleValue();
			DataSetter.setCurrentData(dcBranch.getData().addNewMaxCurrent(),
					lineRatingCurrent, CurrentUnitType.AMP);
		}
		double r=0.0, l=0.0,c=0.0;
		double x=0;
		final double w= 2*3.14*0.02;
		if(!strAry[9].equals("")){
			r = new Double(strAry[9]).doubleValue();
		}
		if(!strAry[10].equals("")){
			l = new Double(strAry[10]).doubleValue();
		}
		if(!strAry[11].equals("")){
			c = new Double(strAry[11]).doubleValue();
		}
		// line reactance  x=XL-XC=2*pi*f*l-1/(2*Pi*f*c)
		if(x!=0.0&&c==0.0){
			x= w*l*0.001;
		}else if(c!=0.0){
			x= w*l*0.001-1/(w*c*0.000001);
		}		
		
		if(r!=0.0||x!=0.0){
			DataSetter.setZValue(dcBranch.getData().addNewLineZ(), r, x, ZUnitType.OHM);
		}
		String mwControlSide="";
		if(!strAry[12].equals("")){
			mwControlSide =strAry[12];
			if(mwControlSide.equals("R")){				
				dcBranch.getData().setControlOnRectifierSide(true);
			}else{				
				dcBranch.getData().setControlOnRectifierSide(false);
				}
		}		
		// DC power control mode, and control power
		double scheduledMw=0.0;
		if(!strAry[13].equals("")){
			scheduledMw =new Double(strAry[13]).doubleValue();
//			dcBranch.getData().setControlMode(DCLineDataXmlType.ControlMode.POWER);
			DataSetter.setPowerData(dcBranch.getData().addNewPowerDemand(), 
					scheduledMw, 0.0, ApparentPowerUnitType.MVA);
		}
		
	/*	double dcLineRatedVoltage=0.0;
		if(!strAry[14].equals("")){
			dcLineRatedVoltage = new Double(strAry[14]).doubleValue();
			DataSetter.setVoltageData(dcBranch.getData().addNewRatedDVol(),
					dcLineRatedVoltage, VoltageUnitType.KV);			
		}*/
		
		/* TODO - the following part has compiling error
		double recOperFiringAngle=0.0, invStopFiringAngle=0.0;
		if(!strAry[15].equals("")){
			recOperFiringAngle= new Double(strAry[15]).doubleValue();
			DataSetter.setAngleData(ContainerHelper.getConverterRecord(rectifierBus,
					baseCaseNet).getData().addNewRectifierMaxFiringAngle(), recOperFiringAngle, AngleUnitType.DEG);
			ContainerHelper.getConverterRecord(rectifierBus,baseCaseNet).
			        setType(ConverterXmlType.Type.RECTIFIER);
		}
		if(!strAry[16].equals("")){
			invStopFiringAngle= new Double(strAry[16]).doubleValue();
			DataSetter.setAngleData(ContainerHelper.getConverterRecord(inverterBus,
					baseCaseNet).getData().addNewInverterMinFiringAgnle(),invStopFiringAngle, AngleUnitType.DEG);
			ContainerHelper.getConverterRecord(rectifierBus,baseCaseNet).
	        setType(ConverterXmlType.Type.INVERTER);
		}
		*/
/*		
		double length=0.0;
		if(!strAry[17].equals("")){
			length= new Double(strAry[17]).doubleValue();
			dcBranch.getData().addNewLength().setValue(length);
			dcBranch.getData().getLength().setUnit(LengthUnitType.MILE);
		}
		
	}	
*/	
	private static String[] getBranchDataFields(final String str) {
		final String[] strAry = new String[20];
		strAry[0] = ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
        strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,3, 3).trim();
		strAry[2] = ModelStringUtil.getStringReturnEmptyString(str,4, 6).trim();		
		strAry[3] = ModelStringUtil.getStringReturnEmptyString(str,7, 14).trim();
		strAry[4] = ModelStringUtil.getStringReturnEmptyString(str,15, 18).trim();
		strAry[5] = ModelStringUtil.getStringReturnEmptyString(str,19, 19).trim();
		strAry[6] = ModelStringUtil.getStringReturnEmptyString(str,20, 27).trim();
		strAry[7] = ModelStringUtil.getStringReturnEmptyString(str,28, 31).trim();
		strAry[8] = ModelStringUtil.getStringReturnEmptyString(str,32, 32).trim();
		strAry[9] = ModelStringUtil.getStringReturnEmptyString(str,33, 33).trim();
		strAry[10] = ModelStringUtil.getStringReturnEmptyString(str,34, 37).trim();
		strAry[11] = ModelStringUtil.getStringReturnEmptyString(str,38, 38).trim();
		strAry[12] = ModelStringUtil.getStringReturnEmptyString(str,39, 44).trim();
		strAry[13] = ModelStringUtil.getStringReturnEmptyString(str,45, 50).trim();
		strAry[14] = ModelStringUtil.getStringReturnEmptyString(str,51, 56).trim();
		strAry[15] = ModelStringUtil.getStringReturnEmptyString(str,57, 62).trim();
		if(strAry[0]=="L"){
			strAry[16] = ModelStringUtil.getStringReturnEmptyString(str,63, 66).trim();
			strAry[17] = ModelStringUtil.getStringReturnEmptyString(str,67, 74).trim();
		}else{
			strAry[16] = ModelStringUtil.getStringReturnEmptyString(str,63, 67).trim();
			strAry[17] = ModelStringUtil.getStringReturnEmptyString(str,69, 74).trim();
		}		
		strAry[18] = ModelStringUtil.getStringReturnEmptyString(str,75, 77).trim();		
		strAry[19] = ModelStringUtil.getStringReturnEmptyString(str,78, 80).trim();		
            
		return strAry;
    }
	
	private static String[] getXformerDataFields(final String str) {
		final String[] strAry = new String[20];
		try{
			strAry[0] = ModelStringUtil.getStringReturnEmptyString(str,1, 2);
            strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,3, 3).trim();
			strAry[2] = ModelStringUtil.getStringReturnEmptyString(str,4, 6).trim();
			
			strAry[3] = ModelStringUtil.getStringReturnEmptyString(str,7, 14).trim();
			strAry[4] = ModelStringUtil.getStringReturnEmptyString(str,15, 18).trim();
			strAry[5] = ModelStringUtil.getStringReturnEmptyString(str,19, 19).trim();
			strAry[6] = ModelStringUtil.getStringReturnEmptyString(str,20, 27).trim();

			strAry[7] = ModelStringUtil.getStringReturnEmptyString(str,28, 31).trim();
			strAry[8] = ModelStringUtil.getStringReturnEmptyString(str,32, 32).trim();
			strAry[9] = ModelStringUtil.getStringReturnEmptyString(str,33, 33).trim();
			strAry[10] = ModelStringUtil.getStringReturnEmptyString(str,34, 37).trim();
			strAry[11] = ModelStringUtil.getStringReturnEmptyString(str,38, 38).trim();
			strAry[12] = ModelStringUtil.getStringReturnEmptyString(str,39, 44).trim();
			strAry[13] = ModelStringUtil.getStringReturnEmptyString(str,45, 50).trim();
			strAry[14] = ModelStringUtil.getStringReturnEmptyString(str,51, 56).trim();
			strAry[15] = ModelStringUtil.getStringReturnEmptyString(str,57, 62).trim();
			strAry[16] = ModelStringUtil.getStringReturnEmptyString(str,63, 67).trim();
			
			//strAry[17] ="";
			strAry[17] = str.substring(67, 72).trim();
			//strAry[18] = str.substring(74, 77).trim();
			
			//strAry[19] = str.substring(77, 80).trim();
		}catch(Exception e){
			ODMLogger.getLogger().severe(e.toString());
		}
		return strAry;
    }
	
	private static String[] getXfrAdjustDataFields(final String str) {
		final String[] strAry = new String[15];
		
		try{
			// type 		
            strAry[0] = ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
            strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,3, 3).trim();
			strAry[2] = ModelStringUtil.getStringReturnEmptyString(str,4, 6).trim();
			//from bus name
			strAry[3] = ModelStringUtil.getStringReturnEmptyString(str,7, 14).trim();
			// rated v
			strAry[4] = ModelStringUtil.getStringReturnEmptyString(str,15, 18).trim();
			strAry[5] = ModelStringUtil.getStringReturnEmptyString(str,19, 19).trim();
			//to bus name
			strAry[6] = ModelStringUtil.getStringReturnEmptyString(str,20, 27).trim();
			// to rated v
			strAry[7] = ModelStringUtil.getStringReturnEmptyString(str,28, 31).trim();
			// controlled bus name and rated v
			strAry[8] = ModelStringUtil.getStringReturnEmptyString(str,34, 41).trim();
			strAry[9] = ModelStringUtil.getStringReturnEmptyString(str,42, 45).trim();
			
			//for R RV RQ RN
			//max tap
			strAry[10] = ModelStringUtil.getStringReturnEmptyString(str,46, 50).trim();
			// min tap
			strAry[11] = ModelStringUtil.getStringReturnEmptyString(str,51, 55).trim();
			// total tap
			strAry[12] = ModelStringUtil.getStringReturnEmptyString(str,56, 57).trim();
			
			strAry[13] = ModelStringUtil.getStringReturnEmptyString(str,58, 62).trim();
			strAry[14] = ModelStringUtil.getStringReturnEmptyString(str,63, 67).trim();
		}catch(Exception e){
			ODMLogger.getLogger().severe(e.toString());
		}
		return strAry;
    }
	
	private static String[] getDCLineBranchDataFields(final String str) {
		final String[] strAry = new String[20];
		try{
			strAry[0] = ModelStringUtil.getStringReturnEmptyString(str,1, 2);
            strAry[1] = ModelStringUtil.getStringReturnEmptyString(str,3, 3).trim();
			strAry[2] = ModelStringUtil.getStringReturnEmptyString(str,4, 6).trim();
			
			strAry[3] = ModelStringUtil.getStringReturnEmptyString(str,7, 14).trim();
			strAry[4] = ModelStringUtil.getStringReturnEmptyString(str,15, 18).trim();
			strAry[5] = ModelStringUtil.getStringReturnEmptyString(str,19, 19).trim();
			strAry[6] = ModelStringUtil.getStringReturnEmptyString(str,20, 27).trim();

			strAry[7] = ModelStringUtil.getStringReturnEmptyString(str,28, 31).trim();
			strAry[8] = ModelStringUtil.getStringReturnEmptyString(str,34, 37).trim();
			strAry[9] = ModelStringUtil.getStringReturnEmptyString(str,38, 43).trim();
			strAry[10] = ModelStringUtil.getStringReturnEmptyString(str,44, 49).trim();
			strAry[11] = ModelStringUtil.getStringReturnEmptyString(str,50, 55).trim();
			strAry[12] = ModelStringUtil.getStringReturnEmptyString(str,56, 56).trim();
			strAry[13] = ModelStringUtil.getStringReturnEmptyString(str,57, 61).trim();
			strAry[14] = ModelStringUtil.getStringReturnEmptyString(str,62, 66).trim();
			strAry[15] = ModelStringUtil.getStringReturnEmptyString(str,67, 70).trim();
			strAry[16] = ModelStringUtil.getStringReturnEmptyString(str,71, 74).trim();
			
			strAry[17] = ModelStringUtil.getStringReturnEmptyString(str,75, 78).trim();
		}catch(Exception e){
			ODMLogger.getLogger().severe(e.toString());
		}
		return strAry;
    }
	
	
}

