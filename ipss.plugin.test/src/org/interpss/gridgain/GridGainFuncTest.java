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

package org.interpss.gridgain;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.interpss.gridgain.util.GridUtil;
import org.junit.Test;

public class GridGainFuncTest extends PluginTestSetup {
	/*
	 * this test make sure there at least one slave node running
	 */
	@Test
	public void isGridLibLoadedCaseTest() {
		GridUtil.startDefaultGrid(GridBaseTestSetup.GridGainHome);
		assertTrue(GridUtil.isGridEnabled());
		if (GridUtil.getDefaultGrid().getAllNodes().size() <= 1)
			System.out.println("Please start a least one Gridgain agent for the test");
		assertTrue(GridUtil.getDefaultGrid().getAllNodes().size() > 1);
		GridUtil.stopDefaultGrid();
	}	
}