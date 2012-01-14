/*
 * @(#)AclfBaseReport.java   
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

package org.interpss.report.impl;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import java.util.Map;

import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;

import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.report.bean.aclf.AclfRptBeanFactory;
import org.interpss.report.bean.aclf.AclfRptSubrptControlRec;
import org.interpss.report.mapper.SimuCtxReportMapper;

import com.interpss.simu.SimuContext;

public class AclfBaseReport {
	private String fnameSubAclfMismatch = "reportTemplate/aclf/AclfMismatchSubReport.jasper";
	private String fnameSubAclfPVLimit = "reportTemplate/aclf/AclfPVLimitSubReport.jasper";
	private String fnameSubAclfPQLimit = "reportTemplate/aclf/AclfPQLimitSubReport.jasper";
	private String fnameSubAclfFuncLoad = "reportTemplate/aclf/AclfFuncLoadSubReport.jasper";
	private String fnameSubAclfRemoteQBus = "reportTemplate/aclf/AclfRemoteQBusSubReport.jasper";
	private String fnameSubAclfPSXfrPControl = "reportTemplate/aclf/AclfPSXfrPControlSubReport.jasper";
	private String fnameSubAclfTapVControl = "reportTemplate/aclf/AclfTapVControlSubReport.jasper";

	public void addSubreportFile(Map<String, Object> parameters) {
		parameters.put("MismatchSubreportFilename", fnameSubAclfMismatch);
		parameters.put("PVLimitSubreportFilename", fnameSubAclfPVLimit);
		parameters.put("PQLimitSubreportFilename", fnameSubAclfPQLimit);
		parameters.put("FuncLoadSubreportFilename", fnameSubAclfFuncLoad);
		parameters.put("RemoteQBusSubreportFilename", fnameSubAclfRemoteQBus);
		parameters.put("PsXfrPControlSubreportFilename",
				fnameSubAclfPSXfrPControl);
		parameters.put("TapVControlSubreportFilename", fnameSubAclfTapVControl);
	}

	public void addSubreports(Map<String, Object> parameters,
			IAppSimuContext appSimuCtx, SimuCtxReportMapper mapper) {
		SimuContext simuCtx = (SimuContext) appSimuCtx.getSimuCtx();

		// map the aclf load flow result to the RptAclfMaxMismatchBean
		parameters.put("MismatchJBeanDatasource", AclfRptBeanFactory
				.getMaxMismatchDataSource(appSimuCtx, mapper));

		AclfRptSubrptControlRec contRec = new AclfRptSubrptControlRec();
		parameters.put("SubreportControlRec", contRec);

		contRec.setPvLimitSubreport(false);
		JRBeanArrayDataSource pvLimit = AclfRptBeanFactory
				.getPVLimitDataSource(simuCtx, mapper);
		if (pvLimit != null) {
			contRec.setPvLimitSubreport(true);
			parameters.put("PVLimitJBeanDatasource", pvLimit);
			ipssLogger.info("Include sub report PVLimit");
		}

		contRec.setPqLimitSubreport(false);
		JRBeanArrayDataSource pqLimit = AclfRptBeanFactory
				.getPQLimitDataSource(simuCtx, mapper);
		if (pqLimit != null) {
			contRec.setPqLimitSubreport(true);
			parameters.put("PQLimitJBeanDatasource", pqLimit);
			ipssLogger.info("Include sub report PQLimit");
		}

		contRec.setFuncloadSubreport(false);
		JRBeanArrayDataSource funcLoad = AclfRptBeanFactory
				.getFuncLoadDataSource(simuCtx, mapper);
		if (funcLoad != null) {
			contRec.setFuncloadSubreport(true);
			parameters.put("FuncLoadJBeanDatasource", funcLoad);
			ipssLogger.info("Include sub report FuncLoad");
		}

		contRec.setRemoteQBusSubreport(false);
		JRBeanArrayDataSource reQBus = AclfRptBeanFactory
				.getRemoteQBusDataSource(simuCtx, mapper);
		if (reQBus != null) {
			contRec.setRemoteQBusSubreport(true);
			parameters.put("RemoteQBusJBeanDatasource", reQBus);
			ipssLogger.info("Include sub report RemoteQBus");
		}

		contRec.setTapVControlSubreport(false);
		JRBeanArrayDataSource tap = AclfRptBeanFactory
				.getTapVControlDataSource(simuCtx, mapper);
		if (tap != null) {
			contRec.setTapVControlSubreport(true);
			parameters.put("TapVControlJBeanDatasource", tap);
			ipssLogger.info("Include sub report TapVControl");
		}

		contRec.setPsXfrPControlSubreport(false);
		JRBeanArrayDataSource psxfr = AclfRptBeanFactory
				.getPsXfrPControlDataSource(simuCtx, mapper);
		if (pvLimit != null) {
			contRec.setPsXfrPControlSubreport(true);
			parameters.put("PsXfrPControlJBeanDatasource", psxfr);
			ipssLogger.info("Include sub report PsXfrPControl");
		}
	}

	/**
	 * @param fnameSubAclfFuncLoad
	 *            the fnameSubAclfFuncLoad to set
	 */
	public void setFnameSubAclfFuncLoad(String fnameSubAclfFuncLoad) {
		this.fnameSubAclfFuncLoad = fnameSubAclfFuncLoad;
	}

	/**
	 * @param fnameSubAclfMismatch
	 *            the fnameSubAclfMismatch to set
	 */
	public void setFnameSubAclfMismatch(String fnameSubAclfMismatch) {
		this.fnameSubAclfMismatch = fnameSubAclfMismatch;
	}

	/**
	 * @param fnameSubAclfPQLimit
	 *            the fnameSubAclfPQLimit to set
	 */
	public void setFnameSubAclfPQLimit(String fnameSubAclfPQLimit) {
		this.fnameSubAclfPQLimit = fnameSubAclfPQLimit;
	}

	/**
	 * @param fnameSubAclfPSXfrPControl
	 *            the fnameSubAclfPSXfrPControl to set
	 */
	public void setFnameSubAclfPSXfrPControl(String fnameSubAclfPSXfrPControl) {
		this.fnameSubAclfPSXfrPControl = fnameSubAclfPSXfrPControl;
	}

	/**
	 * @param fnameSubAclfPVLimit
	 *            the fnameSubAclfPVLimit to set
	 */
	public void setFnameSubAclfPVLimit(String fnameSubAclfPVLimit) {
		this.fnameSubAclfPVLimit = fnameSubAclfPVLimit;
	}

	/**
	 * @param fnameSubAclfRemoteQBus
	 *            the fnameSubAclfRemoteQBus to set
	 */
	public void setFnameSubAclfRemoteQBus(String fnameSubAclfRemoteQBus) {
		this.fnameSubAclfRemoteQBus = fnameSubAclfRemoteQBus;
	}

	/**
	 * @param fnameSubAclfTapVControl
	 *            the fnameSubAclfTapVControl to set
	 */
	public void setFnameSubAclfTapVControl(String fnameSubAclfTapVControl) {
		this.fnameSubAclfTapVControl = fnameSubAclfTapVControl;
	}
}
