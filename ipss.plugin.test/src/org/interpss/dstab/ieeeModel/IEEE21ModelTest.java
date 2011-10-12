 /*
  * @(#)TestEq1MachineCase.java   
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
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.dstab.ieeeModel;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.junit.Test;

import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.cache.StateVariableRecorder;
import com.interpss.dstab.cache.YMatrixChangeRecorder;
import com.interpss.dstab.common.DStabOutSymbol;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class IEEE21ModelTest extends DStabTestSetupBase {
	private double[] 
     	      timePoints    = {0.0,    1.0,    2.0,    3.0,    4.0,    5.0,    6.0,    7.0,    8.0,    9.0,    10.0},
     	      machAngPoints = {49.460, 49.460, 47.475, 48.802, 49.553, 49.943, 50.114, 50.160, 50.136, 50.078, 50.006},
     	      machPePoints  = {0.426,  0.426,  0.343,  0.372,  0.392,  0.404,  0.412,  0.417,  0.421,  0.423,  0.424};
	private Complex yFault = new Complex(1.2595,-100000012.97521),
	                yClear = new Complex(1.2595,-12.97521);
		
	@Test
	public void test_Case1() {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET);
		loadCaseData("testData/dstab_test/ieee2-1Model.ipss", simuCtx);
		
		DStabilityNetwork net = simuCtx.getDStabilityNet();
		//System.out.println(net.net2String());
		
		DynamicSimuAlgorithm algo = createDStabAlgo(net);

		addDynamicEventData(net);
		
		LoadflowAlgorithm aclfAlgo = algo.getAclfAlgorithm();
		aclfAlgo.loadflow();
	  	assertTrue(simuCtx.getDStabilityNet().isLfConverged());
		
		StateVariableRecorder stateTestRecorder = new StateVariableRecorder(0.0001);
		stateTestRecorder.addTestRecords("Mach@0001", StateVariableRecorder.RecType.Machine, 
				DStabOutSymbol.OUT_SYMBOL_MACH_ANG, timePoints, machAngPoints);
		stateTestRecorder.addTestRecords("Mach@0001", StateVariableRecorder.RecType.Machine, 
				DStabOutSymbol.OUT_SYMBOL_MACH_PE, timePoints, machPePoints);
		algo.setSimuOutputHandler(stateTestRecorder);

	  	
		YMatrixChangeRecorder yTestRecorder = new YMatrixChangeRecorder(0.0001);
		// a 3P fault at t = 1.0, duration = 0.1, Y matrix should change
		yTestRecorder.addTestRecord(new YMatrixChangeRecorder.Record("0003", 1.0));
		// The fault cleared at t = 1.1, Y matrix should change again.
		yTestRecorder.addTestRecord(new YMatrixChangeRecorder.Record("0003", 1.1));
		yTestRecorder.initBusNumber(net);
		net.setNetChangeListener(yTestRecorder);	

		if (algo.initialization()) {
			System.out.println("Running DStab simulation ...");
			algo.performSimulation();
		}

		assertTrue(stateTestRecorder.diffTotal("Mach@0001", StateVariableRecorder.RecType.Machine, 
				DStabOutSymbol.OUT_SYMBOL_MACH_ANG) < 0.01);
		assertTrue(stateTestRecorder.diffTotal("Mach@0001", StateVariableRecorder.RecType.Machine, 
				DStabOutSymbol.OUT_SYMBOL_MACH_PE) < 0.01);

		// check 3P fault at t = 1.0
		assertTrue(yTestRecorder.getTestRecord(1.0, "0003").measured);
		assertTrue(yTestRecorder.getTestRecord(1.0, "0003").difference(yFault).abs() < 0.0001);
		
		// check 3P fault clearing at t = 1.1
		assertTrue(yTestRecorder.getTestRecord(1.1, "0003").isChanged());
		assertTrue(yTestRecorder.getTestRecord(1.1, "0003").difference(yClear).abs() < 0.0001);
		
		//System.out.println(stateTestRecorder);
		//System.out.println(yTestRecorder);
	}
}
