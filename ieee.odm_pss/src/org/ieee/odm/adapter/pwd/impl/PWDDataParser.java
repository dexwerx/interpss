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
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfModelParser;

/**
 * A date parser implementation to parse data file in PWD AUX format 
 * 
 * @author mzhou
 *
 */
public class PWDDataParser {
	Hashtable<Integer, String> positionTable;  // 1, .... n
	Hashtable<String, String> fieldTable;
	protected AclfModelParser parser;
	
	public PWDDataParser(AclfModelParser parser) {
		this.positionTable = new Hashtable<Integer, String>();
		this.fieldTable = new Hashtable<String, String>();
		this.parser = parser;
	}
	
	/**
	 * Parse metadata. If there is existing metadata in the parser,
	 * the metadata will be appended 
	 * 
	 * @param data
	 */
	public void parseMetadata(String data) {
		//renew the position table for each data section
		this.positionTable.clear();
		int cnt =0;
		String[] sAry =PWDHelper.parseMetaData(data);
		for (String s : sAry) {
			this.positionTable.put(cnt++, s.trim());
		}
	}

	/**
	 * parse the data string. 
	 * 
	 * @param data
	 * @return true if all fields are parsed
	 */
	public boolean parseData(String data) {
		//renew the fieldTable before processing each model definition
		this.fieldTable.clear();
        int cnt=0;

		String[] sAry = PWDHelper.parseDataFields(data);
		for (String s : sAry) {
			//System.out.print(s+", ");
			this.fieldTable.put(this.positionTable.get(cnt++), s.trim());
		}
		
		return this.positionTable.size() == this.fieldTable.size();
	}
	/**
	 * 
	 * @return
	 */
	public boolean parseData(String data, boolean appendMode){
		if(!appendMode) parseData(data);
		else{
			int cnt =this.fieldTable.size();
			String[] sAry = PWDHelper.parseDataFields(data);
			for (String s : sAry) {
				
				this.fieldTable.put(this.positionTable.get(cnt++), s.trim());
			}
			//Add because the PWD output IEEE14 data detected meta data duplication issue
			if(sAry.length==this.positionTable.size()){
				if(this.fieldTable.size()!=this.positionTable.size()){
			    ODMLogger.getLogger().severe("Duplicated meta data definition detected! "
				    +"\n"+this.positionTable.toString());
				}
			}
		}
		
	    return this.positionTable.size() == this.fieldTable.size();
	  
	}
	
	public void clearProcessedData(){
		this.fieldTable.clear();
	}
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
	
	public long getLong(String key) throws ODMException {
		return Long.valueOf(this.getString(key));
	}
	
	public String toString() {
		return this.positionTable.toString() + "\n" + this.fieldTable.toString();
	}
} 
