package org.interpss.dstab.bpa;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import org.apache.commons.math.complex.Complex;
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
import org.interpss.numeric.NumericConstant;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.junit.Test;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabBus;
import com.interpss.dstab.DStabObjectFactory;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.devent.DynamicEvent;
import com.interpss.dstab.devent.DynamicEventType;
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
//		assertTrue(adapter.parseInputFile(IODMAdapter.NetType.DStabNet,
//				new String[] { "testdata/bpa/07c.dat", 
//				               "testdata/bpa/07c_onlyMach_xq.swi"}));//"testdata/bpa/07c_onlyMach.swi"
		assertTrue(adapter.parseInputFile("testdata/bpa/07c.dat" ));
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		
		//parser.stdout();
		String xml=parser.toXmlDoc(false);
		FileOutputStream out=new FileOutputStream(new File("testdata/ieee_odm/07c_2010_OnlyMach_lf.xml"));
		out.write(xml.getBytes());
		out.flush();
		out.close();
		/*
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
		*/		
	}
	@Test
	public void sys2010_XmlDstabtestCase() throws Exception {
		/*
		 * test data:
		 * 1) only machine: 07c_2010_OnlyMach.xml
		 * 2) machine and exciter: 07c_2010_Mach_Exc_0609.xml
		 * 
		 */
		
		File file = new File("testData/ieee_odm/07c_2010_OnlyMach.xml");
		DStabModelParser parser = ODMObjectFactory.createDStabModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));

			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
			if (!new ODMDStabDataMapper(msg)
						.map2Model(parser, simuCtx)) {
				System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
				return;
			}
			 DStabilityNetwork net = simuCtx.getDStabilityNet();
			 assertTrue(net.checkData());
			 assertTrue(net.getBranchList().size()==308);
			 assertTrue(net.getBusList().size()==141);
			 
			 //System.out.println(net.net2String());
			 /*
			 //setDynamicEventData(net);
			 FileOutputStream out=new FileOutputStream(new File("d:/07c_2010_OnlyMach_netString.txt"));
				out.write(net.net2String().getBytes());
				out.flush();
				out.close();
			 
			  
			 System.out.println(AclfOutFunc.loadFlowSummary(net));
			 */
			 DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
				/*
				 * Run Loadflow
				 */
				LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
				aclfAlgo.loadflow();
				System.out.println(AclfOutFunc.loadFlowSummary(net));
				
				
				dstabAlgo.setRefMachine(net.getMachine("Bus78-mach1"));
				dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
				dstabAlgo.setSimuStepSec(0.01);
				dstabAlgo.setTotalSimuTimeSec(0.02);
				IpssLogger.getLogger().setLevel(Level.INFO);
				dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
				if (dstabAlgo.getSolver().initialization()) {
					System.out.println("Running DStab simulation ...");
					assertTrue(dstabAlgo.performSimulation(msg));
				}		
		}
      
	}
	
	@Test
	public void sys2010_XmlLftestCase() throws Exception {
		/*
		 * lf test data:
		 * 1) 07c_2010_OnlyMach_lf.xml
		 */
		
		File file = new File("testData/ieee_odm/07c_2010_OnlyMach_lf.xml");
		DStabModelParser parser = ODMObjectFactory.createDStabModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));

			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
			if (!new ODMAclfDataMapper(msg)
						.map2Model(parser, simuCtx)) {
				System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
				return;
			}
			
			 AclfNetwork net=simuCtx.getAclfNet();
			 assertTrue(net.checkData());
			 assertTrue(net.getBranchList().size()==308);
			 assertTrue(net.getBusList().size()==141);
			 //System.out.println(net.net2String());
			/*
			 FileOutputStream out=new FileOutputStream(new File("d:/07c_2010_OnlyMach_netString.txt"));
				out.write(net.net2String().getBytes());
				out.flush();
				out.close();
			 */
			  
				/*
				 * Run Loadflow
				 */
			
				LoadflowAlgorithm aclfAlgo =CoreObjectFactory.createLoadflowAlgorithm(net);
				aclfAlgo.loadflow();
				System.out.println(AclfOutFunc.loadFlowSummary(net));
		}
      
	}
	private void setDynamicEventData(DStabilityNetwork net) {
		// define a bus fault event
		DynamicEvent event1 = DStabObjectFactory.createDEvent("BusFault3P@Bus3", "Bus Fault 3P@Bus3", 
				DynamicEventType.BUS_FAULT, net);
		event1.setStartTimeSec(0.5);
		event1.setDurationSec(0.1);
		
		DStabBus faultBus = net.getDStabBus("Bus3");
		AcscBusFault fault = CoreObjectFactory.createAcscBusFault("Bus Fault 3P@Bus3", net);
  		fault.setAcscBus(faultBus);
		fault.setFaultCode(SimpleFaultCode.GROUND_3P);
		fault.setZLGFault(NumericConstant.SmallScZ);
		fault.setZLLFault(new Complex(0.0, 0.0));
		event1.setBusFault(fault);		
	}	
}
