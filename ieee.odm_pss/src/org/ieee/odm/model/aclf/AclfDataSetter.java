 /*
  * @(#)AclfDataSetter.java   
  *
  * Copyright (C) 2009 www.interpss.org
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
  * @Date 04/11/2009
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.model.aclf;

import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.ApparentPowerXmlType;
import org.ieee.odm.schema.BaseRecordXmlType;
import org.ieee.odm.schema.BranchRatingLimitXmlType;
import org.ieee.odm.schema.CurrentUnitType;
import org.ieee.odm.schema.CurrentXmlType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LengthUnitType;
import org.ieee.odm.schema.LineBranchInfoXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenDataXmlType;
import org.ieee.odm.schema.LoadflowLoadDataXmlType;
import org.ieee.odm.schema.MvaRatingXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.TransformerInfoXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.VoltageXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;
import org.ieee.odm.schema.ZXmlType;
import org.ieee.odm.schema.BaseRecordXmlType.OwnerList.Owner;


public class AclfDataSetter extends BaseDataSetter {
	/**
	 * set load data, first create an equivLoad object, then set value(code, p, q, unit) to the created EquivLoadData object
	 * 
	 * @param busData
	 * @param code
	 * @param p
	 * @param q
	 * @param unit
	 */
	public static void setLoadData(LoadflowBusXmlType bus, 
			LFLoadCodeEnumType code, 
			double p, double q, ApparentPowerUnitType unit) {
		LoadflowBusXmlType.LoadData loadData = getFactory().createLoadflowBusXmlTypeLoadData();
		bus.setLoadData(loadData);
		LoadflowLoadDataXmlType equivLoad = getFactory().createLoadflowLoadDataXmlType();
		loadData.setEquivLoad(equivLoad);
    	bus.getLoadData().getEquivLoad().setCode(code);
    	equivLoad.setConstPLoad(createPowerValue(p, q, unit));
	}
	
	/**
	 * set EquivGen object, then set value(code, p, q, unit) to the created EquivGenData object
	 * 
	 * @param busData
	 * @param code
	 * @param p
	 * @param q
	 * @param pUnit
	 */
	public static void setGenData(LoadflowBusXmlType bus, LFGenCodeEnumType code, 
			double v, VoltageUnitType vUnit,
			double ang, AngleUnitType angUnit,
			double p, double q, ApparentPowerUnitType pUnit) {
		setGenData(bus, code, v, vUnit, ang, angUnit);
   		LoadflowGenDataXmlType equivGen = bus.getGenData().getEquivGen();
		equivGen.setPower(createPowerValue(p, q, pUnit));
   		if (code == LFGenCodeEnumType.PV) {
   			equivGen.setDesiredVoltage(createVoltageValue(v, vUnit));
   		}
   		else if (code == LFGenCodeEnumType.SWING) {
   			equivGen.setDesiredVoltage(createVoltageValue(v, vUnit));
   			equivGen.setDesiredAngle(createAngleValue(ang, angUnit));
   		}
	}	

	public static void setGenData(LoadflowBusXmlType bus, LFGenCodeEnumType code, 
			double v, VoltageUnitType vUnit,
			double ang, AngleUnitType angUnit) {
   		bus.setGenData(getFactory().createLoadflowBusXmlTypeGenData());
   		LoadflowGenDataXmlType equivGen = getFactory().createLoadflowGenDataXmlType();
   		bus.getGenData().setEquivGen(equivGen);
   		equivGen.setCode(code);
	}	

	/**
	 * Set bus shunt Y 
	 * 
	 * @param bus
	 * @param re
	 * @param im
	 * @param unit
	 */
	public static void setBusShuntY(LoadflowBusXmlType bus, double re, double im, YUnitType unit) {
		bus.setShuntY(getFactory().createYXmlType());
		bus.getShuntY().setRe(re);
		bus.getShuntY().setIm(im);
		bus.getShuntY().setUnit(unit);
	}	

	public static void setBusShuntVar(LoadflowBusXmlType bus, double var, YUnitType unit) {
		setBusShuntY(bus, 0.0, var, unit);
	}	

	public static void addBusShuntY(LoadflowBusXmlType bus, double re, double im, YUnitType unit) {
		if (bus.getShuntY() == null)
			bus.setShuntY(getFactory().createYXmlType());
		bus.getShuntY().setRe(re + bus.getShuntY().getRe());
		bus.getShuntY().setIm(im + bus.getShuntY().getIm());
		bus.getShuntY().setUnit(unit);
	}	

	public static void addBusShuntVar(LoadflowBusXmlType bus, double var, YUnitType unit) {
		addBusShuntY(bus, 0.0, var, unit);
	}	

	/*
	 * add a LineData object to the branchData object, then set value(r, x, zUnit, g, b, yUnit) 
	 * to the created LineData object
	 * 
	 * @param branchData
	 * @param r
	 * @param x
	 * @param zUnit
	 * @param g
	 * @param b
	 * @param bUnit
	 */
	public static void setLineData(LineBranchXmlType branch, 
			             double r, double x, ZUnitType zUnit, 
			             double g, double b, YUnitType yUnit) {
		ZXmlType z = createZValue(r, x, zUnit);
		branch.setZ(z);
		if (g != 0.0 || b != 0.0) {
			branch.setTotalShuntY(createYValue(g, b, yUnit));
		}
	}
	
	/**
	 * 
	 * @param lineInfo
	 * @param length
	 * @param unit
	 */
	public static void setLineLength(LineBranchInfoXmlType lineInfo, 
            double length, LengthUnitType unit) {
		lineInfo.setLength(getFactory().createLengthXmlType());
		lineInfo.getLength().setValue(length);
		lineInfo.getLength().setUnit(unit);
	}

	/**
	 * add a XformerData object to the branchData object, then set value(r, x, zUnit, gFrom, bFrom, gTo, bTo, yUnit) to the created XfomerData object
	 * 
	 * @param branchData
	 * @param r
	 * @param x
	 * @param zUnit
	 * @param gFrom
	 * @param bFrom
	 * @param gTo
	 * @param bTo
	 * @param bUnit
	 */
	public static void createXformerData(XfrBranchXmlType branch, 
            double r, double x, ZUnitType zUnit,
            double fromTurnRatio, double toTurnRatio) {
		branch.setXfrInfo(getFactory().createTransformerInfoXmlType());
		branch.getXfrInfo().setDataOnSystemBase(true);
		setXformerData(branch,
				r, x, zUnit, fromTurnRatio, toTurnRatio,
				0.0, 0.0, YUnitType.PU);
	}

	public static void createXformerData(XfrBranchXmlType branch, 
			             double r, double x, ZUnitType zUnit,
			             double fromTurnRaio, double toTurnRatio,
			             double gMag, double bMag, YUnitType yUnit) {
		branch.setXfrInfo(getFactory().createTransformerInfoXmlType());
		branch.getXfrInfo().setDataOnSystemBase(true);
		setXformerData(branch,
				r, x, zUnit, fromTurnRaio, toTurnRatio,
				gMag, bMag, yUnit);
	}
	
	private static void setXformerData(XfrBranchXmlType xfr,
			double r, double x, ZUnitType zUnit, 
			double fromTurnRatio, double toTurnRatio,
			double gMag, double bMag, YUnitType yUnit) {
		ZXmlType z = createZValue(r, x, zUnit);
		xfr.setZ(z);
		xfr.setFromTurnRatio(createTurnRatioPU(fromTurnRatio));
		xfr.setToTurnRatio(createTurnRatioPU(toTurnRatio));
		if (gMag != 0.0 || bMag != 0.0) {
			xfr.setMagnitizingY(createYValue(gMag, bMag, yUnit));
		}	
	}	
	
	/**
	 * add a PhaseShiftXfrData object to the branchData object, then set value(r, x, zUnit, gFrom, bFrom, gTo, bTo, yUnit) 
	 * to the created PhaseShiftXfrData object
	 * 
	 * @param branchData
	 * @param r
	 * @param x
	 * @param zUnit
	 * @param gFrom
	 * @param bFrom
	 * @param bUnit
	 */
	public static void createPhaseShiftXfrData(PSXfrBranchXmlType branch,
			double r, double x, ZUnitType zUnit,
			double fromTap, double toTap, double fromAng, double toAng, AngleUnitType angUnit,
			double gFrom, double bFrom, YUnitType yUnit) {
		setXformerData(branch,
				r, x, zUnit, fromTap, toTap, gFrom, bFrom, yUnit);
		if (fromAng != 0.0) {
			branch.setFromAngle(createAngleValue(fromAng, angUnit));
		}
		if (toAng != 0.0) {
			branch.setToAngle(createAngleValue(toAng, angUnit));
		}
	}

	/**
	 * add a PhaseShiftXfrData object to the branchData object, then set value(r, x, zUnit, gFrom, bFrom, gTo, bTo, yUnit) 
	 * to the created PhaseShiftXfrData object
	 * 
	 * @param branchData
	 * @param r
	 * @param x
	 * @param zUnit
	 * @param gFrom
	 * @param bFrom
	 * @param bUnit
	 */
	public static void createPhaseShiftXfrData(PSXfrBranchXmlType branchData,
			double r, double x, ZUnitType zUnit,
			double fromTap, double toTap,
			double fromAng, double toAng, AngleUnitType angUnit) {
		createPhaseShiftXfrData(branchData, r, x, zUnit,
				fromTap, toTap, fromAng, toAng, angUnit,
				0.0, 0.0, YUnitType.PU);
	}	
	/**
	 * set transformer rating data
	 *  
	 * @param branchData
	 * @param fromRatedV
	 * @param toRatedV
	 * @param vUnit
	 * @param normialMva
	 * @param pUnit
	 */
	public static void setXfrRatingData(XfrBranchXmlType branch, 
			double fromRatedV, double toRatedV, VoltageUnitType vUnit,
			double normialMva, ApparentPowerUnitType pUnit) {
		if (branch.getXfrInfo() == null) {
			branch.setXfrInfo(getFactory().createTransformerInfoXmlType());
		}
		TransformerInfoXmlType xfrInfo = branch.getXfrInfo();
		VoltageXmlType fromRatedVolt = getFactory().createVoltageXmlType();
		xfrInfo.setFromRatedVoltage(fromRatedVolt);
		fromRatedVolt.setValue(fromRatedV);
		fromRatedVolt.setUnit(vUnit);
		VoltageXmlType toRatedVolt = getFactory().createVoltageXmlType();
		xfrInfo.setToRatedVoltage(toRatedVolt);
		toRatedVolt.setValue(toRatedV);
		toRatedVolt.setUnit(vUnit);
   		if (normialMva != 0.0) {
   			ApparentPowerXmlType ratedMva = getFactory().createApparentPowerXmlType();
   			xfrInfo.setRatedPower(ratedMva);
   			ratedMva.setValue(normialMva);
   			ratedMva.setUnit(pUnit);		
   		}
	}

	/**
	 * set transformer rating data
	 * 
	 * @param xfrData
	 * @param fromRatedV
	 * @param toRatedV
	 * @param vUnit
	 */
	public static void setXfrRatingData(XfrBranchXmlType xfrData, 
			double fromRatedV, double toRatedV, VoltageUnitType vUnit) {
		setXfrRatingData(xfrData, fromRatedV, toRatedV, vUnit, 0.0, null);
	}	
	
	/**
	 * add a RatingLimitData object to the branchData object, then set value(mvarLimit1, mvarLimit2, mvarLimit3, mvarUnit, curLimit, curUnit) 
	 * to the created RatingLimitData object
	 * 
	 * @param branchLimit
	 * @param mvar1
	 * @param mvar2
	 * @param mvar3
	 * @param mvarUnit
	 * @param current
	 * @param curUnit
	 */
	public static void setBranchRatingLimitData(BranchRatingLimitXmlType branchLimit, 
				double mvar1, double mvar2, double mvar3, ApparentPowerUnitType mvarUnit,
				double current, CurrentUnitType curUnit) {
    	if (mvar1 != 0.0 || mvar2 != 0.0 || mvar3 != 0.0 || current != 0.0) {
        	if (mvar1 != 0.0 || mvar2 != 0.0 || mvar3 != 0.0) {
        		MvaRatingXmlType mvaRating = getFactory().createMvaRatingXmlType();
        		branchLimit.setMva(mvaRating);
        		mvaRating.setRating1(mvar1);
        		mvaRating.setRating2(mvar2);
        		mvaRating.setRating3(mvar3);
        		mvaRating.setUnit(mvarUnit);
        	}

        	if (current != 0.0) {
        		CurrentXmlType limit = getFactory().createCurrentXmlType();
        		branchLimit.setCurrent(limit);
        		limit.setValue(current);
        		limit.setUnit(curUnit);
        	}
    	}
	}

	/**
	 * add a RatingLimitData object to the branchData object, then set value(mvarLimit1, mvarLimit2, mvarLimit3, mvarUnit) 
	 * to the created RatingLimitData object
	 * 
	 * @param branchData
	 * @param mvar1
	 * @param mvar2
	 * @param mvar3
	 * @param mvarUnit
	 */
	public static void setBranchRatingLimitData(BranchRatingLimitXmlType branchLimit, 
				double mvar1, double mvar2, double mvar3, ApparentPowerUnitType mvarUnit) {
		setBranchRatingLimitData(branchLimit, mvar1, mvar2, mvar3, mvarUnit, 0.0, null);
	}

	/**
	 * add a RatingLimitData object to the branchData object, then set value(mvarLimit1, mvarLimit2, mvarLimit3, mvarUnit) 
	 * to the created RatingLimitData object
	 * 
	 * @param branchData
	 * @param mvar1
	 * @param mvar2
	 * @param mvar3
	 * @param mvarUnit
	 */
	public static void setBranchRatingLimitData(BranchRatingLimitXmlType branchLimit, 
			double[] mvarAry, ApparentPowerUnitType mvarUnit) {
		setBranchRatingLimitData(branchLimit, mvarAry[0], mvarAry[1], mvarAry[2], mvarUnit);
//		MvaRatingXmlType mvaRating = branchLimit.getMva();
//		for (double x : mvarAry)
//			if (x > 0.0) {
//				mvaRating.getRatingAry().add(x);
//			}
	}
	/**
	 * add a RatingLimitData object to the branchData object, then set value(curLimit, curUnit) 
	 * to the created RatingLimitData object
	 * 
	 * @param branchData
	 * @param current
	 * @param curUnit
	 */
	public static void setBranchRatingLimitData(BranchRatingLimitXmlType branchLimit, 
				double current, CurrentUnitType curUnit) {
		setBranchRatingLimitData(branchLimit, 0.0, 0.0, 0.0, null, current, curUnit);
	}
}
