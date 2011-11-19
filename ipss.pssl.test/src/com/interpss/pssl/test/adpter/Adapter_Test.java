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

package com.interpss.pssl.test.adpter;

import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.simu.IpssAclf;
import com.interpss.pssl.test.BaseTestSetup;

public class Adapter_Test extends BaseTestSetup {
	@Test
	public void singlePointTest1() {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		               
		
		IpssAclf.createAlgo(net)                        
		            .lfMethod(AclfMethod.NR)
		            .tolerance(0.0001, UnitType.PU)
		            .runLoadflow();               

		System.out.println(AclfOutFunc.loadFlowSummary(net));
	}
}

