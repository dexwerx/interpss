 /*
  * @(#)EditorSpringAppContext.java   
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

import org.interpss.editor.app.AppContext;
import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.output.ISimuRecManager;
import org.interpss.ui.IProjectDataManager;
import org.interpss.ui.IRefDataManager;

import com.interpss.common.datatype.Constants;
import com.interpss.spring.CoreCommonSpringFactory;

public class EditorSpringFactory extends CoreCommonSpringFactory {
	/**
	 * Get the GEditor(singleton) from the SpringAppContext.
	 */
	public static GPGraphpad getGraphicEditor() {
		return (GPGraphpad)SpringAppCtx.getBean("ipssGraphicEditor");
	}
	
	/**
	 * Get the AppContext(singleton) from the SpringAppContext.
	 */
	public static AppContext getAppContext() {
		return (AppContext)SpringAppCtx.getBean("ipssAppContext");
	}

	/**
	 * Get the DB RefDataManager(singleton) object.
	 */
	public static IAppStatus getStatusPanel() {
		return (IAppStatus)SpringAppCtx.getBean("ipssStatusPanel");
	}

	/**
	 * Get the DB RefDataManager(singleton) object.
	 */
	public static IRefDataManager getRefDataManager() {
		return (IRefDataManager)SpringAppCtx.getBean("refDataManager");
	}

	/**
	 * Get the DB ProjectDataManager(singleton) object.
	 */
	public static IProjectDataManager getProjectDataManager() {
		return (IProjectDataManager)SpringAppCtx.getBean("projectDataManager");
	}

	/**
	 * Get the DB SimuRecManager(singleton) object.
	 */
	public static ISimuRecManager getSimuRecManager() {
		return (ISimuRecManager)SpringAppCtx.getBean("simuRecManager");
	}
	
	/**
	 * Get the AppSimuContext from the SpringAppContext.
	 *  
	 * @return the AppSimuContext object
	 */
	public static IAppSimuContext getAppSimuContext() {
		return (IAppSimuContext) SpringAppCtx
				.getBean("ipssAppSimuContext");
	}	
}
