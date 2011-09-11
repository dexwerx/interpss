/*
 * @(#)ODMXmlHelper.java   
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
import org.ieee.odm.schema.GroundingEnumType;
import org.ieee.odm.schema.LengthUnitType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.XformrtConnectionEnumType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

import com.interpss.common.datatype.UnitType;
import com.interpss.core.acsc.BusGroundCode;
import com.interpss.core.acsc.XFormerConnectCode;
import com.interpss.core.net.OriginalDataFormat;


public class ODMXmlHelper {
	static String PSSStudyCaseHead = "<PSSStudyCase xmlns=\"http://www.ieee.org/odm/Schema/2008\">";
	static String PSSStudyCaseEnd = "</PSSStudyCase>";
	
	static String BusListHead = PSSStudyCaseHead + "<baseCase><busList>";
	static String BusListEnd = "</busList></baseCase>" + PSSStudyCaseEnd; 

	static String BranchListHead = PSSStudyCaseHead + "<baseCase><branchList>";
	static String BranchListEnd = "</branchList></baseCase>" + PSSStudyCaseEnd; 
	
	/**
	 * warp the base case xml string for parsing
	 * 
	 * @param str
	 * @return
	 */
	public static String wrapBaseCase(String str) {
		return PSSStudyCaseHead + str + PSSStudyCaseEnd;
	}

	/**
	 * warp the bus list xml string for parsing
	 * 
	 * @param str
	 * @return
	 */
	public static String wrapBusList(String str) {
		return BusListHead + str + BusListEnd;
	}

	/**
	 * warp the branch list xml string for parsing
	 * 
	 * @param str
	 * @return
	 */
	public static String wrapBranchList(String str) {
		return BranchListHead + str + BranchListEnd;
	}
	
	/**
	 * convert XML power unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static byte toApparentPowerUnit(ApparentPowerUnitType unit) {
		if (unit == ApparentPowerUnitType.KVA)
			return UnitType.kVA;
		else if (unit == ApparentPowerUnitType.MVA)
			return UnitType.mVA;
		return UnitType.PU;
	}

	public static byte toActivePowerUnit(ActivePowerUnitType unit) {
		if (unit == ActivePowerUnitType.KW)
			return UnitType.kW;
		else if (unit == ActivePowerUnitType.MW)
			return UnitType.mW;
		return UnitType.PU;
	}

	public static byte toReactivePowerUnit(ReactivePowerUnitType unit) {
		if (unit == ReactivePowerUnitType.KVAR)
			return UnitType.kVar;
		else if (unit == ReactivePowerUnitType.MVAR)
			return UnitType.mVar;
		return UnitType.PU;
	}

	public static byte toZUnit(ZUnitType unit) {
		if (unit == ZUnitType.OHM)
			return UnitType.Ohm;
		else if (unit == ZUnitType.OHM_PER_FT)
			return UnitType.OhmPerFt;
		else if (unit == ZUnitType.OHM_PER_M)
			return UnitType.OhmPerM;
		else if (unit == ZUnitType.PERCENT)
			return UnitType.Percent;
		else if (unit == ZUnitType.MVA)
			return UnitType.mVA;
		else if (unit == ZUnitType.KVA)
			return UnitType.kVA;
		return UnitType.PU;
	}
  
	/**
	 * convert XML Y unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static byte toYUnit(YUnitType unit) {
		if (unit == YUnitType.MHO)
			return UnitType.Mho;
		else if (unit == YUnitType.MICROMHO)
			return UnitType.MicroMho;
		else if (unit == YUnitType.MVAR)
			return UnitType.mVar;
		else if (unit == YUnitType.KVAR)
			return UnitType.kVar;
		return UnitType.PU;
	}

	/**
	 * convert XML voltage unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static byte toVoltageUnit(VoltageUnitType unit) {
		if (unit == VoltageUnitType.VOLT)
			return UnitType.Volt;
		else if (unit == VoltageUnitType.KV)
			return UnitType.kV;
		return UnitType.PU;
	}

	/**
	 * convert XML current unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static byte toCurrentUnit(CurrentUnitType unit) {
		if (unit == CurrentUnitType.AMP)
			return UnitType.Amp;
		else if (unit == CurrentUnitType.KA)
			return UnitType.kAmp;
		return UnitType.PU;
	}
	
	/**
	 * convert XML angle unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static byte toAngleUnit(AngleUnitType unit) {
		if (unit == AngleUnitType.DEG)
			return UnitType.Deg;
		return UnitType.Rad;
	}
	
	/**
	 * convert XML factor unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static byte toFactorUnit(FactorUnitType unit) {
		if (unit == FactorUnitType.PERCENT)
			return UnitType.Percent;
		return UnitType.PU;
	}	
	
	/**
	 * convert XML length unit to Ipss UnitType
	 * 
	 * @param unit power unit 
	 * @return
	 */
	public static byte toLengthUnit(LengthUnitType unit) {
		if (unit == LengthUnitType.FT)
			return UnitType.Ft;
		else if (unit == LengthUnitType.M)
			return UnitType.M;
		else if (unit == LengthUnitType.KM)
			return UnitType.kM;
		else if (unit == LengthUnitType.MILE)
			return UnitType.Mile;
		return UnitType.Ft;
	}		

	/**
	 * Convert GroundingEnumType (ODM) to BusGroundCode (Interpss)
	 * 
	 * @param type
	 * @return
	 */
	public static BusGroundCode toBusGroundCode(GroundingEnumType type) {
		if (type == GroundingEnumType.Z_GROUNDED) 
			return BusGroundCode.ZGROUNDED;
		else if (type == GroundingEnumType.SOLID_GROUNDED)
			return BusGroundCode.SOLID_GROUNDED;
		else	
			return BusGroundCode.UNGROUNDED;
	}	

	/**
	 * Convert XformrtConnectionEnumType (ODM) to XFormerConnectCode (Interpss)
	 * 
	 * @param type
	 * @return
	 */
	public static XFormerConnectCode toXfrConnectCode(XformrtConnectionEnumType type) {
		if (type == XformrtConnectionEnumType.DELTA) 
			return XFormerConnectCode.DELTA;
		else	
			return XFormerConnectCode.WYE;
	}	

	/**
	 * map the ODM data format to InterPSS format
	 * 
	 * @param ofmt
	 * @return
	 */
	public static OriginalDataFormat map(OriginalDataFormatEnumType ofmt) {
		return ofmt == OriginalDataFormatEnumType.IEEE_CDF? OriginalDataFormat.IEEECDF :
					(ofmt == OriginalDataFormatEnumType.CIM? OriginalDataFormat.CIM :
						(ofmt == OriginalDataFormatEnumType.PSS_E? OriginalDataFormat.PSSE :
							(ofmt == OriginalDataFormatEnumType.UCTE_DEF? OriginalDataFormat.UCTE :
								(ofmt == OriginalDataFormatEnumType.GE_PSLF? OriginalDataFormat.GE_PSLF :
									(ofmt == OriginalDataFormatEnumType.BPA? OriginalDataFormat.BPA :
										OriginalDataFormat.IPSS_EDITOR)))));		
	}
}
