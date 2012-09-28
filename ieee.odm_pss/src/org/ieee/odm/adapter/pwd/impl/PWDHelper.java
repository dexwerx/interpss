package org.ieee.odm.adapter.pwd.impl;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter.FileTypeSpecifier;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter.NVPair;

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
	 * @param str
	 * @param nvpairs
	 */
	public static void parseDataFields(String str, List<PowerWorldAdapter.NVPair> nvpairs){
		parseDataFields(str, nvpairs, false);
	}
	
	public static void parseDataFields(String str, List<PowerWorldAdapter.NVPair> nvpairs, boolean debug){
		
		String[] dataFields = new String[nvpairs.size()];
		str = str.trim();
		try {
			if (PowerWorldAdapter.dataSeparator == FileTypeSpecifier.Blank) {
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
						if (!sub.trim().isEmpty()) {
							String[] temp = sub.trim().split("\\s++");

							for (String value : temp) {
								// if (!value.trim().equals(""))
								dataFields[k++] = value.trim();
							}
						}

					}

					else {
						// make the data field within a quote as one data
						sub = str.substring(index, quoteIndexAry.get(n));
						dataFields[k++] = sub;
						if (n == quoteIndexAry.size() - 1) {
							// from the last quote to the end
							sub = str.substring(quoteIndexAry.get(n) + 1);
							if (!sub.trim().isEmpty()) {
								String[] temp = sub.trim().split("\\s++");
								for (String value : temp) {
									dataFields[k++] = value.trim();
								}
							}
						}
					}
					index = quoteIndexAry.get(n) + 1;
				}
			} else {
				String[] tempDataFields = str.split(",");
				for (int i = 0; i < tempDataFields.length; i++) {
					dataFields[i] = tempDataFields[i].trim();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("input: " + str + "\n" + "data fields: "
					+ nvpairs + "\n");
		}

		int cnt = 0;
		for (PowerWorldAdapter.NVPair nv: nvpairs) {
			nv.value = dataFields[cnt++];
		}
		
		if (debug)
			System.out.println(nvpairs);
	}
	
	public static String[]parseDataFields(String str){
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
					/*
					for(String s:dataFields){
						System.out.print(s+",");
					}
					System.out.println();
					*/
					
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
	
	/**
	 * Check whether the Argument Fields is completed yet, since it is
	 * possible that the Argument Fields are defined in multiple lines. 
	 * @param str
	 * @return
	 */
    public static boolean isArgumentFieldsCompleted(String str){
		
		boolean leftParenthesis=false;
		boolean rightParenthesis=false;

		if(str.indexOf("(")>-1)leftParenthesis=true;
		
        rightParenthesis=endsWithRightParenthesis(str);
        
		return leftParenthesis&&rightParenthesis;
		
	}
	
	public static boolean isDataFiledsCompleted(String dataStr,List<NVPair> inputNvPairs ){
		/*
		 * data fields are completed only when all nvPairs in the list are completed 
		 */
		boolean dataCompleted=true;
		parseDataFields(dataStr, inputNvPairs);
		for(NVPair nv:inputNvPairs){
			if(nv.value==null){
				dataCompleted=false;
				break;
			}
		}
		return dataCompleted;
	}
	
	private static boolean endsWithRightParenthesis(String str){
		return str.trim().endsWith(")");
	}
	
	public static String getDataType(String str){
		int indexOfLeftParenthesis=str.indexOf("(");
		int indexOfFirstComma=str.indexOf(",");
		String dataType=str.substring(indexOfLeftParenthesis+1, indexOfFirstComma).trim();
		
		return dataType;
		
	}
	/**
	 * now the in-line comment is not considered yet!. 
	 */
	public static void parseFieldNames(String str,List<NVPair> inputNvPairs){
		
		int indexOfLeftBracket=str.indexOf("[");
		int indexOfRightBracket=str.indexOf("]");
		String[] arguFields=str.substring(indexOfLeftBracket+1,
				indexOfRightBracket).split(",");
		inputNvPairs.clear();
		for(int i=0;i<arguFields.length;i++){
			inputNvPairs.add(new NVPair(arguFields[i].trim()));
		}
	}
	/**
	 * parse a complete data section metaData definition and return as an order string array.
	 * @param str
	 * @return A String array storing the metaData definition
	 */
	public static String[] parseMetaData(String str){
		int indexOfLeftBracket=str.indexOf("[");
		int indexOfRightBracket=str.indexOf("]");
		String[] arguFields=str.substring(indexOfLeftBracket+1,
				indexOfRightBracket).split(",");
		return arguFields;
	}
}
