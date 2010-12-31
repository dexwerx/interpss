/*
 * @(#)PreventiveCaseTest.java   
 *
 * Copyright (C) 2006-2008 www.interpss.org
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
 * @Date 04/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.ruleset;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginTestSetup;
import org.interpss.schema.AclfStudyCaseXmlType;
import org.interpss.schema.PreventiveRuleSetXmlType;
import org.interpss.schema.RuleBaseXmlType;
import org.interpss.schema.RunStudyCaseXmlType;
import org.interpss.schema.ViolationConditionXmlType;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.PreventiveRuleHanlder;
import org.junit.Test;

import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class RuleXmlCaseTest extends PluginTestSetup {
	@Test
	public void runAclfProtectCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14BusProtect.ipss", simuCtx);
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfNet());

		File xmlFile = new File("testData/xml/ruleset/RunAclfCaseProtection.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == RunStudyCaseXmlType.AnalysisRunType.RUN_ACLF);
  		
	  	AclfStudyCaseXmlType aclfCase = parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCaseArray(0);
	  	AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(netStr);
		net.rebuildLookupTable();
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		PluginSpringCtx.getXml2LfAlgorithmMapper()
				.map2Model(aclfCase.getAclfAlgorithm(), algo);
	  	
  		assertTrue(algo.getMaxIterations() == 20);
  		assertTrue(algo.getTolerance() == 1.0E-4);
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());
	  		
  		RuleBaseXmlType aclfRuleBase = parser.getRunStudyCase().getRuleBase();
	  	assertTrue(aclfRuleBase != null);
	  	
	  	PreventiveRuleSetXmlType ruleSet = aclfRuleBase.getPreventiveRuleSetList().getPreventiveRuleSetArray(0);
	  	assertTrue(ruleSet.getPreventiveRuleList().getPreventiveRuleArray().length == 2);
	  	PreventiveRuleSetXmlType.PreventiveRuleList.PreventiveRule rule1 = ruleSet.getPreventiveRuleList().getPreventiveRuleArray()[0];
	  	ViolationConditionXmlType cond = rule1.getCondition();
	  	assertTrue(PreventiveRuleHanlder.evlAclfNetBranchCondition(cond, net, msg));

	  	PreventiveRuleSetXmlType.PreventiveRuleList.PreventiveRule rule2 = ruleSet.getPreventiveRuleList().getPreventiveRuleArray()[1];
	  	cond = rule2.getCondition();
	  	assertTrue(!PreventiveRuleHanlder.evlAclfNetBusCondition(cond, net, 1.2, 0.8, msg));
	  	// volatge at 0003 1.01
	  	assertTrue(PreventiveRuleHanlder.evlAclfNetBusCondition(cond, net, 1.0, 0.8, msg));
	  	assertTrue(PreventiveRuleHanlder.evlAclfNetBusCondition(cond, net, 1.2, 1.05, msg));

	  	assertTrue(PreventiveRuleHanlder.applyRuleSet(net, parser.getRunStudyCase().getRuleBase(), 1, 1.2, 0.8, msg));
	  	assertTrue(!net.getAclfBranch("0010->0009(1)").isActive());
	}		

	//@Test
	public void run3WXfrOffCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfNet());

		File xmlFile = new File("testData/xml/ruleset/IEEE14Bus_W3XfrOff.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == RunStudyCaseXmlType.AnalysisRunType.RUN_ACLF);
  		
	  	AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(netStr);
		net.rebuildLookupTable();
		net.getAclfBranch("0005->0006(1)").setRatingMva1(70.0);

		AclfStudyCaseXmlType aclfCase = parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCaseArray(0);
		PluginSpringCtx.getModXml2NetMapper()
				.map2Model(aclfCase.getModification(), net);


		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		System.out.println(aclfCase.getAclfAlgorithm());
		PluginSpringCtx.getXml2LfAlgorithmMapper()
				.map2Model(aclfCase.getAclfAlgorithm(), algo);
  		
  		assertTrue(algo.getMaxIterations() == 20);
  		assertTrue(algo.getTolerance() == 1.0E-4);
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());  	
  		
	  	assertTrue(!net.getAclfBus("0007").isActive());
	  	assertTrue(!net.getAclfBus("0008").isActive());

	  	assertTrue(!net.getAclfBranch("0004->0007(1)").isActive());
	  	assertTrue(!net.getAclfBranch("0004->0009(1)").isActive());
	  	assertTrue(!net.getAclfBranch("0007->0009(1)").isActive());
	  	assertTrue(!net.getAclfBranch("0007->0008(1)").isActive());
	  	
	  	assertTrue(PreventiveRuleHanlder.applyRuleSet(net, 
	  				parser.getRunStudyCase().getRuleBase(), 1, 1.2, 0.8, msg));
  		assertTrue(algo.loadflow());
	  	
  		assertTrue(PreventiveRuleHanlder.applyRuleSet(net, 
  					parser.getRunStudyCase().getRuleBase(), 2, 1.2, 0.8, msg));
  		assertTrue(algo.loadflow());

  		assertTrue(net.getAclfBus("0014").getLoadCode() == AclfLoadCode.NON_LOAD);
	  	assertTrue(net.getAclfBus("0013").getLoadCode() == AclfLoadCode.NON_LOAD);
	}

	//@Test
	public void run3WXfrOffAnotherApproachCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);

		File xmlFile = new File("testData/xml/ruleset/IEEE14Bus_W3XfrOff.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == RunStudyCaseXmlType.AnalysisRunType.RUN_ACLF);
  		
	  	AclfNetwork net = simuCtx.getAclfNet();
		net.getAclfBranch("0005->0006(1)").setRatingMva1(70.0);

		AclfStudyCaseXmlType aclfCase = parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCaseArray(0);
		PluginSpringCtx.getModXml2NetMapper()
				.map2Model(aclfCase.getModification(), net);

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		PluginSpringCtx.getXml2LfAlgorithmMapper()
				.map2Model(aclfCase.getAclfAlgorithm(), algo);
  		
  		assertTrue(algo.getMaxIterations() == 20);
  		assertTrue(algo.getTolerance() == 1.0E-4);
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());  	
	
	  	PreventiveRuleHanlder.applyRuleSet2AclfNet(algo, parser.getRunStudyCase().getRuleBase(), 1.2, 0.8, msg);

  		assertTrue(net.getAclfBus("0014").getLoadCode() == AclfLoadCode.NON_LOAD);
	  	assertTrue(net.getAclfBus("0013").getLoadCode() == AclfLoadCode.NON_LOAD);
	}
}
