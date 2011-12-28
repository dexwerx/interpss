 /*
  * @(#)ScriptingStabilizer.java   
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

package org.interpss.dstab.control.cml.ui;


import com.interpss.dstab.DStabBus;
import com.interpss.dstab.mach.Machine;
import com.interpss.dstab.mach.MachineControllerType;

public class CMLScriptingStabilizer extends AbstractCMLScriptingController {
	public static String BaseClass = "AnnotateStabilizer";

	// define UI Editor panel for editing the controller data
	private static final NBControllerCMLScriptsEditPanel _editPanel = new NBControllerCMLScriptsEditPanel();


	/**
	 * Constructor
	 */
	public CMLScriptingStabilizer() {
		super("pssId", "pssName", "pssCaty", MachineControllerType.STABILIZER);
	}
	
	/**
	 * Constructor
	 * 
	 * @param id stabilizer id
	 * @param name stabilizer name
	 */
	public CMLScriptingStabilizer(final String id, final String name, final String caty) {
		super(id, name, caty, MachineControllerType.STABILIZER);
	}
	
	/**
	 *  Init the controller states
	 *  
	 *  @param msg the SessionMsg object
	 */
	@Override
	public boolean initStates(DStabBus abus, Machine mach) {
		setId(mach.getId() + "_Pss");
		return super.initStates(abus, mach);
	}	
	
	public boolean checkJavaCode() {
		return checkJavaCode(BaseClass); // all CMLScriptingExciter extends AnnotateStabilizer
	}	
	
	public void createControllerObject() {
		createControllerObject(BaseClass);  
	}	
	
	@Override
	public Object getEditPanel() {
		_editPanel.init(this);
		return _editPanel;
	}	
} 

