package org.interpss.test.facts.statcom;

import static org.junit.Assert.*;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.statcom.LFSolverWithStatcom;
import org.interpss.facts.statcom.StatcomControlType;
import org.interpss.facts.statcom.StatcomLF;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;

public class LFSolverWithStatcomTest {

	@Test
	public void testLFSolverWithStatcomConstQ() throws InterpssException {
		AclfNetwork net = createNet();
        StatcomLF myStatcom = new StatcomLF("Bus2", new Complex(0.0, -5.0), StatcomControlType.ConstQ, 0.5);
        StatcomLF[] statcomArray = {myStatcom};
        LFSolverWithStatcom solver = new LFSolverWithStatcom(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        assertTrue(Math.abs(myStatcom.getSsh(net).getReal()) < 0.0001);
        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() - 0.5) < 0.0001);
	}
	
	@Test
	public void testLFSolverWithStatcomConstB() throws InterpssException {
		AclfNetwork net = createNet();
        StatcomLF myStatcom = new StatcomLF("Bus2", new Complex(0.0, -5.0), StatcomControlType.ConstB, 2.0);
        StatcomLF[] statcomArray = {myStatcom};
        LFSolverWithStatcom solver = new LFSolverWithStatcom(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        double thetai = net.getAclfBus("Bus2").getVoltageAng();
        
        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() / vi / vi - (2.0)) < 0.0001);
        
	}
	
	private static AclfNetwork createNet() {
        // create a sample 5-bus system for Loadflow
        AclfNetwork net = CoreObjectFactory.createAclfNetwork();
        double basekVA = 100000.0;
        net.setBaseKva(basekVA);
        AclfBus bus1 = CoreObjectFactory.createAclfBus("Bus1", net);
        // set bus name and description attributes
        bus1.setAttributes("Bus 1", "");
        // set bus base voltage
        bus1.setBaseVoltage(4000.0);
        // set bus to be a swing bus
        bus1.setGenCode(AclfGenCode.SWING);
        // adapt the bus object to a swing bus object
        SwingBusAdapter swingBus = bus1.toSwingBus();
        // set swing bus attributes
        swingBus.setVoltMag(1.0, UnitType.PU);
        swingBus.setVoltAng(0.0, UnitType.Deg);
        // add the bus into the network
        //net.addBus(bus1);

        AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2", net);
        bus2.setAttributes("Bus 2", "");
        bus2.setBaseVoltage(4000.0);
        bus2.setLoadCode(AclfLoadCode.CONST_P);
        bus2.setLoadP(1.0);
        bus2.setLoadQ(0.8);

        // create an AclfBranch object
        AclfBranch branch = CoreObjectFactory.createAclfBranch();
        // set branch name, description and circuit number
        branch.setAttributes("Branch 1", "", "1");
        // set branch parameters
        branch.setZ(new Complex(0.0, 0.1));
        // add the branch from Bus1 to Bus2
        net.addBranch(branch, "Bus1", "Bus2");
        
        return net;
	}

}