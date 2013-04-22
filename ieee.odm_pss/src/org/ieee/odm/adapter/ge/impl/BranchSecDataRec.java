 /*
  * @(#)BranchSecDataRec.java   
  *
  * Copyright (C) 2006-2008 www.interpss.org
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
  * @Date 06/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.adapter.ge.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.ge.GePslfVersion;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LengthUnitType;
import org.ieee.odm.schema.LineBranchInfoXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class BranchSecDataRec extends BaseBranchDataRec {
	public double r, x, b;
	public double al, l_info, gi, tf, tt;
	public int type = 0;   //Type of line 0=normal line, 1=Xfr

	/*
      	1 "P-1     " 380.00       2 "P-2     " 380.00 "1 "  1 "        " :  1 0.00000 0.02348 0.00000    
      	0.0    0.0    0.0    0.0 1.000    1.0 /
  		1 201 0.0000 0.000 1.000   400101   391231   0 1  0    0.0    0.0    0.0    
  		0.0   1 1.000   0 1.000   0 1.000   0 1.000   0 0.000   0 0.000   0 0.000   0 0.000  0
	 */
	public BranchSecDataRec(String lineStr, GePslfVersion version, AclfModelParser parser) {
		//PSSNetworkXmlType baseCaseNet = parser.getBaseCase();
		//System.out.println("branch sec->" + lineStr);

		String str1 = lineStr.substring(0, lineStr.indexOf(':')),
	           str2 = lineStr.substring(lineStr.indexOf(':')+1);
		
		this.setBranchHeaderData(str1);
/*
		public int f_bus, t_bus, sec;
		public String f_name, t_name, ck, long_id;
		public double f_bkv, t_bkv;
*/		
		final String fid = AclfModelParser.BusIdPreFix + f_bus;
		final String tid = AclfModelParser.BusIdPreFix + t_bus;
		final String cId = ck.replace(' ', '_');
		LineBranchXmlType branchRec = null;
		try {
			branchRec = parser.createLineBranch(fid, tid, cId);
		} catch (Exception e) {
			ODMLogger.getLogger().severe("branch data error, " + e.toString());
		}
		
//		LineBranchInfoXmlType branchInfo = branchRec.getLineInfo(); 
//		branchRec.getLoadflowData().add(branchData);
//		branchData.setCode(LFBranchCodeEnumType.LINE);

/*
		<r> Branch section resistance (pu)
		<x> Branch section reactance (pu)
		<b> Branch section susceptance (pu)
		<r1> First Branch Rating (MVA)
		<r2> Second Branch Rating (MVA)
		<r3> Third Branch Rating (MVA)
 		<ohms> Ohmic data flag
			1 if impedances in ohms
			0 if in per unit
*/		
		StringTokenizer st = new StringTokenizer(str2);
		if (st.hasMoreElements())
			this.st = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.r = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.x = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.b = new Double(st.nextToken()).doubleValue();

		// <r1> <r2> <r3> <r4>
		this.setRMvaAry1_4(st);
		
		if (st.hasMoreElements())
			this.al = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.l_info = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.ar = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.z = new Integer(st.nextToken()).intValue();
/*
		<gi> Transformer magnetizing (line terminated transformers)
		<tf> Transformer "from" bus tap (line terminated transformers)
		<tt> Transformer "to" bus tap (line terminated transformers)
 */		
		if (st.hasMoreElements())
			this.gi = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.tf = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.tt = new Double(st.nextToken()).doubleValue();

		// <d_in> <d_out> <proj id>
		this.setDates(st);

		if (st.hasMoreElements())
			this.nst = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.type = new Integer(st.nextToken()).intValue();

		// <r5> <r6> <r7> <r8>
		this.setRMvaAry5_8(st);
		
		// <o1> <p1> <o2> <p2> <o3> <p3> <o4> <p4> <o5> <p5> <o6> <p6> <o7> <p7> <o8> <p8>
		this.setOwnerAry(st);
		
		if (st.hasMoreElements())
			this.ohms =	new Integer(st.nextToken()).intValue();	
		
		
		/*
		<r> Branch section resistance (pu)
		<x> Branch section reactance (pu)
		<b> Branch section susceptance (pu)
		<r1> First Branch Rating (MVA)
		<r2> Second Branch Rating (MVA)
		<r3> Third Branch Rating (MVA)
 		<ohms> Ohmic data flag
			1 if impedances in ohms
			0 if in per unit
*/	
		branchRec.setOffLine(this.st == 0);
		branchRec.setNormalOffLineStatus(this.nst == 0);
		
		if (this.ohms == 0) 
			AclfDataSetter.setLineData(branchRec, r, x,
					ZUnitType.PU, 0.0, b, YUnitType.PU);			
		else
			AclfDataSetter.setLineData(branchRec, r, x,
					ZUnitType.OHM, 0.0, b, YUnitType.MHO);
		
		AclfDataSetter.setBranchRatingLimitData(branchRec.getRatingLimit(), r_mvaAry, ApparentPowerUnitType.MVA);
		
		/*
		 */
		branchRec.setAreaNumber(ar);
		branchRec.setZoneNumber(z);
		LineBranchInfoXmlType lineInfo = branchRec.getLineInfo();
		lineInfo.setLength(odmObjFactory.createLengthXmlType());
		lineInfo.getLength().setValue(l_info);
		lineInfo.getLength().setUnit(LengthUnitType.MILE);
		
		BaseJaxbHelper.setBranchOwnership(branchRec, oAry, pAry);
	}
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "r, x, b, type: " + st + ", " + r + ", " + x + ", " + b + ", " + type + "\n";
		str += "al, l_info, gi, tf, tt: " + al + ", " + l_info + ", " + gi + ", " + tf + ", " + tt + "\n";
		return str;
	}	
}
