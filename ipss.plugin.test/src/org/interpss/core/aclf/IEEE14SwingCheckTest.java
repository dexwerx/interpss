package org.interpss.core.aclf;

import static org.junit.Assert.assertTrue;

import org.interpss.EditorTestSetup;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.funcImpl.AclfNetHelper;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class IEEE14SwingCheckTest  extends EditorTestSetup {
	@Test
	public void run3WXfrOffCase() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);

	  	net.getBranch("0007", "0008").setStatus(false);
	  	net.getBranch("0001", "0002").setStatus(false);
	  	
		AclfNetHelper helper = new AclfNetHelper(net);
		if (!helper.checkSwingBus())
			helper.assignSwingBus();
  		assertTrue(!net.getBus("0008").isActive());
		//for (Bus bus : net.getBusList())
		//	System.out.println("id, intFlag, status: " + 
		//			bus.getId() + ", " + bus.getIntFlag() + ", " + bus.isActive());
		
  		assertTrue(algo.loadflow());
	}			
}
