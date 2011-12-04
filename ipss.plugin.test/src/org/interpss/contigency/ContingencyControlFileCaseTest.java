package org.interpss.contigency;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginObjectFactory;
import org.interpss.PluginTestSetup;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.custom.run.psseCon.ContingencyFileParser;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.InterPSSXmlType;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;

public class ContingencyControlFileCaseTest extends PluginTestSetup {
	@Test
	public void cr113BusSwitchingModelCaseTest() throws Exception {
		IpssFileAdapter adapter = PluginObjectFactory.getFileAdapter(
				IpssFileAdapter.FileFormat.PSSE, IpssFileAdapter.Version.PSSE_30);
		SimuContext simuCtx = adapter.load("testData/edispatch/CR113Bus.raw");
 		//System.out.println(simuCtx.getAclfNet().net2String());

 		//assertTrue(simuCtx.getAclfNet().getSwitchBreakBranch("60701", null) != null);
 		//assertTrue(simuCtx.getAclfNet().getSwitchBreakBranch("60702", null) != null);
	}

	//@Test
	public void cr113BusCaseTest() throws Exception {
		IpssFileAdapter adapter = PluginObjectFactory.getFileAdapter(
				IpssFileAdapter.FileFormat.PSSE, IpssFileAdapter.Version.PSSE_30);
		SimuContext simuCtx = adapter.load("testData/edispatch/CR113Bus.raw");
// 		System.out.println(simuCtx.getAclfNet().net2String());

  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfNet());

		File xmlFile = new File("testData/edispatch/RunCR113Bus.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	for ( AclfStudyCaseXmlType aclfCase : parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase()) {
	  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(netStr);
			net.rebuildLookupTable();
			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
			PluginSpringFactory.getModXml2NetMapper().map2Model(aclfCase.getModification(), net);
		  	
		  	PluginSpringFactory.getXml2LfAlgorithmMapper()
		  		.map2Model(parser.getRunAclfStudyCase().getDefaultAclfAlgorithm(),	algo);
	  	
	  		assertTrue(algo.loadflow());
	  	}	
	}
	
	@Test
	public void simpleCaseTestAclfRun() throws Exception {
		InterPSSXmlType ipssDoc = ContingencyFileParser.parseControlFile(
				AnalysisRunDataType.RUN_ACLF, "testData/edispatch/contingency.con");
/*
CONTINGENCY LOSEWESTBIGT
OPEN LINE FROM BUS 3004 TO BUS 152 CIRCUIT 1
END
CONTINGENCY LOSEEASTBIGT
OPEN LINE FROM BUS 151 TO BUS 201 CIRCUIT 1
END
CONTINGENCY LOSE2LINESWE
OPEN LINE FROM BUS 3004 TO BUS 152 CIRCUIT 1
OPEN LINE FROM BUS 3006 TO BUS 153 CIRCUIT 1
END
CONTINGENCY LOSE2LINEEA
OPEN LINE FROM BUS 151 TO BUS 201 CIRCUIT 1
OPEN LINE FROM BUS 152 TO BUS 202 CIRCUIT 1
END
END
 */
		assertTrue(ipssDoc.getRunStudyCase().getStandardRun().getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase().size() == 4);
		
		AclfStudyCaseXmlType scase = ipssDoc.getRunStudyCase().getStandardRun().getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase().get(0);
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getFromBusId().equals("3004"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getToBusId().equals("152"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getCircuitNumber().equals("1"));

		scase = ipssDoc.getRunStudyCase().getStandardRun().getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase().get(2);
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getFromBusId().equals("3004"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getToBusId().equals("152"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getCircuitNumber().equals("1"));

		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(1).getFromBusId().equals("3006"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(1).getToBusId().equals("153"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(1).getCircuitNumber().equals("1"));
		
		//System.out.println(ipssDoc.toString());
	}

	@Test
	public void simpleCaseTestContingency() throws Exception {
		InterPSSXmlType ipssDoc = ContingencyFileParser.parseControlFile(
				AnalysisRunDataType.CONTINGENCY_ANALYSIS, "testData/edispatch/contingency.con");
/*
CONTINGENCY LOSEWESTBIGT
OPEN LINE FROM BUS 3004 TO BUS 152 CIRCUIT 1
END
CONTINGENCY LOSEEASTBIGT
OPEN LINE FROM BUS 151 TO BUS 201 CIRCUIT 1
END
CONTINGENCY LOSE2LINESWE
OPEN LINE FROM BUS 3004 TO BUS 152 CIRCUIT 1
OPEN LINE FROM BUS 3006 TO BUS 153 CIRCUIT 1
END
CONTINGENCY LOSE2LINEEA
OPEN LINE FROM BUS 151 TO BUS 201 CIRCUIT 1
OPEN LINE FROM BUS 152 TO BUS 202 CIRCUIT 1
END
END
 */
		assertTrue(ipssDoc.getRunStudyCase().getContingencyAnalysis().getAclfStudyCaseList().getAclfStudyCase().size() == 4);
		
		AclfStudyCaseXmlType scase = ipssDoc.getRunStudyCase().getContingencyAnalysis().getAclfStudyCaseList().getAclfStudyCase().get(0);
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getFromBusId().equals("3004"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getToBusId().equals("152"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getCircuitNumber().equals("1"));

		scase = ipssDoc.getRunStudyCase().getContingencyAnalysis().getAclfStudyCaseList().getAclfStudyCase().get(2);
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getFromBusId().equals("3004"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getToBusId().equals("152"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(0).getCircuitNumber().equals("1"));

		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(1).getFromBusId().equals("3006"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(1).getToBusId().equals("153"));
		assertTrue(scase.getModification().getBranchChangeRecList().getBranchChangeRec().get(1).getCircuitNumber().equals("1"));
		
		//System.out.println(ipssDoc.toString());
	}
}
