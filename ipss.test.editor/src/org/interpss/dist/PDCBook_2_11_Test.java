 /*
  * @(#)Sample2BusTest.java   
  *
  * Copyright (C) 2011 www.interpss.org
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
  * @Date 02/01/2011
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.dist;

import static org.junit.Assert.assertTrue;

import org.interpss.EditorTestSetup;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dist.DistBus;
import com.interpss.dist.DistNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class PDCBook_2_11_Test  extends EditorTestSetup { 
	@Test
	public void lfTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DISTRIBUTE_NET);
		loadCaseData("testData/geditor/dist/PDCBook_2-11.ipss", simuCtx);
		
	  	DistNetwork distNet = simuCtx.getDistNet();		
	  	assertTrue(distNet.getFaultNet().isLfDataLoaded());
	  	assertTrue(distNet.getFaultNet().isScDataLoaded());
	  	
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(distNet.getAclfNet());
	  	algo.loadflow();
	  	assertTrue(distNet.getAclfNet().isLfConverged());
	  	//System.out.println(AclfOutFunc.lfResultsBusStyle(distNet.getAclfNet()));	  	
	  	
	  	DistBus bus = (DistBus)distNet.getBus("0001");
	  	//System.out.println(bus.getAcscBus().getGenResults().getReal());
	  	//System.out.println(bus.getAcscBus().getGenResults().getImaginary());
	  	assertTrue(Math.abs(bus.getAcscBus().getGenResults().getReal() + 0.04) < 0.001);
	  	assertTrue(Math.abs(bus.getAcscBus().getGenResults().getImaginary() + 0.02832) < 0.0001);
	}
	
}

