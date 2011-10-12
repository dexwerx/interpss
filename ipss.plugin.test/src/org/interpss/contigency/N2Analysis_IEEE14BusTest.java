 /*
  * @(#)N2Analysis_IEEE14BusTest.java   
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

package org.interpss.contigency;

import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.interpss.PluginTestSetup;
import org.interpss.display.ContingencyOutFunc;
import org.interpss.xml.IpssXmlHelper;
import org.interpss.xml.schema.ModificationXmlType;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Branch;
import com.interpss.mapper.Modification2ModelMapper;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;
import com.interpss.simu.multicase.aclf.AclfStudyCase;
import com.interpss.simu.multicase.aclf.ContingencyAnalysis;
import com.interpss.simu.multicase.aclf.ContingencyAnalysisType;

public class N2Analysis_IEEE14BusTest extends PluginTestSetup {
	@Test
	public void sampleTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);

		AclfNetwork net = simuCtx.getAclfNet();
		
	  	ContingencyAnalysis mscase = SimuObjectFactory.createContingencyAnalysis(SimuCtxType.ACLF_NETWORK, net);

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		algo.setNonDivergent(true);
		algo.setTolerance(0.001);
		
		mscase.analysis(algo, ContingencyAnalysisType.N2);
		
		System.out.println(ContingencyOutFunc.securityMargin(mscase));		
	}

	//@Test
	public void sample1Test() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);

		AclfNetwork net = simuCtx.getAclfNet();
		
	  	ContingencyAnalysis mscase = SimuObjectFactory.createContingencyAnalysis(SimuCtxType.ACLF_NETWORK, net);

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		algo.setNonDivergent(true);
		algo.setTolerance(0.001);
		
		int cnt = 0;
		for (Branch branch1 : net.getBranchList()) {
			if (branch1.isActive()) {
				for (Branch branch2 : net.getBranchList()) {
					if (!branch1.getId().equals(branch2.getId()) && branch2.isActive()) {
						String caseId = "[x]"+branch1.getId()+","+branch2.getId();
						String caseName = "Open Branch "+branch1.getId()+ ", " + branch2.getId();
						AclfStudyCase scase = SimuObjectFactory.createAclfStudyCase(caseId, caseName, ++cnt, mscase);
						
						ModificationXmlType mod = IpssXmlHelper.createTurnOffBranchRec(branch1);
						IpssXmlHelper.addTurnOffBranchRec(mod, branch2);
						scase.setModification(new Modification2ModelMapper().map2Model(mod));
						//scase.setModStringType(RemoteMessageType.IPSS_XML);
					}
				}
			}
		}	

		mscase.setLfTolerance(algo.getTolerance());
		algo.loadflow();
		algo.setInitBusVoltage(false);
	  	//IpssMapper mapper = new IpssXmlMapper();
		while (!mscase.getStudyCaseList().isEmpty()) {
			ChangeRecorder recorder = new ChangeRecorder(algo.getAclfNetwork());
			
			AclfStudyCase scase = (AclfStudyCase)mscase.getStudyCaseList().poll();
			
			scase.getModification().apply(net, msg);
/*			PluginSpringCtx.getModXml2NetMapper().map2Model(new IpssXmlParser()
					.parserModification(scase.getModificationString()), 
		  			algo.getAclfNetwork());
*/			
			scase.runLoadflow(algo, mscase);
	  		
			recorder.endRecording().apply();
		}
		
		System.out.println(ContingencyOutFunc.securityMargin(mscase));		
	}
}

