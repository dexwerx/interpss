package org.interpss.test.lf;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;

public class EQ07CTest {
	@Test
	public void testCaseNR() throws Exception {
	IpssFileAdapter adapter = PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.PSSE);
	
	SimuContext simuCtx = adapter.load("testData/psse/o7c.raw");
		System.out.println("End loading data ...");

	AclfNetwork net = simuCtx.getAclfNet();
		//System.out.println(net.net2String());
//		assertTrue((net.getBusList().size() == 1824));

  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
  	algo.getLfAdjAlgo().setApplyAdjustAlgo(false);
  	algo.loadflow();
		//System.out.println(net.net2String());

		assertTrue(net.isLfConverged());
	}
}
