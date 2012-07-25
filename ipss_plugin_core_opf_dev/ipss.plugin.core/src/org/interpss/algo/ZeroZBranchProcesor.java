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

import com.interpss.CoreObjectFactory;
import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.common.visitor.IAclfNetBVisitor;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;


public class ZeroZBranchProcesor implements IAclfNetBVisitor {
	private double threshhold = 0.00001;
	
	/**
	 * constructor 
	 * 
	 * @param threshhold zero impedance is define as abs(Z) < threshhold 
	 */
	public ZeroZBranchProcesor(double threshhold) {
		this.threshhold = threshhold;
	}

	@Override
	public boolean visit(AclfNetwork net) {
	  	try {
	  		if (net.isZeroZBranchProcessed()) {
	  			ipssLogger.warning("Zero Z branches have already been processed");
	  			return true;
	  		}
			// scan for zero impedance branch
			for (Branch bra : net.getBranchList()) {
				AclfBranch branch = (AclfBranch)bra;
				if (branch.getZ().abs() < this.threshhold) {
					if (branch.getBranchCode() == AclfBranchCode.LINE)
						branch.setBranchCode(AclfBranchCode.ZERO_IMPEDENCE);
					else
						ipssLogger.warning("Branch " + branch.getId() + " z < threshhold, but it is not a transmission line");
				}
			}
		  	
			// process zero z branch
			for (Branch bra : net.getBranchList()) {
				if (bra.isZeroImpedanceBranch()) {
					processZeroImpedanceBranch(bra);
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
	
	private void processZeroImpedanceBranch(Branch branch) throws InterpssException {
		AclfBus fromBus = (AclfBus)branch.getFromBus();
		AclfBus toBus = (AclfBus)branch.getToBus();
		/*
		 * if both from bus and to bus are bus sections, and their
		 * parent buses are different, we need to handle the special 
		 * situation in the future 
		 */
		if (fromBus.isChildSection() &&
				toBus.isChildSection())
			if (fromBus.getParent().getId()
					.equals(toBus.getParent().getId()))
				throw new InterpssException("Branch from bus and to bus have different parent bus, branch id:" + branch.getId());
		
		Bus parentBus = null;
		/*
		 * First check if fromBus or toBus is a parent bus
		 */
		if (fromBus.isParent())
			parentBus = fromBus;
		else if (toBus.isParent())
			parentBus = toBus;
		/*
		 * Then check if fromBus or toBus is a child bus section. 
		 * if a child bus section, use get<*>Bus() returns the parent
		 */
		else if (fromBus.isChildSection())
			parentBus = fromBus.getParent();
		else if (toBus.isChildSection())
			parentBus = toBus.getParent();
		/*
		 * Last, we create the parent, if the branch buses are not
		 * processed before
		 */
		else {
			parentBus = createParentBus(branch);
		}
	
		/*
		 * if from/toBus is not a parent/child, we add it to the parent
		 */
		if (!parentBus.getId().equals(fromBus.getId()) // if created parentBus is not the fromBus
				&& !fromBus.isChildSection() 
				&& !fromBus.isParent())
			parentBus.addSection(parentBus.getBusSecList().size()+1, fromBus);
		
		if (!parentBus.getId().equals(toBus.getId()) // if created parentBus is not the toBus
				&& !toBus.isChildSection() 
				&& !toBus.isParent())
			parentBus.addSection(parentBus.getBusSecList().size()+1, toBus);
		
		// finally, we turn the branch off
		branch.setStatus(false);
	}
	
	private AclfBus createParentBus(Branch branch) {
		// parent bus selection
		AclfBus fromBus = (AclfBus)branch.getFromBus();
		AclfBus toBus = (AclfBus)branch.getToBus();

		// by default, fromBus is selected as the parent bus ref
		AclfBus parentBusRef = fromBus;
		
		// select a parent bus reference according to the 
		// following rules
		if (!fromBus.isNonContribute())
			parentBusRef = fromBus;
		else if (!toBus.isNonContribute())
			parentBusRef = toBus;
		else if (fromBus.nBranchConnected() == 2 && fromBus.noConnectedBranch(AclfBranchCode.ZERO_IMPEDENCE) == 2)
			parentBusRef = toBus;
		else if (toBus.nBranchConnected() == 2 && toBus.noConnectedBranch(AclfBranchCode.ZERO_IMPEDENCE) == 2)
			parentBusRef = fromBus;
		// more rules might be added		
			
		if (parentBusRef.isGen() || parentBusRef.isLoad()) {
			// use the selected bus as the parent bus
			ipssLogger.info("Parent bus selected, id: " + parentBusRef.getId() + " for zero impedance branch " + branch.getId());
			return parentBusRef;
		}
		else {
			// create parent bus
			String id = Constants.Token_ParentBusPrefix+parentBusRef.getId();
			ipssLogger.info("Parent bus created, id: " + id + " for zero impedance branch " + branch.getId());
			AclfBus parentBus;
			parentBus = CoreObjectFactory.createAclfBus(id, branch.getNetwork());
			parentBus.setBaseVoltage(parentBusRef.getBaseVoltage());
			parentBus.setGenCode(AclfGenCode.NON_GEN);
			parentBus.setLoadCode(AclfLoadCode.NON_LOAD);
			return parentBus;
		}
	}
}
