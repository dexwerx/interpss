 /*
  * @(#)AcscParserHelper.java   
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

import javax.xml.bind.JAXBElement;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.BusGenDataXmlType;
import org.ieee.odm.schema.GroundingEnumType;
import org.ieee.odm.schema.GroundingXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitGenDataXmlType;
import org.ieee.odm.schema.XformerConnectionXmlType;
import org.ieee.odm.schema.XformrtConnectionEnumType;
import org.ieee.odm.schema.ZUnitType;

/**
 * Acsc ODM model parser helper utility functions
 * 
 * @author mzhou
 *
 */
public class AcscParserHelper extends AclfParserHelper {

	/**
	 * create Acsc equiv gen
	 * 
	 * @return
	 */
	public static JAXBElement<ShortCircuitGenDataXmlType> createAcscEquivGen() {
		ShortCircuitGenDataXmlType equivGen = odmObjFactory.createShortCircuitGenDataXmlType();
		return odmObjFactory.createAcscEquivGen(equivGen);
	}
	
	/**
	 * get Acsc Gen Data object on the acscBus with id = genId
	 * 
	 * @param acscBus
	 * @param genId
	 * @return null if acscGenData not found
	 */
	public static ShortCircuitGenDataXmlType getScGenData(ShortCircuitBusXmlType acscBus, String genId) throws ODMException {
		for (JAXBElement<? extends LoadflowGenXmlType> elem : acscBus.getGenData().getContributeGen()) {
			ShortCircuitGenDataXmlType scGenData = (ShortCircuitGenDataXmlType)elem.getValue();
			if (scGenData.getId().equals(genId))
				return scGenData;
		}
    	throw new ODMException("Generator not found, ID: " + genId + "@Bus:" + acscBus.getId());
	}
	
	/**
	 * create a Acsc Contributing Generator object
	 * 
	 */
	public static ShortCircuitGenDataXmlType createAcscContributeGen(ShortCircuitBusXmlType busRec,String machId) {
		BusGenDataXmlType genData = busRec.getGenData();
		if (genData == null) {
			genData = odmObjFactory.createBusGenDataXmlType();
			busRec.setGenData(genData);
			genData.setEquivGen(createAcscEquivGen());
		}
		// some model does not need ContributeGenList
		ShortCircuitGenDataXmlType contribGen = odmObjFactory.createShortCircuitGenDataXmlType();
		contribGen.setId(machId);
		genData.getContributeGen().add(odmObjFactory.createAcscGenData(contribGen));
		return contribGen;
	}
	
	/**
	 * create directed or solid grounding xfr connection xml Type
	 * @return
	 */
	public static XformerConnectionXmlType createDirectedGroundingConnection(){
		XformerConnectionXmlType  xfrConnect = odmObjFactory.createXformerConnectionXmlType();
		GroundingXmlType ground = odmObjFactory.createGroundingXmlType();
		ground.setGroundingConnection(GroundingEnumType.SOLID_GROUNDED);
		xfrConnect.setGrounding(ground);
		return xfrConnect;
	}
	/**
	 * create impdeance grounding xfr connection xml Type
	 * ZG = rg + j* xg
	 * @param rg
	 * @param xg
	 * @return
	 */
	public static XformerConnectionXmlType createZGroundingConnection(double rg, double xg){
		XformerConnectionXmlType  xfrConnect = odmObjFactory.createXformerConnectionXmlType();
		GroundingXmlType ground = odmObjFactory.createGroundingXmlType();
		ground.setGroundingConnection(GroundingEnumType.Z_GROUNDED);
		ground.setGroundingZ(BaseDataSetter.createZValue(rg, xg, ZUnitType.PU));
		xfrConnect.setGrounding(ground);
		return xfrConnect;
	}
	
	/**
	 * create ungrounding xfr connection xml Type
	 * @return
	 */
	public static XformerConnectionXmlType createUnGroundingConnection(){
		XformerConnectionXmlType  xfrConnect = odmObjFactory.createXformerConnectionXmlType();
		GroundingXmlType ground = odmObjFactory.createGroundingXmlType();
		ground.setGroundingConnection(GroundingEnumType.UNGROUNDED);
		xfrConnect.setGrounding(ground);
		return xfrConnect;
	}
	
	  
}
