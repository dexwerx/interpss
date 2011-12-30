 /*
  * @(#)ProjData.java   
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
	Current user project info
*/

import java.util.ArrayList;
import java.util.List;

import org.interpss.db.BaseDataBean;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.xml.schema.RunStudyCaseXmlType;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;

public class ProjData extends BaseDataBean implements IProjectData {
	private static final long serialVersionUID = 1;

	public static String NewProjName = "UnSaved";
	private boolean dirty = false;

	private int projectDbId = 0;
	private String projectName = NewProjName;
	private String description = "";
	private String workspacePath = null;
	private String filepath = null;
	private String dbSchemaVersion = "";

	private RunStudyCaseXmlType stdRunXmlDoc = null;
	private RunStudyCaseXmlType scriptRunXmlDoc = null;
	
	private List caseList = new ArrayList();

	/* current case name */
	private String dclfCaseName = null;
	private String pTradingCaseName = null;
	private String aclfCaseName = null;
	private String acscCaseName = null;
	private String dStabCaseName = null;
	private String scriptsCaseName = null;

	public ProjData() {}

	public int getProjectDbId() {return this.projectDbId;}
	public void setProjectDbId(int id) {this.projectDbId = id; }

	public String getProjectName() {return this.projectName;}
	public void setProjectName(String str) {this.projectName = str;}

	public String getDescription() {return this.description;}
	public void setDescription(String str) {this.description = str;}

	public String getFilepath() {return this.filepath;}
	public String getFilename() {return StringUtil.getFileName(this.filepath);}
	public void setFilepath(String str) {this.filepath = str;}

	public boolean isDirty() {return this.dirty;}
	public void setDirty(boolean b) {
		IpssLogger.getLogger().info("ProjData dirty set to " + b);
		this.dirty = b; 
	}

	public List getCaseList() {return this.caseList;}
	public void setCaseList(List list) {this.caseList = list;}

	// logic functions

	/**
	*	Check if the current case is a new case
	*
	* @return true if is a new case 
	*/
	public boolean isNewProject() { return this.filepath.equals(""); }
	
	/**
	* 	Set the current case to a new case
	*/
	public void setToNewProject() {
		this.projectName = NewProjName;
		this.description = "";
		this.filepath = "";
		this.caseList = new ArrayList();
	}

	/**
	 * @return Returns the dclfCaseName.
	 */
	public String getDclfCaseName() {
		return dclfCaseName;
	}

	/**
	 * @param aclfCaseName The aclfCaseName to set.
	 */
	public void setDclfCaseName(String dclfCaseName) {
		this.dclfCaseName = dclfCaseName;
	}
	
	public String getPTradingCaseName() {
		return pTradingCaseName;
	}
	public void setPTradingCaseName(String n) {
		this.pTradingCaseName = n;
	}

	/**
	 * @return Returns the aclfCaseName.
	 */
	public String getAclfCaseName() {
		return aclfCaseName;
	}

	/**
	 * @param aclfCaseName The aclfCaseName to set.
	 */
	public void setAclfCaseName(String aclfCaseName) {
		this.aclfCaseName = aclfCaseName;
	}

	/**
	 * @return Returns the acscCaseName.
	 */
	public String getAcscCaseName() {
		return acscCaseName;
	}

	/**
	 * @param acscCaseName The acscCaseName to set.
	 */
	public void setAcscCaseName(String name) {
		this.acscCaseName = name;
	}

	/**
	 * @return Returns the dstabCaseName.
	 */
	public String getDStabCaseName() {
		return dStabCaseName;
	}

	/**
	 * @param dstabCaseName The dstabCaseName to set.
	 */
	public void setDStabCaseName(String name) {
		this.dStabCaseName = name;
	}

	/**
	 * @return the workspacePath
	 */
	public String getWorkspacePath() {
		return workspacePath;
	}

	/**
	 * @param workspacePath the workspacePath to set
	 */
	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	/**
	 * @return the dbSchemaVersion
	 */
	public String getDbSchemaVersion() {
		return dbSchemaVersion;
	}

	/**
	 * @param dbSchemaVersion the dbSchemaVersion to set
	 */
	public void setDbSchemaVersion(String dbSchemaVersion) {
		this.dbSchemaVersion = dbSchemaVersion;
	}

	public String getScriptsCaseName() {
		return scriptsCaseName;
	}

	public void setScriptsCaseName(String scriptsCaseName) {
		this.scriptsCaseName = scriptsCaseName;
	}
}