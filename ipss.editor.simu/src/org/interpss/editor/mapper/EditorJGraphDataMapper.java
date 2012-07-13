/*
 * @(#)EditorJGraphDataMapper.java   
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

package org.interpss.editor.mapper;

import java.util.Vector;

import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.spring.EditorPluginSpringFactory;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.mapper.AbstractMapping;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.net.DataCheckConfiguration;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

/**
 * Map editor data NetContainer to a DistNetwork object for simulation purpose.
 */

public class EditorJGraphDataMapper extends AbstractMapping<IGFormContainer, SimuContext> {
	private IPSSMsgHub msg = null;
	public EditorJGraphDataMapper(IPSSMsgHub msg) {
		this.msg = msg;
	}

	/**
	 * mat the GFormContainer object to a SimuContext object
	 * 
	 * @param simuCtx
	 *            a SimuNetwork object
	 * @param fContainer
	 *            a GFormContainer object containing form data
	 */
	@Override
	public boolean map2Model(IGFormContainer gFormContainer, SimuContext simuCtx) {
		// check if current case data is dirty and mapping to SimuContext
		// object if necessary.
		// Currently only project in graph mode needs the mapping
		Vector<String> errMsg = gFormContainer.checkData(msg);
		if (errMsg != null) {
			EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(
						"Network data error", errMsg);
			return false;
		}

		msg.sendStatusMsg("SimuContext data is dirty, map editor data from GFormContainer to simuCtx");
		Object net = null;
		try {
			net = createMappingObject(gFormContainer, GFormContainer.class);
		} catch (InterpssException e) {
			msg.sendErrorMsg(e.toString());
			return false;
		}
		
		if (gFormContainer.getGNetForm().getAppType().equals(IGNetForm.AppType_Distribution)) {
			simuCtx.setNetwork(net, SimuCtxType.DISTRIBUTE_NET);
		} else {
			if (gFormContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_DStabilityNet)) {
				simuCtx.setNetwork(net, SimuCtxType.DSTABILITY_NET);
			} else if (gFormContainer.getGNetForm().getNetType().equals(
						IGNetForm.NetType_AclfNetwork)
						|| gFormContainer.getGNetForm().getNetType().equals(
								IGNetForm.NetType_AclfAdjNetwork)) {
					simuCtx.setNetwork(net, SimuCtxType.ACLF_NETWORK);
				} else if (gFormContainer.getGNetForm().getNetType().equals(
						IGNetForm.NetType_AcscNetwork)) {
					simuCtx.setNetwork(net, SimuCtxType.ACSC_NET);
				}
		}
		msg.sendStatusMsg("Editor data mapped to simuCtx");

		DataCheckConfiguration config = CoreObjectFactory.createDefultDataCheckConfiguration();
		if (simuCtx.getNetType() == SimuCtxType.ACLF_NETWORK) {
				if (!simuCtx.checkData(config)) {
					EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"Network Loadflow Data Error",
							"Please see the message list for details");
					return false;
				}
		} else if (simuCtx.getNetType() == SimuCtxType.ACLF_NETWORK) {
				if (!simuCtx.checkData(config)) {
					EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"Network Loadflow Data Error",
							"Please see the message list for details");
					return false;
				}
		} else if (simuCtx.getNetType() == SimuCtxType.ACSC_NET) {
				if (!simuCtx.checkData(config)) {
					EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"Network Ac Short Circuit Data Error",
							"Please see the message list for details");
					return false;
				}
		} else if (simuCtx.getNetType() == SimuCtxType.DSTABILITY_NET) {
				if (!simuCtx.checkData(config)) {
					EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"Transient stabiliry Data Error",
							"Please see the message list for details");
					return false;
				}
		}
		simuCtx.getNetwork().setDataChecked(false);
		return true;
	}

	private Object createMappingObject(Object editNet, Class<?> klass) throws InterpssException {
		if (klass == GFormContainer.class) {
			IGNetForm netForm = ((GFormContainer) editNet).getGNetForm();
			if (netForm.getAppType().equals(IGNetForm.AppType_Distribution)) {
				return EditorPluginSpringFactory.getForm2DistNetMapper()
						.map2Model((GFormContainer) editNet);
			} else if (netForm.getAppType().equals(
					IGNetForm.AppType_Transmission)) {
				if (netForm.getNetType()
						.equals(IGNetForm.NetType_DStabilityNet)) {
					return EditorPluginSpringFactory.getForm2DStabNetMapper()
								.map2Model((GFormContainer) editNet);
				} else if (netForm.getNetType().equals(
						IGNetForm.NetType_AcscNetwork)) {
					return EditorPluginSpringFactory.getForm2AcscNetMapper()
								.map2Model((GFormContainer) editNet);
				} else if (netForm.getNetType().equals(
						IGNetForm.NetType_AclfNetwork)
						|| netForm.getNetType().equals(
								IGNetForm.NetType_AclfAdjNetwork)) {
					return EditorPluginSpringFactory.getForm2AclfNetMapper()
								.map2Model((GFormContainer) editNet);
				}
			}
		}
		throw new InterpssException("Invalid class inpur: " + klass);
	}
}