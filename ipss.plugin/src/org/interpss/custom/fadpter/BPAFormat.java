 /*
  * @(#)BPAFormat.java   
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
  * @Date 02/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.custom.fadpter;

import org.ieee.odm.ODMFileFormatEnum;
import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.adapter.IODMAdapter;
import org.interpss.custom.fadpter.impl.IpssFileAdapterBase;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.simu.SimuContext;

public class BPAFormat extends IpssFileAdapterBase {
	public BPAFormat(IPSSMsgHub msgHub) {
		super(msgHub);
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
	public void load(final SimuContext simuCtx, final String filepath, boolean debug) throws Exception{
		IODMAdapter adapter = ODMObjectFactory.createODMAdapter(ODMFileFormatEnum.BPA);
		loadByODMTransformation(adapter, simuCtx, filepath, msgHub, debug);
 	}
	
	@Override
	public void load(final SimuContext simuCtx, final String[] filepathAry, boolean debug) throws Exception{
		IODMAdapter adapter = ODMObjectFactory.createODMAdapter(ODMFileFormatEnum.BPA);
		loadByODMTransformation(adapter, simuCtx, filepathAry, msgHub, debug);
 	}
}
