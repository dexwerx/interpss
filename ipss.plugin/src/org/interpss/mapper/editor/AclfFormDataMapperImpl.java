/*
 * @(#)AclfFormDataMapperImpl.java   
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
import org.interpss.editor.data.aclf.AclfAdjBranchData;
import org.interpss.editor.data.aclf.AclfAdjBusData;
import org.interpss.editor.data.aclf.AclfBranchData;
import org.interpss.editor.data.aclf.AclfBusData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.ui.UISpringAppContext;
import org.interpss.editor.ui.util.CoreScriptUtilFunc;
import org.interpss.editor.ui.util.ScriptJavacUtilFunc;
import org.interpss.numeric.datatype.LimitType;
import org.interpss.numeric.datatype.Unit;
import org.interpss.numeric.datatype.Unit.Type;
import org.interpss.util.MemoryJavaCompiler;

import com.interpss.CoreObjectFactory;
import com.interpss.DStabObjectFactory;
import com.interpss.common.datatype.UnitHelper;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.mapper.AbstractMapping;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.rec.BaseDataBean;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.NetUtilFunc;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adj.AdjControlType;
import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.aclf.adj.PQBusLimit;
import com.interpss.core.aclf.adj.PSXfrPControl;
import com.interpss.core.aclf.adj.PVBusLimit;
import com.interpss.core.aclf.adj.RemoteQBus;
import com.interpss.core.aclf.adj.RemoteQControlType;
import com.interpss.core.aclf.adj.TapControl;
import com.interpss.core.aclf.adpter.CapacitorBusAdapter;
import com.interpss.core.aclf.adpter.LineAdapter;
import com.interpss.core.aclf.adpter.LoadBusAdapter;
import com.interpss.core.aclf.adpter.PQBusAdapter;
import com.interpss.core.aclf.adpter.PSXfrAdapter;
import com.interpss.core.aclf.adpter.PVBusAdapter;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.aclf.adpter.XfrAdapter;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.dstab.DStabilityNetwork;

/**
 * Map functions for BaseNetForm, BaseBusForm, BaseBranchForm to/from Network,
 * Bus, Branch simu objects
 */

public class AclfFormDataMapperImpl extends AbstractMapping<GFormContainer, AclfNetwork> {
	private IPSSMsgHub msg = null;
	public AclfFormDataMapperImpl(IPSSMsgHub msg) {
		this.msg = msg;
	}
	
	/**
	 * Map the GFormContainer object to a AclfNetwork object
	 * 
	 * @param editNet
	 *            the GFormContainer object
	 * @param msg
	 *            a SessionMsg object
	 * @return an AclfAdjNetwork object
	 */
	@Override
	public AclfNetwork map2Model(GFormContainer editNet) {
		AclfNetwork aclfNet = CoreObjectFactory.createAclfNetwork();

		BaseFormDataMapperImpl.setBaseNetInfo((GNetForm) editNet.getGNetForm(),
				aclfNet);
		// no extra aclf net info needed to map

		try {
			setAclfNetInfo(editNet, aclfNet, msg);
		} catch (InterpssException e) {
			msg.sendErrorMsg(e.toString());
		}
		// System.out.println(aclfNet.net2String());
		return aclfNet;
	}

	/**
	 * Set AclfNet Bus and Branch info into the aclfNet Object
	 * 
	 * @param editNet
	 *            the GFormContainer object
	 * @param aclfNet
	 *            the AclfNetwork object
	 * @param msg
	 *            a SessionMsg object
	 */
	public static void setAclfNetInfo(GFormContainer editNet,
			AclfNetwork aclfNet, IPSSMsgHub msg) throws InterpssException {
		IpssLogger.getLogger().info(
				"AclfFormDataMapperImpl.setBaseNetInfo() called");

		// first put AclfBus and AclfBranch info into Net
		for (Object obj : editNet.getBusFormList()) {
			// For each AcscBus xml object, parse for an AcscBus form object
			GBusForm busForm = (GBusForm) obj;
			setAddBusForm2Net(busForm, aclfNet);
			// System.out.println("\nBus info, #:" + (i+1));
			// System.out.println(busForm.toString());
		}

		for (Object obj : editNet.getBranchFormList()) {
			// For each AcscBranch xml object, parse for an AcscBranch form
			// object
			GBranchForm branchForm = (GBranchForm) obj;
			setAddBranchForm2Net(branchForm, aclfNet, msg);
			// System.out.println("\nBranch info, #:" + (i+1));
			// System.out.println(branchForm.toString());
		}

		// Then put AclfAdjBus and AclfAdjBranch info into Net
		if (((GNetForm) editNet.getGNetForm()).getAcscNetData().isHasAdjustment()) {
			for (Object obj : editNet.getBusFormList()) {
				// For each AcscBus xml object, parse for an AcscBus form object
				try {
					GBusForm busForm = (GBusForm) obj;
					addAclfAdjBusFormInfo(busForm, aclfNet);
				} catch (InterpssException e) {
					msg.sendErrorMsg(e.toString());
				}
				// System.out.println("\nBus info, #:" + (i+1));
				// System.out.println(busForm.toString());
			}

			for (Object obj : editNet.getBranchFormList()) {
				// For each AcscBranch xml object, parse for an AcscBranch form
				// object
				GBranchForm branchForm = (GBranchForm) obj;
				addAclfAdjBranchFormInfo(branchForm, aclfNet, msg);
				// System.out.println("\nBranch info, #:" + (i+1));
				// System.out.println(branchForm.toString());
			}
		}

		aclfNet.setLfDataLoaded(true);
		/*
		 * if (!aclfNet.checkData(msg)) { msg.sendErrorMsg("AclfAdjNetwork data
		 * ckeck error, \n" + aclfNet.toString()); throw new
		 * InterpssRuntimeException("AclfAdjNetwork data ckeck error, \n" +
		 * aclfNet.toString()); }
		 */
	}

	/**
	 * Create and set the aclf bus info, then add the bus object to the AclfNet
	 * 
	 * @param form
	 *            a GBusForm object
	 * @param net
	 *            the aclf net object, it may be an AcscNetwork object
	 * @return the status
	 */
	public static boolean setAddBusForm2Net(GBusForm form, AclfNetwork net) {
		AclfBus bus = null;
		if (net instanceof DStabilityNetwork)
			bus = DStabObjectFactory.createDStabBus(form.getId(),
					(DStabilityNetwork) net);
		else {
			if (net instanceof AcscNetwork) {
				bus = CoreObjectFactory.createAcscBus(form.getId(), net);
			} else {
				bus = CoreObjectFactory.createAclfBus(form.getId(), net);
			}
			//net.addBus(bus);
		}

		BaseFormDataMapperImpl.setBaseBusInfo(form, bus, net);
		return setAclfBusFormInfo(form, bus, net);
	}

	/**
	 * Create and set the aclf branch info, then add the branch object to the
	 * AclfNet
	 * 
	 * @param form
	 *            a GBranchForm object
	 * @param aclfNet
	 *            the aclf net object, it may be an AcscNetwork object
	 * @return the Branch object of type AclfBranch or AcscBranch
	 */
	public static AclfBranch setAddBranchForm2Net(GBranchForm form,
			AclfNetwork net, IPSSMsgHub msg) {
		AclfBranch branch = null;
		if (net instanceof DStabilityNetwork)
			branch = DStabObjectFactory.createDStabBranch(form.getFromId(),
					form.getToId(), (DStabilityNetwork) net);
		else {
			if (net instanceof AcscNetwork) {
				branch = CoreObjectFactory.createAcscBranch();
			} else {
				branch = CoreObjectFactory.createAclfBranch();
			}
			net.addBranch(branch, form.getFromId(), form.getToId());
		}

		BaseFormDataMapperImpl.setBaseBranchInfo(form, branch, net);
		setAclfBranchFormInfo(form, branch, net, true, msg);

		return branch;
	}

	/**
	 * Set the AclfBusData info to the AclfBus object
	 * 
	 * @param formBus
	 *            the GBusForm object, containing AclfBusData info
	 * @param bus
	 *            the Aclf bus
	 * @param aclfNet
	 *            an Network object containing the bus object.
	 * @return the status
	 */
	public static boolean setAclfBusFormInfo(GBusForm formBus, AclfBus bus,
			AclfNetwork aclfNet) {
		AclfBusData busData = formBus.getAcscBusData();
		if (busData.getGenCode().equals(AclfBusData.GenCode_PQ)) {
			bus.setGenCode(AclfGenCode.GEN_PQ);
			PQBusAdapter pqBus = bus.toPQBus();
			pqBus.setGen(new Complex(busData.getGenP(), busData.getGenQ()),
							Unit.toUnit(busData.getGenUnit()));
		} else if (busData.getGenCode().equals(AclfBusData.GenCode_PV)) {
			bus.setGenCode(AclfGenCode.GEN_PV);
			PVBusAdapter pvBus = bus.toPVBus();
			pvBus.setGenP(busData.getGenP(), Unit.toUnit(busData.getGenUnit()));
			// VoltgeMsg is used to hold PV-VSpec, ReQVolt-VSpec and
			// ReQMvarFlow-MvarSpec
			pvBus.setVoltMag(busData.getVoltageMag(), Unit.toUnit(busData
					.getVoltageMagUnit()));
		} else if (busData.getGenCode().equals(AclfBusData.GenCode_Swing)) {
			bus.setGenCode(AclfGenCode.SWING);
			SwingBusAdapter swing = bus.toSwingBus();
			swing.setVoltMag(busData.getVoltageMag(), Unit.toUnit(busData
					.getVoltageMagUnit()));
			swing.setVoltAng(busData.getVoltageAng(), Unit.toUnit(busData
					.getVoltageAngUnit()));
		} else if (busData.getGenCode().equals(AclfBusData.GenCode_Capacitor)) { // capacitor
																					// bus
			bus.setGenCode(AclfGenCode.CAPACITOR);
			CapacitorBusAdapter cBus = bus.toCapacitorBus();
			cBus.setQ(busData.getCapQ(),
					Unit.toUnit(busData.getCapQUnit()));
		} else if (busData.getGenCode()
				.equals(AclfBusData.GenCode_GenScripting)) {
			bus.setGenCode(AclfGenCode.BUS_EXTENSION);
			// bus.setScripts(busData.getScripts());
		} else {
			bus.setGenCode(AclfGenCode.NON_GEN);
		}

		bus.setLoadCode(parseLoadCode(busData.getLoadCode()));
		if (busData.getLoadCode().equals(AclfBusData.LoadCode_LoadScripting)) {
			// bus.setScripts(busData.getScripts());
		} else {
			LoadBusAdapter loadBus = bus.toLoadBus();
			if (!busData.getLoadCode().equals(AclfBusData.LoadCode_NonLoad))
				loadBus.setLoad(new Complex(busData.getLoadP(), busData
						.getLoadQ()), Unit.toUnit(busData.getLoadUnit()));
		}

		Complex ypu = UnitHelper.yConversion(new Complex(busData.getShuntG(),
				busData.getShuntB()), bus.getBaseVoltage(), aclfNet
				.getBaseKva(), Unit.toUnit(busData.getShuntYUnit()),
				Type.PU);
		bus.setShuntY(ypu);

		if (busData.getGenCode().equals(AclfBusData.GenCode_GenScripting)
				|| busData.getLoadCode().equals(
						AclfBusData.LoadCode_LoadScripting)) {
			if (busData.getScriptLanguage() == BaseDataBean.ScriptLanguage_Java) {
				// compile the source code
				String classname = "";
				String javacode = busData.getScripts();
				if (javacode.startsWith(ScriptJavacUtilFunc.Token_CodeReuse)) { // format
																				// @busId
					classname = ScriptJavacUtilFunc
							.createScriptingClassname(javacode.substring(1));
				} else {
					classname = ScriptJavacUtilFunc
							.createScriptingClassname(bus.getId());
					javacode = CoreScriptUtilFunc.parseAclfJavaCode(busData
							.getScripts(), classname,
							CoreScriptUtilFunc.Tag_AclfScriptBus_Baseclass,
							CoreScriptUtilFunc.Tag_AclfScriptBus_Begin);
				}
				try {
					bus.setExtensionObject(MemoryJavaCompiler
							.javac(CoreScriptUtilFunc.AclfScriptingPackageName
									+ "/" + classname, javacode));
				} catch (Exception e) {
					IpssLogger.logErr(e);
					return false;
				}
			} else {
				Object plugin = UISpringAppContext
						.getCustomAclfBusScriptPlugin(busData
								.getScriptPluginName());
				bus.setExtensionObject(plugin);
			}
		}
		return true;
	}

	/**
	 * Set the AclfAdjBusData info to the AclfBus object
	 * 
	 * @param formBus
	 *            the GBusForm object, containing AclfBusData info
	 * @param aclfNet
	 *            an Network object containing the bus object.
	 * @return the status
	 */
	private static boolean addAclfAdjBusFormInfo(GBusForm formBus,
			AclfNetwork aclfNet) throws InterpssException {
		AclfBus bus = aclfNet.getAclfBus(formBus.getId());
		AclfBusData busData = formBus.getAcscBusData();
		if (busData.getGenCode().equals(AclfBusData.GenCode_PQ)) {
			AclfAdjBusData adjData = formBus.getAcscBusData();
			if (adjData.isHasRemoteVControl()) {
				if (adjData.getReQControlType() == AclfAdjBusData.ReQControlType_Voltage) {
					String remoteBusId = NetUtilFunc
							.getBusIdFromDisplayNameId(adjData
									.getRemoteControlBusId());
					RemoteQBus reQ = CoreObjectFactory.createRemoteQBus(
							bus, RemoteQControlType.BUS_VOLTAGE, aclfNet, remoteBusId);
					reQ.setQLimit(new LimitType(adjData.getMaxGenQ(), adjData
							.getMinGenQ()));
					// VoltgeMsg is used to hold PV-VSpec, ReQVolt-VSpec and
					// ReQMvarFlow-MvarSpec
					reQ.setVSpecified(adjData.getVoltageMag());
				} else if (adjData.getReQControlType() == AclfAdjBusData.ReQControlType_MvarFlow) {
					String remoteBranchId = NetUtilFunc
							.getBranchIdFromDisplayNameId(adjData
									.getRemoteControlBranchId());
					RemoteQBus reQ = CoreObjectFactory.createRemoteQBus(
							bus, RemoteQControlType.BRANCH_Q, aclfNet, remoteBranchId);
					reQ.setQLimit(new LimitType(adjData.getMaxGenQ(), adjData
							.getMinGenQ()));
					// VoltgeMsg is used to hold PV-VSpec, ReQVolt-VSpec and
					// ReQMvarFlow-MvarSpec
					reQ.setMvarSpecified(adjData.getVoltageMag(), Type.PU);
					reQ.setFlowFrom2To(adjData.isFlowFrom2To());
					reQ.setMvarOnFromSide(adjData.isMvarControlOnFromSide());
				}
			} else if (adjData.isHasLimitControl()) {
				double max = adjData.getMaxVoltMag();
				double min = adjData.getMinVoltMag();
				PQBusLimit pqLimit = CoreObjectFactory.createPQBusLimit(bus);
				pqLimit.setVLimit(new LimitType(max, min), Type.PU);
			}
		} else if (busData.getGenCode().equals(AclfBusData.GenCode_PV)) {
			AclfAdjBusData adjData = formBus.getAcscBusData();
			if (adjData.isHasLimitControl()) {
				double max = adjData.getMaxGenQ();
				double min = adjData.getMinGenQ();
				PVBusLimit pvLimit = CoreObjectFactory.createPVBusLimit(bus);
				pvLimit.setQLimit(new LimitType(max, min), Type.PU);
			}
		}

		if (busData.getLoadCode().equals(AclfBusData.LoadCode_FuncLoad)) {
			AclfAdjBusData adjData = formBus.getAcscBusData();
			FunctionLoad fload = CoreObjectFactory.createFunctionLoad(bus);
			fload.getP().setA(adjData.getLoadP_PPct(), Type.Percent);
			fload.getP().setB(adjData.getLoadP_IPct(), Type.Percent);
			fload.getQ().setA(adjData.getLoadQ_PPct(), Type.Percent);
			fload.getQ().setB(adjData.getLoadQ_IPct(), Type.Percent);
		}
		return true;
	}

	private static AclfLoadCode parseLoadCode(String code) {
		return code.equals(AclfBusData.LoadCode_ConstP)
				|| code.equals(AclfBusData.LoadCode_FuncLoad) ? AclfLoadCode.CONST_P
				: (code.equals(AclfBusData.LoadCode_ConstI) ? AclfLoadCode.CONST_I
						: (code.equals(AclfBusData.LoadCode_ConstZ) ? AclfLoadCode.CONST_Z
								: (code
										.equals(AclfBusData.LoadCode_LoadScripting) ? AclfLoadCode.BUS_EXTENSION
										: AclfLoadCode.NON_LOAD)));
	}

	/**
	 * Set the AclfBranchData info to the AclfBranch object
	 * 
	 * @param formBranch
	 *            a GBranchForm object containing the AclfBranchData object.
	 * @param branch
	 *            the AclfBranch object
	 * @param aclfNet
	 *            a Network object containing the branch object
	 * @return the status
	 */
	public static boolean setAclfBranchFormInfo(GBranchForm formBranch,
			AclfBranch branch, AclfNetwork aclfNet, boolean aclf,
			IPSSMsgHub msg) {
		AclfBranchData braData = formBranch.getAcscBranchData();
		if (braData.getLfCode().equals(IGBranchForm.TransBranchLfCode_Line)) { // line
																				// branch
			return setLineBranchFormInfo(formBranch, branch, aclfNet, msg);
		} else if (braData.getLfCode().equals(
				IGBranchForm.TransBranchLfCode_Xfr)) { // xfr branch
			setXfrBranchFormInfo(formBranch, branch, aclfNet, msg);
		} else if (braData.getLfCode().equals(
				IGBranchForm.TransBranchLfCode_PsXfr)) { // psxfr branch
			setPSXfrBranchFormInfo(formBranch, branch, aclfNet, msg);
		} else if (braData.getLfCode().equals(
				IGBranchForm.TransBranchCode_Scripting)
				&& aclf) {
			branch.setBranchCode(AclfBranchCode.BRANCH_EXTENSION);
			if (braData.getScriptLanguage() == BaseDataBean.ScriptLanguage_Java) {
				// branch.setScripts(data.getScripts());
				String classname = ScriptJavacUtilFunc
						.createScriptingClassname(branch.getId());
				String javacode = CoreScriptUtilFunc.parseAclfJavaCode(braData
						.getScripts(), classname,
						CoreScriptUtilFunc.Tag_AclfScriptBranch_Baseclass,
						CoreScriptUtilFunc.Tag_AclfScriptBranch_Begin);
				try {
					branch
							.setExtensionObject(MemoryJavaCompiler
									.javac(
											CoreScriptUtilFunc.AclfScriptingPackageName
													+ "/" + classname, javacode));
				} catch (Exception e) {
					IpssLogger.logErr(e);
					return false;
				}
			} else {
				Object plugin = UISpringAppContext
						.getCustomAclfBranchScriptPlugin(braData
								.getScriptPluginName());
				branch.setExtensionObject(plugin);
			}
		}
		return true;
	}

	private static boolean addAclfAdjBranchFormInfo(GBranchForm formBranch,
			AclfNetwork aclfNet, IPSSMsgHub msg) throws InterpssException {
		AclfBranch branch = (AclfBranch) aclfNet.getBranch(formBranch
				.getFromId(), formBranch.getToId());
		AclfBranchData data = formBranch.getAcscBranchData();
		if (data.getLfCode().equals(IGBranchForm.TransBranchLfCode_Xfr)) { // xfr
																			// branch
			setXfrAdjBranchFormInfo(formBranch, branch, aclfNet, msg);
			return true;
		} else if (data.getLfCode()
				.equals(IGBranchForm.TransBranchLfCode_PsXfr)) { // psxfr
																	// branch
			setPSXfrAdjBranchFormInfo(formBranch, branch, aclfNet, msg);
			return true;
		}
		return true;
	}

	private static boolean setLineBranchFormInfo(GBranchForm branchForm,
			AclfBranch branch, AclfNetwork net, IPSSMsgHub msg) {
		try {
			AclfBranchData data = branchForm.getAcscBranchData();
			branch.setBranchCode(AclfBranchCode.LINE);
			LineAdapter line = branch.toLine();
			line.setZ(new Complex(data.getZR(), data.getZX()), Unit
					.toUnit(data.getZUnit()), branch.getFromAclfBus()
					.getBaseVoltage());
			line.setHShuntY(new Complex(0.0, data.getHalfShuntB()), Unit
					.toUnit(data.getHalfShuntBUnit()), branch.getFromAclfBus()
					.getBaseVoltage());
			line.setFromShuntY(new Complex(data.getFromShuntG(),data.getFromShuntB()), 
					Unit.toUnit(data.getShuntYUnit()));
			line.setToShuntY(new Complex(data.getToShuntG(),data.getToShuntB()), 
					Unit.toUnit(data.getShuntYUnit()));
			line.setMvaRating1(data.getRating1());
			line.setMvaRating2(data.getRating2());
			line.setMvaRating3(data.getRating3());
			return true;
		} catch (Exception e) {
			IpssLogger.logErr(e);
			throw new InterpssRuntimeException(e.toString());
		}
	}

	private static boolean setXfrBranchFormInfo(GBranchForm branchForm,
			AclfBranch branch, AclfNetwork net, IPSSMsgHub msg) {
		try {
			AclfBranchData data = branchForm.getAcscBranchData();
			branch.setBranchCode(AclfBranchCode.XFORMER);
			double fromBaseV = branch.getFromAclfBus().getBaseVoltage(), toBaseV = branch
					.getToAclfBus().getBaseVoltage();
			// the follow only applies if zUnit is in Ohms, which is very
			// unlikely
			double baseV = fromBaseV > toBaseV ? fromBaseV : toBaseV;
			XfrAdapter xfr = branch.toXfr();
			xfr.setZ(new Complex(data.getZR(), data.getZX()), Unit
					.toUnit(data.getZUnit()), baseV);

			xfr.setFromTurnRatio(data.getXfrTapFromSideTap(), Unit
					.toUnit(data.getXfrTapUnit()));
			xfr.setToTurnRatio(data.getXfrTapToSideTap(), Unit.toUnit(data
					.getXfrTapUnit()));
			xfr.setFromShuntY(new Complex(data.getFromShuntG(),data.getFromShuntB()), 
					Unit.toUnit(data.getShuntYUnit()));
			xfr.setToShuntY(new Complex(data.getToShuntG(),data.getToShuntB()), 
					Unit.toUnit(data.getShuntYUnit()));
			xfr.setMvaRating1(data.getRating1());
			xfr.setMvaRating2(data.getRating2());
			xfr.setMvaRating3(data.getRating3());
			return true;
		} catch (Exception e) {
			IpssLogger.logErr(e);
			throw new InterpssRuntimeException(e.toString());
		}
	}

	private static boolean setXfrAdjBranchFormInfo(GBranchForm branchForm,
			AclfBranch branch, AclfNetwork net, IPSSMsgHub msg) throws InterpssException{
		AclfAdjBranchData adjData = branchForm.getAcscBranchData();
		if (adjData.isHasTapVControl()) {
			TapControl tapv = null;
			if (adjData.getTapVControlType() == AclfAdjBranchData.TapControlType_Voltage) {
				String vcBusId = NetUtilFunc.getBusIdFromDisplayNameId(adjData
						.getVcBusId());
				tapv = CoreObjectFactory.createTapVControlBusVoltage(
						branch, AdjControlType.POINT_CONTROL, net, vcBusId);
				tapv.setVSpecified(adjData.getVcVSpec());
				tapv.setVcBusOnFromSide(adjData.isVCBusOnFromSide());
			} else {
				tapv = CoreObjectFactory.createTapVControlMvarFlow(
						branch, AdjControlType.POINT_CONTROL);
				tapv.setMvarSpecified(adjData.getMvarFlowSpec());
				tapv.setMeteredOnFromSide(adjData.isMvarSpecOnFromSide());
				tapv.setFlowFrom2To(adjData.isFlowFrom2To());
			}
			tapv.setTurnRatioLimit(new LimitType(adjData.getVcTapMax(), adjData
					.getVcTapMin()));
			double stepSize = 0.0;
			if (adjData.getVcStep() > 0) {
				stepSize = (adjData.getVcTapMax() - adjData.getVcTapMin())
						/ adjData.getVcStep();
			}
			tapv.setTapStepSize(stepSize);
			tapv.setTapOnFromSide(adjData.isVCTapOnFromSide());
		}
		return true;
	}

	private static boolean setPSXfrBranchFormInfo(GBranchForm formBranch,
			AclfBranch branch, AclfNetwork net, IPSSMsgHub msg) {
		setXfrBranchFormInfo(formBranch, branch, net, msg);
		AclfBranchData data = formBranch.getAcscBranchData();
		branch.setBranchCode(AclfBranchCode.PS_XFORMER);
		PSXfrAdapter psXfr = branch.toPSXfr();
		psXfr.setFromAngle(data.getPhaseShiftAngle(), Unit.toUnit(data
				.getPhaseShiftAngleUnit()));
		return true;
	}

	private static boolean setPSXfrAdjBranchFormInfo(GBranchForm formBranch,
			AclfBranch branch, AclfNetwork net, IPSSMsgHub msg) throws InterpssException {
		AclfAdjBranchData adjData = formBranch.getAcscBranchData();
		if (adjData.isHasPSXfrPControl()) {
			PSXfrPControl psXfrControl = CoreObjectFactory.createPSXfrPControl(
					branch, AdjControlType.POINT_CONTROL);
			psXfrControl.setPSpecified(adjData.getPcPSpec());
			psXfrControl.setAngLimit(new LimitType(Math.toRadians(adjData.getPcAngMax()), 
					Math.toRadians(adjData.getPcAngMin())));
			psXfrControl.setControlOnFromSide(adjData.isPcOnFromSide());
			psXfrControl.setFlowFrom2To(adjData.isFlowFrom2To());
		}
		return true;
	}

}