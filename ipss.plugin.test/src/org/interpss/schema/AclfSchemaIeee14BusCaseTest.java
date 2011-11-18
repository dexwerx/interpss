package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginTestSetup;
import org.interpss.numeric.datatype.Unit.Type;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.MultiStudyCase;
import com.interpss.simu.multicase.aclf.AclfStudyCase;

public class AclfSchemaIeee14BusCaseTest extends PluginTestSetup {
	@Test
	public void runSingleAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfNet());

		File xmlFile = new File("testData/xml/RunAclfCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println(IpssXmlParser.toXmlDocString(parser.getRootDoc()));
  		//System.out.println(parser.toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == AnalysisRunDataType.RUN_ACLF);
  		
	  	MultiStudyCase mscase = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_NETWORK);
	  	int cnt = 0;
  		double i = 0.0;
	  	for ( AclfStudyCaseXmlType aclfCase : parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase()) {
	  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(netStr);
			net.rebuildLookupTable();
			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
			PluginSpringCtx.getXml2LfAlgorithmMapper()
					.map2Model(aclfCase.getAclfAlgorithm(), algo);
	  	
	  		assertTrue(algo.getMaxIterations() == 20);
	  		assertTrue(algo.getTolerance() == 1.0E-4);
	  		assertTrue(algo.loadflow());
	  		i = net.getAclfBranch("0004->0007(1)").current(Type.PU);
	  		
	  		AclfStudyCase scase = SimuObjectFactory.createAclfStudyCase(aclfCase.getRecId(), aclfCase.getRecName(), ++cnt, mscase);
	  		scase.setNetModelString(SerializeEMFObjectUtil.saveModel(net));
	  	}
  		assertTrue(mscase.getStudyCase(1) != null);
  		assertTrue(mscase.getStudyCase(2) != null);
  		assertTrue(mscase.getStudyCase(3) != null);
  		
//  		System.out.println(net.net2String());
  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(1).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(Type.PU)-i) < 1.0E-5);

  		net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(2).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(Type.PU)-i) < 1.0E-5);

  		net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(3).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(Type.PU)-i) < 1.0E-5);
	}			

	@Test
	public void runDefaultAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfNet());

		File xmlFile = new File("testData/xml/RunAclfCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == AnalysisRunDataType.RUN_ACLF);
  		
	  	MultiStudyCase mscase = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_NETWORK);
	  	int cnt = 0;
  		double i = 0.0;
	  	for ( AclfStudyCaseXmlType aclfCase : parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase()) {
	  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(netStr);
			net.rebuildLookupTable();
			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);

		  	if (aclfCase.getAclfAlgorithm() == null) 
		  		aclfCase.setAclfAlgorithm(parser.getRunAclfStudyCase().getDefaultAclfAlgorithm());
		  	
		  	PluginSpringCtx.getXml2LfAlgorithmMapper()
		  			.map2Model(aclfCase.getAclfAlgorithm(), algo);
	  	
	  		assertTrue(algo.getMaxIterations() == 20);
	  		assertTrue(algo.getTolerance() == 1.0E-4);
	  		assertTrue(algo.loadflow());
	  		i = net.getAclfBranch("0004->0007(1)").current(Type.PU);
	  		
	  		AclfStudyCase scase = SimuObjectFactory.createAclfStudyCase(aclfCase.getRecId(), aclfCase.getRecName(), ++cnt, mscase);
	  		scase.setNetModelString(SerializeEMFObjectUtil.saveModel(net));
	  	}
  		assertTrue(mscase.getStudyCase(1) != null);
  		assertTrue(mscase.getStudyCase(2) != null);
  		assertTrue(mscase.getStudyCase(3) != null);
  		
//  		System.out.println(net.net2String());
  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(1).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(Type.PU)-i) < 1.0E-5);

  		net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(2).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(Type.PU)-i) < 1.0E-5);

  		net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(3).getNetModelString());
  		net.rebuildLookupTable();
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").current(Type.PU)-i) < 1.0E-5);
	}			

	@Test
	public void runSingleAclfCaseModificationTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
  		assertTrue((net.getBusList().size() == 14 && net.getBranchList().size() == 20));

		File xmlFile = new File("testData/xml/RunAclfCaseModification.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

  		AclfStudyCaseXmlType aclfCase = parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase().get(0);
  			
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	// modification of the study case also applied
	  	//IpssMapper mapper = new IpssXmlMapper();
	  	PluginSpringCtx.getModXml2NetMapper()
	  			.map2Model(aclfCase.getModification(), net);
	  	
	  	PluginSpringCtx.getXml2LfAlgorithmMapper()
	  			.map2Model(aclfCase.getAclfAlgorithm(), algo);
	  	
	  	assertTrue(!net.getBranch("0010->0009(1)").isActive());
	  	
	  	// load increased by 0.1 pu
	  	assertTrue(net.getAclfBus("0014").getLoadCode() == AclfLoadCode.EXPONENTIAL);
	  	
	  	// branch Z increase by 10%
	  	assertTrue(Math.abs(net.getAclfBranch("0004->0007(1)").getZ().getImaginary()-0.20912*1.1) < 1.0E-5);

	  	assertTrue(algo.loadflow());
	}
}
