package org.interpss.dstab.bpa;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.display.AclfOutFunc;
import org.interpss.dstab.ieeeModel.DStabTestSetupBase;
import org.interpss.dstab.output.TextSimuOutputHandler;
import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class BpaO7CTest extends DStabTestSetupBase{
	//@Test
	public void sys2011_lfTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testData/bpa/07c-dc2load.dat")); 
		AclfModelParser parser=(AclfModelParser) adapter.getModel();
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		if (!new ODMAclfDataMapper(msg)
					.map2Model(parser, simuCtx)) {
			  System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			  return;
	    }
		AclfNetwork net=simuCtx.getAclfNet();
		System.out.print("branch num="+net.getBranchList().size());
		System.out.print("bus num="+net.getBusList().size());
		assertTrue(net.getBranchList().size()==707);
		assertTrue(net.getBusList().size()==536);
		
		LoadflowAlgorithm  algo=CoreObjectFactory.createLoadflowAlgorithm(net);
		net.accept(algo);
		System.out.println(AclfOutFunc.loadFlowSummary(net));
		
		
	    
	}
	//@Test
	public void sys2010_lfTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testData/bpa/07c.dat")); 
		AclfModelParser parser=(AclfModelParser) adapter.getModel();
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		if (!new ODMAclfDataMapper(msg)
					.map2Model(parser, simuCtx)) {
			  System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			  return;
	    }
		AclfNetwork net=simuCtx.getAclfNet();
		System.out.print("branch num="+net.getBranchList().size());
		System.out.print("bus num="+net.getBusList().size());
		//assertTrue(net.getBranchList().size()==215);
		assertTrue(net.getBusList().size()==141);
		
		LoadflowAlgorithm  algo=CoreObjectFactory.createLoadflowAlgorithm(net);
		net.accept(algo);
		System.out.println(AclfOutFunc.loadFlowSummary(net));
		assertTrue(Math.abs(net.getAclfBus("Bus1").getVoltageMag()-1.02484)<0.0001);
		AclfBranch bra= (AclfBranch) net.getBranchList().get(0);
		assertTrue(Math.abs(bra.powerFrom2To().getReal()-16.86)<0.001);
	}
	//@Test
	public void sys2010_noFaultTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile(IODMAdapter.NetType.DStabNet,
				new String[] { "testdata/bpa/07c.dat", 
				               "testdata/bpa/07c_onlyMach.swi"}));
		
		DStabModelParser parser = (DStabModelParser)adapter.getModel();
		
//		parser.stdout();
//		String xml=parser.toXmlDoc(false);
//		FileOutputStream out=new FileOutputStream(new File("07c_2010_BPA_ODM_0608.xml"));
//		out.write(xml.getBytes());
//		out.flush();
//		out.close();
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
		if (!new ODMDStabDataMapper(msg)
					.map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			return;
		}	
		
		DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
		
		dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
		dstabAlgo.setSimuStepSec(0.001);
		dstabAlgo.setTotalSimuTimeSec(0.01);
		
		dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
		if (dstabAlgo.getSolver().initialization()) {
			System.out.println("Running DStab simulation ...");
			dstabAlgo.performSimulation(msg);
		}		
	}
	@Test
	public void testCase() throws Exception {
		File file = new File("testData/ieee_odm/07c_2010_BPA_ODM_0608.xml");
		DStabModelParser parser = ODMObjectFactory.createDStabModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));

			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
			if (!new ODMDStabDataMapper(msg)
						.map2Model(parser, simuCtx)) {
				System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
				return;
			}
			 DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
				
				dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
				dstabAlgo.setSimuStepSec(0.001);
				dstabAlgo.setTotalSimuTimeSec(0.01);
				
				dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
				if (dstabAlgo.getSolver().initialization()) {
					System.out.println("Running DStab simulation ...");
					dstabAlgo.performSimulation(msg);
				}		
		}
      
	}
}
