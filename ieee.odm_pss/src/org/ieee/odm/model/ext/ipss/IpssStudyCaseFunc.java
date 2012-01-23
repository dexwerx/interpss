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

package org.ieee.odm.model.ext.ipss;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.schema.PTradingEDHourlyAnalysisXmlType;
import org.ieee.odm.schema.PtAclfAnalysisXmlType;
import org.ieee.odm.schema.PtAclfOutputXmlType;
import org.ieee.odm.schema.PtCaseDataXmlType;
import org.ieee.odm.schema.PtEDispatchFileXmlType;

/**
 * InterPSS extension study case help funcions
 * 
 * @author mzhou
 *
 */
public class IpssStudyCaseFunc {
	/**
	 * initialize the PTradingEDHourlyAnalysis object
	 * 
	 * @param pt
	 */
	public static void initPTradingEDHourlyAnalysis(PTradingEDHourlyAnalysisXmlType pt) {
		
		PtCaseDataXmlType casedata = odmObjFactory.createPtCaseDataXmlType();
		pt.setCaseData(casedata);
		
		PtEDispatchFileXmlType edfile = odmObjFactory.createPtEDispatchFileXmlType();
		casedata.setEdFile(edfile);
		edfile.setFilename("ED file ...");
		edfile.setGenPFactor(0.9);
		edfile.setLossPercent(2.5);
		edfile.setLoadPFactor(0.98);

		casedata.setInterfaceLimitFilename("Interface limit file ...");
		
		PtAclfAnalysisXmlType aclf = odmObjFactory.createPtAclfAnalysisXmlType();
		pt.setAclfAnalysis(aclf);
		
		aclf.setHour("12:00");
		
		aclf.setGenQAdjustment(false);
		aclf.setSwingBusPQAlloc(false);
		
		PtAclfOutputXmlType outOpt = odmObjFactory.createPtAclfOutputXmlType();
		aclf.setOutputOption(outOpt);
		outOpt.setBusVoltageUpperLimitPU(1.15);
		outOpt.setBusVoltageLowerLimitPU(0.80);
		outOpt.setLargeGSFPoints(5);
		outOpt.setBusVoltageViolation(true);
		outOpt.setBranchLimitViolation(false);
		outOpt.setInterfaceLimitViolation(false);
		outOpt.setZoneSummary(true);
		outOpt.setAreaSummary(true);
	}
}
