 /*
  * @(#)DcSystemModelParser.java   
  *
  * Copyright (C) 2010 www.interpss.org
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
  * @Date 11/11/2010
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.model.dist;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.BreakerDistBranchXmlType;
import org.ieee.odm.schema.DistributionNetXmlType;
import org.ieee.odm.schema.FeederDistBranchXmlType;
import org.ieee.odm.schema.GeneratorDistBusXmlType;
import org.ieee.odm.schema.InductionMotorDistBusXmlType;
import org.ieee.odm.schema.MixedLoadDistBusXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.NonContributingDistBusXmlType;
import org.ieee.odm.schema.ReactorDistBranchXmlType;
import org.ieee.odm.schema.SynchronousMotorDistBusXmlType;
import org.ieee.odm.schema.UtilityDistBusXmlType;
import org.ieee.odm.schema.XFormerDistBranchXmlType;

/**
 * A Xml parser for the IEEE DOM schema. 
 */

public class DistModelParser extends AbstractModelParser {
	/**
	 * Default Constructor 
	 * 
	 */
	public DistModelParser() {
		super();
	}	
	
	/**
	 * get the base case object of type DcNetworkXmlType
	 * 
	 * @return
	 */
	public DistributionNetXmlType getDistNet() {
		return (DistributionNetXmlType)getBaseCase();
	}
	
	/**
	 * create the base case object of type DcNetworkXmlType
	 */
	@Override
	public NetworkXmlType createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			DistributionNetXmlType baseCase = odmObjFactory.createDistributionNetXmlType();
			
			baseCase.setBusList(odmObjFactory.createNetworkXmlTypeBusList());
			baseCase.setBranchList(odmObjFactory.createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
		}
		return getStudyCase().getBaseCase().getValue();
	}
	
	/*
	 * 		Bus functions
	 * 		=============
	 */
	
/*
	<element name="distUtilityBus" type="pss:UtilityDistBusXmlType" substitutionGroup="pss:bus"/>
 */
	public UtilityDistBusXmlType createUtilityDistBus() {
		UtilityDistBusXmlType busRec = odmObjFactory.createUtilityDistBusXmlType();
		BaseJaxbHelper.addBus2Net(busRec, getBaseCase());
		return busRec;
	}	
	
	public UtilityDistBusXmlType createUtilityDistBus(String id) throws Exception {
		UtilityDistBusXmlType busRec = createUtilityDistBus();
		setBusId(busRec, id);
		return busRec;
	}		
	
	public UtilityDistBusXmlType createUtilityDistBus(String id, long number) throws Exception {
		UtilityDistBusXmlType busRec = createUtilityDistBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	public UtilityDistBusXmlType getUtilityDistBus(String id) {
		return (UtilityDistBusXmlType)getBus(id);
	}

	/*
	<element name="distGeneratorBus" type="pss:GeneratorDistBusXmlType" substitutionGroup="pss:bus"/>
 */
	public GeneratorDistBusXmlType createDistGeneratorBus() {
		GeneratorDistBusXmlType busRec = odmObjFactory.createGeneratorDistBusXmlType();
		BaseJaxbHelper.addBus2Net(busRec, getBaseCase());
		return busRec;
	}	
	
	public GeneratorDistBusXmlType createDistGeneratorBus(String id) throws Exception {
		GeneratorDistBusXmlType busRec = createDistGeneratorBus();
		setBusId(busRec, id);
		return busRec;
	}		
	
	public GeneratorDistBusXmlType createDistGeneratorBus(String id, long number) throws Exception {
		GeneratorDistBusXmlType busRec = createDistGeneratorBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	public GeneratorDistBusXmlType getDistGeneratorBus(String id) {
		return (GeneratorDistBusXmlType)getBus(id);
	}

	/*
	<element name="distSynMotorBus" type="pss:SynchronousMotorDistBusXmlType" substitutionGroup="pss:bus"/>
 */
	public SynchronousMotorDistBusXmlType createDistSynMotorBus() {
		SynchronousMotorDistBusXmlType busRec = odmObjFactory.createSynchronousMotorDistBusXmlType();
		BaseJaxbHelper.addBus2Net(busRec, getBaseCase());
		return busRec;
	}	
	
	public SynchronousMotorDistBusXmlType createDistSynMotorBus(String id) throws Exception {
		SynchronousMotorDistBusXmlType busRec = createDistSynMotorBus();
		setBusId(busRec, id);
		return busRec;
	}		
	
	public SynchronousMotorDistBusXmlType createDistSynMotorBus(String id, long number) throws Exception {
		SynchronousMotorDistBusXmlType busRec = createDistSynMotorBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	public SynchronousMotorDistBusXmlType getDistSynMotorBus(String id) {
		return (SynchronousMotorDistBusXmlType)getBus(id);
	}

	/*
	<element name="distIndMotorBus" type="pss:InductionMotorDistBusXmlType" substitutionGroup="pss:bus"/>
 */
	public InductionMotorDistBusXmlType createDistIndMotorBus() {
		InductionMotorDistBusXmlType busRec = odmObjFactory.createInductionMotorDistBusXmlType();
		BaseJaxbHelper.addBus2Net(busRec, getBaseCase());
		return busRec;
	}	
	
	public InductionMotorDistBusXmlType createDistIndMotorBus(String id) throws Exception {
		InductionMotorDistBusXmlType busRec = createDistIndMotorBus();
		setBusId(busRec, id);
		return busRec;
	}		
	
	public InductionMotorDistBusXmlType createDistIndMotorBus(String id, long number) throws Exception {
		InductionMotorDistBusXmlType busRec = createDistIndMotorBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	public InductionMotorDistBusXmlType getDistIndMotorBus(String id) {
		return (InductionMotorDistBusXmlType)getBus(id);
	}

	/*
	<element name="distMixedLoadBus" type="pss:MixedLoadDistBusXmlType" substitutionGroup="pss:bus"/>
 */
	public MixedLoadDistBusXmlType createDistMixedLoadBus() {
		MixedLoadDistBusXmlType busRec = odmObjFactory.createMixedLoadDistBusXmlType();
		BaseJaxbHelper.addBus2Net(busRec, getBaseCase());
		return busRec;
	}	
	
	public MixedLoadDistBusXmlType createDistMixedLoadBus(String id) throws Exception {
		MixedLoadDistBusXmlType busRec = createDistMixedLoadBus();
		setBusId(busRec, id);
		return busRec;
	}		
	
	public MixedLoadDistBusXmlType createDistMixedLoadBus(String id, long number) throws Exception {
		MixedLoadDistBusXmlType busRec = createDistMixedLoadBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	public MixedLoadDistBusXmlType getDistMixedLoadBus(String id) {
		return (MixedLoadDistBusXmlType)getBus(id);
	}

	/*
	<element name="distNonContributeBus" type="pss:NonContributingDistBusXmlType" substitutionGroup="pss:bus"/>
 */
	public NonContributingDistBusXmlType createDistNonContributeBus() {
		NonContributingDistBusXmlType busRec = odmObjFactory.createNonContributingDistBusXmlType();
		BaseJaxbHelper.addBus2Net(busRec, getBaseCase());
		return busRec;
	}	
	
	public NonContributingDistBusXmlType createDistNonContributeBus(String id) throws Exception {
		NonContributingDistBusXmlType busRec = createDistNonContributeBus();
		setBusId(busRec, id);
		return busRec;
	}		
	
	public NonContributingDistBusXmlType createDistNonContributeBus(String id, long number) throws Exception {
		NonContributingDistBusXmlType busRec = createDistNonContributeBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	public NonContributingDistBusXmlType getDistNonContributeBus(String id) {
		return (NonContributingDistBusXmlType)getBus(id);
	}
	
	/*
	 * 		Branch functions
	 * 		================
	 */
	
/*
	<element name="distFeederBranch" type="pss:FeederDistBranchXmlType" substitutionGroup="pss:branch"/>
 */
	public void addDistFeederBranch(FeederDistBranchXmlType branch) {
		getBaseCase().getBranchList().getBranch().add(BaseJaxbHelper.branch(branch));
		this.objectCache.put(branch.getId(), branch);
	}
	
	public FeederDistBranchXmlType getDistFeederBranch(String fromId, String toId, String cirId) {
		return (FeederDistBranchXmlType)getBranch(fromId, toId, cirId);
	}

	public FeederDistBranchXmlType createDistFeederBranch() {
		FeederDistBranchXmlType branch = odmObjFactory.createFeederDistBranchXmlType();
		intiBranchData(branch);
		return branch;
	}

	public FeederDistBranchXmlType createDistFeederBranch(String fromId, String toId, String cirId) throws Exception {
		FeederDistBranchXmlType branch = createDistFeederBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}
/*
	<element name="distXfrBranch" type="pss:XFormerDistBranchXmlType" substitutionGroup="pss:branch"/>
 */
	public void addDistXfrBranch(XFormerDistBranchXmlType branch) {
		getBaseCase().getBranchList().getBranch().add(BaseJaxbHelper.branch(branch));
		this.objectCache.put(branch.getId(), branch);
	}
	
	public XFormerDistBranchXmlType getDistXfrBranch(String fromId, String toId, String cirId) {
		return (XFormerDistBranchXmlType)getBranch(fromId, toId, cirId);
	}

	public XFormerDistBranchXmlType createDistXfrBranch() {
		XFormerDistBranchXmlType branch = odmObjFactory.createXFormerDistBranchXmlType();
		intiBranchData(branch);
		return branch;
	}

	public XFormerDistBranchXmlType createDistXfrBranch(String fromId, String toId, String cirId) throws Exception {
		XFormerDistBranchXmlType branch = createDistXfrBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}
	
	/*
	<element name="distReactorBranch" type="pss:ReactorDistBranchXmlType" substitutionGroup="pss:branch"/>
 */
	public void addDistReactorBranch(ReactorDistBranchXmlType branch) {
		getBaseCase().getBranchList().getBranch().add(BaseJaxbHelper.branch(branch));
		this.objectCache.put(branch.getId(), branch);
	}
	
	public ReactorDistBranchXmlType getDistReactorBranch(String fromId, String toId, String cirId) {
		return (ReactorDistBranchXmlType)getBranch(fromId, toId, cirId);
	}

	public ReactorDistBranchXmlType createDistReactorBranch() {
		ReactorDistBranchXmlType branch = odmObjFactory.createReactorDistBranchXmlType();
		intiBranchData(branch);
		return branch;
	}

	public ReactorDistBranchXmlType createDistReactorBranch(String fromId, String toId, String cirId) throws Exception {
		ReactorDistBranchXmlType branch = createDistReactorBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}
	
/*
	<element name="distBreakerBranch" type="pss:BreakerDistBranchXmlType" substitutionGroup="pss:branch"/>
 */
	public void addDistBreakerBranch(BreakerDistBranchXmlType branch) {
		getBaseCase().getBranchList().getBranch().add(BaseJaxbHelper.branch(branch));
		this.objectCache.put(branch.getId(), branch);
	}
	
	public BreakerDistBranchXmlType getDistBreakerBranch(String fromId, String toId, String cirId) {
		return (BreakerDistBranchXmlType)getBranch(fromId, toId, cirId);
	}

	public BreakerDistBranchXmlType createDistBreakerBranch() {
		BreakerDistBranchXmlType branch = odmObjFactory.createBreakerDistBranchXmlType();
		intiBranchData(branch);
		return branch;
	}

	public BreakerDistBranchXmlType createDistBreakerBranch(String fromId, String toId, String cirId) throws Exception {
		BreakerDistBranchXmlType branch = createDistBreakerBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}
	
}
