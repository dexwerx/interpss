 /*
  * @(#)DStabMachData.java   
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

package org.interpss.editor.data.dstab;

import org.interpss.db.BaseDataBean;
import org.interpss.editor.data.common.GroundData;
import org.interpss.editor.form.InitDataUtil;

public class DStabMachData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	public static final String MachType_InfiniteBus = "InfiniteBus";
	public static final String MachType_EConst = "EConstModel";
	public static final String MachType_Eq1 = "Eq1Model";
	public static final String MachType_Eq1Ed1 = "Eq1Ed1Model";
	public static final String MachType_RoundRotor = "RoundRotorModel";
	public static final String MachType_SalientPole = "SalientPoleModel";

    public DStabMachData() {
    	this.type = MachType_Eq1;
    	InitDataUtil.initDStabMachineData(this);
    }
    
	private String type = MachType_Eq1;
	public String getType() { return type; }
	public void setType(String t) { type = t; }

	private boolean hasExc = true;
	public boolean getHasExc() { return hasExc; }
	public void setHasExc(boolean t) { hasExc = t; }

	private DStabExcData excData = new DStabExcData();
	public DStabExcData getExcData() { return excData; }
	public void setExcData(DStabExcData data) { excData = data; }

	private boolean hasPss = true;
	public boolean getHasPss() { return hasPss & hasExc; }
	public void setHasPss(boolean t) { hasPss = t; }

	private DStabPssData pssData = new DStabPssData();
	public DStabPssData getPssData() { return pssData; }
	public void setPssData(DStabPssData data) { pssData = data; }

	private boolean hasGov = true;
	public boolean getHasGov() { return hasGov; }
	public void setHasGov(boolean t) { hasGov = t; }

	private DStabGovData govData = new DStabGovData();
	public DStabGovData getGovData() { return govData; }
	public void setGovData(DStabGovData data) { govData = data; }

    private GroundData ground = new GroundData();
    public GroundData getGround() { return this.ground; }
    public void setGround(GroundData n) { this.ground = n; }  	
    
    private String name = "";
    private double rating = 0.0;
    private double ratedVolt;
    private int poles;
    private double inertia;
    private double damping;
    private double x0;
    private double x2;
    private double xl;
    private double ra;
    private double xq;
    private double xd;
    private double xd1;
    private double xq1;
    private double td01;
    private double tq01;
    private double xd11;
    private double xq11;
    private double td011;
    private double tq011;
    private double s100;
    private double s120;
    private double sliner;

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
    
	/**
	 * @return Returns the damping.
	 */
	public double getDamping() {
		return damping;
	}
	
	public double getScMva1P() {
		return damping;
	}
	
	/**
	 * @param damping The damping to set.
	 */
	public void setDamping(double damping) {
		this.damping = damping;
	}
	public void setScMva1P(double damping) {
		this.damping = damping;
	}
	
	/**
	 * @return Returns the inertia.
	 */
	public double getInertia() {
		return inertia;
	}
	public double getScMva3P() {
		return inertia;
	}
	/**
	 * @param inertia The inertia to set.
	 */
	public void setInertia(double inertia) {
		this.inertia = inertia;
	}
	public void setScMva3P(double inertia) {
		this.inertia = inertia;
	}
	/**
	 * @return Returns the ra.
	 */
	public double getRa() {
		return ra;
	}
	/**
	 * @param ra The ra to set.
	 */
	public void setRa(double ra) {
		this.ra = ra;
	}
	/**
	 * @return Returns the ratedVolt.
	 */
	public double getRatedVolt() {
		return ratedVolt;
	}
	/**
	 * @param ratedVolt The ratedVolt to set.
	 */
	public void setRatedVolt(double ratedVolt) {
		this.ratedVolt = ratedVolt;
	}
	/**
	 * @return Returns the rating.
	 */
	public double getRating() {
		return rating;
	}
	/**
	 * @param rating The rating to set.
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}
	/**
	 * @return Returns the s100.
	 */
	public double getS100() {
		return s100;
	}
	/**
	 * @param s100 The s100 to set.
	 */
	public void setS100(double s100) {
		this.s100 = s100;
	}
	/**
	 * @return Returns the s120.
	 */
	public double getS120() {
		return s120;
	}
	/**
	 * @param s120 The s120 to set.
	 */
	public void setS120(double s120) {
		this.s120 = s120;
	}
	/**
	 * @return Returns the sliner.
	 */
	public double getSliner() {
		return sliner;
	}
	/**
	 * @param sliner The sliner to set.
	 */
	public void setSliner(double sliner) {
		this.sliner = sliner;
	}
	/**
	 * @return Returns the td01.
	 */
	public double getTd01() {
		return td01;
	}
	/**
	 * @param td01 The td01 to set.
	 */
	public void setTd01(double td01) {
		this.td01 = td01;
	}
	/**
	 * @return Returns the td011.
	 */
	public double getTd011() {
		return td011;
	}
	/**
	 * @param td011 The td011 to set.
	 */
	public void setTd011(double td011) {
		this.td011 = td011;
	}
	/**
	 * @return Returns the tq01.
	 */
	public double getTq01() {
		return tq01;
	}
	/**
	 * @param tq01 The tq01 to set.
	 */
	public void setTq01(double tq01) {
		this.tq01 = tq01;
	}
	/**
	 * @return Returns the tq011.
	 */
	public double getTq011() {
		return tq011;
	}
	/**
	 * @param tq011 The tq011 to set.
	 */
	public void setTq011(double tq011) {
		this.tq011 = tq011;
	}
	/**
	 * @return Returns the x0.
	 */
	public double getX0() {
		return x0;
	}
	/**
	 * @param x0 The x0 to set.
	 */
	public void setX0(double x0) {
		this.x0 = x0;
	}
	/**
	 * @return Returns the x2.
	 */
	public double getX2() {
		return x2;
	}
	public double getX_R_3P() {
		return x2;
	}
	/**
	 * @param x2 The x2 to set.
	 */
	public void setX2(double x2) {
		this.x2 = x2;
	}
	public void setX_R_3P(double x2) {
		this.x2 = x2;
	}
	/**
	 * @return Returns the xd.
	 */
	public double getXd() {
		return xd;
	}
	/**
	 * @param xd The xd to set.
	 */
	public void setXd(double xd) {
		this.xd = xd;
	}
	/**
	 * @return Returns the xd1.
	 */
	public double getXd1() {
		return xd1;
	}
	/**
	 * @param xd1 The xd1 to set.
	 */
	public void setXd1(double xd1) {
		this.xd1 = xd1;
	}
	/**
	 * @return Returns the xd11.
	 */
	public double getXd11() {
		return xd11;
	}
	/**
	 * @param xd11 The xd11 to set.
	 */
	public void setXd11(double xd11) {
		this.xd11 = xd11;
	}
	/**
	 * @return Returns the xl.
	 */
	public double getXl() {
		return xl;
	}
	public double getX_R_1P() {
		return xl;
	}
	/**
	 * @param xl The xl to set.
	 */
	public void setXl(double xl) {
		this.xl = xl;
	}
	public void setX_R_1P(double xl) {
		this.xl = xl;
	}
	/**
	 * @return Returns the xq.
	 */
	public double getXq() {
		return xq;
	}
	/**
	 * @param xq The xq to set.
	 */
	public void setXq(double xq) {
		this.xq = xq;
	}
	/**
	 * @return Returns the xq1.
	 */
	public double getXq1() {
		return xq1;
	}
	/**
	 * @param xq1 The xq1 to set.
	 */
	public void setXq1(double xq1) {
		this.xq1 = xq1;
	}
	/**
	 * @return Returns the xq11.
	 */
	public double getXq11() {
		return xq11;
	}
	/**
	 * @param xq11 The xq11 to set.
	 */
	public void setXq11(double xq11) {
		this.xq11 = xq11;
	}
	/**
	 * @return Returns the ratedSpeed.
	 */
	public int getPoles() {
		return poles;
	}
	/**
	 * @param ratedSpeed The ratedSpeed to set.
	 */
	public void setPoles(int poles) {
		this.poles = poles;
	}

}
