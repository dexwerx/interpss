/*
 * @(#)DclfOutFunc.java   
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

package org.interpss.display;

import java.util.ArrayList;
import java.util.List;

import org.interpss.schema.BranchRecXmlType;
import org.interpss.schema.BusRecXmlType;
import org.interpss.schema.DclfBranchSensitivityXmlType;
import org.interpss.schema.DclfBusSensitivityXmlType;
import org.interpss.schema.SenAnalysisBusRecXmlType;
import org.interpss.schema.SenBusAnalysisDataType;

import com.interpss.common.datatype.Constants;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.Number2String;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.dclf.DclfAlgorithm;
import com.interpss.core.dclf.DclfSensitivityType;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

public class DclfOutFunc {
	/**
	 * Out put Dclf voltage angle results
	 * 
	 * @param net
	 * @param algo
	 * @return
	 */
	public static String dclfResults(DclfAlgorithm algo) {
		String str = "\n\n";
		str += "      DC Loadflow Results\n\n";
		str += "   Bud Id       VoltAng(deg)\n";
		str += "=================================\n";
		for (Bus bus : algo.getAclfNetwork().getBusList()) {
			int n = bus.getSortNumber();
			double angle = algo.getBusAngle(n) * Constants.RtoD;
			str += Number2String.toFixLengthStr(8, bus.getId()) + "        "
					+ Number2String.toStr(angle) + "\n";
		}

		str += "\n\n";
		str += "       FromId->ToId       Power Flow(pu)\n";
		str += "==========================================\n";
		for (Branch bra : algo.getAclfNetwork().getBranchList()) {
			double fAng = algo.getBusAngle(bra.getFromBus().getSortNumber()) * Constants.RtoD;
			double tAng = algo.getBusAngle(bra.getToBus().getSortNumber()) * Constants.RtoD;
			AclfBranch aclfBra = (AclfBranch)bra;
			str += Number2String.toFixLengthStr(20, bra.getId()) + "     "  
					+ Number2String.toStr((fAng-tAng)*aclfBra.getZ().getImaginary()) + "\n";
		}
		return str;
	}

	/**
	 * out power angle sensitivity calculation results
	 * 
	 * @param sen XML sensitivity calculation records
	 * @param algo
	 * @param msg
	 * @return
	 */
	public static String pAngleSensitivityResults(
			DclfBusSensitivityXmlType sen, DclfAlgorithm algo, IPSSMsgHub msg) {
		String busId = sen.getInjectBusList().getInjectBusArray(0).getBusId();
		String str = "\n\n";
		str += "  Power-Angle Sensitivity\n\n";
		str += "   Inject BusId : " + busId + "\n\n";
		str += "   Bud Id       dAng/dP\n";
		str += "=================================\n";
		for (BusRecXmlType bus : sen.getBusArray()) {
			double pang = algo.getBusSensitivity(DclfSensitivityType.PANGLE, busId,
					bus.getBusId(), msg);
			str += Number2String.toFixLengthStr(8, bus.getBusId()) + "       "
					+ Number2String.toStr(pang) + "\n";
		}
		return str;
	}

	/**
	 * Out Q voltage sensitivity calculation results
	 * 
	 * @param sen XML sensitivity calculation records
	 * @param algo
	 * @param msg
	 * @return
	 */
	public static String qVoltageSensitivityResults(
			DclfBusSensitivityXmlType sen, DclfAlgorithm algo, IPSSMsgHub msg) {
		String busId = sen.getInjectBusList().getInjectBusArray(0).getBusId();
		String str = "\n\n";
		str += "   Q-Voltage Sensitivity\n\n";
		str += "    Inject BusId : " + busId + "\n\n";
		str += "   Bud Id         dV/dQ\n";
		str += "=================================\n";
		for (BusRecXmlType bus : sen.getBusArray()) {
			double x = algo.getBusSensitivity(DclfSensitivityType.QVOLTAGE, busId, bus.getBusId(), msg);
			str += Number2String.toFixLengthStr(8, bus.getBusId()) + "       "
					+ Number2String.toStr(x) + "\n";
		}
		return str;
	}

	/**
	 * out generator shift factor calculation results
	 * 
	 * @param gsFactor XML sensitivity calculation records
	 * @param algo
	 * @param msg
	 * @return
	 */
	public static String genShiftFactorResults(
			DclfBranchSensitivityXmlType gsFactor, DclfAlgorithm algo,
			IPSSMsgHub msg) {
		String busId = gsFactor.getInjectBusList().getInjectBusArray(0).getBusId();
		String str = "\n\n";
		str += "   Generator Shift Factor\n\n";
		str += "    Inject BusId : " + busId + "\n\n";
		str += "       Branch Id          GSF\n";
		str += "=========================================\n";
		for (BranchRecXmlType branch : gsFactor.getBranchArray()) {
			double gsf = algo.getGenShiftFactor(busId, branch.getFromBusId(), branch
					.getToBusId(), msg);
			str += Number2String.toFixLengthStr(16, branch.getFromBusId()
					+ "->" + branch.getToBusId())
					+ "       " + Number2String.toStr(gsf) + "\n";
		}
		return str;
	}

	/**
	 * out power transfer distribution factor calculation results
	 * 
	 * @param tdFactor XML sensitivity calculation records
	 * @param algo
	 * @param msg
	 * @return
	 */
	public static String pTransferDistFactorResults(
			DclfBranchSensitivityXmlType tdFactor, DclfAlgorithm algo,
			IPSSMsgHub msg) {
		String str = "\n\n";
		str += "   Power Transfer Distribution Factor";
		if (tdFactor.getInjectBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			String inBusId = tdFactor.getInjectBusList().getInjectBusArray(0).getBusId();
			str += "\n\n    Inject BusId   : " + inBusId + "\n";
			str += withdrawBusInfo(tdFactor);
			str += "       Branch Id          PTDF\n";
			str += "========================================\n";
			for (BranchRecXmlType branch : tdFactor.getBranchArray()) {
				double ptdf = calPTDFactor(tdFactor, algo, branch, inBusId, msg);
				str += Number2String.toFixLengthStr(16, branch.getFromBusId()
						+ "->" + branch.getToBusId())
						+ "       " + Number2String.toStr(ptdf) + "\n";
			}
		}
		else {
			for (BranchRecXmlType branch : tdFactor.getBranchArray()) {
				str += "\n\n    Branch Id   : " + Number2String.toFixLengthStr(16, branch.getFromBusId()
						+ "->" + branch.getToBusId()) + "\n";
				str += withdrawBusInfo(tdFactor);
				str += "       Inject BusId          PTDF\n";
				str += "========================================\n";
				
				List<PTDFRec> list = new ArrayList<PTDFRec>();
				for (BusRecXmlType bus :  tdFactor.getInjectBusList().getInjectBusArray()){
					PTDFRec rec = new PTDFRec();
					rec.ptdf = calPTDFactor(tdFactor, algo, branch, bus.getBusId(), msg);
					rec.busId = bus.getBusId();
					list.add(rec);
				}
				sortPTDFRecList(list);
				for (PTDFRec rec : list){
					str += Number2String.toFixLengthStr(16, rec.busId)						
							+ "          " + Number2String.toStr(rec.ptdf) + "\n";
				}
			}
		}
		return str;
	}
	
	private static class PTDFRec {
		String busId;
		double ptdf = 0.0;
	}
	
	private static double calPTDFactor(DclfBranchSensitivityXmlType tdFactor, DclfAlgorithm algo, 
					BranchRecXmlType branch, String inBusId, IPSSMsgHub msg) {
		double ptdf = 0.0;
		if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			String wdBusId = tdFactor.getWithdrawBusList().getWithdrawBusArray(0).getBusId();
			ptdf = algo.getPTransferDistFactor(inBusId, wdBusId,
							branch.getFromBusId(),	branch.getToBusId(), msg);
		}	
		else 
			ptdf = algo.getPTransferDistFactor(inBusId, 
							branch.getFromBusId(),	branch.getToBusId(), msg);
		return ptdf;
	}
	
	private static String withdrawBusInfo(DclfBranchSensitivityXmlType tdFactor) {
		String str = "";
		if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			String wdBusId = tdFactor.getWithdrawBusList().getWithdrawBusArray(0).getBusId();
			str += "    Withdraw BusId : " + wdBusId + "\n\n";
		}
		else {
			str += "    Withdraw BusId : [";
			for (SenAnalysisBusRecXmlType bus : tdFactor.getWithdrawBusList().getWithdrawBusArray())
				str += " (" + bus.getBusId() + ", " + bus.getPercent() + "%)";
			str += " ]\n\n";
		}
		return str;
	}
	
	private static void sortPTDFRecList(List<PTDFRec> list) {
		boolean done = false;
		while (!done) {
			done = true;
			for (int i = 0; i < list.size()-1; i++) {
				PTDFRec rec1 = list.get(i);
				PTDFRec rec2 = list.get(i+1);
				if (rec1.ptdf < rec2.ptdf) {
					done = false;
					PTDFRec buffer = new PTDFRec();
					buffer.busId = rec1.busId;
					buffer.ptdf = rec1.ptdf;
					rec1.busId = rec2.busId;
					rec1.ptdf = rec2.ptdf;
					rec2.busId = buffer.busId;
					rec2.ptdf = buffer.ptdf;
				}
			}
		}		
	}
}