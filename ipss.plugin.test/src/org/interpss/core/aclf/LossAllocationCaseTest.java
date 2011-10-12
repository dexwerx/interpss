package org.interpss.core.aclf;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.interpss.display.AclfOutFunc;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.path.LossAllocationAlgorithm;
import com.interpss.core.algo.path.NetPathWalkDirectionEnum;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;
import com.interpss.simu.util.sample.SampleCases;

public class LossAllocationCaseTest  extends PluginTestSetup {
	@Test
	public void lossAllocationStep() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
	  	algo.setNonDivergent(true);
  		assertTrue(algo.loadflow());
  		/*
  		for (Branch bra : net.getBranchList()) {
  			System.out.println("Branch id, loss: " + bra.getId() + ", " + 
  					((AclfBranch)bra).loss().getReal() + ", " +
  					((AclfBranch)bra).loss().getReal()*100 + "MW");
  		}
  		*/
  		LossAllocationAlgorithm lossAlgo = CoreObjectFactory.createLossAllocationAlgorithm();
  		assertTrue(!lossAlgo.hasActivePowerLoop(net));
  		
  		//netAdapter.initForWalkThrough();
  		lossAlgo.setDirection(NetPathWalkDirectionEnum.ALONG_PATH); 
  		net.accept(lossAlgo);
		System.out.println(AclfOutFunc.loadLossAllocation(net));

  		//netAdapter.initForWalkThrough();
  		lossAlgo.setDirection(NetPathWalkDirectionEnum.OPPOSITE_PATH); 
  		net.accept(lossAlgo);
		System.out.println(AclfOutFunc.genLossAllocation(net));
	}	
	
	@Test
	public void sample5BusTest() {
  		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		SampleCases.load_LF_5BusSystem(net);
		//System.out.println(net.net2String());

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
	  	algo.setMaxIterations(20);
	  	algo.setTolerance(0.0001, UnitType.PU, net.getBaseKva());
	  	algo.loadflow();
  		//System.out.println(net.net2String());
  		assertTrue(net.isLfConverged());
  		
  		LossAllocationAlgorithm lossAlgo = CoreObjectFactory.createLossAllocationAlgorithm();
  		assertTrue(!lossAlgo.hasActivePowerLoop(net));

  		//netAdapter.initForWalkThrough();
  		lossAlgo.setDirection(NetPathWalkDirectionEnum.ALONG_PATH); 
  		net.accept(lossAlgo);
		System.out.println(AclfOutFunc.loadLossAllocation(net));

  		//netAdapter.initForWalkThrough();
		lossAlgo.setDirection(NetPathWalkDirectionEnum.OPPOSITE_PATH); 
  		net.accept(lossAlgo);
		System.out.println(AclfOutFunc.genLossAllocation(net));
	}	
}
