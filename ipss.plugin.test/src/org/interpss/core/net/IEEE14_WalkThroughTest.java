package org.interpss.core.net;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.ActivePowerPathWalkEnum;
import com.interpss.core.algorithm.ActivePowerPathWalkThrough;
import com.interpss.core.common.visitor.IAclfNetBVisitor;
import com.interpss.core.common.visitor.IBranchBVisitor;
import com.interpss.core.common.visitor.IBusBVisitor;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class IEEE14_WalkThroughTest  extends PluginTestSetup {
	@Test
	public void runIEEE14BusAdjustChangeStep() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
		IAclfNetBVisitor algo = CoreObjectFactory.createLfAlgoVisitor();
	  	
  		net.accept(algo);
  		assertTrue(net.isLfConverged());
  		
  		ActivePowerPathWalkThrough walkAlgo = CoreObjectFactory.createActivePowerPathWalkThrough();
  		walkAlgo.setBusVisitor(new IBusBVisitor() {
			@Override
			public boolean visit(Bus bus) {
				//System.out.println("Bus " + bus.getId() + " visited");
				return true;
			}
  		});
  		walkAlgo.setBranchVisitor(new IBranchBVisitor() {
			@Override
			public boolean visit(Branch branch) {
				//System.out.println("Branch " + branch.getId() + " visited");
				return true;
			}
  		});
  		
		System.out.println("Source to Load direction");
  		walkAlgo.setDirection(ActivePowerPathWalkEnum.SOURCE_TO_LOAD);
  		net.accept(walkAlgo);
  		
  		for (Bus bus : net.getBusList())
  	  		assertTrue(bus.isVisited());
  		for (Branch branch : net.getBranchList())
  	  		assertTrue(branch.isVisited());
  		
		System.out.println("Load to Source direction");
  		walkAlgo.setDirection(ActivePowerPathWalkEnum.LOAD_TO_SOURCE);
  		net.accept(walkAlgo);

  		for (Bus bus : net.getBusList())
  	  		assertTrue(bus.isVisited());
  		for (Branch branch : net.getBranchList())
  	  		assertTrue(branch.isVisited());
	}			
}
