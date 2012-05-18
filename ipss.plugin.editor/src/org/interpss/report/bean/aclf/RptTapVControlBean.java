/*
 * @(#)RptTapVControlBean.java   
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

public class RptTapVControlBean extends RptBaseBean {
	private String branchId = null;
	private String vcBusId = null;
	private String actual = null;
	private String spec = null;
	private String tap = null;
	private String tapMax = null;
	private String tapMin = null;
	private String stepSize = null;
	private String status = null;

	public RptTapVControlBean() {
	}

	/**
	 * @return the actual
	 */
	public String getActual() {
		return actual;
	}

	/**
	 * @param actual the actual to set
	 */
	public void setActual(String actual) {
		this.actual = actual;
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
	 * @return the spec
	 */
	public String getSpec() {
		return spec;
	}

	/**
	 * @param spec the spec to set
	 */
	public void setSpec(String spec) {
		this.spec = spec;
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

	/**
	 * @return the stepSize
	 */
	public String getStepSize() {
		return stepSize;
	}

	/**
	 * @param stepSize the stepSize to set
	 */
	public void setStepSize(String stepSize) {
		this.stepSize = stepSize;
	}

	/**
	 * @return the tap
	 */
	public String getTap() {
		return tap;
	}

	/**
	 * @param tap the tap to set
	 */
	public void setTap(String tap) {
		this.tap = tap;
	}

	/**
	 * @return the tapMax
	 */
	public String getTapMax() {
		return tapMax;
	}

	/**
	 * @param tapMax the tapMax to set
	 */
	public void setTapMax(String tapMax) {
		this.tapMax = tapMax;
	}

	/**
	 * @return the tapMin
	 */
	public String getTapMin() {
		return tapMin;
	}

	/**
	 * @param tapMin the tapMin to set
	 */
	public void setTapMin(String tapMin) {
		this.tapMin = tapMin;
	}

	/**
	 * @return the vcBusId
	 */
	public String getVcBusId() {
		return vcBusId;
	}

	/**
	 * @param vcBusId the vcBusId to set
	 */
	public void setVcBusId(String vcBusId) {
		this.vcBusId = vcBusId;
	}
}