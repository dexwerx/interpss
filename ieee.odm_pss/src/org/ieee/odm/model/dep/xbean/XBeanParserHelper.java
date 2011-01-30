 /*
  * @(#)ContainerHelper.java   
  *
  * Copyright (C) 2008 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 02/11/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.model.dep.xbean;

import java.util.logging.Logger;

import org.apache.xmlbeans.XmlOptions;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ActivePowerUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.AnalysisCategoryEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ApparentPowerUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BaseRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BranchFaultXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BranchRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BusFaultXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BusRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.DcLineFaultXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ExciterXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.FaultTypeEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.FaultXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.GeneratorXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LFGenCodeEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LFLoadCodeEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowBranchDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowBusDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowGenDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowLoadDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NameValuePairListXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NameValuePairXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NetAreaXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NetZoneXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.NetworkCategoryEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PSSNetworkXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ReactivePowerUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ShuntCompensatorXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.StabilizerXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.StaticVarCompensatorXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.StudyCaseXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TransientSimulationXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TurbineGovernorXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.VoltageUnitType;

public class XBeanParserHelper {
	/**
	 * Set BaseCase to Loadflow and Transmission 
	 * 
	 * @param parser
	 * @param originalFormat
	 */
	public static void setLFTransInfo(XBeanODMModelParser parser, StudyCaseXmlType.ContentInfo.OriginalDataFormat.Enum originalFormat) {
		StudyCaseXmlType.ContentInfo info = parser.getStudyCase().addNewContentInfo();
		info.setOriginalDataFormat(originalFormat);
		info.setAdapterProviderName("www.interpss.org");
		info.setAdapterProviderVersion("1.00");
		
		parser.getStudyCase().getBaseCase().setAnalysisCategory(
				AnalysisCategoryEnumType.LOADFLOW);
		parser.getStudyCase().getBaseCase().setNetworkCategory(
				NetworkCategoryEnumType.TRANSMISSION);		
	}
	
	/**
	 * consolidate branch genContributionList and loadContributionList to the equiv gen and load 
	 * 
	 */
	public static boolean createBusEquivData(PSSNetworkXmlType baseCaseNet, Logger logger) {
		boolean ok = true;

		for (BusRecordXmlType busRec : baseCaseNet.getBusList().getBusArray()) {
			LoadflowBusDataXmlType.GenData genData = busRec.getLoadflowData().getGenData();
			if (genData != null) {
				if ( genData.getContributeGenList() != null && 
						genData.getContributeGenList().getContributeGenArray().length > 0) {
					LoadflowGenDataXmlType equivGen = genData.getEquivGen();
					double pgen = 0.0, qgen = 0.0, qmax = 0.0, qmin = 0.0, pmax = 0.0, pmin = 0.0, vSpec = 0.0;
					VoltageUnitType.Enum vSpecUnit = VoltageUnitType.PU;
					String remoteBusId = null;
					boolean offLine = true;
					for ( LoadflowGenDataXmlType gen : genData.getContributeGenList().getContributeGenArray()) {
						if (!gen.getOffLine()) {
							offLine = false;
							if (remoteBusId == null) {
								if (gen.getRemoteVoltageControlBus() != null)
									remoteBusId = gen.getRemoteVoltageControlBus().getIdRef();
							}
							else if (!remoteBusId.equals(gen.getRemoteVoltageControlBus().getIdRef())) {
								logger.severe("Inconsistant remote control bus id, " + remoteBusId +
										", " + gen.getRemoteVoltageControlBus().getIdRef());
								return false; 
							}
							
							pgen += gen.getPower().getRe();
							qgen += gen.getPower().getIm();
							if(gen.getQLimit() != null) {
								qmax += gen.getQLimit().getMax();
								qmin += gen.getQLimit().getMin();
							}
							if(gen.getPLimit() != null) {
								pmax += gen.getPLimit().getMax();
								pmin += gen.getPLimit().getMin();
							}
							
							if (gen.getDesiredVoltage() != null) {
								if (vSpec == 0.0) {
									vSpec = gen.getDesiredVoltage().getValue();
									vSpecUnit = gen.getDesiredVoltage().getUnit();
								}
								else if (vSpec != gen.getDesiredVoltage().getValue()) {
									logger.severe("Inconsistant gen desired voltage, " + 
											gen.getRemoteVoltageControlBus().getIdRef());
									return false; 
								}
							}
						}
					}
					
					if (offLine && genData.getEquivGen().getCode() != LFGenCodeEnumType.SWING)
						// generator on a swing bus might turned off
						genData.getEquivGen().setCode(LFGenCodeEnumType.OFF);
					else {	
						if (equivGen.getPower() == null)
							equivGen.addNewPower();
						XBeanDataSetter.setPowerData(equivGen.getPower(), pgen, qgen, ApparentPowerUnitType.MVA);
						if (pmax != 0.0 || pmin != 0.0)
							XBeanDataSetter.setActivePowerLimitData(equivGen.addNewPLimit(), pmax, pmin, ActivePowerUnitType.MW);
						if (qmax != 0.0 || qmin != 0.0)
							XBeanDataSetter.setReactivePowerLimitData(equivGen.addNewQLimit(), qmax, qmin, ReactivePowerUnitType.MVAR);
						if (vSpec != 0.0) {
							if (equivGen.getDesiredVoltage() == null)
								equivGen.addNewDesiredVoltage();
							XBeanDataSetter.setVoltageData(equivGen.getDesiredVoltage(), vSpec, vSpecUnit);
						}
					}
					
					if (remoteBusId != null && !remoteBusId.equals(busRec.getId()) && 
							genData.getEquivGen().getCode() == LFGenCodeEnumType.PV){
						// Remote Q  Bus control, we need to change this bus to a GPQ bus so that its Q could be adjusted
						genData.getEquivGen().addNewRemoteVoltageControlBus().setIdRef(remoteBusId);
					}
						
				}
				else {
					genData.getEquivGen().setCode(LFGenCodeEnumType.NONE_GEN);
					if (genData.getEquivGen().getPower() != null)
						genData.getEquivGen().unsetPower();
					if (genData.getEquivGen().getVoltageLimit() != null)
						genData.getEquivGen().unsetVoltageLimit();
				}
			}
			
			LoadflowBusDataXmlType.LoadData loadData = busRec.getLoadflowData().getLoadData();
			if (loadData != null) {
				if (loadData.getContributeLoadList() != null &&
						loadData.getContributeLoadList().getContributeLoadArray().length > 0) {
					LoadflowLoadDataXmlType equivLoad = loadData.getEquivLoad();
					double cp_p=0.0, cp_q=0.0, ci_p=0.0, ci_q=0.0, cz_p=0.0, cz_q=0.0; 
					for ( LoadflowLoadDataXmlType load : loadData.getContributeLoadList().getContributeLoadArray()) {
						if (!load.getOffLine()) {
							if (load.getConstPLoad() != null) {
								cp_p += load.getConstPLoad().getRe();
								cp_q += load.getConstPLoad().getIm();
							}
							if (load.getConstILoad() != null) {
								ci_p += load.getConstILoad().getRe();
								ci_q += load.getConstILoad().getIm();
							}
							if (load.getConstZLoad() != null) {
								cz_p += load.getConstZLoad().getRe();
								cz_q += load.getConstZLoad().getIm();
							}
						}					
					}
					
					if ((cp_p != 0.0 || cp_q != 0.0) && (ci_p==0.0 && ci_q ==0.0 && cz_p==0.0 && cz_q ==0.0) ) {
						equivLoad.setCode(LFLoadCodeEnumType.CONST_P);
			  			XBeanDataSetter.setPowerData(equivLoad.addNewConstPLoad(), cp_p, cp_q, ApparentPowerUnitType.MVA);
			  		}
					else if ((ci_p != 0.0 || ci_q != 0.0) && (cp_p==0.0 && cp_q ==0.0 && cz_p==0.0 && cz_q ==0.0) ) {
						equivLoad.setCode(LFLoadCodeEnumType.CONST_I);
			  			XBeanDataSetter.setPowerData(equivLoad.addNewConstILoad(), ci_p, ci_q, ApparentPowerUnitType.MVA);
			  		}
					else if ((cz_p != 0.0 || cz_q != 0.0) && (ci_p==0.0 && ci_q ==0.0 && cp_p==0.0 && cp_q ==0.0) ) {
						equivLoad.setCode(LFLoadCodeEnumType.CONST_Z);
			  			XBeanDataSetter.setPowerData(equivLoad.addNewConstZLoad(), cz_p, cz_q, ApparentPowerUnitType.MVA);
			  		}
					else if ((cp_p != 0.0 || cp_q != 0.0 || ci_p!= 0.0 || ci_q != 0.0 || cz_p != 0.0 || cz_q !=0.0)) {
						equivLoad.setCode(LFLoadCodeEnumType.FUNCTION_LOAD);
			  			XBeanDataSetter.setPowerData(equivLoad.addNewConstPLoad(), cp_p, cp_q, ApparentPowerUnitType.MVA);
			  			XBeanDataSetter.setPowerData(equivLoad.addNewConstILoad(), ci_p, ci_q, ApparentPowerUnitType.MVA);
			  			XBeanDataSetter.setPowerData(equivLoad.addNewConstZLoad(), cz_p, cz_q, ApparentPowerUnitType.MVA);
					}
					else {
						loadData.getEquivLoad().setCode(LFLoadCodeEnumType.NONE_LOAD);
					}
				}
				else
					loadData.getEquivLoad().setCode(LFLoadCodeEnumType.NONE_LOAD);
			}
		}
		
		return ok;
	}
	
	/*
	 *      Parser Container object creation and retrieval functions
	 *      ========================================================== 
	 */
	
	/**
	 * create a Contribution Load object
	 * 
	 */
	public static LoadflowLoadDataXmlType createContriLoad(BusRecordXmlType busRec) {
		LoadflowBusDataXmlType.LoadData loadData = busRec.getLoadflowData().getLoadData();
		if (loadData == null) { 
			loadData = busRec.getLoadflowData().addNewLoadData();
			loadData.addNewEquivLoad();
		}
		if (loadData.getContributeLoadList() == null) 
			loadData.addNewContributeLoadList();
	    return loadData.getContributeLoadList().addNewContributeLoad(); 
	}
	
	/**
	 * create a Contribution Load object
	 * 
	 */
	public static LoadflowGenDataXmlType createContriGen(BusRecordXmlType busRec) {
		LoadflowBusDataXmlType.GenData genData = busRec.getLoadflowData().getGenData();
		if (genData == null) {
			genData = busRec.getLoadflowData().addNewGenData();
			genData.addNewEquivGen();
		}
		// some model does not need ContributeGenList
		if (genData.getContributeGenList() == null) 
			genData.addNewContributeGenList();
	    return genData.getContributeGenList().addNewContributeGen();
	}
	
	/**
	 * create a SVC object
	 * 
	 */
	public static StaticVarCompensatorXmlType createSVC(BusRecordXmlType bus) {
		if (bus.getSvcData() == null)
			bus.addNewSvcData();
		if (bus.getSvcData().getSvcList() == null)
			bus.getSvcData().addNewSvcList();
		return bus.getSvcData().getSvcList().addNewSvc();
	}

	/**
	 * create a ShuntCompensatorXmlType object
	 * 
	 */
	public static ShuntCompensatorXmlType createShuntCompensator(BusRecordXmlType bus) {
		if (bus.getLoadflowData().getShuntCompensatorData() == null)
			bus.getLoadflowData().addNewShuntCompensatorData();
		if (bus.getLoadflowData().getShuntCompensatorData().getShuntCompensatorList() == null)
			bus.getLoadflowData().getShuntCompensatorData().addNewShuntCompensatorList();
		return bus.getLoadflowData()
					.getShuntCompensatorData()
					.getShuntCompensatorList()
					.addNewShunCompensator(); 
	}

	/**
	 * Get bus record with the id
	 * 
	 * @param id
	 * @param baseCaseNet
	 * @return
	 */
	public static BusRecordXmlType findBusRecord(String id, PSSNetworkXmlType baseCaseNet) {
		for (BusRecordXmlType busRec : baseCaseNet.getBusList().getBusArray()) {
			if (id.equals(busRec.getId()))
				return busRec;
		}
		return null;
	}
	
	/**
	 * Get bus record with the id
	 * 
	 * @param id
	 * @param baseCaseNet
	 * @return
	 */
	public static BranchRecordXmlType findBranchRecord(String fromId, String toId, String cirId, PSSNetworkXmlType baseCaseNet) {
		for (BranchRecordXmlType braRec : baseCaseNet.getBranchList().getBranchArray()) {
			if (fromId.equals(braRec.getFromBus().getIdRef()) &&
					toId.equals(braRec.getToBus().getIdRef()) &&
							cirId.equals(braRec.getCircuitId()))
				return braRec;
		}
		return null;
	}
	
	/**
	 * Get branch record using the fromId and toId
	 * 
	 * @param id
	 * @param baseCaseNet
	 * @return
	 */
	public static BranchRecordXmlType findBranchRecord(String fromId, String toId,PSSNetworkXmlType baseCaseNet) {
		for (BranchRecordXmlType braRec : baseCaseNet.getBranchList().getBranchArray()) {
			if (fromId.equals(braRec.getFromBus().getIdRef()) &&
					toId.equals(braRec.getToBus().getIdRef()))
				return braRec;
		}
		return null;
	}	
	
	/**
	 * there might be multiple branch sections in the branchRec, this function gets the first for those
	 * formats only has one branch section
	 * 
	 * @param branchRec
	 * @return
	 */
	public static LoadflowBranchDataXmlType  getDefaultBranchData(BranchRecordXmlType branchRec) {
      	// there might be multiple branch sections, but UTCE only has one
		return branchRec.getLoadflowDataArray(0);
	}
	
	/**
	 * add a name/value pair to the name/value pair List
	 * 
	 * @param nvList name/value pair list
	 * @param name name string
	 * @param value value string
	 */
	public static void addNVPair(NameValuePairListXmlType nvList, String name, String value) {
    	NameValuePairXmlType nvPair = nvList.addNewNvPair();
    	nvPair.setName(name);
    	nvPair.setValue(value);
	}

	/**
	 * add an owner record to the BaseRecord
	 * 
	 * @param rec
	 * @param id
	 * @param ownership
	 */
	public static void addOwner(BaseRecordXmlType rec, String id, double ownership) {
		if (rec.getOwnerList() == null) {
			rec.addNewOwnerList();
		}
		BaseRecordXmlType.OwnerList.Owner owner = rec.getOwnerList().addNewOwner();
		owner.setId(id);
		owner.setOwnership(ownership);
	}
	
	/**
	 * add an owner records to the BaseRecord
	 * 
	 * @param rec
	 * @param id
	 * @param ownership
	 */
	public static void addOwner(BaseRecordXmlType rec, 
			String id1, double ownership1,
			String id2, double ownership2,
			String id3, double ownership3,
			String id4, double ownership4) {
		if (id1 != null && ownership1 > 0.0)
			addOwner(rec, id1, ownership1);
		if (id2 != null && ownership2 > 0.0)
			addOwner(rec, id2, ownership2);
		if (id1 != null && ownership3 > 0.0)
			addOwner(rec, id3, ownership3);
		if (id1 != null && ownership4 > 0.0)
			addOwner(rec, id4, ownership4);
	}

	/**
	 * Get area record with the areaname
	 * 
	 * @param areaName
	 * @param baseCaseNet
	 * @return
	 */
	public static NetAreaXmlType getAreaRecordByAreaName(String areaName, PSSNetworkXmlType baseCaseNet) {
		for (NetAreaXmlType area:baseCaseNet.getAreaList().getAreaArray()) {
			if (areaName.equals(area.getName()))
				return area;
		}
		return null;
	}
	
	/**
	 * Get area record with the zone
	 * 
	 * @param zoneName
	 * @param baseCaseNet
	 * @return
	 */
	public static NetAreaXmlType getAreaRecordByZone(int zoneNo, PSSNetworkXmlType baseCaseNet) {
		for (NetAreaXmlType area:baseCaseNet.getAreaList().getAreaArray()) {
			for(NetZoneXmlType zone : area.getZoneList().getZoneArray()){
				if (zoneNo == zone.getNumber())
					return area;
			}			
		}
		return null;
	}
	
	/**
	 * Get zone record with the zonename
	 * 
	 * @param zoneName
	 * @param area
	 * @return
	 */
	public static NetZoneXmlType getZoneRecord(String zoneName, NetAreaXmlType area) {
		for (NetZoneXmlType zone:area.getZoneList().getZoneArray()) {
			if (zoneName.equals(zone.getName()))
				return zone;
		}
		return null;
	}
	
	
	public static FaultXmlType getFaultRecord(TransientSimulationXmlType tranSimu,
			FaultTypeEnumType.Enum faultType,String fbus,String tbus){
		if(tranSimu.getDynamicDataList().getFaultList().getFaultArray()!=null){			
			for(FaultXmlType fault:tranSimu.getDynamicDataList().getFaultList().getFaultArray()){				
				if(faultType.equals(fault.getFaultType())){					
					if(faultType.equals(FaultTypeEnumType.BUS_FAULT)){						
						BusFaultXmlType busFault=fault.getBusFault();
						if(fbus.equals(busFault.getFaultedBus().getName())&&
								tbus.equals(busFault.getRemoteEndBus().getName())){							
							return fault;							
						}
					}
				}				
			}		
		   }
		return tranSimu.getDynamicDataList().getFaultList().addNewFault();
		
		}		
  
	
	public static BusFaultXmlType getBusFaultRecord(TransientSimulationXmlType tranSimu,
			String fbus,String tbus){
		
		for(FaultXmlType fault:tranSimu.getDynamicDataList().getFaultList().getFaultArray()){
			if(fault.getBusFault()!=null){
				BusFaultXmlType busFault=fault.getBusFault();				
				if(fbus.equals(busFault.getFaultedBus().getName())&& 
						tbus.equals(busFault.getRemoteEndBus().getName()))					
			         return busFault;
			}else{
				return fault.addNewBusFault();
			}			
		}
		return null;
	}
	
	public static BranchFaultXmlType getBranchFaultRecord(TransientSimulationXmlType tranSimu,
			String fbus,String tbus){		
		for(FaultXmlType fault:tranSimu.getDynamicDataList().getFaultList().getFaultArray()){
			if(fault.getBranchFault()!=null){
				BranchFaultXmlType braFault=fault.getBranchFault();				
				if(fbus.equals(braFault.getFromBus().getName())&& tbus.equals(braFault.getToBus().getName()))
			         return braFault;
			}else{
				return fault.addNewBranchFault();
			}			
		}
		return null;
	}
	
	public static DcLineFaultXmlType getDCFaultRecord(TransientSimulationXmlType tranSimu,
			String fbus,String tbus){
		for(FaultXmlType fault: tranSimu.getDynamicDataList().getFaultList().getFaultArray()){
			if(fault.getDcLineFault()!=null){				
				DcLineFaultXmlType dcFault= fault.getDcLineFault();				
				if(fbus.equals(dcFault.getFromACBus().getName())&&
						tbus.equals(dcFault.getToACBus().getName())){					
					return dcFault;
				}else {					
					return fault.addNewDcLineFault();
				}
			}
		}
		return null;
	}
	
	public static GeneratorXmlType getGeneratorRecord(TransientSimulationXmlType tranSimu,
			   String busId,String genId){		
		for(GeneratorXmlType gen: tranSimu.getDynamicDataList().getBusDynDataList()
				.getGeneratorDataList().getGeneratorArray()){						
			if(busId.equals(gen.getLocatedBus().getName())){
				if(gen.getGenId()!=null){
					if(genId.equals(gen.getGenId().getName())){
						return gen;
					}
				}else{
					return gen;
				}			
			}
		}		
		return null;		
	}
	
	public static ExciterXmlType getExciterRecord(TransientSimulationXmlType tranSimu,
			 String busId, String excId){
		for(ExciterXmlType exc: tranSimu.getDynamicDataList().getBusDynDataList().
				       getExciterDataList().getExciterArray()){
			if(busId.equals(exc.getLocatedBus().getName())){
				if(exc.getExcId()!=null){
					if(excId.equals(exc.getExcId().getName())){
						return exc;
					}
				}else{
					return exc;
				}
			}			
		}		
		return null;		
	}
	
	public static TurbineGovernorXmlType getTGRecord(TransientSimulationXmlType tranSimu,
			 String busId, String tgId){
		for(TurbineGovernorXmlType tg: tranSimu.getDynamicDataList().getBusDynDataList()
                   .getTurbineGovernorDataList().getTurbineGovernorArray()){
			if(busId.equals(tg.getLocatedBus().getName())){
				if(tg.getTgId()!=null){
					if(tgId.equals(tg.getTgId().getName())){
						return tg;
					}
				}else{
					return tg;
				}
			}			
		}	
		return null;		
	}
	
	public static StabilizerXmlType getPSSRecord(TransientSimulationXmlType tranSimu,
			 String busId, String macId){
		for(StabilizerXmlType pss: tranSimu.getDynamicDataList().getBusDynDataList()
                  .getStabilizerDataList().getStabilizerArray()){
			if(busId.equals(pss.getLocatedBus().getName())){
				if(pss.getMacId()!=null){
					if(macId.equals(pss.getMacId().getName())){
						return pss;
					}
				}else{
					return pss;
				}
			}			
		}	
		return null;		
	}
	
	public static XmlOptions getXmlOpts() {
		 XmlOptions opts = new XmlOptions();
		 java.util.Map<String, String> prefixMap = new java.util.HashMap<String, String>();
		 prefixMap.put(XBeanODMModelParser.Token_nsPrefix, XBeanODMModelParser.Token_nsUrl);
		 opts.setSaveImplicitNamespaces(prefixMap);
		 return opts;
	}
}
