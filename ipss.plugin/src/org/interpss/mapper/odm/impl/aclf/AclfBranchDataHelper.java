/*
 * @(#)AclfXfrDataHelper.java   
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
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.TransformerInfoXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YXmlType;
import org.interpss.mapper.odm.ODMXmlHelper;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.LineAdapter;
import com.interpss.core.aclf.adpter.PSXfrAdapter;
import com.interpss.core.aclf.adpter.XfrAdapter;

public class AclfBranchDataHelper {
	private AclfNetwork aclfNet = null;
	private AclfBranch aclfBra = null;
	
	public AclfBranchDataHelper(AclfNetwork aclfNet, AclfBranch aclfBra) {
		this.aclfNet = aclfNet;
		this.aclfBra = aclfBra;
	}
	
	public void setLineBranchData(LineBranchXmlType braLine) throws InterpssException {
		YXmlType fromShuntY = null, toShuntY = null;
		double baseKva = aclfNet.getBaseKva();

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
	
	public void setXfrBranchData(XfrBranchXmlType braXfr) throws InterpssException {
		YXmlType fromShuntY = null;
		double baseKva = aclfNet.getBaseKva();
		
		aclfBra.setBranchCode(AclfBranchCode.XFORMER);
		setXformerInfoData(braXfr);
		fromShuntY = braXfr.getMagnitizingY();

		if (fromShuntY != null) {
			Complex ypu = UnitType.yConversion(new Complex(fromShuntY.getRe(),	fromShuntY.getIm()),
					aclfBra.getFromAclfBus().getBaseVoltage(), baseKva,
					ODMXmlHelper.toUnit(fromShuntY.getUnit()), UnitType.PU);
			aclfBra.setFromShuntY(ypu);
		}
	}
	
	public void setPsXfrBranchData(PSXfrBranchXmlType braPsXfr) throws InterpssException {
		aclfBra.setBranchCode(AclfBranchCode.PS_XFORMER);

		setXfrBranchData(braPsXfr);
		
		PSXfrAdapter psXfr = aclfBra.toPSXfr();
		if(braPsXfr.getFromAngle() != null)
			psXfr.setFromAngle(braPsXfr.getFromAngle().getValue(), 
						ODMXmlHelper.toUnit(braPsXfr.getFromAngle().getUnit()));
		if(braPsXfr.getToAngle() != null)
			psXfr.setToAngle(braPsXfr.getToAngle().getValue(), 
						ODMXmlHelper.toUnit(braPsXfr.getToAngle().getUnit()));
	}

	private void setXformerInfoData(XfrBranchXmlType xfrBranch) {
		double baseKva = aclfNet.getBaseKva();

		double fromBaseV = aclfBra.getFromAclfBus().getBaseVoltage(), 
		       toBaseV = aclfBra.getToAclfBus().getBaseVoltage();
		// turn ratio is based on xfr rated voltage
		// voltage units should be same for both side 
		double fromRatedV = fromBaseV;
		double toRatedV = toBaseV;
		double zratio = 1.0;
		double tapratio = 1.0;
		
		TransformerInfoXmlType xfrData = xfrBranch.getXfrInfo();
		if (xfrData != null) {
			if (xfrData.getFromRatedVoltage() != null)
				fromRatedV = xfrData.getFromRatedVoltage().getValue();
			if (xfrData.getToRatedVoltage() != null)
				toRatedV = xfrData.getToRatedVoltage().getValue();

			if (xfrData != null &&
					!xfrData.isDataOnSystemBase() &&
					xfrData.getRatedPower() != null && 
					xfrData.getRatedPower().getValue() > 0.0) 
				zratio = xfrData.getRatedPower().getUnit() == ApparentPowerUnitType.KVA?
						baseKva / xfrData.getRatedPower().getValue() :
							0.001 * baseKva / xfrData.getRatedPower().getValue();

			if (!xfrData.isDataOnSystemBase())
				tapratio = (fromRatedV/fromBaseV) / (toRatedV/toBaseV) ;
		}
		
		double baseV = fromBaseV > toBaseV ? fromBaseV : toBaseV;
		XfrAdapter xfr = aclfBra.toXfr();
		xfr.setZ(new Complex(xfrBranch.getZ().getRe()*zratio, xfrBranch.getZ().getIm()*zratio),
				ODMXmlHelper.toUnit(xfrBranch.getZ().getUnit()), baseV);
		xfr.setFromTurnRatio(xfrBranch.getFromTurnRatio().getValue() == 0.0 ? 1.0 : 
				xfrBranch.getFromTurnRatio().getValue()*tapratio, UnitType.PU);
		xfr.setToTurnRatio(xfrBranch.getToTurnRatio().getValue() == 0.0 ? 1.0 : 
				xfrBranch.getToTurnRatio().getValue()/tapratio, UnitType.PU);
	}
}