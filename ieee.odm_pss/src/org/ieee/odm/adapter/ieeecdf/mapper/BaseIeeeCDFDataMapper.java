/*
 * @(#)BaseIeeeCDFDataMapper.java   
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
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.adapter.ieeecdf.mapper;

import org.ieee.odm.adapter.AbstractDataFieldParser;
import org.ieee.odm.adapter.ieeecdf.parser.IeeeCDFBusDataParser;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;

public class BaseIeeeCDFDataMapper {
	protected AbstractDataFieldParser dataParser = null;
}