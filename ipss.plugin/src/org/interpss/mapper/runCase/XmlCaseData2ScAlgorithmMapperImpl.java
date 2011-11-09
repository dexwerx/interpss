/*
 * @(#)XmlCaseData2AlgorithmMapperImpl.java   
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

package org.interpss.mapper.runCase;

import org.interpss.xml.schema.AcscFaultCategoryDataType;
import org.interpss.xml.schema.AcscFaultDataType;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.BusAcscInitVoltDataType;

import com.interpss.CoreObjectFactory;
import com.interpss.common.datatype.Constants;
import com.interpss.common.mapper.AbstractMapping;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.NetUtilFunc;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBranchFault;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.ScBusVoltageType;
import com.interpss.core.algo.SimpleFaultAlgorithm;

public class XmlCaseData2ScAlgorithmMapperImpl extends AbstractMapping<AcscStudyCaseXmlType, SimpleFaultAlgorithm> {
	public XmlCaseData2ScAlgorithmMapperImpl(IPSSMsgHub msg) {
	}
	
	/**
	 * Map AcscCaseData to a SimpleFaultAlgorithm object, Fault data will be
	 * mapped into the SimpleFaultNetwork object
	 * 
	 * @param xmlCaseData
	 * @param algo
	 */
	@Override
	public boolean map2Model(AcscStudyCaseXmlType xmlCaseData, SimpleFaultAlgorithm algo) {
		AcscNetwork faultNet = algo.getAcscNetwork();
		String faultIdStr = algo.getDesc();
		if (xmlCaseData.getFaultData().getFaultType() == AcscFaultDataType.BUS_FAULT) {
			String id  = NetUtilFunc.getBusIdFromDisplayNameId(xmlCaseData.getFaultData().getBusBranchId());
			AcscBus faultBus = (AcscBus) faultNet.getBus(id);
			if (faultBus == null) {
				IpssLogger.getLogger().severe("Programming Error - Fault bus/branch not found, id: " + id);
				return false;
			}

			AcscBusFault fault = CoreObjectFactory.createAcscBusFault(
					Constants.Token_BusFaultId + faultBus.getId(), faultNet);
			RunCaseMapperHelper.acscFaultData2AcscBusFaultMapping(xmlCaseData.getFaultData(), fault);
			if (xmlCaseData.getFaultData().getFaultCategory() == AcscFaultCategoryDataType.FAULT_ALL) {
				addAllFaultCategory(faultBus.getId(), faultIdStr, fault, algo);
			} 
			else
				algo.addBusFault(faultBus.getId(), faultIdStr, fault);
		} 
		else if (xmlCaseData.getFaultData().getFaultType() == AcscFaultDataType.BRANCH_FAULT) {
			String id  = NetUtilFunc.getBranchIdFromDisplayNameId(xmlCaseData.getFaultData().getBusBranchId());
			AcscBranch faultBranch = (AcscBranch) faultNet.getBranch(id);
			if (faultBranch == null) {
				IpssLogger.getLogger().severe(
								"Programming Error - Fault bus/branch not found, this maybe a parallel branch issue, id: " + id);
				return false;
			}

			AcscBranchFault fault = CoreObjectFactory.createAcscBranchFault(
					Constants.Token_BranchFaultId+faultBranch.getId(), faultNet);
			RunCaseMapperHelper.acscFaultData2AcscBranchFaultMapping(xmlCaseData.getFaultData(), fault);
			if (xmlCaseData.getFaultData().getFaultCategory() == AcscFaultCategoryDataType.FAULT_ALL) {
				addAllFaultCategory(faultBranch.getId(), faultIdStr, fault,
						algo);
			} else
				algo.addBranchFault(faultBranch.getId(), faultIdStr, fault);
		} 
		else {
			IpssLogger.getLogger().severe(
					"Programming Error - Branch outage not implemented");
			return false;
		}

		algo.setMultiFactor(xmlCaseData.getMultiFactor() == null?
				1.0 : xmlCaseData.getMultiFactor() * 0.01);
		// algo.multiFactor in PU and acscData.getMFactor in %
		algo.setScBusVoltage(xmlCaseData.getBusAcscInitVolt() == BusAcscInitVoltDataType.UNIT_VOLT? 
				ScBusVoltageType.UNIT_VOLT : ScBusVoltageType.LOADFLOW_VOLT); // UnitV | LFVolt
		return true;
	}

	private static void addAllFaultCategory(String busId, String faultIdStr,
			AcscBusFault fault, SimpleFaultAlgorithm faultAlgo) {
		String id = fault.getId();

		fault.setId(id + "_3P");
		fault.setFaultCode(SimpleFaultCode.GROUND_3P);
		faultAlgo.addBusFault(busId, faultIdStr, fault);

		fault.setId(id + "_LG");
		fault.setFaultCode(SimpleFaultCode.GROUND_LG);
		faultAlgo.addBusFault(busId, faultIdStr, fault);

		fault.setId(id + "_LLG");
		fault.setFaultCode(SimpleFaultCode.GROUND_LLG);
		faultAlgo.addBusFault(busId, faultIdStr, fault);

		fault.setId(id + "_LL");
		fault.setFaultCode(SimpleFaultCode.GROUND_LL);
		faultAlgo.addBusFault(busId, faultIdStr, fault);
	}
}