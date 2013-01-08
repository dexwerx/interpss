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
import java.util.Hashtable;
import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter.FileTypeSpecifier;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;

/**
 *  It first parses the meta data definition string, and save the attributes in a hashtable, 
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
public class InputLineStringParser {
	/**
	 * store key position info { (1, BusNum), (2, BusNum:1) ... }
	 */
	private Hashtable<Integer, String> positionTable;  // 1, .... n
	/**
	 * store the nv pairs { (BusNum, 4), (BusNum:1, 5) ... }
	 */
	private Hashtable<String, String> fieldTable;
	
	private List<String> dataList;
	
	/**
	 * constructor
	 */
	public InputLineStringParser() {
		this.positionTable = new Hashtable<Integer, String>();
		this.fieldTable = new Hashtable<String, String>();
		this.dataList = new ArrayList<String>();
	}
	
	/**
	 * Parse metadata. The hashTable storing meta data will be renewed
	 * for each data section, therefore, make sure the input string for the 
	 * metaData is completed by itself.  
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
	 * parse the string data. The append mode is an option, if it is set
	 * to be true, then the data will be appended to the fieldTable, while the fiedTable will
	 * not be renewed or cleared.
	 * 
	 * @param data
	 * @param appendMode
	 * @return
	 */
	public boolean parseData(String data, boolean appendMode){
		//System.out.println(data);
		if(!appendMode) 
			parseData(data);
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
		//System.out.println("position table="+this.positionTable.size()+", fieldTable ="+this.fieldTable.size());
	    return this.positionTable.size() == this.fieldTable.size();
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
	 * parse a complete data section metaData definition and return as an string array.
	 * 
	 *    [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA]
	 *    
	 *    { "BusNum", "BusNum:1", "LineCircuit", "LineStatus" }
	 * 
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
	
	/**
	 * Parse an input string to a string array
	 *   input string : 4     5 " 1" "Closed"  0.000000  0.100000  0.000000  0.000000
	 *   to a string array : { "4", "5", " 1", "Closed", "0.000000", "0.100000", "0.000000", "0.000000"}
	 *   
	 * @param Str
	 * @return
	 */
	public String[] parseDataFields(String Str){
	    this.dataList.clear();

		boolean quotBegin = false;

		int beginIdx =0, endIdx=0;
		
		String s=Str.trim();
		int length = s.length();
		int length_1 = length-1;
		
		//convert the input string to a char array
	    char[] charAry =s.toCharArray();

	    for( int i = 0;i<charAry.length;i++){
			//string within a quotation is processed separately 
			// and treated as a whole
			if (!(charAry[i] == '"' || charAry[i] == '\'')) {
			   // PWD uses the space to separate data by default, the consecutive non-space
			   // characters together form a string
				if (i > 0 && !quotBegin) {
					if (i <= length_1) {
						if (Character.isWhitespace(charAry[i-1])
								&& (!Character.isWhitespace(charAry[i]))) {
							beginIdx = i;
						} else if (Character.isWhitespace(charAry[i])&& 
								(!Character.isWhitespace(charAry[i-1]) && 
								 !(charAry[i-1] == '"' || charAry[i-1] == '\''))) {
							endIdx = i;
							this.dataList.add(s.substring(beginIdx, endIdx)); 
							//this.dataList.add(new String(charAry, beginIdx, endIdx-beginIdx));
						}
					}
					// if the processing data is the last one, since no
					// space after it, it needs to be treated specially.
					if (i == (length_1)) {
						endIdx = length;
						this.dataList.add(s.substring(beginIdx, endIdx));
						//this.dataList.add(new String(charAry, beginIdx, endIdx-beginIdx));
					}
				} // end if i>1 && !quotBegin
			}	   
			else{ // The following processes the string within quotations.
				if (!quotBegin){
					quotBegin = true;
				    beginIdx=i+1;
				}else{//this quotation completes a pair
					endIdx=i;
					this.dataList.add(s.substring(beginIdx, endIdx));
					//this.dataList.add(new String(charAry, beginIdx, endIdx-beginIdx));
					quotBegin = false;
				}
			   
		   }
		}
		//System.out.println(dataList.toString());
		return this.dataList.toArray(new String[1]);
	}	
	
	/*
	public String[] parseDataFields(String Str){
	    this.dataList.clear();
	    
		StringBuilder strBuf=new StringBuilder();

		boolean isEntry = false;

		boolean quotBegin = false, quotEnd = false;

		int beginIdx =0, endIdx=0;
		
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
				if (!Character.isWhitespace(charAry[i]) || quotBegin) {
					strBuf.append(charAry[i]);
					isEntry = true;
					//if the processing data is the last one, since no white space
					//after it, it needs to be treated specially.
					if (i == charAry.length - 1) {
						this.dataList.add(strBuf.toString());
					}
				}
			    //if any white space not within a quotation is encountered, then one data entry
			    // is completed and should be added to the data list
				else if(isEntry){
					this.dataList.add(strBuf.toString());
					strBuf.setLength(0);
					//reset the flag
					isEntry=false;
				}
			}	   
			else	{ // charAry[i]=='"' || charAry[i]=='\'')
				if (!quotBegin)
					quotBegin = true;
				else
					quotEnd = true;
				
			   //if the quotation counter is two, then one complete data entry within
			   // a quotation has been processed and need to save to the data list
			   if(quotEnd){
				   this.dataList.add(strBuf.toString());
				   strBuf.setLength(0);
				   quotBegin = false;
				   quotEnd = false;
				   isEntry=false;
			   }
		   }
		}
		//System.out.println(dataList.toString());
		return this.dataList.toArray(new String[1]);
	}	
	
	public String[] xparseDataFields(String str){
		List<String> dataList=new ArrayList<String>();
		String[] dataFields=null;
		str=str.trim();
		try{
		if (PowerWorldAdapter.dataSeparator == FileTypeSpecifier.Blank) {
				int j = -1;
				//int k = 0;
				// get the quote index
				List<Integer> quoteIndexAry = new ArrayList<Integer>();
				do {
					j = str.indexOf("\"", j + 1);// index of double-quote
					if (j != -1)
						quoteIndexAry.add(j);
				} while (j != -1);

				int index = 0;
				for (int n = 0; n < quoteIndexAry.size(); n++) {
					String sub = "";

					if (n % 2 == 0) {
						sub = str.substring(index, quoteIndexAry.get(n));
						// separating substrings without double-quote with blank
						if(!sub.trim().isEmpty()){
						  String[] temp = sub.trim().split("\\s++");
								
						  for (String value : temp) {
							//if (!value.trim().equals(""))
								//dataFields[k++] = value.trim();
							  dataList.add(value.trim());
						  }
						}

					}

					else {
						//make the data field within a quote as one data 
						sub = str.substring(index, quoteIndexAry.get(n)); 
						//dataFields[k++] = sub;
						dataList.add(sub);
						if (n == quoteIndexAry.size() - 1) {
							//from the last quote to the end
							sub = str.substring(quoteIndexAry.get(n) + 1); 
							if(!sub.trim().isEmpty()){
							   String[] temp = sub.trim().split("\\s++");
							   for (String value : temp) {
									//dataFields[k++] = value.trim();
								   dataList.add(value.trim());
							   }
							}
						}
					}
					index = quoteIndexAry.get(n) + 1;
					//System.out.println("n=" +n+", k="+k);
					
				}
				
				//set the result to dataFields[];
				dataFields=new String[dataList.size()];
				for(int i=0;i<dataList.size();i++){
					dataFields[i]=dataList.get(i);
				}
				
			} else {
				String[] tempDataFields = str.split(",");
				dataFields=new String[tempDataFields.length];
				for (int i = 0; i < tempDataFields.length; i++) {
					dataFields[i] = tempDataFields[i].trim();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("input: " + str + "\n" + 
					           "data fields: " + dataFields+ "\n");
		}
		return dataFields;
	}	
	*/
} 
