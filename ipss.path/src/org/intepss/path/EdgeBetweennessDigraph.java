package org.intepss.path;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;

import org.interpss.IpssPlugin;
import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// Class to calculate edge betweenness
// ****** Key idea: the bigger the active power flow is, the closer the two terminal of a certain branch is, on another word, the more important
// this branch is, if edge betweennesses will be calculated by shortest path, it is vital to use the inverse other than the active power directly
public class EdgeBetweennessDigraph extends DirectedWeightedMultigraph<String, DefaultWeightedEdge> {

	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EdgeBetweennessDigraph(DirectedWeightedMultigraph<String, DefaultWeightedEdge> originalDigraph) {
		
		super(DefaultWeightedEdge.class);	// Constructor inherented from parent class
		
		// 1. Initialize the edge betweenness digraph
		// 1.1. Add vertices
		for (String thisVertex : originalDigraph.vertexSet())
			this.addVertex(thisVertex);
		// 1.2. Add edges
		for (DefaultWeightedEdge thisEdge : originalDigraph.edgeSet()) {
			DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
			String sendingName = originalDigraph.getEdgeSource(thisEdge);
			String receivingName = originalDigraph.getEdgeTarget(thisEdge);
			this.addEdge(sendingName, receivingName, newEdge);
			this.setEdgeWeight(newEdge, 0.0);
		}
		// 1.3. Create corresponding unweighted graph
		// 1.3.1. Add vertices
		DirectedWeightedMultigraph<String, DefaultWeightedEdge> originalUnweightedDigraph = new DirectedWeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		for (String thisVertex : originalDigraph.vertexSet())
			originalUnweightedDigraph.addVertex(thisVertex);
		// 1.3.2. Add edges
		for (DefaultWeightedEdge thisEdge : originalDigraph.edgeSet()) {
			DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
			String sendingName = originalDigraph.getEdgeSource(thisEdge);
			String receivingName = originalDigraph.getEdgeTarget(thisEdge);
			originalUnweightedDigraph.addEdge(sendingName, receivingName, newEdge);
			originalUnweightedDigraph.setEdgeWeight(newEdge, 1.0);
		}
		
		for (String thisVertex : this.vertexSet()) {
			// 2. Create shortest-path graph for every vertex by Dijkstra algorithm
			DirectedMultigraph<String, DefaultEdge> shortestPathDigraph = DijkstraAlgorithm(originalUnweightedDigraph, thisVertex);
			
			// 3. Calculate the contribution of current vertex's shortest-path graph to final edge betweenness (Newman algorithm)
			SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> edgeBetweennessDigraph = NewmanAlgorithm(shortestPathDigraph, thisVertex);
			
			// 4. Update final edge betweenness digraph with current vertex's shortest-path graph
			if (edgeBetweennessDigraph != null)
				for (DefaultWeightedEdge thisEdge : edgeBetweennessDigraph.edgeSet()) {
					String sourceVertex = edgeBetweennessDigraph.getEdgeSource(thisEdge);
					String targetVertex = edgeBetweennessDigraph.getEdgeTarget(thisEdge);
					double betweennessWeight = edgeBetweennessDigraph.getEdgeWeight(thisEdge);
					DefaultWeightedEdge edgeInWholeGraph = this.getEdge(sourceVertex, targetVertex);
					double newWeight = this.getEdgeWeight(edgeInWholeGraph) + betweennessWeight;
					this.setEdgeWeight(edgeInWholeGraph, newWeight);
				}
		}
		
		// 5. Revise the edge betweenness digraph with the original active power digraph
		for (DefaultWeightedEdge thisEdge : this.edgeSet()) {
			String sourceVertex = this.getEdgeSource(thisEdge);
			String targetVertex = this.getEdgeTarget(thisEdge);
			double betweennessWeight = this.getEdgeWeight(thisEdge);
			DefaultWeightedEdge edgeInWholeGraph = originalDigraph.getEdge(sourceVertex, targetVertex);
			double newWeight =  betweennessWeight / originalDigraph.getEdgeWeight(edgeInWholeGraph);
			this.setEdgeWeight(thisEdge, newWeight);
//			System.out.println("Final edge betweenness between " + sourceVertex + " and " + targetVertex + ": " + newWeight);
		}
		
	}

	// Calculate the contribution of current vertex's shortest-path graph to final edge betweenness (Newman algorithm)
	// M. E. J. Newman and M. Girvan, Finding and evaluating community structure in networks, Physical Review E69, 026113 (2004)
	private SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> NewmanAlgorithm(
			DirectedMultigraph<String, DefaultEdge> shortestPathDigraph, String thisVertex) {
		
		if (shortestPathDigraph.edgeSet().size() == 0)	// No outer degree
			return null;
		
		// 1. Initialize the edge betweenness digraph
		SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> edgeBetweennessDigraph = 
			new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		// Add vertices
		for (String curVertex : shortestPathDigraph.vertexSet())
			edgeBetweennessDigraph.addVertex(curVertex);
		
		// 2. Deal with corresponding edges
		// 2.1. Calculate vertex distance and weight
		// 2.1.1. Initialize related variables
		HashMap<String, Integer> mapVertexDistance = new HashMap<String, Integer>();	// Vertex distance
		for (String curVertex : shortestPathDigraph.vertexSet())
			mapVertexDistance.put(curVertex, 0);
		HashMap<String, Double> mapVertexWeight = new HashMap<String, Double>();	// Vertex weight
		for (String curVertex : shortestPathDigraph.vertexSet())
			mapVertexWeight.put(curVertex, 1.0);
		Queue<String> verticesQueue = new LinkedList<String>();	// Store the queue of operated vertices
		// 2.1.2. Traverse the shortest-path digraph, get corresponding vertex distance and weight
		verticesQueue.add(thisVertex);
		String currentVertex = thisVertex;
		while (!verticesQueue.isEmpty()) {
			// 2.1.2.1. Read the bottom vertex in the queue, deal with all adjacent edges
			for (DefaultEdge curEdge : shortestPathDigraph.outgoingEdgesOf(currentVertex)) {
				String targetVertex = shortestPathDigraph.getEdgeTarget(curEdge);
				if (mapVertexDistance.get(targetVertex) == 0) {	// Target vertex value hasn't been evaluated yet
					mapVertexDistance.put(targetVertex, mapVertexDistance.get(currentVertex) + 1);
					mapVertexWeight.put(targetVertex, mapVertexWeight.get(currentVertex));
					verticesQueue.add(targetVertex);
				}
				// Target vertex value has already been set to source vertex distance plus 1
				else if (mapVertexDistance.get(targetVertex) == (mapVertexDistance.get(currentVertex) + 1))
						mapVertexWeight.put(targetVertex, mapVertexWeight.get(targetVertex) + mapVertexWeight.get(currentVertex));
			}
			// 2.1.2.2. All adjacent edges have been treated, remove the bottom element
			verticesQueue.remove();
			currentVertex = verticesQueue.peek();
		}
		
		// 2.2. Calculate the contribution of every edges to the edge betweenness
		// 2.2.1. Sort the vertices descendingly by the distance to the first vertex
		List<Map.Entry<String, Integer>> listVertexDistance = new ArrayList<Map.Entry<String, Integer>>(mapVertexDistance.entrySet());
		Collections.sort(listVertexDistance, new Comparator<Map.Entry<String, Integer>>() {	// Sorting the list descendingly

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return (o2.getValue().compareTo(o1.getValue()));
			}
			
		});
		// 2.2.2. Traverse the shortest-path digraph with the sorted vertices list
		for (Map.Entry<String, Integer> thisVertexDistance : listVertexDistance) {
			currentVertex = thisVertexDistance.getKey();
			for (DefaultEdge curEdge : shortestPathDigraph.incomingEdgesOf(currentVertex)) {
				// 2.2.2.1. Get the source vertex
				String sourceVertex = shortestPathDigraph.getEdgeSource(curEdge);
				// 2.2.2.2. Calculate the value of the sum of all adjacent edges plus 1
				double totalSubWeights = 1.0;
				for (DefaultWeightedEdge curSubEdge : edgeBetweennessDigraph.outgoingEdgesOf(currentVertex))
					totalSubWeights += edgeBetweennessDigraph.getEdgeWeight(curSubEdge);
				// 2.2.2.3. The new edge weight is calculated as totalSubWeights * (the quotient of the weights of the two terminals)
				double wi = mapVertexWeight.get(sourceVertex);
				double wj = mapVertexWeight.get(currentVertex);
				totalSubWeights /= wi / wj;
				// 2.2.2.4. Add new edge with correct weight into the edge betweenness digraph
				DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
				edgeBetweennessDigraph.addEdge(sourceVertex, currentVertex, newEdge);
				edgeBetweennessDigraph.setEdgeWeight(newEdge, totalSubWeights);
			}
		}
		
		return edgeBetweennessDigraph;
	}

	// Create shortest-path graph for every vertex by depth-first traversal algorithm
	private DirectedMultigraph<String, DefaultEdge> DijkstraAlgorithm(DirectedWeightedMultigraph<String, DefaultWeightedEdge> originalDigraph, 
			String thisVertex) {
	
		// 1. Simplify the multi-graph of the active power flow into a simple graph
		SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> originalSimpleGraph = 
			new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		for (String curVertex : originalDigraph.vertexSet())
			originalSimpleGraph.addVertex(curVertex);
		for (DefaultWeightedEdge curEdge : originalDigraph.edgeSet()) {
			String sourceVertex = originalDigraph.getEdgeSource(curEdge);
			String targetVertex = originalDigraph.getEdgeTarget(curEdge);
			if (originalSimpleGraph.containsEdge(sourceVertex, targetVertex)) {
				DefaultWeightedEdge modifiedEdge = originalSimpleGraph.getEdge(sourceVertex, targetVertex);
				double newEdgeWeight = originalSimpleGraph.getEdgeWeight(modifiedEdge) + originalDigraph.getEdgeWeight(curEdge); 
				originalSimpleGraph.setEdgeWeight(modifiedEdge, newEdgeWeight);
			}
			else {
				DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
				originalSimpleGraph.addEdge(sourceVertex, targetVertex, newEdge);
				originalSimpleGraph.setEdgeWeight(newEdge, originalDigraph.getEdgeWeight(curEdge));
			}
		}
		// Issue (2010/10/25): Maybe larger amount of active power transfer still means weaker relationship between the two terminal buses of a certain branch,
		// thus originalSimpleGraph other than inverseGraph should be used here.
		// Use the inverse of active power to build a new weighted directed graph (the larger the active power is, the close the two buses will be)
//		SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> inverseGraph = 
//			new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
//		for (String curVertex : originalSimpleGraph.vertexSet())
//			inverseGraph.addVertex(curVertex);
//		for (DefaultWeightedEdge curEdge : originalSimpleGraph.edgeSet()) {
//			String sourceVertex = originalSimpleGraph.getEdgeSource(curEdge);
//			String targetVertex = originalSimpleGraph.getEdgeTarget(curEdge);
//			DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
//			inverseGraph.addEdge(sourceVertex, targetVertex, newEdge);
//			inverseGraph.setEdgeWeight(newEdge, 1 / originalSimpleGraph.getEdgeWeight(curEdge));
//		}
		// 2. Initialize the map of vertices and the corresponding weights (distance from current vertex to the first vertex)
		HashMap<String, Double> mapVertexShortestDistance = new HashMap<String, Double>();
//		for (String thisOriginalVertex : inverseGraph.vertexSet())
		for (String thisOriginalVertex : originalSimpleGraph.vertexSet())
			mapVertexShortestDistance.put(thisOriginalVertex, 10E10);
		// The weight of the first vertex is zero
		mapVertexShortestDistance.put(thisVertex, 0.0);
		
		// 3. Depth-first traversing, update the shortest-path values
		Stack<String> bfiVertices = new Stack<String>();	// Stack to store passed vertices in a breadth-first traversing
		// The map of a weighted edge and the flag of having been visited
//		HashMap<DefaultWeightedEdge, Boolean> mapEdgeVisited = new HashMap<DefaultWeightedEdge, Boolean>();
//		for (DefaultWeightedEdge thisEdge : inverseGraph.edgeSet())
//			mapEdgeVisited.put(thisEdge, false);
		String currentVertex = thisVertex;
		bfiVertices.push(currentVertex);
//		System.out.println(bfiVertices.toString());
		while (!bfiVertices.isEmpty()) {
			// Operate the following codes for those edges started with current vertex
			boolean hasNewEdge = false;
//			for (DefaultWeightedEdge curEdge : inverseGraph.outgoingEdgesOf(currentVertex)) {
			for (DefaultWeightedEdge curEdge : originalSimpleGraph.outgoingEdgesOf(currentVertex)) {
//				if (!mapEdgeVisited.get(curEdge)) {	// Used for those edges that have not been treated yet
					// 3.1. Mark current edge as already been visited
//					mapEdgeVisited.put(curEdge, true);
//				String nextVertex = inverseGraph.getEdgeTarget(curEdge);
					String nextVertex = originalSimpleGraph.getEdgeTarget(curEdge);
					// 3.2. Update shortest-path values
					double curSD = mapVertexShortestDistance.get(currentVertex);
//					double edgeWeight = inverseGraph.getEdgeWeight(curEdge);
					double edgeWeight = originalSimpleGraph.getEdgeWeight(curEdge);
					double newSD = curSD + edgeWeight;
					if (mapVertexShortestDistance.get(nextVertex) > newSD) {
						hasNewEdge = true;
						mapVertexShortestDistance.put(nextVertex, newSD);
						// 3.3. Push the target vertex of current edge into the stack
						bfiVertices.push(nextVertex);
//						System.out.println(bfiVertices.toString());
						break;
//						System.out.println("New shortest path [" + nextVertex + "]: " + newSD);
					}
//				}
			}
			if (!hasNewEdge) {
				bfiVertices.pop();
			}
			if (!bfiVertices.isEmpty())
				currentVertex = bfiVertices.peek();
		}
		// 4. Create shortest-path digraph of current vertex
		// 4.1. Initialize the shortest-path digraph
		DirectedMultigraph<String, DefaultEdge> shortestPathDigraph = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
		// 4.2. Add all qualified edges
//		for (DefaultWeightedEdge curEdge : inverseGraph.edgeSet()) {
		for (DefaultWeightedEdge curEdge : originalSimpleGraph.edgeSet()) {
			// 4.2.1. Evaluate if current edge is suitable
//			String sourceVertex = inverseGraph.getEdgeSource(curEdge);
//			String targetVertex = inverseGraph.getEdgeTarget(curEdge);
			String sourceVertex = originalSimpleGraph.getEdgeSource(curEdge);
			String targetVertex = originalSimpleGraph.getEdgeTarget(curEdge);
//			if (Math.abs(inverseGraph.getEdgeWeight(curEdge) - 
			if (originalSimpleGraph.getEdgeWeight(curEdge) > 1.0E-5) {
				if (Math.abs(originalSimpleGraph.getEdgeWeight(curEdge) - 
						(mapVertexShortestDistance.get(targetVertex) - mapVertexShortestDistance.get(sourceVertex))) < 1.0E-5) {
						// 4.2.2. Add suitable edge that found just now
						DefaultEdge newEdge = new DefaultEdge();
						if (!shortestPathDigraph.containsVertex(sourceVertex))
							shortestPathDigraph.addVertex(sourceVertex);
						if (!shortestPathDigraph.containsVertex(targetVertex))
							shortestPathDigraph.addVertex(targetVertex);
						shortestPathDigraph.addEdge(sourceVertex, targetVertex, newEdge);
					}
			}
		}
		return shortestPathDigraph;
	}
	
//	// Test of the Dijkstra Algorithm
//	private void TestDijkstra() {
//		// 1. Create the weighted digraph
//		// 1.1. Initialise the graph
//		DirectedWeightedMultigraph<String, DefaultWeightedEdge> testGraph = 
//			new DirectedWeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
//		// 1.2. Add nodes
//		for (int i = 1; i <= 6; i++)
//			testGraph.addVertex((new Integer(i)).toString());
//		// 1.3. Add edges
//		DefaultWeightedEdge edge1 = new DefaultWeightedEdge();
//		testGraph.addEdge("1", "2", edge1);
//		testGraph.setEdgeWeight(edge1, new Double(1.0 / 6.0));
//		DefaultWeightedEdge edge2 = new DefaultWeightedEdge();
//		testGraph.addEdge("1", "3", edge2);
//		testGraph.setEdgeWeight(edge2, new Double(1.0 / 4.0));
//		DefaultWeightedEdge edge3 = new DefaultWeightedEdge();
//		testGraph.addEdge("2", "3", edge3);
//		testGraph.setEdgeWeight(edge3, new Double(1.0 / 2.0));
//		DefaultWeightedEdge edge4 = new DefaultWeightedEdge();
//		testGraph.addEdge("2", "4", edge4);
//		testGraph.setEdgeWeight(edge4, new Double(1.0 / 2.0));
//		DefaultWeightedEdge edge5 = new DefaultWeightedEdge();
//		testGraph.addEdge("3", "4", edge5);
//		testGraph.setEdgeWeight(edge5, new Double(1.0 / 1.0));
//		DefaultWeightedEdge edge6 = new DefaultWeightedEdge();
//		testGraph.addEdge("3", "5", edge6);
//		testGraph.setEdgeWeight(edge6, new Double(1.0 / 2.0));
//		DefaultWeightedEdge edge7 = new DefaultWeightedEdge();
//		testGraph.addEdge("4", "6", edge7);
//		testGraph.setEdgeWeight(edge7, new Double(1.0 / 7.0));
//		DefaultWeightedEdge edge8 = new DefaultWeightedEdge();
//		testGraph.addEdge("5", "4", edge8);
//		testGraph.setEdgeWeight(edge8, new Double(1.0 / 1.0));
//		DefaultWeightedEdge edge9 = new DefaultWeightedEdge();
//		testGraph.addEdge("5", "6", edge9);
//		testGraph.setEdgeWeight(edge9, new Double(1.0 / 3.0));
//
//		// 2. Test the algorithm
//		for (String thisVertex : testGraph.vertexSet()) {
//			DirectedMultigraph<String, DefaultEdge> spd1 = DijkstraAlgorithm(testGraph, thisVertex);
//			System.out.println(spd1.toString());
//		}
//	}
	
	// Output the histogram of all the edge betweenness
	public void histogramOfEdgeBetweenness() {
		// TODO
	}

	public static void main(String[] args) throws Exception {
		
		
		IpssPlugin.init();
//		IPSSActivePowerDigraph afd = new IPSSActivePowerDigraph("testdata/ieee_cdf/ieee14.ieee");
		AclfNetwork net = PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF).load("testdata/ieee_cdf/ieee118cdf.txt").getAclfNet();
	    LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		net.accept(algo);
		IPSSActivePowerDigraph afd = new IPSSActivePowerDigraph(net);
		EdgeBetweennessDigraph ebd = new EdgeBetweennessDigraph(afd.getpDigraph());
//		ebd.TestDijkstra();
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
		// 3. Output to result file
		Collection<String> colEdgeBetweenness = new ArrayList<String>();
		for (Map.Entry<DefaultWeightedEdge, Double> thisEntry : listEdgeBetweenness) {
			DefaultWeightedEdge thisEdge = thisEntry.getKey();
			double thisWeight = thisEntry.getValue();
			String sourceVertex = ebd.getEdgeSource(thisEdge);
			while (sourceVertex.length() < 10)
				sourceVertex = sourceVertex + " ";
			String targetVertex = ebd.getEdgeTarget(thisEdge);
			while (targetVertex.length() < 10)
				targetVertex = targetVertex + " ";
			String thisResult = sourceVertex + " " + targetVertex + " " + thisWeight;
			colEdgeBetweenness.add(thisResult);
		}
		createResultFile("line_betweenness.txt", colEdgeBetweenness);
		System.out.println("All transmission lines are treated successfully.");
		System.out.println();
		
	}
	
	// Create result file
	public static void createResultFile(String resultFileName, Collection<String> resultNetworkData) {
		
		FileWriter fwTemp = null;
		BufferedWriter bwTemp = null;
		try {
			try {
				fwTemp = new FileWriter(resultFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			bwTemp = new BufferedWriter(fwTemp);
			for (String thisData : resultNetworkData) {
				bwTemp.write(thisData);
				bwTemp.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (bwTemp != null)
					bwTemp.close();
				if (fwTemp != null)
					fwTemp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		

	}

}