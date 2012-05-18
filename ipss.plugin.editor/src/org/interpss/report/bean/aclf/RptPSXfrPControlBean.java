/*
 * @(#)RptPSXfrPControlBean.java   
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

public class RptPSXfrPControlBean extends RptBaseBean {
	private String branchId = null;
	private String pact = null;
	private String pspec = null;
	private String angle = null;
	private String angMax = null;
	private String angMin = null;
	private String status = null;

	public RptPSXfrPControlBean() {
	}

	/**
	 * @return the angle
	 */
	public String getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(String angle) {
		this.angle = angle;
	}

	/**
	 * @return the angMax
	 */
	public String getAngMax() {
		return angMax;
	}

	/**
	 * @param angMax the angMax to set
	 */
	public void setAngMax(String angMax) {
		this.angMax = angMax;
	}

	/**
	 * @return the angMin
	 */
	public String getAngMin() {
		return angMin;
	}

	/**
	 * @param angMin the angMin to set
	 */
	public void setAngMin(String angMin) {
		this.angMin = angMin;
	}

	/**
	 * @return the branchId
	 */
	public String getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId the branchId to set
	 */
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	/**
	 * @return the pact
	 */
	public String getPact() {
		return pact;
	}

	/**
	 * @param pact the pact to set
	 */
	public void setPact(String pact) {
		this.pact = pact;
	}

	/**
	 * @return the pspec
	 */
	public String getPspec() {
		return pspec;
	}

	/**
	 * @param pspec the pspec to set
	 */
	public void setPspec(String pspec) {
		this.pspec = pspec;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}