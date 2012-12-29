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

package org.ieee.odm.adapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfModelParser;

/**
 * Parse an input string according to the field definition in the nv pair. For example,  
 * 
	  [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA,
            LineCMVA,LineShuntMW,LineShuntMW:1,LineShuntMVR,LineShuntMVR:1,LineXfmr,LineTap,
            LinePhase,SeriesCapStatus]
	    4     5 " 1" "Closed"  0.000000  0.100000  0.000000  0.000000  1000.000  1000.000  1000.000     0.000     0.000     0.000     0.000  "YES"    0.993750   0.000000 "Not Bypassed"
 * 
 * after the parsing, the nv pair list will store
 * 
 *       <"BusNum","4">, <"BusNum:1","5">, ....
 * 
 * @author mzhou
 *
 */
public class PWDDataParser {
	/**
	 * store key position info { (1, BusNum), (2, BusNum:1) ... }
	 */
	private Hashtable<Integer, String> positionTable;  // 1, .... n
	/**
	 * store the nv pairs { (BusNum, 4), (BusNum:1, 6) ... }
	 */
	private Hashtable<String, String> fieldTable;
	
	public PWDDataParser() {
		this.positionTable = new Hashtable<Integer, String>();
		this.fieldTable = new Hashtable<String, String>();
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
		String[] sAry = parseMetaData(data);
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

		String[] sAry = parseDataFields(data);
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
			String[] sAry = parseDataFields(data);
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
	
	//
	
	/**
	 * parse a complete data section metaData definition and return as an order string array.
	 * @param str
	 * @return A String array storing the metaData definition
	 */
	private String[] parseMetaData(String str){
		int indexOfLeftBracket=str.indexOf("[");
		int indexOfRightBracket=str.indexOf("]");
		String[] arguFields=str.substring(indexOfLeftBracket+1,
				indexOfRightBracket).split(",");
		return arguFields;
	}	
	
	protected String[] parseDataFields(String Str){
		    List<String> dataList=new ArrayList<String>();
			StringBuffer strBuf=new StringBuffer();
			boolean isEntry = false;
			//quotation counter
			int quotCnt = 0;
			String s=Str.trim();
			//convert the input string to a char array
		   char[] charAry =s.toCharArray();
			for( int i = 0;i<charAry.length;i++){
				
				//string within a quotation is processed separately 
				// and treated as a whole
				if(!(charAry[i]=='"'||charAry[i]=='\'')){
					
				  //PWD uses the space to separate data, the consecutive non-space
				  //characters are appended together to form a string, and set the 
				  //isEntry to be true
					if (!Character.isWhitespace(charAry[i]) || quotCnt == 1) {
						strBuf.append(charAry[i]);
						isEntry = true;
						//if the processing data is the last one, since no white space
						//after it, it needs to be treated specially.
						if (i == charAry.length - 1) {
							dataList.add(strBuf.toString());
						}
					}
				   //if any white space not within a quotation is encountered, then one data entry
				  // is completed and should be added to the data list
				   else if(isEntry){
					   dataList.add(strBuf.toString());
					   strBuf=new StringBuffer();
					   //reset the flag
					   isEntry=false;
				   }
				   
				}	   
			   else{
				   quotCnt+=1; 
				   //if the quotation counter is two, then one complete data entry within
				   // a quotation has been processed and need to save to the data list
				   if(quotCnt==2){
					   dataList.add(strBuf.toString());
					   strBuf=new StringBuffer();
					   quotCnt = 0;
					   isEntry=false;
				   }
			   }
					
			}
			//System.out.println(dataList.toString());
			return dataList.toArray(new String[1]);
	   }	
} 
