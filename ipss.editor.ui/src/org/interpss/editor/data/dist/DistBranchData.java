 /*
  * @(#)DistBranchData.java   
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

import org.interpss.db.BaseDataBean;
import org.interpss.editor.data.common.XfrConnectData;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;

/**
*	DistBranchForm class for storing distribution branch data.
*/

public class DistBranchData  extends BaseDataBean {
	private static final long serialVersionUID = 1;

	private String  branchCode = IGBranchForm.DistBranchCode_Feeder;     // Transformer | 3WTransformer | Feeder | Breaker
    private String  zUnit = "PU";
    private double  zX = 0d;                
    private double  zR = 0d;
    private double  halfShuntB = 0d;     
    private String  halfShuntBUnit = "PU";
    private double  length = 0.0d;
    private String  lengthUnit = "Ft";   // "Ft" or "M"

    private double  xfrTapFromSideTap = 1.0d;
    private double  xfrTapToSideTap = 1.0d;
    private String  xfrTapUnit = "PU";

	private double  xfrRating = 0.0d;       // for Transformer Rated KVA
    private String  xfrRatingUnit = "MVA";  // "MVA", "KVA"
	private double  fromRatedVolt = 1000.0d;
	private double  toRatedVolt = 1000.0d;
    private String  ratedVoltUnit = "Volt";  // "Volt", "KV"

    private double  z0R = 0d;            // if length > 0.0, zX, zR 0hms/1000
    private double  z0X = 0d;
    private String  z0Unit = "PU";
    private double  halfShuntB0 = 0d;    // if length > 0.0, B microMhos/1000 
    private XfrConnectData  fromXfrConnectData = new XfrConnectData();
    private XfrConnectData  toXfrConnectData = new XfrConnectData();        

    // load schedule analysis
    private boolean hasServiceSchedule = false;
    private List<Boolean> serviceScheduleList = new ArrayList<Boolean>();

    public String getBranchCode() { return this.branchCode;     }
    public void setBranchCode(String value) { this.branchCode = value;     }

    public double getHalfShuntB() { return this.halfShuntB;    }
    public void setHalfShuntB(double value) { this.halfShuntB = value;     }

    public String getHalfShuntBUnit() { return this.halfShuntBUnit;    }
    public void setHalfShuntBUnit(String value) { this.halfShuntBUnit = value;     }

    public double getXfrTapFromSideTap() { return  this.xfrTapFromSideTap;     }
    public double getXfrTapToSideTap() { return this.xfrTapToSideTap;     }

    public String getXfrTapUnit() { return this.xfrTapUnit;     }
    public void setXfrTapFromSideTap(double value) { this.xfrTapFromSideTap = value;     }

    public void setXfrTapToSideTap(double value) { this.xfrTapToSideTap = value;     }
    public void setXfrTapUnit(String value) { this.xfrTapUnit = value;     }

    public double getZR() { return this.zR;     }
    public void setZR(double value) { this.zR = value;    }

    public double getZX() { return this.zX;     }
    public void setZX(double value) { this.zX = value;     }

    public String getZUnit() { return this.zUnit;     }
    public void setZUnit(String value) { this.zUnit = value;     }

    public double getLength() { return this.length; }
    public void setLength(double value) {this.length = value;}
	
    public String getLengthUnit() { return this.lengthUnit; }
    public void setLengthUnit(String n) { this.lengthUnit = n; }        

    public double getXfrRating() { return this.xfrRating; }
    public void setXfrRating(double value) {this.xfrRating = value;}
	
    public String getXfrRatingUnit() { return this.xfrRatingUnit; }
    public void setXfrRatingUnit(String n) { this.xfrRatingUnit = n; }        

	public double getFromRatedVolt() { return this.fromRatedVolt; }
	public void setFromRatedVolt(double d) { this.fromRatedVolt = d; }
    
	public double getToRatedVolt() { return this.toRatedVolt; }
	public void setToRatedVolt(double d) { this.toRatedVolt = d; }
    
    public String getRatedVoltUnit() { return this.ratedVoltUnit; }
    public void setRatedVoltUnit(String s) { this.ratedVoltUnit = s; }

    public double getZ0R() { return this.z0R;}
    public void setZ0R(double r) { this.z0R = r; }

    public double getZ0X() { return this.z0X;}
    public void setZ0X(double x) { this.z0X = x; }

    public String getZ0Unit() { return this.z0Unit; }
    public void setZ0Unit(String unit) { this.z0Unit = unit; }

    public double getHalfShuntB0() { return this.halfShuntB0; }
    public void setHalfShuntB0(double b) { this.halfShuntB0 = b; }

    public XfrConnectData getFromXfrConnectData() { return this.fromXfrConnectData; }
    public void setFromXfrConnectData(XfrConnectData n) { this.fromXfrConnectData = n; }        

    public XfrConnectData getToXfrConnectData() { return this.toXfrConnectData; }
    public void setToXfrConnectData(XfrConnectData n) { this.toXfrConnectData = n; }
	/**
	 * @return Returns the serviceScheduleList.
	 */
	public List<Boolean> getServiceScheduleList() {
		return serviceScheduleList;
	}
	public void setServiceScheduleList(List<Boolean> list) {
		serviceScheduleList = list;
	}
	public boolean getServiceSchedule(int index) { 
		if (serviceScheduleList.size() > index)
			return ((Boolean)serviceScheduleList.get(index)).booleanValue();
		else 
			return true;
	}
	public void setServiceSchedule(boolean b, int index) { 
		if (serviceScheduleList.size() > index)
			serviceScheduleList.set(index, new Boolean(b));
		else 
			serviceScheduleList.add(index, new Boolean(b)); 
	}
	
	/**
	 * @return Returns the hasServiceSchedule.
	 */
	public boolean isHasServiceSchedule() {
		return hasServiceSchedule;
	}
	/**
	 * @param hasServiceSchedule The hasServiceSchedule to set.
	 */
	public void setHasServiceSchedule(boolean hasServiceSchedule) {
		this.hasServiceSchedule = hasServiceSchedule;
	}       
	
    public boolean isR_LT_X() {
    	return this.zR > this.zX;
    }	
}