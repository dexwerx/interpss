 /*
  * @(#)UISpringAppContext.java   
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

import java.util.ArrayList;
import java.util.List;

import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.ui.IScriptPluginEditing;

import com.interpss.common.util.IpssLogger;
import com.interpss.spring.CoreCommonSpringFactory;

public class UISpringFactory extends CoreCommonSpringFactory {
	public static int BrowserDialog_TextRows = 30;
	public static int BrowserDialog_TextColumns = 130;
	
	/**
	 * Get the OutputTextDialog(singleton) from the SpringAppContext.
	 *  
	 * @return the OutputTextDialog object
	 */	
	public static IOutputTextDialog getOutputTextDialog(String title) {
		IOutputTextDialog dialog = (IOutputTextDialog)SpringAppCtx.getBean("outputTextDialog");
		dialog.setTitle(title);
		return dialog;
	}
	
	/**
	 * Get the ServiceScheduleDialog(singleton) from the SpringAppContext.
	 *  
	 * @return the ServiceScheduleDialog object
	 */	
	public static IFormDataDialog getServiceScheduleDialog() {
		return (IFormDataDialog)SpringAppCtx.getBean("serviceScheduleDialog");
	}	
	
	/**
	 * Get the LoadScheduleDialog(singleton) from the SpringAppContext.
	 *  
	 * @return the LoadScheduleDialog object
	 */	
	public static IFormDataDialog getLoadScheduleDialog() {
		return (IFormDataDialog)SpringAppCtx.getBean("loadScheduleDialog");
	}	
	
	/**
	 * Get the CustomAclfBusScriptPluginList(singleton) from the SpringAppContext.
	 *  
	 * @return the CustomAclfBusScriptPluginList object
	 */	
	public static List<IScriptPluginEditing> getCustomScriptPluginList(String id) {
		return (List<IScriptPluginEditing>)SpringAppCtx.getBean(id);
	}	
	
	/**
	 * Get the AclfBusScriptingPlugin(prototype) name list.
	 *  
	 * @return the AclfBusScriptingPlugin name list
	 */	
	public static Object[] getCustomAclfBusScriptPluginNameList() {
		return getCustomScriptPluginNameList("customAclfBusScriptPluginList");
	}	

	/**
	 * Get the AclfBusScriptingPlugin(prototype) object.
	 *  
	 * @return the AclfBusScriptingPlugin object
	 */	
	public static IScriptPluginEditing getCustomAclfBusScriptPlugin(String name) {
		return getCustomScriptPlugin(name, "customAclfBusScriptPluginList");
	}
	
	/**
	 * Get the AclfBranchScriptingPlugin(prototype) name list.
	 *  
	 * @return the AclfBranchScriptingPlugin name list
	 */	
	public static Object[] getCustomAclfBranchScriptPluginNameList() {
		return getCustomScriptPluginNameList("customAclfBranchScriptPluginList");
	}	

	/**
	 * Get the AclfBranchScriptingPlugin(prototype) object.
	 *  
	 * @return the AclfBranchScriptingPlugin object
	 */	
	public static IScriptPluginEditing getCustomAclfBranchScriptPlugin(String name) {
		return getCustomScriptPlugin(name, "customAclfBranchScriptPluginList");
	}

	/**
	 * Get the AcscBusScriptingPlugin(prototype) name list.
	 *  
	 * @return the AcscBusScriptingPlugin name list
	 */	
	public static Object[] getCustomAcscBusScriptPluginNameList() {
		return getCustomScriptPluginNameList("customAcscBusScriptPluginList");
	}	

	/**
	 * Get the AcscBusScriptingPlugin(prototype) object.
	 *  
	 * @return the AcscBusScriptingPlugin object
	 */	
	public static IScriptPluginEditing getCustomAcscBusScriptPlugin(String name) {
		return getCustomScriptPlugin(name, "customAcscBusScriptPluginList");
	}
	
	/**
	 * Get the AcscBranchScriptingPlugin(prototype) name list.
	 *  
	 * @return the AcscBranchScriptingPlugin name list
	 */	
	public static Object[] getCustomAcscBranchScriptPluginNameList() {
		return getCustomScriptPluginNameList("customAcscBranchScriptPluginList");
	}	

	/**
	 * Get the AcscBranchScriptingPlugin(prototype) object.
	 *  
	 * @return the AcscBranchScriptingPlugin object
	 */	
	public static IScriptPluginEditing getCustomAcscBranchScriptPlugin(String name) {
		return getCustomScriptPlugin(name, "customAcscBranchScriptPluginList");
	}

	/**
	 * Get the DynamicBusDeviceScriptingPlugin(prototype) name list.
	 *  
	 * @return the DynamicBusDeviceScriptingPlugin name list
	 */	
	public static Object[] getCustomDynamicBusDeviceScriptPluginNameList() {
		return getCustomScriptPluginNameList("customDynamicBusDeviceScriptPluginList");
	}	

	/**
	 * Get the DynamicBusDeviceScriptingPlugin(prototype) object.
	 *  
	 * @return the DynamicBusDeviceScriptingPlugin object
	 */	
	public static IScriptPluginEditing getCustomDynamicBusDeviceScriptPlugin(String name) {
		return getCustomScriptPlugin(name, "customDynamicBusDeviceScriptPluginList");
	}
	
	private static Object[] getCustomScriptPluginNameList(String id) {
		List<String> nameList = new ArrayList<String>();
		List<IScriptPluginEditing> pluginList = getCustomScriptPluginList(id);
		for (IScriptPluginEditing plugin : pluginList) {
			nameList.add(plugin.getName());
		}
		return nameList.toArray();
	}	
	
	private static IScriptPluginEditing getCustomScriptPlugin(String name, String listId) {
		List<IScriptPluginEditing> list = getCustomScriptPluginList(listId);
		for (IScriptPluginEditing plugin : list) {
			if (name.equals(plugin.getName())) {
				return plugin;
			}	
		}
		IpssLogger.getLogger().warning("Costum script plugin not found, name" + name);
		return null;
	}	
}
