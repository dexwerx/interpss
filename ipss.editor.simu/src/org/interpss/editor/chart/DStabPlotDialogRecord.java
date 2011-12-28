/*
 * @(#)DStabPlotDialogRecord.java   
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
 * @Date 05/01/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.editor.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.interpss.dstab.output.DStabSimuDBRecord;
import org.interpss.numeric.util.Number2String;
import org.interpss.output.BaseSimuDBRecord;
import org.interpss.output.ISimuRecManager;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.IProjectDataManager;

import com.interpss.common.datatype.Constants;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;

/**
 * Utility class for translate plot dialog selection strings to plot records
 * 
 * @author mzhou
 *
 */
public class DStabPlotDialogRecord {
	public static final double TimeErrorTolerance = 0.0000001;

	public String elemId;
	public String machId;
	public String busId;
	public String stateName;
	public String recType; // of type ISimuRecManager.REC_TYPE_DStabExcStates

	/**
	 * Parse plot dialog selection string, format: stateName(elemId) to a DStabPlotDialogRecord
	 * 
	 * @param str stateName(elemId)
	 * @return a DStabPlotDialogRecord 
	 */
	public static DStabPlotDialogRecord parseStateSelection(String str) {
		DStabPlotDialogRecord rec = new DStabPlotDialogRecord();
		rec.elemId = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
		rec.stateName = str.substring(0, str.indexOf("("));
		String type;
		if (rec.elemId.startsWith(Constants.Token_MachId)) {
			if (rec.elemId.endsWith(DStabSimuDBRecord.EXCITER_ID_EXT)) {
				type = ISimuRecManager.REC_TYPE_DStabExcStates;
				rec.machId = rec.elemId.replaceAll(
						DStabSimuDBRecord.EXCITER_ID_EXT, "");
			} else if (rec.elemId.endsWith(DStabSimuDBRecord.GOVERNER_ID_EXT)) {
				type = ISimuRecManager.REC_TYPE_DStabGovStates;
				rec.machId = rec.elemId.replaceAll(
						DStabSimuDBRecord.GOVERNER_ID_EXT, "");
			} else if (rec.elemId.endsWith(DStabSimuDBRecord.STABILIZER_ID_EXT)) {
				type = ISimuRecManager.REC_TYPE_DStabPssStates;
				rec.machId = rec.elemId.replaceAll(
						DStabSimuDBRecord.STABILIZER_ID_EXT, "");
			} else {
				type = ISimuRecManager.REC_TYPE_DStabMachineStates;
				rec.machId = rec.elemId;
			}
		} else if (rec.elemId.startsWith(Constants.Token_DBusDeviceId)) {
			type = ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates;
		} else {
			type = ISimuRecManager.REC_TYPE_DStabBusStates;
			rec.busId = rec.elemId.replaceAll(Constants.Token_BusId, "");
		}
		rec.recType = type;
		return rec;
	}

	/**
	 * Translate the selection str set to a state name list: Time, Machine Angle ....
	 * 
	 * @param strList
	 * @return
	 */
	public static List<String> getStateNameList(Object[] strList) {
		List<String> nameList = new ArrayList<String>();
		nameList.add("Time");
		for (Object strObj : strList) {
			nameList.add(DStabPlotDialogRecord
					.parseStateSelection((String) strObj).stateName);
		}
		return nameList;
	}

	/**
	 * According to the selection str set, retrieve data from the DB and create the value list
	 *    [<Time, 0.00>, <Machine Angle, 50.0> ....]
	 *    [<Time, 0.01>, <Machine Angle, 50.0> ....]
	 * Please note the record in database maybe out of order regarding time    
	 * 
	 * @param caseId
	 * @param strList
	 * @return
	 */
	public static List<Hashtable<String, String>> createValueList(int caseId,
			Object[] strList) {
		// find <elemId, recType> set. elemId may be duplicated in the selecting strList
		Map<String, String> map = new HashMap<String, String>();
		for (Object strObj : strList) {
			DStabPlotDialogRecord rec = DStabPlotDialogRecord
					.parseStateSelection((String) strObj);
			String elemId = rec.elemId;
			if (rec.recType.equals(ISimuRecManager.REC_TYPE_DStabBusStates))
				elemId = rec.busId;
			if (!map.containsKey(elemId))
				map.put(elemId, rec.recType);
		}

		// create elemId[], recType[]
		Object[] objList = map.keySet().toArray();
		String[] elemIdList = new String[objList.length];
		String[] recTypeList = new String[objList.length];
		int cnt = 0;
		for (Object obj : objList) {
			elemIdList[cnt] = (String) obj;
			recTypeList[cnt++] = map.get((String) obj);
		}
		IpssLogger.getLogger().info(
				"Plot Dialog selected ElemIdList, RecTyprList: "
						+ map.toString());

		// retrieve rec from DB
		ISimuRecManager simuRecManager = PluginSpringFactory.getSimuRecManager();
		List<BaseSimuDBRecord> elemRecList = null;
		try {
			elemRecList = simuRecManager.getSimuRecList(caseId, recTypeList,
					elemIdList, IProjectDataManager.CaseType_DStabSimuRec);
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
			PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
					"Error to GetSimuRecList from DB",
					ex.toString() + "\n Please contact InterPSS support");
		}

		// find time array from the elemRecList, there may be multiple sets
		List<Double> timeList = new ArrayList<Double>();
		double time = 0.0;
		for (BaseSimuDBRecord r : elemRecList) {
			DStabSimuDBRecord rec = (DStabSimuDBRecord) r;
			if (rec.getSimuTime() > time) {
				time = rec.getSimuTime();
				timeList.add(new Double(time));
			}
		}

		// create the valueList
		List<Hashtable<String, String>> valueList = new ArrayList<Hashtable<String, String>>();
		for (Double tPoint : timeList) {
			Hashtable<String, String> row = new Hashtable<String, String>();
			double t = tPoint.doubleValue();
			row.put("Time", Number2String.toStr(t, "00.000"));
			for (Object strObj : strList) {
				String stateName = DStabPlotDialogRecord
						.parseStateSelection((String) strObj).stateName;
				for (BaseSimuDBRecord r : elemRecList) {
					DStabSimuDBRecord rec = (DStabSimuDBRecord) r;
					if (Math.abs(rec.getSimuTime() - t) < TimeErrorTolerance) {
						Hashtable table = StringUtil.parseStr2Hashtable(rec
								.getSimuRec());
						Object obj = table.get(stateName);
						if (obj != null) {
							row.put(stateName, Number2String.toStr(new Double(
									(String) obj).doubleValue(), "0.0000"));
						}
					}
				}
			}
			valueList.add(row);
		}
		return valueList;
	}
}
