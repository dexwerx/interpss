 /*
  * @(#)DummyBranchForm.java   
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
	A JavaBean class for Graphic Branch form data
*/

import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.jgraph.ui.form.IUserData;

import com.interpss.common.datatype.Constants;
import com.interpss.common.util.XmlBeanUtil;

public class DummyBranchForm implements IGBranchForm, java.io.Serializable {
	private static final long serialVersionUID = 1;

	private String transBranchLfCode;
	
	private String distBranchCode;
	
	public static boolean XmlBinding = true;
	
    protected String id = "";    // stored bus number or branchid fBusNo->tBusNo
    private String name = "";
    private boolean status = true;   
    private int area = 1;
    private int zone = 1;
	private String  appType = IGNetForm.AppType_Distribution;  			// Transmision | Distribution 

	private String branchNumber = "0000";
    private String fromId = "";
    private String toId = "";
    private String fromBusName = "";
    private String toBusName = "";
    private int circuitNumber = 1;


	/* if the branch is in newly created status */
	private boolean newState = true;
	
	/**
	*	Default constructor
	*/
    public DummyBranchForm() {
    }	

	/**
	*	Constructor
	*
	* @param id branch id
	*/
    public DummyBranchForm(String id, String appType) {
        setAppType(appType);
        setId(id);
    }
    
    public String getBranchNumber() { return this.branchNumber;    }
    public void setBranchNumber(String value) { this.branchNumber = value;    }

    public int getCircuitNumber() { return this.circuitNumber;    }
    public void setCircuitNumber(int value) { this.circuitNumber = value;    }

    public String getFromId() { return this.fromId;    }
    public void setFromId(String value) { this.fromId = value;    }

    public String getToId() { return this.toId;    }
    public void setToId(String value) { this.toId = value;    }

    public String getFromBusName() { return this.fromBusName;    }
    public void setFromBusName(String value) { this.fromBusName = value;    }

    public String getToBusName() { return this.toBusName;    }
    public void setToBusName(String value) { this.toBusName = value;    }
	
    public String getDefaultName() {
	    return getFromBusName() + Constants.Token_BranchIdConnectStr + getToBusName();
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
    
    public void rebuildRelation() {
    }
    
	/*
	*	Clone the current branch object
	*
	* @return the clone
	*/
    public Object clone() {
		String xml = XmlBeanUtil.toXmlString(this);
		DummyBranchForm form = (DummyBranchForm)XmlBeanUtil.toObject(xml);
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

	public boolean isNewState() { return this.newState; }
	public void setNewState(boolean b) { this.newState = b; } 
	
	public String getTransBranchLfCode() {
		return transBranchLfCode;
	}
	
	public String getDistBranchCode() {
		return distBranchCode;
	}

	/**
	*	Convert the object to a string representation
	*
	* @return the string representation
	*/
	public String toString() {
		if (XmlBinding)
			return XmlBeanUtil.toXmlString(this);
		else
			return "";
	} 
}