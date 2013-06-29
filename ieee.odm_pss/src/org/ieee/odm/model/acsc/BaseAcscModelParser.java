 /*
  * @(#)AcscModelParser.java   
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
  * @Date 08/11/2010
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.model.acsc;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.common.ODMBranchDuplicationException;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.BaseAclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.LineShortCircuitXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.ScGenDataXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitNetXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.ieee.odm.schema.XfrShortCircuitXmlType;

/**
 * An Acsc ODM Xml parser for the IEEE DOM schema. 
 */
public class BaseAcscModelParser<
					TNetXml extends NetworkXmlType, 
					TBusXml extends BusXmlType,
					TLineXml extends BranchXmlType,
					TXfrXml extends BranchXmlType,
					TPsXfrXml extends BranchXmlType
				> extends BaseAclfModelParser<TNetXml, TBusXml, TLineXml, TXfrXml, TPsXfrXml> {	

	/**
	 * Default Constructor 
	 * 
	 */
	public BaseAcscModelParser() {
		super();
	}	
	
	/**
	 * get the base case object of type ShortCircuitNetXmlType
	 * 
	 * @return
	 */
	public ShortCircuitNetXmlType getAcscNet() {
		return (ShortCircuitNetXmlType)getBaseCase();
	}
	
	/**
	 * create the base case object of type ShortCircuitNetXmlType
	 */
	@SuppressWarnings("unchecked")
	@Override public TNetXml createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			ShortCircuitNetXmlType baseCase = odmObjFactory.createShortCircuitNetXmlType();
			baseCase.setBusList(odmObjFactory.createNetworkXmlTypeBusList());
			baseCase.setBranchList(odmObjFactory.createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
		}
		return (TNetXml)getStudyCase().getBaseCase().getValue();
	}
	
	/**
	 * add a new Bus record to the base case
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override public TBusXml createBus() {
		ShortCircuitBusXmlType busRec = odmObjFactory.createShortCircuitBusXmlType();
		busRec.setOffLine(false);
		busRec.setAreaNumber(1);
		busRec.setZoneNumber(1);
		getBaseCase().getBusList().getBus().add(BaseJaxbHelper.bus(busRec));
		return (TBusXml)busRec;
	}	
	
	/**
	 * get the Acsc bus object using the id. If the bus object is of type aclfBus or acscBus,
	 * cast it to the Acsc type
	 * 
	 * @param id
	 * @return
	 */
	/*
	public ShortCircuitBusXmlType getBus(String id) throws ODMException {
		BusXmlType bus = getBus(id);
		if (bus != null) {
			if (!(bus instanceof ShortCircuitBusXmlType)) {
				ShortCircuitBusXmlType acscbus = null;
				if (bus instanceof LoadflowBusXmlType) {
					acscbus = (ShortCircuitBusXmlType)ModelStringUtil.casting(bus, "aclfBus", "acscBus", this.encoding);
					this.replaceBus(id, acscbus);
				}
				else if (bus instanceof DStabBusXmlType) {
					acscbus = (ShortCircuitBusXmlType)bus;
				}
				else
					throw new ODMException("Bus not found in the DStabNet, id: " + id);
				
				return acscbus;
			}
			else
				return (DStabBusXmlType)bus;
		}
		throw new ODMException("Bus not found in the AcscNet, id: " + id);
	}
	
	//Todo Add factory functions
	
	/*
	 * =================Machine sequence data=================
	 * 
	 */
	
	//ScGenDataXmlType
	
	public ScGenDataXmlType createScGenData(ShortCircuitBusXmlType acscBus, String machId){
		ScGenDataXmlType scGenData=odmObjFactory.createScGenDataXmlType();
		scGenData.setId(machId);
		acscBus.getScGenData().add(scGenData);
		return scGenData;
	}
	
	//LineShortCircuitXmlType
	
	/**
	 * create a new LineShortCircuitXmlType AcscLine. This function should be used when
	 * no AclfBranch has been defined for the same id. If not, use the getAcscLine() instead!
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 * @throws ODMBranchDuplicationException
	 */
	public LineShortCircuitXmlType createAcscLine(String fromId, String toId, String cirId) throws ODMBranchDuplicationException{
		LineShortCircuitXmlType acscLine= odmObjFactory.createLineShortCircuitXmlType();
		addBranch2BaseCase(acscLine,fromId,toId,null,cirId);
		return acscLine;
	}
	
	/**
	 * get the LineShortCircuitXmlType type AcscLine from the network; if the corresponding branch is 
	 * Aclf LineBranchXmlType or LineDStabXmlType type ,it is casted to LineShortCircuitXmlType
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 * @throws ODMException
	 */
    public LineShortCircuitXmlType getAcscLine(String fromId, String toId, String cirId) throws ODMException {
		BaseBranchXmlType branch = this.getBranch(fromId, toId, cirId);
		if (branch != null) {
			if (!(branch instanceof LineShortCircuitXmlType)) {
				String id = ModelStringUtil.formBranchId(fromId, toId, cirId);
				LineShortCircuitXmlType acscbra = null;
				if (branch instanceof LineBranchXmlType) {
					acscbra = (LineShortCircuitXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfLine", "acscLine", this.encoding);
					this.replaceBranch(id, acscbra);
				}
				else if (branch instanceof LineDStabXmlType) {
					acscbra = (LineShortCircuitXmlType)branch;
				}
				else
					throw new ODMException("Branch is not of AcscXfr type, however, it is neither Aclf nor Dstab Xfr type, id: " + fromId + "->" + toId + "(" + cirId + ")");
			
				return acscbra;
			}
			else
				return (LineShortCircuitXmlType)branch;
		}
		throw new ODMException("Branch not found in the AcscNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
    }
    
	/**
	 * create a new XfrShortCircuitXmlType AcscXfr. This function is used only when
	 * no AclfXfr or DstabXfr has been defined for the same id. If not, use the getAcscXfr() instead!
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
    public XfrShortCircuitXmlType createAcscXfr(String fromId, String toId, String cirId){
    	XfrShortCircuitXmlType branch = odmObjFactory.createXfrShortCircuitXmlType();
		branch.setXfrInfo(odmObjFactory.createTransformerInfoXmlType());
		intiBranchData(branch);
		return branch;
    }
  
	 /**
	  * get the Acsc Xfr object using the id. If the branch object is of type aclfXfr or DstabXfr,
	  * cast it to the acscXfr type
	  * @param fromId
	  * @param toId
	  * @param cirId
	  * @return
	 * @throws ODMException 
	  */
    public XfrShortCircuitXmlType getAcscXfr(String fromId, String toId, String cirId) throws ODMException{
		BaseBranchXmlType branch = this.getBranch(fromId, toId, cirId);
		if (branch != null) {
			if (!(branch instanceof XfrShortCircuitXmlType)) {
				String id = ModelStringUtil.formBranchId(fromId, toId, cirId);
				XfrShortCircuitXmlType acscXfr = null;
				if (branch instanceof XfrBranchXmlType) {
					acscXfr  = (XfrShortCircuitXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfXfr", "acscXfr", this.encoding);
					this.replaceBranch(id, acscXfr );
				}
				else if (branch instanceof XfrDStabXmlType) {
					acscXfr  = (XfrShortCircuitXmlType)branch;
				}
				else
					throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
				
				 return acscXfr;
			}
			else
				return (XfrShortCircuitXmlType)branch;
		}
		throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
   	}
}
