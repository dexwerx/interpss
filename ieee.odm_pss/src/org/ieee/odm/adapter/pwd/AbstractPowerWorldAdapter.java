package org.ieee.odm.adapter.pwd;


import javax.xml.bind.JAXBElement;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.pwd.impl.BranchDataProcessor;
import org.ieee.odm.adapter.pwd.impl.BusDataProcessor;
import org.ieee.odm.adapter.pwd.impl.NetDataProcessor;
import org.ieee.odm.adapter.pwd.impl.PWDHelper;
import org.ieee.odm.adapter.pwd.impl.TransformerDataProcessor;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;

 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.2  01/08/2012
  * @author  
  * 
  */
public abstract class AbstractPowerWorldAdapter extends AbstractODMAdapter{
	public  static final String Token_Data="DATA";
	public  static final String Token_Bus="BUS";
	public  static final String Token_Load="LOAD";
	public  static final String Token_Gen="GEN";
	public  static final String Token_Shunt="SHUNT";
	public  static final String Token_Branch="BRANCH";
	public  static final String Token_XFormer="TRANSFORMER";
	public  static final String Token_3WXFormer="3WXFORMER";
	public  static final String Token_Area="AREA";
	public  static final String Token_Zone="ZONE";
	public  static final String Token_CaseInfo="PWCASEINFORMATION";//PWCASEINFORMATION
	//Define the record data type
	public static enum RecType{BUS,LOAD,GEN,SHUNT,BRANCH,XFORMER,TRI_W_XFORMER,AREA,ZONE,CASE_INFO,Undefined};
	
	protected void processInputFile(IFileReader din) throws ODMException {
		String str;
		RecType recordType=RecType.Undefined;
		do{
			str=din.readLine().trim();
			
			if(str!=null) {
				if(str.startsWith(Token_Data)) {
					recordType=PWDHelper.getDataType(str);
				    
					processMetadata(din, str, recordType);
				} //end of processing data type
			
				else if(str.startsWith("//"))
					ODMLogger.getLogger().fine("comments:"+str);
				else if(str.startsWith("{"))
			    	ODMLogger.getLogger().info(recordType.toString()+" type data begins");
				else if(str.startsWith("}")){
					ODMLogger.getLogger().info(recordType.toString()+" type data ends");
				}
				// start processing record data
				else if(!str.isEmpty()){
					processData(din, str, recordType);
				}
			}//end of if str is not null  
		}while (str!=null);
	}
	
	abstract protected void processMetadata(IFileReader din, String str, RecType recordType) throws ODMException ;
	abstract protected void processData(IFileReader din, String str, RecType recordType) throws ODMException ;
}
