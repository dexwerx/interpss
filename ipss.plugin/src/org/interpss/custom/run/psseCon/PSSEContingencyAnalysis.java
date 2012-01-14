/*
 * @(#)PSSEContingencyAnalysis.java   
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

package org.interpss.custom.run.psseCon;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import org.interpss.custom.run.CustomRunScriptPluginBase;
import org.interpss.xml.IpssXmlDataSetter;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.interpss.xml.schema.AclfMethodDataType;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.ContingencyAnalysisOptionXmlType;
import org.interpss.xml.schema.GridAclfOptionXmlType;
import org.interpss.xml.schema.GridComputingXmlType;
import org.interpss.xml.schema.InterPSSXmlType;
import org.interpss.xml.schema.ReturnStudyCaseDataType;

import com.interpss.common.util.StringUtil;


public class PSSEContingencyAnalysis extends CustomRunScriptPluginBase {
	@Override
	public InterPSSXmlType createIpssXmlDocument(AnalysisRunDataType type, String scripts) {
		ipssLogger.info("Run custom scripts with plugin: PSSEContingencyAnalysis");
		try {
			String[] strAry = StringUtil.strToken2Array(scripts, System.getProperty("line.separator"));
			//for(String s : strAry) System.out.println(s);
			// parse the contingency control for modification to the network 
			InterPSSXmlType ipssXmlDoc = ContingencyFileParser.parseControlScripts(type, strAry);
			
			// add script run control scripts to the xml document 
			createRunXmlScripts(type, ipssXmlDoc);
			return ipssXmlDoc;
		} catch (Exception e) {
			ipssLogger.severe(e.toString());
			return null;
		}
	}

	private boolean createRunXmlScripts(AnalysisRunDataType type, InterPSSXmlType ipssXmlDoc) {
		if (type == AnalysisRunDataType.RUN_ACLF) {
			// grid computing settings
			GridComputingXmlType gridRun = IpssXmlParser.getFactory().createGridComputingXmlType();
			ipssXmlDoc.getRunStudyCase().setGridRunOption(gridRun);
			gridRun.setEnableGridRun(true);
			gridRun.setRemoteJobCreation(true);
			GridAclfOptionXmlType opt = IpssXmlParser.getFactory().createGridAclfOptionXmlType();
			gridRun.setAclfOption(opt);
			opt.setReturnStudyCase(ReturnStudyCaseDataType.DIVERGED_CASE);
		}
		else if (type == AnalysisRunDataType.CONTINGENCY_ANALYSIS) {
			ContingencyAnalysisOptionXmlType opt = IpssXmlParser.getFactory().createContingencyAnalysisOptionXmlType();
			ipssXmlDoc.getRunStudyCase().getContingencyAnalysis().setOption(opt);
			opt.setBusVLimitPU(IpssXmlDataSetter.createLimitXmlType(1.1, 0.9));
		}
		
		// default Aclf Algorithm settings
		AclfAlgorithmXmlType algo = IpssXmlParser.getFactory().createAclfAlgorithmXmlType();
		if (type == AnalysisRunDataType.RUN_ACLF) {
			ipssXmlDoc.getRunStudyCase().getStandardRun().getRunAclfStudyCase().setDefaultAclfAlgorithm(algo);
		}
		else {
			ipssXmlDoc.getRunStudyCase().getContingencyAnalysis().setDefaultAclfAlgorithm(algo);
		}
		algo.setLfMethod(AclfMethodDataType.NR);
		algo.setMaxIterations(20);
		algo.setTolerance(0.0001);
		algo.setNonDivergent(true);
		return true;
	}
}
