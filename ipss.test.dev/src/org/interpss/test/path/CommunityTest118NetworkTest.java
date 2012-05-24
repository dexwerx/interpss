package org.interpss.test.path;

import static org.junit.Assert.assertTrue;

import org.intepss.path.CommunityDetection;
import org.intepss.path.IPSSNetworkGraph;
import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.numeric.util.NumericUtil;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;

public class CommunityTest118NetworkTest extends DevTestSetup {

	@Test
	public void test1() throws Exception {
		IpssCorePlugin.init();
		AclfNetwork net = CorePluginObjFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testdata/ieee_cdf/ieee118cdf.txt")
				.getAclfNet();

		IPSSNetworkGraph ng = new IPSSNetworkGraph(net);
		CommunityDetection cd = new CommunityDetection(ng.getnGraph(), net);
		cd.detectingWeightedUndirectedCommunities();

		//cd.saveCommunityResults("community_118_network.txt", cd.getResult());
		//cd.saveQResults("q_118_network.txt", cd.getqSet());
		
		//System.out.println(cd.getqSet().size());
		//System.out.println(cd.getqSet());
		
		// make sure the total items = 67
		assertTrue(cd.getqSet().size() == 67);
		
		double x = (Double)(cd.getqSet().toArray()[2]);
		assertTrue(NumericUtil.equals(x, 0.12072, 0.0001));
		
		x = (Double)(cd.getqSet().toArray()[3]);
		assertTrue(NumericUtil.equals(x, 0.416694, 0.0001));
	}

}