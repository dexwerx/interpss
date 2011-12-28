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

import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;
import org.interpss.xml.schema.AreaTransferAnalysisXmlType;
import org.interpss.xml.schema.BranchRecXmlType;
import org.interpss.xml.schema.BusRecXmlType;
import org.interpss.xml.schema.DclfBranchSensitivityXmlType;
import org.interpss.xml.schema.DclfBusSensitivityXmlType;
import org.interpss.xml.schema.SenAnalysisBusRecXmlType;
import org.interpss.xml.schema.SenBusAnalysisDataType;

import com.interpss.common.util.NetUtilFunc;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.DclfAlgorithm;
import com.interpss.core.dclf.SenAnalysisType;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

public class DclfOutFunc {
	/**
	 * dclf branch title 
	 * 
	 * @return
	 */
	public static String branchFlowTitle() {
		String str = "\n";
		str += "       FromId->ToId     Power Flow(Mw)   MWLimit    Loading%  Violation\n";
		str += "=========================================================================";
		return str;
		
	}
	
	/**
	 * dclf branch flow details
	 * 
	 * @param algo
	 * @return
	 */
	public static String branchFlow(DclfAlgorithm algo, double threshhold) {
		StringBuffer str = new StringBuffer("\n");
		str.append(branchFlowTitle() + "\n");
		for (Branch bra : algo.getAclfNetwork().getBranchList()) {
			AclfBranch aclfBra = (AclfBranch)bra;
			double baseMva = algo.getAclfNetwork().getBaseKva() * 0.001;
			double fAng = algo.getBusAngle(aclfBra.getFromBus().getSortNumber());
			double tAng = algo.getBusAngle(aclfBra.getToBus().getSortNumber());
			double mwFlow = (fAng-tAng)*aclfBra.b1ft()*baseMva;
			double limitMva = aclfBra.getRatingMva1();
			double loading = Math.abs(100*(mwFlow)/limitMva);
			boolean v = Math.abs(mwFlow) > limitMva;
			if (loading >= threshhold) {
				str.append(Number2String.toFixLengthStr(22, aclfBra.getId()) + "     "	+ String.format("%8.2f",mwFlow));
				str.append("     " + String.format("%8.2f", limitMva)); 
				if (limitMva > 0.0)
					str.append("      " + String.format("%5.1f", loading) + "      " + (v? "x" : " ") + "\n");
			}
		}		
		return str.toString();
	}

	/**
	 * Out put Dclf voltage angle results
	 * 
	 * @param aclfNet
	 * @param algo
	 * @return
	 */
	public static String dclfResults(DclfAlgorithm algo, boolean branchViolation) {
		StringBuffer str = new StringBuffer("\n\n");
		str.append("      DC Loadflow Results\n\n");
		str.append("   Bud Id       VoltAng(deg)     Gen/Load\n");
		str.append("============================================\n");
		double baseMva = algo.getAclfNetwork().getBaseKva() * 0.001;
		for (Bus bus : algo.getAclfNetwork().getBusList()) {
			AclfBus aclfBus = (AclfBus)bus; 
			int n = bus.getSortNumber();
			double angle = Math.toDegrees(algo.getBusAngle(n));
			double p = (aclfBus.getGenP() - aclfBus.getLoadP()) * baseMva; 
			str.append(Number2String.toFixLengthStr(8, bus.getId()) + "        "
					+ String.format("%8.2f",angle) + "         "
					+ ((p != 0.0)? String.format("%8.2f",p) : "") 
					+ "\n");
		}

		str.append(branchFlow(algo, branchViolation? 100.0 : 0.0));
		
		return str.toString();
	}

	/**
	 * line outage analysis output title
	 * 
	 * @param name trade name
	 * @param mw trade amount 
	 * @return
	 */
	public static String lineOutageAnalysisTitle(String name, String branchId) {
		String str = "\n\n";
		str += "                   Line Outage '" + name + "'  Branch Id: " + branchId + "\n\n";

		str += "          Branch Id         ShiftFactor      MwFlow     MWLimit   DeratedLimit  Loading%  Violation\n";
		str += "========================================================================================================";
		return str;
	}

	/**
	 * line outage analysis output details
	 * 
	 * @param aclfBra monitoring branch
	 * @param algo dclf algorithm
	 * @param mw trade amount in mw
	 * @param f trade shifting factor
	 * @param dfactor derating factor
	 * @return
	 */
	public static String lineOutageAnalysisBranchFlow(AclfBranch aclfBra, DclfAlgorithm algo, double mw, double f) {
		AclfNetwork net = algo.getAclfNetwork();
		double baseMva = net.getBaseKva() * 0.001;

		String str = "";

		double fAng = algo.getBusAngle(aclfBra.getFromBus().getSortNumber());
		double tAng = algo.getBusAngle(aclfBra.getToBus().getSortNumber());
		double mwFlow = (fAng-tAng)*aclfBra.b1ft()*baseMva;
		
		double limitMva = aclfBra.getRatingMva1();
		double deratedLimit = limitMva - mw*f;
		boolean v = mwFlow > deratedLimit;
		str += Number2String.toFixLengthStr(22, aclfBra.getId())
					+ "      " + String.format("%9.3f", f) 
					+ "      " + String.format("%8.2f", mwFlow) 
					+ "    " + String.format("%8.2f", limitMva) 
					+ "    " + String.format("%8.2f", deratedLimit); 
			if (deratedLimit > 0.0)
				str += "       " + String.format("%5.1f", 100*(mwFlow)/deratedLimit) 
				+ "      " + (v? "x" : " "); 
		return str;
	}

	/**
	 * trade analysis output title
	 * 
	 * @param name trade name
	 * @param mw trade amount 
	 * @return
	 */
	public static String tradeAnalysisTitle(String name, double mw) {
		String str = "\n\n";
		str += "                    Trade '" + name + "'  Amount (MW) : " + String.format("%8.2f", mw) + "\n\n";

		str += "          Branch Id         ShiftFactor      BaseCaseMw   PredictedMW    MWLimit    Loading%  Violation\n";
		str += "========================================================================================================";
		return str;
	}

	/**
	 * trade analysis output details
	 * 
	 * @param aclfBra monitoring branch
	 * @param algo dclf algorithm
	 * @param mw trade amount in mw
	 * @param f trade shifting factor
	 * @param dfactor derating factor
	 * @return
	 */
	public static String tradeAnalysisBranchFlow(AclfBranch aclfBra, DclfAlgorithm algo, 
							double mw, double f, double dfactor) {
		AclfNetwork net = algo.getAclfNetwork();
		double baseMva = net.getBaseKva() * 0.001;

		String str = "";

		double fAng = algo.getBusAngle(aclfBra.getFromBus().getSortNumber());
		double tAng = algo.getBusAngle(aclfBra.getToBus().getSortNumber());
		double mwFlow = (fAng-tAng)*aclfBra.b1ft()*baseMva;
		
		double newMva = mwFlow + mw * f;
		double limitMva = aclfBra.getRatingMva1() * dfactor;
		boolean v = newMva > limitMva;
		str += Number2String.toFixLengthStr(22, aclfBra.getId())
					+ "      " + String.format("%9.3f", f) 
					+ "        " + String.format("%8.2f", mwFlow) 
					+ "      " + String.format("%8.2f", newMva) 
					+ "    " + String.format("%8.2f", limitMva); 
			if (limitMva > 0.0)
				str += "       " + String.format("%5.1f", 100*(newMva)/limitMva) 
				+ "      " + (v? "x" : " "); 
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
			DclfBusSensitivityXmlType sen, DclfAlgorithm algo) {
		String busId = sen.getInjectBusList().getInjectBus().get(0).getBusId();
		String str = "\n\n";
		str += "  Power-Angle Sensitivity\n\n";
		str += "   Inject BusId : " + busId + "\n\n";
		str += "   Bud Id       dAng/dP\n";
		str += "=================================\n";
		for (BusRecXmlType bus : sen.getBus()) {
			double pang = algo.calBusSensitivity(SenAnalysisType.PANGLE, busId,
					bus.getBusId());
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
			DclfBusSensitivityXmlType sen, DclfAlgorithm algo) {
		String busId = sen.getInjectBusList().getInjectBus().get(0).getBusId();
		String str = "\n\n";
		str += "   Q-Voltage Sensitivity\n\n";
		str += "    Inject BusId : " + busId + "\n\n";
		str += "   Bud Id         dV/dQ\n";
		str += "=================================\n";
		for (BusRecXmlType bus : sen.getBus()) {
			double x = algo.calBusSensitivity(SenAnalysisType.QVOLTAGE, busId, bus.getBusId());
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
			DclfBranchSensitivityXmlType gsFactor, DclfAlgorithm algo) {
		String busId = gsFactor.getInjectBusList().getInjectBus().get(0).getBusId();
		String str = "\n\n";
		str += "   Generator Shift Factor\n\n";
		str += "    Inject BusId : " + busId + "\n\n";
		str += "       Branch Id          GSF\n";
		str += "=========================================\n";
		for (BranchRecXmlType branch : gsFactor.getBranch()) {
			double gsf = algo.calGenShiftFactor(busId, branch.getFromBusId(), branch.getToBusId(), branch.getCircuitNumber());
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
			DclfBranchSensitivityXmlType tdFactor, DclfAlgorithm algo) {
		String str = "\n\n";
		str += "   Power Transfer Distribution Factor";
		if (tdFactor.getInjectBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			String inBusId = tdFactor.getInjectBusList().getInjectBus().get(0).getBusId();
			str += "\n\n    Inject BusId   : " + inBusId + "\n";
			str += withdrawBusInfo(tdFactor);
			str += "       Branch Id          PTDF\n";
			str += "========================================\n";
			for (BranchRecXmlType branch : tdFactor.getBranch()) {
				double ptdf = calPTDFactor(tdFactor, algo, branch, inBusId);
				str += Number2String.toFixLengthStr(16, branch.getFromBusId()
						+ "->" + branch.getToBusId())
						+ "       " + Number2String.toStr(ptdf) + "\n";
			}
		}
		else {
			for (BranchRecXmlType branch : tdFactor.getBranch()) {
				str += "\n\n    Branch Id   : " + Number2String.toFixLengthStr(16, branch.getFromBusId()
						+ "->" + branch.getToBusId()) + "\n";
				str += withdrawBusInfo(tdFactor);
				str += "       Inject BusId          PTDF\n";
				str += "========================================\n";
				
				List<PTDFRec> list = new ArrayList<PTDFRec>();
				for (BusRecXmlType bus :  tdFactor.getInjectBusList().getInjectBus()){
					PTDFRec rec = new PTDFRec();
					rec.ptdf = calPTDFactor(tdFactor, algo, branch, bus.getBusId());
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
	
	/**
	 * out area transfer analysis results
	 * 
	 * @param areaTransfer XML sensitivity calculation records
	 * @param algo
	 * @param msg
	 * @return
	 */
	public static String areaTransferAnalysisResults(
						AreaTransferAnalysisXmlType areaTransfer, DclfAlgorithm algo) {
		AclfNetwork net = algo.getAclfNetwork();
		
		String str = "\n\n";
		str += "   Area Transfer Distribution Factor\n\n";
		
		str += "   Area Transfer Amount (MW) : " + String.format("%8.2f", areaTransfer.getTransderAmountMW()) + "\n\n";
		str += "    From Area : " + areaTransfer.getFromArea().getAreaNo() + 
				"     To Area : " + areaTransfer.getToArea().getAreaNo() + "\n\n";
		
		str += getSenBusList("From Area", areaTransfer.getInjectBusList().getInjectBus());
		str += getSenBusList("To Area", areaTransfer.getWithdrawBusList().getWithdrawBus());
		str += "       Branch Id    AreaTransFactor   BaseCaseMva   PredictedMW    MWLimit    Loading%  Violation\n";
		str += "==================================================================================================\n";
		for (BranchRecXmlType branch : areaTransfer.getBranch()) {
			String fromBusId = branch.getFromBusId(), 
			       toBusId = branch.getToBusId(),
			       cirNumber = branch.getCircuitNumber();
			AclfBranch bra = net.getAclfBranch(NetUtilFunc.formBranchId(fromBusId, toBusId, cirNumber));
			if (branch == null) {
				return "Branch cannot be found, " + fromBusId+"->"+toBusId;
			}			
			
			double f = algo.getAreaTransferFactor(fromBusId, toBusId, cirNumber);
			double baseMva = bra.mvaFlow(UnitType.mVA);
			double newMva = baseMva + areaTransfer.getTransderAmountMW() * f;
			double limitMva = bra.getRatingMva1() * areaTransfer.getDeratingFactor();
			boolean v = newMva > limitMva;
			str += Number2String.toFixLengthStr(16, branch.getFromBusId()
					+ "->" + branch.getToBusId())
					+ "      " + String.format("%9.3f", f) 
					+ "        " + String.format("%8.2f", baseMva) 
					+ "       " + String.format("%8.2f", newMva) 
					+ "    " + String.format("%8.2f", limitMva); 
			if (limitMva > 0.0)
				str += "      " + String.format("%5.1f", 100*(newMva)/limitMva) 
					+ "      " + (v? "x" : " "); 
			str += "\n";
		}
		return str;
	}

	private static class PTDFRec {
		String busId;
		double ptdf = 0.0;
	}
	
	private static double calPTDFactor(DclfBranchSensitivityXmlType tdFactor, DclfAlgorithm algo, 
					BranchRecXmlType branch, String inBusId) {
		double ptdf = 0.0;
		if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			String wdBusId = tdFactor.getWithdrawBusList().getWithdrawBus().get(0).getBusId();
			ptdf = algo.pTransferDistFactor(inBusId, wdBusId,
							branch.getFromBusId(),	branch.getToBusId(), branch.getCircuitNumber());
		}	
		else 
			ptdf = algo.pTransferDistFactor(inBusId, 
							branch.getFromBusId(),	branch.getToBusId(), branch.getCircuitNumber());
		return ptdf;
	}
	
	private static String withdrawBusInfo(DclfBranchSensitivityXmlType tdFactor) {
		String str = "";
		if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			String wdBusId = tdFactor.getWithdrawBusList().getWithdrawBus().get(0).getBusId();
			str += "    Withdraw BusId : " + wdBusId + "\n\n";
		}
		else {
			str += getSenBusList("Withdraw", tdFactor.getWithdrawBusList().getWithdrawBus());
		}
		return str;
	}

	private static String getSenBusList(String s, List<SenAnalysisBusRecXmlType> busList) {
		String str = "    " + s + " Bus List : [";
		for (SenAnalysisBusRecXmlType bus : busList)
			str += " (" + bus.getBusId() + ", " + bus.getPercent() + "%)";
		str += " ]\n\n";
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