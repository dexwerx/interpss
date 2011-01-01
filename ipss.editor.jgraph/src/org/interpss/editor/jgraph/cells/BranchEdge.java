package org.interpss.editor.jgraph.cells;


/**
*	Graph Branch(Edge) Model(cell) class
*/

import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Map;

import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.jgraph.ui.form.IUserData;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.NetUtilFunc;
 
public class BranchEdge extends DefaultEdge implements IIpssEdge{
	public static String BranchLabelFontName = "Arial Narrow";
	public static int    BranchLabelFontStyle = Font.PLAIN;
	public static int    BranchLabelFontSize = 10;
	
	// Mike 12/25/05
	/*
	 * define default label font
	 */
	public static Font	LabelFont = new Font(BranchLabelFontName, BranchLabelFontStyle, BranchLabelFontSize);	
	
	private static final long serialVersionUID = 1;

	/**
	* 	Constructor
	*
	* @param userObject user defined object assciated with this cell
	*/
//	public BranchEdge (Object userObject) {
//		super(userObject);
//	}
	public BranchEdge (Object userObject) {
		super(userObject);
	}
	
	public void setExtraLabels(JGraph graph, String sourceLabel, String targetLabel) {
		IpssLogger.getLogger().fine("Set Branch fromLabel, toLabel: " + sourceLabel + "," + targetLabel);

		Object[] extraLabels = {sourceLabel, targetLabel};
		
		Point2D sourceLacation = null;
		Point2D targetLacation = null;
		
		Point2D[] extraLabelPositions = GraphConstants.getExtraLabelPositions(this.getAttributes());
		if (extraLabelPositions != null && extraLabelPositions.length > 0) {
			sourceLacation = extraLabelPositions[0];
		}
		if (sourceLacation == null) {
			//sourceLacation =  GraphUtilFunc.getPosition(graph, getSourceBus());
			//List p= GraphConstants.getPoints(this.getAttributes());

//			Rectangle2D r = GraphConstants.getBounds(this.getAttributes());
//			sourceLacation = new java.awt.Point((r.OUT_LEFT+r.OUT_RIGHT)/2,(r.OUT_TOP+r.OUT_BOTTOM)/2);
			
			sourceLacation =  new Point2D.Double(GraphConstants.PERMILLE/8, -20);
			
		}	

		if (extraLabelPositions != null && extraLabelPositions.length > 1) {
			targetLacation = extraLabelPositions[1];
		}
		if (targetLacation == null) {
			//targetLacation =  GraphUtilFunc.getPosition(graph, getTargetBus());

//			Rectangle2D r = GraphConstants.getBounds(this.getAttributes());
//			targetLacation = new java.awt.Point((int)sourceLacation.getX()+50,(int)sourceLacation.getY()+30);
			
			targetLacation =  new Point2D.Double(GraphConstants.PERMILLE*7/8, -20);
		}	
			
		AttributeMap map = new AttributeMap();
		GraphConstants.setExtraLabels(map, extraLabels);
		Point2D[] positions = {sourceLacation, targetLacation};
		GraphConstants.setExtraLabelPositions(map, positions);

		GraphConstants.setFont(map, LabelFont);
		
		Object[] edges = {this};
		graph.getGraphLayoutCache().edit(edges, map);
	}
	
	/**
	* 	Get the branch form object
	*
	* @return the branch form object
	*/
	public IGBranchForm getBranchForm() {
		return (IGBranchForm)getUserObject();
	}

	/**
	* 	Create branch attributes
	*
	* @return attributes map
	*/
	public Map createAttributes() {
		// Create a Map that holds the attributes for the edge
		Map map = super.getAttributes();

		// Add a Line End Attribute
		//GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
		
		return map;
	}

	/**
	* 	Connect this branch to the source and target port of the graph object
	*
	* @param graph the graph object
	* @param source the source port
	* @param target the target port
	*/
	public void connect(JGraph graph, Port aSource, Port aTarget) {
		// Create Connection between source and target using edge
		ConnectionSet conSet = new ConnectionSet();
		conSet.connect(this, aSource, aTarget);

		// Construct a Map from cells to Maps (for insert)
		Hashtable attrib = new Hashtable();

		// Associate the Edge with its Attributes
		attrib.put(this, createAttributes());

		// Insert the Edge and its Attributes
		setBranchId((IGBranchForm)this.getUserObject(), aSource, aTarget);

		IpssLogger.getLogger().info("Branch object inserted on the graph and launch BranchEditorDialog");
//		CellEditorFactory.getEditorDialog(this, (IpssGraph)graph);

		graph.getGraphLayoutCache().insert(
			new Object[] { this },
			attrib,
			conSet,
			null,
			null);
		
	    // add by Mike 12/23/05
		/*
		 * add from and to annotation label to a branch object
		 */
		setExtraLabels(graph, "From Label", "To Label");
	}

	/**
	*	Set the branch id according to the source port and the targer port
	*
	* @param bform the branch form object
	*/
	public void setBranchId(IGBranchForm bform) {
		setBranchId(bform, (Port)getSource(), (Port)getTarget());
	}

	/**
	*	Set the branch id according to the source port and the targer port
	*
	* @param bform the branch form object
	* @param source the source port
	* @param target the target port
	*/
	public void setBranchId(IGBranchForm bform, Port source, Port target) {
		BusCell sbus = (BusCell)((DefaultPort)source).getParent();
		IGBusForm sform = (IGBusForm)sbus.getUserObject();
		BusCell tbus = (BusCell)((DefaultPort)target).getParent();
		IGBusForm tform = (IGBusForm)tbus.getUserObject();
		bform.setId(NetUtilFunc.createBranchDisplayId(sform.getId(), tform.getId()));
		bform.setFromId(sform.getId());
		bform.setToId(tform.getId());
		bform.setFromBusName(sform.getName());
		bform.setToBusName(tform.getName());
		bform.setName(NetUtilFunc.createBranchDisplayName(sform.getName(), tform.getName(), bform.getBranchNumber()));
	}
	
	/**
	*	Get the BusCell attached to the source port
	*
	* @return the BusCell object
	*/
	public BusCell getSourceBus() {
		DefaultPort sport = (DefaultPort)getSource();
      if (sport == null)
          return null;
		return (BusCell)sport.getParent();
	}	
	
	/**
	*	Get the BusCell attached to the target port
	*
	* @return the BusCell object
	*/
	public BusCell getTargetBus() {
		DefaultPort tport = (DefaultPort)getTarget();
      if (tport == null)
          return null;
		return (BusCell)tport.getParent();
	}
	public String toString() { 
	    return getBranchForm().getLabel(IUserData.BRANCH_LABEL);
	} 
}
