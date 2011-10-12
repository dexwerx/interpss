/*
 * @(#)CustomScriptRunWorker.java   
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
 * @Date 04/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.editor.runAct;

import org.interpss.custom.run.ICustomRunScriptPlugin;
import org.interpss.editor.runAct.xml.XmlScriptContingency;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.InterPSSXmlType;

import com.interpss.simu.SimuContext;

public class CustomScriptRunWorker {
	/**
	 * Run the scripts using custom plugin
	 * 
	 * @param scripts run scripts, a string representing an XML document
	 * @param pluginName custom script run plugin name
	 * @param simuCtx the SimuContext object
	 * @return
	 */
	public static boolean runCase(String scripts, String pluginName, SimuContext simuCtx) {
		// create a custom script run adapter based on the plugin name
		ICustomRunScriptPlugin adapter = PluginSpringCtx.getCustomScriptRunPlugin(pluginName);
		
		// run Aclf using the xml document
		//InterPSSXmlType ipssXmlDoc = adapter.createIpssXmlDocument(AnalysisRunType.RUN_ACLF, scripts, simuCtx.getMsgHub());
		//return XmlScriptAclfRun.runAclf(ipssXmlDoc, simuCtx.getAclfAdjNet(), simuCtx.getMsgHub());		

		InterPSSXmlType ipssXmlDoc = adapter.createIpssXmlDocument(AnalysisRunDataType.CONTINGENCY_ANALYSIS, scripts);
		return XmlScriptContingency.runContingencyAnalysis(ipssXmlDoc, simuCtx.getAclfNet());		
	}
}
