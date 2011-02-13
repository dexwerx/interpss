 /*
  * @(#)ODMObjectFactory.java   
  *
  * Copyright (C) 2008-2010 www.interpss.org
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
  * @Date 12/04/2010
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.ge.GE_PSLF_Adapter;
import org.ieee.odm.adapter.ieeecdf.IeeeCDFAdapter;
import org.ieee.odm.adapter.psse.v26.PSSEV26Adapter;
import org.ieee.odm.adapter.psse.v30.PSSEV30Adapter;
import org.ieee.odm.adapter.ucte.UCTE_DEFAdapter;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.model.dc.DcSystemModelParser;
import org.ieee.odm.model.dist.DistModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.model.opf.OpfModelParser;

public class ODMObjectFactory {
	public static AclfModelParser createAclfModelParser() {
		AclfModelParser parser = new AclfModelParser();
		return parser;
	}

	public static DcSystemModelParser createDcSystemModelParser() {
		DcSystemModelParser parser = new DcSystemModelParser();
		return parser;
	}

	public static DistModelParser createDistModelParser() {
		DistModelParser parser = new DistModelParser();
		return parser;
	}
	
	public static OpfModelParser createOpfModelParser() {
		OpfModelParser parser = new OpfModelParser();
		return parser;
	}

	public static DStabModelParser createDStabModelParser() {
		DStabModelParser parser = new DStabModelParser();
		return parser;
	}
	
	public static AcscModelParser createAcscModelParser() {
		AcscModelParser parser = new AcscModelParser();
		return parser;
	}
	
	public static IODMAdapter createODMAdapter(ODMFileFormatEnum f) throws ODMException {
		if ( f == ODMFileFormatEnum.IeeeCDF ) 
			return new IeeeCDFAdapter(ODMLogger.getLogger());
		else if ( f == ODMFileFormatEnum.PsseV26 )
			return new PSSEV26Adapter(ODMLogger.getLogger());
		else if ( f == ODMFileFormatEnum.PsseV30 )
			return new PSSEV30Adapter(ODMLogger.getLogger());
		else if ( f == ODMFileFormatEnum.GePSLF ) 
			return new GE_PSLF_Adapter(ODMLogger.getLogger());
		else if ( f == ODMFileFormatEnum.UCTE ) 
			return new UCTE_DEFAdapter(ODMLogger.getLogger());
		
		throw new ODMException("Error - unkown ODM file type");
	}
}
