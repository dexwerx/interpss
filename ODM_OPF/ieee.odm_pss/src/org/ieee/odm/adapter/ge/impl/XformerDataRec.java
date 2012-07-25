 /*
  * @(#)XformerDataRec.java   
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

import java.util.StringTokenizer;

import org.ieee.odm.adapter.ge.GE_PSLF_Adapter;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.TransformerInfoXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class XformerDataRec extends BaseBranchDataRec {
	public int type, kregBus, zt, iintBus, tertBus;
	public double kregBkv, iintBkv, tertBkv, tbase, zpsr;
	public String kregName, iintName, tertName;
	public double zpsx, zptr, zptx, ztsr, ztsx, vnomp, vnoms, vnomt, anglp, gmag, bmag;
	public double aloss, tmax, tmin, vtmax, vtmin, stepp, tapp, tapfp, tapfs, tapft;
	public double tbasept, tbasets, angls, anglt;
	public double rs1, rs2, rs3, rt1, rt2, rt3, alosss, alosst;

	public XformerDataRec(String lineStr, GE_PSLF_Adapter.VersionNo version, AclfModelParser parser) {
		//PSSNetworkXmlType baseCaseNet = parser.getBaseCase();
		/*
		<f bus> <"f name"> <f bkv> <t bus> <"t name"> <t bkv> <"ck"> <"long id">:
		<st> <type> <kreg bus> <"kreg name"> <kreg bkv> <zt> <iint bus> <"iint name"> /
		<iint bkv> <tert bus> <"tert name"> <tert bkv> <area> <zone> <tbase> <zpsr> /
		<zpsx> <zptr> <zptx> <ztsr> <ztsx> <vnomp> <vnoms> <vnomt> <anglp> <gmag> /
		<bmag> <r1> <r2> <r3> <r4> <aloss> <tmax> <tmin> <vtmax> <vtmin> <stepp> <tapp> <tapfp> <tapfs> <tapft> <date_in> <date_out> <projid> <stn> /
		<r5> <r6> <r7> <r8> <o1> <p1> <o2> <p2> <o3> <p3> <o4> <p4> <o5> <p5> /
		<o6> <p6> <o7> <p7> <o8> <p8> <ohms> <tbasept> <tbasets> <angls> <anglt> /
		<rs1> <rs2> <rs3> <rt1> <rt2> <rt3> <alosss> <alosst> <rxunits> <gbunits> / <tunits> <rcomp> <xcomp>

       	1 "NORTH-01" 230.00      101 "NORTH-G1"  16.00 "1 " "        " :  
       	1  1   -1      "        " 000.00   0   -1      "        " 000.00   -1      "        " 
       	000.00   1    1  600.0 0.00000 0.10000 0.00000 0.00000 0.00000 0.00000 /
       	230.00  16.00   0.00    0.0 0.00000 0.00000  600.0    0.0    0.0    
       	0.0 0.000 1.5000 0.5100 1.5000 0.5100 0.00000 1.0000 1.0000 1.0000 1.0000   400101   391231   0 0     0.0    0.0    0.0    0.0 /
       	0 0.000   0 0.000   0 0.000   0 0.000   0 0.000   0 0.000   0 0.000   0 0.000		 
	 	*/
		// System.out.println("xfr data->" + lineStr);
		
		String str1 = lineStr.substring(0, lineStr.indexOf(':')),
               str2 = lineStr.substring(lineStr.indexOf(':')+1);
	
		this.setBranchHeaderData(str1);		

//		LoadflowBranchDataXmlType branchData = parser.getFactory().createLoadflowBranchDataXmlType(); 
//		branchRec.getLoadflowData().add(branchData);
//		branchData.setCode(LFBranchCodeEnumType.TRANSFORMER);
		
		StringTokenizer st = new StringTokenizer(str2, "\"");
		String s1 = st.nextToken();
		this.kregName = st.nextToken();
		String s2 = st.nextToken();
		this.iintName = st.nextToken();
		String s3 = st.nextToken();
		this.tertName = st.nextToken();
		String s4 = st.nextToken();
		
		// 1  1   -1  <st> <type> <kreg bus>    
		st = new StringTokenizer(s1);
		this.st = new Integer(st.nextToken()).intValue();
		this.type = new Integer(st.nextToken()).intValue();
		this.kregBus = new Integer(st.nextToken()).intValue();

		// 000.00   0   -1    <kreg bkv> <zt> <iint bus>  
		st = new StringTokenizer(s2);
		this.kregBkv = new Double(st.nextToken()).doubleValue();
		this.zt = new Integer(st.nextToken()).intValue();
		this.iintBus = new Integer(st.nextToken()).intValue();
		
		// 000.00   -1 <iint bkv> <tert bus>
		st = new StringTokenizer(s3);
		this.iintBkv = new Double(st.nextToken()).doubleValue();
		this.tertBus = new Integer(st.nextToken()).intValue();

		/*
		000.00   1    1  600.0 0.00000 0.10000 0.00000 0.00000 0.00000 0.00000 /
       	230.00  16.00   0.00    0.0 0.00000 0.00000  600.0    0.0    0.0    
       	0.0 0.000 1.5000 0.5100 1.5000 0.5100 0.00000 1.0000 1.0000 1.0000 1.0000   400101   391231   0 0     0.0    0.0    0.0    0.0 /
       	0 0.000   0 0.000   0 0.000   0 0.000   0 0.000   0 0.000   0 0.000   0 0.000
       	
		<tert bkv> <area> <zone> <tbase> <zpsr> /
		<zpsx> <zptr> <zptx> <ztsr> <ztsx> <vnomp> <vnoms> <vnomt> <anglp> <gmag> /
		<bmag> <r1> <r2> <r3> <r4> <aloss> <tmax> <tmin> <vtmax> <vtmin> <stepp> <tapp> <tapfp> <tapfs> <tapft> <date_in> <date_out> <projid> <stn> /
		<r5> <r6> <r7> <r8> <o1> <p1> <o2> <p2> <o3> <p3> <o4> <p4> <o5> <p5> /
		<o6> <p6> <o7> <p7> <o8> <p8> <ohms> <tbasept> <tbasets> <angls> <anglt> /
		<rs1> <rs2> <rs3> <rt1> <rt2> <rt3> <alosss> <alosst> <rxunits> <gbunits> / <tunits> <rcomp> <xcomp>       	
       	*/
		st = new StringTokenizer(s4);
		this.tertBkv = new Double(st.nextToken()).doubleValue();
		this.ar = new Integer(st.nextToken()).intValue();
		this.z = new Integer(st.nextToken()).intValue();
		this.tbase = new Double(st.nextToken()).doubleValue();
		this.zpsr = new Double(st.nextToken()).doubleValue();
		this.zpsx = new Double(st.nextToken()).doubleValue();
		this.zptr = new Double(st.nextToken()).doubleValue();
		this.zptx = new Double(st.nextToken()).doubleValue();
		this.ztsr = new Double(st.nextToken()).doubleValue();
		this.ztsx = new Double(st.nextToken()).doubleValue();
		this.vnomp = new Double(st.nextToken()).doubleValue();
		this.vnoms = new Double(st.nextToken()).doubleValue();
		this.vnomt = new Double(st.nextToken()).doubleValue();
		this.anglp = new Double(st.nextToken()).doubleValue();
		this.gmag = new Double(st.nextToken()).doubleValue();
		this.bmag = new Double(st.nextToken()).doubleValue(); 
		
		// <r1> <r2> <r3> <r4>
		this.setRMvaAry1_4(st); 
		
		this.aloss = new Double(st.nextToken()).doubleValue();
		this.tmax = new Double(st.nextToken()).doubleValue();
		this.tmin = new Double(st.nextToken()).doubleValue();
		this.vtmax = new Double(st.nextToken()).doubleValue();
		this.vtmin = new Double(st.nextToken()).doubleValue();
		this.stepp = new Double(st.nextToken()).doubleValue();
		this.tapp = new Double(st.nextToken()).doubleValue();
		this.tapfp = new Double(st.nextToken()).doubleValue();
		this.tapfs = new Double(st.nextToken()).doubleValue();
		this.tapft  = new Double(st.nextToken()).doubleValue();
		
		// <d_in> <d_out> <proj id>
		this.setDates(st);
		
		//<stn> 
		this.nst = new Integer(st.nextToken()).intValue();
		
		// <r5> <r6> <r7> <r8>
		this.setRMvaAry5_8(st);
			
		// <o1> <p1> <o2> <p2> <o3> <p3> <o4> <p4> <o5> <p5> <o6> <p6> <o7> <p7> <o8> <p8>
		this.setOwnerAry(st);
		
		if (st.hasMoreElements())
			this.ohms = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.tbasept = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.tbasets = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.angls = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.anglt = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.rs1 = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.rs2 = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.rs3 = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.rt1 = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.rt2 = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.rt3 = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.alosss = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.alosst = new Double(st.nextToken()).doubleValue();
		
/*
		<type> Control type {1,11,2,12,4,14} 
       		1 or 11= None 
       		2 or 12= Controls a voltage by adjusting TCUL ratio 
       		4 or 14= Controls real power flow by adjusting ps angle
		<tbase> Transformer Base (primary to secondary) (MVA)
		<vnomp> Primary winding nominal voltage (kV)
		<vnoms> Secondary winding nominal voltage (kV)
		<zpsr> Resistance primary to secondary (pu on tbase)
		<zpsx> Reactance primary to secondary (pu on tbase)
		<tapfp> Primary winding fixed tap position (pu)
		<tapfs> Secondary winding fixed tap position (pu)
		<anglp> Primary winding phase angle (degrees)
		<angls> Secondary winding phase angle (degrees)
		<gmag> Magnetizing conductance (pu on tbase)
		<bmag> Magnetizing susceptance (pu on tbase)
		<aloss> Loss factor (0.0 - 1.0) used to assign losses.1.0 = 100% loss assigned to "from" side of transformer
		<alosss> Secondary Loss factor (0.0 - 1.0)
*/
		boolean isPsXfr = this.anglp != 0.0 || this.angls != 0.0;

		final String fid = AclfModelParser.BusIdPreFix + f_bus;
		final String tid = AclfModelParser.BusIdPreFix + t_bus;
		final String cId = ck.replace(' ', '_');
		XfrBranchXmlType branchRec = null;
		try {
			branchRec = isPsXfr?
				parser.createPSXfrBranch(fid, tid, cId) : parser.createXfrBranch(fid, tid, cId);
		} catch (Exception e) {
			ODMLogger.getLogger().severe("branch data error, " + e.toString());
		}				
		
		BaseJaxbHelper.addNVPair(branchRec, GE_PSLF_Adapter.Token_XfrType, new Integer(this.type).toString());
		
		TransformerInfoXmlType xfrInfo = branchRec.getXfrInfo();
		xfrInfo.setRatedPower(BaseDataSetter.createPowerMvaValue(this.tbase));
		xfrInfo.setFromRatedVoltage(BaseDataSetter.createVoltageValue(this.vnomp, VoltageUnitType.KV));
		xfrInfo.setToRatedVoltage(BaseDataSetter.createVoltageValue(this.vnoms, VoltageUnitType.KV));
		
		branchRec.setZ(BaseDataSetter.createZValue(this.zpsr, this.zpsx, ZUnitType.PU));
		branchRec.getXfrInfo().setDataOnSystemBase(false);
		
		branchRec.setFromTurnRatio(BaseDataSetter.createTurnRatioPU(this.tapfp));
		branchRec.setToTurnRatio(BaseDataSetter.createTurnRatioPU(this.tapfs));
		
		if (isPsXfr) {
			PSXfrBranchXmlType branch = (PSXfrBranchXmlType)branchRec;
			branch.setFromAngle(BaseDataSetter.createAngleValue(this.anglp, AngleUnitType.DEG));
			branch.setToAngle(BaseDataSetter.createAngleValue(this.angls, AngleUnitType.DEG));
		}

		if (this.gmag != 0.0 || this.bmag != 0.0)
			branchRec.setMagnitizingY(BaseDataSetter.createYValue(this.gmag, this.bmag, YUnitType.PU));
		
		if (this.aloss != 0.0)
			xfrInfo.setFromLossFactor(this.aloss);
		if (this.alosss != 0.0)
			xfrInfo.setToLossFactor(this.alosss);

		/*		
		<kreg bus> Number of bus whose voltage is controlled by this transformer if type is not a 1.
		<"kreg name"> Regulating bus name enclosed in quotation marks  // no need
		<kreg bkv> Regulating bus base voltage           // no need
		<tmax> Maximum TCUL ratio if type 2 (pu) (degrees) Maximum phase angle if type 4
		<tmin> Minimum TCUL ratio if type 2 (pu) (degrees) Minimum phase angle if type 4
		<vtmax> Upper limit of controlled voltage band if type 2 (pu) Upper limit of controlled MW flow band if type 4 (pu)
		<vtmin> Lower limit of controlled voltage band if type 2 (pu) Lower limit of controlled MW flow band if type 4 (pu)
		<stepp> Ratio step of TCUL unit if type is 2 Angle step of TCUL unit if type is 4 (pu) (degrees)
		<tapp> TCUL tap position (primary winding) (pu)
		<iztabl> Transformer impedance table number
*/

		if (this.type == 2 || this.type == 12) {
			/* TODO
			xfr.setAdjBusNumber(this.kregBus);
			xfr.setTapAngMax(this.tmax);
			xfr.setTapAngMin(this.tmin);
			xfr.setVmax(this.vtmax);
			xfr.setVmin(this.vtmin);
			xfr.setAdjTapAngStep(this.stepp);
			xfr.setAdjTapPrim(this.tapp);
			xfr.setZTableNumber(this.zt);
			*/
		}
		else if (this.type == 4 || this.type == 14) {
			/* TODO
			xfr.setAdjBusNumber(this.kregBus);
			xfr.setTapAngMax(this.tmax);
			xfr.setTapAngMin(this.tmin);
			xfr.setVmax(this.vtmax);
			xfr.setVmin(this.vtmin);
			xfr.setAdjTapAngStep(this.stepp);
			xfr.setAdjTapPrim(this.tapp);
			xfr.setZTableNumber(this.zt);
			*/
		}
/*
// Teriary data
  
		<iint bus> 3-winding point bus number
		<"iint name"> 3-winding point bus name enclosed in quotation marks
		<iint bkv> 3-winding point bus base voltage

		<tert bus> Tertiary winding bus number
		<"tert name"> Tertiary winding bus name enclosed in quotation marks
		<tert bkv> Tertiary winding bus base voltage

		<zptr> Resistance primary to tertiary (pu on tbasept)
		<zptx> Reactance primary to tertiary (pu on tbasept)
		<ztsr> Resistance tertiary to secondary (pu on tbasets)
		<ztsx> Reactance tertiary to secondary (pu on tbasets)
 
		<vnomt> Tertiary winding nominal voltage (kV) 
		<tapft> Tertiary winding fixed tap position (pu)

		<tbasept> Transformer Base (primary to tertiary) (MVA)
		<tbasets> Transformer Base (tertiary to secondary) (MVA)
		<anglt> Tertiary winding phase angle (degrees)

		<rt1> Tertiary rating 1 (MVA)
		<rt2> Tertiary rating 1 (MVA)
		<rt3> Tertiary rating 1 (MVA)

		<rs1> Secondary rating 1 (MVA)
		<rs2> Secondary rating 2 (MVA)
		<rs3> Secondary rating 3 (MVA)

		<alosst> Tertiary Loss factor (0.0 - 1.0)
 */	
	}
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "type, kregBus, zt, iintBus, tertBus: " + type + ", " + kregBus + ", " + zt + ", " + iintBus + ", " + tertBus + "\n";
		str += "kregBkv, iintBkv, tertBkv, tbase, zpsr: " + kregBkv + ", " + iintBkv + ", " + tertBkv + ", " + tbase + ", " + zpsr + "\n";
		str += "kregName, iintName, tertName: " + kregName + ", " + iintName + ", " + tertName + "\n";
		str += "zpsx, zptr, zptx, ztsr, ztsx, vnomp, vnoms, vnomt, anglp, gmag, bmag: " + 
		          zpsx + ", " + zptr + ", " + zptx + ", " + ztsr + ", " + ztsx + ", " + vnomp + ", " + vnoms + ", " + vnomt + ", " + 
		          anglp + ", " + gmag + ", " + bmag + "\n";
		str += "aloss, tmax, tmin, vtmax, vtmin, stepp, tapp, tapfp, tapfs, tapft: " + 
		          aloss + ", " + tmax + ", " + tmin + ", " + vtmax + ", " + vtmin + ", " + stepp + ", " + 
		          tapp + ", " + tapfp + ", " + tapfs + ", " + tapft + "\n";
		str += "tbasept, tbasets, angls, anglt: " + tbasept + ", " + tbasets + ", " + angls + ", " + anglt + "\n";
		str += "rs1, rs2, rs3, rt1, rt2, rt3, alosss, alosst: " + 
		          rs1 + ", " + rs2 + ", " + rs3 + ", " + rt1 + ", " + rt2 + ", " + rt3 + ", " + alosss + ", " + 
		          alosst + "\n";
		return str;
	}	
}
