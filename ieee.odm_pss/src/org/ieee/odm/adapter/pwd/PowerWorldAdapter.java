package org.ieee.odm.adapter.pwd;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.AngleXmlType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NetAreaXmlType;
import org.ieee.odm.schema.NetZoneXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.ShuntCompensatorBlockXmlType;
import org.ieee.odm.schema.ShuntCompensatorDataXmlType;
import org.ieee.odm.schema.ShuntCompensatorModeEnumType;
import org.ieee.odm.schema.ShuntCompensatorXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.VoltageXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
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
	
	private enum FileTypeSpecifier{CSV,Blank};
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
			    
			    //get all the argument fields of a record, then save them to a list.
			    while(!isArgumentFieldsCompleted(str)){
					str+=din.readLine();
				}
			    argumentFileds=getArgumentFields(str);
			    
			} //end of processing data type
			
			 else if(str.startsWith("//"))
				 ODMLogger.getLogger().fine("comments:"+str);
			 else if(str.startsWith("{"))
			    	ODMLogger.getLogger().info(recordType.toString()+" type data begins");
			 
			 else if(str.startsWith("}")){
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
					   processBusBasicData(str, parser);
				   else if(recordType==RecType.LOAD)
					   processBusLoadData(str, parser);
				   else if(recordType==RecType.GEN)
					   processBusGenData(str, parser);
				   else if(recordType==RecType.SHUNT)
					   processBusShuntData(str, parser);
				   else if(recordType==RecType.BRANCH)
					   processBranchData(str, parser);
				   else if(recordType==RecType.XFORMER)
					   processXFormerData(str, parser);
				   else if(recordType==RecType.TRI_W_XFORMER)
					   process3WXFomerData(str, baseCaseNet);
				   else if(recordType==RecType.AREA)
					   processAreaData(str, parser);
				   else if(recordType==RecType.ZONE)
					   processZoneData(str, parser);
				  
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
		//TODO
		return null;
	}
	
	private void processBusBasicData(String busDataStr,AclfModelParser parser){
		/*
		 * DATA (BUS, [BusNum,BusName,BusNomVolt,BusPUVolt,BusAngle,BusG:1,BusB:1,AreaNum,ZoneNum,
            SubNum,BusSlack])
		 */
		
		
		long busNum=-1;
		int areaNum=-1,zoneNum=-1;// purposely set to -1, a not real number;
		String busName="",busId="";
		double basekV=0, puVolt=0,angle=-360,busG=0,busB=0;
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
		if(angle!=-360)bus.setAngle(BaseDataSetter.createAngleValue(angle,AngleUnitType.DEG));
		
		if (busG != 0.0 || busB != 0.0) {
			bus.setShuntY(BaseDataSetter.createYValue(busG, busB,YUnitType.MVAR));
		}
		
		if(isSlackBus){
			swingBusNum=busNum;
			
		}
		
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
		if(regBusNum==busNum) { //this generator control the bus it connects to
			
			if(busNum!=swingBusNum){//This bus is a PV bus
				VoltageXmlType v=bus.getVoltage();
				AclfDataSetter.setGenData(bus,
						LFGenCodeEnumType.PV,
						v.getValue(), v.getUnit(),
						0, AngleUnitType.DEG,genMW, genMVR, ApparentPowerUnitType.MVA);
				
				LoadflowGenXmlType equivGen=bus.getGenData().getEquivGen();
				equivGen.setId(genId);
				equivGen.setOffLine(!genOnLine);
				
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
				equivGen.setId(genId);
				
				equivGen.setOffLine(!genOnLine);
								
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
			
			//TODO how to define a remote bus that a generator controls/regulates, as a PV?
			
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
			equivGen.setId(genId);
			equivGen.setPLimit(BaseDataSetter.createActivePowerLimit(
						genMWMax, genMWMin, ActivePowerUnitType.MW));
			
			equivGen.getPLimit().setActive(pLimitForced);
			
			equivGen.setQLimit(BaseDataSetter.createReactivePowerLimit( 
					genMVRMax, genMVRMin, ReactivePowerUnitType.MVAR));
			
			if(areaNum!=-1)equivGen.setAreaNumber(areaNum);
			if(zoneNum!=-1)equivGen.setZoneNumber(zoneNum);
			// define the remote control bus
			equivGen.setRemoteVoltageControlBus(parser.createBusRef(regBusId));
			
		}
	}
	
	
	
	
	
	
	}
	private void processBusShuntData(String shuntDataStr,AclfModelParser parser){
		/*
		 * DATA (SHUNT, [BusNum,ShuntID,AreaNum,ZoneNum,SSRegNum,SSStatus,SSCMode,SSVHigh,SSVLow,SSNMVR,
            SSBlockNumSteps,SSBlockMVarPerStep,SSBlockNumSteps:1,SSBlockMVarPerStep:1,
            SSBlockNumSteps:2,SSBlockMVarPerStep:2,SSBlockNumSteps:3,SSBlockMVarPerStep:3,
            SSBlockNumSteps:4,SSBlockMVarPerStep:4,SSBlockNumSteps:5,SSBlockMVarPerStep:5,
            SSBlockNumSteps:6,SSBlockMVarPerStep:6,SSBlockNumSteps:7,SSBlockMVarPerStep:7,
            SSBlockNumSteps:8,SSBlockMVarPerStep:8,SSBlockNumSteps:9,SSBlockMVarPerStep:9])
		 */
		
		long busNum=-1,regBusNum=-1;
		int areaNum=-1,zoneNum=-1,steps1=0,steps2=0;
		String shuntId="";
		boolean closed=false;
		double vHigh=1.0,vLow=1.0,normalMVR=0,MVarPerStep1=0,MVarPerStep2=0;
		ShuntCompensatorModeEnumType mode=null; //Control Mode: Fixed, Discrete, Continuous, or Bus Shunt;
		
		String[] shuntData=getDataFields(shuntDataStr, dataSeparator);
		int i=-1;
		i=argumentFileds.indexOf("BusNum") ;
		if(i!=-1)busNum=Long.valueOf(shuntData[i]); //mandatory field
		
		i=argumentFileds.indexOf("SSRegNum") ;
		if(i!=-1)regBusNum=Long.valueOf(shuntData[i]);
		
		i=argumentFileds.indexOf("ShuntID"); if(i!=-1)shuntId=shuntData[i];
		
		i=argumentFileds.indexOf("SSStatus"); 
	    if(i!=-1)closed=shuntData[i].equals("Closed")?true:false;
		
	    i=argumentFileds.indexOf("AreaNum"); if(i!=-1)areaNum=Integer.valueOf(shuntData[i]);
		i=argumentFileds.indexOf("ZoneNum"); if(i!=-1)zoneNum=Integer.valueOf(shuntData[i]);
		
		i=argumentFileds.indexOf("SSCMode");
		mode=shuntData[i].equals("Discrete")?ShuntCompensatorModeEnumType.DISCRETE:
			(shuntData[i].equals("Continuous")?ShuntCompensatorModeEnumType.CONTINUOUS:
				ShuntCompensatorModeEnumType.FIXED);
		
		i=argumentFileds.indexOf("SSVHigh") ;
		if(i!=-1)vHigh=Double.valueOf(shuntData[i]);
		
		i=argumentFileds.indexOf("SSVLow") ;
		if(i!=-1)vLow=Double.valueOf(shuntData[i]);
		
		i=argumentFileds.indexOf("SSNMVR") ;
		if(i!=-1)normalMVR=Double.valueOf(shuntData[i]);
		
		//TODO How to determine the number of blocks
		i=argumentFileds.indexOf("SSBlockNumSteps") ;
		if(i!=-1)steps1=new Double(shuntData[i]).intValue();
		
		i=argumentFileds.indexOf("SSBlockMVarPerStep");
		if(i!=-1)MVarPerStep1=Double.valueOf(shuntData[i]);
		
		
		i=argumentFileds.indexOf("SSBlockNumSteps:1") ;
		if(i!=-1)steps2=new Double(shuntData[i]).intValue();
		
		i=argumentFileds.indexOf("SSBlockMVarPerStep:1");
		if(i!=-1)MVarPerStep2=Double.valueOf(shuntData[i]);
		
		
		String busId=parser.BusIdPreFix+busNum;
		LoadflowBusXmlType bus=parser.getAclfBus(busId);
		
		AclfDataSetter.setShuntCompensatorData(bus, mode, normalMVR, vHigh, vLow);
		ShuntCompensatorXmlType shunt=bus.getShuntCompensatorData().getShuntCompensator().get(0);
		// regulate a remote bus
		if(busNum!=regBusNum)
			shunt.setRemoteControlledBus(parser.createBusRef(parser.BusIdPreFix+regBusNum));
		
		if(steps1>0&&MVarPerStep1!=0)
		AclfDataSetter.addShuntCompensatorBlock(bus, steps1, MVarPerStep1, ReactivePowerUnitType.MVAR);
        
		if(steps2>0&&MVarPerStep2!=0)
			AclfDataSetter.addShuntCompensatorBlock(bus, steps2, MVarPerStep2, ReactivePowerUnitType.MVAR);
        //TODO now only two blocks are considered. 
		//This should be enough for almost all real cases, one for capacitive, one for reactive
		
	}
	
	private void processBranchData(String branchDataStr,AclfModelParser parser){
		/*
		 * DATA (BRANCH, [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA,
              LineCMVA,LineShuntMW,LineShuntMW:1,LineShuntMVR,LineShuntMVR:1,LineTap,
              LinePhase,SeriesCapStatus])
		 * 
		 * BusNum-># of fromBus
		 * BusNum:1-># of toBus
		 * LineC->shuntB(Per unit susceptance (B) of branch on the system base), 50% at each end; 
		 * LineG->Per unit conductance (G) of branch on the system base
		 */
		//TODO exact meanings of LineAMVA,LineBMVA,LineCMVA
		long fromBusNum=-1,toBusNum=-1;
		String fromBusId, toBusId,circuitId="1";
		boolean closed=true, isXfmr=false;
		double r=0,x=0,b=0,g=0, // all per unit value on system base;
		       mvaRatingA=9999,mvaRatingB=9999,mvaRatingC=9999,
		       lineTap=1.0;//mvaRatingB,mvaRatingC,
		String[] branchData=getDataFields(branchDataStr, dataSeparator);
		int i=-1;
		try{
		//TODO branch id, NE-ISO use "customString:1" as the corresponding argument;	
		i=argumentFileds.indexOf("BusNum") ;
		fromBusNum=Long.valueOf(branchData[i]); //mandatory field
		
		i=argumentFileds.indexOf("BusNum:1") ;
		toBusNum=Long.valueOf(branchData[i]); //mandatory field
		
		i=argumentFileds.indexOf("LineCircuit"); if(i!=-1)circuitId=branchData[i];
		
		i=argumentFileds.indexOf("LineXfmr"); if(i!=-1)isXfmr=branchData[i].equals("YES")?true:false;
		
		i=argumentFileds.indexOf("LineStatus"); 
	    if(i!=-1)closed=branchData[i].equals("Closed")?true:false;
	    
	    i=argumentFileds.indexOf("LineR"); if(i!=-1)r=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineX"); if(i!=-1)x=Double.valueOf(branchData[i]);
	    i=argumentFileds.indexOf("LineC"); if(i!=-1)b=Double.valueOf(branchData[i]);
	    i=argumentFileds.indexOf("LineG"); if(i!=-1)g=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineAMVA"); 
	    if(i!=-1)mvaRatingA=Double.valueOf(branchData[i]); // line limit rating
	    
	    i=argumentFileds.indexOf("LineBMVA"); //same as LineAMVA:1?
	    if(i!=-1)mvaRatingB=Double.valueOf(branchData[i]);
	    i=argumentFileds.indexOf("LineCMVA"); //same as LineAMVA:2?
	    if(i!=-1)mvaRatingC=Double.valueOf(branchData[i]);
	    
	    
	    i=argumentFileds.indexOf("LineTap"); 
	    if(i!=-1)lineTap=Double.valueOf(branchData[i]); 
	    
	    fromBusId=parser.BusIdPreFix+fromBusNum;
	    toBusId=parser.BusIdPreFix+toBusNum;
	    
	    // create a branch record
	    BranchXmlType branch=null;
	    
	    //parser.getAclfBus(fromBusId).getBaseVoltage().getValue()!=parser.getAclfBus(toBusId).getBaseVoltage().getValue()
    	
	    if(!isXfmr)branch = parser.createLineBranch(fromBusId, toBusId, circuitId);
	    else branch=parser.createXfrBranch(fromBusId, toBusId, circuitId);
		
		branch.setOffLine(!closed);
		branch.setZ(BaseDataSetter.createZValue(r, x, ZUnitType.PU));
		
		
		if(branch instanceof LineBranchXmlType){
			if(g!=0||b!=0)((LineBranchXmlType) branch).setToShuntY(
					BaseDataSetter.createYValue(g, b, YUnitType.PU));
		}
		else if(branch instanceof XfrBranchXmlType){
			if(g!=0||b!=0)((XfrBranchXmlType) branch).setMagnitizingY(
					BaseDataSetter.createYValue(g, b, YUnitType.PU));
			((XfrBranchXmlType) branch).setFromTurnRatio(
					BaseDataSetter.createTurnRatioPU(lineTap));
			//TODO define toTurnRatio
			//what is the difference between transformer tap and turn ratio;
			
			//TODO NE-ISO use Branch type to define transformer
			
		}
		
		//set rating limit
		branch.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
		
		AclfDataSetter.setBranchRatingLimitData(branch.getRatingLimit(),
				mvaRatingA, mvaRatingB, mvaRatingC, ApparentPowerUnitType.MVA);
	    
		}catch(Exception e){
			e.printStackTrace();
		}
		
	    
	    
		
		
	}
	private void processXFormerData(String xfomerDataStr,AclfModelParser parser){
		/*
		DATA (TRANSFORMER, [BusNum,BusNum:1,LineCircuit,LineXFType,XFAuto,XFRegMin,XFRegMax,XFTapMin,
		                    XFTapMax,XFStep,XFTableNum,XFRegBus])
		*/
		long fromBusNum=-1,toBusNum=-1;
		String fromBusId, toBusId,circuitId="1";
		String[] shuntData=getDataFields(xfomerDataStr, dataSeparator);
		int i=-1;
		try{
		i=argumentFileds.indexOf("BusNum") ;
		fromBusNum=Long.valueOf(shuntData[i]); //mandatory field
		
		i=argumentFileds.indexOf("BusNum:1") ;
		toBusNum=Long.valueOf(shuntData[i]); //mandatory field
		
		i=argumentFileds.indexOf("LineCircuit"); if(i!=-1)circuitId=shuntData[i];
		
		fromBusId=parser.BusIdPreFix+fromBusNum;
		toBusId=parser.BusIdPreFix+toBusNum;
		
		XfrBranchXmlType xfr=parser.getXfrBranch(fromBusId, toBusId, circuitId);
		//TODO set type and regulation info;
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void process3WXFomerData(String triWXformerDataStr,LoadflowNetXmlType baseCaseNet){
		/*
		 * the 3-winding transformers are treated as 3 2-winding transformers
		 *  with an additional star bus added to the network;
		 */
		
		
	}
	private void processAreaData(String areaDataStr,AclfModelParser parser){
		/*
		 * DATA (AREA, [AreaNum,AreaName,BGAGC,BGAutoSS,BGAutoXF,EnforceGenMWLimits,SchedName,SAName,
           ConvergenceTol,AreaEDIncludeLossPF,BusSlack,AreaUnSpecifiedStudyMW])
		 */
		//now only consider areaNum, areaName
		
		int areaNum=-1;
		String areaName="";
		String[] areaData=getDataFields(areaDataStr, dataSeparator);
		int i=0;
		i=argumentFileds.indexOf("AreaNum");
		areaNum=Integer.valueOf(areaData[i]);
		
		i=argumentFileds.indexOf("AreaName");
		areaName=areaData[i];
		
		NetAreaXmlType area=odmObjFactory.createNetAreaXmlType();
		area.setNumber(areaNum);
		area.setName(areaName);
		if(parser.getAclfNet().getAreaList()==null)
			parser.getAclfNet().setAreaList(odmObjFactory.createNetworkXmlTypeAreaList());
		
		parser.getAclfNet().getAreaList().getArea().add(area);
		
		
		
		
	}
	private void processZoneData(String zoneDataStr,AclfModelParser parser){
		/*
		 * DATA (ZONE, [ZoneNum,ZoneName,SchedName])
		 */
		int zoneNum=-1;
		String zoneName="";
		String[] zoneData=getDataFields(zoneDataStr, dataSeparator);
		int i=0;
		i=argumentFileds.indexOf("ZoneNum");
		zoneNum=Integer.valueOf(zoneData[i]);
		
		i=argumentFileds.indexOf("ZoneName");
		zoneName=zoneData[i];
		
		NetZoneXmlType zone=odmObjFactory.createNetZoneXmlType();
		zone.setNumber(zoneNum);
		zone.setName(zoneName);
		if(parser.getAclfNet().getLossZoneList()==null)
			parser.getAclfNet().setLossZoneList(odmObjFactory.createNetworkXmlTypeLossZoneList());
		
		parser.getAclfNet().getLossZoneList().getLossZone().add(zone);
		
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
	
	private String[] getDataFields(String str,FileTypeSpecifier type){
	
		String[] dataFields=new String[argumentFileds.size()];
		if(type==FileTypeSpecifier.Blank) {
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
		    	//System.out.println("n="+n+", n%2="+n%2);
		    	String sub="";
		    	
		    	if(n%2==0){
		    		sub=str.substring(index, quoteIndexAry.get(n));
		    		
		    		String[] temp=sub.split("\\s++");
				    for(String value:temp){
					   if(!value.trim().equals(""))dataFields[k++]=value;
				    }
				    
		    	}
		    	
		    	else {
		    		//sub=str.substring(quoteIndexAry.get(n++)+1, quoteIndexAry.get(n));
		    		sub=str.substring(index, quoteIndexAry.get(n));
		    		dataFields[k++]=sub;
		    	    if(n==quoteIndexAry.size()-1){
		    		   sub=str.substring(quoteIndexAry.get(n)+1);
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
