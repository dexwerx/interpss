/*
 * @(#)EditDelete.java	1.2 30.01.2003
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.interpss.editor.coreframework.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.IpssAbstractGraphAction;
import org.interpss.editor.coreframework.jgraphsubclassers.GPGraphModel;
import org.interpss.editor.jgraph.cells.BranchEdge;
import org.interpss.editor.jgraph.cells.BusCell;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.jgraph.graph.DefaultGraphModel;



public class EditDelete extends IpssAbstractGraphAction {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
			Object[] cells = getCurrentGraph().getSelectionCells();
			
			if (cells != null) {
				cells =	DefaultGraphModel.getDescendants(getCurrentGraph().getModel(), cells).toArray();
				for (int i = 0; i < cells.length; i++) {
					if (cells[i] instanceof BusCell) {
						IGFormContainer container = ((GPGraphModel) (getCurrentGraph().getModel())).getGFormContainer();
						BusCell cell = (BusCell)cells[i];
						container.removeBusForm((cell.getBusForm()).getId());
						getCurrentGraph().getModel().remove(new Object[]{cell.getLabel(),cell.get_labelAnnotate()});
					}
					else if (cells[i] instanceof BranchEdge) {
						IGFormContainer container = ((GPGraphModel) (getCurrentGraph().getModel())).getGFormContainer();
						BranchEdge edge = (BranchEdge)cells[i];
						container.removeBranchForm(edge.getBranchForm().getId());
					}
				}
				getCurrentGraph().getModel().remove(cells);
				graphpad.refreshCurrentDocumentEditorPanel();
			}
	}

}
