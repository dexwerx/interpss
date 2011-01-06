/*
 * @(#)ODMLoadflowDataMapperImpl.java   
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
 * @Date 02/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.odm.impl.dep.xbean;

import org.apache.commons.math.complex.Complex;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.AngleXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ApparentPowerUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BranchRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.BusRecordXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.CimRdfXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LFBranchCodeEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LFGenCodeEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LFLoadCodeEnumType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowBranchDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowBusDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowGenDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.LoadflowLoadDataXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PSSNetworkXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PowerXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.VoltageUnitType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.VoltageXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.YXmlType;
import org.ieee.odm.model.dep.xbean.XBeanParserHelper;
import org.interpss.numeric.datatype.LimitType;


import com.interpss.common.datatype.UnitType;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adj.PQBusLimit;
import com.interpss.core.aclf.adj.PVBusLimit;
import com.interpss.core.aclf.adj.RemoteQBus;
import com.interpss.core.aclf.adj.RemoteQControlType;
import com.interpss.core.aclf.adpter.LineAdapter;
import com.interpss.core.aclf.adpter.LoadBusAdapter;
import com.interpss.core.aclf.adpter.PQBusAdapter;
import com.interpss.core.aclf.adpter.PSXfrAdapter;
import com.interpss.core.aclf.adpter.PVBusAdapter;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.aclf.adpter.XfrAdapter;
import com.interpss.core.net.Area;
import com.interpss.core.net.CimRecord;
import com.interpss.core.net.Zone;

public class XmlBeansODMLoadflowDataMapperImpl {
	/**
	 * Map the network info only
	 * 
	 * @param xmlNet
	 * @return
	 * @throws Exception
	 */
	public static AclfNetwork mapNetworkData(PSSNetworkXmlType xmlNet) throws Exception {
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		net.setId(xmlNet.getId());
		net.setName(xmlNet.getName() == null? "ODM Loadflow Case" : xmlNet.getName());
		net.setDesc(xmlNet.getDesc());
		net.setBaseKva(xmlNet.getBasePower().getValue()*(xmlNet.getBasePower().getUnit()==ApparentPowerUnitType.MVA?1000.0:1.0));
				// BasePowerUnit [ MVA, KVA]
		net.setAllowParallelBranch(true);
		return net;
	}
	
	/**
	 * Map a bus record
	 * 
	 * @param busRec
	 * @param adjNet
	 * @return
	 * @throws Exception
	 */
	public static AclfBus mapBusData(BusRecordXmlType busRec, AclfNetwork adjNet) throws Exception {
		AclfBus aclfBus = CoreObjectFactory.createAclfBus(busRec.getId(), adjNet);
		//adjNet.addBus(aclfBus);
		aclfBus.setNumber(busRec.getNumber());
		aclfBus.setName(busRec.getName() == null? "Aclf Bus" : busRec.getName());
		aclfBus.setDesc(busRec.getDesc() == null? "Aclf Bus" : busRec.getDesc());
		aclfBus.setStatus(!busRec.getOffLine());
		if (!aclfBus.isActive()) {
			IpssLogger.getLogger().info("Aclf Bus is not active, " + aclfBus.getId());
		}
		
		if (busRec.getCimRdfRecords() != null && busRec.getCimRdfRecords().getRdfRecArray().length > 0) {
			for (CimRdfXmlType cimRec : busRec.getCimRdfRecords().getRdfRecArray()) {
				CimRecord rec = CoreObjectFactory.createCimRecod(cimRec.getRdfId(), cimRec.getName());
				aclfBus.getCimRec().add(rec);
			}
		}
		
		aclfBus.setDesc(busRec.getDesc());
		Area area = CoreObjectFactory.createArea(busRec.getAreaNumber(), adjNet);
		aclfBus.setArea(area);
		Zone zone = CoreObjectFactory.createZone(busRec.getZoneNumber(), adjNet);
		aclfBus.setZone(zone);
		aclfBus.setBaseVoltage(busRec.getBaseVoltage().getUnit()==VoltageUnitType.KV ?    // Base V unit [KV, Volt] 
									busRec.getBaseVoltage().getValue()*1000.0	: busRec.getBaseVoltage().getValue());
		if (busRec.getLoadflowData() != null) {
			XmlBeansODMLoadflowDataMapperImpl.setBusLoadflowData(busRec.getLoadflowData(), aclfBus, adjNet);
		}
		return aclfBus;
	}
	
	/**
	 * Map a branch record
	 * 
	 * @param branchRec
	 * @param adjNet
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public static AclfBranch mapBranchData(BranchRecordXmlType branchRec, AclfNetwork adjNet, IPSSMsgHub msg) throws Exception {
		AclfBranch aclfBranch = CoreObjectFactory.createAclfBranch();
		aclfBranch.setCircuitNumber(branchRec.getCircuitId());
		try {
			adjNet.addBranch(aclfBranch, branchRec.getFromBus().getIdRef(), branchRec.getToBus().getIdRef());
		} catch (Exception e) {
			msg.sendErrorMsg(e.toString() + ", the branch is ignored");
			return null;
		}
		aclfBranch.setName(branchRec.getName() == null ? "" : branchRec.getName());
		aclfBranch.setDesc(branchRec.getDesc() == null ? "" : branchRec.getDesc());
		aclfBranch.setStatus(!branchRec.getOffLine());
		if (!aclfBranch.isActive()) {
			IpssLogger.getLogger().info("Aclf Branch is not active, " + aclfBranch.getId());
		}
		Area area = CoreObjectFactory.createArea(branchRec.getAreaNumber(), adjNet);
		aclfBranch.setArea(area);
		Zone zone = CoreObjectFactory.createZone(branchRec.getZoneNumber(), adjNet);
		aclfBranch.setZone(zone);
		
		if (branchRec.getLoadflowDataArray().length > 0) {
			if (branchRec.getLoadflowDataArray().length == 1)
				XmlBeansODMLoadflowDataMapperImpl.setBranchLoadflowData( 
						XBeanParserHelper.getDefaultBranchData(branchRec), aclfBranch, adjNet, msg);
		}
		return aclfBranch;
	}
	
	private static void setBusLoadflowData(LoadflowBusDataXmlType busXmlData, AclfBus aclfBus, AclfNetwork adjNet) throws Exception {
		VoltageXmlType vXml = busXmlData.getVoltage();
		double vpu = vXml == null ? 1.0 : UnitType.vConversion(vXml.getValue(),
				aclfBus.getBaseVoltage(), XmlBeansODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU);
		double angRad = busXmlData.getAngle() ==  null? 0.0 :
			UnitType.angleConversion(busXmlData.getAngle().getValue(),
					XmlBeansODMXmlHelper.toUnit(busXmlData.getAngle().getUnit()), UnitType.Rad);
		aclfBus.setVoltage(vpu, angRad);

		if (busXmlData.getGenData() != null) {
			LoadflowGenDataXmlType xmlEquivGenData = busXmlData.getGenData().getEquivGen();
			if (xmlEquivGenData.getCode() == LFGenCodeEnumType.PQ) {
				aclfBus.setGenCode(AclfGenCode.GEN_PQ);
				PQBusAdapter pqBus = aclfBus.toPQBus();
				pqBus.setGen(new Complex(xmlEquivGenData.getPower().getRe(), 
						                 xmlEquivGenData.getPower().getIm()),
						           XmlBeansODMXmlHelper.toUnit(xmlEquivGenData.getPower().getUnit()));
				if (xmlEquivGenData.getVoltageLimit() != null) {
  			  		final PQBusLimit pqLimit = CoreObjectFactory.createPQBusLimit(aclfBus);
  			  		pqLimit.setVLimit(new LimitType(xmlEquivGenData.getVoltageLimit().getMax(), 
  			  										xmlEquivGenData.getVoltageLimit().getMin()), 
  			  				XmlBeansODMXmlHelper.toUnit(xmlEquivGenData.getVoltageLimit().getUnit()));						
				}
			} else if (xmlEquivGenData.getCode() == LFGenCodeEnumType.PV &&
					xmlEquivGenData != null) {
				if (xmlEquivGenData.getRemoteVoltageControlBus() == null) {
					aclfBus.setGenCode(AclfGenCode.GEN_PV);
					PVBusAdapter pvBus = aclfBus.toPVBus();
					//if (xmlEquivGenData == null)
					//	System.out.print(busXmlData);
					pvBus.setGenP(xmlEquivGenData.getPower().getRe(),
								XmlBeansODMXmlHelper.toUnit(xmlEquivGenData.getPower().getUnit()));
					vXml = xmlEquivGenData.getDesiredVoltage();
					vpu = UnitType.vConversion(vXml.getValue(),
							aclfBus.getBaseVoltage(), XmlBeansODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU);
					pvBus.setVoltMag(vpu, UnitType.PU);
					if (xmlEquivGenData.getQLimit() != null) {
	  			  		final PVBusLimit pvLimit = CoreObjectFactory.createPVBusLimit(aclfBus);
	  			  		pvLimit.setQLimit(new LimitType(xmlEquivGenData.getQLimit().getMax(), 
	  			  										xmlEquivGenData.getQLimit().getMin()), 
	  			  				XmlBeansODMXmlHelper.toUnit(xmlEquivGenData.getQLimit().getUnit()));						
					}
				}
				else {
					// remote bus voltage
  					IpssLogger.getLogger().fine("Bus is a RemoteQBus, id: " + aclfBus.getId());
  					aclfBus.setGenCode(AclfGenCode.GEN_PQ);
  					// The remote bus to be adjusted is normally defined as a PV bus. It needs to
  					// be changed to PQ bus
  					String remoteId = xmlEquivGenData.getRemoteVoltageControlBus().getIdRef();
  					AclfBus remoteBus = adjNet.getAclfBus(remoteId);
  					if (remoteBus != null) {
  	  					if (remoteBus.isGenPV())
  	  						remoteBus.setGenCode(AclfGenCode.GEN_PQ);
  	  			  		final RemoteQBus reQBus = CoreObjectFactory.createRemoteQBus(aclfBus, 
  	  			  				RemoteQControlType.BUS_VOLTAGE, adjNet, remoteId);
  			  			final PQBusAdapter gen = (PQBusAdapter)aclfBus.getAdapter(PQBusAdapter.class);
  			  			gen.setGen(new Complex(xmlEquivGenData.getPower().getRe(),
  			  					               xmlEquivGenData.getPower().getIm()), 
  			  					               XmlBeansODMXmlHelper.toUnit(xmlEquivGenData.getPower().getUnit()));
  	  			  		reQBus.setQLimit(new LimitType(xmlEquivGenData.getQLimit().getMax(), 
  														xmlEquivGenData.getQLimit().getMin()), 
  										XmlBeansODMXmlHelper.toUnit(xmlEquivGenData.getQLimit().getUnit()));						
  	  			  		reQBus.setVSpecified(UnitType.vConversion(xmlEquivGenData.getDesiredVoltage().getValue(),
  								aclfBus.getBaseVoltage(), XmlBeansODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU));					
  					}
				}
			} else if (xmlEquivGenData.getCode() == LFGenCodeEnumType.SWING) {
				aclfBus.setGenCode(AclfGenCode.SWING);
				SwingBusAdapter swing = aclfBus.toSwingBus();
				vXml = busXmlData.getGenData().getEquivGen().getDesiredVoltage();
				vpu = UnitType.vConversion(vXml.getValue(),
						aclfBus.getBaseVoltage(), XmlBeansODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU);
				AngleXmlType angXml = busXmlData.getGenData().getEquivGen().getDesiredAngle(); 
				angRad = UnitType.angleConversion(angXml.getValue(),
							XmlBeansODMXmlHelper.toUnit(angXml.getUnit()), UnitType.Rad);				
				swing.setVoltMag(vpu, UnitType.PU);
				swing.setVoltAng(angRad, UnitType.Rad);				
			} else {
				aclfBus.setGenCode(AclfGenCode.NON_GEN);
			}
		} else {
			aclfBus.setGenCode(AclfGenCode.NON_GEN);
		}

		if (busXmlData.getLoadData() != null) {
			aclfBus.setLoadCode(busXmlData.getLoadData().getEquivLoad().getCode() == LFLoadCodeEnumType.CONST_I ? 
							AclfLoadCode.CONST_I : (busXmlData.getLoadData().getEquivLoad().getCode() == LFLoadCodeEnumType.CONST_Z ? 
									AclfLoadCode.CONST_Z : AclfLoadCode.CONST_P));
			LoadBusAdapter loadBus = aclfBus.toLoadBus();
			LoadflowLoadDataXmlType xmlEquivLoad = busXmlData.getLoadData().getEquivLoad();
			if (xmlEquivLoad != null) {
				PowerXmlType p;
				if (aclfBus.getLoadCode() == AclfLoadCode.CONST_P)
					p = xmlEquivLoad.getConstPLoad();
				else if (aclfBus.getLoadCode() == AclfLoadCode.CONST_I)
					p = xmlEquivLoad.getConstILoad();
				else	
					p = xmlEquivLoad.getConstZLoad();
				if (p != null)
					loadBus.setLoad(new Complex(p.getRe(), p.getIm()),
							XmlBeansODMXmlHelper.toUnit(p.getUnit()));
			}
		} else {
			aclfBus.setLoadCode(AclfLoadCode.NON_LOAD);
		}

		if (busXmlData.getShuntY() != null) {
			Complex ypu = UnitType.yConversion(new Complex(busXmlData.getShuntY().getRe(), busXmlData.getShuntY().getIm()),
					aclfBus.getBaseVoltage(), adjNet.getBaseKva(), XmlBeansODMXmlHelper.toUnit(busXmlData.getShuntY().getUnit()),
					UnitType.PU);
			aclfBus.setShuntY(ypu);			
		}
	}

	private static void setBranchLoadflowData(LoadflowBranchDataXmlType braXmlData, AclfBranch aclfBra, 
							AclfNetwork adjNet, IPSSMsgHub msg) throws Exception {
		YXmlType fromShuntY = null, toShuntY = null;
		double baseKva = adjNet.getBaseKva();
		
		if (braXmlData.getCode() == LFBranchCodeEnumType.LINE) {
				aclfBra.setBranchCode(AclfBranchCode.LINE);
				//System.out.println(braXmlData.getLineData().getZ().getIm());
				LineAdapter line = aclfBra.toLine();
				line.setZ(new Complex(braXmlData.getZ().getRe(), braXmlData.getZ().getIm()), 
							XmlBeansODMXmlHelper.toUnit(braXmlData.getZ().getUnit()), 
							aclfBra.getFromAclfBus().getBaseVoltage());
				if (braXmlData.getTotalShuntY() != null)
					line.setHShuntY(new Complex(0.5 * braXmlData.getTotalShuntY().getRe(),
									0.5 * braXmlData.getTotalShuntY().getIm()),
							XmlBeansODMXmlHelper.toUnit(braXmlData.getTotalShuntY().getUnit()), 
							aclfBra.getFromAclfBus().getBaseVoltage());
				
				fromShuntY = braXmlData.getFromShuntY();
				toShuntY = braXmlData.getToShuntY();
		} else if (braXmlData.getCode() == LFBranchCodeEnumType.TRANSFORMER) {
			aclfBra.setBranchCode(AclfBranchCode.XFORMER);
			setXformerLoadflowData(aclfBra, braXmlData, adjNet, msg);
			fromShuntY = braXmlData.getFromShuntY();
			toShuntY = braXmlData.getToShuntY();
		} else if (braXmlData.getCode() == LFBranchCodeEnumType.PHASE_SHIFT_XFORMER) {
			aclfBra.setBranchCode(AclfBranchCode.PS_XFORMER);
			setXformerLoadflowData(aclfBra, braXmlData, adjNet, msg);
			PSXfrAdapter psXfr = aclfBra.toPSXfr();
			if(braXmlData.getFromAngle() != null)
				psXfr.setFromAngle(braXmlData.getFromAngle().getValue(), 
						XmlBeansODMXmlHelper.toUnit(braXmlData.getFromAngle().getUnit()));
			if(braXmlData.getToAngle() != null)
				psXfr.setToAngle(braXmlData.getToAngle().getValue(), 
						XmlBeansODMXmlHelper.toUnit(braXmlData.getToAngle().getUnit()));
			fromShuntY = braXmlData.getFromShuntY();
			toShuntY = braXmlData.getToShuntY();
		} else {
			throw new Exception("Error: LoadflowBranchData.code type, "+ braXmlData.toString());
		}

		if (fromShuntY != null) {
			Complex ypu = UnitType.yConversion(new Complex(fromShuntY.getRe(),	fromShuntY.getIm()),
					aclfBra.getFromAclfBus().getBaseVoltage(), baseKva,
					XmlBeansODMXmlHelper.toUnit(fromShuntY.getUnit()), UnitType.PU);
			aclfBra.setFromShuntY(ypu);
		}
		if (toShuntY != null) {
			Complex ypu = UnitType.yConversion(new Complex(toShuntY.getRe(),	toShuntY.getIm()),
					aclfBra.getToAclfBus().getBaseVoltage(), baseKva,
					XmlBeansODMXmlHelper.toUnit(toShuntY.getUnit()), UnitType.PU);
			aclfBra.setToShuntY(ypu);
		}

		if (braXmlData.getBranchRatingLimit() != null && braXmlData.getBranchRatingLimit().getMva() != null) {
			double factor = 1.0;
			if (braXmlData.getBranchRatingLimit().getMva().getUnit() == ApparentPowerUnitType.PU)
				factor = baseKva * 0.001;
			else if (braXmlData.getBranchRatingLimit().getMva().getUnit() == ApparentPowerUnitType.KVA)
				factor = 0.001;
			aclfBra.setRatingMva1(braXmlData.getBranchRatingLimit().getMva().getRating1()
					* factor);
			aclfBra.setRatingMva2(braXmlData.getBranchRatingLimit().getMva().getRating2()
					* factor);
			aclfBra.setRatingMva3(braXmlData.getBranchRatingLimit().getMva().getRating3()
					* factor);
		}
	}

	private static void setXformerLoadflowData(AclfBranch aclfBra, LoadflowBranchDataXmlType xfrData, 
						AclfNetwork adjNet, IPSSMsgHub msg) {
		double fromBaseV = aclfBra.getFromAclfBus().getBaseVoltage(), 
		       toBaseV = aclfBra.getToAclfBus().getBaseVoltage();
		// turn ratio is based on xfr rated voltage
		// voltage units should be same for both side 
		double fromRatedV = fromBaseV;
		double toRatedV = toBaseV;
		double zratio = 1.0;
		double tapratio = 1.0;
		if (xfrData.getXfrInfo() != null) {
			if (xfrData.getXfrInfo().getRatedVoltage1() != null)
				fromRatedV = xfrData.getXfrInfo().getRatedVoltage1().getValue();
			if (xfrData.getXfrInfo().getRatedVoltage2() != null)
				toRatedV = xfrData.getXfrInfo().getRatedVoltage2().getValue();

			if (!xfrData.getXfrInfo().isSetDataOnSystemBase() &&
				xfrData.getXfrInfo().getRatedPower12() != null && 
				xfrData.getXfrInfo().getRatedPower12().getValue() > 0.0) 
				zratio = xfrData.getXfrInfo().getRatedPower12().getUnit() == ApparentPowerUnitType.KVA?
					adjNet.getBaseKva() / xfrData.getXfrInfo().getRatedPower12().getValue() :
						0.001 * adjNet.getBaseKva() / xfrData.getXfrInfo().getRatedPower12().getValue();

			if (!xfrData.getXfrInfo().isSetDataOnSystemBase())
				tapratio = (fromRatedV/fromBaseV) / (toRatedV/toBaseV) ;
		}
		
		double baseV = fromBaseV > toBaseV ? fromBaseV : toBaseV;
		XfrAdapter xfr = aclfBra.toXfr();
		xfr.setZ(new Complex(xfrData.getZ().getRe()*zratio, xfrData.getZ().getIm()*zratio),
				XmlBeansODMXmlHelper.toUnit(xfrData.getZ().getUnit()), baseV);
		xfr.setFromTurnRatio(xfrData.getFromTurnRatio().getValue() == 0.0 ? 1.0 : xfrData
				.getFromTurnRatio().getValue()*tapratio, UnitType.PU);
		xfr.setToTurnRatio(xfrData.getToTurnRatio().getValue() == 0.0 ? 1.0 : xfrData
				.getToTurnRatio().getValue()/tapratio, UnitType.PU);
		
	}
}