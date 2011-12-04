package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.IpssAbstractActionDefault;
import org.interpss.grid.gridgain.util.GridEnvHelper;

import com.interpss.common.util.IpssLogger;
import com.interpss.spring.CoreCommonSpringFactory;

public class ToolsDebugGridEnv extends IpssAbstractActionDefault {
	private static final long serialVersionUID = 1;
    
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		IpssLogger.getLogger().info("Tools | Degug | Grid Environment");
		
		if (GridEnvHelper.isGridEnabled()) {
			CoreCommonSpringFactory.getEditorDialogUtil().showMsgDialog("Info", "Grid Computing env has been setup properly");
		}
		else {
			IpssLogger.getLogger().info("Grid Computing env has not been set up properly");
			CoreCommonSpringFactory.getEditorDialogUtil().showWarnMsgDialog("Warnnig", "Cannot start Grid computation environment");
		}
	}
}
