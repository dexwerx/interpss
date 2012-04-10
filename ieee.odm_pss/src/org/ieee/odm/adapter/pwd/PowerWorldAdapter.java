package org.ieee.odm.adapter.pwd;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.pwd.impl.BranchDataProcessor;
import org.ieee.odm.adapter.pwd.impl.BusDataProcessor;
import org.ieee.odm.adapter.pwd.impl.NetDataProcessor;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.1  04/03/2012
  * @author Tony Huang
  * 
  */
public class PowerWorldAdapter extends AbstractODMAdapter{
	
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
	
	private enum RecType{BUS,LOAD,GEN,SHUNT,BRANCH,XFORMER,TRI_W_XFORMER,AREA,ZONE,CASE_INFO};

	public static enum FileTypeSpecifier{CSV,Blank};
	public static FileTypeSpecifier dataSeparator=FileTypeSpecifier.Blank;//By default

	private List<String> argumentFileds;

	public PowerWorldAdapter(){
		super();
	}
	
	@Override
	protected IODMModelParser parseInputFile(IFileReader din, String encoding)
			throws Exception {
		
		AclfModelParser parser=new AclfModelParser(encoding);
		
		parser.setLFTransInfo(OriginalDataFormatEnumType.POWER_WORLD);
		
		// BaseCase object, plus busRecList and BranchRecList are created 
		LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
		baseCaseNet.setId("Base_Case_from_PowerWorld_format");
		baseCaseNet.setBasePower(BaseDataSetter.createPowerMvaValue(100.0));//not defined in the file
		String str;
		String dataType;
		RecType recordType=null;
		
		
		do{
			str=din.readLine();
			try{
			if(str!=null&& str.startsWith(Token_Data)){
				dataType=getDataType(str);
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
			    else ODMLogger.getLogger().warning("Undifined data type:"+dataType);
			    
			    //get all the argument fields of a record, then save them to a list.
			    while(!isArgumentFieldsCompleted(str)){
					str+=din.readLine();
				}
			    argumentFileds=getArgumentFields(str);
			    
			} //end of processing data type
			
			 else if(str.trim().startsWith("//"))
				 ODMLogger.getLogger().fine("comments:"+str);
			 else if(str.trim().startsWith("{"))
			    	ODMLogger.getLogger().info(recordType.toString()+" type data begins");
			 
			 else if(str.trim().startsWith("}")){
					ODMLogger.getLogger().info(recordType.toString()+" type data ends");
					//TODO Assume the zone type data is at the end of load flow data definition 
			        if (recordType==RecType.ZONE) {
			        	ODMLogger.getLogger().info("End of processing Zone data, " +
			        			"LoadFlow data processing completed!");
			        	break;
			        }
			 }
			 // start processing record data
			//TODO assume all data in one line; NE-ISO file uses multiple lines to store some data, e.g. transformer data;
			 else if(!str.trim().isEmpty()){
				  
				   if(recordType==RecType.BUS) 
					   BusDataProcessor.processBusBasicData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.LOAD)
					   BusDataProcessor.processBusLoadData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.GEN)
					   BusDataProcessor.processBusGenData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.SHUNT)
					   BusDataProcessor.processBusShuntData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.BRANCH)
					   BranchDataProcessor.processBranchData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.XFORMER)
					   BranchDataProcessor.processXFormerData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.TRI_W_XFORMER)
					   BranchDataProcessor.process3WXFomerData(str, this.argumentFileds, baseCaseNet);
				   else if(recordType==RecType.AREA)
					   NetDataProcessor.processAreaData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.ZONE)
					   NetDataProcessor.processZoneData(str, this.argumentFileds, parser);
				   else if(recordType==RecType.CASE_INFO){
					   //TODO
					   ODMLogger.getLogger().info("Case Info data# "+str);
				   }
				  
			   }
			   
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}while (str!=null);
		
		return parser;
	}

	@Override
	protected IODMModelParser parseInputFile(NetType type, IFileReader[] din,
			String encoding) throws Exception {
		throw new Exception("Method not implemented");
	}
	
	private boolean isArgumentFieldsCompleted(String str){
		
		boolean leftParenthesis=false;
		boolean rightParenthesis=false;

		if(str.indexOf("(")>-1)leftParenthesis=true;
		
        rightParenthesis=endsWithRightParenthesis(str);
        
		return leftParenthesis&&rightParenthesis;
		
	}
	
	private boolean endsWithRightParenthesis(String str){
		return str.trim().endsWith(")");
	}
	
	private String getDataType(String str){
		int indexOfLeftParenthesis=str.indexOf("(");
		int indexOfFirstComma=str.indexOf(",");
		String dataType=str.substring(indexOfLeftParenthesis+1, indexOfFirstComma).trim();
		
		return dataType;
		
	}
	/**
	 * now the in-line comment is not considered yet!. 
	 */
	private List<String> getArgumentFields(String str){
		
		int indexOfLeftBracket=str.indexOf("[");
		int indexOfRightBracket=str.indexOf("]");
		String[] arguFields=str.substring(indexOfLeftBracket+1,
				indexOfRightBracket).split(",");
		
		List<String> arguFieldsList=new ArrayList<String>(arguFields.length);
		
		for(int i=0;i<arguFields.length;i++){
			arguFieldsList.add(arguFields[i].trim());
		}
		
		return arguFieldsList;
	}
}
