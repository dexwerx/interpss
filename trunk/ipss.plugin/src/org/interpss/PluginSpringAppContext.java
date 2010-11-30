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

package org.interpss;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.interpss.custom.IpssFileAdapter;
import org.interpss.custom.run.ICustomRunScriptPlugin;
import org.interpss.mapper.IpssXmlMapper;
import org.interpss.output.IOutputSimuResult;
import org.interpss.schema.AclfAlgorithmXmlType;
import org.interpss.schema.AcscStudyCaseXmlType;
import org.interpss.schema.DStabStudyCaseXmlType;

import com.interpss.common.SpringAppContext;
import com.interpss.common.datatype.Constants;
import com.interpss.common.mapper.IMapping;
import com.interpss.common.mapper.IpssMapper;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.core.algorithm.SimpleFaultAlgorithm;
import com.interpss.dstab.DynamicSimuAlgorithm;

public class PluginSpringAppContext extends SpringAppContext {
	/**
	 * Get the IpssXmlMapper(singleton) from the SpringAppContext.
	 *  
	 * @return the RunForm2AlgorithmMapper object
	 */
	public static IpssMapper getIpssXmlMapper() {
		if (SpringAppCtx == null) // for grid computing
			return new IpssXmlMapper(SpringAppContext.getIpssMsgHub());
		return (IpssMapper) SpringAppCtx.getBean(Constants.SID_IpssXmlMapper);
	}

	public static JDialog getCaseInfoDialog() {
		return (JDialog) SpringAppCtx.getBean(Constants.SID_CaseInfoDialog);
	}

	/**
	 * Get the CustomFileAdapterList(singleton) from the SpringAppContext.
	 *  
	 * @return the CustomFileAdapterList object
	 */
	@SuppressWarnings("unchecked")
	public static List<IpssFileAdapter> getCustomFileAdapterList() {
		return (List<IpssFileAdapter>) SpringAppCtx.getBean(Constants.SID_CustomFileAdapterList);
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
		return (List<ICustomRunScriptPlugin>) SpringAppCtx.getBean(Constants.SID_CustomScriptRunPluginList);
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
		return (IOutputSimuResult) SpringAppCtx.getBean(Constants.SID_SimuResultOutput);
	}
	
	/**
	 * Get the RunForm2AlgorithmMapper(singleton) from the SpringAppContext.
	 *  
	 * @return the RunForm2AlgorithmMapper object
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
}
