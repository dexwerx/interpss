/*
 * @(#)SpringReportContext.java   
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

package org.interpss.spring;

import org.interpss.report.IIpssReport;

import com.interpss.spring.CoreCommonSpringFactory;

public class ReportSpringFactory extends CoreCommonSpringFactory {
	/**
	 * Get the AclfSummaryReport(singleton) from the SpringReportContext.
	 *  
	 * @return the IIpssReport object
	 */
	public static IIpssReport getAclfSummaryReport() {
		return (IIpssReport) SpringAppCtx
				.getBean("aclfSummaryReport");
	}

	/**
	 * Get the AclfBusStyleReport(singleton) from the SpringReportContext.
	 *  
	 * @return the IIpssReport object
	 */
	public static IIpssReport getAclfBusStyleReport() {
		return (IIpssReport) SpringAppCtx.getBean("aclfBusStyleReport");
	}

	/**
	 * Get the Acsc3PFaultReport(singleton) from the SpringReportContext.
	 *  
	 * @return the IIpssReport object
	 */
	public static IIpssReport getAcsc3PFaultReport() {
		return (IIpssReport) SpringAppCtx.getBean("acsc3PFaultReport");
	}

	/**
	 * Get the Acsc3PFaultReport(singleton) from the SpringReportContext.
	 *  
	 * @return the IIpssReport object
	 */
	public static IIpssReport getAcscNSFaultReport() {
		return (IIpssReport) SpringAppCtx.getBean("acscNSFaultReport");
	}
}