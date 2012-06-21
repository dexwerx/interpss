package org.interpss.sample.gml;

import java.util.List;

import org.graphdrawing.gml.GraphType;
import org.graphdrawing.gml.GraphmlType;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.spring.EditorPluginSpringFactory;

import com.interpss.CoreObjectFactory;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.msg.impl.IPSSMsgHubImpl;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringFactory;

public class IEEE14GmlImpl {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// InterPSS core simulation engine configuration
		CoreCommonSpringFactory.setAppContext(new String[] {IpssCorePlugin.CtxPath});

    	// Build the base case network
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee14.ieee");
		AclfNetwork adjNet = simuCtx.getAclfNet();
		
		// load the Gml file
		GraphmlType gml = GmlHelper.load("testData/gml/sample_001.gml");
		
		// retrieve all Graph objects, there may be more than one graphs
		List<GraphType> glist = GmlHelper.getGraphObjects(gml);
		
		for (GraphType graph : glist) {
			// for each graph object, create a sub network
			AclfNetwork subNet = GmlHelper.createSubNet(adjNet, graph);

			// Define LF algorithm for the sub network 
			IPSSMsgHub msg = new IPSSMsgHubImpl();
		  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(subNet);
		  	algo.setNonDivergent(true);
		  	//algo.setLfMethod(AclfMethod.PQ);
		  	
		  	// perform loadflow to the sub network
		  	algo.loadflow();
		  	System.out.println(AclfOutFunc.loadFlowSummary(subNet));
		}
	}
}
