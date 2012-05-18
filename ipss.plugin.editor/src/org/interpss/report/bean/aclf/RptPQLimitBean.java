/*
 * @(#)RptPQLimitBean.java   
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

public class RptPQLimitBean extends RptBaseBean {
	private String busId = null;
	private String qact = null;
	private String qspec = null;
	private String v = null;
	private String vmax = null;
	private String vmin = null;
	private String status = null;

	public RptPQLimitBean() {
	}

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
	 * @return the qact
	 */
	public String getQact() {
		return qact;
	}

	/**
	 * @param qact the qact to set
	 */
	public void setQact(String qact) {
		this.qact = qact;
	}

	/**
	 * @return the qspec
	 */
	public String getQspec() {
		return qspec;
	}

	/**
	 * @param qspec the qspec to set
	 */
	public void setQspec(String qspec) {
		this.qspec = qspec;
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
	 * @return the v
	 */
	public String getV() {
		return v;
	}

	/**
	 * @param v the v to set
	 */
	public void setV(String v) {
		this.v = v;
	}

	/**
	 * @return the vmax
	 */
	public String getVmax() {
		return vmax;
	}

	/**
	 * @param vmax the vmax to set
	 */
	public void setVmax(String vmax) {
		this.vmax = vmax;
	}

	/**
	 * @return the vmin
	 */
	public String getVmin() {
		return vmin;
	}

	/**
	 * @param vmin the vmin to set
	 */
	public void setVmin(String vmin) {
		this.vmin = vmin;
	}

}