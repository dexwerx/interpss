/*
 * @(#)Xml2AlgorithmMapperImpl.java   
 *
 * Copyright (C) 2006-2007 www.interpss.org
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
 * @Date 09/15/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.runCase.dep;

import org.apache.commons.math.complex.Complex;
import org.interpss.xml.IpssXmlHelper;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.interpss.xml.schema.AclfMethodDataType;
import org.interpss.xml.schema.AcscFaultCategoryDataType;
import org.interpss.xml.schema.AcscFaultDataType;
import org.interpss.xml.schema.AcscFaultXmlType;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.BusAcscInitVoltDataType;
import org.interpss.xml.schema.UnitDataType;

import com.interpss.CoreObjectFactory;
import com.interpss.common.datatype.Constants;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBranchFault;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.ScBusVoltageType;
import com.interpss.core.algo.SimpleFaultAlgorithm;

public class Xml2AlgorithmMapperImpl {
	/**
	 * Map RunAclfStudyCaseXmlType object to a LoadflowAlgorithm object.
	 * Modifications defined inside the study case also applied to the
	 * AclfNetwork object
	 * 
	 * @param caseData
	 * @param algo
	 */
	public static void aclfCaseData2AlgoMapping(
			AclfAlgorithmXmlType xmlAlgo, LoadflowAlgorithm algo, IPSSMsgHub msg) {
		algo.setLfMethod(xmlAlgo.getLfMethod() == AclfMethodDataType.NR ? AclfMethod.NR
						: (xmlAlgo.getLfMethod() == AclfMethodDataType.PQ ? AclfMethod.PQ
								: AclfMethod.GS));
		algo.setMaxIterations(xmlAlgo.getMaxIterations());
		double e = xmlAlgo.getTolerance();
		if (xmlAlgo.getToleranceUnit() != null
				&& xmlAlgo.getToleranceUnit() != UnitDataType.PU) {
			byte unit = IpssXmlHelper.mapXmlUnitType2IpssUnitType(xmlAlgo.getToleranceUnit());
			e = UnitType.pConversion(e, algo.getAclfNetwork().getBaseKva(),
					unit, UnitType.PU);
		}
		algo.setTolerance(e);
		algo.setInitBusVoltage(xmlAlgo.isInitBusVoltage());
		algo.setNonDivergent(xmlAlgo.isNonDivergent());
		if (xmlAlgo.getAccFactor() != 0.0
				&& algo.getLfMethod() == AclfMethod.GS)
			algo.setGsAccFactor(xmlAlgo.getAccFactor());
	}

	/**
	 * Map RunAcscStudyCaseXmlType object to a SimpleFaultAlgorithm object.
	 * Modifications defined inside the study case also applied to the
	 * SimpleFaultNetwork object
	 * 
	 * @param acscCase
	 * @param algo
	 */
	public static boolean acscCaseData2AlgoMapping(
			AcscStudyCaseXmlType acscCase, SimpleFaultAlgorithm algo, String faultIdStr, IPSSMsgHub msg) {
		AcscNetwork faultNet = algo.getAcscNetwork();
		if (acscCase.getFaultData().getFaultType() == AcscFaultDataType.BUS_FAULT) {
			AcscBus faultBus = (AcscBus) faultNet.getBus(acscCase.getFaultData().getBusBranchId());
			if (faultBus == null) {
				IpssLogger.getLogger().severe(
						"Programming Error - Fault bus/branch not found");
				return false;
			}

			AcscBusFault fault = CoreObjectFactory.createAcscBusFault(
					Constants.Token_BusFaultId+faultBus.getId(), faultNet);
			acscFaultData2AcscBusFaultMapping(acscCase.getFaultData(), fault);
			algo.addBusFault(faultBus.getId(), faultIdStr, fault);
		} else {
			AcscBranch faultBranch = (AcscBranch) faultNet.getBranch(acscCase.getFaultData().getBusBranchId()
					+ Constants.Token_DefaultBranchCirNoStr);
			if (faultBranch == null) {
				IpssLogger
						.getLogger()
						.severe(
								"Programming Error - Fault bus/branch not found, this maybe a parallel branch issue");
				return false;
			}

			AcscBranchFault fault = CoreObjectFactory
					.createAcscBranchFault(Constants.Token_BranchFaultId+faultBranch.getId(), faultNet);
			acscFaultData2AcscBranchFaultMapping(acscCase.getFaultData(), fault);
			algo.addBranchFault(faultBranch.getId(), faultIdStr, fault);
		}

		if (acscCase.getMultiFactor() != 0.0)
			algo.setMultiFactor(acscCase.getMultiFactor() * 0.01);
		// algo.multiFactor in PU and acscData.getMFactor in %
		if (acscCase.getBusAcscInitVolt() != null)
			algo.setScBusVoltage(acscCase.getBusAcscInitVolt() == BusAcscInitVoltDataType.UNIT_VOLT ? 
								ScBusVoltageType.UNIT_VOLT : ScBusVoltageType.LOADFLOW_VOLT); // UnitV | LFVolt
		return true;
	}

	/**
	 * Acsc bus fault xml data to AcscBusFault object mapping
	 * 
	 * @param data
	 * @param fault
	 */
	public static void acscFaultData2AcscBusFaultMapping(AcscFaultXmlType data,
			AcscBusFault fault) {
		fault
				.setFaultCode(data.getFaultCategory() == AcscFaultCategoryDataType.FAULT_LLG ? SimpleFaultCode.GROUND_LLG
						: (data.getFaultCategory() == AcscFaultCategoryDataType.FAULT_LG ? SimpleFaultCode.GROUND_LG
								: (data.getFaultCategory() == AcscFaultCategoryDataType.FAULT_LL ? SimpleFaultCode.GROUND_LL
										: SimpleFaultCode.GROUND_3P)));
		if (data.getZLG() != null)
			fault.setZLGFault(new Complex(data.getZLG().getRe(), data.getZLG()
					.getIm()));
		if (data.getZLL() != null)
			fault.setZLLFault(new Complex(data.getZLL().getRe(), data.getZLL()
					.getIm()));
	}

	/**
	 * Acsc branch fault xml data to AcscBranchFault object mapping
	 * 
	 * @param data
	 * @param fault
	 */
	public static void acscFaultData2AcscBranchFaultMapping(
			AcscFaultXmlType data, AcscBranchFault fault) {
		acscFaultData2AcscBusFaultMapping(data, fault);
		fault.setDistance(data.getDistance(), UnitType.Percent);
	}
}