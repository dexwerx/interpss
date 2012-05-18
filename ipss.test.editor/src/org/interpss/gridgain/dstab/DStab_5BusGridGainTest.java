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
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.task.singleJob.DStabSingleJobTask;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.grid.msg.GridMessageRouter;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.gridgain.GridBaseTestSetup;
import org.junit.Test;

import com.interpss.SimuObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class DStab_5BusGridGainTest extends GridBaseTestSetup {
	@Test
	public void testDStab5BusCase() throws InterpssException, GridException {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET);
		loadCaseData("testData/dstab_test/DStab-5Bus.ipss", simuCtx);
		
		DStabilityNetwork net = simuCtx.getDStabilityNet();
		net.setId("NetId");
		addDynamicEventData(net);
		//System.out.println(net.net2String());
		
		DynamicSimuAlgorithm algo = createDStabAlgo(net);
		//algo.setTotalSimuTimeSec(0.2);
		
		Grid grid = GridEnvHelper.getDefaultGrid();
        	
       	GridMessageRouter msgRouter = new GridMessageRouter(msg);
       	grid.addMessageListener(msgRouter);
/*        	
   		Workspace.setCurrentType(Workspace.Type.Sample);
    		
   		IDStabSimuDatabaseOutputHandler dstabBbHandler = new DatabaseSimuOutputHandler();
   		try {
   			// assuming there is an existing case with id = 83
   			assertTrue(dstabBbHandler.init(58, "DStab-5Bus"+" SimuRecord"));
   		} catch (Exception e) {
   			IpssLogger.logErr(e);
   			SpringAppContext.getEditorDialogUtil().showErrMsgDialog("Error to Create DB SimuRecord", 
    					e.toString() + "\nPlease contact InterPSS support");
   		}

        //msgRouter.setIDStabSimuDatabaseOutputHandler(dstabBbHandler);
        	
/*
		setDbSimuCaseId(handler.getCaseId());
		// setup if there is output filtering
		handler.setOutputFilter(dStabCaseData.isOutputFilter());
		if (handler.isOutputFilter()) 
			handler.setOutputVarIdList(dStabCaseData.getOutVarList());
		simuCtx.getDynSimuAlgorithm().setSimuOutputHandler(handler);
 */        	
   		DStabSingleJobTask.RemoteNodeId = GridEnvHelper.getAnyRemoteNodeId();
    		
   		RemoteMessageTable result = new GridRunner(grid, "Grid DStab 5-Bus Sample system", algo).executeSingleJobTask(0);
       	assertTrue(result.getReturnStatus());
	}
}
