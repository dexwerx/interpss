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

import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabNetXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.NetworkXmlType;

/**
 * A Xml parser for the IEEE DOM schema. 
 */

public class DStabModelParser extends AclfModelParser {
	// some input file might carry DStab Simu data;
	private DStabSimulationXmlType tranSimu = null;
	
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
	
	public DStabSimulationXmlType getDStabSimu() {
		if (this.tranSimu == null)
			this.tranSimu = this.getFactory().createDStabSimulationXmlType();
		return this.tranSimu;
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
			this.tranSimu = this.getFactory().createDStabSimulationXmlType();
		}
		return getStudyCase().getBaseCase().getValue();
	}
	
	/*
	 * 		Bus functions
	 * 		=============
	 */

	/**
	 * get the bus object using the id
	 * 
	 * @param id
	 * @return
	 */
	public DStabBusXmlType getDStabBus(String id) throws ODMException {
		BusXmlType bus = getBus(id);
		if (bus != null) {
			if (!(bus instanceof DStabBusXmlType)) {
				if (bus instanceof LoadflowBusXmlType) {
					DStabBusXmlType dbus = (DStabBusXmlType)ModelStringUtil.casting(bus, "aclfBus", "dstabBus");
					this.replaceBus(id, dbus);
					return dbus;
				}
			}
			else
				return (DStabBusXmlType)bus;
		}
		throw new ODMException("Bus not found in the DStabNet, id: " + id);
	}
	
}
