/*
 * @(#)NetModificationHelper.java   
 *
 * Copyright (C) 2006-2012 www.interpss.org
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
 * @Date 08/30/2012
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.model.modify;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.schema.BranchChangeRecXmlType;
import org.ieee.odm.schema.BusChangeRecXmlType;
import org.ieee.odm.schema.ModifyRecordXmlType;
import org.ieee.odm.schema.NetModificationXmlType;

/**
 *
 */
public class NetModificationHelper {
	private IODMModelParser parser = null;
	
	public NetModificationHelper (IODMModelParser parser) {
		this.parser = parser;
	}
	
	/**
	 * create an NetModificationXmlType for net modification records
	 * 
	 * @return
	 */
	public NetModificationXmlType createNetModificationList() {
		NetModificationXmlType rec = odmObjFactory.createNetModificationXmlType();
		addModifyRecord(rec);
		return rec;
	}	
	
	/**
	 * create an BranchChangeRecXmlType record and added to the netModifyList
	 * 
	 * @return
	 */	
	public BranchChangeRecXmlType createBranchChangeRecXmlType(NetModificationXmlType netModifyList) {
		BranchChangeRecXmlType branchChange = odmObjFactory.createBranchChangeRecXmlType();
		netModifyList.getBranchChangeRecList().getBranchChangeRec().add(branchChange);
		return branchChange;
	}
	
	/**
	 * create an BusChangeRecXmlType record and added to the netModifyList
	 * 
	 * @return
	 */	
	public BusChangeRecXmlType createBusChangeRecXmlType(NetModificationXmlType netModifyList) {
		BusChangeRecXmlType busChange = odmObjFactory.createBusChangeRecXmlType();
		netModifyList.getBusChangeRecList().getBusChangeRec().add(busChange);
		return busChange;
	}

	private void addModifyRecord(ModifyRecordXmlType rec) {
		if (this.parser.getStudyCase().getModificationList() == null) {
			this.parser.getStudyCase().setModificationList(odmObjFactory.createStudyCaseXmlTypeModificationList());
		}
		this.parser.getStudyCase().getModificationList().getModification().add(rec);
	}	
}
