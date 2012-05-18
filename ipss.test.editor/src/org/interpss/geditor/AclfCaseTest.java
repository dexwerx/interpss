package org.interpss.geditor;

import static org.junit.Assert.assertTrue;

import org.interpss.EditorTestSetup;
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

public class AclfCaseTest  extends EditorTestSetup {
	@Test
	public void AclfNegativeBranchXCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfNegativeBranchX.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.1153)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-1.03063)<0.0001);
	}			

	@Test
	public void AclfSimple2BusCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.1153)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-1.03063)<0.0001);
	}			

	@Test
	public void AclfSimple2BusCapacitorCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusCapacitor.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.07225)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 0.54343)<0.0001);
	}

	@Test
	public void AclfSimple2BusConstILoadCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusConstILoad.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.95018)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 0.85852)<0.0001);
	}
	
	@Test
	public void AclfSimple2BusConstZLoadCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusConstZLoad.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-0.84498)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 0.75283)<0.0001);
	}
	
	@Test
	public void AclfSimple2BusPQLoadCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusPQLoad.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.11529)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 1.03059)<0.0001);
	}
	
	@Test
	public void AclfSimple2BusPsXfrCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusPsXfr.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.11526)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 1.03056)<0.0001);
	}
	
	@Test
	public void AclfSimple2BusPVLoadCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusPVLoad.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()- 1.10463)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary() + 0.93403)<0.0001);
	}
	
	@Test
	public void AclfSimple2BusXfrCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusXfr.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.11529)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 1.03059)<0.0001);
	}
	
	@Test
	public void AclfSimple2BusXfrFromTurnRatioCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusXfrFromTurnRatio.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.13363)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 1.06727)<0.0001);
	}
	
	@Test
	public void AclfSimple2BusXfrToTurnRatioCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/geditor/aclf/AclfSimple2BusXfrToTurnRatio.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	
  		assertTrue(algo.loadflow());
  		//System.out.println(net.net2String());

  		AclfBus swingBus = (AclfBus)net.getBus("0001");
  		AclfSwingBus swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU, net.getBaseKva())));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-1.1153)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()- 1.0306)<0.0001);
	}
}
