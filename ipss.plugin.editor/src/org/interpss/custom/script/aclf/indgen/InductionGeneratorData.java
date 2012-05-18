 /*
  * @(#)InductionGeneratorData.java   
  *
  * Copyright (C) 2006-2007 www.interpss.org
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
  * @Date 11/27/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.custom.script.aclf.indgen;

/**
 * Plugin data object is a JavaBean. It has to have a default constructor
 * and getter/setter methods 
 */

public class InductionGeneratorData {
	public InductionGeneratorData() {}

	// We need to put the default values here, so that the controller could be 
	// properly initialized
	private double pe = 0.375, 
	               r  = 0.1, 
	               xs = 0.2, 
	               xm = 0.9;

	public double getPe() {
		return this.pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public double getR() {
		return this.r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public double getXs() {
		return this.xs;
	}

	public void setXs(double xs) {
		this.xs = xs;
	}

	public double getXm() {
		return this.xm;
	}

	public void setXm(double xm) {
		this.xm = xm;
	}
}

