package org.interpss.editor.coreframework.actions.celltoolboxes;

import java.awt.geom.Rectangle2D;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.jgraph.cells.SimpleLabelCell;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;


public class ToolBoxSimpleLabel extends AbstractDefaultVertexnPortsCreator {
	
	public GraphCell createCell() {
		GPDocument doc = getCurrentDocument();
		return new SimpleLabelCell(null, "Input here");
	}

	/**
	 * You can override how the view attributes of the cell are created
	 * 
	 * @param cell
	 * @param bounds
	 * @return a new attribute map with the defined bounds
	 */
	public AttributeMap getAttributeMap(GraphCell cell,	Rectangle2D bounds) {
		AttributeMap attributeMap = new AttributeMap();
		double scale = graphpad.getCurrentGraph().getScale();
		bounds.setRect(bounds.getX()/scale, bounds.getY()/scale, bounds.getWidth()/scale, bounds.getHeight()/scale);
		bounds = reSize(bounds); // thus we can constraint the bounding box
									// size
		GraphConstants.setBounds(attributeMap, bounds);
		GraphConstants.setOpaque(attributeMap, true);
		GraphConstants.setInset(attributeMap, 40);
	//	GraphConstants.setBorderColor(attributeMap, Color.black);
		GraphConstants.setFont(attributeMap, ((SimpleLabelCell)cell).getFont());
		return attributeMap;
	}
}