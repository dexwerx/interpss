package org.interpss.test.path;

import java.io.FileWriter;
import java.io.IOException;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.openide.util.Lookup;

public class JGraphToGephi {

	private DirectedWeightedMultigraph<String, DefaultWeightedEdge> dwGraph;	// Directed weighted graph in JGraphT format
//	private DirectedGraph gephiDWGraph;

	// Constructor, to initialize the directed weighted graph
	public JGraphToGephi(
			DirectedWeightedMultigraph<String, DefaultWeightedEdge> dwGraph) {
		super();
		this.dwGraph = dwGraph;
	}

	// Covert directed weighted graph from JGraphT format into Gephi format
	public void getGraphML(String fileName) throws IOException {
		// 1. Initialize the Gephi directed graph object
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();
		GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
		GraphModel model = graphController.getModel();
//		gephiDWGraph = model.getDirectedGraph();
		// 2. Add nodes
		for (String thisVertex : dwGraph.vertexSet()) {
			Node thisNode = model.factory().newNode(thisVertex);
			model.getDirectedGraph().addNode(thisNode);
		}
		// 3. Add edges
		for (DefaultWeightedEdge thisEdge : dwGraph.edgeSet()) {
			String sourceVertex = dwGraph.getEdgeSource(thisEdge);
			String targetVertex = dwGraph.getEdgeTarget(thisEdge);
			float weight = (float) dwGraph.getEdgeWeight(thisEdge);
			Edge thisGephiEdge = model.factory().newEdge(model.getDirectedGraph().getNode(sourceVertex), 
					model.getDirectedGraph().getNode(targetVertex), weight, true);
			model.getDirectedGraph().addEdge(thisGephiEdge);
		}
		// 4. Export to GraphML files
		ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		Exporter exporterGraphML = ec.getExporter("graphml");     //Get GraphML exporter
		exporterGraphML.setWorkspace(workspace);
		FileWriter stringWriter = new FileWriter(fileName);
		ec.exportWriter(stringWriter, (CharacterExporter) exporterGraphML);
	}
	
}