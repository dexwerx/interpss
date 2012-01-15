package org.interpss.test.facts.simult.svc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.general.SVCControlType;
import org.interpss.facts.simult.svc.SVCSimultLF;
import org.interpss.facts.simult.svc.SVCSimultSolver;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfLoadBus;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.algo.LoadflowAlgorithm;

public class SimpleSVCTest extends DevTestSetup { 
	@Test
	public void ConstV_testCase() {
		AclfNetwork net = createNet();
//        LoadflowAlgorithm algoi = CoreObjectFactory.createLoadflowAlgorithm(net);
//        algoi.loadflow();
//	  	System.out.println(AclfOutFunc.loadFlowSummary(net));
		
        AclfBus bus = net.getAclfBus("Bus2");
        SVCSimultLF svc = new SVCSimultLF(bus, new Complex(0.0, -5.0), SVCControlType.ConstV, 0.8, net.getNoBus(), -100.0, 100.0);
//        svc.setQc(1.0);
//        svc.setYsh(0.0, -5.0);
//        svc.setLoad(new Complex(1.0, 0.8)); // set Load on the SVC bus
//        svc.init();

        // set svc as AclfBus extension
        bus.setExtensionObject(svc);
        
        SVCSimultLF[] svcArray = {svc};
//        SVCSimultLF[] svcArray = {};
        SVCSimultSolver svcSolver = new SVCSimultSolver(net, svcArray);
        
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // set algo NR solver to the CustomNrSolver
        algo.setNrSolver(svcSolver);

        // run Loadflow
        boolean lfConverged = net.accept(algo);
//        boolean lfConverged = algo.loadflow();
        // output loadflow calculation results
        //System.out.println(AclfOutFunc.loadFlowSummary(net));
        
        assertTrue(lfConverged);
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        double thetai = net.getAclfBus("Bus2").getVoltageAng();
        
        System.out.println("Vi=" + vi + ", thetai=" + thetai);

//        assertTrue(Math.abs(svc.getSsh(net).getReal() / vi / vi) < 0.0001);
        double vsh = svc.getVsh();
        double thetash = svc.getThetash();

        System.out.println("Vsh=" + vsh + ", thetash=" + thetash);
        
        double gsh = svc.getConverter().getYth().getReal();
        double bsh = svc.getConverter().getYth().getImaginary();
        
        assertTrue(Math.abs(vsh * vsh * gsh - vi * vsh * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash))) < 0.0001);
        assertTrue(Math.abs(vi - svc.getTunedValue()) < 0.0001);

	}
	
	@Test
	public void ConstB_testCase() {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus2");
//        SVCSimultLF svc = new SVCSimultLF(bus, new Complex(0.0, -5.0), SVCControlType.ConstB, -0.1, net.getNoBus(), -100.0, 100.0);
        SVCSimultLF svc = new SVCSimultLF(bus, new Complex(0.0, -5.0), SVCControlType.ConstB, 0.0, net.getNoBus(), -100.0, 100.0);

        // set svc as AclfBus extension
        bus.setExtensionObject(svc);
        
        SVCSimultLF[] svcArray = {svc};
//        SVCSimultLF[] svcArray = {};
        SVCSimultSolver svcSolver = new SVCSimultSolver(net, svcArray);
        
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // set algo NR solver to the CustomNrSolver
        algo.setNrSolver(svcSolver);
        algo.setTolerance(1.0E-7);

        // run Loadflow
        boolean lfConverged = net.accept(algo);
        
        assertTrue(lfConverged);
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        double thetai = net.getAclfBus("Bus2").getVoltageAng();
        
        System.out.println("Vi=" + vi + ", thetai=" + thetai);

        double vsh = svc.getVsh();
        double thetash = svc.getThetash();

        System.out.println("Vsh=" + vsh + ", thetash=" + thetash);
        
        double gsh = svc.getConverter().getYth().getReal();
        double bsh = svc.getConverter().getYth().getImaginary();
        
        double pe = vsh * vsh * gsh - vi * vsh * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));
        double qsh = vi * vi * bsh + vi * vsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
        
        assertTrue(Math.abs(pe) < 0.0001);
        assertTrue(Math.abs(qsh / vi / vi - svc.getTunedValue()) < 0.0001);

	}
	
	@Test
	public void ConstQ_testCase() {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus2");

//        LoadflowAlgorithm initalgo = CoreObjectFactory.createLoadflowAlgorithm(net);
//		initalgo.loadflow();
//        double viinit = net.getAclfBus("Bus2").getVoltageMag();
//        System.out.println("vi_init=" + viinit);

        SVCSimultLF svc = new SVCSimultLF(bus, new Complex(0.0, -5.0), SVCControlType.ConstQ, 0.0, net.getNoBus(), -100.0, 100.0);

        // set svc as AclfBus extension
//        bus.setExtensionObject(svc);
        
        SVCSimultLF[] svcArray = {svc};
//        SVCSimultLF[] svcArray = null;
        SVCSimultSolver svcSolver = new SVCSimultSolver(net, svcArray);
        
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // set algo NR solver to the CustomNrSolver
        algo.setNrSolver(svcSolver);
        algo.setTolerance(1.0E-7);

        // run Loadflow
        boolean lfConverged = net.accept(algo);
//        boolean lfConverged = algo.loadflow();
        // output loadflow calculation results
        //System.out.println(AclfOutFunc.loadFlowSummary(net));
        
        assertTrue(lfConverged);
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        double thetai = net.getAclfBus("Bus2").getVoltageAng();
        
        System.out.println("Vi=" + vi + ", thetai=" + thetai);

        double vsh = svc.getVsh();
        double thetash = svc.getThetash();

        System.out.println("Vsh=" + vsh + ", thetash=" + thetash);
        
        double gsh = svc.getConverter().getYth().getReal();
        double bsh = svc.getConverter().getYth().getImaginary();
        
        double pe = vsh * vsh * gsh - vi * vsh * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));
        double qsh = vi * vi * bsh + vi * vsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
        
        System.out.println("Psh=" + pe + ", Qsh=" + qsh);
        
        assertTrue(Math.abs(pe) < 0.0001);
        assertTrue(Math.abs(qsh - svc.getTunedValue()) < 0.0001);

	}

	public static AclfNetwork createNet() {
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
        AclfSwingBus swingBus = bus1.toSwingBus();
        // set swing bus attributes
        swingBus.setVoltMag(1.0, UnitType.PU);
        swingBus.setVoltAng(0.0, UnitType.Deg);
        // add the bus into the network
        //net.addBus(bus1);

        AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2", net);
        bus2.setAttributes("Bus 2", "");
        bus2.setBaseVoltage(4000.0);

// Prof Wu : there is no use to defined bus load info here, since
// *           the info will be override by the SVC implementation        
        // set the bus to a non-generator bus
        bus2.setGenCode(AclfGenCode.NON_GEN);
        // set the bus to a constant power load bus
        bus2.setLoadCode(AclfLoadCode.CONST_P);
//        bus2.setVoltageMag(1.05);
        // adapt the bus object to a Load bus object
        AclfLoadBus loadBus = bus2.toLoadBus();
        // set load to the bus
        loadBus.setLoad(new Complex(1.0, 0.8), UnitType.PU);
        //net.addBus(bus2);
//*/        

        // create an AclfBranch object
        AclfBranch branch = CoreObjectFactory.createAclfBranch();
        // set branch name, description and circuit number
        branch.setAttributes("Branch 1", "", "1");
        // set branch parameters
        branch.setZ(new Complex(0.0, 0.1));
//        lineBranch.setZ(new Complex(0.05, 0.1), UnitType.PU, 4000.0, msg);
        // add the branch from Bus1 to Bus2
        net.addBranch(branch, "Bus1", "Bus2");
        
        return net;
	}
}

