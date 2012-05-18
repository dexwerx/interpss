/*
 * @(#)RptMainTitleBean.java   
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

package org.interpss.report.bean;

public class RptMainTitleBean extends RptBaseBean {
	private String reportTitle = "";
	private String versionNumber = "";
	private String projectName = "";
	private String userName = "";
	private String fileName = "";
	private String caseName = "";
	private String reportNumber = "";

	public RptMainTitleBean() {
	}

	public static RptMainTitleBean anAclfSample() {
		RptMainTitleBean bean = new RptMainTitleBean();
		bean.setReportTitle("Load Flow");
		bean.setVersionNumber("2.02");
		bean.setProjectName("ipssReport");
		bean.setUserName("InterpSS");
		bean.setFileName("JRViewMain.java");
		bean.setCaseName("Case Study 101");
		bean.setReportNumber("rpt20060228");
		return bean;
	}

	public static RptMainTitleBean anAcscSample() {
		RptMainTitleBean bean = new RptMainTitleBean();
		bean.setReportTitle("Short Circuit");
		bean.setVersionNumber("2.02");
		bean.setProjectName("ipssReport");
		bean.setUserName("InterpSS");
		bean.setFileName("JRViewMain.java");
		bean.setCaseName("Case Study 101");
		bean.setReportNumber("rpt20060228");
		return bean;
	}

	/**
	 * @return Returns the caseName.
	 */
	public String getCaseName() {
		return caseName;
	}

	/**
	 * @param caseName The caseName to set.
	 */
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return Returns the projectName.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName The projectName to set.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return Returns the reportNumber.
	 */
	public String getReportNumber() {
		return reportNumber;
	}

	/**
	 * @param reportNumber The reportNumber to set.
	 */
	public void setReportNumber(String reportNumber) {
		this.reportNumber = reportNumber;
	}

	/**
	 * @return Returns the reportTitle.
	 */
	public String getReportTitle() {
		return reportTitle;
	}

	/**
	 * @param reportTitle The reportTitle to set.
	 */
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the versionNumber.
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @param versionNumber The versionNumber to set.
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

}