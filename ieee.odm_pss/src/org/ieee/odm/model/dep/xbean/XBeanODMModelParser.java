/*
 * @(#)ODMModelParser.java   
 *
 * Copyright (C) 2006-2008 www.interpss.org
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
 * @Date 02/01/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.model.dep.xbean;

/**
 * A Xml parser for the IEEE DOM schema. 
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.xmlbeans.XmlException;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BranchRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BusRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.DCLineData2TXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.IDRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NetAreaXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PSSNetworkXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PSSStudyCaseDocument;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ScenarioXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.StudyCaseXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TransientSimulationXmlType;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.ModelStringUtil;

public class XBeanODMModelParser implements IODMModelParser {
	// add "No" to the bus number to create Bus Id
	public static final String BusIdPreFix = "Bus";
		
	public static final String Token_nsPrefix = "pss";
	public static final String Token_nsUrl = "http://www.ieee.org/cmte/psace/oss/odm/pss/Schema/v1";
	
	private static final StudyCaseXmlType.SchemaVersion.Enum 
			CurrentSchemaVerion = StudyCaseXmlType.SchemaVersion.V_0_5;
	
	// bus and branch object cache for fast lookup. 
	private Hashtable<String,IDRecordXmlType> objectCache = null;

	private PSSStudyCaseDocument doc = null;
	
	/**
	 * Constructor using an Xml file
	 * 
	 * @param xmlFile
	 * @throws Exception
	 */
	public boolean parse(File xmlFile) {
		try {
			this.doc = PSSStudyCaseDocument.Factory.parse(xmlFile);
			this.objectCache = new Hashtable<String, IDRecordXmlType>();
			if (!doc.validate()) 
				throw new Exception("Error: input XML document is invalid, file: " + xmlFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Constructor using an Xml string
	 * 
	 * @param xmlString
	 * @throws XmlException
	 */
	public boolean parse(String xmlString) {
		try {
			this.doc = PSSStudyCaseDocument.Factory.parse(xmlString);
			this.objectCache = new Hashtable<String, IDRecordXmlType>();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Constructor using an Xml string
	 * 
	 * @param in
	 * @throws Exception
	 */
	public boolean parse(InputStream in) {
		try {
			this.doc = PSSStudyCaseDocument.Factory.parse(in);
			this.objectCache = new Hashtable<String, IDRecordXmlType>();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Default Constructor 
	 * 
	 */
	public XBeanODMModelParser() {
		this.objectCache = new Hashtable<String, IDRecordXmlType>();
		this.doc = PSSStudyCaseDocument.Factory.newInstance();
		this.getStudyCase().setId("ODM_StudyCase");
		this.getStudyCase().setSchemaVersion(CurrentSchemaVerion);
	}
	
	/**
	 * Get the root schema element StudyCase
	 * 
	 * @return
	 */
	public StudyCaseXmlType getStudyCase() {
		if (this.doc.getPSSStudyCase() == null) {
			StudyCaseXmlType scase = this.doc.addNewPSSStudyCase();
			// init the base case
			getBaseCase();
			// init the default study scenario
			scase.addNewScenarioList();
		}	
		return this.doc.getPSSStudyCase();
	}

	/**
	 * Get the cashed object by id
	 * 
	 * @param id
	 * @return
	 */
	private IDRecordXmlType getCachedObject(String id) {
		return this.objectCache.get(id);
	}
	
	/**
	 * Get the cashed bus object by id
	 * 
	 * @param id
	 * @return
	 */
	public BusRecordXmlType getBusRecord(String id) {
		return (BusRecordXmlType)this.getCachedObject(id);
	}
	
	/**
	 * Get the cashed branch object by id
	 * 
	 * @param id
	 * @return
	 */
	public BranchRecordXmlType getBranchRecord(String branchId) {
		return (BranchRecordXmlType)this.getCachedObject(branchId); 
	}

	public BranchRecordXmlType getBranchRecord(String fromId, String toId, String cirId) {
		String id = ModelStringUtil.formBranchId(fromId, toId, cirId);
		return (BranchRecordXmlType)this.getCachedObject(id);
	}
	
	public BranchRecordXmlType getBranchRecord(String fromId, String toId, String tertId, String cirId) {
		String id = ModelStringUtil.formBranchId(fromId, toId, tertId, cirId);
		return (BranchRecordXmlType)this.getCachedObject(id);
	}

	/**
	 * Get the cashed dcLine2T object by id
	 * 
	 * @param id
	 * @return
	 */
	public DCLineData2TXmlType getDcLine2TRecord(String recId, String invId, int number) {
		String id = ModelStringUtil.formBranchId(recId, invId, new Integer(number).toString());
		return (DCLineData2TXmlType)this.getCachedObject(id);
	}
	
	/**
	 * Get the cashed object by id
	 * 
	 * @param id
	 */
	public void removeCachedObject(String id) {
		this.objectCache.remove(id);
	}

	/**
	 * Get the baseCase element
	 * 
	 * @return
	 */
	public PSSNetworkXmlType getBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			PSSNetworkXmlType baseCase = getStudyCase().addNewBaseCase();
			baseCase.addNewBusList();
			baseCase.addNewBranchList();		
		}
		return getStudyCase().getBaseCase();
	}

	/**
	 * Get the default scenario
	 * 
	 * @return
	 */
	public ScenarioXmlType getDefaultScenario() {
		return getStudyCase().getScenarioList().getScenarioArray()[0];
	}

	/**
	 * Get the transient stability simulation record of the default scenario
	 *  
	 * @return
	 */
	public TransientSimulationXmlType getDefaultTransSimu(){
		return XBeanTranStabSimuHelper.getTransientSimlation(getDefaultScenario());
	}
	/**
	 * add a new Bus record to the base case
	 * 
	 * @return
	 */
	public BusRecordXmlType addNewBaseCaseBus() {
		return getStudyCase().getBaseCase().getBusList().addNewBus();
	}	
	
	/**
	 * add a new bus record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	public BusRecordXmlType addNewBaseCaseBus(String id, long number) throws Exception {
		BusRecordXmlType bus = getStudyCase().getBaseCase().getBusList().addNewBus();
		bus.setId(id);
		bus.setNumber(number);
		if (this.objectCache.get(id) != null) {
			throw new Exception("Bus record duplication, bus id: " + id);
		}
		this.objectCache.put(id, bus);
		return bus;
	}	

	/**
	 * add a new Branch record to the base case
	 * 
	 * @return
	 */
	public BranchRecordXmlType addNewBaseCaseBranch() {
		return getStudyCase().getBaseCase().getBranchList().addNewBranch();		
	}
	
	/**
	 * add a new branch record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	public BranchRecordXmlType addNewBaseCaseBranch(String id) throws Exception {
		BranchRecordXmlType branch = getStudyCase().getBaseCase().getBranchList().addNewBranch();
		branch.setId(id);
		if (this.objectCache.get(id) != null) {
			throw new Exception("Branch record duplication, bus id: " + id);
		}
		this.objectCache.put(id, branch);
		return branch;
	}
	
	/**
	 * add a new 2T DcLine record to the base case and to the cache table
	 * 
	 * @param id
	 * @return
	 */
	public DCLineData2TXmlType addNewBaseCaseDCLine2T(String recId, String invId, int number) throws Exception {
		if (getStudyCase().getBaseCase().getDcLineList() == null)
			getStudyCase().getBaseCase().addNewDcLineList();
		DCLineData2TXmlType dcLine = getStudyCase().getBaseCase().getDcLineList().addNewDcLint2T();
		String branchId = ModelStringUtil.formBranchId(recId, invId, new Integer(number).toString());
		dcLine.setId(branchId);
		dcLine.setNumber(number);
		if (this.objectCache.get(branchId) != null) {
			throw new Exception("DCLine record duplication, bus id: " + branchId);
		}
		this.objectCache.put(branchId, dcLine);

		dcLine.addNewRectifier();
		dcLine.getRectifier().addNewBusId().setIdRef(recId);

		dcLine.addNewInverter();
		dcLine.getInverter().addNewBusId().setIdRef(invId);
		return dcLine;
	}

	/**
	 * add a new area record to the base case
	 * 
	 * @return
	 */
	public PSSNetworkXmlType.AreaList getAreaList(){
		if(getStudyCase().getBaseCase().getAreaList()==null){
			getStudyCase().getBaseCase().addNewAreaList();
		}
		return getStudyCase().getBaseCase().getAreaList();
	}
	
	/**
	 * create an area object and added to the net.areaList
	 * 
	 * @return
	 */
	
	public NetAreaXmlType addNewBaseCaseArea() {
		return getAreaList().addNewArea();
	}
	
	/**
	 * get the net.tieLineList 
	 * 
	 * @return
	 */
	public PSSNetworkXmlType.TieLineList getTielineList(){
		if(getStudyCase().getBaseCase().getTieLineList()==null){
			getStudyCase().getBaseCase().addNewTieLineList();
		}
		return getStudyCase().getBaseCase().getTieLineList();
	}
	
	/**
	 * create a tieLine object
	 * 
	 * @return
	 */
	public PSSNetworkXmlType.TieLineList.Tieline addNewBaseCaseTieline() {
		return getTielineList().addNewTieline();
	}
	
	public void save(String filename) throws IOException {
		this.doc.save(new File(filename));
	}
	
	public void load(String filename) throws XmlException, IOException {
		 File inputXMLFile = new File(filename);
		this.doc = PSSStudyCaseDocument.Factory.parse(inputXMLFile);
	}
	
	public String toXmlDoc(boolean addXsi) {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><pss:PSSStudyCase xmlns:pss=\"" + Token_nsUrl + "\"";
		if (addXsi)
			return this.doc.xmlText(XBeanParserHelper.getXmlOpts()).replaceFirst("<pss:PSSStudyCase", 
				 str + " " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		else
			return this.doc.xmlText(XBeanParserHelper.getXmlOpts()).replaceFirst("<pss:PSSStudyCase", str);
	}

	/**
	 * convert the document object to an XML string
	 */
	@Override
	public String toString() {
		 return this.doc.toString(); 
	}
}
