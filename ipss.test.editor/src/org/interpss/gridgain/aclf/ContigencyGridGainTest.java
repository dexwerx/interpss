 /*
  * @(#)DStab_5BusGridGainTest.java   
  *
  * Copyright (C) 2007 www.interpss.org
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
  * @Date 10/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.gridgain.aclf;

import static org.junit.Assert.assertTrue;

import org.interpss.grid.GridObjectFactory;
import org.interpss.grid.algo.GridContingencyAnalysis;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.gridgain.GridBaseTestSetup;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.StudyCase;
import com.interpss.simu.multicase.aclf.ContingencyAnalysisType;

public class ContigencyGridGainTest extends GridBaseTestSetup {
	@Test
	public void AlgoEMFCaseTest2() throws Exception {
		/*
		 * step-1 Build the base case
		 */
    	SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
		//System.out.println(net.net2String());	
		
		/*
		 * step-2 Define LF algorithem
		 */
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	//algo.setLfMethod(AclfMethod.PQ);

		GridContingencyAnalysis analysis = GridObjectFactory.createGridContingencyAnalysis(simuCtx.getNetType(), simuCtx.getAclfNet(),
				GridEnvHelper.getDefaultGrid());
		analysis.perform(algo, ContingencyAnalysisType.N1);
    	for (StudyCase scase : analysis.getStudyCaseList()) {
    		if (scase.getNetModelString() != null) {
    			AclfNetwork aclfNet = (AclfNetwork)SerializeEMFObjectUtil.loadModel(scase.getNetModelString());
    			aclfNet.rebuildLookupTable();
    			assertTrue(aclfNet.isLfConverged());
    		}
    	}
	}
}
