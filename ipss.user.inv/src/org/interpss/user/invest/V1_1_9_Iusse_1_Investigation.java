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
import org.interpss.numeric.util.PerformanceTimer;
import org.interpss.user.UserTestSetup;
import org.junit.Test;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.contingency.Contingency;
import com.interpss.pssl.simu.IpssDclf;
import com.interpss.pssl.simu.IpssDclf.DclfAlgorithmDSL;
import com.interpss.pssl.util.ContingencyAnalysisHelper;
import com.isone.ipss.ca.HourlyNetworkModel;
import com.isone.ipss.ca.util.NetDataMassager;

public class V1_1_9_Iusse_1_Investigation extends UserTestSetup {
	@Test
	public void issue_1_1_9_3() throws Exception {
		IpssCorePlugin.init(Level.WARNING);		
		//IpssCorePlugin.setSparseEqnSolver(SolverType.Native);

		// BaseCase CA investigation [V1.1.9 Issue-1]
		ODMLogger.getLogger().setLevel(Level.WARNING);

		HourlyNetworkModel model = new HourlyNetworkModel(false)
				.loadAclfNet(TestDataDir + "/_06012012/network_model.aux")
				.loadContingencyFile(TestDataDir + "/_06012012/contingency.aux")				
				.initCurrentCase(new NetDataMassager());

		AclfNetwork net = model.getAclfNet();
		
		PerformanceTimer timer = new PerformanceTimer();
		DclfAlgorithmDSL algoDsl = IpssDclf.createDclfAlgorithm(net, true).runDclfAnalysis();	
		timer.logStd("Dclf run ntp model: ");
		
		AclfBus refBus = net.getBus(net.getRefBusId());
		System.out.println("Ref Bus " + net.getRefBusId() + ", p=" + algoDsl.algo().getBusPower(refBus));
		
		/*
		 * Contingency Analysis
		 * ====================
		 */
		
		ContingencyAnalysisHelper contHelper = new ContingencyAnalysisHelper(algoDsl, true);
		contHelper.setViolationThreshold(1.0);  // branch violation threshold is set to 1.0 or 100%
		contHelper.setUseCAMonitoringStatus(true);
				
		//Contingency cont = model.getContingency("396");
		for (Contingency cont: model.getContingencyInfo().getContingencyList()) {
			
			if (cont.getId().equalsIgnoreCase("M13")) {
				//System.out.println("Pre-cont: number of monitoring branches = " + cont.getMonitoringBranches().size()); 
				contHelper.contAnalysis(cont);
				contHelper.outResult(cont);
			}
		}
		algoDsl.destroy();	
	}
}
