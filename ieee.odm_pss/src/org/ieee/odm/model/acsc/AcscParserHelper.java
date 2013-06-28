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

import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.schema.ScGenDataXmlType;

/**
 * Acsc ODM model parser helper utility functions
 * 
 * @author mzhou
 *
 */
public class AcscParserHelper extends AclfParserHelper {
	
	
	public static ScGenDataXmlType getScGenData(AcscModelParser parser, String busId, String genId) throws ODMException {
		ScGenDataXmlType targetScGen=null;
		if(!parser.getBus(busId).getScGenData().isEmpty()){
			for(ScGenDataXmlType scGenData: parser.getBus(busId).getScGenData()){
				if(scGenData.getId().equals(genId)){
					targetScGen= scGenData;
				}
			}
		}
		
		return targetScGen;
	}
}
