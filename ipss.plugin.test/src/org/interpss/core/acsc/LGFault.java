package org.interpss.core.acsc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.PluginTestSetup;
import org.interpss.numeric.util.TestUtilFunc;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class LGFault  extends PluginTestSetup {
	@Test
	public void lg() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACSC_NET, msg);
		loadCaseData("testData/support/Fitesa_1.ipss", simuCtx);
		
	  	AcscNetwork faultNet = simuCtx.getAcscNet();
	  	SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(faultNet);

		AcscBusFault fault = CoreObjectFactory.createAcscBusFault("0003", algo);
		fault.setFaultCode(SimpleFaultCode.GROUND_LG);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
	  	algo.calculateBusFault(fault);
  		//System.out.println(fault.toString(fault.getAcscBus().getBaseVoltage(), faultNet));
	  	
  		/*
   fault amps(a):     -0.15 + 0.03i pu    385.27325(169.09466) amps
   fault amps(b):     0 pu      0.0000(  0.0000) amps
   fault amps(c):     0 pu      0.0000(  0.0000) amps

   fault amps(1):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(2):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(0):     -0.05 + 0.01i pu    128.42442(169.09466) amps
     		 */
		//System.out.println(simuCtx.getAcscFaultNet().net2String());
  		
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getSCCurrent_abc(), 
	  			-0.15071023628251298, 0.029036786613257246, 0.0, 0.0, 0.0, 0.0, 0.0001) );  		

		//System.out.println(AcscOutFunc.faultResult2String(faultNet, algo));
	}			

	@Test
	public void llg() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACSC_NET, msg);
		loadCaseData("testData/support/Fitesa_1.ipss", simuCtx);
		
		AcscNetwork faultNet = simuCtx.getAcscNet();
	  	SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(faultNet);

	  	AcscBusFault fault = CoreObjectFactory.createAcscBusFault("0003", algo);
		fault.setFaultCode(SimpleFaultCode.GROUND_LLG);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
	  	algo.calculateBusFault(fault);
  		//System.out.println(fault.toString(fault.getAcscBus().getBaseVoltage(), faultNet));
	  	
  		/*
   fault amps(a):     -0.15 + 0.03i pu    385.27325(169.09466) amps
   fault amps(b):     0 pu      0.0000(  0.0000) amps
   fault amps(c):     0 pu      0.0000(  0.0000) amps

   fault amps(1):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(2):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(0):     -0.05 + 0.01i pu    128.42442(169.09466) amps
     		 */
		//System.out.println(simuCtx.getAcscFaultNet().net2String());

	  	// iPU_012 = 0 - 0i  0.69 + 0.07i  -0.62 - 0.07i
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getSCCurrent_abc(), 
	  			0.0,0.0, 0.69,0.07, -0.62,-0.07, 0.01) );  		

		//System.out.println(AcscOutFunc.faultResult2String(faultNet, algo));
	}			

	@Test
	public void ll() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACSC_NET, msg);
		loadCaseData("testData/support/Fitesa_1.ipss", simuCtx);
		
		AcscNetwork faultNet = simuCtx.getAcscNet();
	  	SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(faultNet);

	  	AcscBusFault fault = CoreObjectFactory.createAcscBusFault("0003", algo);
		fault.setFaultCode(SimpleFaultCode.GROUND_LL);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
	  	algo.calculateBusFault(fault);
  		//System.out.println(fault.toString(fault.getAcscBus().getBaseVoltage(), faultNet));
	  	
  		/*
   fault amps(a):     -0.15 + 0.03i pu    385.27325(169.09466) amps
   fault amps(b):     0 pu      0.0000(  0.0000) amps
   fault amps(c):     0 pu      0.0000(  0.0000) amps

   fault amps(1):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(2):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(0):     -0.05 + 0.01i pu    128.42442(169.09466) amps
     		 */
		//System.out.println(simuCtx.getAcscFaultNet().net2String());
  		
	  	//iPU_012 = 0  0.65 + 0.07i  -0.65 - 0.07i
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getSCCurrent_abc(), 
	  			0.0,0.0,  0.65,0.07,  -0.65,-0.07, 0.01) );  		

//		System.out.println(AcscOutFunc.faultResult2String(faultNet, algo));
	}			
}
