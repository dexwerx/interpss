package org.ieee.odm.adapter.pwd;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.AclfGenDataXmlType;
import org.ieee.odm.schema.AclfLoadDataXmlType;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.AngleXmlType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.LoadflowLoadXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.VoltageXmlType;
import org.ieee.odm.schema.YUnitType;

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
	private long swingBusNum=-1;
	
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
					   processBusLoadData(str, parser);
				   else if(recordType==RecType.GEN)
					   processBusGenData(str, parser);
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
		double basekV=0, puVolt=0,angle=0,busG=0,busB=0;
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
				basekV=Double.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusPUVolt")){
				puVolt=Double.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusAngle")){
				angle=Double.valueOf(busBasicData[i]);
			}
			
			//'User Input Value: Represents the MW injection that the system
			//would see from the shunt at 1.0 per unit voltage (positive value represents Load)
			//TODO what is its relationship to 'Shunt'
			else if(field.equals("BusG:1")){
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
		bus.setNumber(busNum);
		if(!busName.equals("")) bus.setName(busName);
		if(areaNum!=-1)bus.setAreaNumber(areaNum);
		if(zoneNum!=-1)bus.setZoneNumber(zoneNum);
		
		bus.setBaseVoltage(BaseDataSetter.createVoltageValue(basekV, VoltageUnitType.KV));
		bus.setVoltage(BaseDataSetter.createVoltageValue(puVolt, VoltageUnitType.PU));
		
		if (busG != 0.0 || busB != 0.0) {
			bus.setShuntY(BaseDataSetter.createYValue(busG, busB,YUnitType.MVAR));
		}
		
		if(isSlackBus)swingBusNum=busNum;
		
		//Data specified for other types of Buses(PV,PQ) is defined in Load and Gen data.
		
		} catch (ODMException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private void processBusLoadData(String busLoadDataStr,AclfModelParser parser){
		/*
		 * DATA (LOAD, [BusNum,LoadID,LoadStatus,LoadSMW,LoadSMVR,LoadIMW,LoadIMVR,LoadZMW,LoadZMVR,
   AreaNum,ZoneNum])
		 */
		
		long busNum=-1;
		String busId="",loadId="";
		double loadSMVR=0,loadSMW=0,loadIMVR=0,loadIMW=0,loadZMVR=0,loadZMW=0;
		int areaNum=-1,zoneNum=-1;
		boolean loadOnLine=false;
		
		String[] loadData=getDataFields(busLoadDataStr, dataSeparator);
		int i=0;
		for(String s:argumentFileds){
			if(s.equals("BusNum")) busNum=Long.valueOf(loadData[i]); //mandatory filed
			else if(s.equals("LoadID")) loadId=loadData[i];
			else if(s.equals("LoadStatus")) {
				if(loadData[i].equals("Closed"))loadOnLine=true;
			}
			else if(s.equals("LoadSMW")) loadSMW=Double.valueOf(loadData[i]);
			else if(s.equals("LoadSMVR"))loadSMVR=Double.valueOf(loadData[i]);
			else if(s.equals("LoadIMW")) loadIMW=Double.valueOf(loadData[i]);
			else if(s.equals("LoadIMVR"))loadSMVR=Double.valueOf(loadData[i]);
			else if(s.equals("LoadZMW")) loadZMW=Double.valueOf(loadData[i]);
			else if(s.equals("LoadZMVR"))loadSMVR=Double.valueOf(loadData[i]);
			else if(s.equals("AreaNum")) areaNum=Integer.valueOf(loadData[i]);
			else if(s.equals("ZoneNum")) zoneNum=Integer.valueOf(loadData[i]);
		    i++;		
		}
		
		busId=parser.BusIdPreFix+busNum;
		
		LoadflowBusXmlType bus=parser.getAclfBus(busId);
		//TODO no loadId in ODM
		
		if(loadSMW!=0||loadSMVR!=0){
			if(loadIMW!=0||loadIMVR!=0||loadZMW!=0||loadZMVR!=0)
			  AclfDataSetter.setZIPLoadData(bus, loadSMW, loadSMVR, loadIMW, loadIMVR,
					  loadZMW, loadZMVR, ApparentPowerUnitType.MVA);
		    
			  else AclfDataSetter.setLoadData(bus, LFLoadCodeEnumType.CONST_P, 
				loadSMW, loadSMVR, ApparentPowerUnitType.MVA);
		}
		//TODO constI, constZ part of load is not processed. 
		
	}
	
	private void processBusGenData(String busGenDataStr,AclfModelParser parser){
		/*
		 * DATA (GEN, [BusNum,GenID,GenStatus,GenMW,GenMVR,GenAGCAble,GenEnforceMWLimits,GenMWMin,
   GenMWMax,GenParFac,GenAVRAble,GenVoltSet,GenRegNum,GenMVRMin,GenMVRMax,
   GenRMPCT,GenUseCapCurve,GenUseLDCRCC,GenXLDCRCC,GenMVABase,GenZR,GenZX,
   GenStepR,GenStepX,GenStepTap,AreaNum,ZoneNum])
		 */
		/*Now we Only consider the following 
		  BusNum,GenID,GenStatus,GenMW,GenMVR,GenEnforceMWLimits,GenMWMin,
		   GenMWMax GenRegNum,GenMVRMin,GenMVRMax,GenMVABase,AreaNum,ZoneNum
		*/
	long busNum=-1,regBusNum=-1;
	int areaNum=-1,zoneNum=-1;
	String genId="";
	double genMW=0,genMVR=0,genMWMin=0,genMWMax=0,genMVRMin=-9999,genMVRMax=9999,genMVABase=100;
	boolean genOnLine=false;
	boolean pLimitForced=true;
	boolean isPV=false;
	String[] genData=getDataFields(busGenDataStr, dataSeparator);
	int i=0;
	/*
	for(String s:argumentFileds){
		if(s.equals("BusNum")) busNum=Long.valueOf(genData[i]); //mandatory filed
		else if(s.equals("GenID")) genId=genData[i];
		else if(s.equals("GenStatus")) {
			if(genData[i].equals("Closed"))genOnLine=true;
		}
		else if(s.equals("GenMW")) genMW=Double.valueOf(genData[i]);
		else if(s.equals("GenMVR"))genMVR=Double.valueOf(genData[i]);
		else if(s.equals("GenEnforceMWLimits")){
			if(genData[i].equalsIgnoreCase("YES"))pLimit=true;
		}
		else if(s.equals("GenMWMin"))genMWMin=Double.valueOf(genData[i]);
		else if(s.equals("GenMWMax")) genMWMax=Double.valueOf(genData[i]);
		else if(s.equals("GenMVRMin"))genMVRMin=Double.valueOf(genData[i]);
		else if(s.equals("GenMVRMax")) genMVRMax=Double.valueOf(genData[i]);
		else if(s.equals("GenMVABase")) genMVABase=Double.valueOf(genData[i]);
		else if(s.equals("AreaNum")) areaNum=Integer.valueOf(genData[i]);
		else if(s.equals("ZoneNum")) zoneNum=Integer.valueOf(genData[i]);
		
	    i++;		
	}
	*/
	i=argumentFileds.indexOf("BusNum") ;
	if(i!=-1)busNum=Long.valueOf(genData[i]); //mandatory field
	
	i=argumentFileds.indexOf("GenID"); if(i!=-1)genId=genData[i];
	
	i=argumentFileds.indexOf("GenStatus"); 
    if(i!=-1)genOnLine=genData[i].equals("Closed")?true:false;
	
    i=argumentFileds.indexOf("GenMW"); if(i!=-1)genMW=Double.valueOf(genData[i]);
    
    i=argumentFileds.indexOf("GenEnforceMWLimits"); 
    if(i!=-1)pLimitForced=genData[i].equals("YES")?true:false;
    
    i=argumentFileds.indexOf("GenRegNum"); if(i!=-1)regBusNum=Integer.valueOf(genData[i]);
    
    i=argumentFileds.indexOf("GenMVR"); if(i!=-1)genMVR=Double.valueOf(genData[i]);
	i=argumentFileds.indexOf("GenMWMin"); if(i!=-1)genMWMin=Double.valueOf(genData[i]);
	
	i=argumentFileds.indexOf("GenMWMax"); if(i!=-1)genMWMax=Double.valueOf(genData[i]);
	i=argumentFileds.indexOf("GenMVRMin"); if(i!=-1)genMVRMin=Double.valueOf(genData[i]);
	i=argumentFileds.indexOf("GenMVRMax"); if(i!=-1)genMVRMax=Double.valueOf(genData[i]);
	i=argumentFileds.indexOf("GenMVABase"); if(i!=-1) genMVABase=Double.valueOf(genData[i]);
	i=argumentFileds.indexOf("AreaNum"); if(i!=-1)areaNum=Integer.valueOf(genData[i]);
	i=argumentFileds.indexOf("ZoneNum"); if(i!=-1)zoneNum=Integer.valueOf(genData[i]);
	
	String busId=parser.BusIdPreFix+busNum;
	LoadflowBusXmlType bus=parser.getAclfBus(busId);
	
	if(regBusNum!=-1){
		if(regBusNum==busNum) {
			if(busNum!=swingBusNum){//This bus is a PV bus
				VoltageXmlType v=bus.getVoltage();
				AclfDataSetter.setGenData(bus,
						LFGenCodeEnumType.PV,
						v.getValue(), v.getUnit(),
						0, AngleUnitType.DEG,genMW, genMVR, ApparentPowerUnitType.MVA);
				
				LoadflowGenXmlType equivGen=bus.getGenData().getEquivGen();
				
				equivGen.setPLimit(BaseDataSetter.createActivePowerLimit(
							genMWMax, genMWMin, ActivePowerUnitType.MW));
				
				equivGen.getPLimit().setActive(pLimitForced);
				
				equivGen.setQLimit(BaseDataSetter.createReactivePowerLimit( 
						genMVRMax, genMVRMin, ReactivePowerUnitType.MVAR));
				
				if(areaNum!=-1)equivGen.setAreaNumber(areaNum);
				if(zoneNum!=-1)equivGen.setZoneNumber(zoneNum);
				
				
			}
			else{ //swing bus
				VoltageXmlType v=bus.getVoltage();
				AngleXmlType angle=bus.getAngle();
				AclfDataSetter.setGenData(bus,
						LFGenCodeEnumType.SWING,
						v.getValue(), v.getUnit(),
						angle.getValue(), angle.getUnit(),genMW, genMVR, ApparentPowerUnitType.MVA);
				
				LoadflowGenXmlType equivGen=bus.getGenData().getEquivGen();
				//p limit
				equivGen.setPLimit(BaseDataSetter.createActivePowerLimit(
						genMWMax, genMWMin, ActivePowerUnitType.MW));
				//TODO need to set plimitforced?
				equivGen.getPLimit().setActive(pLimitForced);
				//q limit
				equivGen.setQLimit(BaseDataSetter.createReactivePowerLimit( 
						genMVRMax, genMVRMin, ReactivePowerUnitType.MVAR));
				
				if(areaNum!=-1)equivGen.setAreaNumber(areaNum);
				if(zoneNum!=-1)equivGen.setZoneNumber(zoneNum);
				
			}
			
		}
		else{// the regulated bus is a PV bus
			//TODO how to define a gen bus to control a remote bus;
			//it is a PQ bus itself?
			
			//set remote bus data
			String regBusId=parser.BusIdPreFix+regBusNum;
			LoadflowBusXmlType regBus=parser.getAclfBus(regBusId);
			VoltageXmlType vSet=regBus.getVoltage();
			
			AclfDataSetter.setGenData(regBus,
					LFGenCodeEnumType.PV,
					vSet.getValue(), vSet.getUnit(),
					0, AngleUnitType.DEG,0, 0, ApparentPowerUnitType.MVA);
			
			// set this gen bus data
			VoltageXmlType v=bus.getVoltage();
			AclfDataSetter.setGenData(bus,
					LFGenCodeEnumType.PQ,
					0, VoltageUnitType.PU,
					0, AngleUnitType.DEG,genMW, genMVR, ApparentPowerUnitType.MVA);
			
			LoadflowGenXmlType equivGen=bus.getGenData().getEquivGen();
			
			equivGen.setPLimit(BaseDataSetter.createActivePowerLimit(
						genMWMax, genMWMin, ActivePowerUnitType.MW));
			
			equivGen.getPLimit().setActive(pLimitForced);
			
			equivGen.setQLimit(BaseDataSetter.createReactivePowerLimit( 
					genMVRMax, genMVRMin, ReactivePowerUnitType.MVAR));
			
			if(areaNum!=-1)equivGen.setAreaNumber(areaNum);
			if(zoneNum!=-1)equivGen.setZoneNumber(zoneNum);
			
		}
	}
	
	
	
	
	
	
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
