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

package com.interpss.test.psse;

import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.test.QATestSetup;
import com.interpss.test.util.FileReader;
import com.interpss.test.util.impl.PSSE_FileProcessor;

public class WECC_10212010_09hs2sbp extends QATestSetup {
	@Test
	public void testCase1() throws Exception {
		AclfNetwork net = PluginObjectFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.PSSE, IpssFileAdapter.Version.PSSE_30)
				.load("testData/psse/WECC_10212010_09hs2sbp.raw")
				.getAclfNet();	

//		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
//	  	algo.loadflow();
//  		//System.out.println(net.net2String());
//	  	
//  		assertTrue(net.isLfConverged());	
  		
		PSSE_FileProcessor proc = new PSSE_FileProcessor(net);
		new FileReader("output/psse/WECC_10212010_09hs2sbp_raw_01262011.txt")
				.processFile(proc);		
//		System.out.println("Total bus: " + proc.getTotalBus());
//		//assertTrue(proc.getTotalBus() == 659);
//		
		if (proc.getErrMsgList().size() > 0) {
			System.out.println("WECC_10212010_09hs2sbp_raw");
			System.out.println(proc.getErrMsgList().toString());
		}
	}
}

