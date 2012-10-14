package org.interpss.sample.acsc;

import org.apache.commons.math3.complex.Complex;
import org.interpss.display.AclfOutFunc;
import org.interpss.display.AcscOutFunc;
import org.interpss.numeric.datatype.Unit.UnitType;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.adpter.AclfLoadBus;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.simu.util.input.AcscInputUtilFunc;

public class TestDaniilPanasetsky {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        AcscNetwork net=CoreObjectFactory.createAcscNetwork();
        net.setBaseKva(100000.0);

        AcscBus Bus1=AcscInputUtilFunc.addScNonContributeBusTo(net, "Bus1", "Bus1", 13800, 1, 1);
        Bus1.setAttributes("Bus1", "");
        Bus1.setBaseVoltage(13800);
        Bus1.setGenCode(AclfGenCode.NON_GEN);
        Bus1.setLoadCode(AclfLoadCode.CONST_P);

        AclfLoadBus loadBus=Bus1.toLoadBus();
        loadBus.setLoad(new Complex(0.1,0.08), UnitType.PU); 

        AcscBus Bus2=AcscInputUtilFunc.addScContributeBusTo(net, "Bus2", "Bus2", 13800, 1, 1, 0.0, 0.001, 0.0, 0.001, 0.0, 1e10, UnitType.PU, "SolidGrounded", 0.0, 0.0, UnitType.PU);
        Bus2.setAttributes("Bus2", "");
        Bus2.setBaseVoltage(13800);
        Bus2.setGenCode(AclfGenCode.SWING);
        AclfSwingBus swingBus=Bus2.toSwingBus();
        swingBus.setVoltMag(1.2, UnitType.PU);
        swingBus.setVoltAng(0.0, UnitType.Deg);

        AcscBranch bra=CoreObjectFactory.createAcscBranch();       
        bra.setBranchCode(AclfBranchCode.LINE);
        bra.setId("1");
        bra.setName("1-2 Line");
        bra.setZ(new Complex(0.05, 0.1));
        bra.setZ0(new Complex(0.0,0.7));
        bra.setAttributes("Branch 1", "", "1");
        net.addBranch(bra, "Bus1", "Bus2");

        //Load Flow calculations        
        LoadflowAlgorithm lfalgo = CoreObjectFactory.createLoadflowAlgorithm(net);
        lfalgo.setLfMethod(AclfMethod.NR);
        lfalgo.loadflow();
        System.out.println(AclfOutFunc.loadFlowSummary(net));               

        // Short Circuit Calculations

        SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(net);       

        AcscBusFault fault = CoreObjectFactory.createAcscBusFault("Bus1", algo);
        fault.setFaultCode(SimpleFaultCode.GROUND_3P);
        fault.setZLGFault(new Complex(0.0, 0.0));
        fault.setZLLFault(new Complex(0.0, 0.0));       
        algo.calculateBusFault(fault);

        System.out.println(AcscOutFunc.faultResult2String(net, algo));
	}
}
