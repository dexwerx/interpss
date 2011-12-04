 /*
  * @(#)DocumentUtilFunc.java   
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

package org.interpss.editor.util;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.coreframework.IpssReportDocument;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.form.IGNetForm;

import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class DocumentUtilFunc  {
    /**
     * Check if the doc is a simulation document
     * 
     * @param doc
     * @return
     */
	public static boolean isSimuDocument(IpssEditorDocument doc) {
		return doc != null && 
			(doc instanceof GPDocument || doc instanceof IpssCustomDocument); 
	}
    
	/**
	 * Check if the doc is a Ipss graphic document
	 * 
	 * @param doc
	 * @return
	 */
	public static boolean isGraphicDocument(IpssEditorDocument doc) {
		return doc != null && (doc instanceof GPDocument); 
	}
	
	/**
     * Check if the doc is a Report document
     * 
     * @param doc
     * @return
     */
	public static boolean isReportDocument(IpssEditorDocument doc) {
		return doc != null && (doc instanceof IpssReportDocument); 
	}

	/**
	 * Check if Aclf report menuitems should be enabled 
	 * 
	 * @param doc
	 * @return
	 */
	public static boolean enableAclfReport(IpssEditorDocument doc) {
		try {
			IAppSimuContext appSimuCtx = GraphSpringFactory.getIpssGraphicEditor().getCurrentAppSimuContext();
			if (appSimuCtx != null)
				return (isAclfDocument(doc) || isDStabDocument(doc)) && !appSimuCtx.isSimuNetDataDirty();
		} catch (Exception ex) {
			// do nothing
		}		
		return false;
	}

	/**
	 * Check if Acsc report menuitems should be enabled 
	 * 
	 * @param doc
	 * @return
	 */
	public static boolean enableAcscReport(IpssEditorDocument doc) {
		try {
			IAppSimuContext appSimuCtx = GraphSpringFactory.getIpssGraphicEditor().getCurrentAppSimuContext();
			if (appSimuCtx != null)
				return (isAcscDocument(doc) || isDStabDocument(doc)) && !appSimuCtx.isSimuNetDataDirty();
		} catch (Exception ex) {
			//IpssLogger.getLogger().severe(ex.toString());
		}		
		return false;
	}

	/**
	 * Check if DStab report menuitems should be enabled 
	 * 
	 * @param doc
	 * @return
	 */
	public static boolean enableDStabReport(IpssEditorDocument doc) {
		try {
			IAppSimuContext appSimuCtx = GraphSpringFactory.getIpssGraphicEditor().getCurrentAppSimuContext();
			if (appSimuCtx != null)
				return (isDStabDocument(doc)) && !appSimuCtx.isSimuNetDataDirty();
		} catch (Exception ex) {
			//IpssLogger.getLogger().severe(ex.toString());
		}		
		return false;
	}

	/**
     * Check if the doc is a Aclf simulation document
     * 
     * @param doc
     * @return
     */
	public static boolean isAclfDocument(IpssEditorDocument doc) {
		if (doc == null)
			return false;
		if (doc instanceof GPDocument) {
			IGNetForm form = ((GPDocument) doc).getGFormContainer().getGNetForm();
			if (form.getAppType().equals(IGNetForm.AppType_Distribution)) {
				return true;
			} else {
				if (form.getNetType().equals(IGNetForm.NetType_AclfNetwork)
						|| form.getNetType().equals(IGNetForm.NetType_AclfAdjNetwork)) {
					return true;
				} else if (form.getNetType().equals(IGNetForm.NetType_AcscNetwork)) {
					return ((GNetForm)form).getAcscNetData().isHasAclfData();
				}
			}
		} else if (doc instanceof IpssCustomDocument) {
			SimuContext simuCxt = (SimuContext)(((IpssCustomDocument) doc).getSimuAppContext()).getSimuCtx();
			if (simuCxt.getNetType() == SimuCtxType.ACLF_NETWORK || 
				simuCxt.getNetType() == SimuCtxType.DSTABILITY_NET) {
				return true;
			} else if (simuCxt.getNetType() == SimuCtxType.ACSC_NET) {
				return simuCxt.getAcscNet().isLfDataLoaded();
			}
		} 
		return false;
	}

	/**
     * Check if the doc is a Acsc simulation document
     * 
     * @param doc
     * @return
     */
	public static boolean isAcscDocument(IpssEditorDocument doc) {
		if (doc == null) 
			return false;

		if (doc instanceof GPDocument) {
			IGNetForm form = ((GPDocument) doc).getGFormContainer().getGNetForm();
			if (form.getAppType().equals(IGNetForm.AppType_Distribution)) {
				return true;
			} else {
				if (form.getNetType().equals(IGNetForm.NetType_AcscNetwork)) {
					return true;
				}
			}
		} else if (doc instanceof IpssCustomDocument) {
			SimuContext simuCxt = (SimuContext)(((IpssCustomDocument) doc).getSimuAppContext()).getSimuCtx();
			if (simuCxt.getNetType() == SimuCtxType.ACSC_NET ||
				simuCxt.getNetType() == SimuCtxType.DSTABILITY_NET) {
				return true;
			}
		} 		
		return false;
	}

	/**
     * Check if the doc is a DStab simulation document
     * 
     * @param doc
     * @return
     */
	public static boolean isDStabDocument(IpssEditorDocument doc) {
		if (doc == null) 
			return false;

		if (doc instanceof GPDocument) {
			IGNetForm form = ((GPDocument) doc).getGFormContainer().getGNetForm();
			if (form.getAppType().equals(IGNetForm.AppType_Transmission)) {
				if (form.getNetType().equals(IGNetForm.NetType_DStabilityNet))
					return true;
			}
		} else if (doc instanceof IpssCustomDocument) {
			SimuContext simuCxt = (SimuContext)(((IpssCustomDocument) doc).getSimuAppContext()).getSimuCtx();
			if (simuCxt.getNetType() == SimuCtxType.DSTABILITY_NET) 
				return true;
		} 
		
		return false;
	}
}
