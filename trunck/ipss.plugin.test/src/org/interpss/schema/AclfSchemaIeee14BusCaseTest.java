package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.BaseTestSetup;
import org.interpss.editor.mapper.RunForm2AlgorithmMapper;
import org.interpss.mapper.IpssXmlMapper;
import org.interpss.xml.IpssXmlParser;
import org.junit.Test;

import com.interpss.common.SpringAppContext;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.mapper.IpssMapper;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclfadj.AclfAdjNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;
import com.interpss.simu.multicase.MultiStudyCase;
import com.interpss.simu.multicase.aclf.AclfStudyCase;

public class AclfSchemaIeee14BusCaseTest extends BaseTestSetup {
	@Test
	public void runSingleAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_ADJ_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfAdjNet());

		File xmlFile = new File("testData/xml/RunAclfCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println(IpssXmlParser.toXmlDocString(parser.getRootDoc()));
  		//System.out.println(parser.toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == RunStudyCaseXmlType.AnalysisRunType.RUN_ACLF);
  		
	  	MultiStudyCase mscase = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_ADJ_NETWORK);
	  	int cnt = 0;
  		double i = 0.0;
	  	for ( AclfStudyCaseXmlType aclfCase : parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCaseArray()) {
			AclfAdjNetwork net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(netStr);
			net.rebuildLookupTable();
	  		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, SpringAppContext.getIpssMsgHub());
		  	IpssMapper mapper = new RunForm2AlgorithmMapper();
	  		mapper.mapping(aclfCase.getAclfAlgorithm(), algo, AclfAlgorithmXmlType.class);
	  	
	  		assertTrue(algo.getMaxIterations() == 20);
	  		assertTrue(algo.getTolerance() == 1.0E-4);
	  		assertTrue(algo.loadflow());
	  		i = net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva());
	  		
	  		AclfStudyCase scase = SimuObjectFactory.createAclfStudyCase(aclfCase.getRecId(), aclfCase.getRecName(), ++cnt, mscase);
	  		scase.setNetModelString(SerializeEMFObjectUtil.saveModel(net));
	  	}
  		assertTrue(mscase.getStudyCase(1) != null);
  		assertTrue(mscase.getStudyCase(2) != null);
  		assertTrue(mscase.getStudyCase(3) != null);
  		
//  		System.out.println(net.net2String());
  		AclfAdjNetwork net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(1).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva())-i) < 1.0E-5);

  		net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(2).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva())-i) < 1.0E-5);

  		net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(3).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva())-i) < 1.0E-5);
	}			

	@Test
	public void runDefaultAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_ADJ_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfAdjNet());

		File xmlFile = new File("testData/xml/RunAclfCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == RunStudyCaseXmlType.AnalysisRunType.RUN_ACLF);
  		
	  	MultiStudyCase mscase = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_ADJ_NETWORK);
	  	int cnt = 0;
  		double i = 0.0;
	  	for ( AclfStudyCaseXmlType aclfCase : parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCaseArray()) {
			AclfAdjNetwork net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(netStr);
			net.rebuildLookupTable();
	  		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, SpringAppContext.getIpssMsgHub());
		  	IpssMapper mapper = new RunForm2AlgorithmMapper();

		  	if (aclfCase.getAclfAlgorithm() == null) 
		  		aclfCase.setAclfAlgorithm(parser.getRunAclfStudyCase().getDefaultAclfAlgorithm());
		  	
		  	mapper.mapping(aclfCase.getAclfAlgorithm(), algo, AclfAlgorithmXmlType.class);
	  	
	  		assertTrue(algo.getMaxIterations() == 20);
	  		assertTrue(algo.getTolerance() == 1.0E-4);
	  		assertTrue(algo.loadflow());
	  		i = net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva());
	  		
	  		AclfStudyCase scase = SimuObjectFactory.createAclfStudyCase(aclfCase.getRecId(), aclfCase.getRecName(), ++cnt, mscase);
	  		scase.setNetModelString(SerializeEMFObjectUtil.saveModel(net));
	  	}
  		assertTrue(mscase.getStudyCase(1) != null);
  		assertTrue(mscase.getStudyCase(2) != null);
  		assertTrue(mscase.getStudyCase(3) != null);
  		
//  		System.out.println(net.net2String());
  		AclfAdjNetwork net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(1).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva())-i) < 1.0E-5);

  		net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(2).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva())-i) < 1.0E-5);

  		net = (AclfAdjNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(3).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(UnitType.PU, net.getBaseKva())-i) < 1.0E-5);
	}			

	@Test
	public void runSingleAclfCaseModificationTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_ADJ_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
  		assertTrue((net.getBusList().size() == 14 && net.getBranchList().size() == 20));

		File xmlFile = new File("testData/xml/RunAclfCaseModification.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

  		AclfStudyCaseXmlType aclfCase = parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCaseArray(0);
  			
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, SpringAppContext.getIpssMsgHub());
	  	// modification of the study case also applied
	  	IpssMapper mapper = new IpssXmlMapper();
	  	mapper.mapping(aclfCase.getModification(), net, ModificationXmlType.class);
	  	mapper.mapping(aclfCase.getAclfAlgorithm(), algo, AclfAlgorithmXmlType.class);
	  	
	  	assertTrue(!net.getBranch("0010->0009(1)").isActive());
	  	
	  	// load increased by 0.1 pu
	  	assertTrue(net.getAclfBus("0014").getLoadCode() == AclfLoadCode.EXPONENTIAL);
	  	
	  	// branch Z increase by 10%
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").getZ().getImaginary()-0.20912*1.1) < 1.0E-5);

	  	assertTrue(algo.loadflow());
	}
}
