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

package org.interpss.core.adapter.psse;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.PluginObjectFactory;
import org.interpss.PluginTestSetup;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.spring.PluginSpringCtx;

import com.interpss.CoreObjectFactory;
import com.interpss.common.datatype.UnitType;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;

public class CR_UserTestCases extends PluginTestSetup {
	//@Test
	public void testCase1() throws Exception {
		AclfNetwork net = PluginObjectFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.PSSE, IpssFileAdapter.Version.PSSE_30)
				.load("testData/psse/PSSE_5Bus_Test.raw")
				.getAclfNet();	
		
//		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("psse");
//		SimuContext simuCtx = adapter.load("testData/psse/PSSE_5Bus_Test.raw");
////  		System.out.println(simuCtx.getAclfNet().net2String());
//
//		AclfNetwork net = simuCtx.getAclfNet();

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.PQ);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		AclfBus swingBus = net.getAclfBus("1");
		SwingBusAdapter swing = swingBus.toSwingBus();
  		Complex p = swing.getGenResults(UnitType.mW);
  		assertTrue(Math.abs(p.getReal()-22.547)<0.01);
  		assertTrue(Math.abs(p.getImaginary()-15.852)<0.01);	  	
	}
/*
	@Test
	public void odm_testCase() throws Exception {
		IODMAdapter adapter = new XBeanPSSEV30Adapter(IpssLogger.getLogger());
		assertTrue(adapter.parseInputFile("testData/psse/PSSE_5Bus_Test.raw"));		
		
		IEEEODMMapper<XBeanODMModelParser> mapper = new IEEEODMMapper<XBeanODMModelParser>(msg);
		SimuContext simuCtx = mapper.map2Model((XBeanODMModelParser)adapter.getModel());
		AclfNetwork net = simuCtx.getAclfNet();
		
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, CoreCommonSpringCtx.getIpssMsgHub());
	  	algo.setLfMethod(AclfMethod.PQ);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		AclfBus swingBus = net.getAclfBus("Bus1");
		SwingBusAdapter swing = swingBus.toSwingBus();
  		Complex p = swing.getGenResults(UnitType.mW);
  		assertTrue(Math.abs(p.getReal()-22.547)<0.01);
  		assertTrue(Math.abs(p.getImaginary()-15.852)<0.01);	  	
	}
*/	
	//@Test
	public void testCase2() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("psse");
		SimuContext simuCtx = adapter.load("testData/psse/MXV-1120MW_FNC475_FEC196_FAC212_InterPSS_3d.raw");
//  		System.out.println(simuCtx.getAclfNet().net2String());

		AclfNetwork net = simuCtx.getAclfNet();

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.PQ);
	  	algo.loadflow();
  		//System.out.println(net.net2String());

	  	AclfBus swingBus = simuCtx.getAclfNet().getAclfBus("1");
		SwingBusAdapter swing = swingBus.toSwingBus();
  		Complex p = swing.getGenResults(UnitType.mW);
  		//System.out.println(p.getReal() + "  " + p.getImaginary());
  		assertTrue(Math.abs(p.getReal()-1841.659)<0.01);
  		assertTrue(Math.abs(p.getImaginary()-11.714)<0.01);	  	
	}
	
/*	
	@Test
	public void testCase2ODM() throws Exception {
		IODMAdapter adapter = new XBeanPSSEV30Adapter(IpssLogger.getLogger());
		assertTrue(adapter.parseInputFile("testData/psse/MXV-1120MW_FNC475_FEC196_FAC212_InterPSS_3d.raw"));		
		
		IEEEODMMapper<XBeanODMModelParser> mapper = new IEEEODMMapper<XBeanODMModelParser>(msg);
		SimuContext simuCtx = mapper.map2Model((XBeanODMModelParser)adapter.getModel());
		AclfNetwork net = simuCtx.getAclfNet();

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, CoreCommonSpringCtx.getIpssMsgHub());
	  	algo.setLfMethod(AclfMethod.PQ);
	  	algo.loadflow();
  		//System.out.println(net.net2String());

	  	AclfBus swingBus = net.getAclfBus("Bus1");
		SwingBusAdapter swing = swingBus.toSwingBus();
  		Complex p = swing.getGenResults(UnitType.mW);
  		System.out.println(p.getReal() + ", " + p.getImaginary());
  		assertTrue(Math.abs(p.getReal()-1841.665)<0.01);
  		assertTrue(Math.abs(p.getImaginary()-11.7380)<0.01);	  	
	}
*/		
}

