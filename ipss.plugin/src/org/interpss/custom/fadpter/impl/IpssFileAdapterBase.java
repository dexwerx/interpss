 /*
  * @(#)IpssFileAdapterBase.java   
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
  * @Date 09/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.custom.fadpter.impl;

import java.io.File;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.spring.PluginSpringCtx;

import com.interpss.common.exp.InvalidOperationException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class IpssFileAdapterBase implements IpssFileAdapter{
	protected IPSSMsgHub msgHub;

	private String name;
	private String[] versionList = null;
	private String extension;
	private String description;
	private String fileFilterString;
	private String versionSelected;
	
	protected IODMModelParser parser;
	
	public IpssFileAdapterBase(IPSSMsgHub msgHub) {
		this.msgHub = msgHub;
	}
	
	protected void loadByODMTransformation(final IODMAdapter adapter, final SimuContext simuCtx, final String filepath, 
						final IPSSMsgHub msg, boolean debug)  throws Exception{		
		adapter.parseInputFile(filepath);
		this.parser = adapter.getModel();
		if (debug)
			System.out.println(adapter.getModel().toXmlDoc(false));
		
		if (PluginSpringCtx.getOdm2AclfMapper().map2Model((AclfModelParser)adapter.getModel(), simuCtx)) {
  	  		simuCtx.setName(filepath.substring(filepath.lastIndexOf(File.separatorChar)+1));
  	  		simuCtx.setDesc("This project is created by input file " + filepath);
		}
		else {
  			msg.sendErrorMsg("Error to load file: " + filepath);
  			IpssLogger.getLogger().severe("Error to load file: " + filepath);
		}
	}
	
	protected void loadByODMTransformation(final IODMAdapter adapter, final SimuContext simuCtx, final String[] filepathAry, 
			final IPSSMsgHub msg, boolean debug)  throws Exception{		
		adapter.parseInputFile(IODMAdapter.NetType.DStabNet, filepathAry);
		this.parser = adapter.getModel();
		if (debug)
			System.out.println(adapter.getModel().toXmlDoc(false));
		
		// TODO
	}

	@Override
	public IODMModelParser getODMModelParser() {
		return this.parser;
	}

	/**
	 * @return the fileFilterString
	 */
	public String getFileFilterString() {
		return fileFilterString;
	}

	/**
	 * @param fileFilterString the fileFilterString to set
	 */
	public void setFileFilterString(String fileFilterString) {
		this.fileFilterString = fileFilterString;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the versionList
	 */
	public String[] getVersionList() {
		return versionList;
	}

	/**
	 * @param versionList the versionList to set
	 */
	public void setVersionList(String[] versionList) {
		this.versionList = versionList;
	}

	public String getExtension() {
		return extension;
	}
	
	public void setExtension(String s) {
		extension = s;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String s) {
		description = s;
	}

	public void load(SimuContext simuCtx, String filepath, boolean debug) throws Exception {
		throw new InvalidOperationException("Load need to implemented");
	}

	public void load(SimuContext simuCtx, String[] filepathAry, boolean debug) throws Exception {
		throw new InvalidOperationException("Load need to implemented");
	}

	public SimuContext load(String filepath) throws Exception {
  		final SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.NOT_DEFINED, msgHub);
  		load(simuCtx, filepath, false);
  		return simuCtx;
	}

	public SimuContext loadDebug(String filepath) throws Exception {
  		final SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.NOT_DEFINED, msgHub);
  		load(simuCtx, filepath, true);
  		return simuCtx;
	}

	public AclfNetwork loadAclfNet(String filepath) throws Exception {
		return load(filepath).getAclfNet();
	}

	public boolean save(String filepath, SimuContext net) throws Exception {
		throw new InvalidOperationException("Save need to implemented");
	}

	/**
	 * @return the versionSelected
	 */
	public String getVersionSelected() {
		return versionSelected;
	}

	/**
	 * @param versionSelected the versionSelected to set
	 */
	public void setVersionSelected(String versionSelected) {
		this.versionSelected = versionSelected;
	}
}