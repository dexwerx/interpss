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

package com.interpss.pssl.test.cmd;

import static com.interpss.pssl.plugin.IpssAdapter.FileFormat.IEEECommonFormat;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.interpss.pssl.plugin.CmdRunner;
import com.interpss.pssl.test.BaseTestSetup;
import com.interpss.simu.SimuContext;

public class Aclf_CmdTest extends BaseTestSetup {
	@Test
	public void lfTest()  throws FileNotFoundException {
		SimuContext simuCtx = new CmdRunner()
				.setInputFilename("testData/aclf/ieee14.ieee")
				.setFormat(IEEECommonFormat)
				.setOdmControlFilename("testData/aclf/AclfRun.xml")
				.run();
		
	  	assertTrue(simuCtx.getAclfNet().isLfConverged());
	}	
	
}

