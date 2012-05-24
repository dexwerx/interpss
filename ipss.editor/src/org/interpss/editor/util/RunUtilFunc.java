 /*
  * @(#)RunUtilFunc.java   
  *
  * Copyright (C) 2006-2007 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 12/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.editor.util;

import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.io.CustomFileUtility;
import org.interpss.editor.ui.EditorActionAdapter;
import org.interpss.spring.EditorPluginSpringFactory;

import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringFactory;

public class RunUtilFunc  {
	/**
	 * perform the run action 
	 * 
	 * @param doc current editor document
	 * @param type run type (Aclf, Dclf ....)
	 * @param graphpad the graph pad object
	 */
	public static void performRunAction(IpssEditorDocument doc, SimuRunEnum type, GPGraphpad graphpad) {
		if (graphpad.isBGProcessingBusy()) {
			EditorPluginSpringFactory.getEditorDialogUtil().showWarnMsgDialog("Simulation Thread Busy", 
				"The run-simulation thread is busy. Please wait for its finishing before starting another one.");
			return;
		}	

		if (doc instanceof GPDocument) {
			EditorActionAdapter.menu_run(type, true, graphpad.getCurrentGraph(), doc);
		} 
		else if (doc instanceof IpssCustomDocument) {
			if (((IpssCustomDocument)doc).getDocFile().isModified()) {
				String filepath = graphpad.getCurrentProject().getProjectPath() + "/" + 
				((IpssCustomDocument)doc).getFileName();
				SimuContext simuCtx = (SimuContext)doc.getSimuAppContext().getSimuCtx();
				// at this stage, we open a file, such as PSS/E, the version is inside the file
				if (CustomFileUtility.loadCustomFile(filepath, "", simuCtx)) {
					EditorActionAdapter.menu_run(type, false, null, doc);
					doc.getSimuAppContext().setSimuNetDataDirty(false);
				}	
				else {
					CoreCommonSpringFactory.getIpssMsgHub().sendWarnMsg("Custom data file loading error, filename: " + filepath);
				}
			} 
			else {
				EditorActionAdapter.menu_run(type, false, null, doc);
			}
		}
		// After a run, some menuitems may need to be enabled
		graphpad.update();
	}
}
