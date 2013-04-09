/*
 * @(#)ContingencyOutFunc.java   
 *
 * Copyright (C) 2006 www.interpss.org
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
 * @Date 10/27/2009
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.display;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.multicase.aclf.ContingencyAnalysis;
import com.interpss.simu.multicase.result.AclfBranchResultRec;
import com.interpss.simu.multicase.result.AclfBusResultRec;

/**
 * Contingency analysis output configuration, a default implementation
 * 
 * @author mzhou
 *
 */
public class ContingencyOutConfigure implements ContingencyOutFunc.IConfigure {
	private ContingencyAnalysis mcase;
	private AclfNetwork aclfNet;
	
	public ContingencyOutConfigure(ContingencyAnalysis mcase, AclfNetwork aclfNet) {
		this.mcase = mcase;
		this.aclfNet = aclfNet;
	}
	
	@Override public void setBusLimit() {
  		for (AclfBus aclfBus : aclfNet.getBusList()) {
			AclfBusResultRec rec = mcase.getBusResultSummary().get(aclfBus.getId());
			rec.getLimit().setUpperVoltLimit(mcase.getBusVoltageUpperLimitPU());
			rec.getLimit().setLowerVoltLimit(mcase.getBusVoltageLowerLimitPU());
		}
	}

	@Override public void setBranchRating() {
  		for (AclfBranch bra : aclfNet.getBranchList()) {
  			AclfBranchResultRec rec = mcase.getBranchResultSummary().get(bra.getId());
  			rec.getRating().setThermalMvaRating(bra.getRatingMva1());
  			rec.getRating().setThermalAmpsRating(0.0);
  		}
	}
}
