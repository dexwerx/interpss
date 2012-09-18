package org.ieee.odm.adapter.pwd.impl;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter.FileTypeSpecifier;

public class PWDHelper {
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
	 * return true if all data fields specified in the nvpairs are parsed.
	 * 
	 * @param str
	 * @param nvpairs
	 * @return true if all data fields are parsed
	 */
	public static boolean parseDataFields(String str, List<PowerWorldAdapter.NVPair> nvpairs){
		return parseDataFields(str, nvpairs, false);
	}
	
	public static boolean parseDataFields(String str, List<PowerWorldAdapter.NVPair> nvpairs, boolean debug){
		
		String[] dataFields=new String[nvpairs.size()];
		//System.out.println("nv size="+nvpairs.size());
		str=str.trim();
		try{
		if (PowerWorldAdapter.dataSeparator == FileTypeSpecifier.Space) {
				int j = -1;
				int k = 0;
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
								dataFields[k++] = value.trim();
						  }
						}

					}

					else {
						//make the data field within a quote as one data 
						sub = str.substring(index, quoteIndexAry.get(n)); 
						dataFields[k++] = sub;
						if (n == quoteIndexAry.size() - 1) {
							//from the last quote to the end
							sub = str.substring(quoteIndexAry.get(n) + 1); 
							if(!sub.trim().isEmpty()){
							   String[] temp = sub.trim().split("\\s++");
							   for (String value : temp) {
									dataFields[k++] = value.trim();
							   }
							}
						}
					}
					index = quoteIndexAry.get(n) + 1;
					//System.out.println("n=" +n+", k="+k);
					/*
					for(String s:dataFields){
						System.out.print(s+",");
					}
					System.out.println();
					*/
					
				}
			} else {
				String[] tempDataFields = str.split(",");
				for (int i = 0; i < tempDataFields.length; i++) {
					dataFields[i] = tempDataFields[i].trim();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("input: " + str + "\n" + 
					           "data fields: " + nvpairs + "\n");
		}
		
		
		int cnt = 0;
		for (PowerWorldAdapter.NVPair nv: nvpairs) {
			nv.value = dataFields[cnt++];
		}
		
		if (debug)
			System.out.println(nvpairs);
		
		return nvpairs.get(nvpairs.size()-1).value != null;
	}
}
