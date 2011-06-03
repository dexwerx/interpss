package org.interpss.sample.acsc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;

import org.interpss.display.AcscOutFunc;
import org.interpss.editor.jgraph.ui.IIpssGraphModel;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.editor.util.IOUtilFunc;
import org.interpss.numeric.util.TestUtilFunc;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.XfrConnectCode;
import com.interpss.core.acsc.adpter.AcscXfrAdapter;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class Acsc5BusSample  {
	@Test
	public void lg() throws Exception {
	
		
	  	AcscNetwork faultNet =create_SC_5BusSystem();
	  	SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(faultNet);

		AcscBusFault fault = CoreObjectFactory.createAcscBusFault("3", algo);
		fault.setFaultCode(SimpleFaultCode.GROUND_3P);
		fault.setZLGFault(new Complex(0.0, 0.0));
		fault.setZLLFault(new Complex(0.0, 0.0));
		
	  	algo.calculateBusFault(fault);
  		System.out.println(fault.toString(fault.getAcscBus().getBaseVoltage(), faultNet));
	  	
  		/*
   fault amps(a):     -0.15 + 0.03i pu    385.27325(169.09466) amps
   fault amps(b):     0 pu      0.0000(  0.0000) amps
   fault amps(c):     0 pu      0.0000(  0.0000) amps

   fault amps(1):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(2):     -0.05 + 0.01i pu    128.42442(169.09466) amps
   fault amps(0):     -0.05 + 0.01i pu    128.42442(169.09466) amps
     		 */
		//System.out.println(simuCtx.getAcscFaultNet().net2String());
  		
	  	//assertTrue(TestUtilFunc.compare(fault.getFaultResult().getSCCurrent_012(), 
	  	//		-0.15071023628251298, 0.029036786613257246, 0.0, 0.0, 0.0, 0.0) );  		

		System.out.println(AcscOutFunc.faultResult2String(faultNet, algo));
	}			

	
	
	public static AcscNetwork create_SC_5BusSystem() {
		 AcscNetwork net=CoreObjectFactory.createAcscNetwork();
		net.setId( "ACSC 5-bus test system" );
		net.setBaseKva(100000.0);

    	AcscInputUtilFunc.addScNonContributeBusTo(net, "1", "Bus-1", 13800, 1, 1);
    	AcscInputUtilFunc.addScNonContributeBusTo(net, "2", "Bus-2", 13800, 1, 1);
    	AcscInputUtilFunc.addScNonContributeBusTo(net, "3", "Bus-3", 13800, 1, 1);
    	AcscInputUtilFunc.addScContributeBusTo(net, "4", "Bus-4", 13800, 1, 1,
  	         0.0, .02, 0.0, .02, 0.0, .0e10, UnitType.PU, "SolidGrounded", 0.0, 0.0, UnitType.PU);
    	AcscInputUtilFunc.addScContributeBusTo(net, "5", "Bus-5", 13800, 1, 1,
  	         0.0, .02, 0.0, .02, 0.0, .0e10, UnitType.PU, "SolidGrounded", 0.0, 0.0, UnitType.PU);

		AcscBranch bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.LINE);
		bra.setZ( new Complex( 0.0, 0.25 ));
		bra.setZ0( new Complex(0.0,0.7));
		net.addBranch(bra, "1", "2");

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.LINE);
		net.addBranch(bra, "1", "3");
		bra.setZ( new Complex( 0.0, 0.35 ));
		bra.setZ0( new Complex(0.0,1.0));

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.LINE);
		net.addBranch(bra, "2", "3");
		bra.setZ( new Complex( 0.0, 0.3 ));
		bra.setZ0( new Complex(0.0,0.75));

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.XFORMER);
		net.addBranch(bra, "4", "2");
		bra.setZ( new Complex( 0.0, 0.015 ));
		bra.setZ0( new Complex(0.0, 0.03 ));
		AcscXfrAdapter xfr = (AcscXfrAdapter)bra.getAdapter(AcscXfrAdapter.class);
		xfr.setFromConnectGroundZ(XfrConnectCode.WYE_UNGROUNDED, new Complex(0.0,0.0), UnitType.PU);
		xfr.setToConnectGroundZ(XfrConnectCode.DELTA, new Complex(0.0,0.0), UnitType.PU);

		bra = CoreObjectFactory.createAcscBranch();
		bra.setBranchCode(AclfBranchCode.XFORMER);
		net.addBranch(bra, "5", "3");
		bra.setZ( new Complex( 0.0, 0.03 ));
		bra.setZ0( new Complex(0.0, 0.03 ));
		xfr = (AcscXfrAdapter)bra.getAdapter(AcscXfrAdapter.class);
		xfr.setFromConnectGroundZ(XfrConnectCode.WYE_UNGROUNDED, new Complex(0.0,0.0), UnitType.PU);
		xfr.setToConnectGroundZ(XfrConnectCode.DELTA, new Complex(0.0,0.0), UnitType.PU);

		net.setScDataLoaded(true);
		IpssLogger.getLogger().info( "ACSC 5-bus test system loaded" );
		return net;
	}
}
