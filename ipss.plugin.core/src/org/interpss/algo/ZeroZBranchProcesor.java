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

import java.util.ArrayList;
import java.util.List;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.contingency.Contingency;
import com.interpss.core.aclf.contingency.OutageBranch;
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
	public static enum Method {ZValue, BranchType};
	
	private Method method = Method.ZValue;
	private double threshold = 1.0e-10;
	private boolean allowZeroZBranchLoop = false;
	
	private List<String> protectedBranchIds = new ArrayList<String>();
	/**
	 * set protected branch id list
	 * 
	 * @param list
	 */
	public void setProtectedBranchIdList(List<String> list) { this.protectedBranchIds = list; }
	
	private List<Contingency> contingencyList = null;
	/**
	 * set contingency list for protected branch 
	 * 
	 * @param list
	 */
	public void setContingencyList(List<Contingency> list) { this.contingencyList = list; }
	
	/**
	 * constructor 
	 * 
	 * @param threshold zero impedance is define as abs(Z) < threshold 
	 */
	public ZeroZBranchProcesor(double threshold) {
		this.method = Method.ZValue;
		this.threshold = threshold;
	}

	/**
	 * constructor 
	 * 
	 * @param threshold zero impedance is define as abs(Z) < threshold 
	 */
	public ZeroZBranchProcesor(boolean allowZeroZBranchLoop) {
		this.method = Method.BranchType;
		this.allowZeroZBranchLoop = allowZeroZBranchLoop;
	}

	/**
	 * constructor 
	 * 
	 * @param threshold zero impedance is define as abs(Z) < threshold 
	 */
	public ZeroZBranchProcesor(double threshold, boolean allowZeroZBranchLoop) {
		this.method = Method.BranchType;
		this.threshold = threshold;
		this.allowZeroZBranchLoop = allowZeroZBranchLoop;
	}
	
	@Override public boolean visit(AclfNetwork net) {
		try {
		  	// bus and branch visited status will be used
			// in the processing
		  	net.setVisitedStatus(false);
		  	
		  	// marked those protected branches with visited = true
		  	if (this.protectedBranchIds.size() > 0) 
		  		for (String id : this.protectedBranchIds)
		  			net.getBranch(id).setVisited(true);
		  	
		  	// marked contingency outage branches with visited = true
		  	if (this.contingencyList != null)
		  		for (Contingency cont : this.contingencyList) {
		  			for (AclfBranch branch : cont.getOutageAclfBranches())
		  				branch.setVisited(true);
		  		}
		  	
		  	// mark small Z branch with regarding to the threshold
		  	// line branch will be turned to ZERO_IMPEDENCE branch
		  	// if threshold = 0.0, Breaker branches are turned to zero-z branch
		  	net.markSmallZBranch(this.threshold, true, this.method==Method.ZValue);		

		  	// bus and branch visited status will be used
			// in the processing
		  	net.setVisitedStatus(false);
		  		  	
			
		  	for (Bus b : net.getBusList()) {		  		
		  		
		  		if (b.isStatus()){
		  			if (!b.isVisited()) {
			  			// find all buses on the zero z branch path of the bus, including
			  			// the bus itself
			  			List<Bus> list = ((AclfBus)b).findZeroZPathBuses(allowZeroZBranchLoop);
			  			// if more than one, meaning there is zero-z branch(es), process
			  			// zero-z branch
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
		  	}
		  	
		  	// turn-off processed zero-z branches
		  	for (Branch b : net.getBranchList()) {
		  		if (((AclfBranch)b).getBranchCode() == AclfBranchCode.ZERO_IMPEDENCE) {
		  			if (b.isVisited()) {
		  				ipssLogger.info("Turn processed small Z branch off, " + b.getId());
			  			b.setStatus(false);
		  			}
		  			else
		  				ipssLogger.warning("Small Z branch not processed, " + b.getId());
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
	  	// first select Swing or PV
		for (Bus b : busList) {
			if (b.isStatus()){
				AclfBus bus = (AclfBus)b;
		  		// if the bus is a gen/load bus
		  		if (bus.isSwing() || bus.isGenPV())
		  			return b;
			}
	  		
	  	}
		// next select PQ bus
		for (Bus b : busList) {
			if (b.isStatus()){
				AclfBus bus = (AclfBus)b;
		  		// if the bus is a gen/load bus
		  		if (bus.isGenPQ())
		  			return b;
			}
	  		
	  	}
	  	// then select Load bus
		for (Bus b : busList) {
			if(b.isStatus()){
				AclfBus bus = (AclfBus)b;
		  		// if the bus is a gen/load bus
		  		if (bus.isLoad())
		  			return b;
			}
	  		
	  	}
	  	// at the end, select bus with largest zero-z branches
		Bus bus = busList.get(0);
		int cnt = 0;
		boolean found = false;
		for (Bus b : busList) {
			if (b.isStatus()){
				found = true;
				AclfBus aclfBus = (AclfBus)b;
		  		// if the bus is a gen/load bus
		  		if (aclfBus.noConnectedBranch(AclfBranchCode.ZERO_IMPEDENCE) > cnt) {
		  			bus = b;
		  			cnt = aclfBus.noConnectedBranch(AclfBranchCode.ZERO_IMPEDENCE);
		  		}
			}	  		
	  	}
		if (found == false)
			ipssLogger.warning("No parent bus was found for the bus group of: "+busList.toString());
		
		return bus;
	}
}
