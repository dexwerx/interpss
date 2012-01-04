/*
 * @(#)AppSimuContextImpl.java   
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

package org.interpss.editor.app;

/**
 * A Facede for for all application related info
 */

import java.util.List;
import java.util.Vector;

import javax.swing.JPopupMenu;

import org.interpss.db.DBManager;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.chart.ChartManager;
import org.interpss.editor.data.proj.CaseData;
import org.interpss.editor.data.proj.ProjData;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.editor.runAct.ui.AcscRunForm;
import org.interpss.editor.runAct.ui.DStabRunForm;
import org.interpss.spring.EditorSimuSpringFactory;
import org.interpss.xml.schema.AcscFaultCategoryDataType;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.XmlBeanUtil;
import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreSimuSpringFactory;

public class AppSimuContextImpl implements IAppSimuContext {

	// Project info - loaded when loading a project
	private IProjectData projData = null;

	// Simulation info - mapped when run a project
	private SimuContext simuCtx = null;
	private boolean simuCtxDataDirty = false;
	private boolean lfConverged = false;
	private boolean scCalculated = false;

	private DStabRunForm dStabRunForm = null;

	// track the last run type
	private SimuRunEnum lastRunType = null;

	// the current case db id
	private int dbSimuCaseId = 0;

	public AppSimuContextImpl() {
		this.simuCtxDataDirty = true;
	}

	public void reset() {
		getProjData().setToNewProject();
		this.lfConverged = false;
		this.scCalculated = false;
		this.simuCtxDataDirty = true;
		setDbSimuCaseId(0);
	}

	public Object getSimuCtx() {
		if (this.simuCtx == null) {
			IpssLogger.getLogger().info("SimuSpringAppContext.getSimuContext() called to create a new SimuContext object");
			this.simuCtx = CoreSimuSpringFactory.getSimuContext();
			this.simuCtx.setMsgHub(CoreSimuSpringFactory.getIpssMsgHub());
		}
		return this.simuCtx;
	}

	public void setSimuCtx(Object ctx) {
		this.simuCtx = (SimuContext) ctx;
		this.simuCtx.setMsgHub(CoreSimuSpringFactory.getIpssMsgHub());
	}

	/* current project info, including projname ... */

	public IProjectData getProjData() {
		if (this.projData == null) {
			this.projData = EditorSimuSpringFactory.getProjectData();
			this.projData.setDbSchemaVersion(DBManager.DB_SCHEMA_VERSION);
		}
		return this.projData;
	}

	public void setProjData(IProjectData info) {
		this.projData = info;
	}

	/* For Acsc calculation, also for reporting */
	public boolean isLfConverged() {
		return this.lfConverged;
	}

	public void setLfConverged(boolean b) {
		this.lfConverged = b;
	}

	/* Trace appCtx modification status */
	public boolean isSimuNetDataDirty() {
		return this.simuCtxDataDirty;
	}

	public void setSimuNetDataDirty(boolean b) {
		this.simuCtxDataDirty = b;
	}

	/**
	 * Check if the current AcscRunForm has a non-symmetric fault
	 * 
	 * @return
	 */
	public boolean isNonSymmetricFault() {
		AcscRunForm form = EditorSimuSpringFactory.getAcscRunForm();
		if (form != null && form.getXmlCaseData() != null && form.getXmlCaseData().getFaultData() != null)
			return form.getXmlCaseData().getFaultData().getFaultCategory() != AcscFaultCategoryDataType.FAULT_3_P;
		else 
			return false;
	}

	// Case info functions
	// ===================

	public String getCurrentCaseName(SimuRunEnum caseType) {
		return getCurrentCaseData(caseType).getCaseName();

	}

	public CaseData getCurrentCaseData(SimuRunEnum caseType) {
		ProjData aProjData = (ProjData) getProjData();
		String casename = caseType.equals(SimuRunEnum.Aclf) ? aProjData
				.getAclfCaseName()
				: (caseType.equals(SimuRunEnum.Acsc) ? aProjData
						.getAcscCaseName() : aProjData.getDStabCaseName());
		return getCaseData(casename, caseType);
	}

	/**
	 * Get an array of case data object for the case type
	 * 
	 * @param caseType
	 *            case type
	 * @return case data array of type Object[]
	 */
	public String getCasename(SimuRunEnum caseType) {
			String name = "Aclf Analysis Case";
			if (caseType.equals(SimuRunEnum.SenAnalysis))
				name = "Sensitivity Analysis Case";
			else if (caseType.equals(SimuRunEnum.Acsc))
				name = "Acsc Analysis Case";
			else if (caseType.equals(SimuRunEnum.DStab))
				name = "Transient Stability Case";
			else if (caseType.equals(SimuRunEnum.Scripts))
				name = "Custom Scripting Run Case";
			return name;
	}

	/**
	 * Delete the case data by casename and case type
	 * 
	 * @param casename
	 *            case name
	 * @param caseType
	 *            case type
	 * @return if the case data delete, return true, else false
	 */
	public boolean deleteCaseData(String casename, SimuRunEnum caseType) {
		CaseData caseData = getCaseData(casename, caseType);
		if (caseData != null) {
			getProjData().getCaseList().remove(caseData);
			return true;
		}
		return false;
	}

	/**
	 * Get the CaseData object by casename and case type
	 * 
	 * @param casename
	 *            case name
	 * @param caseType
	 *            case type
	 * @return the case data object
	 */
	public CaseData getCaseData(String casename, SimuRunEnum caseType) {
		List<?> caseList = getProjData().getCaseList();
		for (int i = 0; i < caseList.size(); i++) {
			CaseData caseData = (CaseData) caseList.get(i);
			if (caseData != null)
				if (casename.equals(caseData.getCaseName())
						&& (caseData.getCaseType().equals(caseType))) {
					IpssLogger.getLogger().info(
							"CaseInfo found, casename: " + casename);
					return caseData;
				}
		}
		return null;
	}

	/**
	 * Create a CaseData object with the casename and put into <*>RunForm. Since
	 * casename has to be unique in a project, if a caseInfo object with the
	 * same casename found, return null.
	 * 
	 * @param casename
	 *            the case name
	 * @param caseType
	 *            the case type
	 * @return the created case, null the casename already exists
	 */
	public CaseData createCaseData(String casename, SimuRunEnum caseType) {
		if (getCaseData(casename, caseType) == null) {
			CaseData caseData = new CaseData();
			caseData.setCaseName(casename);
			caseData.setCaseType(caseType);
			List list = getProjData().getCaseList();
			list.add(caseData);
			IpssLogger.getLogger().info("CaseInfo created, casename: " + casename);
			return caseData;
		} else
			return null;
	}

	/**
	 * Get all popup menu actions for the cell
	 */
	public void addPopupMenuAction(JPopupMenu menu, final Object cell) {
		IpssLogger.getLogger().info(
				"AppSimuContextImpl.addPopupMenuAction called");
		try {
			ChartManager.addPopupMenuAction(menu, cell);
		} catch (Exception ex) {
			IpssLogger.getLogger().severe(ex.toString());
		}
	}

	/**
	 * Convert the net to a string for display purpose, including bus and branch
	 * 
	 * @return the string representation
	 */
	public String toString() {
		return XmlBeanUtil.toXmlString(this);
	}

	public void releaseResource() {
	}

	/**
	 * @return Returns the scCalculated.
	 */
	public boolean isScCalculated() {
		return scCalculated;
	}

	/**
	 * @param scCalculated
	 *            The scCalculated to set.
	 */
	public void setScCalculated(boolean scCalculated) {
		this.scCalculated = scCalculated;
	}

	/**
	 * @return the lastRunType
	 */
	public SimuRunEnum getLastRunType() {
		return lastRunType;
	}

	public boolean hasLastRun() {
		return lastRunType != null;
	}

	public DStabRunForm getDStabRunForm() {
		if (this.dStabRunForm == null)
			this.dStabRunForm = EditorSimuSpringFactory.getDStabRunForm();
		return this.dStabRunForm;
	}

	/**
	 * @param lastRunType
	 *            the lastRunType to set
	 */
	public void setLastRunType(SimuRunEnum lastRunType) {
		this.lastRunType = lastRunType;
	}

	/**
	 * @return Returns the dbSimuCaseId.
	 */
	public int getDbSimuCaseId() {
		return dbSimuCaseId;
	}

	/**
	 * @param dbSimuCaseId
	 *            The dbSimuCaseId to set.
	 */
	public void setDbSimuCaseId(int dbSimuCaseId) {
		this.dbSimuCaseId = dbSimuCaseId;
	}
}