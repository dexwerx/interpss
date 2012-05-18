/*
 * @(#)IRefDataManager.java   
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

package org.interpss.ui;

public interface IRefDataManager {
	public static final int REFDATA_LoadSchedule = 1;

	/**
	 * Load all ref data object from, for example, the ref database
	 * 
	 */
	void loadAllRefData();

	/**
	 * Get a ref data object of the type
	 * 
	 * @return Returns the object.
	 */
	Object getRefDataObject(int type);

	/**
	 * Set a ref data objct
	 * 
	 * @param obj the ref data to set.
	 * @param type object typet.
	 */
	void setRefDataObject(Object obj, int type);

	/**
	 * Convert all ref data objects to a string for display purpose
	 * @return
	 */
	String toString();
}