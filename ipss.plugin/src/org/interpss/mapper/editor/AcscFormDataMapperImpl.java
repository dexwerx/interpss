/*
 * @(#)AcscFormDataMapperImpl.java   
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

import org.apache.commons.math.complex.Complex;
import org.interpss.editor.data.acsc.AcscBranchData;
import org.interpss.editor.data.acsc.AcscBusData;
import org.interpss.editor.data.acsc.AcscNetData;
import org.interpss.editor.data.common.XfrConnectData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.ui.util.CoreScriptUtilFunc;
import org.interpss.editor.ui.util.ScriptJavacUtilFunc;
import org.interpss.numeric.NumericConstant;
import org.interpss.numeric.datatype.Unit;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.spring.UISpringFactory;
import org.interpss.util.MemoryJavaCompiler;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.mapper.AbstractMapping;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.rec.BaseDataBean;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.acsc.AcscBranch;
import com.interpss.core.acsc.AcscBus;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.BusGroundCode;
import com.interpss.core.acsc.BusScCode;
import com.interpss.core.acsc.SequenceCode;
import com.interpss.core.acsc.XfrConnectCode;
import com.interpss.core.acsc.adpter.AcscLineAdapter;
import com.interpss.core.acsc.adpter.AcscXfrAdapter;
import com.interpss.core.datatype.ScGroundEnum;
import com.interpss.core.datatype.ScGroundType;
import com.interpss.core.util.CoreUtilFunc;

/**
 * Map functions for BaseNetForm, BaseBusForm, BaseBranchForm to/from Network,
 * Bus, Branch simu objects
 */

public class AcscFormDataMapperImpl extends AbstractMapping<GFormContainer, AcscNetwork> {
	private IPSSMsgHub msg = null;

	public AcscFormDataMapperImpl(IPSSMsgHub msg) {
		this.msg = msg;
	}
	
	/**
	 * Map the GNetContainer object to a SimpleFaultNetwork object
	 * 
	 * @param editNet
	 *            the GFormContainer object
	 * @param msg
	 *            the SessionMsg object
	 * @return a SimpleFaultNetwork object
	 */
	@Override
	public AcscNetwork map2Model(GFormContainer editNet) {
		AcscNetwork acscNet = CoreObjectFactory
				.createAcscNetwork();

		BaseFormDataMapperImpl.setBaseNetInfo((GNetForm) editNet.getGNetForm(),
				acscNet);

		AcscNetData data = ((GNetForm) editNet.getGNetForm()).getAcscNetData();
		if (data.isHasAclfData()) {
			// all buese/branches are added in the following setup process
			acscNet.setLfDataLoaded(true);
			try {
				AclfFormDataMapperImpl.setAclfNetInfo(editNet, acscNet, msg);
			} catch (InterpssException e) {
				msg.sendErrorMsg(e.toString());
			}
		} else {
			acscNet.setLfDataLoaded(false);
		}

		for (Object obj : editNet.getBusFormList()) {
			// For each AcscBus xml object, parse for an AcscBus form object
			GBusForm busForm = (GBusForm) obj;
			if (!acscNet.isLfDataLoaded())
				setAddBusForm2Net(busForm, acscNet);
			else {
				AcscBus bus = (AcscBus) acscNet.getBus(busForm.getId());
				setBusInfo(busForm, bus, acscNet);
			}
			// System.out.println("\nBus info, #:" + (i+1));
			// System.out.println(busForm.toString());
		}

		for (Object obj : editNet.getBranchFormList()) {
			// For each AcscBranch xml object, parse for an AcscBranch form
			// object
			GBranchForm branchForm = (GBranchForm) obj;
			if (!acscNet.isLfDataLoaded())
				setAddBranchForm2Net(branchForm, acscNet, msg);
			else {
				// TODO: multiple branch situation
				AcscBranch branch = (AcscBranch) acscNet.getBranch(branchForm
						.getFromId(), branchForm.getToId());
				setBranchInfo(branchForm, branch, acscNet, msg);
			}
			// System.out.println("\nBranch info, #:" + (i+1));
			// System.out.println(branchForm.toString());
		}

		acscNet.setScDataLoaded(true);
		// if load flow info loaded, the ckeckData will also perform checking
		// for LF.
		/*
		 * the checkData() need to be called at high level if
		 * (!acscNet.checkData(msg)) { msg.sendErrorMsg("AcscNetwork data ckeck
		 * error, \n" + acscNet.toString()); throw new
		 * InterpssRuntimeException("AcscAdjNetwork data ckeck error, \n" +
		 * acscNet.toString()); }
		 */
		// System.out.println(acscNet.net2String());
		return acscNet;
	}

	private static boolean setAddBusForm2Net(GBusForm form, AcscNetwork acscNet) {
		AcscBus bus = CoreObjectFactory.createAcscBus(form.getId(), acscNet);
		//acscNet.addBus(bus);

		BaseFormDataMapperImpl.setBaseBusInfo(form, bus, acscNet);
		return setBusInfo(form, bus, acscNet);
	}

	private static boolean setAddBranchForm2Net(GBranchForm form,
			AcscNetwork acscNet, IPSSMsgHub msg) {
		AcscBranch branch = CoreObjectFactory.createAcscBranch();
		acscNet.addBranch(branch, form.getFromId(), form.getToId());

		BaseFormDataMapperImpl.setBaseBranchInfo(form, branch, acscNet);
		return setBranchInfo(form, branch, acscNet, msg);
	}

	public static boolean setBusInfo(GBusForm formBus, AcscBus bus,
			AcscNetwork net) {
		AclfFormDataMapperImpl.setAclfBusFormInfo(formBus, bus, net);

		AcscBusData busData = formBus.getAcscBusData();
		if (busData.getScCode().equals(AcscBusData.ScCode_Contribute)) {
			return setContributeBusFormInfo(busData, bus, net);
		} else if (busData.getScCode().equals(AcscBusData.ScCode_NonContribute)) {
			return setNonContributeBusFormInfo(busData, bus, net);
		} else if (busData.getScCode().equals(AcscBusData.ScCode_BusScripting)) {
			bus.setScCode(BusScCode.SC_BUS_SCRIPTING);

			if (busData.getScriptLanguage() == BaseDataBean.ScriptLanguage_Java) {
				String classname = ScriptJavacUtilFunc
						.createScriptingClassname(bus.getId());
				String javacode = CoreScriptUtilFunc.parseAcscJavaCode(busData
						.getScripts(), classname,
						CoreScriptUtilFunc.Tag_AcscScriptBus_Baseclass,
						CoreScriptUtilFunc.Tag_AcscScriptBus_Begin);
				try {
					bus.setExtensionObject(MemoryJavaCompiler
							.javac(CoreScriptUtilFunc.AcscScriptingPackageName
									+ "/" + classname, javacode));
				} catch (Exception e) {
					IpssLogger.logErr(e);
					return false;
				}
			} else {
				Object plugin = UISpringFactory
						.getCustomAcscBusScriptPlugin(busData
								.getScriptPluginName());
				bus.setExtensionObject(plugin);
			}
		} else {
			throw new InterpssRuntimeException(
					"Wrong bus Branch type for mapping AcscBusInfo, type: "
							+ busData.getScCode());
		}
		return true;
	}

	private static boolean setContributeBusFormInfo(AcscBusData busData,
			AcscBus bus, AcscNetwork net) {
		bus.setScCode(BusScCode.CONTRIBUTE);
		try {
			setBusScZ(bus, net.getBaseKva(), busData.getZ1R(),
					busData.getZ1X(), busData.getZ2R(), busData.getZ2X(),
					busData.getZ0R(), busData.getZ0X(), Unit.toUnit(busData
							.getZUnit()));
			setBusScZg(bus, bus.getBaseVoltage(), net.getBaseKva(), busData
					.getGround().getCode(), busData.getGround().getR(), busData
					.getGround().getX(), Unit.toUnit(busData.getGround()
					.getUnit()));
			return true;
		} catch (Exception e) {
			IpssLogger.logErr(e);
			throw new InterpssRuntimeException(e.toString());
		}
	}

	private static boolean setNonContributeBusFormInfo(AcscBusData busData,
			AcscBus bus, AcscNetwork net) {
		bus.setScCode(BusScCode.NON_CONTRI);
		bus.setScZ(NumericConstant.LargeBusZ, SequenceCode.POSITIVE);
		bus.setScZ(NumericConstant.LargeBusZ, SequenceCode.NEGATIVE);
		bus.setScZ(NumericConstant.LargeBusZ, SequenceCode.ZERO);
		bus.getGrounding().setCode(BusGroundCode.UNGROUNDED);
		bus.getGrounding().setZ(NumericConstant.LargeBusZ);
		return true;
	}

	private static void setBusScZ(AcscBus bus, double baseKVA, double r1,
			double x1, double r2, double x2, double r0, double x0, UnitType zUnit) {
		bus.setScZ(new Complex(r1, x1), SequenceCode.POSITIVE, zUnit);
		bus.setScZ(new Complex(r2, x2), SequenceCode.NEGATIVE, zUnit);
		bus.setScZ(new Complex(r0, x0), SequenceCode.ZERO, zUnit);
	}

	private static void setBusScZg(AcscBus bus, double baseV, double baseKVA,
			String gType, double rg, double xg, UnitType zgUnit) {
		bus.getGrounding().setCode(
				CoreUtilFunc.scGroundType2BusGroundCode(gType));
		bus.getGrounding().setZ(new Complex(rg, xg), zgUnit, baseV, baseKVA);
	}

	public static boolean setBranchInfo(GBranchForm formBranch,
			AcscBranch branch, AcscNetwork net, IPSSMsgHub msg) {
		AclfFormDataMapperImpl.setAclfBranchFormInfo(formBranch, branch, net,
				false, msg);

		AcscBranchData braData = formBranch.getAcscBranchData();
		if (braData.getLfCode().equals(IGBranchForm.TransBranchLfCode_Line)) { // line
																				// branch
			return setAcscLineFormInfo(braData, branch, net, msg);
		} else if (braData.getLfCode().equals(
				IGBranchForm.TransBranchLfCode_Xfr)
				|| braData.getLfCode().equals(
						IGBranchForm.TransBranchLfCode_PsXfr)) { // psxfr
																	// branch
			return setAcscXfrFormInfo(braData, branch, net, msg);
		} else if (braData.getLfCode().equals(
				IGBranchForm.TransBranchCode_Scripting)
				&& branch instanceof AcscBranch) {
			branch.setBranchCode(AclfBranchCode.BRANCH_EXTENSION);
			// branch.setScripts(data.getScripts());

			if (braData.getScriptLanguage() == BaseDataBean.ScriptLanguage_Java) {
				// branch.setScripts(data.getScripts());
				String classname = ScriptJavacUtilFunc
						.createScriptingClassname(branch.getId());
				String javacode = CoreScriptUtilFunc.parseAcscJavaCode(braData
						.getScripts(), classname,
						CoreScriptUtilFunc.Tag_AcscScriptBranch_Baseclass,
						CoreScriptUtilFunc.Tag_AcscScriptBranch_Begin);
				try {
					branch
							.setExtensionObject(MemoryJavaCompiler
									.javac(
											CoreScriptUtilFunc.AcscScriptingPackageName
													+ "/" + classname, javacode));
				} catch (Exception e) {
					IpssLogger.logErr(e);
					return false;
				}
			} else {
				Object plugin = UISpringFactory
						.getCustomAcscBranchScriptPlugin(braData
								.getScriptPluginName());
				branch.setExtensionObject(plugin);
			}
		} else {
			throw new InterpssRuntimeException(
					"Wrong Aclf Branch type for mapping AcscBranchInfo, type: "
							+ braData.getLfCode());
		}
		return true;
	}

	private static boolean setAcscLineFormInfo(AcscBranchData branchData,
			AcscBranch branch, AcscNetwork net, IPSSMsgHub msg) {
		double baseV = branch.getFromAclfBus().getBaseVoltage();
		AcscLineAdapter line = (AcscLineAdapter) branch
				.getAdapter(AcscLineAdapter.class);
		line.setZ0(new Complex(branchData.getZ0R(), branchData.getZ0X()),
				Unit.toUnit(branchData.getZ0Unit()), baseV);
		line.setHB0(branchData.getHalfShuntB0(), Unit.toUnit(branchData
				.getHalfShuntB0Unit()), baseV);
		return true;
	}

	private static boolean setAcscXfrFormInfo(AcscBranchData branchData,
			AcscBranch branch, AcscNetwork net, IPSSMsgHub msg) {
		double baseV = branch.getFromAclfBus().getBaseVoltage() > branch
				.getToAclfBus().getBaseVoltage() ? branch.getFromAclfBus()
				.getBaseVoltage() : branch.getToAclfBus().getBaseVoltage();
		AcscXfrAdapter xfr = (AcscXfrAdapter) branch
				.getAdapter(AcscXfrAdapter.class);
		xfr.setZ0(new Complex(branchData.getZ0R(), branchData.getZ0X()),
				Unit.toUnit(branchData.getZ0Unit()), baseV);

		XfrConnectData connect = branchData.getFromXfrConnectData();
		xfr.setFromConnectGroundZ(calXfrConnectCode(connect), new Complex(
				connect.getGrounding().getR(), connect.getGrounding().getX()),
				Unit.toUnit(connect.getGrounding().getUnit()));

		connect = branchData.getToXfrConnectData();
		xfr.setToConnectGroundZ(calXfrConnectCode(connect), new Complex(connect
				.getGrounding().getR(), connect.getGrounding().getX()),
				Unit.toUnit(connect.getGrounding().getUnit()));

		return true;
	}

	private static XfrConnectCode calXfrConnectCode(XfrConnectData connect) {
		// connectCode : [Delta | Wye]
		// groundCode : [SolidGrounded | ZGrounded | Ungrounded ]
		if (connect.getCode().equals(XfrConnectData.Code_Delta))
			// str set [ DeltaConnected | SolidGrounded | ZGrounded | Ungrounded
			// ]
			return XfrConnectCode.DELTA;
		else {
			ScGroundEnum code = ScGroundType.str2Code(connect.getGrounding().getCode());
			if (code == ScGroundEnum.Ungrounded)
				return XfrConnectCode.WYE_UNGROUNDED;
			else if (code == ScGroundEnum.ZGrounded)
				return XfrConnectCode.WYE_ZGROUNDED;
			else if (code == ScGroundEnum.SolidGrounded)
				return XfrConnectCode.WYE_SOLID_GROUNDED;
		}
		throw new InterpssRuntimeException(
				"Wrong input in AcscFormDataMapperImpl.calXfrConnectCode()");
	}
}