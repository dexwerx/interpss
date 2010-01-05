/*
 * @(#)PreventiveRuleHanlder.java   
 *
 * Copyright (C) 2006-2008 www.interpss.org
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
 * @Date 04/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.xml;

import java.util.ArrayList;
import java.util.List;

import org.interpss.schema.RuleBaseXmlType;
import org.interpss.schema.ViolationConditionXmlType;
import org.interpss.schema.PreventiveRuleSetXmlType;
import org.interpss.schema.ViolationConditionXmlType.ConditionType;
import org.interpss.schema.ViolationConditionXmlType.BranchConditionSet.BranchCondition;
import org.interpss.schema.ViolationConditionXmlType.BusConditionSet.BusCondition;
import org.interpss.schema.PreventiveRuleSetXmlType.PreventiveRuleList.PreventiveRule;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.core.algorithm.ViolationType;
import com.interpss.core.net.Network;

public class PreventiveRuleHanlder {
	/**
	 * Apply the rule set to AclfNetwork. If there is any rule set action, AC Loadflow will be re-run
	 * 
	 * @param algo
	 * @param ruleBase
	 * @param vMaxPU
	 * @param vMinPU
	 * @param msg
	 * @return a list of msg for violation checking and protection rule application
	 */
	public static List<Object> applyRuleSet2AclfNet(LoadflowAlgorithm algo, RuleBaseXmlType ruleBase, 
					double vMaxPU, double vMinPU, IPSSMsgHub msg) {
		int max = getUpperPriority(ruleBase);
		List<Object> msgList = new ArrayList<Object>();
		for (int i = 1; i <= max; i++) {
			if (algo.violation(ViolationType.ALL, vMaxPU, vMinPU, msgList))
				if (applyRuleSet(algo.getAclfAdjNetwork(), ruleBase, i, vMaxPU, vMinPU, msg)) {
					IpssLogger.getLogger().info("Applied rule set with priority = " + i);
					msgList.add("Applied rule set with priority = " + i);
					// re-run Loadflow after applying the rule set action
					algo.loadflow();
				}
		}
		return msgList;
	}
	
	/**
	 * Apply the protection rule set with the priority to the network object. 
	 * 
	 * @param net a Network/AclfNetwork/AclfAdjNetwork/... object to be modified
	 * @param aclfRuleBase aclf rule base
	 * @param priority protection rule priority to be applied. Only rule.priority = priority will be applied
	 * @param vMaxPU upper voltage limit for voltage violation check
	 * @param vMinPU lower voltage limit for voltage violation check
	 * @param msg the IPSS Msg object
	 * @return true if rules applied and changes are made
	 */
	public static boolean applyRuleSet(Network net, RuleBaseXmlType aclfRuleBase, 
					int priority, double vMaxPU, double vMinPU, IPSSMsgHub msg) {
		boolean rtn = false;
		for (PreventiveRuleSetXmlType ruleSet : aclfRuleBase.getPreventiveRuleSetList().getPreventiveRuleSetArray()) {
			if (ruleSet.getPriority() == priority) {
				for (PreventiveRule rule : ruleSet.getPreventiveRuleList().getPreventiveRuleArray()) {
					if (rule.getCondition().getBusConditionSetArray().length == 0 &&
						rule.getCondition().getBranchConditionSetArray().length == 0)
						return false;
					
					// evaluate Aclf net conditions
					boolean busAclfCond = false;
					boolean braAclfCond = false;
					if (rule.getCondition().getConditionType() == ConditionType.AND) {
						// for AND type, all condition has to be true. There should be at least
						// one condition to evaluate, so initially set the following
						busAclfCond = true;
						braAclfCond = true;
					}
						
					AclfNetwork aclfNet = (AclfNetwork)net;
					if (rule.getCondition().getBusConditionSetArray().length > 0)
						busAclfCond = evlAclfNetBusCondition(rule.getCondition(), aclfNet, vMaxPU, vMinPU, msg);
					if (rule.getCondition().getBranchConditionSetArray().length > 0)
						braAclfCond = evlAclfNetBranchCondition(rule.getCondition(), aclfNet, msg);

					// evaluate Other net conditions for future use
					boolean busOtherCond = false;
					boolean braOtherCond = false;
					if (rule.getCondition().getConditionType() == ConditionType.AND) {
						busOtherCond = true;
						braOtherCond = true;
					}

					if (rule.getCondition().getConditionType() == ConditionType.AND &&
							(busAclfCond && braAclfCond && busOtherCond && braOtherCond ) ||
						rule.getCondition().getConditionType() == ConditionType.OR &&
							(busAclfCond || braAclfCond || busOtherCond || braOtherCond)) {
						if (rule.getBusAction() != null) {
							msg.sendInfoMsg("Protective/Preventive action condition, Apply bus change action to net " + net.getId());
							if(XmlNetParamModifier.applyBusChange(rule.getBusAction(), net, msg))
								rtn = true;
						}
						if (rule.getBranchAction() != null) {
							msg.sendInfoMsg("Protective/Preventive action condition, Apply branch change action to net " + net.getId());
							if(XmlNetParamModifier.applyBranchChange(rule.getBranchAction(), net, msg))
								rtn = true;
						}
					}
				}
			}
		}
		return rtn;
	}
	
	/**
	 * Evaluate Aclf Net bus condition for protection/preventive actions, including bus voltage violation
	 * 
	 * @param cond
	 * @param net
	 * @param vMaxPU
	 * @param vMinPU
	 * @param msg
	 * @return
	 */
	public static boolean evlAclfNetBusCondition(ViolationConditionXmlType cond, AclfNetwork net, double vMaxPU, double vMinPU, IPSSMsgHub msg) {
		boolean evalCond = false;
		for (ViolationConditionXmlType.BusConditionSet busCond : cond.getBusConditionSetArray()) {
			AclfBus bus = (AclfBus)IpssXmlUtilFunc.getBus(busCond, net);
			if (bus == null) {
				msg.sendErrorMsg("Error: cannot fin bus, id: " + busCond.getRecId());
				return false;
			}
			
			if (busCond.getBusCondition() == BusCondition.LOWER_VOLTAGE_LIMIT_VIOLATION) {
				evalCond = bus.getVoltageMag() < vMinPU;
				if (evalCond)
					msg.sendInfoMsg("Protection condition, Lower voltage limit violation at bus " + busCond.getRecId());
			}
			else if (busCond.getBusCondition() == BusCondition.UPPER_VOLTAGE_LIMIT_VIOLATION) {
				evalCond = bus.getVoltageMag() > vMaxPU;
				if (evalCond)
					msg.sendInfoMsg("Protection condition, upper voltage limit violation at bus " + busCond.getRecId());
			}
			if (cond.getConditionType() == ViolationConditionXmlType.ConditionType.AND && evalCond == false)
				return false;
			else if (evalCond)
				return true;
		}
		return false;
	}

	/**
	 * Evaluate Aclf Network branch condition for protective/preventive action. Condition include MVA rating and
	 * AMPS violation
	 * 
	 * @param cond
	 * @param net
	 * @param msg
	 * @return
	 */
	public static boolean evlAclfNetBranchCondition(ViolationConditionXmlType cond, AclfNetwork net, IPSSMsgHub msg) {
		boolean evalCond = false;
		for (ViolationConditionXmlType.BranchConditionSet braCond : cond.getBranchConditionSetArray()) {
			AclfBranch branch = (AclfBranch)IpssXmlUtilFunc.getBranch(braCond, net);
			if (branch == null) {
				msg.sendErrorMsg("Error: cannot fin branch, " + braCond.getFromBusId() + "->" + braCond.getToBusId());
				return false;
			}

			double amps = branch.current(UnitType.Amp, net.getBaseKva());
			double mva = branch.mvaFlow(UnitType.mVA, net.getBaseKva());
			if (braCond.getBranchCondition() == BranchCondition.RATING_MVA_1_VIOLATION) {
				evalCond = mva > branch.getRatingMva1();
				if (evalCond)
					msg.sendInfoMsg("Protection condition, RatingMva1 violation at branch, " + braCond.getFromBusId() + "->" + braCond.getToBusId());
			}
			else if (braCond.getBranchCondition() == BranchCondition.RATING_MVA_2_VIOLATION) {
				evalCond = mva > branch.getRatingMva2();
				if (evalCond)
					msg.sendInfoMsg("Protection condition, RatingMva2 violation at branch, " + braCond.getFromBusId() + "->" + braCond.getToBusId());
			}
			else if (braCond.getBranchCondition() == BranchCondition.RATING_MVA_3_VIOLATION) {
				evalCond = mva > branch.getRatingMva3();
				if (evalCond)
					msg.sendInfoMsg("Protection condition, RatingMva3 violation at branch, " + braCond.getFromBusId() + "->" + braCond.getToBusId());
			}
			else if (braCond.getBranchCondition() == BranchCondition.RATING_AMPS_VIOLATION) {
				evalCond = amps > branch.getRatingAmps();
				if (evalCond)
					msg.sendInfoMsg("Protection condition, RatingAmps violation at branch, " + braCond.getFromBusId() + "->" + braCond.getToBusId());
			}
			
			if (cond.getConditionType() == ViolationConditionXmlType.ConditionType.AND && evalCond == false)
				return false;
			else if (evalCond)
				return true;
		}
		return false;
	}
	
	/**
	 * Find the largest priority number. 
	 * 
	 * @param aclfRuleBase
	 * @return
	 */
	public static int getUpperPriority(RuleBaseXmlType aclfRuleBase) {
		int p = 1;   // priority starts from 1, max 10
		for (PreventiveRuleSetXmlType ruleSet : aclfRuleBase.getPreventiveRuleSetList().getPreventiveRuleSetArray()) {
			if (ruleSet.getPriority() > p)
				p = ruleSet.getPriority();
		}
		return p;
	}	
}