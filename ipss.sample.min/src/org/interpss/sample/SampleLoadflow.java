 /*
  * @(#)SampleLoadflow.java   
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

package org.interpss.sample;

import static org.junit.Assert.assertTrue;

import org.interpss.IpssPlugin;
import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.display.AclfOutFunc;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

public class SampleLoadflow {
	@Test
	public void set2BusNetworkData() throws Exception {
		IpssPlugin.init();
		
		AclfNetwork net = PluginObjectFactory
			.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
			.loadDebug("testData/ieee14.ieee")
			.getAclfNet();	
		System.out.println(net.net2String());	
		
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
  		assertTrue(algo.loadflow());
  		System.out.println(AclfOutFunc.loadFlowSummary(net));
    }
}
