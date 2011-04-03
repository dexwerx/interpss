/*
 * @(#)NumericUtil.java   
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
 * @Date 04/15/2011
 * 
 *   Revision History
 *   ================
 *
 */
package org.ieee.odm.common;

public class NumericUtil {
	static double ERR = 0.00001;
	
	/**
	 * Check if the two doubles are equal regarding to the err
	 * 
	 * @param x
	 * @param y
	 * @param err
	 * @return
	 */
	public static boolean equals(double x, double y) {
		return Math.abs(x - y) < ERR;
	}

	/**
	 * Check if the two doubles are equal regarding to the err
	 * 
	 * @param x
	 * @param y
	 * @param err
	 * @return
	 */
	public static boolean equals(double x, double y, double err) {
		return Math.abs(x - y) < err;
	}
}
