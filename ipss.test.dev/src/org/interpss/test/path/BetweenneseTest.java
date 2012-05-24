package org.interpss.test.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.intepss.path.EdgeBetweennessDigraph;
import org.intepss.path.IPSSActivePowerDigraph;
import org.interpss.CorePluginObjFactory;
import org.interpss.IpssPlugin;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.test.DevTestSetup;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

public class BetweenneseTest extends DevTestSetup {
	@Test
	public void testCase1() throws Exception {
		IpssPlugin.init();
		AclfNetwork net = CorePluginObjFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF).load("testdata/ieee_cdf/ieee118cdf.txt").getAclfNet();
	    LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		net.accept(algo);
		IPSSActivePowerDigraph afd = new IPSSActivePowerDigraph(net);
		EdgeBetweennessDigraph ebd = new EdgeBetweennessDigraph(afd.getpDigraph());
		// Output all the edge betweenness descendingly
		// 1. Create the map between the edge and the corresponding weight
		HashMap<DefaultWeightedEdge, Double> mapEdgeBetweenness = new HashMap<DefaultWeightedEdge, Double>();
		for (DefaultWeightedEdge thisEdge : ebd.edgeSet()) {
			double thisBetweenness = ebd.getEdgeWeight(thisEdge);
			mapEdgeBetweenness.put(thisEdge, thisBetweenness);
		}
		// 2. Sort the edges with the weights descendingly
		// 2.2.1. The weight is the distance between current vertex and the first vertex
		List<Map.Entry<DefaultWeightedEdge, Double>> listEdgeBetweenness = 
			new ArrayList<Map.Entry<DefaultWeightedEdge, Double>>(mapEdgeBetweenness.entrySet());
		Collections.sort(listEdgeBetweenness, new Comparator<Map.Entry<DefaultWeightedEdge, Double>>() {	// Sorting the list descendingly
			@Override
			public int compare(Entry<DefaultWeightedEdge, Double> o1, Entry<DefaultWeightedEdge, Double> o2) {
				return (o2.getValue().compareTo(o1.getValue()));
			}
		});
	}
}
