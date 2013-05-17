 /*
  * @(#)PSSEV30GenDataRec.java   
  *
  * Copyright (C) 2006 www.interpss.org
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
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.adapter.psse.mapper;

import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.adapter.psse.parser.PSSEInterAreaTransferDataParser;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.AclfModelParser;

public class PSSEInterAreaTransferDataMapper extends BasePSSEDataMapper {
	
	public PSSEInterAreaTransferDataMapper(PsseVersion ver) {
		super(ver);
		this.dataParser = new PSSEInterAreaTransferDataParser(ver);
	}
	

	public void procLineString(String lineStr, final AclfModelParser parser) throws ODMException {
		dataParser.parseFields(lineStr);
	}
}
