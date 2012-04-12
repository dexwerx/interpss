 /*
  * @(#)CR_UserTestCases.java   
  *
  * Copyright (C) 2008 www.interpss.org
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
  * @Date 02/15/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.pwd;

import java.util.logging.Level;

import org.ieee.odm.common.ODMLogger;
import org.interpss.IpssPlugin;
import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;

public class NEISO_LF {
	public static void main(String args[]) throws InterpssException {
		IpssPlugin.init();

		ODMLogger.getLogger().setLevel(Level.INFO);
		AclfNetwork net = PluginObjectFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.PWD)
				.load("testData/pwd/neiso_test.aux")
				.getAclfNet();	
		System.out.println("No of buses: " + net.getNoBus() + ", branches: " + net.getNoBranch());
	}	
}

