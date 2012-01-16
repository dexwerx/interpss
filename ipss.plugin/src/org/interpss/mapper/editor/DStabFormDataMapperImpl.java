/*
 * @(#)DStabFormDataMapperImpl.java   
 *
 * Copyright (C) 2006 www.interpss.org
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
 * @Date 09/15/2006
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.editor;

import static com.interpss.common.util.IpssLogger.ipssLogger;
import static com.interpss.core.AcscFunction.Str2ScGroundCode;

import java.util.List;

import org.apache.commons.math.complex.Complex;
import org.interpss.db.BaseDataBean;
import org.interpss.dstab.device.ScriptDynamicBusDeviceHolder;
import org.interpss.editor.data.dstab.DStabBusData;
import org.interpss.editor.data.dstab.DStabExcData;
import org.interpss.editor.data.dstab.DStabGovData;
import org.interpss.editor.data.dstab.DStabMachData;
import org.interpss.editor.data.dstab.DStabPssData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.numeric.datatype.Unit;
import org.interpss.numeric.datatype.Unit.UnitType;

import com.interpss.DStabObjectFactory;
import com.interpss.common.datatype.Constants;
import com.interpss.common.datatype.ScriptLangEnum;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.mapper.AbstractMapping;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.datatype.ScGroundType;
import com.interpss.core.funcImpl.CoreUtilFunc;
import com.interpss.dstab.DStabBus;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.device.DynamicBusDeviceType;
import com.interpss.dstab.mach.DynamicMachine;
import com.interpss.dstab.mach.EConstMachine;
import com.interpss.dstab.mach.Eq1Ed1Machine;
import com.interpss.dstab.mach.Eq1Machine;
import com.interpss.dstab.mach.Machine;
import com.interpss.dstab.mach.MachineController;
import com.interpss.dstab.mach.MachineType;
import com.interpss.dstab.mach.RoundRotorMachine;
import com.interpss.dstab.mach.SalientPoleMachine;
import com.interpss.simu.util.SimuSpringAppCtxUtil;

/**
 * Map functions for BaseNetForm, BaseBusForm, BaseBranchForm to/from Network,
 * Bus, Branch simu objects
 */

public class DStabFormDataMapperImpl extends AbstractMapping<GFormContainer, DStabilityNetwork> {
	private IPSSMsgHub msg = null;

	public DStabFormDataMapperImpl(IPSSMsgHub msg) {
		this.msg = msg;
	}
	/**
	 * Map the GNetContainer object to a DStabilityNetwork object
	 * 
	 * @param editNet
	 *            the GFormContainer object
	 * @param msg
	 *            the SessionMsg object
	 * @return a DStabilityNetwork object
	 */
	@Override
	public DStabilityNetwork map2Model(GFormContainer editNet) {
		DStabilityNetwork dstabNet = DStabObjectFactory
				.createDStabilityNetwork();

		BaseFormDataMapperImpl.setBaseNetInfo((GNetForm) editNet.getGNetForm(),
				dstabNet);
		try {
			AclfFormDataMapperImpl.setAclfNetInfo(editNet, dstabNet, msg);
		} catch (InterpssException e) {
			msg.sendErrorMsg(e.toString());
		}

		dstabNet.setSaturatedMachineParameter(((GNetForm) editNet.getGNetForm()).getDStabNetData().isSaturatedMachData());

		List busList = editNet.getBusFormList();
		for (int i = 0; i < busList.size(); i++) {
			GBusForm busForm = (GBusForm) busList.get(i);
			if (busForm.getDStabBusData().isDBusScripting()) {
				setScriptDynamicBusCodeInfo(busForm.getDStabBusData(),
						dstabNet, busForm.getId(), msg);
				// setDBusScriptingInfo(busForm.getDStabBusData(), dstabNet,
				// busForm.getId(), msg);
			} else if (busForm.getDStabBusData().isMachineBus()) {
				setMachineInfo(busForm.getDStabBusData().getMachData(),
						dstabNet, busForm.getId(), msg);
			}
		}

		List branchList = editNet.getBranchFormList();
		for (int i = 0; i < branchList.size(); i++) {
			// For each AcscBranch xml object, parse for an AcscBranch form
			// object
			GBranchForm branchForm = (GBranchForm) branchList.get(i);
			// TODO: multiple branch situation
			AcscBranch branch = (AcscBranch) dstabNet.getBranch(branchForm
					.getFromId(), branchForm.getToId());
			AcscFormDataMapperImpl.setBranchInfo(branchForm, branch, dstabNet,
					msg);
		}

		return dstabNet;
	}

	private static void setScriptDynamicBusCodeInfo(DStabBusData busData,
			DStabilityNetwork dstabNet, String busId, IPSSMsgHub msg) {
		ipssLogger.info(
				"Set CML Dynamic Bus code info, busId: " + busId);
		ScriptDynamicBusDeviceHolder busDevice = new ScriptDynamicBusDeviceHolder();
		dstabNet.addScriptDynamicBusDevice(busDevice, busId);
		DStabBus dstabBus = dstabNet.getDStabBus(busId);
		dstabBus.getDynamicBusDeviceList().add(busDevice); // bind bus and machine
													// together
		busDevice.setDeviceType(DynamicBusDeviceType.SCRIPT_DYNAMIC_BUS_DEVICE);
		busDevice.setId(Constants.Token_DBusDeviceId + busId);
		busDevice.setName(Constants.Token_DBusDeviceId + busId);
		busDevice
				.setScriptLang(busData.getScriptLanguage() == BaseDataBean.ScriptLanguage_Java ? ScriptLangEnum.Java
						: ScriptLangEnum.Plugin);
		if (busData.getScriptLanguage() == BaseDataBean.ScriptLanguage_Java)
			busDevice.setScripts(busData.getScripts());
		else {
			busDevice.setPluginName(busData.getScriptPluginName());
			busDevice.setPluginDataXmlStr(busData.getScriptPluginXmlStr());
		}
	}

	/*
	 * replaced by CML DynamicBusDevice private static void
	 * setDBusScriptingInfo(DStabBusData busData, DStabilityNetwork dstabNet,
	 * String busId, IPSSMsgHub msg) { ipssLogger.info("Set
	 * DBusScripting info, busId: " + busId); ScriptingDBusDevice busDevice =
	 * new DefaultScriptingDBusDevice();
	 * dstabNet.addScriptingDBusDevice(busDevice, busId);
	 * busDevice.setDeviceType(DynamicBusDeviceType.SRIPTING_BUS_DEVICE);
	 * busDevice.setId(Constants.DBusDeviceIdToken+busId);
	 * busDevice.setName(Constants.DBusDeviceIdToken+busId);
	 * busDevice.setScripts(busData.getScripts()); }
	 */
	private static void setMachineInfo(DStabMachData machData,
			DStabilityNetwork dstabNet, String busId, IPSSMsgHub msg) {
		ipssLogger.info("Set Machine info, busId: " + busId);
		DStabBus dstabBus = dstabNet.getDStabBus(busId);
		Machine mach = null;
		if (machData.getType().equals(DStabMachData.MachType_InfiniteBus)) {
			Complex z1 = new Complex(0.0, 0.0);
			if (machData.getScMva3P() > 0.0 && machData.getX_R_3P() > 0.0)
				z1 = CoreUtilFunc.calUitilityZ1PU(machData.getScMva3P() * 1000,
						machData.getX_R_3P(), dstabNet.getBaseKva());
			Complex z0 = new Complex(0.0, 0.0);
			if (machData.getScMva1P() > 0 && machData.getX_R_1P() > 0.0)
				z0 = CoreUtilFunc.calUitilityZ0PU(machData.getScMva1P() * 1000,
						machData.getX_R_1P(), dstabNet.getBaseKva(), z1);
			mach = DStabObjectFactory.createInfiniteMachine(
					Constants.Token_MachId + busId, machData.getName(), z1, z0,
					dstabNet, busId);
		} else {
			mach = DStabObjectFactory.createMachine(Constants.Token_MachId
					+ busId, machData.getName(),
					getMachType(machData.getType()), dstabNet, busId);
			mach.setRating(machData.getRating(), UnitType.mVA, dstabNet.getBaseKva());
			mach.setRatedVoltage(machData.getRatedVolt());
			mach.setMultiFactors(dstabBus);
			mach.setH(machData.getInertia());
			mach.setD(machData.getDamping());
			mach.setPoles(machData.getPoles());
			mach.setX0(machData.getX0());
			mach.setX2(machData.getX2());
			ScGroundType gtype = new ScGroundType();
			gtype.setZ(new Complex(machData.getGround().getR(), machData
					.getGround().getX()), Unit.toUnit(machData.getGround()
					.getUnit()), machData.getRatedVolt(),
					machData.getRating() * 1000.0);
			mach.getGrounding().setCode(Str2ScGroundCode.f(machData.getGround().getCode()));
			mach.getGrounding().setZ(
					new Complex(machData.getGround().getR(), machData
							.getGround().getX()),
							Unit.toUnit(machData.getGround().getUnit()),
					machData.getRatedVolt(), machData.getRating() * 1000.0);
			mach.getGrounding().setUnit(UnitType.PU);

			if (mach.getMachType() == MachineType.ECONSTANT) {
				EConstMachine m = (EConstMachine) mach;
				m.setXd1(machData.getXd1());
			} else {
				DynamicMachine m = (DynamicMachine) mach;
				m.setRa(machData.getRa());
				m.setXl(machData.getXl());
				m.setXd(machData.getXd());
				m.setXq(machData.getXq());
				m.setSliner(machData.getSliner());
				m.setSe100(machData.getS100());
				m.setSe120(machData.getS120());

				Eq1Machine mEq1 = (Eq1Machine) mach;
				mEq1.setXd1(machData.getXd1());
				mEq1.setTd01(machData.getTd01());

				if (mach.getMachType() == MachineType.EQ1_ED1_MODEL) {
					Eq1Ed1Machine mEq1Ed1 = (Eq1Ed1Machine) mach;
					mEq1Ed1.setXq1(machData.getXq1());
					mEq1Ed1.setTq01(machData.getTq01());
				} else if (mach.getMachType() == MachineType.EQ11_SALIENT_POLE
						|| mach.getMachType() == MachineType.EQ11_ED11_ROUND_ROTOR) {
					SalientPoleMachine mPole = (SalientPoleMachine) mach;
					mPole.setXd11(machData.getXd11());
					mPole.setXq11(machData.getXq11());
					mPole.setTd011(machData.getTd011());
					mPole.setTq011(machData.getTq011());
					if (mach.getMachType() == MachineType.EQ11_ED11_ROUND_ROTOR) {
						RoundRotorMachine mRotor = (RoundRotorMachine) mach;
						mRotor.setXq1(machData.getXq1());
						mRotor.setTq01(machData.getTq01());
					}
				}
			}

			if (machData.getHasExc()) {
				setExciterInfo(machData, mach, msg);
			}

			if (machData.getHasGov()) {
				setGovernorInfo(machData.getGovData(), mach, msg);
			}
		}
		dstabBus.getDynamicBusDeviceList().add(mach); // bind bus and machine together

		ipssLogger.fine(
				"Machine info set to: " + machData.toString());
	}

	private static void setExciterInfo(DStabMachData machData, Machine mach,
			IPSSMsgHub msg) {
		ipssLogger
				.info("Set Exciter info, machid: " + mach.getId());
		DStabExcData excData = machData.getExcData();
		MachineController controller = SimuSpringAppCtxUtil
				.getExciter(excData.getTypeName());
		if (controller != null) {
			controller.setData(excData.getDataXmlStr(), controller
					.getDataClass());
			controller.setScripts(machData.getExcData().getScripts());
			mach.getControllerList().add(controller);
			ipssLogger.info(
					"Exciter info set to: " + excData.getDataXmlStr());
			if (machData.getHasPss()) {
				setStabilizerInfo(machData.getPssData(), mach, msg);
			}
		} else {
			ipssLogger.warning(
					"Exciter not found, machid: " + mach.getId());
			msg.sendWarnMsg("Exciter not found, machid: " + mach.getId());
		}
	}

	private static void setGovernorInfo(DStabGovData govData, Machine mach,
			IPSSMsgHub msg) {
		ipssLogger.info(
				"Set Governor info, machid: " + mach.getId());
		MachineController controller = SimuSpringAppCtxUtil
				.getGovernor(govData.getTypeName());
		if (controller != null) {
			controller.setData(govData.getDataXmlStr(), controller
					.getDataClass());
			controller.setScripts(govData.getScripts());
			ipssLogger.info(
					"Governor info set to: " + govData.getDataXmlStr());
			mach.getControllerList().add(controller);
		} else {
			ipssLogger.warning(
					"Governor not found, machid: " + mach.getId());
			msg.sendWarnMsg("Governor not found, machid: " + mach.getId());
		}
	}

	private static void setStabilizerInfo(DStabPssData pssData, Machine mach,
			IPSSMsgHub msg) {
		ipssLogger.info(
				"Set Stabilizer info, machid: " + mach.getId());
		MachineController controller = SimuSpringAppCtxUtil
				.getStabilizer(pssData.getTypeName());
		if (controller != null) {
			controller.setData(pssData.getDataXmlStr(), controller
					.getDataClass());
			controller.setScripts(pssData.getScripts());
			ipssLogger.info(
					"Stabilizer info set to: " + pssData.getDataXmlStr());
			mach.getControllerList().add(controller);
		} else {
			ipssLogger.warning(
					"Stabilizer not found, machid: " + mach.getId());
			msg.sendWarnMsg("Stabilizer not found, machid: " + mach.getId());
		}
	}

	private static MachineType getMachType(String mtype) {
		if (mtype.equals(DStabMachData.MachType_EConst))
			return MachineType.ECONSTANT;
		else if (mtype.equals(DStabMachData.MachType_Eq1))
			return MachineType.EQ1_MODEL;
		else if (mtype.equals(DStabMachData.MachType_Eq1Ed1))
			return MachineType.EQ1_ED1_MODEL;
		else if (mtype.equals(DStabMachData.MachType_SalientPole))
			return MachineType.EQ11_SALIENT_POLE;
		else if (mtype.equals(DStabMachData.MachType_RoundRotor))
			return MachineType.EQ11_ED11_ROUND_ROTOR;
		else {
			ipssLogger
					.severe("Wrong Machine Type String: " + mtype);
			throw new InterpssRuntimeException("Wrong Machine Type String: " + mtype);
		}
	}
}