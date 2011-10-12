/*
 * @(#)AcscFaultReport.java   
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

/*
 * Fault Report for both 3P and NS faults
 */

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.GraphSpringAppContext;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.report.IIpssReport;
import org.interpss.report.bean.RptMainTitleBean;
import org.interpss.report.bean.acsc.AcscRptBeanFactory;
import org.interpss.report.mapper.SimuCtxReportMapper;

import com.interpss.common.util.IpssLogger;
import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringCtx;

public class AcscFaultReport implements IIpssReport {
	private String fnameAcscFaultMaster = "reportTemplate/acsc/Acsc3PFaultMaster.jasper";
	private String fnameSubFaultSummary = "reportTemplate/acsc/AcscFaultSummarySubReport.jasper";

	public JasperPrint createReport(String version) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FaultSummarySubreportFilename", fnameSubFaultSummary);

		try {
			IAppSimuContext appSimuCtx = GraphSpringAppContext
					.getIpssGraphicEditor().getCurrentAppSimuContext();
			SimuContext simuCtx = (SimuContext) appSimuCtx.getSimuCtx();
			SimuCtxReportMapper mapper = new SimuCtxReportMapper();

			// map the title bean
			RptMainTitleBean titleBean = new RptMainTitleBean();
			mapper.mapping(appSimuCtx, titleBean);
			titleBean.setReportTitle("Short Circuit Calculation");
			titleBean.setCaseName(appSimuCtx
					.getCurrentCaseName(SimuRunEnum.Acsc));
			parameters.put("ReportMainTitleBean", titleBean);

			parameters.put("FaultSummaryJBeanDatasource", AcscRptBeanFactory
					.getFaultSummaryDataSource(simuCtx));

			return JasperFillManager.fillReport(fnameAcscFaultMaster,
					parameters, AcscRptBeanFactory
							.getAcscVoltAmpsDataSource(simuCtx));
		} catch (Exception e) {
			IpssLogger.logErr(e);
			CoreCommonSpringCtx.getIpssMsgHub().sendErrorMsg(e.toString());
		}
		return null;
	}

	/**
	 * @param fnameAcscFaultMaster the fnameAcsc3PFaultMaster to set
	 */
	public void setFnameAcscFaultMaster(String fnameAcscFaultMaster) {
		this.fnameAcscFaultMaster = fnameAcscFaultMaster;
	}

	/**
	 * @param fnameSubFaultSummary the fnameSubFaultSummary to set
	 */
	public void setFnameSubFaultSummary(String fnameSubFaultSummary) {
		this.fnameSubFaultSummary = fnameSubFaultSummary;
	}
}
