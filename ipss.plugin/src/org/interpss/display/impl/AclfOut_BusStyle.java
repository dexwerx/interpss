/*
 * @(#)AclfOutFunc.java   
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
 * @Date 11/27/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.display.impl;

import org.apache.commons.math.complex.Complex;
import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.util.Number2String;

import com.interpss.common.datatype.UnitHelper;
import com.interpss.common.datatype.UnitType;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.CapacitorBusAdapter;
import com.interpss.core.aclf.adpter.GenBusAdapter;
import com.interpss.core.aclf.adpter.PSXfrAdapter;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

public class AclfOut_BusStyle {
	/*
	 *   Bus Style output
	 *   ================
	 */
	public static String busStyleTitle(AclfNetwork net) {
		StringBuffer str = new StringBuffer("");
		str.append("\n\n                                              Load Flow Results\n\n");
		str.append(AclfOutFunc.maxMismatchToString(net,"                    ") + "\n");
		str.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
		str.append(" Bus ID             Bus Voltage         Generation           Load             To             Branch P+jQ          Xfr Ratio   PS-Xfr Ang\n");
		str.append("              baseKV    Mag   Ang     (mW)    (mVar)    (mW)    (mVar)      Bus ID      (mW)    (mVar)   (kA)   (From)  (To) (from)   (to)\n");
		str.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
		return str.toString();
	}

	public static String lfResultsBusStyle(AclfBus bus, AclfNetwork net, AclfOutFunc.BusIdStyle style) {
		double baseKVA = net.getBaseKva();
		StringBuffer str = new StringBuffer("");

		GenBusAdapter genBus = bus.toGenBus();
		Complex busGen = genBus.getGenResults(UnitType.mVA);
		Complex busLoad = genBus.getLoadResults(UnitType.mVA);
		if (bus.isCapacitor()) {
			CapacitorBusAdapter cap = bus.toCapacitorBus();
			busGen = busGen.add(new Complex(0.0, cap.getQResults(bus.getVoltageMag(), UnitType.PU)));
		}
		String id = style == AclfOutFunc.BusIdStyle.BusId_No?
				AclfOutFunc.getBusId(bus, net.getOriginalDataFormat()):
				bus.getName().trim();
		str.append(Number2String.toStr(-12, id) + " ");
		str.append(String.format(" %s ", AclfOutFunc.formatKV(bus.getBaseVoltage()*0.001)));
		str.append(Number2String.toStr("0.0000", bus.getVoltageMag(UnitType.PU)) + " ");
		str.append(Number2String.toStr("##0.0", bus.getVoltageAng(UnitType.Deg)) + " ");
		str.append(Number2String.toStr("####0.00", busGen.getReal()) + " ");
		str.append(Number2String.toStr("####0.00", busGen.getImaginary()) + " ");
		str.append(Number2String.toStr("####0.00", busLoad.getReal()) + " ");
		str.append(Number2String.toStr("####0.00", busLoad.getImaginary()) + " ");
		// str.append( " - - - - - - - - - - -\n" );

		int cnt = 0;
		for (Branch br : bus.getBranchList()) {
			AclfBranch bra = (AclfBranch) br;
			if (bra.isActive()) {

//				final AclfBus busj = bus.equals(bra.getFromAclfBus())?
//					bra.getToAclfBus() : bra.getFromAclfBus();

				Complex pq = new Complex(0.0, 0.0);
				double amp = 0.0, fromRatio = 1.0, toRatio = 1.0, fromAng = 0.0, toAng = 0.0;
				AclfBus toBus = null;
				if (bra.isActive()) {
					if (bus.getId().equals(bra.getFromAclfBus().getId())) {
						toBus = bra.getToAclfBus();
						pq = bra.powerFrom2To(UnitType.mVA);
						amp = UnitHelper.iConversion(bra.current(UnitType.PU), bra.getFromAclfBus().getBaseVoltage(),
								baseKVA, UnitType.PU, UnitType.Amp);
						if (bra.isXfr() || bra.isPSXfr()) {
							fromRatio = bra.getFromTurnRatio();
							toRatio = bra.getToTurnRatio();
							if (bra.isPSXfr()) {
								PSXfrAdapter psXfr = bra.toPSXfr();
								fromAng = psXfr.getFromAngle(UnitType.Deg);
								toAng = psXfr.getToAngle(UnitType.Deg);
							}
						}
					} else {
						toBus = bra.getFromAclfBus();
						pq = bra.powerTo2From(UnitType.mVA);
						amp = UnitHelper.iConversion(bra.current(UnitType.PU), bra.getToAclfBus().getBaseVoltage(),
								baseKVA, UnitType.PU, UnitType.Amp);
						if (bra.isXfr() || bra.isPSXfr()) {
							toRatio = bra.getFromTurnRatio();
							fromRatio = bra.getToTurnRatio();
							if (bra.isPSXfr()) {
								PSXfrAdapter psXfr = (PSXfrAdapter) bra.getAdapter(PSXfrAdapter.class);
								toAng = psXfr.getFromAngle(UnitType.Deg);
								fromAng = psXfr.getToAngle(UnitType.Deg);
							}
						}
					}
				}
				if (cnt++ > 0)
					str.append(Number2String.toStr(67, " ")	+ "    ");
				id = style == AclfOutFunc.BusIdStyle.BusId_No?
						AclfOutFunc.getBusId(toBus, net.getOriginalDataFormat()):
						toBus.getName().trim();
				str.append(" " + Number2String.toStr(-12, id) + " ");
				str.append(Number2String.toStr("####0.00", pq.getReal()) + " ");
				str.append(Number2String.toStr("####0.00", pq.getImaginary()) + " ");
				str.append(Number2String.toStr("##0.0##", 0.001 * amp) + " ");
				if (bra.isXfr() || bra.isPSXfr()) {
					if (fromRatio != 1.0)
						str.append(Number2String.toStr("0.0###", fromRatio) + " ");
					else
						str.append("       ");

					if (toRatio != 1.0)
						str.append(Number2String.toStr("0.0###", toRatio));
					else
						str.append("      ");

					if (bra.isPSXfr()) {
						if (fromAng != 0.0)
							str.append("   " + Number2String .toStr("##0.0", fromAng));
						else
							str.append("        ");

						if (toAng != 0.0)
							str.append(" " + Number2String.toStr("##0.0", toAng));
						else
							str.append("      ");
					}
					str.append("\n");
				} else {
					str.append("\n");
				}
			}
		}
		return str.toString();
	}
	
	public static String lfResultsBusStyle(AclfNetwork net, AclfOutFunc.BusIdStyle style) {
		StringBuffer str = new StringBuffer("");
		try {
			str.append(busStyleTitle(net));

			for (Bus b : net.getBusList()) {
				AclfBus bus = (AclfBus) b;
				if (bus.isActive()) {
					str.append(lfResultsBusStyle(bus, net, style));
				}
			}
			str.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
		} catch (Exception emsg) {
			str.append(emsg.toString());
		}
		return str.toString();
	}
}