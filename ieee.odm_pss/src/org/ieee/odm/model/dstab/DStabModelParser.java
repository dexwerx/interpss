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
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabNetXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.LineShortCircuitXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.PSXfrDStabXmlType;
import org.ieee.odm.schema.PSXfrShortCircuitXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.ieee.odm.schema.XfrShortCircuitXmlType;

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
	 * get the DStab bus object using the id. If the bus object is of type aclfBus or acscBus,
	 * cast it to the dstabBus type
	 * 
	 * @param id
	 * @return
	 */
	public DStabBusXmlType getDStabBus(String id) throws ODMException {
		BusXmlType bus = getBus(id);
		if (bus != null) {
			if (!(bus instanceof DStabBusXmlType)) {
				DStabBusXmlType dbus = null;
				if (bus instanceof LoadflowBusXmlType) {
					dbus = (DStabBusXmlType)ModelStringUtil.casting(bus, "aclfBus", "dstabBus");
				}
				else if (bus instanceof ShortCircuitBusXmlType) {
					dbus = (DStabBusXmlType)ModelStringUtil.casting(bus, "acscBus", "dstabBus");
				}
				else
					throw new ODMException("Bus not found in the DStabNet, id: " + id);
				this.replaceBus(id, dbus);
				return dbus;
			}
			else
				return (DStabBusXmlType)bus;
		}
		throw new ODMException("Bus not found in the DStabNet, id: " + id);
	}
	
	/*
	 * 		Branch functions
	 * 		================
	 */

	/**
	 * get the DStab Line object using the id. If the branch object is of type aclfLine or acscLine,
	 * cast it to the dstabLine type
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public LineDStabXmlType getDStabLine(String fromId, String toId, String cirId) throws ODMException {
		BaseBranchXmlType branch = this.getBranch(fromId, toId, cirId);
		if (branch != null) {
			if (!(branch instanceof LineDStabXmlType)) {
				String id = ModelStringUtil.formBranchId(fromId, toId, cirId);
				LineDStabXmlType dbra = null;
				if (branch instanceof LineBranchXmlType) {
					dbra = (LineDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfLine", "dstabLine");
				}
				else if (branch instanceof LineShortCircuitXmlType) {
					dbra = (LineDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "acscLine", "dstabLine");
				}
				else
					throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
				this.replaceBranch(id, dbra);
				return dbra;
			}
			else
				return (LineDStabXmlType)branch;
		}
		throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
	}

	/**
	 * get the DStab Xfr object using the id. If the branch object is of type aclfXfr or acscXfr,
	 * cast it to the dstabXfr type
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public XfrDStabXmlType getDStabXfr(String fromId, String toId, String cirId) throws ODMException {
		BaseBranchXmlType branch = this.getBranch(fromId, toId, cirId);
		if (branch != null) {
			if (!(branch instanceof XfrDStabXmlType)) {
				String id = ModelStringUtil.formBranchId(fromId, toId, cirId);
				XfrDStabXmlType dbra = null;
				if (branch instanceof XfrBranchXmlType) {
					dbra = (XfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfXfr", "dstabXfr");
				}
				else if (branch instanceof XfrShortCircuitXmlType) {
					dbra = (XfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "acscXfr", "dstabXfr");
				}
				else
					throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
				this.replaceBranch(id, dbra);
				return dbra;
			}
			else
				return (XfrDStabXmlType)branch;
		}
		throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
	}
	
	/**
	 * get the DStab PSXfr object using the id. If the branch object is of type aclfPSXfr or acscPSXfr,
	 * cast it to the dstabPSXfr type
	 * 
	 * @param fromId
	 * @param toId
	 * @param cirId
	 * @return
	 */
	public PSXfrDStabXmlType getDStabPSXfr(String fromId, String toId, String cirId) throws ODMException {
		BaseBranchXmlType branch = this.getBranch(fromId, toId, cirId);
		if (branch != null) {
			if (!(branch instanceof PSXfrDStabXmlType)) {
				String id = ModelStringUtil.formBranchId(fromId, toId, cirId);
				PSXfrDStabXmlType dbra = null;
				if (branch instanceof PSXfrBranchXmlType) {
					dbra = (PSXfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfPSXfr", "dstabPSXfr");
				}
				else if (branch instanceof PSXfrShortCircuitXmlType) {
					dbra = (PSXfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "acscPSXfr", "dstabPSXfr");
				}
				else
					throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
				this.replaceBranch(id, dbra);
				return dbra;
			}
			else
				return (PSXfrDStabXmlType)branch;
		}
		throw new ODMException("Branch not found in the DStabNet, id: " + fromId + "->" + toId + "(" + cirId + ")");
	}
}
