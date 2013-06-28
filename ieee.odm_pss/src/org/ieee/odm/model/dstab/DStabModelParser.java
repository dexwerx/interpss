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

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.aclf.BaseAclfModelParser;
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
import org.ieee.odm.schema.ShortCircuitNetXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.ieee.odm.schema.XfrShortCircuitXmlType;

/**
 * A DStab ODM Xml model parser for the IEEE DOM schema. 
 */
public class DStabModelParser extends BaseAclfModelParser<DStabNetXmlType> {
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
	 * constructor
	 * 
	 * @param encoding
	 */
	public DStabModelParser(String encoding) {
		super(encoding);
	}	

	/**
	 * get the base case object of type DStabNetXmlType
	 * 
	 * @return
	 */
	public DStabNetXmlType getDStabNet() {
		return (DStabNetXmlType)getBaseCase();
	}
	
	/**
	 * get DStab Simulation record
	 * 
	 * @return
	 */
	public DStabSimulationXmlType getDStabSimu() {
		if (this.tranSimu == null)
			this.tranSimu = odmObjFactory.createDStabSimulationXmlType();
		return this.tranSimu;
	}
	
	/**
	 * create the base case object of type DStabNetXmlType
	 */
	@Override
	public DStabNetXmlType createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			DStabNetXmlType baseCase = odmObjFactory.createDStabNetXmlType();
			baseCase.setBusList(odmObjFactory.createNetworkXmlTypeBusList());
			baseCase.setBranchList(odmObjFactory.createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
			this.tranSimu = odmObjFactory.createDStabSimulationXmlType();
		}
		return (DStabNetXmlType)getStudyCase().getBaseCase().getValue();
	}
	
	/*
	 * 		Bus functions
	 * 		=============
	 */
	
	/**
	 * add a new Bus record to the base case
	 * 
	 * @return
	 */
	@Override
	public LoadflowBusXmlType createAclfBus() {
		DStabBusXmlType busRec = odmObjFactory.createDStabBusXmlType();
		busRec.setOffLine(false);
		busRec.setAreaNumber(1);
		busRec.setZoneNumber(1);
		getBaseCase().getBusList().getBus().add(BaseJaxbHelper.bus(busRec));
		return busRec;
	}

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
					dbus = (DStabBusXmlType)ModelStringUtil.casting(bus, "aclfBus", "dstabBus", this.encoding);
				}
				else if (bus instanceof ShortCircuitBusXmlType) {
					dbus = (DStabBusXmlType)ModelStringUtil.casting(bus, "acscBus", "dstabBus", this.encoding);
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
	 * create a LineBranchXmlType object
	 * 
	 * @return
	 */
	@Override
	public LineDStabXmlType createLineBranch() {
		LineDStabXmlType branch = odmObjFactory.createLineDStabXmlType();
		branch.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
		branch.setLineInfo(odmObjFactory.createLineBranchInfoXmlType());
		intiBranchData(branch);
		return branch;
	}
	
	/**
	 * create a XfrBranchXmlType object
	 * 
	 * @return
	 */
	@Override
	public XfrDStabXmlType createXfrBranch() {
		XfrDStabXmlType branch = odmObjFactory.createXfrDStabXmlType();
		branch.setXfrInfo(odmObjFactory.createTransformerInfoXmlType());
		intiBranchData(branch);
		return branch;
	}

	/**
	 * create a PSXfrBranchXmlType object
	 * 
	 * @return
	 */
	@Override
	public PSXfrDStabXmlType createPSXfrBranch() {
		PSXfrDStabXmlType branch = odmObjFactory.createPSXfrDStabXmlType();
		intiBranchData(branch);
		return branch;
	}
	
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
					dbra = (LineDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfLine", "dstabLine", this.encoding);
				}
				else if (branch instanceof LineShortCircuitXmlType) {
					dbra = (LineDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "acscLine", "dstabLine", this.encoding);
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
					dbra = (XfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfXfr", "dstabXfr", this.encoding);
				}
				else if (branch instanceof XfrShortCircuitXmlType) {
					dbra = (XfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "acscXfr", "dstabXfr", this.encoding);
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
					dbra = (PSXfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "aclfPSXfr", "dstabPSXfr", this.encoding);
				}
				else if (branch instanceof PSXfrShortCircuitXmlType) {
					dbra = (PSXfrDStabXmlType)ModelStringUtil.casting((BranchXmlType)branch, "acscPSXfr", "dstabPSXfr", this.encoding);
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
