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

import org.ieee.odm.schema.GroundingEnumType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.XformrtConnectionEnumType;

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
