package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.GEditor;
import org.interpss.editor.IpssPropertiesLoader;
import org.interpss.editor.coreframework.IpssAbstractActionDefault;
import org.interpss.ui.Workspace;

public class FileSelectWorkspaceUser extends IpssAbstractActionDefault {
	private static final long serialVersionUID = 1;

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		GEditor.getGraphPad().closeWorkspace(e);
		GEditor.getGraphPad().getSmartFrame().setVisible(false);
		GEditor.getGraphPad().getSmartFrame().dispose();

		IpssPropertiesLoader.setUserPty(GEditor.Pty_CurrentWorkspace, GEditor.Pty_UserWorkspace);
		GEditor.exitEditor();
	}
	
	public void update() {
		setEnabled(Workspace.getCurrentType() == Workspace.Type.Sample);
	}	
}
