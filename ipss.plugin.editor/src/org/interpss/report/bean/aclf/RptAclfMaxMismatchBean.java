/*
 * @(#)RptAclfMaxMismatchBean.java   
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

public class RptAclfMaxMismatchBean extends RptBaseBean {
	private String pMaxBusId = null;
	private String pMaxPu = null;
	private String pMaxKva = null;
	private String qMaxBusId = null;
	private String qMaxPu = null;
	private String qMaxKva = null;

	public RptAclfMaxMismatchBean() {
	}

	/**
	 * @return Returns the pMaxKva.
	 */
	public String getPMaxKva() {
		return pMaxKva;
	}

	/**
	 * @param maxKva The pMaxKva to set.
	 */
	public void setPMaxKva(String maxKva) {
		pMaxKva = maxKva;
	}

	/**
	 * @return Returns the pMaxPu.
	 */
	public String getPMaxPu() {
		return pMaxPu;
	}

	/**
	 * @param maxPu The pMaxPu to set.
	 */
	public void setPMaxPu(String maxPu) {
		pMaxPu = maxPu;
	}

	/**
	 * @return Returns the pMmaxBusId.
	 */
	public String getPMaxBusId() {
		return pMaxBusId;
	}

	/**
	 * @param mmaxBusId The pMmaxBusId to set.
	 */
	public void setPMaxBusId(String maxBusId) {
		pMaxBusId = maxBusId;
	}

	/**
	 * @return Returns the qMaxBusId.
	 */
	public String getQMaxBusId() {
		return qMaxBusId;
	}

	/**
	 * @param maxBusId The qMaxBusId to set.
	 */
	public void setQMaxBusId(String maxBusId) {
		qMaxBusId = maxBusId;
	}

	/**
	 * @return Returns the qMaxKva.
	 */
	public String getQMaxKva() {
		return qMaxKva;
	}

	/**
	 * @param maxKva The qMaxKva to set.
	 */
	public void setQMaxKva(String maxKva) {
		qMaxKva = maxKva;
	}

	/**
	 * @return Returns the qMaxPu.
	 */
	public String getQMaxPu() {
		return qMaxPu;
	}

	/**
	 * @param maxPu The qMaxPu to set.
	 */
	public void setQMaxPu(String maxPu) {
		qMaxPu = maxPu;
	}

}