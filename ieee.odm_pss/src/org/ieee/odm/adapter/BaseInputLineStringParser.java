/*
 * @(#)InputLineStringParser.java   
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

package org.ieee.odm.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;

/**
 *  It first parses the meta data definition string, and save the attributes in a LinkedHashMap, 
 *  named positionTable. Then it parses the data string according to the field definition as 
 *  name-value pairs. For example,  
 * 
	  [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA,
            LineCMVA,LineShuntMW,LineShuntMW:1,LineShuntMVR,LineShuntMVR:1,LineXfmr,LineTap,
            LinePhase,SeriesCapStatus]
	    4     5 " 1" "Closed"  0.000000  0.100000  0.000000  0.000000  1000.000  1000.000  1000.000     0.000     0.000     0.000     0.000  "YES"    0.993750   0.000000 "Not Bypassed"
 * 
 * after the parsing, the nv pairs in the fieldTable will store
 * 
 *       <"BusNum","4">, <"BusNum:1","5">, ....
 * 
 * @author mzhou
 *
 */
public class BaseInputLineStringParser {
	/**
	 * store key position info { (1, BusNum), (2, BusNum:1) ... }
	 */
	protected LinkedHashMap<Integer, String> positionTable;  // 1, .... n

	/**
	 * store the nv pairs { (BusNum, 4), (BusNum:1, 5), (BusName, Name) ... }
	 */
	protected LinkedHashMap<String, String> fieldTable;
	
	/**
	 * constructor
	 */
	public BaseInputLineStringParser() {
		this.positionTable = new LinkedHashMap<Integer, String>();
		this.fieldTable = new LinkedHashMap<String, String>();
	}
	
	/**
	 * set parser meta data
	 * 
	 * @param dataAry meta date string array
	 */
	public void setMetadata(String[] dataAry) {
		//renew the position table for each data section
		this.positionTable.clear();
		int cnt =0;
		for (String s : dataAry) {
			this.positionTable.put(cnt++, s.trim());
		}
	}

	/**
	 * set value at the postion
	 * 
	 * @param position
	 * @param value
	 */
	public void setValue(int position, String value) {
		this.fieldTable.put(this.positionTable.get(position), value);
	}
	
	/**
	 * clear the name-value pair table 
	 */
	public void clearNVPairTableData(){
		this.fieldTable.clear();
	}
	
	/**
	 * check if all data fields are parsed
	 * 
	 * @return
	 */
	public boolean isDataCompleted(){
		 return this.positionTable.size() == this.fieldTable.size();
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
		return Double.valueOf(this.getString(key));
	}

	/**
	 * Get field of type int
	 * 
	 * @param key
	 * @return
	 * @throws ODMException throw exception if the field does not exist
	 */
	public int getInt(String key) throws ODMException {
		return Integer.valueOf(this.getString(key));
	}
	
	/**
	 * Get field of type long
	 * 
	 * @param key
	 * @return
	 * @throws ODMException throw exception if the field does not exist
	 */
	public long getLong(String key) throws ODMException {
		return Long.valueOf(this.getString(key));
	}
	
	@Override public String toString() {
		return this.positionTable.toString() + "\n" + this.fieldTable.toString();
	}
	
	/**
	 * get the field lookup table
	 * 
	 * @return the field lookup table
	 */
	public LinkedHashMap<String, String> getFieldTable() {
		return fieldTable;
	}

	/**
	 * set the field lookup table
	 * 
	 * @param fieldTable the field lookup table object
	 */
	public void setFieldTable(LinkedHashMap<String, String> fieldTable) {
		this.fieldTable = fieldTable;
	}
} 
