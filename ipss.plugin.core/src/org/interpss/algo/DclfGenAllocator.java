/*
  * @(#)DclfGenAllocator.java   
  *
  * Copyright (C) 2006-2012 www.interpss.com
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
  * @Date 08/15/2012
  * 
  *   Revision History
  *   ================
  *
  */
package org.interpss.algo;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.common.visitor.IAclfNetBVisitor;
import com.interpss.core.net.Bus;

/**
 * Allocate system Gen-Load to a set of buses according to bus
 * gen participation factor.
 * 
 * @author mzhou
 *
 */
public class DclfGenAllocator implements IAclfNetBVisitor {
	
	@Override
	public boolean visit(AclfNetwork net) {
		net.createDistGenList();

		// calculate system total Gen - Load
		double diff =  0.0;
		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.isActive()) {
				diff += bus.getDclfBusP();
			}
		}
		ipssLogger.info("Total network gen to be allocated: " + diff);

		// allocate diff to gen buses with participation factor > 0
		for (Bus b : net.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.isActive() && bus.getGenPartFactor() > 0.0) {
				double old = bus.getGenP();
				bus.setGenP(bus.getGenP() - diff * bus.getGenPartFactor());
				ipssLogger.info("Gen @" + bus.getId() + " adjusted to : " + bus.getGenP() + "  from " + old);
			}
		}
		
	  	return true;
	}
}
