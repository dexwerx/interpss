/*
 * @(#)ChartManager.java   
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

package org.interpss.editor.chart;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.interpss.chart.dist.LoadScheduleChart;
import org.interpss.chart.dstab.SimpleOneStateChart;
import org.interpss.dstab.output.DStabSimuDBRecord;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.cells.BusCell;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.numeric.util.Number2String;
import org.interpss.output.BaseSimuDBRecord;
import org.interpss.output.ISimuRecManager;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.IProjectDataManager;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.core.net.Bus;
import com.interpss.dstab.DStabBus;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.common.DStabOutSymbol;
import com.interpss.dstab.device.ScriptDynamicBusDevice;
import com.interpss.dstab.mach.Machine;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.spring.CoreCommonSpringFactory;

public class ChartManager {
	public static void addPopupMenuAction(JPopupMenu menu, final Object cell) throws Exception {
		final IAppSimuContext appSimuCtx = GraphSpringFactory
				.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext) appSimuCtx.getSimuCtx();

		if (cell == null
				&& appSimuCtx.getLastRunType() == SimuRunEnum.ScriptsMultiCase) {
			menu.addSeparator();
			IpssLogger.getLogger().info(
					"No element selected for addPopupMenuAction()");
			JMenu simuCaseMenu = new JMenu("Select SimuCase");
			menu.add(simuCaseMenu);
			String[] caseIdList = PluginSpringFactory.getSimuRecManager()
					.getCaseIdList();
			for (final String str : caseIdList) {
				if (PluginSpringFactory.getSimuRecManager().getDBCaseId(str) != appSimuCtx
						.getDbSimuCaseId()) {
					simuCaseMenu.add(new AbstractAction(str) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
							appSimuCtx.setDbSimuCaseId(PluginSpringFactory
									.getSimuRecManager().getDBCaseId(str));
						}
					});
				}
			}
		} else if (cell instanceof BusCell) {
			menu.addSeparator();
			final IGBusForm bus = ((BusCell) cell).getBusForm();
			if (simuCtx.getNetType() == SimuCtxType.DISTRIBUTE_NET
					&& simuCtx.getDistNet().getLoadScheduleData()
							.getSchedulePoints() > 0
					&& appSimuCtx.isLfConverged()) {
				menu.add(new AbstractAction("Plot Load Curve") {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						ChartManager.chartBusLoadCurve(bus.getId());
					}
				});
			} 
			if (simuCtx.getNetType() == SimuCtxType.DSTABILITY_NET) {
				int caseId = appSimuCtx.getDbSimuCaseId();
				if (caseId > 0) {
					DStabBus dstabBus = simuCtx.getDStabilityNet().getDStabBus(
							bus.getId());
					if (dstabBus.getMachine() != null) {
						addMachItem2ActionList(menu, dstabBus.getMachine(),
								caseId, simuCtx.getDStabilityNet()
										.getFrequency(), simuCtx
										.getDStabilityNet().getBaseKva());
						addControllerItem2ActionList(menu, dstabBus
								.getMachine(), caseId, "Exciter",
								ISimuRecManager.REC_TYPE_DStabExcStates,
								DStabSimuDBRecord.EXCITER_ID_EXT, simuCtx
										.getDStabilityNet().getBaseKva());
						addControllerItem2ActionList(menu, dstabBus
								.getMachine(), caseId, "Governor",
								ISimuRecManager.REC_TYPE_DStabGovStates,
								DStabSimuDBRecord.GOVERNER_ID_EXT, simuCtx
										.getDStabilityNet().getBaseKva());
						addControllerItem2ActionList(menu, dstabBus
								.getMachine(), caseId, "Stabilizer",
								ISimuRecManager.REC_TYPE_DStabPssStates,
								DStabSimuDBRecord.STABILIZER_ID_EXT, simuCtx
										.getDStabilityNet().getBaseKva());
					} else if (dstabBus.getScriptDynamicBusDevice() != null) {
						addDynamicBusDeviceItem2ActionList(menu, dstabBus
								.getScriptDynamicBusDevice(), caseId, simuCtx
								.getDStabilityNet());
					}
					/*
					else if (dstabBus.getScriptDBusDevice() != null) {
					    addScriptDBusDeviceItem2ActionList(menu, dstabBus.getScriptDBusDevice(), caseId, simuCtx.getDStabilityNet());
					}
					 */
					else {
						addBusItem2ActionList(menu, dstabBus, caseId, simuCtx
								.getDStabilityNet().getBaseKva());
					}
				}
			}
		}
	}

	//===========================================
	//  DStab Plot Functions
	//===========================================

	private static void addMachItem2ActionList(JPopupMenu menu,
			final Machine mach, final int caseId, double baseFreq,
			double baseKva) {
		JMenu machStateMenu = new JMenu("Machine State");
		menu.add(machStateMenu);
		Object[] stateList = getStatesNameList(caseId, mach.getId(),
				ISimuRecManager.REC_TYPE_DStabMachineStates);
		for (int i = 0; i < stateList.length; i++) {
			final String yLabel = (String) stateList[i];
			final String yDataLabel = getMachDataLabel(mach, yLabel, baseFreq,
					baseKva);
			machStateMenu.add(new AbstractAction("Plot Machine State - "
					+ yLabel) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					plotStateCurve(caseId, mach.getId(), yLabel, yDataLabel,
							ISimuRecManager.REC_TYPE_DStabMachineStates);
				}
			});
		}
		machStateMenu.add(new AbstractAction("Machine State Table Output") {
			private static final long serialVersionUID = 1L;
			ISimuRecManager simuRecManager = PluginSpringFactory
					.getSimuRecManager();

			public void actionPerformed(ActionEvent e) {
				List<BaseSimuDBRecord> machRecList = null;
				try {
					simuRecManager.getSimuRecList(caseId,
							ISimuRecManager.REC_TYPE_DStabMachineStates, mach
									.getId(),
							IProjectDataManager.CaseType_DStabSimuRec);
				} catch (Exception ex) {
					IpssLogger.logErr(ex);
					PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
							"Error to GetSimuRecList",
							ex.toString()
									+ "\n Please contact InterPSS support");
				}
				if (machRecList != null && machRecList.size() > 0) {
					try {
						IAppSimuContext appSimuCtx = GraphSpringFactory
									.getIpssGraphicEditor().getCurrentAppSimuContext();
						if (appSimuCtx.isSimuNetDataDirty()) {
							CoreCommonSpringFactory.getIpssMsgHub().sendWarnMsg(
									"The SimuNetwork object is dirty. You may want to re-run the analysis");
						}
					} catch (Exception ex) {
						IpssLogger.logErr(ex);
					}						
					IOutputTextDialog dialog = UISpringFactory
							.getOutputTextDialog("Machine State Output");
					dialog.display(machRecList);
				}
			}
		});
	}

	private static void addControllerItem2ActionList(JPopupMenu menu,
			final Machine mach, final int caseId, String name,
			final String recType, final String idExt, double baseKva) {
		JMenu excStateMenu = new JMenu(name + " State");
		menu.add(excStateMenu);
		Object[] stateList = getStatesNameList(caseId, mach.getId() + idExt,
				recType);
		for (int i = 0; i < stateList.length; i++) {
			final String yLabel = (String) stateList[i];
			String str = "";
			if (recType.equals(ISimuRecManager.REC_TYPE_DStabExcStates))
				str = getExcDataLabel(mach, yLabel);
			else if (recType.equals(ISimuRecManager.REC_TYPE_DStabGovStates))
				str = getGovDataLabel(mach, yLabel, baseKva);
			else if (recType.equals(ISimuRecManager.REC_TYPE_DStabPssStates))
				str = getPssDataLabel(mach, yLabel);
			final String yDataLabel = str;
			excStateMenu.add(new AbstractAction("Plot " + name + " State - "
					+ yLabel) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					plotStateCurve(caseId, mach.getId() + idExt, yLabel,
							yDataLabel, recType);
				}
			});
		}
	}

	/*
	 public static final String OUT_SYMBOL_MACH_ID 		= "MachId";
	 public static final String OUT_SYMBOL_MACH_ANG 		= "Mach Ang";
	 public static final String OUT_SYMBOL_MACH_SPEED 	= "Mach Speed";
	 public static final String OUT_SYMBOL_MACH_PE 		= "Mach Pe";
	 public static final String OUT_SYMBOL_MACH_PM 		= "Mach Pm";
	 public static final String OUT_SYMBOL_MACH_Q 		= "Mach Q";
	 public static final String OUT_SYMBOL_MACH_E 		= "Mach E";
	 public static final String OUT_SYMBOL_MACH_EQ1 		= "Mach Eq1";
	 public static final String OUT_SYMBOL_MACH_EQ11 	= "Mach Eq11";
	 public static final String OUT_SYMBOL_MACH_ED1 		= "Mach Ed1";
	 public static final String OUT_SYMBOL_MACH_ED11 	= "Mach Ed11";
	 */
	public static String getMachDataLabel(Machine mach, String state,
			double baseFreq, double baseKva) {
		String id = "Machine Id:" + mach.getId() + ", ";
		String ratedV = Number2String.toStr(mach.getRatedVoltage(), "0.0");
		String rating = Number2String.toStr(mach.getRating()*baseKva/1000.0, "0.0");
		String rpm = Number2String.toStr((int) (2.0 * baseFreq * 60.0 / mach
				.getPoles()));
		String baseV = Number2String.toStr(mach.getDStabBus().getBaseVoltage(), "0.0");
		if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_ANG))
			return id + "(Unit:Deg)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_SPEED))
			return id + "(Unit:PU, RPM:" + rpm + ")";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_PE))
			return id + "(Unit:PU, Rating:" + rating + " mva)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_PM))
			return id + "(Unit:PU, Rating:" + rating + " mva)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_Q))
			return id + "(Unit:PU, Rating:" + rating + " mva)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_E))
			return id + "(Unit:PU, Rated Voltage:" + ratedV + " Volts)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_EQ1))
			return id + "(Unit:PU, Rated Voltage:" + ratedV + " Volts)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_EQ11))
			return id + "(Unit:PU, Rated Voltage:" + ratedV + " Volts)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_ED1))
			return id + "(Unit:PU, Rated Voltage:" + ratedV + " Volts)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_ED11))
			return id + "(Unit:PU, Rated Voltage:" + ratedV + " Volts)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_VMAG))
			return id + "(Unit:PU, Voltage Base:" + baseV + " Volts)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_VANG))
			return id + "(Unit:Deg)";
		else
			return "unknown";
	}

	/*
	public static final String OUT_SYMBOL_EXC_EFD 		= "Exciter Efd";
	 */
	public static String getExcDataLabel(Machine mach, String state) {
		String id = "Machine Id:" + mach.getId() + ", ";
		String ratedV = Number2String.toStr(mach.getRatedVoltage(), "0.0");
		if (state.equals(DStabOutSymbol.OUT_SYMBOL_EXC_EFD))
			return id + "(Unit:PU, Rated Voltage:" + ratedV + " Volts)";
		else
			return "unknown";
	}

	/*
	public static final String OUT_SYMBOL_GOV_PM 		= "Governor Pm";
	 */
	public static String getGovDataLabel(Machine mach, String state,
			double baseKva) {
		String id = "Machine Id:" + mach.getId() + ", ";
		String rating = Number2String.toStr(mach.getRating()*baseKva/1000.0, "0.0");
		if (state.equals(DStabOutSymbol.OUT_SYMBOL_GOV_PM))
			return id + "(Unit:PU, Rating:" + rating + " mva)";
		else
			return "unknown";
	}

	/*
	public static final String OUT_SYMBOL_PSS_VS 		= "PSS Vs";
	 */
	public static String getPssDataLabel(Machine mach, String state) {
		String id = "Machine Id:" + mach.getId() + ", ";
		String ratedV = Number2String.toStr(mach.getRatedVoltage(), "0.0");
		if (state.equals(DStabOutSymbol.OUT_SYMBOL_PSS_VS))
			return id + "(Unit:PU, Rated Voltage:" + ratedV + " Volts)";
		else
			return "unknown";
	}

	/*
	public static final String OUT_SYMBOL_BUS_VMAG 		= "Bus Volt Mag";
	public static final String OUT_SYMBOL_BUS_VANG 		= "Bus Volt Ang";
	public static final String OUT_SYMBOL_BUS_PLOAD 	= "Bus Load P";
	public static final String OUT_SYMBOL_BUS_QLOAD 	= "Bus Load Q";
	 */
	public static String getBusDataLabel(Bus bus, String state, double baseKva) {
		String id = "Bus Id:" + bus.getId() + ", ";
		String baseV = Number2String.toStr(bus.getBaseVoltage(), "0.0");
		String baseMva = Number2String.toStr(baseKva/1000.0, "0.0");
		if (state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_VMAG))
			return id + "(Unit:PU, Voltage Base:" + baseV + " Volts)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_VANG))
			return id + "(Unit:Deg)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_PLOAD))
			return id + "(Unit:PU, Base Kva:" + baseMva + " mva)";
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_QLOAD))
			return id + "(Unit:PU, Base Kva:" + baseMva + " mva)";
		else
			return "unknown";
	}

	private static double getAutoRangeMinimumSize(String state) {
		if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_ANG)
				|| state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_VANG))
			return 10.0;
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_SPEED)
				|| state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_FREQ))
			return 0.2;
		else if (state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_PE)
				|| state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_PM)
				|| state.equals(DStabOutSymbol.OUT_SYMBOL_MACH_Q)
				|| state.equals(DStabOutSymbol.OUT_SYMBOL_GOV_PM)
				|| state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_PLOAD)
				|| state.equals(DStabOutSymbol.OUT_SYMBOL_BUS_QLOAD))
			return 0.2;
		return 1.0;
	}

	private static void addBusItem2ActionList(JPopupMenu menu, final Bus bus,
			final int caseId, double baseKva) {
		JMenu busStateMenu = new JMenu("Bus Variable");
		menu.add(busStateMenu);
		Object[] stateList = getStatesNameList(caseId, bus.getId(),
				ISimuRecManager.REC_TYPE_DStabBusStates);
		for (int i = 0; i < stateList.length; i++) {
			final String yLabel = (String) stateList[i];
			final String yDataLabel = getBusDataLabel(bus, yLabel, baseKva);
			busStateMenu
					.add(new AbstractAction("Plot Bus Variable - " + yLabel) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
							plotStateCurve(caseId, bus.getId(), yLabel,
									yDataLabel,
									ISimuRecManager.REC_TYPE_DStabBusStates);
						}
					});
		}
	}

	private static void addDynamicBusDeviceItem2ActionList(JPopupMenu menu,
			final ScriptDynamicBusDevice device, final int caseId,
			DStabilityNetwork net) {
		JMenu deviceStateMenu = new JMenu("Dynamic Bus Device Variable");
		menu.add(deviceStateMenu);
		Object[] stateList = getStatesNameList(caseId, device.getId(),
				ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates);
		for (int i = 0; i < stateList.length; i++) {
			final String yLabel = (String) stateList[i];
			final String yDataLabel = yLabel;
			deviceStateMenu.add(new AbstractAction(
					"Plot Dynamic Bus Device Variable - " + yLabel) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					plotStateCurve(caseId, device.getId(), yLabel, yDataLabel,
							ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates);
				}
			});
		}
	}

	/*
	private static void addScriptDBusDeviceItem2ActionList(JPopupMenu menu, final ScriptingDBusDevice device, final int caseId, DStabilityNetwork net) {
		JMenu deviceStateMenu = new JMenu("Script DBus Device Variable");
		menu.add(deviceStateMenu);
		Object[] stateList = getStatesNameList(caseId, device.getId(), ISimuRecManager.REC_TYPE_DStabScripDBusDeviceStates);
		for (int i = 0; i < stateList.length; i++) {
			final String yLabel = (String)stateList[i];
			final String yDataLabel = yLabel;
			deviceStateMenu.add(new AbstractAction("Plot Script DBus Device Variable - " + yLabel) {
				public void actionPerformed(ActionEvent e) {
				    plotStateCurve(caseId, device.getId(), yLabel, yDataLabel, ISimuRecManager.REC_TYPE_DStabScripDBusDeviceStates);
				}
			});
		}
	}
	 */
	/**
	 * Get plot element (Machine, Bus) state name list
	 * 
	 * @param caseId simulation case id
	 * @param elemId element id
	 * @param recType element record type
	 * @return a list of names
	 */
	public static Object[] getStatesNameList(int caseId, String elemId,
			String recType) {
		ISimuRecManager simuRecManager = PluginSpringFactory.getSimuRecManager();
		List<BaseSimuDBRecord> elemRecList = null;
		try {
			elemRecList = simuRecManager.getSimuRecList(caseId, recType,
					elemId, IProjectDataManager.CaseType_DStabSimuRec);
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
			PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
					"Error to GetSimuRecList form DB",
					ex.toString() + "\n Please contact InterPSS support");
			return null;
		}

		if (elemRecList != null && elemRecList.size() > 0) {
			DStabSimuDBRecord elemRec = (DStabSimuDBRecord) elemRecList.get(0);
			Hashtable<String, String> elemStates = StringUtil
					.parseStr2Hashtable(elemRec.getSimuRec());
			elemStates.remove(DStabOutSymbol.OUT_SYMBOL_TIME);
			if (recType.equals(ISimuRecManager.REC_TYPE_DStabMachineStates))
				elemStates.remove(DStabOutSymbol.OUT_SYMBOL_MACH_ID);
			else if (recType.equals(ISimuRecManager.REC_TYPE_DStabBusStates))
				elemStates.remove(DStabOutSymbol.OUT_SYMBOL_BUS_ID);
			else if (recType
					.equals(ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates)) {
				elemStates.remove(DStabOutSymbol.OUT_SYMBOL_BUS_DEVICE_ID);
				elemStates.remove(DStabOutSymbol.OUT_SYMBOL_BUS_ID);
			}
			return elemStates.keySet().toArray();
		}
		return new Object[0];
	}

	/**
	 * Plot state curve
	 * 
	 * @param caseId simulation case id
	 * @param elemId element id
	 * @param recType element record type
	 * @param yLabel state name
	 */
	public static void plotStateCurve(int caseId, String elemId, String yLabel,
							String yDataLabel, String recType) {
		try {
			IAppSimuContext appSimuCtx = GraphSpringFactory
			.getIpssGraphicEditor().getCurrentAppSimuContext();
			if (appSimuCtx.isSimuNetDataDirty()) {
				CoreCommonSpringFactory
						.getIpssMsgHub()
						.sendWarnMsg(
								"The SimuNetwork object is dirty. Network data may have been modified. You may want to re-run the analysis.");
			}
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
		}	
		
		ISimuRecManager simuRecManager = PluginSpringFactory.getSimuRecManager();
		List<BaseSimuDBRecord> elemRecList = null;
		try {
			elemRecList = simuRecManager.getSimuRecList(caseId, recType,
					elemId, IProjectDataManager.CaseType_DStabSimuRec);
			/*
			for (BaseSimuDBRecord rec : elemRecList) {
				System.out.println(((DStabSimuDBRecord)rec).getSimuTime());
			}
			 */
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
			PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
					"Error to GetSimuRecList from DB",
					ex.toString() + "\n Please contact InterPSS support");
			return;
		}

		IpssLogger.getLogger().info(
				"CaseId, elemId, state, recType: " + caseId + ", " + elemId
						+ ", " + yLabel + ", " + recType);
		IpssLogger.getLogger().info(
				"Plot state: " + yLabel + ", # of records "
						+ elemRecList.size());

		SimpleOneStateChart plot = null;
		if (recType.equals(ISimuRecManager.REC_TYPE_DStabMachineStates))
			plot = new SimpleOneStateChart(GraphSpringFactory
					.getIpssGraphicEditor().getFrame(), true,
					"Machine State Curve Plot");
		else if (recType.equals(ISimuRecManager.REC_TYPE_DStabExcStates))
			plot = new SimpleOneStateChart(GraphSpringFactory
					.getIpssGraphicEditor().getFrame(), true,
					"Exciter State Curve Plot");
		else if (recType.equals(ISimuRecManager.REC_TYPE_DStabGovStates))
			plot = new SimpleOneStateChart(GraphSpringFactory
					.getIpssGraphicEditor().getFrame(), true,
					"Governor State Curve Plot");
		else if (recType.equals(ISimuRecManager.REC_TYPE_DStabPssStates))
			plot = new SimpleOneStateChart(GraphSpringFactory
					.getIpssGraphicEditor().getFrame(), true,
					"Stabilizer State Curve Plot");
		else if (recType.equals(ISimuRecManager.REC_TYPE_DStabBusStates))
			plot = new SimpleOneStateChart(GraphSpringFactory
					.getIpssGraphicEditor().getFrame(), true,
					"Bus Voltage State Curve Plot");
		else if (recType
				.equals(ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates))
			plot = new SimpleOneStateChart(GraphSpringFactory
					.getIpssGraphicEditor().getFrame(), true,
					"Scripting Dynamic Bus Device Curve Plot");

		double[] xdata = new double[elemRecList.size()];
		double[] ydata = new double[elemRecList.size()];
		for (int i = 0; i < elemRecList.size(); i++) {
			DStabSimuDBRecord machRec = (DStabSimuDBRecord) elemRecList.get(i);
			xdata[i] = machRec.getSimuTime();
			Hashtable<String, String> machStates = StringUtil
					.parseStr2Hashtable(machRec.getSimuRec());
			ydata[i] = new Double((String) machStates.get(yLabel))
					.doubleValue();
		}

		// sort data points. They may be out of order in the case of Grid computing
		boolean done = false;
		while (!done) {
			done = true;
			for (int i = 0; i < elemRecList.size() - 1; i++) {
				if (xdata[i] > xdata[i + 1]) {
					double x = xdata[i + 1], y = ydata[i + 1];
					xdata[i + 1] = xdata[i];
					ydata[i + 1] = ydata[i];
					xdata[i] = x;
					ydata[i] = y;
					done = false;
				}
			}
		}
		/*
		for (int i = 0; i < elemRecList.size(); i++) {
		System.out.println("time, y: " + xdata[i] + "," + ydata[i]);
		}
		 */
		plot.setPlotData(yLabel, yDataLabel, xdata, ydata,
				getAutoRangeMinimumSize(yLabel));

		plot.createChart();
		plot.showChart();
	}

	//===========================================
	//  Dist Plot Functions
	//===========================================

	private static void chartBusLoadCurve(String busid) {
		LoadScheduleChart plot = new LoadScheduleChart("Bus Load Schedule");

		try {
			SimuContext simuCtx = (SimuContext) GraphSpringFactory
					.getIpssGraphicEditor().getCurrentAppSimuContext().getSimuCtx();
//			DistNetwork distNet = simuCtx.getDistNet();
//			DistBus distBus = (DistBus) distNet.getBus(busid);
//			int size = distNet.getLoadNetData().getSchedulePoints();
//			double[] vdata = new double[size];
//			for (int i = 0; i < size; i++) {
//				vdata[i] = ((Complex) distBus.getLoadBusData()
//							.getPointVoltageList().get(i)).abs();
//			}
//			plot.setVoltageData("voltage", vdata);
//
//			double[] pdata = new double[size];
//			double[] qdata = new double[size];
//			for (int i = 0; i < size; i++) {
//				pdata[i] = ((Complex) distBus.getLoadBusData()
//					.getLoadScheduleList().get(i)).getReal();
//				qdata[i] = ((Complex) distBus.getLoadBusData()
//					.getLoadScheduleList().get(i)).getImaginary();
//			}
//			plot.setPQData("Load P%", "Load Q%", pdata, qdata);
//
//			plot.createChart();
//			plot.showChart();
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
		}		
	}
}
