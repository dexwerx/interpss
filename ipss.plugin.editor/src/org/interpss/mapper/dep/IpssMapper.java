/*
 * @(#)IpssMapper.java   
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

//public interface IpssMapper <Tfrom, Tto> {
public interface IpssMapper {
	/**
	 */
	IPSSMsgHub getMsg();

	/**
	 */
	void setMsg(IPSSMsgHub value);

	/**
	 * map the fromObj to the toObj. klass is the type of fromObj or toobj depending on 
	 * actual implementation.
	 */
	<T> boolean mapping(Object fromObj, T toObj);
	
	boolean mapping(Object fromObj, Object toObj, Class<?> klass);

	/**
	 * map the fromObj to a list of objects of type kclass. Extra parameters for the mapping
	 * could be passed by the parameters map
	 */
	Object[] mappingMultiObject(Object fromObj, Class<?> klass, 
			Map<String, Object> parameters);

} // AbstractMapper
