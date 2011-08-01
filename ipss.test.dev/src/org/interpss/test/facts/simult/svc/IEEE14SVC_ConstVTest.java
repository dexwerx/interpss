package org.interpss.test.facts.simult.svc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
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
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

public class IEEE14SVC_ConstVTest extends DevTestSetup {
	/*
	 * Issue
	 * 	[12/17/2010] 
	 *     - We should have a method svc.setVref(vc) for ConstV
	 *     - Add documentation to SVCControl
	 *     - Set a break-point at the SVCContrl.mismatch() method
	 *            Now for the first iteration getBi() = (-1.3815418792701684 + j0.3746548473785305)
	 *            which is not right. The real-part should be always small.
	 */
	@Test
	public void singleConstV_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus4");
        // Issue: the initial value of the controlled voltage must be exactly the same as the controlled value, 
        // this is strange but always be true in tested cases.
        bus.setVoltageMag(1.05);
        assertTrue(bus != null);

        SVCSimultLF svc = new SVCSimultLF(bus, net.getNoBus(), SVCControlType.ConstV);

        double vc = 1.05; // voltage ref for the bus, without SVC the bus = 1.0355

        // this is confusing. We should have a method svc.setVref(vc)
        svc.setQc(vc);
        svc.setYsh(0.0, -5.0);
        
        svc.setLoad(bus.getLoad()); // set Load on the SVC bus

        // set svc as AclfBus extension
        bus.setExtensionObject(svc);

        // init SVC states
        svc.init();
        
        SVCSimultLF[] svcArray = {svc};
        SVCSimultSolver svcNrSolver = new SVCSimultSolver(net, svcArray);
		
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // set algo NR solver to the CustomNrSolver
        algo.setNrSolver(svcNrSolver);
        
        // run Loadflow
        net.accept(algo);
        assertTrue(net.isLfConverged());
        assertTrue(Math.abs(svc.getParentAclfBus().getVoltageMag() - 1.05) < 0.0001);
		        
		//System.out.println(net.net2String());
	}
	
	@Test
	public void singleConstQ_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus4");
        assertTrue(bus != null);

        SVCSimultLF svc = new SVCSimultLF(bus, net.getNoBus(), SVCControlType.ConstQ);

        double qc = 0.1;

        // this is confusing. We should have a method svc.setVref(vc)
        svc.setQc(qc);
        svc.setYsh(0.0, -5.0);
        
        svc.setLoad(bus.getLoad()); // set Load on the SVC bus

        // set svc as AclfBus extension
        bus.setExtensionObject(svc);

        // init SVC states
        svc.init();
        
        SVCSimultLF[] svcArray = {svc};
        SVCSimultSolver svcNrSolver = new SVCSimultSolver(net, svcArray);
		
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // set algo NR solver to the CustomNrSolver
        algo.setNrSolver(svcNrSolver);
        algo.setMaxIterations(100);
        
        // run Loadflow
        net.accept(algo);
        assertTrue(net.isLfConverged());
        
        double vi = svc.getParentAclfBus().getVoltageMag(), thetai = svc.getParentAclfBus().getVoltageAng();
        double vsh = svc.getVsh(), thetash = svc.getThedash();
        double gsh = 0.0, bsh = -5.0;
        double qsh = -(vi * vi * bsh + vi * vsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash)));
        assertTrue(Math.abs(qsh - 0.1) < 0.0001);
		        
		//System.out.println(net.net2String());
	}
	
	@Test
	public void singleConstB_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		
        AclfBus bus = net.getAclfBus("Bus4");
        assertTrue(bus != null);

        SVCSimultLF svc = new SVCSimultLF(bus, net.getNoBus(), SVCControlType.ConstB);

        double qc = 0.1;

        // this is confusing. We should have a method svc.setVref(vc)
        svc.setQc(qc);
        svc.setYsh(0.0, -5.0);
        
        svc.setLoad(bus.getLoad()); // set Load on the SVC bus

        // set svc as AclfBus extension
        bus.setExtensionObject(svc);

        // init SVC states
        svc.init();
        
        SVCSimultLF[] svcArray = {svc};
        SVCSimultSolver svcNrSolver = new SVCSimultSolver(net, svcArray);
		
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // set algo NR solver to the CustomNrSolver
        algo.setNrSolver(svcNrSolver);
        algo.setMaxIterations(100);
        
        // run Loadflow
        net.accept(algo);
        assertTrue(net.isLfConverged());
        
        Complex vic = new Complex(bus.getVoltageMag() * Math.cos(bus.getVoltageAng()), bus.getVoltageMag() * Math.sin(bus.getVoltageAng()));
        Complex vshc = new Complex(svc.getVsh() * Math.cos(svc.getThedash()), svc.getVsh() * Math.sin(svc.getThedash()));
        Complex yshc = new Complex(0.0, -5.0);
        Complex yc = (vic.subtract(vshc)).multiply(yshc).divide(vic);
	  	assertTrue(Math.abs(yc.getImaginary() - 0.1) < 0.0005); 
		        
		//System.out.println(net.net2String());
	}
	
	private AclfNetwork createNet() throws InterpssException, Exception {
		return PluginObjectFactory
			.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
			.load("testdata/ieee_cdf/ieee14.ieee")
			.getAclfNet();
	}
	
}