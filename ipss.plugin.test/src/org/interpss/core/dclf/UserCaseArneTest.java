package org.interpss.core.dclf;

import org.apache.commons.math.complex.Complex;
import org.interpss.PluginTestSetup;
import org.interpss.display.DclfOutFunc;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;

/**
 *   This test case is contributed by Arne Lüllmann 
 * 
 */
public class UserCaseArneTest extends PluginTestSetup {
	@Test
	public void runSingleAclfCaseTest() throws Exception {
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(createTestNet())
					.runDclfAnalysis(true);

		System.out.println(DclfOutFunc.dclfResults(algoDsl.algo()));
	}
	
	private AclfNetwork createTestNet() {
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		
		AclfBus bus1 = CoreObjectFactory.createAclfBus("Bus1", net);
        bus1.setBaseVoltage(1.0, UnitType.Volt);
        bus1.setId("Bus1");
        bus1.setGenCode(AclfGenCode.SWING);
        //net.addBus(bus1);

        AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2", net);
        bus2.setBaseVoltage(1.0, UnitType.Volt);
        bus2.setId("Bus2");
        bus2.setLoadCode(AclfLoadCode.CONST_P);
        bus2.setLoadP(10);
        //net.addBus(bus2);

        AclfBus bus3 = CoreObjectFactory.createAclfBus("Bus3", net);
        bus3.setBaseVoltage(1.0, UnitType.Volt);
        bus3.setId("Bus3");
        bus3.setLoadCode(AclfLoadCode.CONST_P);
        bus3.setLoadP(10);
        //net.addBus(bus3);

        AclfBus bus4 = CoreObjectFactory.createAclfBus("Bus4", net);
        bus4.setBaseVoltage(1.0, UnitType.Volt);
        bus4.setId("Bus4");
        bus4.setLoadCode(AclfLoadCode.CONST_P);
        bus4.setLoadP(20.0);
        //net.addBus(bus4);

        AclfBranch branch1 = CoreObjectFactory.createAclfBranch();
        branch1.setBranchCode(AclfBranchCode.LINE);
        branch1.setFromBus(net.getBus("Bus1"));
        branch1.setToBus(net.getBus("Bus2"));
        branch1.setZ(new Complex(.0, 1.0));
        net.addBranch(branch1, "Bus1", "Bus2");

        AclfBranch branch2 = CoreObjectFactory.createAclfBranch();
        branch2.setBranchCode(AclfBranchCode.LINE);
        branch2.setFromBus(net.getBus("Bus2"));
        branch2.setToBus(net.getBus("Bus3"));
        branch2.setZ(new Complex(.0, 1.0));
        net.addBranch(branch2, "Bus2", "Bus3");

        AclfBranch branch3 = CoreObjectFactory.createAclfBranch();
        branch3.setBranchCode(AclfBranchCode.LINE);
        branch3.setFromBus(net.getBus("Bus2"));
        branch3.setToBus(net.getBus("Bus4"));
        branch3.setZ(new Complex(.0, 1.0));
        net.addBranch(branch3, "Bus2", "Bus4");

        AclfBranch branch4 = CoreObjectFactory.createAclfBranch();
        branch4.setBranchCode(AclfBranchCode.LINE);
        branch4.setFromBus(net.getBus("Bus1"));
        branch4.setToBus(net.getBus("Bus4"));
        branch4.setZ(new Complex(.0, 1.0));
        net.addBranch(branch4, "Bus1", "Bus4");
        
        return net;
	}
}
