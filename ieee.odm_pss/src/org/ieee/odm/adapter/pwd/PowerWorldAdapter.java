package org.ieee.odm.adapter.pwd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.pwd.impl.BranchDataProcessor;
import org.ieee.odm.adapter.pwd.impl.BusDataProcessor;
import org.ieee.odm.adapter.pwd.impl.NetDataProcessor;
import org.ieee.odm.adapter.pwd.impl.PWDDataParser;
import org.ieee.odm.adapter.pwd.impl.PWDHelper;
import org.ieee.odm.adapter.pwd.impl.TransformerDataProcessor;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.aclf.AclfParserHelper;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.ShuntCompensatorDataXmlType;
 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.1  04/03/2012
  * @author Tony Huang
  * 
  */
public class PowerWorldAdapter extends AbstractODMAdapter{
	
	
	private  static final String Token_Data="DATA";
	private  static final String Token_Bus="BUS";
	private  static final String Token_Load="LOAD";
	private  static final String Token_Gen="GEN";
	private  static final String Token_Shunt="SHUNT";
	private  static final String Token_Branch="BRANCH";
	private  static final String Token_XFormer="TRANSFORMER";
	private  static final String Token_3WXFormer="3WXFORMER";
	private  static final String Token_Area="AREA";
	private  static final String Token_Zone="ZONE";
	private  static final String Token_CaseInfo="PWCASEINFORMATION";//PWCASEINFORMATION
	
	private enum RecType{BUS,LOAD,GEN,SHUNT,BRANCH,XFORMER,TRI_W_XFORMER,AREA,ZONE,CASE_INFO,Undefined};

	public static enum FileTypeSpecifier{CSV,Blank};
	public static FileTypeSpecifier dataSeparator=FileTypeSpecifier.Blank;//By default


	public PowerWorldAdapter(){
		super();
	}
	
	@Override
	protected IODMModelParser parseInputFile(IFileReader din, String encoding) {
		
		AclfModelParser parser=new AclfModelParser(encoding);
		
		parser.setLFTransInfo(OriginalDataFormatEnumType.POWER_WORLD);
		
		// BaseCase object, plus busRecList and BranchRecList are created 
		LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
		baseCaseNet.setId("Base_Case_from_PowerWorld_format");
		baseCaseNet.setBasePower(BaseDataSetter.createPowerMvaValue(100.0));//not defined in the file
		String str;
		String dataType;
		RecType recordType=null;

		NetDataProcessor netProc = new NetDataProcessor(parser);
		BusDataProcessor busProc = new BusDataProcessor(parser);
		BranchDataProcessor branchProc = new BranchDataProcessor(parser);
		TransformerDataProcessor xfrProc = new TransformerDataProcessor(parser);
		try{
			do{
				str=din.readLine();
				//System.out.println("processing data#"+str);
				if(str!=null){
				  if(str.startsWith(Token_Data)){
					dataType=PWDHelper.getDataType(str);
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
				    	//TODO add undefined record type
				    	recordType=RecType.Undefined;
				    	ODMLogger.getLogger().info("Undifined data type:"+dataType);
				    }
				    
				    //get all the argument fields of a record, then save them to a list.
				    while(!PWDHelper.isArgumentFieldsCompleted(str)){
						str+=din.readLine();
					}
				    //parse meta data
				    switch(recordType){
				    case BUS     :busProc.parseMetadata(str);break;
				    case GEN     :busProc.parseMetadata(str);break;
				    case LOAD    :busProc.parseMetadata(str);break;
				    case SHUNT   :busProc.parseMetadata(str);break;
				    case BRANCH  :branchProc.parseMetadata(str);break;
				    case XFORMER :xfrProc.parseMetadata(str);break;
				    case ZONE    :netProc.parseMetadata(str);break;
				    case AREA    :netProc.parseMetadata(str);break;
				   
				    }

				    
				} //end of processing data type
				
				
				 else if(str.trim().startsWith("//"))
					 ODMLogger.getLogger().fine("comments:"+str);
				 else if(str.trim().startsWith("{"))
				    	ODMLogger.getLogger().info(recordType.toString()+" type data begins");
				 
				 else if(str.trim().startsWith("}")){
						ODMLogger.getLogger().info(recordType.toString()+" type data ends");
				 }
				 // start processing record data
				//TODO assume all data in one line except the branch and transformer type 
				 else if(!str.trim().isEmpty()){
		
					   if(recordType==RecType.BUS) 
						   busProc.processBusBasicData(str);
					   else if(recordType==RecType.LOAD)
						   busProc.processBusLoadData(str);
					   else if(recordType==RecType.GEN)
						   busProc.processBusGenData(str);
					   else if(recordType==RecType.SHUNT)
						   busProc.processBusShuntData(str);
					   else if(recordType==RecType.BRANCH){
						  
						   //NE-ISO file uses multiple lines to store some data, e.g. transformer data;
						 //clear the processed data in memory, or it will cause fieldTable size wrong
						   branchProc.clearProcessedData();  
						   //System.out.println("processing #"+str);
						   while(!branchProc.parseData(str,true))
								str=din.readLine();
						 
						   //NOTE:No need to parse data within branch processor anymore
						   branchProc.processBranchData(str);
						  
						   
					   }
						   //Here we assumed that TRANSFOMER part data is supplementary to the BRANCH part data
					       //and is only to provide the transformer control/adjustment data
					   else if(recordType==RecType.XFORMER)
						   xfrProc.processXFormerControlData(str);
					   else if(recordType==RecType.TRI_W_XFORMER){}
						   //xfrProc.process3WXFomerData(str);
					   else if(recordType==RecType.AREA)
						   netProc.processAreaData(str);
					   else if(recordType==RecType.ZONE)
						   netProc.processZoneData(str);
					   else{
						  // ODMLogger.getLogger().warning("unsupported data #"+str);
					   }
					  
				   }
				}//end of if str is not null  
			}while (str!=null);
		}catch(Exception e){
			e.printStackTrace();
			ODMLogger.getLogger().severe(e.toString());
		}
		
		postProcessing(parser);
		
		return parser;
	}
	
	public boolean postProcessing(AclfModelParser parser) {
		LoadflowNetXmlType baseCaseNet = parser.getAclfNet(); 

		for (JAXBElement<? extends BusXmlType> bus : baseCaseNet.getBusList().getBus()) {
			LoadflowBusXmlType busRec = (LoadflowBusXmlType)bus.getValue();
			// turn-off bus if bus voltage is 0.0
			if (busRec.getVoltage().getValue() < 0.1) {
				busRec.setOffLine(Boolean.TRUE);
			}
		}

		AclfParserHelper.createBusEquivShuntData(parser);
		
		return true;
	}	

	@Override
	protected IODMModelParser parseInputFile(NetType type, IFileReader[] din, String encoding) {
		ODMLogger.getLogger().severe("Method not implemented");
		return null;
	}
	
	
}
