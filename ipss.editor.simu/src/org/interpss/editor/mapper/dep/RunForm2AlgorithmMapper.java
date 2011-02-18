/*
 * @(#)RunForm2AlgorithmMapper.java   
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

package org.interpss.editor.mapper.dep;

import org.interpss.editor.runAct.ui.AclfRunForm;
import org.interpss.editor.runAct.ui.AcscRunForm;
import org.interpss.editor.runAct.ui.DStabRunForm;
import org.interpss.mapper.dep.AbstractMapper;
import org.interpss.mapper.runCase.dep.XmlCaseData2AlgorithmMapperImpl;

import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;

public class RunForm2AlgorithmMapper extends AbstractMapper {

	public RunForm2AlgorithmMapper() {
	}

	/**
	 * map info store in the fromObj to the toObj
	 * 
	 * @param fromObj the fromObj object
	 * @param toObj the to object
	 * @param kclass class type, used to determine mapping details 
	 */
	public boolean mapping(Object fromObj, Object toObj, Class<?> klass) {
		if (klass == LoadflowAlgorithm.class) {
			/*
			 * map AclfRunForm object to an AclfAlgo object
			 */
			AclfRunForm runForm = (AclfRunForm) fromObj;
			LoadflowAlgorithm algo = (LoadflowAlgorithm) toObj;
			XmlCaseData2AlgorithmMapperImpl.aclfCaseData2AlgoMapping(runForm.getAclfCaseData().getAclfAlgorithm(), algo);
		} else if (klass == SimpleFaultAlgorithm.class) {
			/*
			 * map AcscRunForm object to a SimpleFaultAlgorithm object
			 */
			AcscRunForm runForm = (AcscRunForm) fromObj;
			SimpleFaultAlgorithm algo = (SimpleFaultAlgorithm) toObj;
			return XmlCaseData2AlgorithmMapperImpl.acscCaseData2AlgoMapping(
					runForm.getXmlCaseData(), algo);
		} else if (klass == DynamicSimuAlgorithm.class) {
			/*
			 * map DStabRunForm object to a DynamicSimuAlgorithm object
			 */
			DStabRunForm runForm = (DStabRunForm) fromObj;
			DynamicSimuAlgorithm algo = (DynamicSimuAlgorithm) toObj;
			return XmlCaseData2AlgorithmMapperImpl.dstabCaseData2AlgoMapping(runForm.getXmlCaseData(), algo, msg);
		} 
		return true;
	}
}
