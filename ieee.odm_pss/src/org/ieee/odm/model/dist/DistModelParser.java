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

import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.BaseJaxbHelper;
import org.ieee.odm.schema.DistributionNetXmlType;
import org.ieee.odm.schema.FeederDistBranchXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.UtilityDistBusXmlType;

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
			DistributionNetXmlType baseCase = this.getFactory().createDistributionNetXmlType();
			
			baseCase.setBusList(this.getFactory().createNetworkXmlTypeBusList());
			baseCase.setBranchList(this.getFactory().createNetworkXmlTypeBranchList());
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
	<element name="distGeneratorBus" type="pss:GeneratorDistBusXmlType" substitutionGroup="pss:bus"/>
	<element name="distSynMotorBus" type="pss:SynchronousMotorDistBusXmlType" substitutionGroup="pss:bus"/>
	<element name="distIndMotorBus" type="pss:InductionMotorDistBusXmlType" substitutionGroup="pss:bus"/>
	<element name="distMixedLoadBus" type="pss:MixedLoadDistBusXmlType" substitutionGroup="pss:bus"/>
	<element name="distNonContributeSynMotorBus" type="pss:NonContributingDistBusXmlType" substitutionGroup="pss:bus"/>
 */

	/**
	 * add a new Bus record to the base case
	 * 
	 * @return
	 */
	public UtilityDistBusXmlType createUtilityDistBus() {
		UtilityDistBusXmlType busRec = this.getFactory().createUtilityDistBusXmlType();
		BaseJaxbHelper.addBus2Net(busRec, getBaseCase());
		return busRec;
	}	
	
	/**
	 * create a bus object with the id, make sure there is no duplication
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public UtilityDistBusXmlType createUtilityDistBus(String id) throws Exception {
		UtilityDistBusXmlType busRec = createUtilityDistBus();
		setBusId(busRec, id);
		return busRec;
	}		
	
	/**
	 * add a new bus record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	public UtilityDistBusXmlType createUtilityDistBus(String id, long number) throws Exception {
		UtilityDistBusXmlType busRec = createUtilityDistBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	/**
	 * get the bus object using the id
	 * 
	 * @param id
	 * @return
	 */
	public UtilityDistBusXmlType getUtilityDistBus(String id) {
		return (UtilityDistBusXmlType)getBus(id);
	}

	/*
	 * 		Branch functions
	 * 		================
	 */
	
/*
	<element name="distFeederBranch" type="pss:FeederDistBranchXmlType" substitutionGroup="pss:branch"/>
	<element name="distXfrBranch" type="pss:XFormerDistBranchXmlType" substitutionGroup="pss:branch"/>
	<element name="distReactorBranch" type="pss:ReactorDistBranchXmlType" substitutionGroup="pss:branch"/>
	<element name="distBreakerBranch" type="pss:BreakerDistBranchXmlType" substitutionGroup="pss:branch"/>
 */
	
	public void addDistFeederBranch(FeederDistBranchXmlType branch) {
		getBaseCase().getBranchList().getBranch().add(BaseJaxbHelper.branch(branch));
		this.objectCache.put(branch.getId(), branch);
	}
	
	/**
	 * get the Line branch object
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public FeederDistBranchXmlType getDistFeederBranch(String fromId, String toId, String cirId) {
		return (FeederDistBranchXmlType)getBranch(fromId, toId, cirId);
	}

	/**
	 * create a LineBranchXmlType object
	 * 
	 * @return
	 */
	public FeederDistBranchXmlType createDistFeederBranch() {
		FeederDistBranchXmlType branch = this.getFactory().createFeederDistBranchXmlType();
		intiBranchData(branch);
		return branch;
	}

	/**
	 * add a new Line branch record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	public FeederDistBranchXmlType createDistFeederBranch(String fromId, String toId, String cirId) throws Exception {
		FeederDistBranchXmlType branch = createDistFeederBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}
}
