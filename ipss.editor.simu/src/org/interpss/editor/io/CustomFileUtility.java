/*
 * @(#)CustomFileUtility.java   
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

package org.interpss.editor.io;

import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.spring.PluginSpringFactory;

import com.interpss.common.util.IpssLogger;
import com.interpss.simu.SimuContext;

public class CustomFileUtility {
	public static boolean loadCustomFile(String filepath, String version, SimuContext simuCtx) {
		String ext = filepath.substring(filepath.lastIndexOf('.') + 1);
		IpssLogger.getLogger().info("Custom file path, ext, version : " + 
				filepath + ", " + ext + ", " + version);
		/*
		 * assumption adapter type is uniquely identified by file extension
		 */
		
		IpssFileAdapter adapter = PluginSpringFactory.getCustomFileAdapter(ext);
		
		if (adapter == null) {
			IpssLogger.getLogger().severe(
					"Custom Input File Adapter not found, file : " + filepath);
			return false;
		}

		try {
			adapter.setVersionSelected(version);
			if (ext.equals(IpssFileAdapter.BPA_DStab_LF_ext)) {
				// for BPA *.bpa_lf", DStab info assumed stored in *.bpa_dstab
				String filepathDstab = filepath.replace(IpssFileAdapter.BPA_DStab_LF_ext, 
						IpssFileAdapter.BPA_DStab_Dstab_ext);
				String[] fileAry = {filepath, filepathDstab};
				adapter.load(simuCtx, fileAry, false, null);
			}
			else 
				adapter.load(simuCtx, filepath, false, null);
		} catch (Exception e) {
			PluginSpringFactory.getEditorDialogUtil().showMsgDialog(
					"Custom Data File Loading Error", e.toString());
			IpssLogger.logErr(e);
			return false;
		}

		if (simuCtx != null && !simuCtx.checkData()) {
			boolean b = PluginSpringFactory.getEditorDialogUtil().showMsgDialogWithOptions(
					"Network Loadflow Data Error",
					"Please see the message list for details, Do you want to run the study case?");
			if (b)
				simuCtx.getAclfNet().setBypassDataCheck(true);
			return b;
		}
		return true;
	}
}
