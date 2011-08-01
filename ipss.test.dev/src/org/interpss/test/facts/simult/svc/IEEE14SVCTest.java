package org.interpss.test.facts.simult.svc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.IpssPlugin;
import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.facts.simult.svc.SVCSimultLF;
import org.interpss.facts.simult.svc.SVCControlType;
import org.interpss.facts.simult.svc.SVCSimultSolver;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;

public class IEEE14SVCTest extends DevTestSetup {

	@Test
	public void singleConstV_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		for (Bus bus : net.getBusList()) {
			AclfBus thisBus = net.getAclfBus(bus.getId());
			if (thisBus.getGenCode() == AclfGenCode.GEN_PQ) {
				System.out.println(bus.getId() + ": " + thisBus.getVoltageMag());
				AclfNetwork currentNet = createNet();
		        AclfBus currentBus = currentNet.getAclfBus(bus.getId());
		        SVCSimultLF svc = new SVCSimultLF(currentBus, currentNet.getNoBus(), SVCControlType.ConstV);
		        double vc = currentBus.getVoltageMag();
		        svc.setQc(vc * 0.9);
		        svc.setYsh(0.0, -5.0);
		        svc.setLoad(currentBus.getLoad()); // set Load on the SVC bus

		        // set svc as AclfBus extension
		        currentBus.setExtensionObject(svc);
		        
		        svc.init();
		        SVCSimultLF[] svcArray = {svc};
		        SVCSimultSolver svcNrSolver = new SVCSimultSolver(currentNet, svcArray);
		        
		        // create a Loadflow algo object
		        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		        // set algo NR solver to the CustomNrSolver
		        algo.setNrSolver(svcNrSolver);

		        // run Loadflow
		        currentNet.accept(algo);
		        
		        System.out.println("Converged voltage: " + currentBus.getVoltageMag() + ", control objective: " + vc);
			  	assertTrue(Math.abs(currentBus.getVoltageMag() - vc * 0.9) < 0.0001); 
			  	currentNet = null;
			}
		}
	}
	
	@Test
	public void singleConstQ_testCase() throws InterpssException, Exception {
		IpssPlugin.init();
		AclfNetwork net = createNet();
		for (Bus bus : net.getBusList()) {
			AclfBus thisBus = net.getAclfBus(bus.getId());
			if (thisBus.getGenCode() == AclfGenCode.GEN_PQ) {
				IpssPlugin.init();
				AclfNetwork currentNet = createNet();
		        AclfBus currentBus = currentNet.getAclfBus(bus.getId());
		        SVCSimultLF svc = new SVCSimultLF(currentBus, currentNet.getNoBus(), SVCControlType.ConstQ);
		        double qc = 0.1;
		        svc.setQc(qc);
		        svc.setYsh(0.0, -5.0);
		        svc.setLoad(currentBus.getLoad()); // set Load on the SVC bus

		        // set svc as AclfBus extension
		        currentBus.setExtensionObject(svc);
		        
		        svc.init();
		        SVCSimultLF[] svcArray = {svc};
		        SVCSimultSolver svcNrSolver = new SVCSimultSolver(currentNet, svcArray);
		        
		        // create a Loadflow algo object
		        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		        // set algo NR solver to the CustomNrSolver
		        algo.setNrSolver(svcNrSolver);
		        algo.setMaxIterations(100);
		        algo.setTolerance(0.0001);

		        // run Loadflow
		        currentNet.accept(algo);
		        
		        double vi = currentBus.getVoltageMag();
		        double vsh = svc.getVsh();
		        double thetai = currentBus.getVoltageAng();
		        double thetash = svc.getThedash();
		        double gsh = 0.0, bsh = -5.0;
		        double qsh = -(vi * vi * bsh + vi * vsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash)));
		        System.out.println("[" + bus.getId() + "] Converged Q: " + qsh + ", control objective: " + qc);
			  	assertTrue(Math.abs(qsh - qc) < 0.001); 
			  	currentNet = null;
			}
		}
	}
	
	@Test
	public void singleConstB_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		for (Bus bus : net.getBusList()) {
			AclfBus thisBus = net.getAclfBus(bus.getId());
			if (thisBus.getGenCode() == AclfGenCode.GEN_PQ) {
				AclfNetwork currentNet = createNet();
		        AclfBus currentBus = currentNet.getAclfBus(bus.getId());
		        SVCSimultLF svc = new SVCSimultLF(currentBus, currentNet.getNoBus(), SVCControlType.ConstB);
		        double qc = 0.05;
		        svc.setQc(qc);
		        svc.setYsh(0.0, -5.0);
		        svc.setLoad(currentBus.getLoad()); // set Load on the SVC bus

		        // set svc as AclfBus extension
		        currentBus.setExtensionObject(svc);
		        
		        svc.init();
		        SVCSimultLF[] svcArray = {svc};
		        SVCSimultSolver svcNrSolver = new SVCSimultSolver(currentNet, svcArray);
		        
		        // create a Loadflow algo object
		        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		        // set algo NR solver to the CustomNrSolver
		        algo.setNrSolver(svcNrSolver);
		        algo.setMaxIterations(100);
		        algo.setTolerance(0.001);

		        // run Loadflow
		        currentNet.accept(algo);

		        Complex vic = new Complex(currentBus.getVoltageMag() * Math.cos(currentBus.getVoltageAng()), currentBus.getVoltageMag() * Math.sin(currentBus.getVoltageAng()));
		        Complex vshc = new Complex(svc.getVsh() * Math.cos(svc.getThedash()), svc.getVsh() * Math.sin(svc.getThedash()));
		        Complex yshc = new Complex(0.0, -5.0);
		        Complex yc = (vic.subtract(vshc)).multiply(yshc).divide(vic);

		        System.out.println("[" + currentBus.getId() + "] Converged B: " + yc.getImaginary() + ", control objective: " + qc);
			  	assertTrue(Math.abs(yc.getImaginary() - qc) < 0.01); 
			  	currentNet = null;
			}
		}
	}

	private AclfNetwork createNet() throws InterpssException, Exception {
		return PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF).load("testdata/ieee_cdf/ieee14.ieee").getAclfNet();
	}
	
}