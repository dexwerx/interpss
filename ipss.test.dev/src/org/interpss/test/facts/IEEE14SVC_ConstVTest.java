package org.interpss.test.facts;

import static org.junit.Assert.assertTrue;

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

public class IEEE14SVC_ConstVTest extends DevTestSetup {
	@Test
	public void base_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        
        // run Loadflow
        net.accept(algo);
        assertTrue(net.isLfConverged());
		        
		//System.out.println(net.net2String());
	}

	@Test
	public void base1_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();

        SVCNrSolver svcNrSolver = new SVCNrSolver(net, null);
        algo.setNrSolver(svcNrSolver);
        
        // run Loadflow
        net.accept(algo);
        assertTrue(net.isLfConverged());
		        
		//System.out.println(net.net2String());
	}

	@Test
	public void base2_testCase() throws InterpssException, Exception {
		AclfNetwork net = createNet();
		
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();

        SVCControl[] svcArray = {};
        SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
        algo.setNrSolver(svcNrSolver);
        
        // run Loadflow
        net.accept(algo);
        assertTrue(net.isLfConverged());
		        
		//System.out.println(net.net2String());
	}

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
		
        AclfBus bus = net.getAclfBus("Bus14");
        assertTrue(bus != null);

        SVCControl svc = new SVCControl(bus, net.getNoBus()+1, SVCControlType.ConstV);

        double vc = 1.035; // voltage ref for the bus, without SVC the bus = 1.0355

        // this is confusing. We should have a method svc.setVref(vc)
        svc.setQc(vc);
        svc.setYsh(0.0, -5.0);
        
        svc.setLoad(bus.getLoad()); // set Load on the SVC bus

        // set svc as AclfBus extension
        bus.setExtensionObject(svc);

        // init SVC states
        svc.init();
        
        SVCControl[] svcArray = {svc};
        SVCNrSolver svcNrSolver = new SVCNrSolver(net, svcArray);
		
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // set algo NR solver to the CustomNrSolver
        algo.setNrSolver(svcNrSolver);
        
        // run Loadflow
        net.accept(algo);
        assertTrue(net.isLfConverged());
		        
		//System.out.println(net.net2String());
	}
	
	private AclfNetwork createNet() throws InterpssException, Exception {
		return PluginObjectFactory
			.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
			.load("testdata/ieee_cdf/ieee14.ieee")
			.getAclfNet();
	}
	
}