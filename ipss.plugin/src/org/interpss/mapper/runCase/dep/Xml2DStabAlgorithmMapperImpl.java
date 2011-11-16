/*
 * @(#)Xml2AlgorithmMapperImpl.java   
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

import org.interpss.numeric.util.StringHelper;
import org.interpss.xml.schema.AcscFaultCategoryDataType;
import org.interpss.xml.schema.AcscFaultDataType;
import org.interpss.xml.schema.AcscFaultXmlType;
import org.interpss.xml.schema.DStabStudyCaseXmlType;
import org.interpss.xml.schema.DynamicEventDataType;
import org.interpss.xml.schema.DynamicEventXmlType;
import org.interpss.xml.schema.DynamicLoadChangeDataType;
import org.interpss.xml.schema.DynamicLoadChangeXmlType;
import org.interpss.xml.schema.DynamicSetPointChangeXmlType;
import org.interpss.xml.schema.DynamicSimuMethodDataType;
import org.interpss.xml.schema.DynamicStaticLoadDataType;
import org.interpss.xml.schema.MachineControllerDataType;
import org.interpss.xml.schema.ValueChangeDataType;

import com.interpss.CoreObjectFactory;
import com.interpss.DStabObjectFactory;
import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.dstab.DStabBranch;
import com.interpss.dstab.DStabBus;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.StaticLoadModel;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.algo.DynamicSimuMethod;
import com.interpss.dstab.devent.BranchOutageEvent;
import com.interpss.dstab.devent.BranchOutageType;
import com.interpss.dstab.devent.DStabBranchFault;
import com.interpss.dstab.devent.DynamicEvent;
import com.interpss.dstab.devent.DynamicEventType;
import com.interpss.dstab.devent.LoadChangeEvent;
import com.interpss.dstab.devent.LoadChangeEventType;
import com.interpss.dstab.devent.SetPointChangeEvent;
import com.interpss.dstab.mach.Machine;
import com.interpss.dstab.mach.MachineControllerType;
import com.interpss.spring.CoreCommonSpringCtx;

public class Xml2DStabAlgorithmMapperImpl {
	/**
	 * Map RunDStabStudyCaseXmlType object to a DynamicSimuAlgorithm object.
	 * Modifications defined inside the study case also applied to the
	 * DStabilityNetwork object
	 * 
	 * @param caseRec
	 * @param algo
	 */
	public static boolean dstabCaseData2AlgoMapping(
			DStabStudyCaseXmlType dstabCase,
			DynamicSimuAlgorithm algo, IPSSMsgHub msg) {
		Xml2AlgorithmMapperImpl.aclfCaseData2AlgoMapping(dstabCase.getAclfAlgorithm(), algo
				.getAclfAlgorithm(), msg);

		algo.setSimuMethod(dstabCase.getSimuConfig().getSimuMethod() == DynamicSimuMethodDataType.MODIFIED_EULER ? DynamicSimuMethod.MODIFIED_EULER
						: DynamicSimuMethod.RUNGE_KUTTA);
		algo.setTotalSimuTimeSec(dstabCase.getSimuConfig().getTotalSimuTimeSec());
		algo.setSimuStepSec(dstabCase.getSimuConfig().getSimuStepSec());
		algo.setDisableDynamicEvent(dstabCase.getDynamicEventData().isDisableEvent());

		if (dstabCase.getSimuConfig().isAbsoluteMachAngValue()) {
			algo.setRefMachine(null);
		} else {
			Machine mach = getMachine(algo.getDStabNet(), dstabCase
					.getSimuConfig().getRefMachineBusId());
			if (mach == null)
				return false;
			IpssLogger.getLogger().info("Ref mach set to : " + mach.getId());
			algo.setRefMachine(mach);
		}

		// transfer output variable filter info to the DStabAlgo object, which
		// then
		// will be carried by the object to the remote grid node
		algo.setOutputFiltered(dstabCase.getOutputConfig() != null && 
				dstabCase.getOutputConfig().isOutputFilter());
		if (algo.isOutputFiltered()) {
			algo.setOutputVarIdList(StringHelper.toStrArray(dstabCase.getOutputConfig()
					.getOutputVarList().getVariableName().toArray()));
		}

		return dstabCaseData2NetMapping(dstabCase, algo.getDStabNet(), msg);
	}

	private static boolean dstabCaseData2NetMapping(
			DStabStudyCaseXmlType dstabData,
			DStabilityNetwork dstabNet, IPSSMsgHub msg) {
		dstabNet.setNetEqnIterationNoEvent(dstabData.getNetEqnSolveConfig() != null &&
				dstabData.getNetEqnSolveConfig().getNetEqnItrNoEvent() != 0 ? 
						dstabData.getNetEqnSolveConfig().getNetEqnItrNoEvent() : 
							Constants.DStabNetItrNoEvent);
		dstabNet.setNetEqnIterationWithEvent(dstabData.getNetEqnSolveConfig() != null &&
				dstabData.getNetEqnSolveConfig().getNetEqnItrWithEvent() != 0 ? 
						dstabData.getNetEqnSolveConfig().getNetEqnItrWithEvent()
							: Constants.DStabNetItrWithEvent);

		if (dstabData.getStaticLoadModel() != null) {
			dstabNet
					.setStaticLoadModel(dstabData.getStaticLoadModel()
							.getStaticLoadType() == DynamicStaticLoadDataType.CONST_Z ? StaticLoadModel.CONST_Z
							: StaticLoadModel.CONST_P);
			if (dstabData.getStaticLoadModel().getSwitchVolt() != 0.0)
				dstabNet.setStaticLoadSwitchVolt(dstabData.getStaticLoadModel()
						.getSwitchVolt());
			if (dstabData.getStaticLoadModel().getSwitchDeadZone() != 0.0)
				dstabNet.setStaticLoadSwitchDeadZone(dstabData
						.getStaticLoadModel().getSwitchDeadZone());
		}

		if (dstabData.getDynamicEventData().isDisableEvent()) {
			if (dstabData.getDynamicEventData().getEventList() != null && 
			    dstabData.getDynamicEventData().getEventList().getEvent().size() > 0 && 
					dstabData.getDynamicEventData().getEventList().getEvent().get(0).getEventType() == DynamicEventDataType.SET_POINT_CHANGE) {
				DynamicSetPointChangeXmlType scdata = dstabData.getDynamicEventData().getEventList().getEvent().get(0).getSetPointChangeData();
				if (scdata != null) {
					IpssLogger.getLogger().info("Dynamic Event Type: SetPointChange");
					String machId = scdata.getMachId();
					Machine mach = getMachine(dstabNet, machId);
					if (mach == null)
						return false;
					IpssLogger.getLogger().info("SetPointChange mach id : " + mach.getId());

					DynamicEvent event = DStabObjectFactory.createDEvent(
							Constants.Token_SetPointChangeId + machId,
							"SetPointChange", DynamicEventType.SET_POINT_CHANGE,
							dstabNet);
					event.setStartTimeSec(0.0);
					event.setDurationSec(dstabData.getSimuConfig()
							.getTotalSimuTimeSec());
					SetPointChangeEvent eSetPoint = DStabObjectFactory.createSetPointChangeEvent(machId, dstabNet);
					eSetPoint.setControllerType(scdata.getControllerType() == MachineControllerDataType.EXCITER ? MachineControllerType.EXCITER
									: scdata.getControllerType() == MachineControllerDataType.GOVERNOR ? MachineControllerType.GOVERNOR
											: MachineControllerType.STABILIZER);
					eSetPoint.setChangeValue(scdata.getChangeValue());
					eSetPoint.setAbusoluteChange(scdata.getValueChangeType() == ValueChangeDataType.ABSOLUTE);
					event.setBusDynamicEvent(eSetPoint);
				}
			}
		} else {
			for (DynamicEventXmlType eventData : dstabData.getDynamicEventData().getEventList().getEvent()) {
				// make sure that event name is not "" or NewEventName
				IpssLogger.getLogger().info("Event Data: " + eventData);
				// create event name
				String name = "EventAt_" + eventData.getStartTimeSec() + eventData.getEventType();
				// map event type
				DynamicEventType deType = getDEventType(eventData.getEventType(), eventData.getFault().getFaultType());
				// create the DStabEvent
				DynamicEvent event = DStabObjectFactory.createDEvent(eventData.getRecName(), name, deType, dstabNet);
				if (event == null) {
					CoreCommonSpringCtx.getEditorDialogUtil().showErrMsgDialog(
							"Error to create DynamicEvent",
							"Please see the log file for details");
					return false;
				}

				try {
					setEventData(event, eventData, dstabData.getSimuConfig()
							.getTotalSimuTimeSec(), dstabNet, msg);
				} catch (Exception e) {
					IpssLogger.logErr(e);
					CoreCommonSpringCtx.getEditorDialogUtil().showErrMsgDialog(
							"Error to process DynamicEvent",
							"See log file for details, " + e.toString());
					return false;
				}
			}
		}
		return true;
	}

	private static Machine getMachine(DStabilityNetwork net, String machId) {
		Machine mach = net.getMachine(machId);
		if (mach == null) {
			CoreCommonSpringCtx.getEditorDialogUtil().showErrMsgDialog(
					"Machine Id Error",
					"Machine cannot be found, mach id : " + machId);
			IpssLogger.getLogger().severe(
					"Machine cannot be found, mach id : " + machId);
		}
		return mach;
	}

	private static DynamicEventType getDEventType(DynamicEventDataType eventType, AcscFaultDataType faultType) {
		if (eventType == DynamicEventDataType.FAULT) {
			if (faultType == AcscFaultDataType.BUS_FAULT)
				return DynamicEventType.BUS_FAULT;
			else if (faultType == AcscFaultDataType.BRANCH_FAULT)
				return DynamicEventType.BRANCH_FAULT;
			else if (faultType == AcscFaultDataType.BRANCH_OUTAGE)
				return DynamicEventType.BRANCH_OUTAGE;

		} else {
			if (eventType == DynamicEventDataType.LOAD_CHANGE)
				return DynamicEventType.LOAD_CHANGE;
			else if (eventType == DynamicEventDataType.SET_POINT_CHANGE)
				return DynamicEventType.SET_POINT_CHANGE;
		}
		CoreCommonSpringCtx.getEditorDialogUtil().showErrMsgDialog(
				"Dynamic Event Type Error", "EventDataType: " + eventType);
		throw new InterpssRuntimeException(
				"Programming error, eventDataType: " + eventType);
	}

	private static void setEventData(DynamicEvent event,
			DynamicEventXmlType eventData, double toltalSimuTime,
			DStabilityNetwork dstabNet, IPSSMsgHub msg) throws Exception {
		// for LoadChange
		// LowFreq and LowVolt startTime will set by system
		// FixedTime startTime = threshhold
		// always permanent
		IpssLogger.getLogger().info(
				"Dynamic Event Type: " + eventData.getEventType().toString());

		if (eventData.getEventType() == DynamicEventDataType.LOAD_CHANGE) {
			event.setPermanent(true);
			eventData.setDurationSec(0.0);
			if (eventData.getLoadChangeData().getLoadChangeType() == DynamicLoadChangeDataType.FIXED_TIME)
				eventData.setStartTimeSec(eventData.getLoadChangeData()
						.getThreshhold());
			else
				eventData.setStartTimeSec(toltalSimuTime);
		}

		event.setStartTimeSec(eventData.getStartTimeSec());
		event.setPermanent(eventData.isPermanent());
		if (event.isPermanent()) {
			event.setDurationSec(toltalSimuTime);
		} else {
			event.setDurationSec(eventData.getDurationSec());
		}

		if (eventData.getEventType() == DynamicEventDataType.LOAD_CHANGE) {
			event.setType(DynamicEventType.LOAD_CHANGE);
			DynamicLoadChangeXmlType ldata = eventData.getLoadChangeData();
			LoadChangeEvent eLoad = DStabObjectFactory.createLoadChangeEvent(
					ldata.getBusId(), dstabNet);
			eLoad
					.setType(ldata.getLoadChangeType() == DynamicLoadChangeDataType.LOW_FREQUENCY ? LoadChangeEventType.LOW_FREQUENCY
							: (ldata.getLoadChangeType() == DynamicLoadChangeDataType.LOW_VOLTAGE) ? LoadChangeEventType.LOW_VOLTAGE
									: LoadChangeEventType.FIXED_TIME);
			if (ldata.getChangeFactor() != 0.0)
				eLoad.setChangeFactor(ldata.getChangeFactor());
			if (ldata.getThreshhold() != 0.0)
				eLoad.setThreshhold(ldata.getThreshhold());
			if (ldata.getDelayTime() != 0.0)
				eLoad.setDelaySec(ldata.getDelayTime());
			event.setBusDynamicEvent(eLoad);
		} else if (eventData.getEventType() == DynamicEventDataType.FAULT) {
			AcscFaultXmlType fdata = eventData.getFault();
			if (eventData.getFault().getFaultType() == AcscFaultDataType.BRANCH_OUTAGE) {
				event.setType(DynamicEventType.BRANCH_OUTAGE);
				BranchOutageEvent bOutageEvent = DStabObjectFactory
						.createBranchOutageEvent(fdata.getBusBranchId(),
								dstabNet);
				if (fdata.getFaultCategory() == AcscFaultCategoryDataType.OUTAGE_3_PHASE)
					bOutageEvent.setOutageType(BranchOutageType.THREE_PHASE);
				else if (fdata.getFaultCategory() == AcscFaultCategoryDataType.OUTAGE_1_PHASE)
					bOutageEvent.setOutageType(BranchOutageType.SINGLE_PHASE);
				else if (fdata.getFaultCategory() == AcscFaultCategoryDataType.OUTAGE_2_PHASE)
					bOutageEvent.setOutageType(BranchOutageType.DOUBLE_PHASE);
				event.setBranchDynamicEvent(bOutageEvent);
			} else if (eventData.getFault().getFaultType() == AcscFaultDataType.BUS_FAULT) {
				event.setType(DynamicEventType.BUS_FAULT);
				AcscBusFault fault = CoreObjectFactory
						.createAcscBusFault(Constants.Token_BusFaultId
								+ fdata.getBusBranchId(), dstabNet);
				Xml2AlgorithmMapperImpl.acscFaultData2AcscBusFaultMapping(
						fdata, fault);
				event.setBusFault(fault);
				DStabBus bus = dstabNet.getDStabBus(fdata.getBusBranchId());
				if (bus != null)
					fault.setFaultBus(bus);
				else {
					CoreCommonSpringCtx.getEditorDialogUtil()
							.showErrMsgDialog(
									"Bus Data Error",
									"Bus cannot be found, id:"
											+ fdata.getBusBranchId());
					throw new InterpssRuntimeException(
							"Programming erroe, Bus cannot be found, id:"
									+ fdata.getBusBranchId());
				}
			} else if (eventData.getFault().getFaultType() == AcscFaultDataType.BRANCH_FAULT) {
				event.setType(DynamicEventType.BRANCH_FAULT);
				DStabBranchFault fault = createDStabBranchFault(fdata, dstabNet);
				event.setBranchFault(fault);
				if (fault.isReclosure()) {
					String name = "EventAt_" + eventData.getStartTimeSec()
							+ eventData.getEventType();
					DynamicEvent event2 = DStabObjectFactory.createDEvent(event
							.getId()
							+ "-Reclosure", name,
							DynamicEventType.BRANCH_RECLOSURE, dstabNet);
					event2.setStartTimeSec(fault.getReclosureTime());
					event2.setDurationSec(toltalSimuTime);
					event2.setPermanent(true);
					event2.setBranchFault(createDStabBranchFault(fdata,
							dstabNet));
				}
			}
		}
	}

	private static DStabBranchFault createDStabBranchFault(
			AcscFaultXmlType fdata, DStabilityNetwork dstabNet) {
		DStabBranchFault fault = DStabObjectFactory
				.createDStabBranchFault(Constants.Token_BranchFaultId
						+ fdata.getBusBranchId());
		Xml2AlgorithmMapperImpl.acscFaultData2AcscBranchFaultMapping(fdata,
				fault);
		fault.setReclosure(fdata.isBranchReclosure());
		fault.setReclosureTime(fdata.getReclosureTime());
		DStabBranch branch = dstabNet.getDStabBranch(fdata.getBusBranchId());
		if (branch != null)
			fault.setFaultBranch(branch);
		else {
			CoreCommonSpringCtx.getEditorDialogUtil().showErrMsgDialog(
					"Branch Data Error",
					"Branch cannot be found, id:" + fdata.getBusBranchId());
			throw new InterpssRuntimeException(
					"Programming error, Branch cannot be found, id:"
							+ fdata.getBusBranchId());
		}
		return fault;
	}
}