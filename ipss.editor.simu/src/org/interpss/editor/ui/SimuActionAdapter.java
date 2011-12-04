 /*
  * @(#)SimuActionAdapter.java   
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

import org.interpss.editor.graph.GraphSimuUtilFunc;
import org.interpss.editor.jgraph.GraphSpringAppContext;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.spring.PluginSpringFactory;
import org.jgraph.JGraph;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.XmlBeanUtil;
import com.interpss.simu.SimuContext;

public class SimuActionAdapter {
	public static void menu_annotate_loadflow(JGraph graph) {
		if (graph != null) {
			try {
				SimuContext simuCtx = (SimuContext)GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext().getSimuCtx();
				GraphSimuUtilFunc.refreshCellLabel(simuCtx, graph, GraphSimuUtilFunc.LABEL_ACT_ACLF);
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}
		}		
	}

	public static void menu_annotate_acscPositive(JGraph graph) {
		if (graph != null) {
			try {
				SimuContext simuCtx = (SimuContext)GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext().getSimuCtx();
				GraphSimuUtilFunc.refreshCellLabel(simuCtx, graph, GraphSimuUtilFunc.LABEL_ACT_ACSC_POSITIVE);
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}
		}		
	}

	public static void menu_annotate_acscNegative(JGraph graph) {
		if (graph != null) {
			try {
				SimuContext simuCtx = (SimuContext)GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext().getSimuCtx();
				GraphSimuUtilFunc.refreshCellLabel(simuCtx, graph, GraphSimuUtilFunc.LABEL_ACT_ACSC_NEGATIVE);
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}
		}		
	}

	public static void menu_annotate_acscZero(JGraph graph) {
		if (graph != null) {
			try {
				SimuContext simuCtx = (SimuContext)GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext().getSimuCtx();
				GraphSimuUtilFunc.refreshCellLabel(simuCtx, graph, GraphSimuUtilFunc.LABEL_ACT_ACSC_ZERO);
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}
		}		
	}

	public static void menu_annotate_clear(JGraph graph) {
		if (graph != null) {
			try {
				SimuContext simuCtx = (SimuContext)GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext().getSimuCtx();
				GraphSimuUtilFunc.refreshCellLabel(simuCtx, graph, GraphSimuUtilFunc.LABEL_ACT_CLEAR);
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}
		}		
	}

	public static void menu_tools_debug_projDataInfo(IAppSimuContext appSimuContext) {
		XmlBeanUtil.ToolKid = XmlBeanUtil.TOOL_JDK;
		String str = appSimuContext.getProjData().toString();
		XmlBeanUtil.ToolKid = XmlBeanUtil.TOOL_CASTOR;
  		IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("Project data");
  		dialog.display(str);		
	}
	
	public static void menu_tools_debug_netDataInfo(IGFormContainer gFormContainer) {
		XmlBeanUtil.ToolKid = XmlBeanUtil.TOOL_JDK;
		String str = gFormContainer.toString();
		XmlBeanUtil.ToolKid = XmlBeanUtil.TOOL_CASTOR;
  		IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("Network data");
  		dialog.display(str);		
	}

	public static void menu_tools_debug_refData() {
		String str = PluginSpringFactory.getRefDataManager().toString();
  		IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("Reference Data");
  		dialog.display(str);		
	}

	public static void menu_tools_debug_simuCtxInfo() {
		String str = "";
		try {
			IAppSimuContext project = GraphSpringAppContext.getIpssGraphicEditor().getCurrentAppSimuContext();
			SimuContext simuCtx = (SimuContext)project.getSimuCtx();
			str = simuCtx.toString();
		} catch (Exception e) {
			IpssLogger.logErr(e);
			str = e.toString();
		}
  		IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("SimuNetwork object info");
  		dialog.display(str);		
	}
}
