/*
 * @(#)AclfRunForm.java   
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

package org.interpss.editor.runAct.ui;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.interpss.display.ContingencyOutFunc;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.grid.GridObjectFactory;
import org.interpss.grid.algo.GridContingencyAnalysis;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.task.singleJob.DStabSingleJobTask;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.grid.result.IRemoteResult;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.spring.UISpringFactory;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.ContingencyAnalysisXmlType;
import org.interpss.xml.schema.GridComputingXmlType;

import com.interpss.SimuObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
import com.interpss.dist.DistBus;
import com.interpss.dist.DistNetwork;
import com.interpss.dist.adpter.DistBusAdapter;
import com.interpss.simu.ISimuCaseRunner;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.aclf.ContingencyAnalysis;
import com.interpss.simu.multicase.aclf.ContingencyAnalysisType;

public class AclfRunForm extends BaseRunForm implements ISimuCaseRunner {
	private AclfStudyCaseXmlType xmlCaseData;
	private GridComputingXmlType xmlGridOpt;
	private ContingencyAnalysisXmlType xmlContingency;
	private boolean contingencyAnalysis = false;
	
	public AclfRunForm() {
	}

	public AclfStudyCaseXmlType getAclfCaseData() {
		return this.xmlCaseData;
	}
	
    public void setXmlCaseData(AclfStudyCaseXmlType data, GridComputingXmlType xmlGridOpt) {
    	this.xmlCaseData = data;
    	this.xmlGridOpt = xmlGridOpt;
    	this.contingencyAnalysis = false;
    }

    public void setXmlCaseData(ContingencyAnalysisXmlType xmlContingency, GridComputingXmlType xmlGridOpt) {
    	this.xmlContingency = xmlContingency;
    	this.xmlGridOpt = xmlGridOpt;
    	this.contingencyAnalysis = true;
    }

    public boolean runCase(SimuContext simuCtx) {
		boolean converge = false;
		if (this.xmlGridOpt.isEnableGridRun()) {
			final AclfNetwork aclfNet = simuCtx.getAclfNet();
			Grid grid = GridEnvHelper.getDefaultGrid();
			if (this.contingencyAnalysis) {
				IpssLogger.getLogger().info("Run Grid contingency analysis");

				try {
					GridContingencyAnalysis analysis = GridObjectFactory
							.createGridContingencyAnalysis(simuCtx.getNetType(), simuCtx.getAclfNet(), grid);
				  	analysis.setLimitRunCase(this.xmlContingency.isLimitRunCases());
				  	if (analysis.isLimitRunCase())
				  		analysis.setMaxRunCase(this.xmlContingency.getMaxRunCases());
					
				  	LoadflowAlgorithm algo = simuCtx.getLoadflowAlgorithm();
					PluginSpringFactory.getXml2LfAlgorithmMapper()
							.map2Model(this.xmlContingency.getDefaultAclfAlgorithm(), algo);
					analysis.perform(algo, ContingencyAnalysisType.N1);
					//System.out.println(analysis.getResult(IRemoteResult.DisplayType_SecViolation));		
					//System.out.println(analysis.getResult(IRemoteResult.DisplayType_SecAssessment));		

					IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Contingency Analysis Info");
					StringBuffer buffer = new StringBuffer();
					buffer.append(analysis.getResult(IRemoteResult.DisplayType_SecViolation));		
					buffer.append(analysis.getResult(IRemoteResult.DisplayType_SecAssessment));		
					dialog.display(buffer);					
				} catch (InterpssException e) {
					PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
							"Grid Aclf Error", e.toString());
					return false;
				}
			}
			else {
				String nodeId = GridEnvHelper.nodeIdLookup(this.xmlGridOpt.getRemoteNodeName());
				DStabSingleJobTask.RemoteNodeId = nodeId;
				GridRunner.MasterNodeId = grid.getLocalNode().getId().toString();
				try {
					RemoteMessageTable result = new GridRunner(
							grid, "InterPSS Grid Aclf Calculation", simuCtx.getLoadflowAlgorithm())
							.executeSingleJobTask(this.xmlGridOpt.getTimeout());
					String str = result.getSerializedAclfNet();
					AclfNetwork adjNet = (AclfNetwork) SerializeEMFObjectUtil.loadModel(str);
					adjNet.rebuildLookupTable();
					simuCtx.setAclfNet(adjNet);
					converge = adjNet.isLfConverged();
					if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
						IOutputTextDialog dialog = UISpringFactory
								.getOutputTextDialog("Loadflow Analysis Run by Remote "
										+ this.xmlGridOpt.getRemoteNodeName());
						dialog.display(adjNet);
					}
				} catch (GridException e) {
					PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
							"Grid Aclf Error", e.toString());
					return false;
				}
			}
			simuCtx.setAclfNet(aclfNet);
		} else {
			if (this.contingencyAnalysis) {
				IpssLogger.getLogger().info("Run contingency analysis");
				
			  	ContingencyAnalysis analysis = SimuObjectFactory.createContingencyAnalysis(
			  			SimuCtxType.ACLF_NETWORK, simuCtx.getAclfNet());
			  	analysis.setLimitRunCase(this.xmlContingency.isLimitRunCases());
			  	if (analysis.isLimitRunCase())
			  		analysis.setMaxRunCase(this.xmlContingency.getMaxRunCases());
				
			  	LoadflowAlgorithm algo = simuCtx.getLoadflowAlgorithm();
				PluginSpringFactory.getXml2LfAlgorithmMapper()
						.map2Model(this.xmlContingency.getDefaultAclfAlgorithm(), algo);
				analysis.analysis(algo, ContingencyAnalysisType.N1);
				
				IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Contingency Analysis Info");
				dialog.display(ContingencyOutFunc.securityMargin(analysis));					
			}
			else
				converge = runLoadflow(simuCtx.getAclfNet(), simuCtx);
		}
		return converge;
	}

	public void displaySummaryResult(SimuContext simuCtx) {
		if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
			IOutputTextDialog dialog = UISpringFactory
					.getOutputTextDialog("Loadflow Analysis Info");
			dialog.display(simuCtx.getAclfNet());
		}
	}

	private boolean runLoadflow(DistNetwork distNet, SimuContext simuCtx) {
		boolean converge = true;
		if (distNet.getLoadScheduleData().getSchedulePoints() == 0) {
			distNet.setNameplateAclfNetData();
			converge = runLoadflow_internal(distNet.getAcscNet(), simuCtx
					.getLoadflowAlgorithm());

			if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
				IOutputTextDialog dialog = UISpringFactory
						.getOutputTextDialog("Loadflow Analysis Info");
				dialog.display(distNet.getAcscNet());
			}
		} else {
			double loss = 0.0;
			for (int i = 0; i < distNet.getLoadScheduleData().getSchedulePoints(); i++) {
				distNet.setPointAclfNetData(i);
				if (!runLoadflow_internal(distNet.getAcscNet(), simuCtx
						.getLoadflowAlgorithm()))
					converge = false;

				for (Bus b : distNet.getBusList()) {
					DistBus distBus = (DistBus) b;
					DistBusAdapter aBusApt = (DistBusAdapter) distBus
							.getAdapter(DistBusAdapter.class);
					aBusApt.setPointVoltage(distBus.getAcscBus().getVoltage(),
							i);
				}
			}

			distNet.getLoadScheduleData().setTotalLossKwHr(loss);
			if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
				IOutputTextDialog dialog = UISpringFactory
						.getOutputTextDialog("Distribution Loadflow Analysis Info");
				dialog.display(distNet);
			}
		}
		return converge;
	}

	private boolean runLoadflow(AclfNetwork aclfAdjNet, SimuContext simuCtx) {
		boolean converge = runLoadflow_internal(aclfAdjNet, simuCtx.getLoadflowAlgorithm());
		if (!converge)
			IpssLogger.getLogger().warning("Loadflow does not converge!");
		displaySummaryResult(simuCtx);
		return converge;
	}

	private boolean runLoadflow_internal(AclfNetwork aclfAdjNet,
			LoadflowAlgorithm algo) {
		algo.setAclfNetwork(aclfAdjNet);
		PluginSpringFactory.getXml2LfAlgorithmMapper()
				.map2Model(this.getAclfCaseData().getAclfAlgorithm(), algo);

		algo.loadflow();

		return aclfAdjNet.isLfConverged();
	}
}