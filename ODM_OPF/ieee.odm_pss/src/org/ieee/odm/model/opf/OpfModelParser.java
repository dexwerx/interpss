 /*
  * @(#)OpfModelParser.java   
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
  * @Date 04/11/2010
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.model.opf;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ActivePowerLimitXmlType;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AnalysisCategoryEnumType;
import org.ieee.odm.schema.BaseOpfNetworkXmlType;
import org.ieee.odm.schema.ContentInfoXmlType;
import org.ieee.odm.schema.NetworkCategoryEnumType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.OpfBranchXmlType;
import org.ieee.odm.schema.OpfDclfNetworkXmlType;
import org.ieee.odm.schema.OpfGenBusXmlType;
import org.ieee.odm.schema.OpfNetworkXmlType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.PieceWiseLinearModelXmlType;
import org.ieee.odm.schema.QuadraticModelXmlType;

/**
 * A Xml parser for the IEEE DOM schema. It supports two types of Opf net
 * 
 *   - OpfNetwork
 *   
 *   - OpfDclfNetwork
 * 
 */

public class OpfModelParser extends AclfModelParser {
	public static enum OpfNetType { 
			OPF,     // Full OPF implementation
			DclfOpf  // a sample DCLF OPF implementation based on ISU OPF implementation
		}
	
	private OpfNetType netType = OpfNetType.OPF;
	
	/**
	 * Default Constructor 
	 * 
	 */
	public OpfModelParser(OpfNetType type) {
		super();
		this.netType = type;
	}	
	
	/**
	 * Set BaseCase to Loadflow and Transmission 
	 * 
	 * @param parser
	 * @param originalFormat
	 */
	public void setOPFTransInfo(OriginalDataFormatEnumType originalDataFormat) {
		ContentInfoXmlType info = odmObjFactory.createContentInfoXmlType();
		getStudyCase().setContentInfo(info);
		info.setOriginalDataFormat(originalDataFormat);
		info.setAdapterProviderName("www.interpss.org");
		info.setAdapterProviderVersion("1.00");
		
		getStudyCase().setAnalysisCategory(
				AnalysisCategoryEnumType.OPF);
		getStudyCase().setNetworkCategory(
				NetworkCategoryEnumType.TRANSMISSION);		
	}	
	
	public BaseOpfNetworkXmlType getBaseOpfNet() {
		return (BaseOpfNetworkXmlType)getBaseCase();
	}	
	
	/**
	 * create the base case object of type LoadflowXmlType
	 */
	@Override
	public NetworkXmlType createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			BaseOpfNetworkXmlType baseCase;
			if (netType == OpfNetType.DclfOpf)
				baseCase = odmObjFactory.createOpfDclfNetworkXmlType();
			else
				baseCase = odmObjFactory.createOpfNetworkXmlType();
			baseCase.setBusList(odmObjFactory.createNetworkXmlTypeBusList());
			baseCase.setBranchList(odmObjFactory.createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
		}
		return getStudyCase().getBaseCase().getValue();
	}	
	
	/*
	 * OpfNetwork
	 * ==========
	 */
	
	/**
	 * add a new Bus record to the base case
	 * 
	 * @return
	 */
	public OpfGenBusXmlType createOpfGenBus() {
		OpfGenBusXmlType busRec = odmObjFactory.createOpfGenBusXmlType();
		busRec.setOffLine(false);
		busRec.setAreaNumber(1);
		busRec.setZoneNumber(1);
		getBaseCase().getBusList().getBus().add(BaseJaxbHelper.bus(busRec));
		return busRec;
	}	
	
	/**
	 * create a bus object with the id, make sure there is no duplication
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OpfGenBusXmlType createOpfGenBus(String id) throws ODMException {
		OpfGenBusXmlType busRec = createOpfGenBus();
		busRec.setId(id);
		if (this.objectCache.get(id) != null) {
			throw new ODMException("Bus record duplication, bus id: " + id);
		}
		this.objectCache.put(id, busRec);
		return busRec;
	}		
	
	/**
	 * add a new bus record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	public OpfGenBusXmlType createOpfGenBus(String id, long number) throws ODMException {
		OpfGenBusXmlType busRec = createOpfGenBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	/**
	 * create a LineBranchXmlType object
	 * 
	 * @return
	 */
	public OpfBranchXmlType createOpfBranch() {
		OpfBranchXmlType branch = odmObjFactory.createOpfBranchXmlType();
		branch.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
		branch.setLineInfo(odmObjFactory.createLineBranchInfoXmlType());
		intiBranchData(branch);
		return branch;
	}
	
	public PieceWiseLinearModelXmlType createPWCostModel(){
		return odmObjFactory.createPieceWiseLinearModelXmlType();
	}
	
	public QuadraticModelXmlType createQuadraticCostModel(){
		return odmObjFactory.createQuadraticModelXmlType();
	}
	
	// use BaseDataSetter.createActivePowerLimit(double max, double min, ActivePowerUnitType unit)  instead
	//public ActivePowerLimitXmlType createActivePowerLimit(){
	//	return odmObjFactory.createActivePowerLimitXmlType();
	//}
	
	public OpfBranchXmlType createOpfBranch(String fromId, String toId, String cirId) throws ODMException {
		OpfBranchXmlType branch =  createOpfBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}
	
	public OpfNetworkXmlType getOpfNetwork(){
		return (OpfNetworkXmlType) getBaseCase();
	}
	
	public OpfGenBusXmlType getOpfGenBus(String id) {
		return (OpfGenBusXmlType) getAclfBus(id);
	}
	
	/*
	 * OpfDclfNetwork
	 * ==============
	 */
	
	/**
	 * get the base case object of type LoadflowXmlType
	 * 
	 * @return
	 */
	public OpfDclfNetworkXmlType getOpfDclfNet() {
		return (OpfDclfNetworkXmlType)getBaseCase();
	}
		

	public OpfModelParser(String encoding) {
		super(encoding);
	}
}
