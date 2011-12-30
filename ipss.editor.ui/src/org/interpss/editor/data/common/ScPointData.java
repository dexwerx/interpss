 /*
  * @(#)ScPointData.java   
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
*	ScPointForm class for defining SC Point in the Network editor.
*/

public class ScPointData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	private String  name = "";
	private boolean enable = true;
	private String  description = "";

    public ScPointData() {}
    
    public ScPointData(String name, boolean enable, String desc) {
    	this.name = name;
    	this.enable = enable;
    	this.description = desc;
    }	

    public void setName(String s) { this.name = s; }
    public String getName() { return this.name; }

    public void setEnable(boolean s) { this.enable = s; }
    public boolean getEnable() { return this.enable; }
    
    public void setDescription(String s) { this.description = s; }
    public String getDescription() { return this.description; }
}