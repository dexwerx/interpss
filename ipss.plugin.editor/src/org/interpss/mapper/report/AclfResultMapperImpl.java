/*
 * @(#)AclfResultMapperImpl.java   
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

package org.interpss.mapper.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;
import org.interpss.report.bean.aclf.RptAclfBusStyleBean;
import org.interpss.report.bean.aclf.RptAclfMaxMismatchBean;
import org.interpss.report.bean.aclf.RptAclfSummaryBusBean;
import org.interpss.report.bean.aclf.RptFuncLoadBean;
import org.interpss.report.bean.aclf.RptPQLimitBean;
import org.interpss.report.bean.aclf.RptPSXfrPControlBean;
import org.interpss.report.bean.aclf.RptPVLimitBean;
import org.interpss.report.bean.aclf.RptRemoteQBusBean;
import org.interpss.report.bean.aclf.RptTapVControlBean;

import com.interpss.common.datatype.UnitHelper;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.aclf.adj.PQBusLimit;
import com.interpss.core.aclf.adj.PSXfrPControl;
import com.interpss.core.aclf.adj.PVBusLimit;
import com.interpss.core.aclf.adj.RemoteQBus;
import com.interpss.core.aclf.adj.RemoteQControlType;
import com.interpss.core.aclf.adj.TapControl;
import com.interpss.core.aclf.adj.XfrTapControlType;
import com.interpss.core.aclf.adpter.AclfCapacitorBus;
import com.interpss.core.aclf.adpter.AclfGenBus;
import com.interpss.core.aclf.adpter.AclfPSXformer;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.datatype.Mismatch;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

public class AclfResultMapperImpl {
	public static void mapAclfMaxMismatch(AclfNetwork net,
			RptAclfMaxMismatchBean bean) {
		double baseKVA = net.getBaseKva();
		Mismatch mis = net.maxMismatch(AclfMethod.NR);
		bean.setPMaxBusId(mis.maxPBus.getId());
		bean.setPMaxPu(Number2String
				.toStr("####0.000000", mis.maxMis.getReal()));
		bean.setPMaxKva(Number2String.toStr("####0.000", baseKVA
				* mis.maxMis.getReal()));
		bean.setQMaxBusId(mis.maxQBus.getId());
		bean.setQMaxPu(Number2String.toStr("####0.000000", mis.maxMis
				.getImaginary()));
		bean.setQMaxKva(Number2String.toStr("####0.000", baseKVA
				* mis.maxMis.getImaginary()));
	}

	public static Object[] createAclfSummaryBusBeanArray(AclfNetwork net) {
		List<RptAclfSummaryBusBean> list = new ArrayList<RptAclfSummaryBusBean>();
		try {
			double baseKVA = net.getBaseKva();
			for (Bus b : net.getBusList()) {
				AclfBus bus = (AclfBus) b;
				AclfGenBus genBus = bus.toGenBus();
				Complex busPQ = genBus.getGenResults(UnitType.PU)
						.subtract(genBus.getLoadResults(UnitType.PU));
				if (bus.isCapacitor()) {
					AclfCapacitorBus cap = bus.toCapacitorBus();
					busPQ = busPQ.add(new Complex(0.0, cap.getQResults(bus
							.getVoltageMag(), UnitType.PU)));
				}
				RptAclfSummaryBusBean bean = new RptAclfSummaryBusBean();
				bean.setBusId(new String(bus.getId()));
				bean.setBusCode(Number2String.toStr(-13, bus.code2String()));
				bean.setBusVolt(Number2String.toStr("####0.0000", bus
						.getVoltageMag(UnitType.PU)));
				bean.setBusAngle(Number2String.toStr("####0.00", bus
						.getVoltageAng(UnitType.Deg)));
				bean
						.setBusP(Number2String.toStr("####0.0000", busPQ
								.getReal()));
				bean.setBusQ(Number2String.toStr("####0.0000", busPQ
						.getImaginary()));
				list.add(bean);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			return null;
		}
		return list.toArray();
	}

	public static Object[] createAclfBusStyleBeanArray(AclfNetwork net) {
		List<RptAclfBusStyleBean> list = new ArrayList<RptAclfBusStyleBean>();
		try {
			double baseKVA = net.getBaseKva();
			for (Bus b : net.getBusList()) {
				AclfBus bus = (AclfBus) b;
				AclfGenBus genBus = bus.toGenBus();
				Complex busGen = genBus.getGenResults(UnitType.mVA);
				Complex busLoad = genBus.getLoadResults(UnitType.mVA);
				if (bus.isCapacitor()) {
					AclfCapacitorBus cap = bus.toCapacitorBus();
					busGen = busGen.add(new Complex(0.0, cap.getQResults(bus
							.getVoltageMag(), UnitType.PU)));
				}
				RptAclfBusStyleBean bean = new RptAclfBusStyleBean();
				bean.setBusId(bus.getId());
				bean.setBaseVolt(Number2String.toStr("#######0", bus
						.getBaseVoltage()));
				bean.setVoltMsg(Number2String.toStr("0.0000", bus
						.getVoltageMag(UnitType.PU)));
				bean.setVoltAng(Number2String.toStr("##0.0", bus
						.getVoltageAng(UnitType.Deg)));
				bean.setPGen(Number2String.toStr("####0.00", busGen.getReal()));
				bean.setQGen(Number2String.toStr("####0.00", busGen
						.getImaginary()));
				bean.setPLoad(Number2String
						.toStr("####0.00", busLoad.getReal()));
				bean.setQLoad(Number2String.toStr("####0.00", busLoad
						.getImaginary()));

				int cnt = 0;
				for (Object br : bus.getBranchList()) {
					AclfBranch bra = (AclfBranch) br;
					AclfBus busj;
					if (bus.equals(bra.getFromAclfBus()))
						busj = bra.getToAclfBus();
					else
						busj = bra.getFromAclfBus();

					Complex pq = new Complex(0.0, 0.0);
					double amp = 0.0, fromRatio = 1.0, toRatio = 1.0;
					if (bra.isActive()) {
						if (bus.equals(bra.getFromAclfBus())) {
							pq = bra.powerFrom2To(UnitType.mVA);
							amp = UnitHelper.iConversion(bra.current(UnitType.PU), bra.getFromAclfBus()
									.getBaseVoltage(), baseKVA, UnitType.PU,
									UnitType.Amp);
							if (bra.isXfr() || bra.isPSXfr()) {
								fromRatio = bra.getFromTurnRatio();
								toRatio = bra.getToTurnRatio();
							}
						} else {
							pq = bra.powerTo2From(UnitType.mVA);
							amp = UnitHelper.iConversion(bra.current(UnitType.PU), bra.getToAclfBus()
									.getBaseVoltage(), baseKVA, UnitType.PU,
									UnitType.Amp);
							if (bra.isXfr() || bra.isPSXfr()) {
								toRatio = bra.getFromTurnRatio();
								fromRatio = bra.getToTurnRatio();
							}
						}
					}
					if (cnt++ > 0) {
						bean = new RptAclfBusStyleBean();
						bean.setBusId("");
						bean.setBaseVolt("");
						bean.setVoltMsg("");
						bean.setVoltAng("");
						bean.setPGen("");
						bean.setQGen("");
						bean.setPLoad("");
						bean.setQLoad("");
					}
					bean.setToBusId(busj.getId());
					bean.setBranchP(Number2String.toStr("####0.00", pq
							.getReal()));
					bean.setBranchQ(Number2String.toStr("####0.00", pq
							.getImaginary()));
					bean.setBranchKA(Number2String
							.toStr("##0.0##", 0.001 * amp));
					bean.setXfrRatioFrom("");
					bean.setXfrRatioTo("");
					bean.setPsXfrAngle("");
					if (bra.isXfr() || bra.isPSXfr()) {
						if (fromRatio != 1.0)
							bean.setXfrRatioFrom(Number2String.toStr("0.0###",
									fromRatio));

						if (toRatio != 1.0)
							bean.setXfrRatioTo(Number2String.toStr("0.0###",
									toRatio));

						if (bra.isPSXfr()) {
							AclfPSXformer psXfr = bra.toPSXfr();
							bean.setPsXfrAngle(Number2String.toStr("##0.0",
									psXfr.getFromAngle(UnitType.Deg)));
						}
					}
					list.add(bean);
				} // while ...
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			return null;
		}
		return list.toArray();
	}

	public static Object[] createPVBusLimitBeanArray(AclfNetwork net) {
		List<RptPVLimitBean> list = new ArrayList<RptPVLimitBean>();
		double baseKva = net.getBaseKva();
		for (Bus b : net.getBusList()) { 
			AclfBus bus = (AclfBus)b;
			if (bus.isPVBusLimit()) {
				PVBusLimit pv = (PVBusLimit)bus.getBranchList();
				AclfGenBus genBus = pv.getParentBus().toGenBus();
				RptPVLimitBean bean = new RptPVLimitBean();
				bean.setBusId(Number2String.toStr(-8, pv.getParentBus().getId()));
				bean.setVact(Number2String.toStr("###0.0000", pv.getParentBus()
						.getVoltageMag(UnitType.PU)));
				bean.setVspec(Number2String.toStr("###0.0000", pv
						.getVSpecified(UnitType.PU)));
				bean.setQ(Number2String.toStr("#####0.00", genBus.getGenResults(
						UnitType.PU).getImaginary()));
				bean.setQmax(Number2String.toStr("#####0.00", pv.getQLimit(
						UnitType.PU).getMax()));
				bean.setQmin(Number2String.toStr("#####0.00", pv.getQLimit(
						UnitType.PU).getMin()));
				bean.setStatus(Number2String.toStr(6, pv.isActive() ? "on" : "off")
						+ "\n");
				list.add(bean);
			}
		}
		return list.toArray();
	}

	public static Object[] createPQBusLimitBeanArray(AclfNetwork net) {
		List<RptPQLimitBean> list = new ArrayList<RptPQLimitBean>();
		double baseKva = net.getBaseKva();
		for (Bus b : net.getBusList()) { 
			AclfBus bus = (AclfBus)b;
			if (bus.isPVBusLimit()) {
				PQBusLimit pq = (PQBusLimit)bus.getBranchList();
				AclfGenBus genBus = pq.getParentBus().toGenBus();
				RptPQLimitBean bean = new RptPQLimitBean();
				bean.setBusId(Number2String.toStr(-8, pq.getParentBus().getId()));
				bean.setQact(Number2String.toStr("####0.00", genBus.getGenResults(
						UnitType.PU).getImaginary()));
				bean.setQspec(Number2String.toStr("####0.00", pq.getQSpecified(
						UnitType.PU)));
				bean.setV(Number2String.toStr("##0.0000", pq.getParentBus()
						.getVoltageMag(UnitType.PU)));
				bean.setVmax(Number2String.toStr("##0.0000", pq.getVLimit(
						UnitType.PU).getMax()));
				bean.setVmin(Number2String.toStr("##0.0000", pq.getVLimit(
						UnitType.PU).getMin()));
				bean
						.setStatus(Number2String.toStr(5, pq.isActive() ? "on"
								: "off"));
				list.add(bean);
			}
		}
		return list.toArray();
	}

	public static Object[] createFunctionLoadBeanArray(AclfNetwork net) {
		List<RptFuncLoadBean> list = new ArrayList<RptFuncLoadBean>();
		double baseKva = net.getBaseKva();
		
		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.getFunctionLoad() != null) {
				FunctionLoad fload = bus.getFunctionLoad();
				double vpu = fload.getParentBus().getVoltage().abs();
				RptFuncLoadBean bean = new RptFuncLoadBean();
				bean.setBusId(fload.getParentBus().getId());
				bean.setPact(Number2String.toStr("##0.0000", fload.getP().getLoad(
						vpu, UnitType.PU, baseKva)));
				bean.setQact(Number2String.toStr("##0.0000", fload.getQ().getLoad(
						vpu, UnitType.PU, baseKva)));
				bean.setV(Number2String.toStr("##0.0000", fload.getParentBus()
						.getVoltageMag(UnitType.PU)));
				bean.setP0(Number2String.toStr("##0.0000", fload.getP().getLoad0(
						UnitType.PU, baseKva)));
				bean.setQ0(Number2String.toStr("##0.0000", fload.getQ().getLoad0(
						UnitType.PU, baseKva)));
				bean.setStatus(Number2String.toStr(5, fload.isActive() ? "on"
						: "off"));
				list.add(bean);
			}
		}
		return list.toArray();
	}

	public static Object[] createRemoteQBusBeanArray(AclfNetwork net) {
		List<RptRemoteQBusBean> list = new ArrayList<RptRemoteQBusBean>();
		for (Bus b : net.getBusList()) { 
			AclfBus bus = (AclfBus)b;
			if (bus.isPVBusLimit()) {
				RemoteQBus re = (RemoteQBus)bus.getBranchList();
				AclfGenBus genBus = re.getParentBus().toGenBus();
				RptRemoteQBusBean bean = new RptRemoteQBusBean();
				bean.setVcBusId(re.getParentBus().getId());
				bean.setType(Number2String.toStr(-9,
						(re.getControlType() == RemoteQControlType.BUS_VOLTAGE ? " Voltage"	: "MvarFlow")));
				bean.setReQBusBranch(Number2String.toStr(15,
						re.getControlType() == RemoteQControlType.BUS_VOLTAGE ? re
								.getRemoteBus().getId() : re.getRemoteBranch()
								.getId()));
				bean.setActual(Number2String.toStr("###0.0000",
						re.getControlType() == RemoteQControlType.BUS_VOLTAGE ? re
								.getRemoteBus().getVoltageMag(UnitType.PU) : re
								.getMvarFlowCalculated(re.getRemoteBranch(), UnitType.PU)));
				bean.setSpec(Number2String.toStr("###0.0000", re
						.getVSpecified(UnitType.PU)));
				bean.setQ(Number2String.toStr("#####0.00", genBus.getGenResults(
						UnitType.PU).getImaginary()));
				bean.setQmax(Number2String.toStr("#####0.00", re.getQLimit(
						UnitType.PU).getMax()));
				bean.setQmin(Number2String.toStr("#####0.00", re.getQLimit(
						UnitType.PU).getMin()));
				bean
						.setStatus(Number2String.toStr(6, re.isActive() ? "on"
								: "off"));
				list.add(bean);
			}
		}
		return list.toArray();
	}

	public static Object[] createPSXfrPControlBeanArray(AclfNetwork net) {
		List<RptPSXfrPControlBean> list = new ArrayList<RptPSXfrPControlBean>();
		double baseKva = net.getBaseKva();
		for (Branch b : net.getBranchList()) {
			AclfBranch branch = (AclfBranch)b;
			if (branch.isTapControl()) {
				PSXfrPControl psCtrl = branch.getPSXfrPControl();
				RptPSXfrPControlBean bean = new RptPSXfrPControlBean();
				bean.setBranchId(psCtrl.getParentBranch().getId());
				bean.setPact(Number2String.toStr("##0.0000", (psCtrl
						.isControlOnFromSide() ? psCtrl.getParentBranch()
						.powerFrom2To(UnitType.PU).getReal() : psCtrl
						.getParentBranch().powerTo2From(UnitType.PU)
						.getReal())));
				bean.setPspec(Number2String.toStr("##0.0000", psCtrl.getPSpecified(
						UnitType.PU, baseKva)));
				AclfPSXformer psXfr = psCtrl.getParentBranch().toPSXfr();
				bean.setAngle(Number2String.toStr("#0.00", psXfr
						.getFromAngle(UnitType.Deg)));
				bean.setAngMax(Number2String.toStr("#0.00", psCtrl.getAngLimit(
						UnitType.Deg).getMax()));
				bean.setAngMin(Number2String.toStr("#0.00", psCtrl.getAngLimit(
						UnitType.Deg).getMin()));
				bean.setStatus(Number2String.toStr(6, psCtrl.isActive() ? "on"
						: "off"));
				list.add(bean);
			}
		}
		return list.toArray();
	}

	public static Object[] createTapVControlBeanArray(AclfNetwork net) {
		List<RptTapVControlBean> list = new ArrayList<RptTapVControlBean>();
		double baseKva = net.getBaseKva();
		for (Branch b : net.getBranchList()) {
			AclfBranch branch = (AclfBranch)b;
			if (branch.isTapControl()) {
				TapControl tap = branch.getTapControl();
				RptTapVControlBean bean = new RptTapVControlBean();
				bean.setBranchId(tap.getParentBranch().getId());
				if (tap.getControlType() == XfrTapControlType.BUS_VOLTAGE) {
					bean.setVcBusId(tap.getVcBus().getId());
					bean.setActual(Number2String.toStr("##0.0000", tap.getVcBus()
							.getVoltageMag(UnitType.PU)));
					bean.setSpec(Number2String.toStr("##0.0000", tap
							.getVSpecified(UnitType.PU)));
				} else {
					bean.setVcBusId(Number2String.toStr(-8, " "));
					bean.setActual(Number2String.toStr("##0.0000", tap
							.getMvarFlowCalculated(UnitType.PU, baseKva)));
					bean.setSpec(Number2String.toStr("##0.0000", tap
							.getMvarSpecified(UnitType.PU, baseKva)));
				}
				bean.setTap(Number2String.toStr("0.000",
						(tap.isControlOnFromSide() ? tap.getParentBranch()
								.getFromTurnRatio() : tap.getParentBranch()
								.getToTurnRatio())));
				bean.setTapMax(Number2String.toStr("0.000", tap.getTurnRatioLimit()
						.getMax()));
				bean.setTapMin(Number2String.toStr("0.000", tap.getTurnRatioLimit()
						.getMin()));
				bean
						.setStepSize(Number2String.toStr("####0", tap
								.getTapStepSize()));
				bean.setStatus(Number2String
						.toStr(6, tap.isActive() ? "on" : "off"));
				list.add(bean);
			}
		}
		return list.toArray();
	}
}