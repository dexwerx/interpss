/*
 * @(#)DebugOutFunc.java   
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
 * @Date 10/20/2012
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.display;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.net.Branch;


/**
 * Debug output functions
 * 
 * @author mzhou
 *
 */
public class DebugOutFunc {
	
	public static StringBuffer busConnectivityInfo(AclfBus bus) {
		StringBuffer buf = new StringBuffer();
	
		buf.append("\n\nBus Id: " + bus.getId() + "\n");
		buf.append("Bus status: " + bus.isActive() + "\n");
		
		buf.append("\nConnected Branch info: \n");
		for (Branch b : bus.getBranchList()) {
			AclfBranch bra = (AclfBranch)b;
			buf.append("  Branch Id : " + bra.getId() + "\n");
			buf.append("  Branch status: " + bra.isActive() + "\n");
			buf.append("  Branch type: " + bra.getBranchCode() + "\n\n");
		}

		// display debug info the connected branches
		return buf;
	}


}
