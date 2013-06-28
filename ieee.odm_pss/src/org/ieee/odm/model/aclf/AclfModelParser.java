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

import org.ieee.odm.schema.LoadflowNetXmlType;

/**
 * An Aclf Xml parser for the IEEE DOM schema. 
 */
public class AclfModelParser extends BaseAclfModelParser<LoadflowNetXmlType> {
	/**
	 * Default Constructor 
	 * 
	 */
	public AclfModelParser() {
		super();
	}	
	
	/**
	 * constructor
	 * 
	 * @param encoding
	 */
	public AclfModelParser(String encoding) {
		super(encoding);
	}	
}
