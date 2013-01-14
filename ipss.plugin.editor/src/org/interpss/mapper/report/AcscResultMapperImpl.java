/*
 * @(#)AcscResultMapperImpl.java   
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

import org.apache.commons.math3.complex.ComplexFormat;
import org.eclipse.emf.common.util.EList;
import org.interpss.numeric.datatype.Complex3x1;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;
import org.interpss.report.bean.acsc.RptAcscVoltAmpsBean;
import org.interpss.report.bean.acsc.RptFaultSummaryBean;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBranchFault;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

public class AcscResultMapperImpl {
	public static void mapAcscFaultSummary(AcscNetwork faultNet, SimpleFaultAlgorithm algo,
			RptFaultSummaryBean bean) {
		double baseKVA = faultNet.getBaseKva();
		Object fault = algo.getFaultList().get(0);
		if (fault instanceof AcscBranchFault) {
			AcscBranchFault fBranch = (AcscBranchFault) fault;
			double baseV = fBranch.getFaultBranch().getFromAcscBus()
					.getBaseVoltage();
			bean.setType(RptFaultSummaryBean.Type_BranchFault);
			bean.setBusId(fBranch.getFaultBranch().getId());
			bean.setBusName(fBranch.getFaultBranch().getName());
			bean.setFaultType("BranchFault");
			bean.setFaultCode(fBranch.getFaultCode().toString());
			bean.setFaultAmpspu(new ComplexFormat().format(fBranch
					.getFaultResult().getSCCurrent_012().b_1));
			bean.setFaultAmps(new ComplexFormat().format(fBranch
					.getFaultResult().getSCCurrent_012(UnitType.Amp, baseV,
							baseKVA).b_1));
			bean.setFaultDistance(Number2String.toStr("##0.0", fBranch
					.getDistance() * 100.0)
					+ "%");
		} else {
			AcscBusFault fBus = (AcscBusFault) fault;
			double baseV = fBus.getAcscBus().getBaseVoltage();
			bean.setType(RptFaultSummaryBean.Type_BusFault);
			bean.setBusId(fBus.getAcscBus().getId());
			bean.setBusName(fBus.getAcscBus().getName());
			bean.setFaultType("BusFault");
			bean.setFaultCode(fBus.getFaultCode().toString());
			bean.setFaultAmpspu(new ComplexFormat().format(fBus
					.getFaultResult().getSCCurrent_012().b_1));
			bean.setFaultAmps(new ComplexFormat().format(fBus.getFaultResult()
					.getSCCurrent_012(UnitType.Amp, baseV, baseKVA).b_1));
			bean.setFaultDistance(" ");
		}
	}

	public static Object[] createAcscVoltAmpsBeanArray(
			AcscNetwork faultNet, SimpleFaultAlgorithm algo) {
		List<RptAcscVoltAmpsBean> list = new ArrayList<RptAcscVoltAmpsBean>();
		try {
			double baseKva = faultNet.getBaseKva();
			AcscBusFault fault = algo.getFaultList().get(0);
			if (fault != null) {
				fault.getFaultResult().calContributingCurrent();
				if (fault.getFaultCode() == SimpleFaultCode.GROUND_3P) {
					for (Bus b : faultNet.getBusList()) {
						AcscBus bus = (AcscBus) b;
						RptAcscVoltAmpsBean bean = new RptAcscVoltAmpsBean();
						bean.setRecType(RptAcscVoltAmpsBean.RecType_Bus3P);
						bean.setBusName(bus.getName());
						bean.setBusId(bus.getId());
						double vpu = fault.getFaultResult()
								.getBusVoltage_012(bus).b_1.abs();
						Complex3x1 ampPu = fault.getFaultResult()
								.getBusContriAmps_012(bus);
						Complex3x1 amps = fault.getFaultResult()
								.getBusContriAmps_012(bus,	UnitType.Amp, bus.getBaseVoltage(),
										baseKva);
						bean.setBusFaultVoltpu(Number2String.toStr("####0.##",
								vpu));
						bean.setBusFaultVolt(Number2String.toStr("######0.#",
								vpu * bus.getBaseVoltage()));
						bean.setBusContribAmpspu(Number2String.toStr(
								"####0.##", ampPu.b_1.abs()));
						bean.setBusContribAmps(Number2String.toStr("######0.#",
								amps.b_1.abs()));
						list.add(bean);
					}
				} else {
					for (Bus b : faultNet.getBusList()) {
						AcscBus bus = (AcscBus) b;
						RptAcscVoltAmpsBean bean = new RptAcscVoltAmpsBean();
						bean.setRecType(RptAcscVoltAmpsBean.RecType_Bus012);
						bean.setBusName(bus.getName());
						bean.setBusId(bus.getId());

						Complex3x1 v012 = fault.getFaultResult()
								.getBusVoltage_012(bus);
						double vpu1 = v012.b_1.abs();
						bean.setBusFaultVoltpu(Number2String.toStr("####0.##",
								vpu1));
						bean.setBusFaultVolt(Number2String.toStr("######0.#",
								vpu1 * bus.getBaseVoltage()));

						double vpu0 = v012.a_0.abs();
						bean.setBusFaultVolt0pu(Number2String.toStr("####0.##",
								vpu0));
						bean.setBusFaultVolt0(Number2String.toStr("######0.#",
								vpu0 * bus.getBaseVoltage()));

						double vpu2 = v012.c_2.abs();
						bean.setBusFaultVolt2pu(Number2String.toStr("####0.##",
								vpu2));
						bean.setBusFaultVolt2(Number2String.toStr("######0.#",
								vpu2 * bus.getBaseVoltage()));

						Complex3x1 ampPu = fault.getFaultResult()
								.getBusContriAmps_012(bus);
						Complex3x1 amps = fault.getFaultResult()
								.getBusContriAmps_012(bus,
										UnitType.Amp, bus.getBaseVoltage(),
										baseKva);
						bean.setBusContribAmpspu(Number2String.toStr(
								"####0.##", ampPu.b_1.abs()));
						bean.setBusContribAmps(Number2String.toStr("######0.#",
								amps.b_1.abs()));
						list.add(bean);
					}

					for (Bus b : faultNet.getBusList()) {
						AcscBus bus = (AcscBus) b;
						RptAcscVoltAmpsBean bean = new RptAcscVoltAmpsBean();
						bean.setRecType(RptAcscVoltAmpsBean.RecType_BusABC);
						bean.setBusName(bus.getName());
						bean.setBusId(bus.getId());

						Complex3x1 v012 = fault.getFaultResult()
								.getBusVoltage_012(bus);
						Complex3x1 vabc = Complex3x1.z12_to_abc(v012);

						double vpu1 = vabc.a_0.abs();
						bean.setBusFaultVoltApu(Number2String.toStr("####0.##",
								vpu1));
						bean.setBusFaultVoltA(Number2String.toStr("#####0.#",
								vpu1 * bus.getBaseVoltage()));

						double vpu0 = vabc.b_1.abs();
						bean.setBusFaultVoltBpu(Number2String.toStr("####0.##",
								vpu0));
						bean.setBusFaultVoltB(Number2String.toStr("######0.#",
								vpu0 * bus.getBaseVoltage()));

						double vpu2 = vabc.c_2.abs();
						bean.setBusFaultVoltCpu(Number2String.toStr("####0.##",
								vpu2));
						bean.setBusFaultVoltC(Number2String.toStr("######0.#",
								vpu2 * bus.getBaseVoltage()));

						Complex3x1 ampPu = fault.getFaultResult()
								.getBusContriAmps_012(bus);
						Complex3x1 amps = fault.getFaultResult()
								.getBusContriAmps_012(bus,
										UnitType.Amp, bus.getBaseVoltage(),
										baseKva);
						bean.setBusContribAmpspu(Number2String.toStr(
								"####0.##", ampPu.b_1.abs()));
						bean.setBusContribAmps(Number2String.toStr("######0.#",
								amps.b_1.abs()));

						list.add(bean);
					}
				}

				fault.getFaultResult().calBranchCurrent();
				if (fault.getFaultCode() == SimpleFaultCode.GROUND_3P) {
					EList<AclfBranch> branchList = faultNet.getBranchList();
					int cnt = 0;
					for (int n = 0; n < branchList.size(); n++) {
						AcscBranch bra = (AcscBranch) branchList.get(n);
						RptAcscVoltAmpsBean bean = new RptAcscVoltAmpsBean();
						bean.setRecType(RptAcscVoltAmpsBean.RecType_Branch3P);
						bean.setBranchId(bra.getId());
						bean.setBranchName(bra.getName());
						Complex3x1 cpu = fault.getFaultResult()
								.getBranchAmpsFrom2To_012(++cnt);
						Complex3x1 camp = fault.getFaultResult()
								.getBranchAmpsFrom2To_012(cnt, UnitType.Amp,
										bra.getFromBus().getBaseVoltage(),
										baseKva);
						bean.setBranchFaultAmpspu(Number2String.toStr(
								"###0.##", cpu.b_1.abs()));
						bean.setBranchFaultAmps(Number2String.toStr(
								"######0.#", camp.b_1.abs()));
						list.add(bean);
					}
				} else {
					EList<AclfBranch> branchList = faultNet.getBranchList();
					int cnt = 0;
					for (int n = 0; n < branchList.size(); n++) {
						AcscBranch bra = (AcscBranch) branchList.get(n);
						RptAcscVoltAmpsBean beanFrom2To = new RptAcscVoltAmpsBean();
						beanFrom2To
								.setRecType(RptAcscVoltAmpsBean.RecType_Branch012);
						beanFrom2To.setBranchId(bra.getId());
						beanFrom2To.setBranchName(bra.getName());

						RptAcscVoltAmpsBean beanTo2From = new RptAcscVoltAmpsBean();
						beanTo2From
								.setRecType(RptAcscVoltAmpsBean.RecType_Branch012);
						beanTo2From.setBranchId("<--    ");
						beanTo2From.setBranchName("");

						try {
							Complex3x1 cpu = fault.getFaultResult()
									.getBranchAmpsFrom2To_012(++cnt);
							Complex3x1 camp = fault.getFaultResult()
									.getBranchAmpsFrom2To_012(cnt, UnitType.Amp,
											bra.getFromBus().getBaseVoltage(),
											faultNet.getBaseKva());
							beanFrom2To.setBranchFaultAmpspu(Number2String
									.toStr("###0.##", cpu.b_1.abs()));
							beanFrom2To.setBranchFaultAmps(Number2String.toStr(
									"######0.", camp.b_1.abs()));
							beanFrom2To.setBranchFaultAmps0pu(Number2String
									.toStr("###0.##", cpu.a_0.abs()));
							beanFrom2To.setBranchFaultAmps0(Number2String
									.toStr("######0.#", camp.a_0.abs()));
							beanFrom2To.setBranchFaultAmps2pu(Number2String
									.toStr("###0.##", cpu.c_2.abs()));
							beanFrom2To.setBranchFaultAmps2(Number2String
									.toStr("######0.#", camp.c_2.abs()));

							cpu = fault.getFaultResult().getBranchAmpsTo2From_012(cnt);
							camp = fault.getFaultResult()
									.getBranchAmpsTo2From_012(cnt, UnitType.Amp,
											bra.getToBus().getBaseVoltage(),
											faultNet.getBaseKva());
							beanTo2From.setBranchFaultAmpspu(Number2String
									.toStr("###0.##", cpu.b_1.abs()));
							beanTo2From.setBranchFaultAmps(Number2String.toStr(
									"######0.#", camp.b_1.abs()));
							beanTo2From.setBranchFaultAmps0pu(Number2String
									.toStr("###0.##", cpu.a_0.abs()));
							beanTo2From.setBranchFaultAmps0(Number2String
									.toStr("######0.#", camp.a_0.abs()));
							beanTo2From.setBranchFaultAmps2pu(Number2String
									.toStr("###0.##", cpu.c_2.abs()));
							beanTo2From.setBranchFaultAmps2(Number2String
									.toStr("######0.#", camp.c_2.abs()));
						} catch (Exception e) {
							IpssLogger.logErr(e);
						}
						list.add(beanFrom2To);
						list.add(beanTo2From);
					}

					cnt = 0;
					for (int n = 0; n < branchList.size(); n++) {
						AcscBranch bra = (AcscBranch) branchList.get(n);
						RptAcscVoltAmpsBean beanFrom2To = new RptAcscVoltAmpsBean();
						RptAcscVoltAmpsBean beanTo2From = new RptAcscVoltAmpsBean();
						beanFrom2To
								.setRecType(RptAcscVoltAmpsBean.RecType_BranchABC);
						beanFrom2To.setBranchId(bra.getId());
						beanFrom2To.setBranchName(bra.getName());

						beanTo2From
								.setRecType(RptAcscVoltAmpsBean.RecType_BranchABC);
						beanTo2From.setBranchId("<--     ");
						beanTo2From.setBranchName("");

						try {
							Complex3x1 cpu = fault.getFaultResult().getBranchAmpsFrom2To_abc(++cnt);
							Complex3x1 camp = fault.getFaultResult()
									.getBranchAmpsFrom2To_abc(cnt, UnitType.Amp,
											bra.getFromBus().getBaseVoltage(),
											faultNet.getBaseKva());
							beanFrom2To.setBranchFaultAmpsApu(Number2String
									.toStr("###0.##", cpu.a_0.abs()));
							beanFrom2To.setBranchFaultAmpsA(Number2String
									.toStr("######0.#", camp.a_0.abs()));
							beanFrom2To.setBranchFaultAmpsBpu(Number2String
									.toStr("###0.##", cpu.b_1.abs()));
							beanFrom2To.setBranchFaultAmpsB(Number2String
									.toStr("######0.#", camp.b_1.abs()));
							beanFrom2To.setBranchFaultAmpsCpu(Number2String
									.toStr("###0.##", cpu.c_2.abs()));
							beanFrom2To.setBranchFaultAmpsC(Number2String
									.toStr("######0.#", camp.c_2.abs()));

							cpu = fault.getFaultResult()
									.getBranchAmpsTo2From_abc(cnt);
							camp = fault.getFaultResult()
									.getBranchAmpsTo2From_abc(cnt, UnitType.Amp,
											bra.getToBus().getBaseVoltage(),
											faultNet.getBaseKva());
							beanTo2From.setBranchFaultAmpsApu(Number2String
									.toStr("###0.##", cpu.a_0.abs()));
							beanTo2From.setBranchFaultAmpsA(Number2String
									.toStr("######0.#", camp.a_0.abs()));
							beanTo2From.setBranchFaultAmpsBpu(Number2String
									.toStr("###0.##", cpu.b_1.abs()));
							beanTo2From.setBranchFaultAmpsB(Number2String
									.toStr("######0.#", camp.b_1.abs()));
							beanTo2From.setBranchFaultAmpsCpu(Number2String
									.toStr("###0.##", cpu.c_2.abs()));
							beanTo2From.setBranchFaultAmpsC(Number2String
									.toStr("######0.#", camp.c_2.abs()));
						} catch (Exception e) {
							IpssLogger.logErr(e);
						}
						list.add(beanFrom2To);
						list.add(beanTo2From);
					}
				}
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			return null;
		}
		return list.toArray();
	}

}