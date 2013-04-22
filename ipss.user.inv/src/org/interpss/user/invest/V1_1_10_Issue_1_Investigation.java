/*
 * @(#) ISONE_CompreResult.java   
 *
 * Copyright (C) 2008 www.interpss.org
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
 * @Date 02/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.user.invest;

import java.util.logging.Level;

import org.ieee.odm.common.ODMLogger;
import org.interpss.IpssCorePlugin;
import org.interpss.user.UserTestSetup;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.isone.ipss.ca.HourlyNetworkModel;


/*
 * codes to investigate [V1.1.10- Issue 1]
 * test version: v1.1.10 
 * Issue description: 	interPSS doesn't update the phase shifter impedance 
 * 						according to the hourly dispatched angle value
 */

public class V1_1_10_Issue_1_Investigation extends UserTestSetup {
	@Test
	public void issue_1_1_10_1() throws Exception {		
		IpssCorePlugin.init(Level.WARNING);		
		ODMLogger.getLogger().setLevel(Level.WARNING);

		HourlyNetworkModel model = new HourlyNetworkModel(false)
		.loadAclfNet(TestDataDir + "/_06012012/network_model.aux") 
		.loadEDispathFile(TestDataDir + "/_06012012/BusGenLoad.csv", "testData/_06012012/PARAngles.csv")
		.loadTransmissionOutageFile(TestDataDir + "/_06012012/Trans_Outage.csv")
		.loadGenOutageFile(TestDataDir + "/_06012012/Gen_Outage.csv")
		.loadBranchOverrideFile(TestDataDir + "/_06012012/Branch_Override.csv")
		.loadBreakerOverrideFile(TestDataDir + "/_06012012/Breaker_Override.csv")
		.loadContingencyFile(TestDataDir + "/_06012012/contingency.aux")
		.loadContingencyOverrideFile(TestDataDir + "/_06012012/Contingency_Override.csv")
		.loadBranchLossAreaAllocationFactor(TestDataDir + "/_06012012/branchLossAreaAllocationFactor.csv")
		.initCurrentCase();

		int hr = 0; 
		System.out.println("setup hourly case at H = 0");
		AclfNetwork aclfNet  = model.setHour(hr).getAclfNet(); 
		
		//-------output corrected phase shifter impedance ----------------// 
		String branchID = "Bus12084->Bus12089(1)"; 	
		System.out.println(branchID);
		System.out.println(aclfNet.getBranch(branchID).getFromPSXfrAngle()*180/Math.PI + " [deg]");
		System.out.println(1/aclfNet.getBranch(branchID).b1tf());
		
		branchID = "Bus10908->Bus10909(1)"; 	
		System.out.println(branchID);
		System.out.println(aclfNet.getBranch(branchID).getFromPSXfrAngle()*180/Math.PI + " [deg]");
		System.out.println(1/aclfNet.getBranch(branchID).b1tf());
		
		
	}
}
