 /*
  * @(#)AcscFaultData.java   
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

package org.interpss.editor.data.acsc;

import org.interpss.db.BaseDataBean;

import com.interpss.common.util.NetUtilFunc;

public class AcscFaultData extends BaseDataBean {
	private static final long serialVersionUID = 1;
	
	public static final String FaultCaty_Fault_3P = "3P";
	public static final String FaultCaty_Fault_LG = "LG";
	public static final String FaultCaty_Fault_LL = "LL";
	public static final String FaultCaty_Fault_LLG = "LLG";
	public static final String FaultCaty_Outage_3P = "Outage_3P";
	public static final String FaultCaty_Outage_1P = "Outage_1P";
	public static final String FaultCaty_Outage_2P = "Outage_2P";
	public static final String FaultCaty_Fault_All = "All";
	
	public static final String FaultType_BusFault = "Bus";
	public static final String FaultType_BranchFault = "Branch";
	public static final String FaultType_BranchOutage = "BranchOutage";
	
  	protected String type = FaultType_BusFault;  
  	public String getType() { return this.type; }
  	public void setType(String faultType) { this.type = faultType; }

  	protected String busBranchNameId = "";
  	public String getBusBranchNameId() { return this.busBranchNameId; }
  	public void setBusBranchNameId(String id) { this.busBranchNameId = id; }
  	public String getBusBranchId() { return NetUtilFunc.getBusIdFromDisplayNameId(this.busBranchNameId); }
  	public String getBusId() { return NetUtilFunc.getBusIdFromDisplayNameId(this.busBranchNameId); }
  	public String getBranchId() { return NetUtilFunc.getBranchIdFromDisplayNameId(this.busBranchNameId); }

	protected double distance = 0; // distance in %
    public double getDistance() { return this.distance; }
    public void setDistance(double d) { this.distance = d; }

  	protected String category = FaultCaty_Fault_3P;   
  	public String getCategory() { return this.category;  }
  	public void setCategory(String faultCategory) { this.category = faultCategory; }

	protected double LG_R = 0.0;
    public double getLG_R() { return this.LG_R; }
    public void setLG_R(double fault_LG_R) { this.LG_R = fault_LG_R; }

	protected double LG_X = 0.0;
    public double getLG_X() { return this.LG_X; }
    public void setLG_X(double fault_LG_X) { this.LG_X = fault_LG_X; }

	protected double LL_R = 0.0;
    public double getLL_R() { return this.LL_R; }
    public void setLL_R(double fault_LL_R) { this.LL_R = fault_LL_R; }

	protected double LL_X = 0.0;
    public double getLL_X() { return this.LL_X; }
    public void setLL_X(double fault_LL_X) { this.LL_X = fault_LL_X; }

    // the following are for transient stability simulation
    private boolean branchReclosure = false;
    private double  reclosureTime = 0.0;
  	public boolean isBranchReclosure() {
		return branchReclosure;
	}
	public void setBranchReclosure(boolean branchReclosure) {
		this.branchReclosure = branchReclosure;
	}
	public double getReclosureTime() {
		return reclosureTime;
	}
	public void setReclosureTime(double reclosureTime) {
		this.reclosureTime = reclosureTime;
	}    
}