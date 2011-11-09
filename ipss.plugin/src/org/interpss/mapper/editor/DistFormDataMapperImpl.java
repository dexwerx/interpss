/*
 * @(#)DistFormDataMapperImpl.java   
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

/*
 * Provide mapping functions to map DistNetForm, DistBusForm and DistBranchForm
 * object to an AcscAdjNetwork simu object.
 */

import java.util.List;

import org.apache.commons.math.complex.Complex;
import org.interpss.editor.data.common.GroundData;
import org.interpss.editor.data.common.ScPointData;
import org.interpss.editor.data.common.XfrConnectData;
import org.interpss.editor.data.common.ZData;
import org.interpss.editor.data.dist.DistBranchData;
import org.interpss.editor.data.dist.DistBusData;
import org.interpss.editor.data.dist.DistNetData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;

import com.interpss.DistObjectFactory;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.mapper.AbstractMapping;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.acsc.XFormerConnectCode;
import com.interpss.core.util.CoreUtilFunc;
import com.interpss.dist.DistBranch;
import com.interpss.dist.DistBranchCode;
import com.interpss.dist.DistBus;
import com.interpss.dist.DistBusCode;
import com.interpss.dist.DistNetwork;
import com.interpss.dist.ScStanderd;
import com.interpss.dist.datatype.ScPointType;

public class DistFormDataMapperImpl extends AbstractMapping<GFormContainer, DistNetwork> {
	private IPSSMsgHub msg = null;

	public DistFormDataMapperImpl(IPSSMsgHub msg) {
		this.msg = msg;
	}
	
	/**
	 * Map the GNetContainer object to a DistNetwork object
	 * 
	 * @param editNet
	 *            a NetContainer object, containing the net info
	 * @param msg
	 *            a SessionMsg object
	 * @return the DistNetwork object
	 */
	@Override
	public DistNetwork map2Model(GFormContainer editNet) {
		// Parse the AcscNet xml object into a AcscNet form object
		DistNetwork distNet = DistFormDataMapperImpl
				.createDistNet((GNetForm) editNet.getGNetForm());

		List busList = editNet.getBusFormList();
		for (int i = 0; i < busList.size(); i++) {
			// For each AcscBus xml object, parse for an AcscBus form object
			GBusForm busForm = (GBusForm) busList.get(i);
			DistFormDataMapperImpl.addBusForm2Net(busForm, distNet);
		}

		List branchList = editNet.getBranchFormList();
		for (int i = 0; i < branchList.size(); i++) {
			// For each AcscBranch xml object, parse for an AcscBranch form
			// object
			GBranchForm branchForm = (GBranchForm) branchList.get(i);
			DistFormDataMapperImpl.addBranchForm2Net(branchForm, distNet);
			// System.out.println("\nBranch info, #:" + (i+1));
			// System.out.println(formBranch.toString());
		}

		// populate the FaultNet with initial data.
		distNet.createAcscNetData();

		distNet.getFaultNet().setLfDataLoaded(true);
		distNet.getFaultNet().setScDataLoaded(true);
		/*
		 * the checkData() need to be called at high level if
		 * (!distNet.getFaultNet().checkData(msg)) {
		 * msg.sendErrorMsg("AcscAdjNetwork data ckeck error, \n" +
		 * distNet.toString()); throw new
		 * InterpssRuntimeException("AcscAdjNetwork data ckeck error, \n" +
		 * distNet.toString()); }
		 */
		return distNet;
	}

	/**
	 * Create a empty DistNetwork object based on the formNet object. Only
	 * network level attributes are set at this stage. There is no bus or branch
	 * in the net yet.
	 * 
	 * @param formData
	 *            a DistNetForm object from the EditorJGraph
	 * @return the DistNetwork ojbect
	 */
	private static DistNetwork createDistNet(GNetForm netForm) {
		DistNetwork net = DistObjectFactory.createDistNetwork();
		DistNetData netData = netForm.getDistNetData();

		BaseFormDataMapperImpl.setBaseNetInfo(netForm, net);

		// multi-point load data
		// if loadSchedulePoints = 0, no load schedule analysis
		net.getLoadScheduleData().setSchedulePoints(netData.getLoadSchedulePoints());
		net.getLoadScheduleData().setSchedulePeriodLength(
				netData.getLoadSchedulePeriodLength());
		net.getLoadScheduleData().setSchedulePeriodUnit(
				netData.getLoadSchedulePeriodUnit());

		// multi-point SC data
		net.setScStd(netData.getScStd().equals(DistNetData.ScStd_Generic) ? ScStanderd.GENERIC
						: (netData.getScStd().equals(DistNetData.ScStd_ANSI) ? ScStanderd.ANSI
								: ScStanderd.IEC));

		java.util.List plist = netData.getScPointList();
		for (int i = 0; i < plist.size(); i++) {
			ScPointData row = (ScPointData) plist.get(i);
			if (row.getEnable()) {
				ScPointType p = new ScPointType(row.getName(), row
						.getDescription(), row.getEnable());
				net.getScPointNetData().getScPointList().add(p);
			}
		}
		return net;
	}

	/**
	 * Create DistBus object basred on the DisBusForm object, containing a
	 * DistBusData object and add it to the DistNetwork object
	 * 
	 * @param formData
	 *            a BusForm object containing DistBusData object
	 * @param distNet
	 *            the DistNetwork object
	 * @return the DistBus object
	 */
	private static DistBus addBusForm2Net(GBusForm form, DistNetwork distNet) {
		DistBus bus = DistObjectFactory.createDistBus(form.getId(), distNet);
		DistBusData busData = form.getDistBusData();

		BaseFormDataMapperImpl.setBaseBusInfo(form, bus, distNet);
		// Utility | Generator | SynMotor | IndMotor | MixedLoad |
		// Non-Contribute
		bus.setBusCode(busData.getBusCode().equals(
						DistBusData.BusCode_Utility) ? DistBusCode.UTILITY
						: (busData.getBusCode().equals(
								DistBusData.BusCode_Generator) ? DistBusCode.GENERATOR
								: (busData.getBusCode().equals(
										DistBusData.BusCode_SynMotor) ? DistBusCode.SYN_MOTOR
										: (busData.getBusCode().equals(
												DistBusData.BusCode_IndMotor) ? DistBusCode.IND_MOTOR
												: (busData.getBusCode().equals(
																DistBusData.BusCode_MixedLoad) ? DistBusCode.MIXED_LOAD
														: DistBusCode.NON_CONTRIBUTE)))));
		bus.setVoltageMag(busData.getVoltage());
		bus.setVMagUnit(UnitType.toUnit(busData.getVoltageUnit()));
		bus.setVoltageAng(busData.getVAngle());
		bus.setVAngUnit(UnitType.toUnit(busData.getVAngleUnit()));
		bus.setMvaRating3P(busData.getMvaRating3P());
		bus.setMvaRating1P(busData.getMvaRating1P());
		bus.setMvaRatingUnit(UnitType.toUnit(busData.getMvaRatingUnit()));
		bus.setX_R3P(busData.getX_r3P());
		bus.setX_R1P(busData.getX_r1P());
		bus.setRatedVoltage(busData.getRatedVolt());
		bus.setRatedVUnit(UnitType.toUnit(busData.getRatedVoltUnit()));
		bus.setBusRating(busData.getBusRating());
		bus.setBusRatingUnit(UnitType.toUnit(busData.getBusRatingUnit()));
		bus.setPFactor(busData.getPFactor());
		bus.setPfUnit(UnitType.toUnit(busData.getPFactorUnit()));
		bus.setEff(busData.getEff());
		bus.setMotorPercent(busData.getMotorPercent());
		bus.setLoading(busData.getLoading());
		for (ZData zdata : busData.getZ1List()) {
			bus.getScPointBusData().getZ1List().add(
					new Complex(zdata.getR(), zdata.getX()));
		}
		bus.setZ1(new Complex(busData.getZ1R(), busData.getZ1X()));
		bus.setZ0(new Complex(busData.getZ0R(), busData.getZ0X()));
		bus.setZ2(new Complex(busData.getZ2R(), busData.getZ2X()));
		bus.setZUnit(UnitType.toUnit(busData.getZUnit()));

		bus.getGrounding().setCode(
				CoreUtilFunc.scGroundType2BusGroundCode(busData.getGround()
						.getCode()));
		bus.getGrounding().setZ(
				new Complex(busData.getGround().getR(), busData.getGround()
						.getX()),
				UnitType.toUnit(busData.getGround().getUnit()),
				bus.getBaseVoltage(), distNet.getBaseKva());

		if (distNet.getLoadScheduleData().getSchedulePoints() > 0
				&& busData.isHasLoadSchedule()) {
			for (int i = 0; i < busData.getLoadScheduleList().size(); i++) {
				/*
				 * LoadScheduleList and PointVoltageList are EList
				 */
				Complex c = busData.getLoadSchedule(i);
				bus.getLoadScheduleData().getLoadScheduleList().add(c);
				bus.getLoadScheduleData().getPointVoltageList().add(
						new Complex(1.0, 0.0));
			}
		}
		//distNet.addBus(bus);
		return bus;
	}

	/**
	 * Create DistBranch object basred on the DisBranchForm object, containing a
	 * DistBranchData object and add it to the DistNetwork object
	 * 
	 * @param formData
	 *            a BranchForm object containing DistBranchData object
	 * @param distNet
	 *            the DistNetwork object
	 * @return the DistBranch object
	 */
	private static DistBranch addBranchForm2Net(GBranchForm branchForm,
			DistNetwork distNet) {
		DistBranch branch = DistObjectFactory.createDistBranch();
		DistBranchData branchData = branchForm.getDistBranchData();

		BaseFormDataMapperImpl.setBaseBranchInfo(branchForm, branch, distNet);

		branch.setBranchCode(branchData.getBranchCode().equals(
						IGBranchForm.DistBranchCode_Feeder) ? DistBranchCode.FEEDER
						: (branchData.getBranchCode().equals(
								IGBranchForm.DistBranchCode_Breaker) ? DistBranchCode.BREAKER
								: (branchData.getBranchCode().equals(
										IGBranchForm.DistBranchCode_Xfr) ? DistBranchCode.TRANSFROMER
										: DistBranchCode.W3_TRANSFORMER)));
		branch.setZ1(new Complex(branchData.getZR(), branchData.getZX()));
		branch.setZ0(new Complex(branchData.getZ0R(), branchData.getZ0X()));
		if (branch.getBranchCode() == DistBranchCode.FEEDER
				|| branch.getBranchCode() == DistBranchCode.BREAKER) {
			branch.setZUnit(UnitType.toUnit("Ohm"));
		} else
			branch.setZUnit(UnitType.toUnit(branchData.getZUnit()));
		branch.setHShuntY(new Complex(0.0, branchData.getHalfShuntB()));
		branch.setHShuntY0(new Complex(0.0, branchData.getHalfShuntB0()));
		branch.setShuntYUnit(UnitType.toUnit(branchData.getHalfShuntBUnit()));
		branch.setLength(branchData.getLength());
		branch.setLengthUnit(UnitType.toUnit(branchData.getLengthUnit()));
		branch.setPrimaryTurnRatio(branchData.getXfrTapFromSideTap());
		branch.setSecondaryTurnRatio(branchData.getXfrTapToSideTap());
		branch.setXfrTurnRatioUnit(UnitType.toUnit(branchData.getXfrTapUnit()));
		branch.setXfrRating(branchData.getXfrRating());
		branch.setXfrRatingUnit(UnitType.toUnit(branchData.getXfrRatingUnit()));
		branch.setPrimaryRatedV(branchData.getFromRatedVolt());
		branch.setSecondaryRatedV(branchData.getToRatedVolt());
		branch.setRatedVUnit(UnitType.toUnit(branchData.getRatedVoltUnit()));

		branch.setPrimaryConnect(branchData.getFromXfrConnectData().getCode()
				.equals(XfrConnectData.Code_Wye) ? XFormerConnectCode.WYE
				: XFormerConnectCode.DELTA);
		branch.setSecondaryConnect(branchData.getToXfrConnectData().getCode()
				.equals(XfrConnectData.Code_Wye) ? XFormerConnectCode.WYE
				: XFormerConnectCode.DELTA);
		double baseV = 1.0;
		if (!branchData.getFromXfrConnectData().getGrounding().getUnit()
				.toUpperCase().equals("PU")) {
			baseV = distNet.getBus(branchForm.getFromId()).getBaseVoltage();
		}
		GroundData gdata = branchData.getFromXfrConnectData().getGrounding();
		branch.getPrimaryGrounding().setZ(
				new Complex(gdata.getR(), gdata.getX()),
				UnitType.toUnit(gdata.getUnit()), baseV, distNet.getBaseKva());

		if (!branchData.getToXfrConnectData().getGrounding().getUnit().toUpperCase().equals("PU")) {
			baseV = distNet.getBus(branchForm.getToId()).getBaseVoltage();
		}
		gdata = branchData.getToXfrConnectData().getGrounding();
		branch.getSecondaryGrounding().setCode(
				CoreUtilFunc.scGroundType2BusGroundCode(gdata.getCode()));
		branch.getSecondaryGrounding().setZ(
				new Complex(gdata.getR(), gdata.getX()),
				UnitType.toUnit(gdata.getUnit()), baseV, distNet.getBaseKva());

		distNet.addBranch(branch, branchForm.getFromId(), branchForm.getToId());
		return branch;
	}
}