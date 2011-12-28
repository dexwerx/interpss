/*
 * @(#)IProjectDataManager.java   
 *
 * Copyright (C) 2006 www.interpss.com
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

package org.interpss.ui;

import com.interpss.common.exp.InterpssException;

public interface IProjectDataManager {
	public static final int CaseType_DStabSimuRec = 1,
			CaseType_AclfStudyCase = 2, CaseType_AcscStudyCase = 3,
			CaseType_DStabStudyCase = 4;

	/**
	 * Save ProjData of the current project to DB
	 * 
	 * @param projData the ProjectData object
	 */
	void saveProjectDataToDB(Object projData) throws InterpssException;

	/**
	 * Load ProjData of the current project from DB
	 * 
	 * @param filepath full file path of the current project
	 * @param appSimuContext object containing ProjectData object
	 */
	void loadProjectDataFromDB(int projDbId, String filename, String projName,
			Object appSimuContext);

	/**
	 * Perform db action for Project Data 
	 * 
	 * @param action insert, select, update and delete implemented.
	 * @param projData projectData object for update and delete
	 * @return created (INSERT) or updated project data oject
	 */
	Object dbActionProject(int action, Object projectData)
			throws InterpssException;

	/**
	 * Perform db action for IpssCase table
	 * 
	 * @param action insert, select, update and delete implemented. Update update the time stamp to the current dateTime
	 * @param projDbId
	 * @param caseName
	 * @return
	 */
	Object dbActionIpssCase(int action, int projDbId, String caseName)
			throws InterpssException;
}