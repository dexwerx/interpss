package org.interpss.test.facts.simult.svc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.simult.svc.SVCLF;
import org.interpss.facts.simult.svc.SVCControlType;
import org.interpss.facts.simult.svc.SVCNrSolver;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algo.LoadflowAlgorithm;

public class Pass1_SimpleSVCTest extends DevTestSetup { 
	@Test
	public void ConctV_testCase() {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus2");
        bus.setVoltageMag(0.9);
//        bus.setVoltageMag(1.05);
        SVCLF svc = new SVCLF(bus, net.getNoBus(), SVCControlType.ConstV);
        svc.setQc(1.05);
        svc.setYsh(0.0, -5.0);
        svc.setLoad(new Complex(1.0, 0.8)); // set Load on the SVC bus
        
        if (svc.init()) {
            // set svc as AclfBus extension
            bus.setExtensionObject(svc);
            
            SVCLF[] svcArray = {svc};
            SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
            
            // create a Loadflow algo object
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
            // set algo NR solver to the CustomNrSolver
            algo.setNrSolver(svcNrSolver);
//            algo.setTolerance(0.001);

            // run Loadflow
            net.accept(algo);
            // output loadflow calculation results
            //System.out.println(AclfOutFunc.loadFlowSummary(net));
            
//            Complex vic = new Complex(bus.getVoltageMag() * Math.cos(bus.getVoltageAng()), bus.getVoltageMag() * Math.sin(bus.getVoltageAng()));
//            Complex vshc = new Complex(svc.getVsh() * Math.cos(svc.getThedash()), svc.getVsh() * Math.sin(svc.getThedash()));
//            Complex yshc = new Complex(0.0, -5.0);
//            Complex yc = (vic.subtract(vshc)).multiply(yshc).divide(vic);
            
            //System.out.println("yshunt=(" + yc.getReal() + ")+i(" + yc.getImaginary() + ")");
//            System.out.println("Vsh, Thedash: " + svc.getVsh() + ", " + svc.getThedash());
            /*
    			yshunt=(-2.7383583558709127E-9)+i(-0.8501256427294784)
    			Vsh, Thedash: 0.8299748714541043, -0.10016742050169442
             */
//    	  	assertTrue(Math.abs(yc.getReal()) < 0.00001); 
//    	  	assertTrue(Math.abs(yc.getImaginary() + 0.85013) < 0.00001); 
//    	  	assertTrue(Math.abs(svc.getVsh() - 0.82998) < 0.00001); 
//    	  	assertTrue(Math.abs(svc.getThedash() + 0.10017) < 0.00001); 
            assertTrue(Math.abs(svc.getParentAclfBus().getVoltageMag() - 1.05) < 0.0001);
        }

	}

	@Test
	public void ConstQ_testCase() {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus2");
        SVCLF svc = new SVCLF(bus, net.getNoBus(), SVCControlType.ConstQ);
        svc.setQc(1.05);
        svc.setYsh(0.0, -5.0);
        svc.setLoad(new Complex(1.0, 0.8));
        
        if (svc.init()) {
            // set svc as AclfBus extension
            bus.setExtensionObject(svc);
            
            SVCLF[] svcArray = {svc};
            SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
            
            // create a Loadflow algo object
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
            // set algo NR solver to the CustomNrSolver
            algo.setNrSolver(svcNrSolver);

            // run Loadflow
            net.accept(algo);
            // output loadflow calculation results
            //System.out.println(AclfOutFunc.loadFlowSummary(net));
            
//            Complex vic = new Complex(bus.getVoltageMag() * Math.cos(bus.getVoltageAng()), bus.getVoltageMag() * Math.sin(bus.getVoltageAng()));
//            Complex vshc = new Complex(svc.getVsh() * Math.cos(svc.getThedash()), svc.getVsh() * Math.sin(svc.getThedash()));
//            Complex yshc = new Complex(0.0, -5.0);
//            Complex yc = (vic.subtract(vshc)).multiply(yshc).divide(vic);
            
            //System.out.println("yshunt=(" + yc.getReal() + ")+i(" + yc.getImaginary() + ")");
            //System.out.println("Vsh, Thedash: " + svc.getVsh() + ", " + svc.getThedash());	
            /*
    		yshunt=(4.941690964361082E-6)+i(-0.9733914104446989)
    		Vsh, Thedash: 0.8162216037743674, -0.09882646523007681
             */
//    	  	assertTrue(Math.abs(yc.getReal()) < 0.00001); 
//    	  	assertTrue(Math.abs(yc.getImaginary() + 0.973393) < 0.00001); 
//    	  	assertTrue(Math.abs(svc.getVsh() - 0.81622) < 0.00001); 
//    	  	assertTrue(Math.abs(svc.getThedash() + 0.09883) < 0.00001);
            double vi = svc.getParentAclfBus().getVoltageMag(), thetai = svc.getParentAclfBus().getVoltageAng();
            double vsh = svc.getVsh(), thetash = svc.getThedash();
            double gsh = 0.0, bsh = -5.0;
            double qsh = vi * vi * bsh + vi * vsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
            assertTrue(Math.abs(-qsh - 1.05) < 0.0005);
        }

	}

	@Test
	public void ConstB_testCase() {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus2");
        SVCLF svc = new SVCLF(bus, net.getNoBus(), SVCControlType.ConstB);
        svc.setQc(0.08);
        svc.setYsh(0.0, -5.0);
        svc.setLoad(new Complex(1.0, 0.8));
        
        if (svc.init()) {
            // set svc as AclfBus extension
            bus.setExtensionObject(svc);
            
            SVCLF[] svcArray = {svc};
            SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
            
            // create a Loadflow algo object
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
            // set algo NR solver to the CustomNrSolver
            algo.setNrSolver(svcNrSolver);

            // run Loadflow
            net.accept(algo);
            // output loadflow calculation results
            //System.out.println(AclfOutFunc.loadFlowSummary(net));
            
            Complex vic = new Complex(bus.getVoltageMag() * Math.cos(bus.getVoltageAng()), bus.getVoltageMag() * Math.sin(bus.getVoltageAng()));
            Complex vshc = new Complex(svc.getVsh() * Math.cos(svc.getThedash()), svc.getVsh() * Math.sin(svc.getThedash()));
            Complex yshc = new Complex(0.0, -5.0);
            Complex yc = (vic.subtract(vshc)).multiply(yshc).divide(vic);
            
            //System.out.println("yshunt=(" + yc.getReal() + ")+i(" + yc.getImaginary() + ")");
            //System.out.println("Vsh, Thedash: " + svc.getVsh() + ", " + svc.getThedash());	
            /*
    		yshunt=(1.1800157013745432E-4)+i(0.049875933325408905)
    		Vsh, Thedash: 0.9088964271644712, -0.11136805274941748
             */
//    	  	assertTrue(Math.abs(yc.getReal()) < 0.00019); 
//    	  	assertTrue(Math.abs(yc.getImaginary() - 0.04987) < 0.00001); 
//    	  	assertTrue(Math.abs(svc.getVsh() - 0.90890) < 0.00001); 
//    	  	assertTrue(Math.abs(svc.getThedash() + 0.11137) < 0.00001);         
    	  	assertTrue(Math.abs(yc.getImaginary() - 0.08) < 0.0001); 
        }

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
        SwingBusAdapter swingBus = bus1.toSwingBus();
        // set swing bus attributes
        swingBus.setVoltMag(1.0, UnitType.PU);
        swingBus.setVoltAng(0.0, UnitType.Deg);
        // add the bus into the network
        //net.addBus(bus1);

        AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2", net);
        bus2.setAttributes("Bus 2", "");
        bus2.setBaseVoltage(4000.0);

/* Prof Wu : there is no use to defined bus load info here, since
 *           the info will be override by the SVC implementation        
        // set the bus to a non-generator bus
        bus2.setGenCode(AclfGenCode.NON_GEN);
        // set the bus to a constant power load bus
        bus2.setLoadCode(AclfLoadCode.CONST_P);
//        bus2.setVoltageMag(1.05);
        // adapt the bus object to a Load bus object
        LoadBusAdapter loadBus = bus2.toLoadBus();
        // set load to the bus
        loadBus.setLoad(new Complex(1.0, 0.8), UnitType.PU);
        //net.addBus(bus2);
*/        

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

