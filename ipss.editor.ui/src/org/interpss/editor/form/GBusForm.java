 /*
  * @(#)GBusForm.java   
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
*  A JavaBean class for Graphic Bus form data
*/

import org.interpss.editor.data.acsc.AcscBusData;
import org.interpss.editor.data.dist.DistBusData;
import org.interpss.editor.data.dstab.DStabBusData;
import org.interpss.editor.form.base.BaseBusForm;
import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.jgraph.ui.form.IUserData;

import com.interpss.common.util.XmlBeanUtil;

public class GBusForm extends BaseBusForm implements IGBusForm, java.io.Serializable {
	private static final long serialVersionUID = 1;

	// constants
	public static byte
			H_Orientation = 1,
			V_Orientation = 2;
	
	//private String busLabel = null;
	private String annotateLabel = null;

	/**
	*	Default constructor
	*/
    public GBusForm() {
    }	

    /**
	*	Constructor
	*
	* @param id bus id
	*/
    public GBusForm(String id, String appType) {
        setAppType(appType);
		if (appType.equals(IGNetForm.AppType_Distribution))
			distBusData = new DistBusData();
		else {
    		acscBusData =  new AcscBusData();
    		dStabBusData =  new DStabBusData();
    		rebuildRelation();
		}	
        setId(id);
	}	    
    
    public void rebuildRelation() {
		if (dStabBusData != null)
			dStabBusData.setAcscBusData(acscBusData);
    }
    
    public Object clone() {
		XmlBeanUtil.ToolKid = XmlBeanUtil.TOOL_JDK;
		String xml = XmlBeanUtil.toXmlString(this);
		GBusForm form = (GBusForm)XmlBeanUtil.toObject(xml);
		form.rebuildRelation();
		return form;
    }

	/**
	*	Get the display string
	*
	* @return the display string
	*/
	public String getLabel(String type) {
		if (IUserData.BUS_LABEL.equals(type))
			return getBusLabel();
		else if (IUserData.BUS_ANNOTATE_LABEL.equals(type))
			return getAnnotateLabel();
		else
			return "Wrong label type: " + type;
	}
	
	private DStabBusData dStabBusData = null;
	public DStabBusData getDStabBusData() { return this.dStabBusData; }
	public void setDStabBusData(DStabBusData form) { this.dStabBusData = form; } 
	
	private AcscBusData acscBusData = null;
	public AcscBusData getAcscBusData() { return this.acscBusData; }
	public void setAcscBusData(AcscBusData form) { this.acscBusData = form; } 
	
	private DistBusData distBusData = null;
	public DistBusData getDistBusData() { return this.distBusData; }
	public void setDistBusData(DistBusData form) { this.distBusData = form; } 

	/* if the branch is in newly created status */
	private boolean newState = true;
	public boolean isNewState() { return this.newState; }
	public void setNewState(boolean b) { this.newState = b; } 
	
	/* bus cell graph orientation */
	private byte orientation = V_Orientation;
	public byte getOrientation() { return this.orientation; }
	public void setOrientation(byte b) { this.orientation = b; }

	/**
	 * @return Returns the annotateLabel.
	 */
	public String getAnnotateLabel() {
		if (annotateLabel == null)
			return "Annotate\nLabel";
		return annotateLabel;
	}

	/**
	 * @param annotateLabel The annotateLabel to set.
	 */
	public void setAnnotateLabel(String annotateLabel) {
		this.annotateLabel = annotateLabel;
	}

	/**
	 * @return Returns the busLabel.
	 */
	public String getBusLabel() {
		return getName();
	}

	// do not remove : for old file format
	public String getDisplayStr() {
		return getLabel(IUserData.BUS_LABEL);
	}
}