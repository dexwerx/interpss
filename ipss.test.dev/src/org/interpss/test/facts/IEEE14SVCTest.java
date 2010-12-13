package org.interpss.test.facts;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.facts.SVCControl;
import org.interpss.facts.SVCControlType;
import org.interpss.facts.SVCNrSolver;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.core.net.Bus;

public class IEEE14SVCTest extends DevTestSetup {

	@Test
	public void singleConstV_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		for (Bus bus : net.getBusList()) {
			AclfBus thisBus = net.getAclfBus(bus.getId());
			if (thisBus.isLoad()) {
				System.out.println(bus.getId() + ": " + thisBus.getVoltageMag());
		        SVCControl svc = new SVCControl(thisBus, net.getNoBus()+1, SVCControlType.ConstV);
		        double vc = thisBus.getVoltageMag();
		        svc.setQc(vc);
		        svc.setYsh(0.0, -5.0);
		        svc.setLoad(thisBus.getLoad()); // set Load on the SVC bus

		        // set svc as AclfBus extension
		        bus.setExtensionObject(svc);
		        
		        SVCControl[] svcArray = {svc};
		        SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
		        
		        // create a Loadflow algo object
		        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		        // set algo NR solver to the CustomNrSolver
		        algo.setNrSolver(svcNrSolver);

		        // run Loadflow
		        net.accept(algo);
		        
		        System.out.println("Converged voltage: " + thisBus.getVoltageMag() + ", control objective: " + vc);
			  	assertTrue(Math.abs(thisBus.getVoltageMag() - vc) < 0.00001); 
			}
		}
	}
	
	@Test
	public void singleConstQ_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		for (Bus bus : net.getBusList()) {
			AclfBus thisBus = net.getAclfBus(bus.getId());
			if (thisBus.isLoad()) {
		        SVCControl svc = new SVCControl(thisBus, net.getNoBus()+1, SVCControlType.ConstQ);
		        double qc = 0.5;
		        svc.setQc(qc);
		        svc.setYsh(0.0, -5.0);
		        svc.setLoad(thisBus.getLoad()); // set Load on the SVC bus

		        // set svc as AclfBus extension
		        bus.setExtensionObject(svc);
		        
		        SVCControl[] svcArray = {svc};
		        SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
		        
		        // create a Loadflow algo object
		        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		        // set algo NR solver to the CustomNrSolver
		        algo.setNrSolver(svcNrSolver);

		        // run Loadflow
		        net.accept(algo);
		        
		        double vi = thisBus.getVoltageMag();
		        double vsh = svc.getVsh();
		        double thetai = thisBus.getVoltageAng();
		        double thetash = svc.getThedash();
		        double gsh = 0.0, bsh = -5.0;
		        double qsh = -(vi * vi * bsh + vi * vsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash)));
		        System.out.println("[" + bus.getId() + "] Converged Q: " + qsh + ", control objective: " + qc);
			  	assertTrue(Math.abs(qsh - qc) < 0.005); 
			}
		}
	}
	
	@Test
	public void singleConstB_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		for (Bus bus : net.getBusList()) {
			AclfBus thisBus = net.getAclfBus(bus.getId());
			if (thisBus.isLoad()) {
		        SVCControl svc = new SVCControl(thisBus, net.getNoBus()+1, SVCControlType.ConstB);
		        double qc = 0.05;
		        svc.setQc(qc);
		        svc.setYsh(0.0, -5.0);
		        svc.setLoad(thisBus.getLoad()); // set Load on the SVC bus

		        // set svc as AclfBus extension
		        bus.setExtensionObject(svc);
		        
		        SVCControl[] svcArray = {svc};
		        SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
		        
		        // create a Loadflow algo object
		        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		        // set algo NR solver to the CustomNrSolver
		        algo.setNrSolver(svcNrSolver);

		        // run Loadflow
		        net.accept(algo);

		        Complex vic = new Complex(thisBus.getVoltageMag() * Math.cos(thisBus.getVoltageAng()), thisBus.getVoltageMag() * Math.sin(thisBus.getVoltageAng()));
		        Complex vshc = new Complex(svc.getVsh() * Math.cos(svc.getThedash()), svc.getVsh() * Math.sin(svc.getThedash()));
		        Complex yshc = new Complex(0.0, -5.0);
		        Complex yc = (vic.subtract(vshc)).multiply(yshc).divide(vic);

		        System.out.println("[" + bus.getId() + "] Converged Q: " + yc.getImaginary() + ", control objective: " + qc);
			  	assertTrue(Math.abs(yc.getImaginary() - qc) < 0.005); 
			}
		}
	}

	private AclfNetwork createNet() throws InterpssException, Exception {
		// TODO Auto-generated method stub
		return PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF).load("testdata/ieee_cdf/ieee14.ieee").getAclfNet();
	}
	
}