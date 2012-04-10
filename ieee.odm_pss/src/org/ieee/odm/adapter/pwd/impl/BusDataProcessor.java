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
public class BusDataProcessor {
	public static long swingBusNum=-1;
	
	public static void processBusBasicData(String busDataStr, List<String> argumentFileds, AclfModelParser parser){
		/*
		 * DATA (BUS, [BusNum,BusName,BusNomVolt,BusPUVolt,BusAngle,BusG:1,BusB:1,AreaNum,ZoneNum,
            SubNum,BusSlack])
		 */
		
		
		long busNum=-1;
		int areaNum=-1,zoneNum=-1,ownerNum=-1;// purposely set to -1, a not real number;
		String busName="",busId="";
		double basekV=0, puVolt=0,angle=-360,busG=0,busB=0;
		boolean isSlackBus=false,busConnected=true;
		
		String[] busBasicData=PWDHelper.getDataFields(busDataStr, argumentFileds);
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
			
			else if(field.equals("BusStatus")){
				busConnected=(busBasicData[i].equalsIgnoreCase("Connected")||busBasicData[i].equalsIgnoreCase("Closed"))?true:false;
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
			else if(field.equals("OwnerNum")){
				ownerNum=Integer.valueOf(busBasicData[i]);
			}
			else if(field.equals("BusSlack")){
				if(busBasicData[i].equalsIgnoreCase("YES")) isSlackBus=true;
			}
			i++;
		  }//end for
		}
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
	
	public static void processBusLoadData(String busLoadDataStr, List<String> argumentFileds, AclfModelParser parser){
		/*
		 * DATA (LOAD, [BusNum,LoadID,LoadStatus,LoadSMW,LoadSMVR,LoadIMW,LoadIMVR,LoadZMW,LoadZMVR,
   AreaNum,ZoneNum])
		 */
		
		long busNum=-1;
		String busId="",loadId="";
		double loadSMVR=0,loadSMW=0,loadIMVR=0,loadIMW=0,loadZMVR=0,loadZMW=0;
		int areaNum=-1,zoneNum=-1;
		boolean loadOnLine=false;
		
		String[] loadData=PWDHelper.getDataFields(busLoadDataStr, argumentFileds);
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
			else if(s.equals("LoadIMVR"))loadIMVR=Double.valueOf(loadData[i]);
			else if(s.equals("LoadZMW")) loadZMW=Double.valueOf(loadData[i]);
			else if(s.equals("LoadZMVR"))loadZMVR=Double.valueOf(loadData[i]);
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
	
	public static void processBusGenData(String busGenDataStr, List<String> argumentFileds, AclfModelParser parser){
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
	String[] genData=PWDHelper.getDataFields(busGenDataStr, argumentFileds);
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
				//equivGen.setRatedPower(BaseDataSetter.createApparentPower(genMVABase, ApparentPowerUnitType.MVA));
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
				//equivGen.setRatedPower(BaseDataSetter.createApparentPower(genMVABase, ApparentPowerUnitType.MVA));
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
	
	public static void processBusShuntData(String shuntDataStr, List<String> argumentFileds, AclfModelParser parser){
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
		
		String[] shuntData=PWDHelper.getDataFields(shuntDataStr, argumentFileds);
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
}
