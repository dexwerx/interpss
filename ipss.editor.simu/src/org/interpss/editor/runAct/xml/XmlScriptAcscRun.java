/*
 * @(#)XmlScriptAcscRun.java   
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

package org.interpss.editor.runAct.xml;

import org.interpss.editor.runAct.RunActUtilFunc;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.InterPSSXmlType;
import org.interpss.xml.schema.RunAcscStudyCaseXmlType;

import com.interpss.CoreObjectFactory;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBranchFault;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.algo.SimpleFaultAlgorithm;

public class XmlScriptAcscRun {
	/**
	 * Run Acsc run or run(s) defined in the Xml scripts
	 * 
	 * @param parser The InterPSS xml parser object
	 * @param aclfNet
	 * @param msg
	 * @return
	 */
	public static boolean runAcsc(InterPSSXmlType ipssXmlDoc, AcscNetwork faultNet) {
		//IpssMapper mapper = PluginSpringCtx.getIpssXmlMapper();
		if (ipssXmlDoc.getRunStudyCase().getStandardRun().getRunAcscStudyCase() != null) {
			RunAcscStudyCaseXmlType xmlRunCase = ipssXmlDoc.getRunStudyCase().getStandardRun()
					.getRunAcscStudyCase();
			SimpleFaultAlgorithm algo = CoreObjectFactory
					.createSimpleFaultAlgorithm(faultNet);
			
			AcscStudyCaseXmlType xmlDefaultCase = xmlRunCase.getDefaultAcscStudyCase(); 
			
			for ( AcscStudyCaseXmlType xmlCase : xmlRunCase.getAcscStudyCaseList().getAcscStudyCase()) {
				if (xmlCase.getModification() != null) {
					// TODO: apply the modification
				}

				if (xmlCase == null) {
					if (xmlDefaultCase == null) {
						IpssLogger.getLogger().severe("No Acsc study case defined");
						return false;
					}
					xmlCase = xmlDefaultCase;
				} 
				if (xmlCase.getModification() != null)
					PluginSpringCtx.getModXml2NetMapper()
						.map2Model(xmlCase.getModification(), faultNet);

				PluginSpringCtx.getXml2ScAlgorithmMapper()
						.map2Model(xmlCase, algo);
				
				Object fault = algo.getFaultList().get(0);
				if (fault instanceof AcscBusFault)
					algo.calculateBusFault((AcscBusFault) fault);
				else
					algo.calculateBranchFault((AcscBranchFault) fault);
				RunActUtilFunc.displayAcscSummaryResult(faultNet, algo);
			}
		}
		return true;
	}
}
