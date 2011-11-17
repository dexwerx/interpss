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
	public static enum Type { 
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

	public static Type 
			VUnit = Type.PU, 
			IUnit = Type.PU, 
			PUnit = Type.PU, 
			ZUnit = Type.PU,
			YUnit = Type.PU;

	/**
	 * Check if the unit is a load or generation unit (PU|VA|Var|Watt|kW|kVA|kVar|mW|mVA|mVar)
	 *
	 * @param unit the P/Q unit
	 * @return true or false
	 */
	public static boolean isPQUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.VA) || (unit == Type.kVA) || (unit == Type.mVA)
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
	public static boolean isPUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.Watt) || (unit == Type.kW) || (unit == Type.mW) ||
		   		(unit == Type.VA) || (unit == Type.kVA) || (unit == Type.mVA);
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
	public static boolean isQUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.Var) || (unit == Type.kVar)
				|| (unit == Type.mVar);
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
	public static boolean isYUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.Mho)
				|| (unit == Type.MicroMho);
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
	public static boolean isZUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.Ohm)
				|| (unit == Type.MilliOhm) || (unit == Type.kOhm);
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
	public static boolean isVUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.Volt)
				|| (unit == Type.kV);
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
	public static boolean isAmpUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.Amp)
				|| (unit == Type.kAmp);
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
	public static boolean isAngleUnit(final Type unit) {
		return (unit == Type.Rad) || (unit == Type.Deg);
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
	public static boolean isTapUnit(final Type unit) {
		return (unit == Type.PU) || (unit == Type.Percent);
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
	public static String toString(final Type u) {
		if (u == Type.Percent) {
			return "%";
		}
		else if (u == Type.PU) {
			return "pu";
		}
		else if (u == Type.Deg) {
			return "deg";
		}
		else if (u == Type.Rad) {
			return "rad";
		}
		else if (u == Type.Volt) {
			return "V";
		}
		else if (u == Type.kV) {
			return "KV";
		}
		else if (u == Type.kAmp) {
			return "KAmps";
		}
		else if (u == Type.Amp) {
			return "Amps";
		}
		else if (u == Type.MilliAmp) {
			return "mA";
		}
		else if (u == Type.Watt) {
			return "W";
		}
		else if (u == Type.kW) {
			return "KW";
		}
		else if (u == Type.mW) {
			return "MW";
		}
		else if (u == Type.HP) {
			return "HP";
		}
		else if (u == Type.Var) {
			return "Var";
		}
		else if (u == Type.kVar) {
			return "KVar";
		}
		else if (u == Type.mVar) {
			return "MVar";
		}
		else if (u == Type.VA) {
			return "VA";
		}
		else if (u == Type.kVA) {
			return "KVA";
		}
		else if (u == Type.mVA) {
			return "MVA";
		}
		else if (u == Type.Ohm) {
			return "Ohms";
		}
		else if (u == Type.MilliOhm) {
			return "mOhms";
		}
		else if (u == Type.kOhm) {
			return "kOhms";
		}
		else if (u == Type.Mho) {
			return "Mhos";
		}
		else if (u == Type.MicroMho) {
			return "microMhos";
		}
		else if (u == Type.Henry) {
			return "Henry";
		}
		else if (u == Type.MilliH) {
			return "mH";
		}
		else if (u == Type.MicroH) {
			return "microH";
		}
		else if (u == Type.Farad) {
			return "F";
		}
		else if (u == Type.MilliF) {
			return "mF";
		}
		else if (u == Type.MicroF) {
			return "microF";
		}
		else if (u == Type.Year) {
			return "Year";
		}
		else if (u == Type.Month) {
			return "Month";
		}
		else if (u == Type.Day) {
			return "Day";
		}
		else if (u == Type.Hour) {
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
	public static Type toUnit(final String unit) {
		if ((unit.compareTo("%") == 0) || unit.toLowerCase().equals("percent")) {
			return Type.Percent;
		}
		else if (unit.toLowerCase().equals("pu")) {
			return Type.PU;
		}
		else if (unit.toLowerCase().equals("deg")) {
			return Type.Deg;
		}
		else if (unit.toLowerCase().equals("rad")) {
			return Type.Rad;
		}
		else if (unit.toLowerCase().equals("v") || unit.toLowerCase().equals("volt")) {
			return Type.Volt;
		}
		else if (unit.toLowerCase().equals("kv") || (unit.compareTo("kVolt") == 0)) {
			return Type.kV;
		}
		else if (unit.toLowerCase().equals("kamps")
				|| unit.toLowerCase().equals("kamp")
				|| unit.toLowerCase().equals("va")) {
			return Type.kAmp;
		}
		else if (unit.toLowerCase().equals("amps")
				|| unit.toLowerCase().equals("amp")
				|| unit.toLowerCase().equals("a")) {
			return Type.Amp;
		}
		else if (unit.toLowerCase().equals("ma")
				|| unit.toLowerCase().equals("milliamps")) {
			return Type.MilliAmp;
		}
		else if (unit.toLowerCase().equals("w") || unit.toLowerCase().equals("watt")) {
			return Type.Watt;
		}
		else if (unit.toLowerCase().equals("kw")
				|| unit.toLowerCase().equals("kwatt")) {
			return Type.kW;
		}
		else if (unit.toLowerCase().equals("HP")
				|| unit.toLowerCase().equals("hp")
				|| unit.toLowerCase().equals("Hp")) {
			return Type.HP;
		}
		else if (unit.toLowerCase().equals("mw")
				|| unit.toLowerCase().equals("mwatt")) {
			return Type.mW;
		}
		else if (unit.toLowerCase().equals("var")) {
			return Type.Var;
		}
		else if (unit.toLowerCase().equals("kvar")) {
			return Type.kVar;
		}
		else if (unit.toLowerCase().equals("mvar")) {
			return Type.mVar;
		}
		else if (unit.toLowerCase().equals("va")) {
			return Type.VA;
		}
		else if (unit.toLowerCase().equals("kva")) {
			return Type.kVA;
		}
		else if (unit.toLowerCase().equals("mva")) {
			return Type.mVA;
		}
		else if (unit.toLowerCase().equals("ohm")
				|| unit.toLowerCase().equals("ohms")) {
			return Type.Ohm;
		}
		else if (unit.toLowerCase().equals("mohm")
				|| unit.toLowerCase().equals("mohms")) {
			return Type.MilliOhm;
		}
		else if (unit.toLowerCase().equals("kohm")
				|| unit.toLowerCase().equals("Kohm")) {
			return Type.kOhm;
		}
		else if (unit.toLowerCase().equals("mho")
				|| unit.toLowerCase().equals("mhos")) {
			return Type.Mho;
		}
		else if (unit.toLowerCase().equals("micromho")
				|| unit.toLowerCase().equals("micromhos")) {
			return Type.MicroMho;
		}
		else if (unit.toLowerCase().equals("h")
				|| unit.toLowerCase().equals("henry")) {
			return Type.Henry;
		}
		else if (unit.toLowerCase().equals("mh")) {
			return Type.MilliH;
		}
		else if (unit.toLowerCase().equals("microh")) {
			return Type.MicroH;
		}
		else if (unit.toLowerCase().equals("f")
				|| unit.toLowerCase().equals("farad")) {
			return Type.Farad;
		}
		else if (unit.toLowerCase().equals("mf")) {
			return Type.MilliF;
		}
		else if (unit.toLowerCase().equals("microf")) {
			return Type.MicroF;
		}
		else if ("year".equals(unit.toLowerCase())) {
			return Type.Year;
		}
		else if ("month".equals(unit.toLowerCase())) {
			return Type.Month;
		}
		else if ("day".equals(unit.toLowerCase())) {
			return Type.Day;
		}
		else if ("hour".equals(unit.toLowerCase())) {
			return Type.Hour;
		}
		return Type.PU;
	}
}