 /*
  * @(#)DummyBusForm.java   
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

package org.interpss.editor.jgraph.ui.impl.form;

/**
*  A JavaBean class for Graphic Bus form data
*/

import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.jgraph.ui.form.IUserData;

import com.interpss.common.util.XmlBeanUtil;

public class DummyBusForm implements IGBusForm, java.io.Serializable {
	private static final long serialVersionUID = 1;

	private String busLabel = null;
	private String annotateLabel = null;

    protected String id = "";    // stored bus number or branchid fBusNo->tBusNo
    private String name = "";
    private boolean status = true;   
    private int area = 1;
    private int zone = 1;
	private String  appType = IGNetForm.AppType_Distribution;  			// Transmision | Distribution 
    
    private double  baseVoltage = 1000d;
    private String  baseVoltUnit = "Volt";  // "Volt", "KV"
    
	private boolean newState = true;
	private byte orientation = V_Orientation;

	/**
	*	Default constructor
	*/
    public DummyBusForm() {
    }	

    /**
	*	Constructor
	*
	* @param id bus id
	*/
    public DummyBusForm(String id, String appType) {
        setAppType(appType);
   		rebuildRelation();
        setId(id);
	}	    

    public String getId() { return this.id;}
    public void setId(String id) { this.id = id;}        

    public String getName() { return this.name; }
    public void setName(String n) { this.name = n; }        

    public boolean getStatus() { return this.status; }
    public void setStatus(boolean value) { this.status = value; }

    public int getArea() { return this.area; }
    public void setArea(int value) { this.area = value; }

    public int getZone() { return this.zone;}
    public void setZone(int value) { this.zone = value; }

    public String getAppType() { return this.appType; }
	public void setAppType(String str) { this.appType = str; }
	
    public String getNameIdStr() {
    	return 	getName() + "(" + getId() + ")";
    }
	public boolean isNewState() { return this.newState; }
	public void setNewState(boolean b) { this.newState = b; } 
	
	public byte getOrientation() { return this.orientation; }
	public void setOrientation(byte b) { this.orientation = b; }

	public String getAnnotateLabel() {
		if (annotateLabel == null)
			return "Annotate\nLabel";
		return annotateLabel;
	}
	public void setAnnotateLabel(String annotateLabel) {this.annotateLabel = annotateLabel;}

	public String getBusLabel() {
		if (busLabel == null)
			busLabel = getName();
		return busLabel;
	}
	public void setBusLabel(String busLabel) {this.busLabel = busLabel; } 

	public String getBaseVoltUnit() { return this.baseVoltUnit; }
    public void setBaseVoltUnit(String n) { this.baseVoltUnit = n; }        
      
    public double getBaseVoltage() { return this.baseVoltage; }
    public void setBaseVoltage(double value) { this.baseVoltage = value; }
    
	public void rebuildRelation() {
    }
    
    public Object clone() {
		String xml = XmlBeanUtil.toXmlString(this);
		DummyBusForm form = (DummyBusForm)XmlBeanUtil.toObject(xml);
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
	
	/**
	*	Convert the object to a string representation
	*
	* @return the string representation
	*/
	public String toString() {
		return XmlBeanUtil.toXmlString(this);
	} 	
}