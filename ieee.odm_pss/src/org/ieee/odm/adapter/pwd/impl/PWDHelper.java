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
	 * @param str
	 * @param nvpairs
	 */
	public static void parseDataFields(String str, List<PowerWorldAdapter.NVPair> nvpairs){
		parseDataFields(str, nvpairs, false);
	}
	
	public static void parseDataFields(String str, List<PowerWorldAdapter.NVPair> nvpairs, boolean debug){
		
		String[] dataFields=new String[nvpairs.size()];
		if(PowerWorldAdapter.dataSeparator==FileTypeSpecifier.Blank) {
			int j=-1;
			int k=0;
			//get the quote index
			List<Integer> quoteIndexAry=new ArrayList<Integer>(); 
			do{
				j=str.indexOf("\"",j+1);//index of double-quote
				if(j!=-1)quoteIndexAry.add(j);
			}while (j!=-1);
			
			int index=0;
		    for(int n=0;n<quoteIndexAry.size();n++){
		    	
		    	String sub="";
		    	
		    	if(n%2==0){
		    		sub=str.substring(index, quoteIndexAry.get(n));
		    		
		    		String[] temp=sub.split("\\s++");// separating substrings without double-quote with blank
				    for(String value:temp){
					   if(!value.trim().equals(""))dataFields[k++]=value;
				    }
				    
		    	}
		    	
		    	else {
<<<<<<< .mine
		    		//sub=str.substring(quoteIndexAry.get(n++)+1, quoteIndexAry.get(n));
		    		sub=str.substring(index, quoteIndexAry.get(n)); //select a data field with double-quote 
=======
		    		
		    		sub=str.substring(index, quoteIndexAry.get(n));//select a data field with double-quote 
>>>>>>> .r6524
		    		dataFields[k++]=sub;
		    	    if(n==quoteIndexAry.size()-1){
<<<<<<< .mine
		    		   sub=str.substring(quoteIndexAry.get(n)+1); // from the last double-quote to the end;
=======
		    		   sub=str.substring(quoteIndexAry.get(n)+1);// from the last double-quote to the end;
>>>>>>> .r6524
		    		   String[] temp=sub.split("\\s++");
				       for(String value:temp){
					       if(!value.trim().equals(""))dataFields[k++]=value;
				        }
		    	    }
		    	}
		    		index=quoteIndexAry.get(n)+1;
		    }
		}
		else {
			String[] tempDataFields=str.split(",");
			for(int i=0;i<tempDataFields.length;i++){
				 dataFields[i]=tempDataFields[i].trim();
			}
		}
		
		int cnt = 0;
		for (PowerWorldAdapter.NVPair nv: nvpairs) {
			nv.value = dataFields[cnt++];
		}
		
		if (debug)
			System.out.println(nvpairs);
	}
}
