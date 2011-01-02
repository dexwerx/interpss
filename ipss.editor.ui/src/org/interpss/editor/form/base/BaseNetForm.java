 /*
  * @(#)BaseNetForm.java   
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

package org.interpss.editor.form.base;

import com.interpss.common.util.XmlBeanUtil;

/**
*	BaseNetForm JavaBean for storing base network data.
*/

public class BaseNetForm {
    protected String id = "";
    protected String name = "";
    private String  description = null;

    private double  baseKVA = 100000.0d;
    private double  freqHZ = 50.0d;
    private boolean isAllowParallelBranch = false;
    private boolean isCheckBusDuplication = true;

    public BaseNetForm() { this(""); }	
    public BaseNetForm(String id) { setId(id); }	    

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description;}

    public double getBaseKVA() {return this.baseKVA;}
    public void setBaseKVA(double baseKVA) {this.baseKVA = baseKVA;}

    public double getFreqHZ() {return this.freqHZ;}
    public void setFreqHZ(double freqHZ) {this.freqHZ = freqHZ;}

    public boolean isAllowParallelBranch() { return this.isAllowParallelBranch; }
    public void setAllowParallelBranch(boolean b) { this.isAllowParallelBranch = b;}

    public void setCheckBusDuplication(boolean b) { this.isCheckBusDuplication = b; }
    public boolean isCheckBusDuplication() { return this.isCheckBusDuplication;}
    
    // Additional functions
	// ====================

    public Object clone() {
		String xml = XmlBeanUtil.toXmlString(this);
		return XmlBeanUtil.toObject(xml);
    }

    /**
	*	Convert the object to a string representation
	*
	* @return the string representation
	*/
	public String toString() {
		return XmlBeanUtil.toXmlString(this);
	}
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
}