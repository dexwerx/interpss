 /*
  * @(#)UserPrefData.java   
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

package org.interpss.editor.data.user;

import org.interpss.db.BaseDataBean;

public class UserPrefData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	private boolean autoSave = true;
	public boolean isAutoSave() { return this.autoSave; }
	public void setAutoSave(boolean b) { this.autoSave = b;}
	
	private int autoSaveInterval = 10;  // in min
	public int getAutoSaveInterval() { return this.autoSaveInterval; }
	public void setAutoSaveInterval(int n) { this.autoSaveInterval = n;}
}