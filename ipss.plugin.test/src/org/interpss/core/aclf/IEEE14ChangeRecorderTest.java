package org.interpss.core.aclf;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.interpss.PluginTestSetup;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class IEEE14ChangeRecorderTest  extends PluginTestSetup {
	@Test
	public void runIEEE14BusAdjustChangeStep() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
		
		// set a bookmark to the AclfNetwork object
		ChangeRecorder recorder = new ChangeRecorder(net);
		
		// run loadflow, this will modify the network object
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
	  	algo.setNonDivergent(true);
	  	
  		assertTrue(algo.loadflow());
  		AclfBus swingBus = (AclfBus)net.getBus("0001");
		SwingBusAdapter swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-2.32394)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()+0.20674)<0.0001);
  		
  		// make more changes to the network object
  		net.getAclfBus("0001").setGenCode(AclfGenCode.GEN_PV);
  		net.getBus("0008").setStatus(false);
  		net.getBranch("0007", "0008").setStatus(false);
  		
  		// roll back all recorded changes to the bookmark point
  		recorder.endRecording().apply();
  		
		//System.out.println(net.net2String());
  		// when the network loaded into InterPSS, all voltage ang = 0.0
  		for (Bus b : net.getBusList()) {
  			AclfBus bus = (AclfBus)b;
  	  		assertTrue(Math.abs(bus.getVoltageAng())<0.0001);
  		}

  		assertTrue(algo.loadflow());
  		swingBus = (AclfBus)net.getBus("0001");
		swing = (SwingBusAdapter)swingBus.getAdapter(SwingBusAdapter.class);
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-2.32394)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()+0.20674)<0.0001);
	}			
}
