/*
 * @(#)TextSimuOutputHandler.java   
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

package org.interpss.dstab.output;

import java.util.Hashtable;

import com.interpss.common.msg.IpssMessage;
import com.interpss.dstab.datatype.DStabSimuAction;
import com.interpss.dstab.util.AbstractSimuOutputHandler;

public class TextSimuOutputHandler extends AbstractSimuOutputHandler {
	public TextSimuOutputHandler() {
	}

	@Override
	public void onMsgEvent(IpssMessage event) {
		// Plot step outout message processing
		DStabSimuAction e = (DStabSimuAction) event;
		if (e.getType() == DStabSimuAction.PlotStepMachineStates) {
			Hashtable<String, Object> machStates = e.getHashtableData();
			try {
				System.out.print(DStabOutFunc.getStateStr(machStates));
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		} else if (e.getType() == DStabSimuAction.PlotStepBusStates) {
			// Hashtable<String, Object> busVariables = e.getHashtableData();
		} else if (e.getType() == DStabSimuAction.PlotStepScriptDynamicBusDeviceStates) {
			// Hashtable<String, Double> scriptVariables = e.getHashtableData();
		}

		// Simulaiton time step outout message processing
		if (e.getType() == DStabSimuAction.TimeStepMachineStates) {
			// Hashtable<String, Object> machStates = e.getHashtableData();
		} else if (e.getType() == DStabSimuAction.TimeStepBusStates) {
			// Hashtable<String, Object> busVariables = e.getHashtableData();
		} else if (e.getType() == DStabSimuAction.TimeStepScriptDynamicBusDeviceStates) {
			// Hashtable<String, Object> scriptVariables = e.getHashtableData();
		}
	}

	@Override
	public boolean onMsgEventStatus(IpssMessage e) {
		onMsgEvent(e);
		return true;
	}
}