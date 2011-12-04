package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.IpssAbstractActionDefault;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.doc.IpssDocument;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.util.DocumentUtilFunc;

import com.interpss.common.util.IpssLogger;

public class EditProjectData extends IpssAbstractActionDefault {
	private static final long serialVersionUID = 1;

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		IpssLogger.getLogger().info("Starting Editing Project Data and launch Dialog");
//		IpssProjectItem item = graphpad.getCurrentProjectItem();
//		if ((item == null) || (!item.isMain()) || (!item.isLoaded()))
//			return;
		
//		IpssDocument doc = item.getDocument();
		IpssDocument doc = getCurrentDocument();
		if (doc instanceof GPDocument)
		{
			GraphSpringFactory.getEditorDialog(null, ((GPDocument)doc).getGraph());
			doc.getProjData().setDirty(true);
			doc.setModified(true);
		}
	}
	
	public void update() {
		IpssEditorDocument doc = getCurrentDocument();
		setEnabled(DocumentUtilFunc.isGraphicDocument(doc));
	}
}
