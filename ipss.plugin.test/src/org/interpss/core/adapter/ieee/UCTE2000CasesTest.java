 /*
  * @(#)Test_IEEECommonFormat.java   
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

package org.interpss.core.adapter.ieee;

import static org.junit.Assert.assertTrue;

import org.interpss.CorePluginObjFactory;
import org.interpss.PluginTestSetup;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.spring.PluginSpringFactory;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;

public class UCTE2000CasesTest extends PluginTestSetup {
	@Test 
	public void testCase1() throws Exception {
		AclfNetwork net = CorePluginObjFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee_format/UCTE_2000_WinterOffPeak.ieee")
				.getAclfNet();
		
//		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ieee");
//		SimuContext simuCtx = adapter.load("testData/ieee_format/UCTE_2000_WinterOffPeak.ieee");
//
//		AclfNetwork net = simuCtx.getAclfNet();
  		//assertTrue((net.getBusList().size() == 14 && net.getBranchList().size() == 20));

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setNonDivergent(true);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		assertTrue(net.isLfConverged());		
	}

	//@Test 
	public void testCase2() throws Exception {
		IpssFileAdapter adapter = PluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_format/UCTE_2000_WinterPeak.ieee");

		AclfNetwork net = simuCtx.getAclfNet();
  		//assertTrue((net.getBusList().size() == 14 && net.getBranchList().size() == 20));

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.PQ);
	  	algo.setNonDivergent(true);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	  	
  		assertTrue(net.isLfConverged());		
	}
}

