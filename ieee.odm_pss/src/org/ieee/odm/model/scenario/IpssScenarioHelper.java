/*
 * @(#)IpssScenarioHelper.java   
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
 * @Date 09/01/2011
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.model.scenario;

import java.util.List;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.IpssScenarioXmlType;
import org.ieee.odm.schema.IpssSimuAlgorithmXmlType;
import org.ieee.odm.schema.IpssStudyScenarioXmlType;
import org.ieee.odm.schema.PTradingAnalysisXmlType;

public class IpssScenarioHelper {
	private IODMModelParser parser = null;
	
	public IpssScenarioHelper (IODMModelParser parser) {
		this.parser = parser;
	}

	public List<DclfSenAnalysisXmlType> getSenAnalysisList() {
		if (getSimuAlgo().getSenAnalysis() == null) {
			ODMLogger.getLogger().severe("contact support@interpss.org");
		}
		return getSimuAlgo().getSenAnalysis();
	}

	public PTradingAnalysisXmlType getPTradingAnalysis() {
		if (getSimuAlgo().getPTradingAnalysis() == null) {
			getSimuAlgo().setPTradingAnalysis(parser.getFactory().createPTradingAnalysisXmlType());
		}
		return getSimuAlgo().getPTradingAnalysis();
	}
	
	private IpssStudyScenarioXmlType getIpssScenario() {
		if (parser.getStudyScenario() == null) {
			IpssStudyScenarioXmlType studyScenario = parser.getFactory().createIpssStudyScenarioXmlType();
			studyScenario.setScenarioList(parser.getFactory().createIpssStudyScenarioXmlTypeScenarioList());
			parser.getStudyCase().setStudyScenario(parser.getFactory().createIpssStudyScenario(studyScenario));
			
			IpssScenarioXmlType scenario = parser.getFactory().createIpssScenarioXmlType();
			studyScenario.getScenarioList().getScenario().add(scenario);

			IpssSimuAlgorithmXmlType simuAlgo = parser.getFactory().createIpssSimuAlgorithmXmlType();
			scenario.setSimuAlgo(simuAlgo);
		}
		return (IpssStudyScenarioXmlType)parser.getStudyScenario();
	}

	private IpssSimuAlgorithmXmlType getSimuAlgo() {
		return this.getIpssScenario().getScenarioList().getScenario().get(0).getSimuAlgo();
	}
}
