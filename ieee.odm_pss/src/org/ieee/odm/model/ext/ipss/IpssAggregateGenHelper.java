/*
 * @(#)AggregatePricingHelper.java   
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
 * @Date 09/01/2011
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.model.ext.ipss;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.schema.AggregateGenBusXmlType;
import org.ieee.odm.schema.AggregateGenGroupXmlType;
import org.ieee.odm.schema.AggregateGenXmlType;

/**
 * 
 * @author mzhou
 *
 */
public class IpssAggregateGenHelper {
	private AggregateGenXmlType ap = null;
	
	/**
	 * constructor
	 * 
	 * @param parser
	 */
	public IpssAggregateGenHelper (AggregateGenXmlType ap) {
		this.ap = ap;
	}

	public AggregateGenGroupXmlType getAggregateGenGroup(String id) {
		for ( AggregateGenGroupXmlType node : this.ap.getApGroup()) {
			if ( node.getId().equals(id))
				return node;
		}
		return null;
	}

	public AggregateGenGroupXmlType createAggregateGenGroup(String id) {
		AggregateGenGroupXmlType node = odmObjFactory.createAggregateGenGroupXmlType();
		node.setId(id);
		this.ap.getApGroup().add(node);
		return node;
	}

	public AggregateGenBusXmlType createAggregateGenBus(String id, AggregateGenGroupXmlType node) {
		AggregateGenBusXmlType bus = odmObjFactory.createAggregateGenBusXmlType();
		node.getApBus().add(bus);
		bus.setBusId(id);
		return bus;
	}

	public String[] getAPGroupIdAry() {
		String[] sAry = new String[this.ap.getApGroup().size()];
		int cnt = 0;
		for ( AggregateGenGroupXmlType node : this.ap.getApGroup()) {
			sAry[cnt++] = node.getId();
		}		
		return sAry;
	}	
}
