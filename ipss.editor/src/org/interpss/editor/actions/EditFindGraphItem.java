package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.IpssAbstractGraphAction;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.doc.IpssDocument;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.edit.IElemSearchDialog;
import org.interpss.editor.util.DocumentUtilFunc;

import com.interpss.common.util.IpssLogger;

public class EditFindGraphItem extends IpssAbstractGraphAction {
	private static final long serialVersionUID = 1;

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {


		IpssDocument doc = graphpad.getCurrentDocument();

		if (doc instanceof GPDocument)
		{
			IElemSearchDialog dialog = GraphSpringFactory.getSearchDialog(GraphSpringFactory.EditorType_FindDistObject, ((GPDocument)doc).getGraph());
			if (dialog.getSelectedObj() != null) {
				IpssLogger.getLogger().info("Find a Bus/Branch object and launch Bus/BranchEditorDialog");
				GraphSpringFactory.getEditorDialog(dialog.getSelectedObj(), ((GPDocument)doc).getGraph());
			}
		}
	}
	
	public void update() {
		IpssEditorDocument doc = getCurrentDocument();
		setEnabled(DocumentUtilFunc.isGraphicDocument(doc));
	}
	
}