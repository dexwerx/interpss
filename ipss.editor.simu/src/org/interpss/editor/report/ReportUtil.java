/*
 * @(#)ReportUtil.java   
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

package org.interpss.editor.report;

import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.resources.Translator;
import org.interpss.report.IpssReportFactory;

import com.interpss.common.datatype.Constants;

public class ReportUtil {
	public static void displayReport(String rptType) {
		/*
		 * start the busy indicator
		 */
		IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor()
				.getAppStatus();
		appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
				"Editor is displaying report ...", "");

		IpssRptViewer.getDefault().loadReport(
				IpssReportFactory.createReport(rptType, GraphSpringFactory
						.getIpssGraphicEditor().getVersion()));

		/*
		 * stop the busy indicator
		 */
		appStatus.busyStop("Report displayed");
	}

	public static String getDefaultReportType(IGNetForm form,
			SimuRunEnum lastRunType, boolean nsFault) {
		if (form.getAppType().equals(IGNetForm.AppType_Distribution)) {
			if (lastRunType == SimuRunEnum.Acsc)
				return IpssReportFactory.RPT_TYPE_ACSC3PFAULT;
			else if (lastRunType == SimuRunEnum.Aclf)
				return IpssReportFactory.RPT_TYPE_ACLFSUMMARY;
		} else {
			if (form.getNetType().equals(IGNetForm.NetType_AclfNetwork)
					|| form.getNetType().equals(
							IGNetForm.NetType_AclfAdjNetwork)) {
				return IpssReportFactory.RPT_TYPE_ACLFSUMMARY;
			} else if (form.getNetType().equals(IGNetForm.NetType_AcscNetwork)) {
				if (nsFault)
					return IpssReportFactory.RPT_TYPE_ACSCNSFAULT;
				else
					return IpssReportFactory.RPT_TYPE_ACSC3PFAULT;
			}
		}
		return IpssReportFactory.RPT_TYPE_ACLFSUMMARY;
	}

	public static String getReportName(String type, String projectName) {
		String name = "unknown";
		if (type.equals(IpssReportFactory.RPT_TYPE_ACLFSUMMARY)) {
			name = Translator.getString("Report.Name.AclfSummary");
		} else if (type.equals(IpssReportFactory.RPT_TYPE_ACLFBUSSTYLE)) {
			name = Translator.getString("Report.Name.AclfIeeeBusStyle");
		} else if (type.equals(IpssReportFactory.RPT_TYPE_ACSC3PFAULT)) {
			name = Translator.getString("Report.Name.Acsc3PFault");
		} else if (type.equals(IpssReportFactory.RPT_TYPE_ACSCNSFAULT)) {
			name = Translator.getString("Report.Name.AcscNSFault");
		}
		return name + "_" + projectName + "."
				+ Translator.getString("ReportFileExtension");
	}
}
