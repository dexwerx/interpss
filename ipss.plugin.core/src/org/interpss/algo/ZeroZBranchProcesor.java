/*
  * @(#)ZeroZBranchProcesor.java   
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
  * @Date 04/15/2012
  * 
  *   Revision History
  *   ================
  *
  */
package org.interpss.algo;
import static com.interpss.common.util.IpssLogger.ipssLogger;

import java.util.List;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.common.visitor.IAclfNetBVisitor;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

/**
 * A processor, a AclfNetwork visitor, to process Small/Zero Z branches.
 * 
 * @author mzhou
 *
 */
public class ZeroZBranchProcesor implements IAclfNetBVisitor {
	private double threshold = 0.00001;
	
	/**
	 * constructor 
	 * 
	 * @param threshold zero impedance is define as abs(Z) < threshold 
	 */
	public ZeroZBranchProcesor(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public boolean visit(AclfNetwork net) {
		try {
		  	net.setVisitedStatus(false);
		  	net.markSmallZBranch(this.threshold);		
			
		  	for (Bus b : net.getBusList()) {
		  		if (!b.isVisited()) {
		  			List<Bus> list = ((AclfBus)b).findZeroZPathBuses();
		  			if (list.size() > 1) {
		  				ipssLogger.info("Select parent bus, total buses: " + list.size());
		  				Bus parentBus = selectParentBus(list);
		  				ipssLogger.info("Selected parent bus: " + parentBus.getId());
		  				for (Bus bus : list) {
			  				if (!bus.getId().equals(parentBus.getId())) {
			  					ipssLogger.info("child bus: " + bus.getId());
			  					parentBus.addSection(parentBus.getBusSecList().size()+1, bus);
			  				}
		  				}
		  			}
		  		}
		  	}
		  	
		  	for (Branch b : net.getBranchList()) {
		  		if (b.isVisited() && ((AclfBranch)b).getBranchCode() == AclfBranchCode.ZERO_IMPEDENCE) {
		  			ipssLogger.info("Turn processed small Z branch off, " + b.getId());
		  			b.setStatus(false);
		  		}
		  	}		  	
		  	
			net.setZeroZBranchProcessed(true);
			net.setBusNumberArranged(false);		  	
		} catch (InterpssException e) {
			ipssLogger.severe(e.toString());
			return false;
		}		
	  	return true;
	}
	
	/**
	 * Select parent bus from the bus list
	 * 
	 * @param busList
	 * @return
	 */
	private Bus selectParentBus(List<Bus> busList) {
	  	for (Bus b : busList) {
	  		AclfBus bus = (AclfBus)b;
	  		// if the bus is a gen/load bus
	  		if (!bus.isNonContribute())
	  			return b;
	  		// if the bus has three or more zero Z branch connected.
	  		else if (bus.noConnectedBranch(AclfBranchCode.ZERO_IMPEDENCE) > 2)
	  			return b;
	  	}
	  	// at the end, the first bus in the list is selected
		return busList.get(0);
	}
}
