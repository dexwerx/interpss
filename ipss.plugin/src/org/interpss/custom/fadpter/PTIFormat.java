 /*
  * @(#)PTIFormat.java   
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

package org.interpss.custom.fadpter;

import org.ieee.odm.ODMFileFormatEnum;
import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.adapter.IODMAdapter;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.custom.fadpter.impl.IpssFileAdapterBase;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.simu.SimuContext;

public class PTIFormat extends IpssFileAdapterBase {
	private IpssFileAdapter.Version version = IpssFileAdapter.Version.NotDefined;
	
	public PTIFormat(IPSSMsgHub msgHub) {
		super(msgHub);
		this.version = IpssFileAdapter.Version.PSSE_30;
	}

	public PTIFormat(IpssFileAdapter.Version v, IPSSMsgHub msgHub) {
		super(msgHub);
		this.version = v;
	}
	
	/**
	 * Load the data in the data file, specified by the filepath, into the SimuContext object. An AclfAdjNetwork
	 * object will be created to hold the data for loadflow analysis.
	 * 
	 * @param simuCtx the SimuContext object
	 * @param filepath full path path of the input file
	 * @param msg the SessionMsg object
	 */
	@Override
	public void load(final SimuContext simuCtx, final String filepath, boolean debug, String outfile) throws Exception{
		IODMAdapter adapter = ODMObjectFactory.createODMAdapter(
				this.version == IpssFileAdapter.Version.PSSE_26? ODMFileFormatEnum.PsseV26 : 
						ODMFileFormatEnum.PsseV30);
		loadByODMTransformation(adapter, simuCtx, filepath, msgHub, debug, outfile);
	}
	
	/**
	 * Create a SimuContext object and Load the data in the data file, specified by the filepath, into the object. 
	 * An AclfAdjNetwork object will be created to hold the data for loadflow analysis.
	 * 
	 * @param filepath full path path of the input file
	 * @param msg the SessionMsg object
	 * @return the created SimuContext object.
	 */
//	@Override
//	public SimuContext load(final String filepath) throws Exception{
//  		final SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.NOT_DEFINED, msgHub);
//  		load(simuCtx, filepath, false);
//  		return simuCtx;
//	}
	
	/**
	 * @param versionSelected the versionSelected to set
	 */
	@Override public void setVersionSelected(String versionSelected) {
		super.setVersionSelected(versionSelected);
		this.version = versionSelected.equals("PSS/E-30")?
				IpssFileAdapter.Version.PSSE_30 : 
					(versionSelected.equals("PSS/E-29")? 
							IpssFileAdapter.Version.PSSE_29 : IpssFileAdapter.Version.PSSE_26);
	}	
}