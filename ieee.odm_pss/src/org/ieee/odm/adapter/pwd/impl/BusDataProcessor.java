package org.ieee.odm.adapter.pwd.impl;

import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.AngleXmlType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.ShuntCompensatorModeEnumType;
import org.ieee.odm.schema.ShuntCompensatorXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.VoltageXmlType;
import org.ieee.odm.schema.YUnitType;
 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.1  04/03/2012
  * @author Tony Huang
  * 
  */
public class BusDataProcessor extends PWDDataParser {
	public static long swingBusNum=-1;
	private boolean isSubDataSection=false;

	
	public BusDataProcessor(AclfModelParser parser) {
		super(parser);
	}
	
	
	public void processBusBasicData(String busDataStr){
		/*
		 * DATA (BUS, [BusNum,BusName,BusNomVolt,BusPUVolt,BusAngle,BusG:1,BusB:1,AreaNum,ZoneNum,
            SubNum,BusSlack])
		 */
		
		
		long busNum=-1;
		int areaNum=-1,zoneNum=-1,ownerNum=-1;// purposely set to -1, a not real number;
		String busName="",busId="";
		double basekV=0, puVolt=0,kvVolt=0,angle=-360,busG=0,busB=0;
		boolean isSlackBus=false,busConnected=true;
		//first, parse the data and set it to the internal Hashtable
		parseData(busDataStr);
		try {

			busNum=getLong("BusNum");
	
			busName=exist("BusName")?getString("BusName"):"";

			
			if(exist("BusStatus")){
				busConnected=(getString("BusStatus").equalsIgnoreCase("Connected")||getString("BusStatus").equalsIgnoreCase("Closed"))?true:false;
			}
			if(exist("BusNomVolt")){
				basekV=getDouble("BusNomVolt");
			}
			if(exist("BusPUVolt")){
				puVolt=getDouble("BusPUVolt");
			}
			if(exist("BusKVVolt")){
				kvVolt=getDouble("BusKVVolt");
			}
			if(exist("BusAngle")){
				angle=getDouble("BusAngle");
			}
			
			//'User Input Value: Represents the MW injection that the system
			//would see from the shunt at 1.0 per unit voltage (positive value represents Load)
			//TODO what is its relationship to 'Shunt'
			
			busG=exist("BusG:1")?getDouble("BusG:1"):0;
			
			busB=exist("BusB:1")?getDouble("BusB:1"):0;
			
			if(exist("AreaNum")){
				areaNum=getInt("AreaNum");
			}
			if(exist("ZoneNum")){
				zoneNum=getInt("ZoneNum");
			}
			if(exist("OwnerNum")){
				ownerNum=getInt("OwnerNum");
			}
			if(exist("BusSlack")){
				isSlackBus=getString("BusSlack").equalsIgnoreCase("YES")
						?isSlackBus=true:false;
			}
			else
				throw new ODMException("No slack bus information is provided " +
						"in the input data!");
	
		
		if(busNum==-1) 
			ODMLogger.getLogger().severe("bus Num is not defined yet!");
		busId=AclfModelParser.BusIdPreFix+busNum;
		
		LoadflowBusXmlType bus=parser.createAclfBus(busId);
		bus.setId(busId);
		bus.setNumber(busNum);
		bus.setOffLine(!busConnected);
		
		if(!busName.equals("")) bus.setName(busName);
		if(areaNum!=-1)bus.setAreaNumber(areaNum);
		if(zoneNum!=-1)bus.setZoneNumber(zoneNum);
		//TODO OwnerNumber
		//if(ownerNum!=-1)bus.getOwnerList().add(new OwnerXmlType());//setOwnerNumber(ownerNum);
		bus.setBaseVoltage(BaseDataSetter.createVoltageValue(basekV, VoltageUnitType.KV));
		if(puVolt==0&&kvVolt!=0){
			puVolt=kvVolt/basekV;
		}
		bus.setVoltage(BaseDataSetter.createVoltageValue(puVolt, VoltageUnitType.PU));
		
		if(angle!=-360)bus.setAngle(BaseDataSetter.createAngleValue(angle,AngleUnitType.DEG));
		
		if (busG != 0.0 || busB != 0.0) {
			bus.setShuntY(BaseDataSetter.createYValue(busG, busB,YUnitType.MVAR));
		}
		
		if(isSlackBus){
			swingBusNum=busNum;
			//System.out.println ("==>Swing Bus number:"+swingBusNum);
			
		}
		
		//Data specified for other types of Buses(PV,PQ) is defined in Load and Gen data.
		
		} catch (ODMException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public void processBusLoadData(String busLoadDataStr){
		/*
		 * DATA (LOAD, [BusNum,LoadID,LoadStatus,LoadSMW,LoadSMVR,LoadIMW,LoadIMVR,LoadZMW,LoadZMVR,
   AreaNum,ZoneNum])
		 */
		
		long busNum=-1;
		String busId="",loadId="";
		double loadSMVR=0,loadSMW=0,loadIMVR=0,loadIMW=0,loadZMVR=0,loadZMW=0;
		int areaNum=-1,zoneNum=-1;
		boolean loadOnLine=false;
		
		parseData(busLoadDataStr);
	
		try {
		busNum=getLong("BusNum"); //mandatory filed
		
		if(exist("LoadID")) loadId=getString("LoadID");
		if(exist("LoadStatus")) 
		   loadOnLine=getString("LoadStatus").equalsIgnoreCase("Closed")?true:false;
       
		loadSMW  = exist("LoadSMW")?getDouble("LoadSMW"):0;
		loadSMVR = exist("LoadSMVR")?getDouble("LoadSMVR"):0;
		loadIMW  = exist("LoadIMW")?getDouble("LoadIMW"):0;
		loadIMVR = exist("LoadIMVR")?getDouble("LoadIMVR"):0;
		loadZMW  = exist("LoadZMW")?getDouble("LoadZMW"):0;
		loadZMVR = exist("LoadZMVR")?getDouble("LoadZMVR"):0;
		areaNum  = exist("AreaNum")?getInt("AreaNum"):0;
		zoneNum  = exist("ZoneNum")?getInt("ZoneNum"):0;
		} catch (ODMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public void processBusGenData(String busGenDataStr){
		/*
		 * DATA (GEN, [BusNum,GenID,GenStatus,GenMW,GenMVR,GenAGCAble,GenEnforceMWLimits,GenMWMin,
   GenMWMax,GenParFac,GenAVRAble,GenVoltSet,GenRegNum,GenMVRMin,GenMVRMax,
   GenRMPCT,GenUseCapCurve,GenUseLDCRCC,GenXLDCRCC,GenMVABase,GenZR,GenZX,
   GenStepR,GenStepX,GenStepTap,AreaNum,ZoneNum])
		 */
		
		/*Now we Only consider the following 
		  BusNum,GenID,GenStatus,GenMW,GenMVR,GenEnforceMWLimits,GenMWMin,
		   GenMWMax GenRegNum,GenMVRMin,GenMVRMax,GenVoltSet,GenMVABase,AreaNum,ZoneNum
		*/
		long busNum=-1,regBusNum=-1;
		int areaNum=-1,zoneNum=-1;
		String genId="";
		double genVoltSet=0;
		double genMW=0,genMVR=0,genMWMin=0,genMWMax=0,genMVRMin=-9999,genMVRMax=9999,genMVABase=100;
		boolean genOnLine=false;
		boolean pLimitForced=true;
		double partFactor = 0.0;
		boolean genAGCAble = false;
		
		if(busGenDataStr.trim().startsWith("<SUBDATA")){
			isSubDataSection=true;
			return;
		}
		else if(busGenDataStr.trim().startsWith("</SUBDATA>")){
			isSubDataSection=false;
			return;
		}
	
		/*
		 * skips SubData, since they are not used in load flow analysis;
		 */
	
		if (!isSubDataSection) {// if there is no subData or subData ends
		
		   parseData(busGenDataStr);
         
		   try {
			busNum=getLong("BusNum");// mandatory filed
		
		   genId =exist("GenID")?getString("GenID"):"";
		   
		   
		  if (exist("GenStatus")) 
			  genOnLine =getString("GenStatus").equalsIgnoreCase("Closed")?true:false;

		  if (exist("GenMW"))
					genMW =getDouble("GenMW");
		  if(exist("GenEnforceMWLimits")) 
				 pLimitForced= getString("GenEnforceMWLimits").equalsIgnoreCase("YES")?
						true:false;
		  if(exist("GenRegNum"))
				regBusNum =getInt("GenRegNum");
		
		  genMVR =getDouble("GenMVR");
		  
		  if(exist("GenMWMin"))
				genMWMin = getDouble("GenMWMin");
		  
		  if (exist("GenMWMax"))
				genMWMax =getDouble("GenMWMax");
		  
		  if (exist("GenMVRMin"))
					genMVRMin = getDouble("GenMVRMin");
		  if (exist("GenMVRMax"))
					genMVRMax = getDouble("GenMVRMax");
		  if (exist("GenMVABase"))
					genMVABase =getDouble("GenMVABase");
		  if (exist("AreaNum"))
					areaNum = getInt("AreaNum");
		  if (exist("ZoneNum"))
		            zoneNum = getInt("ZoneNum");
		  
		  if(exist("GenVoltSet"))
					genVoltSet=getDouble("GenVoltSet");
		  if(exist("GenAGCAble"))
			  genAGCAble=getString("GenAGCAble").toLowerCase().equals("yes");
		  if(exist("GenParFac"))		
			  partFactor= getDouble("GenParFac");
		  
		  } catch (ODMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		   }

			String busId = parser.BusIdPreFix + busNum;
			LoadflowBusXmlType bus = parser.getAclfBus(busId);

			if (regBusNum != -1) {
				// this generator control the bus it connects to
				
				//generator regulates itself by default when regBusNum==0
				if (regBusNum == busNum||regBusNum==0) { 

					if (busNum != swingBusNum) {// This bus is a PV bus
						
						AclfDataSetter.setGenData(bus, LFGenCodeEnumType.PV, genVoltSet, VoltageUnitType.PU, 0, AngleUnitType.DEG,
								genMW, genMVR, ApparentPowerUnitType.MVA);

						LoadflowGenXmlType equivGen = bus.getGenData()
								.getEquivGen();
						equivGen.setId(genId);
						equivGen.setOffLine(!genOnLine);
						// equivGen.setRatedPower(BaseDataSetter.createApparentPower(genMVABase,
						// ApparentPowerUnitType.MVA));
						equivGen.setPLimit(BaseDataSetter
								.createActivePowerLimit(genMWMax, genMWMin,
										ActivePowerUnitType.MW));

						equivGen.getPLimit().setActive(pLimitForced);

						equivGen.setQLimit(BaseDataSetter
								.createReactivePowerLimit(genMVRMax, genMVRMin,
										ReactivePowerUnitType.MVAR));

						if (areaNum != -1)
							equivGen.setAreaNumber(areaNum);
						if (zoneNum != -1)
							equivGen.setZoneNumber(zoneNum);

					} else { // swing bus
						//VoltageXmlType v = bus.getVoltage();
						AngleXmlType angle = bus.getAngle();
						AclfDataSetter.setGenData(bus, LFGenCodeEnumType.SWING,
								genVoltSet, VoltageUnitType.PU, angle.getValue(),
								angle.getUnit(), genMW, genMVR,
								ApparentPowerUnitType.MVA);

						LoadflowGenXmlType equivGen = bus.getGenData()
								.getEquivGen();
						equivGen.setId(genId);
						equivGen.setRatedPower(BaseDataSetter.createPowerMvaValue(genMVABase));
						equivGen.setOffLine(!genOnLine);

						// p limit
						equivGen.setPLimit(BaseDataSetter
								.createActivePowerLimit(genMWMax, genMWMin,
										ActivePowerUnitType.MW));
						// TODO need to set plimitforced?
						equivGen.getPLimit().setActive(pLimitForced);
						// q limit
						equivGen.setQLimit(BaseDataSetter
								.createReactivePowerLimit(genMVRMax, genMVRMin,
										ReactivePowerUnitType.MVAR));

						if (areaNum != -1)
							equivGen.setAreaNumber(areaNum);
						if (zoneNum != -1)
							equivGen.setZoneNumber(zoneNum);

					}

				} else {// the regulated bus is a remote bus

					//Define a remote bus that a generator controls/regulates, as a PV
					// And this gen bus is a PV bus itself

					// set remote bus data
					
					String regBusId = parser.BusIdPreFix + regBusNum;
		
					// set this gen bus data
					AclfDataSetter.setGenData(bus, LFGenCodeEnumType.PV, genVoltSet,
							VoltageUnitType.PU, 0, AngleUnitType.DEG, genMW,
							genMVR, ApparentPowerUnitType.MVA);

					LoadflowGenXmlType equivGen = bus.getGenData().getEquivGen();
							
					equivGen.setId(genId);
					equivGen.setRatedPower(BaseDataSetter.createPowerMvaValue(genMVABase));
					
					equivGen.setPLimit(BaseDataSetter.createActivePowerLimit(
							genMWMax, genMWMin, ActivePowerUnitType.MW));

					equivGen.getPLimit().setActive(pLimitForced);

					equivGen.setQLimit(BaseDataSetter.createReactivePowerLimit(
							genMVRMax, genMVRMin, ReactivePowerUnitType.MVAR));

					if (areaNum != -1)
						equivGen.setAreaNumber(areaNum);
					if (zoneNum != -1)
						equivGen.setZoneNumber(zoneNum);
					// define the remote control bus
					equivGen.setRemoteVoltageControlBus(parser
							.createBusRef(regBusId));
				}

				// process generator participation factor
				LoadflowGenXmlType equivGen = bus.getGenData().getEquivGen();
				if (genAGCAble)
					equivGen.setMwControlParticipateFactor(partFactor);
			}
		}//end of if-subData
	}
	
	public void processBusShuntData(String shuntDataStr){
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
		
		parseData(shuntDataStr);
		
		try {
			busNum=getLong("BusNum");
		
		if(exist("SSRegNum"))
				regBusNum=getLong("SSRegNum");
	
		shuntId=exist("ShuntID")?getString("ShuntID"):"";
		
		if(exist("SSStatus")) 
		    	closed=getString("SSStatus").equalsIgnoreCase("Closed")?true:false;
		if (exist("AreaNum")) 
		    	areaNum=getInt("AreaNum");
		
		if (exist("ZoneNum")) 
		    	zoneNum=getInt("ZoneNum");
			
		if (exist("SSCMode")){
			String modeStr=getString("SSCMode");
		    	mode=modeStr.equalsIgnoreCase("Discrete")?ShuntCompensatorModeEnumType.DISCRETE:
		    				(modeStr.equalsIgnoreCase("Continuous")?ShuntCompensatorModeEnumType.CONTINUOUS:
		    					ShuntCompensatorModeEnumType.FIXED);
		}
		if (exist("SSVHigh"))
			vHigh=getDouble("SSVHigh");
			
		if (exist("SSVLow"))
			vLow=getDouble("SSVLow");
			
		if (exist("SSNMVR"))
				normalMVR=getDouble("SSNMVR");
			
		//TODO How to determine the number of blocks
		if (exist("SSBlockNumSteps"))
				steps1=new Double(getString("SSBlockNumSteps")).intValue();
			
		if (exist("SSBlockMVarPerStep"))
				MVarPerStep1=getDouble("SSBlockMVarPerStep");
			
			
		if (exist("SSBlockNumSteps:1"))
				steps2=new Double(getString("SSBlockNumSteps:1")).intValue();
			
		if (exist("SSBlockMVarPerStep:1"))
				MVarPerStep2=getDouble("SSBlockMVarPerStep:1");
		} catch (ODMException e) {
			e.printStackTrace();
		} 
		

		//TODO no shunt status defined in ODM
		
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
}
