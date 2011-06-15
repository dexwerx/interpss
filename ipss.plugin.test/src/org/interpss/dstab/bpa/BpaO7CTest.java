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
import org.interpss.display.impl.AclfOut_PSSE.Format;
import org.interpss.dstab.ieeeModel.DStabTestSetupBase;
import org.interpss.dstab.output.TextSimuOutputHandler;
import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.interpss.numeric.NumericConstant;
import org.junit.Test;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
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
	/*Test data: 
	 * 07c_0615 : explicitly add switch shuntVar to compensate the un-planned shuntVar of BPA for BE type Bus
	 * [test data updated by Tony 06/15]
	 */
	//@Test
	public void sys2010_lfTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testData/bpa/07c_0615.dat")); 
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
		//System.out.println(AclfOutFunc.lf4Google(net));
		//get the genResult
		
		for(Bus b:net.getBusList()){
			AclfBus bus=(AclfBus) b;
			if(bus.isGen()){
				System.out.println(bus.getName()+", "+bus.getId()+" ,p= "+bus.getGenResults().getReal()+",q= "+bus.getGenResults().getImaginary());
			}
		}
		
		FileOutputStream out=new FileOutputStream(new File("d:/07c_2010_lfResult.txt"));
		out.write(AclfOutFunc.lfResultsPsseStyle(net, Format.GUI).getBytes());
		out.flush();
		out.close();
		assertTrue(Math.abs(net.getAclfBus("Bus1").getVoltageMag()-1.02484)<0.0001);
		AclfBranch bra= (AclfBranch) net.getBranchList().get(0);
		assertTrue(Math.abs(bra.powerFrom2To().getReal()-16.86)<0.001);
	}
	//@Test
	public void sys2010_noFaultTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile(IODMAdapter.NetType.DStabNet,
				new String[] { "testdata/bpa/07c_0615.dat", 
				               "testdata/bpa/07c_onlyMach_noSe.swi"}));//"testdata/bpa/07c_onlyMach.swi"
		//assertTrue(adapter.parseInputFile("testdata/bpa/07c.dat" ));
		DStabModelParser parser=(DStabModelParser) adapter.getModel();
		//AclfModelParser parser = (AclfModelParser)adapter.getModel();
		
		//parser.stdout();
		String xml=parser.toXmlDoc(false);
		FileOutputStream out=new FileOutputStream(new File("testdata/ieee_odm/07c_2010_OnlyMach_noSe0614.xml"));
		out.write(xml.getBytes());
		out.flush();
		out.close();
		
//		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
//		if (!new ODMDStabDataMapper(msg)
//					.map2Model(parser, simuCtx)) {
//			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
//			return;
//		}	
//		
//		DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
//	
//		
//		dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
//		dstabAlgo.setSimuStepSec(0.001);
//		dstabAlgo.setTotalSimuTimeSec(0.01);
//		
//		dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
//		if (dstabAlgo.getSolver().initialization()) {
//			System.out.println("Running DStab simulation ...");
//			dstabAlgo.performSimulation(msg);
//		}
				
	}
	
	/***************************************************
	 * test data:
	 * 1) only machine: 07c_2010_OnlyMach_0613.xml
	 * 2) machine and exciter: 07c_2010_Mach_Exc_0609.xml
	 * 3) machine and exciter, not consider saturation: 07c_2010_Mach_Exc_noSe0614.xml
	 * 14/06 Tony: with 07c_2010_Mach_Exc_noSe0614.xml, the initial machine angles are the same!
	 */
	@Test
	public void sys2010_XmlDstabtestCase() throws Exception {
		
		File file = new File("testData/ieee_odm/07c_2010_OnlyMach_noSe0614.xml");
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
			 
			//setDynamicEventData(net);
			// System.out.println(net.getMachine("Bus59-mach1").getMachData().toString());
			// System.out.println(net.getMachine("Bus56-mach1").getMachData().getXl());
			/* 
			 FileOutputStream out=new FileOutputStream(new File("d:/07c_2010_MachExc_noSe_netString.txt"));
				out.write(net.net2String().getBytes());
				out.flush();
				out.close();
			
			 */ 
		 
			DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
			dstabAlgo.setRefMachine(net.getMachine("Bus78-mach1"));
			dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
			dstabAlgo.setSimuStepSec(0.01);
			dstabAlgo.setTotalSimuTimeSec(1.0);
			
			// run load flow test case
			LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
			assertTrue(aclfAlgo.loadflow());
			System.out.println(AclfOutFunc.loadFlowSummary(net));
			//System.out.println(AclfOutFunc.lfResultsBusStyle(net));
				
			create3PFaultEvent(net, "Bus10", "HUIZHLZ1");

			//IpssLogger.getLogger().setLevel(Level.INFO);
			dstabAlgo.setSimuOutputHandler(new TextSimuOutputHandler());
			if (dstabAlgo.getSolver().initialization()) {
				System.out.println("Running DStab simulation ...");
				assertTrue(dstabAlgo.performSimulation(msg));
			}
		}
	}
	
	/************************************************
	 * lf test data:
	 * 1) 07c_2010_OnlyMach_lf.xml
	 * 
	 * Status:
	 * 06/12 Mike : Lf run can converge
	 */
	//@Test
	public void sys2010_XmlLftestCase() throws Exception {
		File file = new File("testData/ieee_odm/07c_2010_OnlyMach_lf.xml");
		AclfModelParser parser = ODMObjectFactory.createAclfModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));

			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
			if (!new ODMAclfDataMapper(msg).map2Model(parser, simuCtx)) {
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
	
	private void create3PFaultEvent(DStabilityNetwork net, String busId, String busName) {
		// define a bus fault event
		DynamicEvent event1 = DStabObjectFactory.createDEvent(
				"BusFault3P@"+busId, "Bus Fault 3P @"+busName, 
				DynamicEventType.BUS_FAULT, net);
		event1.setStartTimeSec(0.0);
		event1.setDurationSec(0.1);
		
		// define a 3P fault
		DStabBus faultBus = net.getDStabBus(busId);
		AcscBusFault fault = CoreObjectFactory.createAcscBusFault("Bus Fault 3P@"+busId, net);
  		fault.setAcscBus(faultBus);
		fault.setFaultCode(SimpleFaultCode.GROUND_3P);
		fault.setZLGFault(NumericConstant.SmallScZ);
		fault.setZLLFault(new Complex(0.0, 0.0));
		
		// add the fault to the event
		event1.setBusFault(fault);		
	}	
}
