package org.interpss.test.path;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.xml.transform.TransformerConfigurationException;

import org.interpss.IpssPlugin;
import org.interpss.PluginObjectFactory;
//import org.interpss.PluginSpringCtx;
import org.interpss.custom.IpssFileAdapter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.xml.sax.SAXException;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

// Active power digraph of InterPSS network model
public class IPSSActivePowerDigraph {

	private String networkName;
	private AclfNetwork net;
	private DirectedWeightedMultigraph<String, DefaultWeightedEdge> pDigraph;	// Active power digraph
	
	// Constructor, build the network model
	public IPSSActivePowerDigraph(String networkName) throws Exception {
		this.networkName = networkName;
		this.net = PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF).load(this.networkName).getAclfNet();
//	    IPSSMsgHub msg = CoreCommonSpringCtx.getIpssMsgHub();
//		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(msg);
//		net.accept(algo);
//        System.out.println(AclfOutFunc.loadFlowSummary(net));
	}

	// Get the active power digraph
	public DirectedWeightedMultigraph<String, DefaultWeightedEdge> getpDigraph() {
		// Initialize the digraph
		pDigraph = new DirectedWeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		// 1. Add all the nodes
		for (Bus thisBus : net.getBusList())
			pDigraph.addVertex(thisBus.getId());
		// 2. Add all the edges
		for (Branch thisBranch : net.getBranchList()) {
			String fromId = thisBranch.getFromBusId();
			String toId = thisBranch.getToBusId();
			String branchID = thisBranch.getId();
			AclfBranch thisAclfBranch = net.getAclfBranch(branchID);
			double activePowerFrom2To = thisAclfBranch.powerFrom2To().getReal();
			double activePowerTo2From = thisAclfBranch.powerTo2From().getReal();
			if (activePowerFrom2To > 0) {
				DefaultWeightedEdge thisEdge = new DefaultWeightedEdge();
				pDigraph.addEdge(fromId, toId, thisEdge);
				pDigraph.setEdgeWeight(thisEdge, activePowerFrom2To);
			}
			else if (activePowerTo2From > 0) {
				DefaultWeightedEdge thisEdge = new DefaultWeightedEdge();
				pDigraph.addEdge(toId, fromId, thisEdge);
				pDigraph.setEdgeWeight(thisEdge, activePowerTo2From);
			}
		}
		return pDigraph;
	}
	
	// Output the digraph into GML format
	public void digraphToGML(String outputName) throws IOException, TransformerConfigurationException, SAXException {
		VertexNameProvider<String> vnp = new StringNameProvider<String>();
		EdgeNameProvider<DefaultWeightedEdge> enp = new StringEdgeNameProvider<DefaultWeightedEdge>();
		EdgeNameProvider<DefaultWeightedEdge> elp = new StringEdgeNameProvider<DefaultWeightedEdge>() {

			@Override
			public String getEdgeName(DefaultWeightedEdge edge) {
				// TODO Auto-generated method stub
				DecimalFormat edgeWeight = new DecimalFormat("#.000");
				return edgeWeight.format(pDigraph.getEdgeWeight(edge));
			}
			
		};
		GraphMLExporter<String, DefaultWeightedEdge> graphml = new GraphMLExporter<String, DefaultWeightedEdge>(vnp, vnp, enp, elp);
		File fileOutput = new File(outputName);
		FileWriter writerOutput = new FileWriter(fileOutput, true);
		graphml.export(writerOutput, this.getpDigraph());
		System.out.println(outputName + " created successfully.");
	}
	
	public static void main(String[] args) throws Exception {
		IpssPlugin.init();
		IPSSActivePowerDigraph apd = new IPSSActivePowerDigraph("testdata/ieee_cdf/UCTE_2002_Summer.ieee");
//		for (DefaultWeightedEdge thisEdge : apd.getpDigraph().edgeSet()) {
//			String fromId = apd.getpDigraph().getEdgeSource(thisEdge);
//			String toId = apd.getpDigraph().getEdgeTarget(thisEdge);
//			double weight = apd.getpDigraph().getEdgeWeight(thisEdge);
//			System.out.println(fromId + "=>" + toId + ": " + weight);
//		}
//		apd.digraphToGML("ieee14.graphml");
		JGraphToGephi j2g = new JGraphToGephi(apd.getpDigraph());
		String outputName = "UCTE_2002_Summer.graphml";
		j2g.getGraphML(outputName);
		System.out.println(outputName + " created successfully.");
	}

}