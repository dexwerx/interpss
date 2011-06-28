/*
 * @(#)DStabParserHelper.java   
 *
 * Copyright (C) 2006-2010 www.interpss.org
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
 * @Date 02/01/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.model.dstab;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.schema.ClassicMachineXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.Eq11Ed11MachineXmlType;
import org.ieee.odm.schema.Eq11MachineXmlType;
import org.ieee.odm.schema.Eq1Ed1MachineXmlType;
import org.ieee.odm.schema.Eq1MachineXmlType;
import org.ieee.odm.schema.EquiMachineXmlType;
import org.ieee.odm.schema.ExcBPAECXmlType;
import org.ieee.odm.schema.ExcBPAEKXmlType;
import org.ieee.odm.schema.ExcBPAFJXmlType;
import org.ieee.odm.schema.ExcBPAFKXmlType;
import org.ieee.odm.schema.ExcBPAFQXmlType;
import org.ieee.odm.schema.ExcBPAFRXmlType;
import org.ieee.odm.schema.ExcBPAFSXmlType;
import org.ieee.odm.schema.ExcBPAFUXmlType;
import org.ieee.odm.schema.ExcBPAFVXmlType;
import org.ieee.odm.schema.ExcIEEE1968Type1SXmlType;
import org.ieee.odm.schema.ExcIEEE1968Type1XmlType;
import org.ieee.odm.schema.ExcIEEE1968Type2XmlType;
import org.ieee.odm.schema.ExcIEEE1968Type3XmlType;
import org.ieee.odm.schema.ExcIEEE1968Type4XmlType;
import org.ieee.odm.schema.ExcIEEE1981ST1XmlType;
import org.ieee.odm.schema.ExcIEEE1981TypeAC2XmlType;
import org.ieee.odm.schema.ExcIEEE1981TypeDC1XmlType;
import org.ieee.odm.schema.ExcIEEE1981TypeDC2XmlType;
import org.ieee.odm.schema.ExcIEEE1992TypeAC1AXmlType;
import org.ieee.odm.schema.ExcIEEEModified1968Type1XmlType;
import org.ieee.odm.schema.ExcSimpleTypeXmlType;
import org.ieee.odm.schema.ExcTSATTypeEXC34XmlType;
import org.ieee.odm.schema.GovBPAGiGaTbCombinedModelXmlType;
import org.ieee.odm.schema.GovBPAGsTbCombinedModelXmlType;
import org.ieee.odm.schema.GovBPAHydroTurbineGHXmlType;
import org.ieee.odm.schema.GovHydroSteamGeneralModelXmlType;
import org.ieee.odm.schema.GovHydroTurbineXmlType;
import org.ieee.odm.schema.GovHydroXmlType;
import org.ieee.odm.schema.GovIEEE1981Type1XmlType;
import org.ieee.odm.schema.GovIEEE1981Type2XmlType;
import org.ieee.odm.schema.GovIEEE1981Type3XmlType;
import org.ieee.odm.schema.GovSimpleTypeXmlType;
import org.ieee.odm.schema.GovSteamNRXmlType;
import org.ieee.odm.schema.GovSteamTCSRXmlType;
import org.ieee.odm.schema.GovSteamTDSRXmlType;
import org.ieee.odm.schema.GovernorModelXmlType;
import org.ieee.odm.schema.PssBPADualInputXmlType;
import org.ieee.odm.schema.PssIEE2STXmlType;
import org.ieee.odm.schema.PssIEEE1981TypeXmlType;
import org.ieee.odm.schema.PssIEEE1992Type2AXmlType;
import org.ieee.odm.schema.PssIEEE1AXmlType;
import org.ieee.odm.schema.PssIEEEDualInputXmlType;
import org.ieee.odm.schema.PssSimpleTypeXmlType;
import org.ieee.odm.schema.SpeedGovBPAGSModelXmlType;
import org.ieee.odm.schema.SpeedGovBPAGiGaCombinedXmlType;
import org.ieee.odm.schema.SpeedGovBPARegGIModelXmlType;
import org.ieee.odm.schema.SpeedGovBPAServoGAModelXmlType;
import org.ieee.odm.schema.SpeedGovModelXmlType;
import org.ieee.odm.schema.SteamTurbineBPATBModelXmlType;
import org.ieee.odm.schema.SteamTurbineNRXmlType;
import org.ieee.odm.schema.SteamTurbineTCDRXmlType;
import org.ieee.odm.schema.SteamTurbineTCSRXmlType;

public class DStabParserHelper extends AclfParserHelper {
	
	/**
	 * Get or create a dynamic generator record at the bus
	 * 
	 * @param bus
	 * @return
	 */
	public static DynamicGeneratorXmlType getDynamicGenRec(DStabBusXmlType bus) {
		if (bus.getDynamicGenList() == null)
			bus.setDynamicGenList(getFactory().createDStabBusXmlTypeDynamicGenList());
		if (bus.getDynamicGenList().getDynamicGen().size() ==0 )
			bus.getDynamicGenList().getDynamicGen().add(getFactory().createDynamicGeneratorXmlType());
		return bus.getDynamicGenList().getDynamicGen().get(0);
	}

	/*
	 * Machine model creation functions
	 * ================================
	 */
	
	public static ClassicMachineXmlType createClassicMachine(DynamicGeneratorXmlType gen) {
		ClassicMachineXmlType mach = getFactory().createClassicMachineXmlType();
		gen.setMachineModel(getFactory().createClassicMachModel(mach));
		return mach;
	}
	
	
	public static EquiMachineXmlType createEquiMachine(DynamicGeneratorXmlType gen) {
		EquiMachineXmlType mach = getFactory().createEquiMachineXmlType();
		gen.setMachineModel(getFactory().createEquiMachModel(mach));
		return mach;
	}
	
	
	public static Eq1MachineXmlType createEq1Machine(DynamicGeneratorXmlType gen) {
		Eq1MachineXmlType mach = getFactory().createEq1MachineXmlType();
		gen.setMachineModel(getFactory().createEq1MachModel(mach));
		return mach;
	}
	
	public static Eq1Ed1MachineXmlType createEq1Ed1Machine(DynamicGeneratorXmlType gen) {
		Eq1Ed1MachineXmlType mach = getFactory().createEq1Ed1MachineXmlType();
		gen.setMachineModel(getFactory().createEq1Ed1MachModel(mach));
		return mach;
	}
	
	public static Eq11MachineXmlType createEq11Machine(DynamicGeneratorXmlType gen) {
		Eq11MachineXmlType mach = getFactory().createEq11MachineXmlType();
		gen.setMachineModel(getFactory().createEq11MachModel(mach));
		return mach;
	}
	
	public static Eq11Ed11MachineXmlType createEq11Ed11Machine(DynamicGeneratorXmlType gen) {
		Eq11Ed11MachineXmlType mach = getFactory().createEq11Ed11MachineXmlType();
		gen.setMachineModel(getFactory().createEq11Ed11MachModel(mach));
		return mach;
	}
	
	public static Eq1MachineXmlType.SeFmt1 createMachineSeFmt1() {
		Eq1MachineXmlType.SeFmt1 seFmt1 = getFactory().createEq1MachineXmlTypeSeFmt1();
		seFmt1.setSe100(0.0);			
		seFmt1.setSe120(0.0);
		seFmt1.setSliner(10.0);	// this means the machine will never get into saturation	
		return seFmt1;
	}
	
	/*
	 * Exciter model creation functions
	 * ================================
	 */

	public static ExcSimpleTypeXmlType createExcSimpleTypeXmlType(DynamicGeneratorXmlType gen) {
		ExcSimpleTypeXmlType exc = getFactory().createExcSimpleTypeXmlType();
		gen.setExciter(getFactory().createExcSimpleType(exc));
		return exc;
	}

	public static ExcIEEE1992TypeAC1AXmlType createExcIEEE1992TypeAC1AXmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1992TypeAC1AXmlType exc = getFactory().createExcIEEE1992TypeAC1AXmlType();
		gen.setExciter(getFactory().createExcIEEE1992TypeAC1A(exc));
		return exc;
	}

	public static ExcIEEE1981TypeAC2XmlType createExcIEEE1981TypeAC2XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1981TypeAC2XmlType exc = getFactory().createExcIEEE1981TypeAC2XmlType();
		gen.setExciter(getFactory().createExcIEEE1981TypeAC2(exc));
		return exc;
	}
	public static ExcIEEE1981TypeDC1XmlType createExcIEEE1981TypeDC1XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1981TypeDC1XmlType exc = getFactory().createExcIEEE1981TypeDC1XmlType();
		gen.setExciter(getFactory().createExcIEEE1981TypeDC1(exc));
		return exc;
	}
	public static ExcIEEE1981TypeDC2XmlType createExcIEEE1981TypeDC2XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1981TypeDC2XmlType exc = getFactory().createExcIEEE1981TypeDC2XmlType();
		gen.setExciter(getFactory().createExcIEEE1981TypeDC2(exc));
		return exc;
	}
	
	public static ExcIEEE1981ST1XmlType createExcIEEE1981ST1XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1981ST1XmlType exc = getFactory().createExcIEEE1981ST1XmlType();
		gen.setExciter(getFactory().createExcIEEE1981ST1(exc));
		return exc;
	}

	public static ExcIEEE1968Type1XmlType createExcIEEE1968Type1XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1968Type1XmlType exc = getFactory().createExcIEEE1968Type1XmlType();
		gen.setExciter(getFactory().createExcIEEE1968Type1(exc));
		return exc;
	}

	public static ExcIEEEModified1968Type1XmlType createExcIEEEModified1968Type1XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEEModified1968Type1XmlType exc = getFactory().createExcIEEEModified1968Type1XmlType();
		gen.setExciter(getFactory().createExcIEEEModified1968Type1(exc));
		return exc;
	}

	public static ExcIEEE1968Type1SXmlType createExcIEEE1968Type1SXmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1968Type1SXmlType exc = getFactory().createExcIEEE1968Type1SXmlType();
		gen.setExciter(getFactory().createExcIEEE1968Type1S(exc));
		return exc;
	}

	public static ExcIEEE1968Type2XmlType createExcIEEE1968Type2XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1968Type2XmlType exc = getFactory().createExcIEEE1968Type2XmlType();
		gen.setExciter(getFactory().createExcIEEE1968Type2(exc));
		return exc;
	}

	public static ExcIEEE1968Type4XmlType createExcIEEE1968Type4XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1968Type4XmlType exc = getFactory().createExcIEEE1968Type4XmlType();
		gen.setExciter(getFactory().createExcIEEE1968Type4(exc));
		return exc;
	}

	public static ExcIEEE1968Type3XmlType createExcIEEE1968Type3XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1968Type3XmlType exc = getFactory().createExcIEEE1968Type3XmlType();
		gen.setExciter(getFactory().createExcIEEE1968Type3(exc));
		return exc;
	}
	


	public static ExcTSATTypeEXC34XmlType createExcTSATTypeEXC34XmlType(DynamicGeneratorXmlType gen) {
		ExcTSATTypeEXC34XmlType exc = getFactory().createExcTSATTypeEXC34XmlType();
		gen.setExciter(getFactory().createExcTSATTypeEXC34(exc));
		return exc;
	}
	
	public static ExcBPAECXmlType createExcBPAECXmlType(DynamicGeneratorXmlType gen) {
		ExcBPAECXmlType exc = getFactory().createExcBPAECXmlType();
		gen.setExciter(getFactory().createExcBPATypeEC(exc));
		return exc;
	}
	public static ExcBPAEKXmlType createExcBPAEKXmlType(DynamicGeneratorXmlType gen) {
		ExcBPAEKXmlType exc = getFactory().createExcBPAEKXmlType();
		gen.setExciter(getFactory().createExcBPATypeEK(exc));
		return exc;
	}

	public static ExcBPAFJXmlType createExcBPAFJXmlType(DynamicGeneratorXmlType gen) {
		ExcBPAFJXmlType exc = getFactory().createExcBPAFJXmlType();
		gen.setExciter(getFactory().createExcBPATypeFJ(exc));
		return exc;
	}
	public static ExcBPAFKXmlType createExcBPAFKXmlType(
			DynamicGeneratorXmlType gen) {
		ExcBPAFKXmlType exc = getFactory().createExcBPAFKXmlType();
		gen.setExciter(getFactory().createExcBPATypeFK(exc));
		return exc;
	}
	
	public static ExcBPAFQXmlType createExcBPAFQXmlType(
			DynamicGeneratorXmlType gen) {
		ExcBPAFQXmlType exc = getFactory().createExcBPAFQXmlType();
		gen.setExciter(getFactory().createExcBPATypeFQ(exc));
		return exc;
	}
	
	public static ExcBPAFRXmlType createExcBPAFRXmlType(
			DynamicGeneratorXmlType gen) {
		ExcBPAFRXmlType exc = getFactory().createExcBPAFRXmlType();
		gen.setExciter(getFactory().createExcBPATypeFR(exc));
		return exc;
	}
	
	public static ExcBPAFSXmlType createExcBPAFSXmlType(
			DynamicGeneratorXmlType gen) {
		ExcBPAFSXmlType exc = getFactory().createExcBPAFSXmlType();
		gen.setExciter(getFactory().createExcBPATypeFS(exc));
		return exc;
	}
	
	public static ExcBPAFUXmlType createExcBPAFUXmlType(
			DynamicGeneratorXmlType gen) {
		ExcBPAFUXmlType exc = getFactory().createExcBPAFUXmlType();
		gen.setExciter(getFactory().createExcBPATypeFU(exc));
		return exc;
	}
	
	public static ExcBPAFVXmlType createExcBPAFVXmlType(
			DynamicGeneratorXmlType gen) {
		ExcBPAFVXmlType exc = getFactory().createExcBPAFVXmlType();
		gen.setExciter(getFactory().createExcBPATypeFV(exc));
		return exc;
	}
	
	/*
	 * Governor model creation functions
	 * =================================
	 */
	//GovernorModelXmlType is an abstract type, can not initiated.
	/*
	public static GovernorModelXmlType createGovernorModelXmlType(DynamicGeneratorXmlType gen) {
		GovernorModelXmlType gov = new GovernorModelXmlType();
			getFactory().createGovernor();
		gen.setGovernor(getFactory().createGovSimpleType(gov));
		return gov;
	}
	*/
	public static GovSimpleTypeXmlType createGovSimpleTypeXmlType(DynamicGeneratorXmlType gen) {
		GovSimpleTypeXmlType gov = getFactory().createGovSimpleTypeXmlType();
		gen.setGovernor(getFactory().createGovSimpleType(gov));
		return gov;
	}

	public static GovIEEE1981Type1XmlType createGovIEEE1981Type1XmlType(DynamicGeneratorXmlType gen) {
		GovIEEE1981Type1XmlType gov = getFactory().createGovIEEE1981Type1XmlType();
		gen.setGovernor(getFactory().createGovIEEE1981Type1(gov));
		return gov;
	}

	public static GovIEEE1981Type2XmlType createGovIEEE1981Type2XmlType(DynamicGeneratorXmlType gen) {
		GovIEEE1981Type2XmlType gov = getFactory().createGovIEEE1981Type2XmlType();
		gen.setGovernor(getFactory().createGovIEEE1981Type2(gov));
		return gov;
	}

	public static GovIEEE1981Type3XmlType createGovIEEE1981Type3XmlType(DynamicGeneratorXmlType gen) {
		GovIEEE1981Type3XmlType gov = getFactory().createGovIEEE1981Type3XmlType();
		gen.setGovernor(getFactory().createGovIEEE1981Type3(gov));
		return gov;
	}

	public static GovHydroXmlType createGovHydroXmlType(DynamicGeneratorXmlType gen) {
		GovHydroXmlType gov = getFactory().createGovHydroXmlType();
		gen.setGovernor(getFactory().createGovHydro(gov));
		return gov;
	}

	public static GovHydroTurbineXmlType createGovHydroTurbineXmlType(DynamicGeneratorXmlType gen) {
		GovHydroTurbineXmlType gov = getFactory().createGovHydroTurbineXmlType();
		gen.setGovernor(getFactory().createGovHydroTurbine(gov));
		return gov;
	}

	public static GovHydroSteamGeneralModelXmlType createGovHydroSteamGeneralModelXmlType(DynamicGeneratorXmlType gen) {
		GovHydroSteamGeneralModelXmlType gov = getFactory().createGovHydroSteamGeneralModelXmlType();
		gen.setGovernor(getFactory().createGovHydroSteamGeneralModel(gov));
		return gov;
	}
	
	public static GovSteamNRXmlType createGovSteamNRXmlType(DynamicGeneratorXmlType gen) {
		GovSteamNRXmlType gov = getFactory().createGovSteamNRXmlType();
		gen.setGovernor(getFactory().createGovSteamNR(gov));
		return gov;
	}

	public static GovSteamTCSRXmlType createGovSteamTCSRXmlType(DynamicGeneratorXmlType gen) {
		GovSteamTCSRXmlType gov = getFactory().createGovSteamTCSRXmlType();
		gen.setGovernor(getFactory().createGovSteamTCSR(gov));
		return gov;
	}

	public static GovSteamTDSRXmlType createGovSteamTDSRXmlType(DynamicGeneratorXmlType gen) {
		GovSteamTDSRXmlType gov = getFactory().createGovSteamTDSRXmlType();
		gen.setGovernor(getFactory().createGovSteamTDSR(gov));
		return gov;
	}
	/*  Stream turbine  models: NR, TCSR,TCDR type
	 * ====================
	 */
	public static SteamTurbineNRXmlType createSteamTurbineNRXmlType(DynamicGeneratorXmlType gen){
		SteamTurbineNRXmlType stNR=getFactory().createSteamTurbineNRXmlType();
		gen.getGovernor().getValue().setTurbine(getFactory().createTurbineSteamNR(stNR));
		return stNR;
		
	}
	public static SteamTurbineTCSRXmlType createSteamTurbineTCSRRXmlType(DynamicGeneratorXmlType gen){
		SteamTurbineTCSRXmlType stTCSR=getFactory().createSteamTurbineTCSRXmlType();
		gen.getGovernor().getValue().setTurbine(getFactory().createTurbineSteamTCSR(stTCSR));
		return stTCSR;
		
	}
	public static SteamTurbineTCDRXmlType createSteamTurbineTCDRXmlType(DynamicGeneratorXmlType gen){
		SteamTurbineTCDRXmlType stTCDR=getFactory().createSteamTurbineTCDRXmlType();
		gen.getGovernor().getValue().setTurbine(getFactory().createTurbineSteamNR(stTCDR));
		return stTCDR;
		
	}
	
	
	// The following are BPA Governor models, including speedGov and turbine 
	
	
	//GH 
	public static GovBPAHydroTurbineGHXmlType createGovBPAHydroTurbineGHXmlType(
			DynamicGeneratorXmlType gen) {
		GovBPAHydroTurbineGHXmlType gov=getFactory().createGovBPAHydroTurbineGHXmlType();
		gen.setGovernor(getFactory().createGovBPAHydroTurbineGHModel(gov));
		
		return gov;
	}
	//TODO GS is a speed Governing model, but there is no method in DynamicGeneratorXmlType for such model now 
	public static SpeedGovBPAGSModelXmlType createSpeedGovBPAGSModelXmlType(
			DynamicGeneratorXmlType gen) throws ODMException {
		SpeedGovBPAGSModelXmlType spdgov=getFactory().createSpeedGovBPAGSModelXmlType();
        if(gen.getGovernor().getValue()!=null){
		gen.getGovernor().getValue().setSpeedGov(getFactory().createSpeedGov(spdgov));
		return spdgov;
        }
        else throw new ODMException("Error: Governernor need to be created first!");
		
	}
	//  GI/I+ model is  a regulator, part of a speed Governing model,
	public static SpeedGovBPARegGIModelXmlType createSpeedGovBPARegGIModelXmlType(
			DynamicGeneratorXmlType gen) throws ODMException {
		SpeedGovBPARegGIModelXmlType regGi =getFactory().createSpeedGovBPARegGIModelXmlType();
		//TODO before linking a regulator to a gen,  it need to be first connected to SpeedGov.
		SpeedGovModelXmlType spdGov=null;
		if(gen.getGovernor().getValue().getSpeedGov().getValue()!=null){
			spdGov=gen.getGovernor().getValue().getSpeedGov().getValue();
		
		   if (spdGov instanceof SpeedGovBPAGiGaCombinedXmlType) {
				SpeedGovBPAGiGaCombinedXmlType giGa = (SpeedGovBPAGiGaCombinedXmlType) spdGov;
				giGa.setRegulator(regGi);
			}
		  return regGi;
	    }
		throw new ODMException("Error:SpeedGov not created yet!");

	}

	public static SpeedGovBPAGiGaCombinedXmlType createSpeedGovBPAGiGaCombinedXmlType(
			DynamicGeneratorXmlType gen) {
		SpeedGovBPAGiGaCombinedXmlType spdgov=getFactory().createSpeedGovBPAGiGaCombinedXmlType();
		
        //create a governor when it is none in a generator;
		gen.getGovernor().getValue().setSpeedGov(getFactory().createSpeedGov(spdgov));
		return spdgov;
	}

	
	//GA model is a servo motor model, part of a speed Governing model.
	public static SpeedGovBPAServoGAModelXmlType createSpeedGovBPAServoGAModelXmlType(
			DynamicGeneratorXmlType dynGen) throws ODMException {
		SpeedGovBPAServoGAModelXmlType servoGa =getFactory().createSpeedGovBPAServoGAModelXmlType();
		//TODO before linking a servo to a gen,  it need to be first connected to SpeedGov.
		
		SpeedGovModelXmlType spdGov=null;
		if(dynGen.getGovernor().getValue().getSpeedGov().getValue()!=null){
			spdGov=dynGen.getGovernor().getValue().getSpeedGov().getValue();
		
		   if (spdGov instanceof SpeedGovBPAGiGaCombinedXmlType) {
				SpeedGovBPAGiGaCombinedXmlType giGa = (SpeedGovBPAGiGaCombinedXmlType) spdGov;
				giGa.setServo(servoGa);
			}
		  return servoGa;
	    }
		throw new ODMException("SpeedGov not created yet!");

	}
	public static SteamTurbineBPATBModelXmlType createSteamTurbineBPATBModelXmlType(
			DynamicGeneratorXmlType gen) {
		SteamTurbineBPATBModelXmlType tur=getFactory().createSteamTurbineBPATBModelXmlType();
        //create a governor when it is none in a generator;
		//TODO
		//if(gen.getGovernor()==null)gen.setGovernor(getFactory().createGovernorModelXmlType());
		gen.getGovernor().getValue().setTurbine(getFactory().createTurbine(tur));
		return tur;
	}

	//GS+TB
	public static GovBPAGsTbCombinedModelXmlType createGovBPAGsTbCombinedModelXmlType(
			DynamicGeneratorXmlType gen) {
		GovBPAGsTbCombinedModelXmlType gov=getFactory().createGovBPAGsTbCombinedModelXmlType();
		gen.setGovernor(getFactory().createGovBPAGsTbCombinedModel(gov));
		return gov;
	}
	//GI+GA+TB
	public static GovBPAGiGaTbCombinedModelXmlType createGovBPAGiGaTbCombinedModelXmlType(
			DynamicGeneratorXmlType gen) {
		GovBPAGiGaTbCombinedModelXmlType gov=getFactory().createGovBPAGiGaTbCombinedModelXmlType();
		gen.setGovernor(getFactory().createGovBPAGiGaTbCombinedModel(gov));		
		return gov;
		
	}
    

	/*
	 * PSS model creation functions
	 * ============================
	 */
	
	public static PssSimpleTypeXmlType createPssSimpleTypeXmlType(DynamicGeneratorXmlType gen) {
		PssSimpleTypeXmlType pss = getFactory().createPssSimpleTypeXmlType();
		gen.setStabilizer(getFactory().createPssSimpleType(pss));
		return pss;
	}

	public static PssIEEE1981TypeXmlType createPssIEEE1981TypeXmlType(DynamicGeneratorXmlType gen) {
		PssIEEE1981TypeXmlType pss = getFactory().createPssIEEE1981TypeXmlType();
		gen.setStabilizer(getFactory().createPssIEEE1981Type(pss));
		return pss;
	}

	public static PssIEEE1992Type2AXmlType createPssIEEE1992Type2AXmlType(DynamicGeneratorXmlType gen) {
		PssIEEE1992Type2AXmlType pss = getFactory().createPssIEEE1992Type2AXmlType();
		gen.setStabilizer(getFactory().createPssIEEE1992Type2A(pss));
		return pss;
	}

	public static PssIEEE1AXmlType createPssIEEE1AXmlType(DynamicGeneratorXmlType gen) {
		PssIEEE1AXmlType pss = getFactory().createPssIEEE1AXmlType();
		gen.setStabilizer(getFactory().createPssIEEEType1A(pss));
		return pss;
	}

	public static PssIEE2STXmlType createPssIEE2STXmlType(DynamicGeneratorXmlType gen) {
		PssIEE2STXmlType pss = getFactory().createPssIEE2STXmlType();
		gen.setStabilizer(getFactory().createPssIEE2ST(pss));
		return pss;
	}

	public static PssIEEEDualInputXmlType createPssIEEEDualInputXmlType(DynamicGeneratorXmlType gen) {
		PssIEEEDualInputXmlType pss = getFactory().createPssIEEEDualInputXmlType();
		gen.setStabilizer(getFactory().createPssIEEEDualInput(pss));
		return pss;
	}
	
	public static PssBPADualInputXmlType createPssBPADualInputXmlType(DynamicGeneratorXmlType gen) {
		PssBPADualInputXmlType pss = getFactory().createPssBPADualInputXmlType();
		gen.setStabilizer(getFactory().createPssBPADualInput(pss));
		return pss;
	}





}
