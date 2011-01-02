 /*
  * @(#)BaseBusForm.java   
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
*	BaseBusForm class for storing bus data.
*/

public class BaseBusForm extends BaseForm {
    public BaseBusForm() { this(""); }	
    public BaseBusForm(String id) { setId(id); }	

    private double  baseVoltage = 1000d;
    private String  baseVoltUnit = "Volt";  // "Volt", "KV"
    
    public String getBaseVoltUnit() { return this.baseVoltUnit; }
    public void setBaseVoltUnit(String n) { this.baseVoltUnit = n; }        
      
    public double getBaseVoltage() { return this.baseVoltage; }
    public void setBaseVoltage(double value) { this.baseVoltage = value; }
    
	/**
	*	Convert the object to a string representation
	*
	* @return the string representation
	*/
	public String toString() {
		return XmlBeanUtil.toXmlString(this);
	}    
}