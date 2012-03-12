/*
 * @(#)ODMAclfDataMapper.java   
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
 * @Date 03/15/2012
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.odm.impl;

import static org.interpss.CorePluginFunction.AclfParser2AclfNet;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.NetworkXmlType;
import org.interpss.mapper.odm.IMultiNetProcessor;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;


public class MultiNetHepler {
	private AclfModelParser aclfParser;
	
	public MultiNetHepler(AclfModelParser aclfParser) {
		this.aclfParser = aclfParser;
	}
	
	/**
	 * build multi-network case. The child networks are processed by the childNetProcessor
	 * 
	 * @param childNetProcesor
	 * @return
	 * @throws InterpssException
	 */
	public AclfNetwork buildAclfMultiNet(IMultiNetProcessor childNetProcesor) throws InterpssException {
		AclfNetwork mainNet = AclfParser2AclfNet.fx(aclfParser);
		
		for (JAXBElement<? extends NetworkXmlType> childNet : aclfParser.getChildNetList()) {
			childNetProcesor.setMainNet(mainNet).process(childNet.getValue());
		}
		return mainNet;
	}
}
