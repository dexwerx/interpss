 /*
  * @(#)DistBusData.java   
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

/**
*	DistBusForm class for storing distribute bus data.
*/

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.complex.Complex;
import org.interpss.db.BaseDataBean;
import org.interpss.editor.data.common.ComplexData;
import org.interpss.editor.data.common.GroundData;
import org.interpss.editor.data.common.ZData;

import com.interpss.common.exp.InterpssRuntimeException;

public class DistBusData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	public static String
			BusCode_Utility 		= "Utility",
			BusCode_Generator 		= "Generator",
			BusCode_SynMotor 		= "SynMotor",
			BusCode_IndMotor 		= "IndMotor",
			BusCode_MixedLoad 		= "MixedLoad",
			BusCode_NonContribute 	= "Non-Contribute",
			BusCode_AllBus 			= "AllBuses";
	
    private String  busCode = BusCode_NonContribute; 
    private double  voltage = 1.0d;
    private String  voltageUnit = "PU";
    private double  vAngle = 0.0d;
    private String  vAngleUnit = "Deg";
    private double  mvaRating3P = 0.0d;
	private double  mvaRating1P = 0.0d;
    private String  mvaRatingUnit = "MVA";  // MVA | KVA | KAmps | Amps 
	private double  x_r3P = 0.0d;
	private double  x_r1P = 0.0d;
    private double  ratedVolt = 1.0d;
    private String  ratedVoltUnit = "PU";   // PU | Volt | KV
	private double  busRating = 0.0d;          // for Generator Rated KW; For Motor Rated HP; For MixedLoad Total KVA
    private String  busRatingUnit = "HP";      // for Generator: KW, MW; for Motor: "HP", "KW"; for MixedLoad: "KVA", "MVA"
	private double  pFactor = 0.8d;
    private String  pFactorUnit = "PU";     // "PU", "%"
	private double  eff = 0.0d;             // in %
	private double  motorPercent = 80.0d;   // in %
	private double  loading = 100.0d;       // in %, apply to Generator, SynMotor, IndMotor

    private double  z1X = 0d;
    private double  z1R = 0d;
    private double  z0X = 0d;
    private double  z0R = 0d;
    private double  z2X = 0d;
    private double  z2R = 0d;
    private String  zUnit = "PU";              // all Z share the same Unit, PU | Percent | Ohm | milliOhm
    private GroundData ground = new GroundData();

    // multi-points SC analysis
    private List<ZData>    z1List = new ArrayList<ZData>();   // of ZForm

    // load schedule analysis
    private boolean hasLoadSchedule = false;
    private List<ComplexData> loadScheduleList = new ArrayList<ComplexData>();
    private List<ComplexData> pointVoltageList = new ArrayList<ComplexData>();

    public String getBusCode() { return this.busCode; }
    public void setBusCode(String c) { this.busCode = c;  }

    public double getVoltage() { return this.voltage; }
    public void setVoltage(double value) {this.voltage = value;}

    public String getVoltageUnit() { return this.voltageUnit; }
    public void setVoltageUnit(String n) { this.voltageUnit = n; }        

    public double getVAngle() { return this.vAngle; }
    public void setVAngle(double value) {this.vAngle = value;}

    public String getVAngleUnit() { return this.vAngleUnit; }
    public void setVAngleUnit(String n) { this.vAngleUnit = n; }        

    public double getMvaRating3P() { return this.mvaRating3P; }
    public void setMvaRating3P(double value) {this.mvaRating3P = value;}

    public double getMvaRating1P() { return this.mvaRating1P; }
    public void setMvaRating1P(double value) {this.mvaRating1P = value;}

    public String getMvaRatingUnit() { return this.mvaRatingUnit; }
    public void setMvaRatingUnit(String n) { this.mvaRatingUnit = n; }        

    public double getX_r3P() { return this.x_r3P; }
    public void setX_r3P(double value) {this.x_r3P = value;}

    public double getX_r1P() { return this.x_r1P; }
    public void setX_r1P(double value) {this.x_r1P = value;}

    public double getBusRating() { return this.busRating; }
    public void setBusRating(double value) {this.busRating = value;}

    public String getBusRatingUnit() { return this.busRatingUnit; }
    public void setBusRatingUnit(String n) { this.busRatingUnit = n; }        

    public double getPFactor() { return this.pFactor; }
    public void setPFactor(double value) {this.pFactor = value;}

    public String getPFactorUnit() { return this.pFactorUnit; }
    public void setPFactorUnit(String n) { this.pFactorUnit = n; }      
      
    public double getRatedVolt() { return this.ratedVolt; }
    public void setRatedVolt(double d) { this.ratedVolt = d; }
    
    public String getRatedVoltUnit() { return this.ratedVoltUnit; }
    public void setRatedVoltUnit(String s) { this.ratedVoltUnit = s; }
    
	public double getEff() { return this.eff; }
	public void setEff(double d) { this.eff = d; }
    
	public double getMotorPercent() { return this.motorPercent; }    
	public void setMotorPercent(double d) { this.motorPercent = d; } 
    
	public double getLoading() { return this.loading; }    
	public void setLoading(double d) { this.loading = d; } 

	public List<ZData> getZ1List() { return this.z1List; }
	public void setZ1List(List<ZData> list) { this.z1List = list; }
	public ZData getZ1(int index) { 
		if (z1List.size() > index)
			return (ZData)z1List.get(index);
		else 
			throw new InterpssRuntimeException("Invalid DistBusData.getZ1() index: " + index);
	}
	public void setZ1(ZData z, int index) { 
		if (z1List.size() > index)
			z1List.set(index, z);
		else 
			z1List.add(index, z); 
	}
	
    public double getZ1X() { return this.z1X;}
    public void setZ1X(double z1X) { this.z1X = z1X;}

    public double getZ1R() {  return this.z1R; }
    public void setZ1R(double z1R) { this.z1R = z1R; }

    public double getZ0X() { return this.z0X;}
    public void setZ0X(double z0X) { this.z0X = z0X;}

    public double getZ0R() {  return this.z0R; }
    public void setZ0R(double z0R) { this.z0R = z0R; }

    public double getZ2X() { return this.z2X;}
    public void setZ2X(double z2X) { this.z2X = z2X; }

    public double getZ2R() { return this.z2R; }
    public void setZ2R(double z2R) { this.z2R = z2R;}

    public void setZUnit(String zUnit) { this.zUnit = zUnit; }
    public String getZUnit() { return this.zUnit; }

    public GroundData getGround() { return this.ground; }
    public void setGround(GroundData n) { this.ground = n; }        

    // Addtional functions:
	 // ===================
	public void initZ_SCList(int n) { 
		int m = n - this.z1List.size();
		if ( m  > 0 ) {
			for (int i = 0; i < m; i++) {
    			ZData row = new ZData(0.0, 0.0);
    			this.z1List.add(row);
			}
		}
		else if ( m < 0 ) {
			for (int i = n; i < this.z1List.size(); i++) {
    			this.z1List.remove(i);
			}
		}
	}
	
	public List<ComplexData> getLoadScheduleList() {
		return loadScheduleList;
	}
	public void setLoadScheduleList(List<ComplexData> list) {
		loadScheduleList = list;
	}
	
	public Complex getLoadSchedule(int index) {
		if (loadScheduleList.size() > index)
			return loadScheduleList.get(index).createComplex();
		else {
			return null;
		}	
	}

	public void setLoadSchedule(Complex loadSchedule, int index) {
		if (loadScheduleList.size() > index)
			loadScheduleList.set(index, new ComplexData(loadSchedule));
		else 
			loadScheduleList.add(index, new ComplexData(loadSchedule));
	}
	
	public List<ComplexData> getPointVoltageList() {
		return pointVoltageList;
	}

	public void setPointVoltageList(List<ComplexData> list) {
		pointVoltageList = list;
	}

	public Complex getPointVoltage(int index) {
		if (pointVoltageList.size() > index)
			return pointVoltageList.get(index).createComplex();
		else {
			return null;
		}	
	}

	public void setPointVoltage(Complex voltage, int index) {
		if (pointVoltageList.size() > index)
			pointVoltageList.set(index, new ComplexData(voltage));
		else 
			pointVoltageList.add(index, new ComplexData(voltage));
	}
	
	/**
	 * @return Returns the hasLoadSchedule.
	 */
	public boolean isHasLoadSchedule() {
		return hasLoadSchedule;
	}
	/**
	 * @param hasLoadSchedule The hasLoadSchedule to set.
	 */
	public void setHasLoadSchedule(boolean hasLoadSchedule) {
		this.hasLoadSchedule = hasLoadSchedule;
	}
}