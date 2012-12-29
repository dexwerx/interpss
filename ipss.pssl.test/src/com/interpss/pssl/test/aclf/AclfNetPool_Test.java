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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.plugin.IpssAdapter.FileImportDSL;
import com.interpss.pssl.test.BaseTestSetup;
import com.interpss.pssl.util.AclfNetObjectPool;

public class AclfNetPool_Test extends BaseTestSetup {
	@Test
	public void poolTestCase() throws Exception {
		FileImportDSL dsl = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load();		
		
		AclfNetObjectPool pool = new AclfNetObjectPool(dsl);
		
		AclfNetwork net1 = (AclfNetwork)pool.borrowObject();
		System.out.println(net1.getName());

		AclfNetwork net2 = (AclfNetwork)pool.borrowObject();
		System.out.println(net2.getName());
		
		assertTrue(pool.getNumActive() == 2);
		//System.out.println("active: " + pool.getNumActive() + ", idle: " + pool.getNumIdle());
		
		pool.returnObject(net1);

		assertTrue(pool.getNumActive() == 1);
		//System.out.println("active: " + pool.getNumActive() + ", idle: " + pool.getNumIdle());
	}		
}

