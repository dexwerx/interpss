 /*
  * @(#)PSSEV30XfrDataRec.java   
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

package org.ieee.odm.adapter.dep.v07.psse.v30.impl;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.ieee.odm.adapter.dep.v07.psse.PsseVersion;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.BaseDataSetter;
import org.ieee.odm.model.BaseJaxbHelper;
import org.ieee.odm.model.ModelStringUtil;
import org.ieee.odm.model.dep.jaxb.JaxbDataSetter;
import org.ieee.odm.model.dep.jaxb.JaxbODMModelParser;
import org.ieee.odm.schema.AdjustmentModeEnumType;
import org.ieee.odm.schema.AngleAdjustmentXmlType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchMeterLocationEnumType;
import org.ieee.odm.schema.BranchRecordXmlType;
import org.ieee.odm.schema.LFBranchCodeEnumType;
import org.ieee.odm.schema.LoadflowBranchDataXmlType;
import org.ieee.odm.schema.TapAdjustmentEnumType;
import org.ieee.odm.schema.TapAdjustmentXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class PSSEV30XfrDataRec {
	private static int i, j, k, cw, cz, cm, stat, nmetr;
	private static String ckt, name;
	private static double mag1, mag2;
	private static int o1 = 0, o2 = 0, o3 = 0, o4 = 0;
	private static double f1 = 0.0, f2 = 0.0, f3 = 0.0, f4 = 0.0;
	private static double r1_2, x1_2, sbase1_2, r2_3, x2_3, sbase2_3, r3_1, x3_1, sbase3_1, vmstar, anstar;
	private static int cod, cont, ntp, tab;
	private static double windv1, nomv1, windv2, nomv2, windv3, nomv3;
	private static double rma, rmi, vma, vmi, cr, cx;
	private static double ang1, rata1, ratb1, ratc1, ang2, rata2, ratb2, ratc2, ang3, rata3, ratb3, ratc3;
	
	public static void procLineString(String lineStr1, String lineStr2, String lineStr3, String lineStr4, String lineStr5, 
							PsseVersion version, JaxbODMModelParser parser, Logger logger) {
		procLineString(lineStr1, lineStr2, lineStr3, lineStr4, lineStr5, version, logger);

		boolean is3W = k != 0; 
		boolean isPsXfr = false;
/*
	    Line-1 
	    For 2W and 3W Xfr: 
	    	I,     J,     K,    CKT, CW,CZ,CM, MAG1,     MAG2,    NMETR,’NAME’,        STAT,O1,F1,...,O4,F4
	        26,    54,    0,    '1 ',1, 1, 1,  0.00000,  0.00000, 2,    '        ',    1,   1,1.0000,   0,1.0000,   0,1.0000,   0,1.0000
            27824, 27871, 27957,'W ',2, 2, 1,  0.00089,  -0.00448,1,    'D575121     ',1,   1,1.0000

*/
		final String fid = AbstractModelParser.BusIdPreFix+i;
		final String tid = AbstractModelParser.BusIdPreFix+j;
		final String tertId = AbstractModelParser.BusIdPreFix+k;
		String branchId = is3W? ModelStringUtil.formBranchId(fid, tid, tertId, ckt) : ModelStringUtil.formBranchId(fid, tid, ckt);

		BranchRecordXmlType branchRec;
		try {
			branchRec = parser.createBranchRecord(branchId);
		} catch (Exception e) {
			logger.severe(e.toString());
			return;
		}		
		branchRec.setFromBus(parser.createBusRef(fid));
		branchRec.setToBus(parser.createBusRef(tid));	
		if (is3W)
			branchRec.setTertiaryBus(parser.createBusRef(tertId));
		branchRec.setCircuitId(ckt);
		
		branchRec.setName(name);
		branchRec.setOffLine(stat != 1);
		
      	LoadflowBranchDataXmlType branchData = parser.getFactory().createLoadflowBranchDataXmlType(); 
      	branchRec.getLoadflowData().add(branchData);	
		branchData.setCode(LFBranchCodeEnumType.TRANSFORMER);
		branchData.setXfr3W(is3W);
       	LoadflowBranchDataXmlType.XfrInfo xfrInfo = parser.getFactory().createLoadflowBranchDataXmlTypeXfrInfo(); 
       	branchData.setXfrInfo(xfrInfo);
       	
       	// rated voltage could be entered 0.0, Bus BaseVoltage should be used in this case
       	if (nomv1 == 0.0)
       		nomv1 = parser.getBusRecord(fid).getBaseVoltage().getValue();
       	if (nomv2 == 0.0)
       		nomv2 = parser.getBusRecord(tid).getBaseVoltage().getValue();
       	if (is3W && nomv3 == 0.0)
       		nomv3 = parser.getBusRecord(tertId).getBaseVoltage().getValue();
       	
		branchData.setMeterLocation( nmetr==1 ? BranchMeterLocationEnumType.FROM_SIDE :
							BranchMeterLocationEnumType.TO_SIDE);

		/*
		CM - The magnetizing admittance I/O code that defines the units in which MAG1 and MAG2 are specified: 
			1 for complex admittance in pu on system MVA base and winding one bus voltage base; 
			2 for no load loss in watts and exciting current in pu on winding one to two base MVA and winding one nominal voltage.
			CM = 1 by default.
		MAG1, MAG2 The magnetizing conductance and susceptance, respectively, in pu on system
			MVA base and winding one bus voltage base when CM is 1; 
			MAG1 is the no load loss in watts and MAG2 is the exciting current in pu on winding one to
			two base MVA (SBASE1-2) and winding one nominal voltage (NOMV1)when CM is 2. 
			MAG1 = 0.0 and MAG2 = 0.0 by default. 
			For three-phase transformers or three-phase banks of single phase transformers, the three-phase noload
			loss should be entered.
			When CM is 1 and a non-zero MAG2 is specified, MAG2 should be entered as a negative quantity; 
			when CM is 2 and a non-zero MAG2 is specified, MAG2 should always be entered as a positive quantity.
    	 */
	
		// sample data : 1,   0.00089,  -0.00448
    	if (cm == 2) {
    		//TODO
    		if (mag1 != 0.0 || mag2 != 0.0)
    			branchData.setFromShuntY(BaseDataSetter.createYValue(mag1, mag2, YUnitType.PU));
    	}
    	else {
    		if (mag1 != 0.0 || mag2 != 0.0)
    			branchData.setFromShuntY(BaseDataSetter.createYValue(mag1, mag2, YUnitType.PU));
    	}
      	
    	// owner id = 0.0, no contribution
    	BaseJaxbHelper.addOwner(branchRec, 
    			new Integer(o1).toString(), f1, 
    			new Integer(o2).toString(), o2==0?0.0:f2, 
    			new Integer(o3).toString(), o3==0?0.0:f3, 
    			new Integer(o4).toString(), o4==0?0.0:f4);
	
    	/*
       	Line-2 
       		format 2w: R1-2,X1-2,SBASE1-2
       		format 3w: R1-2,X1-2,SBASE1-2,R2-3,X2-3,SBASE2-3,R3-1,X3-1,SBASE3-1,VMSTAR,ANSTAR
       		
			VMSTAR The voltage magnitude at the hidden "star point" bus; entered in pu. VMSTAR = 1.0 by default.
			ANSTAR The bus voltage phase angle at the hidden "star point" bus; entered in degrees. ANSTAR = 0.0 by default.       		

    	*/
    	xfrInfo.setRatedPower12(BaseDataSetter.createPowerMvaValue(sbase1_2));
       	if (is3W) {
       		xfrInfo.setRatedPower23(BaseDataSetter.createPowerMvaValue(sbase2_3));
       		xfrInfo.setRatedPower31(BaseDataSetter.createPowerMvaValue(sbase3_1));
       		xfrInfo.setStarVMag(BaseDataSetter.createVoltageValue(vmstar, VoltageUnitType.PU));
       		xfrInfo.setStarVAng(BaseDataSetter.createAngleValue(anstar, AngleUnitType.DEG));
       	}
       	
       	if (cz == 1) {
       		// When CZ is 1, they are the resistance and reactance, respectively, in pu on 
       		// system base quantities; 
       		branchData.setZ(BaseDataSetter.createZValue(r1_2, x1_2, ZUnitType.PU));
        	xfrInfo.setDataOnSystemBase(true);
       	}
       	else if (cz == 2) {
       		// when CZ is 2, they are the resistance and reactance, respectively, in pu on 
       		// winding one to two base MVA (SBASE1-2) and winding one bus base voltage; 
       		branchData.setZ(BaseDataSetter.createZValue(r1_2, x1_2, ZUnitType.PU));
        	xfrInfo.setDataOnSystemBase(false);
       	}
       	else if (cz == 3) {
       		// when CZ is 3, R1-2 is the load loss in watts, and X1-2 is the impedance magnitude 
       		// in pu on winding one to two base MVA (SBASE1-2) and winding one bus base voltage.
       		double zpu = x1_2;
       		double rpu = r1_2 * 0.001 * 0.001 / sbase1_2;  
       		branchData.setZ(BaseDataSetter.createZValue(rpu, Math.sqrt(zpu*zpu - rpu*rpu), ZUnitType.PU));
        	xfrInfo.setDataOnSystemBase(true);
       	}
       	
       	if (is3W) {
           	if (cz == 1) {
           		branchData.getXfrInfo().setZ23(BaseDataSetter.createZValue(r2_3, x2_3, ZUnitType.PU));
           		branchData.getXfrInfo().setZ31(BaseDataSetter.createZValue(r3_1, x3_1, ZUnitType.PU));
           	}
           	else if (cz == 2) {
           		branchData.getXfrInfo().setZ23(BaseDataSetter.createZValue(r2_3, x2_3, ZUnitType.PU));
           		branchData.getXfrInfo().setZ31(BaseDataSetter.createZValue(r3_1, x3_1, ZUnitType.PU));
           	}
           	else if (cz == 3) {
           		double zpu = x2_3;
           		double rpu = r2_3  * 0.001 * 0.001 / sbase2_3;  
           		branchData.getXfrInfo().setZ23(BaseDataSetter.createZValue(rpu, Math.sqrt(zpu*zpu - rpu*rpu), ZUnitType.PU));
           		zpu = x3_1;
           		rpu = r3_1  * 0.001 * 0.001 / sbase3_1;  
           		branchData.getXfrInfo().setZ31(BaseDataSetter.createZValue(rpu, Math.sqrt(zpu*zpu - rpu*rpu), ZUnitType.PU));
           	}
       	}
		      	
    	/*
		 Line-3 
		 	format 2W and 3W: 
		 	 	WINDV1,  NOMV1,     ANG1,    RATA1,RATB1,RATC1,          COD,    CONT,RMA,     RMI,      VMA,     VMI,      NTP,TAB,CR,CX
            	352.001, 360.000,   0.000,   150.00,   150.00,   150.00, 0,      0,   540.0000,183.6000, 1.50000, 0.51000,  33, 0, 0.00000, 0.00000
		 	
    	 */
  		
  		if (cw == 1) {
       		// The winding one off-nominal turns ratio in pu of winding one bus base voltage
       		// when CW is 1; WINDV1 is 1.0 by default. 
        	xfrInfo.setDataOnSystemBase(true);
       	}
       	else if (cw == 2) {
       		// WINDV1 is the actual winding one voltage in kV when CW is 2; 
        	xfrInfo.setDataOnSystemBase(false);
       	}
  		
  		if (!xfrInfo.isDataOnSystemBase()) {
  			windv1 /= nomv1;
  			xfrInfo.setRatedVoltage1(BaseDataSetter.createVoltageValue(nomv1, VoltageUnitType.KV));
  		}
  		branchData.setFromTurnRatio(BaseDataSetter.createTapPU(windv1));
	
    	if ( (is3W && (ang1 != 0.0 || ang2 != 0.0 || ang3 != 0.0)) ||
    		 (!is3W && ang1 != 0.0) || 
    		  cod == 3 || cod == -3) {
    		// PhaseShifting transformer branch
    		isPsXfr = true;
			branchData.setCode(LFBranchCodeEnumType.PHASE_SHIFT_XFORMER);
			branchData.setFromAngle(BaseDataSetter.createAngleValue(ang1, AngleUnitType.DEG));
    	}
    	branchData.setBranchRatingLimit(parser.getFactory().createBranchRatingLimitXmlType());
    	JaxbDataSetter.setBranchRatingLimitData(branchData.getBranchRatingLimit(), rata1, ratb1, ratc1, ApparentPowerUnitType.MVA);
		
		/*
		 * The transformer control mode for automatic adjustments of the winding one
			tap or phase shift angle during power flow solutions: 0 for no control (fixed tap
			and phase shift); 
			±1 for voltage control; 
			±2 for reactive power flow control; 
			±3 for active power flow control; 
			±4 for control of a dc line quantity (+4 is valid only for two-winding transformers). 
			
			If the control mode is entered as a positive
			number, automatic adjustment of this transformer winding is enabled when the
			corresponding adjustment is activated during power flow solutions;
			 
			a negative control mode suppresses the automatic adjustment of this transformer winding.
			COD1 = 0 by default.

		If CONT is entered as a positive number, or a quoted extended bus name, the ratio is
			adjusted as if bus CONT is on the winding two or winding three side of the
			transformer; if CONT is entered as a negative number, or a quoted extended
			bus name with a minus sign preceding the first character, the ratio is adjusted
			as if bus |CONT| is on the winding one side of the transformer.
      	 */
	
      	boolean onFromSide = false;
      	if (cont < 0) {
      		cont = -cont;
      		onFromSide = true;
      	}
      	
      	String reBusId = AbstractModelParser.BusIdPreFix+cont;
      	
		// COD1,CONT1,RMA,RMI,VMA,VMI,NTP,TAB, 
		//Sample data : 1,    31, 1.10000, 0.90000, 1.09255, 1.04255, 33, 0, 0.00000, 0.00000
      	/*
        RMA1, RMI1 The upper and lower limits, respectively, of either:
				• Off-nominal turns ratio in pu of winding one bus base voltage when
					|COD1| is 1 or 2 and CW is 1; RMA1 = 1.1 and RMI1 = 0.9 by default.
				• Actual winding one voltage in kV when |COD1| is 1 or 2 and CW is 2. No
					default is allowed.
				• Phase shift angle in degrees when |COD1| is 3. No default is allowed.
				• Not used when |COD1| is 0 or 4; RMA1 = 1.1 and RMI1 = 0.9 by default.
		VMA1, VMI1 The upper and lower limits, respectively, of either:
				• Voltage at the controlled bus (bus |CONT1|) in pu when |COD1| is 1.
					VMA1 = 1.1 and VMI1 = 0.9 by default.
				• Reactive power flow into the transformer at the winding one bus end in
					Mvar when |COD1| is 2. No default is allowed.
				• Active power flow into the transformer at the winding one bus end in MW
					when |COD1| is 3. No default is allowed.
				• Not used when |COD1| is 0 or 4; VMA1 = 1.1 and VMI1 = 0.9 by default.
		NTP1 The number of tap positions available; used when COD1 is 1 or 2. NTP1 must be
				between 2 and 9999. NTP1 = 33 by default.
      	 */
      	if (cod > 0) {
          	if (branchData.getCode() == LFBranchCodeEnumType.TRANSFORMER) {
           		TapAdjustmentXmlType tapAdj = parser.getFactory().createTapAdjustmentXmlType();
           		branchData.getXfrInfo().setTapAdjustment(tapAdj);
           		tapAdj.setOffLine(cod < 0);
           		tapAdj.setTapAdjOnFromSide(onFromSide);
           		tapAdj.setTapLimit(BaseDataSetter.createTapLimit(rma, rmi));
           		tapAdj.setTapAdjStep(ntp);
           		if (Math.abs(cod) == 1) {
               		tapAdj.setAdjustmentType(TapAdjustmentEnumType.VOLTAGE);
        	    	TapAdjustmentXmlType.VoltageAdjData vAdjData = parser.getFactory().createTapAdjustmentXmlTypeVoltageAdjData();
        	    	tapAdj.setVoltageAdjData(vAdjData);
        	    	vAdjData.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
        	    	vAdjData.setMax(vma);
        	    	vAdjData.setMin(vmi);       
        	    }
           		else {
                 	tapAdj.setAdjustmentType(TapAdjustmentEnumType.M_VAR_FLOW);
        	    	TapAdjustmentXmlType.MvarFlowAdjData mvaAdjData = parser.getFactory().createTapAdjustmentXmlTypeMvarFlowAdjData(); 
        	    	tapAdj.setMvarFlowAdjData(mvaAdjData);
        	    	mvaAdjData.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
        	    	mvaAdjData.setMax(vma);
        	    	mvaAdjData.setMin(vmi);               		
           		}
          	}
    	    else if (branchData.getCode() == LFBranchCodeEnumType.PHASE_SHIFT_XFORMER) {
    	    	AngleAdjustmentXmlType angAdj = parser.getFactory().createAngleAdjustmentXmlType();
    	    	branchData.getXfrInfo().setAngleAdjustment(angAdj);
    	    	angAdj.setAngleLimit(BaseDataSetter.createAngleLimit(rma, rmi, AngleUnitType.DEG));
    	    	angAdj.setMax(vma);
    	    	angAdj.setMin(vmi);
    	    	angAdj.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
    	    	angAdj.setDesiredMeasuredOnFromSide(onFromSide);
    	    }              	
      	}
      	
      	/*
      	 * CR1, CX1 - The load drop compensation impedance for voltage controlling transformers
						entered in pu on system base quantities; used when COD1 is 1.
						CR1 + j CX1 = 0.0 by default
      	 */
      	if (cr != 0.0 || cx != 0.0) {
      		if (branchData.getNvPairList() == null)
      			branchData.setNvPairList(parser.getFactory().createNameValuePairListXmlType());
      		BaseJaxbHelper.addNVPair(branchData.getNvPairList(), "Xfr LoadDropCZ", new Double(cr).toString() + "," + new Double(cx).toString());
      	}

      	/*
      	 	TAB1 The number of a transformer impedance correction table if this transformer
				winding’s impedance is to be a function of either off-nominal turns ratio or
				phase shift angle (see Section 4.1.1.11), or 0 if no transformer impedance correction
				is to be applied to this transformer winding. TAB1 = 0 by default.					

      	 */
      	if (tab > 0)
      		xfrInfo.setZTableNumber(tab);
      	
  		/*
		Line-4 
			format 2W : WINDV2,NOMV2
			format 3W : 
			    WINDV2,  NOMV2,     ANG2,    RATA2,RATB2,RATC2,          COD2,   CONT2,RMA2,RMI2,VMA2,VMI2,              NTP2,TAB2,CR2,CX2
				137.500, 137.500,   0.000,   150.00,   150.00,   150.00, 0,      0, 0.00000, 0.00000, 0.00000, 0.00000,  33, 0, 0.00000, 0.00000
			
		
			WINDV2 The winding two off-nominal turns ratio in pu of winding two bus base voltage
					when CW is 1; WINDV2 = 1.0 by default. WINDV2 is the actual winding two
					voltage in kV when CW is 2; WINDV2 is equal to the base voltage of bus J by
					default.	
			NOMV2 The nominal (rated) winding two voltage in kV, or zero to indicate that nominal
					winding two voltage is to be taken as the base voltage of bus J. NOMV2
					is present for information purposes only; it is not used in any of the calculations
					for modeling the transformer. NOMV2 = 0.0 by default.								
  		 */
  		if (!xfrInfo.isDataOnSystemBase()) {
  			windv2 /= nomv2;
  			xfrInfo.setRatedVoltage2(BaseDataSetter.createVoltageValue(nomv2, VoltageUnitType.KV));
  		}
  		branchData.setToTurnRatio(BaseDataSetter.createTapPU(windv2));
       	if (isPsXfr) {
       		branchData.setToAngle(BaseDataSetter.createAngleValue(ang2, AngleUnitType.DEG));
       	}

       	if (is3W) {
       		xfrInfo.setBranchRatingLimit23(parser.getFactory().createBranchRatingLimitXmlType());
       		JaxbDataSetter.setBranchRatingLimitData(xfrInfo.getBranchRatingLimit23(), rata2, ratb2, ratc2, ApparentPowerUnitType.MVA);
       	}

       	/*
		Line-5 
			format 2W : N/A
			format 3W : 
				WINDV3,   NOMV3,  ANG3,       RATA3,RATB3,RATC3,         COD3,   CONT3,RMA3,RMI3,VMA3,VMI3,              NTP3,TAB3,CR3,CX3
            	34.5000,  34.500, -30.000,    22.29,    22.29,    22.29, 0,      0, 0.00000, 0.00000, 0.00000, 0.00000,  33, 0, 0.00000, 0.00000
		*/
       	if (is3W) {
      		if (!xfrInfo.isDataOnSystemBase()) {
      			windv3 /= nomv3;
      			xfrInfo.setRatedVoltage3(BaseDataSetter.createVoltageValue(nomv3, VoltageUnitType.KV));
      		}
      		xfrInfo.setTurnRatio3(BaseDataSetter.createTapPU(windv3));
      		xfrInfo.setBranchRatingLimit13(parser.getFactory().createBranchRatingLimitXmlType());
           	JaxbDataSetter.setBranchRatingLimitData(xfrInfo.getBranchRatingLimit13(), rata3, ratb3, ratc3, ApparentPowerUnitType.MVA);
           	if (isPsXfr) {
           		xfrInfo.setShiftAngle3(BaseDataSetter.createAngleValue(ang3, AngleUnitType.DEG));
           	}
       	}
	}
	
	private static void procLineString(String lineStr1, String lineStr2, String lineStr3, String lineStr4, String lineStr5, 
							PsseVersion version, Logger logger) {
		// 324558,324023,     0,'1 ',2,2,1,   0.00036,  -0.00197,1,'HFL- 1,2    ',1,   1,1.0000
		// The name field might have ','
		StringTokenizer st = new StringTokenizer(lineStr1, "'");
		String s1 = st.nextToken();  // 324558,324023,     0, 
		String s2 = st.nextToken();  // 1 
		String s3 = st.nextToken();  // ,2,2,1,   0.00036,  -0.00197,1, 
		String s4 = st.nextToken();  // HFL- 1,2    
		String s5 = st.nextToken();  // ,1,   1,1.0000 

		st = new StringTokenizer(s1, ",");
		i = new Integer(st.nextToken().trim()).intValue();
		j = new Integer(st.nextToken().trim()).intValue();
		k = new Integer(st.nextToken().trim()).intValue();

		ckt = s2.trim();

		st = new StringTokenizer(s3, ",");
		cw = new Integer(st.nextToken().trim()).intValue();
		cz = new Integer(st.nextToken().trim()).intValue();
		cm = new Integer(st.nextToken().trim()).intValue();
		mag1 = new Double(st.nextToken().trim()).doubleValue();
		mag2 = new Double(st.nextToken().trim()).doubleValue();
		nmetr = new Integer(st.nextToken().trim()).intValue();

		name = s4;

		st = new StringTokenizer(s5, ",");
		stat = new Integer(st.nextToken().trim()).intValue();

		if (st.hasMoreTokens())
			o1 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f1 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o2 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f2 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o3 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f3 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o4 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f4 = new Double(st.nextToken().trim()).doubleValue();

		st = new StringTokenizer(lineStr2, ",");
		r1_2 = new Double(st.nextToken().trim()).doubleValue();
		x1_2 = new Double(st.nextToken().trim()).doubleValue();
		sbase1_2 = new Double(st.nextToken().trim()).doubleValue();
		if (k != 0) {
			r2_3 = new Double(st.nextToken().trim()).doubleValue();
			x2_3 = new Double(st.nextToken().trim()).doubleValue();
			sbase2_3 = new Double(st.nextToken().trim()).doubleValue();
			r3_1 = new Double(st.nextToken().trim()).doubleValue();
			x3_1 = new Double(st.nextToken().trim()).doubleValue();
			sbase3_1 = new Double(st.nextToken().trim()).doubleValue();
			vmstar = new Double(st.nextToken().trim()).doubleValue();
			anstar  = new Double(st.nextToken().trim()).doubleValue();
		}

		st = new StringTokenizer(lineStr3, ",");
		windv1 = new Double(st.nextToken().trim()).doubleValue();
		nomv1 = new Double(st.nextToken().trim()).doubleValue();
		ang1 = new Double(st.nextToken().trim()).doubleValue();
		rata1 = new Double(st.nextToken().trim()).doubleValue();
		ratb1 = new Double(st.nextToken().trim()).doubleValue();
		ratc1 = new Double(st.nextToken().trim()).doubleValue();
		cod = new Integer(st.nextToken().trim()).intValue();
		cont = new Integer(st.nextToken().trim()).intValue();
		rma = new Double(st.nextToken().trim()).doubleValue();
		rmi = new Double(st.nextToken().trim()).doubleValue();
		vma = new Double(st.nextToken().trim()).doubleValue();
		vmi = new Double(st.nextToken().trim()).doubleValue();
		ntp = new Integer(st.nextToken().trim()).intValue();
		tab = new Integer(st.nextToken().trim()).intValue();
		cr = new Double(st.nextToken().trim()).doubleValue();
		cx = new Double(st.nextToken().trim()).doubleValue();

		st = new StringTokenizer(lineStr4, ",");
		windv2 = new Double(st.nextToken().trim()).doubleValue();
		nomv2 = new Double(st.nextToken().trim()).doubleValue();
		if (k != 0) {
			ang2 = new Double(st.nextToken().trim()).doubleValue();
			rata2 = new Double(st.nextToken().trim()).doubleValue();
			ratb2 = new Double(st.nextToken().trim()).doubleValue();
			ratc2 = new Double(st.nextToken().trim()).doubleValue();
		}

		if (k != 0) {
			st = new StringTokenizer(lineStr5, ",");
			windv3 = new Double(st.nextToken().trim()).doubleValue();
			nomv3 = new Double(st.nextToken().trim()).doubleValue();
			if (k != 0) {
				ang3 = new Double(st.nextToken().trim()).doubleValue();
				rata3 = new Double(st.nextToken().trim()).doubleValue();
				ratb3 = new Double(st.nextToken().trim()).doubleValue();
				ratc3 = new Double(st.nextToken().trim()).doubleValue();
			}
		}
	}
	
}
