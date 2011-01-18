package org.interpss.test.eurostag;

import junit.framework.TestCase;

import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;

public class EurostagImporterTest extends TestCase {
	@Test
	public final void testImportEurostagLoadflowModel_SIM1() throws InterpssException {
		EurostagImporter ei = new EurostagImporter("testData/eurostag/sim1");
		AclfNetwork network = ei.ImportEurostagLoadflowModel();
		
		// print out the network object
        System.out.println(network.net2String());
		
/*		
//		System.out.println(network.get);
        // create a Loadflow algo object
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
        // run Loadflow
        network.accept(algo);
        System.out.println(algo.getNrSolver().formJMatrix());
		assertTrue(network.isLfConverged());
*/		
	}

}