/*
 * @(#)ODMUnitHelper.java   
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
 * @Date 02/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.odm;

import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.CurrentUnitType;
import org.ieee.odm.schema.FactorUnitType;
import org.ieee.odm.schema.LengthUnitType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

import org.interpss.numeric.datatype.Unit.Type;


public class ODMUnitHelper {
	/**
	 * convert XML power unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static Type toApparentPowerUnit(ApparentPowerUnitType unit) {
		if (unit == ApparentPowerUnitType.KVA)
			return Type.kVA;
		else if (unit == ApparentPowerUnitType.MVA)
			return Type.mVA;
		return Type.PU;
	}

	public static Type toActivePowerUnit(ActivePowerUnitType unit) {
		if (unit == ActivePowerUnitType.KW)
			return Type.kW;
		else if (unit == ActivePowerUnitType.MW)
			return Type.mW;
		return Type.PU;
	}

	public static Type toReactivePowerUnit(ReactivePowerUnitType unit) {
		if (unit == ReactivePowerUnitType.KVAR)
			return Type.kVar;
		else if (unit == ReactivePowerUnitType.MVAR)
			return Type.mVar;
		return Type.PU;
	}

	public static Type toZUnit(ZUnitType unit) {
		if (unit == ZUnitType.OHM)
			return Type.Ohm;
		else if (unit == ZUnitType.OHM_PER_FT)
			return Type.OhmPerFt;
		else if (unit == ZUnitType.OHM_PER_M)
			return Type.OhmPerM;
		else if (unit == ZUnitType.PERCENT)
			return Type.Percent;
		else if (unit == ZUnitType.MVA)
			return Type.mVA;
		else if (unit == ZUnitType.KVA)
			return Type.kVA;
		return Type.PU;
	}
  
	/**
	 * convert XML Y unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static Type toYUnit(YUnitType unit) {
		if (unit == YUnitType.MHO)
			return Type.Mho;
		else if (unit == YUnitType.MICROMHO)
			return Type.MicroMho;
		else if (unit == YUnitType.MVAR)
			return Type.mVar;
		else if (unit == YUnitType.KVAR)
			return Type.kVar;
		return Type.PU;
	}

	/**
	 * convert XML voltage unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static Type toVoltageUnit(VoltageUnitType unit) {
		if (unit == VoltageUnitType.VOLT)
			return Type.Volt;
		else if (unit == VoltageUnitType.KV)
			return Type.kV;
		return Type.PU;
	}

	/**
	 * convert XML current unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static Type toCurrentUnit(CurrentUnitType unit) {
		if (unit == CurrentUnitType.AMP)
			return Type.Amp;
		else if (unit == CurrentUnitType.KA)
			return Type.kAmp;
		return Type.PU;
	}
	
	/**
	 * convert XML angle unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static Type toAngleUnit(AngleUnitType unit) {
		if (unit == AngleUnitType.DEG)
			return Type.Deg;
		return Type.Rad;
	}
	
	/**
	 * convert XML factor unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static Type toFactorUnit(FactorUnitType unit) {
		if (unit == FactorUnitType.PERCENT)
			return Type.Percent;
		return Type.PU;
	}	
	
	/**
	 * convert XML length unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static Type toLengthUnit(LengthUnitType unit) {
		if (unit == LengthUnitType.FT)
			return Type.Ft;
		else if (unit == LengthUnitType.M)
			return Type.M;
		else if (unit == LengthUnitType.KM)
			return Type.kM;
		else if (unit == LengthUnitType.MILE)
			return Type.Mile;
		return Type.Ft;
	}		
}
