/*
 * @(#)AbstractODMAcscDataMapper.java   
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

package org.interpss.mapper.odm.impl.acsc;

import static org.interpss.mapper.odm.ODMUnitHelper.toYUnit;
import static org.interpss.mapper.odm.ODMUnitHelper.toZUnit;

import javax.xml.bind.JAXBElement;

import org.apache.commons.math.complex.Complex;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.schema.AnalysisCategoryEnumType;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.GroundingEnumType;
import org.ieee.odm.schema.GroundingXmlType;
import org.ieee.odm.schema.IpssStudyScenarioXmlType;
import org.ieee.odm.schema.LineShortCircuitXmlType;
import org.ieee.odm.schema.NetworkCategoryEnumType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.ieee.odm.schema.PSXfrShortCircuitXmlType;
import org.ieee.odm.schema.ScSimpleBusXmlType;
import org.ieee.odm.schema.ShortCircuitBusEnumType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitNetXmlType;
import org.ieee.odm.schema.XformerConnectionXmlType;
import org.ieee.odm.schema.XformrtConnectionEnumType;
import org.ieee.odm.schema.XfrShortCircuitXmlType;
import org.ieee.odm.schema.YXmlType;
import org.ieee.odm.schema.ZXmlType;
import org.interpss.mapper.odm.ODMHelper;
import org.interpss.mapper.odm.impl.aclf.AbstractODMAclfDataMapper;
import org.interpss.mapper.odm.impl.aclf.AclfBusDataHelper;
import org.interpss.numeric.NumericConstant;
import org.interpss.numeric.datatype.Unit.UnitType;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.BusGroundCode;
import com.interpss.core.acsc.BusScCode;
import com.interpss.core.acsc.SequenceCode;
import com.interpss.core.acsc.XfrConnectCode;
import com.interpss.core.acsc.adpter.AcscLineAdapter;
import com.interpss.core.acsc.adpter.AcscXfrAdapter;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public abstract class AbstractODMAcscDataMapper<Tfrom> extends AbstractODMAclfDataMapper<Tfrom> {
	public AbstractODMAcscDataMapper() {
	}
	
	/**
	 * transfer info stored in the parser object into simuCtx object. 
	 * 
	 * @param p a ODM parser object, representign an ODM xml file
	 * @param simuCtx
	 * @return
	 */
	@Override
	public boolean map2Model(Tfrom p, SimuContext simuCtx) {
		boolean noError = true;

		AcscModelParser parser = (AcscModelParser) p;
		if (simuCtx.getNetType() != SimuCtxType.ACSC_NET) {
			IpssLogger.getLogger().severe("SimuNetwork type should be set to ACSC_FAULT_NET for mapping ODM to SimpleFaultNetwork");
			return false;
		}
		
		if (parser.getStudyCase().getNetworkCategory() == NetworkCategoryEnumType.TRANSMISSION
				&& parser.getStudyCase().getAnalysisCategory() == AnalysisCategoryEnumType.SHORT_CIRCUIT) {
			// get the base net xml record from the parser object
			ShortCircuitNetXmlType xmlNet = parser.getAcscNet();
			try {
				// create a AcscFaultNetwork object and map the net info 
				AcscNetwork acscFaultNet =  CoreObjectFactory.createAcscNetwork();						
				simuCtx.setAcscNet(acscFaultNet);

				SimpleFaultAlgorithm acscAlgo = CoreObjectFactory.createSimpleFaultAlgorithm(acscFaultNet);
				simuCtx.setSimpleFaultAlgorithm(acscAlgo);
				
				mapAcscNetworkData(acscFaultNet,xmlNet);

				// map the bus info
				for (JAXBElement<? extends BusXmlType> bus : xmlNet.getBusList().getBus()) {
					// for short circuit, the bus could be acscBus or acscNoLFBus 
					AcscBus acscBus = CoreObjectFactory.createAcscBus(bus.getValue().getId(), acscFaultNet);		
					// add the acscBus object into acscNet and build bus <-> net relationship
					//acscNet.addBus(acscBus);
					if (bus.getValue() instanceof ShortCircuitBusXmlType) {
						// lf info included
						ShortCircuitBusXmlType acscBusXml = (ShortCircuitBusXmlType) bus.getValue();
						// map the base bus info part
						mapBaseBusData(acscBusXml, acscBus, acscFaultNet);
						// map the Aclf info part		
						AclfBusDataHelper helper = new AclfBusDataHelper(acscFaultNet, acscBus);
						helper.setAclfBusData(acscBusXml);
						
						setAcscBusData(acscBusXml, acscBus);
					} else if (bus.getValue() instanceof ScSimpleBusXmlType){
						// no loadflow info included
						ScSimpleBusXmlType acscBusXml = (ScSimpleBusXmlType) bus.getValue();
						// map the base bus info part
						mapBaseBusData(acscBusXml, acscBus, acscFaultNet);
						AbstractODMAcscDataMapper.setAcscBusNoLFData(acscBusXml, acscBus);
					}
					else {
						IpssLogger.getLogger().severe( "Error: only scscBus and pss:acscNoLFBus could be used for DStab study");
						noError = false;
					}
				}

				// map the branch info
				for (JAXBElement<? extends BaseBranchXmlType> branch : xmlNet.getBranchList().getBranch()) {
					if (branch.getValue() instanceof LineShortCircuitXmlType || 
							branch.getValue() instanceof XfrShortCircuitXmlType ||
							branch.getValue() instanceof PSXfrShortCircuitXmlType) {
						AcscBranch acscBranch = CoreObjectFactory.createAcscBranch();
						BranchXmlType acscBraXml = (BranchXmlType)branch.getValue();
						// the branch is added into acscNet in the mapAclfBranchData() method
						mapAclfBranchData(branch.getValue(), acscBranch, acscFaultNet);
						setAcscBranchData(acscBraXml, acscBranch);
					}
					else {
						IpssLogger.getLogger().severe( "Error: only acsc<Branch> could be used for SC study");
						noError = false;
					}
				}		
				
				// map the fault network information
				if(parser.getStudyCase().getStudyScenario()!=null){
					IpssStudyScenarioXmlType s = (IpssStudyScenarioXmlType)parser.getStudyCase().getStudyScenario().getValue();
					new AcscScenarioHelper(acscFaultNet, acscAlgo).
								mapOneFaultScenario(s);
				}
			} catch (InterpssException e) {
				IpssLogger.getLogger().severe(e.toString());
				e.printStackTrace();
				noError = false;
			}
		} 
		else {
			IpssLogger.getLogger().severe( "Error: wrong Transmission NetworkType and/or ApplicationType");
			return false;
		}

		OriginalDataFormatEnumType ofmt = parser.getStudyCase().getContentInfo().getOriginalDataFormat();
		simuCtx.getAcscNet().setOriginalDataFormat(ODMHelper.map(ofmt));		
		return noError;
	}

	/**
	 * Map the network info only
	 * 
	 * @param xmlNet
	 * @return
	 */
	public void mapAcscNetworkData(AcscNetwork net, ShortCircuitNetXmlType xmlNet) {
		mapAclfNetworkData(net, xmlNet);
		net.setPositiveSeqDataOnly(xmlNet.isPositiveSeqDataOnly());		
	}	

	/**
	 * Set SC bus info only
	 * 
	 * @param acscBusXml
	 * @param acscBus
	 */
	public void setAcscBusData(ShortCircuitBusXmlType acscBusXml, AcscBus acscBus) throws InterpssException {
		if (acscBusXml.getScCode() == ShortCircuitBusEnumType.CONTRIBUTING) {
			setContributeBusInfo(acscBusXml, acscBus);
		} else { // non-contributing
			setNonContributeBusFormInfo(acscBus);
		} 
	}

	public static void setAcscBusNoLFData(ScSimpleBusXmlType acscBusXml, AcscBus acscBus) throws InterpssException {
		if (acscBusXml.getScCode() == ShortCircuitBusEnumType.CONTRIBUTING) {
			setContributeBusNoLFInfo(acscBusXml, acscBus);
		} else { // non-contributing
			setNonContributeBusFormInfo(acscBus);
		} 
	}

	private static void setNonContributeBusFormInfo(AcscBus acscBus) {
		acscBus.setScCode(BusScCode.NON_CONTRI);
		acscBus.setScZ(NumericConstant.LargeBusZ, SequenceCode.POSITIVE);
		acscBus.setScZ(NumericConstant.LargeBusZ, SequenceCode.NEGATIVE);
		acscBus.setScZ(NumericConstant.LargeBusZ, SequenceCode.ZERO);
		acscBus.getGrounding().setCode(BusGroundCode.UNGROUNDED);
		acscBus.getGrounding().setZ(NumericConstant.LargeBusZ);
	}

	private static void setContributeBusInfo(ShortCircuitBusXmlType busData, AcscBus acscBus) {
		acscBus.setScCode(BusScCode.CONTRIBUTE);
		if (busData.getScGenData() != null) {
			setBusScZ(acscBus, acscBus.getNetwork().getBaseKva(), 
					busData.getScGenData().getPotiveZ(),
					busData.getScGenData().getNegativeZ(),
					busData.getScGenData().getZeroZ());
			setBusScZg(acscBus, acscBus.getBaseVoltage(), acscBus.getNetwork().getBaseKva(), 
					busData.getScGenData().getGrounding());
		}
	}

	private static void setContributeBusNoLFInfo(ScSimpleBusXmlType busData, AcscBus acscBus) {
		acscBus.setScCode(BusScCode.CONTRIBUTE);
		if (busData.getScGenData() != null) {
			setBusScZ(acscBus, acscBus.getNetwork().getBaseKva(), 
					busData.getScGenData().getPotiveZ(),
					busData.getScGenData().getNegativeZ(),
					busData.getScGenData().getZeroZ());
			if(busData.getScGenData().getGrounding() != null){
				setBusScZg(acscBus, acscBus.getBaseVoltage(), acscBus.getNetwork().getBaseKva(), 
						busData.getScGenData().getGrounding());
			}

		}
	}

	private static void setBusScZ(AcscBus bus, double baseKVA, 
			ZXmlType z1, ZXmlType z2, ZXmlType z0) {
		UnitType zUnit = toZUnit.f(z1.getUnit());
		bus.setScZ(new Complex(z1.getRe(), z1.getIm()), SequenceCode.POSITIVE, zUnit);
		bus.setScZ(new Complex(z2.getRe(), z2.getIm()), SequenceCode.NEGATIVE, zUnit);
		bus.setScZ(new Complex(z0.getRe(), z0.getIm()), SequenceCode.ZERO, zUnit);
	}

	private static void setBusScZg(AcscBus bus, double baseV, double baseKVA, GroundingXmlType g) {
		ZXmlType z = g.getGroundingZ();
		bus.getGrounding().setCode(ODMHelper.toBusGroundCode(g.getGroundingConnection()));
		if(z != null){
			UnitType zgUnit = toZUnit.f(z.getUnit());			
			bus.getGrounding().setZ(new Complex(z.getRe(), z.getIm()), zgUnit, baseV, baseKVA);
		}
	}

	/**
	 * Set SC branch info only
	 * 
	 * @param acscBraXml
	 * @param acscBra
	 * @param msg
	 * @return
	 */
	public void setAcscBranchData(BranchXmlType acscBraXml, AcscBranch acscBra) {

		if (acscBraXml instanceof LineShortCircuitXmlType) { // line branch
			setAcscLineFormInfo((LineShortCircuitXmlType)acscBraXml, acscBra);
		} 
		else if ( acscBraXml instanceof XfrShortCircuitXmlType ||
				acscBraXml instanceof PSXfrShortCircuitXmlType) { // xfr or psxfr branch
			setAcscXfrFormInfo((XfrShortCircuitXmlType)acscBraXml, acscBra);
		}
	}

	private void setAcscLineFormInfo(LineShortCircuitXmlType braXml, AcscBranch acscBra) {
		double baseV = acscBra.getFromAclfBus().getBaseVoltage();
		AcscLineAdapter line = (AcscLineAdapter) acscBra.getAdapter(AcscLineAdapter.class);
		ZXmlType z0 = braXml.getZ0();
		if (z0 != null)
			line.setZ0(new Complex(z0.getRe(), z0.getIm()),	toZUnit.f(z0.getUnit()), baseV);
		YXmlType y0 = braXml.getY0Shunt();
		if (y0 != null)
			line.setHB0(0.5*y0.getIm(), toYUnit.f(y0.getUnit()), baseV);
	}

	// for SC, Xfr and PSXfr behave the same
	private void setAcscXfrFormInfo(XfrShortCircuitXmlType braXml, AcscBranch acscBra) {
		double baseV = acscBra.getFromAclfBus().getBaseVoltage() > acscBra
		.getToAclfBus().getBaseVoltage() ? acscBra.getFromAclfBus()
				.getBaseVoltage() : acscBra.getToAclfBus().getBaseVoltage();
				AcscXfrAdapter xfr = (AcscXfrAdapter) acscBra.getAdapter(AcscXfrAdapter.class);
				ZXmlType z0 = braXml.getZ0();
				if (z0 != null)
					xfr.setZ0(new Complex(z0.getRe(), z0.getIm()), toZUnit.f(z0.getUnit()), baseV);

				XformerConnectionXmlType connect = braXml.getFromSideConnection();
				if(connect != null){
					XfrConnectCode conCode=calXfrConnectCode(connect);
					acscBra.setXfrFromConnectCode(conCode);
					if(connect.getGrounding() != null){
						ZXmlType z = connect.getGrounding().getGroundingZ();
						if (z != null) 
							xfr.setFromConnectGroundZ(calXfrConnectCode(connect), new Complex(z.getRe(), z.getIm()),
									toZUnit.f(z.getUnit()));
					}
				}				

				connect = braXml.getToSideConnection();
				if(connect != null){
					XfrConnectCode conCode=calXfrConnectCode(connect);
					acscBra.setXfrToConnectCode(conCode);
					if(connect.getGrounding() != null){
						ZXmlType z = connect.getGrounding().getGroundingZ();
						if (z != null) 
							xfr.setFromConnectGroundZ(calXfrConnectCode(connect), new Complex(z.getRe(), z.getIm()),
									toZUnit.f(z.getUnit()));
					}
				}	
	}

	private XfrConnectCode calXfrConnectCode(XformerConnectionXmlType connect) {
		// connectCode : [Delta | Wye]
		// groundCode : [SolidGrounded | ZGrounded | Ungrounded ]
		if (connect.getXfrConnection() == XformrtConnectionEnumType.DELTA)
			return XfrConnectCode.DELTA;
		else {  // Wye connection
			if (connect.getGrounding().getGroundingConnection() == GroundingEnumType.SOLID_GROUNDED)
				return XfrConnectCode.WYE_SOLID_GROUNDED;
			else if (connect.getGrounding().getGroundingConnection() == GroundingEnumType.Z_GROUNDED)
				return XfrConnectCode.WYE_ZGROUNDED;
			else 
				return XfrConnectCode.WYE_UNGROUNDED;
		}
	}
}