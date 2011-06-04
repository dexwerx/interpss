package org.interpss.dstab.bpa;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.display.AclfOutFunc;
import org.interpss.dstab.ieeeModel.DStabTestSetupBase;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabObjectFactory;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.devent.DynamicEvent;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class ODMMaper_IEEE9BusTest  extends DStabTestSetupBase {
	@Test
	public void lfTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile(IODMAdapter.NetType.DStabNet,
				new String[] { "testdata/bpa/IEEE9.dat", 
				               "testdata/bpa/IEEE9-dyn.swi"}));
		
		DStabModelParser parser = (DStabModelParser)adapter.getModel();
		
		parser.stdout();
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
		if (!new ODMDStabDataMapper(msg)
					.map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			return;
		}	

		DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
		DStabilityNetwork dstabNet = simuCtx.getDStabilityNet();

		LoadflowAlgorithm lfAlgo = dstabAlgo.getAclfAlgorithm();
		lfAlgo.loadflow();
		assertTrue(dstabNet.isLfConverged());
		//System.out.println(AclfOutFunc.loadFlowSummary(dstabNet));
		//System.out.println("bus2 Angle(deg)="+dstabNet.getDStabBus("Bus2").getVoltageAng(UnitType.Deg));
		assertTrue(Math.abs(dstabNet.getDStabBus("Bus2").getVoltageMag() - 1.039) < 0.001);
		assertTrue(Math.abs(dstabNet.getDStabBus("Bus2").getVoltageAng(UnitType.Deg) + 3.43) < 0.01);
	}
	
	@Test
	public void DstabTestCase() throws Exception {
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile(IODMAdapter.NetType.DStabNet,
				new String[] { "testdata/bpa/IEEE9.dat", 
				               "testdata/bpa/IEEE9-dyn.swi"}));
		
		DStabModelParser parser = (DStabModelParser)adapter.getModel();
		
		parser.stdout();
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET, msg);
		if (!new ODMDStabDataMapper(msg)
					.map2Model(parser, simuCtx)) {
			System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
			return;
		}	
		
		DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
		DStabilityNetwork dstabNet = simuCtx.getDStabilityNet();
		
		dstabAlgo.setSimuMethod(DynamicSimuMethod.MODIFIED_EULER);
		dstabAlgo.setSimuStepSec(0.001);
		dstabAlgo.setTotalSimuTimeSec(10);
		/*
		 *  in DStab_Ipss5BusTest, simu setting and events are stored in xml file
		 *  
		 *  but till now we have not done anything about ipssStudyScenario
		 */
		
		
		//TODO Mike, please add a sample to create busFault and dynamic event here.
		//DynamicEvent event =DStabObjectFactory.createBranchReclosureEvent(dstabNet, fault, eventId, eventName)
		//dstabNet.addDynamicEvent(paramDynamicEvent, paramString)
		
		
		
		
		
		

		
	}

}
