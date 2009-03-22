 /*
  * @(#)FileAdapter_IeeeCommonFormat.java   
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
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.custom.exchange.impl;

/**
 *  Custom input file adapter for IEEE Common Format. It loads a data file in the format and create an
 *  AclfAdjNetwork object. The data fields could be positional or separeted by comma  
 */


import java.util.StringTokenizer;

import org.apache.commons.math.complex.Complex;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclfadj.AclfAdjNetwork;
import com.interpss.core.aclfadj.FlowControlType;
import com.interpss.core.aclfadj.RemoteQControlType;
import com.interpss.core.aclfadj.XfrTapControlType;
import com.interpss.core.net.Area;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Zone;
import com.interpss.simu.dsl.IpssAclf;

public class IeeeCommonFormat_in {
	private static final int BusData = 1;
	private static final int BranchData = 2;
	private static final int LossZone = 3;
	private static final int InterchangeData = 4;
	private static final int TielineData = 5;

    public static AclfAdjNetwork loadFile(final java.io.BufferedReader din, final IPSSMsgHub msg) throws Exception {
    	IpssAclf.setMsgHub(msg);
    	
    	final AclfAdjNetwork  adjNet = CoreObjectFactory.createAclfAdjNetwork();
    	adjNet.setAllowParallelBranch(true);

    	String str = din.readLine();
    	IpssLogger.getLogger().fine(str);
        processNetData(str, adjNet);
    	
    	int dataType = 0;
    	do {
          	str = din.readLine();   //kvaBase
        	IpssLogger.getLogger().fine(str);
        	if (!str.trim().equals("END OF DATA")) {
    			try {
            		// process the data
            		if (str.startsWith("-999") || str.startsWith("-99") || str.startsWith("-9")) {
            			dataType = 0;
            		}
            		else if (dataType == BusData) {
            			processBusData(str, adjNet);
            		}
            		else if (dataType == BranchData) {
            		    processBranchData(str, adjNet, msg);
            		}
            		else if (dataType == LossZone) {
            		    processLossZoneData(str, adjNet, msg);
            		}
            		else if (dataType == InterchangeData) {
            		    processInterchangeData(str, adjNet, msg);
            		}
            		else if (dataType == TielineData) {
            		    processTielineData(str, adjNet, msg);
            		}
            		else if ((str.length() > 3) && str.substring(0,3).equals("BUS")) {
            			dataType = BusData;
                    	IpssLogger.getLogger().info("load bus data");
            		}
            		else if ((str.length() > 6) && str.substring(0,6).equals("BRANCH")) {
            			dataType = BranchData;
                    	IpssLogger.getLogger().info("load branch data");
            		}
            		else if ((str.length() > 4) && str.substring(0,4).equals("LOSS")) {
            			dataType = LossZone;
                    	IpssLogger.getLogger().info("load loss zone data");
            		}
            		else if ((str.length() > 11) && str.substring(0,11).equals("INTERCHANGE")) {
            			dataType = InterchangeData;
                    	IpssLogger.getLogger().info("load interchange data");
            		}
            		else if ((str.length() > 3) && str.substring(0,3).equals("TIE")) {
            			dataType = TielineData;
                    	IpssLogger.getLogger().info("load tieline data");
            		}
    			} catch (final Exception e) {
    				e.printStackTrace();
    				msg.sendErrorMsg(e.toString());
    				msg.sendErrorMsg("Error Line: " + str);
    			}
        	}
       	} while (!str.trim().equals("END OF DATA"));

    	return adjNet;
    }

    /*
     *   Network data
     *   ============ 
     */

    private static void processNetData(final String str, final AclfAdjNetwork  adjNet) {
    	// parse the input data line
    	final String[] strAry = getNetDataFields(str);
    	
        //[0] Columns  2- 9   Date, in format DD/MM/YY with leading zeros.  If no date provided, use 0b/0b/0b where b is blank.
    	final String date = strAry[0];
    	
        //[1] Columns 11-30   Originator's name [A]
    	final String orgName = strAry[1];
    	
        //[3] Columns 39-42   Year [I]
    	final String year = strAry[3];
    	
        //[4] Column  44      Season (S - Summer, W - Winter)
    	final String season = strAry[4];
    	
        //[5] Column  46-73   Case identification [A]
    	final String caseId = strAry[5];
    	IpssLogger.getLogger().fine("date, orgName, year, season, caseId: " + date + ", " 
    			+ orgName + ", " + year + ", " + season + ", " + caseId);

        //[2] Columns 32-37   MVA Base [F] *
    	final double baseMva = new Double(strAry[2]).doubleValue();  // in MVA
    	IpssLogger.getLogger().fine("BaseKva: " + baseMva ); 

    	// set the input data to the network container
    	adjNet.setId(caseId);
    	adjNet.setDesc("Aclf case created from an IEEE Common Format file.\n" + str);
    	adjNet.setBaseKva(baseMva*1000.0);
    }
    
    private static String[] getNetDataFields(final String str) {
    	final String[] strAry = new String[6];
    	
    	if (str.indexOf(',') >= 0) {
    		final StringTokenizer st = new StringTokenizer(str, ",");
    		int cnt = 0;
    		while(st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
    	}
    	else {
            //Columns  2- 9   Date, in format DD/MM/YY with leading zeros.  If no date provided, use 0b/0b/0b where b is blank.
        	strAry[0] = str.substring(1,9);
            //Columns 11-30   Originator's name [A]
        	strAry[1] = str.substring(10,30);
            //Columns 32-37   MVA Base [F] *
        	strAry[2] = str.substring(31,37);  // in MVA
        	//Columns 39-42   Year [I]
        	strAry[3] = str.substring(38,42);
            //Column  44      Season (S - Summer, W - Winter)
        	strAry[4] = str.substring(43,44);
            //Column  46-73   Case identification [A]
        	strAry[5] = str.substring(45);
    	}
    	return strAry;
    }

    /*
     *   Bus data
     *   ======== 
     */

    private static void processBusData(final String str, final AclfAdjNetwork  net) {
    	// parse the input data line
    	final String[] strAry = getBusDataFields(str);
    	
    	//Columns  1- 4   Bus number [I] *
    	final String busId = strAry[0];
    	IpssLogger.getLogger().fine("Bus data loaded, id: " + busId);
    	
    	//Columns  6-17   Name [A] (left justify) *
    	final String busName = strAry[1];
    	
    	//Columns 19-20   Load flow area number [I].  Don't use zero! *
    	//Columns 21-23   Loss zone number [I]
    	final int areaNo = new Integer(strAry[2]).intValue();
    	final int zoneNo = new Integer(strAry[3]).intValue();
    	
    	//Columns 77-83   Base kV [F]
    	double baseKv = new Double(strAry[11]).doubleValue();
    	if (baseKv == 0.0) {
			baseKv = 1.0;
		}
    	
    	//Columns 25-26   Type [I] *
        //		0 - Unregulated (load, PQ)
        //		1 - Hold MVAR generation within voltage limits, (gen, PQ)
        //		2 - Hold voltage within VAR limits (gen, PV)
        //		3 - Hold voltage and angle (swing, V-Theta; must always have one)
    	final int type = new Integer(strAry[4]).intValue();

    	//Columns 28-33   Final voltage, p.u. [F] *
    	//Columns 34-40   Final angle, degrees [F] *
    	final double vpu = new Double(strAry[5]).doubleValue();
    	final double angDeg = new Double(strAry[6]).doubleValue();

    	//Columns 41-49   Load MW [F] *
    	//Columns 50-59   Load MVAR [F] *
    	final double loadMw = new Double(strAry[7]).doubleValue();
    	final double loadMvar = new Double(strAry[8]).doubleValue();

    	//Columns 60-67   Generation MW [F] *
    	//Columns 68-75   Generation MVAR [F] *
    	final double genMw = new Double(strAry[9]).doubleValue();
    	final double genMvar = new Double(strAry[10]).doubleValue();

    	//Columns 107-114 Shunt conductance G (per unit) [F] *
    	//Columns 115-122 Shunt susceptance B (per unit) [F] *
    	final double gPU = new Double(strAry[15]).doubleValue();
    	final double bPU = new Double(strAry[16]).doubleValue();
    	

    	//Columns 85-90   Desired volts (pu) [F] (This is desired remote voltage if this bus is controlling another bus.)
    	final double vSpecPu = new Double(strAry[12]).doubleValue();

    	//Columns 91-98   Maximum MVAR or voltage limit [F]
    	//Columns 99-106  Minimum MVAR or voltage limit [F]
    	final double max = new Double(strAry[13]).doubleValue();
    	final double min = new Double(strAry[14]).doubleValue();

    	//Columns 124-127 Remote controlled bus number
    	final String reBusId = strAry[17];
    	
    	// create an AclfBus object
      	IpssAclf.AclfBusBaseDSL<AclfBus, AclfNetwork> busDSL = IpssAclf.addAclfBus(busId, busName, net)
      				.setAreaNumber(areaNo)
      				.setZoneNumber(zoneNo)
      				.setOwnerId("1")
      				.setBaseVoltage(baseKv, UnitType.kV)
      				.setInitVoltage(vpu, Math.toRadians(angDeg))
      				.setShuntY(new Complex(gPU,bPU), UnitType.PU);
      	
    	// add the bus object into the network container
    	//net.addBus(bus);

    	// set input data to the bus object
      	if ( type == 3 ) {
      		// Swing bus
      		busDSL.setGenCode(AclfGenCode.SWING)
      				.setVoltageSpec(vpu, UnitType.PU, angDeg, UnitType.Deg)
      				.setLoadCode(AclfLoadCode.CONST_P)
  					.setLoad(new Complex(loadMw, loadMvar), UnitType.mVA);
    	}
    	else if ( type == 1 ) {
    		// PQ bus
    		busDSL.setGenCode(AclfGenCode.GEN_PQ)
    				.setGen(new Complex(genMw, genMvar), UnitType.mVA)
    				.setLoadCode(AclfLoadCode.CONST_P)
    				.setLoad(new Complex(loadMw, loadMvar), UnitType.mVA);
    		
    		if ((max != 0.0) || (min != 0.0)) {
    			IpssLogger.getLogger().fine("Bus is a PQLimitBus, id: " + busId);
    		  	IpssAclf.addPQBusLimit(busId, net)
    		  				.setQSpecified(genMvar, UnitType.mVA)
    		  				.setVLimit(max, min, UnitType.PU);
    		}
    	}
    	else if ( type == 2 ) {
    		// PV or remote Q bus
   		 	busDSL.setGenCode(AclfGenCode.GEN_PV)
   		 		.setGenP_VMag(genMw, UnitType.mW, vpu, UnitType.PU)
   		 		.setLoadCode(AclfLoadCode.CONST_P)
   		 		.setLoad(new Complex(loadMw, loadMvar), UnitType.mVA);
   		 	
  			if ((max != 0.0) || (min != 0.0)) {
  				if (reBusId.equals("0") || reBusId.equals("") || reBusId.equals(busId)) {
  					// PV Bus limit control
  					IpssLogger.getLogger().fine("Bus is a PVLimitBus, id: " + busId);
  	    		  	IpssAclf.addPVBusLimit(busId, net)
  	    		  				.setVSpecified(vpu, UnitType.mVA)
  	    		  				.setQLimit(max, min, UnitType.mVar);
  				}
  				else {
  					// Remote Q  Bus control
  					IpssLogger.getLogger().fine("Bus is a RemoteQBus, id: " + busId);
  					IpssAclf.addRemoteQBus(busId, net)
  								.setControlType(RemoteQControlType.BUS_VOLTAGE)
  								.setAdjBusBranchId(reBusId)
  								.setQLimit(max, min, UnitType.mVar)
  								.setVSpecified(vSpecPu, UnitType.PU);
  				}
  			}
    	}
    	else if ( (loadMw != 0.0) || (loadMvar != 0.0)  ) {
    		// Non-gen load bus
   		 	busDSL.setLoadCode(AclfLoadCode.CONST_P)
   		 			.setLoad(new Complex(loadMw, loadMvar), UnitType.mVA);
    	}
    	else {
    		// Non-gen and non-load bus
    	}
    }

    private static String[] getBusDataFields(final String str) {
    	final String[] strAry = new String[18];
    	
    	if (str.indexOf(',') >= 0) {
    		final StringTokenizer st = new StringTokenizer(str, ",");
    		int cnt = 0;
    		while(st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
    	}
    	else {
        	//Columns  1- 4   Bus number [I] *
        	strAry[0] = str.substring(0,4).trim();
        	
        	//Columns  6-17   Name [A] (left justify) *
        	strAry[1] = str.substring(5, 17).trim();
        	
        	//Columns 19-20   Load flow area number [I].  Don't use zero! *
        	//Columns 21-23   Loss zone number [I]
        	strAry[2] = str.substring(18, 20).trim();
        	strAry[3] = str.substring(20, 23).trim();
        	
        	//Columns 77-83   Base kV [F]
        	strAry[11] = str.substring(76,83);
        	
        	//Columns 25-26   Type [I] *
            //		0 - Unregulated (load, PQ)
            //		1 - Hold MVAR generation within voltage limits, (gen, PQ)
            //		2 - Hold voltage within VAR limits (gen, PV)
            //		3 - Hold voltage and angle (swing, V-Theta; must always have one)
        	strAry[4] = str.substring(24,26).trim();

        	//Columns 28-33   Final voltage, p.u. [F] *
        	//Columns 34-40   Final angle, degrees [F] *
        	strAry[5] = str.substring(27,33);
        	strAry[6] = str.substring(33,40);

        	//Columns 41-49   Load MW [F] *
        	//Columns 50-59   Load MVAR [F] *
        	strAry[7] = str.substring(40,49);
        	strAry[8] = str.substring(49,59);

        	//Columns 60-67   Generation MW [F] *
        	//Columns 68-75   Generation MVAR [F] *
        	strAry[9] = str.substring(59,67);
        	strAry[10] = str.substring(67,75);

        	//Columns 107-114 Shunt conductance G (per unit) [F] *
        	//Columns 115-122 Shunt susceptance B (per unit) [F] *
        	strAry[15] = str.substring(106,114);
        	strAry[16] = str.substring(114,122);
        	

        	//Columns 85-90   Desired volts (pu) [F] (This is desired remote voltage if this bus is controlling another bus.)
        	strAry[12] = str.substring(84,90);

        	//Columns 91-98   Minimum MVAR or voltage limit [F]
        	//Columns 99-106  Maximum MVAR or voltage limit [F]
        	strAry[13] = str.substring(90,98);
        	strAry[14] = str.substring(98,106);

        	//Columns 124-127 Remote controlled bus number
        	strAry[17] = str.substring(123,127).trim();
    	}
    	return strAry;
    }
    
    /*
     *   Branch data
     *   =========== 
     */

    private static void processBranchData(final String str, final AclfAdjNetwork net, final IPSSMsgHub msg) {
    	// parse the input data line
    	final String[] strAry = getBranchDataFields(str);

//    	Columns  1- 4   Tap bus number [I] *
//      	For transformers or phase shifters, the side of the model the non-unity tap is on.
//		Columns  6- 9   Z bus number [I] *
//      	For transformers and phase shifters, the side of the model the device impedance is on.
    	final String fid = strAry[0];
    	final String tid = strAry[1];
    	IpssLogger.getLogger().fine("Branch data loaded, from-id, to-id: " + fid + ", " + tid);

//    	Columns 11-12   Load flow area [I]
//    	Columns 13-15   Loss zone [I]
//    	Column  17      Circuit [I] * (Use 1 for single lines)
    	final int areaNo = new Integer(strAry[2]).intValue();
    	final int zoneNo = new Integer(strAry[3]).intValue();
    	final int cirNo  = new Integer(strAry[4]).intValue();
    	
//    	Column  19      Type [I] *
//      0 - Transmission line
//      1 - Fixed tap
//      2 - Variable tap for voltage control (TCUL, LTC)
//      3 - Variable tap (turns ratio) for MVAR control
//      4 - Variable phase angle for MW control (phase shifter)
    	final int type = new Integer(strAry[5]).intValue();
    	
//    	Columns 20-29   Branch resistance R, per unit [F] *
//    	Columns 30-40   Branch reactance X, per unit [F] * No zero impedance lines
//    	Columns 41-50   Line charging B, per unit [F] * (total line charging, +B), Xfr B is negative
    	final double rpu = new Double(strAry[6]).doubleValue();
    	final double xpu = new Double(strAry[7]).doubleValue();
    	final double bpu = new Double(strAry[8]).doubleValue();

//    	Columns 77-82   Transformer final turns ratio [F]
//    	Columns 84-90   Transformer (phase shifter) final angle [F]
    	final double ratio = new Double(strAry[14]).doubleValue();
    	final double angle = new Double(strAry[15]).doubleValue();
    	
//    	Columns 51-55   Line MVA rating No 1 [I] Left justify!
//    	Columns 57-61   Line MVA rating No 2 [I] Left justify!
//    	Columns 63-67   Line MVA rating No 3 [I] Left justify!
    	// InterPSS currently is not using these three fields
    	
    	/*
    	final double rating1Mvar = new Integer(strAry[9]).intValue();
    	final double rating2Mvar = new Integer(strAry[10]).intValue();
    	final double rating3Mvar = new Integer(strAry[11]).intValue();
        */
    	
    	String controlBusId = "";
    	int controlSide = 0;
    	double stepSize = 0.0, maxTapAng = 0.0, minTapAng = 0.0, maxVoltPQ = 0.0, minVoltPQ = 0.0;
    	if (type > 1) {
//    		Columns 69-72   Control bus number
        	controlBusId = strAry[12];

//        	Column  74      Side [I]
//          	0 - Controlled bus is one of the terminals
//          	1 - Controlled bus is near the tap side
//          	2 - Controlled bus is near the impedance side (Z bus)
        	controlSide = new Integer(strAry[13]).intValue();

//        	Columns 106-111 Step size [F]
        	stepSize = new Double(strAry[18]).doubleValue();
        	
//        	Columns 91-97   Minimum tap or phase shift [F]
//        	Columns 98-104  Maximum tap or phase shift [F]
        	minTapAng = new Double(strAry[16]).doubleValue();
        	maxTapAng = new Double(strAry[17]).doubleValue();

//        	Columns 113-119 Minimum voltage, MVAR or MW limit [F]
//        	Columns 120-126 Maximum voltage, MVAR or MW limit [F]
        	maxVoltPQ = new Double(strAry[19]).doubleValue();
        	minVoltPQ = new Double(strAry[20]).doubleValue();
    	}

    	// create an AclfBranch object
      	if (type == 0) {
      		// A line branch
      		IpssAclf.addAclfBranch(fid, tid, new Integer(cirNo).toString(), net)
						.setAreaNumber(areaNo)
						.setZoneNumber(zoneNo)
      					.setBranchCode(AclfBranchCode.LINE)
      					.setZ(new Complex(rpu,xpu), UnitType.PU)
      					.setShuntB(bpu, UnitType.PU);
      	}
      	else if (type >= 1) {
          	AclfBranch aclfBranch =	IpssAclf.addAclfBranch(fid, tid, new Integer(cirNo).toString(), net)
          					.setAreaNumber(areaNo)
          					.setZoneNumber(zoneNo)
          					.setBranchCode(AclfBranchCode.XFORMER)
          					.setZ(new Complex(rpu,xpu), UnitType.PU)
          					.setTurnRatio(ratio, 1.0, UnitType.PU)
          					.getAclfBranch(); 
        	if (bpu < 0.0) {
        		IpssLogger.getLogger().fine("Xfr B: " + bpu);
        		IpssAclf.objWrapper(aclfBranch, net)
        					.getAclfBranch()
        					.getFromAclfBus()
        						.setShuntY(new Complex(0.0, -bpu));
        	}
        	if (angle != 0.0) {
        		// PhaseShifting transformer branch
        		IpssAclf.objWrapper(aclfBranch, net)
        	 				.setBranchCode(AclfBranchCode.PS_XFORMER)
        	 				.setShiftAngle(angle, 0.0, UnitType.Deg);
        	}
        	
          	if (type == 2) {
//                2 - Variable tap for voltage control (TCUL, LTC)
          		// TODO: volt spec is not defined
          		IpssAclf.addTapControl(fid, tid, new Integer(cirNo).toString(), net)
							.setControlType(XfrTapControlType.BUS_VOLTAGE)
							.setAdjBusBranchId(controlBusId)
							.setFlowControlType(FlowControlType.RANGE_CONTROL)
							.setVSpecified(1.0, UnitType.PU)
							.setTurnRatioLimit(maxTapAng, minTapAng)
							.setTapStepSize(stepSize)
							.setVcBusOnFromSide(getSide(controlSide, controlBusId, aclfBranch))
							.setTapOnFromSide(true);
          	}
          	else if (type == 3) {
//              3 - Variable tap (turns ratio) for MVAR control
          		// TODO: volt spec is not defined
          		IpssAclf.addTapControl(fid, tid, new Integer(cirNo).toString(), net)
							.setControlType(XfrTapControlType.MVAR_FLOW)
							.setFlowControlType(FlowControlType.RANGE_CONTROL)
							.setMvarSpecified(1.0, UnitType.PU)
							.setTurnRatioLimit(maxVoltPQ, minVoltPQ)
							.setTapStepSize(stepSize)
							.setTapOnFromSide(getSide(controlSide, controlBusId, aclfBranch))
							.setFlowFrom2To(true)
							.setMeteredOnFromSide(true);
          	}
          	else if (type == 4) {
//              4 - Variable phase angle for MW control (phase shifter)
          		// TODO: volt spec is not defined
        		IpssAclf.addPSXfrPControl(fid, tid, new Integer(cirNo).toString(), net)
							.setFlowControlType(FlowControlType.RANGE_CONTROL)
							.setPSpecified(0.2, UnitType.PU)
							.setAngLimit(maxTapAng, minTapAng, UnitType.Deg)
							.setFlowFrom2To(true)
							.setControlOnFromSide(true);          		
          	}
      	}
    }
    
    private static String[] getBranchDataFields(final String str) {
    	final String[] strAry = new String[21];
    	
    	if (str.indexOf(',') >= 0) {
    		final StringTokenizer st = new StringTokenizer(str, ",");
    		int cnt = 0;
    		while(st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
    	}
    	else {
//        	Columns  1- 4   Tap bus number [I] *
//      	For transformers or phase shifters, the side of the model the non-unity tap is on.
//			Columns  6- 9   Z bus number [I] *
//      	For transformers and phase shifters, the side of the model the device impedance is on.
    		strAry[0] = str.substring(0,4).trim();
    		strAry[1] = str.substring(5,9).trim();
    		IpssLogger.getLogger().fine("Branch data loaded, from-id, to-id: " + strAry[0] + ", " + strAry[1]);

//    		Columns 11-12   Load flow area [I]
//    		Columns 13-15   Loss zone [I]
//    		Column  17      Circuit [I] * (Use 1 for single lines)
    		strAry[2] = str.substring(10, 12).trim();
    		strAry[3] = str.substring(12, 15).trim();
    		strAry[4] = str.substring(16, 17).trim();
    	
//    		Column  19      Type [I] *
    		strAry[5] = str.substring(18,19).trim();
    	
//    		Columns 20-29   Branch resistance R, per unit [F] *
//    		Columns 30-40   Branch reactance X, per unit [F] * No zero impedance lines
//    		Columns 41-50   Line charging B, per unit [F] * (total line charging, +B)
    		strAry[6] = str.substring(19,29);
    		strAry[7] = str.substring(29,40);
    		strAry[8] = str.substring(40,50);

//    		Columns 77-82   Transformer final turns ratio [F]
//    		Columns 84-90   Transformer (phase shifter) final angle [F]
    		strAry[14] = str.substring(76,82);
    		strAry[15] = str.substring(83,90);
    	
//    		Columns 51-55   Line MVA rating No 1 [I] Left justify!
//    		Columns 57-61   Line MVA rating No 2 [I] Left justify!
//    		Columns 63-67   Line MVA rating No 3 [I] Left justify!
    		strAry[9] =  str.substring(50,55).trim();
    		strAry[10] = str.substring(56,61).trim();
    		strAry[11] = str.substring(62,67).trim();

    		if (new Integer(strAry[5]).intValue() > 1) {
//    			Columns 69-72   Control bus number
    			strAry[12] = str.substring(68,72).trim();

//        		Column  74      Side [I]
    			strAry[13] = str.substring(73,74).trim();

//        		Columns 106-111 Step size [F]
    			strAry[18] = str.substring(105,111);
        	
//        		Columns 91-97   Maximum tap or phase shift [F]
//        		Columns 98-104  Minimum tap or phase shift [F]
    			strAry[16] = str.substring(90,97);
    			strAry[17] = str.substring(97,104);

//        		Columns 113-119 Minimum voltage, MVAR or MW limit [F]
//        		Columns 120-126 Maximum voltage, MVAR or MW limit [F]
    			strAry[19] = str.substring(112,119);
    			strAry[20] = str.substring(119,126);
    		}
    	}
    	return strAry;
    }	
    	
    /*
     *   Loss Zone data
     *   ============== 
     */

    private static void processLossZoneData(final String str, final AclfAdjNetwork net, final IPSSMsgHub msg) {
    	final String[] strAry = getLossZoneDataFields(str);

//    	Columns  1- 3   Loss zone number [I] *
//    	Columns  5-16   Loss zone name [A] 
    	final int no = new Integer(strAry[0]).intValue();
    	final String name = strAry[1];
    	final Zone zone = CoreObjectFactory.createZone(no, net);
    	zone.setName(name);
    }

    private static String[] getLossZoneDataFields(final String str) {
    	final String[] strAry = new String[2];
    	
    	if (str.indexOf(',') >= 0) {
    		final StringTokenizer st = new StringTokenizer(str, ",");
    		int cnt = 0;
    		while(st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
    	}
    	else {
        	strAry[0] = str.substring(0,3).trim();
        	strAry[1] = str.substring(4).trim();    	
        }
    	return strAry;
    }	

    /*
     *   Interchange data
     *   ================ 
     */
    
    private static void processInterchangeData(final String str, final AclfAdjNetwork net, final IPSSMsgHub msg) {
    	final String[] strAry = getInterchangeDataFields(str);
    	
//    	Columns  1- 2   Area number [I], no zeros! *
      	final int no = new Integer(strAry[0]).intValue();
      	
//    	Columns  4- 7   Interchange slack bus number [I] *
//      Columns  9-20   Alternate swing bus name [A]
      	final String slackBusId = strAry[1];
      	final String alSwingBusName = strAry[2];
      	
//      Columns 21-28   Area interchange export, MW [F] (+ = out) *
//      Columns 30-35   Area interchange tolerance, MW [F] *
      	final double mw = new Double(strAry[3]).doubleValue();
      	final double err = new Double(strAry[4]).doubleValue();
      	
//      Columns 38-43   Area code (abbreviated name) [A] *
//      Columns 46-75   Area name [A]
      	final String code = strAry[5];
      	final String name = strAry[6];

      	final Area area = CoreObjectFactory.createArea(no, net);
      	area.setInterSwingBusId(slackBusId);
      	area.setAlterSwingBusName(alSwingBusName);
      	area.setInterExpPower(mw);
      	area.setInterErr(err);
    	area.setCode(code);
    	area.setName(name);
    }        
    
    private static String[] getInterchangeDataFields(final String str) {
    	final String[] strAry = new String[7];
    	
    	if (str.indexOf(',') >= 0) {
    		final StringTokenizer st = new StringTokenizer(str, ",");
    		int cnt = 0;
    		while(st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
    	}
    	else {
//        	Columns  1- 2   Area number [I], no zeros! *
          	strAry[0] = str.substring(0,2).trim();
          	
//        	Columns  4- 7   Interchange slack bus number [I] *
//          Columns  9-20   Alternate swing bus name [A]
          	strAry[1] = str.substring(3,7).trim();
          	strAry[2] = str.substring(8,20).trim();
          	
//          Columns 21-28   Area interchange export, MW [F] (+ = out) *
//          Columns 30-35   Area interchange tolerance, MW [F] *
          	strAry[3] = str.substring(20,28);
          	strAry[4] = str.substring(29,35);
          	
//          Columns 38-43   Area code (abbreviated name) [A] *
//          Columns 46-75   Area name [A]
          	strAry[5] = str.substring(37,43);
          	strAry[6] = str.substring(45).trim();
        }
    	return strAry;
    }	
    
    /*
     *   Tieline data
     *   ============ 
     */

    private static void processTielineData(final String str, final AclfAdjNetwork net, final IPSSMsgHub msg) {
    	final String[] strAry = getTielineDataFields(str);
    	
//    	Columns  1- 4   Metered bus number [I] *
//    	Columns  7-8    Metered area number [I] *
      	final String meteredBusId = strAry[0];
      	final int meteredAreaNo = new Integer(strAry[1]).intValue();
      	
//      Columns  11-14  Non-metered bus number [I] *
//      Columns  17-18  Non-metered area number [I] *
      	final String nonMeteredBusId = strAry[2];
      	final int nonMeteredAreaNo = new Integer(strAry[3]).intValue();
      	
//      Column   21     Circuit number
      	final int cirNo = new Integer(strAry[4]).intValue();
      	
      	net.addTieLine(meteredBusId, meteredAreaNo, nonMeteredBusId, nonMeteredAreaNo, new Integer(cirNo).toString());
    }
        
    private static String[] getTielineDataFields(final String str) {
    	final String[] strAry = new String[5];
    	
    	if (str.indexOf(',') >= 0) {
    		final StringTokenizer st = new StringTokenizer(str, ",");
    		int cnt = 0;
    		while(st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
    	}
    	else {
//        	Columns  1- 4   Metered bus number [I] *
//        	Columns  7-8    Metered area number [I] *
          	strAry[0] = str.substring(0,4).trim();
          	strAry[1] = str.substring(6,8).trim();
          	
//          Columns  11-14  Non-metered bus number [I] *
//          Columns  17-18  Non-metered area number [I] *
          	strAry[2] = str.substring(10,14).trim();
          	strAry[3] = str.substring(16,18).trim();
          	
//          Column   21     Circuit number
          	strAry[4] = str.substring(20,21);
    	}
    	return strAry;
    }	
    
    private static boolean getSide(final int controlSide, final String controlBusId, final Branch bra) {
		boolean onFromSide = true;
		if (controlSide == 0) {
			onFromSide = controlBusId.equals(bra.getFromBus().getId())? true : false;
		}
		else if (controlSide == 1) {
			onFromSide = true;
		}
		else {
			onFromSide = false;
		} 
		return onFromSide;    
	}
}