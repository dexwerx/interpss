package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.IpssAbstractProjectAction;
import org.interpss.editor.coreframework.IpssTextFile;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.project.IpssNewTextDialog;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;


public class FileAddText extends IpssAbstractProjectAction {

	public void actionPerformed(ActionEvent e) {

		String defaultname = Translator.getString("NewTextFile");
		
		IpssProject project = graphpad.getCurrentProject();
		
		IpssNewTextDialog editor = new IpssNewTextDialog(graphpad, project.getProjectName()+" - "+defaultname);
		editor.init(project,defaultname);
		
		if (editor.isCancelExit()) return;
		
		if (editor.isNewFile()){
			if (graphpad.getProjectPanel().isProjectSelected())
				graphpad.addTextDocument(editor.getFileName(), project);
			else
				graphpad.addTextDocument(editor.getFileName(), graphpad.getCurrentProjectItem());
			graphpad.getCommand("FileSave").actionPerformed(e);
		}
		else
		{
			IpssTextFile file = editor.getFile();
			if (file==null){
				graphpad.error("Target Text File is error.");
				return;	
			}
			
			String dstfile= editor.getFileName();
			if (!Utilities.copy(editor.getSrcFileName(),dstfile)) 
			{
				graphpad.error("Can't create Text File.");
				return;
			}

			if (graphpad.getProjectPanel().isProjectSelected())
				graphpad.addTextDocument(dstfile,project, file);
			else
				graphpad.addTextDocument(dstfile,graphpad.getCurrentProjectItem(), file, true);
			
		}
		graphpad.saveProject(project);
		graphpad.update();
	}
	
	public void update() {
		if (graphpad.getCurrentProject() == null)
			setEnabled(false);
		else if (graphpad.getProjectPanel().isTextAddable())
			setEnabled(true);
		else
			setEnabled(false);
	}
	
}
