 /*
  * @(#)GBranchForm.java   
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

package org.interpss.editor.form;

/**
	A JavaBean class for Graphic Branch form data
*/

import org.interpss.editor.data.acsc.AcscBranchData;
import org.interpss.editor.data.dist.DistBranchData;
import org.interpss.editor.data.dstab.DStabBranchData;
import org.interpss.editor.form.base.BaseBranchForm;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.jgraph.ui.form.IUserData;

import com.interpss.common.util.XmlBeanUtil;

public class GBranchForm extends BaseBranchForm implements IGBranchForm, java.io.Serializable {
	private static final long serialVersionUID = 1;

	/**
	*	Default constructor
	*/
    public GBranchForm() {
    }	

	/**
	*	Constructor
	*
	* @param id branch id
	*/
    public GBranchForm(String id, String appType) {
        setAppType(appType);
		if (appType.equals(IGNetForm.AppType_Distribution))
			distBranchData = new DistBranchData();
		else {
    		acscBranchData =  new AcscBranchData();
    		dstabBranchData =  new DStabBranchData();
    		dstabBranchData.setAcscBranchData(acscBranchData);
		}	
        setId(id);
    }	    

    public void rebuildRelation() {
		if (dstabBranchData != null)
			dstabBranchData.setAcscBranchData(acscBranchData);
    }
    
    public boolean isR_LT_X() {
		if (getAppType().equals(IGNetForm.AppType_Distribution))
			return distBranchData.isR_LT_X();
		else {
    		return acscBranchData.isR_LT_X();
		}	
    }
    
	/*
	*	Clone the current branch object
	*
	* @return the clone
	*/
    public Object clone() {
		XmlBeanUtil.ToolKid = XmlBeanUtil.TOOL_JDK;
		String xml = XmlBeanUtil.toXmlString(this);
		GBranchForm form = (GBranchForm)XmlBeanUtil.toObject(xml);
		form.rebuildRelation();
		return form;
    }
    
	/**
	*	Get the display string
	*
	* @return the display string
	*/
	public String getLabel(String type) {
		if (IUserData.BRANCH_LABEL.equals(type))
			return getName();
		else if (IUserData.BRANCH_SOURCE_LABEL.equals(type))
			return "1.005\n1.2+j0.8";
		else if (IUserData.BRANCH_TARGET_LABEL.equals(type))
			return "1.005\n1.2+j0.8";
		else
			return "Wrong label type: " + type;
	}

	private DStabBranchData dstabBranchData = null;
	public DStabBranchData getDStabBranchData() { return this.dstabBranchData; }
	public void setDStabBranchData(DStabBranchData form) { this.dstabBranchData = form; } 

	private AcscBranchData acscBranchData = null;
	public AcscBranchData getAcscBranchData() { return this.acscBranchData; }
	public void setAcscBranchData(AcscBranchData form) { this.acscBranchData = form; } 
	
	private DistBranchData distBranchData = null;
	public DistBranchData getDistBranchData() { return this.distBranchData; }
	public void setDistBranchData(DistBranchData form) { this.distBranchData = form; } 
	
	/* if the branch is in newly created status */
	private boolean newState = true;
	public boolean isNewState() { return this.newState; }
	public void setNewState(boolean b) { this.newState = b; } 
	
	public String getTransBranchLfCode() {
		return getAcscBranchData().getLfCode();
	}
	
	public String getDistBranchCode() {
		return getDistBranchData().getBranchCode();
	}
	
	// do not remove : for old file format
	public String getDisplayStr() {
		return getLabel(IUserData.BRANCH_LABEL);
	}
	
	/**
	 * Get the from(source) node label
	 */
	public String getSourceLabel() {
		return "1.2+j0.7";
	}
	
	/**
	 * Get the to(traget) node label
	 */	
	public String getTargetLabel() {
		return "1.2+j0.7";
	}	
}