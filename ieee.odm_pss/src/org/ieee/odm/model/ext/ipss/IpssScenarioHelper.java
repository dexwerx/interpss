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

import java.util.List;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.AclfAnalysisXmlType;
import org.ieee.odm.schema.AcscBranchFaultXmlType;
import org.ieee.odm.schema.AcscBusFaultXmlType;
import org.ieee.odm.schema.AcscFaultAnalysisXmlType;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ActivePowerXmlType;
import org.ieee.odm.schema.ApparentPowerXmlType;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.BranchShiftFactorXmlType;
import org.ieee.odm.schema.ContingencyAnalysisXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.FlowInterfaceRecXmlType;
import org.ieee.odm.schema.GenLossFactorXmlType;
import org.ieee.odm.schema.GridComputingXmlType;
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
		this.parser.getStudyCase().setStudyScenario(odmObjFactory.createIpssStudyScenario(sce));
	}
	
	/*
	 *             Grid functions
	 *             ==============
	 */

	public GridComputingXmlType getGridRunOption() {
		if (this.getIpssScenario().getGridRunOption() == null) {
			this.getIpssScenario().setGridRunOption(odmObjFactory.createGridComputingXmlType());
		}
		return this.getIpssScenario().getGridRunOption();
	}

	/*
	 *             Aclf functions
	 *             ==============
	 */

	public IpssAclfAlgorithmXmlType createIpssAclfAlgorithm() {
		return odmObjFactory.createIpssAclfAlgorithmXmlType();
	}
	
	public AclfAnalysisXmlType getAclfAnalysis() {
		if (getSimuAlgo().getAclfAnalysis() == null) {
			getSimuAlgo().setAclfAnalysis(odmObjFactory.createAclfAnalysisXmlType());
		}
		return getSimuAlgo().getAclfAnalysis();
	}

	public ApparentPowerXmlType createApparentPower(double kvaPU) {
		ApparentPowerXmlType p = odmObjFactory.createApparentPowerXmlType();
		p.setValue(kvaPU);
		return p;	
	}
	
 	/*
	 *             Contingency analysis functions
	 *             ==============================
	 */

	public ContingencyAnalysisXmlType getContingencyAnalysis() {
		if (getSimuAlgo().getContingencyAnalysis() == null) {
			getSimuAlgo().setContingencyAnalysis(odmObjFactory.createContingencyAnalysisXmlType());
		}
		return getSimuAlgo().getContingencyAnalysis();
	}	

	/*
	 *             Acsc functions
	 *             ==============
	 */

	public AcscFaultAnalysisXmlType getAcscFaultAnalysis() {
		if (getSimuAlgo().getAcscAnalysis() == null) {
			getSimuAlgo().setAcscAnalysis(odmObjFactory.createAcscFaultAnalysisXmlType());
		}
		return getSimuAlgo().getAcscAnalysis();
	}

	public AcscBusFaultXmlType createAcscBusFault() {
		AcscBusFaultXmlType busFault = odmObjFactory.createAcscBusFaultXmlType();
		busFault.setRefBus(odmObjFactory.createBusRefXmlType());
		busFault.setZLG(odmObjFactory.createZXmlType());
		busFault.setZLL(odmObjFactory.createZXmlType());
		return busFault;
	}
	
	public AcscBranchFaultXmlType createAcscBranchFault() {
		AcscBranchFaultXmlType braFault = odmObjFactory.createAcscBranchFaultXmlType();
		braFault.setRefBranch(odmObjFactory.createBranchRefXmlType());
		braFault.setZLG(odmObjFactory.createZXmlType());
		braFault.setZLL(odmObjFactory.createZXmlType());
		braFault.setReclosureTime(odmObjFactory.createTimePeriodXmlType());
		return braFault;
	}

	/*
	 *             DStab functions
	 *             ===============
	 */

	public DStabSimulationXmlType getDStabSimulation() {
		if (getSimuAlgo().getDStabAnalysis() == null) {
			getSimuAlgo().setDStabAnalysis(odmObjFactory.createDStabSimulationXmlType());
		}
		return getSimuAlgo().getDStabAnalysis();
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
		DclfSenAnalysisXmlType dclfCase = odmObjFactory.createDclfSenAnalysisXmlType();
		getSenAnalysisList().add(dclfCase);
		return dclfCase;
	}

	public DclfBranchSensitivityXmlType createGSF(DclfSenAnalysisXmlType dclfCase) {
		DclfBranchSensitivityXmlType gsf = odmObjFactory.createDclfBranchSensitivityXmlType();
		dclfCase.getGenShiftFactor().add(gsf);
		//gsf.setInjectBus(odmObjFactory.createDclfSensitivityXmlTypeInjectBusList());
		//gsf.setWithdrawBus(odmObjFactory.createDclfSensitivityXmlTypeWithdrawBusList());
		return gsf;
	}

	public LineOutageDFactorXmlType createLODF(DclfSenAnalysisXmlType dclfCase) {
		LineOutageDFactorXmlType lodf = odmObjFactory.createLineOutageDFactorXmlType();
		dclfCase.getLineOutageDFactor().add(lodf);
		return lodf;
	}
	
	public SenAnalysisOutOptionXmlType createSenAnalysisOutConfig(DclfSenAnalysisXmlType dclfCase) {
		SenAnalysisOutOptionXmlType out = odmObjFactory.createSenAnalysisOutOptionXmlType();
		dclfCase.setOutOption(out);
		return out;
	}	

	public GenLossFactorXmlType createGenLossFactor(DclfSenAnalysisXmlType dclfCase) {
		GenLossFactorXmlType lf = odmObjFactory.createGenLossFactorXmlType();
		dclfCase.getGenLossFactors().add(lf);
		//lf.setInjectBusList(odmObjFactory.createDclfSensitivityXmlTypeInjectBusList());
		//lf.setWithdrawBusList(odmObjFactory.createDclfSensitivityXmlTypeWithdrawBusList());
		return lf;
	}
	
	public BranchRefXmlType creatBranchRef(List<BranchRefXmlType> braList) {
		BranchRefXmlType bra = odmObjFactory.createBranchRefXmlType();
		braList.add(bra);
		return bra;
	}
	
	public BranchRefXmlType createMonitorBranch(List<LODFMonitorBranchXmlType> braList) {
		LODFMonitorBranchXmlType bra = odmObjFactory.createLODFMonitorBranchXmlType();
		braList.add(bra);
		bra.setBranch(odmObjFactory.createBranchRefXmlType());
		return bra.getBranch();
	}

	public SenAnalysisBusXmlType createSenAnalysisBus(List<SenAnalysisBusXmlType> busList) {
		SenAnalysisBusXmlType bus = odmObjFactory.createSenAnalysisBusXmlType();
		busList.add(bus);
		return bus;
	}

	public BranchShiftFactorXmlType createBranchSFactor(List<BranchShiftFactorXmlType> braList) {
		BranchShiftFactorXmlType sf = odmObjFactory.createBranchShiftFactorXmlType();
		braList.add(sf);
		return sf;
	}
	
	public InterfaceShiftFactorXmlType createInterfaceSFactor(List<InterfaceShiftFactorXmlType> infList) {
		InterfaceShiftFactorXmlType sf = odmObjFactory.createInterfaceShiftFactorXmlType();
		infList.add(sf);
		return sf;
	}
	
	public FlowInterfaceRecXmlType createInterface() {
		FlowInterfaceRecXmlType sf = odmObjFactory.createFlowInterfaceRecXmlType();
		return sf;
	}

	public BranchRefXmlType createBranchRefXmlType() {
		return odmObjFactory.createBranchRefXmlType();
	}
	
	public ActivePowerXmlType createActivePower(double p, String unit) {
		ActivePowerXmlType pxml = odmObjFactory.createActivePowerXmlType();
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
			getSimuAlgo().setPTradingAnalysis(odmObjFactory.createPTradingAnalysisXmlType());
		}
		return getSimuAlgo().getPTradingAnalysis();
	}

	private IpssStudyScenarioXmlType getIpssScenario() {
		if (parser.getStudyScenario() == null) {
			IpssStudyScenarioXmlType studyScenario = odmObjFactory.createIpssStudyScenarioXmlType();
			studyScenario.setStudyCaseList(odmObjFactory.createIpssStudyScenarioXmlTypeStudyCaseList());
			parser.getStudyCase().setStudyScenario(odmObjFactory.createIpssStudyScenario(studyScenario));
			
			IpssStudyCaseXmlType scenario = odmObjFactory.createIpssStudyCaseXmlType();
			studyScenario.getStudyCaseList().getStudyCase().add(scenario);

			IpssSimuAlgorithmXmlType simuAlgo = odmObjFactory.createIpssSimuAlgorithmXmlType();
			scenario.setSimuAlgo(simuAlgo);
		}
		return (IpssStudyScenarioXmlType)parser.getStudyScenario();
	}

	private IpssStudyCaseXmlType getStudyCase() {
		return this.getIpssScenario().getStudyCaseList().getStudyCase().get(0);
	}

	private IpssSimuAlgorithmXmlType getSimuAlgo() {
		return getStudyCase().getSimuAlgo();
	}
}
