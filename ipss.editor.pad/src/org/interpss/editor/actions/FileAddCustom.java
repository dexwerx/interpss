package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.IpssAbstractProjectAction;
import org.interpss.editor.coreframework.IpssCustomFile;
import org.interpss.editor.coreframework.IpssTextFile;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.doc.IpssProjectItem;
import org.interpss.editor.project.IpssNewCustomDialog;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;



public class FileAddCustom extends IpssAbstractProjectAction {
	public void actionPerformed(ActionEvent e) {

		String defaultname = Translator.getString("NewCustomData");
		
		IpssProject project = graphpad.getCurrentProject();
		
		IpssNewCustomDialog fileSelector = new IpssNewCustomDialog(graphpad, project.getProjectName()+" - "+defaultname);
		fileSelector.init(project,defaultname);
		
		if (fileSelector.isCancelExit()) return;
		
		String fileName = fileSelector.getSrcFileName();
		if (fileName==null){
			graphpad.error("Target Data file is error.");
			return;	
		}
		
		String dstfile= fileSelector.getFileName();
		if (!Utilities.copy(fileSelector.getSrcFileName(),dstfile)) {
			graphpad.error("Can't create Data file.");
			return;
		}
		
		if (fileSelector.hasDstabFile()) {
			String dstfileDstab = fileSelector.getDstabFileName();
			if (!Utilities.copy(fileSelector.getDstabSrcFileName(),dstfileDstab)) {
				graphpad.error("Can't create Dstab Data file.");
				return;
			}
		}

		try {
			// we open a file with version number selected by user, we should check if the number 
			// is correct.
			IpssCustomFile file = org.interpss.editor.util.Utilities.OpenCustomFile(graphpad,dstfile, fileSelector.getVersion());
			IpssProjectItem item = graphpad.addCustomDocument(dstfile, project, file);
			if (fileSelector.hasDstabFile()) {
				String dstfileDstab = fileSelector.getDstabFileName();
				IpssTextFile fileDstab = new IpssTextFile(dstfileDstab);
				graphpad.addTextDocument(dstfileDstab, item, fileDstab, false);
			}
			org.interpss.editor.util.Utilities.loadProjectData(item);
		} catch (Exception e1) {
			e1.printStackTrace();
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
