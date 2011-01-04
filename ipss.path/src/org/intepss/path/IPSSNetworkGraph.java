package org.intepss.path;

import java.util.ArrayList;
import java.util.Collection;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

public class IPSSNetworkGraph {
	private AclfNetwork net;
	private WeightedMultigraph<String, DefaultWeightedEdge> nGraph;
	
	// Constructor, build the network model
	public IPSSNetworkGraph(AclfNetwork net) throws Exception {
		this.net = net;
	}

	// Get the active power digraph
	public WeightedMultigraph<String, DefaultWeightedEdge> getnGraph() {
		// Initialize the digraph
		nGraph = new WeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		// 1. Add all the nodes
		for (Bus thisBus : net.getBusList())
			nGraph.addVertex(thisBus.getId());
		// 2. Add all the edges
		for (Branch thisBranch : net.getBranchList()) {
			String fromId = thisBranch.getFromBusId();
			String toId = thisBranch.getToBusId();
			String branchID = thisBranch.getId();
			AclfBranch thisAclfBranch = net.getAclfBranch(branchID);
			double x = thisAclfBranch.getZ().getImaginary();
			DefaultWeightedEdge thisEdge = new DefaultWeightedEdge();
			nGraph.addEdge(fromId, toId, thisEdge);
			nGraph.setEdgeWeight(thisEdge, x);
		}
		// 3. Remove those nodes with zero degrees
		Collection<String> nodesToBeRemoved = new ArrayList<String>();
		for (String thisNode : nGraph.vertexSet()) {
			if (nGraph.degreeOf(thisNode) == 0) {
				nodesToBeRemoved.add(thisNode);
				System.out.println("Node " + thisNode + " is removed because it is isolated from the network.");
			}
		}
		nGraph.removeAllVertices(nodesToBeRemoved);
		return nGraph;
	}

}