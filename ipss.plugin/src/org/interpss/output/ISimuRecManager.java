/*
 * @(#)ISimuRecManager.java   
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

package org.interpss.output;

import java.util.List;

import com.interpss.common.exp.InterpssException;

public interface ISimuRecManager {
	public static final String
	// the following strings have to be in the SIMURECORDTYPE table. The string
	// length < 24                            "----+----+----+----+----"  
	REC_TYPE_DStabBusStates = "DStabBusStates",
			REC_TYPE_DStabMachineStates = "DStabMachineStates",
			REC_TYPE_DStabExcStates = "DStabExcStates",
			REC_TYPE_DStabGovStates = "DStabGovStates",
			REC_TYPE_DStabPssStates = "DStabPssStates",

			REC_TYPE_DStabScriptBusDeviceStates = "DStabScripDBusStates",
			REC_TYPE_DStabDynamicLoadStates = "DStabDynamicLoadStates",
			REC_TYPE_DStabSVCStates = "DStabSVCStates",
			REC_TYPE_DStabFACSTStates = "DStabFACSTStates",
			REC_TYPE_DStabRelayStates = "DStabRelayStates",
			REC_TYPE_DStabScripDBranchDeviceStates = "DStabScripDBranchStates";

	/**
	 * Get SimuRecord list for the case, record type and element id, and appType
	 * 
	 * @param caseId
	 * @param recType
	 * @param elemId
	 * @return
	 */
	List<BaseSimuDBRecord> getSimuRecList(int caseId, String recType,
			String elemId, int appType) throws InterpssException;

	/**
	 * Get SimuRecord list for the case, record type and element id, and appType
	 * 
	 * @param caseId
	 * @param recType
	 * @param elemId
	 * @return
	 */
	List<BaseSimuDBRecord> getSimuRecList(int caseId, String[] recTypeList,
			String[] elemIdList, int appType) throws InterpssException;

	/**
	 * Get elem id str list for the case, record type and appType
	 * 
	 * @param caseId
	 * @param recType
	 * @return
	 */
	List<String> getElemIdStrList(int caseId, String recType, int appType)
			throws InterpssException;

	/**
	 * Get SimuRecord for the rec id and appType
	 * 
	 * @param id
	 * @return
	 */
	Object getSimuRec(int id, int appType) throws InterpssException;

	/**
	 * Perform db action for the SimuRecord table and appType
	 * 
	 * @param action insert and update implemented
	 * @param aRec
	 */
	void dbActionSimuRec(int action, Object aRec, int appType)
			throws InterpssException;

	/**
	 * Delete all simu record of case id in the SimuRecord Table, int appType
	 * 
	 * @param caseId
	 */
	void deleteAllSimuRec(int caseId, int appType) throws InterpssException;

	/**
	 * Delete all simu record for projDbId in the SimuRecord Table
	 * 
	 * @param projId
	 */
	void deleteAllSimuRecForProject(int projId, int appType)
			throws InterpssException;

	/**
	 * Get the record type id for the record type and appType
	 * 
	 * @param recType
	 * @return the record type id
	 */
	int getRecTypeId(String recType, int appType) throws InterpssException;
	
	/**
	 * Get the database case id in case of multiple simulation cases
	 * 
	 * @param caseId
	 * @return
	 */
	int getDBCaseId(String caseId);

	/**
	 * Get the simu case id list store in the caseid2DbCaseId map hashtable
	 * 
	 * @return
	 */
	String[] getCaseIdList();

	/**
	 * add a caseId to dbCaseId relationship
	 * 
	 * @param caseId
	 * @param dbCaseId
	 */
	void addDBCaseId(String caseId, int dbCaseId);	

	/**
	 * clear the lookup table
	 * 
	 */
	void clearDbCaseIdLookup();	
}