/*
 * @(#)BaseFormDataMapperImpl.java   
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
 * @Date 09/15/2006
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.editor;

import org.interpss.editor.form.base.BaseBranchForm;
import org.interpss.editor.form.base.BaseBusForm;
import org.interpss.editor.form.base.BaseNetForm;
import org.interpss.numeric.datatype.Unit;

import com.interpss.CoreObjectFactory;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.net.Area;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;
import com.interpss.core.net.Zone;

/**
 * Map functions for BaseNetForm, BaseBusForm, BaseBranchForm to/from Network, Bus, Branch simu objects
 */

public class BaseFormDataMapperImpl {
	/**
	 * Set the BaseNetForm object info to the Network object
	 * 
	 * @param formNet the BaseNetForm object
	 * @param net the Network object
	 */
	public static void setBaseNetInfo(BaseNetForm formNet, Network net) {
		IpssLogger.getLogger().info(
				"BaseFormDataMapperImpl.setBaseNetInfo() called");
		net.setId(formNet.getId());
		net.setDesc(formNet.getDescription());
		net.setBaseKva(formNet.getBaseKVA());
		net.setFrequency(formNet.getFreqHZ());
		net.setAllowParallelBranch(formNet.isAllowParallelBranch());
		net.setCheckElementDuplication(formNet.isCheckBusDuplication());
	}

	/**
	 * Set the BaseBusForm object info to the Bus object
	 * 
	 * @param formBus the BaseBusForm object
	 * @param bus the Bus object
	 * @param net the Network object contains the bus
	 */
	public static void setBaseBusInfo(BaseBusForm formBus, Bus bus, Network net) {
		bus.setId(formBus.getId());
		bus.setName(formBus.getName());
		bus.setStatus(formBus.getStatus());
		Area area = CoreObjectFactory.createArea(formBus.getArea(), net);
		Zone zone = CoreObjectFactory.createZone(formBus.getZone(), net);
		bus.setArea(area);
		bus.setZone(zone);
		bus.setBaseVoltage(formBus.getBaseVoltage(), Unit.toUnit(formBus
				.getBaseVoltUnit()));
	}

	/**
	 * Set the BaseBranchForm object info to the Branch object
	 * 
	 * @param formBranch the BaseBranchForm object
	 * @param branch the Branch object
	 * @param net the Network object contains the bus
	 */
	public static void setBaseBranchInfo(BaseBranchForm formBranch,
			Branch branch, Network net) {
		branch.setName(formBranch.getName());
		branch.setStatus(formBranch.getStatus());
		Area area = CoreObjectFactory.createArea(formBranch.getArea(), net);
		Zone zone = CoreObjectFactory.createZone(formBranch.getZone(), net);
		branch.setArea(area);
		branch.setZone(zone);
	}
}