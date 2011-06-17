package org.interpss.dstab.bpa;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.display.AclfOutFunc;
import org.interpss.dstab.ieeeModel.DStabTestSetupBase;
import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Bpa07c_0615_Test extends DStabTestSetupBase{
	@Test
	public void sys2011_lfTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testData/bpa/07c_0615_notBE.dat")); 
		AclfModelParser parser=(AclfModelParser) adapter.getModel();
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		if (!new ODMAclfDataMapper(msg)
					.map2Model(parser, simuCtx)) {
			  System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			  return;
	    }
		AclfNetwork net=simuCtx.getAclfNet();
		
		LoadflowAlgorithm  algo=CoreObjectFactory.createLoadflowAlgorithm(net);
		net.accept(algo);
		System.out.println(AclfOutFunc.lfResultsBusStyle(net));
	}
}
