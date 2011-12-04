 /*
  * @(#)DummyNetForm.java   
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
*	A JavaBean: for storing form objects for Graphic Editor
*    Delete bus not implemented yet, bus id (number will not work)
*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.interpss.editor.DataChangeMessage;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.jgraph.ui.impl.data.DummyProjData;
import org.interpss.numeric.util.Number2String;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.XmlBeanUtil;
import com.interpss.spring.CoreCommonSpringFactory;

public class DummyFormContainer implements IGFormContainer {
	private DummyNetForm gNetForm = null;

	public DummyFormContainer() {
    	this.gNetForm = new DummyNetForm("undefined");
		setBusFormList(new ArrayList());
		setBranchFormList(new ArrayList());
		IpssLogger.getLogger().info("GFormContainer constructed");
	}	

	private List     busFormList = null;
	public List getBusFormList() { return this.busFormList; }
	public void setBusFormList(List l) { this.busFormList = l; }

	private List     branchFormList = null;
	public List getBranchFormList() { return this.branchFormList; }
	public void setBranchFormList(List l) { this.branchFormList = l; }
   
	private boolean dataDirty = false;
    public boolean isDataDirty() { return dataDirty; }
    
	public Vector<String> checkData(IPSSMsgHub msg) {
		Vector<String> errMsg = new Vector<String>();
		return errMsg;
	}
	
	public void setDataDirty(boolean b) {
		dataDirty = b;
		if (b) {
			CoreCommonSpringFactory.getIpssMsgHub().sendMsg(new DataChangeMessage(DataChangeMessage.DataDirty));
		}
	}

   protected String formatIdNumber(String n) {
      if (n.length() == 1)
			n = "000" + n;
		else if (n.length() == 2)	
			n = "00" + n;
		else if (n.length() == 3)	
			n = "0" + n;
		return n;	
	}

    /**
	*	Add the BusForm object to the container. If a BusForm object with the same id
	*	is already in the container, the add request is ignored and the existing BusForm
	*	object return
	*
	* @param form the BusForm object to be added
	* @param the added or existing BusForm object
	*/
	public IGBusForm addGBusForm(IGBusForm form) {
		IGBusForm bus = getGBusForm(form.getId());
		if (bus != null)
			return bus;
		busFormList.add(form);
		return form;
	}
	

	/**
	*	Add the BranchForm object to the container. If a BranchForm object with the same id
	*	is already in the container, the add request is ignored and the existing BranchForm
	*	object return
	*
	* @param form the BranchForm object to be added
	* @param the added or existing BranchForm object
	*/
	public IGBranchForm addGBranchForm(IGBranchForm form) {
		IGBranchForm bra = getGBranchForm(form.getId());
		if (bra != null)
			return bra;
		branchFormList.add(form);
		return form;
	}
	
	/**
	*	Create a BusForm object with the default bus id and add the object to the bus list
	*
	* @return the created BusForm object
	*/	
	public IGBusForm createGBusForm() {
		DummyBusForm form = new DummyBusForm(getDefaultBusId(), gNetForm.getAppType());
		form.setName("Bus-"+ new Integer(form.getId()).toString());
		form.setNewState(true);
		getBusFormList().add(form);
		return form;
	}
	
	/**
	*	Make a copy of the form and add the object to the bus list with the default id
	*
	* @param form the bus form object to be copied
	* @return the created BusForm object
	*/	
	public IGBusForm createGBusForm(IGBusForm form) {
		IGBusForm newForm = (IGBusForm)form.clone();
		newForm.setId(getDefaultBusId());
		form.setName("Bus-"+ new Integer(form.getId()).toString());
		getBusFormList().add(newForm);
		return newForm;
	}
	
	/**
	*	Delete the branch object from the branch list
	*
	* @param form the branch form object to be copied
	* @return true if the object has been deleted
	*/	
	public boolean deleteGBranchForm(IGBranchForm form) {
		return deleteGBranchForm(form.getId());
	}
	
	/**
	*	Delete the branch object from the branch list
	*
	* @param branchid the branch id
	* @return true if the object has been deleted
	*/	
	public boolean deleteGBranchForm(String branchid) {
		for (int i = 0; i < getBranchFormList().size(); i++) {
			IGBranchForm form = (IGBranchForm)getBranchFormList().get(i);
			if (form.getId().equals(branchid)) {
				getBranchFormList().remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	*	Delete the bus object from the branch list
	*
	* @param form the bus form object to be copied
	* @return true if the object has been deleted
	*/	
	public boolean deleteGBusForm(IGBusForm form) {
		return deleteGBusForm(form.getId());
	}
	
	/**
	*	Delete the bus object from the bus list
	*
	* @param busid the bus id
	* @return true if the object has been deleted
	*/	
	public boolean deleteGBusForm(String busid) {
		for (int i = 0; i < getBusFormList().size(); i++) {
			IGBusForm form = (IGBusForm)getBusFormList().get(i);
			if (form.getId().equals(busid)) {
				getBusFormList().remove(i);
				return true;
			}
		}
		return false;
	}
	
	
	/**
	*	Get the default bus id, "0001", "0002" ...
	*
	* @return the generated bus id
	*/
	public String getDefaultBusId() {
		int n = gNetForm.getNextBusNumber();
      if (n == 0) {
          n = getBusFormList().size()+1;
      }    
		gNetForm.setNextBusNumber(n+1);
		return formatIdNumber(new Integer(n).toString());
	}
	
   /**
	*	Get the default branch id, "0001", "0002" ...
	*
	* @return the generated bus id
	*/
	public String getDefaultBranchId() {
		int n = gNetForm.getNextBranchNumber();
      if (n == 0) {
          n = getBranchFormList().size()+1;
      }    
		gNetForm.setNextBranchNumber(n+1);
		return formatIdNumber(new Integer(n).toString());
   }
   
	/**
	*	Create a BranchForm object and add the object to the branch list
	*
	* @return the created BranchForm object
	*/	
	public IGBranchForm createGBranchForm() {
		DummyBranchForm form = new DummyBranchForm("branchid", gNetForm.getAppType());
		form.setNewState(true);
		form.setBranchNumber(getDefaultBranchId());
		getBranchFormList().add(form);
		return form;
	}
	
	/**
	*	Make a copy of the form and add the object to the branch list
	*
	* @param form the branch form object to be copied
	* @return the created BranchForm object
	*/	
	public IGBranchForm createGBranchForm(IGBranchForm form) {
		IGBranchForm newForm = (IGBranchForm)form.clone();
		newForm.setId("branchid");
		newForm.setBranchNumber(getDefaultBranchId());
		getBranchFormList().add(newForm);
		return newForm;
	}
	
	public IGNetForm getGNetForm() { return this.gNetForm;	}
	public void setGNetForm(DummyNetForm netForm) { this.gNetForm = netForm;	}
	public void setGNetForm(IGNetForm netForm) { this.gNetForm = (DummyNetForm)netForm;	}
	
	public Object xml2Object(String xmlStr, Class klass) {
		if (klass == IGBusForm.class) 
			return XmlBeanUtil.toObject(xmlStr, DummyBusForm.class);
		else if (klass == IGBranchForm.class) 
			return XmlBeanUtil.toObject(xmlStr, DummyBranchForm.class);
		else if (klass == IGNetForm.class) 
			return XmlBeanUtil.toObject(xmlStr, DummyNetForm.class);
		else if (klass == IProjectData.class) 
			return XmlBeanUtil.toObject(xmlStr, DummyProjData.class);
		return null;
	}	
	
	/**
	*	Get the BusForm object with the id from the container
	*
	* @param id the object id
	* @return the BusForm object found
	*/
	public IGBusForm getGBusForm(String id) {
		for (int i = 0; i < busFormList.size(); i++) {
			DummyBusForm form = (DummyBusForm)busFormList.get(i);
			if (id.equals(form.getId()))
				return form;
		}
		return null;
	}

	/**
	*	Get the BranchForm object with the id from the container
	*
	* @param id the object id
	* @return the BranchForm object found
	*/
	public IGBranchForm getGBranchForm(String id) {
		for (int i = 0; i < branchFormList.size(); i++) {
			DummyBranchForm form = (DummyBranchForm)branchFormList.get(i);
			if (id.equals(form.getId()))
				return form;
		}
		return null;
	}	
	
	public void rebuildRelation() {
		getGNetForm().rebuildRelation();
		for (int i = 0; i < getBusFormList().size(); i++) {
			((DummyBusForm)getBusFormList().get(i)).rebuildRelation();
		}
		for (int i = 0; i < getBranchFormList().size(); i++) {
			((DummyBranchForm)getBranchFormList().get(i)).rebuildRelation();
		}
	}	
	
	/** 
	* 	Reset the NetContainer to empty
	*/
	public void reset() {
		busFormList.clear();
		branchFormList.clear();
		setDataDirty(false); 
		getGNetForm().setId("");
		getGNetForm().setNewState(true);
		getGNetForm().setNextBusNumber(0);
		getGNetForm().setNextBranchNumber(0);
		getGNetForm().getBaseVoltList().clear();
	}	
	
	/**
	*	Remove the BusForm object with the id from the container
	*
	* @param id the object id
	*/
	public void removeBusForm(String id) {
		for (int i = 0; i < busFormList.size(); i++) {
			DummyBusForm form = (DummyBusForm)busFormList.get(i);
			if (id.equals(form.getId())) {
				busFormList.remove(i);
				IpssLogger.getLogger().info("Bus removed, id: " + form.getId());
            return;
			}	
		}
	}

	/**
	*	Remove the BranchForm object with the id from the container
	*
	* @param id the object id
	*/
	public void removeBranchForm(String id) {
		for (int i = 0; i < branchFormList.size(); i++) {
			DummyBranchForm form = (DummyBranchForm)branchFormList.get(i);
			if (id.equals(form.getId())) {
				branchFormList.remove(i);
				IpssLogger.getLogger().info("Branch removed, id: " + form.getId());
            return;
			}	
		}
	}	
	
	/** 
	* 	Add a user entered base voltage value into the Base Voltage List
	*
	* @param volt the base voltage to add
	*/
	public void addBaseVolt(double volt) {
		String str = Number2String.toStr(volt, "#0.00");
		Iterator itr = getGNetForm().getBaseVoltList().iterator();
		while (itr.hasNext()) {
			String v = (String)(itr.next());
			if (str.equals(v))
				return;
		}		
		getGNetForm().getBaseVoltList().add(str);		
	}	
	
	/** 
	* 	Get the user entered base voltage array
	*
	* @return the base voltage array
	*/
	public Object[] getBaseVoltArray() {
		return getGNetForm().getBaseVoltList().toArray();
	}	
	
	/** 
	* 	Check if the name is already used by other bus
	*
   * @param id bus id of the bus
   * @param name name to be checked
	* @return true or false
	*/
	public boolean hasBusName(String id, String name) {
      for ( int i = 0; i < getBusFormList().size(); i++ ) {
    	  DummyBusForm form = (DummyBusForm)(getBusFormList().get(i));
         if (!form.getId().equals(id) && name.equals(form.getName()))
            return true;
      }   
		return false;
	}
	
	/**
	*	Init the NetForm object to the specified netType and appType
	*
	* @param netType the network type string
	* @param appType the application type string
	*/	
	public void initGNetForm(String netType, String appType) {
		gNetForm.setName("aDummyNetwork");
		gNetForm.setName("A Dummy Network");
		gNetForm.setDescription("A Dummy Network for GEdtior development and testing");
		gNetForm.setBaseKVA(100000.0);
		gNetForm.setFreqHZ(50.0);
	}	
	
	public boolean isBranchR_LT_X() { return false; }
	
    /**
	*	Convert the object to a string representation
	*
	* @return the string representation
	*/
	public String toString() {
		return XmlBeanUtil.toXmlString(this);
	}
}