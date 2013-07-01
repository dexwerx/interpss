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

package org.interpss.odm.acsc;

import java.io.File;
import java.io.FileInputStream;

import org.ieee.odm.model.acsc.AcscModelParser;
import org.interpss.CorePluginTestSetup;
import org.interpss.mapper.odm.ODMAcscDataMapper;
import org.junit.Test;

import com.interpss.core.acsc.AcscNetwork;

public class Acsc5Bus_TestCase extends CorePluginTestSetup {
	@Test
	public void testCase() throws Exception {
		File file = new File("testdata/ieee_odm/ODM_AcscNoLF_5Bus.xml");
		AcscModelParser parser = new AcscModelParser();
		parser.parse(new FileInputStream(file));	
		
		AcscNetwork faultNet = new ODMAcscDataMapper().map2Model(parser).getAcscNet();
	}

	@Test
	public void testCase1() throws Exception {
		File file = new File("testdata/ieee_odm/ODM_Acsc_5Bus.xml");
		AcscModelParser parser = new AcscModelParser();
		parser.parse(new FileInputStream(file));		
	}
}

