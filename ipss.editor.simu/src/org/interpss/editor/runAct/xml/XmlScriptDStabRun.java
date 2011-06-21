/*
 * @(#)XmlScriptDStabRun.java   
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
 * @Date 01/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.editor.runAct.xml;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.interpss.dstab.output.DatabaseSimuOutputHandler;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.GraphSpringAppContext;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.runAct.RunActUtilFunc;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.task.singleJob.DStabSingleJobTask;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.grid.msg.DStabGridMessageRouter;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.schema.DStabStudyCaseXmlType;
import org.interpss.xml.schema.InterPSSXmlType;
import org.interpss.xml.schema.RunDStabStudyCaseXmlType;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabObjectFactory;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.common.IDStabSimuDatabaseOutputHandler;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;
import com.interpss.simu.multicase.MultiStudyCase;
import com.interpss.simu.multicase.dstab.DStabStudyCase;
import com.interpss.spring.CoreCommonSpringCtx;
import com.interpss.spring.CoreSpringCtx;

public class XmlScriptDStabRun {
	/**
	 * Run DStab run or run(s) defined in the Xml scripts
	 * 
	 * @param parser
	 *            The InterPSS xml parser object, which contents the Xml Run
	 *            scripts
	 * @param simuCtx
	 *            the SimuContext object
	 * @param msg
	 * @return
	 */
	public static boolean runDStab(InterPSSXmlType ipssXmlDoc, SimuContext simuCtx, IPSSMsgHub msg) {
		// get the RunStudyCase object, root level modification has already
		// applied
		// to the DStabNet object
		RunDStabStudyCaseXmlType xmlRunCase = ipssXmlDoc.getRunStudyCase().getStandardRun().getRunDStabStudyCase();

		if (xmlRunCase != null) {
			IAppSimuContext appSimuCtx = null;
			try {
				appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
			} catch (Exception ex) {
				IpssLogger.logErr(ex);
				return false;
			}
			
			DStabilityNetwork dstabNet = simuCtx.getDStabilityNet();
			DStabStudyCaseXmlType xmlDefaultCase = xmlRunCase.getDefaultDStabStudyCase(); 

			// single run case
			if (xmlRunCase.getDStabStudyCaseList().getDStabStudyCase().size() == 1) {
				appSimuCtx.setLastRunType(SimuRunEnum.DStab);

				// get the run case info defined in the Xml scripts
				DStabStudyCaseXmlType xmlCase = xmlRunCase.getDStabStudyCaseList().getDStabStudyCase().get(0);
				// config the DStabAlgo object, including apply case-level
				// modification to the DStabNet object
				DynamicSimuAlgorithm dstabAlgo = DStabObjectFactory
						.createDynamicSimuAlgorithm(dstabNet, 
								new DatabaseSimuOutputHandler(), msg);

				if (xmlCase == null) {
					if (xmlDefaultCase == null) {
						msg.sendErrorMsg("No DStab case defined");
						return false;
					}
					xmlCase = xmlDefaultCase;
				}
				if (xmlCase.getModification() != null) {
					PluginSpringCtx.getModXml2NetMapper()
							.map2Model(xmlCase.getModification(), dstabNet);
				}
				if (!configDStaAlgo(dstabAlgo, xmlCase, msg))
					return false;

				if (RunActUtilFunc.isGridEnabled(ipssXmlDoc.getRunStudyCase())) {
					Grid grid = GridEnvHelper.getDefaultGrid();
					// get any remote node to distribute the simulation job
					DStabSingleJobTask.RemoteNodeId = GridEnvHelper
							.getAnyRemoteNodeId();
					// set the master node id, so that remote msg could be sent
					// to the master node
					GridRunner.MasterNodeId = grid.getLocalNode().getId().toString();

					/*
					 * The simuMsg sending from remote node to the master node
					 * will be routed by the router to msg object, excepting
					 * DStab simu msg, which will be handled by the
					 * dbSimuDataHandler object
					 */
					DStabGridMessageRouter msgRouter = new DStabGridMessageRouter(msg);
					grid.addMessageListener(msgRouter);
					msgRouter.addDStabSimuDbOutputHandler(dstabAlgo
							.getSimuOutputHandler());

					try {
						// run DStab simu at a remote node
						RemoteMessageTable result = new GridRunner(
										grid,
										"InterPSS Transient Stability Simulation",
										dstabAlgo).executeSingleJobTask(ipssXmlDoc.getRunStudyCase().getGridRunOption().getTimeout());
						// init the Net object for plotting purpose.
						dstabNet.initialization();
						// set the DStabNet object back to the SimuCtx
						simuCtx.setDStabilityNet(dstabNet);
						return result.getReturnStatus();
					} catch (GridException e) {
						CoreCommonSpringCtx.getEditorDialogUtil()
								.showErrMsgDialog("Grid DStab Error",
										e.toString());
						return false;
					}
				} else {
					runLocalDStabRun(dstabAlgo, xmlCase, msg);
				}
			} else {
				// Multi-DStab run case
				appSimuCtx.setLastRunType(SimuRunEnum.ScriptsMultiCase);
				PluginSpringCtx.getSimuRecManager().clearDbCaseIdLookup();
				
				DStabGridMessageRouter msgRouter = null;
				if (RunActUtilFunc.isGridEnabled(ipssXmlDoc.getRunStudyCase())) {
					Grid grid = GridEnvHelper.getDefaultGrid();
					GridRunner.MasterNodeId = grid.getLocalNode().getId()
							.toString();
					msgRouter = new DStabGridMessageRouter(msg);
					grid.addMessageListener(msgRouter);
				}

				// save the base case Network model to the netStr
				String netStr = SerializeEMFObjectUtil.saveModel(dstabNet);
				// create a multi-case container
				MultiStudyCase mCaseContainer = SimuObjectFactory
						.createDStabMultiStudyCase(SimuCtxType.DSTABILITY_NET);
				int cnt = 0;
				for (DStabStudyCaseXmlType xmlCase : xmlRunCase.getDStabStudyCaseList().getDStabStudyCase()) {
					// deserialize the base case
					DStabilityNetwork net = (DStabilityNetwork) SerializeEMFObjectUtil.loadModel(netStr);
					net.rebuildLookupTable();
					DynamicSimuAlgorithm dstabAlgo = DStabObjectFactory
							.createDynamicSimuAlgorithm(net, 
									new DatabaseSimuOutputHandler(), msg);
					
					if (xmlCase == null) {
						if (xmlDefaultCase == null) {
							msg.sendErrorMsg("No DStab case defined");
							return false;
						}
						xmlCase = xmlDefaultCase;
					}
					if (xmlCase.getModification() != null) {
						PluginSpringCtx.getModXml2NetMapper()
								.map2Model(xmlCase.getModification(), dstabNet);
					}
					if (!configDStaAlgo(dstabAlgo, xmlCase, msg))
						return false;

					// net.id is used to retrieve study case info at remote
					// node. so we need to sure net.id and studyCase.id are
					// the same for Grid computing.
					net.setId(xmlCase.getRecId());
					try {
						DStabStudyCase studyCase = SimuObjectFactory.createDStabStudyCase(xmlCase.getRecId(),
								xmlCase.getRecName(), ++cnt, mCaseContainer);
						if (RunActUtilFunc.isGridEnabled(ipssXmlDoc.getRunStudyCase())) {
							// if Grid computing, save the net and algo objects
							// to the study case object
							studyCase.setNetModelString(SerializeEMFObjectUtil
									.saveModel(net));
							studyCase
									.setAclfAlgoModelString(SerializeEMFObjectUtil
											.saveModel(dstabAlgo
													.getAclfAlgorithm()));
							studyCase
									.setDstabAlgoModelString(SerializeEMFObjectUtil
											.saveModel(dstabAlgo));
							msgRouter.addDStabSimuDbOutputHandler(dstabAlgo
									.getSimuOutputHandler());
						} else {
							// if not grid computing, perform DStab run
							runLocalDStabRun(dstabAlgo, xmlCase, msg);
							studyCase.setDesc("DStab by Local Node");
						}
					} catch (Exception e) {
						CoreCommonSpringCtx.getEditorDialogUtil()
								.showErrMsgDialog("Study Case Creation Error",
										e.toString());
						return false;
					}
				}

				if (RunActUtilFunc.isGridEnabled(ipssXmlDoc.getRunStudyCase())) {
					Grid grid = GridEnvHelper.getDefaultGrid();
					try {
						RemoteMessageTable[] objAry = new GridRunner(
										grid, "InterPSS Transient Stability Simulation",
										mCaseContainer).executeMultiJobSplitTask(ipssXmlDoc.getRunStudyCase().getGridRunOption().getTimeout());
						for (RemoteMessageTable result : objAry) {
							Boolean b = result.getReturnStatus();
							if (!b.booleanValue()) {
								CoreCommonSpringCtx
										.getEditorDialogUtil()
										.showWarnMsgDialog("Grid DStab Error",
												"Please check InterPSS log file for details");
								return false;
							}
						}
					} catch (GridException e) {
						CoreCommonSpringCtx.getEditorDialogUtil()
								.showErrMsgDialog("Grid DStab Error",
										e.toString());
						return false;
					}
				}
				// init the Net object for plotting purpose.
				dstabNet.initialization();
				// set the DStabNet object back to the SimuCtx
				simuCtx.setDStabilityNet(dstabNet);
			}
		} else {
			CoreCommonSpringCtx.getEditorDialogUtil().showErrMsgDialog(
					"Invalid Xml", "runDStabStudyCase element not defined");
			return false;
		}
		return true;
	}

	private static boolean configDStaAlgo(DynamicSimuAlgorithm dstabAlgo,
			DStabStudyCaseXmlType dstabCase, IPSSMsgHub msg) {
		// map the Xml study case data to dstabAlgo, including modification to
		// the network model data
		PluginSpringCtx.getXml2DStabAlgorithmMapper()
				.map2Model(dstabCase, dstabAlgo);
		
		if (!RunActUtilFunc.checkDStabSimuData(dstabAlgo, msg))
			return false; // if something is wrong, we stop running here

		// create a DB handler to store simulation result, a Db simu case will
		// be created if not existed.
		// to get db case id: dstabDbHandler.getDBCaseId()
		IDStabSimuDatabaseOutputHandler dstabDbHandler = null;
		try {
			dstabDbHandler = RunActUtilFunc.createDBOutputHandler(dstabAlgo, dstabCase);
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
		}
		if (dstabDbHandler == null)
			return false;
		
		// correlate net.id, case.id and dbCaseId
		dstabAlgo.getDStabNet().setId(dstabCase.getRecId());
		PluginSpringCtx.getSimuRecManager().addDBCaseId(dstabCase.getRecId(), dstabDbHandler
				.getDBCaseId());

		// transfer output variable filter info to the DStabAlgo object, which
		// then
		// will be carried by the object to the remote grid node, in case of
		// grid computing
		dstabDbHandler.setOutputFilter(dstabAlgo.isOutputFiltered());
		if (dstabDbHandler.isOutputFilter())
			dstabDbHandler.setOutputVarIdList(dstabAlgo.getOutputVarIdList());

		// set the DB handler to the algo object
		dstabAlgo.setSimuOutputHandler(dstabDbHandler);
		return true;
	}

	private static boolean runLocalDStabRun(DynamicSimuAlgorithm dstabAlgo,
			DStabStudyCaseXmlType dstabCase, IPSSMsgHub msg) {
		dstabAlgo.getDStabNet().setNetChangeListener(
				CoreSpringCtx.getNetChangeHandler());

		LoadflowAlgorithm aclfAlgo = dstabAlgo.getAclfAlgorithm();
		aclfAlgo.loadflow();
		if (dstabCase.getAclfAlgorithm().isDisplaySummary())
			RunActUtilFunc.displayAclfSummaryResult(dstabAlgo);
		if (!dstabAlgo.getDStabNet().isLfConverged()) {
			msg
					.sendWarnMsg("Loadflow diverges, please make sure that loadflow converges before runing the transient stability simulation");
			return false;
		}

		if (dstabAlgo.initialization()) {
			dstabAlgo.performSimulation(msg);
		}
		return true;
	}
}
