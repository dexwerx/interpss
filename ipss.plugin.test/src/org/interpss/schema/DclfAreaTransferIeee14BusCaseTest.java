package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginTestSetup;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.AreaTransferAnalysisXmlType;
import org.interpss.xml.schema.BranchRecXmlType;
import org.interpss.xml.schema.DclfStudyCaseXmlType;
import org.interpss.xml.schema.SenAnalysisBusRecXmlType;
import org.junit.Test;

import com.interpss.SimuObjectFactory;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.DclfObjectFactory;
import com.interpss.core.dclf.DclfAlgorithm;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.spring.CoreCommonSpringFactory;

public class DclfAreaTransferIeee14BusCaseTest extends PluginTestSetup {
	@Test
	public void runSingleAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		File xmlFile = new File("testData/xml/RunAreaTransferCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == AnalysisRunDataType.RUN_DCLF);
		
	  	IPSSMsgHub msg = CoreCommonSpringFactory.getIpssMsgHub();
	  	
		DclfAlgorithm algo = DclfObjectFactory.createDclfAlgorithm(simuCtx.getAclfNet());
		assertTrue(algo.checkCondition());
			
		DclfStudyCaseXmlType dclfCase = parser.getRunDclfStudyCase().getDclfStudyCaseList().getDclfStudyCase().get(0);

		for (AreaTransferAnalysisXmlType atFactor : dclfCase.getAreaTransferAnalysis()) {
			algo.getInjectBusList().clear();
			for (SenAnalysisBusRecXmlType bus :  atFactor.getInjectBusList().getInjectBus()){
				//algo.calculateSensitivity(SenAnalysisType.PANGLE, bus.getBusId());
				algo.addInjectBus(bus.getBusId(), bus.getPercent());
			}
			algo.getWithdrawBusList().clear();
			for (SenAnalysisBusRecXmlType bus :  atFactor.getWithdrawBusList().getWithdrawBus()){
				//algo.calculateSensitivity(SenAnalysisType.PANGLE, bus.getBusId());
				algo.addWithdrawBus(bus.getBusId(), bus.getPercent());
			}
			
			for (BranchRecXmlType branch : atFactor.getBranch()) {
				double f = algo.getAreaTransferFactor(branch.getFromBusId(), branch.getToBusId(), "1");
				System.out.println("ATFactor: " + branch.getFromBusId() + "->" + branch.getToBusId() + " " + f);
			}
		}
	}

	@Test
	public void runDSL_SingleAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(simuCtx.getAclfNet());
		algoDsl.runAnalysis("testData/xml/RunAreaTransferCase.xml");
		DclfAlgorithm algo = algoDsl.getAlgorithm();
			
		File xmlFile = new File("testData/xml/RunAreaTransferCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());
	  	IPSSMsgHub msg = CoreCommonSpringFactory.getIpssMsgHub();
		DclfStudyCaseXmlType dclfCase = parser.getRunDclfStudyCase().getDclfStudyCaseList().getDclfStudyCase().get(0);
		for (AreaTransferAnalysisXmlType atFactor : dclfCase.getAreaTransferAnalysis()) {
			for (BranchRecXmlType branch : atFactor.getBranch()) {
				double f = algo.getAreaTransferFactor(branch.getFromBusId(), branch.getToBusId(), "1");
				System.out.println("ATFactor: " + branch.getFromBusId() + "->" + branch.getToBusId() + " " + f);
			}
		}
	}
}
