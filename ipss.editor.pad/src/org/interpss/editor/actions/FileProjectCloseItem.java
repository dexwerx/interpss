package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.IpssAbstractGraphAction;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.doc.IpssProjectItem;


public class FileProjectCloseItem extends IpssAbstractGraphAction {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		IpssProjectItem projectItem = graphpad.getCurrentProjectItem();
		// if ((projectItem != null)
		// && (projectItem.getDocument() instanceof GPDocument)) {
		// graphpad.closeDocument((GPDocument) projectItem.getDocument());

		if ((projectItem != null)
				&& (projectItem.getDocument() instanceof IpssEditorDocument)) {
			graphpad.closeDocument((IpssEditorDocument) projectItem
					.getDocument());
		}
		graphpad.update();
	}

	public void update() {
		if (graphpad.getCurrentProjectItem() == null)
			setEnabled(false);
		else
			setEnabled(true);
	}

}