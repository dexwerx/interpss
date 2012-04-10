package org.ieee.odm.adapter.pwd.impl;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter.FileTypeSpecifier;

public class PWDHelper {
	public static String[] getDataFields(String str, List<String> argumentFileds){
		
		String[] dataFields=new String[argumentFileds.size()];
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
		    		
		    		sub=str.substring(index, quoteIndexAry.get(n));//select a data field with double-quote 
		    		dataFields[k++]=sub;
		    	    if(n==quoteIndexAry.size()-1){
		    		   sub=str.substring(quoteIndexAry.get(n)+1);// from the last double-quote to the end;
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
		
		
		return dataFields;
	}

}
