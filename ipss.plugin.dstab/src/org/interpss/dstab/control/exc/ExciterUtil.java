/*
 * @(#)ExciterObjectFactory.java   
 *
 * Copyright (C) 2008-2010 www.interpss.org
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
 * @Author Tony Huang, Mike Zhou
 * @Version 1.0
 * @Date 06/25/2011
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.dstab.control.exc;

import com.interpss.dstab.mach.Machine;

public class ExciterUtil {
	/**
	 * Calculate machine Ifd using the exciter PU system 
	 * 
	 * @param mach
	 * @return
	 */
	public static double getExciterBasedIfd(Machine mach){
		double xad=mach.getMachData().getXd()-mach.getMachData().getXl();
		// by default, Ifd is based on machine rating
		return mach.calculateIfd(mach.getDStabBus())*xad;
	}
}
