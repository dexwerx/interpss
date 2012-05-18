/*
 * @(#)AclfRptSubrptControlRec.java   
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

public class AclfRptSubrptControlRec {
	private boolean pvLimitSubreport = true;
	private boolean pqLimitSubreport = true;
	private boolean funcloadSubreport = true;
	private boolean remoteQBusSubreport = true;
	private boolean tapVControlSubreport = true;
	private boolean psXfrPControlSubreport = true;

	/**
	 * @return the funcloadSubreport
	 */
	public boolean isFuncloadSubreport() {
		return funcloadSubreport;
	}

	/**
	 * @param funcloadSubreport
	 *            the funcloadSubreport to set
	 */
	public void setFuncloadSubreport(boolean funcloadSubreport) {
		this.funcloadSubreport = funcloadSubreport;
	}

	/**
	 * @return the pqLimitSubreport
	 */
	public boolean isPqLimitSubreport() {
		return pqLimitSubreport;
	}

	/**
	 * @param pqLimitSubreport
	 *            the pqLimitSubreport to set
	 */
	public void setPqLimitSubreport(boolean pqLimitSubreport) {
		this.pqLimitSubreport = pqLimitSubreport;
	}

	/**
	 * @return the psxfrPControlSubreport
	 */
	public boolean isPsXfrPControlSubreport() {
		return psXfrPControlSubreport;
	}

	/**
	 * @param psxfrPControlSubreport
	 *            the psxfrPControlSubreport to set
	 */
	public void setPsXfrPControlSubreport(boolean psXfrPControlSubreport) {
		this.psXfrPControlSubreport = psXfrPControlSubreport;
	}

	/**
	 * @return the pvLimitSubreport
	 */
	public boolean isPvLimitSubreport() {
		return pvLimitSubreport;
	}

	/**
	 * @param pvLimitSubreport
	 *            the pvLimitSubreport to set
	 */
	public void setPvLimitSubreport(boolean pvLimitSubreport) {
		this.pvLimitSubreport = pvLimitSubreport;
	}

	/**
	 * @return the remoteQBusSubreport
	 */
	public boolean isRemoteQBusSubreport() {
		return remoteQBusSubreport;
	}

	/**
	 * @param remoteQBusSubreport
	 *            the remoteQBusSubreport to set
	 */
	public void setRemoteQBusSubreport(boolean remoteQBusSubreport) {
		this.remoteQBusSubreport = remoteQBusSubreport;
	}

	/**
	 * @return the tapVControlSubreport
	 */
	public boolean isTapVControlSubreport() {
		return tapVControlSubreport;
	}

	/**
	 * @param tapVControlSubreport
	 *            the tapVControlSubreport to set
	 */
	public void setTapVControlSubreport(boolean tapVControlSubreport) {
		this.tapVControlSubreport = tapVControlSubreport;
	}
}
