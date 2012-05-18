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

package org.interpss.mapper.runCase.dep;

import org.interpss.mapper.dep.AbstractMapper;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.DStabStudyCaseXmlType;
import org.interpss.xml.schema.ModificationXmlType;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;

public class IpssXmlMapper extends AbstractMapper {

	public IpssXmlMapper() {
	}

	public IpssXmlMapper(IPSSMsgHub newMsg) {
		this.msg = newMsg;
	}

	/**
	 * map info store in the fromObj to the toObj
	 * 
	 * @param fromObj the fromObj object
	 * @param toObj the to object
	 * @param kclass class type, used to determine mapping details 
	 */
	@Override
	public <T> boolean mapping(Object fromObj, T toObj) {
		if (fromObj instanceof AclfAlgorithmXmlType) {
			// map an AclfAlgorithmXmlType xml record to an LoadflowAlgorithm object
			Xml2AlgorithmMapperImpl.aclfCaseData2AlgoMapping(
					(AclfAlgorithmXmlType) fromObj,
					(LoadflowAlgorithm) toObj, msg);
		} else if (fromObj instanceof AcscStudyCaseXmlType) {
			/*
			 * map an AcscStudyCase xml record to an LoadflowAlgorithm object
			 */
			AcscStudyCaseXmlType scase = (AcscStudyCaseXmlType)fromObj;
			String faultIdStr = scase.getRecId();
			return Xml2AlgorithmMapperImpl.acscCaseData2AlgoMapping(
					scase, (SimpleFaultAlgorithm) toObj, 
					faultIdStr, msg);
		} else if (fromObj instanceof DStabStudyCaseXmlType) {
			/*
			 * map a DStabStudyCase xml record (fromObj) to an LoadflowAlgorithm object (toObj)
			 */
			return Xml2DStabAlgorithmMapperImpl.dstabCaseData2AlgoMapping(
					(DStabStudyCaseXmlType) fromObj,
					(DynamicSimuAlgorithm) toObj, msg);

		} else if (fromObj instanceof ModificationXmlType) {
			/*
			 * Apply the modification (fromObj) info to the Network object (toObj) 
			 */ 
			//XmlNetParamModifier.applyModification(
			//	(Network) toObj, 
			//	(ModificationXmlType) fromObj, msg);
		}
		return true;
	}
}
