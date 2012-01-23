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

import org.ieee.odm.schema.AggregatePricingBusXmlType;
import org.ieee.odm.schema.AggregatePricingNodeXmlType;
import org.ieee.odm.schema.AggregatePricingXmlType;

/**
 * 
 * @author mzhou
 *
 */
public class AggregatePricingHelper {
	private AggregatePricingXmlType ap = null;
	
	/**
	 * constructor
	 * 
	 * @param parser
	 */
	public AggregatePricingHelper (AggregatePricingXmlType ap) {
		this.ap = ap;
	}

	public AggregatePricingNodeXmlType getAggregatePricingNode(String id) {
		for ( AggregatePricingNodeXmlType node : this.ap.getApNode()) {
			if ( node.getId().equals(id))
				return node;
		}
		return null;
	}

	public AggregatePricingNodeXmlType createAggregatePricingNode(String id) {
		AggregatePricingNodeXmlType node = odmObjFactory.createAggregatePricingNodeXmlType();
		node.setId(id);
		this.ap.getApNode().add(node);
		return node;
	}

	public AggregatePricingBusXmlType createAggregatePricingBus(String id, AggregatePricingNodeXmlType node) {
		AggregatePricingBusXmlType bus = odmObjFactory.createAggregatePricingBusXmlType();
		node.getApNode().add(bus);
		bus.setBusId(id);
		return bus;
	}

	public String[] getAPNodeIdAry() {
		String[] sAry = new String[this.ap.getApNode().size()];
		int cnt = 0;
		for ( AggregatePricingNodeXmlType node : this.ap.getApNode()) {
			sAry[cnt++] = node.getId();
		}		
		return sAry;
	}	
}
