 /*
  * @(#)XfrConnectData.java   
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


public class XfrConnectData  extends BaseDataBean {
	private static final long serialVersionUID = 1;

	public static String 
			Code_Wye = "Wye",
			Code_Delta = "Delta";			
	
    private String  code = "Wye";    // Wye | Delta
    private GroundData grounding = new GroundData();

    public XfrConnectData() { }	
    
    public GroundData getGrounding() { return this.grounding; }
    public void setGrounding(GroundData n) { this.grounding = n; }        

    public String getCode() { return this.code;  }
    public void setCode(String code) {  this.code = code;  }
}