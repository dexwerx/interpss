 /*
  * @(#)GEDataRec.java   
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

import org.ieee.odm.adapter.ge.GE_PSLF_Adapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.ExchangeAreaXmlType;
import org.ieee.odm.schema.ExchangeZoneXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;

public class NetDataRec {
	static public class TitleRec {
		public void processLineStr(String lineStr, GE_PSLF_Adapter.VersionNo version, AclfModelParser parser) {
			LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
			BaseJaxbHelper.addNVPair(baseCaseNet, "Title", lineStr);
		}
	}
	
	static public class CommentsRec {
		public String comments = "";

		public void processLineStr(String lineStr, GE_PSLF_Adapter.VersionNo version, AclfModelParser parser) {
			LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
			BaseJaxbHelper.addNVPair(baseCaseNet, "Comments", lineStr);
		}
	}

	/*
	 */	
	static public class SolutionParamRec {

		public void processLineStr(String lineStr, GE_PSLF_Adapter.VersionNo version, AclfModelParser parser) {
			LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
			int tap, phas, area, svd, dctap, gcd;
			double jump, toler;

			//System.out.println("solutionParam->" + lineStr);
			StringTokenizer st = new StringTokenizer(lineStr);
			st.nextElement();
			String str = st.nextToken();
			if (lineStr.startsWith("tap"))
				tap = new Integer(str).intValue();
			else if (lineStr.startsWith("phas"))
				phas = new Integer(str).intValue();
			else if (lineStr.startsWith("area"))
				area = new Integer(str).intValue();
			else if (lineStr.startsWith("svd"))
				svd = new Integer(str).intValue();
			else if (lineStr.startsWith("dctap"))
				dctap = new Integer(str).intValue();
			else if (lineStr.startsWith("gcd"))
				gcd = new Integer(str).intValue();
			else if (lineStr.startsWith("jump"))
				jump = new Double(str).doubleValue();
			else if (lineStr.startsWith("toler"))
				toler = new Double(str).doubleValue();
			else if (lineStr.startsWith("sbase")) {
				double sbase = new Double(str).doubleValue();
				baseCaseNet.setBasePower(BaseDataSetter.createPowerMvaValue(sbase));
			}
		}
	}

	static public class AreaRec {
		public int arnum, swing;
		public String arnam;
		public double pnetdes, pnettol, pnet, qnet;

		public AreaRec(String lineStr, GE_PSLF_Adapter.VersionNo version, AclfModelParser parser) throws Exception {
			LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
			//System.out.println("area->" + lineStr);
			StringTokenizer st = new StringTokenizer(lineStr, "\"");
			
			/*
		        1 "P                               "       0      0.0   1000.0    -88.2    -84.1	
			 */

			this.arnum = new Integer(st.nextToken().trim()).intValue();
			this.arnam = st.nextToken();
			
			String str = st.nextToken();
			//        0      0.0   1000.0    -88.2    -84.1
			st = new StringTokenizer(str);
			this.swing = new Integer(st.nextToken()).intValue();
			this.pnetdes = new Double(st.nextToken()).doubleValue();
			this.pnettol = new Double(st.nextToken()).doubleValue();
			this.pnet = new Double(st.nextToken()).doubleValue();
			this.qnet = new Double(st.nextToken()).doubleValue();	
			
			if (baseCaseNet.getAreaList() == null)
				baseCaseNet.setAreaList(odmObjFactory.createNetworkXmlTypeAreaList());
			ExchangeAreaXmlType area = odmObjFactory.createExchangeAreaXmlType();
			baseCaseNet.getAreaList().getArea().add(area);
			area.setId(new Integer(this.arnum).toString());
			area.setNumber(this.arnum);
			area.setName(this.arnam);	
			area.setSwingBusId(parser.createBusRef(new Integer(this.swing).toString()));
			area.setTotalExchangePower(BaseDataSetter.createPowerValue(this.pnet, this.qnet, ApparentPowerUnitType.MVA));
			area.setDesiredExchangePower(BaseDataSetter.createActivePowerValue(this.pnetdes, ActivePowerUnitType.MW));
			area.setExchangeErrTolerance(BaseDataSetter.createActivePowerValue(this.pnettol, ActivePowerUnitType.MW));
		}
	}

	static public class ZoneRec {
		public int zonum;
		public String zonam;
		public double pznet, qznet;

		public ZoneRec(String lineStr, GE_PSLF_Adapter.VersionNo version, AclfModelParser parser) throws Exception {
			LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
			//System.out.println("zone->" + lineStr);
			StringTokenizer st = new StringTokenizer(lineStr, "\"");
			
			/*
		    	2 "Italyz2                         "    9.448  112.738
			 */
			this.zonum = new Integer(st.nextToken().trim()).intValue();
			this.zonam = st.nextToken();
			
			String str = st.nextToken();
			st = new StringTokenizer(str);
			this.pznet = new Double(st.nextToken()).doubleValue();
			this.qznet = new Double(st.nextToken()).doubleValue();
			
			if (baseCaseNet.getLossZoneList() == null)
				baseCaseNet.setLossZoneList(odmObjFactory.createNetworkXmlTypeLossZoneList());
			ExchangeZoneXmlType zone = odmObjFactory.createExchangeZoneXmlType(); 
			baseCaseNet.getLossZoneList().getLossZone().add(zone);
			zone.setId(new Integer(this.zonum).toString());
			zone.setNumber(this.zonum);
			zone.setName(this.zonam);	
			zone.setExchangePower(BaseDataSetter.createPowerValue(this.pznet, this.qznet, ApparentPowerUnitType.MVA)); 
		}
	}

	/*
       1 "                                " "O1  " :       0.00       0.00       0.00       0.00   0
	 */
	static public class OwnerRec {
		public int ownerNo, ar;
		public String oname, sname;
		public double net_mw, net_mvar, sch_mw, sch_mvar;

		public OwnerRec(String lineStr, GE_PSLF_Adapter.VersionNo version) {
			String str1 = lineStr.substring(0, lineStr.indexOf(':')),
		           str2 = lineStr.substring(lineStr.indexOf(':')+1);

			//System.out.println("owner->" + lineStr);
			StringTokenizer st = new StringTokenizer(str1, "\"");
			this.ownerNo = new Integer(st.nextToken().trim()).intValue();
			this.oname = st.nextToken();
			st.nextToken();
			this.sname = st.nextToken();
			
			//        0.00       0.00       0.00       0.00   0
			st = new StringTokenizer(str2);
			this.net_mw = new Double(st.nextToken()).doubleValue();
			this.net_mvar = new Double(st.nextToken()).doubleValue();
			this.sch_mw = new Double(st.nextToken()).doubleValue();
			this.sch_mvar = new Double(st.nextToken()).doubleValue();
			this.ar = new Integer(st.nextToken().trim()).intValue();
		}
	}
	
/*
    1 "E-P                             "   88.209   84.065  2275.0     0.0     0.0    0.0     0.0     0.0     0.0     0.0 
	
 */	
	static public class InterfaceRec {
		public int ifno;
		public String name;
		public double pnet, qnet; 
		double[] rAry = new double[8];
		
		public InterfaceRec(String lineStr, GE_PSLF_Adapter.VersionNo version) {
			// System.out.println("interface->" + lineStr);
			StringTokenizer st = new StringTokenizer(lineStr, "\"");
			
			this.ifno = new Integer(st.nextToken().trim()).intValue();
			this.name = st.nextToken();
			
			String str = st.nextToken();
			st = new StringTokenizer(str);
			this.pnet = new Double(st.nextToken()).doubleValue();
			this.qnet = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[0] = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[1] = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[2] = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[3] = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[4] = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[5] = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[6] = new Double(st.nextToken()).doubleValue();
			if (st.hasMoreElements())
				this.rAry[7] = new Double(st.nextToken()).doubleValue();
		}
	}
	
/*
     79 "E-55    " 380.00       1 "P-1     " 380.00 "1 "   :      1     1.000
*/	
	static public class InterfaceBranchRec extends BranchHeaderRec{
		public int ifn;
		public double pf;

		public InterfaceBranchRec(String lineStr, GE_PSLF_Adapter.VersionNo version) {
			//System.out.println("inter branch->" + lineStr);
			String str1 = lineStr.substring(0, lineStr.indexOf(':')),
	               str2 = lineStr.substring(lineStr.indexOf(':')+1);
		
			this.setBranchHeaderData(str1);
			StringTokenizer st = new StringTokenizer(str2);
			if (st.hasMoreElements())
				this.ifn = new Integer(st.nextToken()).intValue();
			if (st.hasMoreElements())
				this.pf = new Double(st.nextToken()).doubleValue();			
		}
	}
	
	public static void setOwnerAry(StringTokenizer st, int[] oAry, double[] pAry) {
		if (st.hasMoreElements())
			oAry[0] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[0] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			oAry[1] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[1] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			oAry[2] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[2] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			oAry[3] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[3] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			oAry[4] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[4] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			oAry[5] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[5] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			oAry[6] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[6] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			oAry[7] = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			pAry[7] = new Double(st.nextToken()).doubleValue();
	}	
	
	public static void setDates(StringTokenizer st, String d_in, String d_out, String projId) {
		if (st.hasMoreElements())
			d_in = st.nextToken();
		if (st.hasMoreElements())
			d_out = st.nextToken();
		if (st.hasMoreElements())
			projId = st.nextToken();
	}		
}
