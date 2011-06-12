/*
 * @(#)NetModelComparator.java   
 *
 * Copyright (C) 2006-2011 www.interpss.com
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
 * @Date 06/12/2011
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.tools;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;

public class NetModelComparator {
	private Network baseNet = null;
	public NetModelComparator(Network baseNet) {
		this.baseNet = baseNet;
	}
	
	public void compare(AclfNetwork aclfNet, boolean debug, IAclfNetComparator netComp, IAclfBusComparator busComp, IAclfBranchComparator braComp) {
		boolean ok = true;
		
		if (!netComp.compare((AclfNetwork)baseNet, aclfNet)) {
			if (debug) {
				
			}
			ok = false;
		}
			
		for (Bus b : baseNet.getBusList()) {
			if (!busComp.compare((AclfBus)b, aclfNet.getAclfBus(b.getId()))) {
				if (debug) {
					System.out.println("\n////////////////// Begin ///////////////////////////");
					
					System.out.println("-------------Base Bus Object------------");
					System.out.println(((AclfBus)b).toString(baseNet.getBaseKva()));

					if (aclfNet.getAclfBus(b.getId()) != null) {
						System.out.println("-------------Compare Bus Object------------");
						System.out.println(aclfNet.getAclfBus(b.getId()).toString(baseNet.getBaseKva()));
					}
					
					System.out.println("//////////////////  End  ///////////////////////////\n");
				}
				ok = false;
			}
		}

		for (Branch b : baseNet.getBranchList()) {
			if (!braComp.compare((AclfBranch)b, aclfNet.getAclfBranch(b.getId()))) {
				if (debug) {
					System.out.println("\n////////////////// Begin ///////////////////////////");
					
					System.out.println("-------------Base Branch Object------------");
					System.out.println(((AclfBranch)b).toString(baseNet.getBaseKva()));

					if (aclfNet.getAclfBranch(b.getId()) != null) {
						System.out.println("-------------Compare Branch Object------------");
						System.out.println(aclfNet.getAclfBranch(b.getId()).toString(baseNet.getBaseKva()));
					}
					else {
						System.out.println("-------------Compare Branch Object------------");
						System.out.println("Compare branch missing");
					}
					
					System.out.println("//////////////////  End  ///////////////////////////\n");
				}
				ok = false;
			}
		}
		
		if (ok) 
			System.out.println("Model Comparison has not Error");
		else
			System.out.println("********** Model Comparison has Error");
	}
}
