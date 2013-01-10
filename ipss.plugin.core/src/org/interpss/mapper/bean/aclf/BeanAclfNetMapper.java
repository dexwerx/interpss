/*
 * @(#)BeanAclfNetMapper.java   
 *
 * Copyright (C) 2008 www.interpss.org
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
 * @Date 01/15/2013
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.bean.aclf;

import org.apache.commons.math3.complex.Complex;
import org.interpss.datamodel.bean.aclf.AclfBranchBean;
import org.interpss.datamodel.bean.aclf.AclfBusBean;
import org.interpss.datamodel.bean.aclf.AclfCaseBean;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.mapper.AbstractMapping;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfPQGenBus;
import com.interpss.core.aclf.adpter.AclfPVGenBus;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.aclf.adpter.AclfXformer;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

/**
 * mapper implementation to map AclfCaseBean object to InterPSS AclfNetwork object
 * 
 * @author mzhou
 */
public class BeanAclfNetMapper extends AbstractMapping<AclfCaseBean, SimuContext> {
	/**
	 * constructor
	 */
	public BeanAclfNetMapper() {
	}
	
	/**
	 * map into store in the ODM parser into simuCtx object
	 * 
	 * @param p ODM parser object, representing a ODM xml file
	 * @param simuCtx
	 */
	@Override
	public SimuContext map2Model(AclfCaseBean p) throws InterpssException {
		final SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.NOT_DEFINED);
		if (this.map2Model(p, simuCtx)) {
  	  		simuCtx.setId("InterPSS_SimuCtx");
  	  		simuCtx.setName("InterPSS SimuContext Object");
  	  		simuCtx.setDesc("InterPSS SimuContext Object - created from JSON Bean model");
  			return simuCtx;
		}	
		throw new InterpssException("Error in map ODM model to SimuContext object");
	}	
	
	/**
	 * map into store in the ODM parser into simuCtx object
	 * 
	 * @param p a LoadflowNetXmlType object, representing a aclf base network
	 * @param simuCtx
	 */
	@Override public boolean map2Model(AclfCaseBean netBean, SimuContext simuCtx) {
		boolean noError = true;
		simuCtx.setNetType(SimuCtxType.ACLF_NETWORK);
		AclfNetwork aclfNet = CoreObjectFactory.createAclfNetwork();
			
		simuCtx.setAclfNet(aclfNet);
		
		aclfNet.setBaseKva(netBean.baseKva);
		
		for (AclfBusBean busBean : netBean.busList) {
			AclfBus bus = CoreObjectFactory.createAclfBus(busBean.id, aclfNet);
			bus.setBaseVoltage(busBean.baseVoltage);
			if (busBean.genCode != null) {
				if (busBean.genCode==AclfBusBean.GenCode.PQ) {
					bus.setGenCode(AclfGenCode.GEN_PQ);
					AclfPQGenBus pqBus = bus.toPQBus();
					pqBus.setGen(new Complex(busBean.p_gen, busBean.q_gen));
				}
				else if (busBean.genCode==AclfBusBean.GenCode.PV) {
					bus.setGenCode(AclfGenCode.GEN_PV);
					AclfPVGenBus pvBus = bus.toPVBus();
					pvBus.setGenP(busBean.p_gen);
					pvBus.setVoltMag(busBean.v_msg);
				}
				else {
					bus.setGenCode(AclfGenCode.SWING);
					AclfSwingBus swingBus = bus.toSwingBus();
					swingBus.setVoltMag(busBean.v_msg);
					swingBus.setVoltAngDeg(busBean.v_ang);
				}
				
			}
			if (busBean.loadCode != null) {
				bus.setLoadCode(busBean.loadCode==AclfBusBean.LoadCode.ConstP? AclfLoadCode.CONST_P :
					(busBean.loadCode==AclfBusBean.LoadCode.ConstI? AclfLoadCode.CONST_I : AclfLoadCode.CONST_Z));
				bus.setLoadP(busBean.p_load);
				bus.setLoadQ(busBean.q_load);
			}
		}

		for (AclfBranchBean branchBean : netBean.branchList) {
			AclfBranch branch = CoreObjectFactory.createAclfBranch();
			aclfNet.addBranch(branch, branchBean.fromId, branchBean.toId, branchBean.cirId);
			branch.setBranchCode(branchBean.branchCode == AclfBranchBean.BranchCode.Line? AclfBranchCode.LINE :
				(branchBean.branchCode == AclfBranchBean.BranchCode.Xfr? AclfBranchCode.XFORMER : AclfBranchCode.PS_XFORMER));
			branch.setZ(new Complex(branchBean.r, branchBean.x));
			if (branch.getBranchCode() == AclfBranchCode.LINE) {
				branch.setHShuntY(new Complex(0.0, branchBean.b*0.5));
			}
			else {
				AclfXformer xfr = branch.toXfr();
				xfr.setFromTurnRatio(branchBean.fromTurnRatio);
				xfr.setToTurnRatio(branchBean.toTurnRatio);
			}
		}

		return noError;
	}
}