package org.intepss.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;

public class EdgeBetweennessGraph extends WeightedMultigraph<String, DefaultWeightedEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EdgeBetweennessGraph(WeightedMultigraph<String, DefaultWeightedEdge> originalGraph) {
		super(DefaultWeightedEdge.class);	// Constructor inherented from parent class
		
		// 1. Initialize the edge betweenness digraph
		// 1.1. Add vertices
		for (String thisVertex : originalGraph.vertexSet())
			this.addVertex(thisVertex);
		// 1.2. Add edges
		for (DefaultWeightedEdge thisEdge : originalGraph.edgeSet()) {
			DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
			String sendingName = originalGraph.getEdgeSource(thisEdge);
			String receivingName = originalGraph.getEdgeTarget(thisEdge);
			this.addEdge(sendingName, receivingName, newEdge);
			this.setEdgeWeight(newEdge, 0.0);
		}
		// 1.3. Create corresponding unweighted graph
		// 1.3.1. Add vertices
		WeightedMultigraph<String, DefaultWeightedEdge> originalUnweightedGraph = new WeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		for (String thisVertex : originalGraph.vertexSet())
			originalUnweightedGraph.addVertex(thisVertex);
		// 1.3.2. Add edges
		for (DefaultWeightedEdge thisEdge : originalGraph.edgeSet()) {
			DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
			String sendingName = originalGraph.getEdgeSource(thisEdge);
			String receivingName = originalGraph.getEdgeTarget(thisEdge);
			originalUnweightedGraph.addEdge(sendingName, receivingName, newEdge);
			originalUnweightedGraph.setEdgeWeight(newEdge, 1.0);
		}
		
		for (String thisVertex : this.vertexSet()) {
			// 2. Create shortest-path graph for every vertex by Dijkstra algorithm
			Multigraph<String, DefaultEdge> shortestPathDigraph = DijkstraAlgorithm(originalUnweightedGraph, thisVertex);
			
			// 3. Calculate the contribution of current vertex's shortest-path graph to final edge betweenness (Newman algorithm)
			SimpleWeightedGraph<String, DefaultWeightedEdge> edgeBetweennessDigraph = NewmanAlgorithm(shortestPathDigraph, thisVertex);
			
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
			DefaultWeightedEdge edgeInWholeGraph = originalGraph.getEdge(sourceVertex, targetVertex);
			double newWeight =  betweennessWeight / originalGraph.getEdgeWeight(edgeInWholeGraph);
			this.setEdgeWeight(thisEdge, newWeight);
//			System.out.println("Final edge betweenness between " + sourceVertex + " and " + targetVertex + ": " + newWeight);
		}
	}
	
	// Calculate the contribution of current vertex's shortest-path graph to final edge betweenness (Newman algorithm)
	// M. E. J. Newman and M. Girvan, Finding and evaluating community structure in networks, Physical Review E69, 026113 (2004)
	private SimpleWeightedGraph<String, DefaultWeightedEdge> NewmanAlgorithm(Multigraph<String, DefaultEdge> shortestPathGraph, String thisVertex) {
		
		if (shortestPathGraph.edgeSet().size() == 0)	// No outer degree
			return null;
		
		// 1. Initialize the edge betweenness digraph
		SimpleWeightedGraph<String, DefaultWeightedEdge> edgeBetweennessDigraph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		// Add vertices
		for (String curVertex : shortestPathGraph.vertexSet())
			edgeBetweennessDigraph.addVertex(curVertex);
		
		// 2. Deal with corresponding edges
		// 2.1. Calculate vertex distance and weight
		// 2.1.1. Initialize related variables
		HashMap<String, Integer> mapVertexDistance = new HashMap<String, Integer>();	// Vertex distance
		for (String curVertex : shortestPathGraph.vertexSet())
			mapVertexDistance.put(curVertex, 0);
		HashMap<String, Double> mapVertexWeight = new HashMap<String, Double>();	// Vertex weight
		for (String curVertex : shortestPathGraph.vertexSet())
			mapVertexWeight.put(curVertex, 1.0);
		Queue<String> verticesQueue = new LinkedList<String>();	// Store the queue of operated vertices
		// 2.1.2. Traverse the shortest-path digraph, get corresponding vertex distance and weight
		verticesQueue.add(thisVertex);
		String currentVertex = thisVertex;
		while (!verticesQueue.isEmpty()) {
			// 2.1.2.1. Read the bottom vertex in the queue, deal with all adjacent edges
			for (DefaultEdge curEdge : shortestPathGraph.edgesOf(currentVertex)) {
				String targetVertex = shortestPathGraph.getEdgeTarget(curEdge);
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
			for (DefaultEdge curEdge : shortestPathGraph.edgesOf(currentVertex)) {
				// 2.2.2.1. Get the source vertex
				String sourceVertex = shortestPathGraph.getEdgeSource(curEdge);
				String targetVertex = shortestPathGraph.getEdgeTarget(curEdge);
				String anotherVertex = "";
				if (currentVertex.equals(sourceVertex))
					anotherVertex = targetVertex;
				else if (currentVertex.equals(targetVertex))
					anotherVertex = sourceVertex;
				// 2.2.2.2. Calculate the value of the sum of all adjacent edges plus 1
				double totalSubWeights = 1.0;
				for (DefaultWeightedEdge curSubEdge : edgeBetweennessDigraph.edgesOf(currentVertex))
					totalSubWeights += edgeBetweennessDigraph.getEdgeWeight(curSubEdge);
				// 2.2.2.3. The new edge weight is calculated as totalSubWeights * (the quotient of the weights of the two terminals)
				double wi = mapVertexWeight.get(anotherVertex);
				double wj = mapVertexWeight.get(currentVertex);
				totalSubWeights /= wi / wj;
				// 2.2.2.4. Add new edge with correct weight into the edge betweenness digraph
				DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
				edgeBetweennessDigraph.addEdge(anotherVertex, currentVertex, newEdge);
				edgeBetweennessDigraph.setEdgeWeight(newEdge, totalSubWeights);
			}
		}
		
		return edgeBetweennessDigraph;
	}
	
	// Create shortest-path graph for every vertex by depth-first traversal algorithm
	private Multigraph<String, DefaultEdge> DijkstraAlgorithm(WeightedMultigraph<String, DefaultWeightedEdge> originalGraph, String thisVertex) {
	
		// 1. Simplify the multi-graph of the active power flow into a simple graph
		SimpleWeightedGraph<String, DefaultWeightedEdge> originalSimpleGraph =	new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		for (String curVertex : originalGraph.vertexSet())
			originalSimpleGraph.addVertex(curVertex);
		for (DefaultWeightedEdge curEdge : originalGraph.edgeSet()) {
			String sourceVertex = originalGraph.getEdgeSource(curEdge);
			String targetVertex = originalGraph.getEdgeTarget(curEdge);
			if (originalSimpleGraph.containsEdge(sourceVertex, targetVertex)) {
				DefaultWeightedEdge modifiedEdge = originalSimpleGraph.getEdge(sourceVertex, targetVertex);
				double newEdgeWeight = originalSimpleGraph.getEdgeWeight(modifiedEdge) + originalGraph.getEdgeWeight(curEdge); 
				originalSimpleGraph.setEdgeWeight(modifiedEdge, newEdgeWeight);
			}
			else {
				DefaultWeightedEdge newEdge = new DefaultWeightedEdge();
				originalSimpleGraph.addEdge(sourceVertex, targetVertex, newEdge);
				originalSimpleGraph.setEdgeWeight(newEdge, originalGraph.getEdgeWeight(curEdge));
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
			for (DefaultWeightedEdge curEdge : originalSimpleGraph.edgesOf(currentVertex)) {
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
		Multigraph<String, DefaultEdge> shortestPathGraph = new Multigraph<String, DefaultEdge>(DefaultEdge.class);
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
						if (!shortestPathGraph.containsVertex(sourceVertex))
							shortestPathGraph.addVertex(sourceVertex);
						if (!shortestPathGraph.containsVertex(targetVertex))
							shortestPathGraph.addVertex(targetVertex);
						shortestPathGraph.addEdge(sourceVertex, targetVertex, newEdge);
					}
			}
		}
		return shortestPathGraph;
	}

}