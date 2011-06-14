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
import org.interpss.dstab.output.TextSimuOutputHandler;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class DStab_5BusNoRegulator extends DStabTestSetupBase {
	@Test
	public void testDStab5BusCase() throws InterpssException, GridException {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
		loadCaseData("testData/dstab_test/DStab-5BusNoReg.ipss", simuCtx);
		
		DStabilityNetwork net = simuCtx.getDStabilityNet();
		System.out.println(net.net2String());

		DynamicSimuAlgorithm dstabAlgo = createDStabAlgo(net);
		LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
		assertTrue(aclfAlgo.loadflow());
		//System.out.println(AclfOutFunc.loadFlowSummary(net));
			
		dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
		dstabAlgo.setSimuStepSec(0.001);
		dstabAlgo.setTotalSimuTimeSec(0.01);
		
		//IpssLogger.getLogger().setLevel(Level.INFO);
		dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
		if (dstabAlgo.getSolver().initialization()) {
			System.out.println("Running DStab simulation ...");
			assertTrue(dstabAlgo.performSimulation(msg));
		}
	}
	
	@Test
	public void sys2010_XmlDstabtestCase() throws Exception {
		File file = new File("testData/ieee_odm/Tran_5Bus.xml");
		DStabModelParser parser = ODMObjectFactory.createDStabModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));

			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
			if (!new ODMDStabDataMapper(msg)
						.map2Model(parser, simuCtx)) {
				System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
				return;
			}
			
			DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
			LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
			assertTrue(aclfAlgo.loadflow());
			//System.out.println(AclfOutFunc.loadFlowSummary(net));
				
			dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
			dstabAlgo.setSimuStepSec(0.001);
			dstabAlgo.setTotalSimuTimeSec(0.01);
			
			//IpssLogger.getLogger().setLevel(Level.INFO);
			dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
			if (dstabAlgo.getSolver().initialization()) {
				System.out.println("Running DStab simulation ...");
				assertTrue(dstabAlgo.performSimulation(msg));
			}
		}
	}
}
