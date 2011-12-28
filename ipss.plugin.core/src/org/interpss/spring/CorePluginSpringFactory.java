/*
 * @(#)CorePluginSpringCtx.java   
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

package org.interpss.spring;

import org.interpss.mapper.odm.ODMAclfDataMapper;
import org.interpss.mapper.odm.ODMAcscDataMapper;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.interpss.mapper.odm.ODMOpfDataMapper;

public class CorePluginSpringFactory extends BasePluginSpringFactory {
	/*
	 * 		Mapper definition Odm -> SimuCtx
	 * 		================================
	 */
	public static ODMAclfDataMapper getOdm2AclfMapper() {
		return (ODMAclfDataMapper) SpringAppCtx.getBean("odm2AclfMapper");
	}	

	public static ODMAcscDataMapper getOdm2AcscMapper() {
		return (ODMAcscDataMapper) SpringAppCtx.getBean("odm2AcscMapper");
	}	
	
	public static ODMDStabDataMapper getOdm2DStabMapper() {
		return (ODMDStabDataMapper) SpringAppCtx.getBean("odm2DStabMapper");
	}	

	public static ODMOpfDataMapper getOdm2OpfMapper() {
		return (ODMOpfDataMapper) SpringAppCtx.getBean("odm2OpfMapper");
	}		
}
