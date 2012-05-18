/*
 * @(#)RptAclfBusStyleBean.java   
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

public class RptAclfBusStyleBean extends RptBaseBean {
	private String busId = null;
	private String baseVolt = null;
	private String voltMsg = null;
	private String voltAng = null;
	private String pGen = null;
	private String QGen = null;
	private String pLoad = null;
	private String QLoad = null;
	private String toBusId = null;
	private String branchP = null;
	private String branchQ = null;
	private String branchKA = null;
	private String xfrRatioFrom = null;
	private String xfrRatioTo = null;
	private String psXfrAngle = null;

	public RptAclfBusStyleBean() {
	}

	/**
	 * @return the baseVolt
	 */
	public String getBaseVolt() {
		return baseVolt;
	}

	/**
	 * @param baseVolt the baseVolt to set
	 */
	public void setBaseVolt(String baseVolt) {
		this.baseVolt = baseVolt;
	}

	/**
	 * @return the branchKA
	 */
	public String getBranchKA() {
		return branchKA;
	}

	/**
	 * @param branchKA the branchKA to set
	 */
	public void setBranchKA(String branchKA) {
		this.branchKA = branchKA;
	}

	/**
	 * @return the branchP
	 */
	public String getBranchP() {
		return branchP;
	}

	/**
	 * @param branchP the branchP to set
	 */
	public void setBranchP(String branchP) {
		this.branchP = branchP;
	}

	/**
	 * @return the branchQ
	 */
	public String getBranchQ() {
		return branchQ;
	}

	/**
	 * @param branchQ the branchQ to set
	 */
	public void setBranchQ(String branchQ) {
		this.branchQ = branchQ;
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
	 * @return the pGen
	 */
	public String getPGen() {
		return pGen;
	}

	/**
	 * @param gen the pGen to set
	 */
	public void setPGen(String gen) {
		pGen = gen;
	}

	/**
	 * @return the pLoad
	 */
	public String getPLoad() {
		return pLoad;
	}

	/**
	 * @param load the pLoad to set
	 */
	public void setPLoad(String load) {
		pLoad = load;
	}

	/**
	 * @return the psXfrAngle
	 */
	public String getPsXfrAngle() {
		return psXfrAngle;
	}

	/**
	 * @param psXfrAngle the psXfrAngle to set
	 */
	public void setPsXfrAngle(String psXfrAngle) {
		this.psXfrAngle = psXfrAngle;
	}

	/**
	 * @return the qGen
	 */
	public String getQGen() {
		return QGen;
	}

	/**
	 * @param gen the qGen to set
	 */
	public void setQGen(String gen) {
		QGen = gen;
	}

	/**
	 * @return the qLoad
	 */
	public String getQLoad() {
		return QLoad;
	}

	/**
	 * @param load the qLoad to set
	 */
	public void setQLoad(String load) {
		QLoad = load;
	}

	/**
	 * @return the toBusId
	 */
	public String getToBusId() {
		return toBusId;
	}

	/**
	 * @param toBusId the toBusId to set
	 */
	public void setToBusId(String toBusId) {
		this.toBusId = toBusId;
	}

	/**
	 * @return the voltAng
	 */
	public String getVoltAng() {
		return voltAng;
	}

	/**
	 * @param voltAng the voltAng to set
	 */
	public void setVoltAng(String voltAng) {
		this.voltAng = voltAng;
	}

	/**
	 * @return the voltMsg
	 */
	public String getVoltMsg() {
		return voltMsg;
	}

	/**
	 * @param voltMsg the voltMsg to set
	 */
	public void setVoltMsg(String voltMsg) {
		this.voltMsg = voltMsg;
	}

	/**
	 * @return the xfrRatioFrom
	 */
	public String getXfrRatioFrom() {
		return xfrRatioFrom;
	}

	/**
	 * @param xfrRatioFrom the xfrRatioFrom to set
	 */
	public void setXfrRatioFrom(String xfrRatioFrom) {
		this.xfrRatioFrom = xfrRatioFrom;
	}

	/**
	 * @return the xfrRatioTo
	 */
	public String getXfrRatioTo() {
		return xfrRatioTo;
	}

	/**
	 * @param xfrRatioTo the xfrRatioTo to set
	 */
	public void setXfrRatioTo(String xfrRatioTo) {
		this.xfrRatioTo = xfrRatioTo;
	}
}