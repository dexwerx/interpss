package org.ieee.odm.adapter.pwd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.pwd.impl.BranchDataProcessor;
import org.ieee.odm.adapter.pwd.impl.BusDataProcessor;
import org.ieee.odm.adapter.pwd.impl.NetDataProcessor;
import org.ieee.odm.adapter.pwd.impl.PWDHelper;
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
	 * Stores input data name/value pairs, For example,  
	 * 
		  [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA,
             LineCMVA,LineShuntMW,LineShuntMW:1,LineShuntMVR,LineShuntMVR:1,LineXfmr,LineTap,
             LinePhase,SeriesCapStatus]
		    4     5 " 1" "Closed"  0.000000  0.100000  0.000000  0.000000  1000.000  1000.000  1000.000     0.000     0.000     0.000     0.000  "YES"    0.993750   0.000000 "Not Bypassed"
	 * 
	 * It goes two steps:
	 * 
	 * 	Step-1: Create data definition. It looks like the following after Step-1
	 * 
	 *       <"BusNum",null>, <"BusNum:1",null>, ....	 
	 * 
	 *  Step-2: Parsing the input date string, After the parsing, the nv pair list will store
	 * 
	 *       <"BusNum","4">, <"BusNum:1","5">, ....	 
	 *   
	 * [Mike 08/12/2012] 
	 * 
	 * The header and data could be presented in multiple lines, as shown below
	 *  
		  [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA,
             LineCMVA,LineShuntMW,LineShuntMW:1,LineShuntMVR,LineShuntMVR:1,LineXfmr,LineTap,
             LinePhase,SeriesCapStatus]
		    4     5 " 1" "Closed"  0.000000  0.100000  0.000000  0.000000  1000.000  1000.000  1000.000     
		    0.000     0.000     0.000     0.000  "YES"    0.993750   0.000000 "Not Bypassed"
		    
		 Assumptions:
		 
		    - The number of header fields should be exactly the same as the number of data fields    
	 *       
  * @version 1.0  04/03/2012
  * @author Tony Huang
  * 
  */

public class PowerWorldAdapter extends AbstractODMAdapter{
	public static class NVPair {
		public String name, value;
		public NVPair(String name) {this.name = name;}
		public String toString() { return "<" + name + "," + (value==null?"null":value) + ">"; }
	}
	private List<NVPair> inputNvPairs;
	
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

	//public static enum FileTypeSpecifier{CSV,Blank};
	public static enum FileTypeSpecifier{Comma,Space};   
	public static FileTypeSpecifier dataSeparator=FileTypeSpecifier.Space;//By default

	public PowerWorldAdapter(){
		super();
		this.inputNvPairs = new ArrayList<NVPair>();
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
		
		NetDataProcessor netProc = new NetDataProcessor(this.inputNvPairs, parser);
		BusDataProcessor busProc = new BusDataProcessor(this.inputNvPairs, parser);
		BranchDataProcessor branchProc = new BranchDataProcessor(this.inputNvPairs, parser);

		String separator = dataSeparator==FileTypeSpecifier.Space? " " : ",";
		try{
			do{
				str=din.readLine();
				//System.out.println("processing data#"+str);
		
				if(str!=null){
					/*
					 * Process header info
					 */
					if(str.startsWith(Token_Data)){
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
						else {
							//TODO add undefined record type
							recordType=RecType.Undefined;
							ODMLogger.getLogger().warning("Undifined data type:"+dataType);
						}
				    
						//get all the argument fields of a record, then save them to a list.
						while(!isArgumentFieldsCompleted(str)){
							str+=din.readLine();
						}
				    
						// parse the str for the field definition 
						parseFieldNames(str);
				    
					} //end of processing data type
				
					/*
					 * Process comments and extra info
					 */
					else if(str.trim().startsWith("//"))
						ODMLogger.getLogger().fine("comments:"+str);
					else if(str.trim().startsWith("{"))
				    	ODMLogger.getLogger().info(recordType.toString()+" type data begins");
				 
					else if(str.trim().startsWith("}")){
						ODMLogger.getLogger().info(recordType.toString()+" type data ends");
					}
					
					/*
					 * Process record data
					 */
					else if(!str.trim().isEmpty()){
						if(recordType==RecType.BUS) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
							// at this point the input data line(s) are parsed and the data fields 
							// are stored in the inputNvPairs
							busProc.processBusBasicData();
						}
						else if(recordType==RecType.LOAD) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
							busProc.processBusLoadData();
						}
						else if(recordType==RecType.GEN) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
							busProc.processBusGenData(str);
						}
						else if(recordType==RecType.SHUNT) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
							busProc.processBusShuntData();
						}
						else if(recordType==RecType.BRANCH){
							//NE-ISO file uses multiple lines to store some data, e.g. transformer data;
							/*
							while(!isDataFiledsCompleted(str)){
								str+=din.readLine();
							}
							*/
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
							branchProc.processBranchData();
						}
						else if(recordType==RecType.XFORMER) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
						   branchProc.processXFormerData();
						}
						else if(recordType==RecType.TRI_W_XFORMER) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
						   branchProc.process3WXFomerData();
						}
						else if(recordType==RecType.AREA) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
						   netProc.processAreaData();
						}
						else if(recordType==RecType.ZONE) {
							while(!PWDHelper.parseDataFields(str, this.inputNvPairs))
								str+=din.readLine();
						   netProc.processZoneData();
						}
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
	
	private boolean isArgumentFieldsCompleted(String str){
		
		boolean leftParenthesis=false;
		boolean rightParenthesis=false;

		if(str.indexOf("(")>-1)leftParenthesis=true;
		
        rightParenthesis=endsWithRightParenthesis(str);
        
		return leftParenthesis&&rightParenthesis;
		
	}
	
	/* not used anymore
	private boolean isDataFiledsCompleted(String dataStr){
		//
		// data fields are completed only when all nvPairs in the list are completed or
		// the last item.value != null
		//
		boolean dataCompleted=true;
		PWDHelper.parseDataFields(dataStr, inputNvPairs);
		for(NVPair nv:inputNvPairs){
			if(nv.value==null){
				dataCompleted=false;
				break;
			}
		}
		return dataCompleted;
		return PWDHelper.parseDataFields(dataStr, inputNvPairs);
	}
	*/

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
	private void parseFieldNames(String str){
		
		int indexOfLeftBracket=str.indexOf("[");
		int indexOfRightBracket=str.indexOf("]");
		String[] arguFields=str.substring(indexOfLeftBracket+1,
				indexOfRightBracket).split(",");
		this.inputNvPairs.clear();
		for(int i=0;i<arguFields.length;i++){
			this.inputNvPairs.add(new NVPair(arguFields[i].trim()));
		}
	}
}
