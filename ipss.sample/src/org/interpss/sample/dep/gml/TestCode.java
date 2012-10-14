package org.interpss.sample.dep.gml;

import java.util.List;

import org.graphdrawing.gml.GraphType;
import org.graphdrawing.gml.GraphmlType;

public class TestCode {
	public static void main(String[] args) throws Exception {
		// load the Gml file
		GraphmlType gml = GmlHelper.load("testData/gml/sample_002.gml");
		
		// retrieve all Graph objects, there may be more than one graphs
		List<GraphType> glist = GmlHelper.getGraphObjects(gml);
		System.out.println("no of graphes: " + glist.size());

		//System.out.println("no of graphes: " + GmlHelper.toXmlString(glist.get(0)));
		
		String str = GmlHelper.toXmlString(glist.get(0));
		GraphType graph = GmlHelper.parseGraph(str);
		System.out.println(GmlHelper.toXmlString(graph));
	}
}
