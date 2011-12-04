package org.interpss.test.lf;

import static org.junit.Assert.assertTrue;

import org.interpss.custom.IpssFileAdapter;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;

public class IEEE14Test extends DevTestSetup {

	@Test
	public void test() throws Exception {
		IpssFileAdapter adapter = PluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee14a.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
        boolean isConverged = algo.loadflow();
        for (Bus thisBus : net.getBusList()) {
        	System.out.println("Voltage of bus " + thisBus.getId() + ": " + net.getAclfBus(thisBus.getId()).getVoltageMag());
        }
        assertTrue(isConverged);
	}

}
