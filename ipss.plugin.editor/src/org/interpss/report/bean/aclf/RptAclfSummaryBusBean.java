/*
 * @(#)RptAclfSummaryBusBean.java   
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

import org.interpss.report.bean.RptBaseBean;

public class RptAclfSummaryBusBean extends RptBaseBean {

	private String busId = null;
	private String busCode = null;
	private String busVolt = null;
	private String busAngle = null;
	private String busP = null;
	private String busQ = null;

	public RptAclfSummaryBusBean() {
	}

	public String getBusId() {
		return busId;
	}

	public String getBusCode() {
		return busCode;
	}

	public String getBusVolt() {
		return busVolt;
	}

	public String getBusAngle() {
		return busAngle;
	}

	public String getBusP() {
		return busP;
	}

	public String getBusQ() {
		return busQ;
	}

	/**
	 * @param busAngle The busAngle to set.
	 */
	public void setBusAngle(String busAngle) {
		this.busAngle = busAngle;
	}

	/**
	 * @param busCode The busCode to set.
	 */
	public void setBusCode(String busCode) {
		this.busCode = busCode;
	}

	/**
	 * @param busId The busId to set.
	 */
	public void setBusId(String busId) {
		this.busId = busId;
	}

	/**
	 * @param busP The busP to set.
	 */
	public void setBusP(String busP) {
		this.busP = busP;
	}

	/**
	 * @param busQ The busQ to set.
	 */
	public void setBusQ(String busQ) {
		this.busQ = busQ;
	}

	/**
	 * @param busVolt The busVolt to set.
	 */
	public void setBusVolt(String busVolt) {
		this.busVolt = busVolt;
	}
}