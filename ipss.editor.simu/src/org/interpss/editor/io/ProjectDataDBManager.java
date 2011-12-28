/*
 * @(#)ProjectDataDBManager.java   
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

package org.interpss.editor.io;

import java.sql.SQLException;
import java.util.List;

import org.interpss.editor.data.proj.CaseData;
import org.interpss.editor.data.proj.DBStudyCase;
import org.interpss.editor.data.proj.ProjData;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.output.ISimuRecManager;
import org.interpss.output.db.DBManager;
import org.interpss.output.db.IpssDBCase;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.IProjectDataManager;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.common.util.XmlBeanUtil;

public class ProjectDataDBManager implements IProjectDataManager {
	/**
	 * Save the project data into DB
	 */
	public void saveProjectDataToDB(Object projData) throws InterpssException {
		dbActionProject(DBManager.SQL_ACTION_UPDATE, projData);
	}

	/**
	 * Load project data from DB using projDbId. If no projDbId or no project
	 * with the projDbId found, use filename, projName to create a DB record for
	 * the project
	 * 
	 */
	public void loadProjectDataFromDB(int projDbId, String filename,
			String projName, Object appSimuCtx) {
		IProjectData p = loadProjectDataFromDB(projDbId, filename, projName);
		((IAppSimuContext) appSimuCtx).setProjData(p);
	}

	/**
	 * Load project data from DB using projDbId. If no projDbId or no project
	 * with the projDbId found, use filename, projName to create a DB record for
	 * the project
	 * 
	 */
	public IProjectData loadProjectDataFromDB(int projDbId, String filename,
			String projName) {
		IProjectData projData = new ProjData();
		IProjectData p = null;
		// first try to select the project with the projDbId
		if (projDbId > 0) {
			try {
				projData.setProjectDbId(projDbId);
				p = (IProjectData) dbActionProject(DBManager.SQL_ACTION_SELECT,
						projData);
			} catch (Exception ex) {
				IpssLogger.getLogger().warning(ex.toString());
				try {
					deleteDbProject(projDbId);
				} catch (Exception e) {
					IpssLogger.logErr(e);
				}
			}
		}
		// if the project is not found, insert a new project
		if (p == null) {
			projData.setFilepath(filename);
			projData.setWorkspacePath(StringUtil.getWorkspacePath(filename));
			projData.setProjectDbId(0);
			projData.setProjectName(projName);
			try {
				p = (IProjectData) dbActionProject(DBManager.SQL_ACTION_INSERT,
						projData);
			} catch (Exception e) {
				IpssLogger.logErr(e);
				PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
						"Error to Create DB Project",
						e.toString() + "\nPlease contact InterPSS support");
			}
		}
		return p;
	}

	/**
	 * Perform db action for the Project Data tables
	 * 
	 * @param action
	 *            insert, select, update and delete implemented.
	 * @param projData
	 *            projectData object for update and delete
	 * @return created (INSERT), selected or updated project data oject
	 */
	public Object dbActionProject(int action, Object projectData)
			throws InterpssException {
		IpssLogger.getLogger().info(
				"Current DB : " + DBManager.getCurrentDBName());
			ProjData projData = (ProjData) projectData;
		switch (action) {
			case DBManager.SQL_ACTION_INSERT: {
				try {
					try {
						IpssLogger.getLogger().info(
								"Insert project into DB, filename = "
										+ projData.getFilepath());
						DBManager.getSqlMap().insert("insertProject", projData);
					} catch (Exception e) {
						// a project with same name/filepath combo exist.
						// First we delete the project and then insert a new project
						IpssLogger.getLogger().info(
								"Project with the same filename found in DB, filename = "
										+ projData.getFilepath());
						ProjData p = (ProjData) dbActionProject(
								DBManager.SQL_ACTION_SELECT, projData);
						if (p != null)
							dbActionProject(DBManager.SQL_ACTION_DELETE, p);
						// Then we create a new project
						IpssLogger.getLogger().info(
								"Insert project into DB again, filename = "
										+ projData.getFilepath());
						DBManager.getSqlMap().insert("insertProject", projData);
					}
					// return the create project data object
					return DBManager.getSqlMap().queryForObject(
							"selectProjectName", projData);
				} catch (Exception e) {
					IpssLogger.logErr(e);
				}
			}
			case DBManager.SQL_ACTION_SELECT: {
				try {
					ProjData newProjData = null;
					// There are two ways to select: 1) by project db id and 2) by
					// project name and filepath
					if (projData.getProjectDbId() > 0) {
						IpssLogger.getLogger().info(
								"Get Project info, projDbId: "
										+ projData.getProjectDbId());
						newProjData = (ProjData) DBManager.getSqlMap()
								.queryForObject("selectProjectId", projData);
					} else {
						IpssLogger.getLogger().info(
								"Get project into DB, filename = "
										+ projData.getFilepath());
						newProjData = (ProjData) DBManager.getSqlMap()
								.queryForObject("selectProjectName", projData);
					}
					
					if (newProjData == null) {
						IpssLogger.getLogger().warning(
								"ProjectData object is not in DB, "
										+ XmlBeanUtil.toXmlString(projData));
						return null;
					}

					// select studyCase data list from DB
					List caseList = (List) DBManager.getSqlMap().queryForList(
							"selectStudyCaseForProject",
							newProjData.getProjectDbId());
					for (int i = 0; i < caseList.size(); i++) {
						DBStudyCase dbCase = (DBStudyCase) caseList.get(i);
						//IpssLogger.getLogger().fine(XmlUtil.toXmlString(dbCase));
						// rebuild the CaseData object from the DBStudyCase object
						// (xmlDataString)
						CaseData aCase = dbCase.getCaseData();
						// add CaseData object to the ProjectData object
						newProjData.getCaseList().add(aCase);
					}
					IpssLogger.getLogger().info(
								"Project found in DB, name, filepath, workspacepath: "
										+ newProjData.getProjectName() + ", " 
										+ newProjData.getFilepath() + ", " 
										+ newProjData.getWorkspacePath());
					return newProjData;
				} catch (Exception e) {
					IpssLogger.logErr(e);
				}
			}
			case DBManager.SQL_ACTION_UPDATE: {
				try {
					// Update the project table first
					DBManager.getSqlMap().update("updateProject", projData);

					// then update the IpssCase and StudyCase tables
					List caseList = projData.getCaseList();
					for (int i = 0; i < caseList.size(); i++) {
						CaseData caseData = (CaseData) caseList.get(i);
						if (caseData != null) {
							// map the CaseData object to a DBStudyCase object
							DBStudyCase dbCase = DBStudyCase
									.createDBStudyCase(caseData);
							dbCase.setProjDbId(projData.getProjectDbId());
							if (dbCase.getCaseId() > 0) {
								// update the IpssCase table
								dbActionIpssCase(DBManager.SQL_ACTION_UPDATE,
										dbCase.getProjDbId(), dbCase.getCaseName());
								// update the StudyCase table
								DBManager.getSqlMap().update("updateStudyCase",
										dbCase);
							} else {
								// insert a row to the IpssCase table
								IpssDBCase aCase = (IpssDBCase) dbActionIpssCase(
										DBManager.SQL_ACTION_INSERT, dbCase
												.getProjDbId(), dbCase
												.getCaseName());
								// set CaseDbId to the DBStudyCase object and the
								// CaseData object in the ProjectData CaseData list
								dbCase.setCaseId(aCase.getCaseDbId());
								caseData.setCaseDbId(aCase.getCaseDbId());
								// insert a row to the StudyCase table
								DBManager.getSqlMap().insert("insertStudyCase",
										dbCase);
							}
						}
					}
				} catch (Exception e) {
					IpssLogger.logErr(e);
				}
				// Select the updated ProjectData object and return
				return dbActionProject(DBManager.SQL_ACTION_SELECT, projData);
			}
			case DBManager.SQL_ACTION_DELETE: {
				// delete all case data associated with this project
				deleteDbProject(projData.getProjectDbId());
				return null;
			}
		}
		throw new InterpssRuntimeException("Cannot dbActionProject, see log file for details");
	}

	public static void deleteDbProject(int projDbId) {
		try {
			IpssLogger.getLogger().info("Delete project: " + projDbId);
			ISimuRecManager simuRecMgr = PluginSpringFactory.getSimuRecManager();
			simuRecMgr.deleteAllSimuRecForProject(projDbId,
				IProjectDataManager.CaseType_DStabSimuRec);
			DBManager.getSqlMap().delete("deleteAllStudyCaseForProject", projDbId);
			DBManager.getSqlMap().delete("deleteIpssCaseForProject", projDbId);
			DBManager.getSqlMap().delete("deleteProject", projDbId);
			IpssLogger.getLogger().info("Project deleted, id: " + projDbId);
		} catch (Exception e) {
			IpssLogger.logErr(e);
		}
	}

	/**
	 * Perform db action for SimuCase table
	 * 
	 * @param action
	 *            insert, select, update and delete implemented. Update update
	 *            the time stamp to the current dateTime
	 * @param projName
	 * @param caseName
	 * @return
	 */
	public Object dbActionIpssCase(int action, int projDbId, String caseName) {
		try {
			IpssDBCase aCase = new IpssDBCase();
			aCase.setProjDbId(projDbId);
			aCase.setCaseName(caseName);
			switch (action) {
			case DBManager.SQL_ACTION_INSERT: {
				DBManager.getSqlMap().insert("insertIpssCase", aCase);
				return dbActionIpssCase(DBManager.SQL_ACTION_SELECT, projDbId,
						caseName);
			}
			case DBManager.SQL_ACTION_SELECT: {
				Integer id = (Integer) DBManager.getSqlMap().queryForObject(
						"selectIpssCase", aCase);
				if (id != null)
					aCase.setCaseDbId(id.intValue());
				else
					aCase.setCaseDbId(0);
				return aCase;
			}
			case DBManager.SQL_ACTION_UPDATE: {
				Integer id = (Integer) DBManager.getSqlMap().queryForObject(
						"selectIpssCase", aCase);
				if (id != null) {
					DBManager.getSqlMap().update("updateIpssCaseTimpStamp", id);
				}
				return null;
			}
			case DBManager.SQL_ACTION_DELETE: {
				DBManager.getSqlMap().delete("deleteIpssCase", aCase);
				return null;
			}
			}
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssRuntimeException(
				"Cannot dbActionIpssCase, see log file for details");
	}
}