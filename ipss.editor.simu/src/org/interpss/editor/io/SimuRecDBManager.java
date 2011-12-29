/*
 * @(#)SimuRecDBManager.java   
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.interpss.db.DBManager;
import org.interpss.db.IpssDBCase;
import org.interpss.dstab.output.DStabSimuDBRecord;
import org.interpss.numeric.util.StringHelper;
import org.interpss.output.ISimuRecManager;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.IProjectDataManager;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.util.IpssLogger;

public class SimuRecDBManager implements ISimuRecManager {
	
	private Hashtable<String, Integer> dbCaseIdLookup = new Hashtable<String, Integer>();
	
	/**
	 * Get SimuRecord list for the case, record type and element id
	 * 
	 * @param caseId
	 * @param recType
	 * @param elemId
	 * @return
	 */
	public List getSimuRecList(int caseId, String recType, String elemId,
			int appType) throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				int typeid = getRecTypeId(recType, appType);
				DStabSimuDBRecord aRec = new DStabSimuDBRecord();
				aRec.setCaseId(caseId);
				aRec.setRecTypeId(typeid);
				aRec.setElemIdStr(elemId);
				List list = DBManager.getSqlMap().queryForList(
						"selectDStabSimuRecordList", aRec);
				return list;
			}
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssException(
				"Cannot getSimuRecList, see log file for details");
	}

	/**
	 * Get SimuRecord list for the case, record types and element ids
	 * 
	 * @param caseId
	 * @param recType
	 * @param elemId
	 * @return
	 */
	public List getSimuRecList(int caseId, String[] recTypeList,
			String[] elemIdList, int appType) throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				int cnt = 0;
				List<DStabSimuDBRecord> totalList = new ArrayList<DStabSimuDBRecord>();
				for (String recType : recTypeList) {
					int typeid = getRecTypeId(recType, appType);
					DStabSimuDBRecord aRec = new DStabSimuDBRecord();
					aRec.setCaseId(caseId);
					aRec.setRecTypeId(typeid);
					aRec.setElemIdStr(elemIdList[cnt++]);
					List<DStabSimuDBRecord> list = DBManager.getSqlMap()
							.queryForList("selectDStabSimuRecordList", aRec);
					totalList.addAll(list);
				}
				return totalList;
			}
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssException(
				"Cannot getSimuRecList, see log file for details");
	}

	/**
	 * Get elem id str list for the case, record type
	 * 
	 * @param caseId
	 * @param recType
	 * @return
	 */
	public List getElemIdStrList(int caseId, String recType, int appType)
			throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				int typeid = getRecTypeId(recType, appType);
				DStabSimuDBRecord aRec = new DStabSimuDBRecord();
				aRec.setCaseId(caseId);
				aRec.setRecTypeId(typeid);
				List list = DBManager.getSqlMap().queryForList(
						"selectDStabElemIdStrList", aRec);
				return list;
			}
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssException(
				"Cannot getSimuRecList, see log file for details");
	}

	/**
	 * Get SimuRecord for the rec id
	 * 
	 * @param id
	 * @return
	 */
	public Object getSimuRec(int id, int appType) throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				return DBManager.getSqlMap().queryForObject(
						"selectDStabSimuRecord", new Integer(id));
			}
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssException(
				"Cannot getSimuRec, see log file for details");
	}

	/**
	 * Perform db action for the SimuRecord table
	 * 
	 * @param action
	 *            insert and update implemented
	 * @param aRec
	 */
	public void dbActionSimuRec(int action, Object aRec, int appType)
			throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				switch (action) {
				case DBManager.SQL_ACTION_INSERT: {
					DBManager.getSqlMap().insert("insertDStabSimuRecord", aRec);
					return;
				}
				case DBManager.SQL_ACTION_UPDATE: {
					DBManager.getSqlMap().update("updateDStabSimuRecord", aRec);
					return;
				}
				}
			}
		} catch (SQLException e) {
			IpssLogger.logErr(e);
			e.printStackTrace();
		}
		throw new InterpssException(
				"Cannot dbActionSimuRec, see log file for details");
	}

	/**
	 * Delete all simu record of case id in the SimuRecord Table
	 * 
	 * @param caseId
	 */
	public void deleteAllSimuRec(int caseId, int appType)
			throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				DBManager.getSqlMap().delete("deleteDStabSimuRecord",
						new Integer(caseId));
				return;
			}
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssException(
				"Cannot deleteSimuRec, see log file for details");
	}

	/**
	 * Delete all simu record for projDbId in the SimuRecord Table
	 * 
	 * @param projId
	 */
	public void deleteAllSimuRecForProject(int projId, int appType)
			throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				List caseList = DBManager.getSqlMap().queryForList(
						"selectIpssCaseForProject", projId);
				for (int i = 0; i < caseList.size(); i++) {
					IpssDBCase aCase = (IpssDBCase) caseList.get(i);
					ISimuRecManager simuRecMgr = PluginSpringFactory
							.getSimuRecManager();
					simuRecMgr.deleteAllSimuRec(aCase.getCaseDbId(),
							IProjectDataManager.CaseType_DStabSimuRec);
				}
				return;
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssException(
				"Cannot deleteSimuRec, see log file for details");
	}

	/**
	 * Get the record type id for the record type
	 * 
	 * @param recType
	 * @return the record type id
	 */
	public int getRecTypeId(String recType, int appType)
			throws InterpssException {
		try {
			if (appType == IProjectDataManager.CaseType_DStabSimuRec) {
				Integer id = (Integer) DBManager.getSqlMap().queryForObject(
						"selectRecordType", recType);
				if (id != null) {
					return id.intValue();
				}
			}
			return -1;
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}
		throw new InterpssException(
				"Cannot getRecTypeId, see log file for details");
	}
	
	@Override
	public void addDBCaseId(String caseId, int dbCaseId) {
		IpssLogger.getLogger()
				.info(
						"DbCaseIdRelation: caseId=" + caseId + ", dbCaseId="
								+ dbCaseId);
		dbCaseIdLookup.put(caseId, new Integer(dbCaseId));
	}

	@Override
	public int getDBCaseId(String caseId) {
		Integer i = dbCaseIdLookup.get(caseId);
		if (i == null) {
			IpssLogger.getLogger().severe(
					"Porgramming Error, caseId = " + caseId + ", "
							+ dbCaseIdLookup.toString());
			throw new InterpssRuntimeException("Programming error");
		}
		return i.intValue();
	}

	@Override
	public String[] getCaseIdList() {
		return StringHelper.toStrArray(dbCaseIdLookup.keySet()
				.toArray());
	}

	@Override
	public void clearDbCaseIdLookup() {
		dbCaseIdLookup.clear();
	}
}