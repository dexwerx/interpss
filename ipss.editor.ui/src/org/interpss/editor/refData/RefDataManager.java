 /*
  * @(#)RefDataManager.java   
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

import org.interpss.ui.IRefDataManager;

import com.interpss.common.util.XmlBeanUtil;

public class RefDataManager implements IRefDataManager {
	private LoadScheduleRefData loadScheduleRefData;
	
	/**
	 * Load schedule ref data from the ref database
	 * 
	 * @param conMgr
	 */
	public void loadAllRefData() {
		setRefDataObject(LoadScheduleRefData.getRefDataFromDB(), IRefDataManager.REFDATA_LoadSchedule);
	}
	
	/**
	 * @return Returns the loadScheduleRefData.
	 */
	public Object getRefDataObject(int type) {
		if (type == IRefDataManager.REFDATA_LoadSchedule)
			return loadScheduleRefData;
		else 
			return null;
	}

	/**
	 * @param loadScheduleRefData The loadScheduleRefData to set.
	 */
	public void setRefDataObject(Object obj, int type) {
		if (type == IRefDataManager.REFDATA_LoadSchedule)
			this.loadScheduleRefData = (LoadScheduleRefData)obj;
	}
	
    public String toString() {
		return XmlBeanUtil.toXmlString(this);
	}		
}