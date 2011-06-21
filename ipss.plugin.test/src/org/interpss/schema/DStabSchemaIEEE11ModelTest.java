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

package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.math.complex.Complex;
import org.interpss.dstab.ieeeModel.DStabTestSetupBase;
import org.interpss.dstab.output.DatabaseSimuOutputHandler;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.DStabStudyCaseXmlType;
import org.junit.Test;

import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabObjectFactory;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.cache.StateVariableRecorder;
import com.interpss.dstab.cache.YMatrixChangeRecorder;
import com.interpss.dstab.common.DStabOutSymbol;
import com.interpss.dstab.devent.DynamicEvent;
import com.interpss.dstab.devent.DynamicEventType;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class DStabSchemaIEEE11ModelTest extends DStabTestSetupBase {
	@Test
	public void test_Case2() throws Exception {
		File xmlFile = new File("testData/xml/RunDStabCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

  		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
		loadCaseData("testData/dstab_test/ieee1-1Model.ipss", simuCtx);
		
		//System.out.println(net.net2String());
		
	  	int caseCnt = 0;
	  	String dstabNetStr = "";
//	  	SimuObjectFactory.initEMFPackage();
	  	
	  	for ( DStabStudyCaseXmlType scase : parser.getRunDStabStudyCase().getDStabStudyCaseList().getDStabStudyCase()) {
			System.out.println("Running DStab case: " + scase.getRecId());

			caseCnt++;
			DStabilityNetwork net = null;
	  		if (caseCnt == 1) {
	  			net = simuCtx.getDStabilityNet();
	  			dstabNetStr = SerializeEMFObjectUtil.saveModel(net);
	  		}
	  		else {
	  			net = (DStabilityNetwork)SerializeEMFObjectUtil.loadModel(dstabNetStr);
	  			net.rebuildLookupTable();
	  		}

	  		DynamicSimuAlgorithm algo = DStabObjectFactory.createDynamicSimuAlgorithm(net, 
	  				new DatabaseSimuOutputHandler(), msg);
		  	//IpssMapper mapper = new IpssXmlMapper();
	  		PluginSpringCtx.getXml2DStabAlgorithmMapper().map2Model(scase, algo);
	  		//System.out.println(net.net2String());
	  		
	  		if (caseCnt == 1) {
			  	assertTrue(algo.getTotalSimuTimeSec() == 1.0);
			  	assertTrue(algo.getSimuStepSec() == 0.002);
			  	assertTrue(algo.getSimuMethod() == DynamicSimuMethod.MODIFIED_EULER);
			  	assertTrue(algo.getRefMachine() != null);
			  	assertTrue(algo.getRefMachine().getId().equals("Mach@0003"));
			  	assertTrue(algo.getDStabNet().getDynamicEventList().size() == 0);

			  	LoadflowAlgorithm aclfAlgo = algo.getAclfAlgorithm();
				aclfAlgo.loadflow();
			  	assertTrue(algo.getDStabNet().isLfConverged());
				
				double[] timePoints    = {0.0,  0.5,  1.0},
		     	 machAngPoints = {49.460, 49.460, 49.460};
				StateVariableRecorder stateTestRecorder = new StateVariableRecorder(0.0001);
				stateTestRecorder.addTestRecords("Mach@0001", StateVariableRecorder.RecType.Machine, 
						DStabOutSymbol.OUT_SYMBOL_MACH_ANG, timePoints, machAngPoints);
				algo.setSimuOutputHandler(stateTestRecorder);
			  	
				if (algo.initialization()) {
					System.out.println("Running DStab simulation ...");
					algo.performSimulation(msg);
				}
				
				assertTrue(stateTestRecorder.diffTotal("Mach@0001", StateVariableRecorder.RecType.Machine, 
						DStabOutSymbol.OUT_SYMBOL_MACH_ANG) < 0.01);
			}
	  		else if (caseCnt == 2) {
			  	assertTrue(algo.getTotalSimuTimeSec() == 10.0);
			  	assertTrue(algo.getSimuStepSec() == 0.002);
			  	assertTrue(algo.getSimuMethod() == DynamicSimuMethod.MODIFIED_EULER);
			  	assertTrue(algo.getRefMachine() != null);
			  	assertTrue(algo.getRefMachine().getId().equals("Mach@0003"));

			  	assertTrue(algo.getDStabNet().getNetEqnIterationNoEvent() == 3);
			  	assertTrue(algo.getDStabNet().getNetEqnIterationWithEvent() == 5);
			  	
			  	assertTrue(algo.getDStabNet().getDynamicEventList().size() == 1);
			  	DynamicEvent event = algo.getDStabNet().getDynamicEventList().get(0);
			  	assertTrue(event.getType() == DynamicEventType.BUS_FAULT);
			  	assertTrue(event.getStartTimeSec() == 1.0);
			  	assertTrue(event.getDurationSec() == 0.1);
			  	assertTrue(event.getBusFault().getBus().getId().equals("0003"));
			  	
			  	LoadflowAlgorithm aclfAlgo = algo.getAclfAlgorithm();
				aclfAlgo.loadflow();
			  	assertTrue(algo.getDStabNet().isLfConverged());
			  	
			  	double[] 	timePoints    = {0.0,    1.0,    2.0,    3.0,    4.0,    5.0,    6.0,    7.0,    8.0,    9.0,    10.0},
				      		machAngPoints = {49.460, 49.460, 46.705, 47.171, 47.563, 47.883, 48.145, 48.361, 48.540, 48.689, 48.814},
				      		machPePoints  = {0.426,  0.426,  0.333,  0.352,  0.367,  0.379,  0.389,  0.396,  0.402,  0.407,  0.411};
				Complex 	yFault = new Complex(1.2595,-100000012.97521),
				         	yClear = new Complex(1.2595,-12.97521);
				
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

				//TextSimuOutputHandler handler = new TextSimuOutputHandler();
				//algo.setSimuOutputHandler(handler);
				if (algo.initialization()) {
					System.out.println("Running DStab simulation ...");
					algo.performSimulation(msg);
				}
/*
				assertTrue(stateTestRecorder.diffTotal("Mach@0001", StateVariableTestRecorder.RecType_Machine, 
						DStabOutSymbol.OUT_SYMBOL_MACH_ANG) < 0.01);
*/						
				assertTrue(stateTestRecorder.diffTotal("Mach@0001", StateVariableRecorder.RecType.Machine, 
						DStabOutSymbol.OUT_SYMBOL_MACH_PE) < 0.01);

				// check 3P fault at t = 1.0
				assertTrue(yTestRecorder.getTestRecord(1.0, "0003").isChanged());
				assertTrue(yTestRecorder.getTestRecord(1.0, "0003").difference(yFault).abs() < 0.0001);
				
				// check 3P fault clearing at t = 1.1
				assertTrue(yTestRecorder.getTestRecord(1.1, "0003").isChanged());
				assertTrue(yTestRecorder.getTestRecord(1.1, "0003").difference(yClear).abs() < 0.0001);
	  		}
	  	}
	}
}
