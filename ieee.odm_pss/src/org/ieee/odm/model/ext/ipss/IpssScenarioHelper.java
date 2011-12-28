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

import java.util.List;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.AcscFaultAnalysisXmlType;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ActivePowerXmlType;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.BranchShiftFactorXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.FlowInterfaceRecXmlType;
import org.ieee.odm.schema.GenLossFactorXmlType;
import org.ieee.odm.schema.InterfaceShiftFactorXmlType;
import org.ieee.odm.schema.IpssAclfAlgorithmXmlType;
import org.ieee.odm.schema.IpssSimuAlgorithmXmlType;
import org.ieee.odm.schema.IpssStudyCaseXmlType;
import org.ieee.odm.schema.IpssStudyScenarioXmlType;
import org.ieee.odm.schema.LODFMonitorBranchXmlType;
import org.ieee.odm.schema.LineOutageDFactorXmlType;
import org.ieee.odm.schema.PTradingAnalysisXmlType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.ieee.odm.schema.SenAnalysisOutOptionXmlType;

public class IpssScenarioHelper {
	private IODMModelParser parser = null;
	
	public IpssScenarioHelper (IODMModelParser parser) {
		this.parser = parser;
	}

	public IpssScenarioHelper (IpssStudyScenarioXmlType sce) {
		// parser is a place holder for scenario object, therefore it does not matter its type
		this.parser = new AclfModelParser();
		this.parser.getStudyCase().setStudyScenario(parser.getFactory().createIpssStudyScenario(sce));
	}
	
	/*
	 *             Aclf Algorithm functions
	 *             ========================
	 */

	public IpssAclfAlgorithmXmlType createIpssAclfAlgorithm() {
		return this.parser.getFactory().createIpssAclfAlgorithmXmlType();
	}
	
	/*
	 *             Sen Analysis functions
	 *             ======================
	 */

	public List<DclfSenAnalysisXmlType> getSenAnalysisList() {
		if (getSimuAlgo().getSenAnalysis() == null) {
			ODMLogger.getLogger().severe("contact support@interpss.org");
		}
		return getSimuAlgo().getSenAnalysis();
	}
	
	public DclfSenAnalysisXmlType createSenCase() {
		DclfSenAnalysisXmlType dclfCase = parser.getFactory().createDclfSenAnalysisXmlType();
		getSenAnalysisList().add(dclfCase);
		return dclfCase;
	}

	public DclfBranchSensitivityXmlType createGSF(DclfSenAnalysisXmlType dclfCase) {
		DclfBranchSensitivityXmlType gsf = parser.getFactory().createDclfBranchSensitivityXmlType();
		dclfCase.getGenShiftFactor().add(gsf);
		//gsf.setInjectBus(parser.getFactory().createDclfSensitivityXmlTypeInjectBusList());
		//gsf.setWithdrawBus(parser.getFactory().createDclfSensitivityXmlTypeWithdrawBusList());
		return gsf;
	}

	public LineOutageDFactorXmlType createLODF(DclfSenAnalysisXmlType dclfCase) {
		LineOutageDFactorXmlType lodf = parser.getFactory().createLineOutageDFactorXmlType();
		dclfCase.getLineOutageDFactor().add(lodf);
		return lodf;
	}
	
	public SenAnalysisOutOptionXmlType createSenAnalysisOutConfig(DclfSenAnalysisXmlType dclfCase) {
		SenAnalysisOutOptionXmlType out = parser.getFactory().createSenAnalysisOutOptionXmlType();
		dclfCase.setOutOption(out);
		return out;
	}	

	public GenLossFactorXmlType createGenLossFactor(DclfSenAnalysisXmlType dclfCase) {
		GenLossFactorXmlType lf = parser.getFactory().createGenLossFactorXmlType();
		dclfCase.getGenLossFactors().add(lf);
		//lf.setInjectBusList(parser.getFactory().createDclfSensitivityXmlTypeInjectBusList());
		//lf.setWithdrawBusList(parser.getFactory().createDclfSensitivityXmlTypeWithdrawBusList());
		return lf;
	}
	
	public BranchRefXmlType creatBranchRef(List<BranchRefXmlType> braList) {
		BranchRefXmlType bra = parser.getFactory().createBranchRefXmlType();
		braList.add(bra);
		return bra;
	}
	
	public BranchRefXmlType createMonitorBranch(List<LODFMonitorBranchXmlType> braList) {
		LODFMonitorBranchXmlType bra = parser.getFactory().createLODFMonitorBranchXmlType();
		braList.add(bra);
		bra.setBranch(parser.getFactory().createBranchRefXmlType());
		return bra.getBranch();
	}

	public SenAnalysisBusXmlType createSenAnalysisBus(List<SenAnalysisBusXmlType> busList) {
		SenAnalysisBusXmlType bus = parser.getFactory().createSenAnalysisBusXmlType();
		busList.add(bus);
		return bus;
	}

	public BranchShiftFactorXmlType createBranchSFactor(List<BranchShiftFactorXmlType> braList) {
		BranchShiftFactorXmlType sf = parser.getFactory().createBranchShiftFactorXmlType();
		braList.add(sf);
		return sf;
	}
	
	public InterfaceShiftFactorXmlType createInterfaceSFactor(List<InterfaceShiftFactorXmlType> infList) {
		InterfaceShiftFactorXmlType sf = parser.getFactory().createInterfaceShiftFactorXmlType();
		infList.add(sf);
		return sf;
	}
	
	public FlowInterfaceRecXmlType createInterface() {
		FlowInterfaceRecXmlType sf = parser.getFactory().createFlowInterfaceRecXmlType();
		return sf;
	}

	public BranchRefXmlType createBranchRefXmlType() {
		return parser.getFactory().createBranchRefXmlType();
	}
	
	public ActivePowerXmlType createActivePower(double p, String unit) {
		ActivePowerXmlType pxml = parser.getFactory().createActivePowerXmlType();
		pxml.setValue(p);
		pxml.setUnit(ActivePowerUnitType.fromValue(unit));
		return pxml;
	}
/*
 *             PTrading functions
 *             ==================
 */
	
	/**
	 * 
	 * @return
	 */
	public PTradingAnalysisXmlType getPTradingAnalysis() {
		if (getSimuAlgo().getPTradingAnalysis() == null) {
			getSimuAlgo().setPTradingAnalysis(parser.getFactory().createPTradingAnalysisXmlType());
		}
		return getSimuAlgo().getPTradingAnalysis();
	}
	
	public AcscFaultAnalysisXmlType getAcscFaultAnalysis() {
		if (getSimuAlgo().getAcscAnalysis() == null) {
			getSimuAlgo().setAcscAnalysis(parser.getFactory().createAcscFaultAnalysisXmlType());
		}
		return getSimuAlgo().getAcscAnalysis();
	}

	public DStabSimulationXmlType getDStabSimulation() {
		if (getSimuAlgo().getDStabAnalysis() == null) {
			getSimuAlgo().setDStabAnalysis(parser.getFactory().createDStabSimulationXmlType());
		}
		return getSimuAlgo().getDStabAnalysis();
	}

	private IpssStudyScenarioXmlType getIpssScenario() {
		if (parser.getStudyScenario() == null) {
			IpssStudyScenarioXmlType studyScenario = parser.getFactory().createIpssStudyScenarioXmlType();
			studyScenario.setStudyCaseList(parser.getFactory().createIpssStudyScenarioXmlTypeStudyCaseList());
			parser.getStudyCase().setStudyScenario(parser.getFactory().createIpssStudyScenario(studyScenario));
			
			IpssStudyCaseXmlType scenario = parser.getFactory().createIpssStudyCaseXmlType();
			studyScenario.getStudyCaseList().getStudyCase().add(scenario);

			IpssSimuAlgorithmXmlType simuAlgo = parser.getFactory().createIpssSimuAlgorithmXmlType();
			scenario.setSimuAlgo(simuAlgo);
		}
		return (IpssStudyScenarioXmlType)parser.getStudyScenario();
	}

	private IpssSimuAlgorithmXmlType getSimuAlgo() {
		return this.getIpssScenario().getStudyCaseList().getStudyCase().get(0).getSimuAlgo();
	}
}
