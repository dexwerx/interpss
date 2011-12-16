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

package org.interpss.display;

import org.apache.commons.math.complex.Complex;
import org.interpss.display.impl.AclfOut_BusStyle;
import org.interpss.display.impl.AclfOut_PSSE;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.FlowInterface;
import com.interpss.core.aclf.FlowInterfaceLimit;
import com.interpss.core.aclf.adj.AdjControlType;
import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.aclf.adj.PQBusLimit;
import com.interpss.core.aclf.adj.PSXfrPControl;
import com.interpss.core.aclf.adj.PVBusLimit;
import com.interpss.core.aclf.adj.RemoteQBus;
import com.interpss.core.aclf.adj.RemoteQControlType;
import com.interpss.core.aclf.adj.TapControl;
import com.interpss.core.aclf.adj.XfrTapControlType;
import com.interpss.core.aclf.adpter.GenBusAdapter;
import com.interpss.core.aclf.adpter.PSXfrAdapter;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.path.NetPathWalkDirectionEnum;
import com.interpss.core.algo.sec.BranchRatingAdapter;
import com.interpss.core.algo.sec.SecAnalysisViolationType;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.common.visitor.IPVBusLimitVisitor;
import com.interpss.core.datatype.Mismatch;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.OriginalDataFormat;
import com.interpss.core.util.CoreUtilFunc;

public class AclfOutFunc {
	public static enum LfResultFormat { Summary, BusStyle };
	public static enum BusIdStyle { BusId_No, BusId_Name };
	
	public static String getBusId(AclfBus bus, OriginalDataFormat fmt) {
		if (fmt == OriginalDataFormat.CIM)
			return "Bus" + bus.getNumber();
		return bus.getId();
	}  
	
	public static String formatKV(double kv) {
		if (kv > 1000.0)
			return String.format("%6.1f ", kv);
		else if (kv > 100.0)
			return String.format("%6.2f ", kv);
		else	
			return String.format("%6.3f ", kv);
	}
  
	public static String lf4Google(AclfNetwork net) {
		if (net.getOriginalDataFormat() == OriginalDataFormat.CIM)
			return loadFlowSummary(net);
		return lfResultsBusStyle(net);
	}
	
	/*
	 *   PSS/E output
	 *   ============
	 */
	public static String lfResultsPsseStyle(AclfNetwork net, AclfOut_PSSE.Format format) {
		return AclfOut_PSSE.lfResults(net, format);
	}

	
	/*
	 *   Bus Style output
	 *   ================
	 */
	public static String lfResultsBusStyle(AclfNetwork net) {
		return AclfOut_BusStyle.lfResultsBusStyle(net, BusIdStyle.BusId_No);
	}

	public static String lfResultsBusStyle(AclfNetwork net, BusIdStyle style) {
		return AclfOut_BusStyle.lfResultsBusStyle(net, style);
	}

	/*
	 *   Summary output
	 *   ==============
	 */
	public static String _loadFlowSummary(AclfNetwork net) {
		final StringBuffer str = new StringBuffer("");
		try {
			str.append("\n                          Load Flow Summary\n");
			str.append(AclfOutFunc.maxMismatchToString(net, "") + "\n");
			if (net.getOriginalDataFormat() == OriginalDataFormat.CIM) {
				str.append("     BusID          Code             Volt(pu)   Angle(deg)     P(pu)     Q(pu)                RdfId\n");
				str.append("  ------------------------------------------------------------------------------------------------------------------\n");
			}
			else {
				str.append("     BusID       Name           Code           Volt(pu)   Angle(deg)     P(pu)     Q(pu)\n");
				str.append("  ---------------------------------------------------------------------------------------\n");
			}
				 
			net.forEachAclfBus(new IAclfBusVisitor() {
				public void visit(AclfBus bus) {
					if (bus.isActive()) {
						Complex busPQ = bus.getPowerInjection();
						str.append("  ");
						str.append(String.format("%-12s ", getBusId(bus, bus.getNetwork().getOriginalDataFormat())));
						str.append(String.format("%-10s ", bus.getName()));
						str.append(String.format("%-17s ", bus.code2String()));
						str.append(String.format("%10.5f   ", bus.getVoltageMag(UnitType.PU)));
						str.append(String.format("%9.1f   ", bus.getVoltageAng(UnitType.Deg)));
						str.append(String.format("%10.4f", busPQ.getReal()));
						str.append(String.format("%10.4f", busPQ.getImaginary()));
						if (bus.getNetwork().getOriginalDataFormat() == OriginalDataFormat.CIM) 
							str.append(String.format("   %s", bus.getId()));
						str.append("\n");
					}
				}
			});
			
//			for (Bus b : net.getBusList()) {
//				AclfBus bus = (AclfBus) b;
//				if (bus.isActive()) {
//					GenBusAdapter genBus = (GenBusAdapter) bus
//							.getAdapter(GenBusAdapter.class);
//					Complex busPQ = genBus.getGenResults(UnitType.PU);
//					busPQ = busPQ.subtract(genBus.getLoadResults(UnitType.PU));
//					if (bus.isCapacitor()) {
//						CapacitorBusAdapter cap = (CapacitorBusAdapter) bus
//								.getAdapter(CapacitorBusAdapter.class);
//						busPQ = busPQ.add(new Complex(0.0, cap.getQResults(bus
//								.getVoltageMag(), UnitType.PU)));
//					}
//					str.append("  ");
//					str.append(String.format("%-12s  ", getBusId(bus, net.getOriginalDataFormat())));
//					str.append(String.format("%-17s ", bus.code2String()));
//					str.append(String.format("%10.5f   ", bus.getVoltageMag(UnitType.PU)));
//					str.append(String.format("%9.1f   ", bus.getVoltageAng(UnitType.Deg)));
//					str.append(String.format("%10.4f", busPQ.getReal()));
//					str.append(String.format("%10.4f", busPQ.getImaginary()));
//					if (net.getOriginalDataFormat() == OriginalDataFormat.CIM) 
//						str.append(String.format("   %s", bus.getId()));
//					str.append("\n");
//				}
//			}
		} catch (Exception emsg) {
			str.append(emsg.toString());
		}

		str.append(branchMvaRatingViolationList(net));

		return str.toString();
	}

	public static String loadLossAllocation(AclfNetwork net) {
		final StringBuffer str = new StringBuffer("");
		final double lossMW = net.totalLoss(UnitType.mVA).getReal();
		final double lossPU = net.totalLoss(UnitType.PU).getReal();

		str.append("\n                          Load Loss Allocation\n");
		str.append("\n                       Total Loss = " + Number2String.toStr("####0.00", lossMW) + " MW\n\n");
		str.append("            BusID          LossAllocFactor         Allocated Loss(MW)\n");
		str.append("  ------------------------------------------------------------------------\n");
		
		
		net.forEachAclfBus(new IAclfBusVisitor() {
			public void visit(AclfBus bus) {
				if ( bus.getLossPFactor(NetPathWalkDirectionEnum.ALONG_PATH, lossPU) > 0.0 && 
						(bus.isLoad() || bus.isSwing())) { 
					str.append(lossString(bus, NetPathWalkDirectionEnum.ALONG_PATH, lossMW, lossPU));
				}
			}
		});
//		for (Bus bus : net.getBusList()) {
//			AclfBus aclfBus = (AclfBus)bus;
//			if ( aclfBus.getLossPFactor(ActivePowerWalkDirection.SOURC2_LOAD, lossPU) > 0.0 && 
//					(aclfBus.isLoad() || aclfBus.isSwing())) { 
//				str.append(lossString(aclfBus, ActivePowerWalkDirection.SOURC2_LOAD, lossMW, lossPU));
//			}
//  		}		
		return str.toString();
	}
	
	public static String genLossAllocation(AclfNetwork net) {
		StringBuffer str = new StringBuffer("");
		double lossMW = net.totalLoss(UnitType.mVA).getReal();
		double lossPU = net.totalLoss(UnitType.PU).getReal();

		str.append("\n                          Gen Loss Allocation\n");
		str.append("\n                       Total Loss = " + Number2String.toStr("####0.00", lossMW) + " MW\n\n");
		str.append("            BusID          LossAllocFactor         Allocated Loss(MW)\n");
		str.append("  ------------------------------------------------------------------------\n");
		for (Bus bus : net.getBusList()) {
			AclfBus aclfBus = (AclfBus)bus;
			if (aclfBus.isGen() && aclfBus.getLossPFactor(NetPathWalkDirectionEnum.OPPOSITE_PATH, lossPU) > 0.0) { 
				str.append(lossString(aclfBus, NetPathWalkDirectionEnum.OPPOSITE_PATH, lossMW, lossPU));
			}
  		}		
		
		return str.toString();
	}
	
	private static String lossString(AclfBus aclfBus, NetPathWalkDirectionEnum direction, double lossMW, double lossPU) {
		StringBuffer str = new StringBuffer("");
		str.append(Number2String.toStr(12, " "));
		str.append(Number2String.toStr(-12, aclfBus.getId()) + "  ");
		str.append(Number2String.toStr(2, " "));
		str.append(Number2String.toStr("####0.000", aclfBus.getLossPFactor(direction, lossPU)));
		str.append(Number2String.toStr(17, " "));
		str.append(Number2String.toStr("####0.00", aclfBus.getLossPFactor(direction, lossPU)*lossMW));
		str.append("\n");
		return str.toString();
	}

	public static String loadFlowSummary(AclfNetwork net) {
		return loadFlowSummary(net, true);
	}
	
	public static String loadFlowSummary(AclfNetwork net, boolean includeAdj) {
		StringBuffer str = new StringBuffer(_loadFlowSummary((AclfNetwork) net));
		try {
			if (includeAdj) {
				if (net.hasPVBusLimit())
					str.append(pvBusLimitToString(net));

				if (net.hasPQBusLimit())
					str.append(pqBusLimitToString(net));

				if (net.hasRemoteQBus())
					str.append(remoteQBusToString(net));

				if (net.hasFunctionLoad())
					str.append(aclfFuncLoadToString(net));

				if (net.hasTapControl())
					str.append(tapVControlToString(net));

				if (net.hasPSXfrPControl())
					str.append(psXfrPControlToString(net));
			}
		} catch (Exception emsg) {
			str.append(emsg.toString());
		}
		return str.toString();
	}

	public static String maxMismatchToString(AclfNetwork net, String prefix) {
		try {
			double baseKVA = net.getBaseKva();
			String str = "\n"+prefix+"                         Max Power Mismatches\n"
					+ prefix+"             Bus              dPmax       Bus              dQmax\n"
					+ prefix+"            -------------------------------------------------------\n";
			Mismatch mis = net.maxMismatch(AclfMethod.NR);
			str += prefix+Number2String.toStr(12, " ");
			str += String.format("%-12s ", getBusId(mis.maxPBus, net.getOriginalDataFormat()));
			str += String.format("%12.6f  ", mis.maxMis.getReal());
			str += String.format("%-12s ", getBusId(mis.maxQBus, net.getOriginalDataFormat()));
			str += String.format("%12.6f (pu)\n", mis.maxMis.getImaginary());
			str += prefix+String.format("%24s", " ");
			str += String.format("%12.6f0 ", baseKVA* mis.maxMis.getReal());
			str += String.format("%14s", " ");
			str += String.format("%12.6f (kva)\n", baseKVA * mis.maxMis.getImaginary());
			return str;
		} catch (Exception emsg) {
			return emsg.toString();
		}
	}

	public static String voltageViolationReport(AclfNetwork net, double max, double min) {
		StringBuffer buf = new StringBuffer("");
		buf.append("\n");
		buf.append("                   Bus Voltage Violation Report   \n\n");
		buf.append(String.format("              Bus Voltage Limit: [%4.2f, %4.2f]\n", max, min));
		buf.append("\n");

		buf.append("         Bus Id      Area   Zone    Voltage     BaseCase   Dispatched      \n");
		buf.append("       ====================================================================\n");
		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.isActive() &&
					(bus.getVoltageMag() > max || bus.getVoltageMag() < min))
				buf.append(String.format("     %12s  %6d  %6d  %8.4f    %8s    %8s\n", 
						bus.getId(), bus.getArea().getNumber(), bus.getZone().getNumber(),
						bus.getVoltageMag(), bus.getGenCodeBaseCase(), bus.getGenCode()));
  		}		
		buf.append("\n");
		return buf.toString();
	}

	public static String branchMvaRatingViolationList(AclfNetwork net) {
		StringBuffer str = new StringBuffer("");
		if (net.hasBranchMavRatingViolation()) {
			str.append("\n\n");
			str
					.append("                        Branch MvaRating Violation List\n\n");
			str
					.append("         BranchID            MvaFlow       PF     Side    MvaRating1   MvaRating2   MvaRating3\n");
			str
					.append(" ---------------------     ------------ ------ -------- ------------ ------------ ------------\n");
			for (Branch b : net.getBranchList()) {
				AclfBranch bra = (AclfBranch) b;
				if (bra.isActive()) {
					BranchRatingAdapter adapter = (BranchRatingAdapter)b.getAdapter(BranchRatingAdapter.class);
					if (adapter.isRatingViolated(SecAnalysisViolationType.BRANCH_THERMAL_MVA_RATING, net.getBaseKva())) {
						str.append(Number2String.toStr(-25, bra.getId()));
						Complex mva = bra.powerFrom2To(UnitType.mVA);
						String side = "From";
						if (bra.powerFrom2To(UnitType.mVA).abs() < bra
								.powerTo2From(UnitType.mVA).abs()) {
							mva = bra.powerTo2From(UnitType.mVA);
							side = "To";
						}

						str.append("     " + Number2String.toStr("####0.0", mva.abs()));
						str.append("   " + Number2String.toStr("##0.0", 100.0*CoreUtilFunc.calPFactor(mva.getReal(), mva.getImaginary())) + "%");
						str.append("    " + Number2String.toStr(-4, side));
						str.append("      " + Number2String.toStr("####0.0", bra.getRatingMva1()));
						str.append("      " + Number2String.toStr("####0.0", bra.getRatingMva2()));
						str.append("      " + Number2String.toStr("####0.0", bra.getRatingMva3()));
						str.append("\n");
					}
				}
			}
		}
		return str.toString();
	}

	public static String interfaceFlowReport(AclfNetwork net, boolean offPeak, boolean violationReport) {
		StringBuffer buffer = new StringBuffer();
		if (violationReport) 
			buffer.append("                Flow Interface Violation Report\n\n");
		
		buffer.append("          Interface           Flow(pu)    Direction     Limit\n");
		buffer.append("    ============================================================\n");
		for (FlowInterface inf : net.getInterfaceList()) {
			FlowInterfaceLimit limit = offPeak? inf.getOffPeakLimit() : inf.getOnPeakLimit(); 
			if (inf.flowExport() > 0.0) {
				double f = inf.flowExport();
				double l = limit.getRefDirExportLimit();
				if (!violationReport || Math.abs(f) > Math.abs(l))
					buffer.append(String.format("     %-25s %7.2f      Export    %7.2f\n", inf.getId(), f, l));
			}
			else {
				double f = -inf.flowImport();
				double l = -limit.getOppsiteRefDirImportLimit();
				if (!violationReport || Math.abs(f) > Math.abs(l))
					buffer.append(String.format("     %-25s %7.2f      Import    %7.2f\n", inf.getId(), f, l));
			}
		}		
		return buffer.toString();
	}	

	public static String pvBusLimitToString(AclfNetwork net)
			throws Exception {
		final StringBuffer str = new StringBuffer("");

		str.append("\n\n");
		str.append("                  PV Bus Limit Adjustment/Control\n\n");
		str
				.append("      BusID     Vact     Vspec      Q      Qmax     Qmin   Status\n");
		str
				.append("     -------- -------- -------- -------- -------- -------- ------\n");

		net.forEachPVBusLimit(new IPVBusLimitVisitor() {
			public void visit(PVBusLimit pv) {
				GenBusAdapter genBus = pv.getParentBus().toGenBus();
				str.append(Number2String.toStr(5, " "));
				str.append(Number2String.toStr(-8, getBusId(pv.getParentBus(), 
						pv.getParentBus().getNetwork().getOriginalDataFormat())));
				str.append(Number2String.toStr("###0.0000", pv.getParentBus()
						.getVoltageMag(UnitType.PU)));
				str.append(Number2String.toStr("###0.0000", pv
						.getVSpecified(UnitType.PU)));
				str.append(Number2String.toStr("#####0.00", genBus.getGenResults(
						UnitType.PU).getImaginary()));
				str.append(Number2String.toStr("#####0.00", pv.getQLimit(
						UnitType.PU).getMax()));
				str.append(Number2String.toStr("#####0.00", pv.getQLimit(
						UnitType.PU).getMin()));
				str.append(Number2String.toStr(6, pv.isActive() ? "on" : "off")
						+ "\n");
			}
		});
		
//		for (Bus b : net.getBusList()) {
//			AclfBus bus = (AclfBus)b;
//			if (bus.isPVBusLimit()) {
//				PVBusLimit pv = (PVBusLimit)bus.getBusControl();
//				GenBusAdapter genBus = (GenBusAdapter) pv.getParentBus().getAdapter(
//						GenBusAdapter.class);
//				str.append(Number2String.toStr(5, " "));
//				str.append(Number2String.toStr(-8, getBusId(pv.getParentBus(), net.getOriginalDataFormat())));
//				str.append(Number2String.toStr("###0.0000", pv.getParentBus()
//						.getVoltageMag(UnitType.PU)));
//				str.append(Number2String.toStr("###0.0000", pv
//						.getVSpecified(UnitType.PU)));
//				str.append(Number2String.toStr("#####0.00", genBus.getGenResults(
//						UnitType.PU).getImaginary()));
//				str.append(Number2String.toStr("#####0.00", pv.getQLimit(
//						UnitType.PU).getMax()));
//				str.append(Number2String.toStr("#####0.00", pv.getQLimit(
//						UnitType.PU).getMin()));
//				str.append(Number2String.toStr(6, pv.isActive() ? "on" : "off")
//						+ "\n");
//			}
//		}
		return str.toString();
	}

	public static String pqBusLimitToString(AclfNetwork net)
			throws Exception {
		StringBuffer str = new StringBuffer("");

		str.append("\n\n");
		str.append("                  PQ Bus Limit Adjustment/Control\n\n");
		str
				.append("      BusID     Qact     Qspec      V      Vmax     Vmin   Status\n");
		str
				.append("     -------- -------- -------- -------- -------- -------- ------\n");

		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.isPQBusLimit()) {
				PQBusLimit pq = bus.getPQBusLimit();
				GenBusAdapter genBus = pq.getParentBus().toGenBus();
				str.append(Number2String.toStr(5, " "));
				str.append(Number2String.toStr(-8, getBusId(pq.getParentBus(), net.getOriginalDataFormat())) + " ");
				str.append(Number2String.toStr("####0.00", genBus.getGenResults(
						UnitType.PU).getImaginary())
						+ " ");
				str.append(Number2String.toStr("####0.00", pq.getQSpecified(
						UnitType.PU))
						+ " ");
				str.append(Number2String.toStr("##0.0000", pq.getParentBus()
						.getVoltageMag(UnitType.PU))
						+ " ");
				str.append(Number2String.toStr("##0.0000", pq
						.getVLimit(UnitType.PU).getMax())
						+ " ");
				str.append(Number2String.toStr("##0.0000", pq
						.getVLimit(UnitType.PU).getMin())
						+ " ");
				str.append(Number2String.toStr(5, pq.isActive() ? "on" : "off")
						+ "\n");
			}
		}
		return str.toString();
	}

	public static String remoteQBusToString(AclfNetwork net)
			throws Exception {
		StringBuffer str = new StringBuffer("");

		str.append("\n\n");
		str.append("                Remote Q Voltage Adjustment/Control\n\n");
		str
				.append("       VcBus    Type    ReQBus/Branch   Actual    Spec       Q      Qmax     Qmin   Status\n");
		str
				.append("     -------- -------- --------------- -------- -------- -------- -------- -------- ------\n");

		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.isRemoteQBus()) {
				RemoteQBus re = bus.getRemoteQBus();
				GenBusAdapter genBus = re.getParentBus().toGenBus();
				str.append(Number2String.toStr(5, " "));
				str.append(Number2String.toStr(-9, getBusId(re.getParentBus(), net.getOriginalDataFormat())));
				str.append(Number2String.toStr(-9,
										(re.getControlType() == RemoteQControlType.BUS_VOLTAGE ? " Voltage"
												: "MvarFlow")));
				str.append(Number2String.toStr(15,
						re.getControlType() == RemoteQControlType.BUS_VOLTAGE ? re
								.getRemoteBus().getId() : re.getRemoteBranch()
								.getId()));
				str.append(Number2String.toStr("###0.0000",
						re.getControlType() == RemoteQControlType.BUS_VOLTAGE ? re
								.getRemoteBus().getVoltageMag(UnitType.PU) : re
								.getMvarFlowCalculated(re.getRemoteBranch(), UnitType.PU)));
				str.append(Number2String.toStr("###0.0000", re.getVSpecified(UnitType.PU)));
				str.append(Number2String.toStr("#####0.00", genBus.getGenResults(
						UnitType.PU).getImaginary()));
				str.append(Number2String.toStr("#####0.00", re.getQLimit(
						UnitType.PU).getMax()));
				str.append(Number2String.toStr("#####0.00", re.getQLimit(
						UnitType.PU).getMin()));
				str.append(Number2String.toStr(6, re.isActive() ? "on" : "off")	+ "\n");
			}
		}
		return str.toString();
	}

	public static String aclfFuncLoadToString(AclfNetwork net)
			throws Exception {
		StringBuffer str = new StringBuffer("");

		double baseKVA = net.getBaseKva();

		str.append("\n\n");
		str.append("                  Aclf FuncLoad Adjustment/Control\n\n");
		str
				.append("      BusID     Pact     Qact       V      P(0)     Q(0)   Status\n");
		str
				.append("     -------- -------- -------- -------- -------- -------- ------\n");

		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.getFunctionLoad() != null) {
				FunctionLoad x = bus.getFunctionLoad();
				str.append(Number2String.toStr(5, " "));
				str.append(Number2String.toStr(-8, x.getParentBus().getId()) + " ");
				double vpu = x.getParentBus().getVoltage().abs();
				str.append(Number2String.toStr("##0.0000", x.getP().getLoad(vpu,
						UnitType.PU, baseKVA))
						+ " ");
				str.append(Number2String.toStr("##0.0000", x.getQ().getLoad(vpu,
						UnitType.PU, baseKVA))
						+ " ");
				str.append(Number2String.toStr("##0.0000", x.getParentBus()
						.getVoltageMag(UnitType.PU))
						+ " ");
				str.append(Number2String.toStr("##0.0000", x.getP().getLoad0(
						UnitType.PU, baseKVA))
						+ " ");
				str.append(Number2String.toStr("##0.0000", x.getQ().getLoad0(
						UnitType.PU, baseKVA))
						+ " ");
				str.append(Number2String.toStr(5, x.isActive() ? "on" : "off")
						+ "\n");
			}
		}
		return str.toString();
	}

	public static String tapVControlToString(AclfNetwork net)
			throws Exception {
		StringBuffer str = new StringBuffer("");

		double baseKva = net.getBaseKva();

		str.append("\n\n");
		str
				.append("                          Tap Voltage Adjustment/Control\n\n");
		str
				.append("          BranchID     VC BusID  Actual   Spec/Range     Tap  Tmax  Tmin  StepSize Status\n");
		str
				.append("     ----------------- -------- -------- -------------- ----- ----- -----   -----  ------\n");

		for (Branch b : net.getBranchList()) {
			AclfBranch branch = (AclfBranch)b;
			if (branch.isTapControl()) {
				TapControl x = branch.getTapControl();
				str.append(Number2String.toStr(5, " "));
				str.append(Number2String.toStr(-17, x.getParentBranch().getId())
						+ " ");

				if (x.getControlType() == XfrTapControlType.BUS_VOLTAGE) {
					str.append(Number2String.toStr(-8, x.getVcBus().getId()) + " ");
					str.append(Number2String.toStr("##0.0000", x.getVcBus()
							.getVoltageMag(UnitType.PU))
							+ " ");
					if (x.getFlowControlType() == AdjControlType.POINT_CONTROL)
						str.append(Number2String.toStr("##0.0000", x
								.getVSpecified(UnitType.PU))
								+ " ");
					else
						str.append(x.getControlRange() + " ");
				} else {
					str.append(Number2String.toStr(-8, " "));
					str.append(Number2String.toStr("##0.0000", x
							.getMvarFlowCalculated(UnitType.PU, baseKva))
							+ " ");
					if (x.getFlowControlType() == AdjControlType.POINT_CONTROL)
						str.append("   "
								+ Number2String.toStr("##0.0000", x
										.getMvarSpecified(UnitType.PU, baseKva))
								+ "    ");
					else
						str.append(x.getControlRange() + " ");
				}

				str.append(Number2String.toStr("0.000",
						(x.isControlOnFromSide() ? x.getParentBranch()
								.getFromTurnRatio() : x.getParentBranch()
								.getToTurnRatio()))
						+ " ");
				str.append(Number2String.toStr("0.000", x.getTurnRatioLimit().getMax())
						+ " ");
				str.append(Number2String.toStr("0.000", x.getTurnRatioLimit().getMin())
						+ "   ");
				str.append(Number2String.toStr("####0", x.getTapStepSize()) + "  ");
				str.append(Number2String.toStr(6, x.isActive() ? "on" : "off")
						+ "\n");
			}
		}
		return str.toString();
	}

	public static String psXfrPControlToString(AclfNetwork net)
			throws Exception {
		StringBuffer str = new StringBuffer("");

		double baseKVA = net.getBaseKva();

		str.append("\n\n");
		str.append("                  PSXfr Power Adjustment/Control\n\n");
		str
				.append("          BranchID       Pact     Spec/Range    Ang   Max   Min  Status\n");
		str
				.append("     ----------------- -------- -------------- ----- ----- ----- ------\n");

		for (Branch b : net.getBranchList()) {
			AclfBranch branch = (AclfBranch)b;
			if (branch.isTapControl()) {
				PSXfrPControl x = branch.getPSXfrPControl();
				str.append(Number2String.toStr(5, " "));
				str.append(Number2String.toStr(-17, x.getParentBranch().getId())
						+ " ");
				str.append(Number2String.toStr("##0.0000",
						(x.isControlOnFromSide() ? x.getParentBranch().powerFrom2To(
								UnitType.PU).getReal() : x.getParentBranch()
								.powerTo2From(UnitType.PU).getReal()))
						+ " ");

				if (x.getFlowControlType() == AdjControlType.POINT_CONTROL)
					str.append(Number2String.toStr("   " + "##0.0000", x
							.getPSpecified(UnitType.PU, baseKVA))
							+ "    ");
				else
					str.append(x.getControlRange() + " ");

				PSXfrAdapter psXfr = x.getParentBranch().toPSXfr();
				str.append(Number2String.toStr("#0.00", psXfr
						.getFromAngle(UnitType.Deg))
						+ " ");
				str.append(Number2String.toStr("#0.00", x.getAngLimit(UnitType.Deg)
						.getMax())
						+ " ");
				str.append(Number2String.toStr("#0.00", x.getAngLimit(UnitType.Deg)
						.getMin())
						+ " ");
				str.append(Number2String.toStr(6, x.isActive() ? "on" : "off")
						+ "\n");
			}
		}
		return str.toString();
	}

	public static String yMatrixToString(AclfNetwork net) {
		String str = "\n        Y-matrix\n";

		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus) b;
			if (bus.isActive()) {
				str += "bus: " + bus.getId() + "    " + "yii: "
						+ bus.yii().toString() + "\n";
				for (Branch br : bus.getBranchList()) {
					AclfBranch bra = (AclfBranch) br;
					if (bra.isActive()) {
						str += "          "
								+ bra.getId()
								+ "   "
								+
								// " yff:" + Num2Str.toStr(bra.yff()) +
								// " ytt:" + Num2Str.toStr(bra.ytt()) +
								"  yft:" + Number2String.toStr(bra.ytf())
								+ "  ytf:" + Number2String.toStr(bra.yft())
								+ "\n";
					}
				}
			}
		}
		return str + "\n";
	}

	public static String b11MatrixToString(AclfNetwork net) {
		try {
			String str = "\n        B11-matrix\n";

			for (Bus b : net.getBusList()) {
				AclfBus bus = (AclfBus) b;
				if (bus.isActive()) {
					str += "bus: " + bus.getId() + "    " + "bii: "
							+ Number2String.toStr(bus.b11ii()) + "\n";
					for (Object br : bus.getBranchList()) {
						AclfBranch bra = (AclfBranch) br;
						if (bra.isActive())
							str += "          " + bra.getId() + "   " + "bij: "
								+ Number2String.toStr(bra.b11ft()) + "\n";
					}
				}
			}
			return str + "\n";
		} catch (Exception emsg) {
			return emsg.toString();
		}
	}

	public static String b1MatrixToString(AclfNetwork net) {
		String str = "\n        B1-matrix\n";

		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus) b;
			if (bus.isActive()) {
				str += "bus: " + bus.getId() + "    " + "bii: "
						+ Number2String.toStr(bus.b1ii()) + "\n";
				for (Branch br : bus.getBranchList()) {
					AclfBranch bra = (AclfBranch) br;
					if (bra.isActive())
						str += "          " + bra.getId() + "   " + "bij: "
							+ Number2String.toStr(bra.b1ft()) + "\n";
				}

			}
		}
		return str + "\n";
	}
}