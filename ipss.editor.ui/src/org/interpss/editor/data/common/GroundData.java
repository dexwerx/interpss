 /*
  * @(#)GroundData.java   
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

package org.interpss.editor.data.common;

import org.interpss.db.BaseDataBean;

/**
*	GroundForm JavaBean for storing grounding data.
*/

public class GroundData  extends BaseDataBean {
	private static final long serialVersionUID = 1;

	private String code = "Ungrounded"; 
    private double r = 0d;
    private double x = 0d;
    private String unit = "Ohm";

    public String getCode() { return this.code; }
    public void setCode(String n) { this.code = n; }        

    public double getR() { return this.r; }
    public void setR(double r) {this.r = r;}

    public double getX() { return this.x; }
    public void setX(double x) {this.x = x;}

    public String getUnit() { return this.unit; }
    public void setUnit(String n) { this.unit = n; }        
}