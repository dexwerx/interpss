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

package org.interpss.core.adapter.ge;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginObjectFactory;
import org.interpss.PluginTestSetup;
import org.interpss.custom.IpssFileAdapter;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.common.datatype.UnitType;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algo.LoadflowAlgorithm;

public class GESampleTestCases extends PluginTestSetup {
	@Test
	public void odmAdapterTestCase() throws Exception {
		AclfNetwork net = PluginObjectFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.GE_PSLF)
				.load("testdata/ge/Sample18Bus.epc")
				.getAclfNet();	
		
//		IODMAdapter adapter = ODMObjectFactory.createODMAdapter(ODMFileFormatEnum.GePSLF);
//		assertTrue(adapter.parseInputFile("testdata/ge/Sample18Bus.epc"));		
//		
//		AclfNetwork net = PluginSpringCtx
//				.getOdm2AclfMapper()
//				.map2Model((AclfModelParser)adapter.getModel())
//				.getAclfNet();
		
		assertTrue(net.getNoBus() == 18);
		assertTrue(net.getNoBranch() == 24);
		
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.loadflow();
		//System.out.println(net.net2String());
	  	
  		assertTrue(net.isLfConverged());		
  		AclfBus swingBus = (AclfBus)net.getBus("Bus101");
		SwingBusAdapter swing = swingBus.toSwingBus();
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-5.234)<0.01);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-1.108)<0.01);
	}	
}

