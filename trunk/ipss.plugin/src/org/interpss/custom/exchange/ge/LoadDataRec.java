 /*
  * @(#)LoadDataRec.java   
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

package org.interpss.custom.exchange.ge;

import java.util.StringTokenizer;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.ext.ExtensionObjectFactory;
import com.interpss.ext.ge.aclf.GeAclfBus;
import com.interpss.ext.ge.aclf.GeAclfNetwork;
import com.interpss.ext.ge.aclf.GeLoad;

public class LoadDataRec extends BusHeaderRec {
	public int st, nst, owner;
	public double p, q, ip, iq, g, b;
			
	public LoadDataRec(String lineStr, GEDataRec.VersionNo version) {
		//System.out.println("load data->" + lineStr);
/*
	<bus> <"name"> <bkv> <"id"> <"long id"> : <st> <p> <q> <ip> <iq> <g> <b> /
	<ar> <z> <d_in> <d_out> <proj id> <nst> <owner>

       2 "P-2     " 380.00 "1 " "        "  :  1  868.096    0.000    0.000    0.000    0.000    0.000   1  201   400101   391231   0 0   1
 */		
		String str1 = lineStr.substring(0, lineStr.indexOf(':')),
        	   str2 = lineStr.substring(lineStr.indexOf(':')+1);

		setHeaderData(str1);
		
		StringTokenizer st = new StringTokenizer(str2);
		if (st.hasMoreElements())
			this.st = new Integer(st.nextToken().trim()).intValue();
		if (st.hasMoreElements())
			this.p = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.q = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.ip = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.iq = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.g = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.b = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements())
			this.ar = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.z = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.d_in = st.nextToken();
		if (st.hasMoreElements())
			this.d_out = st.nextToken();
		if (st.hasMoreElements())
			this.projId = st.nextToken();
		if (st.hasMoreElements())
			this.nst = new Integer(st.nextToken()).intValue();
		if (st.hasMoreElements())
			this.owner = new Integer(st.nextToken()).intValue();
	}
	
	public void setLoad(GeAclfNetwork net, IPSSMsgHub msg) throws Exception {
		GeAclfBus  bus = Ge2IpssUtilFunc.getBus(this.number, net, msg);
		
		GeLoad load = ExtensionObjectFactory.createGeLoad(id, longId);
		bus.getLoadList().add(load);
		load.setGeAreaNo(this.ar);
		load.setGeZoneNo(this.z);
		load.setOwner(this.owner);
		// <st> Load status 1 =	in service; 0 =	out of service
		load.setInSevice(this.st == 1);
		// <nst> Normal load status 1=in service; 0=out of service
		load.setNormalInService(this.nst == 1);
/*
		<p> Constant real power (MW)
		<q> Constant reactive power (MVAR)
		<ip> Constant current real power (MW)
		<iq> Constant current reactive power (MVAR)
		<g> Constant admittance real power (MW)
		<b> Constant admittance reactive power (MVAR)
 */		
		load.setIp(this.ip);
		load.setIq(this.iq);
		load.setP(this.p);
		load.setQ(this.q);
		load.setB(this.b);
		load.setG(this.g);
	}
		
	@Override
	public String toString() {
		String str = super.toString();
		str += "st, nst, owner, d_in, d_out, proj id: " + st + ", " + nst + ", " + owner + ", " + d_in + ", " + d_out + ", " + projId + "\n";
		str += "p, q, ip, iq, g, b: " + p + ", " + q + ", " + ip + ", " + iq + ", " + g + ", " + b + "\n";
		return str;
	}
	
}
