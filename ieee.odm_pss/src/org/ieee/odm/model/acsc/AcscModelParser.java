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
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.LineShortCircuitXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.ScGenDataXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitNetXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.ieee.odm.schema.XfrShortCircuitXmlType;

/**
 * An Acsc ODM Xml parser for the IEEE DOM schema. 
 */
public class AcscModelParser extends BaseAcscModelParser<ShortCircuitNetXmlType, ShortCircuitBusXmlType> {	

	/**
	 * Default Constructor 
	 * 
	 */
	public AcscModelParser() {
		super();
	}	
}
