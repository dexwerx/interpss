package org.interpss.editor.actions;

import java.awt.event.ActionEvent;
import java.io.FileOutputStream;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.IpssAbstractActionDefault;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.coreframework.IpssTextDocument;
import org.interpss.editor.coreframework.IpssXmlDocument;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.project.IpssCustomDataCodec;
import org.interpss.editor.project.IpssGraphCodec;
import org.interpss.editor.project.IpssTextCodec;
import org.interpss.editor.project.IpssXmlCodec;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.ui.IProjectDataManager;

import com.interpss.common.util.IpssLogger;

/**
 * Action opens a dialog to select the file. After that the action saves the
 * current graph to the selected file.
 */
public class FileSave extends IpssAbstractActionDefault {

	public void actionPerformed(ActionEvent e) {
		{
			String fileName = getCurrentDocument().getName();
			if (fileName == null) {
				// graphpad.getCommand("FileSaveAs").actionPerformed(e);
				return;
			}
			try {
				if (graphpad.getCurrentDocument() instanceof GPDocument) {
					IpssGraphCodec.getInstance(graphpad).write(
							new FileOutputStream(fileName),
							(GPDocument)getCurrentDocument());

					IAppSimuContext appSimuCtx = GraphSpringFactory
							.getIpssGraphicEditor().getCurrentAppSimuContext();
					if (appSimuCtx.getProjData().isDirty()) {
                        /* following modefied by Mike 07/31/06 */
						IProjectDataManager projManager = EditorPluginSpringFactory.getProjectDataDBManager();
						projManager.saveProjectDataToDB(getCurrentDocument().getProjData());
						// added by Mike
						getCurrentDocument().getProjData().setDirty(false);
					}

					// added by Mike
					((GPDocument)getCurrentDocument()).getGFormContainer().setDataDirty(false);
					getCurrentDocument().setModified(false);
				}
				else if (graphpad.getCurrentDocument() instanceof IpssCustomDocument)
				{
					// Richard, we should allow user modified Custom doc and save

					IpssCustomDataCodec.getInstance(graphpad).write(
							new FileOutputStream(fileName),
							(IpssCustomDocument)getCurrentDocument());
					getCurrentDocument().setModified(false);

					
					IAppSimuContext appSimuCtx = GraphSpringFactory
							.getIpssGraphicEditor().getCurrentAppSimuContext();
					if (appSimuCtx.getProjData().isDirty()) {
						IProjectDataManager projManager = EditorPluginSpringFactory.getProjectDataDBManager();
						/* modified by Mike. doc.projData and appSimuCtx.projData are out of synch
						projManager.saveProjectDataToDB(getCurrentDocument().getProjData());
						getCurrentDocument().getProjData().setDirty(false);
						*/
						projManager.saveProjectDataToDB(appSimuCtx.getProjData());
						appSimuCtx.getProjData().setDirty(false);
					}
				}
				else if (graphpad.getCurrentDocument() instanceof IpssXmlDocument)
				{
					IpssXmlCodec.getInstance(graphpad).write(
							new FileOutputStream(fileName),
							(IpssXmlDocument)getCurrentDocument());
					getCurrentDocument().setModified(false);
				}
				else if (graphpad.getCurrentDocument() instanceof IpssTextDocument)
				{
					IpssTextCodec.getInstance(graphpad).write(
							new FileOutputStream(fileName),
							(IpssTextDocument)getCurrentDocument());
					getCurrentDocument().setModified(false);
				}
				graphpad.saveProject(getCurrentDocument().getProject());

			} catch (Exception ex) {
				IpssLogger.logErr(ex);
			}
		}
	}

	public void update() {
		if (graphpad.getCurrentDocument() == null)
			setEnabled(false);
		else if (graphpad.getCurrentDocument() instanceof GPDocument)
			setEnabled(true);
		else if (graphpad.getCurrentDocument() instanceof IpssCustomDocument)
			setEnabled(true);
		else if (graphpad.getCurrentDocument() instanceof IpssXmlDocument)
			setEnabled(true);
		else if (graphpad.getCurrentDocument() instanceof IpssTextDocument)
			setEnabled(true);
		else
			setEnabled(false);
	}
}
