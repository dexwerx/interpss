/*
 * @(#)RptFuncLoadBean.java   
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

public class RptFuncLoadBean extends RptBaseBean {
	private String busId = null;
	private String pact = null;
	private String qact = null;
	private String v = null;
	private String p0 = null;
	private String q0 = null;
	private String status = null;

	public RptFuncLoadBean() {
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
	 * @return the p0
	 */
	public String getP0() {
		return p0;
	}

	/**
	 * @param p0 the p0 to set
	 */
	public void setP0(String p0) {
		this.p0 = p0;
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
	 * @return the q0
	 */
	public String getQ0() {
		return q0;
	}

	/**
	 * @param q0 the q0 to set
	 */
	public void setQ0(String q0) {
		this.q0 = q0;
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
}