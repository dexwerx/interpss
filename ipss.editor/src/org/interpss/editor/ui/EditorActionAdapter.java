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

import org.interpss.db.IpssDBCase;
import org.interpss.dstab.output.DatabaseSimuOutputHandler;
import org.interpss.editor.EditorSimuSpringCtx;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.app.ProjectFileUtil;
import org.interpss.editor.chart.DStabPlotSelectionDialog;
import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.coreframework.IpssCustomFile;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.coreframework.IpssReportDocument;
import org.interpss.editor.coreframework.IpssTextFile;
import org.interpss.editor.doc.IpssProjectItem;
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

import com.interpss.DStabObjectFactory;
import com.interpss.common.mapper.IMapping;
import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringFactory;
import com.interpss.spring.CoreSpringFactory;
import com.interpss.spring.DclfSpringFactory;

public class EditorActionAdapter {
	
	public static void menu_run(SimuRunEnum type, boolean graphView, JGraph graph, IpssEditorDocument doc) {
		try {
			if (type == SimuRunEnum.Dclf )
				menu_run_dclf(type, graphView, graph, doc);
			else if (type == SimuRunEnum.SenAnalysis )
				menu_run_sen(graphView, graph, doc);
			else if (type == SimuRunEnum.TradingAnalysis)
				menu_run_trading(graphView, graph, doc);
			else if (type == SimuRunEnum.Aclf)
				menu_run_aclf(graphView, graph, doc);
			else if (type == SimuRunEnum.Acsc)
				menu_run_acsc(graphView, graph, doc);
			else if (type == SimuRunEnum.DStab)
				menu_run_dstab(graphView, graph, doc);
			else if (type == SimuRunEnum.Scripts)
				menu_run_scripting(graphView, graph, doc);
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
		}		
	}

	private static void menu_run_dclf(SimuRunEnum type, boolean graphView, JGraph graph, IpssEditorDocument doc) throws Exception {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		
		IGFormContainer gFormContainer = null ;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IMapping<IGFormContainer, SimuContext> mapper = (IMapping<IGFormContainer, SimuContext>)CoreCommonSpringFactory.ctx().getBean("editorJGraphDataMapper");
			if (!mapper.map2Model(gFormContainer, simuCtx)) 
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
			IMapping<IGFormContainer, SimuContext> mapper = (IMapping<IGFormContainer, SimuContext>)CoreCommonSpringFactory.ctx().getBean("editorJGraphDataMapper");
			if (!mapper.map2Model(gFormContainer, simuCtx)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		
		simuCtx.setDclfAlgorithm(DclfSpringFactory.getDclfAlgorithm());

		try {
			ICaseInfoDialog dialog = EditorSimuSpringCtx.getCaseInfoDialog(SimuRunEnum.SenAnalysis,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunEnum.SenAnalysis).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("SenAnalysis SimuRunWorker");
				worker.configRun(SimuRunEnum.SenAnalysis, simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunEnum.SenAnalysis);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			CoreCommonSpringFactory.getEditorDialogUtil().showMsgDialog("Error", "See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_trading(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception  {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		
		IGFormContainer gFormContainer = null ;
		if (graphView && appSimuCtx.isSimuNetDataDirty()) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IMapping<IGFormContainer, SimuContext> mapper = (IMapping<IGFormContainer, SimuContext>)EditorSimuSpringCtx.getEditorDataMapper();
			if (!mapper.map2Model(gFormContainer, simuCtx)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		
		simuCtx.setDclfAlgorithm(DclfSpringFactory.getDclfAlgorithm());

		try {
			ICaseInfoDialog dialog = EditorSimuSpringCtx.getCaseInfoDialog(SimuRunEnum.TradingAnalysis,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunEnum.TradingAnalysis).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("TradingAnalysis SimuRunWorker");
				worker.configRun(SimuRunEnum.TradingAnalysis, simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunEnum.TradingAnalysis);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			CoreCommonSpringFactory.getEditorDialogUtil().showMsgDialog("Error", "See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_aclf(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception  {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		
		IGFormContainer gFormContainer = null ;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IMapping<IGFormContainer, SimuContext> mapper = (IMapping<IGFormContainer, SimuContext>)EditorSimuSpringCtx.getEditorDataMapper();
			if (!mapper.map2Model(gFormContainer, simuCtx)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		simuCtx.setLoadflowAlgorithm(CoreSpringFactory.getLoadflowAlgorithm());

		try {
			ICaseInfoDialog dialog = EditorSimuSpringCtx.getCaseInfoDialog(SimuRunEnum.Aclf,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunEnum.Aclf).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("Aclf SimuRunWorker");
				worker.configRun(dialog.getCaseType(), simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunEnum.Aclf);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			CoreCommonSpringFactory.getEditorDialogUtil().showMsgDialog("Error", "See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_acsc(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception  {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		IGFormContainer gFormContainer = null;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IMapping<IGFormContainer, SimuContext> mapper = (IMapping<IGFormContainer, SimuContext>)EditorSimuSpringCtx.getEditorDataMapper();
			if (!mapper.map2Model(gFormContainer, simuCtx)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		simuCtx.setLoadflowAlgorithm(CoreSpringFactory.getLoadflowAlgorithm());
		simuCtx.setSimpleFaultAlgorithm(CoreSpringFactory.getSimpleFaultAlgorithm());
 
		try {
			ICaseInfoDialog dialog = EditorSimuSpringCtx.getCaseInfoDialog(SimuRunEnum.Acsc,
					ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunEnum.Acsc).getFilePathName());
			dialog.init(gFormContainer, appSimuCtx);
			if (dialog.isReturnOk()) {
				SimuRunWorker worker = new SimuRunWorker("Acsc SimuRunWorker");
				worker.configRun(SimuRunEnum.Acsc, simuCtx, graph);
				worker.start();
				appSimuCtx.setLastRunType(SimuRunEnum.Acsc);
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			CoreCommonSpringFactory.getIpssMsgHub().sendErrorMsg("See log file for details\n" + e.toString());
		}
	}

	private static void menu_run_dstab(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		IGFormContainer gFormContainer = null;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IMapping<IGFormContainer, SimuContext> mapper = (IMapping<IGFormContainer, SimuContext>)EditorSimuSpringCtx.getEditorDataMapper();
			if (!mapper.map2Model(gFormContainer, simuCtx)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		simuCtx.setLoadflowAlgorithm(CoreSpringFactory.getLoadflowAlgorithm());
		DynamicSimuAlgorithm dstabAlgo = DStabObjectFactory.createDynamicSimuAlgorithm(simuCtx.getDStabilityNet(),
						new DatabaseSimuOutputHandler(), simuCtx.getMsgHub());
		simuCtx.setDynSimuAlgorithm(dstabAlgo);

		ICaseInfoDialog dialog = EditorSimuSpringCtx.getCaseInfoDialog(SimuRunEnum.DStab,
				ProjectFileUtil.getProjectStdRunCaseFile(doc, SimuRunEnum.DStab).getFilePathName());
    	IpssTextFile file = ProjectFileUtil.getProjectFile(doc, ProjectFileUtil.DStabOutputScriptFilename);
    	dialog.setDStabOutputScriptFilename(file.getFilePathName());
		dialog.init(gFormContainer, appSimuCtx);
    	if (dialog.isReturnOk()) {
			SimuRunWorker worker = new SimuRunWorker("DStab SimuRunWorker");
			worker.configRun(SimuRunEnum.DStab, simuCtx);
			worker.start();
			appSimuCtx.setLastRunType(SimuRunEnum.DStab);
		}	
	}
	
	private static void menu_run_scripting(boolean graphView, JGraph graph, IpssEditorDocument doc)  throws Exception {
		IAppSimuContext appSimuCtx = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuCtx.getSimuCtx();
		IGFormContainer gFormContainer = null;
		if (graphView) {
			gFormContainer = ((IIpssGraphModel)graph.getModel()).getGFormContainer();
			IMapping<IGFormContainer, SimuContext> mapper = (IMapping<IGFormContainer, SimuContext>)EditorSimuSpringCtx.getEditorDataMapper();
			if (!mapper.map2Model(gFormContainer, simuCtx)) 
				return;
			appSimuCtx.setSimuNetDataDirty(false);
		}
		else {
			IpssCustomFile file = ((IpssCustomDocument)doc).getDocFile(); 
			CustomFileUtility.loadCustomFile(file.getFilePathName(), "", simuCtx);			
		}
		
		IpssLogger.getLogger().info("Run Scripts");
		ICaseInfoDialog dialog = EditorSimuSpringCtx.getCaseInfoDialog(SimuRunEnum.Scripts,
				ProjectFileUtil.getProjectScriptRunCaseFile(doc).getFilePathName());
		dialog.init(gFormContainer, appSimuCtx);
    	if (dialog.isReturnOk()) {
			SimuRunWorker worker = new SimuRunWorker("Scripting SimuRunWorker");
			IpssDBCase caseData = appSimuCtx.getCaseData(appSimuCtx.getProjData().getScriptsCaseName(), SimuRunEnum.Scripts);
			worker.configRun(SimuRunEnum.Scripts, simuCtx, caseData.getScripts(), caseData.getScriptLanguageType(), caseData.getScriptPluginName());
			worker.start();
			appSimuCtx.setLastRunType(SimuRunEnum.Scripts);
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
