/*
 * @(#)XmlScriptRunWorker.java   
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
 * @Date 01/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.editor.runAct;

import javax.xml.bind.JAXBException;

import org.interpss.editor.runAct.xml.XmlScriptAclfRun;
import org.interpss.editor.runAct.xml.XmlScriptAcscRun;
import org.interpss.editor.runAct.xml.XmlScriptContingency;
import org.interpss.editor.runAct.xml.XmlScriptDStabRun;
import org.interpss.editor.runAct.xml.XmlScriptDclfRun;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.RunStudyCaseXmlType;

import com.interpss.common.util.IpssLogger;
import com.interpss.simu.SimuContext;

public class XmlScriptRunWorker {
	/**
	 * Run the XMl scripts
	 * 
	 * @param scripts
	 *            run scripts, a string representing an XML document
	 * @param simuCtx
	 *            the SimuContext object
	 * @return
	 */
	public static boolean runCase(String scripts, SimuContext simuCtx) {
		// create the XML parser and parse the run scripts
		IpssXmlParser parser;
		try {
			parser = new IpssXmlParser(scripts);
		} catch (JAXBException e) {
			IpssLogger.logErr(e);
			EditorPluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
					"Invalid Xml", e.toString());
			return false;
		}

		// Apply the modification to the base Network object
		if (parser.getModification() != null) {
			EditorPluginSpringFactory.getModXml2NetMapper()
					.map2Model(parser.getModification(), simuCtx.getNetwork());
		}
		
		RunStudyCaseXmlType xmlStudyCase = parser.getRunStudyCase();
		GridRunner.RemoteNodeDebug = xmlStudyCase.getGridRunOption() != null
				&& xmlStudyCase.getGridRunOption().isRemoteNodeDebug();
		if (xmlStudyCase.getAnalysisRunType() == AnalysisRunDataType.RUN_ACLF) {
			return XmlScriptAclfRun.runAclf(parser.getRootDoc(), simuCtx.getAclfNet());
		} else if (xmlStudyCase.getAnalysisRunType() == AnalysisRunDataType.RUN_DCLF) {
			return XmlScriptDclfRun.runDclf(parser.getRootDoc(), simuCtx.getAclfNet());
		} else if (xmlStudyCase.getAnalysisRunType() == AnalysisRunDataType.RUN_ACSC) {
			return XmlScriptAcscRun.runAcsc(parser.getRootDoc(), simuCtx.getAcscNet());
		} else if (xmlStudyCase.getAnalysisRunType() == AnalysisRunDataType.RUN_D_STAB) {
			return XmlScriptDStabRun.runDStab(parser.getRootDoc(), simuCtx);
		}
		else if (xmlStudyCase.getAnalysisRunType() == AnalysisRunDataType.CONTINGENCY_ANALYSIS) {
			return XmlScriptContingency.runContingencyAnalysis(parser.getRootDoc(), simuCtx.getAclfNet());
		}
		IpssLogger.getLogger().severe("Error: wrong analysus type, " + xmlStudyCase.getAnalysisRunType());
		return false;
	}
}
