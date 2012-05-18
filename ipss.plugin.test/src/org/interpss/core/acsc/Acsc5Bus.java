package org.interpss.core.acsc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.EditorTestSetup;
import org.interpss.numeric.util.TestUtilFunc;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class Acsc5Bus  extends EditorTestSetup {
	@Test
	public void lg() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACSC_NET);
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
//		System.out.println(AcscOutFunc.faultResult2String(faultNet, algo));
		
		fault.getFaultResult().calContributingCurrent();
//		for (Bus b : faultNet.getBusList()) {
//			AcscBus bus = (AcscBus)b;
//			Complex3x1 v = fault.getFaultResult().getBusVoltage_012(bus);
//			System.out.println(v);
//		}
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getBusVoltage_012(faultNet.getAcscBus("0002")), 
	  			0.0,0.0,  0.88,0.0,  0.0,0.0, 0.01) );  			
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getBusVoltage_012(faultNet.getAcscBus("0005")), 
	  			0.0,0.0,  0.95,0.0,  0.0,0.0, 0.01) );  			

		fault.getFaultResult().calBranchCurrent();
//		int cnt = 0;
//		for (Branch b : faultNet.getBranchList()) {
//			Complex3x1 amp = fault.getFaultResult().getBranchAmpsFrom2To_012(++cnt);
//			System.out.println(amp);
//		}
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getBranchAmpsFrom2To_012(2), 
	  			0.0,0.0,  0.0,3.51,  0.0,0.0, 0.01) );  			
	  	assertTrue(TestUtilFunc.compare(fault.getFaultResult().getBranchAmpsFrom2To_012(5), 
	  			0.0,0.0,  0.0,2.49,  0.0,0.0, 0.01) );  			
	}			
}
