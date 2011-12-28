/*
 * @(#)ScriptSimuOutputHandler.java   
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
 * @Date 05/01/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.dstab.script;

import java.util.Hashtable;

import javax.swing.JTextArea;

import org.interpss.editor.ui.util.CoreScriptUtilFunc;
import org.interpss.editor.ui.util.GUIFileUtil;
import org.interpss.util.MemoryJavaCompiler;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.datatype.DStabSimuAction;
import com.interpss.dstab.util.AbstractSimuOutputHandler;
import com.interpss.spring.CoreCommonSpringFactory;

public class ScriptSimuOutputHandler extends AbstractSimuOutputHandler {
	private IPSSMsgHub msg = null;
	private DStabilityNetwork net = null;
	private AnnotateDStabOutputScripting anOutput = null;

	public ScriptSimuOutputHandler() {
	}

	@Override
	public boolean init(String scriptFilename, DStabilityNetwork net) {
		this.msg = CoreCommonSpringFactory.getIpssMsgHub();
		this.net = net;

		JTextArea textarea = new JTextArea();
		GUIFileUtil.readFile2TextareaAbsolutePath(scriptFilename, textarea);
		// System.out.println(textarea.getText());

		// get the javacode, compile and create the annotated object
		String javacode = textarea.getText();
		javacode = javacode.replaceFirst(
				CoreScriptUtilFunc.Tag_DStabOutScriptDescBegin,
				CoreScriptUtilFunc.Tag_DStabOutScriptDescBegin_Code);
		javacode = javacode.replaceFirst(
				CoreScriptUtilFunc.Tag_DStabOutScriptDescEnd,
				CoreScriptUtilFunc.Tag_DStabOutScriptDescEnd_Code);
		// System.out.println(javacode);
		try {
			IDStabOutputScripting obj = (IDStabOutputScripting) MemoryJavaCompiler
					.javac(CoreScriptUtilFunc.DStabOutputScriptingClassName,
							javacode);
			this.anOutput = new AnnotateDStabOutputScripting(obj);
		} catch (Exception e) {
			IpssLogger.logErr(e);
			return false;
		}

		try {
			this.anOutput.init(net);
		} catch (Exception e) {
			msg
					.sendErrorMsg("Error in the ScriptSimuOutputHandler.init() section: "
							+ e.toString());
			return false;
		}
		return true;
	}

	@Override
	public boolean onMsgEventStatus(IpssMessage event) {
		DStabSimuAction e = (DStabSimuAction) event;
		if (e.getType() == DStabSimuAction.TimeStepMachineStates) {
			Hashtable<String, Object> machStates = e.getHashtableData();
			if (!this.anOutput.machStates(net, machStates))
				return false;
		}

		if (e.getType() == DStabSimuAction.TimeStepBusStates) {
			Hashtable<String, Object> busStates = e.getHashtableData();
			if (!this.anOutput.busVariables(net, busStates))
				return false;
		}

		if (e.getType() == DStabSimuAction.TimeStepScriptDynamicBusDeviceStates) {
			Hashtable<String, Object> busDeviceStates = e.getHashtableData();
			if (!this.anOutput.busDeviceStates(net, busDeviceStates))
				return false;
		}

		if (e.getType() == DStabSimuAction.EndOfSimuStep) {
			try {
				this.anOutput.endOfSimuStep();
			} catch (Exception ex) {
				msg.sendErrorMsg("Error in processEndOfSimuStep section, "
						+ ex.toString());
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean close() {
		try {
			this.anOutput.close();
		} catch (Exception ex) {
			msg.sendErrorMsg("ScriptSimuOutputHandler.close(), "
					+ ex.toString());
			return false;
		}
		return true;
	}
}