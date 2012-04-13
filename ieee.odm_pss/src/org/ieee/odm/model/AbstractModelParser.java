/*
 * @(#)AbstractModelParser.java   
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
 * @Date 02/01/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.model;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;
import static org.ieee.odm.model.base.ModelContansts.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.AnalysisCategoryEnumType;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusIDRefXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.ContentInfoXmlType;
import org.ieee.odm.schema.IDRecordXmlType;
import org.ieee.odm.schema.NetAreaXmlType;
import org.ieee.odm.schema.NetZoneXmlType;
import org.ieee.odm.schema.NetworkCategoryEnumType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.StudyCaseXmlType;
import org.ieee.odm.schema.StudyScenarioXmlType;

/**
 * Abstract Xml parser implementation as the base for all the IEEE DOM schema parsers. 
 */
public abstract class AbstractModelParser implements IODMModelParser {
	// add "Bus" pre-fix to the bus number to create Bus Id
	public static final String BusIdPreFix = "Bus";
	
	protected String encoding = IODMModelParser.defaultEncoding;
	public String getEncoding() { return this.encoding; }
	
	/*
	 *	property definition
	 * 	=================== 
	 */
	
	// bus and branch object cache for fast lookup. 
	protected Hashtable<String,IDRecordXmlType> objectCache = null;
	public Hashtable<String,IDRecordXmlType> getObjectCache() { return this.objectCache; }

	protected StudyCaseXmlType pssStudyCase = null;
	
//	protected ObjectFactory _factory = null;
//	public ObjectFactory getFactory() {	return this._factory == null? new ObjectFactory() : this._factory; }
	
	/*
	 *	Constructor 
	 *  ===========
	 */
	
	/**
	 * Default Constructor 
	 * 
	 */
	public AbstractModelParser() {
		this.objectCache = new Hashtable<String, IDRecordXmlType>();
		if (!(this instanceof ODMModelParser)) {
			this.getStudyCase().setId("ODM_StudyCase");
		}
	}

	public AbstractModelParser(String encoding) {
		this();
		this.encoding = encoding;
	}
	
	@SuppressWarnings("unchecked")
	public boolean parse(File xmlFile) {
		try {
			JAXBElement<StudyCaseXmlType> elem = (JAXBElement<StudyCaseXmlType>)createUnmarshaller().unmarshal(xmlFile);
			this.pssStudyCase = elem.getValue();
			return true;
		} catch (JAXBException e) { 
			e.printStackTrace();
			ODMLogger.getLogger().severe(e.toString());
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean parse(String xmlString) {
		try {
			ByteArrayInputStream bStr = new ByteArrayInputStream(xmlString.getBytes());
			JAXBElement<StudyCaseXmlType> elem = (JAXBElement<StudyCaseXmlType>)createUnmarshaller().unmarshal(bStr);
			this.pssStudyCase = elem.getValue();
			return true;
		} catch (JAXBException e) { 			
			e.printStackTrace();
			ODMLogger.getLogger().severe(e.toString());
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean parse(InputStream in) {
		try {
			Object obj = createUnmarshaller().unmarshal(in);
			JAXBElement<StudyCaseXmlType> elem = (JAXBElement<StudyCaseXmlType>)obj;
			this.pssStudyCase = elem.getValue();
		} catch (JAXBException e) { 
			//e.printStackTrace();
			ODMLogger.getLogger().severe("ODM xml doc parsing error, " + e.toString());
			return false;
		}
		// cache the loaded bus and branch objects
		for (JAXBElement<? extends BusXmlType> bus : this.getBaseCase().getBusList().getBus()) {
			BusXmlType b = bus.getValue();
			this.objectCache.put(b.getId(), b);
		}
		for (JAXBElement<? extends BaseBranchXmlType> branch : this.getBaseCase().getBranchList().getBranch()) {
			BaseBranchXmlType b = branch.getValue();
			this.objectCache.put(b.getId(), b);
		}
		return true;
	}
	
	/*
	 * 	Abstract functions
	 * 	================== 
	 */

	/**
	 * create BaseCase object, which shold be a child of NetworkXmlType, for
	 * example LoadflowXmlType
	 */
	public abstract NetworkXmlType createBaseCase();
	
	/*
	 * 	Case/Network level functions
	 * 	=========================== 
	 */

	/**
	 * Set BaseCase to Loadflow and Transmission 
	 * 
	 * @param parser
	 * @param originalFormat
	 */
	public void setLFTransInfo(OriginalDataFormatEnumType originalDataFormat) {
		ContentInfoXmlType info = odmObjFactory.createContentInfoXmlType();
		getStudyCase().setContentInfo(info);
		info.setOriginalDataFormat(originalDataFormat);
		info.setAdapterProviderName("www.interpss.org");
		info.setAdapterProviderVersion("1.00");
		
		getStudyCase().setAnalysisCategory(
				AnalysisCategoryEnumType.LOADFLOW);
		getStudyCase().setNetworkCategory(
				NetworkCategoryEnumType.TRANSMISSION);		
	}	
	
	/**
	 * Get the root schema element StudyCase
	 * 
	 * @return
	 */
	public StudyCaseXmlType getStudyCase() {
		if (this.pssStudyCase == null) {
			this.pssStudyCase = new StudyCaseXmlType();
			this.pssStudyCase.setBaseCase(BaseJaxbHelper.network(createBaseCase()));
		}	
		return this.pssStudyCase;
	}

	protected NetworkXmlType getBaseCase() {
		return this.pssStudyCase.getBaseCase().getValue();
	}

	public List<JAXBElement<? extends NetworkXmlType>> getChildNetList() {
		return this.pssStudyCase.getChildNet();
	}

	public StudyScenarioXmlType getStudyScenario() {
		return this.pssStudyCase.getStudyScenario() == null? null :
					this.pssStudyCase.getStudyScenario().getValue();
	}

	/**
	 * create a area object
	 * 
	 * @return
	 */
	public NetAreaXmlType createNetworkArea() {
		if(getBaseCase().getAreaList() == null){
			getBaseCase().setAreaList(odmObjFactory.createNetworkXmlTypeAreaList());
		}
		NetAreaXmlType area = odmObjFactory.createNetAreaXmlType();
		getBaseCase().getAreaList().getArea().add(area);
		return area;
	}

	/**
	 * create a LossZone object
	 * 
	 * @return
	 */
	public NetZoneXmlType createNetworkLossZone() {
		if(getBaseCase().getLossZoneList() == null){
			getBaseCase().setLossZoneList(odmObjFactory.createNetworkXmlTypeLossZoneList());
		}
		NetZoneXmlType zone = odmObjFactory.createNetZoneXmlType();
		getBaseCase().getLossZoneList().getLossZone().add(zone);
		return zone;
	}
	
	/*
	 * 	Bus/Branch object, reference functions
	 * 	======================================
	 */
	
	/**
	 * Get the cashed object by id
	 * 
	 * @param id
	 * @return
	 */
	protected IDRecordXmlType getCachedObject(String id) {
		return this.objectCache.get(id);
	}	
	
	/**
	 * Get the cashed object by id
	 * 
	 * @param id
	 */
	protected void removeCachedObject(String id) {
		this.objectCache.remove(id);
	}
	
	/*
	 *    Bus functions
	 *    =============
	 */

	/**
	 * Get the cashed bus object by id
	 * 
	 * @param id
	 * @return
	 */
	public BusXmlType getBus(String id) {
		return (BusXmlType)this.getCachedObject(id);
	}	

	/**
	 * add a bus object into the branch list and to the cashe table
	 * 
	 */
	public void addBus(BusXmlType bus) {
		getBaseCase().getBusList().getBus().add(BaseJaxbHelper.bus(bus));
		this.objectCache.put(bus.getId(), bus);
	}
	
	/**
	 * remove the branch object from the cache and branch list
	 * 
	 * @param branchId
	 * @return
	 */
	public boolean removeBus(String busId) {
		this.removeCachedObject(busId);
		for (JAXBElement<? extends BusXmlType> busElem : this.getBaseCase().getBusList().getBus()) {
			if (busId.equals(busElem.getValue().getId())) {
				this.getBaseCase().getBusList().getBus().remove(busElem);
				return true;
			}
		}
		return false; 
	}
	
	/**
	 * First remove the bus with the busId and then add the bus record
	 * 
	 * @param busId id of the bus to be removed
	 * @param bus bus object to be added
	 */
	public void replaceBus(String busId, BusXmlType bus) {
		this.removeBus(busId);
		this.addBus(bus);
	}
	
	/**
	 * set bus record id and add the bus object into the cache
	 * 
	 * @param busRec
	 * @param id
	 * @throws Exception
	 */
	public void setBusId(BusXmlType busRec, String id) throws ODMException {
		busRec.setId(id);
		if (this.objectCache.get(id) != null) {
			throw new ODMException("Bus record duplication, bus id: " + id);
		}
		this.objectCache.put(id, busRec);
	}
	
	/**
	 * create a ref record with id
	 * 
	 * @param id
	 * @return
	 */
	public BusIDRefXmlType createBusRef(String id) {
		BusXmlType rec = this.getBus(id);
		BusIDRefXmlType refBus = odmObjFactory.createBusIDRefXmlType();
		refBus.setIdRef(rec);
		return refBus;
	}

	/*
	 *    Branch functions
	 *    ================
	 */
	
	/**
	 * Get the cashed branch object by id
	 * 
	 * @param id
	 * @return
	 */
	public BaseBranchXmlType getBranch(String branchId) {
		return (BaseBranchXmlType)this.getCachedObject(branchId); 
	}

	/**
	 * remove the branch object from the cache and branch list
	 * 
	 * @param branchId
	 * @return
	 */
	public boolean removeBranch(String branchId) {
		this.removeCachedObject(branchId);
		for (JAXBElement<? extends BaseBranchXmlType> braElem : this.getBaseCase().getBranchList().getBranch()) {
			if (branchId.equals(braElem.getValue().getId())) {
				this.getBaseCase().getBranchList().getBranch().remove(braElem);
				return true;
			}
		}
		return false; 
	}
	
	/**
	 * add a branch object into the branch list and to the cashe table
	 * 
	 */
	public void addBranch(BranchXmlType branch) {
		getBaseCase().getBranchList().getBranch().add(BaseJaxbHelper.branch(branch));
		this.objectCache.put(branch.getId(), branch);
	}
	
	/**
	 * get the cashed branch record using fromId, toId and cirId
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public BaseBranchXmlType getBranch(String fromId, String toId, String cirId) {
		String id = ModelStringUtil.formBranchId(fromId, toId, cirId);
		return this.getBranch(id);
	}	
	public BaseBranchXmlType getBranch(String fromId, String toId, String tertId, String cirId) {
		String id = ModelStringUtil.formBranchId(fromId, toId, tertId, cirId);
		return this.getBranch(id);
	}	
	
	/**
	 * First remove the branch with the braId and then add the branch record
	 * 
	 * @param branchId id of the branch to be removed
	 * @param branch branch object to be added
	 */
	public void replaceBranch(String branchId, BranchXmlType branch) {
		this.removeBranch(branchId);
		this.addBranch(branch);
	}
	
	protected void intiBranchData(BaseBranchXmlType branch) {
		getBaseCase().getBranchList().getBranch().add(BaseJaxbHelper.branch(branch));
		branch.setOffLine(false);
		branch.setAreaNumber(1);
		branch.setZoneNumber(1);
	}
	
	protected void addBranch2BaseCase(BaseBranchXmlType branch, String fromId, String toId, String tertId, String cirId)  throws ODMException {
		String id = tertId == null ?
				ModelStringUtil.formBranchId(fromId, toId, cirId) : ModelStringUtil.formBranchId(fromId, toId, tertId, cirId);
		if (this.objectCache.get(id) != null ||
				this.objectCache.get(ModelStringUtil.formBranchId(toId, fromId, cirId)) != null) {
			throw new ODMException("Branch record duplication, bus id: " + id);
		}
		this.objectCache.put(id, branch);		
		branch.setCircuitId(cirId);
		branch.setId(id);
		branch.setFromBus(createBusRef(fromId));
		branch.setToBus(createBusRef(toId));		
		if (tertId != null)
			branch.setTertiaryBus(createBusRef(tertId));		
	}	

	/*
	 * 	marshall/unmarshall, out functions
	 * 	==================================
	 */

	/**
	 * create a Jaxb unmarshaller to unmarshall Xml document to odm object
	 *  
	 * @return
	 * @throws JAXBException
	 */
	private static Unmarshaller unmarshaller = null;
	public Unmarshaller createUnmarshaller() throws JAXBException {
		if (unmarshaller == null) {
			JAXBContext jaxbContext = JAXBContext.newInstance(ODM_Schema_NS);
			unmarshaller = jaxbContext.createUnmarshaller();
			//unmarshaller.setProperty(Marshaller.JAXB_ENCODING, "GB18030");
		}
		return unmarshaller;
	}	

	/**
	 * create a Jaxb marshaller to marshall the odm object to an Xml document
	 * 
	 * @return
	 * @throws JAXBException
	 */
	private Marshaller marshaller = null;
	public Marshaller createMarshaller() throws JAXBException {
		if (marshaller == null) {
			JAXBContext jaxbContext	= JAXBContext.newInstance(ODM_Schema_NS);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//marshaller.setProperty(Marshaller.JAXB_ENCODING, "GB18030");
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		}
		return marshaller;
	}
	
	/**
	 * print the Xml doc to the std out
	 * 
	 */
	public void stdout() {
		try {
			JAXBElement<StudyCaseXmlType> element = odmObjFactory.createPssStudyCase(getStudyCase());
			createMarshaller().marshal( element, System.out );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param addXsi not used in Jabx. It is for XmlBeans
	 */
	public String toXmlDoc() {
		OutputStream ostream = new ByteArrayOutputStream();
		try {
			JAXBElement<StudyCaseXmlType> element = odmObjFactory.createPssStudyCase(getStudyCase());
			createMarshaller().marshal( element, ostream );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ostream.toString();
	}	

	public String toXmlDoc(String outfile) {
		if (outfile == null)
			return toXmlDoc();
		else {
			try {
				OutputStream ostream = new FileOutputStream(new File(outfile));
				JAXBElement<StudyCaseXmlType> element = odmObjFactory.createPssStudyCase(getStudyCase());
				createMarshaller().marshal( element, ostream );
			} catch (Exception e) {
				return e.toString() + " " + outfile;
			}
			return "ODM xml doc write to "  + outfile;
		}
	}
}