/*
 * @(#)AclfScenarioHelper.java   
 *
 * Copyright (C) 2010 www.interpss.org
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
 * @Date 09/15/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.odm.impl.aclf;

import org.ieee.odm.schema.AclfAlgorithmXmlType;
import org.ieee.odm.schema.ApparentPowerXmlType;
import org.ieee.odm.schema.LfMethodEnumType;

import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;

public class AclfScenarioHelper {
	LoadflowAlgorithm aclfAlgo = null;
	
	public AclfScenarioHelper(LoadflowAlgorithm aclfAlgo) {
		this.aclfAlgo = aclfAlgo;
	}

	public LoadflowAlgorithm getAclfAlgo() {
		return this.aclfAlgo;
	}

	public void mapAclfAlgorithm(AclfAlgorithmXmlType lfInit){
		
		// set lf method
		LfMethodEnumType lfMethod = lfInit.getLfMethod();
		if(lfMethod == LfMethodEnumType.PQ){
			aclfAlgo.setLfMethod(AclfMethod.PQ);			
		}else if(lfMethod == LfMethodEnumType.GS){
			aclfAlgo.setLfMethod(AclfMethod.GS);			
		}
		else 
			aclfAlgo.setLfMethod(AclfMethod.NR);			

		int maxInt =lfInit.getMaxIterations();
		aclfAlgo.setMaxIterations(maxInt);
		ApparentPowerXmlType tol = lfInit.getTolerance();
		aclfAlgo.setTolerance(tol.getValue());
		aclfAlgo.setInitBusVoltage(lfInit.isInitBusVoltage());
		
		if(lfInit.getAccFactor()!=null){
			aclfAlgo.setGsAccFactor(lfInit.getAccFactor());
		}
		
		if(lfInit.isNonDivergent()!=null){
			aclfAlgo.setNonDivergent(lfInit.isNonDivergent());
		}	
	}
}