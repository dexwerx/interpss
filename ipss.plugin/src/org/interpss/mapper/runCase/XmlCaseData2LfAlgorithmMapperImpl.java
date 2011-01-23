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

import org.interpss.mapper.AbstractMapping;
import org.interpss.schema.AclfAlgorithmXmlType;
import org.interpss.schema.UnitDataType;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;

public class XmlCaseData2LfAlgorithmMapperImpl extends AbstractMapping<AclfAlgorithmXmlType, LoadflowAlgorithm> {
	public XmlCaseData2LfAlgorithmMapperImpl(IPSSMsgHub msg) {
		super(msg);
	}
	
	/**
	 * Map AclfCaseData to a LoadflowAlgorithm object
	 * 
	 * @param caseData
	 * @param algo
	 */
	@Override
	public boolean map2Model(AclfAlgorithmXmlType xmlAlgo, LoadflowAlgorithm algo) {
		algo.setLfMethod(xmlAlgo.getLfMethod() == AclfAlgorithmXmlType.LfMethod.NR ? AclfMethod.NR
						: (xmlAlgo.getLfMethod() == AclfAlgorithmXmlType.LfMethod.PQ ? AclfMethod.PQ
								: AclfMethod.GS));
		/*
		 * no need for this. PQ method can handle PSXfr now if
		 * (algo.getAclfAdjNetwork().hasPSXfr() && algo.getLfMethod() ==
		 * AclfMethod.PQ) algo.setLfMethod(AclfMethod.NR);
		 */
		algo.setMaxIterations(xmlAlgo.getMaxIterations());
		double factor = 1.0;
		if (xmlAlgo.getToleranceUnit() == UnitDataType.KVA)
			factor = 1.0/algo.getAclfNetwork().getBaseKva();
		else if (xmlAlgo.getToleranceUnit() == UnitDataType.M_VA)
			factor = 1000.0/algo.getAclfNetwork().getBaseKva();
		algo.setTolerance(factor * xmlAlgo.getTolerance());
		algo.setNonDivergent(xmlAlgo.getNonDivergent());
		algo.setInitBusVoltage(xmlAlgo.getInitBusVoltage());
		algo.setGsAccFactor(xmlAlgo.getAccFactor());
		
		return true;
	}
}