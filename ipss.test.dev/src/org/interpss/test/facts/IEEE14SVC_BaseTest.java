package org.interpss.test.facts;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.facts.SVCControl;
import org.interpss.facts.SVCNrSolver;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;

public class IEEE14SVC_BaseTest extends DevTestSetup {
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

	private AclfNetwork createNet() throws InterpssException, Exception {
		return PluginObjectFactory
			.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
			.load("testdata/ieee_cdf/ieee14.ieee")
			.getAclfNet();
	}
	
}