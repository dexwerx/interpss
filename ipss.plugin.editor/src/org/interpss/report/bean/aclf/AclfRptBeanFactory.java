/*
 * @(#)AclfRptBeanFactory.java   
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

package org.interpss.report.bean.aclf;

import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;

import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.report.mapper.SimuCtxReportMapper;

import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.aclf.adj.PQBusLimit;
import com.interpss.core.aclf.adj.PSXfrPControl;
import com.interpss.core.aclf.adj.PVBusLimit;
import com.interpss.core.aclf.adj.RemoteQBus;
import com.interpss.core.aclf.adj.TapControl;
import com.interpss.simu.SimuContext;

public class AclfRptBeanFactory {
	public static RptPVLimitBean[] createPVLimitSampleBeanList() {
		RptPVLimitBean[] list = new RptPVLimitBean[2];
		list[0] = createPVLimitSampleBean();
		list[1] = createPVLimitSampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getPVLimitSampleDataSource() {
		return new JRBeanArrayDataSource(createPVLimitSampleBeanList());
	}

	public static JRBeanArrayDataSource getPVLimitDataSource(
			SimuContext simuCtx, SimuCtxReportMapper mapper) {
		Object[] beans = mapper.mappingMultiObject(simuCtx, PVBusLimit.class,
				null);
		if (beans.length > 0)
			return new JRBeanArrayDataSource(beans);
		else
			return null;
	}

	/*
	 * PQLimit Subreport =================
	 */
	public static RptPQLimitBean createPQLimitSampleBean() {
		RptPQLimitBean bean = new RptPQLimitBean();
		bean.setBusId("0001");
		bean.setQact("1.000");
		bean.setQspec("1.000");
		bean.setV("1.00");
		bean.setVmax("1.00");
		bean.setVmin("1.00");
		bean.setStatus("on");
		return bean;
	}

	public static RptPQLimitBean[] createPQLimitSampleBeanList() {
		RptPQLimitBean[] list = new RptPQLimitBean[2];
		list[0] = createPQLimitSampleBean();
		list[1] = createPQLimitSampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getPQLimitSampleDataSource() {
		return new JRBeanArrayDataSource(createPQLimitSampleBeanList());
	}

	public static JRBeanArrayDataSource getPQLimitDataSource(
			SimuContext simuCtx, SimuCtxReportMapper mapper) {
		Object[] beans = mapper.mappingMultiObject(simuCtx, PQBusLimit.class,
				null);
		if (beans.length > 0)
			return new JRBeanArrayDataSource(beans);
		else
			return null;
	}

	/*
	 * RemoteQBus Subreport ====================
	 */
	public static RptRemoteQBusBean createRemoteQBusSampleBean() {
		RptRemoteQBusBean bean = new RptRemoteQBusBean();
		bean.setVcBusId("0001");
		bean.setType("1.000");
		bean.setReQBusBranch("1.000");
		bean.setActual("1.00");
		bean.setSpec("1.00");
		bean.setQ("1.00");
		bean.setQmax("1.00");
		bean.setQmin("1.00");
		bean.setStatus("on");
		return bean;
	}

	/*
	 * PVLimit Subreport =================
	 */
	public static RptPVLimitBean createPVLimitSampleBean() {
		RptPVLimitBean bean = new RptPVLimitBean();
		bean.setBusId("0001");
		bean.setVact("1.000");
		bean.setVspec("1.000");
		bean.setQ("1.00");
		bean.setQmax("1.00");
		bean.setQmin("1.00");
		bean.setStatus("on");
		return bean;
	}

	public static RptRemoteQBusBean[] createRemoteQBusSampleBeanList() {
		RptRemoteQBusBean[] list = new RptRemoteQBusBean[2];
		list[0] = createRemoteQBusSampleBean();
		list[1] = createRemoteQBusSampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getRemoteQBusSampleDataSource() {
		return new JRBeanArrayDataSource(createRemoteQBusSampleBeanList());
	}

	public static JRBeanArrayDataSource getRemoteQBusDataSource(
			SimuContext simuCtx, SimuCtxReportMapper mapper) {
		Object[] beans = mapper.mappingMultiObject(simuCtx, RemoteQBus.class,
				null);
		if (beans.length > 0)
			return new JRBeanArrayDataSource(beans);
		else
			return null;
	}

	/*
	 * TapControl Subreport =================
	 */
	public static RptTapVControlBean createTapControlSampleBean() {
		RptTapVControlBean bean = new RptTapVControlBean();
		bean.setBranchId("0001");
		bean.setVcBusId("1.000");
		bean.setActual("1.000");
		bean.setSpec("1.00");
		bean.setTap("1.00");
		bean.setTapMax("1.00");
		bean.setTapMin("1.00");
		bean.setStepSize("1.00");
		bean.setStatus("on");
		return bean;
	}

	public static RptTapVControlBean[] createTapVControlSampleBeanList() {
		RptTapVControlBean[] list = new RptTapVControlBean[2];
		list[0] = createTapControlSampleBean();
		list[1] = createTapControlSampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getTapVControlSampleDataSource() {
		return new JRBeanArrayDataSource(createTapVControlSampleBeanList());
	}

	public static JRBeanArrayDataSource getTapVControlDataSource(
			SimuContext simuCtx, SimuCtxReportMapper mapper) {
		Object[] beans = mapper.mappingMultiObject(simuCtx, TapControl.class,
				null);
		if (beans.length > 0)
			return new JRBeanArrayDataSource(beans);
		else
			return null;
	}

	/*
	 * PSXfrPControl Subreport =======================
	 */
	public static RptPSXfrPControlBean createPSXfrPControlSampleBean() {
		RptPSXfrPControlBean bean = new RptPSXfrPControlBean();
		bean.setBranchId("0001");
		bean.setPact("1.000");
		bean.setPspec("1.000");
		bean.setAngle("1.00");
		bean.setAngMax("1.00");
		bean.setAngMin("1.00");
		bean.setStatus("on");
		return bean;
	}

	public static RptPSXfrPControlBean[] createPSXfrPControlSampleBeanList() {
		RptPSXfrPControlBean[] list = new RptPSXfrPControlBean[2];
		list[0] = createPSXfrPControlSampleBean();
		list[1] = createPSXfrPControlSampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getPSXfrPControlSampleDataSource() {
		return new JRBeanArrayDataSource(createPSXfrPControlSampleBeanList());
	}

	public static JRBeanArrayDataSource getPsXfrPControlDataSource(
			SimuContext simuCtx, SimuCtxReportMapper mapper) {
		Object[] beans = mapper.mappingMultiObject(simuCtx,
				PSXfrPControl.class, null);
		if (beans.length > 0)
			return new JRBeanArrayDataSource(beans);
		else
			return null;
	}

	/*
	 * FuncLoad Subreport =================
	 */
	public static RptFuncLoadBean createFuncLoadSampleBean() {
		RptFuncLoadBean bean = new RptFuncLoadBean();
		bean.setBusId("0001");
		bean.setPact("1.000");
		bean.setQact("1.000");
		bean.setV("1.00");
		bean.setP0("1.00");
		bean.setQ0("1.00");
		bean.setStatus("on");
		return bean;
	}

	public static RptFuncLoadBean[] createFuncLoadSampleBeanList() {
		RptFuncLoadBean[] list = new RptFuncLoadBean[2];
		list[0] = createFuncLoadSampleBean();
		list[1] = createFuncLoadSampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getFuncLoadSampleDataSource() {
		return new JRBeanArrayDataSource(createFuncLoadSampleBeanList());
	}

	public static JRBeanArrayDataSource getFuncLoadDataSource(
			SimuContext simuCtx, SimuCtxReportMapper mapper) {
		Object[] beans = mapper.mappingMultiObject(simuCtx, FunctionLoad.class,
				null);
		if (beans.length > 0)
			return new JRBeanArrayDataSource(beans);
		else
			return null;
	}

	/*
	 * MaxMismatch Subreport =====================
	 */

	public static RptAclfMaxMismatchBean createMaxMismatchSampleBean() {
		RptAclfMaxMismatchBean bean = new RptAclfMaxMismatchBean();
		bean.setPMaxBusId("0006");
		bean.setPMaxPu("0.000016");
		bean.setPMaxKva("1.638642");
		bean.setQMaxBusId("0013");
		bean.setQMaxPu("0.000033");
		bean.setQMaxKva("3.284569");
		return bean;
	}

	public static RptAclfMaxMismatchBean[] createMaxMismatchSampleBeanList() {
		RptAclfMaxMismatchBean[] list = new RptAclfMaxMismatchBean[1];
		list[0] = createMaxMismatchSampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getMaxMismatchSampleDataSource() {
		return new JRBeanArrayDataSource(createMaxMismatchSampleBeanList());
	}

	public static JRBeanArrayDataSource getMaxMismatchDataSource(
			IAppSimuContext appSimuCtx, SimuCtxReportMapper mapper) {
		RptAclfMaxMismatchBean misBean = new RptAclfMaxMismatchBean();
		mapper.mapping(appSimuCtx, misBean);
		return new JRBeanArrayDataSource(new Object[] { misBean });
	}

	/*
	 * Summary Bus Master Report =========================
	 */
	public static RptAclfSummaryBusBean createSummarySampleBean() {
		RptAclfSummaryBusBean bean = new RptAclfSummaryBusBean();
		bean.setBusId("00001");
		bean.setBusCode("ConstP");
		bean.setBusVolt("1.0000");
		bean.setBusAngle("0.00");
		bean.setBusP("0.00");
		bean.setBusQ("0.00");
		return bean;
	}

	public static RptAclfSummaryBusBean[] createSummarySampleBeanList() {
		RptAclfSummaryBusBean[] list = new RptAclfSummaryBusBean[3];
		list[0] = createSummarySampleBean();
		list[1] = createSummarySampleBean();
		list[2] = createSummarySampleBean();
		return list;
	}

	public static JRBeanArrayDataSource getSummaryBusSampleDataSource() {
		return new JRBeanArrayDataSource(createSummarySampleBeanList());
	}

	public static JRBeanArrayDataSource getSummaryBusDataSource(
			SimuContext simuCtx) {
		SimuCtxReportMapper mapper = new SimuCtxReportMapper();
		Object[] beans = mapper.mappingMultiObject(simuCtx,
				RptAclfSummaryBusBean.class, null);
		return new JRBeanArrayDataSource(beans);
	}

	/*
	 * Bus Style Master Report =======================
	 */
	public static RptAclfBusStyleBean createAclfBusStyleSampleBean() {
		RptAclfBusStyleBean bean = new RptAclfBusStyleBean();
		bean.setBusId("00001");
		bean.setBaseVolt("13800.00");
		bean.setVoltMsg("1.0000");
		bean.setVoltAng("0.00");
		bean.setPGen("0.00");
		bean.setQGen("0.00");
		bean.setPLoad("0.00");
		bean.setQLoad("0.00");
		bean.setToBusId("0002");
		bean.setBranchP("0.00");
		bean.setBranchQ("0.00");
		bean.setBranchKA("0.00");
		bean.setXfrRatioFrom("0.00");
		bean.setXfrRatioTo("0.00");
		bean.setPsXfrAngle("0.00");
		return bean;
	}

	public static RptAclfBusStyleBean createAclfBusStyleSampleBean1() {
		RptAclfBusStyleBean bean = new RptAclfBusStyleBean();
		bean.setBusId("");
		bean.setBaseVolt("");
		bean.setVoltMsg("");
		bean.setVoltAng("");
		bean.setPGen("");
		bean.setQGen("");
		bean.setPLoad("");
		bean.setQLoad("");
		bean.setToBusId("00003");
		bean.setBranchP("0.00");
		bean.setBranchQ("0.00");
		bean.setBranchKA("0.00");
		bean.setXfrRatioFrom("0.00");
		bean.setXfrRatioTo("0.00");
		bean.setPsXfrAngle("0.00");
		return bean;
	}

	public static RptAclfBusStyleBean[] createAclfBusStyleSampleBeanList() {
		RptAclfBusStyleBean[] list = new RptAclfBusStyleBean[6];
		list[0] = createAclfBusStyleSampleBean();
		list[1] = createAclfBusStyleSampleBean1();
		list[2] = createAclfBusStyleSampleBean1();
		list[3] = createAclfBusStyleSampleBean();
		list[4] = createAclfBusStyleSampleBean1();
		list[5] = createAclfBusStyleSampleBean1();
		return list;
	}

	public static JRBeanArrayDataSource getAclfBusStyleSampleDataSource() {
		return new JRBeanArrayDataSource(createAclfBusStyleSampleBeanList());
	}

	public static JRBeanArrayDataSource getBusStyleDataSource(
			SimuContext simuCtx) {
		SimuCtxReportMapper mapper = new SimuCtxReportMapper();
		Object[] beans = mapper.mappingMultiObject(simuCtx,
				RptAclfBusStyleBean.class, null);
		return new JRBeanArrayDataSource(beans);
	}
}
