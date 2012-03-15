package org.ieee.odm.adapter.pwd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;

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
	
	private enum FileTypeSpecifier{CVS,Blank};
	private enum RecType{BUS,LOAD,GEN,SHUNT,BRANCH,XFORMER,TRI_W_XFORMER,AREA,ZONE};
	private List<String> argumentFileds;
	private FileTypeSpecifier dataSeparator=FileTypeSpecifier.Blank;//By default
	
	public PowerWorldAdapter(){
		super();
	}
	
	@Override
	protected IODMModelParser parseInputFile(IFileReader din, String encoding)
			throws Exception {
		
		AclfModelParser parser=new AclfModelParser(encoding);
		
		//TODO 
		//parser.setLFTransInfo(OriginalDataFormatEnumType.PowerWorld)
		
		// BaseCase object, plus busRecList and BranchRecList are created 
		LoadflowNetXmlType baseCaseNet = parser.getAclfNet();
		baseCaseNet.setId("Base_Case_from_PowerWorld_format");
		
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
			    else ODMLogger.getLogger().warning("Undifined data type:"+dataType);
			    
			    //get all the argument fields of a record;
			    while(!isDataCompleted(str)){
					str+=din.readLine();
				}
			    argumentFileds=getArgumentFields(str);
			    
			} //end of processing data type 
			 else if(str.startsWith("//"))
				 ODMLogger.getLogger().fine("comments:"+str);
			 else if(str.startsWith("{"))
			    	ODMLogger.getLogger().info(recordType.toString()+" type data follows");
			 
			 else if(str.startsWith("}"))
					ODMLogger.getLogger().info(recordType.toString()+" type data ends");
			
			 // start processing record data
			 else if(!str.trim().isEmpty()){
				 
				   if(recordType==RecType.BUS) 
					   processBusBasicData(str, parser);
				   else if(recordType==RecType.LOAD)
					   processBusLoadData(str, baseCaseNet);
				   else if(recordType==RecType.GEN)
					   processBusGenData(str, baseCaseNet);
				   else if(recordType==RecType.SHUNT)
					   processShuntData(str, baseCaseNet);
				   else if(recordType==RecType.BRANCH)
					   processBranchData(str, baseCaseNet);
				   else if(recordType==RecType.XFORMER)
					   processXFormerData(str, baseCaseNet);
				   else if(recordType==RecType.TRI_W_XFORMER)
					   process3WXFomerData(str, baseCaseNet);
				   else if(recordType==RecType.AREA)
					   processAreaData(str, baseCaseNet);
				   else if(recordType==RecType.ZONE)
					   processZoneData(str, baseCaseNet);
			   }
			   
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}while (str!=null);
		
		return null;
	}

	@Override
	protected IODMModelParser parseInputFile(NetType type, IFileReader[] din,
			String encoding) throws Exception {
		//TODO
		return null;
	}
	
	private void processBusBasicData(String busDataStr,AclfModelParser parser){
		/*
		 * DATA (BUS, [BusNum,BusName,BusNomVolt,BusPUVolt,BusAngle,BusG:1,BusB:1,AreaNum,ZoneNum,
            SubNum,BusSlack])
		 */
		
		//TODO no bus type defined here, delay the type definition until Gen Data is processed
		
		long busNum=-1;
		int areaNum=-1,zoneNum=-1;// purposely set to -1, a not real number;
		String busName="",busId="";
		double nomVolt, puVolt,angle,busG,busB;
		boolean isSlackBus=false;
		
		String[] busBasicData=getDataFields(busDataStr, dataSeparator);
		int i=0;
		try {
		if(argumentFileds!=null){
		for(String field:argumentFileds){// fields are already trimmed.
			 // Note the sequence of the arguments are not defined by PowerWorld, 
			//if statement is used here to judge their existence.
			if(field.equals("BusNum")){
				busNum=Long.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusName")){
				busName=busBasicData[i];
			}
			else if(field.equals("BusNomVolt")){
				nomVolt=Double.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusPUVolt")){
				puVolt=Double.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusAngle")){
				angle=Double.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusG:1")){// Eliminate the "1"?
				busG=Double.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusB:1")){
				busB=Double.valueOf(busBasicData[i]);
			}
			else if(field.equals("AreaNum")){
				areaNum=Integer.valueOf(busBasicData[i]);
			}
			else if(field.equals("ZoneNum")){
				zoneNum=Integer.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusSlack")){
				if(busBasicData[i].equals("YES")) isSlackBus=true;
			}
			i++;
		  }//end for
		}
		if(busNum==-1) logErr("bus Num is not defined yet!");
		busId=AclfModelParser.BusIdPreFix+busNum;
		
		LoadflowBusXmlType bus=parser.createAclfBus(busId);
		bus.setId(busId);
		if(!busName.equals("")) bus.setName(busName);
		bus.setNumber(busNum);
		if(areaNum!=-1)bus.setAreaNumber(areaNum);
		if(zoneNum!=-1)bus.setZoneNumber(zoneNum);
		
		//TODO work on other fields
		} catch (ODMException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private void processBusLoadData(String busLoadDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	
	private void processBusGenData(String busGenDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	private void processShuntData(String ShuntDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	private void processBranchData(String branchDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	private void processXFormerData(String xfomerDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	
	private void process3WXFomerData(String triWXformerDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	private void processAreaData(String areaDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	private void processZoneData(String zoneDataStr,LoadflowNetXmlType baseCaseNet){
		
	}
	private boolean isDataCompleted(String str){
		
		boolean leftParenthesis=false;
		boolean rightParenthesis=false;

		for(int i=0;i<str.length();i++){
			if (str.charAt(i)=='(') {leftParenthesis=true;break;}
		}
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
	
	private String[] getDataFields(String str,FileTypeSpecifier type){
		String separator;
		
		if(type==FileTypeSpecifier.Blank) separator=" ";//by default
		else separator=","; 
		
		String[] arguFields=str.split(separator);
		return arguFields;
	}
	
	
    
}
