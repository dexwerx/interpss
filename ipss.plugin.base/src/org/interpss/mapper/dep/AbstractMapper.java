/*
 * @(#)AbstractMapper.java   
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

package org.interpss.mapper.dep;

import java.util.Map;

import com.interpss.common.msg.IPSSMsgHub;

public abstract class AbstractMapper implements IpssMapper {
	/**
	 */
	protected IPSSMsgHub msg = null;

	/**
	 */
	protected AbstractMapper() {
		super();
	}

	/**
	 */
	public IPSSMsgHub getMsg() {
		return msg;
	}

	/**
	 */
	public void setMsg(IPSSMsgHub newMsg) {
		msg = newMsg;
	}

	/**
	 */
	@Override
	public <T> boolean mapping(Object fromObj, T toObj) {
		// needs to be implemented by the concrete child adapters
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean mapping(Object fromObj, Object toObj, Class<?> klass) {
		// needs to be implemented by the concrete child adapters
		throw new UnsupportedOperationException();
	}
	/**
	 */
	public Object[] mappingMultiObject(Object fromObj, Class<?> klass,
			Map<String, Object> parameters) {
		// needs to be implemented by the concrete child adapters
		throw new UnsupportedOperationException();
	}
} //AbstractMapper
