 /*
  * @(#)PSSEV30GenDataRec.java   
  *
  * Copyright (C) 2006 www.interpss.org
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

package org.ieee.odm.adapter.psse.v30.impl;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.ZUnitType;

public class PSSEV30GenDataRec {
	private static int i, ireg, stat ;
	private static String id;
	private static double pg, qg, qt, qb, vs, mbase, zr, zx, rt, xt, gtap, rmpct, pt, pb;
	private static int o1 = 0, o2 = 0, o3 = 0, o4 = 0;
	private static double f1 = 0.0, f2 = 0.0, f3 = 0.0, f4 = 0.0;
	
	/*
	 * GenData
	 * I,ID,PG,QG,QT,QB,VS,IREG,MBASE,ZR,ZX,RT,XT,GTAP,STAT,RMPCT,PT,PB,O1,F1,...,O4,F4
	 * 
	 * The standard generator boundary condition is a specification of real
	 * power output at the high-voltage bus, bus k, and of voltage magnitude at
	 * some designated bus, not necessarily bus k.
	 * 
PG Generator active power output; entered in MW. PG = 0.0 by default.
QG Generator reactive power output; entered in Mvar. QG need be entered only if the
	case, as read in, is to be treated as a solved case. QG = 0.0 by default.
QT Maximum generator reactive power output; entered in Mvar. For fixed output generators
	(i.e., nonregulating), QT must be equal to the fixed Mvar output.
QT = 9999.0 by default.
QB Minimum generator reactive power output; entered in Mvar. For fixed output
	generators, QB must be equal to the fixed Mvar output. QB = -9999.0 by default.
VS Regulated voltage setpoint; entered in pu. VS = 1.0 by default.

 This is a case where the gen is turn-off
 I,    ID,       PG,        QG,        QT,        QB,   VS,          IREG,MBASE,     ZR,        ZX,        RT,        XT,     GTAP,   STAT,RMPCT,    PT,        PB,      O1,F1,...,O4,F4
 10636,'1 ',     0.000,     0.000,     0.000,     0.000,1.05100,     0,   100.000,   0.00000,   1.00000,   0.00000,   0.00000,1.00000,1,  100.0,     0.000,     0.000,   1,1.0000
	 */

	public static void procLineString(String lineStr, PsseVersion version, final AclfModelParser parser) {
		procFields(lineStr, version);

/*
		I,ID,PG,QG,QT,QB,VS,IREG,MBASE,ZR,ZX,RT,XT,GTAP,STAT,RMPCT,PT,PB,O1,F1,...,O4,F4

		The standard generator boundary condition is a specification of real power output at the
		high-voltage bus, bus k, and of voltage magnitude at some designated bus, not necessarily bus k.
		
		STAT - Initial machine status of one for in-service and zero for out-of-service; STAT = 1 by default.
*/		
	    final String busId = AbstractModelParser.BusIdPreFix+i;
		LoadflowBusXmlType busRec = parser.getAclfBus(busId);
	    if (busRec == null){
	    	ODMLogger.getLogger().severe("Bus "+ busId+ " not found in the network");
	    	return;
	    }
	    
	    LoadflowGenXmlType contriGen = AclfParserHelper.createContriGen(busRec);
	    
	    contriGen.setId(id);
	    contriGen.setName("Gen:" + id + "(" + i + ")");
	    contriGen.setDesc("PSSE Generator " + id + " at Bus " + i);
	    contriGen.setOffLine(stat!=1);

	    contriGen.setPower(BaseDataSetter.createPowerValue(pg, qg, ApparentPowerUnitType.MVA));

	    contriGen.setDesiredVoltage(BaseDataSetter.createVoltageValue(vs, VoltageUnitType.PU));
		
//		if (pt == 0.0 & pb == 0.0 || pt < pb ) {
//			pt = 9999.0; pb = -9999.0;
//		}
		contriGen.setPLimit(BaseDataSetter.createActivePowerLimit(pt, pb, ActivePowerUnitType.MW));
		
//		if (qt == 0.0 & qb == 0.0 || qt < qb) {
//			qt = 9999.0; qb = -9999.0;
//		}
		contriGen.setQLimit(BaseDataSetter.createReactivePowerLimit(qt, qb, ReactivePowerUnitType.MVAR));
		
	    if (ireg > 0) {
	    	final String reBusId = AbstractModelParser.BusIdPreFix+ireg;
	    	contriGen.setRemoteVoltageControlBus(parser.createBusRef(reBusId));
	    }
	    
	    contriGen.setRatedPower(BaseDataSetter.createPowerMvaValue(mbase));

		if ( zr != 0.0 || zx != 0.0 )
			contriGen.setSourceZ(BaseDataSetter.createZValue(zr, zx, ZUnitType.PU));

		if ( rt != 0.0 || xt != 0.0 ) {
			contriGen.setXfrZ(BaseDataSetter.createZValue(rt, xt, ZUnitType.PU));
			contriGen.setXfrTap(gtap);
		}
		
		contriGen.setMvarVControlParticipateFactor(rmpct*0.01);

		BaseJaxbHelper.addOwner(contriGen, 
				new Integer(o1).toString(), f1, 
				new Integer(o2).toString(), o2==0?0.0:f2, 
				new Integer(o3).toString(), o3==0?0.0:f3, 
				new Integer(o4).toString(), o4==0?0.0:f4);
	}
	
	private static void procFields(String lineStr, PsseVersion version) {
		StringTokenizer st;

		st = new StringTokenizer(lineStr, ",");
		i = new Integer(st.nextToken().trim()).intValue();
		id = st.nextToken().trim();

		pg = new Double(st.nextToken().trim()).doubleValue();
		qg = new Double(st.nextToken().trim()).doubleValue();
		
		qt = new Double(st.nextToken().trim()).doubleValue();
		qb = new Double(st.nextToken().trim()).doubleValue();
		if (qt < qb) {
			ODMLogger.getLogger().warning("Gen Data qt (qMax: " + qt + ") < qb (qMin:" + qb + "), set to [9999.0, -9999.0]\n" + lineStr);
		}
		vs = new Double(st.nextToken().trim()).doubleValue();
		ireg = new Integer(st.nextToken().trim()).intValue();
		mbase = new Double(st.nextToken().trim()).doubleValue();
		zr = new Double(st.nextToken().trim()).doubleValue();
		zx = new Double(st.nextToken().trim()).doubleValue();
		rt = new Double(st.nextToken().trim()).doubleValue();
		xt = new Double(st.nextToken().trim()).doubleValue();
		gtap = new Double(st.nextToken().trim()).doubleValue();
		stat = new Integer(st.nextToken().trim()).intValue();
		rmpct = new Double(st.nextToken().trim()).doubleValue();
		
		pt = new Double(st.nextToken().trim()).doubleValue();
		pb = new Double(st.nextToken().trim()).doubleValue();
		if (pt < pb) {
			ODMLogger.getLogger().warning("Gen Data pt (pMax:" + pt + ") < pb (pMin:" + pb + "), set to [9999.0, -9999.0] \n" + lineStr);
		}

		if (st.hasMoreTokens())
			o1 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f1 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o2 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f2 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o3 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f3 = new Double(st.nextToken().trim()).doubleValue();
		if (st.hasMoreTokens())
			o4 = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreTokens())
			f4 = new Double(st.nextToken().trim()).doubleValue();
	}
}
