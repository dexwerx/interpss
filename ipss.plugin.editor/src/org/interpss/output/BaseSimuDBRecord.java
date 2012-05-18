/*
 * @(#)BaseSimuDBRecord.java   
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

package org.interpss.output;

public class BaseSimuDBRecord {
	private int recId;
	private int recTypeId;
	private int caseId;

	/**
	 * @return Returns the caseId.
	 */
	public int getCaseId() {
		return caseId;
	}

	/**
	 * @param caseId The caseId to set.
	 */
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}

	/**
	 * @return Returns the recTypeId.
	 */
	public int getRecTypeId() {
		return recTypeId;
	}

	/**
	 * @param recTypeId The recTypeId to set.
	 */
	public void setRecTypeId(int recTypeId) {
		this.recTypeId = recTypeId;
	}

	/**
	 * @return Returns the recId.
	 */
	public int getRecId() {
		return recId;
	}

	/**
	 * @param recId The recId to set.
	 */
	public void setRecId(int recId) {
		this.recId = recId;
	}
}
