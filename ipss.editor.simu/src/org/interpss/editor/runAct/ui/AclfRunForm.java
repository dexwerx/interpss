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
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.UISpringAppContext;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.task.singleJob.DStabSingleJobTask;
import org.interpss.grid.gridgain.util.GridUtil;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.GridComputingXmlType;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
import com.interpss.dist.DistBus;
import com.interpss.dist.DistNetwork;
import com.interpss.dist.adpter.DistBusAdapter;
import com.interpss.simu.ISimuCaseRunner;
import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringCtx;

public class AclfRunForm extends BaseRunForm implements ISimuCaseRunner {
	private AclfStudyCaseXmlType xmlCaseData;
	private GridComputingXmlType xmlGridOpt;
	
	public AclfRunForm() {
	}

	public AclfStudyCaseXmlType getAclfCaseData() {
		return this.xmlCaseData;
	}
	
	/*
	private AclfCaseData aclfCaseData;

	public AclfCaseData getAclfCaseData() {
		return this.aclfCaseData;
	}

	public void setAclfCaseData(AclfCaseData acase) {
		this.aclfCaseData = acase;
	}
*/	
    public void setXmlCaseData(AclfStudyCaseXmlType data, GridComputingXmlType xmlGridOpt) {
    	this.xmlCaseData = data;
    	this.xmlGridOpt = xmlGridOpt;
    }

	public boolean runCase(SimuContext simuCtx, IPSSMsgHub msg) {
		boolean converge = false;
//		if (simuCtx.getNetType() == SimuCtxType.DISTRIBUTE_NET) {
//			converge = runLoadflow(simuCtx.getDistNet(), simuCtx);
//		} else {
			if (this.xmlGridOpt.isEnableGridRun()) {
				Grid grid = GridUtil.getDefaultGrid();
				String nodeId = GridUtil.nodeIdLookup(this.xmlGridOpt.getRemoteNodeName());
				DStabSingleJobTask.RemoteNodeId = nodeId;
				GridRunner.MasterNodeId = grid.getLocalNode().getId().toString();
				try {
					RemoteMessageTable result = new GridRunner(
							grid, "InterPSS Grid Aclf Calculation", simuCtx.getLoadflowAlgorithm())
							.executeTask(this.xmlGridOpt.getTimeout());
					String str = result.getSerializedAclfNet();
					AclfNetwork adjNet = (AclfNetwork) SerializeEMFObjectUtil.loadModel(str);
					adjNet.rebuildLookupTable();
					simuCtx.setAclfNet(adjNet);
					converge = adjNet.isLfConverged();
					if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
						IOutputTextDialog dialog = UISpringAppContext
								.getOutputTextDialog("Loadflow Analysis Run by Remote "
										+ this.xmlGridOpt.getRemoteNodeName());
						dialog.display(adjNet);
					}
				} catch (GridException e) {
					CoreCommonSpringCtx.getEditorDialogUtil().showErrMsgDialog(
							"Grid Aclf Error", e.toString());
					return false;
				}
			} else
				converge = runLoadflow(simuCtx.getAclfNet(), simuCtx);
//		}
		return converge;
	}

	public void displaySummaryResult(SimuContext simuCtx) {
		if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
			IOutputTextDialog dialog = UISpringAppContext
					.getOutputTextDialog("Loadflow Analysis Info");
			dialog.display(simuCtx.getAclfNet());
		}
	}

	private boolean runLoadflow(DistNetwork distNet, SimuContext simuCtx) {
		boolean converge = true;
		if (distNet.getLoadNetData().getSchedulePoints() == 0) {
			distNet.setNameplateAclfNetData();
			converge = runLoadflow_internal(distNet.getAcscNet(), simuCtx
					.getLoadflowAlgorithm());

			if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
				IOutputTextDialog dialog = UISpringAppContext
						.getOutputTextDialog("Loadflow Analysis Info");
				dialog.display(distNet.getAcscNet());
			}
		} else {
			double loss = 0.0;
			for (int i = 0; i < distNet.getLoadNetData().getSchedulePoints(); i++) {
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

			distNet.getLoadNetData().setTotalLossKwHr(loss);
			if (this.xmlCaseData.getAclfAlgorithm().isDisplaySummary()) {
				IOutputTextDialog dialog = UISpringAppContext
						.getOutputTextDialog("Distribution Loadflow Analysis Info");
				dialog.display(distNet);
			}
		}
		return converge;
	}

	private boolean runLoadflow(AclfNetwork aclfAdjNet, SimuContext simuCtx) {
		boolean converge = runLoadflow_internal(aclfAdjNet, simuCtx.getLoadflowAlgorithm());
		if (!converge)
			simuCtx.getMsgHub().sendWarnMsg("Loadflow does not converge!");
		displaySummaryResult(simuCtx);
		return converge;
	}

	private boolean runLoadflow_internal(AclfNetwork aclfAdjNet,
			LoadflowAlgorithm algo) {
		algo.setAclfNetwork(aclfAdjNet);
		PluginSpringCtx.getXml2LfAlgorithmMapper()
				.map2Model(this.getAclfCaseData().getAclfAlgorithm(), algo);

		algo.loadflow();

		return aclfAdjNet.isLfConverged();
	}
}