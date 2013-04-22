 /*
  * @(#) Sample.java   
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
import org.interpss.numeric.sparse.base.ISparseEquation.SolverType;
import org.interpss.numeric.util.PerformanceTimer;
import org.interpss.user.UserTestSetup;
import org.junit.Test;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.pssl.simu.IpssDclf;
import com.interpss.pssl.simu.IpssDclf.DclfAlgorithmDSL;
import com.isone.ipss.ca.HourlyNetworkModel;
import com.isone.ipss.ca.util.NetDataMassager;

public class Sample extends UserTestSetup {
	@Test
	public void testF() throws Exception {
        IpssCorePlugin.setSparseEqnSolver(SolverType.Native);
		ODMLogger.getLogger().setLevel(Level.WARNING);

		HourlyNetworkModel model = new HourlyNetworkModel(false)
				.loadAclfNet(TestDataDir + "/_06012012/network_model.aux")
				.loadContingencyFile(TestDataDir + "/_06012012/contingency.aux")				
				.initCurrentCase(new NetDataMassager());

		AclfNetwork net = model.getAclfNet();
		
		PerformanceTimer timer = new PerformanceTimer();
		DclfAlgorithmDSL algoDsl = IpssDclf.createDclfAlgorithm(net, true)
				.runDclfAnalysis();	
		timer.logStd("Dclf run ntp model: " + algoDsl.algo().isDclfCalculated());
		
		AclfBus refBus = net.getBus(net.getRefBusId());
		System.out.println("Ref Bus " + net.getRefBusId() + ", p=" + algoDsl.algo().getBusPower(refBus));
		// Ref Bus power 0.06458968842164259
		//assertTrue(NumericUtil.equals(algoDsl.algo().getBusPower(refBus), -9.29309, 0.00001));
		
		algoDsl.destroy();			
	}
}

