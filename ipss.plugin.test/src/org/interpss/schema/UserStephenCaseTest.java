package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginObjectFactory;
import org.interpss.PluginTestSetup;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.MultiStudyCase;
import com.interpss.simu.multicase.aclf.AclfStudyCase;

public class UserStephenCaseTest extends PluginTestSetup {
	@Test
	public void caseTest1() throws Exception {
		IpssFileAdapter adapter = PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF);
		SimuContext simuCtx = adapter.load("testData/ieee_format/ieee14.ieee");
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfNet());

		File xmlFile = new File("testData/xml/usertest/mytest.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());
  		
	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == AnalysisRunDataType.RUN_ACLF);
  		
	  	MultiStudyCase mscase = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_NETWORK);
	  	int cnt = 0;
	  	for ( AclfStudyCaseXmlType aclfCase : parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase()) {
	  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(netStr);
			net.rebuildLookupTable();
			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		  	//IpssMapper mapper = new IpssXmlMapper();
	  		
		  	if (aclfCase.getAclfAlgorithm() == null) 
		  		aclfCase.setAclfAlgorithm(parser.getRunAclfStudyCase().getDefaultAclfAlgorithm());
		  	if (aclfCase.getModification() != null)
		  		PluginSpringCtx.getModXml2NetMapper()
		  				.map2Model(aclfCase.getModification(), net);
		  	
		  	PluginSpringCtx.getXml2LfAlgorithmMapper()
		  			.map2Model(aclfCase.getAclfAlgorithm(), algo);
	  	
	  		assertTrue(algo.loadflow());

	  		AclfStudyCase scase = SimuObjectFactory.createAclfStudyCase(aclfCase.getRecId(), aclfCase.getRecName(), ++cnt, mscase);
	  		scase.setNetModelString(SerializeEMFObjectUtil.saveModel(net));
	  	}
	  	
  		assertTrue(mscase.getStudyCase(1) != null);
  		assertTrue(mscase.getStudyCase(2) != null);
  		
//  		System.out.println(net.net2String());
  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(1).getNetModelString());
  		net.rebuildLookupTable();
  		AclfBus busIncreased = net.getAclfBus("Bus12");
  		
  		net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(2).getNetModelString());
  		net.rebuildLookupTable();
  		AclfBus busBase = net.getAclfBus("Bus12");

  		double dP = busIncreased.getLoadP() - busBase.getLoadP();
  		double dQ = busIncreased.getLoadQ() - busBase.getLoadQ();
  		assertTrue(Math.abs(dP - busBase.getLoadP()*0.1) < 0.00001);
  		assertTrue(Math.abs(dQ - busBase.getLoadQ()*0.1) < 0.00001);
	}			

	@Test
	public void caseTest2() throws Exception {
		IpssFileAdapter adapter = PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF);
		SimuContext simuCtx = adapter.load("testData/ieee_format/ieee14.ieee");
  		// save net to a String
  		String netStr = SerializeEMFObjectUtil.saveModel(simuCtx.getAclfNet());

		File xmlFile = new File("testData/xml/usertest/N-1.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

  		assertTrue(parser.getRunStudyCase().getAnalysisRunType() == AnalysisRunDataType.RUN_ACLF);
  		
	  	MultiStudyCase mscase = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_NETWORK);
	  	int cnt = 0;
	  	for ( AclfStudyCaseXmlType aclfCase : parser.getRunAclfStudyCase().getAclfStudyCaseList().getAclfStudyCase()) {
	  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(netStr);
			net.rebuildLookupTable();
			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		  	//IpssMapper mapper = new IpssXmlMapper();

		  	if (aclfCase.getModification() != null)
		  		PluginSpringCtx.getModXml2NetMapper()
		  				.map2Model(aclfCase.getModification(), net);
		  	if (aclfCase.getAclfAlgorithm() == null) 
		  		aclfCase.setAclfAlgorithm(parser.getRunAclfStudyCase().getDefaultAclfAlgorithm());
		  	
		  	PluginSpringCtx.getXml2LfAlgorithmMapper()
		  			.map2Model(aclfCase.getAclfAlgorithm(), algo);
	  		
		  	algo.setNonDivergent(true);
	  		//assertTrue(algo.loadflow());
	  	
	  		AclfStudyCase scase = SimuObjectFactory.createAclfStudyCase(aclfCase.getRecId(), aclfCase.getRecName(), ++cnt, mscase);
	  		scase.setNetModelString(SerializeEMFObjectUtil.saveModel(net));
	  	}

  		assertTrue(mscase.getStudyCase(1) != null);
  		assertTrue(mscase.getStudyCase(2) != null);

  		AclfNetwork net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(1).getNetModelString());
  		net.rebuildLookupTable();
  		assertTrue(net.getBranch("Bus1", "Bus2").isActive() == false);
  		
  		net = (AclfNetwork)SerializeEMFObjectUtil.loadModel(mscase.getStudyCase(2).getNetModelString());
  		net.rebuildLookupTable();
  		assertTrue(net.getBranch("Bus1", "Bus5").isActive() == false);
	}			
}
