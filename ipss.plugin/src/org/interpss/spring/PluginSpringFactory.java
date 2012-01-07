/*
 * @(#)PluginSpringCtx.java   
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

import javax.swing.JDialog;

import org.interpss.custom.IpssFileAdapter;
import org.interpss.custom.run.ICustomRunScriptPlugin;
import org.interpss.editor.form.GFormContainer;
import org.interpss.output.IOutputSimuResult;
import org.interpss.output.ISimuRecManager;
import org.interpss.ui.IDialogUtil;
import org.interpss.ui.IProjectDataManager;
import org.interpss.ui.IRefDataManager;
import org.interpss.xml.XmlNetParamModifier;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.DStabStudyCaseXmlType;
import org.interpss.xml.schema.ModificationXmlType;

import com.interpss.common.mapper.IMapping;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.core.net.Network;
import com.interpss.dist.DistNetwork;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.spring.CoreSimuSpringFactory;

public class PluginSpringFactory extends CoreSimuSpringFactory {
	/**
	 * Get the IEditorDialogUtil(singleton) from the SpringAppContext.
	 *  
	 * @return the EditorDialogUtil object
	 */
	public static IDialogUtil getEditorDialogUtil() {
		return (IDialogUtil) springAppCtx.getBean("editorDialogUtil");
	}	
	
	/**
	 * Get the SimuRecManager(singleton) from the SpringAppContext.
	 *  
	 * @return the RefDataManager object
	 */
	public static ISimuRecManager getSimuRecManager() {
		return (ISimuRecManager) springAppCtx.getBean("simuRecManager");
	}

	public static JDialog getCaseInfoDialog() {
		return (JDialog) springAppCtx.getBean("caseInfoDialog");
	}

	/**
	 * Get the RefDataManager(singleton) from the SpringAppContext.
	 *  
	 * @return the RefDataManager object
	 */
	public static IRefDataManager getRefDataManager() {
		return (IRefDataManager) springAppCtx.getBean("refDataManager");
	}
	
	/**
	 * Get the SimuRecManager(singleton) from the SpringAppContext.
	 *  
	 * @return the RefDataManager object
	 */
	public static IProjectDataManager getProjectDataDBManager() {
		return (IProjectDataManager) springAppCtx
				.getBean("projectDataManager");
	}
	
	/**
	 * Get the CustomFileAdapterList(singleton) from the SpringAppContext.
	 *  
	 * @return the CustomFileAdapterList object
	 */
	@SuppressWarnings("unchecked")
	public static List<IpssFileAdapter> getCustomFileAdapterList() {
		return (List<IpssFileAdapter>) springAppCtx.getBean("customFileAdapterList");
	}

	/**
	 * This method will be retired. Use getCustomFileAdapterByName instead
	 * 
	 * @return the CustomFileAdapter object
	 */
	public static IpssFileAdapter getCustomFileAdapter(String ext) {
		List<IpssFileAdapter> adapterList = getCustomFileAdapterList();
		for (int i = 0; i < adapterList.size(); i++) {
			IpssFileAdapter adapter = adapterList.get(i);
			if (ext.equals(adapter.getExtension()))
				return adapter;
		}
		return null;
	}

	/**
	 * Get a CustomFileAdapter(prototype) name list.
	 * 
	 * @return the CustomFileAdapter name list
	 */
	public static Object[] getCustomFileAdapterNameList() {
		List<String> nameList = new ArrayList<String>();
		List<IpssFileAdapter> adapterList = getCustomFileAdapterList();
		for (int i = 0; i < adapterList.size(); i++) {
			IpssFileAdapter adapter = adapterList.get(i);
			nameList.add(adapter.getName());
		}
		return nameList.toArray();
	}

	/**
	 * Get a CustomFileAdapter(prototype) name list.
	 * 
	 * @return the CustomFileAdapter name list
	 */
	public static IpssFileAdapter getCustomFileAdapterByName(String name) {
		List<IpssFileAdapter> adapterList = getCustomFileAdapterList();
		for (int i = 0; i < adapterList.size(); i++) {
			IpssFileAdapter adapter = adapterList.get(i);
			if (name.equals(adapter.getName()))
				return adapter;
		}
		return null;
	}

	
	/**
	 * Get the CustomScriptRunPluginList(singleton) from the SpringAppContext.
	 *  
	 * @return the CustomFileAdapterList object
	 */
	@SuppressWarnings("unchecked")
	public static List<ICustomRunScriptPlugin> getCustomScriptRunPluginList() {
		return (List<ICustomRunScriptPlugin>) springAppCtx.getBean("customScriptRunPluginList");
	}
	
	/**
	 * Get a CustomScriptRunPlugin(prototype) name list.
	 * 
	 * @return the CustomScriptRunPlugin name list
	 */
	public static Object[] getCustomScriptRunPluginNameList() {
		List<String> nameList = new ArrayList<String>();
		List<ICustomRunScriptPlugin> adapterList = getCustomScriptRunPluginList();
		for (int i = 0; i < adapterList.size(); i++) {
			ICustomRunScriptPlugin adapter = adapterList.get(i);
			nameList.add(adapter.getName());
		}
		return nameList.toArray();
	}
	
	/**
	 * Get a CustomScriptRunPlugin(prototype) name list.
	 * 
	 * @return the CustomScriptRunPlugin name list
	 */
	public static ICustomRunScriptPlugin getCustomScriptRunPlugin(String name) {
		List<ICustomRunScriptPlugin> adapterList = getCustomScriptRunPluginList();
		for (int i = 0; i < adapterList.size(); i++) {
			ICustomRunScriptPlugin adapter = adapterList.get(i);
			if (name.equals(adapter.getName()))
				return adapter;
		}
		return null;
	}	
	
	/**
	 * Get the SimuResultOutput(singleton) from the SpringAppContext.
	 *  
	 * @return the CustomFileAdapterList object
	 */
	public static IOutputSimuResult getSimuResultOutput() {
		return (IOutputSimuResult) springAppCtx.getBean("simuResultOutput");
	}
	
	/*
	 * 		Mapper definition Xml -> Algo
	 * 		=============================
	 */
	@SuppressWarnings("unchecked")
	public static IMapping<AclfAlgorithmXmlType, LoadflowAlgorithm> getXml2LfAlgorithmMapper() {
		return (IMapping<AclfAlgorithmXmlType, LoadflowAlgorithm>) springAppCtx.getBean("xml2LfAlgorithmMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<AcscStudyCaseXmlType, SimpleFaultAlgorithm> getXml2ScAlgorithmMapper() {
		return (IMapping<AcscStudyCaseXmlType, SimpleFaultAlgorithm>) springAppCtx.getBean("xml2ScAlgorithmMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<DStabStudyCaseXmlType, DynamicSimuAlgorithm> getXml2DStabAlgorithmMapper() {
		return (IMapping<DStabStudyCaseXmlType, DynamicSimuAlgorithm>) springAppCtx.getBean("xml2DStabAlgorithmMapper");
	}

	@SuppressWarnings("unchecked")
	public static IMapping<ModificationXmlType, Network> getModXml2NetMapper() {
		if (springAppCtx == null) // for grid computing
			return new XmlNetParamModifier();
		return (IMapping<ModificationXmlType, Network>) springAppCtx.getBean("modification2NetMapper");
	}
	
	/*
	 * 		Mapper definition InterPSS Form data  -> Network
	 * 		=============================
	 */
	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, AclfNetwork> getForm2AclfNetMapper() {
		return (IMapping<GFormContainer, AclfNetwork>) springAppCtx.getBean("aclfForm2AclfNetMapper");
	}	
	
	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, AcscNetwork> getForm2AcscNetMapper() {
		return (IMapping<GFormContainer, AcscNetwork>) springAppCtx.getBean("acscForm2AcscNetMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, DStabilityNetwork> getForm2DStabNetMapper() {
		return (IMapping<GFormContainer, DStabilityNetwork>) springAppCtx.getBean("dstabForm2DStabNetMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, DistNetwork> getForm2DistNetMapper() {
		return (IMapping<GFormContainer, DistNetwork>) springAppCtx.getBean("distForm2DistNetMapper");
	}	
}
