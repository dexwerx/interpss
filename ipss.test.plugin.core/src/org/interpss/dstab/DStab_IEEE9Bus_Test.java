package org.interpss.dstab;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.mapper.odm.ODMAcscDataMapper;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class DStab_IEEE9Bus_Test extends DStabTestSetupBase{
	
	@Test
	public void test_IEEE9Bus_Dstab(){
		IpssCorePlugin.init();
		PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
		assertTrue(adapter.parseInputFile(NetType.DStabNet, new String[]{
				"testData/psse/v30/IEEE9Bus/ieee9.raw",
				"testData/psse/v30/IEEE9Bus/ieee9.seq",
				"testData/psse/v30/IEEE9Bus/ieee9_dyn_onlyGen.dyr"
		}));
		DStabModelParser parser =(DStabModelParser) adapter.getModel();
		
		
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET);
		if (!new ODMDStabDataMapper(msg)
					.map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			return;
		}
		
		
	    DStabilityNetwork dsNet =simuCtx.getDStabilityNet();
	    System.out.println(dsNet.net2String());
	    
		DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
		LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
		assertTrue(aclfAlgo.loadflow());
		
		
		
	}

}
