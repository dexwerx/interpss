 /*
  * @(#)InitDataUtil.java   
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

package org.interpss.editor.form;

import java.util.Vector;

import org.apache.commons.math3.complex.Complex;
import org.interpss.editor.data.aclf.AclfBusData;
import org.interpss.editor.data.acsc.AcscBusData;
import org.interpss.editor.data.acsc.AcscNetData;
import org.interpss.editor.data.common.ScPointData;
import org.interpss.editor.data.common.XfrConnectData;
import org.interpss.editor.data.dist.DistBusData;
import org.interpss.editor.data.dist.DistNetData;
import org.interpss.editor.data.dstab.DStabControllerData;
import org.interpss.editor.data.dstab.DStabMachData;
import org.interpss.editor.data.dstab.DStabNetData;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGNetForm;

import com.interpss.dstab.mach.MachineController;

public class InitDataUtil {
    // the following fields are used to track last values entered by the user
	public static double LastEnteredBaseVolt = 1000.0d;
	public static String LastEnteredBaseVoltUnit = "Volt";
	public static String LastEnteredMvaRatingUnit = "MVA";
	public static String LastEnteredRatingUnit = "HP";
	public static String LastEnteredPFactorUnit = "PU";
	public static String LastEnteredBusZUnit = "PU";
	public static String LastEnteredLengthUnit = "Ft";
	public static String LastEnteredXfrMvaUnit = "MVA";
	public static String LastEnteredXfrZUnit = "MVA";
	public static String LastEnteredTapUnit = "PU";
	public static int    LastEnteredZone = 1;
	public static int    LastEnteredArea = 1;

	// Distribution Network 
	// ====================
	
	public static void initScData_DistNet(GNetForm form) {
    	form.setId("InterPSS Distribution Network");
    	form.setDescription("This network is created by InterPSS OneLine Draw Module");
    	form.setBaseKVA(100000d);
    	form.setFreqHZ(50d);
    	form.setAllowParallelBranch(false);
    	form.setCheckBusDuplication(true);

    	form.getDistNetData().setScStd(DistNetData.ScStd_Generic);
    	form.getDistNetData().setScPointList(new Vector<ScPointData>());
    	
    	form.getDistNetData().setLoadSchedulePoints(0);
    	form.getDistNetData().setLoadSchedulePeriodLength(1.0);
    	form.getDistNetData().setLoadSchedulePeriodUnit("Hour");
	}

	   public static void initScData_DistBus(DistNetData netData, GBusForm form) {
	    	form.getDistBusData().setBusCode(DistBusData.BusCode_NonContribute);
			form.setBaseVoltage(LastEnteredBaseVolt);
			form.setBaseVoltUnit(LastEnteredBaseVoltUnit);
			form.setZone(LastEnteredZone);
			form.getDistBusData().setMvaRatingUnit(LastEnteredMvaRatingUnit);
			form.getDistBusData().setBusRatingUnit(LastEnteredRatingUnit);
			form.getDistBusData().setPFactorUnit(LastEnteredPFactorUnit);
			form.getDistBusData().setZUnit(LastEnteredBusZUnit);

	    	form.getDistBusData().initZ_SCList(netData.getActiveScPoints());
	    	
	    	if (netData.getLoadSchedulePoints() > 0) {
	    		for (int i = 0; i < netData.getLoadSchedulePoints(); i++) {
	    			form.getDistBusData().setLoadSchedule(new Complex(100.0,100.0), i);
	    			form.getDistBusData().setPointVoltage(new Complex(1.0,0.0), i);
	    		}
	    	}
		}

	   	public static void initScData_DistBranch(DistNetData netData, GBranchForm form) {
	    	form.getDistBranchData().setBranchCode(IGBranchForm.DistBranchCode_Feeder);
			form.setZone(LastEnteredZone);
			form.setZone(LastEnteredArea);
	    	form.getDistBranchData().setLength(1000.0d);
	    	form.getDistBranchData().setZ0Unit("Ohm");
	    	form.getDistBranchData().setHalfShuntBUnit("microMho");
	    	form.getDistBranchData().getFromXfrConnectData().getGrounding().setUnit("Ohm");
	    	form.getDistBranchData().getToXfrConnectData().getGrounding().setUnit("Ohm");
			form.getDistBranchData().setLengthUnit(LastEnteredLengthUnit);
			form.getDistBranchData().setXfrRatingUnit(LastEnteredXfrMvaUnit);
			form.getDistBranchData().setZUnit(LastEnteredXfrZUnit);
			form.getDistBranchData().setXfrTapUnit(LastEnteredTapUnit);
			form.getDistBranchData().getFromXfrConnectData().setCode(XfrConnectData.Code_Wye);
			form.getDistBranchData().getToXfrConnectData().setCode(XfrConnectData.Code_Delta);

			if (netData.getLoadSchedulePoints() > 0) {
	    		for (int i = 0; i < netData.getLoadSchedulePoints(); i++) {
	    			form.getDistBranchData().setServiceSchedule(true, i);
	    		}
	    	}
		}

	   	
	// Aclf/Acsc Network 
	// ==================

	public static void initScData_AcscNet(GNetForm form) {
    	form.setId("InterPSS Transmission Aclf/Acsc Network");
    	form.setDescription("This network is created by InterPSS OneLine Draw Module");
    	form.setBaseKVA(100000d);
    	form.setFreqHZ(50d);
    	form.setAllowParallelBranch(false);
    	form.setCheckBusDuplication(true);
	}

  	public static void initScData_AcscBus(AcscNetData netForm, GBusForm form) {
   	   	form.getAcscBusData().setScCode(AcscBusData.ScCode_NonContribute);
   		form.setBaseVoltage(LastEnteredBaseVolt);
   		form.setBaseVoltUnit(LastEnteredBaseVoltUnit);
   		form.setZone(LastEnteredZone);
   		form.setZone(LastEnteredArea);
   		form.getAcscBusData().setGenCode(AclfBusData.LoadCode_NonLoad);
   		form.getAcscBusData().setLoadCode(AclfBusData.LoadCode_NonLoad);
   		form.getAcscBusData().setGenUnit("PU");
   		form.getAcscBusData().setLoadUnit("PU");
   		form.getAcscBusData().setCapQUnit("PU");
   		form.getAcscBusData().setVoltageMagUnit("PU");
   		form.getAcscBusData().setVoltageAngUnit("Deg");
   		form.getAcscBusData().setZUnit("PU");
	}
   
   public static void initScData_AcscBranch(GBranchForm form) {
   		form.setZone(LastEnteredZone);
   		form.setZone(LastEnteredArea);
   		form.getAcscBranchData().setHalfShuntBUnit("PU");
   		form.getAcscBranchData().setPhaseShiftAngleUnit("Deg");
   		form.getAcscBranchData().setXfrTapUnit("PU");
   		form.getAcscBranchData().setZUnit("PU");
		form.getAcscBranchData().getFromXfrConnectData().setCode(XfrConnectData.Code_Wye);
		form.getAcscBranchData().getToXfrConnectData().setCode(XfrConnectData.Code_Delta);
   }
   
    public static Vector<ScPointData> getAcsc5Points() {
    	Vector<ScPointData> vect = new Vector<ScPointData>();
    	ScPointData row = new ScPointData("1/2 cycle", true, "Half cycle for ...");
    	vect.add(row);

    	row = new ScPointData("3 cycle", true, "Third cycle for ...");
    	vect.add(row);

    	row = new ScPointData("8 cycle", true, "Eighth cycle for ...");
    	vect.add(row);

    	row = new ScPointData("30 cycle", true, "Thirtyth cycle for ...");
    	vect.add(row);

    	row = new ScPointData("SteadyState", true, "Steady state for ...");
    	vect.add(row);
       return vect;
   }
   
   public static Vector<ScPointData> getAcscANSIPoints() {
    	Vector<ScPointData> vect = new Vector<ScPointData>();
    	ScPointData row = new ScPointData("1/2 cycle", true, "For momentary SC current calculation");
    	vect.add(row);

    	row = new ScPointData("1.5~4 cycles", true, "For interrupting SC current calculatoin ");
    	vect.add(row);

    	row = new ScPointData("30 cycles", true, "For steady-state SC current calculation");
    	vect.add(row);
      return vect;
   }
      
	
	// DStability Network 
	// ==================
	
	public static void initScData_DStabNet(GNetForm form) {
		initScData_AcscNet(form);
		form.getAcscNetData().setHasAclfData(true);
    	form.setId("InterPSS Transient Stabiliby Network");
    	form.setAllowParallelBranch(true);
    	form.setCheckBusDuplication(true);
	}
	
  	public static void initScData_DStabBus(DStabNetData netForm, GBusForm form) {
  		initScData_AcscBus(netForm.getAcscNetData(), form);
  	}
  	
    public static void initScData_DStabBranch(GBranchForm form) {
  		initScData_AcscBranch(form);
    }
    
    // Machine Controller
    // ==================
    
    /**
     * set init data to the data object, which will be edited.
     */
  	public static void initDStabControllerData(DStabControllerData data, String typeName, MachineController controller) {
  		data.setTypeName(typeName);
		data.setDataXmlStr(controller.getDataXmlString());
		data.setScripts("");
  	}

  	public static void initDStabMachineData(DStabMachData machData) {
  		if (machData.getType().equals(DStabMachData.MachType_InfiniteBus)) {
  		}
  		else if (machData.getType().equals(DStabMachData.MachType_EConst)) {
			machData.setXd1(0.275);
			machData.setX0(0.058);
			machData.setX2(0.157);
			machData.setRa(0.011);
			
			machData.setInertia(5.6);
			machData.setDamping(6.0);
  		}
  		else if (machData.getType().equals(DStabMachData.MachType_Eq1)) {
			machData.setXd(1.05);
			machData.setXq(0.70);
			machData.setXd1(0.35);
			
			machData.setXl(0.15);
			machData.setX0(0.0825);
			machData.setX2(0.25);
			machData.setRa(0.011);
			
			machData.setTd01(5.25);
			
			machData.setInertia(3.0);
			machData.setDamping(4.0);
			machData.setSliner(0.7);
			machData.setS100(12.0);
			machData.setS120(23.0);
  		}
  		else if (machData.getType().equals(DStabMachData.MachType_Eq1Ed1)) {
			machData.setXd(1.65);
			machData.setXq(1.65);
			machData.setXd1(0.275);
			machData.setXq1(0.65);
			
			machData.setXl(0.15);
			machData.setX0(0.058);
			machData.setX2(0.157);
			machData.setRa(0.00325);
			
			machData.setTd01(6.5);
			machData.setTq01(1.25);
			
			machData.setInertia(5.6);
			machData.setDamping(6.0);
			machData.setSliner(0.7);
			machData.setS100(12.0);
			machData.setS120(23.0);
  		}
  		else if (machData.getType().equals(DStabMachData.MachType_SalientPole)) {
			machData.setXd(1.05);
			machData.setXq(0.70);
			machData.setXd1(0.35);
			machData.setXd11(0.25);
			machData.setXq11(0.33);
			
			machData.setXl(0.15);
			machData.setX0(0.0825);
			machData.setX2(0.25);
			machData.setRa(0.011);
			
			machData.setTd01(5.25);
			machData.setTd011(0.03);
			machData.setTq011(0.05);
			
			machData.setInertia(3.0);
			machData.setDamping(4.0);
			machData.setSliner(0.7);
			machData.setS100(12.0);
			machData.setS120(23.0);
  		}
  		else if (machData.getType().equals(DStabMachData.MachType_RoundRotor)) {
			machData.setXd(1.65);
			machData.setXq(1.65);
			machData.setXd1(0.275);
			machData.setXq1(0.65);
			machData.setXd11(0.185);
			machData.setXq11(0.185);
			
			machData.setXl(6.5);
			machData.setX0(1.25);
			machData.setX2(0.035);
			machData.setRa(0.035);
			
			machData.setTd01(0.15);
			machData.setTq01(0.058);
			machData.setTd011(0.157);
			machData.setTq011(0.00325);
			
			machData.setInertia(5.6);
			machData.setDamping(6.0);
			machData.setSliner(0.7);
			machData.setS100(12.0);
			machData.setS120(23.0);
  		}
  	}

  	public static void setLastEntered_BusData(GBusForm form) {
		LastEnteredBaseVolt = form.getBaseVoltage();
		LastEnteredBaseVoltUnit = form.getBaseVoltUnit();
		LastEnteredZone = form.getZone();
		LastEnteredArea = form.getArea();
		if (form.getAppType().equals(IGNetForm.AppType_Distribution)) {
			LastEnteredMvaRatingUnit = form.getDistBusData().getMvaRatingUnit();
			LastEnteredRatingUnit = form.getDistBusData().getBusRatingUnit();
			LastEnteredPFactorUnit = form.getDistBusData().getPFactorUnit();
			LastEnteredBusZUnit = form.getDistBusData().getZUnit();
		}
	}

	public static void setLastEntered_BranchData(GBranchForm form) {
		LastEnteredZone = form.getZone();
		LastEnteredArea = form.getArea();
		if (form.getAppType().equals(IGNetForm.AppType_Distribution)) {
			LastEnteredLengthUnit = form.getDistBranchData().getLengthUnit();
			LastEnteredXfrMvaUnit = form.getDistBranchData().getXfrRatingUnit();
			LastEnteredXfrZUnit = form.getDistBranchData().getZUnit();
			LastEnteredTapUnit = form.getDistBranchData().getXfrTapUnit();
		}
	}
}