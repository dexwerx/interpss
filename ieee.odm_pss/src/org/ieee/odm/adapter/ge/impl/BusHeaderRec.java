 /*
  * @(#)BusHeaderRec.java   
  *
  * Copyright (C) 2006-2008 www.interpss.org
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
  * @Date 06/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.adapter.ge.impl;

import java.util.StringTokenizer;

public class BusHeaderRec {
	public long number; 
	public int ar, z;
	public String name, id = "", longId = ""; 
	public double bkv;
	public String d_in, d_out, projId;
	
	public void setHeaderData(String dataStr) {
		StringTokenizer st = new StringTokenizer(dataStr, "\"");
		
		this.number = new Integer(st.nextToken().trim()).intValue();
		this.name = st.nextToken();
		this.bkv = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.id = st.nextToken();
		if (st.hasMoreElements())
			this.longId = st.nextToken();
	}
	
	@Override
	public String toString() {
		String str = "";
		str += "number, name, ar, z, id, longId,  bkv: " + number + ", " + name + ", " 
		       + ar + ", " + z + ", " + id + ", " + longId + ", " + bkv + "\n";
		str += "d_in, d_out, projId: " + d_in + ", " + d_out + ", " + projId + "\n";
		return str;
	}
	
}
