 /*
  * @(#)BusDataRec.java   
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
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.VoltageUnitType;

public class BusDataRec extends BusHeaderRec {
			public int ty, owner;
			public double vs_pu, vt_pu, an_deg, vma, vmi;
			public int level, stisol, islnum;
			public double latitude, longitude;

	public BusDataRec(String lineStr, GE_PSLF_Adapter.VersionNo version, final AclfModelParser parser) {
/*
		bus data  [    5]             ty  vsched   volt     angle   ar zone  vmax   vmin   date_in date_out pid L own
       		   1 "P-1     " 380.00  :  0 1.04000 1.040000  -5.00000   1  201 4.0000 0.4000   400101   391231   0 0   1
 */
		String str1 = lineStr.substring(0, lineStr.indexOf(':')),
		       str2 = lineStr.substring(lineStr.indexOf(':')+1);
		
		setHeaderData(str1);
		
		StringTokenizer st = new StringTokenizer(str2);
		if (st.hasMoreElements())
			this.ty = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.vs_pu = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.vt_pu  = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.an_deg  = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.ar = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.z  = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.vma  = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.vmi = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.d_in = st.nextToken();
		if (st.hasMoreElements())
			this.d_out = st.nextToken();
		if (st.hasMoreElements())
			this.projId = st.nextToken();
		if (st.hasMoreElements())
			this.level = new Integer(st.nextToken()).intValue();;
		if (st.hasMoreElements())
			this.owner  = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.stisol  = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.latitude  = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.longitude  = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.islnum  = new Integer(st.nextToken()).intValue();

/*
		public int number, ar, z;
		public String name, id = "", longId = ""; 
		public double bkv;
		public String d_in, d_out, projId;
 */
		final String busId = AbstractModelParser.BusIdPreFix+this.number;
		// XML requires id start with a char
		LoadflowBusXmlType busRec;
		try {
			busRec = parser.createAclfBus(busId, this.number);
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			return;
		}
		busRec.setNumber(this.number);

		busRec.setAreaNumber(ar);
		busRec.setZoneNumber(z);	
		busRec.setName(name);
		if (this.longId != null && !this.longId.equals(""))
			busRec.setDesc(this.longId);
		busRec.setBaseVoltage(BaseDataSetter.createVoltageValue(bkv, VoltageUnitType.KV));
		
		/*
		 */	
		if (owner > 0)
			BaseJaxbHelper.addOwner(busRec, new Integer(owner).toString());

		LFGenCodeEnumType genType = ty == 0? LFGenCodeEnumType.SWING : 
				( ty == 1? LFGenCodeEnumType.PQ : LFGenCodeEnumType.PV);
		AclfDataSetter.setGenData(busRec, genType, vs_pu, 
				VoltageUnitType.PU, an_deg, AngleUnitType.DEG, 
				0.0, 0.0,	ApparentPowerUnitType.MVA);
		busRec.getGenData().getEquivGen().setVoltageLimit(BaseDataSetter.createVoltageLimit(vma, vmi, VoltageUnitType.PU));
	}
}
