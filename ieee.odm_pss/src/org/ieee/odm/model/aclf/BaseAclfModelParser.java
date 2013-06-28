 /*
  * @(#)AclfModelParser.java   
  *
  * Copyright (C) 2009 www.interpss.org
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
  * @Date 04/11/2009
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.model.aclf;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.Hashtable;
import java.util.List;

import org.ieee.odm.common.ODMBranchDuplicationException;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.ConverterXmlType;
import org.ieee.odm.schema.DCLineData2TXmlType;
import org.ieee.odm.schema.FlowInterfaceRecXmlType;
import org.ieee.odm.schema.InterchangeXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.PSXfr3WBranchXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.TielineXmlType;
import org.ieee.odm.schema.Xfr3WBranchXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;

/**
 * An Aclf Xml parser for the IEEE DOM schema. 
 */
public class BaseAclfModelParser<TNetXml extends NetworkXmlType, TBusXml extends BusXmlType> extends AbstractModelParser<TNetXml, TBusXml> {
	/**
	 * Default Constructor 
	 * 
	 */
	public BaseAclfModelParser() {
		super();
	}	
	
	/**
	 * constructor
	 * 
	 * @param encoding
	 */
	public BaseAclfModelParser(String encoding) {
		super(encoding);
	}	
	
	/**
	 * create the base case object of type LoadflowXmlType
	 */
	@Override public TNetXml createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			LoadflowNetXmlType baseCase = odmObjFactory.createLoadflowNetXmlType();
			baseCase.setBusList(odmObjFactory.createNetworkXmlTypeBusList());
			baseCase.setBranchList(odmObjFactory.createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
		}
		return (TNetXml)getStudyCase().getBaseCase().getValue();
	}
	
	/*
	 * 		Bus functions
	 * 		=============
	 */

	/**
	 * add a new Bus record to the base case
	 * 
	 * @return
	 */
	private LoadflowBusXmlType createAclfBus() {
		LoadflowBusXmlType busRec = odmObjFactory.createLoadflowBusXmlType();
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
	@Override public TBusXml createBus(String id) throws ODMException {
		LoadflowBusXmlType busRec = createAclfBus();
		busRec.setId(id);
		if (this.objectCache.get(id) != null) {
			throw new ODMException("Bus record duplication, bus id: " + id);
		}
		this.objectCache.put(id, busRec);
		return (TBusXml)busRec;
	}		
	
	/**
	 * add a new bus record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	@Override public TBusXml createBus(String id, long number) throws ODMException {
		TBusXml busRec = createBus(id);
		busRec.setNumber(number);
		return busRec;
	}	
	
	/*
	 * 		Branch functions
	 * 		================
	 */
	
	/**
	 * get the Line branch object
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public LineBranchXmlType getLineBranch(String fromId, String toId, String cirId) {
		return (LineBranchXmlType)getBranch(fromId, toId, cirId);
	}
	
	/**
	 * get the xfr branch object
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public XfrBranchXmlType getXfrBranch(String fromId, String toId, String cirId) {
		return (XfrBranchXmlType)getBranch(fromId, toId, cirId);
	}
	public Xfr3WBranchXmlType getXfr3WBranch(String fromId, String toId, String tertId, String cirId) {
		return (Xfr3WBranchXmlType)getBranch(fromId, toId, tertId, cirId);
	}

	/**
	 * get the ps xfr branch object
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public PSXfrBranchXmlType getPSXfrBranch(String fromId, String toId, String cirId) {
		return (PSXfrBranchXmlType)getBranch(fromId, toId, cirId);
	}
	/**
	 * get the ps xfr branch object
	 * 
	 * @param fromId
	 * @param toId
	 * @param tertId
	 * @param cirId
	 * @return
	 */
	public PSXfr3WBranchXmlType getPSXfr3WBranch(String fromId, String toId, String tertId, String cirId) {
		return (PSXfr3WBranchXmlType)getBranch(fromId, toId, tertId, cirId);
	}

	/**
	 * create a LineBranchXmlType object
	 * 
	 * @return
	 */
	public LineBranchXmlType createLineBranch() {
		LineBranchXmlType branch = odmObjFactory.createLineBranchXmlType();
		branch.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
		branch.setLineInfo(odmObjFactory.createLineBranchInfoXmlType());
		intiBranchData(branch);
		return branch;
	}
	
	/**
	 * create a XfrBranchXmlType object
	 * 
	 * @return
	 */
	public XfrBranchXmlType createXfrBranch() {
		XfrBranchXmlType branch = odmObjFactory.createXfrBranchXmlType();
		branch.setXfrInfo(odmObjFactory.createTransformerInfoXmlType());
		intiBranchData(branch);
		return branch;
	}

	/**
	 * create a Xfr3WBranchXmlType object
	 * 
	 * @return
	 */
	public Xfr3WBranchXmlType createXfr3WBranch() {
		Xfr3WBranchXmlType branch = odmObjFactory.createXfr3WBranchXmlType();
		intiBranchData(branch);
		return branch;
	}

	/**
	 * create a PSXfrBranchXmlType object
	 * 
	 * @return
	 */
	public PSXfrBranchXmlType createPSXfrBranch() {
		PSXfrBranchXmlType branch = odmObjFactory.createPSXfrBranchXmlType();
		intiBranchData(branch);
		return branch;
	}

	/**
	 * create a PSXfr3WBranchXmlType object
	 * 
	 * @return
	 */
	public PSXfr3WBranchXmlType createPSXfr3WBranch() {
		PSXfr3WBranchXmlType branch = odmObjFactory.createPSXfr3WBranchXmlType();
		intiBranchData(branch);
		return branch;
	}
	
	/**
	 * add a new Line branch record to the base case and to the cache table
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public LineBranchXmlType createLineBranch(String fromId, String toId, String cirId) throws ODMBranchDuplicationException {
		LineBranchXmlType branch = createLineBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}

	/**
	 * add a new Xfr branch record to the base case and to the cache table
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public XfrBranchXmlType createXfrBranch(String fromId, String toId, String cirId) throws ODMBranchDuplicationException {
		XfrBranchXmlType branch = createXfrBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}

	/**
	 * add a new Xfr branch record to the base case and to the cache table
	 * 
	 * @param fromId
	 * @param toId
	 * @param tertId
	 * @param cirId
	 * @return
	 */
	public Xfr3WBranchXmlType createXfr3WBranch(String fromId, String toId, String tertId, String cirId) throws ODMBranchDuplicationException {
		Xfr3WBranchXmlType branch = createXfr3WBranch();
		addBranch2BaseCase(branch, fromId, toId, tertId, cirId);
		return branch;
	}

	/**
	 * add a new PS Xfr branch record to the base case and to the cache table
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public PSXfrBranchXmlType createPSXfrBranch(String fromId, String toId, String cirId) throws ODMBranchDuplicationException {
		PSXfrBranchXmlType branch = createPSXfrBranch();
		addBranch2BaseCase(branch, fromId, toId, null, cirId);
		return branch;
	}

	/**
	 * add a new 3W PS Xfr branch record to the base case and to the cache table
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public PSXfr3WBranchXmlType createPSXfr3WBranch(String fromId, String toId, String tertId, String cirId) throws ODMBranchDuplicationException {
		PSXfr3WBranchXmlType branch = createPSXfr3WBranch();
		addBranch2BaseCase(branch, fromId, toId, tertId, cirId);
		return branch;
	}
	
	/**
	 * Get the cashed dcLine2T object by id
	 * 
	 * @param id
	 * @return
	 */
	public DCLineData2TXmlType getDcLine2TRecord(String recId, String invId, String dcLineId) {
		String id = ModelStringUtil.formBranchId(recId, invId, dcLineId);
		return (DCLineData2TXmlType)this.getCachedObject(id);
	}
	
	/**
	 * add a new 2T DcLine record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	public DCLineData2TXmlType createDCLine2TRecord(String recId, String invId, String dcLineId) throws ODMBranchDuplicationException {
		DCLineData2TXmlType dcLine = odmObjFactory.createDCLineData2TXmlType();
		addBranch2BaseCase(dcLine, recId, invId, null, dcLineId);
		
		ConverterXmlType rectifier = odmObjFactory.createConverterXmlType();
		dcLine.setRectifier(rectifier);
		dcLine.getRectifier().setBusId(createBusRef(recId));
	
		ConverterXmlType inverter = odmObjFactory.createConverterXmlType();
		dcLine.setInverter(inverter);
		dcLine.getInverter().setBusId(createBusRef(invId));
		return dcLine;
	}	
	
	/*
	 * 		Network object functions
	 * 		========================
	 */
	
	/**
	 * create a tieLine object
	 * 
	 * @return
	 */
	public TielineXmlType createTieline() {
		LoadflowNetXmlType net = (LoadflowNetXmlType)getNet();
		if (net.getTieLineList() == null)
			net.setTieLineList(odmObjFactory.createLoadflowNetXmlTypeTieLineList());
		TielineXmlType tieLine = odmObjFactory.createTielineXmlType();
		net.getTieLineList().getTieline().add(tieLine);
		return tieLine;
	}	
	
	/**
	 * create a Interchange object
	 * 
	 * @return
	 */
	public InterchangeXmlType createInterchange() {
		LoadflowNetXmlType net = (LoadflowNetXmlType)getNet();
		if (net.getInterchangeList() == null)
			net.setInterchangeList(odmObjFactory.createLoadflowNetXmlTypeInterchangeList());
		InterchangeXmlType interchange = odmObjFactory.createInterchangeXmlType();
		net.getInterchangeList().getInterchange().add(interchange);
		return interchange;
	}	

	public List<FlowInterfaceRecXmlType> getInterfaceList() {
		LoadflowNetXmlType net = (LoadflowNetXmlType)getNet();
		return net.getFlowInterfaceList().getFlowInterface();
	}	
	
	/**
	 * create a Interface object
	 * 
	 * @return
	 */
	public FlowInterfaceRecXmlType createInterface() {
		LoadflowNetXmlType net = (LoadflowNetXmlType)getNet();
		if (net.getFlowInterfaceList() == null)
			net.setFlowInterfaceList(odmObjFactory.createLoadflowNetXmlTypeFlowInterfaceList());
		FlowInterfaceRecXmlType inter = odmObjFactory.createFlowInterfaceRecXmlType();
		net.getFlowInterfaceList().getFlowInterface().add(inter);
		return inter;
	}	

	/**
	 * get Interface record by id
	 * 
	 * @param id
	 * @return
	 */
	public FlowInterfaceRecXmlType getInterface(String id) {
		LoadflowNetXmlType net = (LoadflowNetXmlType)getNet();
		if (net.getFlowInterfaceList() != null)
			for (FlowInterfaceRecXmlType inter : net.getFlowInterfaceList().getFlowInterface()) {
				if (id.equals(inter.getId()))
					return inter;
			}
		return null;
	}	
	/**
	 * get Interface record by id
	 * 
	 * @param id
	 * @return
	 */

	private Hashtable<String, FlowInterfaceRecXmlType> interfaceLookupTable = null;
	public FlowInterfaceRecXmlType getInterfaceCached(String id) {
		LoadflowNetXmlType net = (LoadflowNetXmlType)getNet();
		if (this.interfaceLookupTable == null) {
			this.interfaceLookupTable = new Hashtable<String, FlowInterfaceRecXmlType>();
			for (FlowInterfaceRecXmlType inter : net.getFlowInterfaceList().getFlowInterface()) {
				this.interfaceLookupTable.put(inter.getId(), inter);
			}
		}
		return this.interfaceLookupTable.get(id);
	}	
}
