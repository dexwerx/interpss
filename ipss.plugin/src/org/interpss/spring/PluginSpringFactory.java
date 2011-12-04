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
import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.interpss.mapper.odm.ODMAcscDataMapper;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.interpss.mapper.odm.ODMOpfDataMapper;
import org.interpss.output.IOutputSimuResult;
import org.interpss.output.ISimuRecManager;
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

public class PluginSpringFactory extends BasePluginSpringFactory {
	/**
	 * Get the SimuRecManager(singleton) from the SpringAppContext.
	 *  
	 * @return the RefDataManager object
	 */
	public static ISimuRecManager getSimuRecManager() {
		return (ISimuRecManager) SpringAppCtx.getBean("simuRecManager");
	}

	public static JDialog getCaseInfoDialog() {
		return (JDialog) SpringAppCtx.getBean("caseInfoDialog");
	}

	/**
	 * Get the CustomFileAdapterList(singleton) from the SpringAppContext.
	 *  
	 * @return the CustomFileAdapterList object
	 */
	@SuppressWarnings("unchecked")
	public static List<IpssFileAdapter> getCustomFileAdapterList() {
		return (List<IpssFileAdapter>) SpringAppCtx.getBean("customFileAdapterList");
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
		return (List<ICustomRunScriptPlugin>) SpringAppCtx.getBean("customScriptRunPluginList");
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
		return (IOutputSimuResult) SpringAppCtx.getBean("simuResultOutput");
	}
	
	/*
	 * 		Mapper definition Odm -> SimuCtx
	 * 		================================
	 */
	public static ODMAclfDataMapper getOdm2AclfMapper() {
		return (ODMAclfDataMapper) SpringAppCtx.getBean("odm2AclfMapper");
	}	

	public static ODMAcscDataMapper getOdm2AcscMapper() {
		return (ODMAcscDataMapper) SpringAppCtx.getBean("odm2AcscMapper");
	}	
	
	public static ODMDStabDataMapper getOdm2DStabMapper() {
		return (ODMDStabDataMapper) SpringAppCtx.getBean("odm2DStabMapper");
	}	

	public static ODMOpfDataMapper getOdm2OpfMapper() {
		return (ODMOpfDataMapper) SpringAppCtx.getBean("odm2OpfMapper");
	}	

	/*
	 * 		Mapper definition Xml -> Algo
	 * 		=============================
	 */
	@SuppressWarnings("unchecked")
	public static IMapping<AclfAlgorithmXmlType, LoadflowAlgorithm> getXml2LfAlgorithmMapper() {
		return (IMapping<AclfAlgorithmXmlType, LoadflowAlgorithm>) SpringAppCtx.getBean("xml2LfAlgorithmMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<AcscStudyCaseXmlType, SimpleFaultAlgorithm> getXml2ScAlgorithmMapper() {
		return (IMapping<AcscStudyCaseXmlType, SimpleFaultAlgorithm>) SpringAppCtx.getBean("xml2ScAlgorithmMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<DStabStudyCaseXmlType, DynamicSimuAlgorithm> getXml2DStabAlgorithmMapper() {
		return (IMapping<DStabStudyCaseXmlType, DynamicSimuAlgorithm>) SpringAppCtx.getBean("xml2DStabAlgorithmMapper");
	}

	@SuppressWarnings("unchecked")
	public static IMapping<ModificationXmlType, Network> getModXml2NetMapper() {
		if (SpringAppCtx == null) // for grid computing
			return new XmlNetParamModifier();
		return (IMapping<ModificationXmlType, Network>) SpringAppCtx.getBean("modification2NetMapper");
	}
	
	/*
	 * 		Mapper definition InterPSS Form data  -> Network
	 * 		=============================
	 */
	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, AclfNetwork> getForm2AclfNetMapper() {
		return (IMapping<GFormContainer, AclfNetwork>) SpringAppCtx.getBean("aclfForm2AclfNetMapper");
	}	
	
	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, AcscNetwork> getForm2AcscNetMapper() {
		return (IMapping<GFormContainer, AcscNetwork>) SpringAppCtx.getBean("acscForm2AcscNetMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, DStabilityNetwork> getForm2DStabNetMapper() {
		return (IMapping<GFormContainer, DStabilityNetwork>) SpringAppCtx.getBean("dstabForm2DStabNetMapper");
	}	

	@SuppressWarnings("unchecked")
	public static IMapping<GFormContainer, DistNetwork> getForm2DistNetMapper() {
		return (IMapping<GFormContainer, DistNetwork>) SpringAppCtx.getBean("distForm2DistNetMapper");
	}	
}
