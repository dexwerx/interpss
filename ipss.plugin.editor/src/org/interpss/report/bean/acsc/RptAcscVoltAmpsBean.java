/*
 * @(#)RptAcscVoltAmpsBean.java   
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

public class RptAcscVoltAmpsBean extends RptBaseBean {
	// for 3P fault
	public static int RecType_Bus3P = 1;
	public static int RecType_Branch3P = 2;

	// for NS fault
	public static int RecType_Bus012 = 1;
	public static int RecType_BusABC = 2;
	public static int RecType_Branch012 = 3;
	public static int RecType_BranchABC = 4;

	private int recType = 0;
	private String busId = null;
	private String busName = null;
	private String busFaultVoltpu = null;
	private String busFaultVolt = null;
	private String busFaultVolt2pu = null;
	private String busFaultVolt2 = null;
	private String busFaultVolt0pu = null;
	private String busFaultVolt0 = null;
	private String busFaultVoltApu = null;
	private String busFaultVoltA = null;
	private String busFaultVoltBpu = null;
	private String busFaultVoltB = null;
	private String busFaultVoltCpu = null;
	private String busFaultVoltC = null;
	private String busContribAmpspu = null;
	private String busContribAmps = null;

	private String branchId = null;
	private String branchName = null;
	private String branchFaultAmpspu = null;
	private String branchFaultAmps = null;
	private String branchFaultAmps2pu = null;
	private String branchFaultAmps2 = null;
	private String branchFaultAmps0pu = null;
	private String branchFaultAmps0 = null;
	private String branchFaultAmpsApu = null;
	private String branchFaultAmpsA = null;
	private String branchFaultAmpsBpu = null;
	private String branchFaultAmpsB = null;
	private String branchFaultAmpsCpu = null;
	private String branchFaultAmpsC = null;

	public RptAcscVoltAmpsBean() {
	}

	/**
	 * @return the branch
	 */
	public String getBranchId() {
		return branchId;
	}

	/**
	 * @param branch the branch to set
	 */
	public void setBranchId(String branch) {
		this.branchId = branch;
	}

	/**
	 * @return the branchFaultAmps
	 */
	public String getBranchFaultAmps() {
		return branchFaultAmps;
	}

	/**
	 * @param branchFaultAmps the branchFaultAmps to set
	 */
	public void setBranchFaultAmps(String branchFaultAmps) {
		this.branchFaultAmps = branchFaultAmps;
	}

	/**
	 * @return the branchFaultAmpspu
	 */
	public String getBranchFaultAmpspu() {
		return branchFaultAmpspu;
	}

	/**
	 * @param branchFaultAmpspu the branchFaultAmpspu to set
	 */
	public void setBranchFaultAmpspu(String branchFaultAmpspu) {
		this.branchFaultAmpspu = branchFaultAmpspu;
	}

	/**
	 * @return the busContribAmps
	 */
	public String getBusContribAmps() {
		return busContribAmps;
	}

	/**
	 * @param busContribAmps the busContribAmps to set
	 */
	public void setBusContribAmps(String busContribAmps) {
		this.busContribAmps = busContribAmps;
	}

	/**
	 * @return the busContribAmpspu
	 */
	public String getBusContribAmpspu() {
		return busContribAmpspu;
	}

	/**
	 * @param busContribAmpspu the busContribAmpspu to set
	 */
	public void setBusContribAmpspu(String busContribAmpspu) {
		this.busContribAmpspu = busContribAmpspu;
	}

	/**
	 * @return the busFaultVolt
	 */
	public String getBusFaultVolt() {
		return busFaultVolt;
	}

	/**
	 * @param busFaultVolt the busFaultVolt to set
	 */
	public void setBusFaultVolt(String busFaultVolt) {
		this.busFaultVolt = busFaultVolt;
	}

	/**
	 * @return the busFaultVoltpu
	 */
	public String getBusFaultVoltpu() {
		return busFaultVoltpu;
	}

	/**
	 * @param busFaultVoltpu the busFaultVoltpu to set
	 */
	public void setBusFaultVoltpu(String busFaultVoltpu) {
		this.busFaultVoltpu = busFaultVoltpu;
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
	 * @return the recType
	 */
	public int getRecType() {
		return recType;
	}

	/**
	 * @param recType the recType to set
	 */
	public void setRecType(int recType) {
		this.recType = recType;
	}

	/**
	 * @return the branchFaultAmpsC
	 */
	public String getBranchFaultAmpsC() {
		return branchFaultAmpsC;
	}

	/**
	 * @param branchFaultAmpsC the branchFaultAmpsC to set
	 */
	public void setBranchFaultAmpsC(String branchFaultAmpsC) {
		this.branchFaultAmpsC = branchFaultAmpsC;
	}

	/**
	 * @return the branchFaultAmpsCpu
	 */
	public String getBranchFaultAmpsCpu() {
		return branchFaultAmpsCpu;
	}

	/**
	 * @param branchFaultAmpsCpu the branchFaultAmpsCpu to set
	 */
	public void setBranchFaultAmpsCpu(String branchFaultAmpsCpu) {
		this.branchFaultAmpsCpu = branchFaultAmpsCpu;
	}

	/**
	 * @return the busFaultVolt0
	 */
	public String getBusFaultVolt0() {
		return busFaultVolt0;
	}

	/**
	 * @param busFaultVolt0 the busFaultVolt0 to set
	 */
	public void setBusFaultVolt0(String busFaultVolt0) {
		this.busFaultVolt0 = busFaultVolt0;
	}

	/**
	 * @return the busFaultVolt0pu
	 */
	public String getBusFaultVolt0pu() {
		return busFaultVolt0pu;
	}

	/**
	 * @param busFaultVolt0pu the busFaultVolt0pu to set
	 */
	public void setBusFaultVolt0pu(String busFaultVolt0pu) {
		this.busFaultVolt0pu = busFaultVolt0pu;
	}

	/**
	 * @return the busFaultVolt2
	 */
	public String getBusFaultVolt2() {
		return busFaultVolt2;
	}

	/**
	 * @param busFaultVolt2 the busFaultVolt2 to set
	 */
	public void setBusFaultVolt2(String busFaultVolt2) {
		this.busFaultVolt2 = busFaultVolt2;
	}

	/**
	 * @return the busFaultVolt2pu
	 */
	public String getBusFaultVolt2pu() {
		return busFaultVolt2pu;
	}

	/**
	 * @param busFaultVolt2pu the busFaultVolt2pu to set
	 */
	public void setBusFaultVolt2pu(String busFaultVolt2pu) {
		this.busFaultVolt2pu = busFaultVolt2pu;
	}

	/**
	 * @return the busFaultVoltA
	 */
	public String getBusFaultVoltA() {
		return busFaultVoltA;
	}

	/**
	 * @param busFaultVoltA the busFaultVoltA to set
	 */
	public void setBusFaultVoltA(String busFaultVoltA) {
		this.busFaultVoltA = busFaultVoltA;
	}

	/**
	 * @return the busFaultVoltApu
	 */
	public String getBusFaultVoltApu() {
		return busFaultVoltApu;
	}

	/**
	 * @param busFaultVoltApu the busFaultVoltApu to set
	 */
	public void setBusFaultVoltApu(String busFaultVoltApu) {
		this.busFaultVoltApu = busFaultVoltApu;
	}

	/**
	 * @return the busFaultVoltB
	 */
	public String getBusFaultVoltB() {
		return busFaultVoltB;
	}

	/**
	 * @param busFaultVoltB the busFaultVoltB to set
	 */
	public void setBusFaultVoltB(String busFaultVoltB) {
		this.busFaultVoltB = busFaultVoltB;
	}

	/**
	 * @return the busFaultVoltBpu
	 */
	public String getBusFaultVoltBpu() {
		return busFaultVoltBpu;
	}

	/**
	 * @param busFaultVoltBpu the busFaultVoltBpu to set
	 */
	public void setBusFaultVoltBpu(String busFaultVoltBpu) {
		this.busFaultVoltBpu = busFaultVoltBpu;
	}

	/**
	 * @return the busFaultVoltC
	 */
	public String getBusFaultVoltC() {
		return busFaultVoltC;
	}

	/**
	 * @param busFaultVoltC the busFaultVoltC to set
	 */
	public void setBusFaultVoltC(String busFaultVoltC) {
		this.busFaultVoltC = busFaultVoltC;
	}

	/**
	 * @return the busFaultVoltCpu
	 */
	public String getBusFaultVoltCpu() {
		return busFaultVoltCpu;
	}

	/**
	 * @param busFaultVoltCpu the busFaultVoltCpu to set
	 */
	public void setBusFaultVoltCpu(String busFaultVoltCpu) {
		this.busFaultVoltCpu = busFaultVoltCpu;
	}

	/**
	 * @return the branchFaultAmps0
	 */
	public String getBranchFaultAmps0() {
		return branchFaultAmps0;
	}

	/**
	 * @param branchFaultAmps0 the branchFaultAmps0 to set
	 */
	public void setBranchFaultAmps0(String branchFaultAmps0) {
		this.branchFaultAmps0 = branchFaultAmps0;
	}

	/**
	 * @return the branchFaultAmps0pu
	 */
	public String getBranchFaultAmps0pu() {
		return branchFaultAmps0pu;
	}

	/**
	 * @param branchFaultAmps0pu the branchFaultAmps0pu to set
	 */
	public void setBranchFaultAmps0pu(String branchFaultAmps0pu) {
		this.branchFaultAmps0pu = branchFaultAmps0pu;
	}

	/**
	 * @return the branchFaultAmps2
	 */
	public String getBranchFaultAmps2() {
		return branchFaultAmps2;
	}

	/**
	 * @param branchFaultAmps2 the branchFaultAmps2 to set
	 */
	public void setBranchFaultAmps2(String branchFaultAmps2) {
		this.branchFaultAmps2 = branchFaultAmps2;
	}

	/**
	 * @return the branchFaultAmps2pu
	 */
	public String getBranchFaultAmps2pu() {
		return branchFaultAmps2pu;
	}

	/**
	 * @param branchFaultAmps2pu the branchFaultAmps2pu to set
	 */
	public void setBranchFaultAmps2pu(String branchFaultAmps2pu) {
		this.branchFaultAmps2pu = branchFaultAmps2pu;
	}

	/**
	 * @return the branchFaultAmpsA
	 */
	public String getBranchFaultAmpsA() {
		return branchFaultAmpsA;
	}

	/**
	 * @param branchFaultAmpsA the branchFaultAmpsA to set
	 */
	public void setBranchFaultAmpsA(String branchFaultAmpsA) {
		this.branchFaultAmpsA = branchFaultAmpsA;
	}

	/**
	 * @return the branchFaultAmpsApu
	 */
	public String getBranchFaultAmpsApu() {
		return branchFaultAmpsApu;
	}

	/**
	 * @param branchFaultAmpsApu the branchFaultAmpsApu to set
	 */
	public void setBranchFaultAmpsApu(String branchFaultAmpsApu) {
		this.branchFaultAmpsApu = branchFaultAmpsApu;
	}

	/**
	 * @return the branchFaultAmpsB
	 */
	public String getBranchFaultAmpsB() {
		return branchFaultAmpsB;
	}

	/**
	 * @param branchFaultAmpsB the branchFaultAmpsB to set
	 */
	public void setBranchFaultAmpsB(String branchFaultAmpsB) {
		this.branchFaultAmpsB = branchFaultAmpsB;
	}

	/**
	 * @return the branchFaultAmpsBpu
	 */
	public String getBranchFaultAmpsBpu() {
		return branchFaultAmpsBpu;
	}

	/**
	 * @param branchFaultAmpsBpu the branchFaultAmpsBpu to set
	 */
	public void setBranchFaultAmpsBpu(String branchFaultAmpsBpu) {
		this.branchFaultAmpsBpu = branchFaultAmpsBpu;
	}

	/**
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}

	/**
	 * @param branchName the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
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
}