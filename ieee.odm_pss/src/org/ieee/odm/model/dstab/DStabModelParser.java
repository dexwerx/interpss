/*
 * @(#)DStabModelParser.java   
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

package org.ieee.odm.model.dstab;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.DStabNetXmlType;
import org.ieee.odm.schema.NetworkXmlType;

/**
 * A Xml parser for the IEEE DOM schema. 
 */

public class DStabModelParser extends AclfModelParser {
	/**
	 * Constructor using an Xml string
	 * 
	 * @param xmlString
	 * @throws XmlException
	 */
//	public DStabModelParser(String xmlString) throws Exception {
//		super(xmlString);
//	}
	
	/**
	 * Constructor using an Xml string
	 * 
	 * @param in
	 * @throws Exception
	 */
//	public DStabModelParser(InputStream in) throws Exception {
//		super(in);
//	}
	
	/**
	 * Default Constructor 
	 * 
	 */
	public DStabModelParser() {
		super();
	}	
	
	/**
	 * get the base case object of type DStabNetXmlType
	 * 
	 * @return
	 */
	public DStabNetXmlType getDStabNet() {
		return (DStabNetXmlType)getBaseCase();
	}
	
	/**
	 * create the base case object of type DStabNetXmlType
	 */
	@Override
	public NetworkXmlType createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			DStabNetXmlType baseCase = this.getFactory().createDStabNetXmlType();
			baseCase.setBusList(this.getFactory().createNetworkXmlTypeBusList());
			baseCase.setBranchList(this.getFactory().createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
		}
		return getStudyCase().getBaseCase().getValue();
	}
}
