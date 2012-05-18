/*
 * @(#)BaseCMLDynamicBusDevice.java   
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

package org.interpss.dstab.device;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import java.util.Hashtable;

import org.interpss.editor.ui.util.CoreScriptUtilFunc;
import org.interpss.editor.ui.util.ScriptJavacUtilFunc;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.IScriptPluginEditing;
import org.interpss.util.MemoryJavaCompiler;

import com.interpss.common.datatype.ScriptLangEnum;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.net.Network;
import com.interpss.dstab.DStabBus;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.device.ScriptDynamicBusDevice;
import com.interpss.dstab.device.impl.ScriptDynamicBusDeviceImpl;

/**
 * This is place holder for a ScriptDynamicBusDevice implementation. The work
 * will be delegated to the device object to perform. The original java code is
 * stored in the object. the code will be compiled to create the device object.
 * 
 * @author mzhou
 * 
 */
public class ScriptDynamicBusDeviceHolder extends ScriptDynamicBusDeviceImpl {
	private ScriptDynamicBusDevice device = null;
	private String pluginName = "";
	private String pluginDataXmlStr = "";

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public void setPluginDataXmlStr(String pluginDataXmlStr) {
		this.pluginDataXmlStr = pluginDataXmlStr;
	}

	/**
	 * Generate Java code, compile code, load compile class and then delegate
	 * init to the created device object
	 * 
	 * @param abus
	 *            the bus object
	 * @param net
	 *            the network object
	 * @param msg
	 *            the MessageHub object
	 * @return false if there is anything wrong
	 */
	@Override
	public boolean initStates(DStabBus abus, Network net) {
		super.initStates(abus);

		createDeviceObject();
		if (device != null) {
			if (abus.getNetwork() == null)
				abus.setNetwork(net);
			return device.initStates(abus);
		}
		else {
			ipssLogger.severe("ScriptDynamicBusDevice create error, device == null");
			return false;
		}
	}

	/**
	 * Solve a step of ODE of the device object
	 * 
	 * @param dt
	 *            time step
	 * @param method
	 *            ODE solution method
	 * @param abus
	 *            the bus object
	 * @param net
	 *            the network object
	 * @param msg
	 *            the MessageHub object
	 * @return false if there is anything wrong
	 */
	public boolean nextStep(double dt, DynamicSimuMethod method, Network net,
			IPSSMsgHub msg) {
		return device.nextStep(dt, method);
	}

	/**
	 * Get the device output object, normally the inject cuerrent into the
	 * network
	 * 
	 * @param abus
	 *            the bus object
	 */
	@Override
	public Object getOutputObject() {
		return device.getOutputObject();
	}

	/**
	 * Get the device states
	 * 
	 * @param abus
	 *            the bus object
	 * @refMach the ref machine object
	 */
	@Override
	public Hashtable<String, Object> getStates(Object refMach) {
		return device.getStates(refMach);
	}

	/**
	 * update device attributes
	 * 
	 * @param abus
	 *            the bus object
	 * @param netChange
	 *            if there is any network change event
	 * @return false if there is any problem
	 */
	@Override
	public boolean updateAttributes(boolean netChange) {
		return device.updateAttributes(netChange);
	}

	private void createDeviceObject() {
		if (getScriptLang() == ScriptLangEnum.Java) {
			String classname = ScriptJavacUtilFunc
					.createScriptingClassname(getId());
			String javacode = getScripts().replaceFirst(
					ScriptJavacUtilFunc.Tag_Classname, classname);
			try {
				device = (ScriptDynamicBusDevice) MemoryJavaCompiler
						.javac(
								CoreScriptUtilFunc.ScriptDynamicBusControllerPackageName
										+ classname, javacode);
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}
		} else {
			ipssLogger.info(
					"Create custom plugin: " + this.pluginName + " with data: "
							+ this.pluginDataXmlStr);
			device = (ScriptDynamicBusDevice) UISpringFactory
					.getCustomDynamicBusDeviceScriptPlugin(this.pluginName);
			((IScriptPluginEditing) device).setData(this.pluginDataXmlStr);
		}
	}
}
