/*
 * @(#)PWDDataParser.java   
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
 * @Date 09/15/2012
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.adapter.pwd.impl;

import java.util.Hashtable;

import org.ieee.odm.common.ODMException;

/**
 * A date parser implementation to parse data file in PWD AUX format 
 * 
 * @author mzhou
 *
 */
public class PWDDataParser {
	Hashtable<Integer, String> poitionTable;  // 1, .... n
	Hashtable<String, String> fieldTable;
	
	public PWDDataParser() {
		this.poitionTable = new Hashtable<Integer, String>();
		this.fieldTable = new Hashtable<String, String>();
	}
	
	/**
	 * Parse metadata. If there is existing metadata in the parser,
	 * the metadata will be appended 
	 * 
	 * @param data
	 */
	public void parseMetadata(String data) {
		int cnt = this.poitionTable.size();

		
		// "A, B, C, A:1, D"
		String[] sAry =PWDHelper.parseMetaData(data);
		for (String s : sAry) {
			this.poitionTable.put(++cnt, s.trim());
		}
	}

	/**
	 * parse the data string. 
	 * 
	 * @param data
	 * @return true if all fields are parsed
	 */
	public boolean parseData(String data) {
		int cnt = this.fieldTable.size();

		// TODO the follow code needs to be update
		// "1, 2, C, 4, 5"
		String[] sAry = PWDHelper.parseDataFields(data);
		for (String s : sAry) {
			this.fieldTable.put(this.poitionTable.get(++cnt), s.trim());
		}
		
		return this.poitionTable.size() == this.fieldTable.size();
	}
	
	/**
	 * check if the data field identified by the key exists
	 * 
	 * @param key
	 * @return
	 */
	public boolean exist(String key) {
		return this.fieldTable.get(key) != null;
	}
	
	/**
	 * Get field of type String
	 * 
	 * @param key
	 * @return
	 * @throws ODMException throw exception if the field does not exist
	 */
	public String getString(String key) throws ODMException {
		String field = this.fieldTable.get(key);
		if (field == null)
			throw new ODMException("Field " + key + " not found");
		return field;
	}
	
	/**
	 * Get field of type double
	 * 
	 * @param key
	 * @return
	 * @throws ODMException throw exception if the field does not exist
	 */
	public double getDouble(String key) throws ODMException {
		return new Double(this.getString(key)).doubleValue();
	}

	/**
	 * Get field of type int
	 * 
	 * @param key
	 * @return
	 * @throws ODMException throw exception if the field does not exist
	 */
	public int getInt(String key) throws ODMException {
		return new Integer(this.getString(key)).intValue();
	}
	
	public String toString() {
		return this.poitionTable.toString() + "\n" + this.fieldTable.toString();
	}
} 
