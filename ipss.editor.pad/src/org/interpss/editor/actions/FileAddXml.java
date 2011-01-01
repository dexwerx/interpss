package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.IpssAbstractProjectAction;
import org.interpss.editor.coreframework.IpssXmlFile;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.project.IpssNewXmlDialog;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;


public class FileAddXml extends IpssAbstractProjectAction {

	public void actionPerformed(ActionEvent e) {

		String defaultname = Translator.getString("NewXmlData");
		
		IpssProject project = graphpad.getCurrentProject();
		
		IpssNewXmlDialog editor = new IpssNewXmlDialog(graphpad, project.getProjectName()+" - "+defaultname);
		editor.init(project,defaultname);
		
		if (editor.isCancelExit()) return;
		
		if (editor.isNewFile()){
			if (graphpad.getProjectPanel().isProjectSelected())
				graphpad.addXmlDocument(editor.getFileName(), project);
			else
				graphpad.addXmlDocument(editor.getFileName(), graphpad.getCurrentProjectItem());
			graphpad.getCommand("FileSave").actionPerformed(e);
		}
		else
		{
			IpssXmlFile file = editor.getFile();
			if (file==null){
				graphpad.error("Target XML File is error.");
				return;	
			}
			
			String dstfile= editor.getFileName();
			if (!Utilities.copy(editor.getSrcFileName(),dstfile)) 
			{
				graphpad.error("Can't create XML File.");
				return;
			}

			if (graphpad.getProjectPanel().isProjectSelected())
				graphpad.addXmlDocument(dstfile,project, file);
			else
				graphpad.addXmlDocument(dstfile,graphpad.getCurrentProjectItem(), file);
			
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
