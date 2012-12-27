/*
 * @(#)UnitType.java   
 *
 * Copyright (C) 2006-2011 www.interpss.com
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

package org.interpss.numeric.datatype;

/**
 * Units class for unit conversion
 *
 */

public class Unit {
	/**
	 * unit type
	 * 
	 * @author mzhou
	 *
	 */
	public static enum UnitType { 
			Percent, PU, 
			Deg, Rad, 
			Volt, kV, 
			kAmp, Amp, MilliAmp,
			Watt, kW, mW, HP,
			Var, kVar, mVar,
			VA, kVA, mVA, 
			Ohm, kOhm, MilliOhm, OhmPerFt, OhmPerM,
			Mho, MicroMho, 
			Henry, MilliH, MicroH,
			Farad, MilliF, MicroF, 
			Ft, M, kM, Mile, // meter
			Hour, Day, Month, Year }

	/**
	 * default unit type 
	 */
	public static UnitType 
			VUnit = UnitType.PU, 
			IUnit = UnitType.PU, 
			PUnit = UnitType.PU, 
			ZUnit = UnitType.PU,
			YUnit = UnitType.PU;

	/**
	 * Check if the unit is a load or generation unit (PU|VA|Var|Watt|kW|kVA|kVar|mW|mVA|mVar)
	 *
	 * @param unit the P/Q unit
	 * @return true or false
	 */
	public static boolean isPQUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.VA) || (unit == UnitType.kVA) || (unit == UnitType.mVA)
				|| isPUnit(unit) || isQUnit(unit);
	}

	/**
	 * Check if the unit is a load or generation unit (PU|VA|Var|Watt|kW|kVA|kVar|mW|mVA|mVar)
	 *
	 * @param unit the P/Q unit
	 * @return true or false
	 */
	public static boolean isPQUnit(final String unit) {
		return unit.toLowerCase().equals("va")
				|| unit.toLowerCase().equals("kva")
				|| unit.toLowerCase().equals("mva") || isPUnit(unit)
				|| isQUnit(unit);
	}

	/**
	 * Check if the unit is a P unit (PU|Watt|kW|mW)
	 *
	 * @param unit the P unit
	 * @return true or false
	 */
	public static boolean isPUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.Watt) || (unit == UnitType.kW) || (unit == UnitType.mW) ||
		   		(unit == UnitType.VA) || (unit == UnitType.kVA) || (unit == UnitType.mVA);
	}

	/**
	 * Check if the unit is a P unit (PU|Watt|kW|mW)
	 *
	 * @param unit the P unit
	 * @return true or false
	 */
	public static boolean isPUnit(final String unit) {
		return unit.toLowerCase().equals("pu")
				|| unit.toLowerCase().equals("watt")
				|| unit.toLowerCase().equals("kw")
				|| unit.toLowerCase().equals("mw");
	}

	/**
	 * Check if the unit is a Q unit (PU|Var|kVar|mVar)
	 *
	 * @param unit the Q unit
	 * @return true or false	 
	 */
	public static boolean isQUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.Var) || (unit == UnitType.kVar)
				|| (unit == UnitType.mVar);
	}

	/**
	 * Check if the unit is a Q unit (PU|Var|kVar|mVar)
	 *
	 * @param unit the Q unit
	 * @return true or false	 
	 */
	public static boolean isQUnit(final String unit) {
		return unit.toLowerCase().equals("pu")
				|| unit.toLowerCase().equals("var")
				|| unit.toLowerCase().equals("kvar")
				|| unit.toLowerCase().equals("mvar");
	}

	/**
	 * Check if the unit is a Y unit (PU|Mho|MicroMho)
	 *
	 * @param unit the Y unit
	 * @return true or false	 
	 */
	public static boolean isYUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.Mho)
				|| (unit == UnitType.MicroMho);
	}

	/**
	 * Check if the unit is a Y unit (PU|Mho|MicroMho)
	 *
	 * @param unit the Y unit
	 * @return true or false	 
	 */
	public static boolean isYUnit(final String unit) {
		return unit.toLowerCase().equals("pu")
				|| unit.toLowerCase().equals("mho")
				|| unit.toLowerCase().equals("micromho")
				|| unit.toLowerCase().equals("micromhos");
	}

	/**
	 * Check if the unit is a Z unit (PU|Ohm|kOhm)
	 *
	 * @param unit the Z unit
	 * @return true or false	 
	 */
	public static boolean isZUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.Ohm)
				|| (unit == UnitType.MilliOhm) || (unit == UnitType.kOhm);
	}

	/**
	 * Check if the unit is a Z unit (PU|Ohm|MilliOhm|kOhm)
	 *
	 * @param unit the Z unit
	 * @return true or false	 
	 */
	public static boolean isZUnit(final String unit) {
		return unit.toLowerCase().equals("pu")
				|| unit.toLowerCase().equals("ohm")
				|| unit.toLowerCase().equals("milliohm")
				|| unit.toLowerCase().equals("kohm")
				|| unit.toLowerCase().equals("%")
				|| unit.toLowerCase().equals("percent");
	}

	/**
	 * Check if the unit is a V unit (PU|Volt|KV)
	 *
	 * @param unit the V unit
	 * @return true or false	 
	 */
	public static boolean isVUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.Volt)
				|| (unit == UnitType.kV);
	}

	/**
	 * Check if the unit is a V unit (PU|Volt|KV)
	 *
	 * @param unit the V unit
	 * @return true or false	 
	 */
	public static boolean isVUnit(final String unit) {
		return unit.toLowerCase().equals("pu")
				|| unit.toLowerCase().equals("volt")
				|| unit.toLowerCase().equals("kv");
	}

	/**
	 * Check if the unit is a Amp unit (PU|Amp|KAmp)
	 *
	 * @param unit the Amp unit
	 * @return true or false	 
	 */
	public static boolean isAmpUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.Amp)
				|| (unit == UnitType.kAmp);
	}

	/**
	 * Check if the unit is a Amp unit (PU|Amp|KAmp)
	 *
	 * @param unit the Amp unit
	 * @return true or false	 
	 */
	public static boolean isAmpUnit(final String unit) {
		return unit.toLowerCase().equals("pu")
				|| unit.toLowerCase().equals("amp")
				|| unit.toLowerCase().equals("kamp");
	}

	/**
	 * Check if the unit is Angle unit (Rad|Deg)
	 *
	 * @param unit the angle unit
	 * @return true or false	 
	 */
	public static boolean isAngleUnit(final UnitType unit) {
		return (unit == UnitType.Rad) || (unit == UnitType.Deg);
	}

	/**
	 * Check if the unit is Angle unit (Rad|Deg)
	 *
	 * @param unit the angle unit
	 * @return true or false	 
	 */
	public static boolean isAngleUnit(final String unit) {
		return unit.toLowerCase().equals("rad")
				|| unit.toLowerCase().equals("deg");
	}

	/**
	 * Check if the unit is Angle unit (PU|Percent)
	 *
	 * @param unit the angle unit
	 * @return true or false	 
	 */
	public static boolean isTapUnit(final UnitType unit) {
		return (unit == UnitType.PU) || (unit == UnitType.Percent);
	}

	/**
	 * Check if the unit is Angle unit (PU|%|Percent)
	 *
	 * @param unit the angle unit
	 * @return true or false	 
	 */
	public static boolean isTapUnit(final String unit) {
		return unit.toLowerCase().equals("pu")
				|| unit.toLowerCase().equals("%")
				|| unit.toLowerCase().equals("percent");
	}

	/**
	 * Get default current unit string
	 *
	 * @return the unit string
	 */
	public static String iUnitStr() {
		return toString(IUnit);
	}

	/**
	 * Get default voltage unit string
	 *
	 * @return the unit string
	 */
	public static String vUnitStr() {
		return toString(VUnit);
	}

	/**
	 * Get default y unit string
	 *
	 * @return the unit string
	 */
	public static String yUnitStr() {
		return toString(YUnit);
	}

	/**
	 * Get default z unit string
	 *
	 * @return the unit string
	 */
	public static String zUnitStr() {
		return toString(ZUnit);
	}

	/**
	 * Get default power unit string
	 *
	 * @return the unit string
	 */
	public static String pUnitStr() {
		return toString(PUnit);
	}

	/**
	 * Convert the unit to a string
	 *
	 * @param u unit to be converted
	 * @return the unit string
	 */
	public static String toString(final UnitType u) {
		if (u == UnitType.Percent) {
			return "%";
		}
		else if (u == UnitType.PU) {
			return "pu";
		}
		else if (u == UnitType.Deg) {
			return "deg";
		}
		else if (u == UnitType.Rad) {
			return "rad";
		}
		else if (u == UnitType.Volt) {
			return "V";
		}
		else if (u == UnitType.kV) {
			return "KV";
		}
		else if (u == UnitType.kAmp) {
			return "KAmps";
		}
		else if (u == UnitType.Amp) {
			return "Amps";
		}
		else if (u == UnitType.MilliAmp) {
			return "mA";
		}
		else if (u == UnitType.Watt) {
			return "W";
		}
		else if (u == UnitType.kW) {
			return "KW";
		}
		else if (u == UnitType.mW) {
			return "MW";
		}
		else if (u == UnitType.HP) {
			return "HP";
		}
		else if (u == UnitType.Var) {
			return "Var";
		}
		else if (u == UnitType.kVar) {
			return "KVar";
		}
		else if (u == UnitType.mVar) {
			return "MVar";
		}
		else if (u == UnitType.VA) {
			return "VA";
		}
		else if (u == UnitType.kVA) {
			return "KVA";
		}
		else if (u == UnitType.mVA) {
			return "MVA";
		}
		else if (u == UnitType.Ohm) {
			return "Ohms";
		}
		else if (u == UnitType.MilliOhm) {
			return "mOhms";
		}
		else if (u == UnitType.kOhm) {
			return "kOhms";
		}
		else if (u == UnitType.Mho) {
			return "Mhos";
		}
		else if (u == UnitType.MicroMho) {
			return "microMhos";
		}
		else if (u == UnitType.Henry) {
			return "Henry";
		}
		else if (u == UnitType.MilliH) {
			return "mH";
		}
		else if (u == UnitType.MicroH) {
			return "microH";
		}
		else if (u == UnitType.Farad) {
			return "F";
		}
		else if (u == UnitType.MilliF) {
			return "mF";
		}
		else if (u == UnitType.MicroF) {
			return "microF";
		}
		else if (u == UnitType.Year) {
			return "Year";
		}
		else if (u == UnitType.Month) {
			return "Month";
		}
		else if (u == UnitType.Day) {
			return "Day";
		}
		else if (u == UnitType.Hour) {
			return "Hour";
		}
		return "WrongUnit";
	}

	/**
	 * Convert a unit string to a unit constant
	 *
	 * @param unit unit string to be converted
	 * @return the unit constant
	 */
	public static UnitType toUnit(final String unit) {
		switch (unit.toLowerCase()) {
			case "%" :
			case "percent" :	
				return UnitType.Percent;
			case "pu" :
				return UnitType.PU;	
			case "deg" :
				return UnitType.Deg;
			case "rad" :
				return UnitType.Rad;
			case "v" :
			case "volt" :
				return UnitType.Volt;	
			case "kv" : 
			case "kVolt" : 
				return UnitType.kV;
			case "kamps" :
			case "kamp" :
			case "ka" :
				return UnitType.kAmp;
			case "amps" :
			case "amp" :
			case "a" :
				return UnitType.Amp;
			case "ma" : 
			case "milliamps" :
				return UnitType.MilliAmp;
			case "w" :
			case "watt" : 
				return UnitType.Watt;
			case "kw" :
			case "kwatt" :
				return UnitType.kW;
			case "hp" :
				return UnitType.HP;
			case "mw" :
			case "mwatt" :
				return UnitType.mW;
			case "var" :
				return UnitType.Var;
			case "kvar" :
				return UnitType.kVar;
			case "mvar" :
				return UnitType.mVar;
			case "va" :
				return UnitType.VA;
			case "kva" :
				return UnitType.kVA;
			case "mva" :
				return UnitType.mVA;
			case "ohm" :
			case "ohms" :
				return UnitType.Ohm;
			case "mohm" :
			case "mohms" :
				return UnitType.MilliOhm;
			case "kohm" :
			case "Kohm" :
				return UnitType.kOhm;
			case "mho" :
			case "mhos" :
				return UnitType.Mho;
			case "micromho" :
			case "micromhos" :
				return UnitType.MicroMho;
			case "h" :
			case "henry" :
				return UnitType.Henry;
			case "mh" :
				return UnitType.MilliH;
			case "microh" :
				return UnitType.MicroH;
			case "f" :
			case "farad" :
				return UnitType.Farad;
			case "mf" :
				return UnitType.MilliF;
			case "microf" :
				return UnitType.MicroF;
			case "year" :
				return UnitType.Year;
			case "month" :
				return UnitType.Month;
			case "day" :
				return UnitType.Day;
			case "hour" :
				return UnitType.Hour;
			default :
				return UnitType.PU;				
		}
	}
}