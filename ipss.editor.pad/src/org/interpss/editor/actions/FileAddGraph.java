package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.GPGraphpadFile;
import org.interpss.editor.coreframework.IpssAbstractProjectAction;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.doc.IpssProjectItem;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.project.IpssNewGraphDialog;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;

public class FileAddGraph extends IpssAbstractProjectAction {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		String defaultname = Translator.getString("NewGraph");
		
		IpssProject project = graphpad.getCurrentProject();
		
		IpssNewGraphDialog editor = new IpssNewGraphDialog(graphpad, project.getProjectName()+" - "+defaultname);
		editor.init(project,defaultname);
		
		if (editor.isCancelExit()) return;
		
		if (editor.isNewFile()){
			graphpad.addGraphDocument(editor.getFileName(), project);
			((GPDocument)getCurrentDocument()).getProjData().setFilepath(editor.getFileName());
			((GPDocument)getCurrentDocument()).getProjData().setWorkspacePath(
					StringUtil.getWorkspacePath(editor.getFileName()));
			GraphSpringFactory.getEditorDialog(null, ((GPDocument)getCurrentDocument()).getGraph());
			getCurrentDocument().getProjData().setDirty(true);
			graphpad.getCommand("FileSave").actionPerformed(e);
		}
		else
		{
			GPGraphpadFile file = editor.getFile();
			if (file==null){
				graphpad.error("Target graphic document is error.");
				return;	
			}
			
			String dstfile= editor.getFileName();
			if (!Utilities.copy(editor.getSrcFileName(),dstfile)) 
			{
				graphpad.error("Can't create graphic document.");
				return;
			}
			IpssProjectItem item = graphpad.addGraphDocument(dstfile,project, file);
			
			try {
				org.interpss.editor.util.Utilities.loadProjectData(item);
			} catch (Exception ex) {
				IpssLogger.logErr(ex);
			}			
		}
		graphpad.saveProject(project);
		graphpad.update();
	}

	public void update() {
		if (graphpad.getCurrentProject() == null)
			setEnabled(false);
		else if (graphpad.getProjectPanel().isProjectSelected())
			setEnabled(true);
		else
			setEnabled(false);
	}
	
}
