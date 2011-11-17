 /*
  * @(#)DistNetData.java   
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

package org.interpss.editor.data.dist;

import java.util.ArrayList;
import java.util.List;

import org.interpss.editor.data.common.ScPointData;

import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.rec.BaseDataBean;

/**
*	DistNetForm class for storing distribution network data.
*/

public class DistNetData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	public static final String ScStd_Generic = "Generic"; 
	public static final String ScStd_ANSI = "ANSI"; 
	public static final String ScStd_IEC = "IEC"; 

	// multi-points SC analysis only applies to ANSI and IEC
	private String  scStd = ScStd_Generic;          // Generic | ANSI | IEC
	private int     scPoints = 0;
	private List<ScPointData>    scPointList = new ArrayList<ScPointData>();	// vector of ScPointForm objects

	// if loadSchedulePoints = 0, no load schedule analysis
	private int     loadSchedulePoints = 0;
	private double  loadSchedulePeriodLength = 1.0;
	private String  loadSchedulePeriodUnit = "Hour";
	private double  totalLossKwHr = 0.0;
	private List<Boolean>    lfStatusList = new ArrayList<Boolean>();
	
    public DistNetData() { }	
   
    // Additional functions
	// ====================
	public int getActiveScPoints() { 
		int n = 0;
		for (int i = 0; i < this.scPointList.size(); i++) {
    		ScPointData row = (ScPointData)this.scPointList.get(i);
    		if (row.getEnable()) n++;
    	}	
    	//System.out.println("NetForm active sc points: " + n);
		return n; 
	}

	public void setLfStatus(boolean status, int index) {
		if (getLfStatusList().size() > index)
			getLfStatusList().set(index, new Boolean(status));
		else
			getLfStatusList().add(index, new Boolean(status));
	}
	
	public boolean getLfStatus(int index) {
		if (getLfStatusList().get(index) != null)
			return ((Boolean)getLfStatusList().get(index)).booleanValue();
		else
			throw new InterpssRuntimeException("DistNetData.getLfStatus(), index: " + index);
	}

	public void setScStd(String s) { this.scStd = s; }
    public String getScStd() { return this.scStd; }

    public List<ScPointData> getScPointList() { return this.scPointList; }
	public void setScPointList(List<ScPointData> list) { this.scPointList = list; }

	/**
	 * @return Returns the ifStatusList.
	 */
	public List<Boolean> getLfStatusList() {
		return lfStatusList;
	}
	public void setLfStatusList(List<Boolean> list) {
		lfStatusList = list;
	}
	
	/**
	 * @return Returns the loadSchedulePeriodLength.
	 */
	public double getLoadSchedulePeriodLength() {
		return loadSchedulePeriodLength;
	}

	/**
	 * @param loadSchedulePeriodLength The loadSchedulePeriodLength to set.
	 */
	public void setLoadSchedulePeriodLength(double loadSchedulePeriodLength) {
		this.loadSchedulePeriodLength = loadSchedulePeriodLength;
	}

	/**
	 * @return Returns the loadSchedulePeriodUnit.
	 */
	public String getLoadSchedulePeriodUnit() {
		return loadSchedulePeriodUnit;
	}

	/**
	 * @param loadSchedulePeriodUnit The loadSchedulePeriodUnit to set.
	 */
	public void setLoadSchedulePeriodUnit(String loadSchedulePeriodUnit) {
		this.loadSchedulePeriodUnit = loadSchedulePeriodUnit;
	}

	/**
	 * @return Returns the loadSchedulePoints.
	 */
	public int getLoadSchedulePoints() {
		return loadSchedulePoints;
	}

	/**
	 * @param loadSchedulePoints The loadSchedulePoints to set.
	 */
	public void setLoadSchedulePoints(int loadSchedulePoints) {
		this.loadSchedulePoints = loadSchedulePoints;
	}

	/**
	 * @return Returns the scPoints.
	 */
	public int getScPoints() {
		return scPoints;
	}

	/**
	 * @param scPoints The scPoints to set.
	 */
	public void setScPoints(int scPoints) {
		this.scPoints = scPoints;
	}

	/**
	 * @return Returns the totalLossKwHr.
	 */
	public double getTotalLossKwHr() {
		return totalLossKwHr;
	}

	/**
	 * @param totalLossKwHr The totalLossKwHr to set.
	 */
	public void setTotalLossKwHr(double totalLossKwHr) {
		this.totalLossKwHr = totalLossKwHr;
	}
}