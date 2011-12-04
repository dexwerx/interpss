 /*
  * @(#)CR_UserTestCases.java   
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

package org.interpss.odm.psse.v30;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.psse.v30.PSSEV30Adapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.PluginTestSetup;
import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.spring.PluginSpringFactory;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class GuideSample_TestCase extends PluginTestSetup {
	@Test
	public void testCase() throws Exception {
		IODMAdapter adapter = new PSSEV30Adapter();
		assertTrue(adapter.parseInputFile("testData/psse/PSSE_GuideSample.raw"));		
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		if (!new ODMAclfDataMapper()
					.map2Model(parser, simuCtx)) {
  	  		System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
  	  		return;
		}	
  		simuCtx.setName("Sample18Bus");
 	  	simuCtx.setDesc("This project is created by input file adapter.getModel()");
 	  	AclfNetwork net = simuCtx.getAclfNet();
  		//System.out.println(net.net2String());

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
	  	algo.setNonDivergent(true);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		AclfBus swingBus = simuCtx.getAclfNet().getAclfBus("Bus3011");
		SwingBusAdapter swing = swingBus.toSwingBus();
  		Complex p = swing.getGenResults(UnitType.mW);
  		System.out.println(p.getReal() + ", " + p.getImaginary());
  		assertTrue(Math.abs(p.getReal()-258.6568)<0.01);
  		assertTrue(Math.abs(p.getImaginary()-104.04017)<0.01);
	}

	@Test
	public void testCase1() throws Exception {
		IODMAdapter adapter = new PSSEV30Adapter();
		assertTrue(adapter.parseInputFile("testData/psse/PSSE_GuideSample.raw"));		
		
		AclfNetwork net = PluginSpringFactory
				.getOdm2AclfMapper()
				.map2Model((AclfModelParser)adapter.getModel())
				.getAclfNet();
		
  		//System.out.println(net.net2String());

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
	  	algo.setNonDivergent(true);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		AclfBus swingBus = net.getAclfBus("Bus3011");
		SwingBusAdapter swing = swingBus.toSwingBus();
  		Complex p = swing.getGenResults(UnitType.mW);
  		//System.out.println(p.getReal() + ", " + p.getImaginary());
  		assertTrue(Math.abs(p.getReal()-258.657)<0.01);
  		assertTrue(Math.abs(p.getImaginary()-104.045)<0.01);
	}
}

