/*
 * @(#)MasterfResultMapperImpl.java   
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

package org.interpss.mapper.report;

import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.report.bean.RptMainTitleBean;

import com.interpss.common.util.IpssLogger;

public class MasterfResultMapperImpl {
	public static void mapMasterTitleBean(IAppSimuContext appSimuCtx,
			RptMainTitleBean titleBean) {
		try {
			titleBean.setVersionNumber(GraphSpringFactory
					.getIpssGraphicEditor().getVersion());
			IProjectData projData = appSimuCtx.getProjData();
			titleBean.setProjectName(projData.getProjectName());
			titleBean.setUserName("InterPSS");
			titleBean.setFileName(projData.getFilename());
			titleBean.setReportNumber("InterPSS-Aclf-0001");
		} catch (Exception e) {
			IpssLogger.logErr(e);
		}
	}
}