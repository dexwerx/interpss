/*
 * @(#)RunActUtilFunc.java   
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

package org.interpss.editor.runAct;

import java.util.ArrayList;
import java.util.List;

import org.interpss.display.AcscOutFunc;
import org.interpss.editor.data.proj.ProjData;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.spring.UISpringFactory;
import org.interpss.xml.schema.DStabStudyCaseXmlType;
import org.interpss.xml.schema.RunStudyCaseXmlType;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.aclf.adj.PQBusLimit;
import com.interpss.core.aclf.adj.PSXfrPControl;
import com.interpss.core.aclf.adj.PVBusLimit;
import com.interpss.core.aclf.adj.RemoteQBus;
import com.interpss.core.aclf.adj.RemoteQControlType;
import com.interpss.core.aclf.adj.TapControl;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.core.net.Area;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.reg.IRegulationDevice;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.common.IDStabSimuDatabaseOutputHandler;

public class RunActUtilFunc {
	public static String AllControlDevices = "All Control Devices";

	public static Object[] getFunctionLoadList(AclfNetwork adjNet,
			double tolerance, IPSSMsgHub msg) {
		List<String> list = new ArrayList<String>();
		list.add(AllControlDevices);
		for (Bus b : adjNet.getBusList()) {
			AclfBus bus = (AclfBus)b;
			if (bus.getFunctionLoad() != null) {
				FunctionLoad load = bus.getFunctionLoad();
				if (load.needAdjust(tolerance))
					list.add(load.getId() + " at " + load.getParentBus().getName());
			}
		}
		return list.toArray();
	}

	public static Object[] getPVBusLimitList(AclfNetwork adjNet,
			IPSSMsgHub msg) {
		List<String> list = new ArrayList<String>();
		list.add(AllControlDevices);
		for (Bus b : adjNet.getBusList()) { 
			AclfBus bus = (AclfBus)b;
			if (bus.isPVBusLimit()) {
				PVBusLimit pvLimit = (PVBusLimit)bus.getBusControl();
				if (pvLimit.needAdjust(0.0))
					list.add(pvLimit.getId() + " at " + pvLimit.getParentBus().getName()
							+ "(" + (pvLimit.isActive() ? "on" : "off") + ")");
			}
		}
		return list.toArray();
	}

	public static Object[] getPQBusLimitList(AclfNetwork adjNet,
			IPSSMsgHub msg) {
		List<String> list = new ArrayList<String>();
		list.add(AllControlDevices);
		for (Bus b : adjNet.getBusList()) { 
			AclfBus bus = (AclfBus)b;
			if (bus.isPQBusLimit()) {
				PQBusLimit pqLimit = (PQBusLimit)bus.getBusControl();
				if (pqLimit.needAdjust(0.0))
					list.add(pqLimit.getId() + " at " + pqLimit.getParentBus().getName()
							+ "(" + (pqLimit.isActive() ? "on" : "off") + ")");
			}
		}
		return list.toArray();
	}

	public static Object[] getRemoteQBusList(AclfNetwork adjNet,
			double tolerance, IPSSMsgHub msg) {
		List<String> list = new ArrayList<String>();
		list.add(AllControlDevices);
		for (Bus b : adjNet.getBusList()) { 
			AclfBus bus = (AclfBus)b;
			if (bus.isRemoteQBus()) {
				RemoteQBus reQ = (RemoteQBus)bus.getBusControl();
				if (reQ.needAdjust(tolerance)) {
					if (reQ.getControlType() == RemoteQControlType.BUS_VOLTAGE)
						list.add(reQ.getId() + " at " + reQ.getParentBus().getName()
								+ "-> Bus:" + reQ.getRemoteBus().getId());
					else
						list.add(reQ.getId() + " at " + reQ.getParentBus().getName()
								+ "-> Branch:" + reQ.getRemoteBranch().getId());
				}
			}
		}
		return list.toArray();
	}

	public static Object[] getXfrTapControlList(AclfNetwork adjNet,
			double tolerance, IPSSMsgHub msg) {
		List<String> list = new ArrayList<String>();
		list.add(AllControlDevices);
		for (Branch b : adjNet.getBranchList()) {
			AclfBranch branch = (AclfBranch)b;
			if (branch.isPSXfrPControl()) {
				TapControl xfr = branch.getTapControl();
				if (xfr.needAdjust(tolerance)) {
					list.add(xfr.getId() + " at " + xfr.getParentBranch().getName());
				}
			}
		}
		return list.toArray();
	}

	public static Object[] getPSXfrPControlList(AclfNetwork adjNet,
			double tolerance, IPSSMsgHub msg) {
		List<String> list = new ArrayList<String>();
		list.add(AllControlDevices);
		for (Branch b : adjNet.getBranchList()) {
			AclfBranch branch = (AclfBranch)b;
			if (branch.isPSXfrPControl()) {
				PSXfrPControl psXfr = branch.getPSXfrPControl();
				if (psXfr.needAdjust(tolerance)) {
					list.add(psXfr.getId() + " at "	+ psXfr.getParentBranch().getName());
				}
			}
		}
		return list.toArray();
	}

	public static Object[] getInterareaPControlList(AclfNetwork adjNet,
			IPSSMsgHub msg) {
		List<String> list = new ArrayList<String>();
		list.add(AllControlDevices);
		for (Object obj : adjNet.getAreaList()) {
			Area a = (Area) obj;
			if (a.getRegDeviceList().size() > 0) {
				IRegulationDevice regDevice = (IRegulationDevice) a
						.getRegDeviceList().get(0);
				if (regDevice.needAdjustment(a, adjNet))
					list.add(a.getNumber() + " name: " + a.getName());
			}
		}
		return list.toArray();
	}

	/**
	 * display fault analysis summary
	 * 
	 * @parem faultNet
	 */
	public static void displayAcscSummaryResult(AcscNetwork faultNet, SimpleFaultAlgorithm algo) {
		IOutputTextDialog dialog = UISpringFactory
				.getOutputTextDialog("Short Circuit Analysis Result Summary");
		dialog.display(AcscOutFunc.faultResult2String(faultNet, algo));
	}

	/**
	 * Display Aclf summary if selected by the user
	 * 
	 * @param algo
	 */
	public static void displayAclfSummaryResult(DynamicSimuAlgorithm algo) {
		IOutputTextDialog dialog = UISpringFactory
				.getOutputTextDialog("Loadflow Analysis Info");
		dialog.display(algo);
	}

	/**
	 * Create a DB output handler object for UI based DStab run.
	 * 
	 * @param algo
	 * @return
	 */
	public static IDStabSimuDatabaseOutputHandler createDBOutputHandler(
			DynamicSimuAlgorithm algo) throws Exception {
		return createDBOutputHandler(algo, null);
	}

	/**
	 * Create a DB output handler object. If dstabXmlCase != null, it RecId will
	 * be appended to the db case name.
	 * 
	 * @param algo
	 * @param dstabXmlCase
	 * @return
	 */
	public static IDStabSimuDatabaseOutputHandler createDBOutputHandler(
			DynamicSimuAlgorithm algo, DStabStudyCaseXmlType dstabCase) 
				throws Exception {
		IDStabSimuDatabaseOutputHandler handler = (IDStabSimuDatabaseOutputHandler) algo
				.getSimuOutputHandler();
		IAppSimuContext appSimuCtx = GraphSpringFactory
				.getIpssGraphicEditor().getCurrentAppSimuContext();
		ProjData projData = (ProjData) appSimuCtx.getProjData();
		try {
			// to avoid conflict with StudyCase name, we add " SimuRecord" to
			// the SimuRecord case.
			String casename = "SimuRecord_";
			if (dstabCase != null)
				casename += dstabCase.getRecId();
			else
				casename += projData.getDStabCaseName();
			if (!handler.init(projData.getProjectDbId(), casename))
				return null;
		} catch (Exception e) {
			IpssLogger.logErr(e);
			EditorPluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
					"Error to Create DB SimuRecord",
					e.toString() + "\nPlease contact InterPSS support");
		}
		appSimuCtx.setDbSimuCaseId(handler.getDBCaseId());
		return handler;
	}

	/**
	 * Check DStab algo and DStab net data
	 * 
	 * @param algo
	 * @param msg
	 * @return
	 */
	public static boolean checkDStabSimuData(DynamicSimuAlgorithm algo) {
		if (!algo.checkData()) {
			IpssLogger.getLogger().warning(
					"DStab simulation data checking failed");
			return false;
		}

		// dstab net data changed in the mapping process
		if (!algo.getDStabNet().checkData()) {
			IpssLogger.getLogger()
					.warning("DStab network data checking failed");
			return false;
		}
		return true;
	}

	/**
	 * check if grid computing is enabled for the study case
	 * 
	 * @param xmlStudyCase study case object
	 * @return
	 */
	public static boolean isGridEnabled(RunStudyCaseXmlType xmlStudyCase) {
		return 	GridEnvHelper.isGridEnabled()
				&& xmlStudyCase.getGridRunOption() != null
				&& xmlStudyCase.getGridRunOption().isEnableGridRun();
	}	
}
