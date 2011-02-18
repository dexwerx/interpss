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

package org.interpss.gridgain.dstab;

import static org.junit.Assert.assertTrue;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.interpss.gridgain.GridBaseTestSetup;
import org.interpss.gridgain.GridRunner;
import org.interpss.gridgain.msg.RemoteMessageTable;
import org.interpss.gridgain.task.singleJob.DStabSingleJobTask;
import org.interpss.gridgain.util.GridUtil;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class DStab_5BusNoRegulatorGridGainTest extends GridBaseTestSetup {
	@Test
	public void testDStab5BusCase() throws InterpssException, GridException {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
		loadCaseData("testData/dstab_test/DStab-5BusNoReg.ipss", simuCtx);
		
		DStabilityNetwork net = simuCtx.getDStabilityNet();
		//System.out.println(net.net2String());
		net.setId("NetId");
		addDynamicEventData(net);
		//System.out.println(net.net2String());
		
		DynamicSimuAlgorithm algo = createDStabAlgo(net);
		//algo.setTotalSimuTimeSec(0.2);		
		
		Grid grid = GridUtil.getDefaultGrid();
		
   		DStabSingleJobTask.RemoteNodeId = GridUtil.getAnyRemoteNodeId();

   		RemoteMessageTable result = new GridRunner(grid, "Grid Aclf 5-Bus Sample system", algo).executeTask(0);
       	assertTrue(result.getReturnStatus());
	}
}
