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

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.ShortCircuitNetXmlType;

/**
 * A Xml parser for the IEEE DOM schema. 
 */

public class AcscModelParser extends AclfModelParser {	

	/**
	 * Default Constructor 
	 * 
	 */
	public AcscModelParser() {
		super();
	}	
	
	/**
	 * get the base case object of type DStabNetXmlType
	 * 
	 * @return
	 */
	public ShortCircuitNetXmlType getAcscNet() {
		return (ShortCircuitNetXmlType)getBaseCase();
	}
	
	/**
	 * create the base case object of type ShortCircuitNetXmlType
	 */
	@Override
	public NetworkXmlType createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			ShortCircuitNetXmlType baseCase = odmObjFactory.createShortCircuitNetXmlType();
			baseCase.setBusList(odmObjFactory.createNetworkXmlTypeBusList());
			baseCase.setBranchList(odmObjFactory.createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
		}
		return getStudyCase().getBaseCase().getValue();
	}
}
