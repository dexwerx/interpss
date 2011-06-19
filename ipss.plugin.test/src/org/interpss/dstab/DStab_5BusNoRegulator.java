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

package org.interpss.dstab;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.gridgain.grid.GridException;
import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.dstab.ieeeModel.DStabTestSetupBase;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.cache.StateVariableRecorder;
import com.interpss.dstab.common.DStabOutSymbol;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class DStab_5BusNoRegulator extends DStabTestSetupBase {
	@Test
	public void testDStab5BusCase() throws InterpssException, GridException {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
		loadCaseData("testData/dstab_test/DStab-5BusNoReg.ipss", simuCtx);
		
		DStabilityNetwork net = simuCtx.getDStabilityNet();
		// System.out.println(net.net2String());

		DynamicSimuAlgorithm dstabAlgo = createDStabAlgo(net);
		LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
		assertTrue(aclfAlgo.loadflow());
		//System.out.println(AclfOutFunc.loadFlowSummary(net));
			
		dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
		dstabAlgo.setSimuStepSec(0.001);
		dstabAlgo.setTotalSimuTimeSec(0.01);
		
		double[] timePoints   = {0.0,      0.004,    0.007,    0.01},
 			     machPePoints = {0.8333,   0.8333,   0.8333,   0.8333},
 			     machVPoints  = {1.0795,   1.0795,   1.0795,   1.0795};

		StateVariableRecorder stateTestRecorder = new StateVariableRecorder(0.0001);
		stateTestRecorder.addTestRecords("Mach@0004", StateVariableRecorder.RecType.Machine, 
					DStabOutSymbol.OUT_SYMBOL_MACH_EQ1, timePoints, machVPoints);
		stateTestRecorder.addTestRecords("Mach@0004", StateVariableRecorder.RecType.Machine, 
					DStabOutSymbol.OUT_SYMBOL_MACH_PE, timePoints, machPePoints);
		dstabAlgo.setSimuOutputHandler(stateTestRecorder);

		//IpssLogger.getLogger().setLevel(Level.INFO);
		//dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
		if (dstabAlgo.getSolver().initialization()) {
			//System.out.println(net.net2String());

			//System.out.println("Running DStab simulation ...");
			assertTrue(dstabAlgo.performSimulation(msg));
		}

		assertTrue(stateTestRecorder.diffTotal("Mach@0004", StateVariableRecorder.RecType.Machine, 
				DStabOutSymbol.OUT_SYMBOL_MACH_EQ1) < 0.0001);
		assertTrue(stateTestRecorder.diffTotal("Mach@0004", StateVariableRecorder.RecType.Machine, 
				DStabOutSymbol.OUT_SYMBOL_MACH_PE) < 0.0001);			
	}
	
	@Test
	public void odmTestCase() throws Exception {
		File file = new File("testData/ieee_odm/Tran_5Bus_062011.xml");
		DStabModelParser parser = ODMObjectFactory.createDStabModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));

			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
			if (!new ODMDStabDataMapper(msg)
						.map2Model(parser, simuCtx)) {
				System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
				return;
			}
			//System.out.println(simuCtx.getDStabilityNet().net2String());
			
			DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
			LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
			assertTrue(aclfAlgo.loadflow());
			//System.out.println(AclfOutFunc.loadFlowSummary(simuCtx.getDStabilityNet()));
				
			dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
			dstabAlgo.setSimuStepSec(0.001);
			dstabAlgo.setTotalSimuTimeSec(0.01);
			
			double[] timePoints   = {0.0,      0.004,    0.007,    0.01},
		             machPePoints = {0.8422,   0.8422,   0.8422,   0.8422},
		             machVPoints  = {1.0976,   1.0976,   1.0976,   1.0976};

			StateVariableRecorder stateTestRecorder = new StateVariableRecorder(0.0001);
			stateTestRecorder.addTestRecords("Bus-4-mach1", StateVariableRecorder.RecType.Machine, 
					DStabOutSymbol.OUT_SYMBOL_MACH_EQ1, timePoints, machVPoints);
			stateTestRecorder.addTestRecords("Bus-4-mach1", StateVariableRecorder.RecType.Machine, 
					DStabOutSymbol.OUT_SYMBOL_MACH_PE, timePoints, machPePoints);
			dstabAlgo.setSimuOutputHandler(stateTestRecorder);
			
			//IpssLogger.getLogger().setLevel(Level.INFO);
			//dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
			if (dstabAlgo.getSolver().initialization()) {
				//System.out.println(simuCtx.getDStabilityNet().net2String());

//				System.out.println("Running DStab simulation ...");
				assertTrue(dstabAlgo.performSimulation(msg));
			}
			
			assertTrue(stateTestRecorder.diffTotal("Bus-4-mach1", StateVariableRecorder.RecType.Machine, 
					DStabOutSymbol.OUT_SYMBOL_MACH_EQ1) < 0.0001);
			assertTrue(stateTestRecorder.diffTotal("Bus-4-mach1", StateVariableRecorder.RecType.Machine, 
					DStabOutSymbol.OUT_SYMBOL_MACH_PE) < 0.001);			
		}
	}
}
