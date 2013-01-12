/*
 * @(#) AclfResultBeanMapper.java   
 *
 * Copyright (C) 2008-2013 www.interpss.org
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
import org.interpss.datamodel.bean.aclf.AclfBranchResultBean;
import org.interpss.datamodel.bean.aclf.AclfBusBean;
import org.interpss.datamodel.bean.aclf.AclfNetResultBean;
import org.interpss.datamodel.bean.aclf.MismatchResultBean;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.mapper.AbstractMapper;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.datatype.Mismatch;

/**
 * mapper implementation to map AclfNetwork object to AclfNetResultBean
 * 
 * @author mzhou
 */
public class AclfResultBeanMapper extends AbstractMapper<AclfNetwork, AclfNetResultBean> {
	/**
	 * constructor
	 */
	public AclfResultBeanMapper() {
	}
	
	/**
	 * map into store in the AclfNetBean object into simuCtx object
	 * 
	 * @param netBean AclfNetBean object
	 * @return SimuContext object
	 */
	@Override public AclfNetResultBean map2Model(AclfNetwork aclfNet) throws InterpssException {
		AclfNetResultBean aclfResult = new AclfNetResultBean();

		map2Model(aclfNet, aclfResult);
		
		return aclfResult;
	}	
	
	/**
	 * map the AclfNetBean object into simuCtx object
	 * 
	 * @param netBean an AclfNetBean object, representing a aclf base network
	 * @param simuCtx
	 */
	@Override public boolean map2Model(AclfNetwork aclfNet, AclfNetResultBean aclfResult) {
		boolean noError = true;
		
		aclfResult.base_kva = aclfNet.getBaseKva();
		aclfResult.lf_converge = aclfNet.isLfConverged();
		
		MismatchResultBean misBean = new MismatchResultBean();
		Mismatch mis = aclfNet.maxMismatch(AclfMethod.NR);
		aclfResult.max_mis = misBean;
		misBean.p_err = mis.maxMis.getReal();
		misBean.q_err = mis.maxMis.getImaginary();
		misBean.p_bus_id = mis.maxPBus.getId(); 
		misBean.q_bus_id = mis.maxQBus.getId();
		
		Complex gen = aclfNet.totalGeneration(UnitType.PU);
		Complex load = aclfNet.totalLoad(UnitType.PU);
		Complex loss = aclfNet.totalLoss(UnitType.PU);
		aclfResult.p_gen = format(gen.getReal());
		aclfResult.q_gen = format(gen.getImaginary());
		aclfResult.p_load = format(load.getReal());
		aclfResult.q_load = format(load.getImaginary());
		aclfResult.p_loss = format(loss.getReal());
		aclfResult.q_loss = format(loss.getImaginary());
		
		for (AclfBus bus : aclfNet.getBusList()) {
			AclfBusBean bean = new AclfBusBean();
			aclfResult.bus_list.add(bean);
			mapBusResult(bus, bean);
		}
		
		for (AclfBranch branch : aclfNet.getBranchList()) {
			AclfBranchResultBean bean = new AclfBranchResultBean();
			aclfResult.branch_list.add(bean);
			mapBranchResult(branch, bean);
		}

		return noError;
	}	
	
	private void mapBusResult(AclfBus bus, AclfBusBean bean) {
		bean.id = bus.getId();
		bean.base_v = bus.getBaseVoltage();
		bean.v_mag = format(bus.getVoltageMag());
		bean.v_ang = format(bus.getVoltageAng(UnitType.Deg));

		Complex gen = bus.getGenResults();
		bean.p_gen = format(gen.getReal());
		bean.q_gen = format(gen.getImaginary());

		Complex load = bus.getLoadResults();
		bean.p_load = format(load.getReal());
		bean.q_load = format(load.getImaginary());
	}
	
	private void mapBranchResult(AclfBranch branch, AclfBranchResultBean bean) {
		bean.f_id = branch.getFromBus().getId();
		bean.t_id = branch.getToBus().getId();
		bean.cir_id = branch.getCircuitNumber();
		
		Complex flow = branch.powerFrom2To();
		bean.p_f2t = format(flow.getReal());
		bean.q_f2t = format(flow.getImaginary());

		flow = branch.powerTo2From();
		bean.p_t2f = format(flow.getReal());
		bean.q_t2f = format(flow.getImaginary());
		
		Complex loss = branch.loss();
		bean.p_loss = format(loss.getReal());
		bean.q_loss = format(loss.getImaginary());
		
		bean.cur = format2(branch.current(UnitType.Amp));
	}	
	
	private double format(double x) {
		return new Double(Number2String.toStr(x)).doubleValue();
	}

	private double format2(double x) {
		return new Double(Number2String.toStr(x, "#0.0#")).doubleValue();
	}
}