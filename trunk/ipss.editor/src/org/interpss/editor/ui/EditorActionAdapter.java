 /*
  * @(#)EditorActionAdapter.java   
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

package org.interpss.editor.ui;

import org.interpss.editor.SimuAppSpringAppContext;
import org.interpss.editor.app.ProjectFileUtil;
import org.interpss.editor.chart.DStabPlotSelectionDialog;
import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.coreframework.IpssCustomFile;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.coreframework.IpssReportDocument;
import org.interpss.editor.coreframework.IpssTextFile;
import org.interpss.editor.doc.IpssProjectItem;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.io.CustomFileUtility;
import org.interpss.editor.jgraph.GraphSpringAppContext;
import org.interpss.editor.jgraph.ui.IIpssGraphModel;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.report.ReportUtil;
import org.interpss.editor.runAct.SimuRunWorker;
import org.interpss.editor.util.Utilities;
import org.interpss.report.IpssReportFactory;
import org.jgraph.JGraph;

import com.interpss.common.SpringAppContext;
import com.interpss.common.datatype.SimuRunType;
import com.interpss.common.mapper.IpssMapper;
import com.interpss.common.rec.IpssDBCase;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreSpringAppContext;
import com.interpss.dstab.DStabSpringAppContext;
import com.interpss.simu.SimuContext;

public class EditorActionAdapter {
	
	public static void menu_run(SimuRunType type, boolean graphView, JGraph graph, IpssEditorDocument doc) {
		try {
			if (type == SimuRunType.Dclf )
				menu_run_dclf(type, graphView, graph, doc);
			else if (type == SimuRunType.SenAnalysis )
				menu_run_sen(graphView, graph, doc);
			else if (type == SimuRunType.TradingAnalysis)
				menu_run_trading(graphView, graph, doc);
			else if (type == SimuRunType.Aclf)
				menu_run_aclf(graphView, graph, doc);
			else if (type == SimuRunType.Acsc)
				menu_run_acsc(graphView, graph, doc);
			else if (type == SimuRunType.DStab)
				menu_run_dstab(graphView, graph, doc);
			else if (type == SimuRunType.Scripts)
				menu_run_scripting(graphView, graph, doc);
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
		}		
	}

	private static void menu_run_dclf(SimuRunType type, boolean graphView, JGraph graph, IpssEditorDocument doc) throws Exception {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		
		IGFormContainer gFormContainer = null ;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IpssMapper mapper = SimuAppSpringAppContext.getEditorJGraphDataMapper();
			if (!mapper.mapping(gFormContainer, simuCtx, GFormContainer.class)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		
		IpssLogger.getLogger().info("Run " + type.toString() + " analysis");
		SimuRunWorker worker = new SimuRunWorker("Dclf SimuRunWorker");
		worker.configRun(type, simuCtx, graph);
		worker.start();
		appSimuCtx.setLastRunType(type);
	}

	private static void menu_run_sen(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception  {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		
		IGFormContainer gFormContainer = null ;
		if (graphView && appSimuCtx.isSimuNetDataDirty()) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IpssMapper mapper = SimuAppSpringAppContext.getEditorJGraphDataMapper();
			if (!mapper.mapping(gFormContainer, simuCtx, GFormContainer.class)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		
		simuCtx.setDclfAlgorithm(CoreSpringAppContext.getDclfAlgorithm());

		try {
			ICaseInfoDialog dialog = SimuAppSpringAppContext.getCaseInfoDialog(SimuRunType.SenAnalysis,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunType.SenAnalysis).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("SenAnalysis SimuRunWorker");
				worker.configRun(SimuRunType.SenAnalysis, simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunType.SenAnalysis);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			SpringAppContext.getEditorDialogUtil().showMsgDialog("Error", "See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_trading(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception  {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		
		IGFormContainer gFormContainer = null ;
		if (graphView && appSimuCtx.isSimuNetDataDirty()) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IpssMapper mapper = SimuAppSpringAppContext.getEditorJGraphDataMapper();
			if (!mapper.mapping(gFormContainer, simuCtx, GFormContainer.class)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		
		simuCtx.setDclfAlgorithm(CoreSpringAppContext.getDclfAlgorithm());

		try {
			ICaseInfoDialog dialog = SimuAppSpringAppContext.getCaseInfoDialog(SimuRunType.SenAnalysis,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunType.SenAnalysis).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("TradingAnalysis SimuRunWorker");
				worker.configRun(SimuRunType.TradingAnalysis, simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunType.TradingAnalysis);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			SpringAppContext.getEditorDialogUtil().showMsgDialog("Error", "See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_aclf(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception  {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		
		IGFormContainer gFormContainer = null ;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IpssMapper mapper = SimuAppSpringAppContext.getEditorJGraphDataMapper();
			if (!mapper.mapping(gFormContainer, simuCtx, GFormContainer.class)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		simuCtx.setLoadflowAlgorithm(CoreSpringAppContext.getLoadflowAlgorithm());

		try {
			ICaseInfoDialog dialog = SimuAppSpringAppContext.getCaseInfoDialog(SimuRunType.Aclf,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunType.Aclf).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("Aclf SimuRunWorker");
				worker.configRun(SimuRunType.Aclf, simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunType.Aclf);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			SpringAppContext.getEditorDialogUtil().showMsgDialog("Error", "See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_acsc(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception  {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		IGFormContainer gFormContainer = null;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IpssMapper mapper = SimuAppSpringAppContext.getEditorJGraphDataMapper();
			if (!mapper.mapping(gFormContainer, simuCtx, GFormContainer.class)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		simuCtx.setLoadflowAlgorithm(CoreSpringAppContext.getLoadflowAlgorithm());
		simuCtx.setSimpleFaultAlgorithm(CoreSpringAppContext.getSimpleFaultAlgorithm());
 
		try {
			ICaseInfoDialog dialog = SimuAppSpringAppContext.getCaseInfoDialog(SimuRunType.Acsc,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunType.Acsc).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("Acsc SimuRunWorker");
				worker.configRun(SimuRunType.Acsc, simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunType.Acsc);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			SpringAppContext.getIpssMsgHub().sendErrorMsg("See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_dstab(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		IGFormContainer gFormContainer = null;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IpssMapper mapper = SimuAppSpringAppContext.getEditorJGraphDataMapper();
			if (!mapper.mapping(gFormContainer, simuCtx, GFormContainer.class)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		simuCtx.setLoadflowAlgorithm(CoreSpringAppContext.getLoadflowAlgorithm());
		simuCtx.setDynSimuAlgorithm(DStabSpringAppContext.getDynamicSimuAlgorithm());

		ICaseInfoDialog dialog = SimuAppSpringAppContext.getCaseInfoDialog(SimuRunType.DStab,
				ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunType.DStab).getFilePathName());
    	IpssTextFile file = ProjectFileUtil.getProjectFile(doc, ProjectFileUtil.DStabOutputScriptFilename);
    	dialog.setDStabOutputScriptFilename(file.getFilePathName());
		dialog.init(gFormContainer, appSimuCtx);
    	if (dialog.isReturnOk()) {
			SimuRunWorker worker = new SimuRunWorker("DStab SimuRunWorker");
			worker.configRun(SimuRunType.DStab, simuCtx);
			worker.start();
			appSimuCtx.setLastRunType(SimuRunType.DStab);
		}	
	}
	
	private static void menu_run_scripting(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		IGFormContainer gFormContainer = null;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IpssMapper mapper = SimuAppSpringAppContext.getEditorJGraphDataMapper();
			if (!mapper.mapping(gFormContainer, simuCtx, GFormContainer.class)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		else {
			IpssCustomFile file = ((IpssCustomDocument)doc).getDocFile(); 
			CustomFileUtility.loadCustomFile(file.getFilePathName(), "", simuCtx);			
		}
		
		IpssLogger.getLogger().info("Run Scripts");
		ICaseInfoDialog dialog = SimuAppSpringAppContext.getCaseInfoDialog(SimuRunType.Scripts,
				ProjectFileUtil.getProjectScriptRunCaseFile(doc).getFilePathName());
		dialog.init(gFormContainer, appSimuCtx);
    	if (dialog.isReturnOk()) {
			SimuRunWorker worker = new SimuRunWorker("Scripting SimuRunWorker");
			IpssDBCase caseData = appSimuCtx.getCaseData(appSimuCtx.getProjData().getScriptsCaseName(), SimuRunType.Scripts);
			worker.configRun(SimuRunType.Scripts, simuCtx, caseData.getScripts(), caseData.getScriptLanguageType(), caseData.getScriptPluginName());
			worker.start();
			appSimuCtx.setLastRunType(SimuRunType.Scripts);
		}	
	}

	public static void menu_report_current(IpssEditorDocument doc) {
		String type = IpssReportFactory.RPT_TYPE_ACLFSUMMARY;
		if (doc instanceof GPDocument) {
			IGNetForm form = ((GPDocument) doc).getGFormContainer()
					.getGNetForm();
			type = ReportUtil.getDefaultReportType(form, doc
					.getSimuAppContext().getLastRunType(), doc
					.getSimuAppContext().isNonSymmetricFault());
		}
		doc.getGraphpad().expendTree2Object(doc);

		displayReport(doc, type);
	}

	public static void menu_report_aclfSummary(IpssEditorDocument doc) {
		displayReport(doc, IpssReportFactory.RPT_TYPE_ACLFSUMMARY);
	}

	public static void menu_report_aclfIeeeBusStype(IpssEditorDocument doc) {
		displayReport(doc, IpssReportFactory.RPT_TYPE_ACLFBUSSTYLE);
	}

	public static void menu_report_acscSymmetric(IpssEditorDocument doc) {
		displayReport(doc, IpssReportFactory.RPT_TYPE_ACSC3PFAULT);
	}

	public static void menu_report_acscNonSymmetric(IpssEditorDocument doc) {
		displayReport(doc, IpssReportFactory.RPT_TYPE_ACSCNSFAULT);
	}

	public static void menu_report_dstabRun(IpssEditorDocument doc) {
	}
	
	public static void menu_output_dstabcurve(IpssEditorDocument doc) {
		DStabPlotSelectionDialog dialog = new DStabPlotSelectionDialog(GraphSpringAppContext.getIpssGraphicEditor().getFrame(), true);
    	IpssTextFile file = ProjectFileUtil.getProjectFile(doc, ProjectFileUtil.DStabPlotScriptFilename);
    	dialog.init((SimuContext)doc.getSimuAppContext().getSimuCtx(), doc.getSimuAppContext().getDbSimuCaseId(), 
				    	file==null?null:file.getFilePathName());
	}
	public static void menu_report_save(IpssEditorDocument doc) {
	}

	public static void menu_report_saveAs(IpssEditorDocument doc) {
		if (doc instanceof IpssReportDocument) {
			IpssReportDocument rptDoc = (IpssReportDocument) doc;
			rptDoc.getMainViewer().saveActionPerformed();
		} else {
			IpssLogger.getLogger().severe(
					"Programming error, doc is not a IpssReportDocument");
		}
	}

	public static void menu_report_export(IpssEditorDocument doc) {
		if (doc instanceof IpssReportDocument) {
			IpssReportDocument rptDoc = (IpssReportDocument) doc;
			rptDoc.getMainViewer().exportActionPerformed();
		} else {
			IpssLogger.getLogger().severe(
					"Programming error, doc is not a IpssReportDocument");
		}
	}

	private static void displayReport(IpssEditorDocument doc, String type) {
		IpssProjectItem item = doc.getGraphpad().getCurrentProjectItem();
		String name = item.getProject().getProjectPath()
		+ System.getProperty("file.separator") +ReportUtil.getReportName(type, item.getFileNameNoExt());
		IpssProjectItem rptItem = doc.getGraphpad().newReportDocument(name,
				item, type, true);

		String filename = rptItem.getName();

		Utilities.delFile(filename);

		((IpssReportDocument) rptItem.getDocument()).getMainViewer().save(filename);
	}
}
