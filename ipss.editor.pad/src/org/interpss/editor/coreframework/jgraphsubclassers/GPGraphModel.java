/*
 * Created on 05.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.interpss.editor.coreframework.jgraphsubclassers;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.interpss.editor.coreframework.GPUserObject;
import org.interpss.editor.jgraph.cells.BusCell;
import org.interpss.editor.jgraph.cells.LabelCell;
import org.interpss.editor.jgraph.ui.IIpssGraphModel;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.editor.util.ICellBuisnessObject;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Port;

import com.interpss.common.util.IpssLogger;


/**
 * The base class JGraph graph model subclassers for GPGraphpad. To use a
 * custom GraphModel subcalsser register your subclass in the graph model
 * factory. As part of the non copyrihted online documentation of JGraph (FAQ
 * section), this file is released here under the LGPL license as stated by the
 * Free Software Fundation.
 * 
 * @see org.interpss.editor.coreframework.GPGraphpadModel
 */
public class GPGraphModel extends DefaultGraphModel implements IIpssGraphModel {

	private IGFormContainer _netContainer = null;

	/* not used any more
	private ProjData projData = null;
	*/
	public GPGraphModel(IGFormContainer container) {
		super();
        this.setGFormContainer(container);
		IpssLogger.getLogger().info("IpssModel object constructed");
	}

	// required for serialization
	public GPGraphModel(List roots, AttributeMap attributes) {
		super(roots, attributes);
	}

	protected Object cloneUserObject(Object userObject) {
		if (userObject instanceof ICellBuisnessObject)
			return ((ICellBuisnessObject) userObject).clone();
		else if (userObject instanceof IGBusForm) {
//			return _netContainer.createGBusForm((IGBusForm)userObject);
			return _netContainer.createGBusForm();
		}
		else if (userObject instanceof IGBranchForm) {
			return _netContainer.createGBranchForm((IGBranchForm)userObject);
		}
		return super.cloneUserObject(userObject);
	}

	protected Object cloneCell(Object cellObj) {
		if (cellObj instanceof BusCell) {
			BusCell buscell = new BusCell(cloneUserObject(((BusCell) cellObj)
					.getUserObject()));
			buscell.getLabel().setAttributes((AttributeMap)(((BusCell) cellObj).getLabel().getAttributes().clone()));
			buscell.get_labelAnnotate().setAttributes((AttributeMap)(((BusCell) cellObj).get_labelAnnotate().getAttributes().clone()));
			return buscell;
		}
		return super.cloneCell(cellObj);
	}

	public Map cloneCells(Object[] cells) {
		Map map = new Hashtable();
		// Add Cells to Queue
		for (int i = 0; i < cells.length; i++) {
			if (!((cells[i] instanceof LabelCell) && (((LabelCell) cells[i])
					.getParentCell() instanceof BusCell))) {
				Object clone = cloneCell(cells[i]);
				map.put(cells[i], clone);
				if (cells[i] instanceof BusCell) {
					
					map.put(((BusCell)cells[i]).getLabel(), ((BusCell)clone).getLabel());
					map.put(((BusCell)cells[i]).get_labelAnnotate(), ((BusCell)clone).get_labelAnnotate());
				}
			}
		}

		// Replace Parent and Anchors
		Iterator it = map.entrySet().iterator();
		Object obj, cell, parent;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			obj = entry.getValue();
			cell = entry.getKey();

			// Replaces the cloned cell's parent with the parent's clone
			parent = getParent(cell);
			if (parent != null)
				parent = map.get(parent);
			if (parent != null)
				((DefaultMutableTreeNode) parent)
						.add((DefaultMutableTreeNode) obj);

			// Replaces the anchors for ports
			if (obj instanceof Port) {
				Object anchor = ((Port) obj).getAnchor();
				if (anchor != null)
					((Port) obj).setAnchor((Port) map.get(anchor));
			}
		}
		return map;
	}
	
	
	public Object valueForCellChanged(Object cell, Object newValue) {
		Object userObject = getValue(cell);
		if (userObject instanceof GPUserObject && newValue instanceof String) {
			ICellBuisnessObject user = (ICellBuisnessObject) userObject;
			Object oldLabel = String.valueOf(user);
			user.setValue(newValue);
			return oldLabel;
		}

		return super.valueForCellChanged(cell, newValue);
	}

	public void setGFormContainer(IGFormContainer _netContainer) {
		this._netContainer = _netContainer;
	}

	public IGFormContainer getGFormContainer() {
		return _netContainer;
	}

	/* not used anymore
	public ProjData getProjData() { return this.projData; }
	public void setProjData(ProjData info) {
		this.projData = info;
	}
	*/
}

