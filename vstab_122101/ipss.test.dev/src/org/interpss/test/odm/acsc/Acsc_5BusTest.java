package org.interpss.test.odm.acsc;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.interpss.mapper.odm.ODMAcscDataMapper;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.common.util.TestUtilFunc;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Acsc_5BusTest extends DevTestSetup { 
	@Test
	public void testCase() throws Exception {
		File file = new File("testdata/ieee_odm/SC_5bus.xml");
		AcscModelParser parser = ODMObjectFactory.createAcscModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));
			
			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACSC_NET, msg);
			if (!new ODMAcscDataMapper(msg)
						.map2Model(parser, simuCtx)) {
	  	  		System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
	  	  		return;
			}	
			
			// test fault network
			AcscBusFault busFault = simuCtx.getSimpleFaultAlgorithm().getFault("Bus-1_Ground_3P_Bus fault at Bus-1");
			//assertTrue(busFault.getBus().getId().equals("Bus-1"));
			//assertTrue(busFault.getFaultType().equals(SimpleFaultType.BUS_FAULT));
			//assertTrue(Math.abs(busFault.getZLGFault().getImaginary()-0)==0);
			
			simuCtx.getSimpleFaultAlgorithm().calculateBusFault(busFault);
			//System.out.println(AcscOutFunc.faultResult2String(faultNet));
			
	  		//System.out.println(busFault.toString(
	  		//		busFault.getAcscBus().getBaseVoltage(), faultNet));
			/*
			 fault amps(1): (  0.0000 + j  6.008784773163099) pu
			 fault amps(2): (  0.0000 + j  0.0000) pu
			 fault amps(0): (  0.0000 + j  0.0000) pu
			 */
		  	assertTrue(TestUtilFunc.compare(busFault.getFaultResult().getSCCurrent_012(), 
		  			0.0, 0.0, 0.0, 6.008784773163099, 0.0, 0.0) );			
		}
	}
}

