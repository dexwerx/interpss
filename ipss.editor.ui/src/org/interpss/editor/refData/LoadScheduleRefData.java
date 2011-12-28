 /*
  * @(#)LoadScheduleRefData.java   
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

package org.interpss.editor.refData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.interpss.output.db.DBManager;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.XmlBeanUtil;

public class LoadScheduleRefData {
	public static final String PERIOD_UNIT_HOUR = "Hour";
	public static final String PERIOD_UNIT_DAY = "Day";
	
	private List scheduleList;
	
	public LoadScheduleRefData() {
		scheduleList = new ArrayList();
	}

	/**
	 * Get the load schedule name list array
	 * 
	 * @param period_unit
	 * @return
	 */
	public Object[]getScheduleNameList(String period_unit) {
		List list = new ArrayList();
		for (int i = 0; i < scheduleList.size(); i++) {
			LoadSchedule s = (LoadSchedule)scheduleList.get(i);
			if (s.getPeriod_unit().equals(period_unit))
				list.add(s.getName());
		}
		IpssLogger.getLogger().info("Load Schedule NameList for unit: " + period_unit);
		IpssLogger.getLogger().info(list.toString());
		return list.toArray();
	}
	
	/**
	 * Get the item list for the schedule name
	 * 
	 * @param schedule_name
	 * @return
	 */
	public Object[] getItemList(String schedule_name) {
		for (int i = 0; i < scheduleList.size(); i++) {
			LoadSchedule s = (LoadSchedule)scheduleList.get(i);
			if (s.getName().equals(schedule_name))
				return s.getItemList().toArray();
		}
    	throw new InterpssRuntimeException("No Load Schedule found, name " + schedule_name);
	}
	
	public static LoadScheduleRefData getRefDataFromDB() {
    	LoadScheduleRefData refData = new LoadScheduleRefData();

    	IpssLogger.getLogger().info("Load LoadScheduleRefData into the systme");
    	try {
			SqlMapClient sqlMap = DBManager.getSqlMap();
			List scheduleList = sqlMap.queryForList("getLoadScheduleList", null);
			for (int i = 0; i < scheduleList.size(); i++ ) {
				LoadSchedule schedule = (LoadSchedule)scheduleList.get(i);
				List itemList = sqlMap.queryForList("getLoadScheduleItemList", new Integer(schedule.getId()));
				for (int j = 0; j < itemList.size(); j++ ) {
					LoadScheduleItem item = (LoadScheduleItem)itemList.get(j);
		        	schedule.getItemList().add(item.getPoint()-1, item);
				}
				
		        if (schedule.getPoints() != schedule.getItemList().size()) {
		        	throw new InterpssRuntimeException("Load Schedule reference data is wrong, points, itemList.size() " +
		        			schedule.getPoints() + ", " + schedule.getItemList().size());
		        }
	        	refData.getScheduleList().add(schedule);				
	        }
		} catch (SQLException e) {
			IpssLogger.logErr(e);
		}		
    	IpssLogger.getLogger().info("LoadScheduleRefData loaded, #:" + refData.getScheduleList().size());
		return refData;
	}
	
	/**
	 * @return Returns the scheduleList.
	 */
	public List getScheduleList() {
		return scheduleList;
	}

	/**
	 * @param scheduleList The scheduleList to set.
	 */
	public void setScheduleList(List scheduleList) {
		this.scheduleList = scheduleList;
	}
	
    public String toString() {
		return XmlBeanUtil.toXmlString(this);
	}		
}
