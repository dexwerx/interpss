/*
 * @(#)SimuAppSpringAppContext.java   
 *
 * Copyright (C) 2006 www.interpss.org
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
 * @Date 09/15/2006
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.spring;

import javax.swing.JDialog;

import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.editor.mapper.EditorJGraphDataMapper;
import org.interpss.editor.runAct.ui.AclfRunForm;
import org.interpss.editor.runAct.ui.AcscRunForm;
import org.interpss.editor.runAct.ui.DStabRunForm;
import org.interpss.editor.runAct.ui.DclfRunForm;
import org.interpss.editor.ui.ICaseInfoDialog;

import com.interpss.spring.CoreCommonSpringFactory;

public class EditorSimuSpringFactory extends CoreCommonSpringFactory {
	/**
	 * Get the ProjectData from the SpringAppContext.
	 *  
	 * @return the ProjectData object
	 */
	public static IProjectData getProjectData() {
		return (IProjectData) springAppCtx.getBean("ipssProjectData");
	}

	/**
	 * Get the EditorJGraphDataMapper(singleton) from the SpringAppContext.
	 *  
	 * @return the EditorJGraphDataMapper object
	 */
	public static EditorJGraphDataMapper getEditorDataMapper() {
		return (EditorJGraphDataMapper)springAppCtx.getBean("editorJGraphDataMapper");
	}

	public static JDialog getCaseInfoDialog() {
		return (JDialog) springAppCtx.getBean("caseInfoDialog");
	}

	/**
	 * Get the AclfRunForm(singleton) from the SpringAppContext.
	 *  
	 * @return the AclfRunForm object
	 */
	public static AclfRunForm getAclfRunForm() {
		return (AclfRunForm) springAppCtx.getBean("aclfRunForm");
	}

	/**
	 * Get the AclfRunForm(singleton) from the SpringAppContext.
	 *  
	 * @return the AclfRunForm object
	 */
	public static DclfRunForm getDclfRunForm() {
		return (DclfRunForm) springAppCtx.getBean("dclfRunForm");
	}

	/**
	 * Get the ProjectData(singleton) from the SpringAppContext.
	 *  
	 * @return the ProjectData object
	 */
	public static AcscRunForm getAcscRunForm() {
		return (AcscRunForm) springAppCtx.getBean("acscRunForm");
	}

	/**
	 * Get the DStabRunForm(singleton) from the SpringAppContext.
	 *  
	 * @return the DStabRunForm object
	 */
	public static DStabRunForm getDStabRunForm() {
		return (DStabRunForm) springAppCtx.getBean("dstabRunForm");
	}
	
	public static ICaseInfoDialog getCaseInfoDialog(SimuRunEnum type, String filename) {
		ICaseInfoDialog dialog = (ICaseInfoDialog)getCaseInfoDialog();
		dialog.setCaseType(type);
		dialog.setRunStudyCaseFilename(filename);
		return dialog;
	}
}
