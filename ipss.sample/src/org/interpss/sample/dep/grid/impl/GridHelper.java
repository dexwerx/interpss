package org.interpss.sample.dep.grid.impl;

import java.io.Serializable;
import java.util.UUID;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridMessageListener;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.util.GridEnvHelper;

public class GridHelper {
	public static String GridGainHome = "c:/Program Files (x86)/gridgain-2.1.1";

	public static Grid initGridEnv() {
		// start interpss grid env
		GridEnvHelper.startDefaultGrid(GridGainHome);
		if (!GridEnvHelper.isGridEnabled()) {
			System.out.println("Cannot start grid env");
			return null;
		}
		
		if (GridEnvHelper.getDefaultGrid().getAllNodes().size() <= 1) {
			System.out.println("Please start a least one Gridgain agent for the test");
			return null;
		}

		// set interpss grid env 
		Grid grid = GridEnvHelper.getDefaultGrid();
		GridRunner.MasterNodeId = grid.getLocalNode().getId().toString();
		GridRunner.RemoteNodeDebug = false;

		// make sure Grid env is setup properly
		String[] list = GridEnvHelper.gridNodeNameList(grid, false);
		if ( list.length <= 0) {
			System.out.println("InterPSS grid env is not started properly");
			return null;
		}
		
		// set remote message listener, so that message from remote note are printed out
    	grid.addMessageListener(new GridMessageListener() {
    		public void onMessage(UUID arg0, Serializable arg1) {
    			System.out.println(arg1);
    		}        		
    	});

    	System.out.println("InterPSS Grid Env started successfully");
    	return grid;
	}
}
