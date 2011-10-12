 /*
  * @(#)WECC_10212010_TestCase.java   
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
  * @Author Stephen Hou
  * @Version 1.0
  * @Date 02/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.odm.psse.v30;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.psse.v30.PSSEV30Adapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.PluginTestSetup;
import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.junit.Test;

import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class PSSE_5Bus_TestCase extends PluginTestSetup { 
	@Test
	public void testCase1() throws Exception {
		IODMAdapter adapter = new PSSEV30Adapter();
		assertTrue(adapter.parseInputFile("testdata/psse/PSSE_5Bus_Test.raw"));
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		//parser.stdout();
		
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		if (!new ODMAclfDataMapper()
					.map2Model(parser, simuCtx)) {
  	  		System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
  	  		return;
		}			
		
	}
}


