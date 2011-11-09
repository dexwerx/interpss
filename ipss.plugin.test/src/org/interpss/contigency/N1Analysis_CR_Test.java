 /*
  * @(#)N11Analysis_IEEE14BusTest.java   
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
  * @Date 07/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.contigency;

import org.interpss.PluginTestSetup;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.display.ContingencyOutFunc;
import org.interpss.spring.PluginSpringCtx;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.aclf.ContingencyAnalysis;
import com.interpss.simu.multicase.aclf.ContingencyAnalysisType;

public class N1Analysis_CR_Test extends PluginTestSetup {
	@Test
	public void sampleTest() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("psse");
		SimuContext simuCtx = adapter.load("testData/psse/MXV-1120MW_FNC475_FEC196_FAC212_InterPSS_3d.raw");

		AclfNetwork net = simuCtx.getAclfNet();
		//System.out.println(net.net2String());

	  	ContingencyAnalysis mscase = SimuObjectFactory.createContingencyAnalysis(SimuCtxType.ACLF_NETWORK, net);
		
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		algo.setNonDivergent(true);
		//algo.setLfMethod(AclfMethod.PQ);
		algo.setTolerance(0.001);
		
  		long starttime = System.currentTimeMillis() ;
		mscase.analysis(algo, ContingencyAnalysisType.N1);
  		System.out.println("time for the : " + (System.currentTimeMillis() - starttime)*0.001);
		
		System.out.println(ContingencyOutFunc.securityMargin(mscase));		
	}
}

