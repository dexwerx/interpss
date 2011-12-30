 /*
  * @(#)DStabBusData.java   
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

package org.interpss.editor.data.dstab;

import org.interpss.db.BaseDataBean;
import org.interpss.editor.data.aclf.AclfBusData;
import org.interpss.editor.data.acsc.AcscBusData;

public class DStabBusData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	private boolean dBusScripting = false;
	
	public boolean isDBusScripting() {
		return dBusScripting;
	}

	public void setDBusScripting(boolean busScripting) {
		dBusScripting = busScripting;
	}

	public boolean isMachineBus() {
		return getAcscBusData().getGenCode().equals(AclfBusData.GenCode_PQ) || 
			   getAcscBusData().getGenCode().equals(AclfBusData.GenCode_PV) ||
			   getAcscBusData().getGenCode().equals(AclfBusData.GenCode_Swing);
	}
	
	public boolean isMotorBus() {
		return getAcscBusData().getLoadCode().equals(AclfBusData.LoadCode_ConstI) || 
			   getAcscBusData().getLoadCode().equals(AclfBusData.LoadCode_ConstP) ||
			   getAcscBusData().getLoadCode().equals(AclfBusData.LoadCode_ConstZ) ||
		       getAcscBusData().getLoadCode().equals(AclfBusData.LoadCode_FuncLoad);
	}
	
	private AcscBusData acscBusData = null;
	public AcscBusData getAcscBusData() { return acscBusData; }
	public void setAcscBusData(AcscBusData data) { acscBusData = data; }
	
	private DStabMachData machData = new DStabMachData();
	public DStabMachData getMachData() { return machData; }
	public void setMachData(DStabMachData data) { machData = data; }
}
