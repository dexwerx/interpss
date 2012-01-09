package org.interpss.geditor;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.interpss.numeric.datatype.ComplexFunc;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class AclfAdjCaseTest  extends PluginTestSetup {
	@Test
	public void AclfAdj2BusFuncLoadCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusFuncLoad.ipss", simuCtx);

		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.1153)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-1.03063)<0.0001);
	}			

	@Test
	public void AclfAdj2BusPQLimit1Case() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusPQLimit1.ipss", simuCtx);

		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.1153)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-1.03063)<0.0001);
	}		
	
	@Test
	public void AclfAdj2BusPQLimit2Case() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusPQLimit2.ipss", simuCtx);

		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.1153)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-1.03063)<0.0001);
	}			

	@Test
	public void AclfAdj2BusPVLimit1Case() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusPVLimit1.ipss", simuCtx);

		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.5414)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() + 0.73144)<0.0001);
	}			

	@Test
	public void AclfAdj2BusPVLimit2Case() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusPVLimit2.ipss", simuCtx);

		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.5414)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() + 0.73144)<0.0001);
	}			

	@Test
	public void AclfAdj2BusTapControlDiscreteStepsCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusTapControlDiscreteSteps.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()- 1.11529)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 1.03059)<0.0001);
	}			

	@Test
	public void AclfAdj2BusTapControlNoViolationCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusTapControlNoViolation.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.11529)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 1.03059)<0.0001);
	}			

	@Test
	public void AclfAdj2BusTapControlViolationCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj2BusTapControlViolation.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.11529)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 1.03059)<0.0001);
	}			

	@Test
	public void AclfAdj3BusPsXfrCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusPsXfr.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.20231)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.61422)<0.0001);
	}			

	@Test
	public void AclfAdj3BusPsXfrPControlFromSideCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusPsXfrPControlFromSide.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.20231)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.61422)<0.0001);
	}			

	@Test
	public void AclfAdj3BusPsXfrPControlFromSideViolationCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusPsXfrPControlFromSideViolation.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.20231)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.61422)<0.0001);
	}			

	@Test
	public void AclfAdj3BusPsXfrPControlToSideCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusPsXfrPControlToSide.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.20231)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.61422)<0.0001);
	}			

	@Test
	public void AclfAdj3BusReQBusNoViolationCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusReQBusNoViolation.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.5777)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.9554)<0.0001);
	}			

	@Test
	public void AclfAdj3BusReQBusViolationCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusReQBusViolation.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.5777)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.9554)<0.0001);
	}			

	@Test
	public void AclfAdj3BusReQMvarFlowFromSideCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusReQMvarFlowFromSide.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.5777)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.9554)<0.0001);
	}			

	@Test
	public void AclfAdj3BusReQMvarFlowToSideCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusReQMvarFlowToSide.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(Type.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.5777)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.9554)<0.0001);
	}			

	@Test
	public void AclfAdj3BusTapControlMvarFlowFromSideCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusTapControlMvarFlowFromSide.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.54776)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.49551)<0.0001);
	}
	@Test
	public void AclfAdj3BusTapControlMvarFlowToSideCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusTapControlMvarFlowToSide.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.54776)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.49551)<0.0001);
	}
	@Test
	public void AclfAdj3BusTapControlMvarFlowToSideTapCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclfadj/AclfAdj3BusTapControlMvarFlowToSideTap.ipss", simuCtx);
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = (AclfSwingBus)swingBus.getAdapter(AclfSwingBus.class);
		System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.54776)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() - 0.49551)<0.0001);
	}
}
