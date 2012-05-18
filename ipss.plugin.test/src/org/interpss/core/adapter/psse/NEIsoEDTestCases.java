 /*
  * @(#)NEIsoEDTestCases.java   
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

import org.interpss.EditorTestSetup;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.spring.PluginSpringFactory;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;

public class NEIsoEDTestCases extends EditorTestSetup {
	@Test
	public void testCase1() throws Exception {
		IpssFileAdapter adapter = PluginSpringFactory.getCustomFileAdapter("psse");
		//SimuContext simuCtx = adapter.load("testData/psse/test_model_V29.raw", SpringAppContext.getIpssMsgHub());
		SimuContext simuCtx = adapter.load("testData/psse/test_model_V30.raw");
  		//System.out.println(simuCtx.getAclfNet().net2String());

		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setNonDivergent(true);
	  	algo.setLfMethod(AclfMethod.NR);
	  	net.setBypassDataCheck(true);
	  	algo.loadflow();
  		//System.out.println(net.net2String());
	}
}

