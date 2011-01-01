/*
 * @(#)EditUndo.java	1.2 30.01.2003
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
import java.util.Vector;

import javax.swing.undo.CannotUndoException;

import org.interpss.editor.coreframework.IpssAbstractGraphAction;



public class EditUndo extends IpssAbstractGraphAction {

	protected Vector menuItems = new Vector();

	public void actionPerformed(ActionEvent e) {
		try {
			getCurrentDocument().getGraphUndoManager().undo(
				graphpad.getCurrentGraph().getGraphLayoutCache());
		} catch (CannotUndoException ex) {
			System.out.println("Unable to graphUndoManager: " + ex);
			ex.printStackTrace();
		}
		graphpad.update();
	}

	public void update() {
		setEnabled(false);

		/* function currently not implemented
		Enumeration e_num = menuItems.elements();
		while (e_num.hasMoreElements()) {
			JMenuItem item = (JMenuItem) e_num.nextElement();
			if (getCurrentDocument() != null &&
				getCurrentDocument().getGraphUndoManager().canUndo(getCurrentGraphLayoutCache())) {
				setEnabled(true);
				item.setText(
					getCurrentDocument().getGraphUndoManager().getUndoPresentationName());
			} else {
				setEnabled(false);
				item.setText(Translator.getString("Component.EditUndo.Text"));
			}
		}
		*/
	}

	/**
	 * @see org.jgraph.pad.actions.IpssAbstractGraphAction#getMenuComponent(String)
	 */
	/* function currently not implemented
	protected Component getMenuComponent(String actionCommand) {
		JMenuItem item = new JMenuItem(this);

		GPBarFactory.fillMenuButton(item, getName(), actionCommand);

		menuItems.add(item); 
		return item;
	}
    */
}
