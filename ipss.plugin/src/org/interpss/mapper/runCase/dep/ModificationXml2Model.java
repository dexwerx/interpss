/*
 * @(#)XmlNetParamModifier.java   
 *
 * Copyright (C) 2006-2007 www.interpss.org
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
 * @Date 09/15/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.runCase.dep;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.datatype.ComplexFunc;
import org.interpss.xml.IpssXmlHelper;
import org.interpss.xml.schema.AclfLoadCodeChangeXmlType;
import org.interpss.xml.schema.AclfLoadCodeDataType;
import org.interpss.xml.schema.BranchChangeRecXmlType;
import org.interpss.xml.schema.BusChangeRecXmlType;
import org.interpss.xml.schema.ComplexValueChangeXmlType;
import org.interpss.xml.schema.ComplexXmlType;
import org.interpss.xml.schema.DoubleValueChangeXmlType;
import org.interpss.xml.schema.ModificationXmlType;
import org.interpss.xml.schema.UnitDataType;
import org.interpss.xml.schema.ValueChangeActionDataType;

import com.interpss.SimuObjectFactory;
import com.interpss.common.datatype.UnitHelper;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.net.Network;
import com.interpss.simu.multicase.modify.BranchModification;
import com.interpss.simu.multicase.modify.BusModification;
import com.interpss.simu.multicase.modify.Modification;

public class ModificationXml2Model {
	
	private static enum ComplexValueType {
		Power, Z
	};
 
	private static enum ValueType {
		Voltage
	};

	/**
	 * Apply the modification record to the network object
	 * 
	 * @param net a Network/AclfNetwork/AclfAdjNetwork/... object to be modified
	 * @param mod the modification record
	 */
	public static boolean applyModification(Modification modModel, Network net, ModificationXmlType mod, IPSSMsgHub msg) {
		IpssLogger.getLogger().info("Apply Network modification");
		
		if (mod.getBusChangeRecList() != null) {
			for (BusChangeRecXmlType busRec : mod.getBusChangeRecList().getBusChangeRec()) {
				if (!applyBusChange(busRec, modModel, net, msg))
					return false;
			}
		}

		if (mod.getBranchChangeRecList() != null) {
			for (BranchChangeRecXmlType braRec : mod.getBranchChangeRecList().getBranchChangeRec()) {
				if (!applyBranchChange(braRec, modModel, net, msg))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Apply the bus change record to the net object 
	 * 
	 * @param busRec
	 * @param net
	 * @param msg
	 * @return
	 */
	public static boolean applyBusChange(BusChangeRecXmlType busRec, Modification modModel, Network net, IPSSMsgHub msg) {
		try {
			BusModification busMod = SimuObjectFactory.createBusModification(busRec.getBusId(), net);
			busMod.setStatus(true);
			busMod.setName(busRec.getRecName());
			busMod.setDesc(busRec.getRecDesc());
			
			busMod.setOutService(busRec.isOffLine());
			
		} catch (InterpssException e ) {
			return false;
		}
		return true;
	}
	
	/**
	 * Apply the branch change record to the net object 
	 * 
	 * @param braRec
	 * @param net
	 * @param msg
	 * @return
	 */
	public static boolean applyBranchChange(BranchChangeRecXmlType braRec, Modification modModel, Network net, IPSSMsgHub msg) {
		try {
			BranchModification braMod = SimuObjectFactory.createBranchModification(braRec.getFromBusId(), braRec.getToBusId(), braRec.getCircuitNumber(), net);
			braMod.setStatus(true);
			braMod.setName(braRec.getRecName());
			braMod.setDesc(braRec.getRecDesc());
			
			braMod.setOutService(braRec.isOffLine());
			
		} catch (InterpssException e ) {
			return false;
		}
		return true;
	}

	
	
	private static Complex applyPowerChangeRec(Complex original,
			ComplexValueChangeXmlType changeRec, double baseKva) {
		return applyComplexValueChangeRec(original, changeRec, ComplexValueType.Power,
				baseKva, 1.0);
	}
	
	private static boolean modifyBusGen(AclfBus bus, BusChangeRecXmlType busRec, double baseKva, IPSSMsgHub msg) {
		// swing bus voltage modification
		if (busRec.getAclfBusChangeData().getGenChangeData().getSwingVoltageChange() != null) {
			if (bus.isSwing()) {
	  			final SwingBusAdapter gen = bus.toSwingBus();
				double x = applyValueChangeRec(gen.getVoltMag(UnitType.PU),
						busRec.getAclfBusChangeData().getGenChangeData().getSwingVoltageChange(),
						ValueType.Voltage, bus.getBaseVoltage());
				gen.setVoltMag(x, UnitType.PU);
			}
			else {
				msg.sendErrorMsg("Error: try to set swing bus voltage of a non-swing bus, id: " + bus.getId());
				return false;
			}
		}
		return true;
	}
	
	private static void modifyBusLoad(AclfBus bus, BusChangeRecXmlType busRec, double baseKva) {
		// modify load value
		if (busRec.getAclfBusChangeData().getLoadChangeData().getValueChange() != null) {
			Complex x = applyPowerChangeRec(
					bus.getLoad(),
					busRec.getAclfBusChangeData().getLoadChangeData().getValueChange(),
					baseKva);
			bus.setLoadP(x.getReal());
			bus.setLoadQ(x.getImaginary());
		}

		// modify load code
		if (busRec.getAclfBusChangeData().getLoadChangeData().getCodeChange() != null) {
			AclfLoadCodeChangeXmlType codeChange = busRec.getAclfBusChangeData().getLoadChangeData().getCodeChange();
			bus.setLoadCode(codeChange.getLoadCode() == 
				AclfLoadCodeDataType.CONST_P ? AclfLoadCode.CONST_P
						: (codeChange.getLoadCode() == AclfLoadCodeDataType.CONST_I ? 
							AclfLoadCode.CONST_I : (codeChange.getLoadCode() == 
								AclfLoadCodeDataType.CONST_Z ? AclfLoadCode.CONST_Z
									: (codeChange.getLoadCode() == 
										AclfLoadCodeDataType.EXPONENTIAL ? AclfLoadCode.EXPONENTIAL
											: AclfLoadCode.NON_LOAD))));
			if (bus.getLoadCode() == AclfLoadCode.EXPONENTIAL) {
				bus.setExpLoadP(codeChange.getExpLoadP());
				bus.setExpLoadQ(codeChange.getExpLoadQ());
			}
		}		
	}

	private static Complex applyComplexValueChangeRec(Complex original,
			ComplexValueChangeXmlType changeRec, ComplexValueType ptype,
			double baseKva, double busBaseVolt) {
		if (changeRec.getChangeAction() == ValueChangeActionDataType.ADD
				|| changeRec.getChangeAction() == ValueChangeActionDataType.SET) {
			// for add/set, use value and unit (PU or power unit)
			ComplexXmlType c = changeRec.getValue();
			double re = c.getRe(), im = c.getIm();
			// the original is in PU
			if (changeRec.getUnit() != UnitDataType.PU) {
				// convert set/add value to PU
				byte unit = IpssXmlHelper.mapXmlUnitType2IpssUnitType(changeRec
						.getUnit());
				if (ptype == ComplexValueType.Power) {
					re = UnitHelper.pConversion(re, baseKva, unit, UnitType.PU);
					im = UnitHelper.pConversion(im, baseKva, unit, UnitType.PU);
				} else if (ptype == ComplexValueType.Z) {
					Complex z = UnitHelper.zConversion(new Complex(re, im),
							busBaseVolt, baseKva, unit, UnitType.PU);
					re = z.getReal();
					im = z.getImaginary();
				}
			}
			if (changeRec.getChangeAction() == ValueChangeActionDataType.ADD) {
				re += original.getReal();
				im += original.getImaginary();
			}
			Complex cReturn = new Complex(re, im);
			IpssLogger.getLogger().info( "Gen/Load add/set to: " + ComplexFunc.toString(cReturn));
			return cReturn;
		} else if (changeRec.getChangeAction() == ValueChangeActionDataType.INCREASE
				|| changeRec.getChangeAction() == ValueChangeActionDataType.DECREASE) {
			// for increase/decrease, use percent and unit (PU or percent)
			double factor = changeRec.getPercent();
			if (changeRec.getUnit() == UnitDataType.PERCENT)
				factor *= 0.01;
			if (changeRec.getChangeAction() == ValueChangeActionDataType.DECREASE)
				factor = -factor;
			Complex cReturn = new Complex(original.getReal() * (1.0 + factor), original
					.getImaginary()
					* (1.0 + factor));
			IpssLogger.getLogger().info( "Gen/Load add/set to: " + ComplexFunc.toString(cReturn));
			return cReturn;
		}

		IpssLogger.getLogger().warning("Wrong ChangeAction, changeRec: " + changeRec.toString());
		return original;
	}

	private static double applyValueChangeRec(double original,
			DoubleValueChangeXmlType changeRec, ValueType ptype, double busBaseVolt) {
		if (changeRec.getChangeAction() == ValueChangeActionDataType.ADD
				|| changeRec.getChangeAction() ==ValueChangeActionDataType.SET) {
			// for add/set, use value and unit (PU or power unit)
			double c = changeRec.getValue();
			if (changeRec.getUnit() != UnitDataType.PU) {
				// convert set/add value to PU
				byte unit = IpssXmlHelper.mapXmlUnitType2IpssUnitType(changeRec
						.getUnit());
				if (ptype == ValueType.Voltage) {
					c = UnitHelper.vConversion(c, busBaseVolt, unit, UnitType.PU);
				} 
			}
			if (changeRec.getChangeAction() == ValueChangeActionDataType.ADD) {
				c += original;
			}
			return c;
		} else if (changeRec.getChangeAction() == ValueChangeActionDataType.INCREASE
				|| changeRec.getChangeAction() == ValueChangeActionDataType.DECREASE) {
			// for increase/decrease, use percent and unit (PU or percent)
			double factor = changeRec.getPercent();
			if (changeRec.getUnit() == UnitDataType.PERCENT)
				factor *= 0.01;
			if (changeRec.getChangeAction() == ValueChangeActionDataType.DECREASE)
				factor = -factor;
			return original * (1.0 + factor);
		}
		IpssLogger.getLogger().warning("Wrong ChangeAction, changeRec: " + changeRec.toString());
		return original;
	}
}