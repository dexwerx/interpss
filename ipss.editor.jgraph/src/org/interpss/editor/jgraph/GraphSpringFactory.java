 /*
  * @(#)GraphSpringAppContext.java   
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

package org.interpss.editor.jgraph;

import javax.swing.JDialog;

import org.interpss.editor.jgraph.cells.BranchEdge;
import org.interpss.editor.jgraph.cells.BusCell;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.jgraph.ui.IIpssGraphModel;
import org.interpss.editor.jgraph.ui.edit.IElemSearchDialog;
import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.jgraph.JGraph;

import com.interpss.common.util.IpssLogger;
import com.interpss.spring.CoreCommonSpringFactory;

public class GraphSpringFactory extends CoreCommonSpringFactory {
	public static int EditorType_FindDistObject = 1,
	 				  EditorType_FindTransObject = 2;
	/**
	 * Get a IpssGraph(prototype) from the SpringAppContext.
	 *  
	 * @return the IpssGraph object
	 */
	public static JGraph getIpssGraph() {
		return (JGraph)springAppCtx.getBean("ipssGraph");
	}
	
	/**
	 * Get a GFormContainer(prototype) from the SpringAppContext.
	 *  
	 * @return the IpssGraph object
	 */
	public static IGFormContainer getEditorFormContainer() {
		return (IGFormContainer)springAppCtx.getBean("editorFormContainer");
	}
	
	/**
	 *	get a cell editor object(singleton) according to the cell type
	 *
	 * @param cell the cell object, null object for create network editor
	 * @param graph the IpssGraph object
	 * @return the created cell editor
	 */
	public static JDialog getEditorDialog(Object obj, JGraph graph) {
		IIpssGraphModel model = (IIpssGraphModel)graph.getModel();
		//	   String appType = model.getGFormContainer().getGNetForm().getAppType();
		if (obj instanceof BusCell) {
			//	    This is called when a new bus is added
			IFormDataDialog busEditor = getBusDataEditor();
			busEditor.init(model.getGFormContainer(), ((BusCell)obj).getUserObject());
			return (JDialog)busEditor;
		}
		else if (obj instanceof IGBusForm) {
			//	    This is called when editing an existng bus
			IFormDataDialog busEditor = getBusDataEditor();
			busEditor.init(model.getGFormContainer(), obj);
			return (JDialog)busEditor;
		}
		else if (obj instanceof BranchEdge) {
			IFormDataDialog branchEditor = getBranchDataEditor();
			branchEditor.init(model.getGFormContainer(), ((BranchEdge)obj).getUserObject());
			return (JDialog)branchEditor;
		}
		else if (obj instanceof IGBranchForm) {
			IFormDataDialog branchEditor = getBranchDataEditor();
			branchEditor.init(model.getGFormContainer(), obj);
			return (JDialog)branchEditor;
		}
		else if (obj == null) {  // this is the case of NetEditor
			try {
				IFormDataDialog netEditor = getProjectDataEditor();
				netEditor.init(model.getGFormContainer(), getIpssGraphicEditor().getCurrentAppSimuContext().getProjData());
				return (JDialog)netEditor;
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}				
		}	
		else {
			IpssLogger.getLogger().severe("Programming error");
		}	
		return null;	
	}

	/**
	 *	get a search dialog object(singleton) according to the cell type
	 *
	 * @param type edtior type
	 * @param graph the IpssGraph object
	 * @return the created cell editor
	 */
	public static IElemSearchDialog getSearchDialog(int type, JGraph graph) {
		IIpssGraphModel model = (IIpssGraphModel)graph.getModel();
		if (type == EditorType_FindDistObject) {
			IFormDataDialog dialog = (IFormDataDialog)springAppCtx.getBean("searchElementDialog");
			dialog.init(model.getGFormContainer(), null);
			return (IElemSearchDialog)dialog;	
		}
		else {
			IpssLogger.getLogger().severe("Programming error");
		}	
	   return null;	
	}
	
	/**
	 * Get the IpssMainFrame(singleton) from the SpringAppContext.
	 *  
	 * @return the IpssMainFrame object
	 */
	public static IGraphicEditor getIpssGraphicEditor() {
		return (IGraphicEditor)springAppCtx.getBean("ipssGraphicEditor");
	}
	
	/**
	 * Get the Project Date editor
	 *  
	 * @return the editor object
	 */
	public static IFormDataDialog getProjectDataEditor() {
		return (IFormDataDialog)springAppCtx.getBean("projectDataEditor");
	}
	
	/**
	 * Get the Bus Date editor
	 *  
	 * @return the editor object
	 */
	public static IFormDataDialog getBusDataEditor() {
		return (IFormDataDialog)springAppCtx.getBean("busDataEditor");
	}

	/**
	 * Get the Branch Date editor
	 *  
	 * @return the editor object
	 */
	public static IFormDataDialog getBranchDataEditor() {
		return (IFormDataDialog)springAppCtx.getBean("branchDataEditor");
	}
}