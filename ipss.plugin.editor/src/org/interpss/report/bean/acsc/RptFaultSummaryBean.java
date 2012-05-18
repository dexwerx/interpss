/*
 * @(#)RptFaultSummaryBean.java   
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

package org.interpss.report.bean.acsc;

import org.interpss.report.bean.RptBaseBean;

public class RptFaultSummaryBean extends RptBaseBean {
	public static int Type_BusFault = 1;
	public static int Type_BranchFault = 2;

	private int type = 1; // 1: busFault, 2: branchFault
	private String busId = null;
	private String busName = null;
	private String faultType = null;
	private String faultCode = null;
	private String faultAmpspu = null;
	private String faultAmps = null;
	private String faultDistance = "";

	/**
	 * @return the busId
	 */
	public String getBusId() {
		return busId;
	}

	/**
	 * @param busId the busId to set
	 */
	public void setBusId(String busId) {
		this.busId = busId;
	}

	/**
	 * @return the busName
	 */
	public String getBusName() {
		return busName;
	}

	/**
	 * @param busName the busName to set
	 */
	public void setBusName(String busName) {
		this.busName = busName;
	}

	/**
	 * @return the faultAmps
	 */
	public String getFaultAmps() {
		return faultAmps;
	}

	/**
	 * @param faultAmps the faultAmps to set
	 */
	public void setFaultAmps(String faultAmps) {
		this.faultAmps = faultAmps;
	}

	/**
	 * @return the faultAmpspu
	 */
	public String getFaultAmpspu() {
		return faultAmpspu;
	}

	/**
	 * @param faultAmpspu the faultAmpspu to set
	 */
	public void setFaultAmpspu(String faultAmpspu) {
		this.faultAmpspu = faultAmpspu;
	}

	/**
	 * @return the faultType
	 */
	public String getFaultType() {
		return faultType;
	}

	/**
	 * @param faultType the faultType to set
	 */
	public void setFaultType(String faultType) {
		this.faultType = faultType;
	}

	/**
	 * @return the faultCode
	 */
	public String getFaultCode() {
		return faultCode;
	}

	/**
	 * @param faultCode the faultType to set
	 */
	public void setFaultCode(String code) {
		this.faultCode = code;
	}

	public RptFaultSummaryBean() {
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the faultDistance
	 */
	public String getFaultDistance() {
		return faultDistance;
	}

	/**
	 * @param faultDistance the faultDistance to set
	 */
	public void setFaultDistance(String faultDistance) {
		this.faultDistance = faultDistance;
	}
}