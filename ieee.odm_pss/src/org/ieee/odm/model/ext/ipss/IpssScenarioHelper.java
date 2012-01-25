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
import static org.ieee.odm.model.ext.ipss.IpssStudyCaseFunc.initPTradingEDHourlyAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.AclfAnalysisXmlType;
import org.ieee.odm.schema.AcscBranchFaultXmlType;
import org.ieee.odm.schema.AcscBusFaultXmlType;
import org.ieee.odm.schema.AcscFaultAnalysisXmlType;
import org.ieee.odm.schema.AggregatePricingXmlType;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.BranchShiftFactorXmlType;
import org.ieee.odm.schema.ContingencyAnalysisXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.GenLossFactorXmlType;
import org.ieee.odm.schema.GridComputingXmlType;
import org.ieee.odm.schema.InterfaceShiftFactorXmlType;
import org.ieee.odm.schema.IpssStudyCaseXmlType;
import org.ieee.odm.schema.IpssStudyScenarioXmlType;
import org.ieee.odm.schema.LODFMonitorBranchXmlType;
import org.ieee.odm.schema.LODFOutageEnumType;
import org.ieee.odm.schema.LineOutageDFactorXmlType;
import org.ieee.odm.schema.PTradingEDHourlyAnalysisXmlType;
import org.ieee.odm.schema.PowerTradingInfoXmlType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.ieee.odm.schema.SenAnalysisOutOptionXmlType;

/**
 * InterPSS extension study scenario helper class
 * 
 * @author mzhou
 *
 */
public class IpssScenarioHelper {
	private IODMModelParser parser = null;
	
	/**
	 * constructor
	 * 
	 * @param parser
	 */
	public IpssScenarioHelper () {
		this.parser = new AclfModelParser();
	}

	/**
	 * constructor
	 * 
	 * @param parser
	 */
	public IpssScenarioHelper (IODMModelParser parser) {
		this.parser = parser;
	}

	/**
	 * constructor
	 * 
	 * @param sce an existing study scenario xml doc
	 */
	public IpssScenarioHelper (IpssStudyScenarioXmlType sce) {
		// parser is a place holder for scenario object, therefore it does not matter its type
		this.parser = new AclfModelParser();
		this.parser.getStudyCase().setStudyScenario(odmObjFactory.createIpssStudyScenario(sce));
	}
	
	/*
	 *             Study case level functions
	 *             ==========================
	 */
	
	/**
	 * get the current study case id
	 * 
	 * @return
	 */
	public String getCurStudyCaseId() {
		if (this.getIpssScenario().getStudyCaseList().getCurStudyCaseId() != null)
			return this.getIpssScenario().getStudyCaseList().getCurStudyCaseId();
		else
			return null;
	}
	
	/**
	 * set the current study case id
	 * 
	 * @param id
	 */
	public void setCurStudyCaseId(String id) {
		this.getIpssScenario().getStudyCaseList().setCurStudyCaseId(id);
	}

	/**
	 * get the current study case. If the current study case id is not
	 * defined, return the first study case
	 * 
	 * @return
	 */
	public IpssStudyCaseXmlType getCurrentStudyCase() {
		return this.getStudyCase(getCurStudyCaseId());
	}

	/**
	 * get study case by study case id
	 * 
	 * @param id
	 * @return
	 */
	private IpssStudyCaseXmlType getStudyCase(String id) {
		if (id != null) {
			for ( IpssStudyCaseXmlType scase : this.getIpssScenario().getStudyCaseList().getStudyCase()) {
				if (scase.getId().equals(id))
					return scase;
			}
			ODMLogger.getLogger().info("Study case not found, id: " + id);
		}
		return this.getIpssScenario().getStudyCaseList().getStudyCase().get(0);
	}	
	
	/**
	 * add a new study case
	 * 
	 * @return
	 */
	public IpssStudyCaseXmlType addNewStudyCase() {
		IpssStudyCaseXmlType scase = odmObjFactory.createIpssStudyCaseXmlType();
		List<IpssStudyCaseXmlType> list = this.getIpssScenario().getStudyCaseList().getStudyCase();
		list.add(scase);
		scase.setId("StudyCaseId-" + list.size());
		return scase;
	}
	
	/**
	 * delete the study case identified by the studyCase id.
	 * 
	 *   - If the scenario only contains one study case, it cannot be deleted
	 *   - The method returns the next Study case id. If not next, it returns the first
	 * 
	 * @param studyCaseId
	 * @return
	 */
	public String deleteStudyCase(String studyCaseId) {
		List<IpssStudyCaseXmlType> list = this.getIpssScenario().getStudyCaseList().getStudyCase();
		// if there is only one study case, do nothing
		if (list.size() == 1) {
			ODMLogger.getLogger().info("number of case = 1, cannot delete the case");
			return studyCaseId;
		}
		
		int cnt = 0;
		for ( IpssStudyCaseXmlType scase : list) {
			if (scase.getId().equals(studyCaseId)) {
				//list.remove(cnt);
				break;
			}
			else
				cnt++;
		}
		
		list.remove(cnt);
		if (cnt >= list.size())
			cnt = 0;
		String id = list.get(cnt).getId();
		ODMLogger.getLogger().info("Delete study case id: " + studyCaseId + " returning next  study case id: " + id);
		return id;
	}
	
	/**
	 * get study case id array
	 * 
	 * @return
	 */
	public String[] getStudyCaseIdAry() {
		String[] sAry = new String[this.getIpssScenario().getStudyCaseList().getStudyCase().size()];
		int cnt = 0;
		for ( IpssStudyCaseXmlType scase : this.getIpssScenario().getStudyCaseList().getStudyCase()) {
			sAry[cnt++] = scase.getId();
		}
		return sAry;
	}

	/*
	 *             Grid functions
	 *             ==============
	 */

	/**
	 * get the grid run option
	 */
	public GridComputingXmlType getGridRunOption() {
		if (this.getIpssScenario().getGridRunOption() == null) {
			this.getIpssScenario().setGridRunOption(odmObjFactory.createGridComputingXmlType());
		}
		return this.getIpssScenario().getGridRunOption();
	}

	/*
	 *             PowerTradingInfo functions
	 *             ==========================
	 */

	/**
	 * get the power trading into object
	 */
	public PowerTradingInfoXmlType getPowerTradingInfo() {
		if (this.getIpssScenario().getPowerTradingInfo() == null) {
			this.getIpssScenario().setPowerTradingInfo(
					IpssStudyCaseFunc.createDefaultPtInfo());
		}
		return this.getIpssScenario().getPowerTradingInfo();
	}

	/**
	 * get the AggregatePricing object
	 * 
	 * @return
	 */
	public AggregatePricingXmlType getAggregatePricing() {
		if (this.getPowerTradingInfo().getLoadDist().getAggregatePricing() == null) {
			this.getPowerTradingInfo().getLoadDist().setAggregatePricing(odmObjFactory.createAggregatePricingXmlType());
		}
		return this.getPowerTradingInfo().getLoadDist().getAggregatePricing();
	}
	
	/*
	 *             Aclf functions
	 *             ==============
	 */
	
	/**
	 * get AclfAnalysis of the current study case 
	 */
	public AclfAnalysisXmlType getAclfAnalysis() {
		return getAclfAnalysis(null);
	}

	/**
	 * get AclfAnalysis of the study case of StudyCaseId 
	 */
	public AclfAnalysisXmlType getAclfAnalysis(String studyCaseId) {
		IpssStudyCaseXmlType scase = getStudyCase(studyCaseId); 
		if (scase.getAclfAnalysis() == null) {
			scase.setAclfAnalysis(odmObjFactory.createAclfAnalysisXmlType());
		}
		return scase.getAclfAnalysis();
	}

 	/*
	 *             Contingency analysis functions
	 *             ==============================
	 */
	
	/**
	 * get ContingencyAnalysis of the current study case 
	 */
	public ContingencyAnalysisXmlType getContingencyAnalysis() {
		return getContingencyAnalysis(null);
	}

	/**
	 * get ContingencyAnalysis of the study case of studyCaseId 
	 */
	public ContingencyAnalysisXmlType getContingencyAnalysis(String studyCaseId) {
		IpssStudyCaseXmlType scase = getStudyCase(studyCaseId); 
		if (scase.getContingencyAnalysis() == null) {
			scase.setContingencyAnalysis(odmObjFactory.createContingencyAnalysisXmlType());
		}
		return scase.getContingencyAnalysis();
	}	

	/*
	 *             Acsc functions
	 *             ==============
	 */

	/**
	 * get AcscFaultAnalysis of the current study case 
	 */
	public AcscFaultAnalysisXmlType getAcscFaultAnalysis() {
		return getAcscFaultAnalysis(null);
	}
	
	/**
	 * get AcscFaultAnalysis of the study case of studyCaseId 
	 */
	public AcscFaultAnalysisXmlType getAcscFaultAnalysis(String studyCaseId) {
		IpssStudyCaseXmlType scase = getStudyCase(studyCaseId); 
		if (scase.getAcscAnalysis() == null) {
			scase.setAcscAnalysis(odmObjFactory.createAcscFaultAnalysisXmlType());
		}
		return scase.getAcscAnalysis();
	}

	/**
	 * create a AcscBusFaultXmlType object
	 * 
	 * @return
	 */
	public AcscBusFaultXmlType createAcscBusFault() {
		AcscBusFaultXmlType busFault = odmObjFactory.createAcscBusFaultXmlType();
		busFault.setRefBus(odmObjFactory.createBusRefXmlType());
		busFault.setZLG(odmObjFactory.createZXmlType());
		busFault.setZLL(odmObjFactory.createZXmlType());
		return busFault;
	}
	
	/**
	 * create a AcscBranchFaultXmlType object
	 * 
	 * @return
	 */
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

	/**
	 * get DStabSimulation of the current study case 
	 */
	public DStabSimulationXmlType getDStabSimulation() {
		return getDStabSimulation(null);
	}
	
	/**
	 * get DStabSimulation of the study case of studyCaseId 
	 */
	public DStabSimulationXmlType getDStabSimulation(String studyCaseId) {
		IpssStudyCaseXmlType scase = getStudyCase(studyCaseId); 
		if (scase.getDStabAnalysis() == null) {
			scase.setDStabAnalysis(odmObjFactory.createDStabSimulationXmlType());
		}
		return scase.getDStabAnalysis();
	}
	
	/*
	 *             Sen Analysis functions
	 *             ======================
	 */
	
	/**
	 * get the SenAnalysisList of the current study case
	 * 
	 */
	public List<DclfSenAnalysisXmlType> getSenAnalysisList() {
		return getSenAnalysisList(null);
	}

	/**
	 * get the SenAnalysisList of the study case of studyCaseId
	 * 
	 */
	public List<DclfSenAnalysisXmlType> getSenAnalysisList(String studyCaseId) {
		IpssStudyCaseXmlType scase = getStudyCase(studyCaseId); 
		if (scase.getSenAnalysis() == null) {
			ODMLogger.getLogger().severe("contact support@interpss.org");
		}
		return scase.getSenAnalysis();
	}
	
	/**
	 * create a SenAnalysis object for the current study case
	 * 
	 * @return
	 */
	public DclfSenAnalysisXmlType createSenAnalysis() {
		return createSenAnalysis(null);
	}
	
	/**
	 * create a SenAnalysis object for the study case of studyCaseId
	 * 
	 * @return
	 */
	public DclfSenAnalysisXmlType createSenAnalysis(String studyCaseId) {
		DclfSenAnalysisXmlType dclfCase = odmObjFactory.createDclfSenAnalysisXmlType();
		getSenAnalysisList(studyCaseId).add(dclfCase);
		return dclfCase;
	}

	/**
	 * create a GSF object and add to the dclfCase
	 * 
	 * @param dclfCase
	 * @return
	 */
	public DclfBranchSensitivityXmlType createGSF(DclfSenAnalysisXmlType dclfCase) {
		DclfBranchSensitivityXmlType gsf = odmObjFactory.createDclfBranchSensitivityXmlType();
		dclfCase.getGenShiftFactor().add(gsf);
		return gsf;
	}

	/**
	 * create a LODF object and add to the dclfCase
	 * 
	 * @param dclfCase
	 * @return
	 */
	public LineOutageDFactorXmlType createLODF(DclfSenAnalysisXmlType dclfCase) {
		LineOutageDFactorXmlType lodf = odmObjFactory.createLineOutageDFactorXmlType();
		lodf.setOutageType(LODFOutageEnumType.SINGLE_BRANCH);
		dclfCase.getLineOutageDFactor().add(lodf);
		return lodf;
	}
	
	/**
	 * create SenAnalysisOutOption object and add to the dclfCase
	 * 
	 * @param dclfCase
	 * @return
	 */
	public SenAnalysisOutOptionXmlType createSenAnalysisOutConfig(DclfSenAnalysisXmlType dclfCase) {
		SenAnalysisOutOptionXmlType out = odmObjFactory.createSenAnalysisOutOptionXmlType();
		dclfCase.setOutOption(out);
		return out;
	}	

	/**
	 * create GenLossFactor object and add to the dclfCase
	 * 
	 * @param dclfCase
	 * @return
	 */
	public GenLossFactorXmlType createGenLossFactor(DclfSenAnalysisXmlType dclfCase) {
		GenLossFactorXmlType lf = odmObjFactory.createGenLossFactorXmlType();
		dclfCase.getGenLossFactors().add(lf);
		return lf;
	}
	
	/**
	 * create BranchRef object and add to the branch list
	 * 
	 * @param braList
	 * @return
	 */
	public BranchRefXmlType creatBranchRef(List<BranchRefXmlType> braList) {
		BranchRefXmlType bra = odmObjFactory.createBranchRefXmlType();
		braList.add(bra);
		return bra;
	}
	
	/**
	 * create MonitorBranch object and add to the branch list
	 * 
	 * @param braList
	 * @return
	 */	
	public BranchRefXmlType createMonitorBranch(List<LODFMonitorBranchXmlType> braList) {
		LODFMonitorBranchXmlType bra = odmObjFactory.createLODFMonitorBranchXmlType();
		braList.add(bra);
		bra.setBranch(odmObjFactory.createBranchRefXmlType());
		return bra.getBranch();
	}

	/**
	 * create SenAnalysisBus object and add to the bus list
	 * 
	 * @param busList
	 * @return
	 */
	public SenAnalysisBusXmlType createSenAnalysisBus(List<SenAnalysisBusXmlType> busList) {
		SenAnalysisBusXmlType bus = odmObjFactory.createSenAnalysisBusXmlType();
		busList.add(bus);
		return bus;
	}

	/**
	 * create BranchSFactor object and add to the branch list
	 * 
	 * @param braList
	 * @return
	 */	
	public BranchShiftFactorXmlType createBranchSFactor(List<BranchShiftFactorXmlType> braList) {
		BranchShiftFactorXmlType sf = odmObjFactory.createBranchShiftFactorXmlType();
		braList.add(sf);
		return sf;
	}
	
	/**
	 * create InterfaceShiftFactor object and add to the interface list
	 * 
	 * @param braList
	 * @return
	 */	
	public InterfaceShiftFactorXmlType createInterfaceSFactor(List<InterfaceShiftFactorXmlType> infList) {
		InterfaceShiftFactorXmlType sf = odmObjFactory.createInterfaceShiftFactorXmlType();
		infList.add(sf);
		return sf;
	}
	
/*
 *             PTradingAnalysis functions
 *             ==========================
 */
	
	/**
	 * return the default PTradingEDHourlyAnalysis of the current study case
	 * 
	 */
	public PTradingEDHourlyAnalysisXmlType getPtEDHourlyAnalysis() {
		return getPtEDHourlyAnalysis(null);
	}
	
	/**
	 * return PTradingEDHourlyAnalysis of the study case of studyCaseId
	 *  
	 * @return
	 */
	public PTradingEDHourlyAnalysisXmlType getPtEDHourlyAnalysis(String studyCaseId) {
		IpssStudyCaseXmlType scase = getStudyCase(studyCaseId); 
		if (scase.getPtAnalysis() == null) {
			PTradingEDHourlyAnalysisXmlType pt = odmObjFactory.createPTradingEDHourlyAnalysisXmlType();
			scase.setPtAnalysis(odmObjFactory.createPtAnalysis(pt));
		}
		return (PTradingEDHourlyAnalysisXmlType)scase.getPtAnalysis().getValue();
	}

	/**
	 * get PTrading case name array
	 * 
	 * @return
	 */
	public List<String> getPTradingCaseNameAry() {
		List<String> list = new ArrayList<String>();
		for ( IpssStudyCaseXmlType scase : this.getIpssScenario().getStudyCaseList().getStudyCase()) {
			if (scase.getPtAnalysis() != null) {
				list.add(scase.getPtAnalysis().getValue().getName());
			}
		}
		return list;
	}

	/**
	 * get study case id by PTrading case name
	 * 
	 * @return
	 */
	public String getStudyCaseIdByPtCaseName(String ptCaseName) {
		for ( IpssStudyCaseXmlType scase : this.getIpssScenario().getStudyCaseList().getStudyCase()) {
			if (scase.getPtAnalysis() != null) {
				if (ptCaseName.equals(scase.getPtAnalysis().getValue().getName()))
					return scase.getId();
			}
		}
		return null;
	}

	/**
	 * create a new study case and init PTradingAnalysis 
	 * 
	 * @param ptName
	 * @return
	 */
	public IpssStudyCaseXmlType addNewPTradingStudyCase(String ptName) {
		IpssStudyCaseXmlType scase = this.addNewStudyCase();
		PTradingEDHourlyAnalysisXmlType ptCase = odmObjFactory.createPTradingEDHourlyAnalysisXmlType();
		scase.setPtAnalysis(odmObjFactory.createPtAnalysis(ptCase));
		ptCase.setName(ptName);
		ptCase.setDesc("Study Case description");
		initPTradingEDHourlyAnalysis(ptCase);
		return scase;
	}
	
	private IpssStudyScenarioXmlType getIpssScenario() {
		if (parser.getStudyScenario() == null) {
			IpssStudyScenarioXmlType studyScenario = odmObjFactory.createIpssStudyScenarioXmlType();
			studyScenario.setStudyCaseList(odmObjFactory.createIpssStudyScenarioXmlTypeStudyCaseList());
			parser.getStudyCase().setStudyScenario(odmObjFactory.createIpssStudyScenario(studyScenario));
			
			IpssStudyCaseXmlType scenario = odmObjFactory.createIpssStudyCaseXmlType();
			studyScenario.getStudyCaseList().getStudyCase().add(scenario);
		}
		return (IpssStudyScenarioXmlType)parser.getStudyScenario();
	}
}
