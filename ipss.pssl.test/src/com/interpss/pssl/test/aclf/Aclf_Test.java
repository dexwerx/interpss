 /*
  * @(#)AclfSampleTest.java   
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
  * @Date 07/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package com.interpss.pssl.test.aclf;

import static com.interpss.pssl.plugin.IpssAdapter.importAclfNet;
import static com.interpss.pssl.plugin.IpssAdapter.FileFormat.IEEECommonFormat;
import static org.interpss.CorePluginFunction.AclfResultSummary;
import static com.interpss.pssl.simu.IpssAclf.createAclfAlgo;
import static org.junit.Assert.assertTrue;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.ext.ipss.IpssScenarioHelper;
import org.ieee.odm.schema.IpssAclfAlgorithmXmlType;
import org.ieee.odm.schema.LfMethodEnumType;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.pssl.common.PSSLException;
import com.interpss.pssl.odm.AclfDslODMRunner;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.test.BaseTestSetup;

public class Aclf_Test extends BaseTestSetup {
	@Test
	public void lfTest()  throws PSSLException {
		AclfNetwork net = importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IEEECommonFormat)
				.load()
				.getAclfNet();	
		
	  	createAclfAlgo(net)
	  			.lfMethod(AclfMethod.NR)
	  			.nonDivergent(true)
	  			.runLoadflow();	
	  	
	  	System.out.println(AclfResultSummary.f(net));
	  	
	  	assertTrue(net.isLfConverged());
	}	
	
	// AclfAlgorithmXmlType
	
	@Test
	public void lfXmlTest()  throws PSSLException {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();	
		
		IpssScenarioHelper helper = new IpssScenarioHelper(new AclfModelParser());
		IpssAclfAlgorithmXmlType algoXml = helper.createIpssAclfAlgorithm();
		
		algoXml.setLfMethod(LfMethodEnumType.NR);
		algoXml.setNonDivergent(true);
		
		new AclfDslODMRunner(net).runAclf(algoXml);
	  	
	  	assertTrue(net.isLfConverged());
	}	
	
}

