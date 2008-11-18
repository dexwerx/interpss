 /*
  * @(#)DBStudyCase.java   
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

package org.interpss.editor.data.proj;


/**
	For persisting CaseData to the StudyCase table
*/

public class DBStudyCase {
	private static final long serialVersionUID = 1;

	private int caseId = 0;
	private int projDbId = 0;
	private String caseName = "";
	private String caseType = "";
	private String dataXmlString = "";
	
    public DBStudyCase() {}

	/**
	 * @return the caseId
	 */
	public int getCaseId() {
		return caseId;
	}

	/**
	 * @param caseId the caseId to set
	 */
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}


	/**
	 * @return the projDbId
	 */
	public int getProjDbId() {
		return projDbId;
	}

	/**
	 * @param projDbId the projDbId to set
	 */
	public void setProjDbId(int projDbId) {
		this.projDbId = projDbId;
	}
	
	/**
	 * @return the caseType
	 */
	public String getCaseName() {
		return caseName;
	}

	/**
	 * @param caseType the caseType to set
	 */
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	
	/**
	 * @return the caseType
	 */
	public String getCaseType() {
		return caseType;
	}

	/**
	 * @param caseType the caseType to set
	 */
	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	/**
	 * @return the dataXmlString
	 */
	public String getDataXmlString() {
		return dataXmlString;
	}

	/**
	 * @param dataXmlString the dataXmlString to set
	 */
	public void setDataXmlString(String dataXmlString) {
		this.dataXmlString = dataXmlString;
	}

	public static DBStudyCase createDBStudyCase(CaseData data) {
		DBStudyCase c = new DBStudyCase();
		c.setCaseId(data.getCaseDbId());
		c.setProjDbId(data.getProjDbId());
		c.setCaseName(data.getCaseName());
		c.setCaseType(data.getCaseType().toString());
		c.setDataXmlString(data.toString());
		return c; 
	}
	
	public CaseData getCaseData() {
		//CaseData aCase = (CaseData)XmlUtil.toObject(this.dataXmlString, CaseData.class);
		CaseData aCase = new CaseData();
		// case db id may be changed
		aCase.setCaseDbId(0);
		return aCase;
	}
}