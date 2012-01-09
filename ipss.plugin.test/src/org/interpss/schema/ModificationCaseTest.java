/*
 * @(#)ModificationCaseTest.java   
 *
 * Copyright (C) 2006-2007 www.interpss.org
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
 * @Date 09/15/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginTestSetup;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.ModificationXmlType;
import org.junit.Test;

import com.interpss.SimuObjectFactory;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class ModificationCaseTest extends PluginTestSetup {
	@Test
	public void modificationOnlyTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
  		assertTrue((net.getBusList().size() == 14 && net.getBranchList().size() == 20));
	  	assertTrue(net.getAclfBus("0003").getGenCode() != AclfGenCode.NON_GEN);
	  	assertTrue(net.getAclfBus("0009").getLoadCode() != AclfLoadCode.NON_LOAD);

		applyModification(net, "testData/xml/ModificationOnly.xml");
	  	
	  	assertTrue(!net.getBranch("0010->0009(1)").isActive());
	  	assertTrue(!net.getBus("0006").isActive());
	  	
	  	// load increased by 0.1 pu
	  	assertTrue(net.getAclfBus("0014").getLoadP() == 1.1*0.149);
	  	assertTrue(net.getAclfBus("0014").getLoadQ() == 1.1*0.05);
	  	
	  	// load set to 0.15+j0.06 pu
	  	assertTrue(net.getAclfBus("0013").getLoadP() == 0.15);
	  	assertTrue(net.getAclfBus("0013").getLoadQ() == 0.06);

	  	// load added 1.0+j1.0 MVA
	  	assertTrue(net.getAclfBus("0012").getLoadP() == 0.071);
	  	assertTrue(Math.abs(net.getAclfBus("0012").getLoadQ()-0.026) < 1.0E-5);

		final AclfSwingBus gen = net.getAclfBus("0001").toSwingBus();
	  	assertTrue(gen.getVoltMag(UnitType.PU) == (1.06*1.01));

	  	// branch Z increase by 10%
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").getZ().getImaginary()-0.20912*1.1) < 1.0E-5);

	  	assertTrue(net.getAclfBus("0003").getGenCode() == AclfGenCode.NON_GEN);

	  	assertTrue(net.getAclfBus("0009").getLoadCode() == AclfLoadCode.NON_LOAD);
	}
	
	@Test
	public void modificationPersistanceTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
  		assertTrue((net.getBusList().size() == 14 && net.getBranchList().size() == 20));

		applyModification(net, "testData/xml/ModificationOnly.xml");
	  	
	  	assertTrue(!net.getBranch("0010->0009(1)").isActive());
	  	assertTrue(!net.getBus("0006").isActive());
	  	
	  	// load increased by 0.1 pu
	  	assertTrue(net.getAclfBus("0014").getLoadP() == 1.1*0.149);
	  	assertTrue(net.getAclfBus("0014").getLoadQ() == 1.1*0.05);
	  	
	  	// load set to 0.15+j0.06 pu
	  	assertTrue(net.getAclfBus("0013").getLoadP() == 0.15);
	  	assertTrue(net.getAclfBus("0013").getLoadQ() == 0.06);

	  	// load added 1.0+j1.0 MVA
	  	assertTrue(net.getAclfBus("0012").getLoadP() == 0.071);
	  	assertTrue(Math.abs(net.getAclfBus("0012").getLoadQ()-0.026) < 1.0E-5);

		final AclfSwingBus gen = net.getAclfBus("0001").toSwingBus();
	  	assertTrue(gen.getVoltMag(UnitType.PU) == (1.06*1.01));

	  	// branch Z increase by 10%
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").getZ().getImaginary()-0.20912*1.1) < 1.0E-5);
	}
	
	private void applyModification(AclfNetwork net, String filename) {
		try {
			File xmlFile = new File(filename);
			IpssXmlParser parser = new IpssXmlParser(xmlFile);
			ModificationXmlType mod = parser.getModification();
			PluginSpringFactory.getModXml2NetMapper().map2Model(mod, net);
		} catch (Exception e) {
			IpssLogger.getLogger().severe("IpssAclf.applyModification(), " + e.toString());
		}
	}	
}
