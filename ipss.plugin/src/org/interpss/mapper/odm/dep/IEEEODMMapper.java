/*
 * @(#)IEEEODMMapper.java   
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
 * @Date 02/21/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.odm.dep;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.model.dep.jaxb.JaxbODMModelParser;
import org.ieee.odm.model.dep.xbean.XBeanODMModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.model.opf.OpfModelParser;
import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.interpss.mapper.odm.ODMAcscDataMapper;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.interpss.mapper.odm.ODMOpfDataMapper;
import org.interpss.mapper.odm.impl.dep.v07.ODM2SimuCtxMapper;
import org.interpss.mapper.odm.impl.dep.xbean.XmlBeansODM2SimuCtxMapperImpl;

import com.interpss.common.exp.InterpssException;
import org.interpss.mapper.AbstractMapping;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class IEEEODMMapper<Tfrom> extends AbstractMapping<Tfrom, SimuContext> {

	public IEEEODMMapper(IPSSMsgHub msg) {
		super(msg);
	}

	/**
	 * map(load) a IEEEODMPSSModelParser object into an InterPSS object of Type Class
	 * 
	 * @param fromObj a IEEEODMPSSModelParser object
	 * @param toObj an object of type Class
	 * @param kclass class type of the toObj 
	 */
	@Override
	public SimuContext map2Model(Tfrom fromObj) throws InterpssException {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.NOT_DEFINED, msg);
		boolean rtn = true;
		if (fromObj instanceof OpfModelParser) {
			OpfModelParser parser = (OpfModelParser) fromObj;
			rtn = new ODMOpfDataMapper(msg).map2Model(parser, simuCtx);
		}
		else if (fromObj instanceof DStabModelParser) {
			DStabModelParser parser = (DStabModelParser) fromObj;
			rtn = new ODMDStabDataMapper(msg).map2Model(parser, simuCtx);
		}
		else if (fromObj instanceof AcscModelParser) {
			AcscModelParser parser = (AcscModelParser) fromObj;
			rtn = new ODMAcscDataMapper(msg).map2Model(parser, simuCtx);
		}
		else if (fromObj instanceof AclfModelParser) {
			AclfModelParser parser = (AclfModelParser) fromObj;
			rtn = new ODMAclfDataMapper(msg).map2Model(parser, simuCtx);
		}			
		else if (fromObj instanceof XBeanODMModelParser) {
			XBeanODMModelParser parser = (XBeanODMModelParser) fromObj;
			rtn = XmlBeansODM2SimuCtxMapperImpl.odm2SimuCtxMapping(parser, simuCtx);
		}
		else if (fromObj instanceof JaxbODMModelParser) {
			JaxbODMModelParser parser = (JaxbODMModelParser) fromObj;
			rtn = ODM2SimuCtxMapper.odm2SimuCtxMapping(parser, simuCtx);
		}
		if (rtn)
			return simuCtx;
		else
			throw new InterpssException("Error - ODM model to SimuContext mapping");
	}
}
