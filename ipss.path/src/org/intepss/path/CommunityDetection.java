package org.intepss.path;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedMultigraph;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;

// Exploring community structures of a given graph, can be used to analyze power transfer graphs, etc.
public class CommunityDetection {

	private static final long serialVersionUID = 1L;

	private DirectedWeightedMultigraph<String, DefaultWeightedEdge> originalDigraph;	// The graph to be analyzed
	private WeightedMultigraph<String, DefaultWeightedEdge> originalGraph;	// The graph to be analyzed
	private AclfNetwork net;	// InterPSS network model
	private Collection<List<Set<String>>> communitySets = new ArrayList<List<Set<String>>>();	// All community results
	private Collection<Double> qSet = new ArrayList<Double>();	// Modularity measures
	private Collection<String> result = new ArrayList<String>();

	public Collection<Double> getqSet() {
		return qSet;
	}

	public Collection<String> getResult() {
		return result;
	}

	// Constructor
	public CommunityDetection(DirectedWeightedMultigraph<String, DefaultWeightedEdge> originalDigraph, AclfNetwork net) {
		super();
		this.originalDigraph = originalDigraph;
		this.net = net;
	}
	
	public CommunityDetection(WeightedMultigraph<String, DefaultWeightedEdge> originalGraph, AclfNetwork net) {
		super();
		this.originalGraph = originalGraph;
		this.net = net;
	}

	// Implementing community detections
	@SuppressWarnings("unchecked")
	public void detectingCommunities() {
		// 0. Initialize the result
		DirectedWeightedMultigraph<String, DefaultWeightedEdge> tempGraph = (DirectedWeightedMultigraph<String, DefaultWeightedEdge>) this.originalDigraph.clone();	// Temporary graph
		List<Set<String>> originalCommunities = new ArrayList<Set<String>>();
		originalCommunities.add(tempGraph.vertexSet());
		this.communitySets.add(originalCommunities);
		this.qSet.add(0.0);
		result.add("[Modularity Measure] " + 0.0);
		result.add("<Community No.1>");
		for (String thisVertex : tempGraph.vertexSet())
			result.add(thisVertex);
//		result.add("");
		int communityNumber = 1;
		// Continue to detect communities until there are too many communities (meaningless for real power systems)
//		while (communityNumber < 0.3 * tempGraph.vertexSet().size()) {
		while (tempGraph.edgeSet().size() > 0) {
			// 1. Calculate all the edge betweenness for current graph
			EdgeBetweennessDigraph ebd = new EdgeBetweennessDigraph(tempGraph);
			// 2. Remove the edges with highest edge betweenness
			// 2.1. Sort the edges in descending order with edge betweenness
			// 2.1.1. Create the map between edges and edge beteennesses
			HashMap<DefaultWeightedEdge, Double> betweennessMap = new HashMap<DefaultWeightedEdge, Double>();
			for (DefaultWeightedEdge thisEdge : ebd.edgeSet()) {
				double thisBetweenness = ebd.getEdgeWeight(thisEdge);
				betweennessMap.put(thisEdge, thisBetweenness);
			}
			// 2.1.2. Sort the map in descending order
			List<Map.Entry<DefaultWeightedEdge, Double>> betweennessList = new ArrayList<Map.Entry<DefaultWeightedEdge, Double>>(betweennessMap.entrySet());
			Collections.sort(betweennessList, new Comparator<Map.Entry<DefaultWeightedEdge, Double>>() {	// Sorting the list descendingly

				@Override
				public int compare(Entry<DefaultWeightedEdge, Double> o1, Entry<DefaultWeightedEdge, Double> o2) {
					return (o2.getValue().compareTo(o1.getValue()));
				}
				
			});
			// 2.2. Remove corresponding edges from tempGraph
			Map.Entry<DefaultWeightedEdge, Double> maxEntry = (Map.Entry<DefaultWeightedEdge, Double>) betweennessList.toArray()[0];
			DefaultWeightedEdge maxEdge = maxEntry.getKey();
			String sourceVertex = ebd.getEdgeSource(maxEdge);
			String targetVertex = ebd.getEdgeTarget(maxEdge);
			tempGraph.removeAllEdges(sourceVertex, targetVertex);
			DecimalFormat df = new DecimalFormat("##.####");
			System.out.println("[Community number: " + communityNumber + "]" + "Branches between [" + sourceVertex + "] and [" + targetVertex + "] removed successfully, " + 
					df.format(((double) (communityNumber) / (double) (tempGraph.vertexSet().size())) * 100) + "% finished.");
			// 3. Add new entry to the hashmap if new communities are detected
			// 3.1. Analyze the connectivity of tempGraph
			ConnectivityInspector<String, DefaultWeightedEdge> conInsp = new ConnectivityInspector<String, DefaultWeightedEdge>(tempGraph);
			List<Set<String>> newCommunities = conInsp.connectedSets();
			if (newCommunities.size() > communityNumber) {
				// 3.2. Calculate current modularity measure
				communityNumber = newCommunities.size();
				// 3.2.1. Construct the map containing the community index of every node
				HashMap<String, Integer> mapVertexCommunity = new HashMap<String, Integer>();
				int communityIndex = 0;
				for (Set<String> thisCommunity : newCommunities) {
					for (String thisVertex : thisCommunity)
						mapVertexCommunity.put(thisVertex, communityIndex);
					communityIndex++;
				}
				// 3.2.2. Calculate all the ai and eii in Newman's paper
				double eii = 0.0;
				double[] a = new double[communityNumber], b = new double[communityNumber];
				// Total edge weight amount
				double totalEdgeWeight = 0.0;
				for (DefaultWeightedEdge thisEdge : this.originalDigraph.edgeSet()) {
					totalEdgeWeight += 1.0;
					sourceVertex = this.originalDigraph.getEdgeSource(thisEdge);
					targetVertex = this.originalDigraph.getEdgeTarget(thisEdge);
					int sourceCommunity = mapVertexCommunity.get(sourceVertex);
					int targetCommunity = mapVertexCommunity.get(targetVertex);
					if (sourceCommunity == targetCommunity)
						eii += 1.0;
					a[sourceCommunity] += 1.0;
					b[targetCommunity] += 1.0;
				}
				// 3.2.3. Calculate Q in Newman's paper
				eii /= totalEdgeWeight;
				for (int i = 0; i < communityNumber; i++) {
					a[i] /= totalEdgeWeight;
					b[i] /= totalEdgeWeight;
				}
				double q = eii;
				for (int i = 0; i < communityNumber; i++)
					q -= a[i] * b[i];
				this.communitySets.add(newCommunities);
				this.qSet.add(q);
				// Output modularity measure
				result.add("[Modularity Measure] " + q);
				// Output communities
				int i = 0;
				for (Set<String> thisCommunity : newCommunities) {
					i++;
					result.add("<Community No." + i + ">");
					for (String thisVertex : thisCommunity)
						result.add(thisVertex);
				}
//				result.add("");
				System.out.println("Community number: " + communityNumber + ", Current modularity measure: " + q);
			}
		}
	}
	
	// Implementing community detections, modularity measure calculated with edge weights
	@SuppressWarnings("unchecked")
	public void detectingWeightedCommunities() {
		// 0. Initialize the result
		DirectedWeightedMultigraph<String, DefaultWeightedEdge> tempGraph = (DirectedWeightedMultigraph<String, DefaultWeightedEdge>) this.originalDigraph.clone();	// Temporary graph
		List<Set<String>> originalCommunities = new ArrayList<Set<String>>();
		originalCommunities.add(tempGraph.vertexSet());
		this.communitySets.add(originalCommunities);
		this.qSet.add(0.0);
		result.add("[Modularity Measure] " + 0.0);
		result.add("<Community No.1>");
		for (String thisVertex : tempGraph.vertexSet())
			result.add(thisVertex);
//		result.add("");
		int edgesToBeRemained = (int) (0.3 * this.originalDigraph.edgeSet().size());
		int communityNumber = 1;
		// Continue to detect communities until there are too many communities (meaningless for real power systems)
//		while (communityNumber < 0.3 * tempGraph.vertexSet().size()) {
		while (tempGraph.edgeSet().size() > edgesToBeRemained) {
			// 1. Calculate all the edge betweenness for current graph
			EdgeBetweennessDigraph ebd = new EdgeBetweennessDigraph(tempGraph);
			// 2. Remove the edges with highest edge betweenness
			// 2.1. Sort the edges in descending order with edge betweenness
			// 2.1.1. Create the map between edges and edge beteennesses
			HashMap<DefaultWeightedEdge, Double> betweennessMap = new HashMap<DefaultWeightedEdge, Double>();
			for (DefaultWeightedEdge thisEdge : ebd.edgeSet()) {
				double thisBetweenness = ebd.getEdgeWeight(thisEdge);
				betweennessMap.put(thisEdge, thisBetweenness);
			}
			// 2.1.2. Sort the map in descending order
			List<Map.Entry<DefaultWeightedEdge, Double>> betweennessList = new ArrayList<Map.Entry<DefaultWeightedEdge, Double>>(betweennessMap.entrySet());
			Collections.sort(betweennessList, new Comparator<Map.Entry<DefaultWeightedEdge, Double>>() {	// Sorting the list descendingly

				@Override
				public int compare(Entry<DefaultWeightedEdge, Double> o1, Entry<DefaultWeightedEdge, Double> o2) {
					return (o2.getValue().compareTo(o1.getValue()));
				}
				
			});
			// 2.2. Remove corresponding edges from tempGraph
			Map.Entry<DefaultWeightedEdge, Double> maxEntry = (Map.Entry<DefaultWeightedEdge, Double>) betweennessList.toArray()[0];
			DefaultWeightedEdge maxEdge = maxEntry.getKey();
			String sourceVertex = ebd.getEdgeSource(maxEdge);
			String targetVertex = ebd.getEdgeTarget(maxEdge);
			tempGraph.removeAllEdges(sourceVertex, targetVertex);
			DecimalFormat df = new DecimalFormat("##.####");
			System.out.println("[Community number: " + communityNumber + "]" + "Branches between [" + sourceVertex + "] and [" + targetVertex + "] removed successfully, " + 
					df.format(((double) (communityNumber) / (double) (tempGraph.vertexSet().size())) * 100) + "% finished.");
			// 3. Add new entry to the hashmap if new communities are detected
			// 3.1. Analyze the connectivity of tempGraph
			ConnectivityInspector<String, DefaultWeightedEdge> conInsp = new ConnectivityInspector<String, DefaultWeightedEdge>(tempGraph);
			List<Set<String>> newCommunities = conInsp.connectedSets();
			if (newCommunities.size() > communityNumber) {
				// 3.2. Calculate current modularity measure
				communityNumber = newCommunities.size();
				// 3.2.1. Construct the map containing the community index of every node
				HashMap<String, Integer> mapVertexCommunity = new HashMap<String, Integer>();
				int communityIndex = 0;
				for (Set<String> thisCommunity : newCommunities) {
					for (String thisVertex : thisCommunity)
						mapVertexCommunity.put(thisVertex, communityIndex);
					communityIndex++;
				}
				// 3.2.2. Calculate all the ai and eii in Newman's paper
				double eii = 0.0;
				double[] a = new double[communityNumber], b = new double[communityNumber];
				// Total edge weight amount
				double totalEdgeWeight = 0.0;
				for (DefaultWeightedEdge thisEdge : this.originalDigraph.edgeSet()) {
					sourceVertex = this.originalDigraph.getEdgeSource(thisEdge);
					targetVertex = this.originalDigraph.getEdgeTarget(thisEdge);
					double thisWeight = this.originalDigraph.getEdgeWeight(thisEdge);
					totalEdgeWeight += thisWeight;
					int sourceCommunity = mapVertexCommunity.get(sourceVertex);
					int targetCommunity = mapVertexCommunity.get(targetVertex);
					if (sourceCommunity == targetCommunity)
						eii += thisWeight;
					a[sourceCommunity] += thisWeight;
					b[targetCommunity] += thisWeight;
				}
				// 3.2.3. Calculate Q in Newman's paper
				eii /= totalEdgeWeight;
				for (int i = 0; i < communityNumber; i++) {
					a[i] /= totalEdgeWeight;
					b[i] /= totalEdgeWeight;
				}
				double q = eii;
				for (int i = 0; i < communityNumber; i++)
					q -= a[i] * b[i];
				this.communitySets.add(newCommunities);
				this.qSet.add(q);
				// Output modularity measure
				result.add("[Modularity Measure] " + q);
				// Output communities
				int i = 0;
				for (Set<String> thisCommunity : newCommunities) {
					i++;
					result.add("<Community No." + i + ">");
					for (String thisVertex : thisCommunity)
						result.add(thisVertex);
				}
//				result.add("");
				System.out.println("Community number: " + communityNumber + ", Current modularity measure: " + q);
			}
		}
	}
	
	// Implementing community detections, modularity measure calculated with edge weights
	@SuppressWarnings("unchecked")
	public void detectingWeightedDirectedCommunities() {
		// 0. Initialize the result
		DirectedWeightedMultigraph<String, DefaultWeightedEdge> tempGraph = (DirectedWeightedMultigraph<String, DefaultWeightedEdge>) this.originalDigraph.clone();	// Temporary graph
		List<Set<String>> originalCommunities = new ArrayList<Set<String>>();
		originalCommunities.add(tempGraph.vertexSet());
		this.communitySets.add(originalCommunities);
		this.qSet.add(0.0);
		result.add("[Modularity Measure] " + 0.0);
		result.add("<Community No.1>");
		for (String thisVertex : tempGraph.vertexSet())
			result.add(thisVertex);
//		result.add("");
		int communityNumber = 1;
		int m = this.originalDigraph.edgeSet().size();
		// Continue to detect communities until there are too many communities (meaningless for real power systems)
//		while (communityNumber < 0.3 * tempGraph.vertexSet().size()) {
		while (tempGraph.edgeSet().size() > 0) {
			// 1. Calculate all the edge betweenness for current graph
			EdgeBetweennessDigraph ebd = new EdgeBetweennessDigraph(tempGraph);
			// 2. Remove the edges with highest edge betweenness
			// 2.1. Sort the edges in descending order with edge betweenness
			// 2.1.1. Create the map between edges and edge beteennesses
			HashMap<DefaultWeightedEdge, Double> betweennessMap = new HashMap<DefaultWeightedEdge, Double>();
			for (DefaultWeightedEdge thisEdge : ebd.edgeSet()) {
				double thisBetweenness = ebd.getEdgeWeight(thisEdge);
				betweennessMap.put(thisEdge, thisBetweenness);
			}
			// 2.1.2. Sort the map in descending order
			List<Map.Entry<DefaultWeightedEdge, Double>> betweennessList = new ArrayList<Map.Entry<DefaultWeightedEdge, Double>>(betweennessMap.entrySet());
			Collections.sort(betweennessList, new Comparator<Map.Entry<DefaultWeightedEdge, Double>>() {	// Sorting the list descendingly

				@Override
				public int compare(Entry<DefaultWeightedEdge, Double> o1, Entry<DefaultWeightedEdge, Double> o2) {
					return (o2.getValue().compareTo(o1.getValue()));
				}
				
			});
			// 2.2. Remove corresponding edges from tempGraph
			Map.Entry<DefaultWeightedEdge, Double> maxEntry = (Map.Entry<DefaultWeightedEdge, Double>) betweennessList.toArray()[0];
			DefaultWeightedEdge maxEdge = maxEntry.getKey();
			String sourceVertex = ebd.getEdgeSource(maxEdge);
			String targetVertex = ebd.getEdgeTarget(maxEdge);
			tempGraph.removeAllEdges(sourceVertex, targetVertex);
			DecimalFormat df = new DecimalFormat("##.####");
			System.out.println("[Community number: " + communityNumber + "]" + "Branches between [" + sourceVertex + "] and [" + targetVertex + "] removed successfully, " + 
					df.format(((double) (communityNumber) / (double) (tempGraph.vertexSet().size())) * 100) + "% finished.");
			// 3. Add new entry to the hashmap if new communities are detected
			// 3.1. Analyze the connectivity of tempGraph
			ConnectivityInspector<String, DefaultWeightedEdge> conInsp = new ConnectivityInspector<String, DefaultWeightedEdge>(tempGraph);
			List<Set<String>> newCommunities = conInsp.connectedSets();
			if (newCommunities.size() > communityNumber) {
				// 3.2. Calculate current modularity measure
				communityNumber = newCommunities.size();
				// 3.2.1. Construct the map containing the community index of every node
				HashMap<String, Integer> mapVertexCommunity = new HashMap<String, Integer>();
				int communityIndex = 0;
				for (Set<String> thisCommunity : newCommunities) {
					for (String thisVertex : thisCommunity)
						mapVertexCommunity.put(thisVertex, communityIndex);
					communityIndex++;
				}
				// 3.2.2. Construct the map containing the weighted in-degree of every node
				HashMap<String, Double> mapVertexWeightedInDegree = new HashMap<String, Double>();
				for (String thisVertex : this.originalDigraph.vertexSet()) {
					double thisWeightedInDegree = 0.0;
					for (DefaultWeightedEdge thisEdge : this.originalDigraph.incomingEdgesOf(thisVertex))
						thisWeightedInDegree += this.originalDigraph.getEdgeWeight(thisEdge);
					mapVertexWeightedInDegree.put(thisVertex, thisWeightedInDegree);
				}
				// 3.2.3. Construct the map containing the weighted out-degree of every node
				HashMap<String, Double> mapVertexWeightedOutDegree = new HashMap<String, Double>();
				for (String thisVertex : this.originalDigraph.vertexSet()) {
					double thisWeightedOutDegree = 0.0;
					for (DefaultWeightedEdge thisEdge : this.originalDigraph.outgoingEdgesOf(thisVertex))
						thisWeightedOutDegree += this.originalDigraph.getEdgeWeight(thisEdge);
					mapVertexWeightedOutDegree.put(thisVertex, thisWeightedOutDegree);
				}
				// 3.2.2. Calculate all the ai and eii in Newman's paper
				double q = 0.0;
				// Total edge weight amount
				double totalEdgeWeight = 0.0;
				for (DefaultWeightedEdge thisEdge : this.originalDigraph.edgeSet()) {
					sourceVertex = this.originalDigraph.getEdgeSource(thisEdge);
					targetVertex = this.originalDigraph.getEdgeTarget(thisEdge);
					double thisWeight = this.originalDigraph.getEdgeWeight(thisEdge);
					totalEdgeWeight += thisWeight;
					int sourceCommunity = mapVertexCommunity.get(sourceVertex);
					int targetCommunity = mapVertexCommunity.get(targetVertex);
					if (sourceCommunity == targetCommunity) {
						double wkin = mapVertexWeightedInDegree.get(sourceVertex);
						double wkout = mapVertexWeightedOutDegree.get(targetVertex);
						q += thisWeight / m - wkin * wkout / m / m;
					}
				}
				this.communitySets.add(newCommunities);
				this.qSet.add(q);
				// Output modularity measure
				result.add("[Modularity Measure] " + q);
				// Output communities
				int i = 0;
				for (Set<String> thisCommunity : newCommunities) {
					i++;
					result.add("<Community No." + i + ">");
					for (String thisVertex : thisCommunity)
						result.add(thisVertex);
				}
//				result.add("");
				System.out.println("Community number: " + communityNumber + ", Current modularity measure: " + q);
			}
		}
	}
	
	// Implementing community detections, modularity measure calculated with edge weights
	@SuppressWarnings("unchecked")
	public void detectingWeightedUndirectedCommunities() {
		// 0. Initialize the result
		WeightedMultigraph<String, DefaultWeightedEdge> tempGraph = (WeightedMultigraph<String, DefaultWeightedEdge>) this.originalGraph.clone();	// Temporary graph
		List<Set<String>> originalCommunities = new ArrayList<Set<String>>();
		originalCommunities.add(tempGraph.vertexSet());
		this.communitySets.add(originalCommunities);
		this.qSet.add(0.0);
		result.add("[Modularity Measure] " + 0.0);
		result.add("<Community No.1>");
		for (String thisVertex : tempGraph.vertexSet())
			result.add(thisVertex);
//		result.add("");
		int communityNumber = 1;
		// Continue to detect communities until there are too many communities (meaningless for real power systems)
//		while (communityNumber < 0.3 * tempGraph.vertexSet().size()) {
		int edgesToBeRemained = (int) (0.3 * this.originalGraph.edgeSet().size());
		while (tempGraph.edgeSet().size() > edgesToBeRemained) {
			// 1. Calculate all the edge betweenness for current graph
			EdgeBetweennessGraph ebd = new EdgeBetweennessGraph(tempGraph);
			// 2. Remove the edges with highest edge betweenness
			// 2.1. Sort the edges in descending order with edge betweenness
			// 2.1.1. Create the map between edges and edge beteennesses
			HashMap<DefaultWeightedEdge, Double> betweennessMap = new HashMap<DefaultWeightedEdge, Double>();
			for (DefaultWeightedEdge thisEdge : ebd.edgeSet()) {
				double thisBetweenness = ebd.getEdgeWeight(thisEdge);
				betweennessMap.put(thisEdge, thisBetweenness);
			}
			// 2.1.2. Sort the map in descending order
			List<Map.Entry<DefaultWeightedEdge, Double>> betweennessList = new ArrayList<Map.Entry<DefaultWeightedEdge, Double>>(betweennessMap.entrySet());
			Collections.sort(betweennessList, new Comparator<Map.Entry<DefaultWeightedEdge, Double>>() {	// Sorting the list descendingly

				@Override
				public int compare(Entry<DefaultWeightedEdge, Double> o1, Entry<DefaultWeightedEdge, Double> o2) {
					return (o2.getValue().compareTo(o1.getValue()));
				}
				
			});
			// 2.2. Remove corresponding edges from tempGraph
			Map.Entry<DefaultWeightedEdge, Double> maxEntry = (Map.Entry<DefaultWeightedEdge, Double>) betweennessList.toArray()[0];
			DefaultWeightedEdge maxEdge = maxEntry.getKey();
			String sourceVertex = ebd.getEdgeSource(maxEdge);
			String targetVertex = ebd.getEdgeTarget(maxEdge);
			tempGraph.removeAllEdges(sourceVertex, targetVertex);
			DecimalFormat df = new DecimalFormat("##.####");
			System.out.println("[Community number: " + communityNumber + "]" + "Branches between [" + sourceVertex + "] and [" + targetVertex + "] removed successfully, " + 
					df.format(((double) (communityNumber) / (double) (tempGraph.vertexSet().size())) * 100) + "% finished.");
			// 3. Add new entry to the hashmap if new communities are detected
			// 3.1. Analyze the connectivity of tempGraph
			ConnectivityInspector<String, DefaultWeightedEdge> conInsp = new ConnectivityInspector<String, DefaultWeightedEdge>(tempGraph);
			List<Set<String>> newCommunities = conInsp.connectedSets();
			if (newCommunities.size() > communityNumber) {
				// 3.2. Calculate current modularity measure
				communityNumber = newCommunities.size();
				// 3.2.1. Construct the map containing the community index of every node
				HashMap<String, Integer> mapVertexCommunity = new HashMap<String, Integer>();
				int communityIndex = 0;
				for (Set<String> thisCommunity : newCommunities) {
					for (String thisVertex : thisCommunity)
						mapVertexCommunity.put(thisVertex, communityIndex);
					communityIndex++;
				}
				// 3.2.2. Calculate all the ai and eii in Newman's paper
				double eii = 0.0;
				double[] a = new double[communityNumber], b = new double[communityNumber];
				// Total edge weight amount
				double totalEdgeWeight = 0.0;
				for (DefaultWeightedEdge thisEdge : this.originalGraph.edgeSet()) {
					sourceVertex = this.originalGraph.getEdgeSource(thisEdge);
					targetVertex = this.originalGraph.getEdgeTarget(thisEdge);
					double thisWeight = this.originalGraph.getEdgeWeight(thisEdge);
					totalEdgeWeight += thisWeight;
					int sourceCommunity = mapVertexCommunity.get(sourceVertex);
					int targetCommunity = mapVertexCommunity.get(targetVertex);
					if (sourceCommunity == targetCommunity)
						eii += thisWeight;
					a[sourceCommunity] += thisWeight;
					b[targetCommunity] += thisWeight;
				}
				// 3.2.3. Calculate Q in Newman's paper
				eii /= totalEdgeWeight;
				for (int i = 0; i < communityNumber; i++) {
					a[i] /= totalEdgeWeight;
					b[i] /= totalEdgeWeight;
				}
				double q = eii;
				for (int i = 0; i < communityNumber; i++)
					q -= a[i] * b[i];
				this.communitySets.add(newCommunities);
				this.qSet.add(q);
				// Output modularity measure
				result.add("[Modularity Measure] " + q);
				// Output communities
				int i = 0;
				for (Set<String> thisCommunity : newCommunities) {
					i++;
					result.add("<Community No." + i + ">");
					for (String thisVertex : thisCommunity)
						result.add(thisVertex);
				}
//				result.add("");
				System.out.println("Community number: " + communityNumber + ", Current modularity measure: " + q);
			}
		}
	}

	// Save results
	public void saveCommunityResults(String fileName, Collection<String> colResult) {
		FileWriter fwTemp = null;
		BufferedWriter bwTemp = null;
		try {
			try {
				fwTemp = new FileWriter(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			bwTemp = new BufferedWriter(fwTemp);
			for (String thisData : colResult) {
				if (thisData.contains("Modularity") || thisData.contains("Community"))
					bwTemp.write(thisData);
				else {
					AclfBus thisBus = this.net.getAclfBus(thisData);
//					System.out.println(thisData);
//					System.out.println(thisBus.getName());
					bwTemp.write(thisBus.getName());
				}
//				bwTemp.write(thisData);
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

	public void saveQResults(String fileName, Collection<Double> qSet) {
		FileWriter fwTemp = null;
		BufferedWriter bwTemp = null;
		try {
			try {
				fwTemp = new FileWriter(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			bwTemp = new BufferedWriter(fwTemp);
			for (Double thisData : qSet) {
				bwTemp.write(thisData.toString());
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