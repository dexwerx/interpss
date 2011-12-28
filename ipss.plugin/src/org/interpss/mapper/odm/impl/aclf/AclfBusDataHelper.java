/*
 * @(#)AAclfBusDataHelper.java   
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
 * @Date 02/01/2011
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.odm.impl.aclf;

import org.apache.commons.math.complex.Complex;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.AclfGenDataXmlType;
import org.ieee.odm.schema.AclfLoadDataXmlType;
import org.ieee.odm.schema.AngleXmlType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.LoadflowLoadXmlType;
import org.ieee.odm.schema.PowerXmlType;
import org.ieee.odm.schema.ReactivePowerXmlType;
import org.ieee.odm.schema.VoltageXmlType;
import org.ieee.odm.schema.YXmlType;
import org.interpss.numeric.datatype.LimitType;
import org.interpss.numeric.datatype.Unit.UnitType;

import com.interpss.CoreObjectFactory;
import com.interpss.common.datatype.UnitHelper;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adj.PQBusLimit;
import com.interpss.core.aclf.adj.PVBusLimit;
import com.interpss.core.aclf.adj.RemoteQBus;
import com.interpss.core.aclf.adj.RemoteQControlType;
import com.interpss.core.aclf.adpter.LoadBusAdapter;
import com.interpss.core.aclf.adpter.PQBusAdapter;
import com.interpss.core.aclf.adpter.PVBusAdapter;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.pssl.util.ODMUnitHelper;

public class AclfBusDataHelper {
	private AclfNetwork aclfNet = null;
	private AclfBus aclfBus = null;
	
	public AclfBusDataHelper(AclfNetwork aclfNet, AclfBus aclfBus) {
		this.aclfNet = aclfNet;
		this.aclfBus = aclfBus;
	}
	
	public void setAclfBusData(LoadflowBusXmlType busXmlData) throws InterpssException {
		VoltageXmlType vXml = busXmlData.getVoltage();
		double vpu = 1.0;
		if (vXml != null) {
			UnitType unit = ODMUnitHelper.toVoltageUnit(vXml.getUnit());
			vpu = UnitHelper.vConversion(vXml.getValue(), aclfBus.getBaseVoltage(), unit, UnitType.PU);
		}
		double angRad = 0.0;
		if (busXmlData.getAngle() !=  null) {
			UnitType unit = ODMUnitHelper.toAngleUnit(busXmlData.getAngle().getUnit()); 
			angRad = UnitHelper.angleConversion(busXmlData.getAngle().getValue(), unit, UnitType.Rad); 
		}
		aclfBus.setVoltage(vpu, angRad);

		if (busXmlData.getGenData() != null) {
			mapGenData(busXmlData.getGenData());
		} else {
			aclfBus.setGenCode(AclfGenCode.NON_GEN);
		}

		if (busXmlData.getLoadData() != null) {
			mapLoadData(busXmlData.getLoadData());
		} else {
			aclfBus.setLoadCode(AclfLoadCode.NON_LOAD);
		}

		if (busXmlData.getShuntY() != null) {
			YXmlType shuntY = busXmlData.getShuntY();
//			byte unit = shuntY.getUnit() == YUnitType.MVAR? UnitType.mVar : UnitType.PU;
			UnitType unit = ODMUnitHelper.toYUnit(shuntY.getUnit());
			Complex ypu = UnitHelper.yConversion(new Complex(shuntY.getRe(), shuntY.getIm()),
					aclfBus.getBaseVoltage(), aclfNet.getBaseKva(), unit, UnitType.PU);
			//System.out.println("----------->" + shuntY.getIm() + ", " + shuntY.getUnit() + ", " + ypu.getImaginary());
			aclfBus.setShuntY(ypu);
		}

		if (busXmlData.getShuntCompensatorData() != null) {
			ReactivePowerXmlType shuntB = busXmlData.getShuntCompensatorData().getEquivQ();
//			byte unit = shuntB.getUnit() == ReactivePowerUnitType.MVAR? UnitType.mVar : UnitType.PU;
			UnitType unit = ODMUnitHelper.toReactivePowerUnit(shuntB.getUnit());
			Complex ypu = UnitHelper.yConversion(new Complex(0.0, shuntB.getValue()),
					aclfBus.getBaseVoltage(), aclfNet.getBaseKva(), unit, UnitType.PU);
			//System.out.println("----------->" + shuntB.getValue() + ", " + shuntB.getUnit() + ", " + ypu.getImaginary());
			aclfBus.setShuntY(ypu);
		}
	}
	
	private void mapGenData(AclfGenDataXmlType genData) throws InterpssException {
		LoadflowGenXmlType xmlEquivGenData = genData.getEquivGen();
		VoltageXmlType vXml = xmlEquivGenData.getDesiredVoltage();
		if (xmlEquivGenData.getCode() == LFGenCodeEnumType.PQ) {
			aclfBus.setGenCode(AclfGenCode.GEN_PQ);
			PQBusAdapter pqBus = aclfBus.toPQBus();
			if (xmlEquivGenData.getPower() != null)
				pqBus.setGen(new Complex(xmlEquivGenData.getPower().getRe(), 
					                 xmlEquivGenData.getPower().getIm()),
					                 ODMUnitHelper.toApparentPowerUnit(xmlEquivGenData.getPower().getUnit()));
			if (xmlEquivGenData.getVoltageLimit() != null) {
			  		final PQBusLimit pqLimit = CoreObjectFactory.createPQBusLimit(aclfBus);
			  		pqLimit.setVLimit(new LimitType(xmlEquivGenData.getVoltageLimit().getMax(), 
			  										xmlEquivGenData.getVoltageLimit().getMin()), 
			  										ODMUnitHelper.toVoltageUnit(xmlEquivGenData.getVoltageLimit().getUnit()));						
			}
		} else if (xmlEquivGenData.getCode() == LFGenCodeEnumType.PV &&
				xmlEquivGenData != null) {
			if (xmlEquivGenData.getRemoteVoltageControlBus() == null) {
				aclfBus.setGenCode(AclfGenCode.GEN_PV);
				PVBusAdapter pvBus = aclfBus.toPVBus();
				//if (xmlEquivGenData == null)
				//	System.out.print(busXmlData);
				if (xmlEquivGenData.getPower() != null) {
					pvBus.setGenP(xmlEquivGenData.getPower().getRe(),
							ODMUnitHelper.toApparentPowerUnit(xmlEquivGenData.getPower().getUnit()));
				
					if (vXml == null)
						throw new InterpssException("For Gen PV bus, equivGenData.desiredVoltage has to be defined, busId: " + aclfBus.getId());
					double vpu = UnitHelper.vConversion(vXml.getValue(),
						aclfBus.getBaseVoltage(), ODMUnitHelper.toVoltageUnit(vXml.getUnit()), UnitType.PU);
				
					pvBus.setVoltMag(vpu, UnitType.PU);
					if (xmlEquivGenData.getQLimit() != null) {
  			  			final PVBusLimit pvLimit = CoreObjectFactory.createPVBusLimit(aclfBus);
  			  			pvLimit.setQLimit(new LimitType(xmlEquivGenData.getQLimit().getMax(), 
  			  										xmlEquivGenData.getQLimit().getMin()), 
  			  									ODMUnitHelper.toReactivePowerUnit(xmlEquivGenData.getQLimit().getUnit()));
  			  			pvLimit.setStatus(xmlEquivGenData.getQLimit().isActive());
					}
				}
			}
			else {
				// remote bus voltage
					IpssLogger.getLogger().fine("Bus is a RemoteQBus, id: " + aclfBus.getId());
					aclfBus.setGenCode(AclfGenCode.GEN_PQ);
					// The remote bus to be adjusted is normally defined as a PV bus. It needs to
					// be changed to PQ bus
					String remoteId = BaseJaxbHelper.getRecId(xmlEquivGenData.getRemoteVoltageControlBus());
					if (remoteId != null) {
						AclfBus remoteBus = aclfNet.getAclfBus(remoteId);
	  					if (remoteBus != null) {
	  	  					if (remoteBus.isGenPV())
	  	  						remoteBus.setGenCode(AclfGenCode.GEN_PQ);
	  	  			  		final RemoteQBus reQBus = CoreObjectFactory.createRemoteQBus(aclfBus, 
	  	  			  				RemoteQControlType.BUS_VOLTAGE, aclfNet, remoteId);
	  			  			final PQBusAdapter gen = (PQBusAdapter)aclfBus.getAdapter(PQBusAdapter.class);
	  			  			gen.setGen(new Complex(xmlEquivGenData.getPower().getRe(),
	  			  					               xmlEquivGenData.getPower().getIm()), 
	  			  					           ODMUnitHelper.toApparentPowerUnit(xmlEquivGenData.getPower().getUnit()));
	  	  			  		reQBus.setQLimit(new LimitType(xmlEquivGenData.getQLimit().getMax(), 
	  														xmlEquivGenData.getQLimit().getMin()), 
	  														ODMUnitHelper.toReactivePowerUnit(xmlEquivGenData.getQLimit().getUnit()));						
	  	  			  		reQBus.setVSpecified(UnitHelper.vConversion(xmlEquivGenData.getDesiredVoltage().getValue(),
	  								aclfBus.getBaseVoltage(), ODMUnitHelper.toVoltageUnit(vXml.getUnit()), UnitType.PU));					
	  					}
					}
			}
		} else if (xmlEquivGenData.getCode() == LFGenCodeEnumType.SWING) {
			aclfBus.setGenCode(AclfGenCode.SWING);
			SwingBusAdapter swing = aclfBus.toSwingBus();
			double vpu = UnitHelper.vConversion(vXml.getValue(),
					aclfBus.getBaseVoltage(), ODMUnitHelper.toVoltageUnit(vXml.getUnit()), UnitType.PU);
			AngleXmlType angXml = genData.getEquivGen().getDesiredAngle(); 
			double angRad = UnitHelper.angleConversion(angXml.getValue(),
					ODMUnitHelper.toAngleUnit(angXml.getUnit()), UnitType.Rad);				
			swing.setVoltMag(vpu, UnitType.PU);
			swing.setVoltAng(angRad, UnitType.Rad);				
		} else {
			aclfBus.setGenCode(AclfGenCode.NON_GEN);
		}
	}
	
	private void mapLoadData(AclfLoadDataXmlType loadData) {
		aclfBus.setLoadCode(loadData.getEquivLoad().getCode() == LFLoadCodeEnumType.CONST_I ? 
				AclfLoadCode.CONST_I : (loadData.getEquivLoad().getCode() == LFLoadCodeEnumType.CONST_Z ? 
						AclfLoadCode.CONST_Z : AclfLoadCode.CONST_P));
		LoadBusAdapter loadBus = aclfBus.toLoadBus();
		LoadflowLoadXmlType xmlEquivLoad = loadData.getEquivLoad();
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
						ODMUnitHelper.toApparentPowerUnit(p.getUnit()));
		}
	}
}