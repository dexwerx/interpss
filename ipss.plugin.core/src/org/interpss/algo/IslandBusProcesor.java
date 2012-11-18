/*
  * @(#)TopologyProcesor.java   
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
  * @Date 10/30/2012
  * 
  *   Revision History
  *   ================
  *
  */
package org.interpss.algo;

import java.util.ArrayList;
import java.util.List;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.StringUtil;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;

/**
 * Class for processing network for finding island buses
 * 
 * @author mzhou
 *
 */
public class IslandBusProcesor {
	private static int EquivOutageInterfaceWalkStepLimit = 50;
	private int IslandSubnetLimit = 5;
	
	AclfNetwork aclfNet = null;
	
	// for storing the interface branches
	private List<String> islandSubnetInterface = null;
	public List<String> getIslandSubnetInterface() { return islandSubnetInterface;	}

	private List<String> islandBusIdList = null;
	public List<String> getIslandBusIdList() { return islandBusIdList; }

	private int walkStepCnt = 0;
	
	public IslandBusProcesor(AclfNetwork net) {
		this.aclfNet = net;
	}
	
	/**
	 * For a set of outage branches, find all island buses due the opening of the 
	 * branches in the set.
	 * 
	 * @param outageBranches
	 * @return true if island bus process properly
	 */
	public boolean findIslandBus(List<AclfBranch> outageBranches) throws InterpssException {
		if (outageBranches.size() == 0)
			throw new InterpssException("Outage branch set has no element");

		// clear the lists. They are used to store search results
		this.islandBusIdList = new ArrayList<String>();
		this.islandSubnetInterface = new ArrayList<String>();
		
		// use branch.visited for track outage branch processing status
		for (AclfBranch branch : outageBranches) 
			branch.setVisited(false);
		
		// there should be at least one branch in the list
		Network net = outageBranches.get(0).getNetwork();

		int cnt = 0;
		while(cnt++ < IslandSubnetLimit) {  // it is assumed that the number of island subnets are limited  
			AclfBranch startBranch = getUnprocessedBranch(outageBranches);
			if (startBranch == null)
				// no outage branch left to be processed
				return true;
			else {
				// visitedBranchList is used for tracking loop situation
				List<String> visitedBranchList = new ArrayList<String>();
				// first try the from side
				List<String> islandInterface = findIslandBus(startBranch, startBranch.getFromBus(), outageBranches, visitedBranchList);
				if (islandInterface == null) {
					// if not outage branch interface found on the from side, try the to side
					visitedBranchList.clear();
					islandInterface = findIslandBus(startBranch, startBranch.getToBus(), outageBranches, visitedBranchList);
				}
				
				if (islandInterface != null) {
					// island subnet found
					// first store the island interface info
					for (String branchId : islandInterface) { 
						net.getBranch(branchId).setVisited(true);
						if (!StringUtil.contain(this.islandSubnetInterface, branchId))
							this.islandSubnetInterface.add(branchId);
					}
					
					// store island bus info
					for (String branchId : visitedBranchList) {
						Branch branch = net.getBranch(branchId);
						if (!isOutageBranch(branch, outageBranches)) {
							if (!StringUtil.contain(this.islandBusIdList, branch.getFromBus().getId()))
								this.islandBusIdList.add(branch.getFromBus().getId());
							if (!StringUtil.contain(this.islandBusIdList, branch.getToBus().getId()))
								this.islandBusIdList.add(branch.getToBus().getId());
						}
					}
				}
			}
		}		
		return false;
	}
	
	/**
	 * For the refBranch, walk into the busOnBranch side to identify island subnet interface
	 * 
	 * @param refBranch
	 * @param busOnBranch
	 * @param outageBranches
	 * @param visitedBranchList
	 * @return interface branchId list or null if no interface found
	 */
	private List<String> findIslandBus(Branch refBranch, Bus busOnBranch, List<AclfBranch> outageBranches, List<String> visitedBranchList) {
		walkStepCnt = 0;
		// for storing the interface branches
		List<String> islandInterface = new ArrayList<String>();

		islandInterface.add(refBranch.getId());
		visitedBranchList.add(refBranch.getId());
		
		if (findIslandBusImpl(refBranch, busOnBranch, outageBranches, islandInterface, visitedBranchList)) {
			return islandInterface;
		}
		else
			return null;
	}
	
	/**
	 * Recursively find island subnet starting from the refBranch, into the bus side
	 * 
	 * @param refBranch
	 * @param bus
	 * @param outageBranches
	 * @param islandInterface
	 * @param visitedBranchList
	 * @return
	 */
	private boolean findIslandBusImpl(Branch refBranch, Bus bus, List<AclfBranch> outageBranches, List<String> islandInterface, List<String> visitedBranchList) {
		if (walkStepCnt++ > EquivOutageInterfaceWalkStepLimit || 
				((AclfBus)bus).isRefBus()) {  // if ref bus found, we stop the search
			//ipssLogger.warning("Find equiv outage interface walk step cnt exceeds " + EquivOutageInterfaceWalkStepLimit + 
			//		"  Contincy: " + this.contingency.getId());
			return false;
		}
		for (Branch branch : bus.getBranchList()) {
			if (branch.isActive() && !branch.getId().equals(refBranch.getId())) {
				if (isOutageBranch(branch, outageBranches)) {
					if (!StringUtil.contain(islandInterface, branch.getId()))
						islandInterface.add(branch.getId());
				}
				else {
					// to handle loop situation, we want to make sure that the branch was not visited before
					if (!StringUtil.contain(visitedBranchList, branch.getId())) {
						visitedBranchList.add(branch.getId());
						if(!findIslandBusImpl(branch, branch.getOppositeBus(bus), outageBranches, islandInterface, visitedBranchList))
							return false;
					}
				}
			}
		}
		return true;
	}
	
	private AclfBranch getUnprocessedBranch(List<AclfBranch> outageBranches) {
		for (AclfBranch branch : outageBranches) 
			if (branch.isActive() && !branch.isVisited()) {
				return branch;
			}
		return null;
	}
	
	private boolean isOutageBranch(Branch branch, List<AclfBranch> outageBranches) {
		for (AclfBranch bra : outageBranches) 
			if (bra.getId().equals(branch.getId()))
				return true;
		return false;
	}
}
