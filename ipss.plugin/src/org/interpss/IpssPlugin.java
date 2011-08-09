/*
 * @(#)IpssPlugin.java   
 *
 * Copyright (C) 2006-2010 www.interpss.org
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
 * @Date 12/04/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss;

import java.util.logging.Level;

import org.ieee.odm.common.ODMLogger;
import org.interpss.numeric.sparse.base.SparseEquation;
import org.interpss.spring.NumericSpringCtx;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.algo.BusNumberArrangeRule;
import com.interpss.spring.CoreCommonSpringCtx;

public class IpssPlugin {
	public static void init() {
		init(Level.WARNING);
	}
	
	public static void init(Level level) {
		setSpringAppCtx();
		setLoggerLevel(level);
		setSparseEqnSolver(SparseEquation.SolverType.Default);
	}

	public static void setSparseEqnSolver(SparseEquation.SolverType solver) {
		if (solver == SparseEquation.SolverType.Default ) {
			CoreObjectFactory.DefaultBusArrangeRule = BusNumberArrangeRule.TINNEY2;
			NumericSpringCtx.setDefualtSparseEqnSolver();
		}
		else if (solver == SparseEquation.SolverType.Native ) {
			CoreObjectFactory.DefaultBusArrangeRule = BusNumberArrangeRule.TINNEY0;
			NumericSpringCtx.setNativeSparseEqnSolver();
		}
	}
	
	public static IPSSMsgHub getMsgHub() {
		return CoreCommonSpringCtx.getIpssMsgHub();
	}
	
	public static void setLoggerLevel(Level level) {
		IpssLogger.getLogger().setLevel(level);
		ODMLogger.getLogger().setLevel(level);
	}	

	private static void setSpringAppCtx() {
		if (CoreCommonSpringCtx.SpringAppCtx == null) {
			CoreCommonSpringCtx.SpringAppCtx = new ClassPathXmlApplicationContext(
					new String[] {
							"org/interpss/spring/PluginSpringCtx.xml"});
		}
	}	
}
