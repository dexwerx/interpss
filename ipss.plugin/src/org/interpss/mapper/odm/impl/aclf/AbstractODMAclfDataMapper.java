/*
 * @(#)AbstractODMAclfDataMapper.java   
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

package org.interpss.mapper.odm.impl.aclf;

import javax.xml.bind.JAXBElement;

import org.apache.commons.math.complex.Complex;
import org.ieee.odm.model.BaseJaxbHelper;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.AngleXmlType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenDataXmlType;
import org.ieee.odm.schema.LoadflowLoadDataXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NetworkCategoryEnumType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.PSXfr3WBranchXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.PowerXmlType;
import org.ieee.odm.schema.VoltageXmlType;
import org.ieee.odm.schema.Xfr3WBranchXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YXmlType;
import org.interpss.mapper.odm.AbstractODMSimuCtxDataMapper;
import org.interpss.mapper.odm.ODMXmlHelper;
import org.interpss.numeric.datatype.LimitType;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
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
import com.interpss.core.aclf.adpter.PVBusAdapter;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public abstract class AbstractODMAclfDataMapper<Tfrom> extends AbstractODMSimuCtxDataMapper<Tfrom> {
	public AbstractODMAclfDataMapper(IPSSMsgHub msg) {
		super(msg);
	}
	
	/**
	 * map into store in the ODM parser into simuCtx object
	 * 
	 * @param p ODM parser object, representing a ODM xml file
	 * @param simuCtx
	 */
	@Override
	public boolean map2Model(Tfrom p, SimuContext simuCtx) {
		boolean noError = true;
		AclfModelParser parser = (AclfModelParser)p;
		if (parser.getAclfNet().getNetworkCategory() == NetworkCategoryEnumType.TRANSMISSION ) {
			LoadflowNetXmlType xmlNet = parser.getAclfNet();
			simuCtx.setNetType(SimuCtxType.ACLF_NETWORK);
			try {
				AclfNetwork adjNet = CoreObjectFactory.createAclfNetwork();
				mapAclfNetworkData(adjNet, xmlNet);
				simuCtx.setAclfNet(adjNet);

				for (JAXBElement<? extends BusXmlType> bus : xmlNet.getBusList().getBus()) {
					LoadflowBusXmlType busRec = (LoadflowBusXmlType) bus.getValue();
					mapAclfBusData(busRec, adjNet);
				}

				for (JAXBElement<? extends BaseBranchXmlType> b : xmlNet.getBranchList().getBranch()) {
					AclfBranch aclfBranch = CoreObjectFactory.createAclfBranch();
					mapAclfBranchData(b.getValue(), aclfBranch, adjNet, simuCtx.getMsgHub());
				}
			} catch (InterpssException e) {
				e.printStackTrace();
				IpssLogger.getLogger().severe(e.toString());
				noError = false;
			}
		} else {
			IpssLogger.getLogger().severe(
							"Error: currently only Transmission NetworkType has been implemented");
			return false;
		}
		
		OriginalDataFormatEnumType ofmt = parser.getStudyCase().getContentInfo().getOriginalDataFormat();
		simuCtx.getNetwork().setOriginalDataFormat(ODMXmlHelper.map(ofmt));		
		return noError;
	}

	/**
	 * Map the network info only
	 * 
	 * @param xmlNet
	 * @return
	 */
	public void mapAclfNetworkData(AclfNetwork net, LoadflowNetXmlType xmlNet) {
		mapNetworkData(net, xmlNet);
	}
	
	/**
	 * Map a bus record
	 * 
	 * @param busRec
	 * @param adjNet
	 * @return
	 * @throws Exception
	 */
	public AclfBus mapAclfBusData(LoadflowBusXmlType busRec, AclfNetwork adjNet) throws InterpssException {
		AclfBus aclfBus = CoreObjectFactory.createAclfBus(busRec.getId(), adjNet);
		//adjNet.addBus(aclfBus);
		mapBaseBusData(busRec, aclfBus, adjNet);
		setAclfBusData(busRec, aclfBus, adjNet);
		return aclfBus;
	}
	
	/**
	 * Set Aclf data
	 * 
	 * @param busXmlData
	 * @param aclfBus
	 * @param adjNet
	 * @throws InterpssException
	 */
	public void setAclfBusData(LoadflowBusXmlType busXmlData, AclfBus aclfBus, AclfNetwork adjNet) throws InterpssException {
		VoltageXmlType vXml = busXmlData.getVoltage();
		double vpu = vXml == null ? 1.0 : UnitType.vConversion(vXml.getValue(),
				aclfBus.getBaseVoltage(), ODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU);
		double angRad = busXmlData.getAngle() ==  null? 0.0 :
			UnitType.angleConversion(busXmlData.getAngle().getValue(),
					ODMXmlHelper.toUnit(busXmlData.getAngle().getUnit()), UnitType.Rad);
		aclfBus.setVoltage(vpu, angRad);

		if (busXmlData.getGenData() != null) {
			LoadflowGenDataXmlType xmlEquivGenData = busXmlData.getGenData().getEquivGen();
			if (xmlEquivGenData.getCode() == LFGenCodeEnumType.PQ) {
				aclfBus.setGenCode(AclfGenCode.GEN_PQ);
				PQBusAdapter pqBus = aclfBus.toPQBus();
				pqBus.setGen(new Complex(xmlEquivGenData.getPower().getRe(), 
						                 xmlEquivGenData.getPower().getIm()),
						           ODMXmlHelper.toUnit(xmlEquivGenData.getPower().getUnit()));
				if (xmlEquivGenData.getVoltageLimit() != null) {
  			  		final PQBusLimit pqLimit = CoreObjectFactory.createPQBusLimit(aclfBus);
  			  		pqLimit.setVLimit(new LimitType(xmlEquivGenData.getVoltageLimit().getMax(), 
  			  										xmlEquivGenData.getVoltageLimit().getMin()), 
  			  				ODMXmlHelper.toUnit(xmlEquivGenData.getVoltageLimit().getUnit()));						
				}
			} else if (xmlEquivGenData.getCode() == LFGenCodeEnumType.PV &&
					xmlEquivGenData != null) {
				if (xmlEquivGenData.getRemoteVoltageControlBus() == null) {
					aclfBus.setGenCode(AclfGenCode.GEN_PV);
					PVBusAdapter pvBus = aclfBus.toPVBus();
					//if (xmlEquivGenData == null)
					//	System.out.print(busXmlData);
					pvBus.setGenP(xmlEquivGenData.getPower().getRe(),
								ODMXmlHelper.toUnit(xmlEquivGenData.getPower().getUnit()));
					vXml = xmlEquivGenData.getDesiredVoltage();
					if (vXml == null)
						throw new InterpssException("For Gen PV bus, equivGenData.desiredVoltage has to be defined, busId: " + aclfBus.getId());
					vpu = UnitType.vConversion(vXml.getValue(),
							aclfBus.getBaseVoltage(), ODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU);
					pvBus.setVoltMag(vpu, UnitType.PU);
					if (xmlEquivGenData.getQLimit() != null) {
	  			  		final PVBusLimit pvLimit = CoreObjectFactory.createPVBusLimit(aclfBus);
	  			  		pvLimit.setQLimit(new LimitType(xmlEquivGenData.getQLimit().getMax(), 
	  			  										xmlEquivGenData.getQLimit().getMin()), 
	  			  				ODMXmlHelper.toUnit(xmlEquivGenData.getQLimit().getUnit()));						
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
  						AclfBus remoteBus = adjNet.getAclfBus(remoteId);
  	  					if (remoteBus != null) {
  	  	  					if (remoteBus.isGenPV())
  	  	  						remoteBus.setGenCode(AclfGenCode.GEN_PQ);
  	  	  			  		final RemoteQBus reQBus = CoreObjectFactory.createRemoteQBus(aclfBus, 
  	  	  			  				RemoteQControlType.BUS_VOLTAGE, adjNet, remoteId);
  	  			  			final PQBusAdapter gen = (PQBusAdapter)aclfBus.getAdapter(PQBusAdapter.class);
  	  			  			gen.setGen(new Complex(xmlEquivGenData.getPower().getRe(),
  	  			  					               xmlEquivGenData.getPower().getIm()), 
  	  			  					               ODMXmlHelper.toUnit(xmlEquivGenData.getPower().getUnit()));
  	  	  			  		reQBus.setQLimit(new LimitType(xmlEquivGenData.getQLimit().getMax(), 
  	  														xmlEquivGenData.getQLimit().getMin()), 
  	  										ODMXmlHelper.toUnit(xmlEquivGenData.getQLimit().getUnit()));						
  	  	  			  		reQBus.setVSpecified(UnitType.vConversion(xmlEquivGenData.getDesiredVoltage().getValue(),
  	  								aclfBus.getBaseVoltage(), ODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU));					
  	  					}
  					}
				}
			} else if (xmlEquivGenData.getCode() == LFGenCodeEnumType.SWING) {
				aclfBus.setGenCode(AclfGenCode.SWING);
				SwingBusAdapter swing = aclfBus.toSwingBus();
				vXml = busXmlData.getGenData().getEquivGen().getDesiredVoltage();
				vpu = UnitType.vConversion(vXml.getValue(),
						aclfBus.getBaseVoltage(), ODMXmlHelper.toUnit(vXml.getUnit()), UnitType.PU);
				AngleXmlType angXml = busXmlData.getGenData().getEquivGen().getDesiredAngle(); 
				angRad = UnitType.angleConversion(angXml.getValue(),
							ODMXmlHelper.toUnit(angXml.getUnit()), UnitType.Rad);				
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
							ODMXmlHelper.toUnit(p.getUnit()));
			}
		} else {
			aclfBus.setLoadCode(AclfLoadCode.NON_LOAD);
		}

		if (busXmlData.getShuntY() != null) {
			Complex ypu = UnitType.yConversion(new Complex(busXmlData.getShuntY().getRe(), busXmlData.getShuntY().getIm()),
					aclfBus.getBaseVoltage(), adjNet.getBaseKva(), ODMXmlHelper.toUnit(busXmlData.getShuntY().getUnit()),
					UnitType.PU);
			aclfBus.setShuntY(ypu);			
		}
	}
	
	/**
	 * 
	 * @param branch
	 * @param adjNet
	 * @param msg
	 * @throws Exception
	 */
	public void mapAclfBranchData(BaseBranchXmlType branch, AclfBranch aclfBranch, AclfNetwork adjNet, IPSSMsgHub msg) throws InterpssException {
		setAclfBranchData((BranchXmlType)branch, aclfBranch, adjNet);
		if (branch instanceof LineBranchXmlType) {
			LineBranchXmlType branchRec = (LineBranchXmlType) branch;
			setLineBranchData(branchRec, aclfBranch, adjNet);
		}
		else if (branch instanceof PSXfr3WBranchXmlType) {
			PSXfr3WBranchXmlType branchRec = (PSXfr3WBranchXmlType) branch;
			System.out.println("PSXfr3WBranchXmlType: " + branchRec.getId());
			//setPsXfrBranchData(branchRec, aclfBranch, adjNet, msg);
		}		
		else if (branch instanceof Xfr3WBranchXmlType) {
			Xfr3WBranchXmlType branchRec = (Xfr3WBranchXmlType) branch;
			//setXfrBranchData(branchRec, aclfBranch, adjNet, msg);
			System.out.println("Xfr3WBranchXmlType: " + branchRec.getId());
		}
		else if (branch instanceof PSXfrBranchXmlType) {
			PSXfrBranchXmlType branchRec = (PSXfrBranchXmlType) branch;
			AclfXfrDataHelper helper = new AclfXfrDataHelper(adjNet, aclfBranch);
			helper.setPsXfrBranchData(branchRec);
		}		
		else if (branch instanceof XfrBranchXmlType) {
			XfrBranchXmlType branchRec = (XfrBranchXmlType) branch;
			AclfXfrDataHelper helper = new AclfXfrDataHelper(adjNet, aclfBranch);
			helper.setXfrBranchData(branchRec);
		}
	}
	
	private void setAclfBranchData(BranchXmlType branchRec, AclfBranch aclfBranch, AclfNetwork adjNet) throws InterpssException {
		mapBaseBranchRec(branchRec, aclfBranch, adjNet);		
		if (branchRec.getRatingLimit() != null && branchRec.getRatingLimit().getMva() != null) {
			double factor = 1.0;
			if (branchRec.getRatingLimit().getMva().getUnit() == ApparentPowerUnitType.PU)
				factor = adjNet.getBaseKva() * 0.001;
			else if (branchRec.getRatingLimit().getMva().getUnit() == ApparentPowerUnitType.KVA)
				factor = 0.001;
			aclfBranch.setRatingMva1(branchRec.getRatingLimit().getMva().getRating1() * factor);
			if (branchRec.getRatingLimit().getMva().getRating2() != null)
				aclfBranch.setRatingMva2(branchRec.getRatingLimit().getMva().getRating2() * factor);
			if (branchRec.getRatingLimit().getMva().getRating3() != null)
				aclfBranch.setRatingMva3(branchRec.getRatingLimit().getMva().getRating3() * factor);
			//if (branchRec.getRatingLimit().getMva().getRating4())
			//	aclfBranch.setRatingMva4(branchRec.getRatingLimit().getMva().getRating3() * factor);
		}
	}

	private void setLineBranchData(LineBranchXmlType braLine, AclfBranch aclfBra, 
							AclfNetwork adjNet) throws InterpssException {
		YXmlType fromShuntY = null, toShuntY = null;
		double baseKva = adjNet.getBaseKva();
		
		aclfBra.setBranchCode(AclfBranchCode.LINE);
		//System.out.println(braXmlData.getLineData().getZ().getIm());
		LineAdapter line = aclfBra.toLine();
		if (braLine.getZ() == null) {
			throw new InterpssException("Line data error, Z == null, branch id: " + braLine.getId());
		}
		
		line.setZ(new Complex(braLine.getZ().getRe(), braLine.getZ().getIm()), 
					ODMXmlHelper.toUnit(braLine.getZ().getUnit()), 
					aclfBra.getFromAclfBus().getBaseVoltage());
		if (braLine.getTotalShuntY() != null)
			line.setHShuntY(new Complex(0.5 * braLine.getTotalShuntY().getRe(),
							0.5 * braLine.getTotalShuntY().getIm()),
					ODMXmlHelper.toUnit(braLine.getTotalShuntY().getUnit()), 
					aclfBra.getFromAclfBus().getBaseVoltage());
		
		fromShuntY = braLine.getFromShuntY();
		toShuntY = braLine.getToShuntY();

		if (fromShuntY != null) {
			Complex ypu = UnitType.yConversion(new Complex(fromShuntY.getRe(),	fromShuntY.getIm()),
					aclfBra.getFromAclfBus().getBaseVoltage(), baseKva,
					ODMXmlHelper.toUnit(fromShuntY.getUnit()), UnitType.PU);
			aclfBra.setFromShuntY(ypu);
		}
		if (toShuntY != null) {
			Complex ypu = UnitType.yConversion(new Complex(toShuntY.getRe(),	toShuntY.getIm()),
					aclfBra.getToAclfBus().getBaseVoltage(), baseKva,
					ODMXmlHelper.toUnit(toShuntY.getUnit()), UnitType.PU);
			aclfBra.setToShuntY(ypu);
		}
	}
}