package org.interpss.test.path;

import org.intepss.path.CommunityDetection;
import org.intepss.path.IPSSNetworkGraph;
import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;

public class CommunityTest118Network {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws InterpssException 
	 */
	public static void main(String[] args) throws InterpssException, Exception {
		IpssCorePlugin.init();
		AclfNetwork net = CorePluginObjFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF).load("testdata/ieee_cdf/ieee118cdf.txt").getAclfNet();
		IPSSNetworkGraph ng = new IPSSNetworkGraph(net);
//		IPSSActivePowerDigraph apd = new IPSSActivePowerDigraph("testdata/ieee_cdf/ieee14.ieee");
		CommunityDetection cd = new CommunityDetection(ng.getnGraph(), net);
//		cd.detectingCommunities();
		cd.detectingWeightedUndirectedCommunities();
//		cd.detectingWeightedDirectedCommunities();
//		cd.saveCommunityResults("community_sz_weighted_normal.txt", cd.getResult());
//		cd.saveQResults("q_sz_weighted_normal.txt", cd.getqSet());
		cd.saveCommunityResults("community_118_network.txt", cd.getResult());
		cd.saveQResults("q_118_network.txt", cd.getqSet());
	}

}