 /*
  * @(#)BaseFormContainer.java   
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

package org.interpss.editor.form.base;

/**
*	A JavaBean: BaseFormContainer class for storing network data
*/

import java.util.ArrayList;
import java.util.List;

import org.interpss.editor.DataChangeMessage;
import org.interpss.editor.data.acsc.AcscBranchData;
import org.interpss.editor.data.dist.DistBranchData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGBusForm;
import org.interpss.editor.jgraph.ui.form.IGNetForm;

import com.interpss.common.util.IpssLogger;
import com.interpss.spring.CoreCommonSpringFactory;

public class BaseFormContainer {
	private List     busFormList = null;
	public List getBusFormList() { return this.busFormList; }
	public void setBusFormList(List l) { this.busFormList = l; }

	private List     branchFormList = null;
	public List getBranchFormList() { return this.branchFormList; }
	public void setBranchFormList(List l) { this.branchFormList = l; }
   
	private boolean dataDirty = false;
    public boolean isDataDirty() { return dataDirty; }
	public void setDataDirty(boolean b) {
		dataDirty = b;
		if (b) {
			CoreCommonSpringFactory.getIpssMsgHub().sendMsg(new DataChangeMessage(DataChangeMessage.DataDirty));
		}
	}

	/**
	*	Constructor
	*/
	public BaseFormContainer() {
	}	
	
   /**
	* 	Return the total number of buses
	*
	* @return the total number
	*/
	public int getBusFormTotal() {
		return busFormList.size();
	}
   
	/**
	* 	Return the total number of branches
	*
	* @return the total number
	*/
	public int getBranchFormTotal() {
		return branchFormList.size();
	}


	/** 
	* 	Reset the NetContainer to empty
	*/
	public void reset() {
		busFormList.clear();
		branchFormList.clear();
		setDataDirty(false); 
		IpssLogger.getLogger().info("NetContainer object reset to empty");
	}
	
	/** 
	* 	Get bus ids into an array
	*
	* @return the bus id array
	*/
	public Object[] getBusIdArray() {
		Object[] ary = new Object[getBusFormList().size()];
		for ( int i = 0; i < getBusFormList().size(); i++ )
			ary[i] = ((BaseBusForm)(getBusFormList().get(i))).getId();
		return ary;
	}

	/** 
	* 	Get bus name(id) into an array
	*
	* @return the bus id array
	*/
	public Object[] getBusNameIdArray() {
		Object[] ary = new Object[getBusFormList().size()];
		for ( int i = 0; i < getBusFormList().size(); i++ ) {
			BaseBusForm form = (BaseBusForm)getBusFormList().get(i);
			ary[i] = form.getNameIdStr();
		}	
		return ary;
	}
	
	/** 
	* 	Get load bus name(id) into an array
	*
	* @return the bus id array
	*/
	public Object[] getLoadBusNameIdArray() {
		List list = new ArrayList();
		for ( int i = 0; i < getBusFormList().size(); i++ ) {
			GBusForm form = (GBusForm)getBusFormList().get(i);
			if (form.getAcscBusData().isLoad())
				list.add(form.getNameIdStr());
		}	
		return list.toArray();
	}
	
	/** 
	* 	Get bus ids into a list
	*
	* @return the bus id list
	*/
	public List getBusIdList() {
		List list = new ArrayList();
		for ( int i = 0; i < getBusFormList().size(); i++ )
			list.add(((BaseBusForm)(getBusFormList().get(i))).getId());
		return list;
	}

	/** 
	* 	Get bus name into an array
	*
	* @return the bus name array
	*/
	public Object[] getBusNameArray() {
		Object[] ary = new Object[getBusFormList().size()];
		for ( int i = 0; i < getBusFormList().size(); i++ )
			ary[i] = ((BaseBusForm)(getBusFormList().get(i))).getName();
		return ary;
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
			BaseBusForm form = (BaseBusForm)(getBusFormList().get(i));
         if (!form.getId().equals(id) && name.equals(form.getName()))
            return true;
      }   
		return false;
	}

	/** 
	* 	Get branch ids into an array
	*
	* @return the branch id array
	*/
	public Object[] getBranchIdArray() {
		Object[] ary = new Object[getBranchFormList().size()];
		for ( int i = 0; i < getBranchFormList().size(); i++ )
			ary[i] = ((BaseBranchForm)(getBranchFormList().get(i))).getId();
		return ary;
	}

	/** 
	* 	Get branch name(id) into an array
	*
	* @return the branch id array
	*/
	public Object[] getBranchNameIdArray() {
		Object[] ary = new Object[getBranchFormList().size()];
		for ( int i = 0; i < getBranchFormList().size(); i++ ) {
			BaseBranchForm form = (BaseBranchForm)getBranchFormList().get(i);
			ary[i] = form.getNameIdStr();
		}	
		return ary;
	}
	
	/** 
	* 	Get branch name(id) without Xfr into an array
	*
	* @return the branch id array
	*/
	public Object[] getBranchNameIdArrayNoXfr(String appType) {
		List list = new ArrayList();
		for ( int i = 0; i < getBranchFormList().size(); i++ ) {
			GBranchForm form = (GBranchForm)getBranchFormList().get(i);
			if (appType.equals(IGNetForm.AppType_Distribution)) {
				DistBranchData data = form.getDistBranchData();
				if (data.getBranchCode().equals(IGBranchForm.DistBranchCode_Feeder))
					list.add(form.getNameIdStr());
			}
			else {
				AcscBranchData data = form.getAcscBranchData();
				if (data.getLfCode().equals(IGBranchForm.TransBranchLfCode_Line))
					list.add(form.getNameIdStr());
			}
		}	
		return list.toArray();
	}

	/** 
	* 	Get branch ids into a list
	*
	* @return the branch id list
	*/
	public List getBranchIdList() {
		List list = new ArrayList();
		for ( int i = 0; i < getBranchFormList().size(); i++ )
			list.add(((BaseBranchForm)(getBranchFormList().get(i))).getId());
		return list;
	}

	/** 
	* 	Get branch bus name (fromBusName->toBusName) into an array
	*
	* @return the branch bus name array
	*/
	public Object[] getBranchBusNameArray() {
		Object[] ary = new Object[getBranchFormList().size()];
		for ( int i = 0; i < getBranchFormList().size(); i++ ) {
			BaseBranchForm form = (BaseBranchForm)(getBranchFormList().get(i));
			ary[i] = form.getDefaultName();
      }   
		return ary;
	}
   
	/** 
	* 	Get branch number into an array
	*
	* @return the branch number array
	*/
	public Object[] getBranchNoArray() {
		Object[] ary = new Object[getBranchFormList().size()];
		for ( int i = 0; i < getBranchFormList().size(); i++ ) {
			ary[i] = ((BaseBranchForm)(getBranchFormList().get(i))).getBranchNumber();
      }   
		return ary;
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
	*	Get the BusForm object with the id from the container
	*
	* @param id the object id
	* @return the BusForm object found
	*/
	public IGBusForm getGBusForm(String id) {
		for (int i = 0; i < busFormList.size(); i++) {
			GBusForm form = (GBusForm)busFormList.get(i);
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
			GBranchForm form = (GBranchForm)branchFormList.get(i);
			if (id.equals(form.getId()))
				return form;
		}
		return null;
	}


	/**
	*	Remove the BusForm object with the id from the container
	*
	* @param id the object id
	*/
	public void removeBusForm(String id) {
		for (int i = 0; i < busFormList.size(); i++) {
			BaseBusForm form = (BaseBusForm)busFormList.get(i);
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
			BaseBranchForm form = (BaseBranchForm)branchFormList.get(i);
			if (id.equals(form.getId())) {
				branchFormList.remove(i);
				IpssLogger.getLogger().info("Branch removed, id: " + form.getId());
            return;
			}	
		}
	}
}