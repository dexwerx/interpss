 /*
  * @(#)DStabNetData.java   
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
import org.interpss.editor.data.acsc.AcscNetData;

public class DStabNetData extends BaseDataBean{
	private static final long serialVersionUID = 1;

	private boolean saturatedMachData = false;
	
	private AcscNetData acscNetData = null;
	public AcscNetData getAcscNetData() { return acscNetData; }
	public void setAcscNetData(AcscNetData data) { acscNetData = data; }
	/**
	 * @return the saturatedMachData
	 */
	public boolean isSaturatedMachData() {
		return saturatedMachData;
	}
	/**
	 * @param saturatedMachData the saturatedMachData to set
	 */
	public void setSaturatedMachData(boolean saturatedMachData) {
		this.saturatedMachData = saturatedMachData;
	}
}
