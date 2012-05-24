/*
 * @(#)XmlScriptUtilFunc.java   
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
 * @Date 01/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.editor.runAct.xml;

import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.RuleBaseXmlType;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.mapper.RuleBase2ModelMapper;
import com.interpss.simu.multicase.MultiStudyCase;

public class XmlScriptUtilFunc {
	public static void mapRuleBase(boolean applyRuleBase, MultiStudyCase mscase, RuleBaseXmlType ruleBase) {
		mscase.getRuleBase().setApplyRuleBase(applyRuleBase);
		if (ruleBase != null)
			try {
			mscase.setRuleBase(new RuleBase2ModelMapper().map2Model(ruleBase));
			} catch (InterpssException e) {
				IpssLogger.getLogger().severe(e.toString());
			}
	}
	
	public static boolean mapAclfStudyCase(AclfStudyCaseXmlType xmlCase, 
			LoadflowAlgorithm algo, AclfAlgorithmXmlType xmlDefaultAlgo, 
						boolean remoteJobCreation) {
		if (xmlCase.getAclfAlgorithm() == null) {
			if (xmlDefaultAlgo == null) {
				IpssLogger.getLogger().severe("No Aclf Algorithm defined");
				return false;
			}
			xmlCase.setAclfAlgorithm(xmlDefaultAlgo);
		}
		if (xmlCase.getModification() != null && !remoteJobCreation)
			EditorPluginSpringFactory.getModXml2NetMapper()
				.map2Model(xmlCase.getModification(), algo.getAclfNetwork());
		
		EditorPluginSpringFactory.getXml2LfAlgorithmMapper()
				.map2Model(xmlCase.getAclfAlgorithm(), algo);
		
		return true;
	}
}
