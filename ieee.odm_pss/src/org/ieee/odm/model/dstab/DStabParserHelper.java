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

import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.schema.ClassicMachineXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.Eq11Ed11MachineXmlType;
import org.ieee.odm.schema.Eq11MachineXmlType;
import org.ieee.odm.schema.Eq1Ed1MachineXmlType;
import org.ieee.odm.schema.Eq1MachineXmlType;
import org.ieee.odm.schema.EquiMachineXmlType;
import org.ieee.odm.schema.ExcBPAFJXmlType;
import org.ieee.odm.schema.ExcIEEE1968Type1SXmlType;
import org.ieee.odm.schema.ExcIEEE1968Type1XmlType;
import org.ieee.odm.schema.ExcIEEE1968Type2XmlType;
import org.ieee.odm.schema.ExcIEEE1968Type3XmlType;
import org.ieee.odm.schema.ExcIEEE1968Type4XmlType;
import org.ieee.odm.schema.ExcIEEE1981NewExcSystemXmlType;
import org.ieee.odm.schema.ExcIEEE1981ST1XmlType;
import org.ieee.odm.schema.ExcIEEE1981TypeAC2XmlType;
import org.ieee.odm.schema.ExcIEEE1992TypeAC1AXmlType;
import org.ieee.odm.schema.ExcIEEEModified1968Type1XmlType;
import org.ieee.odm.schema.ExcIEEETypeDC2XmlType;
import org.ieee.odm.schema.ExcSimpleTypeXmlType;
import org.ieee.odm.schema.ExcTSATTypeEXC34XmlType;

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
	
	public static Eq11Ed11MachineXmlType createEq11Ed11MachineXmlType(DynamicGeneratorXmlType gen) {
		Eq11Ed11MachineXmlType mach = getFactory().createEq11Ed11MachineXmlType();
		gen.setMachineModel(getFactory().createEq11Ed11MachModel(mach));
		return mach;
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

	public static ExcIEEE1981ST1XmlType createExcIEEE1981ST1XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1981ST1XmlType exc = getFactory().createExcIEEE1981ST1XmlType();
		gen.setExciter(getFactory().createExcIEEE1981ST1(exc));
		return exc;
	}

	public static ExcIEEE1981NewExcSystemXmlType createExcIEEE1981NewExcSystemXmlType(DynamicGeneratorXmlType gen) {
		ExcIEEE1981NewExcSystemXmlType exc = getFactory().createExcIEEE1981NewExcSystemXmlType();
		gen.setExciter(getFactory().createExcIEEE1981NewExcSystem(exc));
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
	
	public static ExcIEEETypeDC2XmlType createExcIEEETypeDC2XmlType(DynamicGeneratorXmlType gen) {
		ExcIEEETypeDC2XmlType exc = getFactory().createExcIEEETypeDC2XmlType();
		gen.setExciter(getFactory().createExcIEEETypeDC2(exc));
		return exc;
	}

	public static ExcTSATTypeEXC34XmlType createExcTSATTypeEXC34XmlType(DynamicGeneratorXmlType gen) {
		ExcTSATTypeEXC34XmlType exc = getFactory().createExcTSATTypeEXC34XmlType();
		gen.setExciter(getFactory().createExcTSATTypeEXC34(exc));
		return exc;
	}

	public static ExcBPAFJXmlType createExcBPAFJXmlType(DynamicGeneratorXmlType gen) {
		ExcBPAFJXmlType exc = getFactory().createExcBPAFJXmlType();
		gen.setExciter(getFactory().createExcBPAFJ(exc));
		return exc;
	}
}
