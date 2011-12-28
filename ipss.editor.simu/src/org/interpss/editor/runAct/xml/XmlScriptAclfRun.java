/*
 * @(#)XmlScriptAclfRun.java   
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
import org.interpss.editor.runAct.RunActUtilFunc;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.job.GridAclfReJob;
import org.interpss.grid.gridgain.task.singleJob.DStabSingleJobTask;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.grid.result.IRemoteResult;
import org.interpss.grid.result.RemoteResultFactory;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.spring.UISpringFactory;
import org.interpss.xml.PreventiveRuleHanlder;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.GridAclfOptionXmlType;
import org.interpss.xml.schema.InterPSSXmlType;
import org.interpss.xml.schema.ReturnStudyCaseDataType;
import org.interpss.xml.schema.RuleBaseXmlType;
import org.interpss.xml.schema.RunAclfStudyCaseXmlType;
import org.interpss.xml.schema.RunStudyCaseXmlType;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.mapper.Modification2ModelMapper;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.ReturnRemoteCaseOpt;
import com.interpss.simu.multicase.aclf.AclfMultiStudyCase;
import com.interpss.simu.multicase.aclf.AclfStudyCase;

public class XmlScriptAclfRun {
	public static double DefaultUpperVoltageLimit = 1.2, DefaultLowerVoltageLimit = 0.8; 
	
	/**
	 * Run Aclf run or run(s) defined in the Xml scripts
	 * 
	 * @param parser The InterPSS xml parser object
	 * @param aclfNet
	 * @param msg
	 * @return
	 */
	public static boolean runAclf(InterPSSXmlType ipssXmlDoc, AclfNetwork aclfNet) {
		RunAclfStudyCaseXmlType xmlRunAclfCase = ipssXmlDoc.getRunStudyCase().getStandardRun().getRunAclfStudyCase();
		if (xmlRunAclfCase == null) {
			PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog("Invalid Xml", "runAclfStudyCase element not defined");
			return false;
		}

		boolean applyRuleBase = ipssXmlDoc.getRunStudyCase().isApplyRuleBase();
		AclfAlgorithmXmlType xmlDefaultAlgo = xmlRunAclfCase.getDefaultAclfAlgorithm(); 
		boolean gridRun = RunActUtilFunc.isGridEnabled(ipssXmlDoc.getRunStudyCase());
		long  timeout = gridRun? ipssXmlDoc.getRunStudyCase().getGridRunOption().getTimeout() : 0;
			
		if (xmlRunAclfCase.getAclfStudyCaseList().getAclfStudyCase().size() == 1) {
			AclfStudyCaseXmlType xmlCase = xmlRunAclfCase.getAclfStudyCaseList().getAclfStudyCase().get(0);
			RuleBaseXmlType ruleBase = ipssXmlDoc.getRunStudyCase().getRuleBase();
			if (!aclfSingleRun(aclfNet, xmlCase, xmlDefaultAlgo, ruleBase,	applyRuleBase, gridRun, timeout));
				return false;
		} 
		else {
			AclfMultiStudyCase mCaseContainer = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_NETWORK);
			// save the base case Network model to the netStr
			mCaseContainer.setBaseNetModelString(SerializeEMFObjectUtil.saveModel(aclfNet));

			if (applyRuleBase) 
				XmlScriptUtilFunc.mapRuleBase(applyRuleBase, mCaseContainer, ipssXmlDoc.getRunStudyCase().getRuleBase());
				
			boolean reJobCreation = gridRun? ipssXmlDoc.getRunStudyCase().getGridRunOption().isRemoteJobCreation() : false;
				
			int cnt = 0;
			for (AclfStudyCaseXmlType xmlCase : xmlRunAclfCase.getAclfStudyCaseList().getAclfStudyCase()) {
				// deserialize the base case, if necessary
				AclfNetwork net = aclfNet;
				if (!gridRun || !reJobCreation) { 
					// only deserialized the network if not the case of remote job creation
					net = (AclfNetwork) SerializeEMFObjectUtil.loadModel(mCaseContainer.getBaseNetModelString());
				    net.rebuildLookupTable();
				}

				LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
				// map to the Algo object including network modification at the study case level
				//IpssMapper mapper = PluginSpringCtx.getIpssXmlMapper();
				if (!XmlScriptUtilFunc.mapAclfStudyCase(xmlCase, algo, xmlDefaultAlgo, reJobCreation))
					return false;

				try {
					AclfStudyCase studyCase = SimuObjectFactory.createAclfStudyCase(xmlCase.getRecId(), 
										xmlCase.getRecName(), ++cnt, mCaseContainer);
					if (gridRun) {
						// if Grid computing, save the Algo object to the study case object
						studyCase.setAclfAlgoModelString(SerializeEMFObjectUtil.saveModel(algo));
						if (reJobCreation && xmlCase.getModification() != null) {
							// persist modification to be sent to the remote grid node
							studyCase.setModification(
									new Modification2ModelMapper().map2Model(xmlCase.getModification()));
						}
					} else {
						// if not grid computing, perform Loadflow for the study case
						algo.loadflow();
						if (applyRuleBase) {
							RuleBaseXmlType ruleBase = ipssXmlDoc.getRunStudyCase().getRuleBase();
							PreventiveRuleHanlder.applyRuleSet2AclfNet(algo, ruleBase, DefaultUpperVoltageLimit, DefaultLowerVoltageLimit);
						}
						studyCase.setDesc("Loadflow by Local Node");
						studyCase.setRemoteReturnStatus(true);
						studyCase.setAclfConverged(algo.getAclfNetwork().isLfConverged());
					}
					studyCase.setId(xmlCase.getRecId());
					studyCase.setName(xmlCase.getRecDesc());
					if (!gridRun || !reJobCreation) {
						net.setId(xmlCase.getRecId());
						// persist the AclfNet object to study case. It contains case level modification for
						// grid computing when remoteJobCreation = false. In the case of non grid computing, it contains Loadflow results
						studyCase.setNetModelString(SerializeEMFObjectUtil.saveModel(net));
					}
				} catch (Exception e) {
					PluginSpringFactory.getEditorDialogUtil()
								.showErrMsgDialog("Study Case Creation Error",
										e.toString());
					return false;
				}
			}

			// if Grid computing, send the MultiCase container to perform
			// remote grid computing
			if (gridRun) {
				Grid grid = GridEnvHelper.getDefaultGrid();
				GridRunner.MasterNodeId = grid.getLocalNode().getId().toString();
					
				setAclfRunOpt(mCaseContainer, ipssXmlDoc.getRunStudyCase());
				try {
					RemoteMessageTable[] objAry = new GridRunner(grid,
										"InterPSS Grid Aclf Calculation", mCaseContainer).executeMultiJobSplitTask(timeout);
					for (RemoteMessageTable result : objAry) {
						IRemoteResult resultHandler = RemoteResultFactory.createHandler(GridAclfReJob.class);
							resultHandler.transferRemoteResult(mCaseContainer, result);
					}
				} catch (GridException e) {
					PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog("Grid Aclf Error",	e.toString());
					return false;
				} 
			}

			// display the simulation results
			IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Loadflow Analysis Info");
			dialog.display(mCaseContainer);
		}
		return true;
	}

	private static boolean aclfSingleRun(AclfNetwork aclfNet, AclfStudyCaseXmlType xmlCase, AclfAlgorithmXmlType xmlDefaultAlgo, 
				RuleBaseXmlType ruleBase, boolean applyRuleBase, boolean gridRun, long timeout) {
		//IpssMapper mapper = PluginSpringCtx.getIpssXmlMapper();
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(aclfNet);
		if (!XmlScriptUtilFunc.mapAclfStudyCase(xmlCase, algo, xmlDefaultAlgo, false))
			return false;

		if (gridRun) {
			Grid grid = GridEnvHelper.getDefaultGrid();
			DStabSingleJobTask.RemoteNodeId = GridEnvHelper.getAnyRemoteNodeId();
			GridRunner.MasterNodeId = grid.getLocalNode().getId().toString();
			try {
				RemoteMessageTable result = new GridRunner(
						grid, "InterPSS Grid Aclf Calculation", algo).executeSingleJobTask(timeout);
				String str = result.getSerializedAclfNet();
				aclfNet = (AclfNetwork) SerializeEMFObjectUtil.loadModel(str);
				aclfNet.rebuildLookupTable();
			} catch (GridException e) {
				PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog("Grid Aclf Error", e.toString());
				return false;
			}
		} else {
			algo.loadflow();
			if (applyRuleBase) {
				PreventiveRuleHanlder.applyRuleSet2AclfNet(algo, ruleBase, DefaultUpperVoltageLimit, DefaultLowerVoltageLimit);
			}
		}

		if (xmlCase.getAclfAlgorithm().isDisplaySummary()) {
			IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Loadflow Analysis Info");
			dialog.display(aclfNet);
		}
		return true;
	}
	
	private static void setAclfRunOpt(AclfMultiStudyCase mCaseContainer, RunStudyCaseXmlType runCase) {
		mCaseContainer.setRemoteJobCreation(runCase.getGridRunOption().isRemoteJobCreation());
		if (runCase.getGridRunOption().getAclfOption() != null) {
			GridAclfOptionXmlType opt = runCase.getGridRunOption().getAclfOption();
			mCaseContainer.getAclfGridOption().setReturnCase(
					opt.getReturnStudyCase()==ReturnStudyCaseDataType.ALL_STUDY_CASE? ReturnRemoteCaseOpt.ALL_STUDY_CASE :
						(opt.getReturnStudyCase()==ReturnStudyCaseDataType.DIVERGED_CASE? ReturnRemoteCaseOpt.DIVERGED_CASE :
							ReturnRemoteCaseOpt.NO_STUDY_CASE));
			mCaseContainer.getAclfGridOption().setCalculateViolation(opt.isCalculateViolation());
			if (opt.getBusVoltagePULimit() != null) {
				mCaseContainer.getAclfGridOption().setBusVoltageUpperLimitPU(opt.getBusVoltagePULimit().getMax());
				mCaseContainer.getAclfGridOption().setBusVoltageLowerLimitPU(opt.getBusVoltagePULimit().getMin());
			}
		}		
	}
}
