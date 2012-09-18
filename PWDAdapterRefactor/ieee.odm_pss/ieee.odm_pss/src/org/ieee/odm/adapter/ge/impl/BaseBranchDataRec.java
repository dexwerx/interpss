 /*
  * @(#)BaseBranchDataRec.java   
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


public class BaseBranchDataRec extends BranchHeaderRec {
	public int st, ar, z, nst, ohms = 0;
	public String d_in, d_out, /* yymmdd */ projid;
	public double[] r_mvaAry = new double[8];
	public int[] oAry = new int[8];
	public double[] pAry = new double[8];

	public void setRMvaAry1_4(StringTokenizer st) {
		if (st.hasMoreElements())
			this.r_mvaAry[0] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.r_mvaAry[1] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.r_mvaAry[2] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.r_mvaAry[3] = new Double(st.nextToken()).doubleValue();
	}

	public void setDates(StringTokenizer st) {
		NetDataRec.setDates(st, this.d_in, this.d_out, this.projid);
	}

	public void setRMvaAry5_8(StringTokenizer st) {
		if (st.hasMoreElements())
			this.r_mvaAry[4] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.r_mvaAry[5] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.r_mvaAry[6] = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.r_mvaAry[7] = new Double(st.nextToken()).doubleValue();
	}
		
	public void setOwnerAry(StringTokenizer st) {
		NetDataRec.setOwnerAry(st, this.oAry, this.pAry);
	}
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "st, ar, z, nst, ohms: " + st + ar + ", " + z + ", " + nst + ", " + ohms + "\n";
		str += "r1_mva, r2_mva, r3_mva, r4_mva, r5_mva, r6_mva, r7_mva, r8_mva: " + r_mvaAry[0] + ", " + r_mvaAry[1] + ", " + 
		                r_mvaAry[2] + ", " + r_mvaAry[3] + ", " + r_mvaAry[4] + ", " + r_mvaAry[5] + ", " + r_mvaAry[6] + ", " + r_mvaAry[7] + "\n";
		str += "d_in, d_out, projid: " + d_in + ", " + d_out + ", " + projid + "\n";
		str += "o1, o2, o3, o4, o5, o6, o7, o8: " + oAry[0] + ", " + oAry[1] + ", " + oAry[2] + ", " + 
						oAry[3] + ", " + oAry[4] + ", " + oAry[5] + ", " + oAry[6] + ", " + oAry[7] + "\n";
		str += "p1, p2, p3, p4, p5, p6, p7, p8: " + pAry[0] + ", " + pAry[1] + ", " + pAry[2] + ", " + pAry[3] + ", " + 
						pAry[4] + ", " + pAry[5] + ", " + pAry[6] + ", " + pAry[7] + "\n";
		return str;
	}	
}
