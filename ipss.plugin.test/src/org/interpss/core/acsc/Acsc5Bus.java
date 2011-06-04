package org.interpss.core.acsc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.PluginTestSetup;
import org.interpss.display.AcscOutFunc;
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

public class Acsc5Bus  extends PluginTestSetup {
	@Test
	public void lg() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACSC_NET, msg);
		loadCaseData("testData/geditor/acsc/Acsc5Bus.ipss", simuCtx);
		
	  	AcscNetwork faultNet = simuCtx.getAcscNet();
	  	SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(faultNet);

		AcscBusFault fault = CoreObjectFactory.createAcscBusFault("0001", algo);
		fault.setFaultCode(SimpleFaultCode.GROUND_3P);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
	  	algo.calculateBusFault(fault);
  		//System.out.println(fault.toString(fault.getAcscBus().getBaseVoltage(), faultNet));

  		//System.out.println(fault.getFaultResult().getSCCurrent_abc());
  		// 0 + 6.01i  5.2 - 3i  -5.2 - 3i
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getSCCurrent_abc(), 
	  			0.0, 6.01, 5.2, -3.0, -5.2, -3.0, 0.01) );  		
/*
              Bus Fault Info
              ==============

          Fault Id:      0001_Ground_3P_
          Bus name:      Bus-1
          Fault type:    Ground_3P
          Fault current: 0 + 6.01i pu
                         0 + 25,138.94i amps

      BusID         FaultVoltage            ContribAmps
                 (pu)        (volts)      (pu)       (amps)
     --------   --------   ----------   --------   ----------
     0001              0            0         0            0
     0002          0.877      12102.8         0            0
     0003          0.875      12078.5         0            0
     0004           0.93      12830.2     4.996      20900.1
     0005           0.95      13111.4     7.034      29426.8


        BranchID                FaultAmps
                              (pu)       (Amp) 
     --------------------   --------   ----------
     0002->0003(1)             0.006         24.5
     0002->0001(1)             3.508      14676.6
     0003->0001(1)             2.501      10462.3
     0004->0002(1)             3.514      14701.1
     0005->0003(1)             2.495      10437.8
*/
		System.out.println(AcscOutFunc.faultResult2String(faultNet, algo));
	}			
}
