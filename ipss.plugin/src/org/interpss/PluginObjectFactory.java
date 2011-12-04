/*
 * @(#)PluginObjectFactory.java   
 *
 * Copyright (C) 2006-2010 www.interpss.org
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
 * @Date 12/04/2010
 * 
 *   Revision History
 *   ================
 *
 */
package org.interpss;

import org.interpss.custom.IpssFileAdapter;
import org.interpss.custom.fadpter.BPAFormat;
import org.interpss.custom.fadpter.GEFormat;
import org.interpss.custom.fadpter.IeeeCDFFormat;
import org.interpss.custom.fadpter.PTIFormat;
import org.interpss.custom.fadpter.UCTEFormat;
import org.interpss.spring.BasePluginSpringFactory;
import org.interpss.spring.PluginSpringFactory;

import com.interpss.common.exp.InterpssException;

public class PluginObjectFactory {
	public static IpssFileAdapter getFileAdapter(IpssFileAdapter.FileFormat f) throws InterpssException {
		return getFileAdapter(f, IpssFileAdapter.Version.NotDefined);
	}
	
	public static IpssFileAdapter getFileAdapter(IpssFileAdapter.FileFormat f, IpssFileAdapter.Version v)
					throws InterpssException {
		if (f == IpssFileAdapter.FileFormat.IEEECDF) {
			return new IeeeCDFFormat(BasePluginSpringFactory.getIpssMsgHub());
		}
		else if (f == IpssFileAdapter.FileFormat.GE_PSLF) {
			return new GEFormat(BasePluginSpringFactory.getIpssMsgHub());
		} 
		else if (f == IpssFileAdapter.FileFormat.PSSE) {
			return new PTIFormat(v, BasePluginSpringFactory.getIpssMsgHub());
		} 
		else if (f == IpssFileAdapter.FileFormat.BPA) {
			return new BPAFormat(BasePluginSpringFactory.getIpssMsgHub());
		} 
		else if (f == IpssFileAdapter.FileFormat.UCTE) {
			return new UCTEFormat(BasePluginSpringFactory.getIpssMsgHub());
		} 
		else if (f == IpssFileAdapter.FileFormat.IpssInternal) {
			return PluginSpringFactory.getCustomFileAdapter("ipssdat");
		} 
		else if (f == IpssFileAdapter.FileFormat.Custom) {
			
		} 
		throw new InterpssException("Error - File adapter format/version not implemented");
	}
}
