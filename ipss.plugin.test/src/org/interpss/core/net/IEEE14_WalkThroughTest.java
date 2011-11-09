package org.interpss.core.net;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.path.NetPathWalkAlgorithm;
import com.interpss.core.algo.path.NetPathWalkDirectionEnum;
import com.interpss.core.algo.path.impl.AbstractBranchPowerFlowPathWalker;
import com.interpss.core.algo.path.impl.AbstractBusPowerFlowPathWalker;
import com.interpss.core.common.visitor.IAclfNetBVisitor;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class IEEE14_WalkThroughTest  extends PluginTestSetup {
	@Test
	public void runIEEE14BusAdjustChangeStep() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
		IAclfNetBVisitor algo = CoreObjectFactory.createLfAlgoVisitor();
	  	
  		net.accept(algo);
  		assertTrue(net.isLfConverged());
  		
  		NetPathWalkAlgorithm walkAlgo = CoreObjectFactory.createNetPathWalkAlgorithm();
  		walkAlgo.setBusWalker(new AbstractBusPowerFlowPathWalker() {
  			@Override
  			public boolean visit(Bus bus) {
  				bus.setVisited(true);
  				System.out.println("Bus " + bus.getId() + " visited");
  				return true;
  			}
  		});
  		walkAlgo.setBranchWalker(new AbstractBranchPowerFlowPathWalker() {
  			@Override
  			public boolean visit(Bus bus, Branch branch) {
  				branch.setVisited(true);
  				System.out.println("Branch " + branch.getId() + " visited");
  				return true;
  			}
  		});
  		
		System.out.println("Source to Load direction");
  		walkAlgo.setDirection(NetPathWalkDirectionEnum.ALONG_PATH);
  		net.accept(walkAlgo);
  		
  		for (Bus bus : net.getBusList())
  	  		assertTrue(bus.isVisited());
  		for (Branch branch : net.getBranchList())
  	  		assertTrue(branch.isVisited());
  		
		System.out.println("Load to Source direction");
  		walkAlgo.setDirection(NetPathWalkDirectionEnum.OPPOSITE_PATH);
  		net.accept(walkAlgo);

  		for (Bus bus : net.getBusList())
  	  		assertTrue(bus.isVisited());
  		for (Branch branch : net.getBranchList())
  	  		assertTrue(branch.isVisited());
	}	
}
