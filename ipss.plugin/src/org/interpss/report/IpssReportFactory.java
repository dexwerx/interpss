/*
 * @(#)IpssReportFactory.java   
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

package org.interpss.report;

import org.interpss.spring.ReportSpringFactory;

import net.sf.jasperreports.engine.JasperPrint;

public class IpssReportFactory {
	public static final String RPT_TYPE_ACLFSUMMARY = "AclfSummary";
	public static final String RPT_TYPE_ACLFBUSSTYLE = "AclfBusStyle";
	public static final String RPT_TYPE_ACSC3PFAULT = "Acsc3PFault";
	public static final String RPT_TYPE_ACSCNSFAULT = "AcscNSFault";

	public static JasperPrint createReport(String rptType, String version) {
		if (RPT_TYPE_ACLFSUMMARY.equals(rptType))
			return ReportSpringFactory.getAclfSummaryReport().createReport(
					version);
		else if (RPT_TYPE_ACLFBUSSTYLE.equals(rptType))
			return ReportSpringFactory.getAclfBusStyleReport().createReport(
					version);
		else if (RPT_TYPE_ACSC3PFAULT.equals(rptType))
			return ReportSpringFactory.getAcsc3PFaultReport().createReport(
					version);
		else if (RPT_TYPE_ACSCNSFAULT.equals(rptType))
			return ReportSpringFactory.getAcscNSFaultReport().createReport(
					version);
		else
			return null;
	}
}
