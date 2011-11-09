 /*
  * @(#)Aclf_IEEE14BusGridGainTest.java   
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

import org.gridgain.grid.Grid;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.task.singleJob.AclfSingleJobTask;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.gridgain.GridBaseTestSetup;
import org.interpss.numeric.datatype.ComplexFunc;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class Aclf_IEEE14BusGridGainTest extends GridBaseTestSetup {
	@Test
	public void CaseTest() throws Exception {
		Grid grid = GridEnvHelper.getDefaultGrid();
		String nodeId = GridEnvHelper.getAnyRemoteNodeId();
		
    	// set remote and master node id
    	AclfSingleJobTask.RemoteNodeId = nodeId;

    	SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
		//System.out.println(net.net2String());	
		// network id needs to be set. It is used for identification purpose
		net.setId("IEEE 14_Bus");

		RemoteMessageTable result = new GridRunner(grid, "Grid Aclf IEEE 14-Bus system", net).executeSingleJobTask(0);
    	assertTrue(result.getReturnStatus());
		
		String str = result.getSerializedAclfNet();
		AclfNetwork adjNet = (AclfNetwork) SerializeEMFObjectUtil.loadModel(str);
		adjNet.rebuildLookupTable();
		assert(adjNet.isLfConverged());
    	
  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		SwingBusAdapter swing = swingBus.toSwingBus();
		System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.11824)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-0.37383)<0.0001);
	}	

	@Test
	public void AlgoCaseTest() throws Exception {
		Grid grid = GridEnvHelper.getDefaultGrid();
		String nodeId = GridEnvHelper.getAnyRemoteNodeId();
		
    	// set remote and master node id
    	AclfSingleJobTask.RemoteNodeId = nodeId;

    	SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
		//System.out.println(net.net2String());	
		// network id needs to be set. It is used for identification purpse
		net.setId("IEEE 14_Bus");
		
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	//algo.setLfMethod(AclfMethod.PQ);

		RemoteMessageTable result = new GridRunner(grid, "Grid Aclf IEEE 14-Bus system", algo).executeSingleJobTask(0);
		//System.out.println(result);
    	assertTrue(result.getReturnStatus());
		
		String str = result.getSerializedAclfNet();
		AclfNetwork adjNet = (AclfNetwork) SerializeEMFObjectUtil.loadModel(str);
		adjNet.rebuildLookupTable();
		assert(adjNet.isLfConverged());
    	
  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		SwingBusAdapter swing = swingBus.toSwingBus();
		System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.11824)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-0.37383)<0.0001);
	}	
}
