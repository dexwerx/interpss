package org.ieee.odm.adapter.pwd.impl;

import static org.ieee.odm.adapter.pwd.PowerWorldAdapter.*;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter.RecType;
import org.ieee.odm.common.ODMLogger;

/**
 * PWDHelper is defined to hold some common method used in the data processing.
 * For example, data completeness checking and get record data type
 * @author 
 *
 */

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
	/*
   public static String[] bparseDataFields(String Str){
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
/*	
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
 * 
 */
	
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
	
	private static boolean endsWithRightParenthesis(String str){
		return str.trim().endsWith(")");
	}
	/**
	 * Get the record data type
	 * @param str input record string
	 * @return record data type string
	 */
	public static RecType getDataType(String str){
		int indexOfLeftParenthesis=str.indexOf("(");
		int indexOfFirstComma=str.indexOf(",");
		String dataType=str.substring(indexOfLeftParenthesis+1, indexOfFirstComma).trim();
		
		RecType recordType=null;

		if(dataType.equals(Token_Bus)){
	    	recordType=RecType.BUS;		
		}
	    else if(dataType.equals(Token_Load)){
	    	recordType=RecType.LOAD;		
		} 
	    else if(dataType.equals(Token_Gen)){
	  		recordType=RecType.GEN;		
	    }
	    else if(dataType.equals(Token_Shunt)){
	  		recordType=RecType.SHUNT;		
	    }
	    else if(dataType.equals(Token_Branch)){
	  		recordType=RecType.BRANCH;		
	    }
	    else if(dataType.equals(Token_XFormer)){
	  		recordType=RecType.XFORMER;		
	    }
	    else if(dataType.equals(Token_3WXFormer)){
	  		recordType=RecType.TRI_W_XFORMER;		
	    }
	    else if(dataType.equals(Token_Area)){
	  		recordType=RecType.AREA;		
	    }
	    else if(dataType.equals(Token_Zone)){
	  		recordType=RecType.ZONE;
	    }
	    else if(dataType.equals(Token_CaseInfo)){
	  		recordType=RecType.CASE_INFO;
	    }
	    else {
	    	recordType=RecType.Undefined;
	    	ODMLogger.getLogger().info("Undifined data type:"+dataType);
	    }		
		
		return recordType;
	}
}
