/*
 * @(#)DataChangeMessage.java   
 *
 * Copyright (C) 2006 www.interpss.com
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

package org.interpss.editor;

import com.interpss.common.msg.impl.IpssMessageAdapter;

public class DataChangeMessage extends IpssMessageAdapter {
	public static final byte DataDirty = 1, DataChangeMsg = 2;

	public DataChangeMessage(byte type, String msg) {
		this.msgType = type;
		this.msgStr = msg;
	}

	public DataChangeMessage(byte type) {
		this.msgType = type;
	}
}